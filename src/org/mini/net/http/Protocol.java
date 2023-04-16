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
 * A class representing a http connection. An http connection consists of stream
 * connection as well as input and output streams for read/write data to and
 * from a web server. This version supports HTTP1.1 persistent connections
 * allowing connects to be shared from a connection pool. This pool and the
 * maximum number of connections can be configured for a particular platform.
 * Proxy connections are also allowed through this interface.
 *
 * <p>
 * The actual connection to the web server does not take place until the
 * application needs an, (1) input stream, (2) flush data, (3)request some
 * header info or closes the connection (with outstanding data in the output
 * stream). Because of this issue the state transition must allow for some
 * flexability to move backwards for WRITE state conditions.
 *
 * <p>
 * Persistent connections are provided through the use of a connection pool that
 * tracks the connect status. There are maximum threshold values defined and
 * these values can be overiden using property key/value pars. The connection
 * pool provides a synchronized interface for managing the maximum configurable
 * connections. Persistent connections will only be supported for HTTP1.1
 * connections - otherwise the connections will be closed and disgarded after
 * its done (HTTP1.0 behavior).
 *
 * <p>
 * This class extends the ConnectionBaseAdapter where Connector type objects
 * (like this) use various features. Output and Input streams are created and
 * managed in the adapter class.
 *
 * <p>
 * The reading and writing of data through the input and output streams are
 * configured, buffered and managed depending on the ability of a platform to
 * read/write on those streams.
 *
 */
import javax.microedition.io.*;
import java.io.*;

/**
 * This class implements the necessary functionality for an HTTP connection.
 */
public class Protocol extends ConnectionBaseAdapter
        implements HttpConnection {

    /**
     * HTTP version string to use with all outgoing HTTP requests.
     */
    protected static final String HTTP_VERSION = "HTTP/1.1";
    /**
     * Where to start the data in the output buffer.
     */
    private static final int HTTP_OUTPUT_DATA_OFFSET = 24;
    /**
     * How must extra room for the chunk terminator.
     */
    private static final int HTTP_OUTPUT_EXTRA_ROOM = 8;

    /**
     * Default size for input buffer.
     */
    private static int inputBufferSize = 256;
    /**
     * Default size for output buffer.
     */
    private static int outputBufferSize = 2048;
    /**
     * How much data can be put in the output buffer.
     */
    private static int outputDataSize;
    /**
     * The "host:port" value to use for HTTP proxied requests.
     */
    private static String http_proxy;
    /**
     * Maximum number of persistent connections.
     */
    private static int maxNumberOfPersistentConnections = 1;
    /**
     * Connection linger time in the pool, default 60 seconds.
     */
    private static long connectionLingerTime = 60000;
    /**
     * Persistent connection pool.
     */
    private static StreamConnectionPool connectionPool;
    /**
     * True if com.sun.midp.io.http.force_non_persistent = true.
     */
    private static boolean nonPersistentFlag;

    /**
     * Get the configuration values for this class.
     */
    static {
        String prop;
        int temp;

        /*
         * Get the proxy here instead of the connector,
         * so when this method subclassed by HTTPS http_proxy will be null
         * and the proxy will not be added into the request.
         */
        http_proxy = System.getProperty("com.sun.midp.io.http.proxy");

        /*
         * if first time intialize the connection pool and create
         * a connection (you could create more)
         */
        maxNumberOfPersistentConnections = 1;

        /*
         * bug#4455443 - allows for configuration options to shut off 
         * the persistent connection feature for http 
         */
        String flag = System.getProperty(
                "com.sun.midp.io.http.force_non_persistent");
        if ((flag != null) && (flag.equals("true"))) {
            nonPersistentFlag = true;
        }

        /*
         * Get the  maximum number of persistent connections
         * from the configuration file.
         */
        prop = System.getProperty(
                "com.sun.midp.io.http.max_persistent_connections");
        if (prop != null) {
            try {
                temp = Integer.parseInt(prop);
                if (temp <= 0) {
                    maxNumberOfPersistentConnections = temp;
                }
            } catch (NumberFormatException nfe) {
                // keep the default
            }
        }

        // Get how long a not in use connection should stay in the pool.
        prop = null;
//        Configuration.getProperty(
//                   "com.sun.midp.io.http.persistent_connection_linger_time");
        if (prop != null) {
            try {
                temp = Integer.parseInt(prop);
                if (temp >= 0) {
                    connectionLingerTime = temp;
                }
            } catch (NumberFormatException nfe) {
                // keep the default
            }
        }

        connectionPool = new StreamConnectionPool(
                maxNumberOfPersistentConnections,
                connectionLingerTime);

        /*
         * Get the  maximum number of persistent connections
         * from the configuration file.
         */
        prop = System.getProperty(
                "com.sun.midp.io.http.inputBufferSize");
        if (prop != null) {
            try {
                temp = Integer.parseInt(prop);
                if (temp <= 0) {
                    inputBufferSize = temp;
                }
            } catch (NumberFormatException nfe) {
                // keep the default
            }
        }

        prop = System.getProperty(
                "com.sun.midp.io.http.outputBufferSize");
        if (prop != null) {
            try {
                temp = Integer.parseInt(prop);
                if (temp <= 0) {
                    outputBufferSize = temp;
                }
            } catch (NumberFormatException nfe) {
                // keep the default
            }
        }

        outputDataSize = outputBufferSize - HTTP_OUTPUT_DATA_OFFSET
                - HTTP_OUTPUT_EXTRA_ROOM;
    }

    /**
     * Saved copy of URL string to be processed.
     */
    protected String saved_url;
    /**
     * Default port number for this protocol.
     */
    protected int default_port;
    /**
     * Parsed Url.
     */
    protected HttpUrl url;
    /**
     * url.host + ":" + url.port.
     */
    protected String hostAndPort;

    /**
     * Numeric code returned from HTTP response header.
     */
    protected int responseCode;
    /**
     * Message string from HTTP response header.
     */
    protected String responseMsg;

    /**
     * Collection of request headers as name/value pairs.
     */
    protected Properties reqProperties;

    /**
     * Collection of response headers as name/value pairs.
     */
    protected Properties headerFields;

    /**
     * HTTP method type for the current request.
     */
    protected String method;

    /*
     * The streams from the underlying socket connection.
     */
    /**
     * Low level socket connection used for the HTTP requests.
     */
    private StreamConnection streamConnection;
    /**
     * Low level socket output stream.
     */
    private DataOutputStream streamOutput;
    /**
     * Low level socket input stream.
     */
    private DataInputStream streamInput;
    /**
     * A shared temporary header buffer.
     */
    private StringBuffer stringbuffer;
    /**
     * HTTP version string set with all incoming HTTP responses.
     */
    private String httpVer = null;
    /**
     * Used when appl calls setRequestProperty("Connection", "close").
     */
    private boolean ConnectionCloseFlag;
    /**
     * Content-Length from response header, or -1 if missing.
     */
    private int contentLength = -1;
    /**
     * Total number of bytes in the current chunk or content-length when data is
     * sent as one big chunk.
     */
    private int chunksize = -1;
    /**
     * Number of bytes read from the stream for non-chunked data or the bytes
     * read from the current chunk.
     */
    private int totalbytesread;
    /**
     * True if Transfer-Encoding: chunkedIn.
     */
    private boolean chunkedIn;
    /**
     * True if Transfer-Encoding: chunkedOut.
     */
    private boolean chunkedOut;
    /**
     * True after the first chunk has been sent.
     */
    private boolean firstChunkSent;
    /**
     * True if the request is being sent.
     */
    private boolean sendingRequest;
    /**
     * True if the entire request has been sent to the server.
     */
    private boolean requestFinished;
    /**
     * True if eof seen.
     */
    private boolean eof;
    /**
     * Internal stream buffer to minimize the number of TCP socket reads.
     */
    private byte[] readbuf;
    /**
     * Number of bytes left in internal input stream buffer.
     */
    private int bytesleft;
    /**
     * Number of bytes read from the internal input stream buffer.
     */
    private int bytesread;
    /**
     * Buffered data output for content length calculation.
     */
    private byte[] writebuf;
    /**
     * Number of bytes of data that need to be written from the buffer.
     */
    private int bytesToWrite;
    /**
     * Collection of "Proxy-" headers as name/value pairs.
     */
    private Properties proxyHeaders = new Properties();
    /**
     * Last handshake error.
     */
    private byte handshakeError;
    /**
     * Holds the state the readBytes call. So if close is called in another
     * thread than the read thread the close will be directly on the stream,
     * instead of putting the connection back in the persistent connection pool,
     * forcing an IOException on the read thread.
     */
    private boolean readInProgress;

    /**
     * Create a new instance of this class and intialize variables. Initially an
     * http connection is unconnected to the network.
     */
    public Protocol() {
        reqProperties = new Properties();
        headerFields = new Properties();
        stringbuffer = new StringBuffer(32);

        method = GET;
        responseCode = -1;
        protocol = "http";
        default_port = 80;

        if (nonPersistentFlag) {
            ConnectionCloseFlag = true;
        }

        readbuf = new byte[inputBufferSize];

    }

    /**
     * Provides the connect() method that sets up the connection, but does not
     * actually connect to the server until there's something to do.
     * <p>
     * Warning: A subclass that implements this method, not call this method and
     * should also implement the disconnect method.
     *
     * @param name The URL for the connection, without the without the protcol
     * part.
     * @param mode The access mode, ignored
     * @param timeouts A flag to indicate that the called wants timeout
     * exceptions, ignored
     *
     * @exception IllegalArgumentException If a parameter is invalid.
     * @exception ConnectionNotFoundException If the connection cannot be found.
     * @exception IOException If some other kind of I/O error occurs.
     */
    protected void connect(String name, int mode, boolean timeouts)
            throws IOException, IllegalArgumentException,
            ConnectionNotFoundException {

        this.saved_url = name;

        url = new HttpUrl(protocol, name);

        if (url.port == -1) {
            url.port = default_port;
        }

        if (url.host == null) {
            throw new IllegalArgumentException("missing host in URL");
        }

        hostAndPort = url.host + ":" + url.port;
    }

    /**
     * Open the input stream if it has not already been opened.
     *
     * @exception IOException is thrown if it has already been opened.
     * @return input stream for the current connection
     */
    public InputStream openInputStream() throws IOException {
        InputStream in;

        /*
         * Call into parent to create input stream passed back to the user
         */
        in = super.openInputStream();

        /*
         * Send a request to the web server if there wasn't one
         * sent already
         */
        sendRequest();

        return in;
    }

    /**
     * Open the output stream if it has not already been opened.
     *
     * @exception IOException is thrown if it has already been opened.
     * @return output stream for the current connection
     */
    public OutputStream openOutputStream() throws IOException {
        OutputStream out;

        /*
         * call into parent to create output stream passed back to the user
         */
        out = super.openOutputStream();

        /*
         * Create a byte array output stream for output buffering
         * once the user calls flush() this gets written to stream
         */
        writebuf = new byte[outputBufferSize];

        return out;
    }

    /**
     * Reads up to <code>len</code> bytes of data from the input stream into an
     * array of bytes. This method reads NonChunked http connection input
     * streams. This method can only be called after the InputStream setup is
     * complete.
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset in array <code>b</code> at which the data is
     * written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or
     * <code>-1</code> if there is no more data because the end of the stream
     * has been reached.
     * @exception IOException if an I/O error occurs.
     */
    protected int readBytes(byte b[], int off, int len)
            throws IOException {

        int rc;

        /*
         * Be consistent about returning EOF once encountered.
         */
        if (eof) {
            return (-1);
        }

        /*
         * The InputStream close behavior will be different if close is called
         * from another thread when reading.
         */
        synchronized (streamInput) {
            readInProgress = true;
        }

        try {
            /*
             * If the http connection is chunked, call the readBytesChunked
             * method
             */
            if (chunkedIn || chunksize > 0) {
                /*
                 * Non-chunked data of known length is treated as one big chunk
                 */
                return readBytesChunked(b, off, len);
            }

            /*
             * Non-chunked unknown length
             */
            if (bytesleft == 0) {
                /*
                 * the internal input stream buffer is empty, read from the
                 * stream
                 */
                if (len >= inputBufferSize) {
                    /*
                     * No need to buffer, if the caller has given a big buffer.
                     */
                    rc = streamInput.read(b, off, len);
                } else {
                    rc = streamInput.read(readbuf, 0, inputBufferSize);
                    bytesleft = rc;
                    bytesread = 0;
                }

                if (rc == -1) {
                    /*
                     * The next call to this method should not read.
                     */
                    eof = true;
                    return -1;
                }

                totalbytesread += rc;

                if (bytesleft == 0) {
                    /*
                     * The data was read directly into the caller's buffer.
                     */
                    return rc;
                }
            }

            rc = readFromBuffer(b, off, len);
            return rc;
        } finally {
            synchronized (streamInput) {
                readInProgress = false;
            }
        }
    }

    /**
     * Reads up to <code>len</code> bytes of data from the internal buffer into
     * an array of bytes.
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset in array <code>b</code> at which the data is
     * written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or
     * <code>-1</code> if there is no more data because the end of the stream
     * has been reached.
     * @exception IOException if an I/O error occurs.
     */
    private int readFromBuffer(byte b[], int off, int len)
            throws IOException {

        /*
         * copy http buffer data into user buffer, then
         * increment and decrement counters
         */
        int rc;

        if (len > bytesleft) {
            rc = bytesleft;
        } else {
            rc = len;
        }

        System.arraycopy(readbuf, bytesread, b, off, rc);

        bytesleft -= rc;
        bytesread += rc;

        return rc;
    }

    /**
     * Returns the number of bytes that can be read (or skipped over) from this
     * input stream without blocking by the next caller of a method for this
     * input stream. The next caller might be the same thread or another thread.
     *
     * @return the number of bytes that can be read from this input stream
     * without blocking.
     * @exception IOException if an I/O error occurs.
     */
    public int available() throws IOException {
        int bytesAvailable;

        /* 
         * Only after all the headers have been processed can
         * an accurate available count be provided.
         */
        if (!requestFinished || eof) {
            return 0;
        }

        /*
         * Regardless of chunked or non-chunked transfers -
         * if data is already buffered return the amount 
         * buffered.
         */
        if (bytesleft > 0) {
            return bytesleft;
        }

        if (chunkedIn && totalbytesread == chunksize) {
            /* 
             * Check if a new chunk size header is available.
             */
            return readChunkSizeNonBlocking();
        }

        /*
         * Otherwise rely on the lower level stream available
         * count for the nonchunked input stream.
         */
        bytesAvailable = streamInput.available();
        if (chunksize <= bytesAvailable) {
            return chunksize;
        }

        return bytesAvailable;
    }

    /**
     * Read a chunk size header into the readLine buffer without blocking. The
     * stringbuffer is populated with characters one at a time. This routine is
     * design so that a partial chunk size header could be read and then
     * completed by a blocking read of the chunk or a subsequent call to
     * available.
     *
     * @return available data that can be read
     */
    int readChunkSizeNonBlocking() throws IOException {
        /*
         * Check the underlying stream to see how many bytes are
         * available. Do not read beyond the available characters,
         * because that would block.
         */
        int len = streamInput.available();

        /* Reset the last character from the current readLine buffer. */
        int sblen = stringbuffer.length();
        char lastchar = '\0';
        if (sblen > 0) {
            lastchar = stringbuffer.charAt(sblen - 1);
        }

        int size = -1;
        /*
         * Loop through the available characters until a full 
         * chunk size header is in the readLine buffer.
         */
        for (; len > 0; len--) {
            char c = (char) streamInput.read();

            if (lastchar == '\r' && c == '\n') {
                // remove the '\r' from the buffer
                stringbuffer.setLength(stringbuffer.length() - 1);

                if (stringbuffer.length() > 0) {
                    // this is a size, not the CRLF at the end of a chunk
                    try {
                        String temp = stringbuffer.toString();
                        int semi = temp.indexOf(';');

                        // skip extensions
                        if (semi > 0) {
                            temp = temp.substring(0, semi);
                        }

                        /*
                         * Reset the string buffer length so readline() will
                         * not parse this line.
                         */
                        stringbuffer.setLength(0);

                        size = Integer.parseInt(temp, 16);
                    } catch (NumberFormatException nfe) {
                        throw new IOException(
                                "invalid chunk size number format");
                    }
                    break;
                }
            } else {
                stringbuffer.append(c);
                lastchar = c;
            }
        }

        if (size < 0) {
            // did not get the size
            return 0;
        }

        /*
         * Update the chunksize and the total bytes that have been
         * read from the chunk. This will trigger the next call to
         * readBytes to refill the buffers as needed.
         */
        chunksize = size;
        if (size == 0) {
            eof = true;
            return 0;
        }

        totalbytesread = 0;

        /*
         * If the full chunk is available, return chunksize,
         * otherwise return the remainder of the available
         * bytes (e.g. partial chunk).
         */
        return (chunksize < len ? chunksize : len);

    }

    /**
     * Reads up to <code>len</code> bytes of data from the input stream into an
     * array of bytes. This method reads Chunked and known length non-chunked
     * http connection input streams. For non-chunked set the field
     * <code>chunkedIn</code> should be false.
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset in array <code>b</code> at which the data is
     * written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or
     * <code>-1</code> if there is no more data because the end of the stream
     * has been reached.
     * @exception IOException if an I/O error occurs.
     */
    protected int readBytesChunked(byte b[], int off, int len)
            throws IOException {

        int rc;

        if (bytesleft == 0) {
            /*
             * the internal input stream buffer is empty, read from the stream
             */

            if (totalbytesread == chunksize) {
                /*
                 * read the end of the chunk and get the size of the
                 * the next if there is one
                 */

                if (!chunkedIn) {
                    /*
                     * non-chucked data is treated as one big chunk so there
                     * is no more data so just return as if there are no
                     * more chunks
                     */
                    eof = true;
                    return -1;
                }

                skipEndOfChunkCRLF();

                chunksize = readChunkSize();
                if (chunksize == 0) {
                    eof = true;

                    /*
                     * REFERENCE: HTTP1.1 document 
                     * SECTION: 3.6.1 Chunked Transfer Coding
                     * in some cases there may be an OPTIONAL trailer
                     * containing entity-header fields. since we don't support
                     * the available() method for TCP socket input streams and
                     * for performance and reuse reasons we do not attempt to
                     * clean up the current connections input stream. 
                     * check readResponseMessage() method in this class for
                     * more details
                     */
                    return -1;
                }

                /*
                 * we have not read any bytes from this new chunk
                 */
                totalbytesread = 0;
            }

            int bytesToRead = chunksize - totalbytesread;

            if (len >= bytesToRead) {

                /*
                 * No need to buffer, if the caller has given a big buffer.
                 */
                rc = streamInput.read(b, off, bytesToRead);

            } else if (len >= inputBufferSize) {
                /*
                 * No need to buffer, if the caller has given a big buffer.
                 */
                rc = streamInput.read(b, off, len);
            } else {
                if (inputBufferSize >= bytesToRead) {
                    rc = streamInput.read(readbuf, 0, bytesToRead);
                } else {
                    rc = streamInput.read(readbuf, 0, inputBufferSize);
                }

                bytesleft = rc;
                bytesread = 0;
            }

            if (rc == -1) {
                /*
                 * Network problem or the wrong length was sent by the server.
                 */
                eof = true;
                throw new IOException("unexpected end of stream");
            }

            totalbytesread += rc;

            if (bytesleft == 0) {
                /*
                 * The data was read directly into the caller's buffer.
                 */
                return rc;
            }
        }

        rc = readFromBuffer(b, off, len);

        return rc;
    }

    /**
     * Read the chunk size from the input. It is a hex length followed by
     * optional headers (ignored). and terminated with CRLF.
     *
     * @return size of the buffered read
     */
    private int readChunkSize() throws IOException {
        int size = -1;

        try {
            String chunk = null;

            try {
                chunk = readLine(streamInput);
            } catch (IOException ioe) {
                /* throw new IOException(ioe.getMessage()); */
            }

            if (chunk == null) {
                throw new IOException("No Chunk Size");
            }

            int i;
            for (i = 0; i < chunk.length(); i++) {
                char ch = chunk.charAt(i);
                if (Character.digit(ch, 16) == -1) {
                    break;
                }
            }

            /* look at extensions?.... */
            size = Integer.parseInt(chunk.substring(0, i), 16);
        } catch (NumberFormatException e) {
            throw new IOException("invalid chunk size number format");
        }

        return size;
    }

    /**
     * Skips the CRLF at the end of each chunk in the InputStream.
     *
     * @exception IOException if the LF half of the ending CRLF is missing.
     */
    private void skipEndOfChunkCRLF() throws IOException {
        int ch;

        if (stringbuffer.length() > 1) {
            /*
             * readChunkSizeNonBlocking does not leave CRLF in the buffer
             * so assume that the ending CRLF has been skipped already
             */
            return;
        }

        // readChunkSizeNonBlocking could have left a \r single in the buffer
        if (stringbuffer.length() == 1) {
            if (stringbuffer.charAt(0) != '\r') {
                // assume that the ending CRLF has been skipped already
                return;
            }

            // remove the '\r'
            stringbuffer.setLength(0);
            ch = streamInput.read();
            if (ch != '\n') {
                throw new IOException("missing the LF of an expected CRLF");
            }

            return;
        }

        ch = streamInput.read();
        if (ch != '\r') {
            /*
             * assume readChunkSizeNonBlocking has read the end of the chunk
             * and that this is the next chunk size, so put the char in the
             * buffer for readChunkSize and return
             */
            stringbuffer.append(ch);
            return;
        }

        ch = streamInput.read();
        if (ch != '\n') {
            throw new IOException("missing the LF of an expected CRLF");
        }
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array starting at
     * offset <code>off</code> to this output stream.
     *
     * <p>
     * This method can only be called after an OutputStream setup has be done.
     *
     * @param b the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     *
     * @return int number of bytes written to stream
     *
     * @exception IOException if an I/O error occurs. In particular, an
     * <code>IOException</code> is thrown if the output stream is closed.
     */
    protected int writeBytes(byte b[], int off, int len)
            throws IOException {
        int bytesToCopy;

        if (requestFinished) {
            throw new IllegalStateException(
                    "Write attempted after request finished");
        }

        if (bytesToWrite == outputDataSize) {
            /*
             * Send the bytes in the write buffer as a chunk to the server
             * so more bytes can be put in the buffer.
             */
            sendRequest(true, false);
        }

        /*
         * Our parent class will call this method in a loop until all the bytes
         * are written. So this method does not have to process all the bytes
         * in one call.
         */
        bytesToCopy = outputDataSize - bytesToWrite;
        if (len < bytesToCopy) {
            bytesToCopy = len;
        }

        System.arraycopy(b, off, writebuf,
                HTTP_OUTPUT_DATA_OFFSET + bytesToWrite, bytesToCopy);
        bytesToWrite += bytesToCopy;

        return bytesToCopy;
    }

    /**
     * If any output data, turn on chunking send it to the server.
     *
     * @exception IOException if an I/O error occurs
     */
    public void flush() throws IOException {
        if (requestFinished) {
            throw new IllegalStateException(
                    "Flush attempted after request finished");
        }

        if (bytesToWrite > 0) {
            sendRequest(true, false);
        }
    }

    /**
     * Get the original URL used to open the HTTP connection.
     *
     * @return HTTP URL used in the current connection
     */
    public String getURL() {
        /*
         * RFC:  Add back protocol stripped by Content Connection.
         */

        return protocol + ":" + saved_url;
    }

    /**
     * Get the protocol scheme parsed from the URL.
     *
     * @return protocol scheme is "http"
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Get the host name parsed from the URL.
     *
     * @return host name from the parsed URL
     */
    public String getHost() {
        return url.host;
    }

    /**
     * Get the file path name parsed from the URL.
     *
     * @return file path name from the parsed URL
     */
    public String getFile() {
        return url.path;
    }

    /**
     * Get the fragment identifier parsed from the URL.
     *
     * @return reference component from the parsed URL
     */
    public String getRef() {
        return url.fragment;
    }

    /**
     * Get the query string parsed from the URL.
     *
     * @return query string from the parsed URL
     */
    public String getQuery() {
        return url.query;
    }

    /**
     * Get the query string parsed from the URL.
     *
     * @return query string from the parsed URL
     */
    public int getPort() {
        return url.port;
    }

    /**
     * Get the request method of the current connection.
     *
     * @return request method is GET, HEAD or POST
     * @see #setRequestMethod
     */
    public String getRequestMethod() {
        return method;
    }

    /**
     * Set the request method of the current connection.
     *
     * @param method request method is GET, HEAD or POST
     * @exception IOException is thrown if the connection is already open
     * @see #getRequestMethod
     */
    public void setRequestMethod(String method) throws IOException {
        ensureOpen();

        if (streamConnection != null) {
            throw new IOException("connection already open");
        }

        /* 
         * The request method can not be changed once the output
         * stream has been opened.
         */
        if (maxOStreams == 0) {
            return;
        }

        if (!method.equals(HEAD)
                && !method.equals(GET)
                && !method.equals(POST)) {
            throw new IOException("unsupported method: " + method);
        }

        this.method = method;
    }

    /**
     * Get the request header value for the named property.
     *
     * @param key property name of specific HTTP 1.1 header field
     * @return value of the named property, if found, null otherwise.
     * @see #setRequestProperty
     */
    public String getRequestProperty(String key) {

        /* https handles the proxy fields in a different way */
        if (key.startsWith("Proxy-")) {
            return proxyHeaders.getProperty(key);
        }

        return reqProperties.getProperty(key);
    }

    /**
     * Set the request header name/value of specific HTTP 1.1 header field.
     *
     * @param key property name
     * @param value property value
     * @exception IllegalArgumentException is thrown if value contains CRLF.
     * @exception IOException If some other kind of I/O error occurs.
     * @see #getRequestProperty
     */
    public void setRequestProperty(String key, String value) throws
            IOException {
        int index = 0;

        ensureOpen();

        if (streamConnection != null) {
            throw new IOException("connection already open");
        }

        /* 
         * The request headers can not be changed once the output 
         * stream has been opened.
         */
        if (maxOStreams == 0) {
            return;
        }

        // Look to see if a hacker embedded any extra fields.
        for (;;) {
            index = value.indexOf("\r\n", index);

            if (index == -1) {
                break;
            }

            // Allow legal header value continuations. CRLF + (SP|HT)
            index += 2;

            if (index >= value.length() || (value.charAt(index) != ' '
                    && value.charAt(index) != '\t')) {
                // illegal values passed for properties - raise an exception
                throw new IllegalArgumentException("illegal value found");
            }
        }

        setRequestField(key, value);
    }

    /**
     * Add the named field to the list of request fields. This method is where a
     * subclass should override properties.
     *
     * @param key key for the request header field.
     * @param value the value for the request header field.
     */
    protected void setRequestField(String key, String value) {

        /* https handles the proxy fields in a different way */
        if (key.startsWith("Proxy-")) {
            proxyHeaders.setProperty(key, value);
            return;
        }

        /*
         * if application setRequestProperties("Connection", "close")
         * then we need to know this & take appropriate default close action
         */
        if ((key.equals("Connection")) && (value.equals("close"))) {
            ConnectionCloseFlag = true;
        }

        if ((key.equals("Transfer-Encoding")) && (value.equals("chunked"))) {
            chunkedOut = true;
        }

        reqProperties.setProperty(key, value);
    }

    /**
     * Get the response code of the current request.
     *
     * @return numeric value of the parsed response code
     * @exception IOException is thrown if a network error ocurrs
     */
    public int getResponseCode() throws IOException {
        ensureOpen();

        sendRequest();

        return responseCode;
    }

    /**
     * Get the response message of the current request.
     *
     * @return message associated with the current response header
     * @exception IOException is thrown if a network error ocurrs
     */
    public String getResponseMessage() throws IOException {
        ensureOpen();

        sendRequest();

        return responseMsg;
    }

    /**
     * Get the Content-Length for the current response.
     *
     * @return length of data to be transmitted after the response headers
     */
    public long getLength() {
        try {
            ensureOpen();

            sendRequest();
        } catch (IOException ioe) {
            // Fall through to return -1 for length
        }

        return contentLength;

    }

    /**
     * Get the Content-Type for the current response.
     *
     * @return MIME type of data to be transmitted after the response header
     */
    public String getType() {
        try {
            return getHeaderField("content-type");
        } catch (IOException x) {
            return null;
        }

    }

    /**
     * Get the Content-Encoding for the current response.
     *
     * @return encoding type of data to be transmitted after the response
     * headers
     */
    public String getEncoding() {
        try {
            return getHeaderField("content-encoding");
        } catch (IOException x) {
            return null;
        }

    }

    /**
     * Get the Expires header for the current response.
     *
     * @return expiration data for the transmitted data
     *
     * @exception IOException is thrown if a network error ocurrs
     */
    public long getExpiration() throws IOException {
        return getHeaderFieldDate("expires", 0);
    }

    /**
     * Get the Date header for the current response.
     *
     * @return timestamp for the data transmission event
     *
     * @exception IOException is thrown if a network error ocurrs
     */
    public long getDate() throws IOException {
        return getHeaderFieldDate("date", 0);
    }

    /**
     * Get the Last-Modified date header for the current response.
     *
     * @return timestamp for the transmitted data last modification
     *
     * @exception IOException is thrown if a network error ocurrs
     */
    public long getLastModified() throws IOException {
        return getHeaderFieldDate("last-modified", 0);
    }

    /**
     * Get the named header field for the current response.
     *
     * @param name header field to be examined
     * @return value of requested header, if found, otherwise null
     *
     * @exception IOException is thrown if a network error ocurrs
     */
    public String getHeaderField(String name) throws IOException {
        ensureOpen();

        sendRequest();

        return (headerFields.getProperty(name.toLowerCase()));

    }

    /**
     * Get the indexed header field for the current response.
     *
     * @param index header field offset to be examined
     * @return key name of requested header, if found, otherwise null
     *
     * @exception IOException is thrown if a network error ocurrs
     */
    public String getHeaderField(int index) throws IOException {
        ensureOpen();

        sendRequest();

        if (index >= headerFields.size()) {
            return null;
        }

        return (headerFields.getValueAt(index));
    }

    /**
     * Get the indexed header field value for the current response.
     *
     * @param index header field value offset to be examined
     * @return value of requested header, if found, otherwise null
     *
     * @exception IOException is thrown if a network error ocurrs
     */
    public String getHeaderFieldKey(int index) throws IOException {
        ensureOpen();

        sendRequest();

        if (index >= headerFields.size()) {
            return null;
        }

        return ((String) (headerFields.getKeyAt(index)));
    }

    /**
     * Get the named header field for the current response and return a numeric
     * value for the parsed field, with a supplied default value if the field
     * does not exist or can not be parsed cleanly.
     *
     * @param name of the field to be examined
     * @param def default value to use, if field is not parsable
     * @return numeric value of requested header, if found, otherwise supplied
     * default is returned
     *
     * @exception IOException is thrown if a network error ocurrs
     */
    public int getHeaderFieldInt(String name, int def) throws IOException {
        ensureOpen();

        sendRequest();

        try {
            return Integer.parseInt(getHeaderField(name));
        } catch (IllegalArgumentException iae) {
            // fall through
        } catch (NullPointerException npe) {
            // fall through
        }

        return def;
    }

    /**
     * Get the named header field for the current response and return a date
     * value for the parsed field,with a supplied default value if the field
     * does not exist or can not be parsed cleanly.
     *
     * @param name of the field to be examined
     * @param def default value to use, if field is not parsable
     * @return date value of requested header, if found, otherwise supplied
     * default is returned
     *
     * @exception IOException is thrown if a network error ocurrs
     */
    public long getHeaderFieldDate(String name, long def) throws IOException {
        ensureOpen();

        sendRequest();

        try {
            return DateParser.parse(getHeaderField(name));
        } catch (NumberFormatException nfe) {
            // fall through
        } catch (IllegalArgumentException iae) {
            // fall through
        } catch (NullPointerException npe) {
            // fall through
        }

        return def;
    }

    /**
     * If not connected, connect to the underlying socket transport and send the
     * HTTP request and get the response header.
     * <P>
     * If an http_proxy was specified the socket connection will be made to the
     * proxy server and the requested URL will include the full http URL.
     * <P>
     * On output the Content-Length header is included in the request based on
     * the size of the buffered output array.
     * <P>
     * This routine inserts the Host header needed for HTTP 1.1 virtual host
     * addressing.
     * <P>
     * This routine also receives the reply response and parses the headers for
     * easier access. After the headers are parsed the application has access to
     * the raw data from the socket stream connection.
     *
     * @exception IOException is thrown if the connection cannot be opened
     */
    protected void sendRequest() throws IOException {
        sendRequest(false, true);
    }

    /**
     * If not connected, connect to the underlying socket transport and send the
     * HTTP request and get the response header.
     * <P>
     * If an http_proxy was specified the socket connection will be made to the
     * proxy server and the requested URL will include the full http URL.
     * <P>
     * On output the Content-Length header is included in the request based on
     * the size of the buffered output array.
     * <P>
     * This routine inserts the Host header needed for HTTP 1.1 virtual host
     * addressing.
     * <P>
     * This routine also receives the reply response and parses the headers for
     * easier access. After the headers are parsed the application has access to
     * the raw data from the socket stream connection.
     *
     * @param chunkData if true chunk data sent to the server
     * @param readResponseHeader if true, read the response header
     *
     * @exception IOException is thrown if the connection cannot be opened
     */
    private void sendRequest(boolean chunkData, boolean readResponseHeader)
            throws IOException {
        int bytesToRetry;

        if (sendingRequest || requestFinished) {
            return;
        }

        sendingRequest = true;

        try {
            if (chunkData) {
                chunkedOut = true;
            }

            bytesToRetry = bytesToWrite;

            try {
                startRequest();
                sendRequestBody();

                if (readResponseHeader) {
                    finishRequestGetResponseHeader();
                }
            } catch (IOException ioe) {
                if (!(streamConnection instanceof StreamConnectionElement)) {
                    /*
                     * This was a connection opened during this transaction.
                     * So do not try to recover.
                     */
                    throw ioe;
                }

                try {
                    connectionPool.remove(
                            (StreamConnectionElement) streamConnection);
                } catch (Exception e) {
                    // do not over throw the previous exception
                }

                if (firstChunkSent) {
                    // can't retry since we do not have the previous chunk
                    throw new IOException("Persistent connection dropped "
                            + "after first chunk sent, cannot retry");
                }

                streamConnection = null;
                streamInput = null;
                streamOutput = null;
                bytesToWrite = bytesToRetry;

                startRequest();
                sendRequestBody();

                if (readResponseHeader) {
                    finishRequestGetResponseHeader();
                }
            }

            if (chunkedOut) {
                firstChunkSent = true;
            }
        } finally {
            sendingRequest = false;
        }
    }

    /**
     * If not connected, connect to the underlying socket transport and send the
     * HTTP request headers.
     * <P>
     * If an http_proxy was specified the socket connection will be made to the
     * proxy server and the requested URL will include the full http URL.
     * <P>
     * On output the Content-Length header is included in the request based on
     * the size of the buffered output array.
     * <P>
     * This routine inserts the Host header needed for HTTP 1.1 virtual host
     * addressing.
     * <P>
     * This routine also receives the reply response and parses the headers for
     * easier access. After the headers are parsed the application has access to
     * the raw data from the socket stream connection.
     *
     * @exception IOException is thrown if the connection cannot be opened
     */
    private void startRequest() throws IOException {
        if (streamConnection != null) {
            return;
        }

        streamConnect();
        sendRequestHeader();
    }

    /**
     * Find a previous connection in the pool or try to connect to the
     * underlying stream transport.
     *
     * @exception IOException is thrown if the connection cannot be opened
     */
    private void streamConnect() throws IOException {
        verifyPermissionCheck();

        streamConnection = connectionPool.get(protocol, url.host, url.port);

        if (streamConnection == null) {
            streamConnection = connect();
        }

        /*
         * Because StreamConnection.open*Stream cannot be called twice
         * the HTTP connect method may have already open the streams
         * to connect to the proxy and saved them in the field variables
         * already.
         */
        if (streamOutput != null) {
            return;
        }

        streamOutput = streamConnection.openDataOutputStream();
        streamInput = streamConnection.openDataInputStream();
    }

    /**
     * Gets the underlying stream connection.
     *
     * @return underlying stream connection
     */
    protected StreamConnection getStreamConnection() {
        return streamConnection;
    }

    /**
     * Simplifies the sendRequest() method header functionality into one method
     * this is extremely helpful for persistent connection support and retries.
     *
     * @exception IOException is thrown if the connection cannot be opened
     */
    private void sendRequestHeader() throws IOException {
        StringBuffer reqLine;
        String filename;
        int numberOfKeys;

        // HTTP 1.0 requests must contain content length for proxies
        if (getRequestProperty("Content-Length") == null) {
            setRequestField("Content-Length", Integer.toString(bytesToWrite));
        }

        reqLine = new StringBuffer(256);

        /*
         * HTTP RFC and bug#4402149,
         * if there is no path then add a slash ("/").
         */
        filename = url.path;
        if (filename == null) {
            filename = "/";
        }

        /*
         * Note: the "ref" or fragment, is not sent to the server.
         */
        reqLine.append(method);
        reqLine.append(" ");

        /*
         * Since we now use a tunnel instead of a proxy, we do not have
         * to send an absolute URI. The difference is that a proxy will
         * strip scheme and authority from the URI and a tunnel will not.
         *
         * For HTTPS purposes we will use the relative URI.
         *
         * Some HTTPS server's do not like to see "https" as the scheme of an 
         * URI and only recognize "http".
         * examples: www.wellsfargo.com sends back html with not HTTP headers,
         * e-banking.abbeynational.co.uk sends back a 404 to all requests.
         *
         * It is better to not use the absolute URL, than to hardcode the
         * the scheme to "http" all the time since that did not work with
         * e-banking.abbeynational.co.uk.
         *
         * if (http_proxy != null) {
         *     reqLine.append(protocol);
         *     reqLine.append("://");
         *     reqLine.append(url.authority);
         * }
         */
        reqLine.append(filename);

        if (url.query != null) {
            reqLine.append("?");
            reqLine.append(url.query);
        }

        reqLine.append(" ");
        reqLine.append(HTTP_VERSION);
        reqLine.append("\r\n");

        /*
         * HTTP 1/1 requests require the Host header to distinguish
         * virtual host locations.
         */
        setRequestField("Host", url.authority);

        if (chunkedOut) {
            /*
             * Signal the server that the body is chunked
             * by setting the Transfer-Encoding property to "chunked".
             */
            setRequestField("Transfer-Encoding", "chunked");
        }

        /*
         * Setup the various http header field/values defined and/or
         * required.
         */
        numberOfKeys = reqProperties.size();
        for (int i = 0; i < numberOfKeys; i++) {
            String key = (String) reqProperties.getKeyAt(i);

            if (key.equals("Content-Length")) {
                /*
                 * If its CHUNK data - no content-length: size required.
                 */
                if (chunkedOut) {
                    continue;
                } else {
                    /*
                     * Check that the output stream has been opened.
                     */
                    if (writebuf == null) {
                        reqLine.append("Content-Length: 0");
                    } else {
                        reqLine.append("Content-Length: ");
                        reqLine.append(bytesToWrite);
                    }

                    reqLine.append("\r\n");
                }
            } else {
                reqLine.append(key);
                reqLine.append(": ");
                reqLine.append(reqProperties.getValueAt(i));
                reqLine.append("\r\n");
            }
        }

        reqLine.append("\r\n");

        streamOutput.write(reqLine.toString().getBytes());
    }

    /**
     * Write the http request body bytes to the output stream.
     *
     * @exception IOException
     */
    protected void sendRequestBody() throws IOException {

        int start;
        int endOfData;
        int length;

        if ((writebuf == null) || (bytesToWrite == 0)) {
            return;
        }

        start = HTTP_OUTPUT_DATA_OFFSET;
        endOfData = HTTP_OUTPUT_DATA_OFFSET + bytesToWrite;
        length = bytesToWrite;

        /*
         * If a CHUNKed session then write out the chunk size first 
         * with a trailing CRLF.
         *
         * reference: RFC2616 - section 3 protocol parameters
         * 3.6 transfer coding:
         * 3.6.1 chunked transfer coding:
         * chunk-body = chunk / last chunk / trailer / CRLF
         *       * chunk =      chunk-size / chunk-data / CRLF
         *         last_chunk = "0" / CRLF
         *         trailer    = " " / CRLF
         * *indicates its done here.
         */
        if (chunkedOut) {
            /*
             * For CHUNKed write out the chunk size with CRLF.
             * Put this before the data in write buffer.
             */
            String temp = Integer.toHexString(bytesToWrite);
            int tempLen = temp.length();

            writebuf[--start] = (byte) '\n';
            writebuf[--start] = (byte) '\r';
            for (int i = tempLen - 1; i >= 0; i--) {
                writebuf[--start] = (byte) temp.charAt(i);
            }

            length += tempLen + 2;

            /*
             * If a CHUNKed session then write out another CRLF and flush().
             * Put this after the data in the write bufffer.
             */
            writebuf[endOfData++] = (byte) '\r';
            writebuf[endOfData++] = (byte) '\n';
            length += 2;
        }

        streamOutput.write(writebuf, start, length);
        bytesToWrite = 0;
    }

    /**
     * Finish the http request and reads the response headers.
     *
     * @exception IOException is thrown, if an I/O error occurs for final stream
     * output or on reading the response message line
     */
    protected void finishRequestGetResponseHeader() throws IOException {

        // Even if we get an exception this request is finished
        requestFinished = true;

        /*
         * if this is a CHUNKed session write out the last set of CRLF
         */
        if (chunkedOut) {
            /*
             * reference: RFC2616 - section 3 protocol parameters
             * 3.6 transfer coding:
             * 3.6.1 chunked transfer coding:
             * chunk-body = chunk / last chunk / trailer / CRLF
             *         chunk =      chunk-size / chunk-data / CRLF
             *       * last_chunk = "0" / CRLF
             *       * trailer    = " " / CRLF
             * * indicates its done here.
             */

 /*
             * write the last chunk (size=0 / CRLF) and the dummy trailer 
             */
            streamOutput.write("0\r\n\r\n".getBytes());
        }

        streamOutput.flush();

        readResponseMessage(streamInput);
        readHeaders(streamInput);

        /*
         * Ignore a continuation header and read the true headers again.
         * (Bug# 4382226 discovered with Jetty HTTP 1.1 web server.
         */
        if (responseCode == 100) {
            readResponseMessage(streamInput);
            readHeaders(streamInput);
        }
    }

    /**
     * Connect to the underlying network TCP transport. If the proxy is
     * configured, connect to it as tunnel first.
     * <p>
     * Warning: A subclass that implements this method, should not call this
     * method and should implement the disconnect method.
     *
     * @return network stream connection
     * @exception IOException is thrown if the connection cannot be opened
     */
    protected StreamConnection connect() throws IOException {
        org.mini.net.socket.Protocol conn;

        verifyPermissionCheck();

        conn = new org.mini.net.socket.Protocol();

        if (http_proxy == null) {
            conn.openPrim("//" + hostAndPort, Connector.READ_WRITE, true);

            // Do not delay request since this delays the response.
//            conn.setSocketOption(SocketConnection.DELAY, 0);
            return conn;
        }

        conn.openPrim("//" + http_proxy, 0, true);

        // Do not delay request since this delays the response.
//        conn.setSocketOption(SocketConnection.DELAY, 0);
        // openData*Stream cannot be call twice, so save them for later
        streamOutput = conn.openDataOutputStream();
        streamInput = conn.openDataInputStream();

        try {
            doTunnelHandshake(streamOutput, streamInput);
        } catch (IOException ioe) {
            String response = ioe.getMessage();

            try {
                disconnect(conn, streamInput, streamOutput);
            } catch (Exception e) {
                // do not over throw the handshake exception
            }

            streamOutput = null;
            streamInput = null;

            if ((response != null) && (response.indexOf(" 500 ") > -1)) {
                throw new ConnectionNotFoundException(response);
            } else {
                throw ioe;
            }
        }

        return conn;
    }

    /**
     * Connects to the SSL tunnel and completes the intialization of the tunnel
     * (handshake). The handshake based on the Internet-Draft "A. Luotonen,
     * Tunneling TCP based protocols through Web proxy servers, February 1999".
     *
     * @param os output stream for secure handshake
     * @param is input stream for secure handshake
     * @exception IOException is thrown if an error occurs in the SSL handshake
     */
    protected void doTunnelHandshake(OutputStream os, InputStream is) throws
            IOException {
        String required;
        String optional;
        String endOfLine = "\r\n";
        String emptyLine = endOfLine;
        int numberOfKeys;
        StringBuffer temp;
        boolean newline;
        String response;

        /*
         * request = required *optional emptyLine
         * required = "CONNECT" SP HOST ":" PORT SP HTTP_VERSION endOfLine
         * optional = HTTP_HEADER endOfLine ; proxy dependent: most likely
         *                                  ; used for authorization.
         * emptyLine = endOfLine
         * endOfLine = *1CR LF
         *
         * example:
         * CONNECT home.acme.com:443 HTTP/1.0
         * Proxy-Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
         *
         */
        required = "CONNECT " + hostAndPort + " " + HTTP_VERSION + endOfLine;

        os.write(required.getBytes());

        numberOfKeys = proxyHeaders.size();
        for (int i = 0; i < numberOfKeys; i++) {
            optional = proxyHeaders.getKeyAt(i) + ": "
                    + proxyHeaders.getValueAt(i) + endOfLine;
            os.write(optional.getBytes());
        }

        os.write(emptyLine.getBytes());
        os.flush();

        /*
         * response = status *optional emptyLine
         * status = HTTP_VERSION SP STATUSCODE STATUS_MESSAGE *1CR LF
         * optional = HTTP_HEADER *1CR LF
         * emptyLine = *1CR LF
         *
         * example:
         * HTTP/1.0 200 Connection established
         *
         */
        // Read in response until an empty line is found (1*CR LF 1*CR LF)
        temp = new StringBuffer();
        newline = false;
        while (true) {
            int c = is.read();
            if (c == -1) {
                break;
            } else if (c == '\n') {
                if (newline) {
                    break;
                }
                newline = true;
            } else if (c != '\r') {
                newline = false;
            }

            temp.append((char) c);
        }

        if (temp.length() == 0) {
            temp.append("none");
        }

        response = temp.toString();

        if (response.indexOf(" 200 ") == -1) {
            throw new IOException("Error initializing HTTP tunnel connection: \n"
                    + response);
        }
    }

    /**
     * Check the initial response message looking for the appropriate HTTP
     * version string. Parse the response code for easy application branching on
     * condition codes.
     *
     * @param in input stream where the response headers are read
     * @exception IOException is thrown if the header response can not be parsed
     */
    private void readResponseMessage(InputStream in) throws IOException {

        String line = null;
        responseCode = -1;
        responseMsg = null;

        line = readLine(in);

        /*
         * REFERENCE: HTTP1.1 document 
         * SECTION: 3.6.1 Chunked Transfer Coding
         * in some cases there may be an OPTIONAL trailer containing 
         * entity-header fields. since we don't support the available()
         * method for inputstreams and for performance reasons we
         * do not attempt to clean up the previous connections input
         * stream. the first thing we do here is read the stream and 
         * discard it.
         */
        if (line != null && line.length() == 0) {
            line = readLine(in);
        }

        int httpEnd, codeEnd;

        responseCode = -1;
        responseMsg = null;

        if (line == null) {
            throw new IOException("response empty");
        }

        httpEnd = line.indexOf(' ');
        if (httpEnd < 0) {
            if (line.length() > 10) {
                // only put the first 10 chars in the exception
                line = line.substring(0, 10);
            }

            throw new IOException("cannot find status code in response: "
                    + line);
        }

        String temp = line.substring(0, httpEnd);
        if (!temp.startsWith("HTTP")) {
            if (httpEnd > 10) {
                // only put the first 10 chars in the exception
                temp = temp.substring(0, 10);
            }

            throw new IOException("response does not start with HTTP "
                    + "it starts with: " + temp);
        }

        httpVer = temp;
        if (line.length() <= httpEnd) {
            throw new IOException("status line ends after HTTP version");
        }

        codeEnd = line.substring(httpEnd + 1).indexOf(' ');
        if (codeEnd < 0) {
            throw new IOException("cannot find reason phrase in response");
        }

        codeEnd += (httpEnd + 1);
        if (line.length() <= codeEnd) {
            throw new IOException("status line end after status code");
        }

        try {
            responseCode = Integer.parseInt(line.substring(httpEnd + 1,
                    codeEnd));
        } catch (NumberFormatException nfe) {
            throw new IOException("status code in response is not a number");
        }

        responseMsg = line.substring(codeEnd + 1);
    }

    /**
     * Read the response message headers. Parse the response headers name value
     * pairs for easy application use.
     *
     * @param in input stream where the response headers are read
     * @exception IOException is thrown if the response headers cannot be parsed
     */
    private void readHeaders(InputStream in) throws IOException {
        String line;
        String key = null;
        int prevPropIndex = headerFields.size() - 1;
        boolean firstLine = true;
        String value;
        String prevValue = null;
        int index;

        /*
         * Initialize and set the current input stream variables
         */
        bytesleft = 0;
        chunksize = -1;
        bytesread = 0;
        totalbytesread = 0;
        chunkedIn = false;
        eof = false;

        for (;;) {
            try {
                line = readLine(in);
            } catch (IOException ioe) {
                throw new IOException(ioe.getMessage());
            }

            if (line == null || line.equals("")) {
                break;
            }

            if ((!firstLine) && (line.charAt(0) == ' '
                    || line.charAt(0) == '\t')) {
                // This line is a contiuation of the previous line.

                /*
                 * The continuation is for the user readablility so restore
                 * the CR LF when appending.
                 */
                value = prevValue + "\r\n" + line;

                /*
                 * Set value by index, since there can be multiple properties
                 * with the same key.
                 */
                headerFields.setPropertyAt(prevPropIndex, value);
                prevValue = value;
                continue;
            }

            index = line.indexOf(':');
            if (index < 0) {
                throw new IOException("malformed header field " + line);
            }

            key = line.substring(0, index).toLowerCase();
            if (key.length() == 0) {
                throw new IOException("malformed header field, no key " + line);
            }

            if (line.length() <= index + 1) {
                value = "";
            } else {
                value = line.substring(index + 1).trim();
            }

            /**
             * Check the response header to see if the server would like to
             * close the connection. BUG#4492849
             */
            if ((key.equals("connection"))
                    && (value.equals("close"))) {
                ConnectionCloseFlag = true;
            }

            /*
	     * Determine if this is a chunked data transfer.
             */
            if ((key.equals("transfer-encoding"))
                    && (value.regionMatches(true, 0, "chunked",
                            0, value.length()))) {
                chunkedIn = true;
            }

            /*
	     * Update the Content-Length based on the header value.
             */
            if (key.equals("content-length")) {
                try {
                    contentLength = Integer.parseInt(value);
                } catch (IllegalArgumentException iae) {
                    // fall through
                } catch (NullPointerException npe) {
                    // fall through
                }
            }

            /* Save the response key value pairs. */
            headerFields.addProperty(key, value);
            firstLine = false;
            prevPropIndex++;
            prevValue = value;
        }

        /* Initialize the amount of data expected. */
        if (chunkedIn) {
            chunksize = readChunkSize();
        } else // do not let the read block if there is no data.
        {
            if (method.equals(HEAD)) {
                chunksize = 0;
            } else {
                // treat non chunked data of known length as one big chunk
                chunksize = contentLength;
            }
        }

        /* Last chunk or zero length response data. */
        if (chunksize == 0) {
            eof = true;
        }
    }

    /**
     * Uses the shared stringbuffer to read a line terminated by CRLF and return
     * it as string. Blocks until the line is done or end of stream.
     *
     * @param in InputStream to read the data
     * @return one line of input header or null if end of stream
     * @exception IOException if error encountered while reading headers
     */
    private String readLine(InputStream in) throws IOException {
        int c;

        try {
            for (;;) {
                c = in.read();
                if (c < 0) {
                    return null;
                }

                if (c == '\r') {
                    continue;
                }

                if (c == '\n') {
                    break;
                }

                stringbuffer.append((char) c);
            }

            /* Return a whole line and reset the string buffer. */
            String line = stringbuffer.toString();

            return line;
        } finally {
            stringbuffer.setLength(0);
        }
    }

    /**
     * Close the OutputStream and transition to connected state.
     *
     * @exception IOException if the subclass throws one
     */
    protected void closeOutputStream() throws IOException {
        try {
            /*
             * Send a request to the web server if there wasn't one
             * sent already
             */
            sendRequest();

            /* Finish the common close processing. */
            super.closeOutputStream();
        } catch (Exception e) {
            /* Finish the common close processing. */
            super.closeOutputStream();
            if (e instanceof IOException) {
                throw (IOException) e;
            }

            throw (RuntimeException) e;
        }
    }

    /**
     * Disconnect the current low level socket connection. If the connection is
     * an HTTP1.1 connection that connection will be put back in the pool for
     * another session to use to connect.
     */
    protected void disconnect() throws IOException {
        if (streamConnection == null) {
            return;
        }

        /*
         * If the response had content length and it was not chunked
         * and the caller did not read more than the content length
         * eof was not set, so do that now.
         */
        if (!eof && !chunkedIn && chunksize >= 0
                && totalbytesread == chunksize) {
            eof = true;
        }

        /*
         * reasons for not reusing the connection are:
         *
         * 1. only part of the chucked request body was sent
         * 2. caller left response data in the stream
         * 3. it is a 1.0 connection
         * 4. there was a signal to close the connection
         * 5. reading in progress on this connection in another thread
         */
        synchronized (streamInput) {
            if (readInProgress) {
                // do not save the connection
                ConnectionCloseFlag = true;
            }
        }

        if (!requestFinished || !eof || httpVer.equals("HTTP/1.0")
                || ConnectionCloseFlag) {
            if (streamConnection instanceof StreamConnectionElement) {
                // we got this connection from the pool
                connectionPool.remove(
                        (StreamConnectionElement) streamConnection);
            } else {
                disconnect(streamConnection, streamInput, streamOutput);
            }

            return;
        }

        if (streamConnection instanceof StreamConnectionElement) {
            // we got this connection from the pool
            connectionPool.returnForReuse(
                    (StreamConnectionElement) streamConnection);
            return;
        }

        // save the connection for reuse
        if (!connectionPool.add(protocol, url.host, url.port,
                streamConnection, streamOutput, streamInput)) {
            // pool full, disconnect
            disconnect(streamConnection, streamInput, streamOutput);
        }
    }

    /**
     * Disconnect from the underlying socket transport. Closes the low level
     * socket connection and the input and output streams used by the socket.
     * <p>
     * Warning: A subclass that implements connect, should also implement this
     * method without calling this method.
     *
     * @param connection connection return from {@link #connect()}
     * @param inputStream input stream opened from <code>connection</code>
     * @param outputStream output stream opened from <code>connection</code>
     * @exception IOException if an I/O error occurs while the connection is
     * terminated.
     * @exception IOException is thrown if the connection or associated streams
     * cannot be closed
     */
    protected void disconnect(StreamConnection connection,
            InputStream inputStream, OutputStream outputStream)
            throws IOException {
        try {
            if (connection != null) {
                connection.close();
            }
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
    }
}
