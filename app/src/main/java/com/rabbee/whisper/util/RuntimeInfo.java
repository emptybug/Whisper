package com.rabbee.whisper.util;

import android.content.Context;
import android.os.Handler;

import com.rabbee.whisper.net.P2PReceiver;
import com.rabbee.whisper.net.P2PSender;
import com.rabbee.whisper.obj.FileInfo;
import com.rabbee.whisper.obj.User;

import java.util.List;
import java.util.Set;

/**
 * Created by Rabbee on 2016/7/11.
 */
public class RuntimeInfo {

    //  APP版本
    private static String version = "v1.0";
    //  APP文件夹名字
    private static String appFileFolder = "Whisper";
    //  有SD卡时的文件目录路径
    private static String appMainDir_onSdCard = null;
    //  无SD卡时的文件目录路径
    private static String appMainDir_NoSdCard = null;
    //  主目录路径
    private static String appFileRecvDir = null;
    //  自己
    private static User me;
    //  用集合来保存在线人员
    private static Set<User> onlineFriends;
    //  当前传输文件信息
    private static FileInfo waitingFileInfo;
    //  发送器
    private static P2PSender p2pSender;
    //  接收器
    private static P2PReceiver p2pReceiver;
    //  当前的上下文
    private static Context nowContext;
    //  联系人列表的UI线程控制器
    private static Handler friendsUiHandler;
    //  聊天界面的UI线程控制器
    private static Handler chatUiHandler;


    public static String getVersion() {
        return version;
    }

    public static void setMe(User me) {
        RuntimeInfo.me = me;
    }

    public static User getMe() {
        return me;
    }

    public static void setOnlineFriends(Set<User> onlineFriends) {
        RuntimeInfo.onlineFriends = onlineFriends;
    }

    public static Set<User> getOnlineFriends() {
        return onlineFriends;
    }

    public static void setWaitingFileInfo(FileInfo waitingFileInfo) {
        RuntimeInfo.waitingFileInfo = waitingFileInfo;
    }

    public static FileInfo getWaitingFileInfo() {
        return waitingFileInfo;
    }

    public static Context getNowContext() {
        return nowContext;
    }

    public static void setNowContext(Context nowContext) {
        RuntimeInfo.nowContext = nowContext;
    }

    public static void setP2PReceiver(P2PReceiver p2pReceiver) {
        RuntimeInfo.p2pReceiver = p2pReceiver;
    }

    public static void setP2PSender(P2PSender p2pSender) {
        RuntimeInfo.p2pSender = p2pSender;
    }

    public static P2PReceiver getP2pReceiver() {
        return p2pReceiver;
    }

    public static P2PSender getP2pSender() {
        return p2pSender;
    }

    public static void setFriendsUiHandler(Handler friendsUiHandler) {
        RuntimeInfo.friendsUiHandler = friendsUiHandler;
    }

    public static Handler getFriendsUiHandler() {
        return friendsUiHandler;
    }

    public static void setChatUiHandler(Handler chatUiHandler) {
        RuntimeInfo.chatUiHandler = chatUiHandler;
    }

    public static Handler getChatUiHandler() {
        return chatUiHandler;
    }

    public static String getAppFileFolder() {
        return appFileFolder;
    }

    public static void setAppMainDir_NoSdCard(String appMainDir_NoSdCard) {
        RuntimeInfo.appMainDir_NoSdCard = appMainDir_NoSdCard;
    }

    public static void setAppMainDir_onSdCard(String appMainDir_onSdCard) {
        RuntimeInfo.appMainDir_onSdCard = appMainDir_onSdCard;
    }

    public static void setAppFileRecvDir(String appFileRecvDir) {
        RuntimeInfo.appFileRecvDir = appFileRecvDir;
    }

    public static String getAppFileRecvDir() {
        return appFileRecvDir;
    }

    public static String getAppMainDir_NoSdCard() {
        return appMainDir_NoSdCard;
    }

    public static String getAppMainDir_onSdCard() {
        return appMainDir_onSdCard;
    }
}
