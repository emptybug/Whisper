package com.rabbee.whisper.obj;

import java.io.Serializable;

/**
 * Created by Rabbee on 2016/7/13.
 */
public class FileInfo implements Serializable {

    private String name;

    private long length;

    private String path;

    public FileInfo(String name, long length, String path)
    {
        this.name = name;
        this.length = length;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public long getLength() {
        return length;
    }

    public String getPath() {
        return path;
    }
}
