package com.xns.module.a;

import android.os.Bundle;
import android.widget.ImageView;

import com.xns.base.BaseActivity;

public class AActivity extends BaseActivity {

    public static final int libbase_man=0x7f020053;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iv4 = (ImageView) findViewById(R.id.iv_4);
        iv4.setBackgroundResource(libbase_man);
    }
}
