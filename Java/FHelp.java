/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FHelp extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.f_help, container, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOnClick();
            }
        });

        return view;
    }

    private void menuOnClick() {
        Log.i(TAG, "help on click");

        FWarn warn = new FWarn();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.menu_enter, R.anim.menu_exit, R.anim.menu_enter, R.anim.menu_exit);
        transaction.replace(R.id.layout_menu, warn);
        transaction.show(warn);
        transaction.commit();
        fm.popBackStack();

    }

    String TAG = "help";

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "on destroy");

        System.gc();

    }

}
