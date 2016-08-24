package controllers.oms.inspectionOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.UserLogin;
import models.eeda.oms.GateInOrder;
import models.eeda.oms.GateInOrderItem;
import models.eeda.oms.InspectionOrder;
import models.eeda.oms.InspectionOrderItem;
import models.eeda.oms.Inventory;
import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderGoods;
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
import controllers.yh.job.CustomJob;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class InspectionOrderController extends Controller {

	private Logger logger = Logger.getLogger(InspectionOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/inspectionOrder/inspectionOrderList.html");
	} 
	
    public void create() {
        render("/oms/inspectionOrder/inspectionOrderEdit.html");
    }
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        InspectionOrder inspectionOrder = new InspectionOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			inspectionOrder = InspectionOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, inspectionOrder);
   			
   			//需后台处理的字段
   			inspectionOrder.set("update_by", user.getLong("id"));
   			inspectionOrder.set("update_stamp", new Date());
   			inspectionOrder.update();
   			
   		//保存job记录
   	   		CustomJob.operationLog("InspectionOrder", jsonStr, id, "update", LoginUserController.getLoginUserId(this).toString());
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, inspectionOrder);
   			
   			//需后台处理的字段
   			//inspectionOrder.set("order_no", OrderNoGenerator.getNextOrderNo("DD"));
   			inspectionOrder.set("create_by", user.getLong("id"));
   			inspectionOrder.set("create_stamp", new Date());
   			inspectionOrder.save();
   			
   			id = inspectionOrder.getLong("id").toString();
   			//保存job记录
   	   		CustomJob.operationLog("InspectionOrder", jsonStr, id, "create", LoginUserController.getLoginUserId(this).toString());
   		}
   		
   		String gate_in_order_id = (String) dto.get("gate_in_order_id");
   		if(StringUtils.isNotEmpty(gate_in_order_id)){
   			GateInOrder gor = GateInOrder.dao.findById(gate_in_order_id);
   	   		gor.set("inspection_flag", "Y");
   	   		gor.set("status", "验货中");
   	   		gor.update();
   		}
   		
   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("item_list");
		DbUtils.handleList(itemList, id, InspectionOrderItem.class, "order_id");
		
		long create_by = inspectionOrder.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);
		Record r = inspectionOrder.toRecord();
   		r.set("creator_name", user_name);
   		renderJson(r);
   	}
    
    
    @Before(Tx.class)
    public void confirmOrder(){
    	String order_id = getPara("params");
    	InspectionOrder order = InspectionOrder.dao.findById(order_id);
    	order.set("status","已确认").update();

    	//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
    	UserLogin user = LoginUserController.getLoginUser(this);
   		Long operator = user.getLong("id");
   		
   		//保存job记录
   		CustomJob.operationLog("InspectionOrder", null, order_id, "confirm", LoginUserController.getLoginUserId(this).toString());

   		//更新入库单的数据
   		long gate_in_order_id = order.getLong("gate_in_order_id");
   		updateGateInItem(order_id,gate_in_order_id);
   		
    	renderJson(order);
    }
    
    public void updateGateInItem(String order_id,long gate_in_order_id){
    	List<Record> res = Db.find("select * from inspection_order_item where order_id = ?",order_id);

    	for(Record re :res){
    		long gate_in_item_id = re.getLong("gate_in_item_id");
    		double plan_amount = re.getDouble("plan_amount");
    		double amount = re.getDouble("amount");
    		
    		GateInOrderItem gii = GateInOrderItem.dao.findById(gate_in_item_id);
    		gii.set("received_amount", amount);
    		gii.set("damage_amount", plan_amount-amount);
    		gii.update();
    	}
    	
    	GateInOrder gio = GateInOrder.dao.findById(gate_in_order_id);
    	gio.set("status", "已确认");
    	gio.update();
    	
    	//确认时货品入库
    	gateIn(gate_in_order_id);
    }
    
    
    
    @Before(Tx.class)
    public void gateIn(long order_id){
    	GateInOrder gir = GateInOrder.dao.findById(order_id);
    	List<Record> res = Db.find("select * from gate_in_order_item where order_id = ?",order_id);
    	long user_id = LoginUserController.getLoginUserId(this);
    	for(Record re :res){
    		Inventory inv = null;
    		Double amount = re.getDouble("plan_amount");
    		Double damage_amount = re.getDouble("damage_amount");
    		for (int i = 0; i < amount; i++) {
    			inv = new Inventory();
    			inv.set("gate_in_order_id", order_id);
    			inv.set("customer_id", gir.getLong("customer_id"));
        		inv.set("warehouse_id", gir.getLong("warehouse_id"));
        		inv.set("gate_in_stamp", gir.getTimestamp("gate_in_date"));
        		inv.set("cargo_name", re.getStr("cargo_name"));
        		inv.set("cargo_code", re.getStr("item_code"));
        		inv.set("cargo_barcode", re.getStr("cargo_upc"));
        		inv.set("shelf_life", re.getDate("shelf_life"));
        		inv.set("shelves", null);
        		inv.set("unit", re.getStr("packing_unit"));
        		inv.set("gate_in_amount", 1);
        		if(damage_amount > 0)
        			inv.set("damage_amount", 1);
        		inv.set("create_stamp", new Date());
        		inv.set("create_by", user_id);
        		inv.save();
        		
        		damage_amount--;
			}
    	}
    }
    
    
    
    private List<Record> getInspectionItems(String orderId) {
		String itemSql = "select * from  inspection_order_item where order_id=?";
		List<Record> itemList = Db.find(itemSql, orderId);
		return itemList;
	}
    
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	InspectionOrder inspectionOrder = InspectionOrder.dao.findById(id);
    	setAttr("order", inspectionOrder);
    	
    	//获取明细表信息
    	setAttr("itemList", getInspectionItems(id));
    	
    	//获取报关企业信息
    	GateInOrder gateInOrder = GateInOrder.dao.findById(inspectionOrder.getLong("gate_in_order_id"));
    	setAttr("gateInOrder", gateInOrder);
    	
    	//仓库回显
    	Warehouse warehouse = Warehouse.dao.findById(inspectionOrder.getLong("warehouse_id"));
    	setAttr("warehouse", warehouse);

    	//用户信息
    	long create_by = inspectionOrder.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
        render("/oms/inspectionOrder/inspectionOrderEdit.html");
    }
    
    
    public void list() {
    	String sLimit = "";
    	String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }

        String sql = "select * from(SELECT inso.*,gio.order_no gate_in_no, ifnull(u.c_name, u.user_name) creator_name ,wh.warehouse_name"
    			+ "  from inspection_order inso "
    			+ "  left join warehouse wh on wh.id = inso.warehouse_id"
    			+ "  left join gate_in_order gio on gio.id = inso.gate_in_order_id"
    			+ "  left join user_login u on u.id = inso.create_by"
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
        
        List<Record> BillingOrders = Db.find(sql+ condition + " order by create_stamp desc " +sLimit);
        Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", pageIndex);
        BillingOrderListMap.put("iTotalRecords", rec.getLong("total"));
        BillingOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        BillingOrderListMap.put("aaData", BillingOrders);

        renderJson(BillingOrderListMap); 
    }
    
    //异步刷新字表
    public void tableList(){
    	String order_id = getPara("order_id");
    	List<Record> list = null;
    	list = getInspectionItems(order_id);

    	Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", 1);
        BillingOrderListMap.put("iTotalRecords", list.size());
        BillingOrderListMap.put("iTotalDisplayRecords", list.size());

        BillingOrderListMap.put("aaData", list);

        renderJson(BillingOrderListMap); 
    }
    
    public void queryAmount() throws Exception{
    	String gate_in_id = getPara("gate_in_id");
    	Record record = new Record();
    	if(StringUtils.isNotEmpty(gate_in_id))
    		record = Db.findFirst("select sum(gioi.plan_amount) amount from gate_in_order gio "
    			+ " left join gate_in_order_item gioi on gioi.order_id = gio.id where gio.id = ?",gate_in_id);
    	
    	renderJson(record);
    }
    
    public void getGateInOrderItem() throws Exception{
    	String gate_in_order_id = getPara("gate_in_order_id");
    	String bar_code = getPara("bar_code");
    	Record record = null;;
    	if(StringUtils.isNotEmpty(gate_in_order_id) && StringUtils.isNotEmpty(bar_code))
    		record = Db.findFirst("select * from gate_in_order_item gioi where order_id = ? and cargo_upc = ?",gate_in_order_id,bar_code);
    	
    	if(record==null)
    		record = new Record();
    	
    	renderJson(record);
    }
    
}
