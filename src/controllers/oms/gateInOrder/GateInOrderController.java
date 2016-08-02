package controllers.oms.gateInOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.Party;
import models.UserLogin;
import models.eeda.OrderActionLog;
import models.eeda.oms.GateInOrder;
import models.eeda.oms.GateInOrderItem;
import models.eeda.oms.Inventory;
import models.eeda.oms.SalesOrderCount;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.oms.SalesOrder;
import models.eeda.profile.CustomCompany;
import models.eeda.profile.Warehouse;

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

import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.DingDanGoodsDto;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class GateInOrderController extends Controller {

	private Logger logger = Logger.getLogger(GateInOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/gateInOrder/gateInOrderList.html");
	}
	
    public void create() {
        render("/oms/gateInOrder/gateInOrderEdit.html");
    }
    
    //保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
    @Before(Tx.class)
    public void OperationLog(String json,String order_id,Long operator,String action){
    	OrderActionLog orderActionLog = new OrderActionLog();
    	orderActionLog.set("json", json);
    	orderActionLog.set("time_stamp", new Date());
    	orderActionLog.set("order_type", "gateInOrder");
    	orderActionLog.set("order_id", order_id);
    	orderActionLog.set("operator", operator);
    	orderActionLog.set("action", action);
    	orderActionLog.save();
    }
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr = getPara("params");
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        GateInOrder gateInOrder = new GateInOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		Long operator = user.getLong("id");
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			gateInOrder = GateInOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, gateInOrder);
   			
   			//需后台处理的字段
   			gateInOrder.set("update_by", operator);
   			gateInOrder.set("update_stamp", new Date());
   			gateInOrder.update();
   			//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
   			OperationLog(jsonStr, id, operator,"update");
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, gateInOrder);
   			
   			//需后台处理的字段
   			gateInOrder.set("order_no", OrderNoGenerator.getNextOrderNo("RKTZ"));
   			gateInOrder.set("create_by", operator);
   			gateInOrder.set("create_stamp", new Date());
   			gateInOrder.set("office_id", user.get("office_id"));
   			gateInOrder.save();
   			
   			id = gateInOrder.getLong("id").toString();
   			//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
   			OperationLog(jsonStr, id, operator,"create");
   		}
   		
   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("item_list");
		DbUtils.handleList(itemList, id, GateInOrderItem.class, "order_id");

		long create_by = gateInOrder.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);
		Record r = gateInOrder.toRecord();
   		r.set("create_by_name", user_name);
   		renderJson(r);
   	}
    
    @Before(Tx.class)
    public void confirmOrder(){
    	String order_id = getPara("params");
    	GateInOrder gateInOrder = GateInOrder.dao.findById(order_id);
    	gateInOrder.set("status","已确认").update();

    	//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
    	UserLogin user = LoginUserController.getLoginUser(this);
   		Long operator = user.getLong("id");
    	OperationLog(order_id, order_id, operator,"confirm");
    	
    	//确认时货品入库
    	gateIn(order_id);
    	
    	renderJson(gateInOrder);
    }
    
    @Before(Tx.class)
    public void gateIn(String order_id){
    	GateInOrder gir = GateInOrder.dao.findById(order_id);
    	List<Record> res = Db.find("select * from gate_in_order_item where order_id = ?",order_id);
    	long user_id = LoginUserController.getLoginUserId(this);
    	for(Record re :res){
    		Inventory inv = null;
    		Double amount = re.getDouble("received_amount");
    		for (int i = 0; i < amount; i++) {
    			inv = new Inventory();
    			inv.set("customer_id", gir.getLong("customer_id"));
        		inv.set("warehouse_id", gir.getLong("warehouse_id"));
        		inv.set("gate_in_stamp", gir.getTimestamp("gate_in_date"));
        		inv.set("cargo_name", re.getStr("cargo_name"));
        		inv.set("cargo_code", re.getStr("item_code"));
        		inv.set("shelf_life", re.getStr("shelf_life"));
        		inv.set("shelves", null);
        		inv.set("unit", re.getStr("packing_unit"));
        		inv.set("gate_in_amount", 1);
        		inv.set("create_stamp", new Date());
        		inv.set("create_by", user_id);
        		inv.save();
			}
    	}
    }
    
    @Before(Tx.class)
    public void cancelOrder(){
    	String order_id = getPara("params");
    	GateInOrder gateInOrder = GateInOrder.dao.findById(order_id);
    	gateInOrder.set("status","已取消").update();
    	renderJson(gateInOrder);
    	
    	//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
    	UserLogin user = LoginUserController.getLoginUser(this);
   		Long operator = user.getLong("id");
    	OperationLog(order_id, order_id, operator,"cancel");
    }

    private List<Record> getGateInItems(String orderId) {
		String itemSql = "select * from gate_in_order_item where order_id=?";
		List<Record> itemList = Db.find(itemSql, orderId);
		return itemList;
	}

    
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	GateInOrder gateInOrder = GateInOrder.dao.findById(id);
    	Record gateInOrderRec = gateInOrder.toRecord();
    	Long customer_id = gateInOrder.getLong("customer_id");
    	if(customer_id!=null){
    	    Party p = Party.dao.findById(customer_id);
    	    if(p!=null){
    	        gateInOrderRec.set("customer_name", p.get("abbr"));
    	    }
    	}
    	setAttr("order", gateInOrderRec);
    	
    	//获取明细表信息
    	setAttr("itemList", getGateInItems(id));

    	//用户信息
    	long create_by = gateInOrder.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
    	//仓库回显
    	Warehouse warehouse = Warehouse.dao.findById(gateInOrder.getLong("warehouse_id"));
    	setAttr("warehouse", warehouse);
    	
    	String route_from = gateInOrder.getStr("route_from");
    	String route_to = gateInOrder.getStr("route_to");
    	
    	Record re = null;
    	if(StringUtils.isNotEmpty(route_from)){
    		re = Db.findFirst("select get_loc_full_name(?) address",route_from);
        	setAttr("route_from_input", re.getStr("address"));
    	}
    	if(StringUtils.isNotEmpty(route_to)){
    		re = Db.findFirst("select get_loc_full_name(?) address",route_to);
        	setAttr("route_to_input", re.getStr("address"));
    	}
    	
    	
        render("/oms/gateInOrder/gateInOrderEdit.html");
    }
    
    
    
    public void list() {
    	String sLimit = "";
    	String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }

        String sql = "select * from ( SELECT gio.*, ifnull(u.c_name, u.user_name) creator_name ,wh.warehouse_name"
    			+ "  from gate_in_order gio "
    			+ "  left join warehouse wh on wh.id = gio.warehouse_id"
    			+ "  left join user_login u on u.id = gio.create_by"
    			+ "  ) A where 1 =1 ";
        
        String condition = "";
        String jsonStr = getPara("jsonStr");
    	if(StringUtils.isNotEmpty(jsonStr)){
    		Gson gson = new Gson(); 
            Map<String, String> dto= gson.fromJson(jsonStr, HashMap.class);  
            condition = DbUtils.buildConditions(dto);
    	}

        String sqlTotal = "select count(1) total from ("+ sql + condition +") B";
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));
        List<Record> BillingOrders = Db.find(sql + condition + " order by create_stamp desc " +sLimit);
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

  
    //异步刷新字表
    public void tableList(){
    	String order_id = getPara("order_id");
    	List<Record> list = null;
    	list = getGateInItems(order_id);

    	Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", 1);
        BillingOrderListMap.put("iTotalRecords", list.size());
        BillingOrderListMap.put("iTotalDisplayRecords", list.size());

        BillingOrderListMap.put("aaData", list);

        renderJson(BillingOrderListMap); 
    }

}
