
package controllers.oms.salesOrder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;

import controllers.api.ApiController;

public class OrderReturnController extends Controller {
	private Logger logger = Logger.getLogger(OrderReturnController.class);
	
    
	public void orderResultRecv() throws UnsupportedEncodingException{
		String resultMsg = getPara("cebJsonMsg");//www-form-urlencoded/
		if (resultMsg == null) {//raw 提交
			resultMsg = ApiController.getRequestPayload(getRequest());
			resultMsg = URLDecoder.decode(resultMsg,"UTF-8");
			resultMsg= resultMsg.substring(11, resultMsg.length());
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
	    		String messageCode = (String)orders.get(0).get("code");
	    		
	    		if(StringUtils.isNotEmpty(order_no)){
	    			if("00".equals(messageCode)){
	    				Db.update("update sales_order set status='已上报', submit_status = ?,error_msg = ? where order_no = ?",message,errorMsg,order_no);
	    			}else{
	    				Db.update("update sales_order set submit_status = ?,error_msg = ? where order_no = ?",message,errorMsg,order_no);
	    			}
	    		}
	    	}
	    }
	    renderText("success");
	}
	
	public void orderStatusRecv() throws UnsupportedEncodingException{
		String resultMsg = getPara("cebJsonMsg");//www-form-urlencoded/
		if (resultMsg == null) {//raw 提交
			resultMsg = ApiController.getRequestPayload(getRequest());
			resultMsg = URLDecoder.decode(resultMsg,"UTF-8");
			resultMsg= resultMsg.substring(11, resultMsg.length());
        }
		
		System.out.println("returnStatusValue:"+resultMsg);
	    Db.update("update customize_field set field_code = ? where order_type = 'returnStatusMassge'",resultMsg);
	    
	    if(StringUtils.isNotEmpty(resultMsg)){
	    	Gson gson = new Gson(); 
	    	Map<String, ?> dto= gson.fromJson(resultMsg, HashMap.class);  
	    	String ceb_report = (String)dto.get("ceb_report");  //类型
	    	String org_code = (String)dto.get("org_code");    //企业组织机构代码
	    	String logistics_no = (String)dto.get("logistics_no");  //运单编号
	    	String cop_no = (String)dto.get("cop_no");     //企业每部表示单证编号
	    	String bill_no = (String)dto.get("bill_no");    //单证标号
	    	String return_status = (String)dto.get("return_status");   //回执状态
	    	String return_info = (String)dto.get("return_info");    //状态 说明
	    	String return_time = (String)dto.get("return_time");   //操纵时间
	    	
	    	System.out.println("回调状态的运输单号："+logistics_no);
	    	
	    }
	    renderText("success");
	}
	
//	private String changeHGStatus(String return_status){
//		String status = null;
//		if("23".equals(return_status)){
//			status = "发送海关成功";
//		}else if("23".equals(return_status)){
//			status = "";
//		}else if("23".equals(return_status)){
//			status = "";
//		}else if("23".equals(return_status)){
//			status = "";
//		}else if("23".equals(return_status)){
//			status = "";
//		}else if("23".equals(return_status)){
//			status = "";
//		}else if("23".equals(return_status)){
//			status = "";
//		}
//		
//		return null;
//	}
	
}
