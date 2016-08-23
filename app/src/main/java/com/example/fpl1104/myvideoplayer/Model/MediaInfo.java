package com.example.fpl1104.myvideoplayer.Model;

/**
 * Created by fpl1104 on 16/6/27.
 */
public class MediaInfo {
    private String url;
    private String fileName;
    private String time;

    public MediaInfo(String url, String fileName, String time) {
        this.url = url;
        this.fileName = fileName;
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
