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
		
		order.setGoods_value(salesOrder.getDouble("goods_value"));
		order.setFreight(salesOrder.getDouble("freight"));    
		order.setDiscount(salesOrder.getDouble("discount"));     //+++++++++++++//非现金抵扣金额，无则为0
		order.setTax_total(salesOrder.getDouble("tax_total"));      //+++++++++++++代扣税款，无则为0
		order.setActural_paid(salesOrder.getDouble("actural_paid"));	//++++++++++++实际支付 金额，无则为0
		
		//order.setCurrency("142");// 默认人民币
		order.setConsignee(salesOrder.getStr("consignee"));
		order.setConsignee_address(salesOrder.getStr("consignee_address"));
		order.setConsignee_telephone(salesOrder.getStr("consignee_telephone"));
		order.setDistrict(salesOrder.getStr("district"));
		order.setBuyer_regno(salesOrder.getStr("buyer_regno"));  //订购人注册号
		order.setBuyer_name(salesOrder.getStr("buyer_name"));     //订购人姓名
		order.setBuyer_id_number(salesOrder.getStr("buyer_id_number"));    //
		order.setIs_order_pass(salesOrder.getStr("is_order_pass"));      //是否已完成订单申报  
		order.setIs_pay_pass(salesOrder.getStr("is_pay_pass"));        //是否已完成支付申报（1是，0否，默认0）
		order.setPay_transaction_id(salesOrder.getStr("pay_transaction_id"));  //支付交易编号(支付申报编号)  （可空）
		
		order.setPay_no(salesOrder.getStr("pay_no"));
		
		String pay_time = salesOrder.getDate("pay_time").toString();
		order.setPay_time(pay_time.substring(0, pay_time.length()-2));   //支付时间
		order.setPay_type(salesOrder.getStr("pay_type"));   //支付渠道        
		order.setPay_channel(salesOrder.getStr("pay_channel"));  //支付渠道(可空)
		order.setShop_no(salesOrder.getStr("shop_no"));  //店铺代码（可空）
		order.setNote(salesOrder.getStr("note"));//（可空）
		order.setBatch_numbers(salesOrder.getStr("batch_numbers"));//商品批次号(可空)
		order.setWh_org_code(salesOrder.getStr("wh_org_code"));//企业组织结构代码（仓储企业）
		
		//运单
		order.setLogistics_no(salesOrder.getStr("logistics_no"));   //运单号
		order.setGoods_info(salesOrder.getStr("goods_info"));  //主要货物信息
		order.setInsure_fee(salesOrder.getInt("insure_fee"));// 保价费 默认为0
		order.setWeight(salesOrder.getDouble("weight"));  //毛重
		order.setNet_weight(salesOrder.getDouble("net_weight"));//净重
		order.setPack_no(salesOrder.getInt("pack_no"));  //包裹数
		order.setCop_no(salesOrder.getStr("cop_no"));//企业内部标识单证的编号
		order.setAssure_code(salesOrder.getStr("assure_code"));//担保企业编号
		order.setSign_company(salesOrder.getStr("sign_company"));//承运企业海关备案号
		order.setSign_company_name(salesOrder.getStr("sign_company_name")); //承运企业名称
		order.setEms_no(salesOrder.getStr("ems_no"));//电商账册编号（空）
		order.setDecl_time(salesOrder.getDate("decl_time").toString()); // 申报日期（yyy-mm-dd）
		order.setCustoms_code(salesOrder.getStr("customs_code"));//主管海关代码
		order.setCiq_code(salesOrder.getStr("ciq_code"));//主管检验疫机构代码
		order.setPort_code(salesOrder.getStr("port_code"));//口岸海关代码
		order.setIe_date(salesOrder.getDate("ie_date").toString());//进口日期（yyy-mm-dd）（空）
		order.setTrade_mode(salesOrder.getStr("trade_mode"));//贸易方式
		order.setBusiness_mode(salesOrder.getStr("business_mode"));//业务模式代码
		order.setTraf_mode(salesOrder.getStr("traf_mode"));//运输方式
		order.setTraf_no(salesOrder.getStr("traf_no")); //运输工具编号（可空）
		order.setShip_name(salesOrder.getStr("ship_name"));//运输工具名称
		order.setVoyage_no(salesOrder.getStr("voyage_no"));//航班航次号（可空）
		order.setBill_no(salesOrder.getStr("bill_no"));  //提运单号（可空）
		order.setSupervision_code(salesOrder.getStr("supervision_code"));//（可空）监管场所代码
		order.setCountry_code(salesOrder.getStr("country_code"));//起运国
		order.setWrap_type(salesOrder.getStr("wrap_type"));//包装种类
        
		//报关企业信息
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

//            if(StringUtils.isNotEmpty(customCompany.getStr("agent_code_cus")))
//                order.setAgent_code_cus(customCompany.getStr("agent_code_cus"));//代理清单报关企业（仓储）的海关备案编码(10位)
//            if(StringUtils.isNotEmpty(customCompany.getStr("agent_code_ciq")))
//                order.setAgent_code_ciq(customCompany.getStr("agent_code_ciq"));//代理清单报关企业的国检备案编码(10位)
//            if(StringUtils.isNotEmpty(customCompany.getStr("agent_name")))
//                order.setAgent_name(customCompany.getStr("agent_name"));//代理清单报关企业的海关备案名称
        }
        
        List<DingDanGoodsDto> goodsList=new ArrayList<DingDanGoodsDto>();
        for(SalesOrderGoods item :goodsses){
            DingDanGoodsDto goods=new DingDanGoodsDto();  
            goods.setCurrency(item.getStr("currency"));
    		goods.setItem_no(item.getStr("item_no"));
    		goods.setQty(item.getLong("qty"));
    		goods.setQty1(item.getLong("qty1"));
    		goods.setUnit(item.getStr("unit"));
    		goods.setUnit1(item.getStr("unit1"));
    		goods.setPrice(item.getDouble("price"));
    		goods.setTotal_price(item.getDouble("total"));
    		goods.setCountry(item.getStr("country"));
    		goods.setGcode(item.getStr("gcode"));
    		goods.setG_model(item.getStr("g_model"));
    		goods.setCiq_gno(item.getStr("ciq_gno"));//(可空)
    		goods.setCiq_gmodel(item.getStr("ciq_gmodel"));
    		goods.setBrand(item.getStr("brand"));
    		goods.setNote(item.getStr("note"));//可空
            
            goodsList.add(goods);
        }
        order.setGoodslist(goodsList);
        
        
        return order;
    }
}
