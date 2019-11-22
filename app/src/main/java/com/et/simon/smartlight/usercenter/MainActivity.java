package com.et.simon.smartlight.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import com.et.simon.smartlight.R;


public class MainActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener {

    private ImageView add_device_btn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //进入配网页面
        add_device_btn = findViewById(R.id.add_device_btn);
        add_device_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AddDeviceActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {

    }

}
