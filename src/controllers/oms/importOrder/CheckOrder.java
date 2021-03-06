package controllers.oms.importOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Location;
import models.eeda.oms.GateOutOrder;
import models.eeda.oms.GateOutOrderItem;
import models.eeda.oms.LogisticsOrder;
import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.profile.Product;
import models.eeda.profile.Unit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import controllers.util.IdcardUtil;
import controllers.util.OrderNoGenerator;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class CheckOrder extends Controller {
	private Logger logger = Logger.getLogger(CheckOrder.class);
	Subject currentUser = SecurityUtils.getSubject();
	//Long user_id = null;
	
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
	public static boolean checkDouble(String value){
		boolean flag = true;
		for (int i = value.length();--i>=0;){    
		   if (!Character.isDigit(value.charAt(i)) && !String.valueOf(value.charAt(i)).equals(".")){  
			  flag = false;  
		   }  
	    }  
	    return flag;
	}
	
	
	/**
	 * 数字分割
	 * @param value
	 * @return
	 */
	public static String getDouble(String value){
		String number = null;
		for (int i = 0;i<value.length();i++){    
		   if (!Character.isDigit(value.charAt(i))){  
			   number = value.substring(i+1,value.length());  
		   }else{
			   return number==null?value:number;
		   }
	    }  
	    return number==null?value:number;
	}
	
	/**
	 * upc条码校验
	 * @param value
	 * @return
	 */
	public static boolean checkUpc(String value){
		boolean flag = true;
		Product p = Product.dao.findFirst("select * from product where serial_no = ?",value);
		if(p == null){
			flag = false;  
		}
	    return flag;
	}
	
	
	/**
	 * upc条码取出对应的值
	 * @param value
	 * @return
	 */
	public static String getCargoName(String value){
		String cargoName = null;
		Product p = Product.dao.findFirst("select * from product where serial_no = ?",value);
		if(p != null){
			cargoName = p.getStr("item_name");  
		}
	    return cargoName;
	}
	



	
	/**
	 * 订单编号重复校验
	 */
	public static boolean checkOrderNo (String value){
		boolean flag = true;
		
		GateOutOrder goo = GateOutOrder.dao.findFirst("select * from gate_out_order where customer_refer_no = ?",value);
		if(goo != null){
			flag = false;
		}
	    return flag;
	}
	
	/**
	 * 订单编号重复校验
	 */
	public static boolean checkRefOrderNo (String value){
		boolean flag = true;
		
		SalesOrder so = SalesOrder.dao.findFirst("select * from sales_order where order_no = ?",value);
		if(so != null){
			flag = false;
		}
	    return flag;
	}
	
	/**
	 * 单位校验
	 * @param value
	 * @return
	 */
	public static boolean checkUnit (String value){
		boolean flag = true;
		
		Unit goo = Unit.dao.findFirst("select * from unit where name = ?",value);
		if(goo == null){
			flag = false;
		}
	    return flag;
	}
	
	/**
	 * 单位Code校验
	 * @param value
	 * @return
	 */
	public static boolean checkUnitCode (String value){
		boolean flag = true;
		
		Unit goo = Unit.dao.findFirst("select * from unit where code = ?",value);
		if(goo == null){
			flag = false;
		}
	    return flag;
	}
	
	/**
	 * 日期格式校验
	 * @param value
	 * @return
	 */
	public static boolean checkDate(String dateValue) {    
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
        try{  
            Date date = (Date)formatter.parse(dateValue);   
            return dateValue.equals(formatter.format(date));  
        }catch(Exception e){  
            return false;  
        }  
    }  
	
	
	
	/**
	 * 
	 * 校验地址
	 * 
	 */
//	int i = 0;
//	public boolean checkAddress (String value){
//		boolean flag = true;
//		String city = null;
//		String province = value.substring(0, value.indexOf("省")+1);
//		if(StringUtils.isEmpty(province)){
//			province = value.substring(0, value.indexOf("市")+1); 
//			value = value.substring(value.indexOf("市")+1,value.length());
//			
//			if(StringUtils.isEmpty(province)){
//				province = value.substring(0, value.indexOf("自治区")+1); 
//				value = value.substring(value.indexOf("自治区")+1,value.length());
//			}
//		}else{
//			value = value.substring(value.indexOf("省")+1,value.length());
//		}
//		
//		city = value.substring(0, value.indexOf("市")+1);
//		value = value.substring(value.indexOf("市")+1,value.length());
//		
//		String qv = value.substring(0, value.indexOf("区")+1);
//		if(StringUtils.isEmpty(qv)){
//			qv = value.substring(0, value.indexOf("县")+1);
//		}
//		
//		System.out.println(province+city+qv);
//		
//		i++;
//		
//		if(i==8){
//			flag = false;
//		}
//	    return flag;
//	}
	
	/**
	 * 地区编码自动识别
	 */
	public static String changeAddres (String value){
		String [] valueArra = value.split(" ");
		String provinceCode = null;
		String cityCode = null;
		String qvCode = null;
		
		if(valueArra.length>=3){
			String province = valueArra[0];
		    String city = valueArra[1];
		    String qv = valueArra[2];
			
			List<Record> pros = Db.find("select * from location where name = ?",province);
			for(Record pro : pros){
				if("1".equals(pro.getStr("level"))){
					provinceCode = pro.getStr("code");//省
					
					List<Record> cis = Db.find("select * from location where name = ?",city);
					for(Record ci : cis){
						if(provinceCode.equals(ci.getStr("pcode"))){
							cityCode = ci.getStr("code");//市
							
							List<Record> qs = Db.find("select * from location where name = ?",qv);
							for(Record q : qs){
								if(q.getStr("pcode").equals(cityCode) || q.getStr("pcode").equals(provinceCode)){
									qvCode = q.getStr("code");//区
								}
							}
						}
					}
				}
			}	
		}
		
	    return provinceCode+"#"+cityCode+"#"+qvCode;
	}
	
	/**
	 *  收货人（转化后的）地区编码
	 * @param value
	 * @return
	 */
	public boolean checkLocation (String value){
		boolean flag = true;
		if(value.length()<20){
			flag = false;
		}
	    return flag;
	}

	
	/**
	 *  收货人(纯编码)地区编码
	 * @param value
	 * @return
	 */
    public static boolean checkCode (String value){
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
	 * 数据校验
	 * @param lines
	 * @return
	 */
	@Before(Tx.class)
	public Record importGOCheck( List<Map<String, String>> lines) {
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
						if(!checkCode(location)){
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
	public Record importGOValue( List<Map<String, String>> lines, long user_id, long office_id) {
		Connection conn = null;
		Record result = new Record();
		result.set("result",true);
		
		//importResult = validatingOrderNo(lines);校验是否存在隔单
		//if ("true".equals(importResult.get("result"))) {
		int rowNumber = 1;
		
		try {
			conn = DbKit.getConfig().getDataSource().getConnection();
			DbKit.getConfig().setThreadLocalConnection(conn);
			conn.setAutoCommit(false);// 自动提交变成false
			
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
			
			try {
				if (null != conn)
					conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			result.set("result", false);
			
			result.set("cause", "导入失败<br/>数据导入至第" + (rowNumber)
						+ "行时出现异常:" + e.getMessage() + "<br/>导入数据已取消！");
			
		} finally {
			try {
				if (null != conn) {
					conn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				DbKit.getConfig().removeThreadLocalConnection();
			}
		}
		
		return result;
	}
	
	
	/**
	 * 数据销售订单校验
	 * @param lines
	 * @return
	 */
	@Before(Tx.class)
	public Record importSOCheck( List<Map<String, String>> lines) {
		System.out.println("一共要导入"+lines.size()+"行数据");
		Record result = new Record();
		result.set("result",true);
		String errorMsg = "";
		int rowNumber = 1;
		try {
			for (Map<String, String> line :lines) {
				String order_no = line.get("订单编号")==null?null:line.get("订单编号").trim();
				String bar_code = line.get("商品条码")==null?null:line.get("商品条码").trim();
				String item_name = line.get("商品名称")==null?null:line.get("商品名称").trim();
				String item_no = line.get("商品货号")==null?null:line.get("商品货号").trim();
				String qty = line.get("商品数量")==null?null:line.get("商品数量").trim();
				String unit = line.get("计量单位")==null?null:line.get("计量单位").trim();
				String qty1 = line.get("法定计量单位数量")==null?null:line.get("法定计量单位数量").trim();
				String unit1 = line.get("法定计量单位")==null?null:line.get("法定计量单位").trim();
				String price = line.get("单价")==null?null:line.get("单价").trim(); 
				String tax_rate = line.get("税率")==null?null:line.get("税率").trim(); 
				String gcode = line.get("十位税号")==null?null:line.get("十位税号").trim(); 
				String g_model = line.get("品牌规格型号等")==null?null:line.get("品牌规格型号等").trim(); 
				String ciq_gno= line.get("检验检疫商品备案号")==null?null:line.get("检验检疫商品备案号").trim();
				//String ciq_gmodel = line.get("检验检疫商品规格型号")==null?null:line.get("检验检疫商品规格型号").trim();
				String brand = line.get("品牌")==null?null:line.get("品牌").trim();
				
				//String freight = line.get("运杂费")==null?null:line.get("运杂费").trim(); 
				String consignee = line.get("收货人姓名")==null?null:line.get("收货人姓名").trim();
				String consignee_telephone = line.get("收货人电话")==null?null:line.get("收货人电话").trim();
				String consignee_address = line.get("收货人地址")==null?null:line.get("收货人地址").trim();
				
				//String buyer_regno = line.get("订购人注册号")==null?null:line.get("订购人注册号").trim();
				//String buyer_name = line.get("订购人姓名")==null?null:line.get("订购人姓名").trim();
				String buyer_id_number = line.get("订购人身份证号")==null?null:line.get("订购人身份证号").trim();
				
				//String cop_no = line.get("企业内部标识单证编号")==null?null:line.get("企业内部标识单证编号").trim();
				//String insure_fee = line.get("保价费")==null?null:line.get("保价费").trim();
				String weight = line.get("毛重")==null?null:line.get("毛重").trim(); 
				String net_weight = line.get("净重")==null?null:line.get("净重").trim();
				
				String goods_info = line.get("主要货物信息")==null?null:line.get("主要货物信息").trim(); 
				String pack_no = line.get("包裹数")==null?null:line.get("包裹数").trim();  
				String wrap_type = line.get("包装种类")==null?null:line.get("包装种类").trim(); 
				String ie_date = line.get("进口日期")==null?null:line.get("进口日期").trim(); 
				String note = line.get("备注")==null?null:line.get("备注").trim();
				
				if(StringUtils.isNotEmpty(order_no)){
					if(!checkRefOrderNo(order_no)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:此【订单编号】("+order_no+")已存在，请核实是否有重复导入<br/><br/>");
					}
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【订单编号】不能为空<br/><br/>");
				}
				
				if(StringUtils.isNotEmpty(bar_code)){
					if(!checkUpc(bar_code)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【商品条码】("+bar_code+")有误，系统产品信息不存在此upc<br/><br/>");
					}
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【商品条码】不能为空<br/><br/>");
				}
				
				if(StringUtils.isEmpty(item_name)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【商品名称】不能为空<br/><br/>");
				}
				
				if(StringUtils.isEmpty(item_no)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【商品货号】不能为空<br/><br/>");
				}
				
				if(StringUtils.isNotEmpty(qty)){
					if(!checkDouble(qty)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【商品数量】("+qty+")格式类型有误<br/><br/>");
					}
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【商品数量】不能为空<br/><br/>");
				}
				
				if(StringUtils.isNotEmpty(qty1)){
					if(!checkDouble(qty1)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【法定计量单位数量】("+qty1+")格式类型有误<br/><br/>");
					}
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【法定计量单位数量】不能为空<br/><br/>");
				}
				
				if(StringUtils.isNotEmpty(unit)){
					if(!checkUnit(unit)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【单位】("+unit+")有误，系统不存在此单位名称，请联系管理员维护<br/><br/>");
					}
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【单位】不能为空<br/><br/>");
				}
				
				if(StringUtils.isNotEmpty(unit1)){
					if(!checkUnit(unit1)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【法定计量单位】("+unit1+")有误，系统不存在此单位名称，请联系管理员维护<br/><br/>");
					}
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【法定计量单位】不能为空<br/><br/>");
				}
				
				if(StringUtils.isNotEmpty(price)){
					if(!checkDouble(price)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【单价】("+price+")格式类型有误<br/><br/>");
					}
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【单价】不能为空<br/><br/>");
				}
				
				if(StringUtils.isNotEmpty(tax_rate)){
					if(!checkDouble(tax_rate)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【税率】("+tax_rate+")格式类型有误<br/><br/>");
					}
				}
				
				if(StringUtils.isEmpty(gcode)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【十位税号】不能为空<br/><br/>");
				}
				
				if(StringUtils.isEmpty(g_model)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【品牌规格型号等】不能为空<br/><br/>");
				}
				
				if(StringUtils.isEmpty(ciq_gno)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【检验检疫商品型号】不能为空<br/><br/>");
				}
				
				if(StringUtils.isEmpty(consignee)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【收货人姓名】不能为空<br/><br/>");
				}
				if(StringUtils.isEmpty(consignee_telephone)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【收货人电话】不能为空<br/><br/>");			
				}
				if(StringUtils.isEmpty(consignee_address)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【收货人详细地址】不能为空<br/><br/>");
				}else{
					String addressCode = changeAddres(consignee_address);
					if(!checkCode(addressCode)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:<br/>【收货人详细地址】("+consignee_address+")有误<br/>请检测录入的省市区地址是否正确<br/>注意：请按照【xx省 xx市 xx区/县 xxxxx】格式填写地址，省市区中间必须以空格隔开<br/><br/>");
					}
				}
				
				if(StringUtils.isNotEmpty(buyer_id_number)){
					if(!IdcardUtil.isIdcard(buyer_id_number))
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【订购人身份证号】不是合法的身份证号码<br/><br/>");
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【订购人身份证号】不能为空<br/><br/>");
				}
				
				if(StringUtils.isNotEmpty(weight)){
					if(!checkDouble(weight)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【毛重】("+weight+")格式类型有误<br/><br/>");
					}
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【毛重】不能为空<br/><br/>");	
				}
				
				if(StringUtils.isNotEmpty(net_weight)){
					if(!checkDouble(net_weight)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【净重】("+net_weight+")格式类型有误<br/><br/>");
					}
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【净重】不能为空<br/><br/>");	
				}
				
				if(StringUtils.isEmpty(goods_info)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【主要货物信息】不能为空<br/><br/>");			
				}
				
				if(StringUtils.isNotEmpty(pack_no)){
					if(!checkDouble(pack_no)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【包裹数】("+pack_no+")格式类型有误<br/><br/>");
					}
				}else{
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【包裹数】不能为空<br/><br/>");	
				}
				
				if(StringUtils.isEmpty(wrap_type)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【包装种类】不能为空<br/><br/>");			
				}
				
				if(StringUtils.isNotEmpty(ie_date)){
					if(!checkDate(ie_date)){
						errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【进口日期】("+ie_date+")格式类型有误,请输入正确的日期格式（yyyy-MM-dd或yyyy/MM/dd）<br/><br/>");
					}
				}
				
				if(StringUtils.isEmpty(brand)){
					errorMsg += ("数据校验至第" + (rowNumber+1) + "行时出现异常:【品牌】不能为空<br/><br/>");			
				}

				rowNumber++;
				System.out.println("校验完"+(rowNumber)+"行");
			}
			
			if(StringUtils.isNotEmpty(errorMsg)){
				throw new Exception(errorMsg);
			}
			
		} catch (Exception e) {
			System.out.println("校验操作异常！");
			System.out.println(e.getMessage());
			
			result.set("result", false);
			result.set("cause", e.getMessage());	
		} 
		return result;
	}
	
	
	/**
	 * 销售订单内容开始导入
	 * @param lines
	 * @return
	 */
	@Before(Tx.class)
	public Record importSOValue( List<Map<String, String>> lines, long userId, long officeId) {
		Connection conn = null;
		Record result = new Record();
		result.set("result",true);
		
		int rowNumber = 1;

		try {
			conn = DbKit.getConfig().getDataSource().getConnection();
			DbKit.getConfig().setThreadLocalConnection(conn);
			conn.setAutoCommit(false);// 自动提交变成false
			
			for (Map<String, String> line :lines) {
				String order_no = line.get("订单编号")==null?null:line.get("订单编号").trim();
				String bar_code = line.get("商品条码")==null?null:line.get("商品条码").trim();
				String item_name = line.get("商品名称")==null?null:line.get("商品名称").trim();
				String item_no = line.get("商品货号")==null?null:line.get("商品货号").trim();
				String qty = line.get("商品数量")==null?null:line.get("商品数量").trim();
				String unit = line.get("计量单位")==null?null:line.get("计量单位").trim();
				String qty1 = line.get("法定计量单位数量")==null?null:line.get("法定计量单位数量").trim();
				String unit1 = line.get("法定计量单位")==null?null:line.get("法定计量单位").trim();
				String price = line.get("单价")==null?null:line.get("单价").trim(); 
				String tax_rate = line.get("税率")==null?null:line.get("税率").trim(); 
				String gcode = line.get("十位税号")==null?null:line.get("十位税号").trim(); 
				String g_model = line.get("品牌规格型号等")==null?null:line.get("品牌规格型号等").trim(); 
				String ciq_gno= line.get("检验检疫商品备案号")==null?null:line.get("检验检疫商品备案号").trim();
				//String ciq_gmodel = line.get("检验检疫商品规格型号")==null?null:line.get("检验检疫商品规格型号").trim();
				String brand = line.get("品牌")==null?null:line.get("品牌").trim();
				
				//String freight = line.get("运杂费")==null?null:line.get("运杂费").trim(); 
				String consignee = line.get("收货人姓名")==null?null:line.get("收货人姓名").trim();
				String consignee_telephone = line.get("收货人电话")==null?null:line.get("收货人电话").trim();
				String consignee_address = line.get("收货人地址")==null?null:line.get("收货人地址").trim();
				
				//String buyer_regno = line.get("订购人注册号")==null?null:line.get("订购人注册号").trim();
				//String buyer_name = line.get("订购人姓名")==null?null:line.get("订购人姓名").trim();
				String buyer_id_number = line.get("订购人身份证号")==null?null:line.get("订购人身份证号").trim();
				
				//String cop_no = line.get("企业内部标识单证编号")==null?null:line.get("企业内部标识单证编号").trim();
				//String insure_fee = line.get("保价费")==null?null:line.get("保价费").trim();
				String weight = line.get("毛重")==null?null:line.get("毛重").trim(); 
				String net_weight = line.get("净重")==null?null:line.get("净重").trim();
				
				String goods_info = line.get("主要货物信息")==null?null:line.get("主要货物信息").trim(); 
				String pack_no = line.get("包裹数")==null?null:line.get("包裹数").trim();  
				String wrap_type = line.get("包装种类")==null?null:line.get("包装种类").trim(); 
				String ie_date = line.get("进口日期")==null?null:line.get("进口日期").trim(); 
				String note = line.get("备注")==null?null:line.get("备注").trim();
				

				//默认值带入
				SalesOrder so = new SalesOrder();
				so.set("ref_order_no", OrderNoGenerator.getNextOrderNo("IDQHDF"));
				so.set("logistics_no", "IYQHDF"+getDouble(order_no));
				so.set("custom_id", 9);  //深圳前海德丰投资发展有限公司
				so.set("status", "未上报");  //状态
				so.set("is_pay_pass","0");  //是否已完成申报
				so.set("pay_channel", "01");//支付渠道
				so.set("pay_type", "PTL");//支付渠道
				so.set("assure_code", "4403660065");// 担保企业编号
				so.set("ems_no", "I440366006516001"); //电商账册编号
				so.set("traf_mode", "Y"); //运输方式
				so.set("ship_name", "汽车"); //运输工具名称
				so.set("ciq_code","471800"); //主管检验检疫机构代码
				so.set("supervision_code","5349"); //进出口岸代码
				so.set("country_code", "142");  //起运国
				so.set("customs_code","5349"); //主管海关代码 
				so.set("business_mode","2"); //业务模式代码 *
				so.set("trade_mode","1210"); //贸易方式 
				so.set("sign_company","4403660065"); //承运企业海关备案号 
				so.set("sign_company_name","深圳前海德丰投资发展有限公司"); //承运企业名称 
				so.set("port_code","5349"); //进出口岸代码
				so.set("ie_date",new Date()); //进口日期
				so.set("wh_org_code","349779838"); //企业组织机构代码（仓储企业）
				so.set("freight", 0);  //运杂费
				so.set("insure_fee", 0); //保价费
				
				so.set("create_by", userId);  //操作人
				so.set("create_stamp", new Date());  //操作时间
				so.set("office_id", officeId); 
				
				//---------------------------------excel表字段数据保存
				so.set("order_no", order_no);  //订单编号
				so.set("consignee", consignee); //收货人姓名
				so.set("consignee_telephone", consignee_telephone);//收货人电话
				so.set("consignee_address", consignee_address);//收货人地址
				
				so.set("buyer_name", consignee);//订购人姓名
				so.set("buyer_id_number", buyer_id_number);//订购人身份证
				so.set("buyer_regno", buyer_id_number); //订购人注册号(默认为身份证好)
				so.set("cop_no", getDouble(order_no)); //企业内部标识单证编号
				
				so.set("weight", weight); //毛重
				so.set("net_weight", net_weight); //净重
				so.set("goods_info", goods_info); //主要货物信息
				so.set("pack_no", pack_no); //包裹数
				so.set("wrap_type", wrap_type); //包装种类
				if(StringUtils.isNotEmpty(ie_date)){
					so.set("ie_date", ie_date); //进口日期
				}
				so.set("note", note); //备注
				
				if(StringUtils.isNotEmpty(consignee_address)){
					String addressCode = changeAddres(consignee_address);
					String[] values = addressCode.split("#");
					String province = values[0];//省
					String city = values[1];//市
					String qv = values[2];//区
					so.set("province", province);
					so.set("city", city);
					so.set("district", qv);
				}
					
				
				so.save();	
				//子表数据保存
				SalesOrderGoods sog = new SalesOrderGoods();
				if(StringUtils.isNotEmpty(bar_code)){
					sog.set("bar_code", bar_code);   //条码
				}
				
				sog.set("item_name", item_name);//名称
				sog.set("item_no", item_no); //商品货号
				sog.set("qty", qty);   //数量
				sog.set("qty1", qty1);   //数量
				if(StringUtils.isNotEmpty(unit)){
					Unit unitRec = Unit.dao.findFirst("select * from unit where name = ?",unit);
					if(unitRec!=null)
						sog.set("unit", unitRec.getStr("code"));   //单位
				}
				if(StringUtils.isNotEmpty(unit1)){
					Unit unitRec = Unit.dao.findFirst("select * from unit where name = ?",unit1);
					if(unitRec!=null)
						sog.set("unit1", unitRec.getStr("code"));   //单位
				}
				sog.set("price", price);   //单位
				sog.set("tax_rate", tax_rate);   //税率
				sog.set("gcode", gcode);   //税号
				sog.set("g_model", g_model);   //(品牌规格型号等)
				sog.set("ciq_gno", ciq_gno);   //检验检疫商品备案号
				sog.set("ciq_gmodel", g_model);   //检验检疫商品规格型号
				sog.set("brand", brand);   //品牌
				
				if(StringUtils.isNotEmpty(price)&&StringUtils.isNotEmpty(qty)){
					DecimalFormat df = new DecimalFormat("#.00");
					String total = df.format(Double.parseDouble(price)*Double.parseDouble(qty));
					sog.set("total", changeNum(Double.parseDouble(total)));   //总价
					so.set("goods_value", changeNum(Double.parseDouble(total))).update();  
				}
				
				
				if(StringUtils.isNotEmpty(price)&&StringUtils.isNotEmpty(qty)&&StringUtils.isNotEmpty(tax_rate)){
					String tax_total = changeNum(Double.parseDouble(price)*Double.parseDouble(qty)*(Double.parseDouble(tax_rate)+1));
					sog.set("after_tax_total", changeNum(Double.parseDouble(tax_total)));   //税后总价
					so.set("actural_paid", changeNum(Double.parseDouble(tax_total))).update();   //税后总价
				}
				sog.set("order_id",so.get("id"));
				sog.set("currency","142");
				sog.set("country","142");
				sog.save();
				
//				//生成一张对应的运输单
//				try{
//					String transf_id = createLogOrder(so.getLong("id").toString(),line, userId, officeId);
//					if(StringUtils.isEmpty(transf_id)){
//						throw new Exception("生成相应的运输单失败");
//					}
//				}catch (Exception e) {
//					throw new Exception("生成相应的运输单失败");
//				}
//				
//				
//				try{
//					String gateOut_id = createGOOrder(so.getLong("id").toString(),line, userId, officeId);
//					if(StringUtils.isEmpty(gateOut_id)){
//						throw new Exception("生成相应的出库单失败");
//					}
//				}catch (Exception e) {
//					throw new Exception("生成相应的出库单失败");
//				}
				
				rowNumber++;
			}
			
			conn.commit();
			result.set("cause","成功导入( "+(rowNumber-1)+" )条数据！");
		} catch (Exception e) {
			System.out.println("导入操作异常！");
			System.out.println(e.getMessage());
			e.printStackTrace();
			
			try {
				if (null != conn)
					conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			result.set("result", false);
			
			result.set("cause", "导入失败<br/>数据导入至第" + (rowNumber+1)
						+ "行时出现异常:" + e.getMessage() + "<br/>导入数据已取消！");
			
		} finally {
			try {
				if (null != conn) {
					conn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				DbKit.getConfig().removeThreadLocalConnection();
			}
		}
		
		return result;
	}

    /**
     * 四舍五入
     * @param number
     * @return
     */
	public static String changeNum(Double number){
		return String.format("%.2f", number);
	}
	
	
	//自动生成运输单
    @Before(Tx.class)
    public String createLogOrder(String sales_order_id, Map<String, String> line, long user_id, long office_id){
    	String express_no = line.get("快递信息").trim();
    	String netwt = line.get("净重").trim();
		String weight = line.get("毛重").trim(); 
		//String freight = line.get("运费").trim(); 
		String cargo_name = line.get("中文名称").trim();
		
//		UserLogin user = LoginUserController.getLoginUser(this);
//        Long office_id = user.getLong("office_id");
    	LogisticsOrder logisticsOrder = null;
    	if(StringUtils.isNotEmpty(sales_order_id)){
    		String order_no = OrderNoGenerator.getNextOrderNo("YD");
    		logisticsOrder = new LogisticsOrder();
        	logisticsOrder.set("log_no", order_no);
        	logisticsOrder.set("office_id", office_id);
        	logisticsOrder.set("status","暂存");
    		logisticsOrder.set("create_by", user_id);
    		logisticsOrder.set("create_stamp", new Date());
    		
    		//预填值gln368
    		logisticsOrder.set("country_code", "142");
    		logisticsOrder.set("shipper_country", "142");
    		logisticsOrder.set("shipper_city", "440305");
    		logisticsOrder.set("shipper", "深圳前海德丰投资发展有限公司");
    		logisticsOrder.set("shipper_address", "深圳前海湾保税港区W6仓");
    		logisticsOrder.set("shipper_telephone", "075586968661");
    		logisticsOrder.set("traf_mode", "4");
    		logisticsOrder.set("pack_no", 1);
    		logisticsOrder.set("ship_name", "汽车");
    		logisticsOrder.set("customs_code", "5349");
    		logisticsOrder.set("ciq_code", "471800");
    		logisticsOrder.set("port_code", "5349");
    		logisticsOrder.set("decl_code", "5349");
    		logisticsOrder.set("supervision_code", "5349");
    		logisticsOrder.set("ems_no", "I440366006516001");
    		logisticsOrder.set("trade_mode", "1210");
    		logisticsOrder.set("destination_port", "5349");
    		logisticsOrder.set("ps_type", "2");
    		logisticsOrder.set("trans_mode", "1");
    		logisticsOrder.set("cut_mode", "1");
    		logisticsOrder.set("wrap_type", "CT");
    		logisticsOrder.set("ie_date", new Date());
    		logisticsOrder.set("deliver_date",  new Date());
    		if(StringUtils.isNotEmpty(cargo_name)){
    			logisticsOrder.set("goods_info", cargo_name); 
			}
//    		if(StringUtils.isNotEmpty(freight)){
//    			logisticsOrder.set("freight", freight);  //运费
//			}
    		logisticsOrder.set("insure_fee", "0");
    		if(StringUtils.isNotEmpty(express_no)){
    			logisticsOrder.set("parcel_info",express_no );
    		}
    		if(StringUtils.isNotEmpty(netwt)){
    			logisticsOrder.set("netwt",netwt );
    		}
    		if(StringUtils.isNotEmpty(weight)){
    			logisticsOrder.set("weight",weight );
    		}
    		if(StringUtils.isNotEmpty(sales_order_id)){
        		logisticsOrder.set("sales_order_id",sales_order_id);
        	}
    		
    		logisticsOrder.save();
    	}
		return logisticsOrder.getLong("id").toString();
    }
    
    
    //自动生成出库单
    @Before(Tx.class)
    public String createGOOrder(String sales_order_id,Map<String, String> line, long user_id, long office_id){
    	String order_no = line.get("订单编号").trim();
		String upc = line.get("商品条码（UPC）").trim();
		String cargo_name = line.get("中文名称").trim();
		String amount = line.get("商品数量").trim();
		String consignee = line.get("收货人姓名").trim();
		String consignee_telephone = line.get("收货人电话").trim();
		String consignee_address = line.get("收货人详细地址").trim();
		String unit = line.get("单位").trim(); 
		String consignee_id = line.get("身份证号码").trim();
		String express_no = line.get("快递信息").trim();

		//默认值带入
		GateOutOrder goo = new GateOutOrder();
		goo.set("warehouse_id", 52);  //前海保税区仓库
		goo.set("customer_id", 3);  //候鸟电商
		goo.set("consignee_country", 142);  //身份证
		goo.set("order_no", OrderNoGenerator.getNextOrderNo("CK"));  //身份证
		goo.set("status", "暂存");  //
		goo.set("create_by", user_id);  //
		goo.set("create_stamp", new Date()); 
		goo.set("gate_out_date", new Date());//出库时间
		goo.set("remark", "导入数据");  //
		
//		if(StringUtils.isNotEmpty(location)){
//			String[] values = location.split("#");
//			String province = values[0];//省
//			String city = values[1];//市
//			String qv = values[2];//区
//			
//			goo.set("location", qv);
//		}
		if(StringUtils.isNotEmpty(consignee_address)){
			String addressCode = changeAddres(consignee_address);
			String[] values = addressCode.split("#");
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
			cargo_name = getCargoName(upc);
			if(StringUtils.isNotEmpty(cargo_name)){
				gooi.set("cargo_name", cargo_name);//名称
			}
		}
		
		if(StringUtils.isNotEmpty(unit)){
			gooi.set("packing_unit", unit);
		}
		
		if(StringUtils.isNotEmpty(amount)){
			gooi.set("packing_amount", amount);
		}
		gooi.set("order_id",goo.get("id"));
		gooi.save();
		
		return gooi.getLong("id").toString();
    }
	
	
}
