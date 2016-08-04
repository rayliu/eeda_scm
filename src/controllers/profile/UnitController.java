package controllers.profile;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ParentOfficeModel;
import models.eeda.profile.Unit;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import controllers.util.DbUtils;
import controllers.util.ParentOffice;
import controllers.util.PermissionConstant;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class UnitController extends Controller {
    private Logger logger = Logger.getLogger(UnitController.class);
    Subject currentUser = SecurityUtils.getSubject();
    ParentOfficeModel pom = ParentOffice.getInstance().getOfficeId(this);

    public void searchAllUnit() {
        List<Record> units = Db.find("select * from unit");
        renderJson(units);
    }


    public void index() {
        render("/yh/profile/unit/unitList.html");
    }
    
    public void create() {
        render("/yh/profile/unit/unitEdit.html");
    }
    
    public void list() {
        String sLimit = "";
        String pageIndex = getPara("draw");
        if (getPara("start") != null && getPara("length") != null) {
            sLimit = " LIMIT " + getPara("start") + ", " + getPara("length");
        }

        String sql = "select * from(SELECT * from unit) A where 1 =1 ";
        
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
        
        List<Record> BillingOrders = Db.find(sql+ condition + " order by id desc " +sLimit);
        Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", pageIndex);
        BillingOrderListMap.put("iTotalRecords", rec.getLong("total"));
        BillingOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        BillingOrderListMap.put("aaData", BillingOrders);

        renderJson(BillingOrderListMap); 
    }

    // 编辑条目按钮
    public void edit() {
        String id = getPara("id");
        Unit u = Unit.dao.findById(id);
        setAttr("order", u);
        
        render("/yh/profile/unit/unitEdit.html");
        
    }

    // 删除条目
    @RequiresPermissions(value = { PermissionConstant.PERMSSION_T_DELETE })
    public void delete() {
        String id = getPara();
        if (id != null) {
            Unit l = Unit.dao.findById(id);
            Object obj = l.get("is_stop");
            if (obj == null || "".equals(obj) || obj.equals(false)
                    || obj.equals(0)) {
                l.set("is_stop", true);
            } else {
                l.set("is_stop", false);
            }
            l.update();
        }
        redirect("/unit");
    }

    // 添加,或者编辑保存
    public void save() throws Exception{
        String jsonStr=getPara("params");
        
        Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
        Unit r = null;
        String id = (String) dto.get("id");
        String name = (String) dto.get("name");
        if (StringUtils.isBlank(id)) {
            r = new Unit();
            r.set("name", name);
            r.save();
        } else {
            r = Unit.dao.findById(id);
            r.set("name", name);
            r.update();
        }
        renderJson(r);
    }
    
    
    public void searchUnit(){
    	String value = getPara("value");
    	String conditions = " where 1 = 1" ;
    	
    	if(StringUtils.isNotEmpty(value)){
    		conditions += " and name like '%"+value+"%'";
    	}
    	String sql = "select * from unit";
    	
    	List<Record> re = Db.find(sql+conditions); 
    	renderJson(re);
    }
}
