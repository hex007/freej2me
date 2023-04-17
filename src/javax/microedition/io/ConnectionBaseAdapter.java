/*
 *
 * Copyright  1990-2008 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 *
 */




package javax.microedition.io;


import com.sun.cldc.io.ConnectionBaseInterface;

import java.io.*;



/**
 * Protocol classes extend this class to gain some of the common functionality
 * needed to implement a CLDC Generic Connection.
 * <p>
 * The common functionality includes:</p>
 * <ul>
 * <li>Supplies the input and output stream classes for a StreamConnection</li>
 * <li>Limits the number of streams opened according to mode, but the limit
 * can be overridden. Read-write allows 1 input and 1 output, write-only
 * allows 1 output, read-only allows 1 input</li>
 * <li>Only "disconnects" when the connection and all streams are closed</li>
 * <li>Throws I/O exceptions when used after being closed</li>
 * <li>Provides a more efficient implementation of
 * {@link InputStream#read(byte[], int, int)}, which is called by
 * {@link InputStream#read()}
 * <li>Provides a more efficient implementation of
 * {@link OutputStream#write(byte[], int, int)}, which is called by
 * {@link OutputStream#write(int)}
 * </ul>
 * <p align="center">
 * <b>Class Relationship Diagram</b></p>
 * <p align="center">
 * <img src="doc-files/ConnectionBaseAdapter.gif" border=0></p>
 *
 * @author Stephen Flores
 * @version 3.0 9/1/2000
 */
public abstract class ConnectionBaseAdapter implements ConnectionBaseInterface,
        StreamConnection {

    /**
     * Flag indicating if the connection is open.
     */
    protected boolean connectionOpen = false;
    /**
     * Number of input streams that were opened.
     */
    protected int iStreams = 0;
    /**
     * Maximum number of open input streams. Set this
     * to zero to prevent openInputStream from giving out a stream in
     * write-only mode.
     */
    protected int maxIStreams = 1;
    /**
     * Number of output streams were opened.
     */
    protected int oStreams = 0;
    /**
     * Maximum number of output streams. Set this
     * to zero to prevent openOutputStream from giving out a stream in
     * read-only mode.
     */
    protected int maxOStreams = 1;
    /**
     * The security permission required by a subclass. -1 for none.
     */
    protected int requiredPermission = -1;
    /**
     * Protocol name of subclass to prefix name of resource, can be null.
     */
    protected String protocol;
    /**
     * The subclass needs know that the permission occured.
     */
    private boolean permissionChecked;

    /**
     * Lets a subclass know that the security check was done.
     * Called by the connect method of subclasses that require permission.
     * <p>
     * This method is needed since the security check must be put off
     * until connection since the name of the resource must be
     * presented to the user in the permission question.
     * An attacker could directly construct a in a subclass of the
     * protocol and then call its connect method. A subclass can detect
     * this attack by calling this method.
     *
     * @throws SecurityException if the check did not happen.
     */
    protected void verifyPermissionCheck() {
        if (permissionChecked) {
            return;
        }

        throw new SecurityException("The permission check was bypassed");
    }

    /**
     * Check for required permission and open a connection to a target.
     *
     * @param name     URL for the connection, without the
     *                 without the protocol part
     * @param mode     I/O access mode, see {@link Connector}
     * @param timeouts flag to indicate that the caller
     *                 wants timeout exceptions
     * @return this Connection object
     * @throws IllegalArgumentException    If a parameter is invalid.
     * @throws ConnectionNotFoundException If the connection cannot
     *                                     be found.
     * @throws IOException                 If some other kind of I/O error occurs.
     */
    public Connection openPrim(String name, int mode, boolean timeouts)
            throws IOException {
        permissionChecked = true;
        checkForPermission(name);

        switch (mode) {
            case Connector.READ:
            case Connector.WRITE:
            case Connector.READ_WRITE:
                break;

            default:
                throw new IllegalArgumentException("Illegal mode");
        }

        connect(name, mode, timeouts);
        connectionOpen = true;
        return this;
    }

    /**
     * Check for the required permission, if needed.
     *
     * @param name name of resource to insert into the permission question
     * @throws InterruptedIOException if another thread interrupts the
     *                                calling thread while this method is waiting to preempt the
     *                                display.
     */
    public void checkForPermission(String name) throws InterruptedIOException {

        if (permissionChecked) {
            return;
        }

        permissionChecked = true;

    }

    /**
     * Check for required permission and open a connection to a target.
     * This method can be used with permissions greater than
     * the current MIDlet suite. Assume read/write and no timeouts.
     *
     * @param name URL for the connection, without the
     *             without the protocol part
     * @return this Connection object
     * @throws IllegalArgumentException    If a parameter is invalid.
     * @throws ConnectionNotFoundException If the connection cannot
     *                                     be found.
     * @throws IOException                 If some other kind of I/O error occurs.
     */
    public Connection openPrim(String name)
            throws IOException {
        return openPrim(name, Connector.READ_WRITE, false);
    }

    /**
     * Returns an input stream.
     *
     * @return an input stream for writing bytes to this port.
     * @throws IOException if an I/O error occurs when creating the
     *                     output stream.
     */
    public InputStream openInputStream() throws IOException {
        InputStream i;

        ensureOpen();

        if (maxIStreams == 0) {
            throw new IOException("no more input streams available");
        }

        i = new BaseInputStream(this);
        maxIStreams--;
        iStreams++;
        return i;
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
     * Returns an output stream.
     *
     * @return an output stream for writing bytes to this port.
     * @throws IOException if an I/O error occurs when creating the
     *                     output stream.
     */
    public OutputStream openOutputStream() throws IOException {
        OutputStream o;

        ensureOpen();

        if (maxOStreams == 0) {
            throw new IOException("no more output streams available");
        }

        o = new BaseOutputStream(this);
        maxOStreams--;
        oStreams++;
        return o;
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

    /**
     * Close the connection.
     *
     * @throws IOException if an I/O error occurs when closing the
     *                     connection.
     */
    public void close() throws IOException {
        if (connectionOpen) {
            connectionOpen = false;
            closeCommon();
        }
    }

    /**
     * Called once by each child input stream.
     * If the input stream is marked open, it will be marked closed and
     * the if the connection and output stream are closed the disconnect
     * method will be called.
     *
     * @throws IOException if the subclass throws one
     */
    protected void closeInputStream() throws IOException {
        iStreams--;
        closeCommon();
    }

    /**
     * Called once by each child output stream.
     * If the output stream is marked open, it will be marked closed and
     * the if the connection and input stream are closed the disconnect
     * method will be called.
     *
     * @throws IOException if the subclass throws one
     */
    protected void closeOutputStream() throws IOException {
        oStreams--;
        closeCommon();
    }

    /**
     * Disconnect if the connection and all the streams and the closed.
     *
     * @throws IOException if an I/O error occurs when closing the
     *                     connection.
     */
    void closeCommon() throws IOException {
        if (!connectionOpen && iStreams == 0 && oStreams == 0) {
            disconnect();
        }
    }

    /**
     * Check if the connection is open.
     *
     * @throws IOException is thrown, if the stream is not open.
     */
    protected void ensureOpen() throws IOException {
        if (!connectionOpen) {
            throw new IOException("Connection closed");
        }
    }

    /**
     * Connect to a target.
     *
     * @param name     URL for the connection, without the protocol
     *                 part
     * @param mode     I/O access mode, see {@link Connector}
     * @param timeouts flag to indicate that the called wants
     *                 timeout exceptions
     * @throws IllegalArgumentException    If a parameter is invalid.
     * @throws ConnectionNotFoundException If the connection cannot be
     *                                     found.
     * @throws IOException                 If some other kind of I/O error occurs.
     */
    protected abstract void connect(String name, int mode, boolean timeouts)
            throws IOException;

    /**
     * Free up the connection resources.
     *
     * @throws IOException if an I/O error occurs.
     */
    protected abstract void disconnect() throws IOException;

    /**
     * Reads up to <code>len</code> bytes of data from the input stream into
     * an array of bytes, blocks until at least one byte is available.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset in array <code>b</code>
     *            at which the data is written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or
     * <code>-1</code> if there is no more data because the end of
     * the stream has been reached.
     * @throws IOException if an I/O error occurs.
     */
    protected abstract int readBytes(byte b[], int off, int len)
            throws IOException;

    /**
     * Returns the number of bytes that can be read (or skipped over) from
     * this input stream without blocking by the next caller of a method for
     * this input stream.  The next caller might be the same thread or
     * another thread. This classes implementation always returns
     * <code>0</code>. It is up to subclasses to override this method.
     *
     * @return the number of bytes that can be read from this input stream
     * without blocking.
     * @throws IOException if an I/O error occurs.
     */
    public int available() throws IOException {
        return 0;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this output stream.
     * <p>
     * Polling the native code is done here to allow for simple
     * asynchronous native code to be written. Not all implementations
     * work this way (they block in the native code) but the same
     * Java code works for both.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @return number of bytes written
     * @throws IOException if an I/O error occurs. In particular,
     *                     an <code>IOException</code> is thrown if the output
     *                     stream is closed.
     */
    protected abstract int writeBytes(byte b[], int off, int len)
            throws IOException;

    /**
     * Forces any buffered output bytes to be written out.
     * The general contract of <code>flush</code> is
     * that calling it is an indication that, if any bytes previously
     * written that have been buffered by the connection,
     * should immediately be written to their intended destination.
     * <p>
     * The <code>flush</code> method of <code>ConnectionBaseAdapter</code>
     * does nothing.
     *
     * @throws IOException if an I/O error occurs.
     */
    protected void flush() throws IOException {
    }
}

/**
 * Input stream for the connection
 */
class BaseInputStream extends InputStream {

    /**
     * Pointer to the connection
     */
    private ConnectionBaseAdapter parent;

    /**
     * Buffer for single char reads
     */
    byte[] buf = new byte[1];

    /**
     * Constructs a BaseInputStream for a ConnectionBaseAdapter.
     *
     * @param parent pointer to the connection object
     * @throws IOException if an I/O error occurs.
     */
    BaseInputStream(ConnectionBaseAdapter parent) throws IOException {
        this.parent = parent;
    }

    /**
     * Check the stream is open
     *
     * @throws InterruptedIOException if it is not.
     */
    private void ensureOpen() throws InterruptedIOException {
        if (parent == null) {
            throw new InterruptedIOException("Stream closed");
        }
    }

    /**
     * Returns the number of bytes that can be read (or skipped over) from
     * this input stream without blocking by the next caller of a method for
     * this input stream.  The next caller might be the same thread or
     * another thread.
     *
     * <p>The <code>available</code> method always returns <code>0</code> if
     * {@link ConnectionBaseAdapter#available()} is
     * not overridden by the subclass.
     *
     * @return the number of bytes that can be read from this input stream
     * without blocking.
     * @throws IOException if an I/O error occurs.
     */
    public int available() throws IOException {

        ensureOpen();

        return parent.available();
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     * stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    public int read() throws IOException {
        if (read(buf, 0, 1) > 0) {
            return (buf[0] & 0xFF);
        }

        return -1;
    }

    /**
     * Reads up to <code>len</code> bytes of data from the input stream into
     * an array of bytes.  An attempt is made to read as many as
     * <code>len</code> bytes, but a smaller number may be read, possibly
     * zero. The number of bytes actually read is returned as an integer.
     *
     * <p> This method blocks until input data is available, end of file is
     * detected, or an exception is thrown.
     *
     * <p> If <code>b</code> is <code>null</code>, a
     * <code>NullPointerException</code> is thrown.
     *
     * <p> If <code>off</code> is negative, or <code>len</code> is negative, or
     * <code>off+len</code> is greater than the length of the array
     * <code>b</code>, then an <code>IndexOutOfBoundsException</code> is
     * thrown.
     *
     * <p> If <code>len</code> is zero, then no bytes are read and
     * <code>0</code> is returned; otherwise, there is an attempt to read at
     * least one byte. If no byte is available because the stream is at end of
     * file, the value <code>-1</code> is returned; otherwise, at least one
     * byte is read and stored into <code>b</code>.
     *
     * <p> The first byte read is stored into element <code>b[off]</code>, the
     * next one into <code>b[off+1]</code>, and so on. The number of bytes read
     * is, at most, equal to <code>len</code>. Let <i>k</i> be the number of
     * bytes actually read; these bytes will be stored in elements
     * <code>b[off]</code> through <code>b[off+</code><i>k</i><code>-1]</code>,
     * leaving elements <code>b[off+</code><i>k</i><code>]</code> through
     * <code>b[off+len-1]</code> unaffected.
     *
     * <p> In every case, elements <code>b[0]</code> through
     * <code>b[off]</code> and elements <code>b[off+len]</code> through
     * <code>b[b.length-1]</code> are unaffected.
     *
     * <p> If the first byte cannot be read for any reason other than end of
     * file, then an <code>IOException</code> is thrown. In particular, an
     * <code>IOException</code> is thrown if the input stream has been closed.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset in array <code>b</code>
     *            at which the data is written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or
     * <code>-1</code> if there is no more data because the end of
     * the stream has been reached.
     * @throws IOException if an I/O error occurs.
     * @see InputStream#read()
     */
    public int read(byte b[], int off, int len) throws IOException {
        int test;

        ensureOpen();

        if (len == 0) {
            return 0;
        }

        /*
         * test the parameters so the subclass will not have to.
         * this will avoid crashes in the native code
         */
        test = b[off] + b[len - 1] + b[off + len - 1];

        return parent.readBytes(b, off, len);
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        if (parent != null) {
            parent.closeInputStream();
            parent = null;
        }
    }
}


/**
 * Output stream for the connection
 */
class BaseOutputStream extends OutputStream {

    /**
     * Pointer to the connection
     */
    ConnectionBaseAdapter parent;

    /**
     * Buffer for single char writes
     */
    byte[] buf = new byte[1];

    /**
     * Constructs a BaseOutputStream for an ConnectionBaseAdapter.
     *
     * @param p parent connection
     */
    BaseOutputStream(ConnectionBaseAdapter p) {
        parent = p;
    }

    /**
     * Check the stream is open
     *
     * @throws InterruptedIOException if it is not.
     */
    private void ensureOpen() throws InterruptedIOException {
        if (parent == null) {
            throw new InterruptedIOException("Stream closed");
        }
    }

    /**
     * Writes the specified byte to this output stream. The general
     * contract for <code>write</code> is that one byte is written
     * to the output stream. The byte to be written is the eight
     * low-order bits of the argument <code>b</code>. The 24
     * high-order bits of <code>b</code> are ignored.
     *
     * @param b the <code>byte</code>.
     * @throws IOException if an I/O error occurs. In particular,
     *                     an <code>IOException</code> may be thrown if the
     *                     output stream has been closed.
     */
    public void write(int b) throws IOException {
        buf[0] = (byte) b;
        write(buf, 0, 1);
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this output stream.
     * The general contract for <code>write(b, off, len)</code> is that
     * some of the bytes in the array <code>b</code> are written to the
     * output stream in order; element <code>b[off]</code> is the first
     * byte written and <code>b[off+len-1]</code> is the last byte written
     * by this operation.
     * <p>
     * If <code>b</code> is <code>null</code>, a
     * <code>NullPointerException</code> is thrown.
     * <p>
     * If <code>off</code> is negative, or <code>len</code> is negative, or
     * <code>off+len</code> is greater than the length of the array
     * <code>b</code>, then an <tt>IndexOutOfBoundsException</tt> is thrown.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws IOException if an I/O error occurs. In particular,
     *                     an <code>IOException</code> is thrown if the output
     *                     stream is closed.
     */
    public void write(byte b[], int off, int len)
            throws IOException {
        int test;
        int bytesWritten;

        ensureOpen();

        if (len == 0) {
            return;
        }

        /*
         * test the parameters here so subclases do not have to,
         * this will avoid a crash in the native code
         */
        test = b[off] + b[len - 1] + b[off + len - 1];

        /*
         * Polling the native code is done here to allow for simple
         * asynchronous native code to be written. Not all implementations
         * work this way (they block in the native code) but the same
         * Java code works for both.
         */
        for (bytesWritten = 0; ; ) {
            try {
                bytesWritten += parent.writeBytes(b, off + bytesWritten,
                        len - bytesWritten);
            } finally {
                if (parent == null) {
                    throw new InterruptedIOException("Stream closed");
                }
            }

            if (bytesWritten == len) {
                break;
            }

//            GeneralBase.iowait(); 
        }
    }

    /**
     * Flushes this output stream and forces any buffered output bytes
     * to be written out. The general contract of <code>flush</code> is
     * that calling it is an indication that, if any bytes previously
     * written have been buffered by the implementation of the output
     * stream, such bytes should immediately be written to their
     * intended destination.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void flush() throws IOException {
        ensureOpen();
        parent.flush();
    }

    /**
     * Closes this output stream and releases any system resources
     * associated with this stream. The general contract of <code>close</code>
     * is that it closes the output stream. A closed stream cannot perform
     * output operations and cannot be reopened.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        if (parent != null) {
            parent.closeOutputStream();
            parent = null;
        }
    }
}
        
