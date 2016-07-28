package controllers.profile;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Location;
import models.Office;
import models.ParentOfficeModel;
import models.UserLogin;
import models.WarehouseOrder;
import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderCount;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.profile.CustomCompany;
import models.eeda.profile.Warehouse;
import models.eeda.profile.WarehouseShelves;
import models.yh.profile.Contact;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

import controllers.util.DbUtils;
import controllers.util.OrderNoGenerator;
import controllers.util.ParentOffice;
import controllers.util.PermissionConstant;
@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class WarehouseController extends Controller{

    private Logger logger = Logger.getLogger(WarehouseController.class);
    Subject currentUser = SecurityUtils.getSubject();
    
    ParentOfficeModel pom = ParentOffice.getInstance().getOfficeId(this);
    Long parentID = pom.getParentOfficeId();
    
    
	public void index() {
		render("/profile/warehouse/warehouseList.html");
	}
    
    public void create(){
    	render("/profile/warehouse/warehouseEdit.html");
    }
    
	public void list() {
		Map warehouseListMap = null;
		String sLimit = "";
		String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }
        
		String sql = "select * from(select w.*, u.c_name create_name from warehouse w "
				+ " left join user_login u on w.create_by=u.id) A where 1=1 ";
        
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
        
        List<Record> warehouses = Db.find(sql+ condition + " order by create_stamp desc " +sLimit);
	
			warehouseListMap = new HashMap();
			warehouseListMap.put("sEcho", pageIndex);
			warehouseListMap.put("iTotalRecords", rec.getLong("total"));
			warehouseListMap.put("iTotalDisplayRecords", rec.getLong("total"));
	
			warehouseListMap.put("aaData", warehouses);
		
		renderJson(warehouseListMap);
	}
	
	public void stop() {
		String id = getPara("id");
		Warehouse warehouse = Warehouse.dao.findById(id);
		if("inactive".equals(warehouse.get("status"))){
			warehouse.set("status", "active");
		}else{
			warehouse.set("status", "inactive");
		}
		warehouse.update();
		
		renderJson(warehouse);
	}

	@Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        Warehouse warehouse = new Warehouse();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			warehouse = Warehouse.dao.findById(id);
   			DbUtils.setModelValues(dto, warehouse);
   			
   			//需后台处理的字段
   			warehouse.set("update_by", user.getLong("id"));
   			warehouse.set("update_stamp", new Date());
   			warehouse.update();
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, warehouse);
   			
   			//需后台处理的字段
   			warehouse.set("create_by", user.getLong("id"));
   			warehouse.set("create_stamp", new Date());
   			warehouse.save();

   		}
		long create_by = warehouse.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);

   		Record r = warehouse.toRecord();
   		r.set("create_by_name", user_name);
   		renderJson(r);
   	}
	
	@Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	Warehouse warehouse = Warehouse.dao.findById(id);
    	setAttr("warehouse", warehouse);
    	
    	//	地址回显
    	String district = warehouse.getStr("location");
    	Record re = Db.findFirst("select get_loc_full_name(?) address",district);
    	setAttr("location_name", re.get("address"));
    
    	//用户信息
//    	long create_by = warehouse.getLong("create_by");
//    	UserLogin user = UserLogin.dao.findById(create_by);
//    	setAttr("user",user);
    	
        render("/profile/warehouse/warehouseEdit.html");
    }
	
	
	//查询仓库列表
    public void search(){
    	String inpiutFiled = getPara("warehouseName").trim();
    	String conditions = " where 1 = 1";
    	if(!"".equals(inpiutFiled))
    		conditions += " and warehouse_name like '%" + inpiutFiled + "%' or warehouse_address like '%" + inpiutFiled + "%'";
    	
    	String sql = " select * from warehouse ";
    	List<Warehouse> list = Warehouse.dao.find(sql + conditions);
    	
    	renderJson(list); 
    }

}
