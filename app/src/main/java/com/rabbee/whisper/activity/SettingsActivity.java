package com.rabbee.whisper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rabbee.whisper.R;
import com.rabbee.whisper.obj.User;
import com.rabbee.whisper.util.FileUtils;

/**
 * Created by Rabbee on 2016/7/11.
 */
public class SettingsActivity extends BaseActivity {

    private Button fileTest;

    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //  设置标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        toolbar.setTitle(R.string.action_settings);
        setSupportActionBar(toolbar);
        //  返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) findViewById(R.id.input_filename);

        fileTest = (Button) findViewById(R.id.file_test_btn);
        fileTest.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String filename = null;
                        filename = editText.getText().toString();
                        if(filename != null && !filename.equals(""))
                        {
                            FileUtils.saveOneFile(getApplicationContext(), filename + ".txt");
                        }
                    }
                }
        );


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
}
