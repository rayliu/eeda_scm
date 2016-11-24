package interceptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    //这个map 作为登录用户的cahce, 免得每次都查很多次DB，消耗connection
    public static Map<Long, List<Record>> menuCache = null;

    public static Map<Long, List<Record>> getMenuCache() {
        return menuCache;
    }

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
        Long user_id = user.getLong("id");
        Long office_id = user.getLong("office_id");
        
        List<Record> modules = Collections.EMPTY_LIST;
        if(menuCache == null){
            menuCache = new HashMap<Long, List<Record>>();
            modules = buildMenuRecs(user, user_id, office_id);
        }else{
            if(menuCache.get(user_id)==null){
                modules = buildMenuRecs(user, user_id, office_id);
            }else{
                modules = menuCache.get(user_id);
            }
        }
        
        controller.setAttr("modules", modules);
    }

    private List<Record> buildMenuRecs(UserLogin user, Long user_id,
            Long office_id) {
        List<Record> modules;
        String username = user.getStr("user_name");
        // 查询当前用户菜单
        String sql = "select distinct module.* from eeda_modules module"
                + " where exists ("
                + "     select sub.*, rp.permission_code, rp.is_authorize, rp.role_code from eeda_modules sub "
                + "         left join module_role mr on mr.module_id = sub.id"
                + "         left join role r on mr.role_id=r.id"
                + "        left join role_permission rp on rp.module_role_id=mr.id"
                + "    where sub.parent_id = module.id and sub.office_id=? "
                + "        and rp.permission_code like '%list' and rp.is_authorize=1 "
                + "        and rp.role_id in ("
                + "             select r.id from role r, user_role ur "
                + "             where ur.role_code=r.code and ur.user_name=?)"
                + " )  order by seq";
        modules = Db.find(sql, office_id, username);
        for (Record module : modules) {
            sql = "select sub.id, sub.module_name, p.url, rp.permission_code, rp.is_authorize, rp.role_code from eeda_modules sub "
                    + "      left join module_role mr on mr.module_id = sub.id "
                    + "      left join role r on mr.role_id=r.id "
                    + "     left join role_permission rp on rp.module_role_id=mr.id "
                    + "     left join permission p on p.id = rp.permission_id"
                    + "  where sub.parent_id =? and sub.office_id=? "
                    + "     and rp.permission_code like '%list' and rp.is_authorize=1 "
                    + "     and rp.role_id in ( "
                    + "       select r.id from role r, user_role ur  "
                    + "     where ur.role_code=r.code and ur.user_name=?) "
                    + "  group by id order by sub.seq";

            List<Record> orders = Db.find(sql, module.get("id"), office_id, username);
            module.set("orders", orders);
        }

        if (modules == null){
            modules = Collections.EMPTY_LIST;
        }else{
            menuCache.put(user_id, modules);
        }
        return modules;
    }
}
