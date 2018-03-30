package com.hrf.chess.app;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import static com.hrf.chess.config.Config.LogTag;

/**
 * User: HRF
 * Date: 2017/5/31 0031
 * Time: 下午 1:32
 * Description: Too
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;

    public MyApplication() {
    }

    // 单例模式中获取唯一的LBSApp 实例
    public static MyApplication getInstance() {
        if (null == mInstance) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Logger.init(LogTag)                    //LOG TAG默认是PRETTYLOGGER
                .methodCount(3)                 // 决定打印多少行（每一行代表一个方法）默认：2
                .hideThreadInfo()               // 隐藏线程信息 默认：显示
                .logLevel(LogLevel.NONE)        // 是否显示Log 默认：LogLevel.FULL（全部显示）
                .methodOffset(2);               // 默认：0
    }
}
