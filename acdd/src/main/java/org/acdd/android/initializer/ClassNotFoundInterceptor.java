/*
 * ACDD Project
 * file ClassNotFoundInterceptor.java  is  part of ACCD
 * The MIT License (MIT)  Copyright (c) 2015 Bunny Blue,achellies.
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */
package org.acdd.android.initializer;

import android.content.Intent;

import org.acdd.bundleInfo.BundleInfoList;
import org.acdd.framework.InternalConstant;
import org.acdd.runtime.ClassNotFoundInterceptorCallback;
import org.acdd.runtime.Globals;

import java.util.ArrayList;
import java.util.List;

public class ClassNotFoundInterceptor implements ClassNotFoundInterceptorCallback {
    public static final List<String> GO_H5_BUNDLES_IF_NOT_EXISTS;
    public final String TAG;

    public ClassNotFoundInterceptor() {
        this.TAG = "ClassNotFundInterceptor";
    }

    static {
        GO_H5_BUNDLES_IF_NOT_EXISTS = new ArrayList<String>();
    }

    public static void addGoH5BundlesIfNotExists(String str) {
        if (!GO_H5_BUNDLES_IF_NOT_EXISTS.contains(str)) {
            GO_H5_BUNDLES_IF_NOT_EXISTS.add(str);
        }
    }

    public static void resetGoH5BundlesIfNotExists() {
        GO_H5_BUNDLES_IF_NOT_EXISTS.clear();
    }

    @SuppressWarnings("unused")
    @Override
    public Intent returnIntent(Intent intent) {
        Object obj = 1;
        Object obj2 = null;
        String className = intent.getComponent().getClassName();
        CharSequence dataString = intent.getDataString();
        if (className == null || !className.equals(InternalConstant.BOOT_ACTIVITY)) {
            String bundleForComponet = BundleInfoList.getInstance().getBundleNameForComponet(className);
            //   ACDD.getInstance().getBundle(intent.get)
//            if (mOptDexProcess.sInternalBundles == null) {
//                mOptDexProcess.instance().resolveInternalBundles();
//            }
//            if (mOptDexProcess.sInternalBundles != null) {
//                if (mOptDexProcess.sInternalBundles.contains(bundleForComponet) || ACDD.getInstance().getBundle(bundleForComponet) != null) {
//                    obj = null;
//                }
//                obj2 = obj;
//            } else if (Globals.isMiniPackage() || bundleForComponet.equalsIgnoreCase("com.duanqu.qupai.recorder")) {
//                obj2 = 1;
//            }
            if (obj2 != null) {
//                Component findBundleByActivity =Component();// mOptDexProcess.instance().findBundleByActivity(className);
//                if (!(findBundleByActivity == null || ACDD.getInstance().getBundle(findBundleByActivity.getPkgName()) != null || GO_H5_BUNDLES_IF_NOT_EXISTS.contains(findBundleByActivity.getPkgName()))) {
//                    new Handler(Looper.getMainLooper()).post(new BootRunnable(this, intent, className, findBundleByActivity));
//                }
            }
//            if (!TextUtils.isEmpty(dataString)) {
//                Nav.from(Globals.getApplication()).withCategory(BundleDebug.BROWSER_ONLY_CATEGORY).withExtras(intent.getExtras()).toUri(intent.getData());
//            }
        }
        return intent;
    }

    public static final String KEY_ACTIVITY = "lightapk_activity";
    public static final String KEY_BUNDLE_PKG = "lightapk_pkg";

    class BootRunnable implements Runnable {
        final Intent mIntent;
        final String mActivityName;
        final BundleInfoList.BundleInfo mComponent;
        final ClassNotFoundInterceptor mClassNotFoundInterceptor;

        BootRunnable(ClassNotFoundInterceptor mClassNotFoundInterceptor, Intent mIntent, String mActivityName, BundleInfoList.BundleInfo mComponent) {
            this.mClassNotFoundInterceptor = mClassNotFoundInterceptor;
            this.mIntent = mIntent;
            this.mActivityName = mActivityName;
            this.mComponent = mComponent;
        }

        @Override
        public void run() {
            Intent intent = new Intent();
            if (this.mIntent.getExtras() != null) {
                intent.putExtras(this.mIntent.getExtras());
            }
            intent.putExtra(KEY_ACTIVITY, this.mActivityName);
            intent.putExtra(KEY_BUNDLE_PKG, this.mComponent.bundleName);
            intent.setData(this.mIntent.getData());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.setClass(Globals.getApplication(), InternalConstant.BundleNotFoundActivity);
            Globals.getApplication().startActivity(intent);
        }
    }
}