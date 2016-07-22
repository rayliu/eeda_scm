package controllers.yh.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.eeda.OrderActionLog;
import models.eeda.oms.InspectionOrderItem;
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
	    
	    List<Record> record = Db.find("select * from sales_order where ifnull(status,'') != '订单写入成功'");
	    for(Record re : record){
	    	String order_no = re.getStr("order_no");
	    	long order_id = re.getLong("id");
	    	String jsonMsg = OrderStatusController.queryOrder(order_no);
	    	
	    	Gson gson = new Gson();  
	        Map<String, ?> dto= gson.fromJson(jsonMsg, HashMap.class);  
	        
	        List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("bills");
	        String message = itemList.get(0).get("message");
	        
	        
	        System.out.println(message);
	        
	        //保存查询log
	        operationLog("customStatus",jsonMsg,order_id,"get");
	        
	        //更新销售订单状态
	        SalesOrder so = SalesOrder.dao.findById(order_id);
	        if(!"订单不存在！".equals(message)){
	        	so.set("status", message).update();
	        } 
	    }
	    
	    logger.debug("更新单据状态结束");
	}
	
	
	@Before(Tx.class)
    public void operationLog(String order_type,String json,long order_id,String action){
    	OrderActionLog orderActionLog = new OrderActionLog();
    	orderActionLog.set("json", json);
    	orderActionLog.set("time_stamp", new Date());
    	orderActionLog.set("order_type", order_type);
    	orderActionLog.set("order_id", order_id);
    	orderActionLog.set("action", action);
    	orderActionLog.save();
    }

	

}
