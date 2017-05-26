package com.fxc.lib.net;

public interface HttpRequestCallback {
    void response(int responseCode, String respone);
    boolean response(int responseCode);//返回false则不执行response(int responseCode,String respone)方法
    void failure(String error);
}
