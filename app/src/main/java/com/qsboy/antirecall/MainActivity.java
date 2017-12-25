package com.qsboy.antirecall;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.qsboy.antirecall.ui.MessageFragment;
import com.qsboy.antirecall.ui.Setting;
import com.qsboy.antirecall.ui.DummyContent;
import com.qsboy.antirecall.utils.CheckAuthority;
import com.ramotion.foldingcell.FoldingCell;

public class MainActivity extends AppCompatActivity implements MessageFragment.OnListFragmentInteractionListener{


    private static String TAG = "Main Activity";

    private SettingsFragment settingsFragment;
    private Setting setting;
    private MessageFragment messageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SqlScoutServer.create(this, getPackageName());
        // TODO: 2017/11/8 注意 删除软件会丢失一切信息
        settingsFragment = new SettingsFragment();
        setting = new Setting();
        messageFragment = new MessageFragment();

        CheckAuthority checkAuthority = new CheckAuthority(this);
        boolean overlayPermission = checkAuthority.checkAlertWindowPermission();
        Log.i(TAG, "authorized: " + overlayPermission);
        if (!overlayPermission) {
            Toast.makeText(getApplicationContext(), "为显示撤回的消息\n请授予悬浮窗权限", Toast.LENGTH_LONG).show();
        }
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.content, setting);
//        transaction.replace(R.id.content, settingsFragment);
//        transaction.replace(R.id.content, messageFragment);
//        transaction.commit();

        // get our folding cell
        final FoldingCell fc = findViewById(R.id.folding_cell);
        // attach click listener to folding cell
        fc.setOnClickListener(v -> fc.toggle(false));

        //跳转到辅助功能的设置
//        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//        startActivity(intent);


    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
