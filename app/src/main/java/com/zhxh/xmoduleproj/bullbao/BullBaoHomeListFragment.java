package com.zhxh.xmoduleproj.bullbao;

import android.os.Bundle;
import android.os.UserManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhxh.base.app.BaseLazyFragment;
import com.zhxh.base.network.KeyValueData;
import com.zhxh.base.network.RequestCommand;
import com.zhxh.base.network.RxHttp;
import com.zhxh.base.utils.CommonUtils;
import com.zhxh.xcomponentlib.ximageview.RatioImageView;
import com.zhxh.xlibkit.parser.GsonParser;
import com.zhxh.xlibkit.rxbus.RxBus;
import com.zhxh.xmoduleproj.R;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zhxh on 2018/11/12
 */
public class BullBaoHomeListFragment extends BaseLazyFragment implements OnRefreshListener {

    //type默认=0，赚牛宝，type=1兑好礼
    public static final int bull_home_task = 0;
    public static final int bull_home_gift = 1;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private RefreshLayout refreshLayout;

    private ListAdapter listAdapter;
    private List<BullBaoItemData> resultList = new ArrayList<>();


    private int listType;

    public static BullBaoHomeListFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt("listType", listType);
        BullBaoHomeListFragment fragment = new BullBaoHomeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.common_list_fragment;
    }


    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
        refreshData();
    }

    @Override
    public void onFirstVisible() {
        super.onFirstVisible();
        initList();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        refreshData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        if (getArguments() != null) {
            listType = getArguments().getInt("listType");
        }

    }

    private void initList() {
        listAdapter = new ListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(baseActivity));
        recyclerView.setAdapter(listAdapter);
        listAdapter.setOnLoadMoreListener(() -> {
            //refreshData();
        }, recyclerView);
    }

    public void refreshData() {
        List<KeyValueData> params = new ArrayList<>();
        params.add(new KeyValueData("userToken", UserManager.userToken()));
        params.add(new KeyValueData("type", listType));

        mDisposables.add(RxHttp.call(RequestCommand.COMMAND_BULLBAO_NIUBAOLIST, params, resultStr -> {

            BullBaoListResponse response = GsonParser.parse(resultStr, BullBaoListResponse.class);

            List<BullBaoItemData> treeList = response.getData();
            if (null == treeList || treeList.size() == 0) {
                listAdapter.loadMoreComplete();
            } else {
                if (CommonUtils.isNull(resultList)) {
                    if (refreshLayout != null) {
                        refreshLayout.finishRefresh(true);
                    }
                    listAdapter.loadMoreComplete();
                } else {
                    listAdapter.loadMoreComplete();
                }

                if (listType == bull_home_gift) {
                    setGiftData(treeList);
                } else {
                    setListData(treeList);
                }
            }

            listAdapter.loadMoreEnd(true);

        }));
    }

    /**
     * 重新分组 组成一个list
     *
     * @param treeList
     */
    private void setListData(List<BullBaoItemData> treeList) {
        resultList.clear();
        for (int i = 0; i < treeList.size(); i++) {
            BullBaoItemData outerItem = treeList.get(i);
            List<BullBaoItemData> tempList = new ArrayList<>();
            for (int j = 0; j < outerItem.getTemplates().size(); j++) {
                BullBaoItemData innerItem = outerItem.getTemplates().get(j);
                innerItem.setStatus(outerItem.getStatus());
                if (j == 0) {
                    innerItem.setTreeTitle(outerItem.getTaskname());
                } else {
                    innerItem.setTreeTitle("");
                }
                tempList.add(innerItem);
            }
            resultList.addAll(tempList);
        }
        listAdapter.notifyDataSetChanged();
    }

    /**
     * @param treeList
     */
    private void setGiftData(List<BullBaoItemData> treeList) {
        resultList.clear();
        resultList.addAll(treeList);
        listAdapter.notifyDataSetChanged();
    }


    private class ListAdapter extends BaseMultiItemQuickAdapter<BullBaoItemData, BaseViewHolder> {

        public ListAdapter() {
            super(resultList);
            addItemType(bull_home_task, R.layout.item_bull_bao_home_task);
            addItemType(bull_home_gift, R.layout.item_bull_bao_home_gift);
        }

        @Override
        protected int getDefItemViewType(int position) {
            return listType;
        }

        @Override
        protected void convert(BaseViewHolder helper, BullBaoItemData item) {

            int pos = helper.getLayoutPosition();

            if (bull_home_task == helper.getItemViewType()) {

                TextView tv_list_title = helper.getView(R.id.tv_list_title);
                TextView tv_gold_num = helper.getView(R.id.tv_gold_num);
                TextView tv_name = helper.getView(R.id.tv_name);
                TextView tv_content = helper.getView(R.id.tv_content);
                XButton xbtnRight = helper.getView(R.id.xbtnRight);

                if (CommonUtils.isNull(item.getTreeTitle())) {
                    tv_list_title.setVisibility(View.GONE);
                } else {
                    tv_list_title.setVisibility(View.VISIBLE);
                    tv_list_title.setText(item.getTreeTitle());
                }
                tv_name.setText(item.getTemplatename().trim());
                tv_content.setText(item.getTemplatedescription());

                if (CommonUtils.isNull(item.getButton())) {
                    xbtnRight.setVisibility(View.GONE);
                } else {
                    xbtnRight.setVisibility(View.VISIBLE);
                    xbtnRight.setText(item.getButton());
                }
                tv_gold_num.setText(MessageFormat.format("+{0}", item.getScore()));


                //userrecordstatus	任务完成状态	int	0 未完成，2领取，3已完成
                if ("10".equals(item.getGroupid()) && "3".equals(item.getUserrecordstatus())) {
                    xbtnRight.setAlpha(0.6f);
                    xbtnRight.setEnabled(false);
                } else {
                    xbtnRight.setAlpha(1f);
                    xbtnRight.setEnabled(true);
                }

                xbtnRight.setOnClickListener(v -> {
                    //groupid=9,主线任务，groupid=10，日常任务 只有 0和2

                    if ("9".equals(item.getGroupid())) {//主线任务-寰盈证券开户
                        if ("2".equals(item.getUserrecordstatus())) {
                            //领取
                            requestSyncSub(item);
                        } else {
                            //去开户 去入金
                            //标识任务的唯一性，主线任务，assignmentid=22，港股交易；
                            //assignmentid=23，美股交易；assignmentid=24，港股打新
                            if ("22".equals(item.getAssignmentid())) {
                                //TODO 港股交易
                                QuantManager.gonNewMainActivity(item.getAssignmentid());

                            } else if ("23".equals(item.getAssignmentid())) {
                                //TODO 美股交易
                                QuantManager.gonNewMainActivity(item.getAssignmentid());

                            } else if ("24".equals(item.getAssignmentid())) {
                                //港股打新
                                QuantManager.goCommonWeb(item.getUrl());
                            } else {
                                if (-1 == item.getType()) {
                                    QuantManager.goCommonWeb(item.getUrl());
                                } else {
                                    QuantManager.gonNewMainActivity(item.getAssignmentid());
                                }
                            }
                        }

                    } else if ("25".equals(item.getAssignmentid())) {//添加自选股

                        if ("0".equals(item.getUserrecordstatus())) {
                            //TODO 去添加
                            QuantManager.gonNewMainActivity(item.getAssignmentid());

                        } else if ("2".equals(item.getUserrecordstatus())) {
                            //领取
                            requestSyncSub(item);
                        } else if ("3".equals(item.getUserrecordstatus())) {
                            //已领取
                        }

                    } else if ("26".equals(item.getAssignmentid())) {//模拟交易

                        if ("0".equals(item.getUserrecordstatus())) {
                            //TODO 去交易
                            QuantManager.gonNewMainActivity(item.getAssignmentid());

                        } else if ("2".equals(item.getUserrecordstatus())) {
                            //领取
                            requestSyncSub(item);
                        } else if ("3".equals(item.getUserrecordstatus())) {
                            //已领取
                        }

                    } else if ("27".equals(item.getAssignmentid())) {//猜涨跌下注

                        if ("0".equals(item.getUserrecordstatus())) {
                            //去竞猜
                            QuantManager.goCommonWeb(item.getUrl());
                        } else if ("2".equals(item.getUserrecordstatus())) {
                            //领取
                            requestSyncSub(item);
                        } else if ("3".equals(item.getUserrecordstatus())) {
                            //已领取
                        }

                    } else if ("28".equals(item.getAssignmentid())) {//寰盈实盘交易

                        if ("0".equals(item.getUserrecordstatus())) {
                            //TODO 去交易
                            QuantManager.gonNewMainActivity(item.getAssignmentid());

                        } else if ("2".equals(item.getUserrecordstatus())) {
                            //领取
                            requestSyncSub(item);
                        } else if ("3".equals(item.getUserrecordstatus())) {
                            //已领取
                        }
                    }


                });

            } else if (bull_home_gift == helper.getItemViewType()) {

                TextView tv_list_title = helper.getView(R.id.tv_list_title);
                RecyclerView cy_recyclerview = helper.getView(R.id.cy_recyclerview);
                View line_bottom = helper.getView(R.id.line_bottom);
                LinearLayout btn_bottom = helper.getView(R.id.btn_bottom);


                if (pos == getData().size() - 1) {
                    line_bottom.setVisibility(View.GONE);
                    btn_bottom.setVisibility(View.VISIBLE);
                } else {
                    line_bottom.setVisibility(View.VISIBLE);
                    btn_bottom.setVisibility(View.GONE);
                }

                tv_list_title.setText(item.getTypedesc());

                HorizonListAdapter adapter = new HorizonListAdapter(item.getTasklist());
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                cy_recyclerview.setLayoutManager(manager);
                cy_recyclerview.setNestedScrollingEnabled(true);
                cy_recyclerview.setAdapter(adapter);
/*                if (cy_recyclerview.getItemDecorationCount() <= 0) {
                    cy_recyclerview.addItemDecoration(new RecyclerView.ItemDecoration() {
                        @Override
                        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                            outRect.right = CommonDataManager.getDensityValue(10, getActivity());
                        }
                    });
                }*/
                ((SimpleItemAnimator) cy_recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);//解决刷新item时候闪烁的问题

                btn_bottom.setOnClickListener(v -> QuantManager.goBullBaoRecord(BullBaoRecordActivity.TYPE_CONVERT));
            }

        }
    }

    private class HorizonListAdapter extends RecyclerView.Adapter {

        private List<BullBaoItemData> dataList;

        HorizonListAdapter(List<BullBaoItemData> dataList) {
            this.dataList = dataList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new HorizonListAdapter.ItemViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_bull_bao_home_inner_card, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            final BullBaoItemData data = dataList.get(position);
            final HorizonListAdapter.ItemViewHolder itemHolder = (HorizonListAdapter.ItemViewHolder) holder;


            //cardType=0,免佣卡，cardType=1,行情卡，cardType=2，优惠券
            if ("2".equals(data.getCardtype())) {
                itemHolder.iv_banner.setImageResource(R.drawable.niubao_youhuiquan_bg);
            } else if ("1".equals(data.getCardtype())) {
                itemHolder.iv_banner.setImageResource(R.drawable.niubao_hangqingka_bg);
            } else if ("0".equals(data.getCardtype())) {
                itemHolder.iv_banner.setImageResource(R.drawable.niubao_mianyongka_bg);
            }

            itemHolder.tv_card.setText(ImageUtil.getSizeSpanStr(data.getWorth() + "\n" + data.getCardname(), data.getWorth(), 20));
            itemHolder.tv_title.setText(data.getTitle());
            itemHolder.tv_content.setText(data.getCost());

            itemHolder.item_layout.setLayoutParams(new ViewGroup.LayoutParams((int) ((CommonDataManager.screenWight - 20 * CommonDataManager.screenDensity) / 2), ViewGroup.LayoutParams.WRAP_CONTENT));

            itemHolder.rootView.setOnClickListener(v -> BullBaoTipsDialog.Companion.newInstance(BullBaoTipsDialog.Companion.getTYPE_GIFT(), data).show(baseActivity.getSupportFragmentManager(), "dialog"));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {

            final View rootView;

            ConstraintLayout item_layout;
            RatioImageView iv_banner;
            TextView tv_card;
            TextView tv_title;
            TextView tv_content;

            public ItemViewHolder(View view) {
                super(view);
                rootView = view;

                item_layout = view.findViewById(R.id.item_layout);
                iv_banner = view.findViewById(R.id.iv_banner);
                tv_card = view.findViewById(R.id.tv_card);
                tv_title = view.findViewById(R.id.tv_title);
                tv_content = view.findViewById(R.id.tv_content);
            }
        }
    }

    private void requestSyncSub(BullBaoItemData item) {
        /*  userToken	用户标识	string	获取用户信息，标识唯一用户
         groupId	任务类型	int	groupId=9,主线任务，groupId=10，日常任务
         assignId	任务关系	int
         assignId =20：寰盈证券开户；assignId=21：寰盈账户首次入金

         assignId =22：寰盈首次港股交易 ；assignId =23：寰盈首次美股交易
         assignId =24：寰盈首次港股打新；assignId =25：添加自选股 ；

         assignId =26：模拟交易 ；assignId =27：猜涨跌下注 ；
         assignId =28：寰盈账户实盘交易

         status	任务状态	int	status = 3*/

        List<KeyValueData> params = new ArrayList<>();
        params.add(new KeyValueData("userToken", com.niuguwang.stock.data.manager.UserManager.userToken()));
        params.add(new KeyValueData("groupId", item.getGroupid()));
        params.add(new KeyValueData("assignId", item.getAssignmentid()));

        RxHttp.call(RequestCommand.COMMAND_BULLBAO_SYNCSUBTASK, params, BullBaoResponse.class, response -> {
            ToastTool.showToast(response.getMessage());
            RxBus.getDefault().post(QuantManager.BULL_BAO_HOME_RECEIVE, "0");
            refreshData();
        });
    }

}
