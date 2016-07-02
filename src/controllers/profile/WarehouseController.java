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
import models.Warehouse;
import models.WarehouseOrder;
import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderCount;
import models.eeda.oms.SalesOrderGoods;
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
    
    
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_W_LIST})
	public void index() {
		render("/profile/warehouse/warehouseList.html");
	}
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_W_LIST})
	public void list() {
		Map warehouseListMap = null;
		String warehouseName = getPara("warehouseName");
		String warehouseAddress = getPara("warehouseAddress");
		
		if(warehouseName == null && warehouseAddress == null){
			String sLimit = "";
			String pageIndex = getPara("sEcho");
			if (getPara("iDisplayStart") != null
					&& getPara("iDisplayLength") != null) {
				sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
						+ getPara("iDisplayLength");
			}			
			String sqlTotal = "select count(1) total from warehouse w left join office o on o.id = w.office_id where (o.id = " + parentID + " or o.belong_office = " + parentID +")";
			Record rec = Db.findFirst(sqlTotal);
			logger.debug("total records:" + rec.getLong("total"));
	
			String sql = "select w.*, lc.name dname from warehouse w"
							+ " left join location lc on w.location = lc.code "
							+ " left join office o on o.id = w.office_id where (o.id = " + parentID + " or o.belong_office = " + parentID +")"
							+ " order by w.id desc "
							+ sLimit;
	
			List<Record> warehouses = Db.find(sql);
	
			warehouseListMap = new HashMap();
			warehouseListMap.put("sEcho", pageIndex);
			warehouseListMap.put("iTotalRecords", rec.getLong("total"));
			warehouseListMap.put("iTotalDisplayRecords", rec.getLong("total"));
	
			warehouseListMap.put("aaData", warehouses);
		}else{
			String sLimit = "";
			String pageIndex = getPara("sEcho");
			if (getPara("iDisplayStart") != null
					&& getPara("iDisplayLength") != null) {
				sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
						+ getPara("iDisplayLength");
			}
			String sqlTotal = "select count(1) total from warehouse w left join office o on o.id = w.office_id where w.warehouse_name like '%"+warehouseName+"%' and w.warehouse_address like '%"+warehouseAddress+"%' and (o.id = " + parentID + " or o.belong_office = " + parentID +")";
			Record rec = Db.findFirst(sqlTotal);
			logger.debug("total records:" + rec.getLong("total"));
	
			String sql = "select w.*,(select trim(concat(l2.name, ' ', l1.name,' ',l.name)) from location l left join location  l1 on l.pcode =l1.code left join location l2 on l1.pcode = l2.code where l.code=w.location) dname,lc.name from warehouse w"
							+ " left join location lc on w.location = lc.code"
							+ "  left join office o on o.id = w.office_id"
							+ "  where w.warehouse_name like '%"+warehouseName+"%' and w.warehouse_address like '%"+warehouseAddress+"%' "
							+ "  and (o.id = " + parentID + " or o.belong_office = " + parentID +")"
							+ "order by w.id desc "
							+ sLimit;
	
			List<Record> warehouses = Db.find(sql);
	
			warehouseListMap = new HashMap();
			warehouseListMap.put("sEcho", pageIndex);
			warehouseListMap.put("iTotalRecords", rec.getLong("total"));
			warehouseListMap.put("iTotalDisplayRecords", rec.getLong("total"));
	
			warehouseListMap.put("aaData", warehouses);
		}
		renderJson(warehouseListMap);
	}
	
	public void listContact(){
		List<Contact> contactjson = Contact.dao.find("select * from contact");			
        renderJson(contactjson);
	}
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_W_CREATE})
	public void add() {
		setAttr("saveOK", false);
		render("/profile/warehouse/warehouseEdit.html");
	}
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_W_UPDATE})
	public void edit() {
		long id = getParaToLong();

		Warehouse warehouse = Warehouse.dao.findById(id);
		setAttr("warehouse", warehouse);

		Contact sp = Contact.dao.findFirst("select * from contact where id = (select contact_id from party where id="+warehouse.get("sp_id")+")");
		setAttr("sp", sp);
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
   			warehouse.set("order_no", OrderNoGenerator.getNextOrderNo("DD"));
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
