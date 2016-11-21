package com.rabbee.whisper.net;

import android.os.Handler;
import android.util.Log;

import com.rabbee.whisper.obj.DataProtocol;
import com.rabbee.whisper.obj.DataStream;
import com.rabbee.whisper.util.RuntimeInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Rabbee on 2016/7/12.
 */
public class ServerThread extends Thread {

    private String TAG;

    private Socket socket;

    private ObjectOutputStream os;
    private ObjectInputStream oi;

    public ServerThread(Socket socket)
    {
        this.socket = socket;
        TAG = "ServerThread: " + socket.getInetAddress().toString() + ": ";
    }


    @Override
    public void run() {
        Log.d(TAG, socket.getInetAddress() + ":" + socket.getPort() + " run: 一个服务器线程正在运行");
        try {
            os = new ObjectOutputStream(socket.getOutputStream());
            oi = new ObjectInputStream(socket.getInputStream());
            DataStream dat = null;
            while((dat = (DataStream) oi.readObject()) != null)
            {
                receiveDataStream(dat);

                //  若为登陆请求，不进入阻塞。
                if(dat.getType() == DataProtocol.TYPE_NET_LOGIN)
                {
                    break;
                }
                //  若为退出请求，不进入阻塞。
                if(dat.getType() == DataProtocol.TYPE_NET_EXIT)
                {
                    break;
                }
            }
        }
        catch (ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
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
                    Log.d(TAG, socket.getInetAddress() + " run: 服务器线程已停止");
                    socket.close();
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    private void receiveDataStream(DataStream dat)
    {
        Log.d(TAG, "receiveDataStream: 向接收器传递数据");
        RuntimeInfo.getP2pReceiver().reveiveDataStream(dat);
    }
}
