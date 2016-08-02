package controllers.wms;import java.util.Calendar;import java.util.Date;import java.util.HashMap;import java.util.List;import java.util.Map;import models.UserLogin;import models.eeda.Leads;import org.apache.commons.lang.StringUtils;import org.apache.commons.mail.DefaultAuthenticator;import org.apache.commons.mail.Email;import org.apache.commons.mail.SimpleEmail;import org.apache.shiro.SecurityUtils;import org.apache.shiro.authc.AuthenticationException;import org.apache.shiro.authc.IncorrectCredentialsException;import org.apache.shiro.authc.LockedAccountException;import org.apache.shiro.authc.UnknownAccountException;import org.apache.shiro.authc.UsernamePasswordToken;import org.apache.shiro.authz.annotation.Logical;import org.apache.shiro.authz.annotation.RequiresRoles;import org.apache.shiro.subject.Subject;import com.google.gson.Gson;import com.jfinal.core.Controller;import com.jfinal.log.Logger;import com.jfinal.plugin.activerecord.Db;import com.jfinal.plugin.activerecord.Record;import controllers.eeda.util.DataTablesUtils;import controllers.util.DbUtils;public class MobileController extends Controller {    private Logger logger = Logger.getLogger(MobileController.class);    Subject currentUser = SecurityUtils.getSubject();    public void searchBarcode(){    	    	String barcode = getPara();  	     	    String sql = "SELECT sales_order_no,amount,shelves "    	    		+ "FROM  wave_order_item"    	    		+ "	WHERE  item_code= ?";    	            List<Record> recs = Db.find(sql,barcode);		    	    		    	    	renderJson(recs);    }}