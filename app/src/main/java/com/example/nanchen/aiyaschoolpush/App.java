package com.example.nanchen.aiyaschoolpush;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.example.nanchen.aiyaschoolpush.helper.DemoHelper;
import com.example.nanchen.aiyaschoolpush.helper.QiYuCloudServerHelper;
import com.mob.mobapi.MobAPI;
import com.squareup.leakcanary.LeakCanary;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

import cn.smssdk.SMSSDK;



/**
 * 启动的Application
 *
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush
 * @date 2016/09/08  15:51
 */
public class App extends Application {

    private static final String MSG_APP_KEY = "16faeb1248a89";// 短信验证的app_key
    private static final String MSG_APP_SECRET = "20d994397ced27b44b48ce80956a6f9d";// 短信验证的app_secret
    private static final String MOB_APP_KEY = "1730bae762bbc";// MobApi的应用app_key
    private static final String TAG = "App";

    private static final String MIPUSH_APP_KEY = "5681752153371"; // 小米推送App_key
    private static final String MIPUSH_APP_ID = "2882303761517521371"; //小米推送app_id

    private static App app;

    public static App getInstance(){
        return app;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        // LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);


        // 初始化小米推送相关
        initMiPush();

        // 初始化短信验证SDK
        SMSSDK.initSDK(this, MSG_APP_KEY, MSG_APP_SECRET);

        // 初始化MobApiSDK
        MobAPI.initSDK(getApplicationContext(), MOB_APP_KEY);

        //init demo helper
        DemoHelper.getInstance().init(App.getAppContext());

        // 七鱼客服初始化
        QiYuCloudServerHelper.initCloudServer(this);
    }

    /**
     * 初始化小米推送相关
     */
    private void initMiPush() {
        //初始化push推送服务
        if(shouldInit()) {
            MiPushClient.registerPush(this, MIPUSH_APP_ID, MIPUSH_APP_KEY);
        }

        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }
            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }
            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取Application Context
     * */
    public static Context getAppContext() {
        return app != null ? app.getApplicationContext() : null;
    }

    public static String currentUserNick = "";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
