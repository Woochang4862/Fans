package com.example.jeong_woochang.fans.POJO;

import java.util.ArrayList;

/**
 * Created by jeong-woochang on 2018. 1. 24..
 */

public class RecyclerViewItem {
    private String num;
    private ArrayList<String> thumbnail;
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

    public void setThumbnail(ArrayList<String> thumbnail) {
        this.thumbnail = thumbnail;
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

    public ArrayList<String> getThumbnail() {
        return thumbnail;
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

    @Override
    public String toString() {
        return "RecyclerViewItem{" +
                "num='" + num + '\'' +
                ", thumbnail=" + thumbnail +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", view='" + view + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
