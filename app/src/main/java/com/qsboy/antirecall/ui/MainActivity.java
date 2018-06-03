/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.utils.CheckAuthority;
import com.qsboy.antirecall.utils.LogcatHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity {

    final String TAG = "Main Activity";
    List<Fragment> fragmentList = new ArrayList<>();

    // TODO: 03/06/2018 顶部加filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogcatHelper.getInstance().start();
        Date in = new Date();
        setContentView(R.layout.activity_horizontal_coordinator_ntb);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar);
//        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#009F90AF"));
//        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#9f90af"));

        initTabBar();
        App.deviceHeight = getWindowManager().getDefaultDisplay().getHeight();

        Date out = new Date();
        Log.d(TAG, "onCreateTime: " + (out.getTime() - in.getTime()));
    }

    private void initTabBar() {
        ViewPager viewPager = findViewById(R.id.vp_horizontal_ntb);
        if (fragmentList.size() < 3) {
            fragmentList.add(new QQFragment());
            fragmentList.add(new WeChatFragment());
            fragmentList.add(new SettingsFragment());
        }
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }
        });

        NavigationTabBar navigationTabBar = findViewById(R.id.ntb_horizontal);
        navigationTabBar.show();
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(new NavigationTabBar.Model.Builder(
                VectorDrawableCompat.create(getResources(), R.drawable.ic_qq, null),
                getResources().getColor(R.color.colorQQ))
                .title("QQ/Tim")
                .build());
        models.add(new NavigationTabBar.Model.Builder(
                VectorDrawableCompat.create(getResources(), R.drawable.ic_wechat, null),
                getResources().getColor(R.color.colorWX))
                .title("WeChat")
                .build());
        models.add(new NavigationTabBar.Model.Builder(
                VectorDrawableCompat.create(getResources(), R.drawable.ic_settings, null),
                getResources().getColor(R.color.colorTim))
                .title("SettingsFragment")
                .build());

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setBehaviorEnabled(true);

//        initFab(navigationTabBar);

    }

    private void initFab(NavigationTabBar navigationTabBar) {
//        final CoordinatorLayout coordinatorLayout = findViewById(R.id.parent);
//        findViewById(R.id.fab).setOnClickListener(v -> {
//            for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
//                final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
//                navigationTabBar.postDelayed(() -> {
//                    final String title = String.valueOf(new Random().nextInt(15));
//                    if (!model.isBadgeShowed()) {
//                        model.setBadgeTitle(title);
//                        model.showBadge();
//                    } else model.updateBadgeTitle(title);
//                }, i * 100);
//            }
//
//            coordinatorLayout.postDelayed(() -> {
//                final Snackbar snackbar = Snackbar.make(navigationTabBar, "Coordinator NTB", Snackbar.LENGTH_SHORT);
//                snackbar.getView().setBackgroundColor(Color.parseColor("#9b92b3"));
//                ((TextView) snackbar.getView().findViewById(R.id.snackbar_text))
//                        .setTextColor(Color.parseColor("#423752"));
//                snackbar.show();
//            }, 1000);
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_service) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        if (!new CheckAuthority(this).checkAlertWindowPermission()) {
            Log.i(TAG, "authorized: show warning");
            Toast.makeText(this, "请授予悬浮窗权限\n为了能正常显示撤回的消息 谢谢", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // for test
    public void prepareDataForTest() {
        Date in = new Date();
        Dao dao = Dao.getInstance(this, Dao.DB_NAME_QQ);
//        dao.deleteAll();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -9);
        for (int i = 1; i < 200; ) {
            for (int j = 1; j < 21; j++, i++) {
                dao.addMessage("Jason", "qs", String.valueOf(i), calendar.getTime().getTime());
                calendar.add(Calendar.MINUTE, 3);
                calendar.add(Calendar.SECOND, 3);
            }
            dao.addRecall(i, "Jason", "qs", String.valueOf(i - 1), calendar.getTime().getTime(), null, null, null, null, null);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Date out = new Date();
        Log.i(TAG, "prepareDataForTest: tvTime: " + (out.getTime() - in.getTime()));
    }

}
