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

/**
 * A class representing a persistent connection pool that is used by the http
 * connection class to store persistent connections. Stream Connection 
 * Element are stored in the internal vector located in this class.
 * Each stream connection element is marked when either in use or
 * not. As new connections are requested - the current connections in the pool
 * are searched for a match and inactivity.
 *
 * <p> There is a maximum number of simultanious connections that can be
 * in the pool at any one time. If for some reason there are no matching
 * connections available - a new one may be created (as long as it does not
 * exceed the maximum). If there number of connections in the pool exceeds 
 * the maximum - an adjustment is made to delete unused connections. Once
 * that happens (if at all) a new connection is created. If it can not create
 * a new connection an exception is raised. Once a connection is close down
 * a connection must be returned to the pool as inactive for another use.
 *
 * <p> Each individual stream connection stream element (or container) 
 * includes a in-use flag. Once a connection has been taken from the pool its
 * in-use flag is set to (true) and once that is closed its set to (false).
 * Once the connection stream element is (false) its available for reuse.
 *
 */

import javax.microedition.io.StreamConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Vector;


/**
 * This class implements the necessary functionality for the HTTP persistent
 * connection pool. This class contains individual http connection elements
 * (or containers) including whether or/not a that particular connection is
 * active. If a connection becomes inactive - its is available for reuse by
 * another http connection session or the same one. This reduces the connection
 * time for each supsequent connection.
 *
 * @author  Kerry Finn
 * @version 1.0
 *
 */
class StreamConnectionPool {
    /** How long a connection can linger after its last use. */
    private long m_connectionLingerTime;
    /** internal connection hash table */
    private Vector m_connections;
    /** maximum connections */
    private int m_max_connections;

    /**
     * Create a new instance of this class.
     * We are initially unconnected this will create the
     * first connection.
     *
     * @param number_of_connections initial number of connections 
     *       must greater than zero.
     * @param connectionLingerTime how many millseconds a connection should
     *       stay in the pool after its last use
     */
    StreamConnectionPool(int number_of_connections,
                         long connectionLingerTime) {
        this.m_max_connections = number_of_connections;
        this.m_connectionLingerTime = connectionLingerTime;
        m_connections = new Vector(m_max_connections);
    }
    
    /**
     * Tries to add a reuseable connection to the connection pool.
     * Replace any not in use connections to the same host and port.
     * Will not add to the same host and port in use. Will replace
     * oldest not in use element (if any) if the pool is full.
     * 
     * @param p_protocol            The protocol for the connection
     * @param p_host                The Hostname for the connection
     * @param p_port                The port number for the connection
     * @param sc                    The base stream connection
     * @param dos                   The data output stream from the base
     *                                connection
     * @param dis                   The data input stream from the base
     *                                connection
     *
     * @return true if the connection was added, otherwise false
     */
    synchronized boolean add(String p_protocol,
            String p_host, int p_port, StreamConnection sc,
            DataOutputStream dos, DataInputStream dis) {

        StreamConnectionElement oldestNotInUse = null;

        // find the last unused element
        Enumeration cons = m_connections.elements();
        while (cons.hasMoreElements()) {
            StreamConnectionElement sce =
                (StreamConnectionElement)cons.nextElement();

            if (sce.m_in_use) {
                if (p_host.equals(sce.m_host) && p_port == sce.m_port) {
                    return false;
                }

                continue;
            }

            // if the connection is a duplicate, delete it
            if (p_host.equals(sce.m_host) && p_port == sce.m_port) {
                // no protocol duplicates on host and port
                sce.close();
                m_connections.removeElement(sce);
                break;
            } else {
                if (oldestNotInUse == null ||
                    sce.m_time < oldestNotInUse.m_time) {
                    // save the oldest not in use, it may be removed later
                    oldestNotInUse = sce;
                }
            }
        }

        /*
         * first check and see if the maximum number of connections
         * has been reached - if so delete the first one in the list (FIFO)
         *   or
         * if this port and host are already in the pool.
         */
        if (m_connections.size() >= m_max_connections) {
            if (oldestNotInUse == null) {
                return false;
            }

            oldestNotInUse.close();
            m_connections.removeElement(oldestNotInUse);
        }
     
        m_connections.addElement(new StreamConnectionElement(p_protocol,
                          p_host, p_port, sc, dos, dis));
        return true;
    }
    
    /**
     * Close connection and remove an instance of the stream connection
     * element from the connection pool.
     *
     * @param sce                 The stream connection element to remove
     */
    synchronized void remove(StreamConnectionElement sce) {
	sce.close();
        m_connections.removeElement(sce);
    }
    
    /**
     * get an available connection and set the boolean flag to 
     * true (unavailable) in the connection pool.
     * Also removes any stale connections, since this method gets
     * called more than add or remove.
     *
     * @param p_protocol            The protocol for the connection
     * @param p_host                The Hostname for the connection
     * @param p_port                The port number for the connection
     *
     * @return                      A stream connection element or
     *                              null if not found
     */
    synchronized StreamConnectionElement get(String p_protocol, String p_host,
            int p_port) {

        StreamConnectionElement result = null;
        long c_time = System.currentTimeMillis();

        Enumeration cons = m_connections.elements();
        while (cons.hasMoreElements()) {
            StreamConnectionElement sce =
                (StreamConnectionElement)cons.nextElement();
            
            if ((c_time - sce.m_time) > m_connectionLingerTime) {
                if (!sce.m_in_use) {
                    sce.close();
                } else {
                    // signal returnToUse() to close
                    sce.m_removed = true;
                }

                m_connections.removeElement(sce);
                continue;
            }

            if (p_host.equals(sce.m_host) && p_port == sce.m_port &&
                    p_protocol.equals(sce.m_protocol) && !sce.m_in_use) {
                result = sce;

                // do not break out so old connections can be removed
                continue;
            }
        }

        if (result != null) {
            result.m_in_use = true;
        }

        return result;
    }

    /**
     * Return an instance of the stream connection element to the 
     * connection pool so it can be reused. It is done in the method
     * so it can be synchronized with the get method.
     *
     * @param returned            The stream connection element to return
     */
    synchronized void returnForReuse(StreamConnectionElement returned) {
        returned.m_in_use = false;

        if (returned.m_removed) {
            // the connection was out too long
            returned.close();
            return;
        }

        returned.m_time = System.currentTimeMillis();
    }
}
