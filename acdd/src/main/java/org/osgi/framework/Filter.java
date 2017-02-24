/*
 * ACDD Project
 * file Filter.java  is  part of ACCD
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

import java.util.Dictionary;

/**
 * An RFC 1960-based Filter.
 * <p>
 * <code>Filter</code>s can be created by calling
 * {@link BundleContext#createFilter}with
 * a filter string.
 * <p>
 * A <code>Filter</code> can be used numerous times to determine if the match
 * argument matches the filter string that was used to create the
 * <code>Filter</code>.
 * <p>
 * Some examples of LDAP filters are:
 * <p>
 * <pre>
 *  &quot;(cn=Babs Jensen)&quot;
 *  &quot;(!(cn=Tim Howes))&quot;
 *  &quot;(&amp;(&quot; + Constants.OBJECTCLASS + &quot;=Person)(|(sn=Jensen)(cn=Babs J*)))&quot;
 *  &quot;(o=univ*of*mich*)&quot;
 * </pre>
 *
 * @version $Revision: 6860 $
 * @see "Core Specification, section 5.5, for a description of the filter string syntax."
 * @since 1.1
 */
public interface Filter {
//    /**
//     * Filter using a service's properties.
//     * <p>
//     * This <code>Filter</code> is executed using the keys and values of the
//     * referenced service's properties. The keys are case insensitively matched
//     * with this <code>Filter</code>.
//     *
//     * @param reference The reference to the service whose properties are used
//     *                  in the match.
//     * @return <code>true</code> if the service's properties match this
//     * <code>Filter</code>; <code>false</code> otherwise.
//     */
//    boolean match(ServiceReference<?> reference);

    /**
     * Filter using a <code>Dictionary</code>. This <code>Filter</code> is
     * executed using the specified <code>Dictionary</code>'s keys and values.
     * The keys are case insensitively matched with this <code>Filter</code>.
     *
     * @param dictionary The <code>Dictionary</code> whose keys are used in the
     *                   match.
     * @return <code>true</code> if the <code>Dictionary</code>'s keys and
     * values match this filter; <code>false</code> otherwise.
     * @throws IllegalArgumentException If <code>dictionary</code> contains case
     *                                  variants of the same key name.
     */
    boolean match(Dictionary<String, ?> dictionary);

    /**
     * Returns this <code>Filter</code>'s filter string.
     * <p>
     * The filter string is normalized by removing whitespace which does not
     * affect the meaning of the filter.
     *
     * @return This <code>Filter</code>'s filter string.
     */
    @Override
    String toString();

    /**
     * Compares this <code>Filter</code> to another <code>Filter</code>.
     * <p>
     * <p>
     * This method returns the result of calling
     * <code>this.toString().equals(obj.toString())</code>.
     *
     * @param obj The object to compare against this <code>Filter</code>.
     * @return If the other object is a <code>Filter</code> object, then returns
     * the result of calling
     * <code>this.toString().equals(obj.toString())</code>;
     * <code>false</code> otherwise.
     */
    @Override
    boolean equals(Object obj);

    /**
     * Returns the hashCode for this <code>Filter</code>.
     * <p>
     * <p>
     * This method returns the result of calling
     * <code>this.toString().hashCode()</code>.
     *
     * @return The hashCode of this <code>Filter</code>.
     */
    @Override
    int hashCode();


}
