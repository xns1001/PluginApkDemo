package com.xns.base;

import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Created by xiongningsheng on 2017/2/23.
 */

public class BaseActivity extends FragmentActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
