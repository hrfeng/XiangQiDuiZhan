package com.hrf.chess.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hrf.chess.R;
import com.hrf.chess.adapter.base.BaseAdapter;
import com.hrf.chess.utils.ViewUtils;

import java.util.List;

/**
 * User: HRF
 * Date: 2017/8/25
 * Time: 17:39
 * Description: Too
 */
public class WifiDialogAdapter extends BaseAdapter<ScanResult> {

    public WifiDialogAdapter(Context context, List<ScanResult> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(getContext(), R.layout.item_wifi_diaiog, null);
        TextView tv_content = (TextView) convertView.findViewById(R.id.tv_contnet);
        ViewUtils.setText(tv_content, getItem(position).SSID);
        return convertView;
    }
}
