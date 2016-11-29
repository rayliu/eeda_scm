package controllers.oms.customGateInOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Party;
import models.UserLogin;
import models.eeda.oms.CustomGateInItem;
import models.eeda.oms.CustomGateInOrder;
import models.eeda.oms.CustomInventory;
import models.eeda.oms.GateOutOrder;
import models.eeda.oms.WaveOrder;
import models.eeda.oms.WaveOrderItem;
import models.eeda.profile.Product;

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
public class CustomGateInOrderController extends Controller {

	private Logger logger = Logger.getLogger(CustomGateInOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/customGateInOrder/list.html");
	}
	
    public void create() {
        render("/oms/customGateInOrder/edit.html");
    }
    
    
    public void getProduct(){
    	String para = getPara("input");
    	
    	String condition = " where 1 = 1 and is_stop != 1";
    	if(StringUtils.isNotEmpty(para)){
    		condition += " and (item_name like '%"+para+"%' "
    				+ " or serial_no like '%"+para+"%'"
    				+ " or item_no like '%"+para+"%')";
    	}
    	
    	String sql = "select * from product";
    	
    	List<Record> re = Db.find(sql + condition);
    	renderJson(re);
    }
    
    
    @Before(Tx.class)
   	public void saveProd() throws Exception {		
   		String jsonStr=getPara("params");
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        Product order = new Product();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
        Long officeId = user.getLong("office_id");
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			order = Product.dao.findById(id);
   			DbUtils.setModelValues(dto, order);
   			
   			//需后台处理的字段
//   			order.set("update_by", user.getLong("id"));
//   			order.set("update_stamp", new Date());
   			order.update();
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, order);
   			
   			//需后台处理的字段
   			order.set("create_by", user.getLong("id"));
   			order.set("create_stamp", new Date());
   			order.set("office_id", officeId);
   			order.save();
   			
   		}
   		
   		renderJson(order);
   	}
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        CustomGateInOrder order = new CustomGateInOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			order = CustomGateInOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, order);
   			
   			//需后台处理的字段
   			order.set("update_by", user.getLong("id"));
   			order.set("update_stamp", new Date());
   			order.update();
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, order);
   			
   			//需后台处理的字段
   			order.set("order_no", OrderNoGenerator.getNextOrderNo("BGGI"));
   			order.set("create_by", user.getLong("id"));
   			order.set("create_stamp", new Date());
   			order.save();
   			
   			id = order.getLong("id").toString();
   		}
   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("item_list");
		DbUtils.handleList(itemList, id , CustomGateInItem.class, "order_id");
		
//		for(Map item:itemList){
//			String itemId = (String)item.get("id");
//			CustomGateInItem ggi = null;
//			if("".equals(itemId)){
//				ggi = new CustomGateInItem();
//				ggi.set("product_id", (String)item.get("product_id"));
//				ggi.set("amount", (String)item.get("amount"));
//				ggi.set("change_amount", (String)item.get("change_amount"));
//				ggi.set("remark", (String)item.get("remark"));
//				ggi.set("order_id", id);
//				ggi.save();
//			}else{
//				ggi = CustomGateInItem.dao.findById(itemId);
//				ggi.set("product_id", (String)item.get("product_id"));
//				ggi.set("amount", (String)item.get("amount"));
//				ggi.set("change_amount", (String)item.get("change_amount"));
//				ggi.set("remark", (String)item.get("remark"));
//				ggi.set("order_id", id);
//				ggi.update();
//			}
//		}
		
		long create_by = order.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);
		Record r = order.toRecord();
   		r.set("creator_name", user_name);
   		renderJson(r);
   	}
    
    
    @Before(Tx.class)
   	public void confirmOrder() throws Exception {
    	String order_id = getPara("order_id");
    	CustomGateInOrder order = CustomGateInOrder.dao.findById(order_id);
    	order.set("status", "已确认");
    	order.update();
    	
    	List<Record> reList = Db.find("select * from custom_gate_in_item cgi where cgi.order_id = ?",order_id);
    	for(Record re : reList){
    		Long prod_id = re.getLong("product_id");
    		Double amount = re.getDouble("amount");
    		Double change_amount = re.getDouble("change_amount");
    		String remark = re.getStr("remark");
    		CustomInventory inv = CustomInventory.dao.findFirst("select * from custom_inventory where product_id = ?",prod_id);
    		Double total = 0.0;
			if("暂存".equals(re.getStr("stauts"))){
				total = change_amount + amount;
			}else{
				total = change_amount;
			}
    		if(inv == null){  //新增库存
    			Product p = Product.dao.findById(prod_id);
    			
    			inv = new CustomInventory();
    			inv.set("product_id", prod_id);
    			inv.set("customer_id", order.getLong("customer_id"));
    			inv.set("bar_code", p.getStr("serial_no"));
    			inv.set("item_name", p.getStr("item_name"));
    			inv.set("item_no", p.getStr("item_no"));
    			inv.set("amount", amount);
    			inv.set("push_amount", 0);
    			inv.set("nopush_amount", amount);
    			inv.set("remark", remark);
    			inv.save();
    		}else{  //更新库存
    			inv.set("amount", inv.getDouble("amount") + total);
				inv.set("nopush_amount", inv.getDouble("nopush_amount") + total);
    			inv.set("remark", remark);
    			inv.update();
    		}
    		
    		//更新入库明细状态
			Db.update("update custom_gate_in_item set status = '已入库' where id = ?",re.getLong("id"));
    		
    	}
    	
    	renderJson(order);
    }
    
    
    private List<Record> getItems(String orderId) {
		String itemSql = "select cgi.*,p.item_name,ci.nopush_amount inventory "
				+ " from  custom_gate_in_item cgi"
				+ "	left join product p on p.id = cgi.product_id "
				+ " left join custom_inventory ci on ci.product_id = cgi.product_id"
				+ " where cgi.order_id=? ";
		List<Record> itemList = Db.find(itemSql, orderId);
		return itemList;
	}
    
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	CustomGateInOrder order = CustomGateInOrder.dao.findById(id);
    	setAttr("order", order);
    	
    	Party p = Party.dao.findById(order.getLong("customer_id"));
    	setAttr("party", p);
    	
    	//获取明细表信息
    	setAttr("itemList", getItems(id));
    	//用户信息
    	long create_by = order.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
        render("/oms/customGateInOrder/edit.html");
    }
    
    
    public void list() {
    	String sLimit = "";
    	String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }

        String sql = "SELECT cgi.*, ifnull(u.c_name, u.user_name) creator_name "
    			+ "  from custom_gate_in_order cgi "
    			+ "  left join user_login u on u.id = cgi.create_by"
    			+ "   where 1 =1 ";
        
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
    	if(StringUtils.isNotEmpty(order_id)){
    		list = getItems(order_id);
    	}

    	Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", 1);
        BillingOrderListMap.put("iTotalRecords", list.size());
        BillingOrderListMap.put("iTotalDisplayRecords", list.size());

        BillingOrderListMap.put("aaData", list);

        renderJson(BillingOrderListMap); 
    }
    
}
