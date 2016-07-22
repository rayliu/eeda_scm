package controllers.oms.orderStatus;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import controllers.oms.custom.dto.QueryBillDto;
import controllers.oms.custom.dto.QueryDingDanDto;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class OrderStatusController extends Controller {

	private Logger logger = Logger.getLogger(OrderStatusController.class);
	Subject currentUser = SecurityUtils.getSubject();
	private static String orgCode="349779838";

	public void index() {
		render("/oms/orderStatus/orderStatusList.html");
	} 
	
    public void list() {
    	String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        String sql = "SELECT lor.*,ccy.shop_name custom_name, ifnull(u.c_name, u.user_name) creator_name "
    			+ "  from logistics_order lor "
    			+ "  left join sales_order sor on sor.id = lor.sales_order_id "
    			+ "  left join custom_company ccy on ccy.id = sor.custom_id "
    			+ "  left join user_login u on u.id = lor.create_by"
    			+ "   where 1 =1 ";
        
        String condition = DbUtils.buildConditions(getParaMap());

        String sqlTotal = "select count(1) total from ("+sql+ condition+") B";
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));
        
        List<Record> BillingOrders = Db.find(sql+ condition + " order by create_stamp desc " +sLimit);
        Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", pageIndex);
        BillingOrderListMap.put("iTotalRecords", rec.getLong("total"));
        BillingOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        BillingOrderListMap.put("aaData", BillingOrders);

        renderJson(BillingOrderListMap); 
    }
    
    public void search(){
    	String sales_order_no = getPara("sales_order_no"); 
    	String massage = queryOrder(sales_order_no);
    	

//    	
//    	Record re = new Record();
//    	re.set("massage", massage);
    	renderJson(massage);
    }
    
    
    public static String queryOrder(String orderNo){ 
	    String jsonMsg = setQueryMsg(orderNo);

        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
        String urlStr="http://test.szedi.cn:8088/phy-ceb-web/tgt/service/order_queryCustomsStatus.action";
        paramsMap.put("jsonMsg", jsonMsg);
        String PostData = "";
        PostData = paramsMap.toString().substring(1);
        System.out.println("参数"+PostData);
        String returnMsg = EedaHttpKit.post(urlStr, PostData);
        //String returnMsg = InUtil.getResult(urlStr, PostData);
        System.out.println("结果"+returnMsg);
        return returnMsg;
	}
    
    public static String setQueryMsg(String orderNo) {
        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
        paramsMap.put("orgcode", orgCode);
        paramsMap.put("appkey", "defeng");
        String appsecret = MD5Util.encodeByMD5("888888");
        paramsMap.put("appsecret", appsecret);
        String timestamp = "" + (System.currentTimeMillis() / 1000);
        paramsMap.put("timestamp", timestamp);

        String sign = MD5Util.encodeByMD5(paramsMap + appsecret);// 888888

        System.out.println("参数:" + paramsMap + appsecret);
        paramsMap.put("sign", sign);

        Map<Object, Object> requestMap = new LinkedHashMap<Object, Object>();

        Gson gson = new Gson();
        requestMap.put("postHead", gson.toJson(paramsMap));

        //QueryDto
        QueryDingDanDto dto = new QueryDingDanDto();
        dto.setTotalCount(1);
        
        QueryBillDto bill = new QueryBillDto();
        bill.setOrder_no(orderNo);
        bill.setLogistics_no("");//df_log_order_001
        List bList= new ArrayList<QueryBillDto>();
        bList.add(bill);
        
        dto.setBills(bList);
        
        requestMap.put("total_count", bList.size());
        requestMap.put("bills", bList);
        
        Gson gson1 = new Gson();
        String jsonMsg = gson1.toJson(requestMap);
        
        System.out.println("参数:"+ jsonMsg);
        return jsonMsg;
    }


}
