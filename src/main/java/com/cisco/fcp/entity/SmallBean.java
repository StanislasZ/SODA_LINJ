package com.cisco.fcp.entity;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class SmallBean {

    String host;
    String filename;

    MultipartFile file;
    @Override
    public String toString() {
        return "SmallBean{" +
                "host='" + host + '\'' +
                ", file='" + file.getOriginalFilename() + '\'' +
                '}';
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
