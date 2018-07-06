/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import static android.view.KeyEvent.KEYCODE_BACK;


public class HelpFragment extends Fragment {

    String TAG = "help fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WebView webView = new WebView(getContext());
//        View view = inflater.inflate(R.layout.fragment_help, container, false);

        webView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        webView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        // TODO: 2018/7/5 上线时删去
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.loadUrl("http://anti-recall.qsboy.com");
//        webView.loadUrl("https://github.com/JasonQS/Anti-recall/blob/master/README.md");

        webView.setOnTouchListener((v, event) -> {
            webView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            return false;
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //设定加载开始的操作
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //设定加载结束的操作
            }
        });

        return webView;
    }

}
