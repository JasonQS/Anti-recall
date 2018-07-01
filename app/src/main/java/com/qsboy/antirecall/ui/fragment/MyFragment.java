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

import com.qsboy.antirecall.R;
import com.qsboy.antirecall.ui.activyty.App;

public class MyFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_my, container, false);

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
                        else
                            getFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.activity_main, new LoginFragment())
                                    .addToBackStack("login")
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .commit();
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
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_main, new LoginFragment())
                    .addToBackStack("login")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

}
