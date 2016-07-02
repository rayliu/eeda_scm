package controllers.oms.custom.dto;

public class BaseItemDto {
    private String item_no;// 德丰系统商品货号
    private String item_name;// 德丰系统商品名称
    private String org_code;// 外部企业代码
    private String ref_item_no;// 外部电商商品货号
    private String custom_item_no;// 海关商品货号, 可与企业商品货号一致
    private String custom_list_item_no;// 海关正面清单货号（新规则时必填）
    private String unit;// 计量单位（标准代码，见参数表）1 =台
    private String currency;// 币制代码（标准代码，见参数表） 142 人民币
    private Double price;// 单价(元)
    private Integer length;// 长度(mm)
    private Integer width;// 宽度(mm)
    private Integer height;// 高度(mm)
    private Double volume;// 立方米
    private Double weight;// 重量(kg)
    
    public String getItem_no() {
        return item_no;
    }
    public void setItem_no(String item_no) {
        this.item_no = item_no;
    }
    public String getItem_name() {
        return item_name;
    }
    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
    public String getOrg_code() {
        return org_code;
    }
    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }
    public String getRef_item_no() {
        return ref_item_no;
    }
    public void setRef_item_no(String ref_item_no) {
        this.ref_item_no = ref_item_no;
    }
    public String getCustom_item_no() {
        return custom_item_no;
    }
    public void setCustom_item_no(String custom_item_no) {
        this.custom_item_no = custom_item_no;
    }
    public String getCustom_list_item_no() {
        return custom_list_item_no;
    }
    public void setCustom_list_item_no(String custom_list_item_no) {
        this.custom_list_item_no = custom_list_item_no;
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
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Integer getLength() {
        return length;
    }
    public void setLength(Integer length) {
        this.length = length;
    }
    public Integer getWidth() {
        return width;
    }
    public void setWidth(Integer width) {
        this.width = width;
    }
    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }
    public Double getVolume() {
        return volume;
    }
    public void setVolume(Double volume) {
        this.volume = volume;
    }
    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }

}
