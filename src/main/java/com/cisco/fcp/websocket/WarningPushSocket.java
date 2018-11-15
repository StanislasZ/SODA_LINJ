package com.cisco.fcp.websocket;


import java.io.IOException;

import com.cisco.fcp.controller.PageController;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.server.ServerEndpoint;


import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ServerEndpoint(value = "/html/home/service")
@Component
public class WarningPushSocket {

    private static final Logger logger= LoggerFactory.getLogger(WarningPushSocket.class);

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WarningPushSocket> wsClientMap = new CopyOnWriteArraySet<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     * @param session 当前会话session
     */
    @OnOpen
    public void onOpen (/*@PathParam("userId")String userId,*/Session session) throws IOException{
        this.session = session;
        wsClientMap.add(this);
        addOnlineCount();
        logger.info("session's id={}, new connection comes in, now the number of connection is {}",session.getId(),wsClientMap.size());

    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose (Session session){
        //System.out.println("Close!!!!!!!!!");
        wsClientMap.remove(this);
        subOnlineCount();
        logger.info("one connection closes, now the number of connection is {}",wsClientMap.size());
    }

    /**
     * 收到客户端消息
     * @param message 客户端发送过来的消息
     * @param session 当前会话session
     * @throws IOException
     */
    @OnMessage
    public void onMessage (String message, Session session) throws IOException {
        logger.info("message from browser is {}",message);
        sendMsgToAll(message);
    }

    /**
     * 发生错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("wsClientMap got error!");
        error.printStackTrace();
        logger.error(error.getMessage());
    }

    /**
     * 给所有客户端群发消息
     * @param message 消息内容
     * @throws IOException
     */
    public static void sendMsgToAll(String message) throws IOException {
        for ( WarningPushSocket item : wsClientMap ){
            item.session.getBasicRemote().sendText(message);
        }
        logger.info("send message(group) to {} client(s), message is {}",wsClientMap.size(),message);
    }

    public void sendMessage (String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        logger.info("send one message ok...  message is {}",message);
    }

    public static synchronized  int getOnlineCount (){
        return WarningPushSocket.onlineCount;
    }

    public static synchronized void addOnlineCount (){
        WarningPushSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount (){
        WarningPushSocket.onlineCount--;
    }


    public synchronized void fileListen(){
        String jsonDirPath= PageController.isWindows()?PageController.windowsJsonDir:PageController.linuxJsonDir;
        Path path = Paths.get(jsonDirPath);
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            //给path路径加上文件观察服务
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.ENTRY_DELETE);
            // start an infinite loop
            while (true) {
                final WatchKey key = watchService.take();
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    final WatchEvent.Kind<?> kind = watchEvent.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    //创建事件
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("create detected!!!");
                        return;
                    }
                    //修改事件
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("modify detected!!!");
                        return;
                    }
                    //删除事件
                    if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("delete detected!!!");
                        return;
                    }
                    // get the filename for the event
                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                    final Path filename = watchEventPath.context();
                    // print it out
                    System.out.println(kind + " -> " + filename);

                }
                // reset the key
                boolean valid = key.reset();
                // exit loop if the key is not valid (if the directory was
                // deleted,for
                if (!valid) {
                    break;
                }
            }

        } catch (IOException | InterruptedException ex) {
            System.err.println(ex);
        }


    }








}

