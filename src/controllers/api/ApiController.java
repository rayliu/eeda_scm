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
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import controllers.api.service.BaseItemApiService;
import controllers.api.service.SalesOrderService;
import controllers.oms.custom.dto.DingDanBuilder;
import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.DingDanGoodsDto;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;
import controllers.yh.job.CustomJob;

//@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class ApiController extends Controller {

	private Logger logger = Logger.getLogger(ApiController.class);
	
	@Before(Tx.class)
	public static void saveLog(String orderType, String content) {
        OrderActionLog log = new OrderActionLog();
        log.set("order_type", orderType);
        log.set("action", "get");
        log.set("json", content);
        log.set("time_stamp", new Date());
        log.save();
    }
	
//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		redirect("/apidoc");
	}
	
    public void salesOrder() throws InstantiationException, IllegalAccessException {
        String method = this.getRequest().getMethod();
        if("GET".equals(method)){
//            soGetHandle();
        }else if("POST".equals(method)){
            SalesOrderService s = new SalesOrderService(this);
            s.saveSo();
        }else{
            
        }
    }
    
    public void salesOrderQuery() throws InstantiationException, IllegalAccessException {
        String method = this.getRequest().getMethod();
        if("GET".equals(method)){
            
        }else if("POST".equals(method)){
            //soGetHandle();
            SalesOrderService s = new SalesOrderService(this);
            s.querySo();
        }else{
            
        }
    }
    
    public void baseItem() throws InstantiationException, IllegalAccessException {
        String method = this.getRequest().getMethod();
        BaseItemApiService is = new BaseItemApiService(this);
        is.process(method);
    }
    
    public void baseItemQuery() throws InstantiationException, IllegalAccessException {
        BaseItemApiService is = new BaseItemApiService(this);
        is.query();
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
    
    //当订单状态发生变化时, 调用第3方系统的API, 通知变化
    public void orderNotifyCallback(){
        String order_id = getPara("order_id");
        Record rec = Db.findFirst("select * from sales_order where id=?", order_id);
        String urlStr=PropKit.use("app_config.txt").get("ybcEdiUrl")+"/orderNotify";
        String appKey =PropKit.use("app_config.txt").get("ybcAppKey");
        long salt = new Date().getTime();
        String paraStr = "ref_order_no="+rec.getStr("order_no")+"&appkey="+appKey+"&salt="+salt;
        String sign = MD5Util.encodeByMD5(paraStr).toUpperCase();
        
        Record jsonRec = new Record();
        jsonRec.set("ref_order_no", rec.get("ref_order_no", ""));
        jsonRec.set("order_cus_status_code", rec.get("order_cus_status_code", ""));
        jsonRec.set("order_cus_status_msg", CustomJob.statusShow(rec.get("order_cus_status_code", "").toString()));
        jsonRec.set("order_ciq_status_code", rec.get("order_ciq_status_code", ""));
        jsonRec.set("order_ciq_status_msg", CustomJob.statusShow(rec.get("order_ciq_status_code", "").toString()));
        jsonRec.set("order_pay_status_code", rec.get("order_pay_status_code", ""));
        jsonRec.set("order_pay_status_msg", CustomJob.statusShow(rec.get("order_pay_status_code", "").toString()));
        jsonRec.set("appkey", appKey);
        jsonRec.set("salt", salt);
        jsonRec.set("sign", sign);
        
        System.out.println("参数:"+jsonRec.toJson());
        String returnMsg = EedaHttpKit.post(urlStr, jsonRec.toJson());
        System.out.println("结果"+returnMsg);
        
        CustomJob.operationLog("salesOrder", jsonRec.toJson(), order_id, "orderNotify", LoginUserController.getLoginUserId(this).toString());
        
        renderJson(returnMsg);
    }
}
