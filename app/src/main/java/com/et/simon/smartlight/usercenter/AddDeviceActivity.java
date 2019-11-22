package com.et.simon.smartlight.usercenter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.et.simon.smartlight.R;
import com.et.simon.smartlight.lib.EspWifiAdminSimple;
import com.et.simon.smartlight.task.__IEsptouchTask;
import com.et.simon.smartlight.utils.EsptouchTask;
import com.et.simon.smartlight.utils.IEsptouchListener;
import com.et.simon.smartlight.utils.IEsptouchResult;
import com.et.simon.smartlight.utils.IEsptouchTask;

import java.util.List;

/**
 * Created by SIMON on 2017/11/21.
 */

public class AddDeviceActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = "AddDeviceActivity";
    private EspWifiAdminSimple wifiAdminSimple;
    private TextView apSSID_Text = null;
    private ImageView back_main_btn = null;
    private TextView status = null;

    private EditText apPassword = null;
    private Button smartConfig_btn = null;
    private CheckBox cbLaws = null;
    private boolean mDestroyed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        wifiAdminSimple = new EspWifiAdminSimple(this);

        apSSID_Text = findViewById(R.id.wifi_ssid);
        apPassword = findViewById(R.id.wifi_pwd);

        back_main_btn = findViewById(R.id.back_main_btn);
        smartConfig_btn = findViewById(R.id.smartConfig);
        cbLaws = findViewById(R.id.cbLaws);
        status = findViewById(R.id.status);

        smartConfig_btn.setOnClickListener(this);
        back_main_btn.setOnClickListener(this);
        //cbLaws.setOnClickListener(this);
        apSSID_Text.setFocusableInTouchMode(false);


        if (isSDKAtLeastP()) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {
                        Manifest.permission.ACCESS_COARSE_LOCATION
                };

                requestPermissions(permissions, REQUEST_PERMISSION);
            } else {
                registerBroadcastReceiver();
            }

        } else {
            registerBroadcastReceiver();
        }

        initView();

    }
    private boolean isSDKAtLeastP() {
        return Build.VERSION.SDK_INT >= 28;
    }

    private boolean mReceiverRegistered = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            assert wifiManager != null;

            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    WifiInfo wifiInfo;
                    if (intent.hasExtra(WifiManager.EXTRA_WIFI_INFO)) {
                        wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    } else {
                        wifiInfo = wifiManager.getConnectionInfo();
                    }
                    onWifiChanged(wifiInfo);
                    break;
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    onWifiChanged(wifiManager.getConnectionInfo());
                    break;
            }
        }
    };

    private void onWifiChanged(WifiInfo info) {
        boolean disconnected = info == null
                || info.getNetworkId() == -1
                || "<unknown ssid>".equals(info.getSSID());
        if (disconnected) {
            apSSID_Text.setText("");
            apSSID_Text.setTag(null);
            status.setText(R.string.no_wifi_connection);
            smartConfig_btn.setEnabled(false);

//            if (isSDKAtLeastP()) {
//                checkLocation();
//            }

        } else {
            String ssid = info.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            apSSID_Text.setText(ssid);

            smartConfig_btn.setEnabled(true);
            status.setText("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int frequency = info.getFrequency();
                if (frequency > 4900 && frequency < 5900) {
                    // Connected 5G wifi. Device does not support 5G
                    Toast.makeText(AddDeviceActivity.this, " Frequency: " + frequency + "Hz", Toast.LENGTH_SHORT).show();
                    status.setText(R.string.wifi_5g_message);
                }
            }
        }
    }

    private static final int REQUEST_PERMISSION = 0x01;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!mDestroyed) {
                        registerBroadcastReceiver();
                    }
                }
                break;
        }
    }
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        if (isSDKAtLeastP()) {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        }
        registerReceiver(mReceiver, filter);
        mReceiverRegistered = true;
    }
    private void initView() {
        apPassword.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //密码可视
        cbLaws.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String psw = apPassword.getText().toString();

                if (isChecked) {
                    apPassword.setInputType(0x90);
                } else {
                    apPassword.setInputType(0x81);
                }
                apPassword.setSelection(psw.length());
            }
        });
//        if (!NetworkUtils.whetherNetWorkIsWifi(this)) {
//            Toast.makeText(this, "WIFI尚未打开", Toast.LENGTH_SHORT).show();
//            tvStatus.setText("WIFI尚未打开");
//            initDialog();
//        }
        String apSsid = wifiAdminSimple.getWifiConnectedSsid();
        if (apSsid == null) {
            status.setText("WIFI尚未打开");
            initDialog();
        } else {
            status.setText("智能配网");
        }
    }
    private void initDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new
                        Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
            }
        };
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("WIFI 尚未连接");
        dialogBuilder.setPositiveButton("去连接WiFi", dialogClickListener);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }




    /**
     * 设备连接上WIFI之后的操作
     * @param result
     */
    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String text = result.getBssid() + "已连接到Wi-Fi";
                Toast.makeText(AddDeviceActivity.this, text,
                        Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * 监听设备结果
     */
    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        //显示手机连接到的WIFI
        String apSsid = wifiAdminSimple.getWifiConnectedSsid();
        if (apSsid != null) {
            apSSID_Text.setText(apSsid);
        } else {
            apPassword.setText("");
            status.setText("WIFI尚未连接");
            initDialog();
        }
        //检查WIFI是否已连接
        boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
        smartConfig_btn.setEnabled(!isApSsidEmpty);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_main_btn:
                Intent intent = new Intent();
                intent.setClass(AddDeviceActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.smartConfig:
                String apssid = apSSID_Text.getText().toString();
                String appassword = apPassword.getText().toString();
                String apBssid = wifiAdminSimple.getWifiConnectedBssid();
                Toast.makeText(this, "正在配网...", Toast.LENGTH_SHORT).show();
                if (__IEsptouchTask.DEBUG) {
                    Log.d(TAG, "已点击配网, AP_SSID = " + apssid
                            + ", " + " AP_Password= " + appassword);
                }
                new EsptouchAsyncTask3().execute(apssid, apBssid, appassword, "1");
                break;
        }
    }

    private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {

        private ProgressDialog mProgressDialog;

        private IEsptouchTask mEsptouchTask;
        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(AddDeviceActivity.this);
            mProgressDialog
                    .setMessage("正在使用 ESP-Touch 技术进行智能配网中,\r\n请稍等片刻...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (mLock) {
                        if (__IEsptouchTask.DEBUG) {
                            Log.i(TAG, "取消进度对话框");
                        }
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    "配网中...", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            mProgressDialog.show();
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(false);
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = -1;
            synchronized (mLock) {
                // !!!NOTICE
                String apSsid = wifiAdminSimple.getWifiConnectedSsidAscii(params[0]);
                String apBssid = params[1];
                String apPassword = params[2];
                String taskResultCountStr = params[3];
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, AddDeviceActivity.this);
                mEsptouchTask.setEsptouchListener(myListener);
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(true);
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                    "完成配网");

            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent().setClass(AddDeviceActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                    for (IEsptouchResult resultInList : result) {
                        sb.append("设备 "
                                + resultInList.getBssid().toUpperCase()
                                + " 已连接,设备IP地址为: "
                                + resultInList.getInetAddress()
                                .getHostAddress() + "\n");
                        count++;
                        if (count >= maxDisplayCount) {
                            break;
                        }
                    }
                    if (count < result.size()) {
                        sb.append("\nthere's " + (result.size() - count)
                                + " more result(s) without showing\n");
                    }
                    mProgressDialog.setMessage(sb.toString());
                } else {
                    //mProgressDialog.setMessage("配网失败！");
                    Toast.makeText(AddDeviceActivity.this, "配网失败！请重新配网", Toast.LENGTH_SHORT).show();
                    mProgressDialog.cancel();
                }
            }
        }
    }
}
