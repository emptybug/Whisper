package com.rabbee.whisper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.rabbee.whisper.R;
import com.rabbee.whisper.activity.adapter.ChatAdapter;
import com.rabbee.whisper.net.FileReceiveService;
import com.rabbee.whisper.obj.DataProtocol;
import com.rabbee.whisper.obj.DataStream;
import com.rabbee.whisper.obj.FileInfo;
import com.rabbee.whisper.obj.User;
import com.rabbee.whisper.util.FileUtils;
import com.rabbee.whisper.util.RuntimeInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rabbee on 2016/7/11.
 */
public class ChatActivity extends BaseActivity {

    private User friend = null;

    private LinearLayout root  = null;

    private EditText inputText = null;

    private Button send = null;

    private ListView datListView = null;

    private ChatAdapter datAdapter = null;

    private List<DataStream> datList = null;

    private Handler uiHandler = null;

    private String currentPath = null;

    private int btn_select = 1;

    private int FILE_SELECT_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        datList = new ArrayList<DataStream>();

        friend = getFriend();

        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        //  设置标题栏名字
        toolbar.setTitle(friend.getName());
        toolbar.setSubtitle(friend.getIp());
        toolbar.setLogo(getResources().getDrawable(R.drawable.header_normal_48dp));
        setSupportActionBar(toolbar);
        //  返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //testMsgUI();

        datAdapter = new ChatAdapter(ChatActivity.this, R.layout.chat_item, datList);
        datListView = (ListView) findViewById(R.id.msg_listView);
        datListView.setAdapter(datAdapter);
        datListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        InputMethodManager imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
                        if(imm.isActive())
                        {
                            imm.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                }
        );
        inputText = (EditText) findViewById(R.id.edit_input);
        inputText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(inputText.getText().toString().equals(""))
                        {
                            btn_select = 1;
                            send.setText("文件");
                        }
                        else{
                            btn_select = 2;
                            send.setText("发送");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }
        );
        send = (Button) findViewById(R.id.sent_input);
        send.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(btn_select == 1)
                        {
//                            String filename = "SONY-Z1-Fv6.8.zip";
                            showFileChooser();
//                            String path = Environment.getExternalStorageDirectory().getPath() + File.separator + filename;
//                            File file = new File(path);
//                            if(file.exists())
//                            {
//                                Log.d("Chat", "onClick: 已选择文件" + path);
//                                DataStream dat = new DataStream(RuntimeInfo.getMe());
//                                dat.setReceiver(friend);
//                                dat.setType(DataProtocol.TYPE_NET_FILE_REQUEST);
//                                dat.setBody(new FileInfo(filename, file.length(), Environment.getExternalStorageDirectory().getPath() + File.separator));
//                                RuntimeInfo.getP2pSender().sendDataStream(dat);
//                            }
//                            else {
//                                try{
//                                    file.createNewFile();
//                                }
//                                catch (IOException ioe)
//                                {
//                                    ioe.printStackTrace();
//                                }
//                                Log.d("Chat", "onClick: 文件不存在");
//                            }

                        }
                        else if(btn_select == 2)
                        {
                            String content = inputText.getText().toString();
                            if(!"".equals(content))
                            {
                                DataStream dat = RuntimeInfo.getP2pSender().getNewData(friend, DataProtocol.TYPE_NET_TEXT, content);
                                //  传给发送器出去
                                RuntimeInfo.getP2pSender().sendDataStream(dat);

                                //  添加到自己的界面
                                Message msg = new Message();
                                msg.obj = dat.clone(DataProtocol.TYPE_UI_RIGHT);
                                msg.what = DataProtocol.TYPE_UI_TEXT;
                                uiHandler.sendMessage(msg);
//                            datList.add(dat.clone(DataProtocol.TYPE_UI_RIGHT));
//                            datAdapter.notifyDataSetChanged();


                                inputText.setText("");
                            }

                        }
                    }
                }
        );

        root = (LinearLayout) findViewById(R.id.root_layout_chat);
        root.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
                        if(imm.isActive())
                        {
                            imm.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                }
        );

        uiHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                DataStream dat = (DataStream) msg.obj;
                switch (msg.what)
                {
                    case DataProtocol.TYPE_UI_TEXT:
                        datList.add(dat);
                        datAdapter.notifyDataSetChanged();
                        //  定位至最后一行
                        datListView.setSelection(datList.size());
                        break;
                    case DataProtocol.TYPE_UI_FILE_DIALOG:
                        final FileInfo info = (FileInfo) dat.getBody();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this)
                                .setTitle("文件通知" + info.getName())
                                .setMessage("是否接收该文件？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DataStream access = new DataStream(RuntimeInfo.getMe());
                                        access.setReceiver(friend);
                                        access.setType(DataProtocol.TYPE_NET_FILE_ACCEPT);
                                        access.setBody(info);
                                        RuntimeInfo.getP2pSender().sendDataStream(access);
                                        Log.d("对话框", "onClick: 确认接收");
                                        Intent intent = new Intent(ChatActivity.this, FileReceiveService.class);
                                        ChatActivity.this.startService(intent);
                                        Log.d("对话框", "onClick: 开启接收文件服务");
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        builder.create().show();
                        break;
                }
            }
        };
        RuntimeInfo.setChatUiHandler(uiHandler);

    }

    private void testMsgUI()
    {
        DataStream msg1 = new DataStream(RuntimeInfo.getMe());
        msg1.setBody("你好~~~");
        datList.add(msg1);
//        Message msg2 = new Message();
//        msg2.setMsgType(Message.TYPE_SENT);
//        msg2.setBody("你好~~~");
//        msgList.add(msg2);
//        Message msg3 = new Message();
//        msg3.setMsgType(Message.TYPE_RECEIVED);
//        msg3.setBody("This is test. Nice talking to you. ");
//        msgList.add(msg3);
    }

    private User getFriend()
    {
        Intent intent = getIntent();
        User friend = (User) intent.getSerializableExtra("user");
//        //  Test
//        User friend = RuntimeInfo.getMe();
        return friend;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String TAG = "文件选择";
        if(requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            Log.d(TAG, "onActivityResult: Uri:" + uri);
            currentPath = FileUtils.getPath(this, uri);
            Log.d("path", "onActivityResult: " + currentPath);

            if(currentPath != null)
            {
                String filename = currentPath.substring(currentPath.lastIndexOf("/") + 1,currentPath.length());
                Log.d(TAG, "onActivityResult: name " + filename);
                String filepath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                Log.d(TAG, "onActivityResult: path " + filepath);
                File file = new File(currentPath);
                DataStream dat = new DataStream(RuntimeInfo.getMe());
                dat.setReceiver(friend);
                dat.setType(DataProtocol.TYPE_NET_FILE_REQUEST);
                dat.setBody(new FileInfo(filename, file.length(),filepath));
                RuntimeInfo.getP2pSender().sendDataStream(dat);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


