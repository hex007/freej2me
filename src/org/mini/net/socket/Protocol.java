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


package org.mini.net.socket;

import com.sun.cldc.io.ConnectionBaseInterface;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Connection to the J2ME socket API.
 *
 * @version 1.0 1/16/2000
 */
public class Protocol implements ConnectionBaseInterface, SocketConnection {

    /**
     * Socket object used by native code
     */
//    byte[] handle;
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    /**
     * Access mode
     */
    private int mode;

    /**
     * Open count
     */
    int opens = 0;

    /**
     * Connection open flag
     */
    private boolean copen = false;

    /**
     * Input stream open flag
     */
    protected boolean isopen = false;

    /**
     * Output stream open flag
     */
    protected boolean osopen = false;

    /**
     * Open the connection
     */
    public void open(String name, int mode, boolean timeouts)
            throws IOException {
        throw new RuntimeException( /* #ifdef VERBOSE_EXCEPTIONS */ /// skipped                   "Should not be called"
                /* #endif */);
    }

    /**
     * Open the connection
     *
     * @param name     the target for the connection. It must be in this format:
     *                 "//<name or IP number>:<port number>"
     * @param mode     read/write mode of the connection (currently ignored).
     * @param timeouts A flag to indicate that the called wants timeout
     *                 exceptions (currently ignored).
     */
    public Connection openPrim(String name, int mode, boolean timeouts)
            throws IOException {
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
        socket = new Socket(hostname, port);
        if (socket == null) {

            throw new IOException( /* #ifdef VERBOSE_EXCEPTIONS */ /// skipped                       "connection failed: error = " + errorCode
                    /* #endif */);
        }
        opens++;
        copen = true;
        this.mode = mode;

        openInputStream();
        openOutputStream();
        return this;
    }

    /**
     * Open the connection
     *
     * @param handle an already formed socket handle
     *               <p>
     *               This function is only used by com.sun.cldc.io.j2me.socketserver;
     */
    public void open(Socket handle, int mode) throws IOException {
        this.socket = handle;
        opens++;
        copen = true;
        this.mode = mode;
    }

    /**
     * Ensure connection is open
     */
    void ensureOpen() throws IOException {
        if (!copen) {
            throw new IOException( /* #ifdef VERBOSE_EXCEPTIONS */ /// skipped                       "Connection closed"
                    /* #endif */);
        }
    }

    /**
     * Returns an input stream for this socket.
     *
     * @return an input stream for reading bytes from this socket.
     * @throws IOException if an I/O error occurs when creating the input
     *                     stream.
     */
    synchronized public InputStream openInputStream() throws IOException {
        ensureOpen();
        if ((mode & Connector.READ) == 0) {
            throw new IOException( /* #ifdef VERBOSE_EXCEPTIONS */ /// skipped                       "Connection not open for reading"
                    /* #endif */);
        }
        if (isopen) {
            return inputStream;
        }
        isopen = true;
        inputStream = socket.getInputStream();
        opens++;
        return inputStream;
    }

    /**
     * Returns an output stream for this socket.
     *
     * @return an output stream for writing bytes to this socket.
     * @throws IOException if an I/O error occurs when creating the output
     *                     stream.
     */
    synchronized public OutputStream openOutputStream() throws IOException {
        ensureOpen();
        if ((mode & Connector.WRITE) == 0) {
            throw new IOException( /* #ifdef VERBOSE_EXCEPTIONS */ /// skipped                       "Connection not open for writing"
                    /* #endif */);
        }
        if (osopen) {
            return outputStream;
        }
        osopen = true;
        outputStream = socket.getOutputStream();
        opens++;
        return outputStream;
    }

    /**
     * Close the connection.
     *
     * @throws IOException if an I/O error occurs when closing the
     *                     connection.
     */
    synchronized public void close() throws IOException {
        if (copen) {
            copen = false;
            realClose();
        }
    }

    /**
     * Close the connection.
     *
     * @throws IOException if an I/O error occurs.
     */
    synchronized void realClose() throws IOException {
        if (--opens == 0) {
            socket.close();
        }
    }

    /**
     * Open and return a data input stream for a connection.
     *
     * @return An input stream
     * @throws IOException If an I/O error occurs
     */
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    /**
     * Open and return a data output stream for a connection.
     *
     * @return An input stream
     * @throws IOException If an I/O error occurs
     */
    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(openOutputStream());
    }

    /*
     * A note about readByte()
     *
     * This function will return an unsigned byte, or -1.
     * -1 means that EOF was reached.
     */
    @Override
    public int write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        return len;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int r = inputStream.read(b, off, len);
        return r;
    }

    //    @Override
//    public int available() throws IOException {
//        return available0(handle);
//    }
    boolean nonBlock = false;

    public void setOption(int type, int val, int val2) {

//        if (type == SocketNative.SO_NONBLOCK && val == SocketNative.VAL_NON_BLOCK) {
//            nonBlock = true;
//        }
//        SocketNative.setOption0(handle, type, val, val2);
    }


    public String getLocalAddress() {
        return socket.getLocalAddress().toString();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public String getAddress() {
        return socket.getRemoteSocketAddress().toString();
    }

    public int getPort() {
        return socket.getPort();
    }


    public void setSocketOption(byte option, int value) {
        try {

            switch (option) {
                case DELAY: {
                    break;
                }
                case KEEPALIVE: {
                    socket.setKeepAlive(value == 1);
                    break;
                }
                case LINGER: {
                    socket.setSoLinger(value != 0, value);
                    break;
                }
                case SNDBUF: {
                    socket.setSendBufferSize(value);
                    break;
                }
                case RCVBUF: {
                    socket.setReceiveBufferSize(value);
                    break;
                }
                case TIMEOUT: {
                    socket.setSoTimeout(value);
                    break;
                }
                case NONBLOCK: {
                    socket.setSoTimeout(0);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get current value
     *
     * @param option
     */
    public int getSocketOption(byte option) {
        try {
            switch (option) {
                case DELAY: {
                    break;
                }
                case KEEPALIVE: {
                    return socket.getKeepAlive() ? 1 : 0;
                }
                case LINGER: {
                    return socket.getSoLinger();
                }
                case SNDBUF: {
                    return socket.getSendBufferSize();
                }
                case RCVBUF: {
                    return socket.getReceiveBufferSize();
                }
                case TIMEOUT: {
                    return socket.getSoTimeout();
                }
                case NONBLOCK: {
                    return socket.getSoTimeout() == 0 ? 1 : 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}

