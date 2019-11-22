package com.et.simon.smartlight.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.et.simon.smartlight.R;
import com.et.simon.smartlight.utils.PreferenceUtils;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SIMON on 2017/11/21.
 */

public class ApplicationActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取窗口对象
        Window window = getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
/*
        //隐藏状态栏
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
*/
        DoAction();
    }

    //判断是否为第一次启动
    private void DoAction() {
        boolean isFirst = PreferenceUtils.readBoolean(this, "First", "isFirst", true);
        if (isFirst == true){
            PreferenceUtils.write(this, "First", "isFirst", false);
            //初始化时间戳UUID
            String uuid = getUuidString();
            PreferenceUtils.write(this, "uuid", "uuid", uuid);
            ComeingApp(4000);//首次启动界面显示时间长为4s
        } else {
            ComeingApp(2000);
        }
    }

    /**
     * 得到时间戳参数
     * 在应用安装之后保持不变,且应该尽可能的唯一，以便追踪回溯
     *
     * @return 时间戳参数
     */
    private String getUuidString() {
        Long unixTime = System.currentTimeMillis();
        String uuidStr = Long.toHexString(unixTime);
        if (uuidStr.length() > 12) {
            uuidStr = uuidStr.substring(0, 12);
        } else {
            uuidStr = uuidStr + getRadomString(12-uuidStr.length());
        }
        return uuidStr;
    }

    /**
     * 得到随机的十六进制数
     *
     * @param index
     * @return
     */
    private String getRadomString(int index) {
        String Str;
        String hexStr = "";
        for (int i=0; i<index; i++) {
            int rand = new Random().nextInt(15);
            Str = Integer.toHexString(rand);
            hexStr += Str;
        }
        return hexStr;
    }

    /**
     * 启动欢迎界面
     */
    private void Welcome() {
        Intent intent = new Intent();
        intent.putExtra("startUp", "startUp");
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 进入应用
     * @param delay,欢迎界面显示时长
     */
    private void ComeingApp(int delay) {
        setContentView(R.layout.activity_application);
        Timer timer = new Timer();
        timer.schedule(task, delay);

    }

    /**
     * 定时器 延时操作
     */
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Welcome();
        }
    };
}
