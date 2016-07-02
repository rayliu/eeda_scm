package controllers.api;

import interceptor.SetAttrLoginUserInterceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import models.UserLogin;
import models.eeda.OrderActionLog;
import models.eeda.oms.LogisticsOrder;
import models.eeda.oms.SalesOrderCount;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.oms.SalesOrder;
import models.eeda.profile.CustomCompany;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import controllers.api.service.BaseItemApiService;
import controllers.oms.custom.dto.DingDanBuilder;
import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.DingDanGoodsDto;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;

//@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class ApiController extends Controller {

	private Logger logger = Logger.getLogger(ApiController.class);
	

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		redirect("/apidoc");
	}
	
    public void salesOrder() throws InstantiationException, IllegalAccessException {
        String method = this.getRequest().getMethod();
        if("GET".equals(method)){
            soGetHandle();
        }else if("POST".equals(method)){
            soGetHandle();
        }else{
            
        }
    }
    
    public void baseItem() throws InstantiationException, IllegalAccessException {
        String method = this.getRequest().getMethod();
        BaseItemApiService is = new BaseItemApiService(this);
        is.process(method);
    }

    private void soGetHandle() {
        String fullUrl = getFullURL(getRequest());
        String sign = getPara("sign");
        OrderActionLog log = new OrderActionLog();
        log.set("order_type", "api_so_get");
        log.set("action", "get");
        log.set("json", fullUrl);
        log.set("time_stamp", new Date());
        log.save();
        //校验sign
        int splitIndex = fullUrl.indexOf("?");
        String paraStr = fullUrl.substring(splitIndex+1);
        logger.debug("paraStr="+paraStr);
        int signIndex = paraStr.indexOf("sign");
        if(signIndex==-1){
            Record r = new Record();
            r.set("errCode", "02");
            r.set("errMsg", "请求中sign不存在!");
            renderJson(r);
            return;//注意这里一定要返回,否则会继续往下执行
        }
        String paraStrNoSign = paraStr.substring(0, signIndex-1);
        logger.debug("paraStrNoSign="+paraStrNoSign);
        
        String serverSign = MD5Util.encodeByMD5(paraStrNoSign).toUpperCase();
        logger.debug("serverSign="+serverSign);
        if(!sign.equals(serverSign)){
            Record r = new Record();
            r.set("errCode", "03");
            r.set("errMsg", "请求中sign不正确!");
            renderJson(r);
            return;//注意这里一定要返回,否则会继续往下执行
        }
        
        
        String orderNo = getPara("ref_order_no");
        SalesOrder so = SalesOrder.dao.findFirst("select * from sales_order where ref_order_no = ?", orderNo);
        if(so !=null){
            DingDanDto soDto= DingDanBuilder.buildDingDanDto(so.getLong("id").toString(), "123456");
            renderJson(soDto);
        }else{
            Record r = new Record();
            r.set("errCode", "01");
            r.set("errMsg", "订单号码"+orderNo+"不存在!");
            renderJson(r);
        }
    }
	
    public static String getFullURL(HttpServletRequest req) {
        StringBuffer requestURL = req.getRequestURL();
        String queryString = req.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    @Before(Tx.class)
    public void soPostHandle() throws InstantiationException, IllegalAccessException {
        String orderJsonStr = getPara("order");
        
        if(orderJsonStr==null){
            orderJsonStr = getRequestPayload(getRequest());
        }
        
        if(orderJsonStr==null){
            Record r = new Record();
            r.set("errCode", "02");
            r.set("errMsg", "POST请求中, body不存在!");
            renderJson(r);
            return;
        }
            
        
        OrderActionLog log = new OrderActionLog();
        log.set("order_type", "api_so_post");
        log.set("action", "post");
        log.set("json", orderJsonStr);
        log.set("time_stamp", new Date());
        log.save();
        
        
        //这里将json字符串转化成javabean对象
        //开始生成so
        Map<String, ?> soDto= new Gson().fromJson(orderJsonStr, HashMap.class);
        
        SalesOrder salesOrder = new SalesOrder();
      //create 
        DbUtils.setModelValues(soDto, salesOrder);
        
        //需后台处理的字段
        salesOrder.set("order_no", OrderNoGenerator.getNextOrderNo("DD"));
        salesOrder.set("pay_channel", "01");//默认是网上支付
//        salesOrder.set("create_by", );
        salesOrder.set("create_stamp", new Date());
        salesOrder.save();
        
        String id = salesOrder.getLong("id").toString();
        Long log_id = createLogOrder(id);
        
        List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)soDto.get("goods");
        DbUtils.handleList(itemList, id, SalesOrderGoods.class, "order_id");

        DingDanDto returnSoDto= DingDanBuilder.buildDingDanDto(id, "123456");
        renderJson(returnSoDto);
    }

    public static String getRequestPayload(HttpServletRequest req) {  
        StringBuilder sb = new StringBuilder();  
        try(BufferedReader reader = req.getReader();) {  
                 char[]buff = new char[1024];  
                 int len;  
                 while((len = reader.read(buff)) != -1) {  
                          sb.append(buff,0, len);  
                 }  
        }catch (IOException e) {  
            e.printStackTrace();  
        }  
        return sb.toString();  
    }  
    
  //自动生成运输单
    @Before(Tx.class)
    private Long createLogOrder(String sales_order_id){
        LogisticsOrder logisticsOrder = new LogisticsOrder();
        logisticsOrder.set("log_no", OrderNoGenerator.getNextOrderNo("YD"));
        logisticsOrder.set("sales_order_id",sales_order_id);
        logisticsOrder.set("status","新建");
//        logisticsOrder.set("create_by", LoginUserController.getLoginUserId(this));
        logisticsOrder.set("create_stamp", new Date());
        logisticsOrder.save();
        return logisticsOrder.getLong("id");
    }
}
