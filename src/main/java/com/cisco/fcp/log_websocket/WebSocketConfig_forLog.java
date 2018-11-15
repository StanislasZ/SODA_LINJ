package com.cisco.fcp.log_websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig_forLog extends AbstractWebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/html/home/log")
                .setAllowedOrigins("http://localhost:8976")
                .addInterceptors()
                .withSockJS();
    }

//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;

    /**
     * 推送日志到/topic/pullLogger
     */
   // @PostConstruct
//    public void pushLogger(){
//        ExecutorService executorService= Executors.newFixedThreadPool(4);
//        Runnable processLog=new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        LoggerMessage log = LoggerQueue.getInstance().poll();
//                        if(log!=null){
//                            if(messagingTemplate!=null)
//                                messagingTemplate.convertAndSend("/topic/pullLogger",log);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        Runnable fileLog=new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        String log = LoggerQueue.getInstance().pollFileLog();
//                        if(log!=null){
//                            if(messagingTemplate!=null)
//                                messagingTemplate.convertAndSend("/topic/pullFileLogger",log);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        executorService.submit(fileLog);
//        executorService.submit(fileLog);
//        executorService.submit(processLog);
//        executorService.submit(processLog);
//    }
}