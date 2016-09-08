package controllers.oms.importOrder;

import interceptor.SetAttrLoginUserInterceptor;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.UserLogin;
import models.eeda.oms.GateInOrder;
import models.eeda.oms.InspectionOrder;
import models.eeda.oms.InspectionOrderItem;
import models.eeda.oms.Inventory;
import models.eeda.oms.MoveOrder;
import models.eeda.oms.MoveOrderItem;
import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.oms.WaveOrderItem;
import models.eeda.profile.CustomCompany;
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
public class ImportOrder extends Controller {

	private Logger logger = Logger.getLogger(ImportOrder.class);
	Subject currentUser = SecurityUtils.getSubject();

	public void index() {
		render("/oms/check/checkList.html");
	}

	// 导入入库单
	public void importGOOrder() {
		UploadFile uploadFile = getFile();
		File file = uploadFile.getFile();
		String fileName = file.getName();
		String strFile = file.getPath();

		Record resultMap = new Record();
		try {
			String[] title = null;
			List<Map<String, String>> content = new ArrayList<Map<String, String>>();
			//exel格式区分
			if (fileName.endsWith(".xls")) {
				title = ReaderXLS.getXlsTitle(file);
				content = ReaderXLS.getXlsContent(file);
			} else if (fileName.endsWith(".xlsx")) {
				title = ReaderXlSX.getXlsTitle(file);
				content = ReaderXlSX.getXlsContent(file);
			} else {
				resultMap.set("result", false);
				resultMap.set("cause", "导入失败，请选择正确的excel文件（xls/xlsx）");
			}
			
			//导入模板表头（标题）校验
			if (title != null && content.size() > 0) {
				CheckOrder checkOrder = new CheckOrder();
				if (checkOrder.checkoutExeclTitle(title, "gateOutOrder")) {
					/**
					 * 内容校验
					 */
					resultMap = checkOrder.importCheck(content);
					
					/**
					 * 内容开始导入
					 */
					if(resultMap.get("result")){
						resultMap = checkOrder.importValue(content);
					}
				} else {
					resultMap.set("result", false);
					resultMap.set("cause", "导入失败，excel标题列与模板excel标题列不一致");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.set("result", false);
			resultMap.set("cause","导入失败，请选择正确的excel文件<br/>（建议使用Microsoft Office Excel软件操作数据）");
		}
		logger.debug("result:" + resultMap.get("result") + ",cause:"
				+ resultMap.get("cause"));

		renderJson(resultMap);
	}

}
