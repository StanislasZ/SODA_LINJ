package com.cisco.fcp.entity;



public class Config {
    String taskname;

    //MultipartFile file;
    String host;
    String port;
    int interval;  //发送间隔时间(s)
    String topic;
    int time;  //总时长
    String pattern;
    String filepath;
    String filename;
    String jsonfilename;   //要从本地读json，这个文件名是unique的
    String status;
    long threadId;

    //boolean isTimeup;


    public Config() {  //空参构造函数
        this.status="ready";
        //this.isTimeup=false;
    }

    @Override
    public String toString() {
        return "Config{" +
                //"filename=" + file.getOriginalFilename() +
                "taskname='" + taskname + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", interval=" + interval +
                ", topic='" + topic + '\'' +
                ", time=" + time +
                ", pattern='" + pattern + '\'' +
                ", filepath='" + filepath + '\'' +
                ", filename='" + filename + '\'' +
                ", jsonfilename='" + jsonfilename + '\'' +
                ", status='" + status + '\'' +
                ", threadId='" + threadId + '\'' +
                '}';
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }



    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getJsonfilename() {
        return jsonfilename;
    }

    public void setJsonfilename(String jsonfilename) {
        this.jsonfilename = jsonfilename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }



    public ConfigForJson trans2ForJson(){

        ConfigForJson cfj=new ConfigForJson();
        cfj.setTaskname(this.taskname);
        cfj.setHost(this.host);
        cfj.setPort(this.port);
        cfj.setInterval(this.interval);
        cfj.setTopic(this.topic);
        cfj.setTime(this.time);
        cfj.setPattern(this.pattern);
        cfj.setJsonfilename(this.jsonfilename);
        cfj.setThreadId(this.threadId);
        cfj.setStatus(this.status);
        cfj.setFilepath(this.filepath);
        cfj.setThreadId(this.threadId);

        return cfj;

    }



}

