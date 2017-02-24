/*
 * ACDD Project
 * file ClassLoadFromBundle.java  is  part of ACCD
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

import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.acdd.bundleInfo.BundleInfoList;
import org.acdd.framework.ACDD;
import org.acdd.framework.ACDDConfig;
import org.acdd.framework.BundleImpl;
import org.acdd.framework.Framework;
import org.acdd.framework.InternalConstant;
import org.acdd.log.ACDDMonitor;
import org.acdd.log.Logger;
import org.acdd.log.LoggerFactory;
import org.osgi.framework.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/***Load class From Bundle**/
public class ClassLoadFromBundle {
    private static final String TAG = "ClassLoadFromBundle";
    private static Hashtable<Integer, String> classNotFoundReason;
    private static int reasonCnt;
    public static List<String> sInternalBundles;
    static ZipFile sZipFile = null;
    static Logger log;

    static {
        classNotFoundReason = new Hashtable<Integer, String>();
        reasonCnt = 0;
        log = LoggerFactory.getInstance("ClassLoadFromBundle");
    }

    public static String getClassNotFoundReason(String className) {
        for (int i = 0; i < classNotFoundReason.size(); i++) {
            if ((classNotFoundReason.get(Integer.valueOf(i)) + "").contains(className + "")) {
                return classNotFoundReason.get(Integer.valueOf(i)) + "";
            }
        }
        return "";
    }

    private static void insertToReasonList(String className, String reason) {
        classNotFoundReason.put(Integer.valueOf(reasonCnt), " Not found class " + className + " because " + reason);
        int i = reasonCnt + 1;
        reasonCnt = i;
        reasonCnt = i % 10;
    }

    public static String getPackageNameFromEntryName(String pkgName) {
        String archive = "lib/" + ACDDConfig.PRELOAD_DIR + "/lib";
        return pkgName.substring(pkgName.indexOf(archive) + archive.length(), pkgName.indexOf(".so")).replace("_", ".");
    }

    public static synchronized void resolveInternalBundles() {
        synchronized (ClassLoadFromBundle.class) {
            if (sInternalBundles == null || sInternalBundles.size() == 0) {
                String libcom = "lib/" + ACDDConfig.PRELOAD_DIR + "/libcom_";
                String libcn = "lib/" + ACDDConfig.PRELOAD_DIR + "/libcn_";
                List<String> arrayList = new ArrayList<String>();
                try {
                    sZipFile = new ZipFile(RuntimeVariables.androidApplication.getApplicationInfo().sourceDir);
                    Enumeration<?> entries = sZipFile.entries();
                    while (entries.hasMoreElements()) {
                        String name = ((ZipEntry) entries.nextElement()).getName();
                        if ((name.startsWith(libcom)||name.startsWith(libcn)) && name.endsWith(".so")) {
                            arrayList.add(getPackageNameFromEntryName(name));
                        }
                    }

                    sInternalBundles = arrayList;
                } catch (Exception e) {
                    Log.e(TAG, "Exception while get bundles in assets or lib", e);
                }
            }
        }
    }

    static Class<?> loadFromInstalledBundles(String componet) throws ClassNotFoundException {
        BundleImpl bundleImpl;
        Class<?> cls = null;
        List<Bundle> bundles = Framework.getBundles();
        if (!(bundles == null || bundles.isEmpty())) {
            for (Bundle bundle : bundles) {
                bundleImpl = (BundleImpl) bundle;
                PackageLite packageLite = DelegateComponent.getPackage(bundleImpl.getLocation());
                if (packageLite != null && packageLite.components.contains(componet)) {
                    bundleImpl.getArchive().optDexFile();
                    ClassLoader classLoader = bundleImpl.getClassLoader();
                    if (classLoader != null) {
                        try {
                            cls = classLoader.loadClass(componet);
                            if (cls != null) {
                                return cls;
                            }
                        } catch (ClassNotFoundException e) {
                            throw new ClassNotFoundException("Can't find class " + componet + " in BundleClassLoader: "
                                    + bundleImpl.getLocation() + " [" + (bundles == null ? 0 : bundles.size()) + "]"
                                    + "classloader is: " + (classLoader == null ? "null" : "not null")
                                    + " packageversion " + getPackageVersion() + " exception:" + e.getMessage());
                        }
                    }
                    StringBuilder append = new StringBuilder().append("Can't find class ").append(componet)
                            .append(" in BundleClassLoader: ").append(bundleImpl.getLocation()).append(" [");
                    throw new ClassNotFoundException(append.append( bundles.size()).append("]")
                            .append(classLoader == null ? "classloader is null" : "classloader not null")
                            .append(" packageversion ").append(getPackageVersion()).toString());
                }
            }
        }
        if (!(bundles == null || bundles.isEmpty())) {

            for (Bundle bundle : Framework.getBundles()) {
                bundleImpl = (BundleImpl) bundle;
                if (bundleImpl.getArchive().isDexOpted()) {

                    ClassLoader classLoader = bundleImpl.getClassLoader();
                    if (classLoader != null) {
                        try {
                            cls = classLoader.loadClass(componet);
                            if (cls != null) {
                                return cls;
                            }
                        } catch (ClassNotFoundException e) {
                        }
                    } else {

                    }

                }
            }

        }
        return cls;
    }

    /****
     * check bundle  install and dependency
     * if not install ,check  bundle file exits  on local  file system, if not install from host app
     *****/
    public static void checkInstallBundleAndDependency(String location) {
        List<String> dependencyForBundle = BundleInfoList.getInstance().getDependencyForBundle(location);
        if (dependencyForBundle != null && dependencyForBundle.size() > 0) {
            for (int i = 0; i < dependencyForBundle.size(); i++) {
                checkInstallBundleAndDependency(dependencyForBundle.get(i));

            }
        }
        if (ACDD.getInstance().getBundle(location) == null) {
            String concat = "lib".concat(location.replace(".", "_")).concat(".so");
            File file = new File(new File(Framework.getProperty(InternalConstant.ACDD_APP_DIRECTORY), "lib"), concat);
            if (file.exists()) {
                try {
                    if (checkAvailableDisk()) {
                        ACDD.getInstance().installBundle(location, file);
                        return;
                    }

                } catch (Throwable e) {
                    log.error("failed to install bundle " + location, e);
                    ACDDMonitor.getInstance().trace(Integer.valueOf(-1), location, "",
                            "failed to install bundle ", e);
                    throw new RuntimeException("ACDD failed to install bundle " + location, e);
                }
                return;
            }
            if (sInternalBundles==null){
                resolveInternalBundles();
            }
            if (sInternalBundles == null || !sInternalBundles.contains(location)) {
                log.error(" can not find the library " + concat + " for bundle" + location);
                ACDDMonitor.getInstance().trace(Integer.valueOf(-1), "" + location, "",
                        "can not find the library " + concat);
            } else {
                installFromApkZip(location, concat);
            }
        }
    }

    /***
     * Install bundle  from host app
     **/
    private static void installFromApkZip(String location, String fileName) {

        try {
            if (checkAvailableDisk()) {
                ACDD.getInstance().installBundle(location,
                        sZipFile.getInputStream(sZipFile.getEntry("lib/"+ ACDDConfig.PRELOAD_DIR+"/" + fileName)));
            }
        } catch (Exception e) {
            log.debug("Failed to install bundle " + fileName + " from APK zipfile ");
            e.printStackTrace();
        }
    }

    public static void checkInstallBundleIfNeed(String componet) {
        synchronized (componet) {
            if (sInternalBundles == null) {
                resolveInternalBundles();
            }
            String location = BundleInfoList.getInstance().getBundleNameForComponet(componet);
            if (TextUtils.isEmpty(location)) {
                Log.e(TAG, "Failed to find the bundle in BundleInfoList for component " + componet);
                insertToReasonList(componet, "not found in BundleInfoList!");
            }
            if (sInternalBundles == null || sInternalBundles.contains(location)) {
                checkInstallBundleAndDependency(location);
                return;
            }
        }
    }

    public static void checkBundle(String location) {
        synchronized (location) {
            if (sInternalBundles == null) {
                resolveInternalBundles();
            }
            BundleInfoList.BundleInfo locationw = BundleInfoList.getInstance().getBundleInfo(location);
            if (locationw != null) {
                Log.e(TAG, "Failed to find the bundle in BundleInfoList for bundle " + location);
                insertToReasonList(location, "not found in BundleInfoList!");
            }
            if (sInternalBundles == null || sInternalBundles.contains(location)) {
                checkInstallBundleAndDependency(location);
                return;
            }
        }
    }

    private static long getAvailableInternalMemorySize() {

        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
    }

    private static boolean checkAvailableDisk() {

        if (getAvailableInternalMemorySize() >= 2097152) {
            return true;
        }
        new Handler().post(new Runnable() {
            public void run() {
                Toast.makeText(RuntimeVariables.androidApplication, "checkAvailableDisk error", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        return false;
    }

    private static int getPackageVersion() {
        PackageInfo packageInfo;
        try {
            packageInfo = RuntimeVariables.androidApplication.getPackageManager().getPackageInfo(
                    RuntimeVariables.androidApplication.getPackageName(), 0);
        } catch (Throwable e) {
            Log.e(TAG, "Error to get PackageInfo >>>", e);
            packageInfo = new PackageInfo();
        }
        return packageInfo.versionCode;
    }
}
