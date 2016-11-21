package com.rabbee.whisper.net;

import android.os.Message;
import android.util.Log;

import com.rabbee.whisper.obj.DataProtocol;
import com.rabbee.whisper.obj.DataStream;
import com.rabbee.whisper.obj.User;
import com.rabbee.whisper.util.RuntimeInfo;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Rabbee on 2016/7/12.
 */
public class ClientThread extends Thread {

    private String TAG = "ClientThread:";

    private Socket socket;

    private User friend;
    private DataStream dat;

    private ObjectInputStream oi;
    private ObjectOutputStream os;

    public ClientThread(DataStream dat)
    {
        this.dat = dat;
        this.friend = dat.getReceiver();
    }

    @Override
    public void run() {

        try
        {
            socket = new Socket(dat.getReceiver().getIp(), DataProtocol.SERVER_PORT);
            Log.d(TAG, "run: 一个客户端线程已连接");
            os = new ObjectOutputStream(socket.getOutputStream());
            oi = new ObjectInputStream(socket.getInputStream());
            Log.d(TAG, dat.getReceiver().getIp() + " run: 发送一段对象流给" + friend.getIp());

            os.writeObject(dat);
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

    public void sendDataStream(DataStream dat)
    {
        this.dat = dat;
    }

    private void notifyFriendsUiChange()
    {
        Message msg = new Message();
        msg.what = DataProtocol.TYPE_UI_EXIT;
        msg.obj = friend;
        RuntimeInfo.getFriendsUiHandler().sendMessage(msg);
    }
}
