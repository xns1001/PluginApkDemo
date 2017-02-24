/*
 * ACDD Project
 * file FrameworkEvent.java  is  part of ACCD
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
 * A general event from the Framework.
 * <p>
 * <p>
 * <code>FrameworkEvent</code> objects are delivered to
 * <code>FrameworkListener</code>s when a general event occurs within the OSGi
 * environment. A type code is used to identify the event type for future
 * extendability.
 * <p>
 * <p>
 * OSGi Alliance reserves the right to extend the set of event types.
 *
 * @version $Revision: 6542 $
 * @see FrameworkListener
 */

public class FrameworkEvent extends EventObject {
    static final long serialVersionUID = 207051004521261705L;
    /**
     * Bundle related to the event.
     */
    private final Bundle bundle;

    /**
     * Exception related to the event.
     */
    private final Throwable throwable;

    /**
     * Type of event.
     */
    private final int type;
    public final static int STARTING = 0x00000000;
    /**
     * The Framework has started.
     * <p>
     * <p>
     * This event is fired when the Framework has started after all installed
     * bundles that are marked to be started have been started and the Framework
     * has reached the initial start level. The source of this event is the
     * System Bundle.
     *
     * @see "The Start Level Service"
     */
    public final static int STARTED = 0x00000001;

    /**
     * An error has occurred.
     * <p>
     * <p>
     * There was an error associated with a bundle.
     */
    public final static int ERROR = 0x00000002;

    /**
     * A PackageAdmin.refreshPackage operation has completed.
     * <p>
     * <p>
     * This event is fired when the Framework has completed the refresh packages
     * operation initiated by a call to the PackageAdmin.refreshPackages method.
     * The source of this event is the System Bundle.
     *
     * @see "<code>PackageAdmin.refreshPackages</code>"
     * @since 1.2
     */
    public final static int PACKAGES_REFRESHED = 0x00000004;

    /**
     * A StartLevel.setStartLevel operation has completed.
     * <p>
     * <p>
     * This event is fired when the Framework has completed changing the active
     * start level initiated by a call to the StartLevel.setStartLevel method.
     * The source of this event is the System Bundle.
     *
     * @see "The Start Level Service"
     * @since 1.2
     */
    public final static int STARTLEVEL_CHANGED = 0x00000008;

    /**
     * A warning has occurred.
     * <p>
     * <p>
     * There was a warning associated with a bundle.
     *
     * @since 1.3
     */
    public final static int WARNING = 0x00000010;

    /**
     * An informational event has occurred.
     * <p>
     * <p>
     * There was an informational event associated with a bundle.
     *
     * @since 1.3
     */
    public final static int INFO = 0x00000020;

    /**
     * The Framework has stopped.
     * <p>
     * <p>
     * This event is fired when the Framework has been stopped because of a stop
     * operation on the system bundle. The source of this event is the System
     * Bundle.
     *
     * @since 1.5
     */
    public final static int STOPPED = 0x00000040;

    /**
     * The Framework has stopped during update.
     * <p>
     * <p>
     * This event is fired when the Framework has been stopped because of an
     * update operation on the system bundle. The Framework will be restarted
     * after this event is fired. The source of this event is the System Bundle.
     *
     * @since 1.5
     */
    public final static int STOPPED_UPDATE = 0x00000080;

    /**
     * The Framework has stopped and the boot class path has changed.
     * <p>
     * <p>
     * This event is fired when the Framework has been stopped because of a stop
     * operation on the system bundle and a bootclasspath extension bundle has
     * been installed or updated. The source of this event is the System Bundle.
     *
     * @since 1.5
     */
    public final static int STOPPED_BOOTCLASSPATH_MODIFIED = 0x00000100;

    /**
     * The Framework did not stop before the wait timeout expired.
     * <p>
     * <p>
     * This event is fired when the Framework did not stop before the wait
     * timeout expired. The source of this event is the System Bundle.
     *
     * @since 1.5
     */
    public final static int WAIT_TIMEDOUT = 0x00000200;

    /**
     * Creates a Framework event.
     *
     * @param type   The event type.
     * @param source The event source object. This may not be <code>null</code>.
     * @deprecated As of 1.2. This constructor is deprecated in favor of using
     * the other constructor with the System Bundle as the event
     * source.
     */
    @Deprecated
    public FrameworkEvent(int type, Object source) {
        super(source);
        this.type = type;
        this.bundle = null;
        this.throwable = null;
    }

    /**
     * Creates a Framework event regarding the specified bundle.
     *
     * @param type      The event type.
     * @param bundle    The event source.
     * @param throwable The related exception. This argument may be
     *                  <code>null</code> if there is no related exception.
     */
    public FrameworkEvent(int type, Bundle bundle, Throwable throwable) {
        super(bundle);
        this.type = type;
        this.bundle = bundle;
        this.throwable = throwable;
    }

    /**
     * Returns the exception related to this event.
     *
     * @return The related exception or <code>null</code> if none.
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Returns the bundle associated with the event. This bundle is also the
     * source of the event.
     *
     * @return The bundle associated with the event.
     */
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * Returns the type of framework event.
     * <p>
     * The type values are:
     * <ul>
     * <li>{@link #STARTED}
     * <li>{@link #ERROR}
     * <li>{@link #WARNING}
     * <li>{@link #INFO}
     * <li>{@link #PACKAGES_REFRESHED}
     * <li>{@link #STARTLEVEL_CHANGED}
     * <li>{@link #STOPPED}
     * <li>{@link #STOPPED_BOOTCLASSPATH_MODIFIED}
     * <li>{@link #STOPPED_UPDATE}
     * <li>{@link #WAIT_TIMEDOUT}
     * </ul>
     *
     * @return The type of state change.
     */

    public int getType() {
        return type;
    }
}
