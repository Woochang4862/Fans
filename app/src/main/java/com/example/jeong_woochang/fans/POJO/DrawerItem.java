package com.example.jeong_woochang.fans.POJO;

/**
 * Created by jeong-woochang on 2018. 5. 16..
 */

public class DrawerItem {
    private String img;
    private String name;
    private String boardName;

    public DrawerItem(String img, String name, String boardName) {
        this.img = img;
        this.name = name;
        this.boardName = boardName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }
}
