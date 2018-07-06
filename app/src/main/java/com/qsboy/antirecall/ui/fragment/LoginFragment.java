/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.fragment;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.ui.activyty.App;
import com.qsboy.antirecall.ui.activyty.MainActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoginFragment extends Fragment {

    String TAG = "Login Fragment";
    Toolbar toolbar;
    String phone = "";
    // TODO: 2018/7/1 发布时取消初始化
    String captcha = "";
    boolean isGoingToBuy = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        setHasOptionsMenu(true);

        if (getArguments() != null)
            isGoingToBuy = "true".equals(getArguments().getString("isGoingToBuy"));

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            toolbar = activity.findViewById(R.id.toolbar);
        }
        toolbar.setTitle("注册/登录");

        EditText etPhone = view.findViewById(R.id.et_phone);
        EditText etCaptcha = view.findViewById(R.id.et_captcha);
        Button btnSendMsg = view.findViewById(R.id.btn_sent_msg);
        Button btnVerify = view.findViewById(R.id.btn_verity);
        btnSendMsg.setText("发送短信");
        btnVerify.setText("验证");

        btnSendMsg.setOnClickListener(v -> {
            if (etPhone.getText().length() == 11) {
                phone = etPhone.getText().toString();
                sendMsg();
                ((Button) v).setText("已发送");
            }
        });

        btnVerify.setOnClickListener(v -> {
            if (captcha.equals(etCaptcha.toString())) {
                if (verifyCaptcha(captcha))
                    onLoginFinished();
            } else if (captcha.equals("")) {
                if (verifyCaptcha(captcha))
                    onLoginFinished();
                else
                    btnVerify.setText("✘︎");
            } else
                btnVerify.setText("✘︎");

        });

        etPhone.setText(App.phone);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                App.phone = s.toString();
            }
        });

        etCaptcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                Log.w(TAG, "afterTextChanged: " + editable);
                if (captcha.equals(editable.toString())) {
                    btnVerify.setText("✔︎");
                    new Handler().postDelayed(() -> onLoginFinished(), 500);
                }

                if (captcha.length() == editable.length()) {
                    if (!captcha.equals(editable.toString()) && !captcha.equals(""))
                        btnVerify.setText("✘︎");
                } else
                    btnVerify.setText("验证");

            }
        });

        return view;
    }

    private void onLoginFinished() {
        if (isGoingToBuy)
            openPriceFragment();
        else if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }

    private void openPriceFragment() {
        if (getFragmentManager() != null)
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main, new PriceFragment())
                    .addToBackStack("price")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
    }

    private void sendMsg() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, "http://ar.qsboy.com/j/applyCaptcha",
                response -> {
                    Log.i(TAG, "sendMsg: " + response);
                    captcha = response;
                },
                error -> Log.e(TAG, "sendMsg: " + error)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("phone", phone);
                return map;
            }
        };
        queue.add(request);
    }

    private boolean verifyCaptcha(String captcha) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        RequestFuture future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST,
                "http://ar.qsboy.com/j/verifyCaptcha", future, future) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("phone", phone);
                map.put("captcha", captcha);
                return map;
            }
        };

        queue.add(request);

        try {
            String s = (String) future.get(5, TimeUnit.SECONDS);
            boolean isValid = s != null && s.length() > 0;
            if (isValid) {
                Gson gson = new Gson();
                gson.fromJson(s, App.User.class);
            }
            return isValid;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        toolbar.setTitle(R.string.app_name);
    }
}
