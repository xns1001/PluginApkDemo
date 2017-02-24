/*
 * ACDD Project
 * file BundleContext.java  is  part of ACCD
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

import java.io.File;
import java.io.InputStream;


/**
 * A bundle's execution context within the Framework. The context is used to
 * grant access to other methods so that this bundle can interact with the
 * Framework.
 * <p>
 * <p>
 * <code>BundleContext</code> methods allow a bundle to:
 * <ul>
 * <li>Subscribe to events published by the Framework.
 * <li>Register service objects with the Framework service registry.
 * <li>Retrieve <code>ServiceReferences</code> from the Framework service
 * registry.
 * <li>Get and release service objects for a referenced service.
 * <li>Install new bundles in the Framework.
 * <li>Get the list of bundles installed in the Framework.
 * <li>Get the {@link Bundle} object for a bundle.
 * <li>Create <code>File</code> objects for files in a persistent storage
 * area provided for the bundle by the Framework.
 * </ul>
 * <p>
 * <p>
 * A <code>BundleContext</code> object will be created and provided to the
 * bundle associated with this context
 * <code>BundleContext</code> object is generally for the private use of its
 * associated bundle and is not meant to be shared with other bundles in the
 * OSGi environment.
 * <p>
 * <p>
 * The <code>Bundle</code> object associated with a <code>BundleContext</code>
 * object is called the <em>context bundle</em>.
 * <p>
 * <p>
 * The <code>BundleContext</code> object is only valid during the execution of
 * its context bundle; that is, during the period from when the context bundle
 * is in the <code>STARTING</code>, <code>STOPPING</code>, and
 * <code>ACTIVE</code> bundle states. If the <code>BundleContext</code>
 * object is used subsequently, an <code>IllegalStateException</code> must be
 * thrown. The <code>BundleContext</code> object must never be reused after
 * its context bundle is stopped.
 * <p>
 * <p>
 * The Framework is the only entity that can create <code>BundleContext</code>
 * objects and they are only valid within the Framework that created them.
 *
 * @version $Revision: 6781 $
 */

public interface BundleContext {
    /**
     * Returns the value of the specified property. If the key is not found in
     * the Framework properties, the system properties are then searched. The
     * method returns <code>null</code> if the property is not found.
     * <p>
     * <p>
     * All bundles must have permission to read properties whose names start
     * with &quot;org.osgi.&quot;.
     *
     * @param key The name of the requested property.
     * @return The value of the requested property, or <code>null</code> if the
     * property is undefined.
     * @throws SecurityException If the caller does not have the appropriate
     *                           <code>PropertyPermission</code> to read the property, and the
     *                           Java Runtime Environment supports permissions.
     */
    String getProperty(String key);

    /**
     * Returns the <code>Bundle</code> object associated with this
     * <code>BundleContext</code>. This bundle is called the context bundle.
     *
     * @return The <code>Bundle</code> object associated with this
     * <code>BundleContext</code>.
     * @throws IllegalStateException If this BundleContext is no
     *                               longer valid.
     */
    Bundle getBundle();

    /**
     * Installs a bundle from the specified <code>InputStream</code> object.
     * <p>
     * <p>
     * If the specified <code>InputStream</code> is <code>null</code>, the
     * Framework must create the <code>InputStream</code> from which to read the
     * bundle by interpreting, in an implementation dependent manner, the
     * specified <code>location</code>.
     * <p>
     * <p>
     * The specified <code>location</code> identifier will be used as the
     * identity of the bundle. Every installed bundle is uniquely identified by
     * its location identifier which is typically in the form of a URL.
     * <p>
     * <p>
     * The following steps are required to install a bundle:
     * <ol>
     * <li>If a bundle containing the same location identifier is already
     * installed, the <code>Bundle</code> object for that bundle is returned.
     * <p>
     * <li>The bundle's content is read from the input stream. If this fails, a
     * {@link BundleException} is thrown.
     * <p>
     * <li>The bundle's associated resources are allocated. The associated
     * resources minimally consist of a unique identifier and a persistent
     * storage area if the platform has file system support. If this step fails,
     * a <code>BundleException</code> is thrown.
     * <p>
     * <li>The bundle's state is set to <code>INSTALLED</code>.
     * <p>
     * <li>A bundle event of type {@link BundleEvent#INSTALLED} is fired.
     * <p>
     * <li>The <code>Bundle</code> object for the newly or previously installed
     * bundle is returned.
     * </ol>
     * <p>
     * <b>Postconditions, no exceptions thrown </b>
     * <ul>
     * <li><code>getState()</code> in &#x007B; <code>INSTALLED</code>,
     * <code>RESOLVED</code> &#x007D;.
     * <li>Bundle has a unique ID.
     * </ul>
     * <b>Postconditions, when an exception is thrown </b>
     * <ul>
     * <li>Bundle is not installed and no trace of the bundle exists.
     * </ul>
     *
     * @param location The location identifier of the bundle to install.
     * @param input    The <code>InputStream</code> object from which this bundle
     *                 will be read or <code>null</code> to indicate the Framework must
     *                 create the input stream from the specified location identifier.
     *                 The input stream must always be closed when this method completes,
     *                 even if an exception is thrown.
     * @return The <code>Bundle</code> object of the installed bundle.
     * @throws BundleException       If the input stream cannot be read or the
     *                               installation failed.
     * @throws SecurityException     If the caller does not have the appropriate
     *                               <code>AdminPermission[installed bundle,LIFECYCLE]</code>, and the
     *                               Java Runtime Environment supports permissions.
     * @throws IllegalStateException If this BundleContext is no longer valid.
     */
    Bundle installBundle(String location, InputStream input)
            throws BundleException;

    /**
     * Installs a bundle from the specified <code>location</code> identifier.
     * <p>
     * <p>
     * This method performs the same function as calling
     * {@link #installBundle(String, InputStream)} with the specified
     * <code>location</code> identifier and a <code>null</code> InputStream.
     *
     * @param location The location identifier of the bundle to install.
     * @return The <code>Bundle</code> object of the installed bundle.
     * @throws BundleException       If the installation failed.
     * @throws SecurityException     If the caller does not have the appropriate
     *                               <code>AdminPermission[installed bundle,LIFECYCLE]</code>, and the
     *                               Java Runtime Environment supports permissions.
     * @throws IllegalStateException If this BundleContext is no longer valid.
     * @see #installBundle(String, InputStream)
     */
    Bundle installBundle(String location) throws BundleException;

    /**
     * Returns the bundle with the specified identifier.
     *
     * @param id The identifier of the bundle to retrieve.
     * @return A <code>Bundle</code> object or <code>null</code> if the
     * identifier does not match any installed bundle.
     */
    Bundle getBundle(long id);

    /**
     * Returns a list of all installed bundles.
     * <p>
     * This method returns a list of all bundles installed in the OSGi
     * environment at the time of the call to this method. However, since the
     * Framework is a very dynamic environment, bundles can be installed or
     * uninstalled at anytime.
     *
     * @return An array of <code>Bundle</code> objects, one object per
     * installed bundle.
     */
    Bundle[] getBundles();

//    /**
//     * Adds the specified <code>ServiceListener</code> object with the
//     * specified <code>filter</code> to the context bundle's list of
//     * listeners. See {@link Filter} for a description of the filter syntax.
//     * <code>ServiceListener</code> objects are notified when a service has a
//     * lifecycle state change.
//     * <p>
//     * <p>
//     * If the context bundle's list of listeners already contains a listener
//     * <code>l</code> such that <code>(l==listener)</code>, then this
//     * method replaces that listener's filter (which may be <code>null</code>)
//     * with the specified one (which may be <code>null</code>).
//     * <p>
//     * <p>
//     * The listener is called if the filter criteria is met. To filter based
//     * upon the class of the service, the filter should reference the
//     * OBJECTCLASS property. If <code>filter</code> is
//     * <code>null</code>, all services are considered to match the filter.
//     * <p>
//     * <p>
//     * When using a <code>filter</code>, it is possible that the
//     * <code>ServiceEvent</code>s for the complete lifecycle of a service
//     * will not be delivered to the listener. For example, if the
//     * <code>filter</code> only matches when the property <code>x</code> has
//     * the value <code>1</code>, the listener will not be called if the
//     * service is registered with the property <code>x</code> not set to the
//     * value <code>1</code>. Subsequently, when the service is modified
//     * setting property <code>x</code> to the value <code>1</code>, the
//     * filter will match and the listener will be called with a
//     * <code>ServiceEvent</code> of type <code>MODIFIED</code>. Thus, the
//     * listener will not be called with a <code>ServiceEvent</code> of type
//     * <code>REGISTERED</code>.
//     * <p>
//     * <p>
//     * If the Java Runtime Environment supports permissions, the
//     * <code>ServiceListener</code> object will be notified of a service event
//     * only if the bundle that is registering it has the
//     * <code>ServicePermission</code> to get the service using at least one of
//     * the named classes the service was registered under.
//     *
//     * @param listener The <code>ServiceListener</code> object to be added.
//     * @param filter   The filter criteria.
//     * @throws InvalidSyntaxException If <code>filter</code> contains an
//     *                                invalid filter string that cannot be parsed.
//     * @throws IllegalStateException  If this BundleContext is no
//     *                                longer valid.
//     * @see ServiceEvent
//     * @see ServiceListener
//     * @see ServicePermission
//     */
//    void addServiceListener(ServiceListener listener, String filter)
//            throws InvalidSyntaxException;

//    /**
//     * Adds the specified <code>ServiceListener</code> object to the context
//     * bundle's list of listeners.
//     * <p>
//     * <p>
//     * This method is the same as calling
//     * <code>BundleContext.addServiceListener(ServiceListener listener,
//     * String filter)</code>
//     * with <code>filter</code> set to <code>null</code>.
//     *
//     * @param listener The <code>ServiceListener</code> object to be added.
//     * @throws IllegalStateException If this BundleContext is no
//     *                               longer valid.
//     * @see #addServiceListener(ServiceListener, String)
//     */
//    void addServiceListener(ServiceListener listener);

//    /**
//     * Removes the specified <code>ServiceListener</code> object from the
//     * context bundle's list of listeners.
//     * <p>
//     * <p>
//     * If <code>listener</code> is not contained in this context bundle's list
//     * of listeners, this method does nothing.
//     *
//     * @param listener The <code>ServiceListener</code> to be removed.
//     * @throws IllegalStateException If this BundleContext is no
//     *                               longer valid.
//     */
//    void removeServiceListener(ServiceListener listener);

    /**
     * Adds the specified <code>BundleListener</code> object to the context
     * bundle's list of listeners if not already present. BundleListener objects
     * are notified when a bundle has a lifecycle state change.
     * <p>
     * <p>
     * If the context bundle's list of listeners already contains a listener
     * <code>l</code> such that <code>(l==listener)</code>, this method
     * does nothing.
     *
     * @param listener The <code>BundleListener</code> to be added.
     * @throws IllegalStateException If this BundleContext is no
     *                               longer valid.
     * @throws SecurityException     If listener is a
     *                               <code>SynchronousBundleListener</code> and the caller does not
     *                               have the appropriate
     *                               <code>AdminPermission[context bundle,LISTENER]</code>, and the
     *                               Java Runtime Environment supports permissions.
     * @see BundleEvent
     * @see BundleListener
     */
    void addBundleListener(BundleListener listener);

    /**
     * Removes the specified <code>BundleListener</code> object from the
     * context bundle's list of listeners.
     * <p>
     * <p>
     * If <code>listener</code> is not contained in the context bundle's list
     * of listeners, this method does nothing.
     *
     * @param listener The <code>BundleListener</code> object to be removed.
     * @throws IllegalStateException If this BundleContext is no
     *                               longer valid.
     * @throws SecurityException     If listener is a
     *                               <code>SynchronousBundleListener</code> and the caller does not
     *                               have the appropriate
     *                               <code>AdminPermission[context bundle,LISTENER]</code>, and the
     *                               Java Runtime Environment supports permissions.
     */
    void removeBundleListener(BundleListener listener);

    /**
     * Adds the specified <code>FrameworkListener</code> object to the context
     * bundle's list of listeners if not already present. FrameworkListeners are
     * notified of general Framework events.
     * <p>
     * <p>
     * If the context bundle's list of listeners already contains a listener
     * <code>l</code> such that <code>(l==listener)</code>, this method
     * does nothing.
     *
     * @param listener The <code>FrameworkListener</code> object to be added.
     * @throws IllegalStateException If this BundleContext is no
     *                               longer valid.
     * @see FrameworkEvent
     * @see FrameworkListener
     */
    void addFrameworkListener(FrameworkListener listener);

    /**
     * Removes the specified <code>FrameworkListener</code> object from the
     * context bundle's list of listeners.
     * <p>
     * <p>
     * If <code>listener</code> is not contained in the context bundle's list
     * of listeners, this method does nothing.
     *
     * @param listener The <code>FrameworkListener</code> object to be
     *                 removed.
     * @throws IllegalStateException If this BundleContext is no
     *                               longer valid.
     */
    void removeFrameworkListener(FrameworkListener listener);


//    /**
//     * Returns an array of <code>ServiceReference</code> objects. The returned
//     * array of <code>ServiceReference</code> objects contains services that
//     * were registered under the specified class, match the specified filter
//     * expression, and the packages for the class names under which the services
//     * were registered match the context bundle's packages
//     * <p>
//     * <p>
//     * The list is valid at the time of the call to this method. However since
//     * the Framework is a very dynamic environment, services can be modified or
//     * unregistered at any time.
//     * <p>
//     * <p>
//     * The specified <code>filter</code> expression is used to select the
//     * registered services whose service properties contain keys and values
//     * which satisfy the filter expression. See {@link Filter} for a description
//     * of the filter syntax. If the specified <code>filter</code> is
//     * <code>null</code>, all registered services are considered to match the
//     * filter. If the specified <code>filter</code> expression cannot be parsed,
//     * an {@link InvalidSyntaxException} will be thrown with a human readable
//     * message where the filter became unparsable.
//     * <p>
//     * <p>
//     * The result is an array of <code>ServiceReference</code> objects for all
//     * services that meet all of the following conditions:
//     * <ul>
//     * <li>If the specified class name, <code>clazz</code>, is not
//     * <code>null</code>, the service must have been registered with the
//     * specified class name. The complete list of class names with which a
//     * service was registered is available from the service's
//     * OBJECTCLASS  property.
//     * <li>If the specified <code>filter</code> is not <code>null</code>, the
//     * filter expression must match the service.
//     * <li>If the Java Runtime Environment supports permissions, the caller must
//     * have <code>ServicePermission</code> with the <code>GET</code> action for
//     * at least one of the class names under which the service was registered.
//     * <li>For each class name with which the service was registered, calling
//     * ServiceReference.isAssignableTo(Bundle, String) with the context
//     * bundle and the class name on the service's <code>ServiceReference</code>
//     * object must return <code>true</code>
//     * </ul>
//     *
//     * @param clazz  The class name with which the service was registered or
//     *               <code>null</code> for all services.
//     * @param filter The filter expression or <code>null</code> for all
//     *               services.
//     * @return An array of <code>ServiceReference</code> objects or
//     * <code>null</code> if no services are registered which satisfy the
//     * search.
//     * @throws InvalidSyntaxException If the specified <code>filter</code>
//     *                                contains an invalid filter expression that cannot be parsed.
//     * @throws IllegalStateException  If this BundleContext is no longer valid.
//     */
//    ServiceReference[] getServiceReferences(String clazz, String filter)
//            throws InvalidSyntaxException;


//    /**
//     * Returns a <code>ServiceReference</code> object for a service that
//     * implements and was registered under the specified class.
//     * <p>
//     * <p>
//     * The returned <code>ServiceReference</code> object is valid at the time of
//     * the call to this method. However as the Framework is a very dynamic
//     * environment, services can be modified or unregistered at any time.
//     * <p>
//     * <p>
//     * If multiple such services exist, the service with the highest ranking (as
//     * specified in its SERVICE_RANKING property) is returned.
//     * <p>
//     * If there is a tie in ranking, the service with the lowest service ID (as
//     * specified in its  SERVICE_ID property); that is, the
//     * service that was registered first is returned.
//     *
//     * @param clazz The class name with which the service was registered.
//     * @return A <code>ServiceReference</code> object, or <code>null</code> if
//     * no services are registered which implement the named class.
//     * @throws IllegalStateException If this BundleContext is no longer valid.
//     */
//    ServiceReference getServiceReference(String clazz);

//    /**
//     * Returns the service object referenced by the specified
//     * <code>ServiceReference</code> object.
//     * <p>
//     * A bundle's use of a service is tracked by the bundle's use count of that
//     * service. Each time a service's service object is returned by
//     * {@link #getService(ServiceReference)} the context bundle's use count for
//     * that service is incremented by one. Each time the service is released by
//     * {@link #ungetService(ServiceReference)} the context bundle's use count
//     * for that service is decremented by one.
//     * <p>
//     * When a bundle's use count for a service drops to zero, the bundle should
//     * no longer use that service.
//     * <p>
//     * <p>
//     * This method will always return <code>null</code> when the service
//     * associated with this <code>reference</code> has been unregistered.
//     * <p>
//     * <p>
//     * The following steps are required to get the service object:
//     * <ol>
//     * <li>If the service has been unregistered, <code>null</code> is returned.
//     * <li>The context bundle's use count for this service is incremented by
//     * one.
//     * <li>If the context bundle's use count for the service is currently one
//     * and the service was registered with an object implementing the
//     * <code>ServiceFactory</code> interface, the
//     * {@link ServiceFactory#getService(Bundle, ServiceRegistration)} method is
//     * called to create a service object for the context bundle. This service
//     * object is cached by the Framework. While the context bundle's use count
//     * for the service is greater than zero, subsequent calls to get the
//     * services's service object for the context bundle will return the cached
//     * service object. <br>
//     * If the service object returned by the <code>ServiceFactory</code> object
//     * is not an <code>instanceof</code> all the classes named when the service
//     * was registered or the <code>ServiceFactory</code> object throws an
//     * exception, <code>null</code> is returned and a Framework event of type
//     * {@link FrameworkEvent#ERROR} containing a ServiceException
//     * describing the error is fired.
//     * <li>The service object for the service is returned.
//     * </ol>
//     *
//     * @param reference A reference to the service.
//     * @return A service object for the service associated with
//     * <code>reference</code> or <code>null</code> if the service is not
//     * registered, the service object returned by a
//     * <code>ServiceFactory</code> does not implement the classes under
//     * which it was registered or the <code>ServiceFactory</code> threw
//     * an exception.
//     * @throws SecurityException        If the caller does not have the
//     *                                  <code>ServicePermission</code> to get the service using at least
//     *                                  one of the named classes the service was registered under and the
//     *                                  Java Runtime Environment supports permissions.
//     * @throws IllegalStateException    If this BundleContext is no
//     *                                  longer valid.
//     * @throws IllegalArgumentException If the specified
//     *                                  <code>ServiceReference</code> was not created by the same
//     *                                  framework instance as this <code>BundleContext</code>.
//     * @see #ungetService(ServiceReference)
//     * @see ServiceFactory
//     */
//    Object getService(ServiceReference reference);
//
//    /**
//     * Releases the service object referenced by the specified
//     * <code>ServiceReference</code> object. If the context bundle's use count
//     * for the service is zero, this method returns <code>false</code>.
//     * Otherwise, the context bundle's use count for the service is decremented
//     * by one.
//     * <p>
//     * <p>
//     * The service's service object should no longer be used and all references
//     * to it should be destroyed when a bundle's use count for the service drops
//     * to zero.
//     * <p>
//     * <p>
//     * The following steps are required to unget the service object:
//     * <ol>
//     * <li>If the context bundle's use count for the service is zero or the
//     * service has been unregistered, <code>false</code> is returned.
//     * <li>The context bundle's use count for this service is decremented by
//     * one.
//     * <li>If the context bundle's use count for the service is currently zero
//     * and the service was registered with a <code>ServiceFactory</code> object,
//     * the
//     * {@link ServiceFactory#ungetService(Bundle, ServiceRegistration, Object)}
//     * method is called to release the service object for the context bundle.
//     * <li><code>true</code> is returned.
//     * </ol>
//     *
//     * @param reference A reference to the service to be released.
//     * @return <code>false</code> if the context bundle's use count for the
//     * service is zero or if the service has been unregistered;
//     * <code>true</code> otherwise.
//     * @throws IllegalStateException    If this BundleContext is no
//     *                                  longer valid.
//     * @throws IllegalArgumentException If the specified
//     *                                  <code>ServiceReference</code> was not created by the same
//     *                                  framework instance as this <code>BundleContext</code>.
//     * @see #getService
//     * @see ServiceFactory
//     */
//    boolean ungetService(ServiceReference reference);

    /**
     * Creates a <code>File</code> object for a file in the persistent storage
     * area provided for the bundle by the Framework. This method will return
     * <code>null</code> if the platform does not have file system support.
     * <p>
     * <p>
     * A <code>File</code> object for the base directory of the persistent
     * storage area provided for the context bundle by the Framework can be
     * obtained by calling this method with an empty string as
     * <code>filename</code>.
     * <p>
     * <p>
     * If the Java Runtime Environment supports permissions, the Framework will
     * ensure that the bundle has the <code>java.io.FilePermission</code> with
     * actions <code>read</code>,<code>write</code>,<code>delete</code>
     * for all files (recursively) in the persistent storage area provided for
     * the context bundle.
     *
     * @param filename A relative name to the file to be accessed.
     * @return A <code>File</code> object that represents the requested file
     * or <code>null</code> if the platform does not have file system
     * support.
     * @throws IllegalStateException If this BundleContext is no
     *                               longer valid.
     */
    File getDataFile(String filename);

    /**
     * Creates a <code>Filter</code> object. This <code>Filter</code> object may
     * be used to match a <code>ServiceReference</code> object or a
     * <code>Dictionary</code> object.
     * <p>
     * <p>
     * If the filter cannot be parsed, an {@link InvalidSyntaxException} will be
     * thrown with a human readable message where the filter became unparsable.
     *
     * @param filter The filter string.
     * @return A <code>Filter</code> object encapsulating the filter string.
     * @throws InvalidSyntaxException If <code>filter</code> contains an invalid
     *                                filter string that cannot be parsed.
     * @throws NullPointerException   If <code>filter</code> is null.
     * @throws IllegalStateException  If this BundleContext is no longer valid.
     * @see "Framework specification for a description of the filter string syntax."
     * @since 1.1
     */
    Filter createFilter(String filter) throws InvalidSyntaxException;

//    /**
//     * Registers the specified service object with the specified properties
//     * under the specified class names into the Framework. A
//     * <code>ServiceRegistration</code> object is returned. The
//     * <code>ServiceRegistration</code> object is for the private use of the
//     * <p>
//     * A bundle can register a service object that implements the
//     * {@link ServiceFactory} interface to have more flexibility in providing
//     * service objects to other bundles.
//     * <p>
//     * <p>
//     * The following steps are required to register a service:
//     * <ol>
//     * <li>If <code>service</code> is not a <code>ServiceFactory</code>, an
//     * <code>IllegalArgumentException</code> is thrown if <code>service</code>
//     * is not an <code>instanceof</code> all the specified class names.
//     * <li>The Framework adds the following service properties to the service
//     * properties from the specified <code>Dictionary</code> (which may be
//     * <code>null</code>): <br>
//     * A property named {@link Constants#SERVICE_ID} identifying the
//     * registration number of the service <br>
//     * A property named {@link Constants#OBJECTCLASS} containing all the
//     * specified classes. <br>
//     * Properties with these names in the specified <code>Dictionary</code> will
//     * be ignored.
//     * <li>The service is added to the Framework service registry and may now be
//     * used by other bundles.
//     * <li>A <code>ServiceRegistration</code> object for this registration is
//     * returned.
//     * </ol>
//     *
//     * @param clazzes    The class names under which the service can be located.
//     *                   The class names in this array will be stored in the service's
//     *                   properties under the key {@link Constants#OBJECTCLASS}.
//     * @param service    The service object or a <code>ServiceFactory</code>
//     *                   object.
//     * @param properties The properties for this service. The keys in the
//     *                   properties object must all be <code>String</code> objects. See
//     *                   {@link Constants} for a list of standard service property keys.
//     *                   Changes should not be made to this object after calling this
//     *                   method. To update the service's properties the
//     *                   {@link ServiceRegistration#setProperties} method must be called.
//     *                   The set of properties may be <code>null</code> if the service has
//     *                   no properties.
//     * @return A <code>ServiceRegistration</code> object for use by the bundle
//     * registering the service to update the service's properties or to
//     * unregister the service.
//     * @throws IllegalArgumentException If one of the following is true:
//     *                                  <ul>
//     *                                  <li><code>service</code> is <code>null</code>. <li><code>service
//     *                                  </code> is not a <code>ServiceFactory</code> object and is not an
//     *                                  instance of all the named classes in <code>clazzes</code>. <li>
//     *                                  <code>properties</code> contains case variants of the same key
//     *                                  name.
//     *                                  </ul>
//     * @throws SecurityException        If the caller does not have the
//     *                                  <code>ServicePermission</code> to register the service for all
//     *                                  the named classes and the Java Runtime Environment supports
//     *                                  permissions.
//     * @throws IllegalStateException    If this BundleContext is no longer valid.
//     * @see ServiceRegistration
//     * @see ServiceFactory
//     */
//    ServiceRegistration registerService(String[] clazzes, Object service, Dictionary<String, ?> properties);

//    /**
//     * Registers the specified service object with the specified properties
//     * under the specified class name with the Framework.
//     * <p>
//     * <p>
//     * This method is otherwise identical to
//     * {@link #registerService(String[], Object, Dictionary)} and is provided as
//     * a convenience when <code>service</code> will only be registered under a
//     * single class name. Note that even in this case the value of the service's
//     * {@link Constants#OBJECTCLASS} property will be an array of string, rather
//     * than just a single string.
//     *
//     * @param clazz      The class name under which the service can be located.
//     * @param service    The service object or a <code>ServiceFactory</code>
//     *                   object.
//     * @param properties The properties for this service.
//     * @return A <code>ServiceRegistration</code> object for use by the bundle
//     * registering the service to update the service's properties or to
//     * unregister the service.
//     * @throws IllegalStateException If this BundleContext is no longer valid.
//     * @see #registerService(String[], Object, Dictionary)
//     */
//    ServiceRegistration registerService(String clazz, Object service, Dictionary<String, ?> properties);
}
