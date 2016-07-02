package controllers.oms.custom.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.profile.CustomCompany;

public class DingDanBuilder {
    
    //TODO: 是否会有并发问题???
    public static DingDanDto buildDingDanDto(String order_id, String orgCode){
        
//        String orgCode="349779838";
        SalesOrder salesOrder = SalesOrder.dao.findById(order_id);
        
        //报关企业
        CustomCompany customCompany = CustomCompany.dao.findById(salesOrder.getLong("custom_id"));
        
        //对应的商品表
        List<SalesOrderGoods> goodsses = SalesOrderGoods.dao.find("select * from sales_order_goods where order_id = ?",order_id);
        
        DingDanDto order = new DingDanDto();
        order.setOrg_code(orgCode);
        order.setOrder_no(salesOrder.getStr("order_no"));
        order.setRef_order_no(salesOrder.getStr("ref_order_no"));
        order.setPay_no(salesOrder.getStr("pay_no"));//原始支付单交易编号
        order.setPay_type("PTL");//默认是通联
        order.setPay_channel(salesOrder.getStr("pay_channel"));
        
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
        String order_time = salesOrder.getDate("create_stamp").toString();
        order.setOrder_time(order_time.substring(0, order_time.length()-2));//
        //order.setOrder_time("2016-05-13 13:49:50");
        
        if(customCompany!=null){
            if(StringUtils.isNotEmpty(customCompany.getStr("ebp_code_cus")))
                order.setEbp_code_cus(customCompany.getStr("ebp_code_cus")); //电商平台的海关备案编码
            if(StringUtils.isNotEmpty(customCompany.getStr("ebp_code_ciq")))
                order.setEbp_code_ciq(customCompany.getStr("ebp_code_ciq"));  //电商平台的国检备案编码
            if(StringUtils.isNotEmpty(customCompany.getStr("ebp_name")))
                order.setEbp_name(customCompany.getStr("ebp_name"));//电商平台名称
            
            if(StringUtils.isNotEmpty(customCompany.getStr("ebc_code_cus")))
                order.setEbc_code_cus(customCompany.getStr("ebc_code_cus")); //电商平台的海关备案编码
            if(StringUtils.isNotEmpty(customCompany.getStr("ebc_code_ciq")))
                order.setEbc_code_ciq(customCompany.getStr("ebc_code_ciq"));  //电商平台的国检备案编码
            if(StringUtils.isNotEmpty(customCompany.getStr("ebc_name")))
                order.setEbc_name(customCompany.getStr("ebc_name"));//电商平台名称

            if(StringUtils.isNotEmpty(customCompany.getStr("agent_code_cus")))
                order.setAgent_code_cus(customCompany.getStr("agent_code_cus"));//代理清单报关企业（仓储）的海关备案编码(10位)
            if(StringUtils.isNotEmpty(customCompany.getStr("agent_code_ciq")))
                order.setAgent_code_ciq(customCompany.getStr("agent_code_ciq"));//代理清单报关企业的国检备案编码(10位)
            if(StringUtils.isNotEmpty(customCompany.getStr("agent_name")))
                order.setAgent_name(customCompany.getStr("agent_name"));//代理清单报关企业的海关备案名称
        }
//      order.setPay_code(salesOrder.getStr("pay_code"));//支付企业的海关备案编码（10位)
//      order.setPay_name(salesOrder.getStr("pay_name"));//支付企业的海关备案名称
        
        
        List<DingDanGoodsDto> goodsList=new ArrayList<DingDanGoodsDto>();
        for(SalesOrderGoods item :goodsses){
            DingDanGoodsDto goods=new DingDanGoodsDto();
            goods.setCurrency(item.getStr("currency"));//币制代码（标准代码，见参数表）
            goods.setItem_no(item.getStr("item_no"));//企业商品货号
            goods.setCus_item_no(item.getStr("cus_item_no"));//海关正面清单货号（新规则时必填）
            goods.setGift_flag(item.getStr("gift_flag"));//是否赠品(1:是，0：否)
            goods.setPrice(item.getDouble("price"));//单价
            goods.setQty(item.getLong("qty"));//数量
            goods.setTotal(item.getDouble("total"));//总价
            goods.setUnit(item.getStr("unit"));//计量单位
            
            goodsList.add(goods);
        }
        order.setGoodsList(goodsList);
        
        
        return order;
    }
}
