package com.androideasy.library.http;

import java.util.Map;

/**
 * 类功能:提供给外部的操作方法
 * 公司：AndroidEasy工作室
 * 作者：Easy
 * 邮箱：AndroidEasy@126.com
 * QQ：1400100300
 */
public class HttpUtils {
    // *********************提供给外部的操作方法*************************

    // 封装: 对外提供使用方式, 具体的实现细节封装起来
    public static void get(String url, OnNetResultListener listener) {
//        _startGet(url, listener);
        OkHttpUtils.getInstance().sendRequest(OkHttpUtils.REQUEST_TYPE.GET, url, null, null, listener);
    }


    public static void get(String url, Map<String, String> headers, OnNetResultListener listener) {
        OkHttpUtils.getInstance().sendRequest(OkHttpUtils.REQUEST_TYPE.GET, url, headers, null, listener);
    }


    public static void post(String url, Map<String, String> body, OnNetResultListener listener) {
        OkHttpUtils.getInstance().sendRequest(OkHttpUtils.REQUEST_TYPE.POST, url, null, body, listener);
    }

    public static void post(String url, Map<String, String> body, Map<String, String> headers, OnNetResultListener listener) {
        OkHttpUtils.getInstance().sendRequest(OkHttpUtils.REQUEST_TYPE.POST, url, headers, body, listener);
    }

}
