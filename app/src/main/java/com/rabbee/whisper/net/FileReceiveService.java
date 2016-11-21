package com.rabbee.whisper.net;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.rabbee.whisper.R;
import com.rabbee.whisper.activity.FriendsActivity;
import com.rabbee.whisper.obj.DataProtocol;
import com.rabbee.whisper.obj.FileInfo;
import com.rabbee.whisper.util.RuntimeInfo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Rabbee on 2016/7/13.
 */
public class FileReceiveService extends IntentService {

    private String TAG = "文件后台服务";

    private NotificationManager notificationManager;

    ServerSocket ss;

    public FileReceiveService()
    {
        super("FileReceiveService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try{
            ss = new ServerSocket(DataProtocol.FILE_PORT);
            Log.d(TAG, "onHandleIntent: 开始监听文件服务端口");
            final Socket s = ss.accept();
            Log.d(TAG, "run: 已有文件消息接入，正在运行线程");
            startNotification(0);
            new Thread()
            {
                Socket socket;

                byte[] revByte = null;
                int hasRead = 0;
                DataInputStream dis = null;
                FileOutputStream fos = null;
                boolean isSecceed = false;

                long totalRead = 0;
                long fileSize = RuntimeInfo.getWaitingFileInfo().getLength();

                String filename = RuntimeInfo.getWaitingFileInfo().getName();
                String filePath = RuntimeInfo.getAppFileRecvDir() + File.separator + filename;

                @Override
                public void run() {
                    socket = s;
                    try{
                        Log.d(TAG, "run: 文件线程正在运行……");
                        dis = new DataInputStream(socket.getInputStream());
                        fos = new FileOutputStream(new File(filePath));
                        revByte = new byte[1024];

                        Log.d("接收文件线程", "run: 开始接收数据……");
                        Log.d(TAG, "run: 接收的文件总大小：" + fileSize);
                        int lastPersent = -1;
                        while((hasRead = dis.read(revByte, 0, revByte.length)) > 0)
                        {
                            totalRead += hasRead;
                            int persent = (int)((double)totalRead/fileSize * 100);
                            if(persent != lastPersent){
                                Log.d("接收文件线程", "run: 已接收" + persent + "%");
                                lastPersent = persent;
                                //  更改通知中的进度
                                startNotification(persent);
                            }
                            fos.write(revByte, 0, hasRead);
                            fos.flush();
                        }
                        if(totalRead == fileSize)
                        {
                            isSecceed = true;
                        }

                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    finally {
                        try
                        {
                            if(fos != null)
                            {
                                fos.close();
                            }
                            if(dis != null)
                            {
                                dis.close();
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
                    Log.d("接收文件进程", "run: 接收" + (isSecceed ? "成功" : "失败"));
                }
            }.start();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
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

    private void startNotification(int persent)
    {
        FileInfo info = RuntimeInfo.getWaitingFileInfo();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, FriendsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, R.string.app_name, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(RuntimeInfo.getNowContext())
                .setAutoCancel(false)
                .setTicker("正在接收文件...")
                .setSmallIcon(R.drawable.download)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(info.getName())
                .setContentText("已接收： " + persent + "%")
                .setProgress(100, persent, false)
                .setContentIntent(pendingIntent);
        if(persent == 100)
        {
            builder.setContentText("接收完成！");
        }
        notificationManager.notify(1, builder.build());
    }
}
