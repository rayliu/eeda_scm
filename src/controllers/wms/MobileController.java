package controllers.wms;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import models.UserLogin;
import models.eeda.oms.GateOutOrder;
import models.eeda.oms.Inventory;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.tx.TxConfig;

import controllers.profile.LoginUserController;

public class MobileController extends Controller {
    private Logger logger = Logger.getLogger(MobileController.class);
    Subject currentUser = SecurityUtils.getSubject();
    
    public void getUserInfo(){
        UserLogin user = LoginUserController.getLoginUser(this);
        renderJson(user);
    }
    //拣货, 查询波次单
    public void searchWaveOrder() {
        String barcode = getPara();
        String sql = "select order_no, status from wave_order WHERE order_no= ?";
        Record rec = Db.findFirst(sql, barcode);

        if (rec != null) {
            renderJson(rec);
        } else {
            rec = new Record();
            rec.set("order_no", "null");
            renderJson(rec);
        }
    }
    //拣货, 标记波次单已开始拣货
    public void startPickup() {
        String orderNo = getPara();
        String sql = "update wave_order set status='拣货中' WHERE order_no= ?";
        int count = Db.update(sql, orderNo);

        Record rec = new Record();
        if (count == 1) {
            rec = firstPickup(orderNo);
            renderJson(rec);
        } else {
            rec.set("status", "fail");
            renderJson(rec);
        }
    }
    //拣货, 查询波次单第一个商品
    private Record firstPickup(String orderNo) {
        String pickupSql = "select wo.order_no, p.item_name, woi.* from wave_order_item woi "
                + " left join wave_order wo on woi.order_id = wo.id "
                + " left join product p on woi.cargo_bar_code = p.serial_no "
                + " where woi.pickup_flag='N' and wo.order_no=? order by woi.shelves";
        Record orderRec = Db.findFirst(pickupSql, orderNo);
        if (orderRec != null) {
            return orderRec;
        } else {
            orderRec = new Record();
            orderRec.set("status", "done");
            return orderRec;
        }
    }
    //拣货, 查询波次单下一个商品
    @Before(Tx.class)
    public void nextPickup() {
        String barcode = getPara("barcode");
        String orderNo = getPara("orderNo");
        String pickupFlag = getPara("pickupFlag");
        String itemId = getPara("itemId");
        
        Db.update("update wave_order_item set pickup_flag='"+pickupFlag+"' where id=?", barcode, itemId);
        
        String pickupSql = "select wo.order_no, p.item_name, woi.* from wave_order_item woi "
                + " left join wave_order wo on woi.order_id = wo.id "
                + " left join product p on woi.cargo_bar_code = p.serial_no "
                + " where woi.pickup_flag='N' and wo.order_no=? order by woi.shelves";
        Record orderRec = Db.findFirst(pickupSql, orderNo);
        if (orderRec != null) {
            renderJson(orderRec);
        } else {
            orderRec = new Record();
            orderRec.set("order_no", "done");
            
            //当拣完货后更新波次单状态
            Db.update("update wave_order set status='已完成' where order_no = ?", orderNo);
            
            renderJson(orderRec);
        }
    }
    
    //上架, 查询(验货单)商品条码推荐库位
    public void putOnSearchBarcode() {
        String barcode = getPara();

        String sql = "select ioi.bar_code, ioi.shelves, gii.cargo_name from inspection_order_item ioi"
            +" left join inspection_order io on ioi.order_id=io.id "
            +" left join gate_in_order_item gii on io.gate_in_order_id = gii.order_id"
            +" WHERE shelves is null and bar_code= ?";
        Record rec = Db.findFirst(sql, barcode);
        if (rec != null) {
            renderJson(rec);
        } else {
            rec = new Record();
            rec.set("bar_code", "null");
            renderJson(rec);
        }
    }
    
    //上架确认, 记录商品条码, 数量, 库位
    @Before(Tx.class)
    public void putOnConfirm() {
        String barcode = getPara("barcode");
        String cargoName = getPara("cargoName");
        String shelves = getPara("shelves");
        Integer amount = getParaToInt("amount");
        String userId = getPara("userId");
        String sql = " insert into put_on_item_log(barcode, cargo_name, shelves, amount, creator_id, create_stamp) values (?, ?, ?, ?, ?, ?)";
        int count= Db.update(sql, barcode, cargoName, shelves, amount, userId, new Date());
        
        //更新库存数据
        int flag = updateInvShelves(barcode, cargoName, shelves, amount, userId);
        
        Record rec = new Record();
        if (count>0 && flag>0) {
            rec.set("status", "ok");
            renderJson(rec);
        } else {
        	if(flag<0)
        		rec.set("msg", "上架数量超过了验货数量, 不能上架!");
            rec.set("status", "fail");
            renderJson(rec);
        }
    }
    
    public int updateInvShelves(String barcode, String cargoName, String shelves, Integer amount, String userId){
    	int flag = 1;
		amount = ((int)(amount*100))/100;
		String sql = "select * from inventory inv where shelves is null and cargo_barcode = ? limit 0,?";
		List<Inventory> invs = Inventory.dao.find(sql,barcode,amount);
		
		if(invs.size() == amount){
    		for(Inventory inv : invs){
    			inv.set("shelves", shelves);
        		inv.set("onshelves_stamp", new Date());
        		inv.update();
    		}
		}else{
			flag = -1;
		}
		return flag;
    }
    
    //盘点, 查产品
    public void icSearchBarcode(){
        String barcode = getPara("barcode");
        String sql = "select * from product  WHERE serial_no is not null and serial_no= ?";
            Record rec = Db.findFirst(sql, barcode);
            if (rec != null) {
                renderJson(rec);
            } else {
                rec = new Record();
                rec.set("bar_code", "null");
                renderJson(rec);
            }
    }
    
    //盘点确认, 记录商品条码, 数量, 库位\
    @Before(Tx.class)
    public void invCheckConfirm() {
    	String ivnOrderNo = getPara("ivnOrderNo");
        String barcode = getPara("barcode");
        String cargoName = getPara("cargoName");
        String shelves = getPara("shelves");
        Integer amount = getParaToInt("amount");
        String userId = getPara("userId");
        String sql = " insert into inventory_check_item_log(barcode, cargo_name, shelves, amount, creator_id, create_stamp) values (?, ?, ?, ?, ?, ?)";
        int count= Db.update(sql, barcode, cargoName, shelves, amount, userId, new Date());
        
        int flag = updateInvOrderItem(ivnOrderNo,barcode,shelves,amount);
        
        Record rec = new Record();
        if (count>0 && flag >0) {
            rec.set("status", "ok");
            renderJson(rec);
        } else {
        	if(flag<0)
        		rec.set("msg", "该库位不存在此商品！");
            rec.set("status", "fail");
            renderJson(rec);
        }
    }
    
    public int updateInvOrderItem(String ivnOrderNo,String barcode,String shelves,Integer amount){
    	int flag = 1;
		String sql = "select * from inventory_order invo "
				+ " left join inventory_order_item ioi on ioi.order_id = invo.id"
				+ " where invo.order_no =? and ioi.cargo_code = ? and shelves = ?";
		List<Inventory> invs = Inventory.dao.find(sql,ivnOrderNo,barcode);
		
		if(invs.size()>0){
    		for(Inventory inv : invs){
    			inv.set("check_amount", amount);
        		inv.set("check_stamp", new Date());
        		inv.update();
    		}
		}else{
			flag = -1;
		}
		return flag;
    }
    
    
    
    //移库确认, 记录商品条码, 数量, 库位
    public void invTransferConfirm() {
        String barcode = getPara("cargoBarcode");
        String fromShelves = getPara("fromShelves");
        String toShelves = getPara("fromShelves");
        Integer amount = getParaToInt("amount");
        String userId = getPara("userId");
        String sql = " insert into inventory_transfer_item_log(barcode, from_shelves, to_shelves, amount, creator_id, create_stamp) values (?, ?, ?, ?, ?, ?)";
        int count= Db.update(sql, barcode, fromShelves, toShelves, amount, userId, new Date());
        
        int flag = updateShelves(barcode,fromShelves,toShelves,amount);
        
        Record rec = new Record();
        if (count>0 && flag >0) {
            rec.set("status", "ok");
            renderJson(rec);
            renderJson(rec);
        } else {
            rec.set("status", "fail");
            renderJson(rec);
        }
    }
    
    public void searchInvCheckOrder(){
        String sql = "select order_no from inventory_order where status='新建'";
        List<Record> recs = Db.find(sql);
        if (recs != null) {
            renderJson(recs);
        } else {
            recs = Collections.EMPTY_LIST;
            renderJson(recs);
        }
    }
    public void startInvCheck() {
        String orderNo = getPara("orderNo");
        String sql = "update inventory_order set status='盘点中' WHERE order_no=?";
        int count = Db.update(sql, orderNo);

        
        Record rec = new Record();
        if (count == 1) {
            Record orderRec = Db.findFirst("select * from inventory_order where order_no=?", orderNo);
            rec.set("status", "ok");
            rec.set("shelve_range", orderRec.get("from_shelve")+" ~ "+orderRec.get("to_shelve"));
            renderJson(rec);
        } else {
            rec.set("status", "fail");
            renderJson(rec);
        }

    }
    
    public void invCheckNextItem() {
        String invCheckOrderNo = getPara("invCheckOrderNo");
        String barcode = getPara("cargoBarcode");
        String shelves = getPara("shelve");
        Integer amount = getParaToInt("amount");
        String userId = getPara("userId");
        
        String querySql = "select * from inventory_check_item_log where order_no=? and barcode=? and shelves=? and creator_id=?";
        Record logRec = Db.findFirst(querySql, invCheckOrderNo, barcode, shelves, userId);
        int count= 0;
        if(logRec!=null){
            count = Db.update("update inventory_check_item_log set amount=? where order_no=? and barcode=? and shelves=? and creator_id=?",
                    amount, invCheckOrderNo, barcode, shelves, userId);
        }else{
            String sql = " insert into inventory_check_item_log(order_no, barcode, shelves, amount, creator_id, create_stamp) values (?, ?, ?, ?, ?, ?)";
            count = Db.update(sql, invCheckOrderNo, barcode, shelves, amount, userId, new Date());
        }
        Record rec = new Record();
        if (count>0) {
            rec.set("status", "ok");
            renderJson(rec);
        } else {
            rec.set("status", "fail");
            renderJson(rec);
        }
    }
    
    public void invCheckPreviousItem() {
        String invCheckOrderNo = getPara("orderNo");
        String userId = getPara("userId");
        String sql = "select l.*, p.item_name from inventory_check_item_log l"
                + " left join product p on l.barcode = p.serial_no"
                + " where l.order_no=? and l.creator_id=? order by id desc";
        Record rec = Db.findFirst(sql, invCheckOrderNo, userId);
        
        if (rec!=null) {
            renderJson(rec);
        } else {
            rec = new Record();
            rec.set("status", "fail");
            renderJson(rec);
        }

    }
    public void endInvCheck() {
        String orderNo = getPara("orderNo");
        String sql = "update inventory_order set status='已完成' WHERE order_no=?";
        int count = Db.update(sql, orderNo);
        
        Record rec = new Record();
        if (count == 1) {
            rec.set("status", "ok");
        } else {
            rec.set("status", "fail");
            renderJson(rec);
        }
    }
    
    public int updateShelves(String cargoBarcode,String fromShelves,String toShelves,Integer amount){
		String sql = "select * from inventory inv where lock_amount = 0 cargo_barcode = ?  and shelves=? and (gate_in_amount - gate_out_amount) > 0  limit 0,?";
		List<Inventory> invs = Inventory.dao.find(sql,cargoBarcode,fromShelves,amount);
		int flag = 1;
		if(invs.size()==amount){
			for(Inventory inv : invs){
	    		inv.set("shelves", toShelves);
	    		inv.update();
			}
		}else{
			flag = -1;
		}
		return flag;
    }

}
