package controllers.oms.loadOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.UserLogin;
import models.eeda.oms.InspectionOrder;
import models.eeda.oms.InspectionOrderItem;
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

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class LoadOrderController extends Controller {

	private Logger logger = Logger.getLogger(LoadOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/loadOrder/loadOrderList.html");
	}
	
    public void create() {
        render("/oms/loadOrder/loadOrderEdit.html");
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
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, inspectionOrder);
   			
   			//需后台处理的字段
   			//inspectionOrder.set("order_no", OrderNoGenerator.getNextOrderNo("DD"));
   			inspectionOrder.set("create_by", user.getLong("id"));
   			inspectionOrder.set("create_stamp", new Date());
   			inspectionOrder.save();
   			
   			id = inspectionOrder.getLong("id").toString();
   		}
   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("item_list");
		DbUtils.handleList(itemList, id, InspectionOrderItem.class, "order_id");
		
		long create_by = inspectionOrder.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);
		Record r = inspectionOrder.toRecord();
   		r.set("creator_name", user_name);
   		renderJson(r);
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
    	CustomCompany custom = CustomCompany.dao.findById(inspectionOrder.getLong("custom_id"));
    	setAttr("custom", custom);
    	
    	//仓库回显
    	Warehouse warehouse = Warehouse.dao.findById(inspectionOrder.getLong("warehouse_id"));
    	setAttr("warehouse", warehouse);

    	//用户信息
    	long create_by = inspectionOrder.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
        render("/oms/loadOrder/loadOrderEdit.html");
    }
    
    
    public void list() {
    	String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        String sql = "SELECT inso.*, ifnull(u.c_name, u.user_name) creator_name ,wh.warehouse_name"
    			+ "  from inspection_order inso "
    			+ "  left join warehouse wh on wh.id = inso.warehouse_id"
    			+ "  left join user_login u on u.id = inso.create_by"
    			+ "   where 1 =1 ";
        
        String condition = DbUtils.buildConditions(getParaMap());

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
    
}
