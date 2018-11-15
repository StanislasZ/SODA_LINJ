package com.cisco.fcp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogTest {
    public  final Logger log=LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        LogTest lt=new LogTest();
        lt.test();
    }

    public void test(){
        String testinfo="daaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        log.trace("trace");
        log.debug("debug");
        log.warn("warn");
        for (int i = 0; i < 200000; i++) {
            log.info(testinfo);
        }

        log.error("error");
    }

}
