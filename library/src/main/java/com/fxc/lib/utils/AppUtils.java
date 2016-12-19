package com.fxc.lib.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.DataOutputStream;
import java.io.File;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * @描述： app工具类
 */
public class AppUtils {

    /**
     * 获取唯一标识imei
     *
     * @param context
     *
     * @return
     */
    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String           imei             = telephonyManager.getDeviceId();
        return imei;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     *
     * @return
     */
    public static int getWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     *
     * @return
     */
    public static int getHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        return wm.getDefaultDisplay().getHeight();
    }


    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process          process = null;
        DataOutputStream os      = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * 关机
     */
    public static void shutdown() {

        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c",
                                                                  "reboot -p"});  //关机
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启 reboot
     */
    public static void reboot() {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c",
                                                                  "reboot "});  //关机
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 判断某个界面是否在前台
     * <P>需要添加权限:<uses-permission android:name="android.permission.GET_TASKS"/></P>
     *
     * @param context
     * @param className 某个界面名称
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager                       am   = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo    packageInfo    = packageManager.getPackageInfo(context.getPackageName(), 0);
            int            labelRes       = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo    packageInfo    = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo    packageInfo    = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     *
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean                                  isWork = false;
        ActivityManager                          myAM   = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 关闭自身app
     */
    public static void exitApp() {
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 获取系统的sdk版本
     */
    public static int getAndroidOSVersion() {
        int osVersion;
        try {
            osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            osVersion = 0;
        }

        return osVersion;
    }

    /**
     * 打开微信
     *
     * @param context
     */
    public static void openWeiChat(Context context) {
        try {
            //打开微信
            Intent        intent = new Intent();
            ComponentName cmp    = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否是WiFi状态
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         activeNetInfo       = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null &&
            activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 隐藏输入法弹出框
     */
    public static void hideSoftInput(Activity activity) {
        ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 是否开启 wifi true：开启 false：关闭
     * 一定要加入权限： <uses-permission
     * android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     * <uses-permission
     * android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
     *
     * @param isEnable
     */
    public static void OpenOrCloseWifi(Context context, boolean isEnable) {
        WifiManager mWm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (isEnable) {
            // 开启wifi
            if (!mWm.isWifiEnabled()) {
                mWm.setWifiEnabled(true);
            }
        } else {
            // 关闭 wifi
            if (mWm.isWifiEnabled()) {
                mWm.setWifiEnabled(false);
            }
        }
    }

    /**
     * 判断sd卡是否存在
     */
    public static boolean sdCardExist() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
    }

    /**
     * 获取sd卡根目录
     */
    public static File getSdRoot() {
        File sdDir = Environment.getExternalStorageDirectory();//获取根目录
        return sdDir;
    }

    /**
     * 获取保存目录
     */
    public static File getSaveDir(Context context, String name) {
        File saveDir;
        if (sdCardExist()) {
            saveDir = new File(Environment.getDataDirectory(),
                    context.getPackageName() + "/" + name);//获取根目录
        } else {
            saveDir = new File(context.getFilesDir(), name);
        }
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        return saveDir;
    }

    /**
     * 获取保存目录
     */
    public static File getSdSaveDir(String name) {
        if (sdCardExist()) {
            File saveDir = new File(getSdRoot(), name);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            return saveDir;
        }
        throw new NullPointerException("SD卡不存在");
    }

    /**
     * 统计文件夹大小
     */
    private static long getDirLength(File dir) {
        if (dir == null) {
            return 0;
        }
        if (dir.isFile()) {
            return dir.length();
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return 0;
        }
        long cacheLength = 0;
        for (File file : files) {
            if (file.isFile()) {
                cacheLength += file.length();
            } else {
                cacheLength += getDirLength(file);
            }
        }
        return cacheLength;
    }


    /**
     * 遍历删除文件夹下的所有内容
     */
    private static void deleteDir(File dir) {
        if (dir == null) {
            return;
        }
        if (dir.isFile()) {
            dir.delete();
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            } else {
                deleteDir(file);
                file.delete();
            }
        }
        dir.delete();
    }

}
