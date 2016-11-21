package com.rabbee.whisper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.rabbee.whisper.R;
import com.rabbee.whisper.net.P2PReceiver;
import com.rabbee.whisper.net.P2PSender;
import com.rabbee.whisper.obj.DataProtocol;
import com.rabbee.whisper.obj.DataStream;
import com.rabbee.whisper.obj.User;
import com.rabbee.whisper.util.FriendsList;
import com.rabbee.whisper.util.RuntimeInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FriendsActivity extends BaseActivity {

    private final String TAG = FriendsActivity.class.getName();

    private Toolbar toolbar;

    private ListView listView;
    private Set<User> friends;
    private FriendsList friendsList;

    private Handler friendsUiHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Log.e(TAG, "onCreate: ///////////////////////////////" );
        initVar();
        initLayout();


        //  以下属性需要UI线程控制器控制

        //  设置列表
        friendsList = new FriendsList();
        final List<Map<String, Object>> listItems = friendsList.getList();

        //  设置适配器
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.friend_item,
                new String[] {"header", "name", "ip"},
                new int[] {R.id.header_item, R.id.name_friend_item, R.id.ip_friend_item}
        );

        //  设置已适配列表
        listView = (ListView) findViewById(R.id.friends_listView);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Adapter adapter = parent.getAdapter();
                        Map<String, Object> map = (Map<String, Object>)adapter.getItem(position);
                        User friend = (User)map.get("user");
                        Log.d(TAG, "onItemClick: " + friend.getIp() + "打开聊天窗口");
                        Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
                        Bundle bundle = new Bundle();
                        //  传送目标用户的数据
                        bundle.putSerializable("user", friend);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
        );

        //  联系人页面UI线程控制器设置
        friendsUiHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {

                DataStream dat = (DataStream) msg.obj;
                switch (msg.what)
                {
                    case DataProtocol.TYPE_UI_LOGIN:
                        User friend = dat.getSender();

                        //  更新联系人列表
                        addOneFriend(listItems, friend);
                        simpleAdapter.notifyDataSetChanged();
                        Log.d(TAG, "handleMessage: 一个联系人已添加：" + friend.getName());
                        break;
                    case DataProtocol.TYPE_UI_TEXT:
                        User comingfriend = dat.getSender();
                        friendsList.setFirst(comingfriend);
                        simpleAdapter.notifyDataSetChanged();
                        break;
                    case DataProtocol.TYPE_UI_EXIT:
                        User exitFriend = (User)msg.obj;
                        removeOneFriend(listItems, exitFriend);
                        simpleAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        RuntimeInfo.setFriendsUiHandler(friendsUiHandler);

        /// 以下实例只能创建一次

        P2PReceiver receiver = new P2PReceiver();
        RuntimeInfo.setP2PReceiver(receiver);
        receiver.start();

        P2PSender sender = new P2PSender();
        RuntimeInfo.setP2PSender(sender);
        //  向局域网广播
        sender.sayHelloToNet();

//        addOneFriend(listItems, RuntimeInfo.getMe());

        //test
//        User f = new User("192.168.199.235");
//        f.setName("锦洪");
//        sender.sayHelloToOne(RuntimeInfo.getMe(), f);


//        //test
//        addOneFriend(listItems, new User("192.168.123.123").setName("王五"));
//        addOneFriend(listItems, new User("192.168.123.233").setName("李四"));
//        addOneFriend(listItems, RuntimeInfo.getMe());
//        friendsList.setFirst(RuntimeInfo.getMe());
        simpleAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Intent intent = null;
        switch (id)
        {
            //  “设置”页面
            case R.id.action_settings:
                intent = new Intent(FriendsActivity.this, SettingsActivity.class);
                break;
            //  “关于”页面
            case R.id.about:

                break;
        }
        if(intent != null)
        {
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        for(User friend : friends)
        {
            DataStream dat = new DataStream(RuntimeInfo.getMe());
            dat.setType(DataProtocol.TYPE_NET_EXIT);
            dat.setReceiver(friend);
            dat.setTimeMillis(System.currentTimeMillis());
            RuntimeInfo.getP2pSender().sendDataStream(dat);
        }
        super.onDestroy();
    }

    //  初始化变量
    private void initVar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  在线用户
        friends = new HashSet<User>();
        RuntimeInfo.setOnlineFriends(friends);
    }

    //  初始化界面
    private void initLayout()
    {
        //  设置标题栏名字
//        toolbar.setTitle(me.getName());
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void showDialog()
    {

    }

    private void addOneFriend(List<Map<String, Object>> listItems, User friend)
    {
        //  若集合中已有此人
        if(friendsList.find(friend) != null)
        {
            Log.d(TAG, "reveiveDataStream: 联系人已存在，不回复登陆请求");
            return ;
        }
        friendsList.add(friend);

        //  回复请求
        RuntimeInfo.getP2pSender().sayHelloToOne(RuntimeInfo.getMe(), friend);
        Log.d(TAG, "reveiveDataStream: 回复一条单独登陆请求");
    }

    private void removeOneFriend(List<Map<String, Object>> listItems, User friend)
    {
        if(friends.contains(friend))
        {
            friends.remove(friend);
            Map<String, Object> item = new HashMap<>();
            item.put("header", R.drawable.header_normal);
            item.put("name", friend.getName());
            item.put("ip", friend.getIp());
            item.put("user", friend);
            listItems.remove(item);
        }
    }

}
