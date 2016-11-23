package controllers.yh.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import models.eeda.OrderActionLog;
import models.eeda.oms.InspectionOrderItem;
import models.eeda.oms.LogisticsOrder;
import models.eeda.oms.SalesOrder;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import controllers.oms.orderStatus.OrderStatusController;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;

public class CustomJob implements Runnable {
	private Logger logger = Logger.getLogger(CustomJob.class);

	@Override
	@Before(Tx.class)
	public void run() {
	    logger.debug("更新单据状态开始.....");
	    
	    List<Record> record = Db.find("select * from sales_order where "
	    		+ " (ifnull(order_cus_status,'') != '接收成功' "
	    		+ " or ifnull(order_ciq_status,'') != '接收成功'"
	    		+ " or ifnull(bill_cus_status,'') != '审批通过'"
	    		+ " or ifnull(pay_status,'') != '接收成功')"
	    		+ " and status != '未上报' order by id desc limit 200");
	    for(Record re : record){
	    	String order_no = re.getStr("order_no");
	    	String order_id = re.getLong("id").toString();
	    	String jsonMsg = OrderStatusController.queryOrder(order_no);
	    	
	    	Gson gson = new Gson();  
	        Map<String, ?> dto= gson.fromJson(jsonMsg, HashMap.class);  
	        
	        List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("bills");
	        String message = itemList.get(0).get("message");
	        String order_cus_status = itemList.get(0).get("order_cus_status");
	        String order_ciq_status = itemList.get(0).get("order_ciq_status");
	        String pay_status = itemList.get(0).get("pay_status");
	        String logistics_ciq_status = itemList.get(0).get("logistics_ciq_status");
	        String logistics_cus_status = itemList.get(0).get("logistics_cus_status");
	        String bill_cus_status = itemList.get(0).get("bill_cus_status");
	        String bill_cus_result = itemList.get(0).get("bill_cus_result");
	        
	        System.out.println(message);
	        
	        //保存查询log
	        operationLog("customStatus",jsonMsg,order_id,"get",null);
	        
	        
	        if(!"订单不存在！".equals(message)){
	        	//更新销售订单状态 
	        	SalesOrder so = SalesOrder.dao.findById(order_id);
	        	so.set("order_cus_status", statusShow(order_cus_status,"order_cus_status"));
	        	so.set("order_ciq_status", statusShow(order_ciq_status,"order_ciq_status"));
	        	so.set("pay_status",statusShow(pay_status,"pay_status"));
	        	so.set("bill_cus_status",statusShow(bill_cus_status,"bill_cus_status"));
	        	so.set("bill_cus_result",bill_cus_result);
	        	
	        	if("30".equals(order_cus_status) && "30".equals(order_ciq_status)){
	        		so.set("status", "已通关");
	        	}else{
	        		so.set("status", "报关处理中");
	        	}
	        	so.update();
	        	
	        	//更新运单状态
		        LogisticsOrder lo = LogisticsOrder.dao.findFirst("select * from logistics_order where sales_order_id = ?",order_id);
		        lo.set("logistics_ciq_status", statusShow(logistics_ciq_status,"logistics_ciq_status"));
	        	lo.set("logistics_cus_status", statusShow(logistics_cus_status,"logistics_cus_status"));
	        	
	        	if("30".equals(logistics_ciq_status) && "30".equals(logistics_cus_status)){
	        		lo.set("status","已通关");
	        	}else if("".equals(logistics_ciq_status) && "".equals(logistics_cus_status)){
	        		lo.set("status","未上报");
	        	} else{
	        		lo.set("status","报关处理中");
	        	}
	        	lo.update();
	        } 
	    }
	    
	    logger.debug("更新单据状态结束");
	}
	
	public static String statusShow(String status,String id){ 
    	String massage = "";
    	if("10".equals(status)){
    		massage = "未发送";
    	}else if("20".equals(status)){
    		massage ="已发送";
    	}else if("30".equals(status)){
    		massage = "接收成功";
    	}else if("31".equals(status)){
    		massage = "审批通过";
    	}else if("32".equals(status)){
    		if("bill_cus_status".equals(id) || "bill_ciq_status".equals(id)){
    			massage = "审批不通过";
    		}else{
    			massage = "接收不成功";
    		}
    	}
    	return massage;
    }
	
	
	@Before(Tx.class)
    public static void operationLog(String order_type,String json,String order_id,String action,String user_id){
    	OrderActionLog orderActionLog = new OrderActionLog();
    	orderActionLog.set("json", json);
    	orderActionLog.set("time_stamp", new Date());
    	orderActionLog.set("order_type", order_type);
    	orderActionLog.set("order_id", order_id);
    	orderActionLog.set("action", action);
    	if(StringUtils.isNotEmpty(user_id))
    		orderActionLog.set("operator", user_id);
    	orderActionLog.save();
    }

	

}
