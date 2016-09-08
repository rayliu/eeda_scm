package controllers.oms.importOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import models.Location;
import models.Office;
import models.Party;
import models.TransferOrder;
import models.TransferOrderItem;
import models.TransferOrderItemDetail;
import models.TransferOrderMilestone;
import models.UserLogin;
import models.eeda.oms.GateInOrder;
import models.eeda.oms.GateOutOrder;
import models.eeda.oms.GateOutOrderItem;
import models.eeda.oms.InspectionOrder;
import models.eeda.oms.InspectionOrderItem;
import models.eeda.oms.Inventory;
import models.eeda.oms.MoveOrder;
import models.eeda.oms.MoveOrderItem;
import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.oms.WaveOrderItem;
import models.eeda.profile.CustomCompany;
import models.eeda.profile.Product;
import models.eeda.profile.Warehouse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

import controllers.oms.custom.dto.DingDanDto;
import controllers.oms.custom.dto.DingDanGoodsDto;
import controllers.profile.LoginUserController;
import controllers.util.DbUtils;
import controllers.util.EedaHttpKit;
import controllers.util.MD5Util;
import controllers.util.OrderNoGenerator;
import controllers.util.ReaderXLS;
import controllers.util.ReaderXlSX;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class CheckOrder extends Controller {
	private Logger logger = Logger.getLogger(CheckOrder.class);
	Subject currentUser = SecurityUtils.getSubject();
	
	
	/**
	 * 校验表头是否和数据库的相符
	 * @param title
	 * @param execlType
	 * @return
	 */
	public boolean checkoutExeclTitle(String[] title, String execlType) {
		boolean flag = true;
		List<Record> dbTitleList = Db.find("select execl_title from execl_title where execl_type = ? ", execlType);
		if (dbTitleList != null) {
			// 判断总数是否相等
			if (dbTitleList.size() != title.length) {
				flag = false;
			}else{
				// 判断是否所有列标题一致
				List<String> titleList = new ArrayList<String>(dbTitleList.size());
				for (Record record : dbTitleList) {
					titleList.add(record.getStr("execl_title").trim());
				}
				
				for (int i = 0; i < title.length; i++) {
					String excelTitle = title[i];
					if (!titleList.contains(excelTitle.trim())) {
						flag = false;
					}
				}
			}
		}
		
		return flag;
	}
	

	/**
	 * 检验数据：同一张单的数据是否有隔单（中间还存在其他单据数据）现象
	 * 
	 * @param content
	 * @return
	 */
	private Map<String, String> validatingOrderNo(
			List<Map<String, String>> content) {
		Map<String, String> importResult = new HashMap<String, String>();
		try {
			importResult.put("result", "true");
			importResult.put("cause", "验证数据成功");
			List<String> orderNoList = new ArrayList<String>();
			Set<String> orderNoSet = new HashSet<String>();
			for (int j = 0; j < content.size(); j++) {
				orderNoList.add(content.get(j).get("客户订单号"));
				orderNoSet.add(content.get(j).get("客户订单号"));
			}
			Iterator orderNos = orderNoSet.iterator();// 先迭代出来
			while (orderNos.hasNext()) {// 遍历
				boolean flag = false;
				String orderNo = orderNos.next().toString();
				int firstIndex = orderNoList.indexOf(orderNo);
				int lastIndex = orderNoList.lastIndexOf(orderNo);
				System.out.println("单号：" + orderNo + ",第一次出现位置：" + firstIndex
						+ ",最后一次出现位置：" + lastIndex);
				for (int i = firstIndex; i <= lastIndex; i++) {
					if (!orderNoList.get(i).equals(orderNo)) {
						importResult.put("result", "false");
						importResult.put("cause",
								"验证数据失败，同一张单号数据中:订单号不允许有断开存放的现象，而在第"
										+ (lastIndex + 2) + "行【客户订单号】列有和第"
										+ (firstIndex + 2) + "到第" + (i + 1)
										+ "行相同客户单号存在");
						flag = true;
						break;
					}
				}
				if (flag) {
					break;
				}
			}
		} catch (Exception e) {
			importResult.put("result", "false");
			importResult.put("cause", "验证数据出错，请重新整理文件数据后在导入");
		}
		return importResult;
	}



	/**
	 * 校验是否为double类型
	 * @param value
	 * @return
	 */
	public boolean checkDouble(String value){
		boolean flag = true;
		for (int i = value.length();--i>=0;){    
		   if (!Character.isDigit(value.charAt(i))){  
			  flag = false;  
		   }  
	    }  
	    return flag;
	}
	
	
	/**
	 * upc条码校验
	 * @param value
	 * @return
	 */
	 
	public boolean checkUpc(String value){
		boolean flag = true;
		Product p = Product.dao.findFirst("select * from product where serial_no = ?",value);
		if(p == null){
			flag = false;  
		}
	    return flag;
	}
	


	/**
	 *  收货人地区编码
	 * @param value
	 * @return
	 */
	 
	public boolean checkLocation (String value){
		boolean flag = true;
		if(value.length()<20){
			flag = false;
		}else{
			String[] values = value.split("#");
			String province = values[0];
			String city = values[1];
			String qv = values[2];
			Location lo = Location.dao.findFirst("select * from location where code = ? and pcode = ?",city,province);
			if(lo == null){
				flag = false;
			}
			
			Location lo2 = Location.dao.findFirst("select * from location where (code = ? and pcode = ?) or (code = ? and pcode = ?)",qv,city,qv,province);
			if(lo2 == null){
				flag = false;
			}
		}
	    return flag;
	}
	
	/**
	 * 订单编号重复校验
	 */
	public boolean checkOrderNo (String value){
		boolean flag = true;
		
		GateOutOrder goo = GateOutOrder.dao.findFirst("select * from gate_out_order where customer_refer_no = ?",value);
		if(goo != null){
			flag = false;
		}
	    return flag;
	}
	
	/**
	 * 数据校验
	 * @param lines
	 * @return
	 */
	@Before(Tx.class)
	public Record importCheck( List<Map<String, String>> lines) {
		Record result = new Record();
		result.set("result",true);
		//importResult = validatingOrderNo(lines);校验是否存在隔单
		//if ("true".equals(importResult.get("result"))) {
			int rowNumber = 1;
			try {
				for (Map<String, String> line :lines) {
					String upc = line.get("商品条码（UPC）").trim();
					String cargo_name = line.get("中文名称").trim();
					String amount = line.get("商品数量").trim();
					String consignee = line.get("收货人姓名").trim();
					String consignee_telephone = line.get("收货人电话").trim();
					String consignee_address = line.get("收货人详细地址").trim();
					String location = line.get("收货人地区编码").trim();
					String order_no = line.get("订单编号").trim();
					
					
					if(StringUtils.isNotEmpty(amount)){
						if(!checkDouble(amount)){
							throw new Exception("【商品数量】("+amount+")格式类型有误");
						}
					}else{
						throw new Exception("【商品数量】不能为空");
					}
					
					if(StringUtils.isNotEmpty(upc)){
						if(!checkUpc(upc)){
							throw new Exception("【商品条码（UPC）】("+upc+")有误，系统产品信息不存在此upc");
						}
					}else{
						throw new Exception("【商品条码(UPC)】不能为空");
					}
					
					if(StringUtils.isNotEmpty(location)){
						if(!checkLocation(location)){
							throw new Exception("【收货人地区编码】("+location+")有误，找不到对应编码的城市，请参考标准城市编码");
						}
					}
					
					if(StringUtils.isNotEmpty(order_no)){
						if(!checkOrderNo(order_no)){
							throw new Exception("此【订单编号】("+order_no+")已存在，请核实是否有重复导入");
						}
					}
					
					if(StringUtils.isEmpty(consignee)){
						throw new Exception("【收货人姓名】不能为空");
					}
					if(StringUtils.isEmpty(consignee_telephone)){
						throw new Exception("【收货人电话】不能为空");			
					}
					if(StringUtils.isEmpty(consignee_address)){
						throw new Exception("【收货人详细地址】不能为空");
					}
					
					rowNumber++;
				}
			} catch (Exception e) {
				System.out.println("导入操作异常！");
				System.out.println(e.getMessage());

				result.set("result", false);
				
				result.set("cause", "校验失败<br/>"+"数据校验至第" + (rowNumber)
							+ "行时出现异常:" + e.getMessage() );
				
				return result;
			} 
		return result;
	}
	
	
	
	@Before(Tx.class)
	public Record importValue( List<Map<String, String>> lines) {
		Record result = new Record();
		result.set("result",true);
		
		//importResult = validatingOrderNo(lines);校验是否存在隔单
		//if ("true".equals(importResult.get("result"))) {
		int rowNumber = 1;
		String name = (String) currentUser.getPrincipal();
		List<UserLogin> users = UserLogin.dao
				.find("select * from user_login where user_name='" + name + "'");
		long user_id = users.get(0).getLong("id");
		try {
			for (Map<String, String> line :lines) {
				String order_no = line.get("订单编号").trim();
				String upc = line.get("商品条码（UPC）").trim();
				String cargo_name = line.get("中文名称").trim();
				String amount = line.get("商品数量").trim();
				String consignee = line.get("收货人姓名").trim();
				String consignee_telephone = line.get("收货人电话").trim();
				String consignee_address = line.get("收货人详细地址").trim();
				
				String location = line.get("收货人地区编码").trim();
				String consignee_id = line.get("身份证号码").trim();
				String express_no = line.get("快递信息").trim();

				//默认值带入
				GateOutOrder goo = new GateOutOrder();
				goo.set("warehouse_id", 52);  //前海保税区仓库
				goo.set("customer_id", 3);  //候鸟电商
				goo.set("consignee_country", 142);  //身份证
				goo.set("order_no", OrderNoGenerator.getNextOrderNo("CK"));  //身份证
				goo.set("status", "暂存");  //身份证
				goo.set("create_by", user_id);  //身份证
				goo.set("create_stamp", new Date());  //身份证
				goo.set("remark", "导入数据");  //身份证
				
				if(StringUtils.isNotEmpty(location)){
					String[] values = location.split("#");
					String province = values[0];//省
					String city = values[1];//市
					String qv = values[2];//区
					
					goo.set("location", qv);
				}
				
				if(StringUtils.isNotEmpty(order_no)){
					goo.set("customer_refer_no", order_no);
				}
				if(StringUtils.isNotEmpty(consignee)){
					goo.set("consignee", consignee);
				}
				if(StringUtils.isNotEmpty(consignee_telephone)){
					goo.set("consignee_telephone", consignee_telephone);
				}
				if(StringUtils.isNotEmpty(consignee_address)){
					goo.set("consignee_address", consignee_address);
				}
				if(StringUtils.isNotEmpty(consignee_id)){
					goo.set("consignee_id", consignee_id);
				}
				if(StringUtils.isNotEmpty(express_no)){
					goo.set("express_no", express_no);
				}
				goo.save();	
				
				//字表数据保存
				GateOutOrderItem gooi = new GateOutOrderItem();
				if(StringUtils.isNotEmpty(upc)){
					gooi.set("bar_code", upc);
				}
				if(StringUtils.isNotEmpty(cargo_name)){
					gooi.set("cargo_name", cargo_name);
				}
				if(StringUtils.isNotEmpty(amount)){
					gooi.set("packing_amount", amount);
				}
				gooi.set("order_id",goo.get("id"));
				gooi.save();
				rowNumber++;
			}
			result.set("cause","成功导入( "+(rowNumber-1)+" )条数据！");
		} catch (Exception e) {
			System.out.println("导入操作异常！");
			System.out.println(e.getMessage());
			e.printStackTrace();

			result.set("result", false);
			
			result.set("cause", "导入失败<br/>数据导入至第" + (rowNumber)
						+ "行时出现异常:" + e.getMessage() + "<br/>导入数据已取消！");
			
		} 
		
		return result;
	}


	
	
}
