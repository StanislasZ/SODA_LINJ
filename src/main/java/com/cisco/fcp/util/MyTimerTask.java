package com.cisco.fcp.util;

import com.cisco.fcp.entity.Config;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;
import com.cisco.fcp.controller.PageController;
import com.cisco.fcp.websocket.WarningPushSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MyTimerTask extends TimerTask {
	private static final Logger logger= LoggerFactory.getLogger(MyTimerTask.class);
	long threadId;
	Config config;
	String jsonDir;
	static String status_stop="ready";

	
	public MyTimerTask(long threadId, Config config,String jsonDir) {
		super();
		this.threadId = threadId;
		this.config=config;
		this.jsonDir=jsonDir;
	}


	@Override
	public void run() {
		logger.info("here is timetask run method, task thread id is {}",this.threadId);
		String jsonfilepath=jsonDir+config.getJsonfilename();
		File jf=new File(jsonfilepath);
		if(jf.exists()){
			//System.out.println("json file exists!!!");
			logger.info("json file exists...");
			Thread t_sendm=findThread(this.threadId);
			if(t_sendm!=null){
				logger.info("force to stop thread={}",t_sendm.getName());
				t_sendm.stop();
				logger.info("task thread stop ok...(by timertask)");
				//request.setAttribute("alreadyStart",false);
			}

			//System.out.println("timer的run方法结束");
			if(!config.getStatus().equals("error")) {
				config.setStatus(status_stop);


			}else{
				logger.info("the status is error, no need to modify it");
			}
			config.setThreadId(0);
			PageController.saveJson2Local(config);

			try {
				WarningPushSocket.sendMsgToAll("refresh");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}else{
			logger.warn("json file doesn't exist, so no operation here...");
		}

	}
	
	
	//通过线程组获得线程
    public static Thread findThread(long threadId) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while(group != null) {
            Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
            int count = group.enumerate(threads, true);
            for(int i = 0; i < count; i++) {
                if(threadId == threads[i].getId()) {
                    return threads[i];
                }
            }
            group = group.getParent();
        }
        return null;
    }

}
