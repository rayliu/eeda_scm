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
    
    //表order_action_log
    public void OperationLog(String json,String order_id,Long operator){
    	OrderActionLog orderActionLog = new OrderActionLog();
    	orderActionLog.set("json", json);
    	orderActionLog.set("time_stamp", new Date());
    	orderActionLog.set("order_type", "gateOutOrder");
    	orderActionLog.set("order_id", order_id);
    	orderActionLog.set("operator", operator);
//    	orderActionLog.set("action", "");
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
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, gateOutOrder);
   			
   			//需后台处理的字段
   			gateOutOrder.set("order_no", OrderNoGenerator.getNextOrderNo("CKTZ"));
   			gateOutOrder.set("create_by", operator);
   			gateOutOrder.set("create_stamp", new Date());
   			gateOutOrder.save();
   			
   			id = gateOutOrder.getLong("id").toString();
   		}
   		
   		//保存，更新操作的json插入到order_action_log,方便以后查找谁改了什么数据
   		OperationLog(jsonStr, id, operator);
   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("item_list");
		DbUtils.handleList(itemList, id, GateOutOrderItem.class, "order_id");

		long create_by = gateOutOrder.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);
		Record r = gateOutOrder.toRecord();
   		r.set("create_by_name", user_name);
   		renderJson(r);
   	}
    
    @Before(Tx.class)
    public void confirmOrder(){
    	String order_id = getPara("params");
    	GateInOrder gateInOrder = GateInOrder.dao.findById(order_id);
    	gateInOrder.set("status","已确认").update();
    	renderJson(gateInOrder);
    }
    
    @Before(Tx.class)
    public void canselOrder(){
    	String order_id = getPara("params");
    	GateInOrder gateInOrder = GateInOrder.dao.findById(order_id);
    	gateInOrder.set("status","已取消").update();
    	renderJson(gateInOrder);
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
    	
    	
        render("/oms/gateOutOrder/gateOutOrderEdit.html");
    }
    
    
    @Before(Tx.class)
    public void getUser() {
    	String id = getPara("params");
    	UserLogin user = UserLogin.dao.findById(id);
    	renderJson(user);
    }
    
    
    public void list() {
    	String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        String sql = "select * from (SELECT gio.*, ifnull(u.c_name, u.user_name) creator_name ,wh.warehouse_name"
    			+ "  from gate_out_order gio "
    			+ "  left join warehouse wh on wh.id = gio.warehouse_id"
    			+ "  left join user_login u on u.id = gio.create_by"
    			+ " ) A where 1 =1 ";
        
        String condition = DbUtils.buildConditions(getParaMap());

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

}
