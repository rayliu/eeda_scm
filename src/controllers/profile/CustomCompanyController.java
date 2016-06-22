package controllers.profile;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.UserLogin;
import models.eeda.profile.CustomCompany;

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
public class CustomCompanyController extends Controller {

	private Logger logger = Logger.getLogger(CustomCompanyController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/profile/customCompany/customCompanyList.html");
	}
	
    public void create() {
        render("/profile/customCompany/customCompanyEdit.html");
    }
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        CustomCompany customCompany = new CustomCompany();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			customCompany = CustomCompany.dao.findById(id);
   			DbUtils.setModelValues(dto, customCompany);
   			
   			//需后台处理的字段
   			customCompany.set("update_by", user.getLong("id"));
   			customCompany.set("update_stamp", new Date());
   			customCompany.update();
   		} else {
   			//create 
   			customCompany = new CustomCompany();
   			DbUtils.setModelValues(dto, customCompany);
   			
   			//需后台处理的字段
   			customCompany.set("create_by", user.getLong("id"));
   			customCompany.set("create_stamp", new Date());
   			customCompany.save();
   		}

   		long create_by = customCompany.getLong("create_by");
   		String user_name = LoginUserController.getUserNameById(create_by);

   		Record r = customCompany.toRecord();
   		r.set("create_by_name", user_name);
   		
   		renderJson(r);
   	}
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	CustomCompany customCompany = CustomCompany.dao.findById(id);
    	setAttr("order", customCompany);
    	
    	//用户信息
    	long create_by = customCompany.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
        render("/profile/customCompany/customCompanyEdit.html");
    }
    

    public void list() {
    	String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        String sql = "SELECT ccy.*, ifnull(u.c_name, u.user_name) creator_name "
    			+ "  from custom_company ccy "
    			+ "  left join user_login u on u.id = ccy.create_by where 1 =1 ";
        
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
    
    
    public void submitYunDan(){
    	//CustomManager.getInstance().sendYunDan(dto);
    }

}
