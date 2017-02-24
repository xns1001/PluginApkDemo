package com.xns.pluginapkdemo;

import android.content.Context;

import org.acdd.android.compat.ACDDApp;
import org.acdd.framework.ACDDConfig;
import org.acdd.framework.InternalConstant;

/**
 * Created by xiongningsheng on 2017/2/23.
 */

public class BootApplication extends ACDDApp {
    private static final String TAG = "BootApplication";
    static {
        ACDDConfig.AUTO = new String[]{"com.xns.module.a"};
//        ACDDConfig.AUTO = new String[]{"me.xms.module_b"};
//        ACDDConfig.STORE = new String[]{"com.acdd.android.appcenter"};
        ACDDConfig.stubModeEnable = true;
    }

    @Override
    protected void attachedBaseContext(final Context base) {

    }


    @Override
    public void onCreate() {

        super.onCreate();

        InternalConstant.BundleNotFoundActivity=BundleNotFoundActivity.class;
    }

}
