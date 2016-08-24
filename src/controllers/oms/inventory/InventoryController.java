package controllers.oms.inventory;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.eeda.OrderActionLog;
import models.eeda.oms.Inventory;

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

import controllers.util.DbUtils;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class InventoryController extends Controller {

	private Logger logger = Logger.getLogger(InventoryController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/inventory/inventoryList.html");
	}
	
	public void addInventory() {
		render("/oms/check/checkEdit.html");
	}
	
    public void create() {
        render("/oms/inventory/inventoryEdit.html");
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

    
    public void list() {
    	String sLimit = "";
        String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }
        String showItem = getPara("showItem");
        String group = " group by inv.cargo_name,inv.customer_id ";
        if("Y".equals(showItem)){
        	group = " group by inv.id ";
        }
        
        
        String jsonStr = getPara("jsonStr");
        String conditions = " where 1 = 1 ";
        if(StringUtils.isNotEmpty(jsonStr)){
        	 Gson gson = new Gson();  
             Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
             String customer_id = (String)dto.get("customer_id");
             String cargo_name = (String)dto.get("cargo_name");
             String cargo_status = (String)dto.get("cargo_status");
             String gate_in_stamp_begin_time = (String)dto.get("gate_in_stamp_begin_time");
             String gate_in_stamp_end_time = (String)dto.get("gate_in_stamp_end_time");
             String gate_out_stamp_begin_time = (String)dto.get("gate_out_stamp_begin_time");
             String gate_out_stamp_end_time = (String)dto.get("gate_out_stamp_end_time");
             
             if(StringUtils.isNotEmpty(customer_id)){
             	conditions += " and inv.customer_id = "+customer_id;
             }
             if(StringUtils.isNotEmpty(cargo_name)){
             	conditions += " and inv.cargo_name like '%"+cargo_name+"%'";
             }
             if(StringUtils.isNotEmpty(cargo_status)){
             	conditions += " and  (case "
        		+ "  when (inv.lock_amount>0)"
        		+ "  then '已锁定'"
        		+ "  when (inv.gate_in_amount-inv.gate_out_amount)>0"
        		+ "  then '在库'"
        		+ "  else"
        		+ "  '已出库' end) = '"+cargo_status+"'";
             }
             
             if(StringUtils.isNotEmpty(gate_in_stamp_begin_time) && StringUtils.isNotEmpty(gate_in_stamp_end_time)){
            	 if(StringUtils.isEmpty(gate_in_stamp_begin_time)){
                  	gate_in_stamp_begin_time = "1970-1-1";
                  }
                  if(StringUtils.isEmpty(gate_in_stamp_end_time)){
                  	gate_in_stamp_end_time = "2037-12-31";
                  }else{
                  	gate_in_stamp_end_time = gate_in_stamp_end_time +" 23:59:59" ;
                  }
                  conditions += " and gate_in_stamp between '"+gate_in_stamp_begin_time+"' and '"+gate_in_stamp_end_time+"'";
             }
             
             if(StringUtils.isNotEmpty(gate_out_stamp_begin_time) && StringUtils.isNotEmpty(gate_out_stamp_end_time)){
            	 if(StringUtils.isEmpty(gate_out_stamp_begin_time)){
                  	gate_out_stamp_begin_time = "1970-1-1";
                  }
                  if(StringUtils.isEmpty(gate_out_stamp_end_time)){
                  	gate_out_stamp_end_time = "2037-12-31";
                  }else{
                  	gate_out_stamp_end_time = gate_out_stamp_end_time +" 23:59:59" ;
                  }
                  conditions += " and gate_out_stamp between '"+gate_out_stamp_begin_time+"' and '"+gate_out_stamp_end_time+"'";
             }
             
        }
       
        String sql = "select * from ( SELECT inv.id,inv.cargo_name,inv.cargo_barcode,inv.cargo_code,inv.unit,inv.customer_id,inv.shelf_life ,"
        		+ "  inv.shelves, sum(inv.gate_in_amount) gate_in_amount, sum(inv.gate_out_amount) gate_out_amount,"
        		+ "  sum(inv.lock_amount) lock_amount,sum(inv.damage_amount) damage_amount,"
        		+ "  wh.warehouse_name,p.abbr customer_name,"
        		+ "  (case "
        		+ "  when (inv.lock_amount>0)"
        		+ "  then '已锁定'"
        		+ "  when (inv.gate_in_amount-inv.gate_out_amount)>0"
        		+ "  then '在库'"
        		+ "  else"
        		+ "  '已出库' end) cargo_status"
    			+ "  from inventory inv "
    			+ "  left join warehouse wh on wh.id = inv.warehouse_id"
    			+ "  left join party p on p.id = inv.customer_id"
    			+ conditions
    			+ group
    			+ "  ) A  ";
        
           	
    	String orderBy = " order by shelves,shelf_life ";
        String sqlTotal = "select count(1) total from ("+ sql +" ) B";
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));
        List<Record> BillingOrders = Db.find(sql + orderBy +sLimit);
        Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", pageIndex);
        BillingOrderListMap.put("iTotalRecords", rec.getLong("total"));
        BillingOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        BillingOrderListMap.put("aaData", BillingOrders);

        renderJson(BillingOrderListMap); 
    }
    
    //出库库存校验
    public void check(){
    	String value = getPara("value");
    	String amount = getPara("amount");
    	
    	Record re = new Record();
    	String sql = "";
    	if(StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(amount)){
    		sql = "select count(*) amount from inventory inv where shelves is not null and shelves !='' and (cargo_name = ? or cargo_barcode = ?)  and damage_amount = 0 and lock_amount = 0 and gate_out_amount = 0"; 
    		Record record = Db.findFirst(sql , value, value);
    		long total = record.getLong("amount");
    		if(total >= Double.parseDouble(amount)){
    			re.set("order",record);
    			re.set("result", "ok");
    		}else{
    			re.set("result", "对不起，此商品库存数量只有"+total+",或者部分未上架");
    		}
    	}else if(StringUtils.isNotEmpty(value)){
    		sql = "select * from inventory inv where cargo_name = ? or cargo_barcode = ?  limit 0,2"; 
    		Record record = Db.findFirst(sql , value, value);
    		if(record != null){
    			re.set("order",record);
    			re.set("result", "ok");
    		}else{
    			re.set("result", "库存不存在此商品");
    		}
    	}
    	
    	renderJson(re);
    }
    
    @Before(Tx.class)
    public void updateShelves() throws Exception{
    	String id = getPara("order_id");
    	String shelves = getPara("shelves");
    	
    	Inventory inv = Inventory.dao.findById(id);
    	if(inv!=null){
    		inv.set("shelves", shelves);
        	inv.update();
    	}

    	renderJson(inv);
    }


}
