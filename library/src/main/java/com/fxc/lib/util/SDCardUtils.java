package com.fxc.lib.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * <pre>
 *     time  : 2016/08/11
 *     desc  : SD卡相关工具类
 * </pre>
 */
public final class SDCardUtils {

    private SDCardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean sdCardExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取SD卡路径
     * <p>先用shell，shell失败再普通方法获取，一般是/storage/emulated/0/</p>
     *
     * @return SD卡路径
     */
    public static String getSDCardPath() {
        if (!sdCardExist()) {
            return null;
        }
        String         cmd            = "cat /proc/mounts";
        Runtime        run            = Runtime.getRuntime();
        BufferedReader bufferedReader = null;
        try {
            Process p = run.exec(cmd);
            bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
            String lineStr;
            while ((lineStr = bufferedReader.readLine()) != null) {
                if (lineStr.contains("sdcard") &&
                    lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray.length >= 5) {
                        return strArray[1].replace("/.android_secure", "") +
                               File.separator;
                    }
                }
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(bufferedReader);
        }
        return Environment.getExternalStorageDirectory().getPath() +
               File.separator;
    }

    /**
     * 获取系统SD存储路径
     */
    public static String getSdRootPath() {
        return Environment.getRootDirectory().getAbsolutePath();
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
    public static File getSaveDir(String name) {
        File saveDir;
        if (sdCardExist()) {
            saveDir = new File(Environment.getDataDirectory(),
                    Utils.getContext().getPackageName() + "/" + name);//获取根目录
        } else {
            saveDir = new File(Utils.getContext().getFilesDir(), name);
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
     * 获取SD卡data路径
     *
     * @return SD卡data路径
     */
    public static String getDataPath() {
        if (!sdCardExist()) {
            return null;
        }
        return Environment.getExternalStorageDirectory().getPath() +
               File.separator + "data" + File.separator;
    }

    /**
     * 获取SD卡剩余空间
     *
     * @return SD卡剩余空间
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getFreeSpace() {
        if (!sdCardExist()) {
            return null;
        }
        StatFs stat = new StatFs(getSDCardPath());
        long   blockSize, availableBlocks;
        availableBlocks = stat.getAvailableBlocksLong();
        blockSize = stat.getBlockSizeLong();
        return ConvertUtils.byte2FitMemorySize(availableBlocks * blockSize);
    }

    /**
     * 获取SD卡信息
     *
     * @return SDCardInfo
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getSDCardInfo() {
        if (!sdCardExist()) {
            return null;
        }
        SDCardInfo sd = new SDCardInfo();
        sd.isExist = true;
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        sd.totalBlocks = sf.getBlockCountLong();
        sd.blockByteSize = sf.getBlockSizeLong();
        sd.availableBlocks = sf.getAvailableBlocksLong();
        sd.availableBytes = sf.getAvailableBytes();
        sd.freeBlocks = sf.getFreeBlocksLong();
        sd.freeBytes = sf.getFreeBytes();
        sd.totalBytes = sf.getTotalBytes();
        return sd.toString();
    }

    public static class SDCardInfo {
        boolean isExist;
        long    totalBlocks;
        long    freeBlocks;
        long    availableBlocks;
        long    blockByteSize;
        long    totalBytes;
        long    freeBytes;
        long    availableBytes;

        @Override
        public String toString() {
            return "isExist=" + isExist +
                   "\ntotalBlocks=" + totalBlocks +
                   "\nfreeBlocks=" + freeBlocks +
                   "\navailableBlocks=" + availableBlocks +
                   "\nblockByteSize=" + blockByteSize +
                   "\ntotalBytes=" + totalBytes +
                   "\nfreeBytes=" + freeBytes +
                   "\navailableBytes=" + availableBytes;
        }
    }
}