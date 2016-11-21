package com.rabbee.whisper.util;

import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Rabbee on 2016/7/11.
 */
public class NetUtil {

    private static final String TAG = "NetUtil";

    //获取本地ip地址
    public static String getLocalIP(){

        String ipAddress = "";

        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface networks = en.nextElement();
                // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> address = networks.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress())
                    {
                        ipAddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "获取本地ip地址失败");
            e.printStackTrace();
        }

        System.out.println("本机IP:" + ipAddress);

        return ipAddress;

    }
    //获取IP前缀
    public static String getLocalIpIndex(){

        String str = getLocalIP();
        //  获得形式如：192.168.1.
        if(!str.equals("")){
            return str.substring(0,str.lastIndexOf(".")+1);
        }

        return null;
    }
}
