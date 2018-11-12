package com.zhxh.base.component;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.zhxh.base.R;

/**
 * Created by zhxh on 2018/11/12
 */
public class ProgressDrawableLoadingView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.layout_recycler_load_more_view;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }

}
