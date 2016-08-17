package controllers.oms.waveOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.UserLogin;
import models.eeda.oms.GateOutOrder;
import models.eeda.oms.WaveOrder;
import models.eeda.oms.WaveOrderItem;

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
public class WaveOrderController extends Controller {

	private Logger logger = Logger.getLogger(WaveOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/waveOrder/waveOrderList.html");
	}
	
    public void create() {
        render("/oms/waveOrder/waveOrderEdit.html");
    }
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        WaveOrder waveOrder = new WaveOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			waveOrder = WaveOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, waveOrder);
   			
   			//需后台处理的字段
   			waveOrder.set("update_by", user.getLong("id"));
   			waveOrder.set("update_stamp", new Date());
   			waveOrder.update();
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, waveOrder);
   			
   			//需后台处理的字段
   			waveOrder.set("order_no", OrderNoGenerator.getNextOrderNo("BC"));
   			waveOrder.set("create_by", user.getLong("id"));
   			waveOrder.set("create_stamp", new Date());
   			waveOrder.save();
   			
   			id = waveOrder.getLong("id").toString();
   		}
   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("item_list");
		DbUtils.handleList(itemList, id , WaveOrderItem.class, "order_id");
		
		for(Map item:itemList){
			String itemId = (String)item.get("id");
			if("".equals(itemId)){
				String gate_out_no = (String)item.get("gate_out_no");
				GateOutOrder go = GateOutOrder.dao.findFirst("select * from gate_out_order where order_no = ?",gate_out_no);
				go.set("wave_flag", "Y");
				go.set("wave_id", id);
				go.update();
			}	
		}
		
		long create_by = waveOrder.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);
		Record r = waveOrder.toRecord();
   		r.set("creator_name", user_name);
   		renderJson(r);
   	}
    
    
    private List<Record> getItems(String orderId) {
		String itemSql = "select * from  wave_order_item where order_id=?";
		List<Record> itemList = Db.find(itemSql, orderId);
		return itemList;
	}
    
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	WaveOrder waveOrder = WaveOrder.dao.findById(id);
    	setAttr("order", waveOrder);
    	
    	//获取明细表信息
    	setAttr("itemList", getItems(id));
    	//用户信息
    	long create_by = waveOrder.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
        render("/oms/waveOrder/waveOrderEdit.html");
    }
    
    
    public void list() {
    	String sLimit = "";
    	String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }

        String sql = "SELECT wor.*, ifnull(u.c_name, u.user_name) creator_name "
    			+ "  from wave_order wor "
    			+ "  left join user_login u on u.id = wor.create_by"
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
    
    
    public void gateOutlist() {
    	String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        String sql = "select * from(SELECT goo.*, ifnull(u.c_name, u.user_name) creator_name ,w.warehouse_name"
    			+ "  from gate_out_order goo "
    			+ "  left join user_login u on u.id = goo.create_by"
    			+ "  left join warehouse w on w.id = goo.warehouse_id"
    			+ "  where goo.status = '已确认' and wave_flag = 'N') A where 1 =1";
        
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
    	String order_ids = getPara("order_ids");
    	List<Record> list = null;
    	if(StringUtils.isNotEmpty(order_id)){
    		list = getItems(order_id);
    	}else if(StringUtils.isNotEmpty(order_ids)){
    		String sql = "SELECT NULL id, goo.order_no gate_out_no, inv.cargo_code cargo_bar_code,"
    				+ " goo.customer_refer_no sales_order_no, inv.shelves, inv.lock_amount amount,"
    				+ " null item_code, NULL order_no_code"
    				+ " FROM inventory inv LEFT JOIN gate_out_order goo on goo.id = inv.gate_out_order_id"
    				+ " WHERE inv.gate_out_order_id IN ("+order_ids+") "
    				+ " ORDER BY inv.shelves";
    		list = Db.find(sql);
    	}
    		

    	Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", 1);
        BillingOrderListMap.put("iTotalRecords", list.size());
        BillingOrderListMap.put("iTotalDisplayRecords", list.size());

        BillingOrderListMap.put("aaData", list);

        renderJson(BillingOrderListMap); 
    }
    
}
