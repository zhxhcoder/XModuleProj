package com.zhxh.login;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.zhxh.base.app.BaseActivity;
import com.zhxh.xlibkit.rxbus.RxBus;

@Route(path = "/account/login")
public class LoginActivity extends BaseActivity {

    private TextView tvState;

    @Override
    protected void setLayout() {

    }

    @Override
    protected void refreshData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        updateLoginState();
    }

    private void initView() {
        tvState = (TextView) findViewById(R.id.tv_login_state);
    }

    public void login(View view) {
        UserManager.userInfo = new UserInfo("10086", "Admin");
        updateLoginState();
    }

    private void updateLoginState() {
        tvState.setText("这里是登录界面：" + (UserManager.userInfo == null ? "未登录" : UserManager.userInfo.getUserName()));
    }

    public void exit(View view) {
        UserManager.userInfo = null;
        updateLoginState();
    }

    public void loginShare(View view) {
        RxBus.getDefault().post("loginShareTest");
        ARouter.getInstance().build("/share/share").withString("share_content", "分享数据到微博").navigation();
    }
}
