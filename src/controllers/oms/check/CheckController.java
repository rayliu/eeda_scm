package controllers.oms.check;

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
import models.eeda.oms.WaveOrderItem;
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
public class CheckController extends Controller {

	private Logger logger = Logger.getLogger(CheckController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/check/checkList.html");
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
   		String cargo_barcode = (String) dto.get("cargo_barcode");
   		String customer_id = (String) dto.get("customer_id");
   		String cargo_name = (String) dto.get("cargo_name");
   		String unit = (String) dto.get("unit");
   		String shelf_life = (String) dto.get("shelf_life");
   		String shelves = (String) dto.get("shelves");
   		String gate_in_amount = (String) dto.get("gate_in_amount");
   		String gate_out_amount = (String) dto.get("gate_out_amount");
   		String damage_amount = (String) dto.get("damage_amount");
   		
   		int out_amount = Integer.parseInt(gate_out_amount);
   		int dam_amount = Integer.parseInt(damage_amount);
   		
   		if (StringUtils.isNotEmpty(cargo_barcode)) {
   	    	long user_id = LoginUserController.getLoginUserId(this);
   	    		Inventory inv = null;
   	    		for (int i = 0; i < Integer.parseInt(gate_in_amount); i++) {
   	    			inv = new Inventory();
   	    			inv.set("cargo_name", cargo_name);
   	        		inv.set("cargo_barcode", cargo_barcode);
   	        		if(StringUtils.isNotEmpty(shelf_life))
   	        			inv.set("shelf_life", shelf_life);
   	    			inv.set("customer_id", customer_id);
   	        		inv.set("warehouse_id", 52);
   	        		if(StringUtils.isNotEmpty(shelves))
   	        			inv.set("shelves", shelves);
   	        		inv.set("unit", unit);
   	        		inv.set("gate_in_amount", 1);
   	        		if(out_amount>0){
   	        			inv.set("gate_out_amount", 1);
   	        			out_amount --;
   	        		}else{
   	        			if(dam_amount>0){
   	        				inv.set("damage_amount", 1);
   	   	        			dam_amount --;
   	        			}	
   	        		}
   	        		inv.set("create_stamp", new Date());
   	        		inv.set("create_by", user_id);
   	        		inv.save();	
   				}
   		} 

		Record r = new Record();
		r.set("success", true);
   		renderJson(r);
   	}
    
    
    @Before(Tx.class)
    public void gateIn(long order_id){
    	GateInOrder gir = GateInOrder.dao.findById(order_id);
    	List<Record> res = Db.find("select * from gate_in_order_item where order_id = ?",order_id);
    	long user_id = LoginUserController.getLoginUserId(this);
    	for(Record re :res){
    		Inventory inv = null;
    		Double amount = re.getDouble("received_amount");
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
        		inv.set("create_stamp", new Date());
        		inv.set("create_by", user_id);
        		inv.save();
			}
    	}
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
    		String cargo_barcode= moi.getStr("cargo_bar_code");
    		String cargo_name= moi.getStr("cargo_name");
    		int amount= (int)(moi.getDouble("amount")*100/100);
    		String shelves= moi.getStr("shelves");
    		String to_shelves= moi.getStr("to_shelves");
    		//long warehouse= moi.getLong("warehouse_id");
    		//long to_warehouse= moi.getLong("to_warehouse_id");
    		
    		String sql = "select * from inventory inv where lock_amount = 0 and (cargo_name = ? or cargo_barcode = ?) and shelves=? and gate_out_amount = 0 order by shelf_life limit 0,?";
    		List<Inventory> invs = Inventory.dao.find(sql,cargo_name,cargo_barcode,shelves,amount);
    		System.out.println("-----------------"+invs.size());
    		for(Inventory inv : invs){
    			//inv.set("warehouse_id", to_warehouse);
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
    
    public void list(){
    	String sLimit = "";
    	String sql = "";
    	String flag = getPara("flag");
    	String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }


    	sql = "select * from(SELECT woi.*,goo.id gate_out_id,ifnull(u.c_name, u.user_name) creator_name,goo.order_no gate_out_order_no, wo.id wave_id,wo.order_no wave_order_no"
    			+ "  from wave_order_item woi "
    			+ "  left join wave_order wo on wo.id = woi.order_id "
    			+ "  left join gate_out_order goo on goo.order_no = woi.gate_out_no "
    			+ "  left join user_login u on u.id = goo.create_by"
    			+ "  where woi.check_flag = 'N' "
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
    
    @Before(Tx.class)
    public void checkOrder() throws Exception{
    	String msg = null;
    	String sql = "";
    	WaveOrderItem woi = null;
    	String wave_order_no = getPara("wave_order_no");
    	String cargo_bar_code = getPara("cargo_bar_code");
    	String gate_out_order_no = getPara("gate_out_order_no");
    	
    	if(StringUtils.isEmpty(gate_out_order_no)){
    		sql = "select woi.* from wave_order_item woi "
                    + " left join wave_order wo on woi.order_id = wo.id "
                    + " where woi.pickup_flag='Y' and check_flag = 'N' and wo.order_no=? and cargo_bar_code = ?";
            woi = WaveOrderItem.dao.findFirst(sql, wave_order_no, cargo_bar_code);
    	}else{
    		sql = "select woi.* from wave_order_item woi "
                    + " where woi.pickup_flag='Y' and check_flag = 'N' and woi.gate_out_no=?";
            woi = WaveOrderItem.dao.findFirst(sql, gate_out_order_no);
    	}

        if(woi != null){
        	woi.set("check_flag","Y").update();
        	msg = "success";
        }else{
        	msg = "此波次单不存在此商品";
        }
        
        Record re = new Record();
        re.set("msg", msg);
        
        renderJson(re);
    }
    
   
}
