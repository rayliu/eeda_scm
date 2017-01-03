package controllers.oms.storageInOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.UserLogin;
import models.eeda.oms.LogisticsOrder;
import models.eeda.oms.SalesOrder;
import models.eeda.profile.CustomCompany;
import net.sf.json.xml.XMLSerializer;

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

import controllers.oms.custom.dto.YunDanDto;
import controllers.oms.yunda.DataSecurity;
import controllers.oms.yunda.Item;
import controllers.oms.yunda.Items;
import controllers.oms.yunda.Order;
import controllers.oms.yunda.Orders;
import controllers.oms.yunda.Receiver;
import controllers.oms.yunda.Sender;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.JaxbUtil;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;
import controllers.yh.job.CustomJob;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class StorageInController extends Controller {

	private Logger logger = Logger.getLogger(StorageInController.class);
	Subject currentUser = SecurityUtils.getSubject();
	private static String orgCode="349779838";
//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/storageInOrder/list.html");
	} 
	
    public void create() {
        render("/oms/storageInOrder/edit.html");
    } 
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
   		String msg = null;
   		String mail_no =null;
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        LogisticsOrder logisticsOrder = new LogisticsOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			logisticsOrder = LogisticsOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, logisticsOrder);
   			
   			//需后台处理的字段
   			logisticsOrder.set("update_by", user.getLong("id"));
   			logisticsOrder.set("update_stamp", new Date());
   			
   			//韵达下单
//   			String xml = null;
//   			String parcel_info = logisticsOrder.getStr("parcel_info");
//   			if(!"YD".equals(parcel_info.substring(0, 2))){
//   				xml = createYunda(dto , "update"); 
//   			}else{
//   				xml = createYunda(dto, "create"); 
//   			} 
//   	        String js = (JaxbUtil.xmltoJson(xml)).toString();
//   	        System.out.println(js);
//   	        Map<String, Map> xmlJson= gson.fromJson(js, HashMap.class);  
//   	        Map responses = xmlJson.get("response");
//   	        
//   	        msg = responses.get("msg").toString();
//   	        System.out.println("韵达快递单号："+msg);
//   	        
//   	        if("YD".equals(parcel_info.substring(0, 2))){
//   	        	mail_no =  responses.get("mail_no").toString();
//   	        	logisticsOrder.set("parcel_info",mail_no);
//   	   	        
//   	        }
   	        
   			logisticsOrder.update();
   			CustomJob.operationLog("logisticsOrder", jsonStr, id, "update", LoginUserController.getLoginUserId(this).toString());
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, logisticsOrder);
   			
   			//需后台处理的字段
   			logisticsOrder.set("log_no", OrderNoGenerator.getNextOrderNo("IYQHDF"));
   			logisticsOrder.set("create_by", user.getLong("id"));
   			logisticsOrder.set("create_stamp", new Date());
   			logisticsOrder.save();
   			
   			id = logisticsOrder.getLong("id").toString();
   			CustomJob.operationLog("logisticsOrder", jsonStr, id, "create", LoginUserController.getLoginUserId(this).toString());
   		}
   		
   		long create_by = logisticsOrder.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);

   		Record r = logisticsOrder.toRecord();
   		r.set("create_by_name", user_name);
   		r.set("msg",msg);
   		r.set("mail_no",mail_no);

   		renderJson(r);
   	}
    
    @Before(Tx.class)
    public String createYunda(Map<String, ?> dto,String action){
    	String XmlValue = createXml(dto);
    	String returnMsg = null;
    	
    	String partnerid ="7690811002";
        String pwd = "uam4WYkE5GAyxjJC2XVMSTUh7r6gBD";
        String version = "1.0";
        
        if("create".equals(action)){
        	action = "interface_receive_order__mailno.php";//下单
        }else if("update".equals(action)){
        	action = "interface_modify_order.php";//更新
        }
        
        String urlStr="http://orderdev.yundasys.com:10110/cus_order/order_interface/"+action;
        
        String data;
		try {
			data = DataSecurity.security(partnerid, pwd, XmlValue);
			
			System.out.println(data);
			data += "&version=" + version + "&request=data";
	        
	        returnMsg = EedaHttpKit.post(urlStr, data);
	        System.out.println("结果"+returnMsg);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnMsg;
    }
    
    public String createXml(Map<String, ?> dto){
    	Orders orders = new Orders();
    	Order order = new Order();
    	
    	String id = (String) dto.get("id");
    	String order_no = (String) dto.get("order_no");
    	String log_no = (String) dto.get("log_no");
    	
    	order.setOrder_serial_no(order_no);//订单号（必填）-------------------1
    	order.setKhddh(order_no);//大客户系统订单的订单号（必填）
    	order.setNbckh(id);//内部参考号-------------------1
    	order.setOrder_type("");//运单类型

    	//发货方信息
    	String shipper = (String) dto.get("shipper");
    	String shipper_city = ((String) dto.get("shipper_city_INPUT")).replaceAll("-", ",");
    	String shipper_address = (String) dto.get("shipper_address");
    	String shipper_telephone = (String) dto.get("shipper_telephone");
    	
    	Sender sender = new Sender();
    	sender.setName(shipper);//姓名（必填）----------shipper
    	sender.setCompany("");//公司名
    	sender.setCity(shipper_city);//城市（上海市，上海市，青浦区）-----------------shipper_city
    	sender.setAddress(shipper_address);//地址（上海市，上海市，青浦区XXX路XXX号）（必填）-----------------------shipper_address
    	sender.setPostcode("");//邮编
    	sender.setPhone(shipper_telephone);//固定电话（必填）----------------shipper_telephone
    	sender.setMobile(shipper_telephone);//移动电话（必填）----------------------------shipper_telephone
    	sender.setBranch("");//
    	order.setSender(sender);
    	
    	//收货方信息
    	String consignee = (String) dto.get("consignee");
    	String sales_pro_ci_dis = ((String) dto.get("sales_pro_ci_dis_INPUT")).replaceAll("-", ",");
    	String consignee_address = (String) dto.get("consignee_address");
    	String consignee_telephone = (String) dto.get("consignee_telephone");
    	
    	Receiver receiver = new Receiver();
    	receiver.setName(consignee);//------------------------consignee
    	receiver.setCompany("");//
    	receiver.setCity(sales_pro_ci_dis);//----------------------sales_pro_ci_dis
    	receiver.setAddress(consignee_address);//-----------------consignee_address
    	receiver.setPostcode("");//
    	receiver.setPhone(consignee_telephone);//-------------------consignee_telephone
    	receiver.setMobile(consignee_telephone);//-------------------consignee_telephone
    	receiver.setBranch("");//
    	order.setReceiver(receiver);
    	//商品信息
    	String weight = (String)dto.get("weight");

    	order.setWeight(Double.parseDouble(weight));//物品重量--------
    	order.setSize("");//尺寸
    	//order.setValue(0);//货品金额
    	//order.setCollection_value(1.0);//代收货款金额
    	//order.setSpecial(2);//商品性质
    	//子表
    	List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("cargo_list");
    	List<Item> item = new ArrayList<Item>();
    	for(Map<String, String> list:itemList){
    		String itemName = list.get("item_name");
    		String itemAmount = list.get("sty");

        	Item im = new Item();
        	im.setName(itemName);//商品名称
        	im.setNumber(Integer.parseInt(itemAmount));//商品数量
        	im.setRemark("");//商品备注
        	item.add(im);
    	}
    	Items items = new Items();
    	items.setItem(item);
    	
    	String note = (String) dto.get("note");
    	order.setRemark(note);//订单备注
    	order.setCus_area1("");//自定义显示信息1
    	order.setCus_area2("");//自定义显示信息2
    	order.setCallback_id("");//接口异步回传的时候返回的ID
    	order.setWave_no("");//客户波次好
    	orders.setOrder(order);
    	
    	String str = JaxbUtil.convertToXml(orders);  
        System.out.println(str); 
        return str;
    }
    
   
    
    public List<Record> getSalesOrderGoods(long orderId) {
		String itemSql = "select * from sales_order_goods where order_id=?";
		List<Record> itemList = Db.find(itemSql, orderId);
		return itemList;
	}
    
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	LogisticsOrder logisticsOrder = LogisticsOrder.dao.findById(id);
    	setAttr("order", logisticsOrder);
    	
    	//订单ID
    	SalesOrder salesOrder = SalesOrder.dao.findById(logisticsOrder.getLong("sales_order_id"));
    	setAttr("salesOrder", salesOrder);
    	if(salesOrder != null){
    		long sales_order_id = logisticsOrder.getLong("sales_order_id");
    		long custom_id = salesOrder.getLong("custom_id");
    		
    		//获取明细表信息
        	setAttr("itemList", getSalesOrderGoods(sales_order_id));
        	
        	//获取报关企业信息
        	CustomCompany custom = CustomCompany.dao.findById(custom_id);
        	setAttr("custom", custom);

        	//收货人地址
        	String district = salesOrder.getStr("district");
        	String province = salesOrder.getStr("province");
        	String city = salesOrder.getStr("city");
        	Record re = Db.findFirst("select get_loc_full_name(?) address",district);
        	setAttr("sales_pro_ci_dis_name", re.get("address"));
        	String sales_pro_ci_dis = province+"-"+city+"-"+district;
        	setAttr("sales_pro_ci_dis", sales_pro_ci_dis);
    	}
    	
    	//收货城市
    	String shipper_city = logisticsOrder.getStr("shipper_city");
    	Record re = Db.findFirst("select get_loc_full_name(?) address",shipper_city);
    	setAttr("shipper_city_name", re.get("address"));
    	
    	//用户信息
    	long create_by = logisticsOrder.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
        render("/oms/logisticsOrder/logisticsOrderEdit.html");
    }
 
    public void list() {
    	String sLimit = "";
    	String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }
        UserLogin user = LoginUserController.getLoginUser(this);
        Long office_id = user.getLong("office_id");
        String sql = "select * from(SELECT lor.*,ccy.shop_name custom_name, ifnull(u.c_name, u.user_name) creator_name "
    			+ "  from logistics_order lor "
    			+ "  left join sales_order sor on sor.id = lor.sales_order_id "
    			+ "  left join custom_company ccy on ccy.id = sor.custom_id "
    			+ "  left join user_login u on u.id = lor.create_by"
    			+ "  ) A where 1 =1 and office_id="+office_id;
        
        String condition = "";
        String jsonStr = getPara("jsonStr");
    	if(StringUtils.isNotEmpty(jsonStr)){
    		Gson gson = new Gson(); 
            Map<String, String> dto= gson.fromJson(jsonStr, HashMap.class);  
            condition = DbUtils.buildConditions(dto);
    	}

        String sqlTotal = "select count(1) total from ("+sql+ condition+") B";
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));
        
        List<Record> BillingOrders = Db.find(sql+ condition + " order by create_stamp desc " +sLimit);
        Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", pageIndex);
        BillingOrderListMap.put("iTotalRecords", rec.getLong("total"));
        BillingOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        BillingOrderListMap.put("aaData", BillingOrders);

        renderJson(BillingOrderListMap); 
    }
    
    public void getCustomCompany() {
    	String custom_id = getPara("params");
    	CustomCompany customCompany = CustomCompany.dao.findById(custom_id);
    	renderJson(customCompany);
    }

    public void submitYunDan(){
    	 String order_id = getPara("order_id");
    	 String jsonMsg=setLogMsg(order_id);
         TreeMap<String, String> paramsMap = new TreeMap<String, String>();
         String urlStr=PropKit.use("app_config.txt").get("szediUrl")+"/tgt/service/logistics_create.action";
         paramsMap.put("jsonMsg", jsonMsg);
         String PostData = "";
         PostData = paramsMap.toString().substring(1);
         System.out.println("参数"+PostData);
         String returnMsg = EedaHttpKit.post(urlStr, PostData);
         //String returnMsg = InUtil.getResult(urlStr, PostData);
         System.out.println("结果"+returnMsg);
         CustomJob.operationLog("logisticsOrder", jsonMsg, order_id, "submitYunDan", LoginUserController.getLoginUserId(this).toString());
         renderJson(returnMsg);
    }
    
  //YunDan
  	public static String setLogMsg(String order_id) {
  		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
  		paramsMap.put("orgcode", orgCode);
  		paramsMap.put("appkey", "defeng");
  		String appsecret = MD5Util.encodeByMD5("888888");
  		paramsMap.put("appsecret", appsecret);
  		String timestamp = "" + (System.currentTimeMillis() / 1000);
  		paramsMap.put("timestamp", timestamp);

  		String sign = MD5Util.encodeByMD5(paramsMap + appsecret);// 888888

  		System.out.println("参数:" + paramsMap + appsecret);
  		paramsMap.put("sign", sign);

  		Map<Object, Object> requestMap = new LinkedHashMap<Object, Object>();

  		Gson gson = new Gson();
  		requestMap.put("postHead", gson.toJson(paramsMap));

  		
  		LogisticsOrder logisticsOrder = LogisticsOrder.dao.findById(order_id);
  		long sales_order_id = logisticsOrder.getLong("sales_order_id");
  		SalesOrder salesOrder = SalesOrder.dao.findById(sales_order_id);
  		//YunDan  order 业务数据
  		YunDanDto log = new YunDanDto();
  		log.setOrg_code(orgCode);
  		log.setOrder_no(salesOrder.getStr("order_no"));
  		log.setCurrency(salesOrder.getStr("currency"));// 默认人民币
  		log.setConsignee(salesOrder.getStr("consignee"));
  		log.setConsignee_address(salesOrder.getStr("consignee_address"));
  		log.setConsignee_telephone(salesOrder.getStr("consignee_telephone"));
  		log.setConsignee_country(salesOrder.getStr("consignee_country"));
  		log.setConsignee_type(salesOrder.getStr("consignee_type"));
  		log.setConsignee_id(salesOrder.getStr("consignee_id"));
  		log.setProvince(salesOrder.getStr("province"));
  		log.setCity(salesOrder.getStr("city"));
  		log.setDistrict(salesOrder.getStr("district"));
  		
  		log.setReport_pay_no(logisticsOrder.getStr("report_pay_no"));
  		log.setWeight(logisticsOrder.getDouble("weight"));
  		log.setNetwt(logisticsOrder.getDouble("netwt"));
  		log.setLog_no(logisticsOrder.getStr("log_no"));
  		log.setCountry_code(logisticsOrder.getStr("country_code"));
  		log.setShipper(logisticsOrder.getStr("shipper"));
  		log.setShipper_country(logisticsOrder.getStr("shipper_country"));
  		log.setShipper_city(logisticsOrder.getStr("shipper_city"));
  		log.setShipper_telephone(logisticsOrder.getStr("shipper_telephone"));
  		log.setShipper_address(logisticsOrder.getStr("shipper_address"));
  		log.setTraf_mode(logisticsOrder.getStr("traf_mode"));
  		log.setShip_name(logisticsOrder.getStr("ship_name"));
  		log.setPack_no(logisticsOrder.getInt("pack_no"));
  		log.setGoods_info(logisticsOrder.getStr("goods_info"));
  		log.setCustoms_code(logisticsOrder.getStr("customs_code"));
  		log.setCiq_code(logisticsOrder.getStr("ciq_code"));
  		log.setParcel_info(logisticsOrder.getStr("parcel_info"));
  		
  		log.setPort_code(logisticsOrder.getStr("port_code"));
  		log.setDecl_code(logisticsOrder.getStr("decl_code"));
  		log.setSupervision_code(logisticsOrder.getStr("supervision_code"));
  		log.setEms_no(logisticsOrder.getStr("ems_no"));
  		log.setTrade_mode(logisticsOrder.getStr("trade_mode"));
  		log.setDestination_port(logisticsOrder.getStr("destination_port"));
  		log.setPs_type(logisticsOrder.getStr("ps_type"));
  		log.setTrans_mode(logisticsOrder.getStr("trans_mode"));
  		log.setCut_mode(logisticsOrder.getStr("cut_mode"));
  		log.setWrap_type(logisticsOrder.getStr("wrap_type"));
  		
  		String ie_date = logisticsOrder.getDate("ie_date").toString();
  		log.setIe_date(ie_date.substring(0, ie_date.length()-2));

  		
  		List<YunDanDto> orderList=new ArrayList<YunDanDto>();
  		orderList.add(log);
//  	orderList.add(log1);

  		requestMap.put("total_count", orderList.size());
  		requestMap.put("logistics", orderList);
  		
  		Gson gson1 = new Gson();
  		String jsonMsg = gson1.toJson(requestMap);
  		
  		System.out.println("参数:"+ jsonMsg);
  		return jsonMsg;
  	}

}
