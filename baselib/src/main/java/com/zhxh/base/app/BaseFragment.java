package com.zhxh.base.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhxh.base.config.ActivityRequestContext;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by zhxh on 2018/8/7
 */
public abstract class BaseFragment extends Fragment {
    protected BaseActivity baseActivity;

    protected CompositeDisposable mDisposables = new CompositeDisposable();

    protected View rootView;

    protected abstract int getLayoutId();

    /**
     * 初始化控件
     */
    protected abstract void initView(View view);


    /**
     * 请求数据
     */
    public abstract void requestData();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        baseActivity = (BaseActivity) context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
        }
        getInitBundle();
        initView(rootView);
        return rootView;
    }

    protected ActivityRequestContext initRequest;

    /**
     * 获取Intent传递过来的请求参数
     */
    public void getInitBundle() {
        Intent intent = baseActivity.getIntent();
        if (intent == null)
            return;
        Bundle bundle = intent.getExtras();

        if (bundle == null)
            return;
        initRequest = (ActivityRequestContext)
                bundle.getSerializable("initRequest");

    }

    protected Resources getResource() {
        return getContext().getResources();
    }

    public void onNetWorkChange(boolean isConnected) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDisposables != null) {
            mDisposables.clear();
        }
    }

}
