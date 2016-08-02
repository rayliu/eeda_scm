package controllers.oms.gateOutOrder.zto;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ZtoTest {
    
    public static void main(String[] args) {

       //getLastNo();//OK
      //testTrace();//返回数据为：{"traces":[]}
      //testmarke();//数据返回:{"mark":"武汉转仙桃 20","marke":"武汉转仙桃 20"}
      //testbillimg();// 数据返回:{"result":"false","mailno": "718595286704","remark": "该单号没有签收图片。""}
      //textstatus();// 返回数据:{"track":{"billcode":"718595286704","detail":[]}}
      //textsubmit();//返回数据:{"result": "false","code": "s03","remark": "无效的指令操作"}
      //testbatch_submit();//返回数据:{"result": "false","code": "s03","remark": "无效的指令操作"}
      //testOrderCancel();//返回数据:{"result": "false","code": "D03","remark": "订单号不存在,无法取消"}
      //testBindCancle();//返回数据:{"result":"false","orderId": "ZTO14083000002447","remark": "确认参数无误"}
    	
    	
    }
    
    //电子运单号可用数量查询接口 (mail.counter)
	private static void getLastNo() {
		String url = "http://testpartner.zto.cn/client/interface.php";

        Map<String, Object> map = new HashMap<String, Object>();
        String data = "{\"lastno\": \"\"}";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        try {
            String content = DigestUtil.encryptBASE64(data);
            map.put("content", content);
            map.put("style", "json");

            map.put("func", "mail.counter");

            map.put("partner", "test");
            map.put("datetime", datetime);

            map.put("verify",
                    DigestUtil.digest("test", datetime, content, "ZTO123"));

            String res = HttpUtil.post(url, "utf-8", map);
            System.out.println(res);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	//快件追踪接口 (mail.trace)	
	private static void testTrace() {
		String url = "http://testpartner.zto.cn/client/interface.php";

        Map<String, Object> map = new HashMap<String, Object>();
        String data = "{\"mailno\": \"718595286704\"}";
 //       String data = "{\"lastno\": \"\"}";
//         String data = "{"
//            +"\"sendcity\": \"上海,上海市,虹口区\","
//           +"\"sendaddress\": \"华志路1685号\","
//            +"\"receivercity\": \"湖北,仙桃,仙桃\","
//            +"\"receiveraddress\": \"干河办事处\""
//          +"}";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        try {
            String content = DigestUtil.encryptBASE64(data);
            map.put("content", content);
            map.put("style", "json");
            map.put("func", "mail.trace");
            //map.put("func", "mail.counter");
            //map.put("func", "mail.marke");
            map.put("partner", "test");
            map.put("datetime", datetime);

            map.put("verify",
                    DigestUtil.digest("test", datetime, content, "ZTO123"));

            String res = HttpUtil.post(url, "utf-8", map);
            System.out.println(res);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	//获取大头笔 (order.marke)
	private static void testmarke() {
		String url = "http://testpartner.zto.cn/client/interface.php";

        Map<String, Object> map = new HashMap<String, Object>();
        //String data = "{\"mailno\": \"718595286704\"}";
//        String data = "{\"lastno\": \"\"}";
        String data = "{"
           +"\"sendcity\": \"上海,上海市,虹口区\","
            +"\"sendaddress\": \"华志路1685号\","
            +"\"receivercity\": \"湖北,仙桃,仙桃\","
          +"\"receiveraddress\": \"干河办事处\""
         +"}";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        try {
            String content = DigestUtil.encryptBASE64(data);
            map.put("content", content);
            map.put("style", "json");
            //map.put("func", "mail.trace");
            //map.put("func", "mail.counter");
            map.put("func", "order.marke");
            map.put("partner", "test");
            map.put("datetime", datetime);

            map.put("verify",
                    DigestUtil.digest("test", datetime, content, "ZTO123"));

            String res = HttpUtil.post(url, "utf-8", map);
            System.out.println(res);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	//签收图片接口 (mail.billimg)
	private static void testbillimg() {
		String url = "http://testpartner.zto.cn/client/interface.php";
        Map<String, Object> map = new HashMap<String, Object>();
        //String data = "{\"mailno\": \"718595286704\"}";
//        String data = "{\"lastno\": \"\"}";
        String data = "{\"mailno\":\"718595286704\"}";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        try {
            String content = DigestUtil.encryptBASE64(data);
            map.put("content", content);
            map.put("style", "json");
            //map.put("func", "mail.trace");
            //map.put("func", "mail.counter");
            map.put("func", "mail.billimg");
            map.put("partner", "test");
            map.put("datetime", datetime);

            map.put("verify",
                    DigestUtil.digest("test", datetime, content, "ZTO123"));

            String res = HttpUtil.post(url, "utf-8", map);
            System.out.println(res);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	//快件状态查询 (mail.status)
	private static void textstatus() {
		String url = "http://testpartner.zto.cn/client/interface.php";

        Map<String, Object> map = new HashMap<String, Object>();
        //String data = "{\"lastno\": \"\"}";
        String data="{\"mailno\":\"718595286704\"}";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        try {
            String content = DigestUtil.encryptBASE64(data);
            map.put("content", content);
            map.put("style", "json");

            map.put("func", "mail.status");

            map.put("partner", "test");
            map.put("datetime", datetime);

            map.put("verify",
                    DigestUtil.digest("test", datetime, content, "ZTO123"));

            String res = HttpUtil.post(url, "utf-8", map);
            System.out.println(res);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	//新增或修改订单 (order.submit)
	private static void textsubmit() {
		String url = "http://testpartner.zto.cn/client/interface.php";

        Map<String, Object> map = new HashMap<String, Object>();
        //String data = "{\"lastno\": \"\"}";
        //String data="{\"mailno\":\"718595286704\"}";

        String data="{"
            +"\"id\": \"130520142013234\","
        		+"\"type\": \" \","
        		+"\"tradeid\": \"2701843\","
        		+"\"mailno\": \"1000000000016\","
        		+"\"seller\": \"1023123709\","
        		+"\"buyer\": \"6123928182\","
                +"\"sender\": \"{\","
                +"\"id\": \"130520142010\","
                +"\"name\": \"李琳\","
                +"\"company\": \"新南电子商务有限公司\","
                +"\"mobile\": \"13912345678\","
                +"\"phone\": \"021-87654321\","
                +"\"area\": \"310118\","
                +"\"city\": \"上海市,上海市,青浦区\","
                +"\"address\": \"华新镇华志路123号\","
                +"\"zipcode\": \"610012\","
                +"\"email\": \"ll@abc.com\","
                +"\"im\": \"1924656234\","
                +"\"starttime\": \"2013-05-20 12:00:00\","
                +"\"endtime\": \"2013-05-20 15:00:00\","
           +"}"
           +",\"receiver\": {"
                +"\"id\": \"130520142097\","
                +"\"name\": \"杨逸嘉\","
                +"\"company\": \"逸嘉实业有限公司\","
                +"\"mobile\": \"13687654321\","
                +"\"phone\": \"010-22226789\","
                +"\"area\": \"501022\","
                +"\"city\": \"四川省,成都市,武侯区\","
                +"\"address\": \"育德路497号\","
                +"\"zipcode\": \"610012\","
                +"\"email\": \"yyj@abc.com\","
                +"\"im\": \"yangyijia-abc\","
           +"}"
           +",\"items\": ["
               +"{"
                    +"\"id\": \"1234567\","
                    +"\"name\": \"迷你风扇\","
                    +"\"category\": \"电子产品\","
                    +"\"material\": \"金属\","
                    +"\"size\": \"12,11,23\","
                    +"\"weight\": \"0.752\","
                    +"\"unitprice\": \"79\","
                    +"\"url\": \"http://www.abc.com/123.html\","
                    +"\"quantity\": \"1\","
                    +"\"remark\": \"黑色大号\","
               +"}"
               +",{"
                    +"\"name\": \"USB3.0集线器\","
                    +"\"quantity\": \"1\""
                    +",\"remark\": \" \""
               +"}]"
               +",\"weight\": \"0.753\""
               +",\"size\": \"12,23,11\""
           +",\"quantity\": \"2\""
           +",\"price\": \"126.50\""
           +",\"freight\": \"10.00\""
           +",\"premium\": \"0.50\""
           +",\"pack_charges\": \"1.00\""
           +",\"other_charges\": \"0.00\""
           +",\"order_sum\": \"0.00\""
           +",\"collect_moneytype\": \"CNY\""
           +",\"collect_sum\": \"12.00\""
           +",\"remark\": \"请勿摔货\""
       +"}";
        
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());
          
        try {
            String content = DigestUtil.encryptBASE64(data);
            map.put("content", content);
            map.put("style", "json");
            map.put("func", "order.submit");
            map.put("partner", "test");
            map.put("datetime", datetime);

            map.put("verify",
                    DigestUtil.digest("test", datetime, content, "ZTO123"));

            String res = HttpUtil.post(url, "utf-8", map);
            System.out.println(res);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
    //2. 批量下单，修改接口 (order.batch_submit)
	private static void testbatch_submit() {
		String url = "http://testpartner.zto.cn/client/interface.php";

        Map<String, Object> map = new HashMap<String, Object>();
        //String data = "{\"lastno\": \"\"}";
        String data="[{"
            +"\"id\": \"130520142013234\""
                +",\"type\": \" \""
                +",\"tradeid\": \"2701843\""
                +",\"mailno\": \"1000000000016\""
                +",\"seller\": \"1023123709\""
                +",\"buyer\": \"6123928182\""
                +",\"sender\": {"
                    +"\"id\": \"130520142010\""
                   +",\"name\": \"李琳\""
                   +",\"company\": \"新南电子商务有限公司\""
                   +",\"mobile\": \"13912345678\""
                   +",\"phone\": \"021-87654321\""
                   +",\"area\": \"310118\""
                   +",\"city\": \"上海市,上海市,青浦区\""
                   +",\"address\": \"华新镇华志路123号\""
                   +",\"zipcode\": \"610012\""
                   +",\"email\": \"ll@abc.com\""
                   +",\"im\": \"1924656234\""
                   +",\"starttime\": \"2013-05-20 12:00:00\""
                   +",\"endtime\": \"2013-05-20 15:00:00\""
               +"}"
               +",\"receiver\": {"
                    +"\"id\": \"130520142097\""
                   +",\"name\": \"杨逸嘉\""
                   +",\"company\": \"逸嘉实业有限公司\""
                   +",\"mobile\": \"13687654321\""
                   +",\"phone\": \"010-22226789\""
                   +",\"area\": \"501022\""
                   +",\"city\": \"四川省,成都市,武侯区\""
                   +",\"address\": \"育德路497号\""
                   +",\"zipcode\": \"610012\""
                   +",\"email\": \"yyj@abc.com\""
                   +",\"im\": \"yangyijia-abc\""
               +"}]";
        
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        try {
            String content = DigestUtil.encryptBASE64(data);
            map.put("content", content);
            map.put("style", "json");

            map.put("func", "order.batch_submit");

            map.put("partner", "test");
            map.put("datetime", datetime);

            map.put("verify",
                    DigestUtil.digest("test", datetime, content, "ZTO123"));

            String res = HttpUtil.post(url, "utf-8", map);
            System.out.println(res);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	//4. 订单取消 (order.cancel)
	private static void testOrderCancel() {
		String url = "http://testpartner.zto.cn/client/interface.php";

        Map<String, Object> map = new HashMap<String, Object>();
        //String data = "{\"lastno\": \"\"}";
        String data="{"
            +"\"id\": \"ZTO-130520142013234\""
           +",\"remark\": \"收货人出差了，过几天后再发\""
       +"}";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        try {
            String content = DigestUtil.encryptBASE64(data);
            map.put("content", content);
            map.put("style", "json");

            map.put("func", "order.cancel");

            map.put("partner", "test");
            map.put("datetime", datetime);

            map.put("verify",
                    DigestUtil.digest("test", datetime, content, "ZTO123"));

            String res = HttpUtil.post(url, "utf-8", map);
            System.out.println(res);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	//5. 订单解绑 (bind.cancle)
	private static void testBindCancle() {
		String url = "http://testpartner.zto.cn/client/interface.php";

        Map<String, Object> map = new HashMap<String, Object>();
        //String data = "{\"lastno\": \"\"}";
        String data ="{\"orderId\":\"ZTO14083000002447\" }";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        try {
            String content = DigestUtil.encryptBASE64(data);
            map.put("content", content);
            map.put("style", "json");

            map.put("func", "bind.cancle");

            map.put("partner", "test");
            map.put("datetime", datetime);

            map.put("verify",
                    DigestUtil.digest("test", datetime, content, "ZTO123"));

            String res = HttpUtil.post(url, "utf-8", map);
            System.out.println(res);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
}
