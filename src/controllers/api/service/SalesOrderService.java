package controllers.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import models.Party;
import models.eeda.OrderActionLog;
import models.eeda.oms.LogisticsOrder;
import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderGoods;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import controllers.api.ApiController;
import controllers.oms.custom.dto.DingDanBuilder;
import controllers.oms.custom.dto.DingDanDto;
import controllers.util.DbUtils;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;

public class SalesOrderService {
    private Logger logger = Logger.getLogger(SalesOrderService.class);

    private Controller controller;
    private HttpServletRequest request = null;
    private String requestMethod = null;
    
    public SalesOrderService(Controller controller) {
        this.controller = controller;
        this.request = controller.getRequest();
        this.requestMethod = controller.getRequest().getMethod();
    }
    
    public void saveSo() throws InstantiationException, IllegalAccessException{
        String orderJsonStr = controller.getPara("order");
        
        if(orderJsonStr==null){
            orderJsonStr = ApiController.getRequestPayload(controller.getRequest());
        }
        
        if(orderJsonStr==null){
            Record r = new Record();
            r.set("code", "2");
            r.set("msg", "POST请求中, body不存在!");
            controller.renderJson(r);
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
        
     // 校验sign
        String ref_order_no = soDto.get("ref_order_no").toString();
        String appkey = soDto.get("appkey").toString();
        String salt = new BigDecimal((Double) soDto.get("salt")).toString();
        String sign = soDto.get("sign").toString();
        
        String paraStr = "ref_order_no="+ref_order_no+"&appkey="+appkey+"&salt="+salt+"&sign="+sign;
        logger.debug("paraStr=" + paraStr);
        int signIndex = paraStr.indexOf("sign");
        if (signIndex == -1) {
            Record r = new Record();
            r.set("code", "2");
            r.set("msg", "请求中sign不存在!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        
        //appkey
        Party party = Party.dao.findFirst("select * from party where type=? and appkey=?", Party.PARTY_TYPE_CUSTOMER, appkey);
        if (party == null) {
            Record r = new Record();
            r.set("code", "2");
            r.set("msg", "请求中appkey不正确!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        Long office_id=party.getLong("office_id");//属于哪个公司的
        
        SalesOrder sCheck = SalesOrder.dao.findFirst("select * from sales_order where ref_order_no=? and office_id=?", ref_order_no, office_id);
        if (sCheck != null) {
            Record r = new Record();
            r.set("code", "5");
            r.set("msg", "ref_order_no:"+ref_order_no+"已存在!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        
        SalesOrder salesOrder = new SalesOrder();
      //create 
        DbUtils.setModelValues(soDto, salesOrder);
        
        //需后台处理的字段
        salesOrder.set("order_no", OrderNoGenerator.getNextOrderNo("DD"));
        salesOrder.set("pay_channel", "01");//默认是网上支付
        salesOrder.set("office_id", office_id);
//        salesOrder.set("create_by", );
        salesOrder.set("create_stamp", new Date());
        salesOrder.save();
        
        String id = salesOrder.getLong("id").toString();
        Long log_id = createLogOrder(id);
        
        List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)soDto.get("goods");
        DbUtils.handleList(itemList, id, SalesOrderGoods.class, "order_id");

        DingDanDto returnSoDto= DingDanBuilder.buildDingDanDto(id, "123456");
        Record r = new Record();
        r.set("code", "0");
        r.set("msg", "请求已成功处理!");
        r.set("data", returnSoDto);
        controller.renderJson(r);
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
    
    public void querySo(){
        String orderJsonStr = controller.getPara("orderQuery");//form 提交

        if (orderJsonStr == null) {//raw 提交
            orderJsonStr = ApiController.getRequestPayload(controller.getRequest());
        }

        ApiController.saveLog("api_so_query_post", orderJsonStr);
        
        if (orderJsonStr == null) {
            Record r = new Record();
            r.set("code", "2");
            r.set("msg", "POST请求中, body不存在!");
            controller.renderJson(r);
            return;
        }

        // 这里将json字符串转化成javabean对象
        Map<String, ?> itemDto = new Gson().fromJson(orderJsonStr, HashMap.class);
        
        // 校验sign
        String ref_order_no = itemDto.get("ref_order_no").toString();
        String appkey = itemDto.get("appkey").toString();
        String salt = itemDto.get("salt").toString();
        String sign = itemDto.get("sign").toString();
        
        String paraStr = "ref_order_no="+ref_order_no+"&appkey="+appkey+"&salt="+salt+"&sign="+sign;
        logger.debug("paraStr=" + paraStr);
        int signIndex = paraStr.indexOf("sign");
        if (signIndex == -1) {
            Record r = new Record();
            r.set("code", "2");
            r.set("msg", "请求中sign不存在!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        
        //appkey
        Party party = Party.dao.findFirst("select * from party where type=? and appkey=?", Party.PARTY_TYPE_CUSTOMER, appkey);
        if (party == null) {
            Record r = new Record();
            r.set("code", "2");
            r.set("msg", "请求中appkey不正确!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        String org_code=party.getStr("org_code");
        
        String paraStrNoSign = paraStr.substring(0, signIndex - 1);
        logger.debug("paraStrNoSign=" + paraStrNoSign);

        String serverSign = MD5Util.encodeByMD5(paraStrNoSign).toUpperCase();
        logger.debug("serverSign=" + serverSign);
        if (!sign.equals(serverSign)) {
            Record r = new Record();
            r.set("code", "3");
            r.set("msg", "请求中sign不正确!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        
        SalesOrder so = SalesOrder.dao.findFirst("select * from sales_order where ref_order_no = ?", ref_order_no);
        if(so !=null){
            DingDanDto soDto= DingDanBuilder.buildDingDanDto(so.getLong("id").toString(), "123456");
            Record r = new Record();
            r.set("code", "0");
            r.set("msg", "请求已成功处理!");
            r.set("data", soDto);
            controller.renderJson(r);            
        }else{
            Record r = new Record();
            r.set("code", "1");
            r.set("msg", "订单号码:"+ref_order_no+"不存在!");
            controller.renderJson(r);
        }
    }
}
