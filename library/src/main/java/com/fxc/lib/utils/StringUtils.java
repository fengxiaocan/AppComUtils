package com.fxc.lib.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 *  @项目名：
 *  @文件名:   StringUtils
 *  @创建者:   Administrator
 *  @创建时间:  2016/9/6 14:07
 *  @描述：    字符串操作工具类
 */
public class StringUtils {

    /** 判断字符串是否为空 */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        if ("".equals(str)) {
            return true;
        }
        return false;
    }

    /** 使用字符缓冲区来拼接字符串 */
    public static String join(String... s) {
        StringBuffer sb = new StringBuffer();
        for (String s1 : s) {
            sb.append(s1);
        }
        return sb.toString();
    }

    /**
     * 格式化数字数量
     */
    public static String formatNumber(long num) {
        if (num >= 0 && num < 10000) {
            return num + "";
        }

        if (num >= 10000) {
            long start = num / 10000;
            long end   = num % 10000;
            if (end < 1000) {
                return start + "万";
            } else {
                end = end / 1000;
                return start + "." + end + "万";
            }
        }
        return "0";
    }

    /**
     * 获取流编码
     */
    public static String getTextCode(String filePath) {// 转码
        try {
            File file = new File(filePath);
            return getTextCode(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取流编码
     */
    public static String getTextCode(File file) {
        try {
            FileInputStream is = new FileInputStream(file);
            return getTextCode(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取流编码
     */
    public static String getTextCode(InputStream is) {// 转码
        String text = "GBK";
        try {
            BufferedInputStream in = new BufferedInputStream(is);
            in.mark(4);
            byte[] first3bytes = new byte[3];
            in.read(first3bytes);//找到文档的前三个字节并自动判断文档类型。
            in.reset();
            if (first3bytes[0] == (byte) 0xEF &&
                first3bytes[1] == (byte) 0xBB &&
                first3bytes[2] == (byte) 0xBF) {// utf-8

                text = "utf-8";
            } else if (first3bytes[0] == (byte) 0xFF &&
                       first3bytes[1] == (byte) 0xFE) {
                text = "unicode";
            } else if (first3bytes[0] == (byte) 0xFE &&
                       first3bytes[1] == (byte) 0xFF) {
                text = "utf-16be";
            } else if (first3bytes[0] == (byte) 0xFF &&
                       first3bytes[1] == (byte) 0xFF) {
                text = "utf-16le";
            } else {
                text = "GBK";
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 字符串转换unicode
     */
    public static String str2Unicode(String string) {
        if (string == null) {
            return string;
        }
        try {
            StringBuffer unicode = new StringBuffer();
            for (int i = 0; i < string.length(); i++) {
                // 取出每一个字符
                char c = string.charAt(i);
                // 转换为unicode
                String hexString = Integer.toHexString(c);
                unicode.append("\\u" + hexString);
            }
            return unicode.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {
        if (unicode == null) {
            return unicode;
        }
        String[] hex = unicode.split("\\\\u");
        if (hex == null) {
            return unicode;
        } else {
            StringBuffer string = new StringBuffer();
            for (int i = 0; i < hex.length; i++) {
                try {
                    // 转换出每一个代码点
                    int data = Integer.parseInt(hex[i], 16);
                    // 追加成string
                    string.append((char) data);
                } catch (Exception e) {
                    string.append(hex[i]);
                }

            }
            return string.toString();
        }
    }

    /**
     * 获取url的后缀
     */
    public static String getUrlSuffix(String url) {
        if (isEmpty(url)) {
            return "";
        } else {
            int    of  = url.lastIndexOf('.');
            String fix = url.substring(of, url.length());
            return fix;
        }
    }

    /**
     * 获取url的中的文件名
     */
    public static String getUrlFileName(String url) {
        if (isEmpty(url)) {
            return "";
        } else {
            int    of  = url.lastIndexOf('/');
            String fix = url.substring(of, url.length());
            return fix;
        }
    }

    /**
     * 汉字排序
     */
    public static List<String> ChineseCharacterSort(List<String> list) {
        Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
        Collections.sort(list, com);
        return list;
    }
}
