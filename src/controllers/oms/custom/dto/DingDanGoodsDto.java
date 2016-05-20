package controllers.oms.custom.dto;


public class DingDanGoodsDto {
    private String item_no;
    private String cus_item_no;
    private String unit;
    private String currency;
   
    private double qty;
    private double price;
    private double total;
    private String gift_flag;
    
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
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public String getGift_flag() {
        return gift_flag;
    }
    public void setGift_flag(String gift_flag) {
        this.gift_flag = gift_flag;
    }
   
}
