
package controllers.oms.salesOrder;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;

public class OrderReturn extends Controller {
    
	private Logger logger = Logger.getLogger(OrderReturn.class);
    
	public void orderResultRecv(){
		String resultMsg = getPara("cebJsonMsg");//www-form-urlencoded
		System.out.println(resultMsg);
	    Db.update("update customize_field set field_code = ? where order_type = 'returnMassge'",resultMsg);
		
//	    Map map=new HashMap();
//	    map.put("status", "success");
//	    renderJson(map);
	    renderText("success");
	}
}
