package com.hrf.chess.activity;


import android.view.WindowManager;

import com.hrf.chess.R;
import com.hrf.chess.activity.base.BaseActivity;


public class ManMachineActivity extends BaseActivity {

    @Override
    protected int setContentView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_man_machine;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {

    }


    @Override
    protected void initDate() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
