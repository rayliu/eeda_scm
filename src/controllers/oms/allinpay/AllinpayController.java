package controllers.oms.allinpay;

import com.allinpay.ets.client.SecurityUtil;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;

import controllers.oms.custom.dto.SendReturnDto;

public class AllinpayController extends Controller {

	private Logger logger = Logger.getLogger(AllinpayController.class);
    
	//demo
	public void index(){
        render("/oms/allinpay/demo/index.html");
    }
	
	public void payDemo(){
        render("/oms/allinpay/demo/post.html");
    }
	
	public void payDemoPost(){
        renderJsp("/WEB-INF/template/oms/allinpay/demo/post.jsp");
    }
	
	public void queryDemo(){
        render("/oms/allinpay/demo/merchantOrderQuery.html");
    }
	
	
	//订单支付页面
	public void payOrder(){
	    String serverUrl = getPara("serverUrl");
	    //构造订单请求对象，生成signMsg。
	    com.allinpay.ets.client.RequestOrder requestOrder = new com.allinpay.ets.client.RequestOrder();
	    String inputCharset = getPara("inputCharset");
	    String pickupUrl = getPara("pickupUrl");
	    String receiveUrl = getPara("receiveUrl");
	    String version = getPara("version");
	    String language = getPara("language");
	    String signType = getPara("signType");
	    String payType = getPara("payType");
	    String issuerId = getPara("issuerId");
	    String merchantId = getPara("merchantId");
	    String payerName = getPara("payerName");
	    String payerEmail = getPara("payerEmail");
	    String payerTelephone = getPara("payerTelephone");
	    String payerIDCard = getPara("payerIDCard");
	    String pid = getPara("pid");
	    String orderNo = getPara("orderNo");
	    String orderAmount = getPara("orderAmount");
	    String orderCurrency = getPara("orderCurrency");
	    String orderDatetime = getPara("orderDatetime");
	    String orderExpireDatetime = getPara("orderExpireDatetime");
	    String productName = getPara("productName");
	    String productPrice = getPara("productPrice");
	    String productNum = getPara("productNum");
	    String productId = getPara("productId");
	    String productDesc = getPara("productDesc");
	    String ext1 = getPara("ext1");
	    String ext2 = getPara("ext2");
	    String extTL = getPara("extTL");
	    String pan = getPara("pan");
	    String tradeNature = getPara("tradeNature");
	    String key = getPara("key");
	    
	    String sign="";
//	    若 signType=0 则用 MD5 算法进行验签,检查 MD5Key 设置的是否与商服平台下配置一致(测试环境默认值 1234567890);
	    //若 signType=1 则用通联提供的公钥证书进行验签, 注意证书的存放位置应与商户验签代码中设的位置相同,另外请商户分清证书是测试证 书还是生产证书。
	    
	    //若直连telpshx渠道，payerTelephone、payerName、payerIDCard、pan四个字段不可为空
	    //其中payerIDCard、pan需使用公钥加密（PKCS1格式）后进行Base64编码
	    if(null!=payerIDCard&&!"".equals(payerIDCard)){
	        try{
	            //payerIDCard = SecurityUtil.encryptByPublicKey("C:\\TLCert.cer", payerIDCard);
	            payerIDCard = SecurityUtil.encryptByPublicKey("/opt/conf/TLCert.cer", payerIDCard);
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	    }
	    if(null!=pan&&!"".equals(pan)){
	        try{
	            pan = SecurityUtil.encryptByPublicKey("/opt/conf/TLCert.cer", pan);
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	    }
	    
	    
	    if(null!=inputCharset&&!"".equals(inputCharset)){
	        requestOrder.setInputCharset(Integer.parseInt(inputCharset));
	    }
	    requestOrder.setPickupUrl(pickupUrl);
	    requestOrder.setReceiveUrl(receiveUrl);
	    requestOrder.setVersion(version);
	    if(null!=language&&!"".equals(language)){
	        requestOrder.setLanguage(Integer.parseInt(language));
	    }
	    requestOrder.setSignType(Integer.parseInt(signType));
	    requestOrder.setPayType(Integer.parseInt(payType));
	    requestOrder.setIssuerId(issuerId);
	    requestOrder.setMerchantId(merchantId);
	    requestOrder.setPayerName(payerName);
	    requestOrder.setPayerEmail(payerEmail);
	    requestOrder.setPayerTelephone(payerTelephone);
	    requestOrder.setPayerIDCard(payerIDCard);
	    requestOrder.setPid(pid);
	    requestOrder.setOrderNo(orderNo);
	    requestOrder.setOrderAmount(Long.parseLong(orderAmount));
	    requestOrder.setOrderCurrency(orderCurrency);
	    requestOrder.setOrderDatetime(orderDatetime);
	    requestOrder.setOrderExpireDatetime(orderExpireDatetime);
	    requestOrder.setProductName(productName);
	    if(null!=productPrice&&!"".equals(productPrice)){
	        requestOrder.setProductPrice(Long.parseLong(productPrice));
	    }
	    if(null!=productNum&&!"".equals(productNum)){
	        requestOrder.setProductNum(Integer.parseInt(productNum));
	    }   
	    requestOrder.setProductId(productId);
	    requestOrder.setProductDesc(productDesc);
	    requestOrder.setExt1(ext1);
	    requestOrder.setExt2(ext2);
	    requestOrder.setExtTL(extTL);//通联商户拓展业务字段，在v2.2.0版本之后才使用到的，用于开通分账等业务
	    requestOrder.setPan(pan);
	    requestOrder.setTradeNature(tradeNature);
	    requestOrder.setKey(key); //key为MD5密钥，密钥是在通联支付网关会员服务网站上设置。

	    String strSrcMsg = requestOrder.getSrc(); // 此方法用于debug，测试通过后可注释。
	    String strSignMsg = requestOrder.doSign(); // 签名，设为signMsg字段值。
	    
	    setAttr("order", requestOrder);
	    setAttr("serverUrl", serverUrl);
	    setAttr("strSrcMsg", strSrcMsg);
	    setAttr("strSignMsg", strSignMsg);
	    render("/oms/allinpay/pay.html");
	}
	
	//支付回调接口
	public void payResultRecv(){
	    
    }
}
