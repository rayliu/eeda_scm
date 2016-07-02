package controllers.oms.custom.dto;

import java.util.List;

public class DingDanDto {
    private String org_code;
    private String order_no;
    private String ref_order_no;
    private String order_time;
    private String shop_no;
    private String warehouse_no;
    private String ebp_code_cus;
    private String ebp_code_ciq;
    private String ebp_name;
    private String ebc_code_cus;
    private String ebc_code_ciq;
    private String ebc_name;
    private String agent_code_cus;
    private String agent_code_ciq;
    private String agent_name;
    
    //在《Effective Java》这本书中提到这个原则，float和Double只能用来做科学计算或者是工程计算，
    //在商业计算中我们要用 java.math.BigDecimal。
    private Double goods_value;
    private Double freight;
    private String currency;
    private String consignee_id;
    private String consignee_type;
    private String consignee;
    private String consignee_address;
    private String consignee_telephone;
    private String consignee_country;
    private String province;
    private String city;
    private String district;
    private Double pro_amount;
    private String pro_remark;
    private String note;
    private String pay_no;
    
    private String pay_type; //支付通关模式(PQB：钱宝；PYJF：易极付；PTL：通联) 
    //支付渠道(01：网关支付；02：手机WAP支付；03：线下POS支付；04：手机APP支付；05：预付卡支付；06：便捷付POS支付；07：其他支付渠道；08：新版预付卡支付) 
    private String pay_channel; //通联的支付方式
    
    private String pay_platform;
    private String payer_account;
    private String payer_name;
    private String is_pay_pass;
    private String pass_pay_no;
    private String pay_code;
    private String pay_name;
    
    private List<DingDanGoodsDto> goodsList;
    
    public String getOrg_code() {
        return org_code;
    }
    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }
    public String getOrder_no() {
        return order_no;
    }
    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }
    public String getRef_order_no() {
        return ref_order_no;
    }
    public void setRef_order_no(String ref_order_no) {
        this.ref_order_no = ref_order_no;
    }
    public String getOrder_time() {
        return order_time;
    }
    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }
    public String getShop_no() {
        return shop_no;
    }
    public void setShop_no(String shop_no) {
        this.shop_no = shop_no;
    }
    public String getWarehouse_no() {
        return warehouse_no;
    }
    public void setWarehouse_no(String warehouse_no) {
        this.warehouse_no = warehouse_no;
    }
    public String getEbp_code_cus() {
        return ebp_code_cus;
    }
    public void setEbp_code_cus(String ebp_code_cus) {
        this.ebp_code_cus = ebp_code_cus;
    }
    public String getEbp_code_ciq() {
        return ebp_code_ciq;
    }
    public void setEbp_code_ciq(String ebp_code_ciq) {
        this.ebp_code_ciq = ebp_code_ciq;
    }
    public String getEbp_name() {
        return ebp_name;
    }
    public void setEbp_name(String ebp_name) {
        this.ebp_name = ebp_name;
    }
    public String getEbc_code_cus() {
        return ebc_code_cus;
    }
    public void setEbc_code_cus(String ebc_code_cus) {
        this.ebc_code_cus = ebc_code_cus;
    }
    public String getEbc_code_ciq() {
        return ebc_code_ciq;
    }
    public void setEbc_code_ciq(String ebc_code_ciq) {
        this.ebc_code_ciq = ebc_code_ciq;
    }
    public String getEbc_name() {
        return ebc_name;
    }
    public void setEbc_name(String ebc_name) {
        this.ebc_name = ebc_name;
    }
    public String getAgent_code_cus() {
        return agent_code_cus;
    }
    public void setAgent_code_cus(String agent_code_cus) {
        this.agent_code_cus = agent_code_cus;
    }
    public String getAgent_code_ciq() {
        return agent_code_ciq;
    }
    public void setAgent_code_ciq(String agent_code_ciq) {
        this.agent_code_ciq = agent_code_ciq;
    }
    public String getAgent_name() {
        return agent_name;
    }
    public void setAgent_name(String agent_name) {
        this.agent_name = agent_name;
    }
    public Double getGoods_value() {
        return goods_value;
    }
    public void setGoods_value(Double goods_value) {
        this.goods_value = goods_value;
    }
    public Double getFreight() {
        return freight;
    }
    public void setFreight(Double freight) {
        this.freight = freight;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getConsignee_id() {
        return consignee_id;
    }
    public void setConsignee_id(String consignee_id) {
        this.consignee_id = consignee_id;
    }
    public String getConsignee_type() {
        return consignee_type;
    }
    public void setConsignee_type(String consignee_type) {
        this.consignee_type = consignee_type;
    }
    public String getConsignee() {
        return consignee;
    }
    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }
    public String getConsignee_address() {
        return consignee_address;
    }
    public void setConsignee_address(String consignee_address) {
        this.consignee_address = consignee_address;
    }
    public String getConsignee_telephone() {
        return consignee_telephone;
    }
    public void setConsignee_telephone(String consignee_telephone) {
        this.consignee_telephone = consignee_telephone;
    }
    public String getConsignee_country() {
        return consignee_country;
    }
    public void setConsignee_country(String consignee_country) {
        this.consignee_country = consignee_country;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }
    public Double getPro_amount() {
        return pro_amount;
    }
    public void setPro_amount(Double pro_amount) {
        this.pro_amount = pro_amount;
    }
    public String getPro_remark() {
        return pro_remark;
    }
    public void setPro_remark(String pro_remark) {
        this.pro_remark = pro_remark;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getPay_no() {
        return pay_no;
    }
    public void setPay_no(String pay_no) {
        this.pay_no = pay_no;
    }
    public String getPay_channel() {
        return pay_channel;
    }
    public void setPay_channel(String pay_channel) {
        this.pay_channel = pay_channel;
    }
    public String getPay_type() {
        return pay_type;
    }
    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }
    public String getPay_platform() {
        return pay_platform;
    }
    public void setPay_platform(String pay_platform) {
        this.pay_platform = pay_platform;
    }
    public String getPayer_account() {
        return payer_account;
    }
    public void setPayer_account(String payer_account) {
        this.payer_account = payer_account;
    }
    public String getPayer_name() {
        return payer_name;
    }
    public void setPayer_name(String payer_name) {
        this.payer_name = payer_name;
    }
    public String getIs_pay_pass() {
        return is_pay_pass;
    }
    public void setIs_pay_pass(String is_pay_pass) {
        this.is_pay_pass = is_pay_pass;
    }
    public String getPass_pay_no() {
        return pass_pay_no;
    }
    public void setPass_pay_no(String pass_pay_no) {
        this.pass_pay_no = pass_pay_no;
    }
    public String getPay_code() {
        return pay_code;
    }
    public void setPay_code(String pay_code) {
        this.pay_code = pay_code;
    }
    public String getPay_name() {
        return pay_name;
    }
    public void setPay_name(String pay_name) {
        this.pay_name = pay_name;
    }
    public List<DingDanGoodsDto> getGoodsList() {
        return goodsList;
    }
    public void setGoodsList(List<DingDanGoodsDto> goodsList) {
        this.goodsList = goodsList;
    }
}
