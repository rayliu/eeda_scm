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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import controllers.api.ApiController;
import controllers.oms.custom.dto.DingDanBuilder;
import controllers.oms.custom.dto.DingDanDto;
import controllers.profile.LoginUserController;
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
    
    @Before(Tx.class)
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

        //需后台处理的字段
        salesOrder.set("order_no", OrderNoGenerator.getNextOrderNo("IDQHDF"));   
        salesOrder.set("custom_id", 9);  //深圳前海德丰投资发展有限公司
        salesOrder.set("status", "未上报");  //状态
        salesOrder.set("currency", 142);  //币制
        salesOrder.set("consignee_type", 1);  //证件类型
        salesOrder.set("consignee_country", 142);//国
        salesOrder.set("pro_remark", "无");//优惠说明
        salesOrder.set("pay_channel", "01");//优惠说明
        salesOrder.set("create_stamp", new Date());  //操作时间
        salesOrder.set("note", "导入数据");  //备注
        salesOrder.set("office_id", office_id);
        DbUtils.setModelValues(soDto, salesOrder);

        salesOrder.save();
        String id = salesOrder.getLong("id").toString();
       
        
        List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)soDto.get("goods");
		DbUtils.handleList(itemList, id, SalesOrderGoods.class, "order_id");
		String cargo_name = itemList.get(0).get("cargo_name").trim();
		
		//创建对应运单
	    String log_id = createLogOrder(id,soDto,cargo_name);
		
		String orgCode = soDto.get("org_code").toString();
        DingDanDto returnSoDto= DingDanBuilder.buildDingDanDto(id, orgCode);
        Record r = new Record();
        r.set("code", "0");
        r.set("msg", "请求已成功处理!");
        r.set("data", returnSoDto);
        controller.renderJson(r);
    }

  
   //自动生成运输单
    @Before(Tx.class)
    public String createLogOrder(String sales_order_id,Map<String, ?> soDto,String cargo_name){
    	String express_no = soDto.get("express_no").toString().trim();
    	String netwt = soDto.get("netwt").toString().trim();
		String weight = soDto.get("weight").toString().trim();
		String freight = soDto.get("freight").toString().trim();
		
		
    	LogisticsOrder logisticsOrder = null;
    	if(StringUtils.isNotEmpty(sales_order_id)){
    		String order_no = OrderNoGenerator.getNextOrderNo("YD");
    		logisticsOrder = new LogisticsOrder();
        	logisticsOrder.set("log_no", order_no);
        	logisticsOrder.set("status","暂存");
    		//logisticsOrder.set("create_by", user_id);
    		logisticsOrder.set("create_stamp", new Date());
    		
    		//预填值gln368
    		logisticsOrder.set("country_code", "142");
    		logisticsOrder.set("shipper_country", "142");
    		logisticsOrder.set("shipper_city", "440305");
    		logisticsOrder.set("shipper", "深圳前海德丰投资发展有限公司");
    		logisticsOrder.set("shipper_address", "深圳前海湾保税港区W6仓");
    		logisticsOrder.set("shipper_telephone", "075586968661");
    		logisticsOrder.set("traf_mode", "4");
    		logisticsOrder.set("pack_no", 1);
    		logisticsOrder.set("ship_name", "汽车");
    		logisticsOrder.set("customs_code", "5349");
    		logisticsOrder.set("ciq_code", "471800");
    		logisticsOrder.set("port_code", "5349");
    		logisticsOrder.set("decl_code", "5349");
    		logisticsOrder.set("supervision_code", "5349");
    		logisticsOrder.set("ems_no", "I440366006516001");
    		logisticsOrder.set("trade_mode", "1210");
    		logisticsOrder.set("destination_port", "5349");
    		logisticsOrder.set("ps_type", "2");
    		logisticsOrder.set("trans_mode", "1");
    		logisticsOrder.set("cut_mode", "1");
    		logisticsOrder.set("wrap_type", "CT");
    		logisticsOrder.set("ie_date", new Date());
    		logisticsOrder.set("deliver_date",  new Date());
    		if(StringUtils.isNotEmpty(cargo_name)){
    			logisticsOrder.set("goods_info", cargo_name); 
			}
    		if(StringUtils.isNotEmpty(freight)){
    			logisticsOrder.set("freight", freight);  //运费
			}
    		logisticsOrder.set("insure_fee", "0");
    		if(StringUtils.isNotEmpty(express_no)){
    			logisticsOrder.set("parcel_info",express_no );
    		}
    		if(StringUtils.isNotEmpty(netwt)){
    			logisticsOrder.set("netwt",netwt );
    		}
    		if(StringUtils.isNotEmpty(weight)){
    			logisticsOrder.set("weight",weight );
    		}
    		if(StringUtils.isNotEmpty(sales_order_id)){
        		logisticsOrder.set("sales_order_id",sales_order_id);
        	}
    		
    		logisticsOrder.save();
    	}
		return logisticsOrder.getLong("id").toString();
    }
    
    //自动生成运输单
    @Before(Tx.class)
    public Long createLogOrder1(String sales_order_id,String action){
    	LogisticsOrder logisticsOrder = null;
    	if("create".equals(action)){
    		String order_no = OrderNoGenerator.getNextOrderNo("YD");
    		logisticsOrder = new LogisticsOrder();
        	logisticsOrder.set("log_no", order_no);
        	logisticsOrder.set("sales_order_id",sales_order_id);
        	logisticsOrder.set("status","暂存");
    		//logisticsOrder.set("create_by", LoginUserController.getLoginUserId(this));
    		logisticsOrder.set("create_stamp", new Date());
    		
    		//预填值
    		logisticsOrder.set("country_code", "142");
    		logisticsOrder.set("shipper_country", "142");
    		logisticsOrder.set("shipper_city", "440305");
    		logisticsOrder.set("shipper", "深圳前海德丰投资发展有限公司");
    		logisticsOrder.set("shipper_address", "深圳前海湾保税港区W6仓");
    		logisticsOrder.set("shipper_telephone", "075586968661");
    		logisticsOrder.set("traf_mode", "4");
    		logisticsOrder.set("ship_name", "汽车");
    		logisticsOrder.set("customs_code", "5349");
    		logisticsOrder.set("ciq_code", "471800");
    		logisticsOrder.set("port_code", "5349");
    		logisticsOrder.set("decl_code", "5349");
    		logisticsOrder.set("supervision_code", "5349");
    		logisticsOrder.set("ems_no", "I440366006516001");
    		logisticsOrder.set("trade_mode", "1210");
    		logisticsOrder.set("destination_port", "5349");
    		logisticsOrder.set("ps_type", "2");
    		logisticsOrder.set("trans_mode", "1");
    		logisticsOrder.set("cut_mode", "1");
    		logisticsOrder.set("wrap_type", "CT");
    		logisticsOrder.set("freight", "0");
    		logisticsOrder.set("insure_fee", "0");
    		logisticsOrder.set("parcel_info", order_no);
    		logisticsOrder.set("ie_date", new Date());
    		logisticsOrder.set("deliver_date",  new Date());
    		
    		logisticsOrder.save();
    	}else{
    		logisticsOrder = LogisticsOrder.dao.findFirst("select * from logistics_order where sales_order_id = ?",sales_order_id);
    	}
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
