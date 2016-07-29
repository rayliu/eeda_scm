package controllers.oms.gateOutOrder.zto;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ZtoTest {
    
    public static void main(String[] args) {

        String url = "http://testpartner.zto.cn/client/interface.php";

        Map<String, Object> map = new HashMap<String, Object>();
        //String data = "{\"mailno\": \"718595286704\"}";
        //String data = "{\"lastno\": \"\"}";
        String data = "{"
            +"\"sendcity\": \"上海,上海市,虹口区\""
//            +"\"sendaddress\": \"华志路1685号\","
//            +"\"receivercity\": \"湖北,仙桃,仙桃\","
//            +"\"receiveraddress\": \"干河办事处\""
          +"}";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        try {
            String content = DigestUtil.encryptBASE64(data);
            map.put("content", content);
            map.put("style", "json");
            //map.put("func", "mail.trace");
            //map.put("func", "mail.counter");
            map.put("func", "mail.marke");
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
