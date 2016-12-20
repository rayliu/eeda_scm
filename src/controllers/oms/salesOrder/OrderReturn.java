
package controllers.oms.salesOrder;
import java.util.Date;

import models.eeda.oms.SalesOrder;

import com.allinpay.ets.client.SecurityUtil;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;

import config.EedaConfig;
import controllers.oms.custom.dto.SendReturnDto;

public class OrderReturn extends Controller {
    
	private Logger logger = Logger.getLogger(OrderReturn.class);
    
	public void orderResultRecv(){
		String resultMsg = getPara("cebJsonMsg");
		System.out.println(resultMsg);
	    Db.update("update customize_field set field_code = ? where order_type = 'returnMassge'",resultMsg);
		renderJson();
	}
}
