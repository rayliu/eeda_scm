package controllers.oms.salesOrder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ReturnStatus;
import models.eeda.oms.SalesOrder;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

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
	
	
	public void storageInResultRecv() throws UnsupportedEncodingException{
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
	    	String cop_no = (String)dto.get("cop_no");
	    	
	    	if("00".equals(code)){
	    		if(StringUtils.isNotEmpty(cop_no)){
	    			Db.update("update storage_in_order set status='已上报',error_msg = ? where cop_no = ?",message,cop_no);
	    		}
	    	}else{
	    		if(StringUtils.isNotEmpty(cop_no)){
	    			Db.update("update storage_in_order set error_msg = ? where cop_no = ?",message,cop_no);
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
	    //Db.update("update customize_field set field_code = ? where order_type = 'returnStatusMassge'",resultMsg);
	    
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
	    	
	    	System.out.println("return logistic_no："+logistics_no);
	    	SalesOrder salesRe = null;
	    	if(StringUtils.isNotEmpty(logistics_no)){
	    		salesRe = SalesOrder.dao.findFirst("select * from sales_order where logistics_no = ?",logistics_no);
	    	}else if(StringUtils.isNotEmpty(cop_no)){
	    		salesRe = SalesOrder.dao.findFirst("select * from sales_order where cop_no = ?",cop_no);
	    	}
	    	
	    	if(salesRe != null){
	    		long order_id = salesRe.getLong("id");
	    		
	    		//Db.update("update sales_order set ceb_report = ?,return_status = ?,return_info = ?,return_time = ? where id = ?",ceb_report,return_status,return_info,return_time,order_id);
	    		System.out.println("update status successful："+order_id);
	    		
		    	ReturnStatus returnRe = ReturnStatus.dao.findFirst("select * from return_status where ceb_report = ? and order_id = ?",ceb_report,order_id);

		    	if(returnRe == null){
		    		ReturnStatus rs = new ReturnStatus();
		    		rs.set("order_id", order_id);
		    		rs.set("ceb_report", ceb_report);
		    		rs.set("org_code",org_code );
		    		rs.set("logistics_no",logistics_no );
		    		rs.set("cop_no", cop_no);
		    		rs.set("bill_no", bill_no);
		    		rs.set("return_status", return_status);
		    		rs.set("return_info", return_info);
		    		rs.set("return_time", return_time);
		    		rs.set("create_stamp", new Date());
		    		rs.save();
		    	}else{
		    		returnRe.set("return_status",return_status);
		    		returnRe.set("return_info", return_info);
		    		returnRe.set("return_time", return_time);
		    		returnRe.set("update_stamp", new Date());
		    		returnRe.update();
		    	}
		    	
		    	//更新订单状态
		    	List<Record> res  = Db.find("select * from return_status where order_id = ?",order_id);
	    		
	    		if(res.size() == 0){
	    			salesRe.set("ceb_report", ceb_report);
    	    		salesRe.set("return_status", return_status);
    	    		salesRe.set("return_info", return_info);
    	    		salesRe.set("return_time", return_time);
    	    		salesRe.update();
    	    		System.out.println("first update salesorder status successful!!!");
	    		}else{
	    			for(Record re : res){
		    			String  oldReturn_time= re.getStr("return_time");
		    			if(Double.parseDouble(return_time)>Double.parseDouble(oldReturn_time)){
		    				salesRe.set("ceb_report", ceb_report);
		    	    		salesRe.set("return_status", return_status);
		    	    		salesRe.set("return_info", return_info);
		    	    		salesRe.set("return_time", return_time);
		    	    		salesRe.update();
		    	    		System.out.println("update salesorder status successful!!!");
		    			}
		    		}
	    		}
	    		
	    		if("[Code:2600;Desc:放行]".equals(return_info) && "34".equals(return_status)){
		    		salesRe.set("is_order_pass", "1");
		    		salesRe.update();
		    	}
	    	}
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
