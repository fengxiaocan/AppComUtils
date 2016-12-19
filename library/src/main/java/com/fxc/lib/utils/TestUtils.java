package com.fxc.lib.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @项目名： Advertisement
 * @包名：
 * @创建者: feng
 * @时间: 9:49
 * @描述： 测试工具类
 */
public class TestUtils {
    /**
     * 打印日志
     */
    public static void log(String msg) {
        if (msg == null){
            msg="null";
        }
        Log.e("evil-test", msg);
    }

    /**
     * 测试运行时间
     */
    public static void runTime(CallBack callback) {
        long startTime = System.currentTimeMillis();
        callback.mothod();
        long endTime = System.currentTimeMillis();
        TestUtils.log("time = " + (endTime - startTime));
    }

    /**
     * 上传文件
     * @param filePath
     */
    public static void updataFile(String filePath) {
        File file = new File(filePath);
        updataFile(file);
    }
    /**
     * 上传文件
     * @param file
     */
    public static void updataFile(File file) {
        new Thread(new UpdataRunnable(file)).start();
    }

    public static class UpdataRunnable implements Runnable {
        private File mFile;

        public UpdataRunnable(File file) {
            mFile = file;
        }

        @Override
        public void run() {
            if (mFile == null || !mFile.exists()) {
                TestUtils.log("文件不存在");
                return;
            }
            try {
                Socket          socket = new Socket("192.168.1.254", 58888);
                OutputStream    os     = socket.getOutputStream();
                byte[]          arr    = new byte[(int) mFile.length()];
                FileInputStream fis    = new FileInputStream(mFile);
                fis.read(arr);
                os.write(arr);
                fis.close();
                os.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface CallBack{
        void mothod();
    }
}
