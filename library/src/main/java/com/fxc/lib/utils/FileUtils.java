package com.fxc.lib.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 *  @描述：    流操作工具类
 */
public class FileUtils {
    /**
     * 复制文件
     * @param copy
     * @param goal
     */
    public static void copy(File copy, File goal) {
        try {
            FileInputStream  is = new FileInputStream(copy);
            FileOutputStream os = new FileOutputStream(goal);
            copy(is, os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文件
     * @param is
     * @param os
     */
    public static void copy(InputStream is, OutputStream os) {
        try {
            byte[] arr = new byte[8192];
            int    len;
            while ((len = is.read(arr)) != -1) {
                os.write(arr, 0, len);
            }
            close(os);
            close(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文件
     *
     * @param copyFile 要复制的文件
     * @param toDir    复制到的文件夹
     */
    public static String copyFile(File copyFile, File toDir) {
        File file = new File(toDir, copyFile.getName());
        copy(copyFile, file);
        return file.getAbsolutePath();
    }

    public static void close(Closeable clo) {
        if (clo != null) {
            try {
                clo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 统计文件大小,包括文件和文件夹
     *
     * @return
     */
    public static long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        if (file.isFile()) {
            return file.length();
        } else {
            long   length = 0;
            File[] files  = file.listFiles();
            if (files == null) {
                return 0;
            } else {
                for (File file1 : files) {
                    length += getFileSize(file1);
                }
            }
            return length;
        }
    }
}
