/*
 * ACDD Project
 * file BundleImpl.java  is  part of ACCD
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
package org.acdd.framework;

import org.acdd.framework.bundlestorage.Archive;
import org.acdd.framework.bundlestorage.BundleArchive;
import org.acdd.log.Logger;
import org.acdd.log.LoggerFactory;
import org.acdd.runtime.RuntimeVariables;
import org.acdd.runtime.stub.BundlePackageManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

//import org.osgi.framework.ServiceListener;
//import org.osgi.framework.ServiceReference;

/***OSGI Bundle implementation **/
public final class BundleImpl implements Bundle {
    static final Logger log;
    Archive archive;
    final File bundleDir;
    BundleClassLoader classloader;
    BundlePackageManager packageManager = null;
    int currentStartlevel;
    ProtectionDomain domain;
    Hashtable<String, String> headers = new Hashtable<String, String>();
    final String location;
    boolean persistently;
    List<BundleListener> registeredBundleListeners;
    List<FrameworkListener> registeredFrameworkListeners;
    int state = 0;
    boolean isValid=true;

    static {
        log = LoggerFactory.getInstance("BundleImpl");
    }

    BundleImpl(File bundleDir, String location,
               InputStream archiveInputStream, File archiveFile, boolean isInstall)
            throws BundleException, IOException {
        this.persistently = false;
        this.domain = null;
        this.registeredFrameworkListeners = null;
        this.registeredBundleListeners = null;
        long currentTimeMillis = System.currentTimeMillis();
        this.location = location;

        this.currentStartlevel = Framework.startlevel;
        this.bundleDir = bundleDir;
        if (archiveInputStream != null) {
            //  try {
            this.archive = new BundleArchive(location, bundleDir, archiveInputStream);
//            } catch (Throwable e) {
//                Framework.deleteDirectory(bundleDir);
//                throw new BundleException("Could not install bundle " + location, e);
//            }
        } else if (archiveFile != null) {
            try {
                this.archive = new BundleArchive(location, bundleDir, archiveFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.state = BundleEvent.STARTED;

        updateMetadata();
        if (isInstall) {
            Framework.bundles.put(location, this);
            resolveBundle(false);
            Framework.notifyBundleListeners(BundleEvent.INSTALLED, this);
        }

        if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
            log.info("Framework: Bundle " + toString() + " created. "
                    + (System.currentTimeMillis() - currentTimeMillis) + " ms");
        }
    }

    BundleImpl(File file) throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(new File(file, "meta")));
        this.location = dataInputStream.readUTF();
        this.currentStartlevel = dataInputStream.readInt();
        this.persistently = dataInputStream.readBoolean();
        dataInputStream.close();

        this.bundleDir = file;
        this.state = BundleEvent.STARTED;
        try {
            this.archive = new BundleArchive(this.location, file);
            resolveBundle(false);
            Framework.bundles.put(this.location, this);
            Framework.notifyBundleListeners(BundleEvent.INSTALLED, this);
            if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
                log.info("Framework: Bundle " + toString() + " loaded. " + (System.currentTimeMillis() - currentTimeMillis) + " ms");
            }
        } catch (Exception e) {
            throw new BundleException("Could not load bundle " + this.location, e.getCause());
        }
    }


    private synchronized void resolveBundle(boolean recursive) throws BundleException {
        if (this.state != BundleEvent.STOPPED) {
            if (this.classloader == null) {
                this.classloader = new BundleClassLoader(this);
            }
            if (recursive) {
                this.classloader.resolveBundle(true, new HashSet(0));
                this.state =  BundleEvent.STOPPED;
            } else if (this.classloader.resolveBundle(false, null)) {
                this.state =  BundleEvent.STOPPED;
            }
            Framework.notifyBundleListeners(BundleEvent.LOADED, this);
        }
    }

    @Override
    public long getBundleId() {
        return 0;
    }

    @Override
    public Dictionary<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    public Archive getArchive() {
        return this.archive;
    }

    /***
     * @return BundleClassLoader BundleClassLoader
     **/
    public ClassLoader getClassLoader() {
        return this.classloader;
    }


    @Override
    public URL getResource(String name) {
        if (this.state != BundleEvent.INSTALLED) {
            return this.classloader.getResource(name);
        }
        throw new IllegalStateException("Bundle " + toString()
                + " has been uninstalled");
    }

    @Override
    public int getState() {
        return this.state;
    }

    @Override
    public boolean hasPermission(Object permission) {
        if (this.state != BundleEvent.INSTALLED) {
            return true;
        }
        throw new IllegalStateException("Bundle " + toString()
                + "has been unregistered.");
    }

    @Override
    public synchronized void start() throws BundleException {
        this.persistently = true;
        updateMetadata();
        if (this.currentStartlevel <= Framework.startlevel) {
            startBundle();
        }
    }

    public synchronized void startBundle() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot start uninstalled bundle "
                    + toString());
        } else if (this.state != BundleEvent.RESOLVED) {
            if (this.state == BundleEvent.STARTED) {
                resolveBundle(true);
            }
            this.state = BundleEvent.UPDATED;
            try {

                isValid = true;
                this.state = BundleEvent.RESOLVED;
                Framework.notifyBundleListeners(BundleEvent.STARTED, this);
                if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
                    log.info("Framework: Bundle " + toString() + " started.");
                }
            } catch (Throwable th) {

                Framework.clearBundleTrace(this);
                this.state = BundleEvent.STOPPED;
                String msg = "Error starting bundle " + toString();
                log.error(msg,th);
            }
        }
    }

    @Override
    public synchronized void stop() throws BundleException {
        this.persistently = false;
        updateMetadata();
        stopBundle();
    }

    public synchronized void stopBundle() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot stop uninstalled bundle "
                    + toString());
        } else if (this.state == BundleEvent.RESOLVED) {
            this.state = BundleEvent.UNINSTALLED;
            try {
                if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
                    log.info("Framework: Bundle " + toString() + " stopped.");
                }
                Framework.clearBundleTrace(this);
                this.state = BundleEvent.STOPPED;
                Framework.notifyBundleListeners(BundleEvent.STOPPED, this);
                isValid = false;
            } catch (Throwable th) {

                Framework.clearBundleTrace(this);
                this.state = BundleEvent.STOPPED;
                Framework.notifyBundleListeners(BundleEvent.STOPPED, this);
                isValid = false;
            }
        }
    }

    @Override
    public synchronized void uninstall() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Bundle " + toString() + " is already uninstalled.");
        }
        if (this.state == BundleEvent.RESOLVED) {
            try {
                stopBundle();
            } catch (Throwable th) {
                Framework.notifyFrameworkListeners(BundleEvent.STARTED, this, th);
            }
        }
        this.state = BundleEvent.INSTALLED;
        new File(this.bundleDir, "meta").delete();
        this.classloader.cleanup(true);
        this.classloader = null;
        Framework.bundles.remove(this);
        Framework.notifyBundleListeners(BundleEvent.UNINSTALLED, this);
        isValid = false;



    }

    @Override
    public synchronized void update() throws BundleException {
        String locationUpdate = this.headers.get(Constants.BUNDLE_UPDATELOCATION);
        try {

            if (locationUpdate == null) {
                locationUpdate = this.location;
            }
            update(new URL(locationUpdate).openConnection().getInputStream());
        } catch (Throwable e) {
            throw new BundleException("Could not update " + toString()
                    + " from " + locationUpdate, e);
        }
    }

    @Override
    public synchronized void update(InputStream inputStream)
            throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot update uninstalled bundle "
                    + toString());
        }
        try {
            this.archive
                    .newRevision(this.location, this.bundleDir, inputStream);
        } catch (Throwable e) {
            throw new BundleException("Could not update bundle " + toString(),
                    e);
        }
    }

    @Override
    public synchronized void update(File bundleFile) throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot update uninstalled bundle "
                    + toString());
        }
        try {
            this.archive.newRevision(this.location, this.bundleDir, bundleFile);
        } catch (Throwable e) {
            throw new BundleException("Could not update bundle " + toString(),
                    e);
        }
    }

    public synchronized void refresh() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException(
                    "Cannot refresh uninstalled bundle " + toString());
        }
       boolean isResolved=false;
        if (this.state == BundleEvent.RESOLVED) {
            stopBundle();
            isResolved=true;
        }
        try {
            this.archive = new BundleArchive(this.location, this.bundleDir);
            BundleClassLoader bundleClassLoader = new BundleClassLoader(this);

            this.classloader.cleanup(true);
            this.classloader = bundleClassLoader;
            if (this.classloader.resolveBundle(false, null)) {
                this.state = BundleEvent.STOPPED;
            } else {
                this.state = BundleEvent.STARTED;
            }
            Framework.notifyBundleListeners(BundleEvent.UPDATED, this);
            if (isResolved) {
                startBundle();
            }
        } catch (BundleException e) {
            throw e;
        } catch (Throwable e) {
            throw new BundleException("Could not refresh bundle " + toString(), e);
        }
    }

    public synchronized void optDexFile() {
        getArchive().optDexFile();
    }

    public synchronized void purge() throws BundleException {
        try {
            getArchive().purge();
        } catch (Throwable e) {
            throw new BundleException("Could not purge bundle " + toString(), e);
        }
    }

    void updateMetadata() {
        File file = new File(this.bundleDir, "meta");
        DataOutputStream dataOutputStream = null;
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            dataOutputStream = new DataOutputStream(fileOutputStream);
            dataOutputStream.writeUTF(this.location);
            dataOutputStream.writeInt(this.currentStartlevel);
            dataOutputStream.writeBoolean(this.persistently);
            dataOutputStream.flush();
            fileOutputStream.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public String toString() {
        return this.location;
    }
    public  BundlePackageManager getPackageManager() {
        if (this.packageManager == null) {
            try {
                synchronized (BundleImpl.class) {
                    this.packageManager = BundlePackageManager.parseBundle(RuntimeVariables.androidApplication, this);
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this.packageManager;
    }

    public boolean isUpdated() {

        return getArchive().isUpdated();
    }


}
