package com.et.simon.smartlight.usercenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by SIMON on 2017/11/21.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取当前示例的类名
        Log.d("BaseActivity", getClass().getSimpleName());
    }
}
