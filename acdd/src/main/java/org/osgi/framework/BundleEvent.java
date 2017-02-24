/*
 * ACDD Project
 * file BundleEvent.java  is  part of ACCD
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

package org.osgi.framework;

import java.util.EventObject;


/**
 * An event from the Framework describing a bundle lifecycle change.
 * <p>
 * <code>BundleEvent</code> objects are delivered to
 * <code>SynchronousBundleListener</code>s and <code>BundleListener</code>s
 * when a change occurs in a bundle's lifecycle. A type code is used to identify
 * the event type for future extendability.
 * <p>
 * <p>
 * OSGi Alliance reserves the right to extend the set of types.
 *
 * @version $Revision: 6542 $
 * @see BundleListener
 * @see SynchronousBundleListener
 */

public class BundleEvent extends EventObject {
    static final long serialVersionUID = 4080640865971756012L;
    /**
     * Bundle that had a change occur in its lifecycle.
     */
    private final Bundle bundle;

    /**
     * Type of bundle lifecycle change.
     */
    private final int type;
    public static final int LOADED = 0;
    /**
     * The bundle has been installed.
     *
     * @see BundleContext#installBundle(String)
     */
    public final static int INSTALLED = 0x00000001;

    /**
     * The bundle has been started.
     * <p>
     *
     * @see Bundle#start()
     */
    public final static int STARTED = 0x00000002;

    /**
     * The bundle has been stopped.
     * <p>
     * @see Bundle#stop()
     */
    public final static int STOPPED = 0x00000004;

    /**
     * The bundle has been updated.
     *
     * @see Bundle#update()
     */
    public final static int UPDATED = 0x00000008;

    /**
     * The bundle has been uninstalled.
     *
     * @see Bundle#uninstall
     */
    public final static int UNINSTALLED = 0x00000010;

    /**
     * The bundle has been resolved.
     *
     * @see Bundle#RESOLVED
     * @since 1.3
     */
    public final static int RESOLVED = 0x00000020;

    /**
     * The bundle has been unresolved.
     *
     * @see Bundle#INSTALLED
     * @since 1.3
     */
    public final static int UNRESOLVED = 0x00000040;

    /**
     * The bundle is about to be activated.
     * <p>
     *This
     * event is only delivered to {@link SynchronousBundleListener}s. It is not
     * delivered to <code>BundleListener</code>s.
     *
     * @see Bundle#start()
     * @since 1.3
     */
    public final static int STARTING = 0x00000080;

    /**
     * The bundle is about to deactivated.
     * <p>
     * This
     * event is only delivered to {@link SynchronousBundleListener}s. It is not
     * delivered to <code>BundleListener</code>s.
     *
     * @see Bundle#stop()
     * @since 1.3
     */
    public final static int STOPPING = 0x00000100;

    /**
     * The bundle will be lazily activated.
     * <p>
     * The bundle has a  TIVATION_LAZY lazy activation policy
     * and is waiting to be activated. It is now in the
     * {@link Bundle#STARTING STARTING} state and has a valid
     * <code>BundleContext</code>. This event is only delivered to
     * {@link SynchronousBundleListener}s. It is not delivered to
     * <code>BundleListener</code>s.
     *
     * @since 1.4
     */
    public final static int LAZY_ACTIVATION = 0x00000200;

    /**
     * Creates a bundle event of the specified type.
     *
     * @param type   The event type.
     * @param bundle The bundle which had a lifecycle change.
     */

    public BundleEvent(int type, Bundle bundle) {
        super(bundle);
        this.bundle = bundle;
        this.type = type;
    }

    /**
     * Returns the bundle which had a lifecycle change. This bundle is the
     * source of the event.
     *
     * @return The bundle that had a change occur in its lifecycle.
     */
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * Returns the type of lifecyle event. The type values are:
     * <ul>
     * <li>{@link #INSTALLED}
     * <li>{@link #RESOLVED}
     * <li>{@link #LAZY_ACTIVATION}
     * <li>{@link #STARTING}
     * <li>{@link #STARTED}
     * <li>{@link #STOPPING}
     * <li>{@link #STOPPED}
     * <li>{@link #UPDATED}
     * <li>{@link #UNRESOLVED}
     * <li>{@link #UNINSTALLED}
     * </ul>
     *
     * @return The type of lifecycle event.
     */

    public int getType() {
        return type;
    }
}
