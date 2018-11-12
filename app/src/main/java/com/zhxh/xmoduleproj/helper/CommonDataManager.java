package com.zhxh.xmoduleproj.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.List;

/**
 * Created by zhxh on 2018/11/12
 */
public class CommonDataManager {

    /**
     * 屏幕密度
     */
    public static float screenDensity;
    /**
     * 屏幕宽度
     */
    public static int screenWight;
    /**
     * 屏幕高度
     */
    public static int screenHeight;

    public static DisplayMetrics displayMetrics;
    /**
     * 软件版本号
     */
    public static String VERSIONNAME;
    /**
     * 版本code
     */
    public static int VERSIONCODE = 0;
    /**
     * app类型1主版本 303尊享版
     */
    public static int PACKTYPE = 1;
    /**
     * 设备型号
     */
    public static String DEVICE;

    /*** 设备操作系统版本 **/
    public static String DEVICEVERSION;


    public static void init(Context activity) {

        displayMetrics = activity.getResources().getDisplayMetrics();

        screenDensity = displayMetrics.density;

        screenWight = displayMetrics.widthPixels;

        screenHeight = displayMetrics.heightPixels;

        PackageManager manager = activity.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(activity.getPackageName(), 0);

            VERSIONNAME = info.versionName;
            VERSIONCODE = info.versionCode;
            DEVICE = Build.MODEL;
            DEVICEVERSION = android.os.Build.VERSION.RELEASE;
        } catch (PackageManager.NameNotFoundException e) {
            manager = null;
            info = null;
        } catch (Exception ex) {

        }


    }

    /***
     * 是否联网
     * @param activity
     * @return
     */
    public static boolean isNetworkConnected(Activity activity) {

        if (null == activity)
            return true;

        if (activity.isFinishing())
            return true;

        try {

            //获取当前网络
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo info = cm.getActiveNetworkInfo();

            //没有可用网络
            if (info == null || !info.isAvailable())
                return false;

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return true;
    }

    /***
     * wifi网络
     * @param activity
     * @return
     */
    public static boolean isWifi(Activity activity) {

        if (null == activity)
            return false;

        if (activity.isFinishing())
            return false;

        try {

            //获取当前网络
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(
                    Context.CONNECTIVITY_SERVICE);

            NetworkInfo info = cm.getActiveNetworkInfo();

            //没有可用网络
            if (info == null || !info.isAvailable())
                return false;

            if (info.getTypeName().equalsIgnoreCase("WIFI"))
                return true;

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return false;
    }

    /***
     * 程序是否在前台运行
     * @param context
     * @return
     */
    public static boolean isAppOnForeground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        String packageName = context.getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();

        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {

            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /***
     * 当前剩余高度
     * @param height
     * @return
     */
    public static int getRectHeight(int height, Activity activity) {

        if (displayMetrics == null)
            init(activity);

        int statusBarHeight = (int) Math.ceil(25 * displayMetrics.density);

        return screenHeight - statusBarHeight - (int) Math.ceil(height * displayMetrics.density);
    }


    /***
     * 获取屏幕密度相对值
     * @param value
     * @param activity
     * @return
     */
    public static int getDensityValue(float value, Context activity) {

        if (displayMetrics == null)
            init(activity);

        return (int) Math.ceil(value * displayMetrics.density);
    }


}
