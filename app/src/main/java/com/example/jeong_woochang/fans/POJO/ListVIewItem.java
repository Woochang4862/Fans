package com.example.jeong_woochang.fans.POJO;

/**
 * Created by jeong-woochang on 2018. 1. 24..
 */

public class ListVIewItem {
    private String num;
    private String[] sumnail;
    private String title;
    private String name;
    private String date;
    private String view;
    private String href;

    public void setNum(String num) {
        this.num = num;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSumnail(String[] sumnail) {
        this.sumnail = sumnail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getNum() {
        return num;
    }

    public String getTitle() {
        return title;
    }

    public String[] getSumnail() {
        return sumnail;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getView() {
        return view;
    }

    public String getHref() {
        return href;
    }
}
