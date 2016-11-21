package com.rabbee.whisper.obj;

import java.io.Serializable;

/**
 * Created by Rabbee on 2016/7/11.
 */
public class DataStream implements Serializable{

    private long timeMillis;    //  时间戳
    private User sender;    //  发送者
    private User receiver;  //  接收者
    /*
        数据类型
        具体在DataProtocol中实现
    */
    private int type;

    /*
    /   主体
    /   可为String
    */
    private Object body;

    public DataStream(User sender)
    {
        this.sender = sender;
    }

    public DataStream clone(int type)
    {
        DataStream dat = new DataStream(sender);
        dat.setBody(body)
            .setType(type)
            .setReceiver(receiver)
            .setTimeMillis(timeMillis);
        return dat;
    }

    public DataStream setBody(Object body) {
        this.body = body;
        return this;
    }

    public DataStream setType(int type) {
        this.type = type;
        return this;
    }

    public DataStream setReceiver(User receiver) {
        this.receiver = receiver;
        return this;
    }

    public DataStream setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
        return this;
    }

    public int getType() {
        return type;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public Object getBody() {
        return body;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }


}
