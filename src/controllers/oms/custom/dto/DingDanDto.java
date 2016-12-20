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
    private Double goods_value;       //（不含税）商品明细总价之和
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
    private String is_pay_pass;   //是否已支付完成（1是，0否，默认0）
    private String pass_pay_no;
    private String pay_code;
    private String pay_name;
    
    private Double discount;         //非现金抵扣金额，无则为0
    private Double tax_total;        //代扣税款，无则为0
    private Double actural_paid;     //代扣税款，无则为0
    private String buyer_regno;      //订购人注册号
    private String buyer_name;       //订购姓名
    private String buyer_id_number;  //订购身份证号
    private String is_order_pass;    //是否已完成订单申报  
    private String pay_transaction_id;      //支付交易编号
    private String pay_time;         //支付时间
    private String batch_numbers;    //商品批次号
    private String wh_org_code;      //企业组织结构代码（仓储企业）
    
    //运单
    private String logistics_no;   //运单号
    private String goods_info;
    private int insure_fee;  //保价费
    private double weight;
    private double net_weight;
    private int pack_no;  //包裹数
    private String cop_no; //企业内部表示单证的编号
    private String assure_code; //担保企业编号
    private String sign_company;  //承运企业海关备案号
    private String sign_company_name;  //承运企业名称
    private String ems_no;
    private String decl_time; // 申报日期
    private String customs_code;
    private String ciq_code;
    private String port_code;
    private String ie_date;
    private String trade_mode;
    private String business_mode;//业务模式代码
    private String traf_mode; //运输方式
    private String traf_no;  //运输工具编号（可空）
    private String ship_name;
    private String voyage_no;  //航班航次号（可空）
    private String bill_no;  //提运单号（可空）
    private String supervision_code;//（可空）监管场所代码
    private String country_code;//起运国
    private String wrap_type;//包装种类
    
    public String getLogistics_no() {
		return logistics_no;
	}
	public void setLogistics_no(String logistics_no) {
		this.logistics_no = logistics_no;
	}
	public String getGoods_info() {
		return goods_info;
	}
	public void setGoods_info(String goods_info) {
		this.goods_info = goods_info;
	}
	public int getInsure_fee() {
		return insure_fee;
	}
	public void setInsure_fee(int insure_fee) {
		this.insure_fee = insure_fee;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getNet_weight() {
		return net_weight;
	}
	public void setNet_weight(double net_weight) {
		this.net_weight = net_weight;
	}
	public int getPack_no() {
		return pack_no;
	}
	public void setPack_no(int pack_no) {
		this.pack_no = pack_no;
	}
	public String getCop_no() {
		return cop_no;
	}
	public void setCop_no(String cop_no) {
		this.cop_no = cop_no;
	}
	public String getAssure_code() {
		return assure_code;
	}
	public void setAssure_code(String assure_code) {
		this.assure_code = assure_code;
	}
	public String getSign_company() {
		return sign_company;
	}
	public void setSign_company(String sign_company) {
		this.sign_company = sign_company;
	}
	public String getSign_company_name() {
		return sign_company_name;
	}
	public void setSign_company_name(String sign_company_name) {
		this.sign_company_name = sign_company_name;
	}
	public String getEms_no() {
		return ems_no;
	}
	public void setEms_no(String ems_no) {
		this.ems_no = ems_no;
	}
	public String getDecl_time() {
		return decl_time;
	}
	public void setDecl_time(String decl_time) {
		this.decl_time = decl_time;
	}
	public String getCustoms_code() {
		return customs_code;
	}
	public void setCustoms_code(String customs_code) {
		this.customs_code = customs_code;
	}
	public String getCiq_code() {
		return ciq_code;
	}
	public void setCiq_code(String ciq_code) {
		this.ciq_code = ciq_code;
	}
	public String getPort_code() {
		return port_code;
	}
	public void setPort_code(String port_code) {
		this.port_code = port_code;
	}
	public String getIe_date() {
		return ie_date;
	}
	public void setIe_date(String ie_date) {
		this.ie_date = ie_date;
	}
	public String getBusiness_mode() {
		return business_mode;
	}
	public void setBusiness_mode(String business_mode) {
		this.business_mode = business_mode;
	}
	public String getTraf_mode() {
		return traf_mode;
	}
	public void setTraf_mode(String traf_mode) {
		this.traf_mode = traf_mode;
	}
	public String getTraf_no() {
		return traf_no;
	}
	public void setTraf_no(String traf_no) {
		this.traf_no = traf_no;
	}
	public String getShip_name() {
		return ship_name;
	}
	public void setShip_name(String ship_name) {
		this.ship_name = ship_name;
	}
	public String getVoyage_no() {
		return voyage_no;
	}
	public void setVoyage_no(String voyage_no) {
		this.voyage_no = voyage_no;
	}
	public String getBill_no() {
		return bill_no;
	}
	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}
	public String getSupervision_code() {
		return supervision_code;
	}
	public void setSupervision_code(String supervision_code) {
		this.supervision_code = supervision_code;
	}
	public String getCountry_code() {
		return country_code;
	}
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}
	public String getWrap_type() {
		return wrap_type;
	}
	public void setWrap_type(String wrap_type) {
		this.wrap_type = wrap_type;
	}
	
    
    private List<DingDanGoodsDto> goodslist;
    
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
    public List<DingDanGoodsDto> getGoodslist() {
        return goodslist;
    }
    public void setGoodslist(List<DingDanGoodsDto> goodslist) {
        this.goodslist = goodslist;
    }
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public Double getTax_total() {
		return tax_total;
	}
	public void setTax_total(Double tax_total) {
		this.tax_total = tax_total;
	}
	public Double getActural_paid() {
		return actural_paid;
	}
	public void setActural_paid(Double actural_paid) {
		this.actural_paid = actural_paid;
	}
	public String getBuyer_regno() {
		return buyer_regno;
	}
	public void setBuyer_regno(String buyer_regno) {
		this.buyer_regno = buyer_regno;
	}
	public String getBuyer_id_number() {
		return buyer_id_number;
	}
	public void setBuyer_id_number(String buyer_id_number) {
		this.buyer_id_number = buyer_id_number;
	}
	public String getBuyer_name() {
		return buyer_name;
	}
	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}
	public String getIs_order_pass() {
		return is_order_pass;
	}
	public void setIs_order_pass(String is_order_pass) {
		this.is_order_pass = is_order_pass;
	}
	public String getPay_transaction_id() {
		return pay_transaction_id;
	}
	public void setPay_transaction_id(String pay_transaction_id) {
		this.pay_transaction_id = pay_transaction_id;
	}
	public String getPay_time() {
		return pay_time;
	}
	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}
	public String getBatch_numbers() {
		return batch_numbers;
	}
	public void setBatch_numbers(String batch_numbers) {
		this.batch_numbers = batch_numbers;
	}
	public String getWh_org_code() {
		return wh_org_code;
	}
	public void setWh_org_code(String wh_org_code) {
		this.wh_org_code = wh_org_code;
	}
	public String getTrade_mode() {
		return trade_mode;
	}
	public void setTrade_mode(String trade_mode) {
		this.trade_mode = trade_mode;
	}
}
