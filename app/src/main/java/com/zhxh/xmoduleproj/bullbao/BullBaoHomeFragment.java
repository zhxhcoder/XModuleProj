package com.zhxh.xmoduleproj.bullbao;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhxh.base.app.BaseLazyFragment;
import com.zhxh.base.config.ActivityRequestContext;
import com.zhxh.base.listener.FragmentBackPressListener;
import com.zhxh.base.utils.UIStatusBarHelper;
import com.zhxh.xlibkit.rxbus.RxBus;
import com.zhxh.xlibkit.ui.DisplayUtil;
import com.zhxh.xmoduleproj.R;
import com.zhxh.xmoduleproj.app.NetworkUnAvailableActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhxh on 2018/11/12
 */
public class BullBaoHomeFragment extends BaseLazyFragment implements FragmentBackPressListener {

    public static final String[] TAB_TITLE = {"赚牛宝", "兑好礼"};

    @BindView(R.id.mainTitleLayout)
    View mainTitleLayout;
    @BindView(R.id.titleBackBtn)
    View titleBackBtn;
    @BindView(R.id.titleShareBtn)
    View titleShareBtn;
    @BindView(R.id.titleName)
    TextView titleName;
    @BindView(R.id.titleShareImg)
    ImageView titleShareImg;

    @BindView(R.id.tabSegment)
    TabSegment tabSegment;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @BindView(R.id.bottomPager)
    NoTransViewPager bottomPager;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;


    @BindView(R.id.network_unavailable_bar)
    LinearLayout networkUnavailableBar;

    @BindView(R.id.headerLayout)
    ConstraintLayout headerLayout;

    HeaderViewHolder headerViewHolder;

    private List<BaseLazyFragment> fragments = new ArrayList<>(2);

    /**
     * 用于判断首页是头部已经加载完了
     */
    private BullBaoResponse baoResponse;

    HorizontalListAdapter horizontalAdapter;
    List<BullBaoItemData> signList = new ArrayList<>();

    public static BullBaoHomeFragment newInstance() {
        Bundle args = new Bundle();
        BullBaoHomeFragment fragment = new BullBaoHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.bull_bao_home_fragment;
    }

    @Override
    public void onFirstVisible() {
        super.onFirstVisible();

        titleName.setText("我的牛宝");
        titleShareBtn.setVisibility(View.VISIBLE);
        titleBackBtn.setOnClickListener(v -> Objects.requireNonNull(getActivity()).finish());
        titleShareBtn.setOnClickListener(v -> QuantManager.goBullBaoRecord(BullBaoRecordActivity.TYPE_MULTI));

        UIStatusBarHelper.translucent(getActivity());

        if (MyApplication.SKIN_MODE == MyApplication.SKIN_MODE_NIGHT) {
            UIStatusBarHelper.setStatusBarDarkMode(getActivity());
            titleShareImg.setImageResource(R.drawable.titlebar_white_history);
        } else {
            UIStatusBarHelper.setStatusBarLightMode(getActivity());
            titleShareImg.setImageResource(R.drawable.titlebar_black_history);
        }
        UIStatusBarHelper.setStatusBarPaddingAndHeightInsertView(rootView.findViewById(R.id.statusBarInsert), getContext());

        initNetWorkUnavailableBar();

        initHeaderView();

        initTabFragments();
        initTabSegment();
        initRefresh();

        loadData();

        initSubscribe();
    }

    private void initSubscribe() {
        RxBus.getDefault().subscribe(this, QuantManager.BULL_BAO_SIGN_SUCCESS, new RxBus.Callback<String>() {
            @Override
            public void onEvent(String s) {
                if (String.valueOf(BullBaoMainDialog.Companion.getTYPE_MAIN_SIGN()).equals(s)) {
                    loadData();
                }
            }
        });
        RxBus.getDefault().subscribe(this, QuantManager.BULL_BAO_HOME_RECEIVE, new RxBus.Callback<String>() {
            @Override
            public void onEvent(String s) {
                loadData();
            }
        });
    }

    private void initNetWorkUnavailableBar() {
        networkUnavailableBar.setVisibility(View.GONE);
        networkUnavailableBar.setOnClickListener(v -> baseActivity.moveNextActivity(NetworkUnAvailableActivity.class, new ActivityRequestContext()));
    }

    private void initHeaderView() {
        headerViewHolder = new HeaderViewHolder(headerLayout);
        GridLayoutManager mgr = new GridLayoutManager(getActivity(), 7);
        headerViewHolder.signRecyclerView.setLayoutManager(mgr);

        horizontalAdapter = new HorizontalListAdapter();
        headerViewHolder.signRecyclerView.setAdapter(horizontalAdapter);
    }

    private class HorizontalListAdapter extends BaseQuickAdapter<BullBaoItemData, BaseViewHolder> {

        HorizontalListAdapter() {
            super(R.layout.item_bull_bao_sign);
        }

        @Override
        protected void convert(BaseViewHolder helper, BullBaoItemData item) {
            ImageView iv_status = helper.getView(R.id.iv_status);
            ImageView iv_redpack = helper.getView(R.id.iv_redpack);
            TextView tv_redpack = helper.getView(R.id.tv_redpack);
            TextView tv_time = helper.getView(R.id.tv_time);

            tv_redpack.setText(item.getScore());
            tv_time.setText(item.getIndex() + "天");

            if ("0".equals(item.getStatus())) {//已领取
                iv_status.setVisibility(View.VISIBLE);
                iv_status.setImageResource(R.drawable.redpacket_gray_label);
                iv_redpack.setImageResource(R.drawable.redpacket_gray);
                tv_redpack.setTextColor(DisplayUtil.parseColor("#dadada"));
            } else if ("1".equals(item.getStatus())) {//今日领
                iv_status.setVisibility(View.VISIBLE);
                iv_status.setImageResource(R.drawable.redpacket_get_label);
                iv_redpack.setImageResource(R.drawable.redpacket_get);
                tv_redpack.setTextColor(DisplayUtil.parseColor("#ffe270"));
            } else if ("2".equals(item.getStatus())) {//明日领
                iv_status.setVisibility(View.VISIBLE);
                iv_status.setImageResource(R.drawable.redpacket_hint_label);
                iv_redpack.setImageResource(R.drawable.redpacket_cannotget);
                tv_redpack.setTextColor(DisplayUtil.parseColor("#feeb78"));
            } else {//未领
                iv_status.setVisibility(View.INVISIBLE);
                iv_redpack.setImageResource(R.drawable.redpacket_cannotget);
                tv_redpack.setTextColor(DisplayUtil.parseColor("#feeb78"));
            }

        }
    }

    protected class HeaderViewHolder {

        @BindView(R.id.iv_banner)
        ImageView iv_banner;

        @BindView(R.id.tv_username)
        TextView tv_username;
        @BindView(R.id.tv_record)
        TextView tv_record;
        @BindView(R.id.xbtnSign)
        XButton xbtnSign;

        @BindView(R.id.tv_left1)
        TextView tv_left1;
        @BindView(R.id.tv_left2)
        TextView tv_left2;
        @BindView(R.id.tv_right1)
        TextView tv_right1;
        @BindView(R.id.tv_right2)
        TextView tv_right2;

        @BindView(R.id.tv_sign_tips)
        TextView tv_sign_tips;

        @BindView(R.id.signRecyclerView)
        RecyclerView signRecyclerView;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    private void initRefresh() {
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            requestNetData();
            refreshCurrentChild(refreshLayout);
        });
    }

    /**
     * 刷新底部 fragment
     */
    private void refreshCurrentChild(RefreshLayout refreshLayout) {
        int currentItem = bottomPager.getCurrentItem();
        if (currentItem >= 0 && currentItem < 2) {
            OnRefreshListener child = (OnRefreshListener) fragments.get(currentItem);
            child.onRefresh(refreshLayout);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 内容页的适配器
     */
    private class ContentAdapter extends FragmentPagerAdapter {

        public ContentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_TITLE[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private void initTabFragments() {
        fragments.add(BullBaoHomeListFragment.newInstance(BullBaoHomeListFragment.bull_home_task));
        fragments.add(BullBaoHomeListFragment.newInstance(BullBaoHomeListFragment.bull_home_gift));
        bottomPager.setNoScroll(true);
        bottomPager.setAdapter(new ContentAdapter(getChildFragmentManager()));
        bottomPager.setOffscreenPageLimit(2);
    }


    private void initTabSegment() {
        tabSegment.setDefaultNormalColor(ImageUtil.getHexColor(R.color.C4));
        tabSegment.setDefaultSelectedColor(ImageUtil.getHexColor(R.color.C13));
        tabSegment.setMode(TabSegment.MODE_FIXED);
        tabSegment.setTypefaceProvider(new TabSegment.TypefaceProvider() {
            @Override
            public boolean isNormalTabBold() {
                return true;
            }

            @Override
            public boolean isSelectedTabBold() {
                return true;
            }
        });
        tabSegment.setShowBottomStrict((ContextCompat.getColor(tabSegment.getContext(), R.color.C9)), ContextCompat.getColor(tabSegment.getContext(), R.color.divider));
        tabSegment.setupWithViewPager(bottomPager, true, false);
        tabSegment.addOnTabSelectedListener(new TabSegment.SimpleTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                refreshCurrentChild(null);
            }
        });
    }

    /**
     * 加载数据 有网络的情况下去加载网络数据 无网络状态加载本地
     */
    private void loadData() {
        if (!NetworkUtils.isConnected()) {
            requestSignHeader();
        } else {
            requestSignHeader();
        }
    }

    private void requestSignHeader() {
        List<KeyValueData> params = new ArrayList<>();
        params.add(new KeyValueData("userToken", UserManager.userToken()));

        mDisposables.add(RxHttp.call(RequestCommand.COMMAND_BULLBAO_SIGN_RECORD, params, BullBaoResponse.class, data -> {

            refreshLayout.finishRefresh(true);

            baoResponse = data;

            headerViewHolder.tv_username.setText(data.getData().getNiubao().getUsername());
            headerViewHolder.tv_left1.setText(data.getData().getNiubao().getTotalscore());
            headerViewHolder.tv_right1.setText(data.getData().getNiubao().getTodayscore());

            headerViewHolder.xbtnSign.setText("签到");
            headerViewHolder.tv_sign_tips.setText(data.getData().getSignindays());

            CommonUtils.showImage(data.getData().getBanner().getDisplayContent(), headerViewHolder.iv_banner, R.drawable.default_logo);

            if (!data.getData().isTodaysign()) {
                headerViewHolder.xbtnSign.setClickable(true);
                headerViewHolder.xbtnSign.setText("签到");
                headerViewHolder.xbtnSign.setTextColor(ImageUtil.getHexColor((R.color.C9)));
                headerViewHolder.xbtnSign.setSolidColor(ImageUtil.getHexColor((R.color.C12)));
            } else {
                headerViewHolder.xbtnSign.setClickable(false);
                headerViewHolder.xbtnSign.setText("已签到");
                headerViewHolder.xbtnSign.setTextColor(ImageUtil.getHexColor((R.color.C9)));
                headerViewHolder.xbtnSign.setSolidColor(ImageUtil.getHexColor((R.color.C5)));
            }
            headerViewHolder.xbtnSign.setOnClickListener(v -> {
                if (!data.getData().isTodaysign()) {
                    QuantManager.requestBullBaoSign(getActivity(), BullBaoMainDialog.Companion.getTYPE_MAIN_SIGN());
                }
            });
            headerViewHolder.tv_record.setOnClickListener(v -> {
                QuantManager.goBullBaoRecord(BullBaoRecordActivity.TYPE_MULTI);

            });

            headerViewHolder.iv_banner.setOnClickListener(v -> QuantManager.goCommonWeb(data.getData().getBanner().getTitle(), data.getData().getBanner().getUrl()));

            signList = data.getData().getList();
            horizontalAdapter.replaceData(signList);

        }));
    }

    /**
     * 请求网络数据
     */
    private void requestNetData() {

        requestSignHeader();
    }

    @Override
    public void onNetWorkChange(boolean isConnected) {
        super.onNetWorkChange(isConnected);
        //如果页面是缓存数据则在网络重连后重新请求网络数据
        if (isConnected) {
            networkUnavailableBar.setVisibility(View.GONE);
            if (baoResponse == null) {
                requestNetData();
            }
        } else {
            networkUnavailableBar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onFragmentResume(boolean firstResume) {
        super.onFragmentResume(firstResume);

        if (!NetworkUtils.isConnected()) {
            networkUnavailableBar.setVisibility(View.VISIBLE);
        } else {
            networkUnavailableBar.setVisibility(View.GONE);
        }

        requestNetData();
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
    }


    @OnClick({R.id.xbtnSign, R.id.tv_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.xbtnSign:

                break;
            case R.id.tv_record:

                break;
            default:
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
