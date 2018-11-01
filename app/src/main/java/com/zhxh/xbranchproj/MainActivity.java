package com.zhxh.xbranchproj;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.zhxh.base.app.BaseActivity;
import com.zhxh.base.network.KeyValueData;
import com.zhxh.base.network.RequestCommand;
import com.zhxh.base.network.RxHttp;
import com.zhxh.xlibkit.common.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<KeyValueData> params = new ArrayList<>();
        params.add(new KeyValueData("userToken", "heheda"));
        mDisposables.add(RxHttp.call(RequestCommand.COMMAND_APP_PUSH_CONFIG, params, data -> {
            LogUtil.d("RxHttpRxHttp", data);
        }));

    }

    /**
     * 跳登录界面
     *
     * @param view
     */
    public void login(View view) {
        ARouter.getInstance().build("/account/login").navigation();
    }

    /**
     * 跳分享界面
     *
     * @param view
     */
    public void share(View view) {
        ARouter.getInstance().build("/share/share").withString("share_content", "分享数据到微博").navigation();
    }

    /**
     * 跳 FragmentActivity
     *
     * @param view
     */
    public void fragment(View view) {
        startActivity(new Intent(this, FragmentActivity.class));
    }

    public void toList(View view) {
        startActivity(new Intent(this, FragmentActivity.class));
    }
}
