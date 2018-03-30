package com.hrf.chess.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lin on 2016/9/21.
 * Time: 17:18
 * Description: TOO 存储和访问SharedPreferences工具
 */

public class SharedPreferencesUtil {

    public static void setBoolean(Context context, String name, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putBoolean(key, value);
        editor.commit();// 提交修改
    }

    public static boolean getBoolean(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * 基本功能：保存String类型数据到SharedPreferences
     */
    public static void setString(Context context, String name, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putString(key, value);
        editor.commit();// 提交修改
    }

    /**
     * 基本功能：取得SharedPreferences中存储的String类型数据
     */
    public static String getString(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    /**
     * 基本功能：取得SharedPreferences中存储的String类型数据
     */
    public static String getString(Context context, String name, String key, String messge) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, messge);
    }

    /**
     * 基本功能：存储的Int类型数据到SharedPreferences
     */
    public static void setInt(Context context, String name, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putInt(key, value);
        editor.commit();// 提交修改
    }

    /**
     * 基本功能：取得SharedPreferences中存储的Int类型数据
     */
    public static int getInt(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
}
