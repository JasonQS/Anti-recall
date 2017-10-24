package com.qsboy.antirecall;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.qsboy.antirecall.dummy.DummyContent;
import com.qsboy.antirecall.utils.CheckAuthority;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {


    private static String TAG = "Main Activity";

    private ItemFragment itemFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemFragment = new ItemFragment();
        settingsFragment = new SettingsFragment();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        CheckAuthority checkAuthority = new CheckAuthority(this);
        boolean overlayPermission = checkAuthority.checkAlertWindowPermission();
        Log.i(TAG, "authorized: " + overlayPermission);
        if (!overlayPermission) {
            Toast.makeText(getApplicationContext(), "为显示撤回的消息\n请授予悬浮窗权限", Toast.LENGTH_LONG).show();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.replace(R.id.content, itemFragment);
                    break;
                case R.id.navigation_dashboard:

                    break;
                case R.id.navigation_notifications:
                    transaction.replace(R.id.content, settingsFragment);
                    break;
            }

            transaction.commit();

            return true;
        }

    };

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
