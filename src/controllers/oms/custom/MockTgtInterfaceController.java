package controllers.oms.custom;

import com.jfinal.core.Controller;
import com.jfinal.log.Logger;

import controllers.oms.custom.dto.SendReturnDto;


public class MockTgtInterfaceController extends Controller {

	private Logger logger = Logger.getLogger(MockTgtInterfaceController.class);
	
	//订单查询接口
    public void findOrders(){
        renderText("订单查询接口");
    }
    
	//订单
	public void addOrders(){ 
	    renderJson(new SendReturnDto());
	}
	
	//运单
	public void addLogistics(){
	    renderJson(new SendReturnDto());
    }
}
