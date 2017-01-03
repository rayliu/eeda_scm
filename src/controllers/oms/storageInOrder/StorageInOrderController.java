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

import models.StorageInOrder;
import models.UserLogin;
import models.eeda.oms.LogisticsOrder;
import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderGoods;
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

import config.EedaConfig;
import controllers.oms.custom.dto.DingDanBuilder;
import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.DingDanGoodsDto;
import controllers.oms.custom.dto.StorageInDto;
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
public class StorageInOrderController extends Controller {

	private Logger logger = Logger.getLogger(StorageInOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();
	private static String orgCode="349779838";
//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/storageInOrder/list.html");
	} 
	
    public void create() {
    	String order_id = getPara("order_id");
    	Record re = Db.findFirst("select * from storage_in_order where order_id = ?",order_id);
    	if(re == null){
    		SalesOrder sor = SalesOrder.dao.findById(order_id);
        	setAttr("salesOrder", sor);
        	//获取报关企业信息
        	CustomCompany custom = CustomCompany.dao.findById(sor.getLong("custom_id"));
        	setAttr("custom", custom);
    	}else{
    		setAttr("order", re);
    	}
    	
        render("/oms/storageInOrder/edit.html");
    } 
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
   		
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        StorageInOrder order = new StorageInOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			order = StorageInOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, order);
   			
   			//需后台处理的字段
   			order.set("update_by", user.getLong("id"));
   			order.set("update_stamp", new Date());
   	        
   			order.update();
   			CustomJob.operationLog("storageInOrder", jsonStr, id, "update", LoginUserController.getLoginUserId(this).toString());
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, order);
   			
   			//需后台处理的字段
   			order.set("create_by", user.getLong("id"));
   			order.set("create_stamp", new Date());
   			order.save();
   			
   			id = order.getLong("id").toString();
   			CustomJob.operationLog("storageInOrder", jsonStr, id, "create", LoginUserController.getLoginUserId(this).toString());
   		}
   		
   		long create_by = order.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);

   		Record r = order.toRecord();
   		r.set("create_by_name", user_name);

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
    	StorageInOrder order = StorageInOrder.dao.findById(id);
    	setAttr("order", order);
    	
    	//用户信息
    	long create_by = order.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
        render("/oms/storageInOrder/edit.html");
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

    public void submitOrder(){
    	 String order_id = getPara("order_id");
    	 String jsonMsg=setStorageInMsg(order_id);
         TreeMap<String, String> paramsMap = new TreeMap<String, String>();
         //String urlStr=PropKit.use("app_config.txt").get("szediUrl")+"/tgt/service/logistics_create.action";
         String urlStr=EedaConfig.sysProp.getProperty("szediUrl")+"/tgt/service/logistics_createStorageIn.action";
         paramsMap.put("jsonMsg", jsonMsg);
         String PostData = "";
         PostData = paramsMap.toString().substring(1);
         System.out.println("参数"+PostData);
         String returnMsg = EedaHttpKit.post(urlStr, PostData);
         //String returnMsg = InUtil.getResult(urlStr, PostData);
         System.out.println("结果"+returnMsg);
         CustomJob.operationLog("storageInOrder", jsonMsg, order_id, "submitStorgeIn", LoginUserController.getLoginUserId(this).toString());
         Gson gson = new Gson();  
         Map<String, ?> dto= gson.fromJson("{"+returnMsg+"}", HashMap.class);  
         Map<String, String> orders = (Map<String, String>)dto.get("cebJsonMsg");
         String submit_status = orders.get("message");
         StorageInOrder sio = StorageInOrder.dao.findById(order_id);
         sio.set("submit_status", submit_status).update();
         
         renderJson(orders);
    }
    
    //StorageIn
  	public static String setStorageInMsg(String order_id) {
  		String orgCode="349779838";//接口企业代码
    	TreeMap<String, String> paramsMap = new TreeMap<String, String>();
        paramsMap.put("orgcode", orgCode);
        paramsMap.put("appkey", "QHDF");
        String appsecret = MD5Util.encodeByMD5("888888");
        paramsMap.put("appsecret", appsecret);
        String timestamp = "" + (System.currentTimeMillis() / 1000);
        paramsMap.put("timestamp", timestamp);
        String sign = MD5Util.encodeByMD5(paramsMap + appsecret);
		paramsMap.put("sign", sign);
		Map<Object, Object> requestMap = new LinkedHashMap<Object, Object>();
		
		Gson gson = new Gson(); 
		requestMap.put("postHead", gson.toJson(paramsMap));
		
		StorageInDto order = buildStorageInDto(order_id, orgCode);

		//requestMap.put("total_count", orderList.size());
		requestMap.put("notify_url", "http://"+EedaConfig.sysProp.getProperty("callbackServer")+"/orderReturn/storageInResultRecv");
		requestMap.put("storage", order);
		
		Gson gson1 = new Gson(); 
		String jsonMsg = gson1.toJson(requestMap);
		return jsonMsg;
  	}
  	
  	
  	 public static StorageInDto buildStorageInDto(String order_id, String orgCode){
	  		StorageInOrder sio = StorageInOrder.dao.findById(order_id);
	  		//YunDan  order 业务数据
	  		StorageInDto sid = new StorageInDto();
	  		sid.setOrg_code(sio.getStr("org_code"));
	  		sid.setCustoms_code(sio.getStr("customs_code"));
	  		sid.setCop_no(sio.getStr("cop_no"));
	  		sid.setOperator_code(sio.getStr("operator_code"));
	  		sid.setOperator_name(sio.getStr("operator_name"));
	  		sid.setTraf_mode(sio.getStr("traf_mode"));
	  		sid.setTraf_no(sio.getStr("traf_no"));
	  		sid.setVoyage_no(sio.getStr("voyage_no"));
	  		sid.setBill_no(sio.getStr("bill_no"));
	  		sid.setLogistics_code(sio.getStr("logistics_code"));
	  		sid.setLogistics_name(sio.getStr("logistics_name"));
	  		sid.setUnload_location(sio.getStr("unload_location"));
	  		sid.setNote(sio.getStr("note"));
	  		sid.setLogistics_nos(sio.getStr("logistics_nos"));

	  		return sid;
     }
  	 
  	 public void querySubMsg(){
 		String order_id = getPara("order_id");
 		StorageInOrder sor = StorageInOrder.dao.findById(order_id);
 		
 		renderJson(sor);
 	}

}
