package com.hrf.chess.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hrf.chess.R;
import com.hrf.chess.activity.base.BaseActivity;
import com.hrf.chess.service.SocketService;
import com.hrf.chess.utils.SharedPreferencesUtil;
import com.hrf.chess.utils.WifiUtils;
import com.hrf.chess.widget.WifiDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {

    @BindView(R.id.bt_create)
    Button bt_create;
    @BindView(R.id.bt_add)
    Button bt_add;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.bt_man_machine)
    Button bt_man_machine;
    @BindView(R.id.bt_instructions)
    Button bt_instructions;
    @BindView(R.id.bt_exit)
    Button bt_exit;
    private WifiUtils wifiUtils;
    private WifiDialog wifiDialog;
    private Intent socketServiceIntent;

    @Override
    protected int setContentView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestWriteSettings();
        }
        if (!SharedPreferencesUtil.getBoolean(this, "MPermission", "MPermission"))
            MPermission();
        wifiUtils = WifiUtils.getInstance(this);
        wifiDialog = new WifiDialog(this, R.style.my_dialog);

    }


    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;

    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                Log.i("My", "6.0以上要这样开权限");
                SharedPreferencesUtil.setBoolean(this, "MPermission", "MPermission", true);
            }
        }
    }

    /**
     * android 6.0 权限问题解决
     */
    private void MPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    @Override
    protected void initListener() {
        wifiDialog.setWifiDialogListener(new WifiDialog.WifiDialogListener() {
            @Override
            public void onWifiDialog(ScanResult scanResult) {
                wifiUtils.connectWifiAp(scanResult);
                wifiDialog.dismiss();
            }
        });
        wifiUtils.setWifiScanListener(new WifiUtils.WifiScanListener() {
            @Override
            public void onScanResult(List<ScanResult> scanResults) {
                int networkId = wifiUtils.getNetworkId();
                Log.i("My", "networkId:" + networkId);
                if (wifiDialog != null) {
                    wifiDialog.setScanResults(scanResults);
                }
            }

            @Override
            public void connSuccess() {
                Intent intent = new Intent(MainActivity.this, ClientActivity.class);
                intent.putExtra("creat", false);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initDate() {

    }

    @OnClick({R.id.bt_create, R.id.bt_add, R.id.bt_man_machine, R.id.bt_instructions, R.id.bt_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_create:
                if (wifiUtils.creatWifiAp()) {
                    socketServiceIntent = new Intent(this, SocketService.class);
                    startService(socketServiceIntent);
                    Intent intent = new Intent(this, ClientActivity.class);
                    intent.putExtra("creat", true);
                    startActivity(intent);
                }
                break;
            case R.id.bt_add:
                wifiUtils.openWifi();
                if (((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().equals("\"" + WifiUtils.SSID + "\"")) {
                    Intent intent = new Intent(MainActivity.this, ClientActivity.class);
                    intent.putExtra("creat", false);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "请加入象棋对战热点", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_man_machine:
                startActivity(new Intent(this, ManMachineActivity.class));
                break;
            case R.id.bt_instructions:
                startActivity(new Intent(this, IllustrateActivity.class));
                break;
            case R.id.bt_exit:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiUtils.closeWifi();
        wifiUtils.closeWifiAp();
        if (socketServiceIntent != null) {
            stopService(socketServiceIntent);
        }
    }
}
