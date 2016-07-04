package controllers.profile;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Office;
import models.ParentOfficeModel;
import models.UserLogin;
import models.Warehouse;
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

import controllers.util.DbUtils;
import controllers.util.ParentOffice;
import controllers.util.PermissionConstant;
@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class WarehouseController extends Controller{

    private Logger logger = Logger.getLogger(WarehouseController.class);
    Subject currentUser = SecurityUtils.getSubject();
    
    ParentOfficeModel pom = ParentOffice.getInstance().getOfficeId(this);
    Long parentID = pom.getParentOfficeId();
    
    
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_W_LIST})
	public void index() {
		render("/profile/warehouse/warehouseList.html");
	}
    
    
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_W_LIST})
    public void list() {
    	String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        String sql = "SELECT sor.*, ifnull(u.c_name, u.user_name) creator_name "
    			+ "  from sales_order sor "
    			+ "  left join user_login u on u.id = sor.create_by"
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

	
	
	public void create() {
		render("/profile/warehouse/warehouseEdit.html");
	}
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_W_UPDATE})
	public void edit() {
		String id = getPara("id");

		Warehouse warehouse = Warehouse.dao.findById(id);
		setAttr("warehouse", warehouse);

		//收货人地址
    	String location = warehouse.getStr("location");
    	
    	if(StringUtils.isNotEmpty(location)){
    		Record re = Db.findFirst("select get_loc_full_name(?) address",location);
        	setAttr("location_name", re.get("address"));
    	}

		render("/profile/warehouse/warehouseEdit.html");
	}
	
	
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_W_DELETE})
	public void delete() {
		
		String id = getPara();
		Warehouse warehouse = Warehouse.dao.findById(id);
		if(!"inactive".equals(warehouse.get("status"))){
			warehouse.set("status", "inactive");
		}else{
			warehouse.set("status", "active");
		}
		warehouse.update();
		redirect("/warehouse");
	}

	@RequiresPermissions(value = {PermissionConstant.PERMSSION_W_CREATE, PermissionConstant.PERMSSION_W_UPDATE}, logical=Logical.OR)
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
   			//warehouse.set("order_no", OrderNoGenerator.getNextOrderNo("DD"));
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

	public void findDocaltion(){
		String officeId = getPara("officeId");
		Office office = Office.dao.findById(officeId);
		String code = null;
		if(office.get("location") != null && !"".equals(office.get("location"))){
			code = office.get("location");
		}
		logger.debug("所在地："+code);
        renderJson(code);
	}
	
	
}
