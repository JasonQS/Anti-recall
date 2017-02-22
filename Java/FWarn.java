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


public class FWarn extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.f_warn, container, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOnClick();
            }
        });

        return view;
    }

    private void menuOnClick(){

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.setCustomAnimations(R.anim.menu_enter, R.anim.menu_exit, R.anim.menu_enter, R.anim.menu_exit);
        transaction.remove(this);
        transaction.show(fm.findFragmentByTag("menu"));
        transaction.commit();

        fm.popBackStack();
        Log.i(TAG, "warn on click");
    }

    String TAG = "warn";

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "on destroy");

        System.gc();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "on detach");
    }

}
