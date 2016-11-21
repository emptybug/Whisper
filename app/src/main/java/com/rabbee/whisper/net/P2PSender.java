package com.rabbee.whisper.net;

import android.util.Log;

import com.rabbee.whisper.obj.DataProtocol;
import com.rabbee.whisper.obj.DataStream;
import com.rabbee.whisper.obj.FileInfo;
import com.rabbee.whisper.obj.User;
import com.rabbee.whisper.util.NetUtil;
import com.rabbee.whisper.util.RuntimeInfo;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * Created by Rabbee on 2016/7/11.
 */
public class P2PSender {

    private String TAG = "P2PSender:";

    public P2PSender()
    {

    }

    public DataStream getNewData(User friend, int type, String content)
    {
        DataStream dat = new DataStream(RuntimeInfo.getMe());
        dat.setReceiver(friend);
        dat.setType(type);
        dat.setTimeMillis(System.currentTimeMillis());
        Log.d(TAG, "getNewData: Time:" + new Date(dat.getTimeMillis()));
        dat.setBody(content);
        return dat;
    }

    public void sendDataStream(DataStream dat)
    {
        ClientThread thread = new ClientThread(dat);
        thread.start();
    }

    public void sendFileToOne(User friend, FileInfo info)
    {
        final User f = friend;
        final String filePath = info.getPath();
        final File file = new File(info.getPath() + File.separator + info.getName());
        new Thread()
        {
            Socket socket;
            DataOutputStream dos = null;
            FileInputStream fis = null;

            int hasRead = 0;
            long totalWrite = 0;
            byte[] sendBuffer = null;
            boolean isSucceed = false;

            @Override
            public void run() {
                try{
                    socket = new Socket(f.getIp(), DataProtocol.FILE_PORT);
                    dos = new DataOutputStream(socket.getOutputStream());
                    fis = new FileInputStream(file);
                    long fileSize = file.length();
                    Log.d(TAG, "run: 文件" + file.getPath() +  "大小为：" + file.length());
                    sendBuffer = new byte[1024];

                    int lastPersent = -1;
                    while((hasRead = fis.read(sendBuffer, 0, sendBuffer.length)) > 0)
                    {
                        totalWrite += hasRead;
                        int persent = (int)((double)totalWrite/fileSize * 100);
                        if(persent != lastPersent){
                            Log.d("发送文件线程", "run: 已发送" + persent + "%");
                            lastPersent = persent;
                        }
                        dos.write(sendBuffer, 0, hasRead);
                        dos.flush();
                    }
                    if(totalWrite == fileSize)
                    {
                        isSucceed = true;
                    }
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
                finally {
                    try{
                        if(dos != null)
                        {
                            dos.close();
                        }
                        if(fis != null)
                        {
                            fis.close();
                        }
                        if(socket != null)
                        {
                            socket.close();
                        }
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                }
                Log.d("发送文件线程", "run: 文件发送" + (isSucceed ? "成功" : "失败"));
            }
        }.start();
    }

    public void sayHelloToOne(User m, User f)
    {
        final String ip = f.getIp();
        final User me = m;
        new Thread()
        {
            Socket socket;
            ObjectOutputStream os;
            ObjectInputStream oi;

            @Override
            public void run() {
                try{
                    socket = new Socket(ip, DataProtocol.SERVER_PORT);
                    os = new ObjectOutputStream(socket.getOutputStream());
                    oi = new ObjectInputStream(socket.getInputStream());
                    DataStream dat = new DataStream(me);
                    dat.setReceiver(new User(ip));
                    dat.setType(DataProtocol.TYPE_NET_LOGIN);
                    os.writeObject(dat);
                    Log.d(TAG, "run: 发送one登陆消息给:" + socket.getInetAddress() + ":" + socket.getPort());
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
                finally {
                    try{
                        if(oi != null)
                        {
                            oi.close();
                        }
                        if(os != null)
                        {
                            os.close();
                        }
                        if(socket != null)
                        {
                            socket.close();
                        }
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }

                }
            }
        }.start();
    }

    public void sayHelloToNet()
    {
        final String ipIndex = NetUtil.getLocalIpIndex();
        for(int i = 1; i < 256; ++i)
        {
            final int fix = i;
            new Thread()
            {
                String ip = ipIndex + fix;
                Socket socket;
                ObjectOutputStream os;
                ObjectInputStream oi;

                @Override
                public void run() {
                    try{
                        socket = new Socket(ip, DataProtocol.SERVER_PORT);
                        os = new ObjectOutputStream(socket.getOutputStream());
                        oi = new ObjectInputStream(socket.getInputStream());
                        DataStream dat = new DataStream(RuntimeInfo.getMe());
                        dat.setReceiver(new User(ip));
                        dat.setType(DataProtocol.TYPE_NET_LOGIN);
                        os.writeObject(dat);
                        Log.d(TAG, "run: 发送net登陆消息给" + socket.getInetAddress() + ":" + socket.getPort());
                    }
                    catch (IOException ioe)
                    {
                        Log.d(TAG, "run: " + ip + "无响应");

                    }
                    finally {
                        try{
                            if(oi != null)
                            {
                                oi.close();
                            }
                            if(os != null)
                            {
                                os.close();
                            }
                            if(socket != null)
                            {
                                socket.close();
                            }
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }

                    }
                }
            }.start();
        }
    }
}
