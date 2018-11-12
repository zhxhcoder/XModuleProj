package com.zhxh.xmoduleproj.app;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.zhxh.base.app.BaseActivity;
import com.zhxh.xcomponentlib.XButton;
import com.zhxh.xmoduleproj.R;

import butterknife.BindView;

/**
 * Created by zhxh on 2018/11/12
 */
public class NetworkUnAvailableActivity extends BaseActivity {
    @BindView(R.id.to_settings_btn)
    XButton to_settings_btn;

    @Override
    protected void setLayout() {
        setContentView(R.layout.network_disconnect_layout);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        to_settings_btn.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        });
    }

    @Override
    protected void refreshData() {
    }
}
