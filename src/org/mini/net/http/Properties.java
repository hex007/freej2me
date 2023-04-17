/*
 * Copyright (c) 2003, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */



package org.mini.net.http;

import java.util.Vector;

/**
 * The <code>Properties</code> class represents a persistent set of
 * properties. Each key and its corresponding value in
 * the property list is a string.
 * <p>
 */
public class Properties {
    /** An appropriate initial size for storage vectors (10). */
    private static int INITIAL_SIZE = 10;
    
    /** A vector of property keys. */
    private Vector keys = null;
    /** A vector of property values. */
    private Vector vals = null;
    
    /**
     * Constructor - creates an empty property list.
     */
    public Properties() {
	keys = new Vector(INITIAL_SIZE);
	vals = new Vector(INITIAL_SIZE);
    }

    /**
     * Store multiple key:value pair.  Provided for parallelism with the 
     * <tt>getProperty</tt> method. Enforces use of strings for 
     * property keys and values. 
     *
     * @param key the key to be placed into this property list.
     * @param value the value corresponding to <tt>key</tt>.
     * @see #getProperty
     */
    public synchronized void addProperty(String key, 
                                         String value) {
	keys.addElement(key);
        vals.addElement(value);

        return;
    }

    /**
     * Store a single key:value pair.  Provided for parallelism with the 
     * <tt>getProperty</tt> method. Enforces use of strings for 
     * property keys and values.  If a key already exists in storage,
     * the value corresponing to that key will be replaced and returned.
     *
     * @param key the key to be placed into this property list.
     * @param value the value corresponding to <tt>key</tt>.
     * @return if the new property value replaces an existing one, the old 
     * value is returned.  otherwise, null is returned.
     * @see #getProperty
     * @see #removeProperty
     */
    public synchronized String setProperty(String key, 
                                           String value) {
	
	int idx = keys.indexOf(key);

	String rv = null;
	if (idx == -1) {    // If I don't have this, add it and return null
	    keys.addElement(key);
	    vals.addElement(value);    
	} else {	    // Else replace it and return the old one.
	    rv = (String)vals.elementAt(idx);
	    vals.setElementAt(value, idx);
	}
        return rv;
    }

    /**
     * Replace the value of the property at the given index.
     *
     * @param index 0 based index of a property
     * @param value the new value for the property at <tt>index</tt>.
     *
     * @return previous value
     *
     * @exception IndexOutOfBoundsException if the index is out of bounds
     */
    public synchronized String setPropertyAt(int index, String value) {
        String rv = (String)vals.elementAt(index);

        vals.setElementAt(value, index);

        return rv;
    }

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns <code>null</code> if the property is not found.
     *
     * @param   key   the property key.
     * @return  the value in this property list with the specified key value.
     * @exception NullPointerException is thrown if key is <code>null</code>.
     * @see     #setProperty
     * @see     #removeProperty
     */
    public String getProperty(String key) {
	if (key == null) {
	    throw new NullPointerException();
	}

	int idx = keys.indexOf(key);
	String rv = null;
	if (idx > -1) {
	    rv = (String)vals.elementAt(idx);
	}
	return rv;
    }

    /**
     * Gets a property value by index. Used by the JadWriter as part of the
     * JAD Tool.
     *
     * @param index 0 based index of a property
     * @return  the value of the property with the specified index.
     * @exception ArrayIndexOutOfBoundsException
     *     if an invalid index was given.
     */
    public String getValueAt(int index) {
        return (String)vals.elementAt(index);
    }

    /**
     * Gets a property key by index. Used by the JadWriter as part of the
     * JAD Tool.
     *
     * @param index 0 based index of a property
     * @return  the key of the property with the specified index.
     * @exception ArrayIndexOutOfBoundsException
     *     if an invalid index was given.
     */
    public String getKeyAt(int index) {
        return (String)keys.elementAt(index);
    }

    /**
     * Gets the number of properties.
     *
     * @return  number of properties
     */
    public int size() {
        return keys.size();
    }

    /**
     * Removes a property (key:value pair) from the property
     * list based on the key string.
     *
     * @param key the key to be removed from the property list. 
     * @return  the element associated with the key.
     * @see #setProperty
     * @see #getProperty
     */
    public synchronized String removeProperty(String key) {
	int idx = keys.indexOf(key);
	String rv = null;
	if (idx > -1) {
	    rv = (String)vals.elementAt(idx);
	    keys.removeElementAt(idx);
	    vals.removeElementAt(idx);
	}
	return rv;
    }

}
