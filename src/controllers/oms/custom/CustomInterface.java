package controllers.oms.custom;

import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.QueryDingDanDto;
import controllers.oms.custom.dto.YunDanDto;

public interface CustomInterface {
    public String sendYunDan(YunDanDto dto);
    
    public String sendDingDan(DingDanDto dto);
    
    public String queryDingDan(QueryDingDanDto dto);
    
}
