/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.ui.activyty.MainActivity;
import com.qsboy.antirecall.ui.adapter.PricePagerAdapter;


public class PriceFragment extends Fragment {

    // TODO: 28/06/2018 这一页单独分开来成为"我的" 并在下面统计截获次数等使用情况
    // TODO: 28/06/2018 聊天分布
    Toolbar toolbar;
    String TAG = "Price Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        setHasOptionsMenu(true);

        MainActivity activity = (MainActivity) getActivity();
        toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setTitle("选择套餐");
        activity.setSupportActionBar(toolbar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager = view.findViewById(R.id.horizontalInfiniteCycleViewPager);
        horizontalInfiniteCycleViewPager.setAdapter(new PricePagerAdapter(getActivity()));

        horizontalInfiniteCycleViewPager.setScrollDuration(1000);
//        horizontalInfiniteCycleViewPager.setInterpolator(
//                AnimationUtils.loadInterpolator(getContext(), android.R.anim.overshoot_interpolator)
//        );
//        horizontalInfiniteCycleViewPager.setMediumScaled(false);
        horizontalInfiniteCycleViewPager.setMaxPageScale(0.8F);
//        horizontalInfiniteCycleViewPager.setMinPageScale(0.5F);
//        horizontalInfiniteCycleViewPager.setCenterPageScaleOffset(30.0F);
//        horizontalInfiniteCycleViewPager.setMinPageScaleOffset(5.0F);
//        horizontalInfiniteCycleViewPager.setOnInfiniteCyclePageTransformListener();

//        horizontalInfiniteCycleViewPager.setCurrentItem(
//                horizontalInfiniteCycleViewPager.getRealItem() + 1
//        );

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
