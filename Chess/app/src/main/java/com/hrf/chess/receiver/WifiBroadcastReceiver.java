package com.hrf.chess.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.hrf.chess.utils.WifiUtils;

import static com.hrf.chess.utils.WifiUtils.SSID;

/**
 * User: HRF
 * Date: 2017/8/28
 * Time: 9:22
 * Description: Too
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    public WifiBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        WifiUtils wifiUtils = WifiUtils.getInstance(context);
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = "\"" + SSID + "\"";
        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            // wifi已成功扫描到可用wifi。
            Toast.makeText(context, "扫描成功", Toast.LENGTH_SHORT).show();
            if (wifiUtils != null) {
                wifiUtils.getWifiScanListener().onScanResult(wifiUtils.getWifiList());
            }
        } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                Toast.makeText(context, "连接已断开", Toast.LENGTH_SHORT).show();
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                if (wifiUtils != null && ssid.equals(wifiInfo.getSSID())) {
                    Toast.makeText(context, "已连接到网络" + wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();
                    wifiUtils.getWifiScanListener().connSuccess();
                }
            } else {
                NetworkInfo.DetailedState state = info.getDetailedState();
                if (state == state.CONNECTING) {
                    Toast.makeText(context, "连接中...", Toast.LENGTH_SHORT).show();
                } else if (state == state.AUTHENTICATING) {
                    Toast.makeText(context, "正在验证身份信息...", Toast.LENGTH_SHORT).show();
                } else if (state == state.OBTAINING_IPADDR) {
                    Toast.makeText(context, "正在获取IP地址...", Toast.LENGTH_SHORT).show();
                } else if (state == state.FAILED) {
                    Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
