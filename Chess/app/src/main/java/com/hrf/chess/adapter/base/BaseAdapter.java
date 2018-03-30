package com.hrf.chess.adapter.base;

/**
 * User: HRF
 * Date: 2016/9/21 0021
 * Time: 下午 4:35
 * Description: 适配器基类
 */

import android.content.Context;

import java.util.List;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    private Context mContext;
    private List<T> mList;

    public BaseAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mList = list;
        initData();
    }

    //初始化数据
    protected void initData() {

    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected Context getContext() {
        return mContext;
    }

    public List<T> getmList() {
        return mList;
    }

    public void setmList(List<T> mList) {
        this.mList = mList;
    }
}
