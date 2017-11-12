package com.qsboy.antirecall;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.idescout.sql.SqlScoutServer;
import com.qsboy.antirecall.ui.Setting;
import com.qsboy.antirecall.utils.CheckAuthority;

public class MainActivity extends AppCompatActivity {


    private static String TAG = "Main Activity";

    private SettingsFragment settingsFragment;
    private Setting setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SqlScoutServer.create(this, getPackageName());
        // TODO: 2017/11/8 注意 删除软件会丢失一切信息
        settingsFragment = new SettingsFragment();
        setting = new Setting();

        CheckAuthority checkAuthority = new CheckAuthority(this);
        boolean overlayPermission = checkAuthority.checkAlertWindowPermission();
        Log.i(TAG, "authorized: " + overlayPermission);
        if (!overlayPermission) {
            Toast.makeText(getApplicationContext(), "为显示撤回的消息\n请授予悬浮窗权限", Toast.LENGTH_LONG).show();
        }
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.content, setting);
//        transaction.replace(R.id.content, settingsFragment);
//        transaction.commit();

    }

}
