package controllers.api;

import interceptor.SetAttrLoginUserInterceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import models.UserLogin;
import models.eeda.OrderActionLog;
import models.eeda.oms.LogisticsOrder;
import models.eeda.oms.SalesOrderCount;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.oms.SalesOrder;
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

import controllers.api.service.BaseItemApiService;
import controllers.api.service.SalesOrderService;
import controllers.oms.custom.dto.DingDanBuilder;
import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.DingDanGoodsDto;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;

//@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class ApiController extends Controller {

	private Logger logger = Logger.getLogger(ApiController.class);
	
	@Before(Tx.class)
	public static void saveLog(String orderType, String content) {
        OrderActionLog log = new OrderActionLog();
        log.set("order_type", orderType);
        log.set("action", "get");
        log.set("json", content);
        log.set("time_stamp", new Date());
        log.save();
    }
	
//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		redirect("/apidoc");
	}
	
    public void salesOrder() throws InstantiationException, IllegalAccessException {
        String method = this.getRequest().getMethod();
        if("GET".equals(method)){
//            soGetHandle();
        }else if("POST".equals(method)){
            SalesOrderService s = new SalesOrderService(this);
            s.saveSo();
        }else{
            
        }
    }
    
    public void salesOrderQuery() throws InstantiationException, IllegalAccessException {
        String method = this.getRequest().getMethod();
        if("GET".equals(method)){
            
        }else if("POST".equals(method)){
            //soGetHandle();
            SalesOrderService s = new SalesOrderService(this);
            s.querySo();
        }else{
            
        }
    }
    
    public void baseItem() throws InstantiationException, IllegalAccessException {
        String method = this.getRequest().getMethod();
        BaseItemApiService is = new BaseItemApiService(this);
        is.process(method);
    }
    
    public void baseItemQuery() throws InstantiationException, IllegalAccessException {
        BaseItemApiService is = new BaseItemApiService(this);
        is.query();
    }

    public static String getFullURL(HttpServletRequest req) {
        StringBuffer requestURL = req.getRequestURL();
        String queryString = req.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    public static String getRequestPayload(HttpServletRequest req) {  
        StringBuilder sb = new StringBuilder();  
        try(BufferedReader reader = req.getReader();) {  
                 char[]buff = new char[1024];  
                 int len;  
                 while((len = reader.read(buff)) != -1) {  
                          sb.append(buff,0, len);  
                 }  
        }catch (IOException e) {  
            e.printStackTrace();  
        }  
        return sb.toString(); 
    }
  
}
