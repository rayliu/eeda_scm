package controllers.oms.logisticsOrder;

import interceptor.SetAttrLoginUserInterceptor;

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

import controllers.oms.custom.CustomManager;
import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.YunDanDto;
import controllers.oms.salesOrder.SalesOrderController;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;
import controllers.yh.job.CustomJob;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class LogisticsOrderController extends Controller {

	private Logger logger = Logger.getLogger(LogisticsOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();
	private static String orgCode="349779838";
//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/logisticsOrder/logisticsOrderList.html");
	} 
	
    public void create() {
        render("/oms/logisticsOrder/logisticsOrderEdit.html");
    } 
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
       	
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
   			logisticsOrder.update();
   			
   			CustomJob.operationLog("logisticsOrder", jsonStr, id, "update", LoginUserController.getLoginUserId(this).toString());
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, logisticsOrder);
   			
   			//需后台处理的字段
   			logisticsOrder.set("log_no", OrderNoGenerator.getNextOrderNo("YD"));
   			logisticsOrder.set("create_by", user.getLong("id"));
   			logisticsOrder.set("create_stamp", new Date());
   			logisticsOrder.save();
   			
   			id = logisticsOrder.getLong("id").toString();
   			CustomJob.operationLog("logisticsOrder", jsonStr, id, "create", LoginUserController.getLoginUserId(this).toString());
   		}
   		
   		long create_by = logisticsOrder.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);

//   	List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("cargo_list");
//		DbUtils.handleList(itemList, id, Goods.class, "order_id");
   		Record r = logisticsOrder.toRecord();
   		r.set("create_by_name", user_name);

   		renderJson(r);
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
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        String sql = "SELECT lor.*,ccy.shop_name custom_name, ifnull(u.c_name, u.user_name) creator_name "
    			+ "  from logistics_order lor "
    			+ "  left join sales_order sor on sor.id = lor.sales_order_id "
    			+ "  left join custom_company ccy on ccy.id = sor.custom_id "
    			+ "  left join user_login u on u.id = lor.create_by"
    			+ "   where 1 =1 ";
        
        String condition = DbUtils.buildConditions(getParaMap());

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
         String urlStr="http://test.szedi.cn:8088/phy-ceb-web/tgt/service/logistics_create.action";
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
