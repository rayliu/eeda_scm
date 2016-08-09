package controllers.oms.inventory;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.eeda.OrderActionLog;

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
        
        

        String sql = "select * from ( SELECT inv.id,inv.cargo_name,inv.cargo_barcode,inv.cargo_code,inv.unit,inv.customer_id,inv.shelf_life ,"
        		+ "  inv.shelves, sum(inv.gate_in_amount) gate_in_amount, sum(inv.gate_out_amount) gate_out_amount,sum(inv.lock_amount) lock_amount,"
        		+ "  wh.warehouse_name,p.abbr customer_name,"
        		+ "  (case when (inv.gate_in_amount-inv.gate_out_amount)>0"
        		+ "  then '在库'"
        		+ "  else"
        		+ "  '已出库' end) cargo_status"
    			+ "  from inventory inv "
    			+ "  left join warehouse wh on wh.id = inv.warehouse_id"
    			+ "  left join party p on p.id = inv.customer_id"
    			+ group
    			+ "  ) A where 1 = 1 ";
        
        String condition = "";
        String jsonStr = getPara("jsonStr");
    	if(StringUtils.isNotEmpty(jsonStr)){
    		Gson gson = new Gson(); 
            Map<String, String> dto= gson.fromJson(jsonStr, HashMap.class);  
            condition = DbUtils.buildConditions(dto);
    	}
    	
        String sqlTotal = "select count(1) total from ("+ sql + condition +" ) B";
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));
        List<Record> BillingOrders = Db.find(sql + condition +sLimit);
        Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", pageIndex);
        BillingOrderListMap.put("iTotalRecords", rec.getLong("total"));
        BillingOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        BillingOrderListMap.put("aaData", BillingOrders);

        renderJson(BillingOrderListMap); 
    }
    
    //出库库存校验
    public void check(){
    	String name = getPara("name");
    	String amount = getPara("amount");
    	
    	Record re = new Record();
    	String sql = "";
    	if(StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(amount)){
    		sql = "select * from inventory inv where cargo_name = ? and (gate_in_amount - gate_out_amount) > 0 having count(*) >= ?"; 
    		Record record = Db.findFirst(sql , name, amount);
    		if(record != null){
    			re.set("order",record);
    			re.set("result", "ok");
    		}else{
    			sql = "select count(*) amount from inventory inv where cargo_name = ? and (gate_in_amount - gate_out_amount) > 0"; 
    			record = Db.findFirst(sql , name);
    			re.set("result", "此类商品库存数量只有"+record.getLong("amount"));
    		}
    	}else if(StringUtils.isNotEmpty(name)){
    		sql = "select * from inventory inv where cargo_name = ?"; 
    		Record record = Db.findFirst(sql , name);
    		if(record != null){
    			re.set("order",record);
    			re.set("result", "ok");
    		}else{
    			re.set("result", "库存不存在此类商品");
    		}
    	}
    	
    	renderJson(re);
    }


}
