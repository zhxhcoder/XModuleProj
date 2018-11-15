package com.zhxh.xmoduleproj.bullbao;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhxh.base.app.BaseLazyFragment;
import com.zhxh.base.component.ProgressDrawableLoadingView;
import com.zhxh.base.config.CommonDataManager;
import com.zhxh.base.network.KeyValueData;
import com.zhxh.base.network.RequestCommand;
import com.zhxh.base.network.RxHttp;
import com.zhxh.base.utils.CommonUtils;
import com.zhxh.base.utils.NetworkUtils;
import com.zhxh.base.utils.UIStatusBarHelper;
import com.zhxh.xlibkit.parser.GsonParser;
import com.zhxh.xmoduleproj.MyApplication;
import com.zhxh.xmoduleproj.R;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * Created by zhxh on 2018/11/12
 */
public class BullBaoRecordFragment extends BaseLazyFragment implements OnRefreshListener {

    @BindView(R.id.mainTitleLayout)
    View mainTitleLayout;
    @BindView(R.id.titleBackBtn)
    View titleBackBtn;
    @BindView(R.id.titleName)
    TextView titleName;


    @BindView(R.id.dataListView)
    RecyclerView dataListView;

    private ListAdapter listAdapter;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private int listType;
    private int minboundaryid;
    List<BullBaoItemData> dataList = new ArrayList<>();

    View emptyView;

    int pageSize = 10;


    public static BullBaoRecordFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt("listType", listType);
        BullBaoRecordFragment fragment = new BullBaoRecordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.common_activities;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onFirstVisible() {
        super.onFirstVisible();

        UIStatusBarHelper.translucent(getActivity());

        if (MyApplication.SKIN_MODE == MyApplication.SKIN_MODE_NIGHT) {
            UIStatusBarHelper.setStatusBarDarkMode(getActivity());
        } else {
            UIStatusBarHelper.setStatusBarLightMode(getActivity());
        }

        UIStatusBarHelper.setStatusBarPaddingAndHeight(mainTitleLayout, getContext());

        initList();

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshData();
            }
        });
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        emptyView = view.findViewById(R.id.emptyView);


        if (getArguments() != null) {
            listType = getArguments().getInt("listType");
        }

        titleBackBtn.setOnClickListener(v -> Objects.requireNonNull(getActivity()).finish());
        if (BullBaoRecordActivity.TYPE_MULTI == listType) {
            titleName.setText("牛宝记录");
        } else if (BullBaoRecordActivity.TYPE_CONVERT == listType) {
            titleName.setText("兑换记录");
        } else {
            titleName.setText("牛宝记录");
        }

    }

    @Override
    public void onFragmentResume(boolean firstResume) {
        super.onFragmentResume(firstResume);
        if (firstResume) {
            loadData();
        } else {
            if (CommonUtils.isNull(listAdapter.getData())) {
                loadData();
            }
        }
    }

    private void loadData() {
        if (!NetworkUtils.isConnected(getActivity())) {
            refreshData();
        } else {
            refreshData();
        }
    }

    private void initList() {

        listAdapter = new ListAdapter();
        listAdapter.setEnableLoadMore(true);
        listAdapter.setLoadMoreView(new ProgressDrawableLoadingView());
        listAdapter.setOnLoadMoreListener(this::loadMoreData, dataListView);
        listAdapter.disableLoadMoreIfNotFullPage();
        listAdapter.setHeaderAndEmpty(false);

        TextView header = new TextView(getActivity());
        header.setBackgroundColor(Color.WHITE);
        header.setHeight(CommonDataManager.getDensityValue(20, getActivity()));
        header.setWidth(CommonDataManager.screenWight);
        listAdapter.addHeaderView(header);

        dataListView.setFocusableInTouchMode(false);
        dataListView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataListView.setAdapter(listAdapter);
    }


    public void refreshData() {
        minboundaryid = 0;
        loadNetData();
    }

    public void loadMoreData() {
        loadNetData();
    }

    private void loadNetData() {
        if (listType == BullBaoRecordActivity.TYPE_CONVERT) { //兑换记录
            requestConvertRecords();
        } else {
            requestAllRecords();
        }
    }

    private void requestConvertRecords() {
        List<KeyValueData> params = new ArrayList<>();
        params.add(new KeyValueData("userToken", com.niuguwang.stock.data.manager.UserManager.userToken()));
        params.add(new KeyValueData("size", pageSize));
        params.add(new KeyValueData("BoundaryId", minboundaryid));

        mDisposables.add(RxHttp.call(RequestCommand.COMMAND_BULLBAO_GIFTRECORDS, params, BullBaoListResponse.class, data -> {

            if (minboundaryid == 0) {
                refreshLayout.finishRefresh(true);

                if (!CommonUtils.isNull(data.getData()) && null != data.getPagination()) {
                    this.minboundaryid = data.getPagination().getMinboundaryid();

                    dataList = data.getData();
                    listAdapter.setNewData(data.getData());
                } else {
                    dataList = new ArrayList<>();
                    listAdapter.loadMoreComplete();
                    listAdapter.setNewData(dataList);

                    refreshLayout.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }

            } else {
                if (!CommonUtils.isNull(data.getData()) && null != data.getPagination()) {
                    this.minboundaryid = data.getPagination().getMinboundaryid();

                    dataList.addAll(data.getData());
                    listAdapter.loadMoreComplete();
                    //listAdapter.addData(data.getData());

                } else {
                    listAdapter.loadMoreEnd(true);
                }
            }

        }));
    }

    private void requestAllRecords() {
        List<KeyValueData> params = new ArrayList<>();
        params.add(new KeyValueData("userToken", com.niuguwang.stock.data.manager.UserManager.userToken()));
        params.add(new KeyValueData("size", pageSize));
        params.add(new KeyValueData("BoundaryId", minboundaryid));

        mDisposables.add(RxHttp.call(RequestCommand.COMMAND_BULLBAO_SCORE_RECORD, params, BullBaoListResponse.class, data -> {

            if (minboundaryid == 0) {
                refreshLayout.finishRefresh(true);

                if (!CommonUtils.isNull(data.getData()) && null != data.getPagination()) {
                    this.minboundaryid = data.getPagination().getMinboundaryid();

                    dataList = data.getData();
                    listAdapter.setNewData(data.getData());
                } else {
                    dataList = new ArrayList<>();
                    listAdapter.loadMoreComplete();
                    listAdapter.setNewData(dataList);

                    refreshLayout.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }

            } else {
                if (!CommonUtils.isNull(data.getData()) && null != data.getPagination()) {
                    this.minboundaryid = data.getPagination().getMinboundaryid();

                    dataList.addAll(data.getData());
                    listAdapter.loadMoreComplete();
                    //listAdapter.addData(data.getData());

                } else {
                    listAdapter.loadMoreEnd(true);
                }
            }

        }));
    }


    /**
     * 结束页面刷新
     *
     * @param success 是否刷新成功
     */
    private void finishRefresh(boolean success) {
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.finishRefresh(success);
        }
    }

    /**
     * 结束页面刷加载
     */
    private void finishLoadMore(boolean success) {
        if (listAdapter != null && listAdapter.isLoading()) {
            if (success) {
                listAdapter.loadMoreComplete();
            } else {
                listAdapter.loadMoreFail();
            }
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        refreshData();
    }

    private class ListAdapter extends BaseMultiItemQuickAdapter<BullBaoItemData, BaseViewHolder> {

        public ListAdapter() {
            super(dataList);
            addItemType(BullBaoItemData._RECORD_TYPE_SIGN, R.layout.item_bull_bao_record_sign);
            addItemType(BullBaoItemData._RECORD_TYPE_GUESS, R.layout.item_bull_bao_record_guess);
            addItemType(BullBaoItemData._RECORD_TYPE_GUESS_BET, R.layout.item_bull_bao_record_guess);
            addItemType(BullBaoItemData._RECORD_TYPE_HUANYING, R.layout.item_bull_bao_record_guess);
            addItemType(BullBaoItemData._RECORD_TYPE_CONVERT, R.layout.item_bull_bao_record_convert);
            addItemType(BullBaoItemData._RECORD_TYPE_TRADE_VIRTUAL, R.layout.item_bull_bao_record_trade);
            addItemType(BullBaoItemData._RECORD_TYPE_TRADE_REAL, R.layout.item_bull_bao_record_trade);
            addItemType(BullBaoItemData._RECORD_TYPE_ADD, R.layout.item_bull_bao_record_add);
        }

        @Override
        protected int getDefItemViewType(int position) {
            if (listType == BullBaoRecordActivity.TYPE_CONVERT) {
                return BullBaoItemData._RECORD_TYPE_CONVERT;
            }
            return getItemType(getData().get(position).getSubtype(), getData().get(position).getType());
        }

        @Override
        protected void convert(BaseViewHolder helper, BullBaoItemData item) {

            TextView tv_title = helper.getView(R.id.tv_title);
            TextView tv_time = helper.getView(R.id.tv_time);
            TextView tv_right_num = helper.getView(R.id.tv_right_num);
            ImageView iv_flag = helper.getView(R.id.iv_flag);

            TextView tv_content = helper.getView(R.id.tv_content);

            tv_title.setText(item.getTitle());
            tv_time.setText(item.getAdddatetime());
            tv_right_num.setText(ImageUtil.getMarkValue(item.getScore()));

            if (BullBaoItemData._RECORD_TYPE_SIGN == helper.getItemViewType()) {//签到
                tv_content.setVisibility(View.VISIBLE);
                tv_content.setText(item.getContent());

                iv_flag.setImageResource(R.drawable.niubao_signin);

            } else if (BullBaoItemData._RECORD_TYPE_GUESS == helper.getItemViewType()
                    || BullBaoItemData._RECORD_TYPE_GUESS_BET == helper.getItemViewType()) {//猜涨跌

                if (BullBaoItemData._RECORD_TYPE_GUESS == helper.getItemViewType()) {
                    tv_content.setVisibility(View.VISIBLE);
                    tv_content.setText(GsonParser.parseGsonValue("Remark", item.getContent()));
                } else {
                    tv_content.setVisibility(View.GONE);
                }

                iv_flag.setImageResource(R.drawable.niubao_caizhangdie);

                TextView tv_card_content = helper.getView(R.id.tv_card_content);
                XButton xbtnGuess = helper.getView(R.id.xbtnGuess);

                tv_card_content.setText(GsonParser.parseGsonValue("QuizLead", item.getContent()));
                xbtnGuess.setText("去竞猜");

                xbtnGuess.setOnClickListener(v -> {
                    QuantManager.goCommonWeb("猜涨跌", GsonParser.parseGsonValue("Url", item.getContent()));
                });
            } else if (BullBaoItemData._RECORD_TYPE_HUANYING == helper.getItemViewType()) {//寰盈证券
                tv_content.setVisibility(View.GONE);
                iv_flag.setImageResource(R.drawable.niubao_zhuxian);

                TextView tv_card_content = helper.getView(R.id.tv_card_content);
                XButton xbtnGuess = helper.getView(R.id.xbtnGuess);

                tv_card_content.setText(GsonParser.parseGsonValue("Remark", item.getContent()));
                xbtnGuess.setText(GsonParser.parseGsonValue("Button", item.getContent()));

                xbtnGuess.setOnClickListener(v -> {
                    String Type = GsonParser.parseGsonNumber("Type", item.getContent());
                    String AssignId = GsonParser.parseGsonNumber("AssignId", item.getContent());
                    String Url = GsonParser.parseGsonValue("Url", item.getContent());

                    if ("20".equals(AssignId)) {
                        if ("-1".equals(Type)) {
                            QuantManager.goCommonWeb(Url);
                        } else {
                            QuantManager.gonNewMainActivity(AssignId);
                        }
                    } else if ("22".equals(AssignId)) {
                        //TODO 港股交易
                        QuantManager.gonNewMainActivity(AssignId);
                    } else if ("23".equals(AssignId)) {
                        //TODO 美股交易
                        QuantManager.gonNewMainActivity(AssignId);
                    } else if ("24".equals(AssignId)) {
                        //港股打新
                        QuantManager.goCommonWeb(Url);
                    } else {
                        if ("-1".equals(Type)) {
                            QuantManager.goCommonWeb(Url);
                        } else {
                            QuantManager.gonNewMainActivity(AssignId);
                        }
                    }
                });
            } else if (BullBaoItemData._RECORD_TYPE_CONVERT == helper.getItemViewType()) {//兑好礼
                tv_content.setVisibility(View.VISIBLE);
                tv_content.setText(GsonParser.parseGsonValue("Remark", item.getContent()));

                iv_flag.setImageResource(R.drawable.niubao_duihaoli);

                TextView tv_card = helper.getView(R.id.tv_card);
                ImageView iv_banner = helper.getView(R.id.iv_banner);
                TextView tv_action = helper.getView(R.id.tv_action);
                TextView tv_expire_time = helper.getView(R.id.tv_expire_time);


                String cardType = GsonParser.parseGsonNumber("CardType", item.getContent());
                //cardType=0,免佣卡，cardType=1,行情卡，cardType=2，优惠券
                if ("2".equals(cardType)) {
                    iv_banner.setImageResource(R.drawable.niubao_youhuiquan_bg);
                } else if ("1".equals(cardType)) {
                    iv_banner.setImageResource(R.drawable.niubao_hangqingka_bg);
                } else if ("0".equals(cardType)) {
                    iv_banner.setImageResource(R.drawable.niubao_mianyongka_bg);
                }

                String worth = GsonParser.parseGsonValue("Worth", item.getContent());
                String cardName = GsonParser.parseGsonValue("CardName", item.getContent());
                tv_card.setText(ImageUtil.getSizeSpanStr(worth + "\n" + cardName, worth, 20));

                tv_action.setText(GsonParser.parseGsonValue("ValidText", item.getContent()));

                tv_expire_time.setText(MessageFormat.format("{0}\n{1}",
                        GsonParser.parseGsonValue("TimePrefix", item.getContent()),
                        GsonParser.parseGsonValue("Time", item.getContent())));

            } else if (BullBaoItemData._RECORD_TYPE_TRADE_REAL == helper.getItemViewType() || BullBaoItemData._RECORD_TYPE_TRADE_VIRTUAL == helper.getItemViewType()) {//交易-实盘模拟盘
                tv_content.setVisibility(View.GONE);

                if (BullBaoItemData._RECORD_TYPE_TRADE_REAL == helper.getItemViewType()) {
                    iv_flag.setImageResource(R.drawable.niubao_shipan);
                } else {
                    iv_flag.setImageResource(R.drawable.niubao_moni);
                }

                TextView tv_action = helper.getView(R.id.tv_action);
                TextView tv_stock = helper.getView(R.id.tv_stock);
                TextView tv_bottom = helper.getView(R.id.tv_bottom);

                //type = 1，买入，type = 2 ，卖出
                String type = GsonParser.parseGsonValue("type", item.getContent());
                if ("1".equals(type)) {
                    tv_action.setText("买入");
                } else if ("2".equals(type)) {
                    tv_action.setText("卖出");
                }

                tv_stock.setText(MessageFormat.format("{0}({1})",
                        GsonParser.parseGsonValue("stockname", item.getContent()),
                        GsonParser.parseGsonValue("stockcode", item.getContent())));

                String dot = " · ";

                String unitprice = GsonParser.parseGsonValue("unitprice", item.getContent());
                String position = GsonParser.parseGsonValue("position", item.getContent());
                String profit = GsonParser.parseGsonValue("profit", item.getContent());
                String strBtm;
                if (CommonUtils.isNull(profit)) {
                    strBtm = "成交价 " + unitprice + dot + "仓位占比 " + position;
                } else {
                    if (CommonDataManager.screenDensity < 2.5) {
                        //小屏手机
                        tv_bottom.setTextSize(12);
                    } else {
                        tv_bottom.setTextSize(13);
                    }
                    strBtm = "成交价 " + unitprice + dot + "仓位占比 " + position + dot + "获利 " + profit;
                }

                tv_bottom.setText(strBtm);

                tv_stock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else if (BullBaoItemData._RECORD_TYPE_ADD == helper.getItemViewType()) { //自选股
                tv_content.setVisibility(View.GONE);
                iv_flag.setImageResource(R.drawable.niubao_add);

                TextView tv_stock = helper.getView(R.id.tv_stock);
                TextView tv_num1 = helper.getView(R.id.tv_num1);
                TextView tv_num2 = helper.getView(R.id.tv_num2);
                TextView tv_num3 = helper.getView(R.id.tv_num3);

                tv_stock.setText(GsonParser.parseGsonValue("StockName", item.getContent()));
                tv_num1.setText(GsonParser.parseGsonValue("NowPrice", item.getContent()));
                tv_num2.setText(GsonParser.parseGsonValue("UpDown", item.getContent()));
                tv_num3.setText(GsonParser.parseGsonValue("UpDownRate", item.getContent()));

                tv_num2.setTextColor(ImageUtil.getChangeColor(GsonParser.parseGsonValue("UpDownRate", item.getContent())));
                tv_num3.setTextColor(ImageUtil.getChangeColor(GsonParser.parseGsonValue("UpDownRate", item.getContent())));
            }

        }
    }

    @Override
    public void updateViewData(int requestID, String resultStr, String tag) {

    }

    /// SubType=0,主线任务，SubType=1自选股，SubType=2模拟交易，SubType=3猜涨跌下注奖励，SubType=4，实盘交易
    private int getItemType(int subtype, int type) {

        if (0 == subtype) {//主线任务
            if (2 == type) {
                return BullBaoItemData._RECORD_TYPE_SIGN;
            } else if (3 == type) {
                return BullBaoItemData._RECORD_TYPE_CONVERT;
            } else if (4 == type) {
                return BullBaoItemData._RECORD_TYPE_HUANYING;
            } else if (5 == type) {
                return BullBaoItemData._RECORD_TYPE_GUESS;
            }
        } else if (1 == subtype) {
            return BullBaoItemData._RECORD_TYPE_ADD;
        } else if (2 == subtype) {
            return BullBaoItemData._RECORD_TYPE_TRADE_VIRTUAL;
        } else if (3 == subtype) {
            return BullBaoItemData._RECORD_TYPE_GUESS_BET;
        } else if (4 == subtype) {
            return BullBaoItemData._RECORD_TYPE_TRADE_REAL;
        }
        return -1;
    }
}
