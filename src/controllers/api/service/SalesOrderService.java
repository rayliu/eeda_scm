package controllers.api.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
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
import models.eeda.profile.Product;
import models.eeda.profile.Unit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import controllers.api.ApiController;
import controllers.oms.custom.dto.DingDanBuilder;
import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.importOrder.CheckOrder;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.IdcardUtil;
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
    
    
    public boolean checkBarFromName(String bar_code,String name){
    	boolean flag = true;
    	Product p = Product.dao.findFirst("select * from product where serial_no = ? and item_name = ?",bar_code,name);
    	if(p==null){
    		flag = false;
    	}
    	
    	return flag;
    }
    
    
    public boolean checkExpressNo(String value){
    	boolean flag = true;
    	LogisticsOrder log = LogisticsOrder.dao.findFirst("select * from logistics_order where parcel_info = ?",value);
    	if(log!=null){
    		flag = false;
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
    	r.set("code", "00");
    	String errorMsg = "";
    	
    	String order_no = soDto.get("order_no")==null?null:soDto.get("order_no").toString().trim();
		//String freight = soDto.get("freight").toString()==null?null:soDto.get("freight").toString().trim(); //运杂费
		String consignee = soDto.get("consignee")==null?null:soDto.get("consignee").toString().trim();
		String consignee_telephone = soDto.get("consignee_telephone")==null?null:soDto.get("consignee_telephone").toString().trim();
		String consignee_address = soDto.get("consignee_address")==null?null:soDto.get("consignee_address").toString().trim();
		
		//String buyer_regno = soDto.get("buyer_regno").toString()==null?null:soDto.get("buyer_regno").toString().trim();//订购人注册号
		String buyer_name = soDto.get("buyer_name")==null?null:soDto.get("buyer_name").toString().trim();
		String buyer_id_number = soDto.get("buyer_id_number")==null?null:soDto.get("buyer_id_number").toString().trim();
		
		String cop_no = soDto.get("cop_no")==null?null:soDto.get("cop_no").toString().trim();
		//String insure_fee = soDto.get("insure_fee").toString()==null?null:soDto.get("insure_fee").toString().trim();//保价费
		String weight = soDto.get("weight")==null?null:soDto.get("weight").toString().trim(); 
		String net_weight = soDto.get("net_weight")==null?null:soDto.get("net_weight").toString().trim();
		
		String goods_info = soDto.get("goods_info")==null?null:soDto.get("goods_info").toString().trim(); 
		String pack_no = soDto.get("pack_no")==null?null:soDto.get("pack_no").toString().trim();  
		String wrap_type = soDto.get("wrap_type")==null?null:soDto.get("wrap_type").toString().trim(); 
		
		//String province = soDto.get("province")==null?null:soDto.get("province").toString().trim(); 
		//String city = soDto.get("city")==null?null:soDto.get("city").toString().trim(); 
		//String district = soDto.get("district")==null?null:soDto.get("district").toString().trim();

    	//明细表
    	List<Map<String, String>> itemLists = (ArrayList<Map<String, String>>)soDto.get("goods");
		for(Map<String, String> itemList:itemLists){
			String bar_code = itemList.get("bar_code")==null?null:itemList.get("bar_code").trim();
			String item_name = itemList.get("item_name")==null?null:itemList.get("item_name").trim();
			String item_no = itemList.get("item_no")==null?null:itemList.get("item_no").trim();
			String qty = itemList.get("qty")==null?null:itemList.get("qty").trim();
			String unit = itemList.get("unit")==null?null:itemList.get("unit").trim();
			String qty1 = itemList.get("qty1")==null?null:itemList.get("qty1").trim();
			String unit1 = itemList.get("unit1")==null?null:itemList.get("unit1").trim();
			String price = itemList.get("price")==null?null:itemList.get("price").trim(); 
			String tax_rate = itemList.get("tax_rate")==null?null:itemList.get("tax_rate").trim(); 
			String gcode = itemList.get("gcode")==null?null:itemList.get("gcode").trim(); 
			String g_model = itemList.get("g_model")==null?null:itemList.get("g_model").trim(); 
			String ciq_gno = itemList.get("ciq_gno")==null?null:itemList.get("ciq_gno").trim();
			String ciq_gmodel = itemList.get("ciq_gmodel")==null?null:itemList.get("ciq_gmodel").trim();
			
			if(StringUtils.isNotEmpty(bar_code)){
				if(!CheckOrder.checkUpc(bar_code)){
					errorMsg += ("【商品条码】("+bar_code+")有误，系统产品信息不存在此upc;");
				}
			}else{
				errorMsg += ("【商品条码】不能为空;");
			}
			
			if(StringUtils.isEmpty(item_name)){
				errorMsg += ("【商品名称】不能为空;");
			}
			
			if(StringUtils.isEmpty(item_no)){
				errorMsg += ("【商品货号】不能为空;");
			}
			
			if(StringUtils.isNotEmpty(qty)){
				if(!CheckOrder.checkDouble(qty)){
					errorMsg += ("【商品数量】("+qty+")格式类型有误;");
				}
			}else{
				errorMsg += ("【商品数量】不能为空;");
			}
			
			if(StringUtils.isNotEmpty(qty1)){
				if(!CheckOrder.checkDouble(qty1)){
					errorMsg += ("【法定计量单位数量】("+qty1+")格式类型有误;");
				}
			}else{
				errorMsg += ("【法定计量单位数量】不能为空;");
			}
			
			if(StringUtils.isNotEmpty(unit)){
				if(!CheckOrder.checkUnit(unit)){
					errorMsg += ("【单位】("+unit+")有误，系统不存在此单位名称，请联系管理员维护;");
				}
			}else{
				errorMsg += ("【单位】不能为空;");
			}
			
			if(StringUtils.isNotEmpty(unit1)){
				if(!CheckOrder.checkUnit(unit1)){
					errorMsg += ("【法定计量单位】("+unit1+")有误，系统不存在此单位名称，请联系管理员维护;");
				}
			}else{
				errorMsg += ("【法定计量单位】不能为空;");
			}
			
			if(StringUtils.isNotEmpty(price)){
				if(!CheckOrder.checkDouble(price)){
					errorMsg += ("【单价】("+price+")格式类型有误;");
				}
			}else{
				errorMsg += ("【单价】不能为空;");
			}
			
			if(StringUtils.isNotEmpty(tax_rate)){
				if(!CheckOrder.checkDouble(tax_rate)){
					errorMsg += ("【税率】("+tax_rate+")格式类型有误;");
				}
			}
			
			if(StringUtils.isEmpty(gcode)){
				errorMsg += ("【十位税号】不能为空;");
			}
			
			if(StringUtils.isEmpty(g_model)){
				errorMsg += ("【品牌规格型号等】不能为空;");
			}
			
			if(StringUtils.isEmpty(ciq_gno)){
				errorMsg += ("【检验检疫商品备案号】不能为空;");
			}
			
			if(StringUtils.isEmpty(ciq_gmodel)){
				errorMsg += ("【检验检疫商品规格型号】不能为空;");
			}
			
		}
		
		if(StringUtils.isNotEmpty(order_no)){
			if(!CheckOrder.checkRefOrderNo(order_no)){
				errorMsg += ("此【订单编号】("+order_no+")已存在，请核实是否有重复导入;");
			}
		}else{
			errorMsg += ("【订单编号】不能为空;>");
		}
		
		if(StringUtils.isEmpty(consignee)){
			errorMsg += ("【收货人姓名】不能为空;");
		}
		if(StringUtils.isEmpty(consignee_telephone)){
			errorMsg += ("【收货人电话】不能为空;");			
		}
		if(StringUtils.isEmpty(consignee_address)){
			errorMsg += ("【收货人地址】不能为空;");
		}else{
			String addressCode = CheckOrder.changeAddres(consignee_address);
			if(!checkCode(addressCode)){
				errorMsg += ("【收货人详细地址】("+consignee_address+")有误,请检测录入的省市区地址是否正确,注意：请按照【xx省 xx市 xx区/县 xxxxx】格式填写地址，省市区中间必须以空格隔开;");
			}
		}
		
		if(StringUtils.isEmpty(buyer_name)){
			errorMsg += ("【订购人姓名】不能为空;");			
		}
		if(StringUtils.isNotEmpty(buyer_id_number)){
			if(!IdcardUtil.isIdcard(buyer_id_number))
				errorMsg += ("【订购人身份证号】不是合法的身份证号码;");
		}else{
			errorMsg += ("【订购人身份证号】不能为空;");
		}
		
		if(StringUtils.isEmpty(cop_no)){
			errorMsg += ("【企业内部标识单证编号】不能为空;");			
		}

		if(StringUtils.isNotEmpty(weight)){
			if(!CheckOrder.checkDouble(weight)){
				errorMsg += ("【毛重】("+weight+")格式类型有误;");
			}
		}else{
			errorMsg += ("【毛重】不能为空;");	
		}
		
		if(StringUtils.isNotEmpty(net_weight)){
			if(!CheckOrder.checkDouble(net_weight)){
				errorMsg += ("【净重】("+net_weight+")格式类型有误;");
			}
		}else{
			errorMsg += ("【净重】不能为空;");	
		}
		
		if(StringUtils.isEmpty(goods_info)){
			errorMsg += ("【主要货物信息】不能为空;");			
		}
		
		if(StringUtils.isNotEmpty(pack_no)){
			if(!CheckOrder.checkDouble(pack_no)){
				errorMsg += ("【包裹数】("+pack_no+")格式类型有误;");
			}
		}else{
			errorMsg += ("【包裹数】不能为空;");	
		}
		
		if(StringUtils.isEmpty(wrap_type)){
			errorMsg += ("【包装种类】不能为空;");			
		}
		
		if(StringUtils.isNotEmpty(errorMsg)){
			r.set("code", "6");
			r.set("msg", errorMsg);
		}
    	return r;
    }
    
    @Before(Tx.class)
    public void saveSo() throws InstantiationException, IllegalAccessException{
        String orderJsonStr = controller.getPara("order");
        //Record re = Db.findFirst("select * from customize_field where order_type = 'testJson'");
		//String orderJsonStr = re.getStr("field_code");
        
        if(orderJsonStr==null){
            orderJsonStr = ApiController.getRequestPayload(controller.getRequest());
        }
        
        
        try {
			orderJsonStr = URLDecoder.decode(orderJsonStr,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("接收的数据："+orderJsonStr);
        
        if(orderJsonStr==null){
            Record r = new Record();
            r.set("code", "2");
            r.set("msg", "POST请求中, body不存在!");
            controller.renderJson(r);
            return;
        }
        
        
        //开始生成so
        Map<String, ?> soDto= new Gson().fromJson(orderJsonStr, HashMap.class);
        Long office_id = null;
        //数据校验
        Record configR = checkConfig(soDto);
        if("0".equals(configR.getStr("code"))){
        	office_id = configR.getLong("office_id");
        	Record errorR = checkOrder(soDto);
        	if(!"00".equals(errorR.getStr("code"))){
            	controller.renderJson(errorR);
                return;// 注意这里一定要返回,否则会继续往下执行
        	}
        }else{
        	controller.renderJson(configR);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        
        
        UserLogin ul = UserLogin.dao.findFirst("select * from user_login where user_name = ?","interface@defeng.com");

        SalesOrder salesOrder = new SalesOrder();
        DbUtils.setModelValues(soDto, salesOrder);
        String order_no = soDto.get("order_no").toString().trim();
        //需后台处理的字段
        salesOrder.set("ref_order_no", OrderNoGenerator.getNextOrderNo("IDQHDF")); 
        salesOrder.set("logistics_no", "IYQHDF"+CheckOrder.getDouble(order_no));
        salesOrder.set("custom_id", 9);  //深圳前海德丰投资发展有限公司
        salesOrder.set("status", "未上报");  //状态
        salesOrder.set("is_pay_pass","0");  //是否已完成申报
        salesOrder.set("pay_channel", "01");//支付渠道
        salesOrder.set("pay_type", "PTL");//支付渠道
        salesOrder.set("assure_code", "440300349779838");// 担保企业编号
        salesOrder.set("ems_no", "I440366006516001"); //电商账册编号
        salesOrder.set("traf_mode", 4); //运输方式
        salesOrder.set("ship_name", "汽车"); //运输工具名称
        salesOrder.set("ciq_code","471800"); //主管检验检疫机构代码
        salesOrder.set("supervision_code","5349"); //进出口岸代码
        salesOrder.set("country_code", "142");  //起运国
        salesOrder.set("customs_code","5349"); //主管海关代码 
        salesOrder.set("business_mode","2"); //业务模式代码 *
        salesOrder.set("trade_mode","1210"); //贸易方式 
        salesOrder.set("sign_company","ISDFIJPMRSDIP001"); //承运企业海关备案号 
        salesOrder.set("sign_company_name","德邦物流"); //承运企业名称 
        salesOrder.set("port_code","5349"); //进出口岸代码
        salesOrder.set("ie_date",new Date()); //进口日期
        salesOrder.set("wh_org_code","349779838"); //企业组织机构代码（仓储企业）
        salesOrder.set("freight", 0);  //运杂费
        salesOrder.set("insure_fee", 0); //保价费
        String buyer_id_number = soDto.get("buyer_id_number").toString().trim();
        salesOrder.set("buyer_regno", buyer_id_number); //订购人注册号(默认为身份证号)
        
        String consignee_address = soDto.get("consignee_address").toString().trim();
        if(StringUtils.isNotEmpty(consignee_address)){
			String addressCode = CheckOrder.changeAddres(consignee_address);
			String[] values = addressCode.split("#");
			String province = values[0];//省
			String city = values[1];//市
			String qv = values[2];//区
			salesOrder.set("province", province);
			salesOrder.set("city", city);
			salesOrder.set("district", qv);
		}
		
        salesOrder.set("create_stamp", new Date());  //操作时间
        salesOrder.set("create_by", ul.getLong("id"));  //操作人
        salesOrder.set("note", "接口数据");  //备注
        salesOrder.set("office_id", office_id);

        salesOrder.save();
        String id = salesOrder.getLong("id").toString();
       
    	//明细表
    	List<Map<String, String>> itemLists = (ArrayList<Map<String, String>>)soDto.get("goods");
    	String cargo_name = null;
		for(Map<String, String> itemList:itemLists){		
			String bar_code = itemList.get("bar_code")==null?null:itemList.get("bar_code").trim();
			String item_name = itemList.get("item_name")==null?null:itemList.get("item_name").trim();
			String item_no = itemList.get("item_no")==null?null:itemList.get("item_no").trim();
			String qty = itemList.get("qty")==null?null:itemList.get("qty").trim();
			String unit = itemList.get("unit")==null?null:itemList.get("unit").trim();
			String qty1 = itemList.get("qty1")==null?null:itemList.get("qty1").trim();
			String unit1 = itemList.get("unit1")==null?null:itemList.get("unit1").trim();
			String price = itemList.get("price")==null?null:itemList.get("price").trim(); 
			String tax_rate = itemList.get("tax_rate")==null?null:itemList.get("tax_rate").trim(); 
			String gcode = itemList.get("gcode")==null?null:itemList.get("gcode").trim(); 
			String g_model = itemList.get("g_model")==null?null:itemList.get("g_model").trim(); 
			String ciq_gno = itemList.get("ciq_gno")==null?null:itemList.get("ciq_gno").trim();
			String ciq_gmodel = itemList.get("ciq_gmodel")==null?null:itemList.get("ciq_gmodel").trim();
			//子表数据保存
			SalesOrderGoods sog = new SalesOrderGoods();
			if(StringUtils.isNotEmpty(bar_code)){
				sog.set("bar_code", bar_code);   //条码
			}
			
			sog.set("item_name", item_name);//名称
			sog.set("item_no", item_no); //商品货号
			sog.set("qty", qty);   //数量
			sog.set("qty1", qty1);   //数量
			if(StringUtils.isNotEmpty(unit)){
				Unit unitRec = Unit.dao.findFirst("select * from unit where name = ?",unit);
				if(unitRec!=null)
					sog.set("unit", unitRec.getStr("code"));   //单位
			}
			if(StringUtils.isNotEmpty(unit1)){
				Unit unitRec = Unit.dao.findFirst("select * from unit where name = ?",unit1);
				if(unitRec!=null)
					sog.set("unit1", unitRec.getStr("code"));   //单位
			}
			sog.set("price", price);   //单位
			sog.set("tax_rate", tax_rate);   //税率
			sog.set("gcode", gcode);   //税号
			sog.set("g_model", g_model);   //(品牌规格型号等)
			sog.set("ciq_gno", ciq_gno);   //检验检疫商品备案号
			sog.set("ciq_gmodel", ciq_gmodel);   //检验检疫商品规格型号
			
			if(StringUtils.isNotEmpty(price)&&StringUtils.isNotEmpty(qty)){
				DecimalFormat df = new DecimalFormat("#.00");
				String total = df.format(Double.parseDouble(price)*Double.parseDouble(qty));
				sog.set("total", CheckOrder.changeNum(Double.parseDouble(total)));   //总价
				salesOrder.set("goods_value", CheckOrder.changeNum(Double.parseDouble(total))).update();  
			}
			
			
			if(StringUtils.isNotEmpty(price)&&StringUtils.isNotEmpty(qty)&&StringUtils.isNotEmpty(tax_rate)){
				String tax_total = CheckOrder.changeNum(Double.parseDouble(price)*Double.parseDouble(qty)*(Double.parseDouble(tax_rate)+1));
				sog.set("after_tax_total", CheckOrder.changeNum(Double.parseDouble(tax_total)));   //税后总价
				salesOrder.set("actural_paid", CheckOrder.changeNum(Double.parseDouble(tax_total))).update();   //税后总价
			}
			sog.set("order_id",salesOrder.get("id"));
			sog.set("currency","142");
			sog.set("country","142");
			sog.set("brand","无");
			sog.save();
		}
		
		OrderActionLog log = new OrderActionLog();
        log.set("order_type", "api_so_post");
        log.set("action", "post");
        log.set("json", orderJsonStr);
        log.set("time_stamp", new Date());
        log.save();
			
		
		String orgCode = soDto.get("org_code").toString();
        DingDanDto returnSoDto= DingDanBuilder.buildDingDanDto(id, orgCode);
        Record r = new Record();
        r.set("code", "0");
        r.set("msg", "已生成订单!");

        r.set("data", returnSoDto);
        controller.renderJson(r);
    }

 
    public void querySo(){
        String orderJsonStr = controller.getPara("orderQuery");//form 提交

        if (orderJsonStr == null) {//raw 提交
            orderJsonStr = ApiController.getRequestPayload(controller.getRequest());
        }
        
        try {
			orderJsonStr = URLDecoder.decode(orderJsonStr,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
        String order_no = itemDto.get("order_no").toString();
        String appkey = itemDto.get("appkey").toString();
        String salt = itemDto.get("salt").toString();
        String sign = itemDto.get("sign").toString();
        
        String paraStr = "order_no="+order_no+"&appkey="+appkey+"&salt="+salt+"&sign="+sign;
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
        
        SalesOrder so = SalesOrder.dao.findFirst("select * from sales_order where order_no = ?", order_no);
        if(so !=null){
            DingDanDto soDto= DingDanBuilder.buildDingDanDto(so.getLong("id").toString(), "");
            
            String ceb_report = so.getStr("ceb_report");
            String return_status = so.getStr("return_status");
            String return_info = so.getStr("return_info");
            String return_time = so.getStr("return_time");
            String logistics_no = so.getStr("logistics_no");
            String cop_no = so.getStr("cop_no");
            String bill_no = so.getStr("bill_no");
            String status = so.getStr("status");
            String pay_no = so.getStr("pay_no");
            String pay_time = so.getDate("pay_time").toString();
            
            
            Record r = new Record();
            r.set("code", "0");
            r.set("status",status);
            r.set("ceb_report",ceb_report);
            r.set("return_status",return_status);
            r.set("return_info",return_info);
            r.set("return_time",return_time);
            r.set("logistics_no",logistics_no);
            r.set("cop_no",cop_no);
            r.set("bill_no",bill_no);
            r.set("org_code","349779838");
            
            if(StringUtils.isNotEmpty(pay_no)){
            	r.set("pay_status","已付款");
            	r.set("pay_time", pay_time);
            }else{
            	r.set("pay_status","未付款");
            	r.set("pay_time", "");
            }
            
            r.set("msg", "请求已成功处理!");
            r.set("data", soDto);
            controller.renderJson(r);            
        }else{
            Record r = new Record();
            r.set("code", "1");
            r.set("msg", "订单号码:"+order_no+"不存在!");
            controller.renderJson(r);
        }
    }
}
