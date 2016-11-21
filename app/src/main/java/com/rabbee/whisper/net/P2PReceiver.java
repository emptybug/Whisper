package com.rabbee.whisper.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rabbee.whisper.activity.FriendsActivity;
import com.rabbee.whisper.obj.DataProtocol;
import com.rabbee.whisper.obj.DataStream;
import com.rabbee.whisper.obj.FileInfo;
import com.rabbee.whisper.util.FileUtils;
import com.rabbee.whisper.util.RuntimeInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Created by Rabbee on 2016/7/11.
 */
public class P2PReceiver {

    private String TAG = "接收器：";

    //  监听数据进入端口
    //  每次捕获一个Socket连接就开启一个ServerThread线程对该线程进行读取内容
    //
    public P2PReceiver()
    {

    }

    public void start()
    {
        new Thread()
        {
            ServerSocket ss;

            @Override
            public void run() {
                try
                {
                    Log.d(TAG, "run: 正在监听端口");
                    ss = new ServerSocket(DataProtocol.SERVER_PORT);
                    while(true)
                    {
                        Socket socket = ss.accept();
                        new ServerThread(socket).start();
                    }
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                    Log.e(TAG, "run: 服务器错误");
                }
                finally {
                    try{
                        if(ss != null)
                        {
                            ss.close();
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

    public void reveiveDataStream(DataStream dat)
    {
        Message msg = new Message();
        switch (dat.getType())
        {
            case DataProtocol.TYPE_NET_LOGIN:
                //  登陆请求
                msg.obj = dat;
                msg.what = DataProtocol.TYPE_UI_LOGIN;
                RuntimeInfo.getFriendsUiHandler().sendMessage(msg);
                Log.d(TAG, "reveiveDataStream: 向联系人UI线程控制器转发登陆消息");
                break;
            case DataProtocol.TYPE_NET_TEXT:
                //  文本数据
                //  克隆一个DataStream转发给UI线程
                DataStream datclone = dat.clone(DataProtocol.TYPE_UI_LEFT);
                datclone.setTimeMillis(System.currentTimeMillis());
                msg.obj = datclone;
                msg.what = DataProtocol.TYPE_UI_TEXT;
                Log.d(TAG, "getNewData: RevTime:" + new Date(datclone.getTimeMillis()));
                RuntimeInfo.getChatUiHandler().sendMessage(msg);
                break;
            case DataProtocol.TYPE_NET_FILE_REQUEST:
                //  收到文件请求
                Log.d(TAG, "reveiveDataStream: 收到一条文件请求，添加到进行时");
                RuntimeInfo.setWaitingFileInfo((FileInfo) dat.getBody());

                //  询问用户是否接受
                msg.what = DataProtocol.TYPE_UI_FILE_DIALOG;
                msg.obj = dat;
                RuntimeInfo.getChatUiHandler().sendMessage(msg);

                break;
            case DataProtocol.TYPE_NET_FILE_ACCEPT:
                //  收到对方的允许，立即发送文件
                RuntimeInfo.getP2pSender().sendFileToOne(dat.getSender(), (FileInfo) dat.getBody());
                break;
            case DataProtocol.TYPE_NET_EXIT:
                //  退出消息
                msg.what = DataProtocol.TYPE_UI_EXIT;
                msg.obj = dat;
                RuntimeInfo.getFriendsUiHandler().sendMessage(msg);
                break;
        }
    }
}
