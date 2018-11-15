package com.cisco.fcp.controller;

import com.alibaba.fastjson.JSONObject;
import com.cisco.fcp.service.SimulatorServiceImpl;
import com.cisco.fcp.websocket.WarningPushSocket;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cisco.fcp.entity.*;
import com.cisco.fcp.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


@Controller
@Scope("prototype")
public class PageController extends BasicController {

    private static final Logger logger= LoggerFactory.getLogger(PageController.class);

    private static String windowsFileDir="C:\\Users\\ruozhao\\Simulator\\fileDir";
    public static String windowsJsonDir="C:\\Users\\ruozhao\\Simulator\\jsonDir\\";
    private static String linuxFileDir="/home/ciscokds/simulator/fileDir";
    public static String linuxJsonDir="/home/ciscokds/simulator/jsonDir/";
    private static String windowsFieldDir="C:\\Users\\ruozhao\\Simulator\\field";
    private static String LinuxFieldDir="/home/ciscokds/simulator/field";
    private static String windowsFieldFileDir="C:\\Users\\ruozhao\\Simulator\\field2file";
    private static String linuxFieldFileDir="/home/ciscokds/simulator/field2file";

    static{
        String simulator_home_value = System.getenv("SIMULATOR_HOME");

        if(simulator_home_value!=null) {
            windowsFileDir = simulator_home_value + "\\fileDir";
            windowsJsonDir = simulator_home_value + "\\jsonDir\\";
            linuxFileDir = simulator_home_value + "/fileDir";
            linuxJsonDir = simulator_home_value + "/jsonDir/";
            windowsFieldDir = simulator_home_value + "\\field";
            LinuxFieldDir = simulator_home_value + "/field";
            windowsFieldFileDir = simulator_home_value + "\\field2file";
            linuxFieldFileDir = simulator_home_value + "/field2file";
        }

    }



    public static int clientIdSuffix=1;
    public static HashMap<Long,ReentrantLock> lockMap=new HashMap<>();
    public static HashMap<Long,Boolean> suspendMap=new HashMap<>();

    public static String status_running="running";
    public static String status_pause="paused";
    public static String status_stop="ready";
    public static String status_error="error";



//    @RequestMapping(value = "/listenJsonFile", method = RequestMethod.POST)
//    @ResponseBody
//    public Object listenJsonFile(@RequestBody JSONObject params,Model model) throws Exception {
//        Map<String, Object> map = new HashMap<>();
//        String dirPath=isWindows()?windowsJsonDir:linuxJsonDir;
//        map=getListenResult(dirPath,map);
//
//
//        return map;
//    }
//
//    public Map<String,Object> getListenResult(String dirPath,Map<String,Object> map){
//
//        Path path = Paths.get(dirPath);
//
//        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
//            //给path路径加上文件观察服务
//            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.ENTRY_DELETE);
//            // start an infinite loop
//            while (true) {
//                System.out.println("-----");
//                final WatchKey key = watchService.take();
//                for (WatchEvent<?> watchEvent : key.pollEvents()) {
//                    final WatchEvent.Kind<?> kind = watchEvent.kind();
//                    if (kind == StandardWatchEventKinds.OVERFLOW) {
//                        continue;
//                    }
//                    //创建事件
//                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
//                        System.out.println("create detected!!!");
//                        map.put("success",true);
//                        map.put("message","now refresh！");
//                        return map;
//
//                    }
//                    //修改事件
//                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
//                        System.out.println("modify detected!!!");
//                        map.put("success",true);
//                        map.put("message","now refresh！");
//                        return map;
//                    }
//                    //删除事件
//                    if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
//                        System.out.println("delete detected!!!");
//                        map.put("success",true);
//                        map.put("message","now refresh！");
//                        return map;
//                    }
//                    // get the filename for the event
//                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
//                    final Path filename = watchEventPath.context();
//                    // print it out
//                    System.out.println(kind + " -> " + filename);
//
//                }
//                // reset the key
//                boolean valid = key.reset();
//                // exit loop if the key is not valid (if the directory was
//                // deleted,for
//                if (!valid) {
//                    break;
//                }
//            }
//
//        } catch (IOException | InterruptedException ex) {
//            System.err.println(ex);
//        }
//
//
//        return map;
//    }







    @RequestMapping(value = "/refreshField", method = RequestMethod.POST)
    public String fieldData(Model model) {
        //得到经过按index排序后的列表
        List<FieldForSimulator> list = getFieldList();

        model.addAttribute("fieldList",list);
        return "service_management::fieldlist";
    }

    //获得field文件下所有文件转成对象后的List
    private List<FieldForSimulator> getFieldList() {
        List<FieldForSimulator> list = new ArrayList<>();
        File[] filelist = getFieldFilesArray();
        if(filelist!=null) {
            for (int i = 0; i < filelist.length; i++) {
                File temp = filelist[i];
                String path = temp.getAbsolutePath();
                String jsondata = getDatafromFile(path);
                Gson gson = new Gson();
                FieldForSimulator ffs = gson.fromJson(jsondata, FieldForSimulator.class);
                //System.out.println("get from local, config is "+configFromLocal);
                list.add(ffs);

            }
        }
        //按index排序
        list.sort(new Comparator<FieldForSimulator>() {
            @Override
            public int compare(FieldForSimulator o1, FieldForSimulator o2) {

                if(o1.getIndex()>o2.getIndex() ){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        return list;
    }

    //获得field文件夹下的File数组
    private static File[] getFieldFilesArray() {
        String fieldDir=null;
        if(isWindows()) {
            fieldDir=windowsFieldDir;
        }else{
            fieldDir=LinuxFieldDir;
        }
        File f=new File(fieldDir);
        return f.listFiles();
    }


    @RequestMapping(value = "/rowdata", method = RequestMethod.POST)
    @ResponseBody
    public List<Config> data() {

        //System.out.println("ask for rowdata......");

        List<Config> list = new ArrayList<Config>();

        String jsondir=null;
        if(isWindows()) {
            jsondir=windowsJsonDir;
        }else{
            jsondir=linuxJsonDir;
        }
        File f=new File(jsondir);
        File[] filelist=f.listFiles();
        if(filelist!=null) {
            for (int i = 0; i < filelist.length; i++) {
                File temp = filelist[i];
                String path = temp.getAbsolutePath();
                String jsondata = getDatafromFile(path);
                Gson gson = new Gson();
                Config configFromLocal = gson.fromJson(jsondata, Config.class);
                //System.out.println("get from local, config is "+configFromLocal);

                //刷table的时候检查一下threadId和status
                //若status=running同时id=0，说明中途出问题了，把status改成status_stop
                if(configFromLocal.getStatus().equals(status_running)||configFromLocal.getStatus().equals(status_pause)){
                    if(configFromLocal.getThreadId()==0){
                        configFromLocal.setStatus(status_stop);
                        saveJson2Local(configFromLocal);
                    }
                }

                list.add(configFromLocal);
            }
        }
        //按json文件的文件名的字典顺序排序
        list.sort(new Comparator<Config>() {
            @Override
            public int compare(Config o1, Config o2) {

                if(o1.getJsonfilename().compareTo(o2.getJsonfilename())>0       ){
                    return 1;
                }else{
                    return -1;
                }
            }
        });


        return list;
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ResponseBody
    public Object startTask(@RequestBody JSONObject params,Model model) throws Exception{
        Map<String,Object> map = new HashMap<>();
        String jfname = params.getString("jsonfilename");
        String jsonpath=null;
        if(isWindows()) {
            jsonpath=windowsJsonDir+jfname;
        }else{
            jsonpath=linuxJsonDir+jfname;
        }
        String jsondata=getDatafromFile(jsonpath);
        //System.out.println("get from local is \n"+jsondata);
        Gson g=new Gson();
        Config configFromLocal=g.fromJson(jsondata,Config.class);
        //configFromLocal.setTimeup(false);
        logger.info("config from file is {}",configFromLocal);

        //判断开始
        String status=configFromLocal.getStatus();
        if(status.equals(status_running)){
            map.put("success",true);
            map.put("message","You have already started the task!！");
            return map;
        }else if(status.equals(status_pause)){
            long threadIdInPause=configFromLocal.getThreadId();
            Thread t_sendm=findThread(threadIdInPause);
            logger.info("thread={}",t_sendm);
            if(t_sendm!=null){
                //System.out.println("thread's id is "+t_sendm.getId());
            }else{
                map.put("success",false);
                map.put("message","thread id is not set!");
                return map;
            }

            if(suspendMap.get(threadIdInPause)==true){

                suspendMap.put(threadIdInPause, false);
                logger.info("use lock to notify thread");
                synchronized (lockMap.get(threadIdInPause)) {
                    lockMap.get(threadIdInPause).notifyAll();    //必须放在同步代码块，来唤醒所有线程
                }
                configFromLocal.setStatus(status_running);
                saveJson2Local(configFromLocal);
                WarningPushSocket.sendMsgToAll("refresh");
                map.put("success",true);
                map.put("message","continue success！");

                return map;
            }

        }

        //状态不是running，pause，则是stop
        configFromLocal.setStatus(status_running);


        SimulatorServiceImpl ssi=new SimulatorServiceImpl();
        MultiThreadTest mtt=new MultiThreadTest(configFromLocal,ssi);
        Thread t1=new Thread(mtt);
        long threadId=t1.getId();
        configFromLocal.setThreadId(threadId);
        saveJson2Local(configFromLocal);//   保存状态到本地
        WarningPushSocket.sendMsgToAll("refresh");

        String threadIdStr=String.valueOf(t1.getId());
        //System.out.println("start.do中，threadId="+threadIdStr);

        //System.out.println("线程t1的threadID为"+threadId);

        String pattern=configFromLocal.getPattern();
        long timedelay=0;
        long timeNumber=(long)configFromLocal.getTime();
        //t1启动前，把这个线程id对应的锁和suspend加到各自map
        lockMap.put(t1.getId(), new ReentrantLock());
        suspendMap.put(t1.getId(),false);

        ssi.setThreadId(t1.getId());
        t1.start();   //发送消息的线程启动，和main线程共同运行

        //设定定时器，到了时间就强制停止


        if(!pattern.equals("遍")) {
            if (pattern.equals("分钟")) {
                timedelay = timeNumber * 60 * 1000;
            } else if (pattern.equals("秒")) {
                timedelay = timeNumber * 1000;
            }
            //System.out.println("timedelay=" + timedelay);

            Timer timer = new Timer();
            MyTimerTask mytt = null;
            if (isWindows()) {
                mytt = new MyTimerTask(t1.getId(), configFromLocal, windowsJsonDir);
            } else {
                mytt = new MyTimerTask(t1.getId(), configFromLocal, linuxJsonDir);
            }
            timer.schedule(mytt, timedelay);
        }



        Thread.currentThread().sleep(500);
        if(configFromLocal.getStatus().equals(status_error)){
            map.put("success",false);
            map.put("message","The configuration may be wrong!!");
            return map;
        }
        map.put("success",true);
        map.put("message","task start running!！");


        return map;
    }

    @RequestMapping(value = "/pause", method = RequestMethod.POST)
    @ResponseBody
    public Object pauseTask(@RequestBody JSONObject params) throws IOException {
        Map<String,Object> map = new HashMap<>();
        //1.拿到本地json对应的对象
        String jfname = params.getString("jsonfilename");
        String jsonpath=null;
        if(isWindows()) {
            jsonpath=windowsJsonDir+jfname;
        }else{
            jsonpath=linuxJsonDir+jfname;
        }
        String jsondata=getDatafromFile(jsonpath);
        //System.out.println("get from local is \n"+jsondata);
        Gson g=new Gson();
        Config configFromLocal=g.fromJson(jsondata,Config.class);
        logger.info("config from file is {}",configFromLocal);
        //判断状态
        if(!configFromLocal.getStatus().equals(status_running)){
            map.put("success",false);
            map.put("message","The task in not running!!");
            return map;

        }



        //2.用线程id去找线程
        String threadIdStr=String.valueOf(configFromLocal.getThreadId());
        if(threadIdStr==null||threadIdStr.equals("")||threadIdStr.equals("0")){
            //System.out.println("request域中没有threadId1(pause)，说明之前没有点过start，直接返回到index");
            map.put("success",false);
            map.put("message","You don't start the task yet!");
            return map;
        }

        long threadIdInPause=configFromLocal.getThreadId();
        Thread t_sendm=findThread(threadIdInPause);
        logger.info("thread={}",t_sendm);
        if(t_sendm!=null){
            //System.out.println("thread's id is "+t_sendm.getId());
        }else{
            map.put("success",false);
            map.put("message","thread id is not set!");
            return map;


        }

        if(suspendMap.get(threadIdInPause)==true){


//            suspendMap.put(threadIdInPause, false);
//            System.out.println("modify suspend is "+suspendMap.get(threadIdInPause));
//            System.out.println("use lock to notify all:");
//            synchronized (lockMap.get(threadIdInPause)) {
//                lockMap.get(threadIdInPause).notifyAll();    //必须放在同步代码块，来唤醒所有线程
//            }
//
//            map.put("success",true);
//            map.put("message","notify success！");
//            configFromLocal.setStatus(status_running);
//            saveJson2Local(configFromLocal);
//            return map;
            return map;
        }else{

            suspendMap.put(threadIdInPause, true);
            logger.info("modify suspend -> {}",suspendMap.get(threadIdInPause));
            map.put("success",true);
            map.put("message","pause success！");
            configFromLocal.setStatus(status_pause);
            saveJson2Local(configFromLocal);
            WarningPushSocket.sendMsgToAll("refresh");
            return map;
        }

    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    @ResponseBody
    public Object stopTask(@RequestBody JSONObject params) throws IOException {
        //1.本地读Json, 转成Config对象
        Map<String,Object> map = new HashMap<>();
        String jfname = params.getString("jsonfilename");
        String jsonpath=null;
        if(isWindows()) {
            jsonpath=windowsJsonDir+jfname;
        }else{
            jsonpath=linuxJsonDir+jfname;
        }
        String jsondata=getDatafromFile(jsonpath);
        //System.out.println("get from local is \n"+jsondata);
        Gson g=new Gson();
        Config configFromLocal=g.fromJson(jsondata,Config.class);
        logger.info("config from file is {}",configFromLocal);

        //2. 判断状态
        if(configFromLocal.getStatus().equals(status_stop)){
            map.put("success",false);
            map.put("message","The task is not running or on pause!");

            return map;
        }




        //3.
        String threadIdStr=String.valueOf(configFromLocal.getThreadId());

        if(threadIdStr==null||threadIdStr.equals("")||threadIdStr.equals("0")){
            //有时候出现bug， 状态是running，但是threadId=0，此时应该能够停止
            if(configFromLocal.getStatus().equals(status_running)){
                configFromLocal.setStatus(status_stop);
                saveJson2Local(configFromLocal);
                WarningPushSocket.sendMsgToAll("refresh");
                map.put("success",false);
                map.put("message","Some problem may occur, now force task to stop...");
                return map;
            }


            //System.out.println("you don't click start，return");
            map.put("success",false);
            map.put("message","You don't start the task yet!");
            return map;
        }
        long threadId=Long.parseLong(threadIdStr);
        Thread t_sendm=findThread(threadId);
        if(t_sendm!=null){
            logger.info("force to stop thread:{}",t_sendm.getName());
            t_sendm.stop();
            map.put("success",true);
            map.put("message","stop task success!");
            configFromLocal.setThreadId(0);
            configFromLocal.setStatus(status_stop);
            saveJson2Local(configFromLocal);
            WarningPushSocket.sendMsgToAll("refresh");
            return map;
        }





        return map;
    }


    @RequestMapping(value = "/deleteJsonFile", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteJsonFile(@RequestBody JSONObject params, HttpServletRequest request) throws IOException {
        Map<String,Object> map = new HashMap<>();
        String jfname = params.getString("jsonfilename");
        logger.info("json file name is {}",jfname);
        String jsonpath=isWindows()?windowsJsonDir+jfname:linuxJsonDir+jfname;
        String jsondata=getDatafromFile(jsonpath);
        //System.out.println("get from local is \n"+jsondata);
        Gson g=new Gson();
        Config configFromLocal=g.fromJson(jsondata,Config.class);
        logger.info("config from file is {}",configFromLocal);
        //--------删除文件开始---------------

        String[] namelist=jfname.split("json");
        String deletefilepath=configFromLocal.getFilepath();
        String deletejsonpath=isWindows()?windowsJsonDir+jfname:linuxJsonDir+jfname;
        File deletejson=new File(deletejsonpath);
        File deletefile=new File(deletefilepath);
        boolean deleteflag=deletejson.delete();
        boolean deletefileflag=deletefile.delete();
        if(deleteflag&deletefileflag){
            logger.info("delete task success...");
            WarningPushSocket.sendMsgToAll("refresh");
            map.put("success",true);
            map.put("message","delete success！");
        }else{
            logger.info("delete process failed...");
        }
        //---------删除文件结束--------------
        return map;
    }


    @RequestMapping(value = "/updateTask", method = RequestMethod.POST)
    public String updateTask(Config config,MultipartFile file, HttpServletRequest request) throws IOException, ServletException {

        //先把本来存在/fileDir下的文件删掉
        //1.拿到本地json对应的对象
        String jfname = config.getJsonfilename();
        String jsonpath=null;
        if(isWindows()) {
            jsonpath=windowsJsonDir+jfname;
        }else{
            jsonpath=linuxJsonDir+jfname;
        }
        String jsondata=getDatafromFile(jsonpath);
        Gson g=new Gson();
        Config configFromLocal=g.fromJson(jsondata,Config.class);
        logger.info("config from file is {}",configFromLocal);
        if(!file.isEmpty()) {
            //获取file path,删掉
            File f = new File(configFromLocal.getFilepath());
            boolean f_delete=f.delete();
            logger.info("user rechoose the file, delete previous file ...{}",f_delete);
            logger.info("after edit, config is {}",config);
            config.setFilename(file.getOriginalFilename());
            String fp = request.getServletContext().getRealPath("/");

            //1.转存文件到tomcat目录下(修改了config中的file部分，有)
            transferFile(request, file, config);

            //2.把config对应的json数据存在本地的txt文件中
            saveJson2Local(config);
        }else{
            //如果file输入为空,filename和filepath从之前那个拿就行
            //System.out.println("file input is empty....");
            config.setFilename(configFromLocal.getFilename());
            config.setFilepath(configFromLocal.getFilepath());
            saveJson2Local(config);

        }
        WarningPushSocket.sendMsgToAll("refresh");
        logger.info("update task ok...");
        return "service";
    }


    @RequestMapping(value = "/saveTask", method = RequestMethod.POST)
    @ResponseBody
    public Object saveTask(String file_generate_name,Config config,MultipartFile file, HttpServletRequest request) throws IOException, ServletException {
        Map<String, Object> map = new HashMap<>();
        if (file_generate_name.length()>0){
            try {
                config.setFilename(file_generate_name);
                String file_generate_path = isWindows() ? windowsFieldFileDir + File.separator + file_generate_name :
                        linuxFieldFileDir + File.separator + file_generate_name;
                File file_generate = new File(file_generate_path);

                copy_file_generate(config, file_generate);
                saveJson2Local(config);
                map.put("success",true);
                map.put("message","save task ok...");
            }catch (Exception e){
                logger.error(e.getMessage());
                map.put("success",false);
                map.put("message","save task fail...");
            }

        }else {
            try {
                config.setFilename(file.getOriginalFilename());
                //System.out.println(config);
                //String fp=request.getServletContext().getRealPath("/");

                //1.转存文件到tomcat目录下
                transferFile(request, file, config);

                //2.把config对应的json数据存在本地的txt文件中
                saveJson2Local(config);
                map.put("success",true);
                map.put("message","save task ok...");
                logger.info("save task ok...");
            }catch (Exception e){
                logger.error(e.getMessage());
                map.put("success",false);
                map.put("message","save task fail...");
                logger.info("save task failed...");
            }
        }
        WarningPushSocket.sendMsgToAll("refresh");
        //return "service";
        return map;
    }


    @RequestMapping(value = "/saveField", method = RequestMethod.POST)
    @ResponseBody
    public Object saveField(@RequestBody JSONObject params, Model model) throws Exception{
        Map<String,Object> map = new HashMap<>();
        Gson gson=new Gson();

        FieldForSimulator ffs=new FieldForSimulator();

        ffs.setName(params.getString("field_name"));
        ffs.setType(params.getString("field_type"));
        ffs.setFixed(params.getString("fixed_value"));
        ffs.setSuffix(params.getString("suffix_value"));
        ffs.setFormat(params.getString("date_format"));
        ffs.setOffset(params.getString("date_offset"));
        ffs.setDate_interval(params.getString("date_interval"));
        ffs.setStart(params.getString("number_start_value"));
        ffs.setStep(params.getString("number_step"));
        ffs.setNumber_type(params.getString("number_type"));
        ffs.setRange_left(params.getString("number_range_left"));
        ffs.setRange_right(params.getString("number_range_right"));
        ffs.setDecimal_point(params.getString("decimal_point"));

        logger.info("field is {}",ffs);
        ffs.setExample_value();
        saveField2Local(ffs);
        map.put("success",true);
        map.put("message","save field success！");
        logger.info("");
        return map;
    }


    @RequestMapping(value = "/upField", method = RequestMethod.POST)
    @ResponseBody
    public Object upField(@RequestBody JSONObject params,Model model) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String fieldname = params.getString("field_name");
        System.out.println("upField......fieldname is "+fieldname);

        String fieldDir=null;
        String target_path=null;
        if(isWindows()) {
            fieldDir=windowsFieldDir;
            target_path=fieldDir+File.separator+fieldname+".txt";
        }else{
            fieldDir=LinuxFieldDir;
            target_path=fieldDir+File.separator+fieldname;
        }
        String target_json=getDatafromFile(target_path);
        System.out.println("target_json is "+target_json);
        Gson gson=new Gson();
        FieldForSimulator target_obj=gson.fromJson(target_json,FieldForSimulator.class);
        System.out.println("target_obj is "+target_obj);
        int target_index=target_obj.getIndex();
        System.out.println(target_obj.getName()+", index is "+target_obj.getIndex());
        if(target_index==1){
            map.put("success",false);
            map.put("message","cannot up...");
            return map;
        }
        //target_obj.setIndex(target_index-1);

        File[] filelist=getFieldFilesArray();
        if(filelist!=null) {
            for (int i = 0; i < filelist.length; i++) {
                File temp = filelist[i];
                String path = temp.getAbsolutePath();
                String jsondata = getDatafromFile(path);
                FieldForSimulator ffs = gson.fromJson(jsondata, FieldForSimulator.class);
                if(ffs.getIndex()==target_index-1){
                    //System.out.println("target_index-1 is "+target_obj.getIndex());
                    //System.out.println("find...  "+ffs.getName()+", index is "+ffs.getIndex());
                    ffs.setIndex(target_index);
                    saveField2Local(ffs);
                    target_obj.setIndex(target_index-1);
                    saveField2Local(target_obj);
                    break;
                }


            }
        }
        map.put("success",true);
        map.put("message","field up ok...");
        logger.info("field({}) up ok...",target_obj.getName());
        return map;
    }

    @RequestMapping(value = "/downField", method = RequestMethod.POST)
    @ResponseBody
    public Object downField(@RequestBody JSONObject params,Model model) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String fieldname = params.getString("field_name");
        System.out.println("fieldname is "+fieldname+"|||");
        //System.out.println("downField......fieldname is "+fieldname);

        String fieldDir=null;
        String target_path=null;
        if(isWindows()) {
            fieldDir=windowsFieldDir;
            target_path=fieldDir+File.separator+fieldname+".txt";
        }else{
            fieldDir=LinuxFieldDir;
            target_path=fieldDir+File.separator+fieldname;
        }
        String target_json=getDatafromFile(target_path);
        //System.out.println("target_json is "+target_json);
        Gson gson=new Gson();
        FieldForSimulator target_obj=gson.fromJson(target_json,FieldForSimulator.class);
        System.out.println("target_obj is "+target_obj);
        int target_index=target_obj.getIndex();
        File[] filelist=getFieldFilesArray();
        if(target_index==filelist.length){
            map.put("success",false);
            map.put("message","cannot down...");
            return map;
        }
        if(filelist!=null) {
            for (int i = 0; i < filelist.length; i++) {
                File temp = filelist[i];
                String path = temp.getAbsolutePath();
                String jsondata = getDatafromFile(path);
                FieldForSimulator ffs = gson.fromJson(jsondata, FieldForSimulator.class);
                if(ffs.getIndex()==target_index+1){
                    ffs.setIndex(target_index);
                    saveField2Local(ffs);
                    target_obj.setIndex(target_index+1);
                    saveField2Local(target_obj);
                    break;
                }

            }
        }
        map.put("success",true);
        map.put("message","field down ok...");
        logger.info("field({}) down ok...",target_obj.getName());
        return map;
    }

    @RequestMapping(value = "/deleteField", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteField(@RequestBody JSONObject params,Model model) throws Exception{
        Map<String, Object> map = new HashMap<>();
        String fieldname = params.getString("field_name");
        String fieldDir=isWindows()?windowsFieldDir:LinuxFieldDir;
        String target_path=isWindows()?fieldDir+File.separator+fieldname+".txt":fieldDir+File.separator+fieldname;
        String target_json=getDatafromFile(target_path);
        Gson gson=new Gson();
        FieldForSimulator target_obj=gson.fromJson(target_json,FieldForSimulator.class);
        int target_index=target_obj.getIndex();
        List<FieldForSimulator> fieldList= getFieldList();  //得到按Index排好序的对象list
        for(int i=target_index;i<fieldList.size();i++){
            FieldForSimulator ffs=fieldList.get(i);
            ffs.setIndex(ffs.getIndex()-1);
            saveField2Local(ffs);
        }
        //删除field
        File field_delete_file=new File(target_path);
        boolean delete_flag=field_delete_file.delete();
        if(delete_flag){
            map.put("success",true);
            map.put("message","delete field ok...");
            logger.info("delete field({}) ok...",fieldname);
        }else{
            map.put("success",false);
            map.put("message","delete field failed...");
            logger.info("delete field({}) failed...",fieldname);
        }
        return map;
    }


    @RequestMapping(value = "/field2file", method = RequestMethod.POST)
    @ResponseBody
    public Object field2file(Model model) throws Exception{
        Map<String, Object> map = new HashMap<>();
        try {
            String field2file_path = isWindows() ? windowsFieldFileDir : linuxFieldFileDir;
            File fieldFileDir = new File(field2file_path);
            if (!fieldFileDir.exists()) {
                fieldFileDir.mkdirs();
            }
            //得到经过按index排序后的列表
            List<FieldForSimulator> list = getFieldList();
            String result_json = field2file_service(list);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String file_name_suffix = sdf.format(new Date());
            String file_name = isWindows() ? "data" + file_name_suffix + ".txt" : "data" + file_name_suffix;
            String file_path = isWindows() ? windowsFieldFileDir + File.separator + file_name : linuxFieldFileDir + File.separator + file_name;
            saveDataToFile(file_path, result_json);

            map.put("file_path", file_path);
            map.put("file_name", file_name);
            map.put("success", true);
            map.put("message", "field2file ok...");
            logger.info("save fields to txt file ok...");
        }catch (Exception e){
            System.out.println(e);
            map.put("success", false);
            map.put("message", "field2file fail...");
            logger.info("save fields to txt file failed...");

        }
        return map;
    }


    public static String field2file_service(List<FieldForSimulator> list){

        String result="";
        for(int i=0;i<500;i++){
            result=result+"{";
            for(int j=0;j<list.size();j++){

                if(j!=list.size()-1) {
                    result = result + "\"" + list.get(j).getName() +"\""
                            +":"
                            +"\""+list.get(j).getJson_value(i)+"\""+",";
                }else{
                    result=result + "\"" + list.get(j).getName() +"\""
                            +":"
                            +"\""+list.get(j).getJson_value(i)+"\"";
                }
            }
            result=result+"}\r\n";
        }
        //System.out.println(result);

        return result;
    }


    //获取field文件夹内的文件数量（即字段数量）
    public static int getFileNumOfField(){
        File[] filelist = getFieldFilesArray();
        return filelist.length;

    }


    //把FieldForSimulator对象对应的json数据存在本地
    public static void saveField2Local(FieldForSimulator ffs){
        String fieldDirPath=isWindows()?windowsFieldDir:LinuxFieldDir;
        File fieldDir=new File(fieldDirPath);
        if(!fieldDir.exists()){
            fieldDir.mkdirs();
        }

        int fileNum=getFileNumOfField();
        //System.out.println("number of file is "+fileNum);
        if(!(ffs.getIndex()>0)) {
            ffs.setIndex(fileNum+1);    //没有设置过才用这种方式。
        }

        Gson gson=new Gson();
        String jsonstr=gson.toJson(ffs);
        //System.out.println("jsonstr= "+jsonstr);
        String jsonpath=null;
        if(isWindows()) {
            jsonpath=windowsFieldDir;
        }else{
            jsonpath=LinuxFieldDir;
        }
        File dirfile=new File(jsonpath);
        if(!dirfile.exists()){
            dirfile.mkdirs();   //若目录不存在，则创建该文件夹
            //System.out.println("jsonDir不存在，则创建");
        }
        if(isWindows()) {
            jsonpath=jsonpath+File.separator+ffs.getName()+".txt";
        }else{
            jsonpath=jsonpath+File.separator+ffs.getName();
        }
        //System.out.println("jsonpath is "+jsonpath);
        saveDataToFile(jsonpath,jsonstr);
    }

    //把Config对象对应的json数据存在本地
    public static void saveJson2Local(Config config){

        Gson gson=new Gson();
        String jsonstr=gson.toJson(config);   //Multipartfile 有问题
        String jsonpath=null;
        if(isWindows()) {
            jsonpath=windowsJsonDir;
        }else{
            jsonpath=linuxJsonDir;
        }
        File dirfile=new File(jsonpath);
        if(!dirfile.exists()){
            dirfile.mkdirs();   //若目录不存在，则创建该文件夹
            //System.out.println("jsonDir不存在，则创建");
        }
        jsonpath+=config.getJsonfilename();
        saveDataToFile(jsonpath,jsonstr);
    }


    private void copy_file_generate(Config config,File file) throws IOException{
        String savedFileName=null;
        String savedDir=null;
        //System.out.println("---------copy starting------------");
        if(file.exists()){
            String fileRealName = file.getName();       //获得原始文件名;
            //System.out.println("filerealname is "+fileRealName);
            int pointIndex =  fileRealName.indexOf(".");            //点号的位置
            String fileSuffix=null;
            if(pointIndex!=-1){
                fileSuffix = fileRealName.substring(pointIndex); //截取文件后缀
            }else{
                fileSuffix="";
            }
            UUID FileId = UUID.randomUUID();                        //生成文件的前缀包含连字符
            savedFileName = FileId.toString().replace("-","").concat(fileSuffix);       //文件存取名

            String jsonname=null;
            Long ts=System.currentTimeMillis();
            String ts_str=ts.toString();
            if(isWindows()) {
                savedDir=windowsFileDir;
                //System.out.println("ts="+ts);
                jsonname=ts_str+"json"+FileId.toString().replace("-","")+".txt";

            }else {
                //savedDir = "/root/fileDir";
                savedDir=linuxFileDir;
                jsonname=ts_str+"json"+FileId.toString().replace("-","");
            }
            //System.out.println("jsonfilename= "+jsonname);
            //System.out.println("jsonname in config is "+config.getJsonfilename());
            if(config.getJsonfilename()==null) {
                config.setJsonfilename(jsonname);
            }else{
                //System.out.println("json file name exists, so this is update");
            }
            //System.out.println("savedDir is "+savedDir);

            File dirfile=new File(savedDir);

            if(!dirfile.exists()){
                dirfile.mkdirs();   //若目录不存在，则创建该文件夹
                //System.out.println("fileDir不存在，则创建");
            }

            File savedFile = new File(savedDir,savedFileName);

            Files.copy(file.toPath(), savedFile.toPath());


        }else{
            //file为空
            //System.out.println("upload file is "+file);
        }
        //System.out.println("---------copying ending------------");
        //把config中的filepath修改好
        String filepath=savedDir+File.separator+savedFileName;
        //System.out.println("filepath="+filepath);
        config.setFilepath(filepath);

        System.out.println("after copying file_generate, config is \n"+config);


    }


    //转存文件到tomcat目录下
    private void transferFile(HttpServletRequest request, MultipartFile file,Config config) throws IOException {
        String savedFileName=null;
        String savedDir=null;
        //System.out.println("---------upload starting------------");
        if(!file.isEmpty()){
            //以下的代码是将文件file重新命名并存入Tomcat的webapps目录下项目的下级目录fileDir
            String fileRealName = file.getOriginalFilename();       //获得原始文件名;
            //System.out.println("filerealname is "+fileRealName);
            int pointIndex =  fileRealName.indexOf(".");            //点号的位置
            String fileSuffix=null;
            if(pointIndex!=-1){
                fileSuffix = fileRealName.substring(pointIndex); //截取文件后缀
            }else{
                fileSuffix="";
            }
            UUID FileId = UUID.randomUUID();                        //生成文件的前缀包含连字符
            savedFileName = FileId.toString().replace("-","").concat(fileSuffix);       //文件存取名
            String jsonname=null;
            Long ts=System.currentTimeMillis();
            String ts_str=ts.toString();
            if(isWindows()) {
                savedDir=windowsFileDir;
                jsonname=ts_str+"json"+FileId.toString().replace("-","")+".txt";
                //savedDir = request.getSession().getServletContext().getRealPath("fileDir"); //获取服务器指定文件存取路径
            }else {
                //savedDir = "/root/fileDir";
                savedDir=linuxFileDir;
                jsonname=ts_str+"json"+FileId.toString().replace("-","");
            }
            if(config.getJsonfilename()==null) {
                config.setJsonfilename(jsonname);
            }
            File dirfile=new File(savedDir);
            if(!dirfile.exists()){
                dirfile.mkdirs();   //若目录不存在，则创建该文件夹
                //System.out.println("fileDir不存在，则创建");
            }

            File savedFile = new File(savedDir,savedFileName);
            boolean isCreateSuccess = savedFile.createNewFile();
            if(isCreateSuccess){
               //System.out.println("create success");
                file.transferTo(savedFile);  //转存文件
            }

        }else{
            //file为空
            System.out.println("upload file is "+file);
        }
        //System.out.println("---------upload ending------------")	;

        //把config中的filepath修改好
        String filepath=savedDir+File.separator+savedFileName;
        //System.out.println("filepath="+filepath);
        config.setFilepath(filepath);
        logger.info("after transfer and modify filepath , config is \n {}",config);


    }

    //把字符串存到本地的文件
    public static void saveDataToFile(String filepath,String data) {
        BufferedWriter writer = null;
        File file = new File(filepath);

        //如果文件不存在，则新建一个
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //写入
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("write file success...");
    }
    //从本地文件读取
    private String getDatafromFile(String fileName) {

        String Path=fileName;
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
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

    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }


}



class MultiThreadTest implements Runnable{

    SimulatorServiceImpl ss;
    Config config;
    //static String status_stop="initialized";

    public MultiThreadTest(Config config,SimulatorServiceImpl simulatorService ) {
        super();
        this.config = config;
        this.ss=simulatorService;

    }

    public void run(){


        //System.out.println("pattern is "+this.config.getPattern());
        int end;
        if(this.config.getPattern().equals("遍")){
            //System.out.println("is bian");
            end=this.config.getTime();
        }else{
            //System.out.println("is not bian");
            end=Integer.MAX_VALUE;
        }
        //System.out.println("end is "+end);

        for (int i=0;i<end;i++){ //end=Integer.max，相当于无限循环

            for(int j=0;j<3;j++){
                //System.out.println(j);
            }
            try {
                ss.sendMessageToMqtt(config);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //System.out.println("use circle, now ready to stop the task");


        //System.out.println("json file exists!!!");

        Thread t_sendm=findThread(this.config.getThreadId());
        //Thread t_sendm=findThread(Thread.currentThread().getId());
        //System.out.println("t_sendm="+t_sendm);
        if(t_sendm!=null){
            //System.out.println("t_sendm这个线程的id是"+t_sendm.getId());
        }
        if(t_sendm!=null){
            //System.out.println("force to stop thread"+t_sendm.getName());
            config.setStatus(PageController.status_stop);
            config.setThreadId(0);
            PageController.saveJson2Local(config);
            try {
                WarningPushSocket.sendMsgToAll("refresh");
            } catch (IOException e) {
                e.printStackTrace();
            }
            t_sendm.stop();
            //request.setAttribute("alreadyStart",false);
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