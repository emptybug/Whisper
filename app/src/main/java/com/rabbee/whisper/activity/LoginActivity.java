package com.rabbee.whisper.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.rabbee.whisper.R;
import com.rabbee.whisper.obj.User;
import com.rabbee.whisper.util.NetUtil;
import com.rabbee.whisper.util.RuntimeInfo;

import java.io.File;

/**
 * Created by Rabbee on 2016/7/11.
 */
public class LoginActivity extends BaseActivity {

    private TextInputLayout textInputLayout;
    private EditText name;
    private Button button;
    private ProgressBar progressBar;
    private User me;
    private LinearLayout linearLayout;
    private int height = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if(new File("/mnt/sdcard").exists())
        {
            Log.d("patg", "sd卡目录存在");
            RuntimeInfo.setAppMainDir_onSdCard(Environment.getExternalStorageDirectory().getPath());
            RuntimeInfo.setAppFileRecvDir(RuntimeInfo.getAppMainDir_onSdCard() + File.separator + RuntimeInfo.getAppFileFolder());
        }
        else {

        }
        File fileRecv = new File(RuntimeInfo.getAppFileRecvDir());
        Log.d("path", "app目录：" + fileRecv.getPath());
        if(!fileRecv.exists())
        {
            Log.d("path", "app目录：" + fileRecv.getPath());
            if(fileRecv.mkdirs())
            {
                Log.d("path", "主文件夹创建成功");
            }
        }
        else {
            Log.d("path", "主目录已存在");
        }

        textInputLayout = (TextInputLayout)findViewById(R.id.login_textinputlayout);
        textInputLayout.setErrorEnabled(true);
        name = (EditText) findViewById(R.id.login_name);
        name.setOnKeyListener(
            new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER)
                {
//                    button.callOnClick();
                }
                return false;
            }
        });
        name.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String string = editable.toString();
                        if(string.length() < 1)
                        {
                            textInputLayout.setError("昵称不能为空！");
                        }
                        else {
                            textInputLayout.setError("");
                        }
//                        ((View)linearLayout).scrollTo(0, height);
                    }
                }
        );
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        progressBar.setVisibility(ProgressBar.GONE);

        button = (Button) findViewById(R.id.login_btn);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button.setText("正在登陆...");
                        progressBar.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(LoginActivity.this, FriendsActivity.class);
                        String userName = name.getText().toString();
                        me = new User(NetUtil.getLocalIP());
                        me.setName(userName);
                        RuntimeInfo.setMe(me);
                        startActivity(intent);

                        LoginActivity.this.finish();
                    }
                }
        );

        linearLayout = (LinearLayout) findViewById(R.id.login_llayout);
        controlKeyboardLayout(linearLayout, button);

    }

    /**
     * @param root
     *            最外层布局，需要调整的布局
     * @param scrollToView
     *            被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     */
    private void controlKeyboardLayout(final View root, final View scrollToView) {
        // 注册一个回调函数，当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时调用这个回调函数。
        root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        // 获取root在窗体的可视区域
                        root.getWindowVisibleDisplayFrame(rect);
                        // 当前视图最外层的高度减去现在所看到的视图的最底部的y坐标
                        int rootInvisibleHeight = root.getRootView()
                                .getHeight() - rect.bottom;
                        Log.i("tag", "最外层的高度" + root.getRootView().getHeight());
                        Log.i("tag", "rootInvisibleHeight" + rootInvisibleHeight);
                        // 若rootInvisibleHeight高度大于100，则说明当前视图上移了，说明软键盘弹出了
                        if (rootInvisibleHeight > 200) {
                            //软键盘弹出来的时候
                            int[] location = new int[2];
                            // 获取scrollToView在窗体的坐标
                            scrollToView.getLocationInWindow(location);
                            Log.d("tag", "Button: " + scrollToView.getHeight());
                            Log.d("tag", "location0: " + location[0]);
                            Log.d("tag", "location1: " + location[1]);
                            // 计算root滚动高度，使scrollToView在可见区域的底部
                            int srollHeight = (location[1] + scrollToView
                                    .getHeight()) - rect.bottom;
                            Log.d("tag", "scrollHeight: " + srollHeight);
                            if(height == 0)
                            {
                                height = srollHeight;
                            }
                            root.scrollTo(0, srollHeight);
                        } else {
                            // 软键盘没有弹出来的时候
                            root.scrollTo(0, 0);
                        }
                    }
                });
    }
}
