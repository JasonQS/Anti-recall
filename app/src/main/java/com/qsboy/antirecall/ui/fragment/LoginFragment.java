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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.ui.activyty.App;
import com.qsboy.antirecall.ui.activyty.MainActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginFragment extends Fragment {

    String TAG = "Login Fragment";
    Toolbar toolbar;
    String phone = "";
    // TODO: 2018/7/1 发布时取消初始化
    String captcha = "0000";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            toolbar = activity.findViewById(R.id.toolbar);
        }
        toolbar.setTitle("注册/登录");

        EditText etPhone = view.findViewById(R.id.et_phone);
        EditText etCaptcha = view.findViewById(R.id.et_captcha);
        Button btnSendMsg = view.findViewById(R.id.btn_sent_msg);
        Button btnVerify = view.findViewById(R.id.btn_verity);

        btnSendMsg.setOnClickListener(v -> {
            if (etPhone.getText().length() == 11) {
                phone = etPhone.getText().toString();
                sendMsg();
            }
        });

        btnVerify.setOnClickListener(v -> {
            if (captcha.equals(etCaptcha.toString()) || captcha.equals(""))
                if (getFragmentManager() != null) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activity_main, new PriceFragment())
                            .addToBackStack("price")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
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
                    new Handler().postDelayed(() -> {
                        if (getFragmentManager() != null)
                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.activity_main, new PriceFragment())
                                    .addToBackStack("price")
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .commit();
                    }, 500);
                }

                if (captcha.length() == editable.length())
                    if (!captcha.equals(editable.toString()) && !captcha.equals(""))
                        btnVerify.setText("✘︎");
            }
        });

        return view;
    }

    private void sendMsg() {
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
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        toolbar.setTitle(R.string.app_name);
    }
}
