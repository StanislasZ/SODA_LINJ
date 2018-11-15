package com.cisco.fcp.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.cisco.fcp")
@SpringBootApplication
public class FcpApplication {

	public static void main(String[] args) {
		SpringApplication.run(FcpApplication.class, args);
	}
}
