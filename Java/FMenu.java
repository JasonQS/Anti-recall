/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.qiansheng.messagecapture.Debug.DebugEnabled;

public class FMenu extends Fragment implements View.OnClickListener {

    private final String TAG = "FMenu";

    View btn_debug;

    FragmentManager fm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.f_menu, container, false);

        initBtn(view);

        return view;
    }

    void initBtn(View view) {

        View menu = view.findViewById(R.id.layout_menu);
        menu.setOnClickListener(this);

        View btn_help = view.findViewById(R.id.btn_help);
        View btn_warn = view.findViewById(R.id.btn_warn);
        View btn_about = view.findViewById(R.id.btn_about);
        View btn_about_me = view.findViewById(R.id.btn_about_me);
        View btn_skip = view.findViewById(R.id.btn_skip);
        btn_debug = view.findViewById(R.id.btn_debug);

        btn_help.setOnClickListener(this);
        btn_warn.setOnClickListener(this);
        btn_about.setOnClickListener(this);
        btn_about_me.setOnClickListener(this);
        btn_skip.setOnClickListener(this);
        btn_debug.setOnClickListener(this);

        if (DebugEnabled) {
            XLogcat.getInstance().start();
            view.findViewById(R.id.textView_debug).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBtnOn));

        } else {
            XLogcat.getInstance().stop();
            view.findViewById(R.id.textView_debug).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBtnOff));

        }
    }

    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.menu_enter, R.anim.menu_exit, R.anim.menu_enter, R.anim.menu_exit);

        switch (v.getId()) {
            case R.id.layout_menu:
                Log.i(TAG, "menu on click");
                transaction.remove(this);
                transaction.commit();
                System.out.println(fm.getFragments());
                fm.popBackStack(null, 1);
                System.out.println(fm.getBackStackEntryCount());
                break;
            case R.id.btn_help:
                Log.i(TAG, "help on click");
                Fragment help = new FHelp();
                transaction.add(R.id.layout_menu, help);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.btn_warn:
                Log.i(TAG, "warn on click");
                Fragment warn = new FWarn();
                transaction.add(R.id.layout_menu, warn);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.btn_about:
                Log.i(TAG, "about on click");
                Fragment about = new FAbout();
                transaction.add(R.id.layout_menu, about);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.btn_about_me:
                Log.i(TAG, "about_me on click");
                Fragment about_me = new FAboutMe();
                transaction.add(R.id.layout_menu, about_me);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.btn_skip:
                Log.i(TAG, "skip on click");
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                break;
            case R.id.btn_debug:
                Log.i(TAG, "debug on click");
                Debug debug = new Debug(getContext(), btn_debug);
                debug.onClick();
                break;

        }

    }

}
