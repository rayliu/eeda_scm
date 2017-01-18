package controllers.oms.custom.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.jfinal.plugin.activerecord.Record;

import controllers.api.service.SalesOrderService;
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
    
    public static void main(String[] args) throws UnsupportedEncodingException {
		//createOrder("df_order_002");
		//createLogOrder("df_order_002", "df_log_order_002");
		
		//queryOrder("df_order_002");Map<String, String> queryParas
		//Map<String, String> map=new HashMap<String, String>();
		//map.put("cebJsonMsg", "fdfdfdf");
		//String returnMsg = EedaHttpKit.post("http://yd2demo.eeda123.com/orderReturn/orderResultRecv", map,"fdfdfdf",map);
//    	SalesOrderService s = new SalesOrderService(this);
//    			//s.saveSo();
//    	s.querySo();
		
//		String aa = "returnValue:cebJsonMsg%3D%7B%22code%22%3A%2200%22%2C%22message%22%3A%22%E8%AE%A2%E5%8D%95%E5%86%99%E5%85%A5%E6%8E%A5%E5%8F%A3%E8%B0%83%E7%94%A8%E6%88%90%E5%8A%9F%21%22%2C%22orders%22%3A%5B%7B%22message%22%3A%22%E4%BB%93%E5%82%A8%E4%BC%81%E4%B8%9A%E4%B8%8D%E5%AD%98%E5%9C%A8%EF%BC%8C%E4%BB%93%E5%82%A8%E4%BC%81%E4%B8%9A%E7%9A%84%E7%BB%84%E7%BB%87%E6%9C%BA%E6%9E%84%E4%BB%A3%E7%A0%81%E9%94%99%E8%AF%AF%EF%BC%81%22%2C%22logistics_no%22%3A%22IYQHDF2016122200004%22%2C%22wh_org_code%22%3A%22440300349779838%22%2C%22org_code%22%3A%22349779838%22%2C%22code%22%3A%2201%22%2C%22order_no%22%3A%22IDQHDF2016122200004%22%7D%5D%7D";
//		aa = URLDecoder.decode(aa,"UTF-8");
//		System.out.println(aa);
    
    
    }
    
    
    private static void createOrder(String orderNo) {
        String jsonMsg=setOrderMsg(orderNo);

		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		//String urlStr="http://test.szedi.cn:8088/phy-ceb-web/tgt/service/order_create.action";
		String urlStr="http://test.szedi.cn:7088/ceb/tgt/service/order_createBc.action";
		paramsMap.put("jsonMsg", jsonMsg);
		String PostData = "";
		PostData = paramsMap.toString().substring(1);
		System.out.println("参数"+PostData);
		String returnMsg = EedaHttpKit.post(urlStr, PostData);
		//String returnMsg = InUtil.getResult(urlStr, PostData);
		System.out.println("结果"+returnMsg);
    }
    
	public static String setOrderMsg(String orderNo) {
		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		paramsMap.put("orgcode", orgCode);
		paramsMap.put("appkey", "QHDF");
		String appsecret = MD5Util.encodeByMD5("888888");
		paramsMap.put("appsecret", appsecret);
		String timestamp = "" + (System.currentTimeMillis() / 1000);
		paramsMap.put("timestamp", timestamp);
		
		String sign = MD5Util.encodeByMD5(paramsMap+appsecret);// 888888
		
		System.out.println("参数:"+ paramsMap+appsecret);
		paramsMap.put("sign", sign);

		Map<Object, Object> requestMap = new LinkedHashMap<Object, Object>();
		
		Gson gson = new Gson(); 
		requestMap.put("postHead", gson.toJson(paramsMap)) ;  //---------------toString();???

		//order业务数据
		DingDanDto order = new DingDanDto();
		order.setOrg_code(orgCode);
		order.setOrder_no(orderNo);
		

		order.setEbp_code_cus("4403660001");
		order.setEbp_code_ciq("4718000001");
		order.setEbp_name("德丰");
		
		order.setEbc_code_cus("4403660065");//企业在海关的备案号码
        order.setEbc_code_ciq("4700651300");//企业在国检的备案号码
        order.setEbc_name("深圳前海德丰投资发展有限公司");

		order.setGoods_value(120.3);
		order.setFreight(20.39);    
		order.setDiscount(0.0);     //+++++++++++++//非现金抵扣金额，无则为0
		order.setTax_total(0.0);      //+++++++++++++代扣税款，无则为0
		order.setActural_paid(100.0);	//++++++++++++实际支付 金额，无则为0
		
		//order.setCurrency("142");// 默认人民币
		order.setConsignee("huangx");
		order.setConsignee_address("深圳市福田区竹子林交委大楼");
		order.setConsignee_telephone("15696163997");
		order.setDistrict("440000");
		order.setBuyer_regno("1234565");  //订购人注册号
		order.setBuyer_name("kevin");     //订购人姓名
		order.setBuyer_id_number("0");    //
		order.setIs_order_pass("0");      //是否已完成订单申报  
		order.setIs_pay_pass("0");        //是否已完成支付申报（1是，0否，默认0）
		order.setPay_transaction_id("");  //支付交易编号(支付申报编号)  （可空）
		
		order.setPay_no("123456789");
		order.setPay_time("2016-05-13 13:49:50");  //支付时间
		order.setPay_type("PTL");   //支付渠道        
		order.setPay_channel("");  //支付渠道(可空)
		order.setShop_no("");  //店铺代码（可空）
		order.setNote("备注");//（可空）
		order.setBatch_numbers("");//商品批次号(可空)
		order.setWh_org_code("56566");//企业组织结构代码（仓储企业）
		
		//运单
		order.setLogistics_no("YD20161216001");   //运单号
		order.setGoods_info("测试物品");  //主要货物信息
		order.setInsure_fee(0);// 保价费 默认为0
		order.setWeight(0.0);  //毛重
		order.setNet_weight(0.0);//净重
		order.setPack_no(1);  //包裹数
		order.setCop_no("123");//企业内部标识单证的编号
		order.setAssure_code("123");//担保企业编号
		order.setSign_company("123");//承运企业海关备案号
		order.setSign_company_name("承运企业名称"); //承运企业名称
		order.setEms_no("I440366006516001");//电商账册编号（空）
		order.setDecl_time("2016-12-16"); // 申报日期（yyy-mm-dd）
		order.setCustoms_code("5349");//主管海关代码
		order.setCiq_code("471800");//主管检验疫机构代码
		order.setPort_code("5349");//口岸海关代码
		order.setIe_date("2016-12-15");//进口日期（yyy-mm-dd）（空）
		order.setTrade_mode("1");//贸易方式
		order.setBusiness_mode("1234");//业务模式代码
		order.setTraf_mode("4");//运输方式
		order.setTraf_no(""); //运输工具编号（可空）
		order.setShip_name("汽车");//运输工具名称
		order.setVoyage_no("");//航班航次号（可空）
		order.setBill_no("");  //提运单号（可空）
		order.setSupervision_code("");//（可空）监管场所代码
		order.setCountry_code("142");//起运国
		order.setWrap_type("CT");//包装种类
		
		
//		order.setConsignee_country("142");
//		order.setPro_amount(1.23);
//		order.setPro_remark("SALE");
//		order.setConsignee_type("1");
//		order.setConsignee_id("35012819911215493X");
//		order.setProvince("440000");
//		order.setCity("440300");
//		
//		
//		order.setPayer_account("Payer_account_huangx");
//		order.setPayer_name("Payer_name_huangx");
//		order.setOrder_time("2016-05-13 13:49:50");
		
		
		DingDanGoodsDto goods=new DingDanGoodsDto();
		goods.setCurrency("142");
		goods.setItem_no("ISQHDF9312146008460");
		//goods.setCus_item_no("aaa1");
		//goods.setGift_flag("0");
		goods.setQty(1);
		goods.setQty1(1);
		goods.setUnit("001");
		goods.setUnit1("001");
		goods.setPrice(12.7);
		goods.setTotal_price(12.7);
		goods.setCountry("142");
		goods.setGcode("12345678910");
		goods.setG_model("gggjgjgjgjgg");
		goods.setCiq_gno("");//(可空)
		goods.setCiq_gmodel("ghgh");
		goods.setBrand("无");
		goods.setNote("");//可空
		
		
		
		List<DingDanGoodsDto> goodsList=new ArrayList<DingDanGoodsDto>();
		goodsList.add(goods);
		order.setGoodslist(goodsList);
		
//		DingDanDto order1 = new DingDanDto();
//		order1.setOrder_no("bbbbbbbbb");
		
		List<DingDanDto> orderList=new ArrayList<DingDanDto>();
		orderList.add(order);
//		orderList.add(order1);

		requestMap.put("total_count", orderList.size());
		requestMap.put("notify_url", "444");//回调地址
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
	

    
}
