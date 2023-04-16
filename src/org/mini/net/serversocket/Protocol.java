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


package org.mini.net.serversocket;

import com.sun.cldc.io.ConnectionBaseInterface;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;


/*
 * Note: Since this class references the TCP socket protocol class that
 * extends the NetworkConnectionBaseClass. The native networking will be
 * initialized when this class loads if needed without extending
 * NetworkConnectionBase.
 */

/**
 * StreamConnectionNotifier to the TCP Server Socket API.
 *
 * @author Nik Shaylor
 * @version 1.0 10/08/99
 */
public class Protocol implements ConnectionBaseInterface, ServerSocketConnection {

    /**
     * Socket object used by native code, for now must be the first field.
     */
    ServerSocket serverSocket;

    /**
     * Flag to indicate connection is currently open.
     */
    boolean connectionOpen = false;

    int port;

    String ip;

    @Override
    public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
        if (!name.startsWith("//")) {
            throw new IOException( /* #ifdef VERBOSE_EXCEPTIONS */ /// skipped                       "bad socket connection name: " + name
                    /* #endif */);
        }
        int i = name.indexOf(':');
        if (i < 0) {
            throw new IOException( /* #ifdef VERBOSE_EXCEPTIONS */ /// skipped                       "bad socket connection name: port missing"
                    /* #endif */);
        }
        String hostname = name.substring(2, i);
        int port;
        try {
            port = Integer.parseInt(name.substring(i + 1));
        } catch (NumberFormatException e) {
            throw new IOException( /* #ifdef VERBOSE_EXCEPTIONS */ /// skipped                       "bad socket connection name: bad port"
                    /* #endif */);
        }
        // cstring is always NUL terminated (note the extra byte allocated).
        // This avoids awkward char array manipulation in C code.
        byte cstring[] = new byte[hostname.length() + 1];
        for (int n = 0; n < hostname.length(); n++) {
            cstring[n] = (byte) (hostname.charAt(n));
        }
        serverSocket = new ServerSocket();
        if (serverSocket == null) {
            throw new IOException( /* #ifdef VERBOSE_EXCEPTIONS */ /// skipped                       "connection failed: error = " + errorCode
                    /* #endif */);
        }
        serverSocket.bind(new InetSocketAddress(hostname, port));
        this.connectionOpen = true;
        return this;
    }

    /**
     * Checks if the connection is open.
     *
     * @throws IOException is thrown, if the stream is not open
     */
    void ensureOpen() throws IOException {
        if (!connectionOpen) {
            throw new IOException("Connection closed");
        }
    }

    /**
     * Returns a connection that represents a server side socket connection.
     * <p>
     * Polling the native code is done here to allow for simple asynchronous
     * native code to be written. Not all implementations work this way (they
     * block in the native code) but the same Java code works for both.
     *
     * @return a socket to communicate with a client.
     * @throws IOException if an I/O error occurs when creating the input
     *                     stream
     */
    synchronized public SocketConnection accept()
            throws IOException {

        org.mini.net.socket.Protocol con;

        ensureOpen();

        while (true) {
            Socket clt_handle = serverSocket.accept();
            if (clt_handle != null) {
                con = new org.mini.net.socket.Protocol();
                con.open(clt_handle, Connector.READ_WRITE);
                break;
            } else {
                throw new IOException("accept error, maybe listen() before accept()");
            }
            /* Wait a while for I/O to become ready */
            //Waiter.waitForIO(); 
        }

        return con;
    }

    /**
     * Gets the local address to which the socket is bound.
     *
     * <p>
     * The host address(IP number) that can be used to connect to this end of
     * the socket connection from an external system. Since IP addresses may be
     * dynamically assigned, a remote application will need to be robust in the
     * face of IP number reasssignment.</P>
     * <p>
     * The local hostname (if available) can be accessed from
     * <code> System.getProperty("microedition.hostname")</code>
     * </P>
     *
     * @return the local address to which the socket is bound
     * @throws IOException if the connection was closed
     * @see ServerSocketConnection
     */
    public String getLocalAddress() throws IOException {
        ensureOpen();
        return ip + ":" + port;
    }

    /**
     * Returns the local port to which this socket is bound.
     *
     * @return the local port number to which this socket is connected
     * @throws IOException if the connection was closed
     * @see ServerSocketConnection
     */
    public int getLocalPort() throws IOException {
        ensureOpen();
        return port;
    }

    /**
     * Closes the connection, accesses the handle field.
     *
     * @throws IOException if an I/O error occurs when closing the connection
     */
    public void close() throws IOException {
        if (connectionOpen) {
            serverSocket.close();
            connectionOpen = false;
        }
    }

}
