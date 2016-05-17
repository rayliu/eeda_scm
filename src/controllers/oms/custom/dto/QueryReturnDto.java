package controllers.oms.custom.dto;

public class QueryReturnDto {
    private int code;
    private String message;
    private int total_count;
    private DingDanDto[] orders;
    
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public int getTotal_count() {
        return total_count;
    }
    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }
    public DingDanDto[] getOrders() {
        return orders;
    }
    public void setOrders(DingDanDto[] orders) {
        this.orders = orders;
    }
}
