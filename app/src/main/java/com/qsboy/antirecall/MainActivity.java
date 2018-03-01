package com.qsboy.antirecall;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.qsboy.antirecall.db.Messages;
import com.qsboy.antirecall.ui.MultiMessagesAdapter;
import com.qsboy.antirecall.utils.CheckAuthority;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final String TAG = "Main Activity";

    public static ArrayList<Messages> messagesList = new ArrayList<>();
    MultiMessagesAdapter adapter;
    RecyclerView recyclerView;

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_monitor:
                    mTextMessage.setText(R.string.title_monitor);
                    return true;
                case R.id.navigation_setting:
                    mTextMessage.setText(R.string.title_setting);
                    // 跳转到辅助功能的设置
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        }
    };

    //todo 查找的时候加个program bar提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (!new CheckAuthority(this).checkAlertWindowPermission()) {
            Log.i(TAG, "authorized: show warning");
            Toast.makeText(this, "为显示撤回的消息\n请授予悬浮窗权限", Toast.LENGTH_LONG).show();
        }

        final FoldingCell fc = findViewById(R.id.folding_cell);
        fc.setOnClickListener(v -> fc.toggle(false));

    }

}
