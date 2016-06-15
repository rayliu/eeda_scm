package controllers.oms.salesOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.UserLogin;
import models.eeda.oms.SalesOrderCount;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.oms.SalesOrder;
import models.eeda.profile.CustomCompany;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.DingDanGoodsDto;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class SalesOrderController extends Controller {

	private Logger logger = Logger.getLogger(SalesOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/SalesOrder/SalesOrderList.html");
	}
	
    public void create() {
        render("/oms/SalesOrder/SalesOrderEdit.html");
    }
    
    @Before(Tx.class)
   	public void save() throws Exception {		
   		String jsonStr=getPara("params");
       	
       	Gson gson = new Gson();  
        Map<String, ?> dto= gson.fromJson(jsonStr, HashMap.class);  
            
        SalesOrder salesOrder = new SalesOrder();
   		String id = (String) dto.get("id");
   		
   		UserLogin user = LoginUserController.getLoginUser(this);
   		
   		if (StringUtils.isNotEmpty(id)) {
   			//update
   			salesOrder = SalesOrder.dao.findById(id);
   			DbUtils.setModelValues(dto, salesOrder);
   			
   			//需后台处理的字段
   			salesOrder.set("update_by", user.getLong("id"));
   			salesOrder.set("update_stamp", new Date());
   			salesOrder.update();
   		} else {
   			//create 
   			DbUtils.setModelValues(dto, salesOrder);
   			
   			//需后台处理的字段
   			salesOrder.set("order_no", OrderNoGenerator.getNextOrderNo("DD"));
   			salesOrder.set("create_by", user.getLong("id"));
   			salesOrder.set("create_stamp", new Date());
   			salesOrder.save();
   			
   			id = salesOrder.getLong("id").toString();
   		}
   		
   		List<Map<String, String>> itemList = (ArrayList<Map<String, String>>)dto.get("cargo_list");
		DbUtils.handleList(itemList, id, SalesOrderGoods.class, "order_id");
		
		List<Map<String, String>> countList = (ArrayList<Map<String, String>>)dto.get("count_list");
		DbUtils.handleList(countList, id, SalesOrderCount.class, "order_id");

   		//return dto
   		renderJson(salesOrder);
   	}
    
    
    private List<Record> getSalesOrderGoods(String orderId) {
		String itemSql = "select * from sales_order_goods where order_id=?";
		List<Record> itemList = Db.find(itemSql, orderId);
		return itemList;
	}
    
    private List<Record> getSalesOrderCount(String orderId) {
		String countSql = "select * from sales_order_count where order_id=?";
		List<Record> countList = Db.find(countSql, orderId);
		return countList;
	}
    
    
    @Before(Tx.class)
    public void edit() {
    	String id = getPara("id");
    	SalesOrder salesOrder = SalesOrder.dao.findById(id);
    	setAttr("order", salesOrder);
    	
    	//获取明细表信息
    	setAttr("itemList", getSalesOrderGoods(id));
    	
    	//获取费用明细表信息
    	setAttr("countList", getSalesOrderCount(id));
    	
    	//获取报关企业信息
    	CustomCompany custom = CustomCompany.dao.findById(salesOrder.getLong("custom_id"));
    	setAttr("custom", custom);

    	//用户信息
    	long create_by = salesOrder.getLong("create_by");
    	UserLogin user = UserLogin.dao.findById(create_by);
    	setAttr("user", user);
    	
        render("/oms/SalesOrder/SalesOrderEdit.html");
    }
    
    
    @Before(Tx.class)
    public void getUser() {
    	String id = getPara("params");
    	UserLogin user = UserLogin.dao.findById(id);
    	renderJson(user);
    }
    
    
    public void list() {
    	String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        String sql = "SELECT sor.*, ifnull(u.c_name, u.user_name) creator_name "
    			+ "  from sales_order sor "
    			+ "  left join user_login u on u.id = sor.create_by"
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
    
    public void getCustomCompany() {
    	String custom_id = getPara("params");
    	CustomCompany customCompany = CustomCompany.dao.findById(custom_id);
    	renderJson(customCompany);
    }

    public void submitDingDan(){
    	
    	String order_id = getPara("order_id");
    	
    	String jsonMsg=setOrderMsg(order_id);
    	TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		String urlStr="http://test.szedi.cn:8088/phy-ceb-web/tgt/service/order_create.action";
		
		paramsMap.put("jsonMsg", jsonMsg);
		String PostData = "";
		PostData = paramsMap.toString().substring(1);
		System.out.println("参数"+PostData);
		String returnMsg = EedaHttpKit.post(urlStr, PostData);
		//String returnMsg = InUtil.getResult(urlStr, PostData);
		System.out.println("结果"+returnMsg);
		renderJson(returnMsg);
    }
    
    public static String setOrderMsg(String order_id) {
    	String orgCode="349779838";
    	SalesOrder salesOrder = SalesOrder.dao.findById(order_id);
    	
    	//报关企业
    	CustomCompany customCompany = CustomCompany.dao.findById(salesOrder.getLong("custom_id"));
    	
    	//对应的商品表
    	List<SalesOrderGoods> goodsses = SalesOrderGoods.dao.find("select * from sales_order_goods where order_id = ?",order_id);
    	
    	TreeMap<String, String> paramsMap = new TreeMap<String, String>();
        paramsMap.put("orgcode", orgCode);
        paramsMap.put("appkey", "defeng");
        String appsecret = MD5Util.encodeByMD5("888888");
        paramsMap.put("appsecret", appsecret);
        String timestamp = "" + (System.currentTimeMillis() / 1000);
        paramsMap.put("timestamp", timestamp);

        String sign = MD5Util.encodeByMD5(paramsMap + appsecret);// 888888
		
		System.out.println("参数:"+ paramsMap+appsecret);
		paramsMap.put("sign", sign);

		Map<Object, Object> requestMap = new LinkedHashMap<Object, Object>();
		
		Gson gson = new Gson(); 
		requestMap.put("postHead", gson.toJson(paramsMap));

		//order业务数据
		DingDanDto order = new DingDanDto();
		order.setOrg_code(orgCode);
		order.setOrder_no(salesOrder.getStr("order_no"));
		order.setPay_no(salesOrder.getStr("pay_no"));//原始支付单交易编号

		order.setGoods_value(salesOrder.getDouble("goods_value"));//订单商品货款
		order.setFreight(salesOrder.getDouble("freight"));//订单商品运费
		order.setCurrency(salesOrder.getStr("currency"));// 币制代码
		order.setConsignee(salesOrder.getStr("consignee"));//收货人名称
		order.setConsignee_address(salesOrder.getStr("consignee_address"));//收件人地址
		order.setConsignee_telephone(salesOrder.getStr("consignee_telephone"));//收货人电话
		order.setConsignee_country(salesOrder.getStr("consignee_country"));
		order.setPro_amount(salesOrder.getDouble("pro_amount"));//优惠金额
		order.setPro_remark(salesOrder.getStr("pro_remark"));//优惠信息说明
		order.setConsignee_type(salesOrder.getStr("consignee_type"));//收货人证件类型1-身份证，2-其它
		order.setConsignee_id(salesOrder.getStr("consignee_id"));//收件人身份证号码或其它号码
		order.setProvince(salesOrder.getStr("province"));
		order.setCity(salesOrder.getStr("city"));
		order.setDistrict(salesOrder.getStr("district"));
		order.setNote(salesOrder.getStr("note"));//备注
		order.setPayer_account(salesOrder.getStr("payer_account"));//支付人帐号ID
		order.setPayer_name(salesOrder.getStr("payer_name"));//支付人名称
		String order_time = salesOrder.getDate("order_time").toString();
		order.setOrder_time(order_time.substring(0, order_time.length()-2));//
		//order.setOrder_time("2016-05-13 13:49:50");
		
		order.setEbp_code_cus(customCompany.getStr("ebp_code_cus")); //电商平台的海关备案编码
		order.setEbp_code_ciq(customCompany.getStr("ebp_code_ciq"));  //电商平台的国检备案编码
		order.setEbp_name(customCompany.getStr("ebp_name"));//电商平台名称
		
		order.setEbc_code_cus(customCompany.getStr("ebc_code_cus")); //电商平台的海关备案编码
		order.setEbc_code_ciq(customCompany.getStr("ebc_code_ciq"));  //电商平台的国检备案编码
		order.setEbc_name(customCompany.getStr("ebc_name"));//电商平台名称

		order.setAgent_code_cus(customCompany.getStr("agent_code_cus"));//代理清单报关企业（仓储）的海关备案编码(10位)
		order.setAgent_code_ciq(customCompany.getStr("agent_code_ciq"));//代理清单报关企业的国检备案编码(10位)
		order.setAgent_name(customCompany.getStr("agent_name"));//代理清单报关企业的海关备案名称
		
//		order.setPay_code(salesOrder.getStr("pay_code"));//支付企业的海关备案编码（10位)
//		order.setPay_name(salesOrder.getStr("pay_name"));//支付企业的海关备案名称
		
		
		List<DingDanGoodsDto> goodsList=new ArrayList<DingDanGoodsDto>();
		for(SalesOrderGoods item :goodsses){
			DingDanGoodsDto goods=new DingDanGoodsDto();
			goods.setCurrency(item.getStr("currency"));//币制代码（标准代码，见参数表）
			goods.setItem_no(item.getStr("item_no"));//企业商品货号
			goods.setCus_item_no(item.getStr("cus_item_no"));//海关正面清单货号（新规则时必填）
			goods.setGift_flag(item.getStr("gift_flag"));//是否赠品(1:是，0：否)
			goods.setPrice(item.getDouble("price"));//单价
			goods.setQty(item.getDouble("qty"));//数量
			goods.setTotal(item.getDouble("total"));//总价
			goods.setUnit(item.getStr("unit"));//计量单位
			
			goodsList.add(goods);
		}
		order.setGoodsList(goodsList);
	
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

}
