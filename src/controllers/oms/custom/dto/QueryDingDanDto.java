package controllers.oms.custom.dto;

import java.util.List;

public class QueryDingDanDto {
    private int totalCount;
    private List<QueryBillDto> bills;
    
    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    public List<QueryBillDto> getBills() {
        return bills;
    }
    public void setBills(List<QueryBillDto> bills) {
        this.bills = bills;
    }
   
    
}
