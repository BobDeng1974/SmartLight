package com.et.simon.smartlight.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存数据
 * Created by SIMON on 2017/11/21.
 */


public class PreferenceUtils {

    /**
     * 写入数据(Int类型)
     *
     * @param context, 上下文
     * @param fileName, 保存的文件名
     * @param key, 键值
     * @param value, 数据值
     */
    public static void write(Context context, String fileName, String key, int value){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 写入数据(Boolean类型)
     *
     * @param context, 上下文
     * @param fileName, 保存的文件名
     * @param key, 键值
     * @param value, 数据值
     */
    public static void write(Context context, String fileName, String key, boolean value){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 写入数据(String类型)
     *
     * @param context, 上下文
     * @param fileName, 保存的文件名
     * @param key, 键值
     * @param value, 数据值
     */
    public static void write(Context context, String fileName, String key, String value){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 读取数据(Int类型,默认为0)
     *
     * @param context, 上下文
     * @param fileName, 要检索的文件名
     * @param key, 键值
     * @return
     */
    public static int readInt(Context context, String fileName, String key){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    /**
     * 读取数据(Int类型)
     *
     * @param context, 上下文
     * @param fileName, 要检索的文件名
     * @param key, 键值
     * @param defValue, 读取到的int值
     * @return
     */
    public static int readInt(Context context, String fileName, String key, int defValue){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getInt(key, defValue);
    }

    /**
     * 读取数据(String类型,默认为null)
     *
     * @param context, 上下文
     * @param fileName, 要检索的文件名
     * @param key, 键值
     * @return
     */
    public static String readString(Context context, String fileName, String key){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    /**
     * 读取数据(String类型)
     *
     * @param context, 上下文
     * @param fileName, 要检索的文件名
     * @param key, 键值
     * @param defValue, 读取到的字符串
     * @return
     */
    public static String readString(Context context, String fileName, String key, String defValue){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getString(key, defValue);
    }

    /**
     * 读取数据(Boolean类型,默认为false)
     *
     * @param context, 上下文
     * @param fileName, 要检索的文件名
     * @param key, 键值
     * @return
     */
    public static boolean readBoolean(Context context, String fileName, String key){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    /**
     * 读取数据(Boolean类型)
     *
     * @param context, 上下文
     * @param fileName, 要检索的文件名
     * @param key, 键值
     * @param defValue, 读取到的boolean值
     * @return
     */
    public static boolean readBoolean(Context context, String fileName, String key, boolean defValue){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defValue);
    }

    /**
     * 删除数据
     *
     * @param context, 上下文
     * @param fileName, 要检索的文件名
     * @param key, 键值
     * @return
     */
    public static void remove(Context context, String fileName, String key){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除SharedPreference
     *
     * @param context, 上下文
     * @param fileName, 要检索的文件名
     * @return
     */
    public static void clear(Context context, String fileName){
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
