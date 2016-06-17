package controllers.oms.custom.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;

import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.DingDanGoodsDto;
import controllers.oms.custom.dto.QueryBillDto;
import controllers.oms.custom.dto.QueryDingDanDto;
import controllers.oms.custom.dto.YunDanDto;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;

//import com.phy.ceb.api.dto.LogisticsApiDto;
//import com.phy.ceb.api.dto.OrderApiDto;
//import com.phy.ceb.api.dto.OrderGoodsApiDto;
//import com.phy.ceb.business.wdt.in.InUtil;
//import com.phy.ceb.utils.XmlTransferUtil;
//import com.phy.core.common.util.MD5Util;

public class Test {
    private static String orgCode="349779838";
	public static String setOrderMsg(String orderNo) {
		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		paramsMap.put("orgcode", orgCode);
		paramsMap.put("appkey", "defeng");
		String appsecret = MD5Util.encodeByMD5("888888");
		paramsMap.put("appsecret", appsecret);
		String timestamp = "" + (System.currentTimeMillis() / 1000);
		paramsMap.put("timestamp", timestamp);
		
		String sign = MD5Util.encodeByMD5(paramsMap+appsecret);// 888888
		
		System.out.println("参数:"+ paramsMap+appsecret);
		paramsMap.put("sign", sign);

		Map<Object, Object> requestMap = new LinkedHashMap<Object, Object>();
		
		Gson gson = new Gson(); 
		requestMap.put("postHead", gson.toJson(paramsMap));

		//order业务数据
		DingDanDto order = new DingDanDto();
		order.setOrg_code(orgCode);
		order.setOrder_no(orderNo);
		order.setPay_no("defengpay001");

		order.setGoods_value(120.3);
		order.setFreight(20.39);
		order.setCurrency("142");// 默认人民币
		order.setConsignee("huangx");
		order.setConsignee_address("深圳市福田区竹子林交委大楼");
		order.setConsignee_telephone("15696163997");
		order.setConsignee_country("142");
		order.setPro_amount(1.23);
		order.setPro_remark("SALE");
		order.setConsignee_type("1");
		order.setConsignee_id("35012819911215493X");
		order.setProvince("440000");
		order.setCity("440300");
		order.setDistrict("440000");
		order.setNote("440304");
		order.setPayer_account("Payer_account_huangx");
		order.setPayer_name("Payer_name_huangx");
		order.setOrder_time("2016-05-13 13:49:50");
		
		order.setEbp_code_cus("4403660001");
		order.setEbp_code_ciq("4718000001");
		order.setEbp_name("德丰");
		
		order.setEbc_code_cus("4403660065");//企业在海关的备案号码
        order.setEbc_code_ciq("4700651300");//企业在国检的备案号码
        order.setEbc_name("深圳前海德丰投资发展有限公司");

		order.setAgent_code_cus("4403660065");
		order.setAgent_code_ciq("4700651300");
		order.setAgent_name("深圳前海德丰投资发展有限公司");
		
		DingDanGoodsDto goods=new DingDanGoodsDto();
		goods.setCurrency("142");
		goods.setItem_no("ISQHDF9312146008460");
		goods.setCus_item_no("aaa1");
		goods.setGift_flag("0");
		goods.setPrice(12.7);
		goods.setQty(1);
		goods.setTotal(12.7);
		goods.setUnit("001");
		List<DingDanGoodsDto> goodsList=new ArrayList<DingDanGoodsDto>();
		goodsList.add(goods);
		order.setGoodsList(goodsList);
		
		DingDanDto order1 = new DingDanDto();
		order1.setOrder_no("bbbbbbbbb");
		
		List<DingDanDto> orderList=new ArrayList<DingDanDto>();
		orderList.add(order);
//		orderList.add(order1);

		requestMap.put("total_count", orderList.size());
		requestMap.put("orders", orderList);
		
		Gson gson1 = new Gson(); 
		String jsonMsg = gson1.toJson(requestMap);
		System.out.println("参数:"+ jsonMsg);
		return jsonMsg;
	}
	
	//YunDan
	public static String setLogMsg(String orderNo, String logOrderNo) {
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

		//YunDan  order 业务数据
		YunDanDto log = new YunDanDto();
		log.setOrg_code(orgCode);
		log.setOrder_no(orderNo);
		
		
		log.setReport_pay_no("serviceRPN001");

		log.setWeight(20.39);
		log.setCurrency("142");// 默认人民币
		log.setConsignee("huangx");
		log.setConsignee_address("深圳市福田区竹子林交委大楼");
		log.setConsignee_telephone("15696163997");
		log.setConsignee_country("142");
		log.setConsignee_type("1");
		log.setConsignee_id("35012819911215493X");
		log.setProvince("440000");
		log.setCity("440300");
		log.setDistrict("440000");
		
		log.setLog_no(logOrderNo);
		log.setCountry_code("142");
		log.setShipper("aibi");
		log.setShipper_country("215");
		log.setShipper_city("360400");
		log.setShipper_telephone("12340515001");
		log.setShipper_address("shipppppppppppppppppper");
		log.setTraf_mode("1");
		log.setShip_name("aaaaaaaaaaaaaaaaaaaaaaa");
		log.setPack_no(1111111);
		log.setGoods_info("aaaaaaaaaaaaaaaaaaa");
		log.setCustoms_code("5351");
		log.setCiq_code("471800");
		
		
		log.setParcel_info("aaaaaa");
		log.setIe_date("2016-05-15 09:13:02");
		
		
		YunDanDto log1 = new YunDanDto();
		log1.setOrder_no("bbbbbbbbb");
		
		List<YunDanDto> orderList=new ArrayList<YunDanDto>();
		orderList.add(log);
//		orderList.add(log1);

		requestMap.put("total_count", orderList.size());
		requestMap.put("logistics", orderList);
		
		Gson gson1 = new Gson();
		String jsonMsg = gson1.toJson(requestMap);
		
		System.out.println("参数:"+ jsonMsg);
		return jsonMsg;
	}
	
	//YunDan
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
    
	private static void queryOrder(String orderNo){ 
	    String jsonMsg=setQueryMsg(orderNo);

        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
        String urlStr="http://test.szedi.cn:8088/phy-ceb-web/tgt/service/order_queryCustomsStatus.action";
        paramsMap.put("jsonMsg", jsonMsg);
        String PostData = "";
        PostData = paramsMap.toString().substring(1);
        System.out.println("参数"+PostData);
        String returnMsg = EedaHttpKit.post(urlStr, PostData);
        //String returnMsg = InUtil.getResult(urlStr, PostData);
        System.out.println("结果"+returnMsg);
	}

	public static void main(String[] args) {
//		createOrder("df_order_002");
		createLogOrder("df_order_002", "df_log_order_002");
		
		queryOrder("df_order_002");
	}

	private static void createLogOrder(String orderNo, String logOrderNo) {
        String jsonMsg=setLogMsg(orderNo, logOrderNo);

        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
        String urlStr="http://test.szedi.cn:8088/phy-ceb-web/tgt/service/logistics_create.action";
        paramsMap.put("jsonMsg", jsonMsg);
        String PostData = "";
        PostData = paramsMap.toString().substring(1);
        System.out.println("参数"+PostData);
        String returnMsg = EedaHttpKit.post(urlStr, PostData);
        //String returnMsg = InUtil.getResult(urlStr, PostData);
        System.out.println("结果"+returnMsg);
    }
	
    private static void createOrder(String orderNo) {
        String jsonMsg=setOrderMsg(orderNo);

		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		String urlStr="http://test.szedi.cn:8088/phy-ceb-web/tgt/service/order_create.action";
		paramsMap.put("jsonMsg", jsonMsg);
		String PostData = "";
		PostData = paramsMap.toString().substring(1);
		System.out.println("参数"+PostData);
		String returnMsg = EedaHttpKit.post(urlStr, PostData);
		//String returnMsg = InUtil.getResult(urlStr, PostData);
		System.out.println("结果"+returnMsg);
    }
}
