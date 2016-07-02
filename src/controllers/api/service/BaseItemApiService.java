package controllers.api.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import models.Party;
import models.eeda.OrderActionLog;
import models.eeda.profile.Product;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import controllers.api.ApiController;
import controllers.oms.custom.dto.BaseItemBuilder;
import controllers.oms.custom.dto.BaseItemDto;
import controllers.util.MD5Util;

public class BaseItemApiService {
    private Logger logger = Logger.getLogger(BaseItemApiService.class);

    private Controller controller;
    private HttpServletRequest request = null;
    private String requestMethod = null;

    public BaseItemApiService(Controller controller) {
        this.controller = controller;
        this.request = controller.getRequest();
        this.requestMethod = controller.getRequest().getMethod();
    }

    @Before(Tx.class)
    public void process(String method) {
        if ("GET".equals(requestMethod)) {
            get();
        } else if ("POST".equals(requestMethod)) {
            post();
        } else {
            Record r = new Record();
            r.set("errCode", "04");
            r.set("errMsg", "API接口只支持GET/POST请求.");
            controller.renderJson(r);
        }
    }

    private void get() {
        String fullUrl = ApiController.getFullURL(request);

        saveLog("api_item_get", fullUrl);

        String sign = controller.getPara("sign");
        // 校验sign
        int splitIndex = fullUrl.indexOf("?");
        String paraStr = fullUrl.substring(splitIndex + 1);
        logger.debug("paraStr=" + paraStr);
        int signIndex = paraStr.indexOf("sign");
        if (signIndex == -1) {
            Record r = new Record();
            r.set("errCode", "02");
            r.set("errMsg", "请求中sign不存在!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        String paraStrNoSign = paraStr.substring(0, signIndex - 1);
        logger.debug("paraStrNoSign=" + paraStrNoSign);

        String serverSign = MD5Util.encodeByMD5(paraStrNoSign).toUpperCase();
        logger.debug("serverSign=" + serverSign);
        if (!sign.equals(serverSign)) {
            Record r = new Record();
            r.set("errCode", "03");
            r.set("errMsg", "请求中sign不正确!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        String appkey = controller.getPara("appkey");
        String ref_item_no = controller.getPara("ref_item_no");
        String searchSql = "select pd.*, p.org_code from product pd left join category cat on pd.category_id = cat.id"
                + " left join party p on cat.customer_id=p.id where pd.ref_item_no=? and p.appkey=?";
        Record p = Db.findFirst(searchSql, ref_item_no, appkey);
        if (p != null) {
            BaseItemDto itemDto = BaseItemBuilder.buildItemDto(p.getLong("id")
                    .toString(), p.getStr("org_code"));
            controller.renderJson(itemDto);
        } else {
            Record r = new Record();
            r.set("errCode", "01");
            r.set("errMsg", "商品编号" + ref_item_no + "不存在!");
            controller.renderJson(r);
        }

    }

    private void saveLog(String orderTeyp, String content) {
        OrderActionLog log = new OrderActionLog();
        log.set("order_type", orderTeyp);
        log.set("action", "get");
        log.set("json", content);
        log.set("time_stamp", new Date());
        log.save();
    }

    private void post() {
        String orderJsonStr = controller.getPara("item");//form 提交

        if (orderJsonStr == null) {//raw 提交
            orderJsonStr = ApiController.getRequestPayload(controller.getRequest());
        }

        saveLog("api_item_post", orderJsonStr);
        
        if (orderJsonStr == null) {
            Record r = new Record();
            r.set("errCode", "02");
            r.set("errMsg", "POST请求中, body不存在!");
            controller.renderJson(r);
            return;
        }

        // 这里将json字符串转化成javabean对象
        Map<String, ?> itemDto = new Gson().fromJson(orderJsonStr, HashMap.class);
        
        // 校验sign
        String ref_item_no = itemDto.get("ref_item_no").toString();
        String appkey = itemDto.get("appkey").toString();
        String salt = itemDto.get("salt").toString();
        String sign = itemDto.get("sign").toString();
        
        String paraStr = "ref_item_no="+ref_item_no+"&appkey="+appkey+"&salt="+salt+"&sign="+sign;
        logger.debug("paraStr=" + paraStr);
        int signIndex = paraStr.indexOf("sign");
        if (signIndex == -1) {
            Record r = new Record();
            r.set("errCode", "02");
            r.set("errMsg", "请求中sign不存在!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        
        //appkey
        Party party = Party.dao.findFirst("select * from party where party_type=? and appkey=?", Party.PARTY_TYPE_CUSTOMER, appkey);
        if (party == null) {
            Record r = new Record();
            r.set("errCode", "02");
            r.set("errMsg", "请求中appkey不正确!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        String org_code=party.getStr("org_code");
        
        String paraStrNoSign = paraStr.substring(0, signIndex - 1);
        logger.debug("paraStrNoSign=" + paraStrNoSign);

        String serverSign = MD5Util.encodeByMD5(paraStrNoSign).toUpperCase();
        logger.debug("serverSign=" + serverSign);
        if (!sign.equals(serverSign)) {
            Record r = new Record();
            r.set("errCode", "03");
            r.set("errMsg", "请求中sign不正确!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        
        Product pCheck = Product.dao.findFirst("select * from product where ref_item_no=? and org_code=?", ref_item_no, org_code);
        if (pCheck != null) {
            Record r = new Record();
            r.set("errCode", "05");
            r.set("errMsg", "ref_item_no:"+ref_item_no+"已存在!");
            controller.renderJson(r);
            return;// 注意这里一定要返回,否则会继续往下执行
        }
        
        // 开始生成item
        Product p = new Product();
        p.set("item_no", ref_item_no);
        p.set("item_name", itemDto.get("item_name").toString());
        p.set("ref_item_no", ref_item_no);
        p.set("org_code", org_code);
        p.set("custom_item_no", itemDto.get("custom_item_no").toString());
        p.set("custom_list_item_no", itemDto.get("custom_list_item_no").toString());
        
        p.set("unit", itemDto.get("unit").toString());
        p.set("currency", itemDto.get("currency").toString());
        p.set("price", itemDto.get("price").toString());
        p.set("length", itemDto.get("length").toString());
        p.set("width", itemDto.get("width").toString());
        p.set("height", itemDto.get("height").toString());
        p.set("volume", itemDto.get("volume").toString());
        p.set("weight", itemDto.get("weight").toString());
        
        p.save();

        String id = p.getLong("id").toString();

        BaseItemDto returnSoDto = BaseItemBuilder.buildItemDto(id, org_code);
        controller.renderJson(returnSoDto);
    }
}
