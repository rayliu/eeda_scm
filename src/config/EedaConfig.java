package config;

import handler.UrlHanlder;
import interceptor.EedaMenuInterceptor;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Properties;

import models.Account;
import models.ArapAccountAuditLog;
import models.Category;
import models.Location;
import models.ModuleRole;
import models.Office;
import models.Party;
import models.Permission;
import models.Role;
import models.RolePermission;
import models.UserCustomer;
import models.UserLogin;
import models.UserOffice;
import models.UserRole;
import models.eeda.OrderActionLog;
import models.eeda.oms.GateInOrder;
import models.eeda.oms.GateInOrderItem;
import models.eeda.oms.GateOutOrder;
import models.eeda.oms.GateOutOrderItem;
import models.eeda.oms.InspectionOrder;
import models.eeda.oms.InspectionOrderItem;
import models.eeda.oms.Inventory;
import models.eeda.oms.InventoryOrder;
import models.eeda.oms.InventoryOrderItem;
import models.eeda.oms.LoadOrder;
import models.eeda.oms.LogisticsOrder;
import models.eeda.oms.MoveOrder;
import models.eeda.oms.MoveOrderItem;
import models.eeda.oms.SalesOrder;
import models.eeda.oms.SalesOrderCount;
import models.eeda.oms.SalesOrderGoods;
import models.eeda.oms.WaveOrder;
import models.eeda.oms.WaveOrderItem;
import models.eeda.profile.Country;
import models.eeda.profile.CustomCompany;
import models.eeda.profile.LogisticsCustomCompany;
import models.eeda.profile.Module;
import models.eeda.profile.Product;
import models.eeda.profile.Unit;
import models.eeda.profile.Warehouse;
import models.eeda.profile.WarehouseShelves;
import models.yh.profile.Contact;
import models.yh.profile.CustomizeField;
import models.yh.profile.OfficeCofig;
import models.yh.profile.Route;

import org.apache.log4j.Logger;
import org.bee.tl.ext.jfinal.BeetlRenderFactory;
import org.h2.tools.Server;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.ext.plugin.shiro.ShiroInterceptor;
import com.jfinal.ext.plugin.shiro.ShiroPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.SqlReporter;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.scheduler.SchedulerPlugin;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import controllers.HomeController;
import controllers.api.ApiController;
import controllers.eeda.ModuleController;
import controllers.eeda.report.ReportController;
import controllers.oms.allinpay.AllinpayController;
import controllers.oms.check.CheckController;
import controllers.oms.gateInOrder.GateInOrderController;
import controllers.oms.gateOutOrder.GateOutOrderController;
import controllers.oms.inspectionOrder.InspectionOrderController;
import controllers.oms.inventory.InventoryController;
import controllers.oms.inventoryOrder.InventoryOrderController;
import controllers.oms.loadOrder.LoadOrderController;
import controllers.oms.logisticsOrder.LogisticsOrderController;
import controllers.oms.moveOrder.MoveOrderController;
import controllers.oms.orderStatus.OrderStatusController;
import controllers.oms.salesOrder.SalesOrderController;
import controllers.oms.waveOrder.WaveOrderController;
import controllers.profile.AccountController;
import controllers.profile.CustomCompanyController;
import controllers.profile.LogisticsCustomCompanyController;
import controllers.profile.PrivilegeController;
import controllers.profile.UnitController;
import controllers.profile.WarehouseController;
import controllers.profile.WarehouseShelvesController;
import controllers.wms.MobileController;
import controllers.yh.arap.AccountAuditLogController;

public class EedaConfig extends JFinalConfig {
    private Logger logger = Logger.getLogger(EedaConfig.class);

    private static final String H2 = "H2";
    private static final String Mysql = "Mysql";
    private static final String ProdMysql = "ProdMysql";
      
    public static String mailUser;
    public static String mailPwd;
    
    public static Properties sysProp;//留给以后的controller调用
    /**
     * 
     * 供Shiro插件使用 。
     */
    Routes routes;

    C3p0Plugin cp;
    ActiveRecordPlugin arp;

    @Override
	public void configConstant(Constants me) {
        //加载配置文件    	
        sysProp = loadPropertyFile("app_config.txt");
        
        me.setDevMode(getPropertyToBoolean("devMode", false));
        
    	// ApiConfigKit 设为开发模式可以在开发阶段输出请求交互的 xml 与 json 数据
    	ApiConfigKit.setDevMode(me.getDevMode());
        
    	

        BeetlRenderFactory templateFactory = new BeetlRenderFactory();
        me.setMainRenderFactory(templateFactory);

        BeetlRenderFactory.groupTemplate.setCharset("utf-8");// 没有这句，html上的汉字会乱码

        // 注册后，可以使beetl html中使用shiro tag
        BeetlRenderFactory.groupTemplate.registerFunctionPackage("shiro", new ShiroExt());

        //没有权限时跳转到login
        me.setErrorView(401, "/yh/noLogin.html");//401 authenticate err
        me.setErrorView(403, "/yh/noPermission.html");// authorization err
        
        //内部出错跳转到对应的提示页面，需要考虑提供更详细的信息。
        me.setError404View("/yh/err404.html");
        me.setError500View("/yh/err500.html");
        
        // me.setErrorView(503, "/login.html");
        // get name representing the running Java virtual machine.
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(name);
        // get pid
        String pid = name.split("@")[0];
        logger.info("Pid is: " + pid);
    }

    @Override
	public void configRoute(Routes me) {
        this.routes = me;

        //TODO: 为之后去掉 yh做准备
        String contentPath="/";//"yh";

        // me.add("/fileUpload", HelloController.class);
        setScmRoute(me, contentPath);
        
    }
    

	private void setScmRoute(Routes me, String contentPath) {
		// yh project controller
        me.add("/", controllers.profile.MainController.class, contentPath);
        me.add("/module", ModuleController.class, contentPath);
        me.add("/debug", controllers.profile.LogController.class, contentPath);
        me.add("/warehouse",WarehouseController.class,contentPath);
        me.add("/loginUser", controllers.profile.LoginUserController.class, contentPath);
        //register loginUser
        me.add("/register",controllers.profile.RegisterUserController.class,contentPath);
        me.add("/reset",controllers.profile.ResetPassWordController.class,contentPath);
        me.add("/role", controllers.profile.RoleController.class, contentPath);
        me.add("/userRole",controllers.profile.UserRoleController.class,contentPath);
        me.add("/customer", controllers.profile.CustomerController.class, contentPath);
        me.add("/serviceProvider", controllers.profile.ServiceProviderController.class, contentPath);
        me.add("/location", controllers.profile.LocationController.class, contentPath);
        me.add("/office", controllers.profile.OfficeController.class, contentPath);
        me.add("/product", controllers.profile.ProductController.class, contentPath);
		me.add("/home", HomeController.class, contentPath);
		me.add("/accountAuditLog", AccountAuditLogController.class, contentPath);
		me.add("/account", AccountController.class, contentPath);
		me.add("/privilege", PrivilegeController.class, contentPath);
		me.add("/unit", UnitController.class, contentPath);
		//oms管理系统
		me.add("/customCompany", CustomCompanyController.class, contentPath);
		me.add("/logisticsCustomCompany", LogisticsCustomCompanyController.class, contentPath);
		me.add("/logisticsOrder", LogisticsOrderController.class, contentPath);
		me.add("/salesOrder", SalesOrderController.class, contentPath);
		me.add("/gateInOrder", GateInOrderController.class, contentPath);
		me.add("/inspectionOrder", InspectionOrderController.class, contentPath);
		me.add("/allinpay", AllinpayController.class, contentPath);
		me.add("/api", ApiController.class, contentPath);
		me.add("/warehouseShelves", WarehouseShelvesController.class, contentPath);
		me.add("/loadOrder", LoadOrderController.class, contentPath);
		me.add("/waveOrder", WaveOrderController.class, contentPath);
		me.add("/gateOutOrder", GateOutOrderController.class, contentPath);
		me.add("/searchStatus", OrderStatusController.class, contentPath);
		me.add("/report", ReportController.class, contentPath);
		me.add("/check", CheckController.class, contentPath);
		
		//库存管理
		me.add("/inventory", InventoryController.class, contentPath);
        me.add("/inventoryOrder", InventoryOrderController.class, contentPath);
        me.add("/moveOrder", MoveOrderController.class, contentPath);
		me.add("/m", MobileController.class, contentPath);
	}

    @Override
	public void configPlugin(Plugins me) {
        // 加载Shiro插件, for backend notation, not for UI
    	me.add(new ShiroPlugin(routes));
    	
    	//job启动
    	SchedulerPlugin sp = new SchedulerPlugin("job.properties");
        me.add(sp);
    	
        mailUser = getProperty("mail_user_name");
        mailPwd = getProperty("mail_pwd");
        // H2 or mysql
        initDBconnector();

        me.add(cp);

        arp = new ActiveRecordPlugin(cp);
        arp.setShowSql(true);// 控制台打印Sql
        SqlReporter.setLogger(true);// log4j 打印Sql
        me.add(arp);

        arp.setDialect(new MysqlDialect());
        // 配置属性名(字段名)大小写不敏感容器工厂
        arp.setContainerFactory(new CaseInsensitiveContainerFactory());

        arp.addMapping("office", Office.class);
        arp.addMapping("user_login", UserLogin.class);
        arp.addMapping("role", Role.class);
        arp.addMapping("permission", Permission.class);
        arp.addMapping("user_role", UserRole.class);
        arp.addMapping("module_role", ModuleRole.class);
        arp.addMapping("role_permission", RolePermission.class);
        arp.addMapping("eeda_modules", Module.class);

        arp.addMapping("party", Party.class);
        arp.addMapping("contact", Contact.class);       
        arp.addMapping("route", Route.class);
        arp.addMapping("product", Product.class);
        arp.addMapping("category", Category.class);
        arp.addMapping("location", Location.class);

        //基本数据用户网点
        arp.addMapping("user_office", UserOffice.class);
        arp.addMapping("user_customer", UserCustomer.class);
        
        arp.addMapping("customize_field", CustomizeField.class);
        arp.addMapping("office_config", OfficeCofig.class);
        
        //中转仓
        arp.addMapping("warehouse",Warehouse.class);
        arp.addMapping("fin_account",Account.class);
        arp.addMapping("arap_account_audit_log",ArapAccountAuditLog.class);
        

        //cms
        arp.addMapping("custom_company", CustomCompany.class);
        arp.addMapping("logistics_custom_company", LogisticsCustomCompany.class);
        arp.addMapping("sales_order", SalesOrder.class);
        arp.addMapping("sales_order_goods", SalesOrderGoods.class);
        arp.addMapping("logistics_order", LogisticsOrder.class);
        arp.addMapping("sales_order_count", SalesOrderCount.class);
        arp.addMapping("gate_in_order", GateInOrder.class);
        arp.addMapping("gate_in_order_item", GateInOrderItem.class);
        arp.addMapping("inspection_order", InspectionOrder.class);
        arp.addMapping("inspection_order_item", InspectionOrderItem.class);
        arp.addMapping("warehouse_shelves", WarehouseShelves.class);
        arp.addMapping("wave_order", WaveOrder.class);
        arp.addMapping("wave_order_item", WaveOrderItem.class);
        arp.addMapping("gate_out_order", GateOutOrder.class);
        arp.addMapping("gate_out_order_item", GateOutOrderItem.class);
        arp.addMapping("load_order", LoadOrder.class);
        arp.addMapping("order_action_log", OrderActionLog.class);
        arp.addMapping("unit", Unit.class);
        arp.addMapping("country", Country.class);
        arp.addMapping("inventory", Inventory.class);
        arp.addMapping("inventory_order", InventoryOrder.class);
        arp.addMapping("inventory_order_item", InventoryOrderItem.class);
        arp.addMapping("move_order", MoveOrder.class);
        arp.addMapping("move_order_item", MoveOrderItem.class);

    }

    private void initDBconnector() {
        String dbType = getProperty("dbType");
        String url = getProperty("dbUrl");
        String username = getProperty("username");
        String pwd = getProperty("pwd");

        if (H2.equals(dbType)) {
            connectH2();
        } else {
        	logger.info("DB url: " + url);
            cp = new C3p0Plugin(url, username, pwd);
            //DataInitUtil.initH2Tables(cp);

        }

    }

    private void connectH2() {
        // 这个启动web console以方便通过localhost:8082访问数据库
        try {
            Server.createWebServer().start();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cp = new C3p0Plugin("jdbc:h2:mem:eeda;", "sa", "");
        // cp = new C3p0Plugin("jdbc:h2:data/sample;IFEXISTS=TRUE;", "sa", "");
        cp.setDriverClass("org.h2.Driver");
        DataInitUtil.initH2Tables(cp);
    }

    @Override
	public void configInterceptor(Interceptors me) {
    	if("Y".equals(getProperty("is_check_permission"))){
    		logger.debug("is_check_permission = Y");
         	me.add(new ShiroInterceptor());
    	}
    	// 添加控制层全局拦截器, 每次进入页面时构造菜单项
    	me.addGlobalActionInterceptor(new EedaMenuInterceptor());
    }

    @Override
	public void configHandler(Handlers me) {
        if (H2.equals(getProperty("dbType"))) {
            DataInitUtil.initData(cp);
        }
        //DataInitUtil.initData(cp);
        
        me.add(new UrlSkipHandler("/apidoc.*", false));
        me.add(new UrlHanlder());
        
    }
}
