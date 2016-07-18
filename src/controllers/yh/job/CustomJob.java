package controllers.yh.job;

import java.util.Calendar;

import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;

public class CustomJob implements Runnable {
	private Logger logger = Logger.getLogger(CustomJob.class);

	@Override
	public void run() {
	    logger.debug("更新单据状态开始.....");
	    
	    logger.debug("更新单据状态结束");
	}

}
