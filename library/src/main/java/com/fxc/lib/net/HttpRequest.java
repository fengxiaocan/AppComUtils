package com.fxc.lib.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * @项目名： WebBrowser @包名： com.evil.webbrowser
 * @创建者: Noah.冯
 * @时间: 19:18 @描述： TODO
 */
public class HttpRequest {

    public static void GET(final String url, final HashMap<String, String> map, final HttpRequestCallback callback) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String httpUrl = url;
                    if (map != null) {
                        int position = 0;
                        for (String key : map.keySet()) {
                            if (position == 0) {
                                httpUrl += "?";
                            } else {
                                httpUrl += "&";
                            }
                            position++;
                            httpUrl += key + "=" + map.get(key);
                        }
                    }
                    HttpURLConnection httpURL = getHttpURL(httpUrl);
                    httpURL.setConnectTimeout(5000);// 超时时间
                    httpURL.setRequestMethod("GET");
                    httpURL.connect();
                    int responseCode = httpURL.getResponseCode();
                    if (callback != null) {
                        if (callback.response(responseCode)) {
                            InputStream inputStream = httpURL.getInputStream();
                            byte[] buf = new byte[8 * 1024];
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            int                   lenght;
                            while ((lenght = inputStream.read(buf)) > 0) {
                                out.write(buf, 0, lenght);
                                out.flush();
                            }
                            String string = out.toString("UTF-8");
                            callback.response(responseCode, string);
                            out.close();
                            inputStream.close();
                        }
                    }
                    httpURL.disconnect();

                } catch (Exception e) {
                    if (callback != null) {
                        callback.failure(e.getMessage());
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void GET(String url, HttpRequestCallback callback) {
        GET(url, null, callback);
    }

    public static void GET(String url, HashMap<String, String> map) {
        GET(url, map, null);
    }

    public static void GET(String url) {
        GET(url, null, null);
    }

    public static void POST(final String url, final HashMap<String, String> map, final HttpRequestCallback callback) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    HttpURLConnection httpURL = getHttpURL(url);
                    httpURL.setRequestMethod("POST");
                    httpURL.setConnectTimeout(3000); // 设置发起连接的等待时间，3s
                    httpURL.setReadTimeout(30000); // 设置数据读取超时的时间，30s
                    httpURL.setUseCaches(false); // 设置不使用缓存
                    httpURL.setDoOutput(true);
                    httpURL.setRequestMethod("POST");
                    httpURL.setConnectTimeout(5000);// 超时时间
                    httpURL.setRequestProperty("Connection", "Keep-Alive");
                    httpURL.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
                    if (map != null) {
                        for (String key : map.keySet()) {
                            httpURL.setRequestProperty(key, map.get(key));
                        }
                    }
                    httpURL.connect();
                    int responseCode = httpURL.getResponseCode();
                    if (callback != null) {
                        if (callback.response(responseCode)) {
                            InputStream inputStream = httpURL.getInputStream();
                            byte[] buf = new byte[8 * 1024];
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            int                   lenght;
                            while ((lenght = inputStream.read(buf)) > 0) {
                                out.write(buf, 0, lenght);
                                out.flush();
                            }
                            String string = out.toString("UTF-8");
                            callback.response(responseCode, string);
                            out.close();
                            inputStream.close();
                        }
                    }
                    httpURL.disconnect();
                } catch (Exception e) {
                    if (callback != null) {
                        callback.failure(e.getMessage());
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void POST(String url, HttpRequestCallback callback) {
        POST(url, null, callback);
    }

    public static void POST(String url, HashMap<String, String> map) {
        POST(url, map, null);
    }

    public static void POST(String url) {
        POST(url, null, null);
    }

    private static HttpURLConnection getHttpURL(String requestUrl) throws Exception {
        URL               url         = new URL(requestUrl);
        HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
        return httpUrlConn;
    }
}
