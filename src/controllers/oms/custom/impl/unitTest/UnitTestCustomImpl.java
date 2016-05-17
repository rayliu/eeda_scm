package controllers.oms.custom.impl.unitTest;

import controllers.oms.custom.CustomInterface;
import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.QueryDingDanDto;
import controllers.oms.custom.dto.YunDanDto;

public class UnitTestCustomImpl implements CustomInterface {

    @Override
    public String sendYunDan(YunDanDto dto) {
        //post to http://localhost:8080/tgt/interface/addOrders
        return null;
    }

    @Override
    public String sendDingDan(DingDanDto dto) {
      //post to http://localhost:8080/tgt/interface/addLogistics
        return null;
    }

    @Override
    public String queryDingDan(QueryDingDanDto dto) {
      //post to http://localhost:8080/tgt/interface/findOrders
        return null;
    }

}
