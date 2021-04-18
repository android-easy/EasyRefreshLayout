package com.androideasy.library.http;


/**
 * 类功能:回调方法的封装，方便使用
 * 公司：AndroidEasy工作室
 * 作者：Easy
 * 邮箱：AndroidEasy@126.com
 * QQ：1400100300
 */
public interface OnNetResultListener {
    public void onSuccess(String successResult);
    public void onFailure(String errorResult);
}
