package controllers.oms.yunda;

import interceptor.SetAttrLoginUserInterceptor;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
//import com.yundaex.test.DataSecurity;


import controllers.util.Base64;
import controllers.util.EedaHttpKit;
import controllers.util.JaxbUtil;
import controllers.util.MD5Util;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class TestController extends Controller {

	private Logger logger = Logger.getLogger(TestController.class);
	Subject currentUser = SecurityUtils.getSubject();

//	@RequiresPermissions(value = { PermissionConstant.PERMISSION_TO_LIST })
	public void index() {
		render("/oms/yunda/createXml.html");
	}
	
    public void create() {
        render("/oms/moveOrder/moveOrderEdit.html");
    }
    
    public static String createXml(){
    	Orders orders = new Orders();
    	Order order = new Order();
    	order.setOrder_serial_no("dd23456");//订单号（必填）-------------------1
    	order.setKhddh("dd454545");//大客户系统订单的订单号（必填）
    	order.setNbckh("454545");//内部参考号-------------------1
    	order.setOrder_type("");//运单类型
    	
    	//发货方信息
    	Sender sender = new Sender();
    	sender.setName("kevin");//姓名（必填）----------shipper
    	sender.setCompany("eeda");//公司名
    	sender.setCity("广东省，珠海市，香洲区");//城市（上海市，上海市，青浦区）-----------------shipper_city
    	sender.setAddress("广东省珠海市香洲区XX路XX号");//地址（上海市，上海市，青浦区XXX路XXX号）（必填）-----------------------shipper_address
    	sender.setPostcode("519000");//邮编
    	sender.setPhone("6190005");//固定电话（必填）----------------shipper_telephone
    	sender.setMobile("13545625445");//移动电话（必填）----------------------------shipper_telephone
    	sender.setBranch("");//
    	order.setSender(sender);
    	
    	//收货方信息
    	Receiver receiver = new Receiver();
    	receiver.setName("jasson");//------------------------consignee
    	receiver.setCompany("aaa");//
    	receiver.setCity("广东省，珠海市，金湾区");//----------------------sales_pro_ci_dis
    	receiver.setAddress("广东省珠海市金湾区XX路XX号");//-----------------consignee_address
    	receiver.setPostcode("519090");//
    	receiver.setPhone("6548545");//-------------------consignee_telephone
    	receiver.setMobile("13754525445");//-------------------consignee_telephone
    	receiver.setBranch("");//
    	order.setReceiver(receiver);

    	//商品信息
    	order.setWeight(100.0);//物品重量--------
    	order.setSize("2,2,2");//尺寸
    	order.setValue(1.0);//货品金额
    	order.setCollection_value(1.0);//代收货款金额
    	order.setSpecial(2);//商品性质
    	
    	List<Item> item = new ArrayList<Item>();
    	Item im = new Item();
    	im.setName("外套");//商品名称
    	im.setNumber(1);//商品数量
    	im.setRemark("白色");//商品备注
    	item.add(im);

    	Items items = new Items();
    	items.setItem(item);
    	
    	order.setItems(items);
    	order.setRemark("");//订单备注
    	order.setCus_area1("");//自定义显示信息1
    	order.setCus_area2("");//自定义显示信息2
    	order.setCallback_id("abcd4545454");//接口异步回传的时候返回的ID
    	order.setWave_no("bc45646576");//客户波次好
    	orders.setOrder(order);
    	
    	String str = JaxbUtil.convertToXml(orders);  
        System.out.println(str); 
        return str;
    }
    
   
    public static void main(String[] args) throws JAXBException {
    	String str = createXml();
        
        String partnerid ="7690811002";
        String pwd = "uam4WYkE5GAyxjJC2XVMSTUh7r6gBD";
        String version = "1.0";
        //String action = "interface_receive_order__mailno.php";//下单
        
        String action = "interface_modify_order.php";//更新
        String urlStr="http://orderdev.yundasys.com:10110/cus_order/order_interface/"+action;
        
        //String action = "interface_cancel_order.php";//取消
//        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
//        paramsMap.put("partnerid", partnerid);
//        paramsMap.put("version", "1.0");
//        paramsMap.put("request", "data");
//        paramsMap.put("xmldata", URLEncoder.encode(str));
//        paramsMap.put("validation", MD5Util.GetMD5Code(Base64.getBase64(str)+partnerid+pwd));
//        String PostData = paramsMap.toString();
//        System.out.println("参数"+PostData);
//        String data = "";
//        data = "partnerid="+partnerid+"&version=1.0&request=data&xmldata="+URLEncoder.encode(str)
//        		+"&validation="+MD5Util.GetMD5Code(Base64.getBase64(str)+partnerid+pwd);
        
        String data;
		try {
			data = DataSecurity.security(partnerid, pwd, str);
			
			System.out.println(data);
			System.out.println("我："+MD5Util.GetMD5Code(Base64.getBase64(str)+partnerid+pwd));
			data += "&version=" + version + "&request=data";
	        
	        String returnMsg = EedaHttpKit.post(urlStr, data);
	        System.out.println("结果"+returnMsg);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
}
