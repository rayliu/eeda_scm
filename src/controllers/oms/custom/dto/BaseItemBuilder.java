package controllers.oms.custom.dto;

import models.eeda.profile.Product;

public class BaseItemBuilder {
    
    //TODO: 是否会有并发问题???
    public static BaseItemDto buildItemDto(String itemId, String orgCode){
        
//        String orgCode="349779838";
        Product p = Product.dao.findById(itemId);
        
        //对应的商品表
        BaseItemDto item=new BaseItemDto();
        item.setOrg_code(orgCode);
        item.setItem_no(p.getStr("item_no"));//德丰企业商品货号
        item.setItem_name(p.getStr("item_name"));//德丰企业商品货号
        item.setCurrency(p.getStr("currency"));//币制代码（标准代码，见参数表）
        item.setRef_item_no(p.getStr("ref_item_no"));//第三方企业商品货号
        item.setCustom_item_no(p.getStr("custom_item_no"));//海关正面清单货号（新规则时必填）
        item.setPrice(p.getDouble("price"));//单价
        item.setUnit(p.getStr("unit"));//计量单位
         
        item.setLength(p.getInt("length"));//长 mm 
        item.setWidth(p.getInt("width"));//宽 mm
        item.setHeight(p.getInt("height"));//高 mm
        
        item.setVolume(p.getDouble("volume"));//体积 立方米
        item.setWeight(p.getDouble("weight"));//重量 KG
        return item;
    }
}
