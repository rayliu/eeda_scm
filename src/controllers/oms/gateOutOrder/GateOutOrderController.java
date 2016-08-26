package controllers.oms.gateOutOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.UserLogin;
import models.eeda.OrderActionLog;
import models.eeda.oms.GateInOrder;
import models.eeda.oms.GateOutOrder;
import models.eeda.oms.GateOutOrderItem;
import models.eeda.oms.Inventory;
import models.eeda.oms.WaveOrder;
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

import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.OrderNoGenerator;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class GateOutOrderController extends Controller {

	private Logger logger = Logger.getLogger(GateOutOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

	public void index() {
		render("/oms/gateOutOrder/gateOutOrderList.html");
	}
	
    public void create() {
        render("/oms/gateOutOrder/gateOutOrderEdit.html");
    }
    
    //保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
    public void OperationLog(String json,String order_id,Long operator,String action){
    	OrderActionLog orderActionLog = new OrderActionLog();
    	orderActionLog.set("json", json);
    	orderActionLog.set("time_stamp", new Date());
    	orderActionLog.set("order_type", "gateOutOrder");
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
            
        GateOutOrder gateOutOrder = new GateOutOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		Long operator = user.getLong("id");
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			gateOutOrder = gateOutOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, gateOutOrder);
   			
   			//需后台处理的字段
   			gateOutOrder.set("update_by", operator);
   			gateOutOrder.set("update_stamp", new Date());
   			gateOutOrder.update();
   			//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
   			OperationLog(jsonStr, id, operator,"update");
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, gateOutOrder);
   			
   			//需后台处理的字段
   			gateOutOrder.set("order_no", OrderNoGenerator.getNextOrderNo("CKTZ"));
   			gateOutOrder.set("create_by", operator);
   			gateOutOrder.set("create_stamp", new Date());
   			gateOutOrder.save();
   			
   			id = gateOutOrder.getLong("id").toString();
   			
   			//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
   			OperationLog(jsonStr, id, operator,"create");
   		}
   		
   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("item_list");
		DbUtils.handleList(itemList, id, GateOutOrderItem.class, "order_id");

		long create_by = gateOutOrder.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);
		Record r = gateOutOrder.toRecord();
   		r.set("create_by_name", user_name);
   		renderJson(r);
   	}
    
    @Before(Tx.class)
    public void confirmOrder() throws Exception{
    	String order_id = getPara("params");
    	GateOutOrder gateOutOrder = GateOutOrder.dao.findById(order_id);

    	//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
    	UserLogin user = LoginUserController.getLoginUser(this);
   		Long operator = user.getLong("id");
    	OperationLog(order_id, order_id, operator,"confirm");
    	
    	//锁定库存
    	Record record = lockInventory(order_id);
    	Record re = new Record();
    	if("1".equals(record.getStr("flag"))){
    		gateOutOrder.set("status","已确认").update();
    		re.set("msg", "success");
    	}else{
    		re.set("msg", "当前货架上能出库商品最大数量为："+record.getInt("amount")+"，请通知补货上架");
    	}
    	
    	renderJson(re);
    }
    
    @Before(Tx.class)
    public void checkOrder() throws Exception{
    	String order_id = getPara("params");
    	GateOutOrder gateOutOrder = GateOutOrder.dao.findById(order_id);
    	
    	String msg = "此单据未拣货完成";
    	if(gateOutOrder.getLong("wave_id") != null){
    		long wave_id = gateOutOrder.getLong("wave_id");
    		WaveOrder wo = WaveOrder.dao.findById(wave_id);
    		String status = wo.getStr("status");
    		if("已完成".equals(status)){
    			msg = "success";
    			gateOutOrder.set("status","已复核").update();
    			
    			//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
    	    	UserLogin user = LoginUserController.getLoginUser(this);
    	   		Long operator = user.getLong("id");
    	    	OperationLog(order_id, order_id, operator,"confirm");
    	    	
    	    	//扣库存
    	    	gateOut(order_id);	
    		}
    	}
    	
    	Record record = new Record();
    	record.set("order", gateOutOrder);
    	record.set("msg", msg);
    	
    	renderJson(record);
    }
    
    @Before(Tx.class)
    public Record lockInventory(String order_id){
    	Record record = new Record();
    	List<Record> res = Db.find("select * from gate_out_order_item where order_id = ?",order_id);
    	for(Record re :res){
    		String bar_code = re.getStr("bar_code");
    		int amount = ((int)(re.getDouble("packing_amount")*100))/100;
    		String sql = "select * from inventory inv where cargo_barcode = ? and shelves is not null and shelves != '' "
    				+ " and gate_out_amount=0 and lock_amount=0 and damage_amount = 0 order by shelves,shelf_life limit 0,?";
    		List<Inventory> invs = Inventory.dao.find(sql,bar_code,amount);
    		if(amount == invs.size()){
    			for(Inventory inv : invs){
        			inv.set("gate_out_order_id", order_id);
        			inv.set("lock_stamp", new Date());
        			inv.set("lock_amount", 1);
        			inv.update();
        		}
    			record.set("amount", invs.size());
    			record.set("flag", "1");
    		}else{
    			record.set("amount", invs.size());
    			record.set("flag","-1");
    		}
    	}
    	//单出库单出库两种商品时，可以一下逻辑代码
//    	if(flag == 1){
//    		for(Record re :res){
//        		String bar_code = re.getStr("bar_code");
//        		int amount = ((int)(re.getDouble("packing_amount")*100))/100;
//        		String sql = "select * from inventory inv where cargo_barcode = ? and shelves is not null and shelves != '' and (gate_in_amount - gate_out_amount - lock_amount) > 0 order by shelves,shelf_life limit 0,?";
//        		List<Inventory> invs = Inventory.dao.find(sql,bar_code,amount);
//        		for(Inventory inv : invs){
//        			inv.set("gate_out_order_id", order_id);
//        			inv.set("lock_stamp", new Date());
//        			inv.set("lock_amount", 1);
//        			inv.update();
//        		}
//        	}
//    	}
    	return record;
    }
    
    
    @Before(Tx.class)
    public void gateOut(String order_id){
    	GateOutOrder goo = GateOutOrder.dao.findById(order_id);
    	List<Inventory> res = Inventory.dao.find("select * from inventory where gate_out_order_id = ? and exception = 'N'",order_id);
    	for(Inventory inv :res){
			inv.set("lock_amount", 0);
    		inv.set("gate_out_stamp", goo.getTimestamp("gate_out_date"));
    		inv.set("gate_out_amount", 1);
    		inv.update();
    	}
    }
    
    //直接扣减库存
    @Before(Tx.class)
    public void reduceInv(){
    	String order_id = getPara("params");
    	GateOutOrder gateOutOrder = GateOutOrder.dao.findById(order_id);
    	gateOutOrder.set("status","已复核").update();
    			
		//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
    	UserLogin user = LoginUserController.getLoginUser(this);
   		Long operator = user.getLong("id");
    	OperationLog(order_id, order_id, operator,"derectGateOut");
    	    	
    	//扣库存
    	gateOut(order_id);	
    	
    	renderJson(gateOutOrder);
    }
    
    
    @Before(Tx.class)
    public void cancelOrder() throws Exception{
    	String order_id = getPara("params");
    	GateOutOrder gateOutOrder = GateOutOrder.dao.findById(order_id);
    	gateOutOrder.set("status","已取消").update();
    	renderJson(gateOutOrder);
    	
    	//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
    	UserLogin user = LoginUserController.getLoginUser(this);
   		Long operator = user.getLong("id");
    	OperationLog(order_id, order_id, operator,"cancel");
    }

    private List<Record> getGateOutItems(String orderId) {
		String itemSql = "select * from gate_out_order_item where order_id=?";
		List<Record> itemList = Db.find(itemSql, orderId);
		return itemList;
	}

    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	GateOutOrder gateOutOrder = GateOutOrder.dao.findById(id);
    	setAttr("order", gateOutOrder);
    	
    	//获取明细表信息
    	setAttr("itemList", getGateOutItems(id));

    	//用户信息
    	long create_by = gateOutOrder.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
    	//仓库回显
    	Warehouse warehouse = Warehouse.dao.findById(gateOutOrder.getLong("warehouse_id"));
    	setAttr("warehouse", warehouse);
    	
    	//地址回显（省市区）
    	String location = gateOutOrder.getStr("location");
    	if(StringUtils.isNotEmpty(location)){
    		Record re = Db.findFirst("select get_loc_full_name(?) address",location);
        	setAttr("location_name", re.getStr("address"));
    	}

        render("/oms/gateOutOrder/gateOutOrderEdit.html");
    }
    
    
    
    public void list() {
    	String sLimit = "";
    	String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }

        String sql = "select * from (SELECT gio.*, ifnull(u.c_name, u.user_name) creator_name ,wh.warehouse_name,"
    			+ " GROUP_CONCAT(gooi.bar_code SEPARATOR ' ') cargo_barcode,if(count(gooi.bar_code)>1,'多品','单品') cargo_type "
    			+ " from gate_out_order gio "
    			+ " LEFT JOIN gate_out_order_item gooi on gooi.order_id = gio.id"
    			+ " left join warehouse wh on wh.id = gio.warehouse_id"
    			+ " left join user_login u on u.id = gio.create_by"
    			+ " GROUP BY gio.id"
    			+ " ) A where 1 =1 ";
        
        String condition = "";
        String jsonStr = getPara("jsonStr");
    	if(StringUtils.isNotEmpty(jsonStr)){
    		Gson gson = new Gson(); 
            Map<String, String> dto= gson.fromJson(jsonStr, HashMap.class);  
            condition = DbUtils.buildConditions(dto);
    	}
    	

        String sqlTotal = "select count(1) total from (" + sql + condition +") B";
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));
        
        List<Record> BillingOrders = Db.find(sql+ condition +" order by create_stamp desc " +sLimit);
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
    	list = getGateOutItems(order_id);

    	Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", 1);
        BillingOrderListMap.put("iTotalRecords", list.size());
        BillingOrderListMap.put("iTotalDisplayRecords", list.size());

        BillingOrderListMap.put("aaData", list);

        renderJson(BillingOrderListMap); 
    }
    
    //更新称重重量
    public void updateWeight() throws Exception{
    	String order_id = getPara("order_id");
    	String pack_weight = getPara("pack_weight");
    	
    	GateOutOrder order = GateOutOrder.dao.findById(order_id);
    	order.set("pack_weight", pack_weight);
    	order.update();
    	
    	renderJson(order);
    }

}
