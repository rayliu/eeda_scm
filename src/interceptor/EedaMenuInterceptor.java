package interceptor;

import java.util.Collections;
import java.util.List;

import models.UserLogin;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class EedaMenuInterceptor implements Interceptor {
    private Logger logger = Logger.getLogger(EedaMenuInterceptor.class);

    @Override
    public void intercept(Invocation ai) {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            loadMenu(ai.getController());
        }
        ai.invoke();
    }

    /*
     * 这个如果不放这里,那么每个controller的index凡是要render HTML的地方都要写调用
     */
    private void loadMenu(Controller controller) {
        logger.debug("EedaInterceptor loadMenu...");
        UserLogin user = UserLogin.getCurrentUser();
        Long office_id = user.getLong("office_id");

        // 查询当前用户菜单
        String sql = "select distinct module.* from eeda_modules sub, eeda_modules module, permission p "
                + " where sub.parent_id = module.id and sub.office_id=? "
                + " and p.code like '%list' and sub.id=p.module_id"
                + " and sub.sys_only='N' order by seq";
        List<Record> modules = Db.find(sql, office_id);
        for (Record module : modules) {
            sql = "select m.*, p.url from eeda_modules m, permission p "
                    + " where parent_id =? and office_id=? and p.code like '%list' and m.id=p.module_id"
                    + " order by seq";

            List<Record> orders = Db.find(sql, module.get("id"), office_id);
            module.set("orders", orders);
        }
  
        if (modules == null)
            modules = Collections.EMPTY_LIST;
        controller.setAttr("modules", modules);
    }
}
