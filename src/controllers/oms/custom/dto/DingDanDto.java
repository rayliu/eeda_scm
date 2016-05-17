package controllers.oms.custom.dto;

import java.math.BigDecimal;

public class DingDanDto {
    private String org_code;
    private String order_no;
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
    
    //在《Effective Java》这本书中提到这个原则，float和double只能用来做科学计算或者是工程计算，
    //在商业计算中我们要用 java.math.BigDecimal。
    private BigDecimal goods_value;
    private BigDecimal freight;
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
    private BigDecimal pro_amount;
    private String pro_remark;
    private String note;
    private String pay_no;
    private String pay_platform;
    private String payer_account;
    private String payer_name;
    private String is_pay_pass;
    private String pass_pay_no;
    private String pay_code;
    private String pay_name;
}
