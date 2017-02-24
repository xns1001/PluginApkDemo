/*
 * ACDD Project
 * file ContextImplHook.java  is  part of ACCD
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
package org.acdd.runtime;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import org.acdd.framework.ACDD;
import org.acdd.framework.ACDDConfig;
import org.acdd.framework.BundleImpl;
import org.acdd.framework.Framework;
import org.acdd.log.Logger;
import org.acdd.log.LoggerFactory;
import org.acdd.runtime.stub.BundlePackageManager;
import org.acdd.util.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/***
 * A ContextImplHook that allows you to modify the theme from what is in the
 * wrapped context.
 */
public class ContextImplHook extends ContextWrapper {
    static final Logger log;
    private ClassLoader classLoader;

    static {
        log = LoggerFactory.getInstance("ContextImplHook");
    }

    public ContextImplHook(Context context, ClassLoader classLoader) {
        super(context);
        this.classLoader = null;
        this.classLoader = classLoader;
    }
    /** Return a Resources instance for your application's package. */
    @Override
    public Resources getResources() {
        return RuntimeVariables.delegateResources;
    }
    /** Return an AssetManager instance for your application's package. */
    @Override
    public AssetManager getAssets() {
        return RuntimeVariables.delegateResources.getAssets();
    }
    /** Return PackageManager instance to find global package information. */
    @Override
    public PackageManager getPackageManager() {
        return getApplicationContext().getPackageManager();
    }
    /**
     * Return a class loader you can use to retrieve classes in this package.
     */
    @Override
    public ClassLoader getClassLoader() {
        if (this.classLoader != null) {
            return this.classLoader;
        }
        return super.getClassLoader();
    }

    @Override
    public void startActivity(Intent intent) {
        if (ACDDConfig.stubModeEnable) {
          //  BundlePackageManager.modifyStubActivity(intent);
            if (BundlePackageManager.isNeedCheck(intent)) {
                for (Bundle bundle : ACDD.getInstance().getBundles()) {
                    if (((BundleImpl) bundle).isUpdated() && ((BundleImpl) bundle).getPackageManager().wrapperActivityIntentIfNeed(intent) != null) {
                        break;
                    }
                }
            }
        }

        String packageName;
        String mComponentName = null;
        if (intent.getComponent() != null) {
            packageName = intent.getComponent().getPackageName();
            mComponentName = intent.getComponent().getClassName();
        } else {
            ResolveInfo resolveActivity = getBaseContext().getPackageManager().resolveActivity(intent, 0);
            if (resolveActivity == null || resolveActivity.activityInfo == null) {
                packageName = null;
            } else {
                packageName = resolveActivity.activityInfo.packageName;
                mComponentName = resolveActivity.activityInfo.name;
            }
        }
        ClassLoadFromBundle.checkInstallBundleIfNeed(mComponentName);
        if (!StringUtils.equals(getBaseContext().getPackageName(), packageName)) {
            super.startActivity(intent);
        } else if (DelegateComponent.locateComponent(mComponentName) != null) {
            super.startActivity(intent);
        } else {
            try {
                if (Framework.getSystemClassLoader().loadClass(mComponentName) != null) {
                    super.startActivity(intent);
                }
            } catch (ClassNotFoundException e) {
                log.error("Can't find class " + mComponentName);
                if (Framework.getClassNotFoundCallback() != null) {
                    if (intent.getComponent() == null && !TextUtils.isEmpty(mComponentName)) {
                        intent.setClassName(this, mComponentName);
                    }
                    if (intent.getComponent() != null) {
                        Framework.getClassNotFoundCallback().returnIntent(intent);
                    }
                }
            }
        }
    }

    /**
     * Connect to an application service, creating it if needed.  This defines
     * a dependency between your application and the service.  The given
     * <var>conn</var> will receive the service object when it is created and be
     * told if it dies and restarts.  The service will be considered required
     * by the system only for as long as the calling context exists.  For
     * example, if this Context is an Activity that is stopped, the service will
     * not be required to continue running until the Activity is resumed.
     */
    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        String packageName;
        String mComponentName = null;
        if (service.getComponent() != null) {
            packageName = service.getComponent().getPackageName();
            mComponentName = service.getComponent().getClassName();
        } else {
            ResolveInfo resolveService = getBaseContext().getPackageManager().resolveService(service, 0);
            if (resolveService == null || resolveService.serviceInfo == null) {
                packageName = null;
            } else {
                packageName = resolveService.serviceInfo.packageName;
                mComponentName = resolveService.serviceInfo.name;
            }
        }
        if (!StringUtils.equals(getBaseContext().getPackageName(), packageName)) {
            return super.bindService(service, conn, flags);
        }
        ClassLoadFromBundle.checkInstallBundleIfNeed(mComponentName);
        packageName = DelegateComponent.locateComponent(mComponentName);
        if (packageName != null) {
            BundleImpl bundleImpl = (BundleImpl) Framework.getBundle(packageName);
            if (bundleImpl != null) {
                try {
                    bundleImpl.startBundle();
                } catch (BundleException e) {
                    log.error(e.getMessage() + " Caused by: ", e.getNestedException());
                }
            }
            return super.bindService(service, conn, flags);
        }
        try {
            if (Framework.getSystemClassLoader().loadClass(mComponentName) != null) {
                return super.bindService(service, conn, flags);
            }
        } catch (ClassNotFoundException e) {
            log.error("Can't find class " + mComponentName);
        }
        return false;
    }


    @Override
    public ComponentName startService(Intent service) {
        String packageName;
        String mComponentName;
        if (service.getComponent() != null) {
            packageName = service.getComponent().getPackageName();
            mComponentName = service.getComponent().getClassName();
        } else {
            ResolveInfo resolveService = getBaseContext().getPackageManager().resolveService(service, 0);
            if (resolveService == null || resolveService.serviceInfo == null) {
                mComponentName = null;
                packageName = null;
            } else {
                packageName = resolveService.serviceInfo.packageName;
                mComponentName = resolveService.serviceInfo.name;
            }
        }
        if (!StringUtils.equals(getBaseContext().getPackageName(), packageName)) {
            return super.startService(service);
        }
        ClassLoadFromBundle.checkInstallBundleIfNeed(mComponentName);
        packageName = DelegateComponent.locateComponent(mComponentName);
        if (packageName != null) {
            BundleImpl bundleImpl = (BundleImpl) Framework.getBundle(packageName);
            if (bundleImpl != null) {
                try {
                    bundleImpl.startBundle();
                } catch (BundleException e) {
                    log.error(e.getMessage() + " Caused by: ", e.getNestedException());
                }
            }
            return super.startService(service);
        }
        try {
            if (Framework.getSystemClassLoader().loadClass(mComponentName) != null) {
                return super.startService(service);
            }
            return null;
        } catch (ClassNotFoundException e) {
            log.error("Can't find class " + mComponentName);
            return null;
        }
    }
    @Override
    public void sendBroadcast(Intent intent) {
        if (ACDDConfig.stubModeEnable) {

            if (BundlePackageManager.isNeedCheckReceiver(intent)) {
                for (Bundle bundle : ACDD.getInstance().getBundles()) {
                    if (((BundleImpl) bundle).isUpdated() && ((BundleImpl) bundle).getPackageManager().wrapperReceiverIntentIfNeed(intent) != null) {
                        break;
                    }
                }
            }
        }

        super.sendBroadcast(intent);
    }

}
