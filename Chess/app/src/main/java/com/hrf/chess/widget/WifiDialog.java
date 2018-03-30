package com.hrf.chess.widget;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hrf.chess.R;
import com.hrf.chess.adapter.WifiDialogAdapter;

import java.util.List;

/**
 * User: HRF
 * Date: 2017/8/25
 * Time: 17:09
 * Description: Too
 */
public class WifiDialog extends Dialog {

    private Context context;
    private ListView lv_content;
    private WifiDialogAdapter wifiDialogAdapter;
    private List<ScanResult> scanResults;
    private WifiDialogListener wifiDialogListener;

    public WifiDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public WifiDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected WifiDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifi);
        lv_content = (ListView) findViewById(R.id.lv_content);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        LinearLayout ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ViewGroup.LayoutParams layoutParams = ll_content.getLayoutParams();
        layoutParams.width = w * 4 / 5;
        layoutParams.height = h * 3 / 5;
        ll_content.setLayoutParams(layoutParams);

    }

    public List<ScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(final List<ScanResult> scanResults) {
        this.scanResults = scanResults;
//        for (int i=0;i<scanResults.size();i++) {
//            if (!scanResults.get(i).SSID.equals(WifiUtils.SSID)) {
//                scanResults.remove(scanResults.get(i));
//            }
//        }
        wifiDialogAdapter = new WifiDialogAdapter(context, scanResults);
        lv_content.setAdapter(wifiDialogAdapter);
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (wifiDialogListener != null) {
                    wifiDialogListener.onWifiDialog(scanResults.get(position));
                }
            }
        });
    }

    public interface WifiDialogListener {
        void onWifiDialog(ScanResult scanResult);
    }

    public void setWifiDialogListener(WifiDialogListener wifiDialogListener) {
        this.wifiDialogListener = wifiDialogListener;
    }

    @Override
    public void show() {
        super.show();
        lv_content.setAdapter(null);
    }
}
