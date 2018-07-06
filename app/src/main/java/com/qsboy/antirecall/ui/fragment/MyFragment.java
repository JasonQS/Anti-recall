/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.fragment;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qsboy.antirecall.R;
import com.qsboy.antirecall.ui.activyty.App;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_my, container, false);

        TextView userType = view.findViewById(R.id.userType);
        switch (App.User.userType) {
            case 1:
                userType.setText("普通用户");
                break;
            case 2:
                userType.setText("普通会员");
                break;
            case 3:
                userType.setText("高级会员");
                break;
            case 4:
                userType.setText("超级会员");
                break;
        }

        TextView subscribeTime = view.findViewById(R.id.subscribe_time);
        subscribeTime.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(App.User.subscribeTime));

        // 购买
        view.findViewById(R.id.btn_show_price)
                .setOnClickListener(v -> {
                    if (getFragmentManager() != null) {
                        if (App.isLoggedin)
                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.activity_main, new PriceFragment())
                                    .addToBackStack("price")
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .commit();
                        else {
                            LoginFragment loginFragment = new LoginFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("isGoingToBuy", "true");
                            loginFragment.setArguments(bundle);
                            getFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.activity_main, loginFragment)
                                    .addToBackStack("login")
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .commit();
                        }
                    }

                });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_my, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getFragmentManager() != null) {
            LoginFragment loginFragment = new LoginFragment();
            Bundle bundle = new Bundle();
            bundle.putString("isGoingToBuy", "false");
            loginFragment.setArguments(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_main, loginFragment)
                    .addToBackStack("login")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

}
