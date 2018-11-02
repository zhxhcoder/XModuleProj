package com.zhxh.xmoduleproj;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhxh.base.app.BaseActivity;
import com.zhxh.modulebase.ServiceFactory;

public class FragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        ServiceFactory.getInstance().getAccountService().newUserFragment(this, R.id.layout_fragment, getSupportFragmentManager(), null, "");
    }
}
