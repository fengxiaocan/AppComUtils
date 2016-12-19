package com.fxc.lib.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @描述： 主工具类
 */
public class MainUtils {
    private static MainUtils sMainUtils;
    private static Context   sContext;
    private static Toast sToast;
    private static String sSavePath;

    private MainUtils(Context context) {
        sContext = context;
        sSavePath = sContext.getFilesDir().getAbsolutePath();
    }

    /**
     * 初始化
     */
    public static void initMain(Context context) {
        synchronized (MainUtils.class) {
            if (sMainUtils == null) {
                synchronized (MainUtils.class) {
                    sMainUtils = new MainUtils(context);
                }
            }
        }
    }

    public static Context getContext() {
        return sContext;
    }

    public static void toast(String msg){
        if (sToast == null) {
            sToast = Toast.makeText(sContext, msg, Toast.LENGTH_SHORT);
        }else{
            sToast.setText(msg);
        }
        sToast.show();
    }

    public static String getSavePath(){
        return sSavePath;
    }

}
