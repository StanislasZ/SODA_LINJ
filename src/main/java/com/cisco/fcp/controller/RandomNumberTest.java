package com.cisco.fcp.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class RandomNumberTest {
    public static void main(String[] args){

        Map<String, String> map = System.getenv();
        String simulator_home_key="SIMULATOR_HOME1";
        String value=map.get(simulator_home_key);
        System.out.println(value==null);



        System.out.println(simulator_home_key + "=" + map.get(simulator_home_key));



//        for(Iterator<String> itr = map.keySet().iterator(); itr.hasNext();){
//            String key = itr.next();
//            System.out.println(key + "=" + map.get(key));
//        }





//        System.out.println("ab\"ab");
//        System.out.println(new Date());
//
//        File f=new File("C:\\Users\\ruozhao\\Simulator\\field2file\\data.txt");
//        System.out.println(f.getAbsolutePath());
//        System.out.println(f.getName());
//
//        String time = "2017-03-01 15:23:20";
//        Date date = new Date();
//        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy年MM月dd");
//        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("MM月dd hh时mm分");
//        SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat("yyyyMMddHHmmss");
//
//
//        String result1=simpleDateFormat4.format(date);
//        String result2=simpleDateFormat2.format(date);
//        String result3=simpleDateFormat3.format(date);
//
//        System.out.println(result1+"\n"+result2+"\n"+result3);




//        Random r=new Random();
//        int left=20;
//        int right=40;
//        double target=r.nextDouble()*(right-left)+left;
//
//        System.out.println(target);
//
//        String a="0";
//        String b="";
//        for(int i=0;i<3;i++){
//            b=b+a;
//        }
//        System.out.println(b);

    }
}
