package com.androideasy.library.http;

import android.os.Handler;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 类功能:OkHttp的简单封装 ，方便使用
 * 公司：AndroidEasy工作室
 * 作者：Easy
 * 邮箱：AndroidEasy@126.com
 * QQ：1400100300
 */
class OkHttpUtils {
    public enum REQUEST_TYPE {
        POST, GET
    }

    private Handler handler;
    private OkHttpClient client;

    private static OkHttpUtils INSTANCE;

    private OkHttpUtils() {
        handler = new Handler();
        client = new OkHttpClient.Builder()
                // 等线程池, 缓存, 文件下载讲完之后再来修改添加
//                .retryOnConnectionFailure()
//                .cache()
//                .readTimeout()
                .build();
    }

    public static OkHttpUtils getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new OkHttpUtils();
        }
        return INSTANCE;
    }


    protected void sendRequest(REQUEST_TYPE request_type, final String url, final Map<String, String> header, final Map<String, String> body, final OnNetResultListener listener) {
        // 构建一个Request  Builder
        Request.Builder requestBuilder = new Request.Builder();
        //设置请求的url
        requestBuilder.url(url);
        //设置请求头
        setHeader(requestBuilder, header);

        if (request_type == REQUEST_TYPE.POST) {
            // 构建请求体
            RequestBody requesBody = getRequestBody(body);
            if(requesBody == null){
                // 构建一个Request
                Request request = requestBuilder.build();
                //发送一个请求   并处理回调
                createCall(request, listener);
            }else{
                // 构建一个Request
                Request request = requestBuilder.post(requesBody).build();
                //发送一个请求   并处理回调
                createCall(request, listener);
            }

        } else if (request_type == REQUEST_TYPE.GET) {
            // 构建一个Request
            Request request = requestBuilder.get().build();
            //发送一个请求   并处理回调
            createCall(request, listener);
        }

    }


    private RequestBody getRequestBody(final Map<String, String> body) {
        if(body==null || body.size()<=0){
            return null;
        }
        FormBody.Builder buildder = new FormBody.Builder();
        Set set = body.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = body.get(key);
            buildder.add(key, value);
        }
        RequestBody requesBody = buildder.build();
        return requesBody;
    }

    private void setHeader(Request.Builder requestBuilder, final Map<String, String> header) {
        if(header==null || header.size()<=0){
            return;
        }
        Set headSet = header.keySet();
        Iterator headI = headSet.iterator();
        while (headI.hasNext()) {
            String k = (String) headI.next();
            String v = header.get(k);
            requestBuilder.addHeader(k, v);
        }
    }

    //请求并处理回调
    private void createCall(Request request, final OnNetResultListener listener) {

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                final String errMsg = e.getMessage();
                // 发回主线程
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(errMsg);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String str = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess(str);
                    }
                });
            }
        });

    }


//    // *********************提供给外部的操作方法*************************
//
//    // 封装: 对外提供使用方式, 具体的实现细节封装起来
//    public void startGet(String url, OnNetResultListener listener) {
////        _startGet(url, listener);
//        sendRequest(REQUEST_TYPE.GET, url, null, null, listener);
//    }
//
//
//    public void startHeader(String url, Map<String, String> headers, OnNetResultListener listener) {
//        sendRequest(REQUEST_TYPE.GET, url, headers, null, listener);
//    }
//
//
//    public void startPost(String url, Map<String, String> body, OnNetResultListener listener) {
//        sendRequest(REQUEST_TYPE.POST, url, null, body, listener);
//    }
//
//    public void startPost(String url, Map<String, String> body, Map<String, String> headers, OnNetResultListener listener) {
//        sendRequest(REQUEST_TYPE.POST, url, headers, body, listener);
//    }
}
