package interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import models.Office;
import models.UserLogin;
import models.UserOffice;
import models.yh.profile.OfficeCofig;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class SetAttrLoginUserInterceptor implements Interceptor{
	private Logger logger = Logger.getLogger(SetAttrLoginUserInterceptor.class);
	@Override
	public void intercept(Invocation ai) {
		Subject currentUser = SecurityUtils.getSubject();
		if(currentUser.isAuthenticated()){
		    UserLogin user = ai.getController().getSessionAttr("user");
		    if(user==null){
                user = UserLogin.dao.findFirst("select * from user_login where user_name=?",currentUser.getPrincipal());
                ai.getController().setSessionAttr("user", user);
		    }else{
		        logger.debug("SetAttrLoginUserInterceptor get user from session, c_name:"+user.get("c_name") );
		    }
			
			if(user.get("c_name") != null && !"".equals(user.get("c_name"))){
				ai.getController().setAttr("userId", user.get("c_name"));
			}else{
				ai.getController().setAttr("userId", currentUser.getPrincipal());
			}
			
			String office_name = ai.getController().getSessionAttr("office_name");
			if(office_name==null){
                Record uo = Db.findFirst("select o.office_name from user_office uo left join office o on uo.office_id=o.id" 
                        +" where uo.user_name ='"+currentUser.getPrincipal()+"' and uo.is_main=1");
                if(uo != null){
                    ai.getController().setSessionAttr("office_name", uo.get("office_name"));
                }
			}else{
                logger.debug("SetAttrLoginUserInterceptor get office_name from session:" + office_name);
            }
			ai.getController().setAttr("user_login_id", currentUser.getPrincipal());
			ai.getController().setAttr("permissionMap", ai.getController().getSessionAttr("permissionMap"));
		}
		setSysTitle(ai.getController());
		ai.invoke();
	}
	
	private void setSysTitle(Controller controller) {
	    OfficeCofig of = controller.getSessionAttr("SYS_CONFIG");
	    if(of==null){
            HttpServletRequest request = controller.getRequest();
            String serverName = request.getServerName();
            String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
            
            logger.debug("Current host path:"+basePath);
            of = OfficeCofig.dao.findFirst("select * from office_config where domain like '"+serverName +"%' or domain like '%"+serverName +"%'");
            if(of==null){//没有配置公司的信息会导致页面出错，显示空白页
                of = new OfficeCofig();
                of.set("system_title", "易达物流");
                of.set("logo", "/yh/img/eeda_logo.ico");
            }
            controller.setSessionAttr("SYS_CONFIG", of);
	    }else{
	        logger.debug("SetAttrLoginUserInterceptor get system_title from session:" + of.getStr("system_title"));
	    }
	    controller.setAttr("SYS_CONFIG", of);
	}

}
