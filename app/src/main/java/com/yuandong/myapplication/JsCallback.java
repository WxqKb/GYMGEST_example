package com.yuandong.myapplication;

import android.webkit.JavascriptInterface;

// 定义回调接口
public interface JsCallback {

    @JavascriptInterface
    public void postMessage(String method, String params);
}