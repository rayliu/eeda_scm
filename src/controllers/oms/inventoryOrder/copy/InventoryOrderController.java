package controllers.oms.inventoryOrder.copy;

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
import models.eeda.oms.InspectionOrder;
import models.eeda.oms.InspectionOrderItem;
import models.eeda.oms.InventoryOrder;
import models.eeda.oms.InventoryOrderItem;
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
import controllers.yh.job.CustomJob;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class InventoryOrderController extends Controller {

	private Logger logger = Logger.getLogger(InventoryOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/inventoryOrder/inventoryOrderList.html");
	}
	
    public void create() {
        render("/oms/inventoryOrder/inventoryOrderEdit.html");
    }
    

    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        InventoryOrder order = new InventoryOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			order = InventoryOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, order);
   			
   			//需后台处理的字段
   			order.set("update_by", user.getLong("id"));
   			order.set("update_stamp", new Date());
   			order.update();
   			CustomJob.operationLog("inventoryOrder", "", id, "update", LoginUserController.getLoginUserId(this).toString());
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, order);
   			
   			//需后台处理的字段
   			order.set("order_no", OrderNoGenerator.getNextOrderNo("PD"));
   			order.set("create_by", user.getLong("id"));
   			order.set("create_stamp", new Date());
   			order.save();
   			
   			id = order.getLong("id").toString();
   			CustomJob.operationLog("inventoryOrder", "", id, "create", LoginUserController.getLoginUserId(this).toString());
   		}
   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("item_list");
		DbUtils.handleList(itemList, id, InventoryOrderItem.class, "order_id");
		
		String user_name = null;
		if(order.getLong("create_by") != null){
			long create_by = order.getLong("create_by");
			user_name = LoginUserController.getUserNameById(create_by);
		}
		
		String audit_name = null;
		if(order.getLong("audit_by") != null){
			long audit_by = order.getLong("audit_by");
			audit_name = LoginUserController.getUserNameById(audit_by);
		}
	
		Record r = order.toRecord();
   		r.set("creator_name", user_name);
   		r.set("audit_name", audit_name);
   		renderJson(r);
   	}
    
    @Before(Tx.class)
    public void auditOrder(){
    	String order_id = getPara("params");
    	InventoryOrder order = InventoryOrder.dao.findById(order_id);
    	order.set("status","已审核").update();

    	//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
    	UserLogin user = LoginUserController.getLoginUser(this);
   		Long operator = user.getLong("id");
    	CustomJob.operationLog("salesOrder", "", order_id, "audit", LoginUserController.getLoginUserId(this).toString());

    	renderJson(order);
    }
    
    
    private List<Record> getItems(String orderId) {
		String itemSql = "select * from  inventory_order_item where order_id=?";
		List<Record> itemList = Db.find(itemSql, orderId);
		return itemList;
	}
    
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	InventoryOrder order = InventoryOrder.dao.findById(id);
    	setAttr("order", order);
    	
    	//获取明细表信息
    	setAttr("itemList", getItems(id));
	
    	//仓库回显
    	Warehouse warehouse = Warehouse.dao.findById(order.getLong("warehouse_id"));
    	setAttr("warehouse", warehouse);

    	//用户信息
    	long check_by = order.getLong("check_by");
    	long audit_by = order.getLong("audit_by");
    	String check_name = LoginUserController.getUserNameById(check_by);
    	String audit_name = LoginUserController.getUserNameById(audit_by);
    	setAttr("check_name", check_name);
    	setAttr("audit_name", audit_name);
    	
        render("/oms/inventoryOrder/inventoryOrderEdit.html");
    }
    
    
    
    public void list() {
    	String sLimit = "";
        String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }

        String sql = "select * from ( SELECT inv.* ,wh.warehouse_name"
    			+ "  from inventory_order inv "
    			+ "  left join warehouse wh on wh.id = inv.warehouse_id"
    			+ "  ) A where 1 = 1 ";
        
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
        List<Record> BillingOrders = Db.find(sql + condition  +sLimit);
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
