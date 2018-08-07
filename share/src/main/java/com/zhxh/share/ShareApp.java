package com.zhxh.share;

import android.app.Application;

import com.zhxh.base.app.BaseApp;
import com.zhxh.modulebase.ServiceFactory;

/**
 * Created by zhxh on 2018/8/7
 */
public class ShareApp extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
        initModuleApp(this);
        initModuleData(this);
    }

    @Override
    public void initModuleApp(Application application) {
        ServiceFactory.getInstance().setShareService(new ShareService());

    }

    @Override
    public void initModuleData(Application application) {

    }
}
