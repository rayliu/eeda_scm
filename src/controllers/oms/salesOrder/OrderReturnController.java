
package controllers.oms.salesOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.eeda.oms.SalesOrder;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;

import controllers.api.ApiController;

public class OrderReturnController extends Controller {
	private Logger logger = Logger.getLogger(OrderReturnController.class);
    
	public void orderResultRecv(){
		System.out.println("begin return------------");
		String resultMsg = getPara("cebJsonMsg");//www-form-urlencoded
		
		if (resultMsg == null) {//raw 提交
			resultMsg = ApiController.getRequestPayload(getRequest());
        }
		
		System.out.println("returnValue:"+resultMsg);
	    Db.update("update customize_field set field_code = ? where order_type = 'returnMassge'",resultMsg);
	    
	    if(StringUtils.isNotEmpty(resultMsg)){
	    	Gson gson = new Gson(); 
	    	Map<String, ?> dto= gson.fromJson(resultMsg, HashMap.class);  
	    	String code = (String)dto.get("code");
	    	String message = (String)dto.get("message");
	    	if("00".equals(code)){
	    		List<Map<String, String>> orders = (ArrayList<Map<String, String>>)dto.get("orders");
	    		
	    		String errorMsg = (String)orders.get(0).get("message");
	    		String order_no = (String)orders.get(0).get("order_no");
	    		
	    		if(StringUtils.isNotEmpty(order_no)){
	    			Db.update("update sales_order set submit_status = ?,error_msg = ? where order_no = ?",message,errorMsg,order_no);
	    		}
	    	}
	    }
		
	    renderText("success");
	}
}
