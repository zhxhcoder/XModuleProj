package com.zhxh.xmoduleproj.bullbao

import android.os.Bundle
import com.zhxh.base.app.BaseActivity
import com.zhxh.xmoduleproj.R

/**
 * Created by zhxh on 2018/11/12
 */

class BullBaoHomeActivity : BaseActivity() {

    override fun setLayout() {
        setContentView(R.layout.activity_common_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        supportFragmentManager.beginTransaction()
                .add(R.id.contentFragment, BullBaoHomeFragment.newInstance()).addToBackStack(null).commitAllowingStateLoss()
    }

    override fun refreshData() {
    }
}
