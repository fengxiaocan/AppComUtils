package com.fxc.lib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @项目名： MyComUtils
 * @包名： com.fxc.lib.util
 * @创建者: Noah.冯
 * @时间: 19:02
 * @描述： TODO
 */

public class SpUtils {


    private static SharedPreferences sp;
    private static SpUtils sSpUtils;
    private static String sName;

    private SpUtils() {
    }

    public static SpUtils create(String name) {
        sName = name;
        synchronized (SpUtils.class) {
            if (sSpUtils == null) {
                synchronized (SpUtils.class) {
                    sSpUtils = new SpUtils();
                }
            }
        }
        return sSpUtils;
    }

    private static SharedPreferences getSharedP() {
        if (sp == null) {
            sp = Utils.getContext().getSharedPreferences(sName, Context.MODE_PRIVATE);
        }
        return sp;
    }

    /**
     * 保存各种类型的信息
     */
    public static void save(Object... list) {
        if (list == null)
            return;
        sp = getSharedP();
        // 获取编辑器
        Editor edit = sp.edit();
        for (int i = 0; i < list.length; i += 2) {
            String key = (String) list[i];
            Object object = list[i + 1];
            if (object instanceof String) {
                edit.putString(key, (String) object);
            } else if (object instanceof Integer) {
                edit.putInt(key, (Integer) object);
            } else if (object instanceof Boolean) {
                edit.putBoolean(key, (Boolean) object);
            } else if (object instanceof Float) {
                edit.putFloat(key, (Float) object);
            } else if (object instanceof Long) {
                edit.putLong(key, (Long) object);
            } else {
                edit.putString(key, object.toString());
            }
        }
        // 提交数据
        edit.commit();
        sp = null;
    }

    /**
     * 保存String类型的信息
     */
    public static void save(String key, String value) {
        sp = getSharedP();
        // 获取编辑器
        Editor edit = sp.edit();
        // 写入数据
        edit.putString(key, value);
        // 提交数据
        edit.commit();

        sp = null;
    }

    /**
     * 保存String类型的信息
     */
    public static void save(String... list) {
        if (list == null)
            return;
        sp = getSharedP();
        // 获取编辑器
        Editor edit = sp.edit();
        for (int i = 0; i < list.length; i += 2) {
            // 写入数据
            edit.putString(list[i], list[i + 1]);
        }
        // 提交数据
        edit.commit();
        sp = null;
    }


    /**
     * 获取String类型的数据信息
     */
    public static String getInfo(String key,
                                 String defValue) {
        sp = getSharedP();

        String value = sp.getString(key, defValue);

        return value;
    }

    /**
     * 保存boolean类型的置
     */
    public static void save(String key, boolean value) {
        sp = getSharedP();
        // 获取编辑器
        Editor edit = sp.edit();
        // 写入数据
        edit.putBoolean(key, value);
        // 提交数据
        edit.commit();

        sp = null;
    }

    /**
     * 获取boolean的值
     */
    public static boolean getInfo(String key, boolean defValue) {
        sp = getSharedP();

        boolean value = sp.getBoolean(key, defValue);

        return value;
    }

    /**
     * 获取int类型的值
     */
    public static void save(String key, int value) {
        sp = getSharedP();
        // 获取编辑器
        Editor edit = sp.edit();
        // 写入数据
        edit.putInt(key, value);
        // 提交数据
        edit.commit();

        sp = null;
    }

    /**
     * 获取int类型的值
     */
    public static int getInfo(String key, int defValue) {
        sp = getSharedP();

        int value = sp.getInt(key, defValue);

        return value;
    }
}
