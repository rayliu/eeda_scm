package controllers.oms.salesOrder;

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
import models.eeda.oms.SalesOrderCount;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.oms.SalesOrder;
import models.eeda.profile.Country;
import models.eeda.profile.CustomCompany;
import models.eeda.profile.Warehouse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import config.EedaConfig;
import controllers.oms.custom.dto.DingDanBuilder;
import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.DingDanGoodsDto;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;
import controllers.util.PermissionConstant;
import controllers.yh.job.CustomJob;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class SalesOrderController extends Controller {

	private Logger logger = Logger.getLogger(SalesOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/salesOrder/salesOrderList.html");
	}
	
    public void create() {
        //构造支付url时需要服务器名字,达到动态改UAT, PROD的效果
        setAttr("serverName", EedaConfig.sysProp.getProperty("allinpayServer"));
        
        render("/oms/salesOrder/salesOrderEdit.html");
    }
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
   		Long log_id = null;
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        SalesOrder salesOrder = new SalesOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			salesOrder = SalesOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, salesOrder);
   			
   			//需后台处理的字段
   			salesOrder.set("update_by", user.getLong("id"));
   			salesOrder.set("update_stamp", new Date());
   			salesOrder.update();
   			
   			log_id = createLogOrder(id,"update");
   			
   			CustomJob.operationLog("salesOrder", jsonStr, id, "update", LoginUserController.getLoginUserId(this).toString());
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, salesOrder);
   			
   			//需后台处理的字段
   			salesOrder.set("order_no", OrderNoGenerator.getNextOrderNo("DD"));
   			salesOrder.set("create_by", user.getLong("id"));
   			salesOrder.set("create_stamp", new Date());
   			salesOrder.save();
   			
   			//生成对应的运输单
   			id = salesOrder.getLong("id").toString();
   			log_id = createLogOrder(id,"create");
   			
   			CustomJob.operationLog("salesOrder", jsonStr, id, "create", LoginUserController.getLoginUserId(this).toString());
   		}
   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("cargo_list");
		DbUtils.handleList(itemList, id, SalesOrderGoods.class, "order_id");
		
		List<Map<String, String>> countList = (ArrayList<Map<String, String>>)dto.get("count_list");
		DbUtils.handleList(countList, id, SalesOrderCount.class, "order_id");

		long create_by = salesOrder.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);

   		Record r = salesOrder.toRecord();
   		r.set("create_by_name", user_name);
   		r.set("log_id",log_id);
   		renderJson(r);
   	}
    
    
    //自动生成运输单
    @Before(Tx.class)
    public Long createLogOrder(String sales_order_id,String action){
    	LogisticsOrder logisticsOrder = null;
    	if("create".equals(action)){
    		logisticsOrder = new LogisticsOrder();
        	logisticsOrder.set("log_no", OrderNoGenerator.getNextOrderNo("YD"));
        	logisticsOrder.set("sales_order_id",sales_order_id);
        	logisticsOrder.set("status","新建");
    		logisticsOrder.set("create_by", LoginUserController.getLoginUserId(this));
    		logisticsOrder.set("create_stamp", new Date());
    		logisticsOrder.save();
    	}else{
    		logisticsOrder = LogisticsOrder.dao.findFirst("select * from logistics_order where sales_order_id = ?",sales_order_id);
    	}
		return logisticsOrder.getLong("id");
    }
    
    
    private List<Record> getSalesOrderGoods(String orderId) {
		String itemSql = "select * from sales_order_goods where order_id=?";
		List<Record> itemList = Db.find(itemSql, orderId);
		return itemList;
	}
    
    private List<Record> getSalesOrderCount(String orderId) {
		String countSql = "select * from sales_order_count where order_id=?";
		List<Record> countList = Db.find(countSql, orderId);
		return countList;
	}
    
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	SalesOrder salesOrder = SalesOrder.dao.findById(id);
    	setAttr("order", salesOrder);
    	
    	//获取明细表信息
    	setAttr("itemList", getSalesOrderGoods(id));
    	
    	//获取费用明细表信息
    	setAttr("countList", getSalesOrderCount(id));
    	
    	//获取报关企业信息
    	CustomCompany custom = CustomCompany.dao.findById(salesOrder.getLong("custom_id"));
    	setAttr("custom", custom);
    	
    	//国家回显
    	String sql = "select * from country where code = ?";
    	Country country = Country.dao.findFirst(sql,salesOrder.getStr("consignee_country"));
    	if(country!=null){
    		setAttr("country_name", country.getStr("chinese_name"));
    	}
    	
    	
    	//收货人地址
    	String district = salesOrder.getStr("district");
    	String province = salesOrder.getStr("province");
    	String city = salesOrder.getStr("city");
    	Record re = Db.findFirst("select get_loc_full_name(?) address",district);
    	setAttr("pro_ci_dis", re.get("address"));
    	String pro_ci_dis_id = province+"-"+city+"-"+district;
    	setAttr("pro_ci_dis_id", pro_ci_dis_id);
    	
    	//对应的运输单信息
    	Record log = Db.findFirst("select * from logistics_order where sales_order_id = ?",id);
    	setAttr("logOrder",log);
    	
    	//用户信息
    	Long create_by = salesOrder.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
    	//构造支付url时需要服务器名字,达到动态改UAT, PROD的效果
        setAttr("allinpayServer", EedaConfig.sysProp.getProperty("allinpayServer"));
        setAttr("allinpayCallbackServer", EedaConfig.sysProp.getProperty("allinpayCallbackServer"));
        setAttr("merchantId", EedaConfig.sysProp.getProperty("merchantId"));
        render("/oms/salesOrder/salesOrderEdit.html");
    }
    
    public void list() {
    	String sLimit = "";
    	String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }
        String sql = "select * from(SELECT sor.*, ifnull(u.c_name, u.user_name) creator_name ,"
    			+ "  c.shop_name from sales_order sor "
    			+ "  left join custom_company c on c.id = sor.custom_id"
    			+ "  left join user_login u on u.id = sor.create_by"
    			+ "  ) A where 1 =1 ";
        
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
        
        List<Record> BillingOrders = Db.find(sql+ condition + " order by create_stamp desc" +sLimit);
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

    public void submitDingDan(){
    	
    	String order_id = getPara("order_id");
    	
    	String jsonMsg=setOrderMsg(order_id);
    	TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		String urlStr="http://test.szedi.cn:8088/phy-ceb-web/tgt/service/order_create.action";
		
		paramsMap.put("jsonMsg", jsonMsg);
		String PostData = "";
		PostData = paramsMap.toString().substring(1);
		System.out.println("参数"+PostData);
		String returnMsg = EedaHttpKit.post(urlStr, PostData);
		//String returnMsg = InUtil.getResult(urlStr, PostData);
		System.out.println("结果"+returnMsg);
		Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(returnMsg, HashMap.class);  
        List<Map<String, String>> orders = (ArrayList<Map<String, String>>)dto.get("orders");
        String status = orders.get(0).get("message");
        if("订单写入成功".equals(status)){
        	SalesOrder salesOrder = SalesOrder.dao.findById(order_id);
        	salesOrder.set("status", status).update();
        }
        CustomJob.operationLog("salesOrder", jsonMsg, order_id, "submitDingDan", LoginUserController.getLoginUserId(this).toString());
        
		renderJson(returnMsg);
    }
    
    public static String setOrderMsg(String order_id) {
    	String orgCode="349779838";//接口企业代码
    	
    	
    	TreeMap<String, String> paramsMap = new TreeMap<String, String>();
        paramsMap.put("orgcode", orgCode);
        paramsMap.put("appkey", "defeng");
        String appsecret = MD5Util.encodeByMD5("888888");
        paramsMap.put("appsecret", appsecret);
        String timestamp = "" + (System.currentTimeMillis() / 1000);
        paramsMap.put("timestamp", timestamp);

        String sign = MD5Util.encodeByMD5(paramsMap + appsecret);// 888888
		
		System.out.println("参数:"+ paramsMap+appsecret);
		paramsMap.put("sign", sign);

		Map<Object, Object> requestMap = new LinkedHashMap<Object, Object>();
		
		Gson gson = new Gson(); 
		requestMap.put("postHead", gson.toJson(paramsMap));
		
		DingDanDto order = DingDanBuilder.buildDingDanDto(order_id, orgCode);
	
		List<DingDanDto> orderList=new ArrayList<DingDanDto>();
		orderList.add(order);
//		orderList.add(order1);

		requestMap.put("total_count", orderList.size());
		requestMap.put("orders", orderList);
		
		Gson gson1 = new Gson(); 
		String jsonMsg = gson1.toJson(requestMap);
		System.out.println("参数:"+ jsonMsg);
		return jsonMsg;
	}

    public void tableList(){
    	String order_id = getPara("order_id");
    	String table_type = getPara("table_type");
    	List<Record> list = null;
    	if("item".equals(table_type)){
    		list = getSalesOrderGoods(order_id);
    	}else if("count".equals(table_type)){
    		list = getSalesOrderCount(order_id);
    	}
    	
    	Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", 1);
        BillingOrderListMap.put("iTotalRecords", list.size());
        BillingOrderListMap.put("iTotalDisplayRecords", list.size());

        BillingOrderListMap.put("aaData", list);

        renderJson(BillingOrderListMap); 
    }
    
    
    public void searchOrderNo(){
    	String table = getPara("table");
    	String order_no = getPara("orderNo");
    	String condition = getPara("condition");

    	String conditions = " where 1 = 1";
    	if(!"".equals(order_no))
    		conditions += " and order_no like '%" + order_no + "%'";
    	
    	String sql = " select * from " +table;
    	List<Record> list = Db.find(sql + conditions + condition);
    	
    	renderJson(list);
    }

}
