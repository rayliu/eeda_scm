package controllers.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import models.Location;
import models.Party;
import models.UserLogin;
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
import controllers.oms.importOrder.CheckOrder;
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
    
    
    public boolean checkCode (String value){
        boolean flag = true;
        if(value.length()<6){
            flag = false;
        }else{
            Location lo = Location.dao.findFirst("select * from location where code = ?",value);
            if(lo == null){
                flag = false;
            }
        }
        return flag;
    }
    
    public boolean checkAllCode (String value){
        boolean flag = true;
        if(value.length()<20){
            flag = false;
        }else{
            String[] values = value.split("#");
            String province = values[0];
            String city = values[1];
            String qv = values[2];
            Location lo = Location.dao.findFirst("select * from location where code = ? and pcode = ?",city,province);
            if(lo == null){
                flag = false;
            }
            
            Location lo2 = Location.dao.findFirst("select * from location where (code = ? and pcode = ?) or (code = ? and pcode = ?)",qv,city,qv,province);
            if(lo2 == null){
                flag = false;
            }
        }
        return flag;
    }
    
    public Record checkConfig(Map<String, ?> soDto){
    	// 校验sign
    	Record r = new Record();
    	r.set("code", "0");
    	String ref_order_no = soDto.get("ref_order_no").toString();
        String appkey = soDto.get("appkey").toString();
        String salt = new BigDecimal((Double) soDto.get("salt")).toString();
        String sign = soDto.get("sign").toString();
        
        String paraStr = "ref_order_no="+ref_order_no+"&appkey="+appkey+"&salt="+salt+"&sign="+sign;
        logger.debug("paraStr=" + paraStr);
        int signIndex = paraStr.indexOf("sign");
        if (signIndex == -1) {
            r.set("code", "2");
            r.set("msg", "请求中sign不存在!");
        }
        
        //appkey
        Party party = Party.dao.findFirst("select * from party where type=? and appkey=?", Party.PARTY_TYPE_CUSTOMER, appkey);
        if (party == null) {
            r.set("code", "2");
            r.set("msg", "请求中appkey不正确!");
        }else{
        	Long office_id=party.getLong("office_id");//属于哪个公司的
            SalesOrder sCheck = SalesOrder.dao.findFirst("select * from sales_order where ref_order_no=? and office_id=?", ref_order_no, office_id);
            if (sCheck != null) {
                r.set("code", "5");
                r.set("msg", "ref_order_no:"+ref_order_no+"已存在!");
            }else{
                r.set("office_id", office_id);
            }
        }
        return r;
    }
    
    
    public Record checkOrder(Map<String, ?> soDto){
    	Record r = new Record();
    	String msg = "";
    	
    	String goods_value = soDto.get("goods_value").toString();
    	String consignee = soDto.get("consignee").toString();
    	String consignee_id = soDto.get("consignee_id").toString();
    	String consignee_address = soDto.get("consignee_address").toString();
    	String consignee_telephone = soDto.get("consignee_telephone").toString();
    	String province = soDto.get("province").toString();
    	String city = soDto.get("city").toString();
    	String district = soDto.get("district").toString();
    	String netwt = soDto.get("netwt").toString();
    	String weight = soDto.get("weight").toString();
    	String freight = soDto.get("freight").toString();
    	
        
    	//明细表
    	List<Map<String, String>> itemLists = (ArrayList<Map<String, String>>)soDto.get("goods");
		for(Map<String, String> itemList:itemLists){
			String action = itemList.get("action");
			String bar_code = itemList.get("bar_code");
			String item_no = itemList.get("item_no");
			String unit = itemList.get("unit");
			String name = itemList.get("name");
			String currency = itemList.get("currency");
			String tax_rate = itemList.get("tax_rate");
			String qty = itemList.get("qty");
			String price = itemList.get("price");
			
			if(StringUtils.isNotEmpty(qty)){
				if(!CheckOrder.checkDouble(qty)){
					msg += "【商品数量】("+qty+")格式类型有误";
				}
			}else{
				msg += "【商品数量】不能为空";
			}
			
			if(StringUtils.isNotEmpty(unit)){
				if(!CheckOrder.checkUnitCode(unit)){
					msg += "【单位】("+unit+")有误，系统不存在此单位编码，请参照标准报关单位编码填写";
				}
			}else{
				msg += "【商品数量】不能为空";
			}
			
			if(StringUtils.isNotEmpty(price)){
				if(!CheckOrder.checkDouble(price)){
					msg +=  "【单价】("+price+")格式类型有误";
				}
			}else{
				msg += "【单价】不能为空";
			}
			
			if(StringUtils.isNotEmpty(bar_code)){
				if(!CheckOrder.checkUpc(bar_code)){
					msg += "【商品条码（UPC）】("+bar_code+")有误，系统产品信息不存在此upc";
				}
			}else{
				msg += "【商品条码(UPC)】不能为空";
			}
			
			//【税率】和【含税总价】不能同时为空
			if(StringUtils.isNotEmpty(goods_value)){
				if(!CheckOrder.checkDouble(goods_value)){
					msg += "【含税总价】("+goods_value+")格式类型有误";
				}
			}else if(StringUtils.isNotEmpty(tax_rate)){
				if(!CheckOrder.checkDouble(tax_rate)){
					msg += "【税率】("+tax_rate+")格式类型有误";
				}
			}else{
				msg += "【税率】和【含税总价】不能同时为空";
			}
			
			if(StringUtils.isEmpty(item_no)){
				msg += "【商品货号】不能为空";
			}
			
		}
		
		
		if(StringUtils.isNotEmpty(netwt)){
			if(!CheckOrder.checkDouble(netwt)){
				msg += "【净重】("+netwt+")格式类型有误";
			}
		}
		
		if(StringUtils.isNotEmpty(weight)){
			if(!CheckOrder.checkDouble(weight)){
				msg += "【毛重】("+weight+")格式类型有误";
			}
		}
		
		if(StringUtils.isNotEmpty(freight)){
			if(!CheckOrder.checkDouble(freight)){
				msg += "【运费】("+freight+")格式类型有误";
			}
		}
		
		//省
		if(StringUtils.isNotEmpty(province)){
			if(!checkCode(province)){
				msg += "【省级】("+freight+")行政编码有误";
			}
		}else{
			msg += "【省级】不能为空";
		}
		
		//市
		if(StringUtils.isNotEmpty(city)){
			if(!checkCode(city)){
				msg += "【市级】("+freight+")行政编码有误";
			}
		}else{
			msg += "【市级】不能为空";
		}
		
		//区
		if(StringUtils.isNotEmpty(district)){
			if(checkCode(district)){
				msg += "【区级】("+freight+")行政编码有误";
			}
		}else{
			msg += "【区级】不能为空";
		}
		
		//省市区总
		if(StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city) && StringUtils.isNotEmpty(district)){
			String value = province+"#"+city+"#"+district;
			if(!checkAllCode(value)){
				msg += "【省市区级】不存在上下级关系，请参考标准城市编码";
			}
		}
		
		if(StringUtils.isEmpty(consignee_address)){
			msg += "【收货人详细地址】不能为空";
		}
		
		
		if(StringUtils.isEmpty(consignee)){
			msg += "【收货人姓名】不能为空";
		}
		if(StringUtils.isEmpty(consignee_telephone)){
			msg += "【收货人电话】不能为空";			
		}
		if(StringUtils.isEmpty(consignee_address)){
			msg += "【收货人详细地址】不能为空";
		}
		if(StringUtils.isEmpty(consignee_id)){
			msg += "【身份证号码】不能为空";
		}
		
		if(StringUtils.isNotEmpty(msg)){
			r.set("code", "6");
			r.set("msg", msg);
		}
    	return r;
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
        Long office_id = null;
        //数据校验
        Record configR = checkConfig(soDto);
        if("0".equals(configR.getStr("code"))){
        	office_id = configR.getLong("office_id");
        	Record errorR = checkOrder(soDto);
        	if(errorR!=null){
            	controller.renderJson(errorR);
                return;// 注意这里一定要返回,否则会继续往下执行
        	}
        }else{
        	controller.renderJson(configR);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        
        
        UserLogin ul = UserLogin.dao.findFirst("select * from user_login where user_name = ?","interface@defeng.com");
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
        salesOrder.set("create_by", ul.getLong("id"));  //操作人
        salesOrder.set("note", "接口数据");  //备注
        salesOrder.set("office_id", office_id);
        DbUtils.setModelValues(soDto, salesOrder);

        salesOrder.save();
        String id = salesOrder.getLong("id").toString();
       
        
        List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)soDto.get("goods");
		DbUtils.handleList(itemList, id, SalesOrderGoods.class, "order_id");
		String cargo_name = itemList.get(0).get("item_name").trim();
		//创建对应运单
	    String log_id = createLogOrder(id,soDto,cargo_name,ul);
		
		String orgCode = soDto.get("org_code").toString();
        DingDanDto returnSoDto= DingDanBuilder.buildDingDanDto(id, orgCode);
        Record r = new Record();
       
        if(StringUtils.isNotEmpty(log_id)){
        	r.set("code", "0");
        	r.set("msg", "请求已成功处理!");
        } else{
        	r.set("code", "-1");
        	r.set("msg", "请求创建运单失败!");
        }
        r.set("data", returnSoDto);
        controller.renderJson(r);
    }

  
   //自动生成运输单
    @Before(Tx.class)
    public String createLogOrder(String sales_order_id,Map<String, ?> soDto,String cargo_name,UserLogin ul){
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
    		logisticsOrder.set("create_by", ul.getLong("id"));
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
            DingDanDto soDto= DingDanBuilder.buildDingDanDto(so.getLong("id").toString(), "");
            
            LogisticsOrder lo = LogisticsOrder.dao.findFirst("select * from logistics_order where sales_order_id = ?",so.getLong("id"));
            String logistics_ciq_status = null;
            String logistics_cus_status = null;
            String log_status = null;
            if(lo != null){
            	log_status = lo.getStr("status");
            	logistics_ciq_status = lo.getStr("logistics_ciq_status");
            	logistics_cus_status = lo.getStr("logistics_cus_status");
            }
            
            String status = so.getStr("status");
            String order_cus_status = so.getStr("order_cus_status");
            String order_ciq_status = so.getStr("order_ciq_status");
            String pay_status = so.getStr("pay_status");
            String bill_cus_status = so.getStr("bill_cus_status");
            String bill_cus_result = so.getStr("bill_cus_result");
            
            Record r = new Record();
            r.set("code", "0");
            r.set("status",status);
            r.set("order_cus_status",order_cus_status);
            r.set("order_ciq_status",order_ciq_status);
            r.set("pay_status",pay_status);
            r.set("bill_cus_status",bill_cus_status);
            r.set("bill_cus_result",bill_cus_result);
            
            //运单
            r.set("log_status",log_status);
            r.set("logistics_ciq_status",logistics_ciq_status);
            r.set("logistics_cus_status",logistics_cus_status);

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
