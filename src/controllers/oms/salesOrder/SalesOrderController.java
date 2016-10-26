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
import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderCount;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.profile.Country;
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

import config.EedaConfig;
import controllers.oms.custom.dto.DingDanBuilder;
import controllers.oms.custom.dto.DingDanDto;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;
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
        
        //构造支付url时需要服务器名字,达到动态改UAT, PROD的效果
        setAttr("allinpayServer", EedaConfig.sysProp.getProperty("allinpayServer"));
        setAttr("allinpayCallbackServer", EedaConfig.sysProp.getProperty("allinpayCallbackServer"));
        setAttr("merchantId", EedaConfig.sysProp.getProperty("merchantId"));
        
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
   			salesOrder.set("order_no", OrderNoGenerator.getNextOrderNo("IDQHDF"));
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
    		String order_no = OrderNoGenerator.getNextOrderNo("YD");
    		logisticsOrder = new LogisticsOrder();
        	logisticsOrder.set("log_no", order_no);
        	logisticsOrder.set("sales_order_id",sales_order_id);
        	logisticsOrder.set("status","暂存");
    		logisticsOrder.set("create_by", LoginUserController.getLoginUserId(this));
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
        
        String condition = " where 1 = 1 ";
        String jsonStr = getPara("jsonStr");
    	if(StringUtils.isNotEmpty(jsonStr)){
    		Gson gson = new Gson(); 
            Map<String, String> dto= gson.fromJson(jsonStr, HashMap.class);  
            String order_no = (String)dto.get("order_no");
            String status = (String)dto.get("status");
            String create_stamp_begin_time = (String)dto.get("create_stamp_begin_time");
            String create_stamp_end_time = (String)dto.get("create_stamp_end_time");
            
            //condition = DbUtils.buildConditions(dto);
            if(StringUtils.isNotEmpty(order_no)){
            	condition += " and sor.order_no like '%"+order_no+"%'";
            }
            if(StringUtils.isNotEmpty(status)){
            	condition += " and sor.status = '"+status+"'";
            }
            if(StringUtils.isEmpty(create_stamp_begin_time)){
            	create_stamp_begin_time = " 1970-01-01 00:00:00";
            }
            if(StringUtils.isNotEmpty(create_stamp_end_time)){
            	create_stamp_end_time = create_stamp_end_time+" 23:59:59";
            }else{
            	create_stamp_end_time = "2037-12-31 23:59:59";
            }
            condition += " and sor.create_stamp between '"+create_stamp_begin_time+"' and '"+create_stamp_end_time+"'";
    	}
    	
    	String coulmns = "select sor.*, ifnull(u.c_name, u.user_name) creator_name ,"
        		+ "  lor.logistics_ciq_status,lor.logistics_cus_status,lor.status log_status,"
    			+ "  c.shop_name ";
    	
        String sql = " from sales_order sor  LEFT JOIN logistics_order lor on lor.sales_order_id = sor.id"
    			+ "  left join custom_company c on c.id = sor.custom_id"
    			+ "  left join user_login u on u.id = sor.create_by"
    			+ condition;
        
        String sqlTotal = "select count(1) total "+sql ;
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));
        
        List<Record> BillingOrders = Db.find(coulmns + sql + " order by create_stamp desc" +sLimit);
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

		String urlStr=PropKit.use("app_config.txt").get("szediUrl")+"/tgt/service/order_create.action";
		System.out.println("上报Url: "+urlStr);
		
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
