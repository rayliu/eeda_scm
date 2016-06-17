package controllers.oms.custom.dto;

public class QueryBillDto {
    private String order_no;
    private String logistics_no;
    public String getOrder_no() {
        return order_no;
    }
    public void setOrder_no(String orderNo) {
        this.order_no = orderNo;
    }
    public String getLogistics_no() {
        return logistics_no;
    }
    public void setLogistics_no(String logistics_no) {
        this.logistics_no = logistics_no;
    }
}
