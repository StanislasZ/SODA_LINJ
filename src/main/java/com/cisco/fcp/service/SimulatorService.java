package com.cisco.fcp.service;

import java.io.File;
import com.cisco.fcp.entity.Config;


public interface SimulatorService {

	public File readFileFromLocal();
	
	public void sendMessageToMqtt(Config config);
	
	
	
	
	
	
}
