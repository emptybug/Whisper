package com.rabbee.whisper.obj;


import android.media.Image;

import java.io.Serializable;

/**
 * Created by Rabbee on 2016/7/11.
 */
public class User implements Serializable {

    //  必须，标识
    private String ip;

    //  可选
    private String name; // 昵称
    private Image icon; //  头像

    public User(String ip)
    {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        if(name == null)
        {
            return "";
        }
        return name;
    }

    public Image getIcon() {
        return icon;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

//    public User setIcon(Image icon) {
//        this.icon = icon;
//        return this;
//    }
}
