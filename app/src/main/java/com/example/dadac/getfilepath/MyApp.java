package com.example.dadac.getfilepath;

import android.app.Application;

import org.xutils.x;

/**
 * @ Create by dadac on 2018/9/27.
 * @Function:  供给SQLite使用
 * @Return:
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false);   //输出debug日志，开启会影响性能
    }
}
