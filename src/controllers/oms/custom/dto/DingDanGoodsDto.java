package controllers.oms.custom.dto;


public class DingDanGoodsDto {
    private String item_no;
    private String cus_item_no;
    private String currency;
   
    private String unit;
    private String unit1;
    private double qty;
    private double qty1;
    private double price;
    
  //private double total;//-------------------删掉
    private double total_price;//不含税
    private String gift_flag;
    
    private String item_name;
    private String item_describe;
    private String barcode;
    private String country;
    private String gcode;  //税号
    private String g_model; 
    private String ciq_gno; //检验检疫商品备案号
    private String ciq_gmodel; //检验检疫商品规格型号
    private String brand; // 没有填无
    private String note; //(可空)
    
    
    public String getUnit1() {
		return unit1;
	}
	public void setUnit1(String unit1) {
		this.unit1 = unit1;
	}
	public double getQty1() {
		return qty1;
	}
	public void setQty1(double qty1) {
		this.qty1 = qty1;
	}
	public double getTotal_price() {
		return total_price;
	}
	public void setTotal_price(double total_price) {
		this.total_price = total_price;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getItem_describe() {
		return item_describe;
	}
	public void setItem_describe(String item_describe) {
		this.item_describe = item_describe;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getGcode() {
		return gcode;
	}
	public void setGcode(String gcode) {
		this.gcode = gcode;
	}
	public String getG_model() {
		return g_model;
	}
	public void setG_model(String g_model) {
		this.g_model = g_model;
	}
	public String getCiq_gno() {
		return ciq_gno;
	}
	public void setCiq_gno(String ciq_gno) {
		this.ciq_gno = ciq_gno;
	}
	public String getCiq_gmodel() {
		return ciq_gmodel;
	}
	public void setCiq_gmodel(String ciq_gmodel) {
		this.ciq_gmodel = ciq_gmodel;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
    public String getItem_no() {
        return item_no;
    }
    public void setItem_no(String item_no) {
        this.item_no = item_no;
    }
    public String getCus_item_no() {
        return cus_item_no;
    }
    public void setCus_item_no(String cus_item_no) {
        this.cus_item_no = cus_item_no;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public double getQty() {
        return qty;
    }
    public void setQty(double qty) {
        this.qty = qty;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getGift_flag() {
        return gift_flag;
    }
    public void setGift_flag(String gift_flag) {
        this.gift_flag = gift_flag;
    }
   
}
