package controllers.oms.allinpay;

import models.eeda.oms.SalesOrder;

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
	    if("142".equals(orderCurrency))//彭海运   142: 人民币
	        requestOrder.setOrderCurrency("0");//通联:   默认为人民币; 0,156表示人民币;344表示港币;840表示美元
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
	//支付回调接口, 即通联的pickupUrl
    public void pickupUrl(){
        //Parameter: payDatetime=20160622161441  ext1=123  payAmount=1  returnDatetime=20160622161156  
        //issuerId=  signMsg=jBKEl/ls4ut1Kb0LvdRsrcUaS5g5jNe1NqAG7XDHmDmQFIdlaIPH4lJVB57C1y5H9TMGPIMvqw2nv9ftiDj5w/uR4Szo6yWRe81Z9cO7nChfsbyWM0Om5KB5nvmMwJLMHj8s406e0NFX4jFTEDiMhDawLTj9wdKPPl+o8scSDr4=  
        //payType=0  language=1  orderDatetime=20160622103144  merchantId=100020091218001  errorCode=  version=v1.0  ext2=123  signType=1  orderAmount=1
        //orderNo=DD2016052600004  德丰-销售订单号 
        //paymentOrderId=201606221614261038 通联支付流水号 
        //payResult=1  支付成功
        String orderNo=getPara("orderNo");
        String payResult = getPara("payResult");
        if(!"1".equals(payResult)){
            render("/oms/allinpay/payOK.html");
        }else{
            render("/oms/allinpay/payFail.html");
        }
        
    }
    
	//支付回调接口, 即通联的receiveUrl
	public void payResultRecv(){
	    //Parameter: payDatetime=20160622161441  ext1=123  payAmount=1  returnDatetime=20160622161156  
	    //issuerId=  signMsg=jBKEl/ls4ut1Kb0LvdRsrcUaS5g5jNe1NqAG7XDHmDmQFIdlaIPH4lJVB57C1y5H9TMGPIMvqw2nv9ftiDj5w/uR4Szo6yWRe81Z9cO7nChfsbyWM0Om5KB5nvmMwJLMHj8s406e0NFX4jFTEDiMhDawLTj9wdKPPl+o8scSDr4=  
	    //payType=0  language=1  orderDatetime=20160622103144  merchantId=100020091218001  errorCode=  version=v1.0  ext2=123  signType=1  orderAmount=1
	    //orderNo=DD2016052600004  德丰-销售订单号 
	    //paymentOrderId=201606221614261038 通联支付流水号 
	    //payResult=1  支付成功
	    String orderNo=getPara("orderNo");
	    String payResult = getPara("payResult");
	    if(!"1".equals(payResult))
	        return;
	    
	    SalesOrder so = SalesOrder.dao.findFirst("select * from sales_order where order_no=?", orderNo);
	    if(so!=null){
	        so.set("pay_no", getPara("paymentOrderId")).update();
	    }
	    renderJson();
    }
}
