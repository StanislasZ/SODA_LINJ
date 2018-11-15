package com.cisco.fcp.entity;

public class ConfigForJson {
    String taskname;
    String host;
    String port;
    int interval;  //发送间隔时间(s)
    String topic;
    int time;  //总时长
    String pattern;
    String filepath;
    String jsonfilename;   //要从本地读json，这个文件名是unique的
    String status;
    long threadId;

    @Override
    public String toString() {
        return "ConfigForJson{" +
                "taskname='" + taskname + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", interval=" + interval +
                ", topic='" + topic + '\'' +
                ", time=" + time +
                ", pattern='" + pattern + '\'' +
                ", filepath='" + filepath + '\'' +
                ", jsonfilename='" + jsonfilename + '\'' +
                ", threadId=" + threadId +
                ", status=" + status +
                '}';
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

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getJsonfilename() {
        return jsonfilename;
    }

    public void setJsonfilename(String jsonfilename) {
        this.jsonfilename = jsonfilename;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
