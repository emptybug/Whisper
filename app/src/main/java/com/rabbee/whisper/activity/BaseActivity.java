package com.rabbee.whisper.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.rabbee.whisper.util.RuntimeInfo;

/**
 * Created by Rabbee on 2016/7/12.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        RuntimeInfo.setNowContext(this);
    }
}
