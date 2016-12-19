package com.fxc.lib.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @描述： 流操作
 */
public class IOUtils {
    public static void close(Closeable closeable){
        if (closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
