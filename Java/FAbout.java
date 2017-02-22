/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class FAbout extends Fragment {

    TextView version;

    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    ImageView image5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.f_about, container, false);

        version = (TextView) view.findViewById(R.id.version);

        setVersion();

        image1 = (ImageView) view.findViewById(R.id.about_me_image_1);
        image2 = (ImageView) view.findViewById(R.id.about_me_image_2);
        image3 = (ImageView) view.findViewById(R.id.about_me_image_3);
        image4 = (ImageView) view.findViewById(R.id.about_me_image_4);
        image5 = (ImageView) view.findViewById(R.id.about_me_image_5);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOnClick();
            }
        });

        return view;
    }

    private void setVersion() {

        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getContext().getPackageName(), 0);
            String appVersion = pi.versionName;
            Log.d("version", "appVersion=" + appVersion);
            version.setText(String.valueOf(appVersion));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void menuOnClick() {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.menu_enter, R.anim.menu_exit, R.anim.menu_enter, R.anim.menu_exit);
        transaction.remove(this);
        transaction.show(fm.findFragmentByTag("menu"));
        transaction.commit();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "on view create");
    }

    String TAG = "about";

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "on destroy");

        image1 = null;
        image2 = null;
        image3 = null;
        image4 = null;
        image5 = null;

        System.gc();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "on detach");
    }

}
