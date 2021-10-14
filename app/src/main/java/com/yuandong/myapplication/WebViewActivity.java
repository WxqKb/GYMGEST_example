package com.yuandong.myapplication;

import static android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private WebView webView;
    private String authRes;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.purple_700));

        initView();
        initEvent();
        setWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 开启JavaScript交互
        // 核心方法, 用于处理JavaScript被执行后的回调
        webSettings.setMixedContentMode(MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webView.addJavascriptInterface(new JsCallback() {
            @JavascriptInterface // 注意:此处一定要加该注解,否则在4.1+系统上运行失败
            @Override
            public void postMessage(String method, String params) {
                Log.e("postMessage", method);
                if (method.equals("ready")) {
                    WebViewActivity.this.runOnUiThread(() -> {
                        Gson gson = new Gson();
                        Map map = new HashMap();
                        map.put("appId", "CVTE_CLUB");
                        map.put("debug", true);
                        webView.loadUrl("javascript:sdk_config(" + gson.toJson(map) + ")");
                    });
                } else if (method.equals("gymgest_authorization_success")) {
                    Log.e("auth_success", params);
                    Toast.makeText(getApplicationContext(), params, Toast.LENGTH_SHORT).show();
                    authRes = params;
                    setResult(RESULT_OK, getIntent().putExtra("res", authRes));
                    finish();
                }
            }
        }, "GYMGEST_SDK");// 参1是回调接口的实现; 参2是JavaScript回调对象的名称

        // 系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl("https://yd.gymgest.com.cn/marketing/index.html#/authorization");
    }

    void initEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setResult(RESULT_OK, getIntent().putExtra("res", authRes));
                finish();
            }
        });
    }

    void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = findViewById(R.id.qs_web_view);
    }
}