package com.cisco.fcp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.locks.ReentrantLock;

import com.cisco.fcp.websocket.WarningPushSocket;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.cisco.fcp.entity.Config;
import com.cisco.fcp.controller.PageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//@Service("simulatorService")   //业务层
public class SimulatorServiceImpl /*implements SimulatorService*/ {

    private static final Logger logger= LoggerFactory.getLogger(SimulatorServiceImpl.class);
    long threadId;

    //执行sendMessageToMqtt方法前就执行
    public void setThreadId(long threadId){
        this.threadId=threadId;

    }

    public void sendMessageToMqtt(Config config) throws IOException{

        //broker format:    "tcp://10.75.161.166:1883"
        String broker       ="tcp://"+config.getHost()+":"+config.getPort();
        String topic        =config.getTopic();
        int qos             = 1;
        String clientId     = "JavaSample"+config.getJsonfilename();
        PageController.clientIdSuffix++;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            //创建mqtt客户端实例
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            //System.out.println("增加username,password");
            //connOpts.setUserName("admin");
            //connOpts.setPassword("password".toCharArray());
            connOpts.setCleanSession(false);
            logger.info("Connecting to broker:{}",broker);
            sampleClient.connect(connOpts);
            logger.info("Connected");
            BufferedReader reader=null;
            try {
                String filepath=config.getFilepath();
                filepath=filepath.replace("\\", File.separator); //适应Linux系统
                File fileOnServer=new File(filepath);
                InputStream in= new FileInputStream(fileOnServer);
                byte[] b = new byte[3];
                in.read(b);
                in.close();
                InputStreamReader read=null;
                if (b[0] == -17 && b[1] == -69 && b[2] == -65){
                    read = new InputStreamReader(new FileInputStream(fileOnServer),"utf-8");
                }
                else{
                    read=new InputStreamReader(new FileInputStream(fileOnServer),"gbk");
                }
                reader=new BufferedReader(read);
            }catch(IOException e1){
                e1.printStackTrace();
            }
            String line = null;
            try {
                while((line=reader.readLine())!=null){
                    //获取本次任务对应的锁和suspend值
                    //System.out.println("测试setThreadId方法后，threadId="+this.threadId);
                    synchronized (PageController.lockMap.get(this.threadId)) {
                        if (PageController.suspendMap.get(this.threadId)) {
                            //System.out.println("suspend为"+PageController.suspendMap.get(this.threadId));
                            try {
                                logger.info("let thread wait...");
                                PageController.lockMap.get(this.threadId).wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    MqttMessage message = new MqttMessage(line.getBytes());
                    message.setQos(qos);
                    sampleClient.publish(topic, message);   //发送消息
                    //System.out.println("Publishing message: "+message);
                    logger.info("Publishing message: {}",message);
                    try {
                        Thread.currentThread().sleep(config.getInterval()*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                reader.close();
            }
            logger.info("Message published");
            sampleClient.disconnect();
            logger.info("mqtt client disconnected");

        } catch(MqttException me) {
            logger.warn("catch MqttException: " +
                    "\nresason->{}\nmsg->{}\nloc->{}\ncause->{}\nexcep->{} ",
                    me.getReasonCode(),me.getMessage(),me.getLocalizedMessage(),me.getCause(),me);
            me.printStackTrace();
            //增加异常处理，杀死线程
            logger.info("There is something wrong with mqtt connection....");
            Thread t_sendm=PageController.findThread(config.getThreadId());
            if(t_sendm!=null){
                logger.info("find thread and force it stop");
                config.setStatus(PageController.status_error);
                config.setThreadId(0);
                PageController.saveJson2Local(config);
                logger.info("set status to error , user needs to delete or edit it ...");

                //通知前台刷新
                WarningPushSocket.sendMsgToAll("refresh");
                WarningPushSocket.sendMsgToAll(me.getCause().toString());
                t_sendm.stop(); //这句放最后

            }else{
                logger.warn("catch MqttException ,but task thread is not found...");
            }

        }

    }

}
