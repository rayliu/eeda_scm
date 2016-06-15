package controllers.oms.custom.dto;

public class QueryBillDto {
    private String orderNo;
    private String logistics_no;
    public String getOrderNo() {
        return orderNo;
    }
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    public String getLogistics_no() {
        return logistics_no;
    }
    public void setLogistics_no(String logistics_no) {
        this.logistics_no = logistics_no;
    }
}
