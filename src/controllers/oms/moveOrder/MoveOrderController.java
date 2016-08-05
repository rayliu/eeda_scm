package controllers.oms.moveOrder;

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
import models.eeda.oms.InspectionOrder;
import models.eeda.oms.InspectionOrderItem;
import models.eeda.oms.Inventory;
import models.eeda.oms.MoveOrder;
import models.eeda.oms.MoveOrderItem;
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
import controllers.util.OrderNoGenerator;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class MoveOrderController extends Controller {

	private Logger logger = Logger.getLogger(MoveOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/moveOrder/moveOrderList.html");
	}
	
    public void create() {
        render("/oms/moveOrder/moveOrderEdit.html");
    }
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        MoveOrder order = new MoveOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			order = MoveOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, order);
   			
   			//需后台处理的字段
   			order.set("update_by", user.getLong("id"));
   			order.set("update_stamp", new Date());
   			order.update();
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, order);
   			
   			//需后台处理的字段
   			order.set("order_no", OrderNoGenerator.getNextOrderNo("YK"));
   			order.set("create_by", user.getLong("id"));
   			order.set("create_stamp", new Date());
   			order.save();
   			
   			id = order.getLong("id").toString();
   		}

   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("item_list");
		DbUtils.handleList(itemList, id, MoveOrderItem.class, "order_id");
		
		long create_by = order.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);
		Record r = order.toRecord();
   		r.set("creator_name", user_name);
   		renderJson(r);
   	}
    
    @Before(Tx.class)
    public void confirmOrder(){
    	String order_id = getPara("params");
    	MoveOrder order = MoveOrder.dao.findById(order_id);
    	order.set("status", "已确认");
    	order.update();
    	
    	//库存处理（加减库存）
    	updateInventory(order_id);
    	
    	renderJson(order);
    }
    
    public void updateInventory(String order_id){
    	List<MoveOrderItem> item = MoveOrderItem.dao.find("select * from move_order_item where order_id = ?",order_id);
    	for(MoveOrderItem moi :item){
    		String cargo_name= moi.getStr("cargo_name");
    		int amount= (int)(moi.getDouble("amount")*100/100);
    		String shelves= moi.getStr("shelves");
    		String to_shelves= moi.getStr("to_shelves");
    		long warehouse= moi.getLong("warehouse_id");
    		long to_warehouse= moi.getLong("to_warehouse_id");
    		
    		String sql = "select * from inventory inv where lock_amount = 0 and cargo_name = ? and warehouse_id=? and shelves=? and (gate_in_amount - gate_out_amount) > 0 order by shelf_life limit 0,?";
    		List<Inventory> invs = Inventory.dao.find(sql,cargo_name,warehouse,shelves,amount);
    		for(Inventory inv : invs){
    			inv.set("warehouse_id", to_warehouse);
        		inv.set("shelves", to_shelves);
        		inv.update();
    		}
    	}
    }
    
    
    private List<Record> getItems(String orderId) {
		String itemSql = "select * from  move_order_item where order_id=?";
		List<Record> itemList = Db.find(itemSql, orderId);
		return itemList;
	}
    
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	MoveOrder order = MoveOrder.dao.findById(id);
    	setAttr("order", order);
    	
    	//获取明细表信息
    	setAttr("itemList", getItems(id));
    	
    	//仓库回显
    	Warehouse warehouse = Warehouse.dao.findById(order.getLong("warehouse_id"));
    	setAttr("warehouse", warehouse);

    	//用户信息
    	long create_by = order.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
        render("/oms/moveOrder/moveOrderEdit.html");
    }
    
    
    public void list() {
    	String sLimit = "";
    	String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }

        String sql = "select * from(SELECT mor.*,ifnull(u.c_name, u.user_name) creator_name ,wh.warehouse_name"
    			+ "  from move_order mor "
    			+ "  left join warehouse wh on wh.id = mor.warehouse_id"
    			+ "  left join user_login u on u.id = mor.create_by"
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
    	list = getItems(order_id);

    	Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", 1);
        BillingOrderListMap.put("iTotalRecords", list.size());
        BillingOrderListMap.put("iTotalDisplayRecords", list.size());

        BillingOrderListMap.put("aaData", list);

        renderJson(BillingOrderListMap); 
    }
    

    
}
