/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
import com.qsboy.antirecall.db.QQDao;
import com.qsboy.utils.CheckAuthority;
import com.qsboy.utils.LogcatHelper;
import com.qsboy.utils.UpdateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity {

    final String TAG = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogcatHelper.getInstance().start();
        Date in = new Date();
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_horizontal_coordinator_ntb);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar);
//        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#009F90AF"));
//        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#9f90af"));

        initTabBar();

        checkUpdate();

        Date out = new Date();
        Log.d(TAG, "onCreateTime: " + (out.getTime() - in.getTime()));
    }

    private void initTabBar() {
        final ViewPager viewPager = findViewById(R.id.vp_horizontal_ntb);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new QQFragment();
                        break;
                    case 1:
                        fragment = new WeChatFragment();
                        break;
                    case 2:
                        fragment = new SettingsFragment();
                        break;
                }
                return fragment;
            }
        });

        final NavigationTabBar navigationTabBar = findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_qq),
                getResources().getColor(R.color.colorQQ))
                .title("QQ/Tim")
                .build());
        models.add(new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_wechat_),
                getResources().getColor(R.color.colorWX))
                .title("WeChat")
                .build());
        models.add(new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_settings),
                getResources().getColor(R.color.colorTim))
                .title("SettingsFragment")
                .build());

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.setBehaviorEnabled(true);

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

//        initFab(navigationTabBar);

    }

//    private void initFab(NavigationTabBar navigationTabBar) {
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
//    }

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

    private void checkUpdate() {
        //wifi环境下检查更新
        UpdateHelper helper = new UpdateHelper(this);
//        if (helper.isWifi())
        helper.checkUpdate();
    }

    // for test
    public void prepareDataForTest() {
        Date in = new Date();
        QQDao dao = QQDao.getInstance(this);
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
