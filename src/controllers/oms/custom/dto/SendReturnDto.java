package controllers.oms.custom.dto;

public class SendReturnDto {
    private int code = 0; //0为成功
    private String message="";
    
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
    
}
