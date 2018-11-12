package com.zhxh.xmoduleproj.bullbao

import android.os.Bundle
import com.zhxh.base.app.BaseActivity
import com.zhxh.xmoduleproj.R

/**
 * Created by zhxh on 2018/11/12
 */
class BullBaoRecordActivity : BaseActivity() {

    companion object {
        const val TYPE_MULTI: Int = 1 //牛宝记录
        const val TYPE_CONVERT: Int = 2 //兑换记录
    }

    override fun setLayout() {
        setContentView(R.layout.activity_common_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .add(R.id.contentFragment, BullBaoRecordFragment.newInstance(initRequest.type)).addToBackStack(null).commitAllowingStateLoss()
    }

    override fun refreshData() {
    }
}
