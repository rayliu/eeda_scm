package controllers.profile;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.UserLogin;
import models.eeda.profile.CustomCompany;
import models.eeda.profile.Warehouse;
import models.eeda.profile.WarehouseShelves;

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
public class WarehouseShelvesController extends Controller {

	private Logger logger = Logger.getLogger(WarehouseShelvesController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/profile/warehouseShelves/warehouseShelvesList.html");
	}
	
    public void create() {
        render("/profile/warehouseShelves/warehouseShelvesEdit.html");
    }
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        WarehouseShelves warehouseShelves = new WarehouseShelves();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			warehouseShelves = WarehouseShelves.dao.findById(id);
   			DbUtils.setModelValues(dto, warehouseShelves);
   			
   			//需后台处理的字段
   			warehouseShelves.set("update_by", user.getLong("id"));
   			warehouseShelves.set("update_stamp", new Date());
   			warehouseShelves.update();
   		} else {
   			DbUtils.setModelValues(dto, warehouseShelves);
   			
   			//需后台处理的字段
   			warehouseShelves.set("create_by", user.getLong("id"));
   			warehouseShelves.set("create_stamp", new Date());
   			warehouseShelves.save();
   		}

   		long create_by = warehouseShelves.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);

   		Record r = warehouseShelves.toRecord();
   		r.set("creator_name", user_name);
   		
   		renderJson(r);
   	}
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	WarehouseShelves warehouseShelves = WarehouseShelves.dao.findById(id);
    	setAttr("order", warehouseShelves);
    	
    	//仓库回显
    	long warehouse_id = warehouseShelves.getLong("warehouse_id");
    	Warehouse warehouse = Warehouse.dao.findById(warehouse_id);
    	if(warehouse != null){
    		String warehouse_name = warehouse.getStr("warehouse_name");
    		setAttr("warehouse_name", warehouse_name);
    	}
    	
    	//用户信息
    	long create_by = warehouseShelves.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
        render("/profile/warehouseShelves/warehouseShelvesEdit.html");
    }
    

    public void list() {
    	String sLimit = "";
    	String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }

        String sql = "select * from(SELECT ws.*, ifnull(u.c_name, u.user_name) creator_name ,wh.warehouse_name"
    			+ "  from warehouse_shelves ws "
    			+ "  left join warehouse wh on wh.id = ws.warehouse_id "
    			+ "  left join user_login u on u.id = ws.create_by) A where 1 =1 ";
        
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
    
    //查询报关列表
    public void search(){
    	String inpiutFiled = getPara("customCompanyName").trim();
    	String conditions = " where 1 = 1";
    	if(!"".equals(inpiutFiled))
    		conditions += " and shop_name like '%" + inpiutFiled + "%' or shop_no like '%" + inpiutFiled + "%'";
    	
    	String sql = " select * from custom_company ";
    	List<CustomCompany> list = CustomCompany.dao.find(sql + conditions);
    	
    	renderJson(list); 
    }

}
