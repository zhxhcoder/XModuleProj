package com.zhxh.xmoduleproj.bullbao;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxh on 2018/11/12
 */
public class BullBaoItemData implements MultiItemEntity, Serializable {

    public final static int _RECORD_TYPE_SIGN = 2;//签到
    public final static int _RECORD_TYPE_GUESS = 5;//猜涨跌
    public final static int _RECORD_TYPE_HUANYING = 4;//寰盈
    public final static int _RECORD_TYPE_CONVERT = 3;//兑好礼
    public final static int _RECORD_TYPE_GUESS_BET = 30;//猜涨跌下注奖励
    public final static int _RECORD_TYPE_TRADE_VIRTUAL = 20;//模拟交易
    public final static int _RECORD_TYPE_TRADE_REAL = 40;//实盘交易 depreciation
    public final static int _RECORD_TYPE_ADD = 10;//加入自选股

    //list 数据
    private int index;
    private List<BullBaoItemData> list;
    private String status;
    private String score;

    private boolean todaysign;
    private String signindays;

    //牛宝数据
    private BullBaoItemData niubao;
    private String userid;
    private String username;
    private String userlogourl;
    private String totalscore;
    private String todayscore;
    private String updatedatetime;


    //牛宝记录分页信息
    private BullBaoItemData pagination;
    private String boundaryid;
    private int size;
    private String order;
    private String direction;
    private int minboundaryid;
    private int maxboundaryid;
    private int totaldata;


    //签到 猜涨跌信息
    private String id;
    private int type;
    private int subtype;
    private String title;
    private String content;
    private String adddatetime;

    //签到成功
    private String tomscore;
    private String tip;
    private String url;


    //领牛宝
    private List<BullBaoItemData> templates;
    private String treeTitle;
    private String taskname;
    /**
     * {
     * "templates": [
     * {
     * "assignmentid": 20,
     * "score": 3000,
     * "order": 1,
     * "type": -1,
     * "url": "https://h5.huanyingzq.com/newkaihu/index.html",
     * "button": "去开户",
     * "userrecordstatus": 0,
     * "templateid": 9,
     * "templatename": "寰盈证券开户                                            ",
     * "templatedescription": "开通寰盈证券实股票账户，领取3000牛宝"
     * }
     * ],
     * "totalnumber": 0,
     * "completenumber": 0,
     * "groupid": 9,
     * "taskname": "主线任务",
     * "taskdescription": "寰盈相关主线任务。",
     * "status": 1,
     * "periodtype": 1,
     * "startdatetime": "2018-10-22",
     * "enddatetime": "2018-10-22",
     * "order": 100
     * }
     */
    private String totalnumber;
    private String completenumber;
    private String groupid;
    private String taskdescription;
    private String periodtype;
    private String startdatetime;
    private String enddatetime;
    private String assignmentid;
    private String button;
    private String userrecordstatus;
    private String templateid;
    private String templatename;
    private String templatedescription;

    /**
     * "cardtype": 0,
     * "typedesc": "免佣卡",
     * "tasklist": [
     * {
     * "id": 16,
     * "title": "3天美股免佣",
     * "dialogtitle": "3天美股免佣",
     * "desc": "3天美股免佣",
     * "iconurl": "https://img.huanyingzq.com/active/2018/08/0/NZ_C20AD4D76FE97759AA27A0C99BFF6710.png",
     * "score": 3680,
     * "cost": "3680牛宝",
     * "carddescurl": null,
     * "carddesctext": null,
     * "ruledesc": "16:00前兑换，当日生效，16：00后则次日生效。兑换后3天内使用寰盈账户交易美股免佣金",
     * "button": "3680牛宝兑换",
     * "worth": "3天",
     * "cardname": "美股免佣卡"
     * }
     *
     * @return
     */
    //兑好礼
    private List<BullBaoItemData> tasklist;

    @SerializedName(value = "cardtype", alternate = {"cardType"})
    private String cardtype;
    private String typedesc;
    private String dialogtitle;
    private String desc;
    private String iconurl;
    private String cost;
    private String carddescurl;
    private String carddesctext;
    private String ruledesc;
    private String worth;
    private String cardname;
    private String niubaoscore;//牛宝总数

    public String getNiubaoscore() {
        return niubaoscore;
    }

    public void setNiubaoscore(String niubaoscore) {
        this.niubaoscore = niubaoscore;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public List<BullBaoItemData> getTasklist() {
        return tasklist;
    }

    public void setTasklist(List<BullBaoItemData> tasklist) {
        this.tasklist = tasklist;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getTypedesc() {
        return typedesc;
    }

    public void setTypedesc(String typedesc) {
        this.typedesc = typedesc;
    }

    public String getDialogtitle() {
        return dialogtitle;
    }

    public void setDialogtitle(String dialogtitle) {
        this.dialogtitle = dialogtitle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCarddescurl() {
        return carddescurl;
    }

    public void setCarddescurl(String carddescurl) {
        this.carddescurl = carddescurl;
    }

    public String getCarddesctext() {
        return carddesctext;
    }

    public void setCarddesctext(String carddesctext) {
        this.carddesctext = carddesctext;
    }

    public String getRuledesc() {
        return ruledesc;
    }

    public void setRuledesc(String ruledesc) {
        this.ruledesc = ruledesc;
    }

    public String getWorth() {
        return worth;
    }

    public void setWorth(String worth) {
        this.worth = worth;
    }

    public String getCardname() {
        return cardname;
    }

    public void setCardname(String cardname) {
        this.cardname = cardname;
    }

    public String getAssignmentid() {
        return assignmentid;
    }

    public void setAssignmentid(String assignmentid) {
        this.assignmentid = assignmentid;
    }

    public String getTotalnumber() {
        return totalnumber;
    }

    public void setTotalnumber(String totalnumber) {
        this.totalnumber = totalnumber;
    }

    public String getCompletenumber() {
        return completenumber;
    }

    public void setCompletenumber(String completenumber) {
        this.completenumber = completenumber;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getTaskdescription() {
        return taskdescription;
    }

    public void setTaskdescription(String taskdescription) {
        this.taskdescription = taskdescription;
    }

    public String getPeriodtype() {
        return periodtype;
    }

    public void setPeriodtype(String periodtype) {
        this.periodtype = periodtype;
    }

    public String getStartdatetime() {
        return startdatetime;
    }

    public void setStartdatetime(String startdatetime) {
        this.startdatetime = startdatetime;
    }

    public String getEnddatetime() {
        return enddatetime;
    }

    public void setEnddatetime(String enddatetime) {
        this.enddatetime = enddatetime;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getUserrecordstatus() {
        return userrecordstatus;
    }

    public void setUserrecordstatus(String userrecordstatus) {
        this.userrecordstatus = userrecordstatus;
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getTemplatename() {
        return templatename;
    }

    public void setTemplatename(String templatename) {
        this.templatename = templatename;
    }

    public String getTemplatedescription() {
        return templatedescription;
    }

    public void setTemplatedescription(String templatedescription) {
        this.templatedescription = templatedescription;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getTreeTitle() {
        return treeTitle;
    }

    public void setTreeTitle(String treeTitle) {
        this.treeTitle = treeTitle;
    }

    public List<BullBaoItemData> getTemplates() {
        return templates;
    }

    public void setTemplates(List<BullBaoItemData> templates) {
        this.templates = templates;
    }


    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTomscore() {
        return tomscore;
    }

    public void setTomscore(String tomscore) {
        this.tomscore = tomscore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAdddatetime() {
        return adddatetime;
    }

    public void setAdddatetime(String adddatetime) {
        this.adddatetime = adddatetime;
    }

    public BullBaoItemData getPagination() {
        return pagination;
    }

    public void setPagination(BullBaoItemData pagination) {
        this.pagination = pagination;
    }

    public String getBoundaryid() {
        return boundaryid;
    }

    public void setBoundaryid(String boundaryid) {
        this.boundaryid = boundaryid;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getMinboundaryid() {
        return minboundaryid;
    }

    public void setMinboundaryid(int minboundaryid) {
        this.minboundaryid = minboundaryid;
    }

    public int getMaxboundaryid() {
        return maxboundaryid;
    }

    public void setMaxboundaryid(int maxboundaryid) {
        this.maxboundaryid = maxboundaryid;
    }

    public int getTotaldata() {
        return totaldata;
    }

    public void setTotaldata(int totaldata) {
        this.totaldata = totaldata;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<BullBaoItemData> getList() {
        return list;
    }

    public void setList(List<BullBaoItemData> list) {
        this.list = list;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public boolean isTodaysign() {
        return todaysign;
    }

    public void setTodaysign(boolean todaysign) {
        this.todaysign = todaysign;
    }

    public String getSignindays() {
        return signindays;
    }

    public void setSignindays(String signindays) {
        this.signindays = signindays;
    }

    public BullBaoItemData getNiubao() {
        return niubao;
    }

    public void setNiubao(BullBaoItemData niubao) {
        this.niubao = niubao;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserlogourl() {
        return userlogourl;
    }

    public void setUserlogourl(String userlogourl) {
        this.userlogourl = userlogourl;
    }

    public String getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(String totalscore) {
        this.totalscore = totalscore;
    }

    public String getTodayscore() {
        return todayscore;
    }

    public void setTodayscore(String todayscore) {
        this.todayscore = todayscore;
    }

    public String getUpdatedatetime() {
        return updatedatetime;
    }

    public void setUpdatedatetime(String updatedatetime) {
        this.updatedatetime = updatedatetime;
    }


    @Override
    public int getItemType() {
        return itemType;
    }

    protected int itemType = -1;


}
