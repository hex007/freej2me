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

import java.io.IOException;

/**
 * This interface defines the necessary methods and constants
 * for an HTTP connection.
 * <P>
 * HTTP is a request-response protocol in which the parameters of
 * request must be set before the request is sent.
 * The connection exists in one of three states:
 * <UL>
 * <LI> Setup, in which the request parameters can be set
 * <LI> Connected, in which request parameters have been sent and the
 *      response is expected
 * <LI> Closed, the final state, in which the HTTP connection as been
 * terminated
 * </UL>
 * The following methods may be invoked only in the Setup state:
 * <UL>
 * <LI> <CODE> setRequestMethod</CODE>
 * <LI> <CODE> setRequestProperty</CODE>
 * </UL>
 *
 * The transition from Setup to Connected is caused by any method that
 * requires data to be sent to or received from the server. <p>
 *
 * The following methods cause the transition to the Connected state when
 * the connection is in Setup state.
 * <UL>
 * <LI> <CODE> openInputStream</CODE>
 * <LI> <CODE> openDataInputStream</CODE>
 * <LI> <CODE> getLength</CODE>
 * <LI> <CODE> getType</CODE>
 * <LI> <CODE> getEncoding</CODE>
 * <LI> <CODE> getHeaderField</CODE>
 * <LI> <CODE> getResponseCode</CODE>
 * <LI> <CODE> getResponseMessage</CODE>
 * <LI> <CODE> getHeaderFieldInt</CODE>
 * <LI> <CODE> getHeaderFieldDate</CODE>
 * <LI> <CODE> getExpiration</CODE>
 * <LI> <CODE> getDate</CODE>
 * <LI> <CODE> getLastModified</CODE>
 * <LI> <CODE> getHeaderField</CODE>
 * <LI> <CODE> getHeaderFieldKey</CODE>
 * </UL>
 * <P>
 * The following methods may be invoked while the connection is in Setup or
 * Connected state.
 * <UL>
 * <LI> <CODE> close</CODE>
 * <LI> <CODE> getRequestMethod</CODE>
 * <LI> <CODE> getRequestProperty</CODE>
 * <LI> <CODE> getURL</CODE>
 * <LI> <CODE> getProtocol</CODE>
 * <LI> <CODE> getHost</CODE>
 * <LI> <CODE> getFile</CODE>
 * <LI> <CODE> getRef</CODE>
 * <LI> <CODE> getPort</CODE>
 * <LI> <CODE> getQuery</CODE>
 * </UL>
 *
 * <P>
 * After an output stream has been opened by the
 * <code>openOutputStream</code> or <code>openDataOutputStream</code>
 * methods, attempts to change the request parameters via
 * <code>setRequestMethod</code> or the <code>setRequestProperty</code>
 * are ignored. Once the request parameters have been sent,
 * these methods will throw an <code>IOException</code>.
 *
 * When an output stream is closed via the
 * <code>OutputStream.close</code> or <code>DataOutputStream.close</code>
 * methods, the connection enters the Connected state.
 * When the output stream is flushed via the  
 * <code>OutputStream.flush</code> or <code>DataOutputStream.flush</code>
 * methods, the request parameters MUST be sent along with any data
 * written to the stream.
 * </P>
 *
 * The transition to Closed state from any other state is caused by the
 * <code>close</code> method and the closing all of the streams that were
 * opened from the connection. 
 * <P>
 * <STRONG>Example using StreamConnection</STRONG>
 * <p>
 * Simple read of a URL using <code>StreamConnection</code>.
 * No HTTP specific behavior is needed or used.
 * (<strong>Note:</strong> this example ignores all HTTP response
 * headers and the HTTP response code. Since a proxy or server
 * may have sent an error response page, an application
 * can not distinquish which data is retreived in
 * the <code>InputStream</code>.)
 *
 * <p>
 * <code>Connector.open</code> is used to open URL and a
 * <code>StreamConnection</code> is returned.
 * From the <code>StreamConnection</code> the
 * <code>InputStream</code> is opened.
 * It is used to read every character until end of file (-1).
 * If an exception is thrown the connection and stream are closed.
 * <p>
 * <pre>
 *     void getViaStreamConnection(String url) throws IOException {
 *         StreamConnection c = null;
 *         InputStream s = null;
 *         try {
 *             c = (StreamConnection)Connector.open(url);
 *             s = c.openInputStream();
 *             int ch;
 *             while ((ch = s.read()) != -1) {
 *                 ...
 *             }
 *         } finally {
 *             if (s != null)
 *                 s.close();
 *             if (c != null)
 *                 c.close();
 *         }
 *     }
 * </pre>
 * <p>
 * <STRONG>Example using ContentConnection</STRONG>
 * <p>
 * Simple read of a URL using <code>ContentConnection</code>.
 * No HTTP specific behavior is needed or used.
 * <p>
 * <code>Connector.open</code> is used to open url and
 * a <code>ContentConnection</code> is returned.
 * The <code>ContentConnection</code> may be able to provide the length.
 * If the length is available, it is used to read the data in bulk.
 * From the <code>ContentConnection</code> the
 * <code>InputStream</code> is opened.
 * It is used to read every character until end of file (-1).
 * If an exception is thrown the connection and stream are closed.
 * <p>
 * <pre>
 *     void getViaContentConnection(String url) throws IOException {
 *         ContentConnection c = null;
 *         DataInputStream is = null;
 *         try {
 *             c = (ContentConnection)Connector.open(url);
 *             int len = (int)c.getLength();
 *             dis = c.openDataInputStream();
 *             if (len &gt; 0) {
 *                 byte[] data = new byte[len];
 *                 dis.readFully(data);
 *             } else {
 *                 int ch;
 *                 while ((ch = dis.read()) != -1) {
 *                     ...
 *                 }
 *             }
 *         } finally {
 *             if (dis != null)
 *                 dis.close();
 *             if (c != null)
 *                 c.close();
 *         }
 *     }
 * </pre>
 * <p>
 * <STRONG>Example using HttpConnection</STRONG>
 * <p>
 * Read the HTTP headers and the data using <code>HttpConnection</code>.
 * <p>
 * <code>Connector.open</code> is used to open url and a
 * <code>HttpConnection</code> is returned.
 * The HTTP headers are read and processed.
 * If the length is available, it is used to read the data in bulk.
 * From the <code>HttpConnection</code> the
 * <code>InputStream</code> is opened.
 * It is used to read every character until end of file (-1).
 * If an exception is thrown the connection and stream are closed.
 * <p>
 * <pre>
 *     void getViaHttpConnection(String url) throws IOException {
 *         HttpConnection c = null;
 *         InputStream is = null;
 *         int rc;
 *
 *         try {
 *             c = (HttpConnection)Connector.open(url);
 *
 *             // Getting the response code will open the connection,
 *             // send the request, and read the HTTP response headers.
 *             // The headers are stored until requested.
 *             rc = c.getResponseCode();
 *             if (rc != HttpConnection.HTTP_OK) {
 *                 throw new IOException("HTTP response code: " + rc);
 *             }
 *
 *             is = c.openInputStream();
 *
 *             // Get the ContentType
 *             String type = c.getType();
 *
 *             // Get the length and process the data
 *             int len = (int)c.getLength();
 *             if (len &gt; 0) {
 *                 int actual = 0;
 *                 int bytesread = 0 ;
 *                 byte[] data = new byte[len];
 *                 while ((bytesread != len) && (actual != -1)) {
 *                    actual = is.read(data, bytesread, len - bytesread);
 *                    bytesread += actual;
 *                 }
 *             } else {
 *                 int ch;
 *                 while ((ch = is.read()) != -1) {
 *                     ...
 *                 }
 *             }
 *         } catch (ClassCastException e) {
 *             throw new IllegalArgumentException("Not an HTTP URL");
 *         } finally {
 *             if (is != null)
 *                 is.close();
 *             if (c != null)
 *                 c.close();
 *         }
 *     }
 * </pre>
 * <p>
 * <STRONG>Example using POST with HttpConnection</STRONG>
 * <p>
 *
 * Post a request with some headers and content to the server and
 * process the headers and content.
 * <p>
 * <code>Connector.open</code> is used to open url and a
 * <code>HttpConnection</code> is returned.
 * The request method is set to POST and request headers set.
 * A simple command is written and flushed.
 * The HTTP headers are read and processed.
 * If the length is available, it is used to read the data in bulk.
 * From the <code>HttpConnection</code> the
 * <code>InputStream</code> is opened.
 * It is used to read every character until end of file (-1).
 * If an exception is thrown the connection and stream is closed.
 * <p>
 * <pre>
 *    void postViaHttpConnection(String url) throws IOException {
 *        HttpConnection c = null;
 *        InputStream is = null;
 *        OutputStream os = null;
 *        int rc;
 *
 *        try {
 *            c = (HttpConnection)Connector.open(url);
 *
 *            // Set the request method and headers
 *            c.setRequestMethod(HttpConnection.POST);
 *            c.setRequestProperty("If-Modified-Since",
 *                "29 Oct 1999 19:43:31 GMT");
 *            c.setRequestProperty("User-Agent",
 *                "Profile/MIDP-2.0 Configuration/CLDC-1.0");
 *            c.setRequestProperty("Content-Language", "en-US");
 *
 *            // Getting the output stream may flush the headers
 *            os = c.openOutputStream();
 *            os.write("LIST games\n".getBytes());
 *            os.flush();           // Optional, getResponseCode will flush
 *
 *            // Getting the response code will open the connection,
 *            // send the request, and read the HTTP response headers.
 *            // The headers are stored until requested.
 *            rc = c.getResponseCode();
 *            if (rc != HttpConnection.HTTP_OK) {
 *                throw new IOException("HTTP response code: " + rc);
 *            }
 *
 *            is = c.openInputStream();
 *
 *            // Get the ContentType
 *            String type = c.getType();
 *            processType(type);
 *
 *            // Get the length and process the data
 *            int len = (int)c.getLength();
 *            if (len &gt; 0) {
 *                 int actual = 0;
 *                 int bytesread = 0 ;
 *                 byte[] data = new byte[len];
 *                 while ((bytesread != len) && (actual != -1)) {
 *                    actual = is.read(data, bytesread, len - bytesread);
 *                    bytesread += actual;
 *                 }
 *                process(data);
 *            } else {
 *                int ch;
 *                while ((ch = is.read()) != -1) {
 *                    process((byte)ch);
 *                }
 *            }
 *        } catch (ClassCastException e) {
 *            throw new IllegalArgumentException("Not an HTTP URL");
 *        } finally {
 *            if (is != null)
 *                is.close();
 *            if (os != null)
 *                os.close();
 *            if (c != null)
 *                c.close();
 *        }
 *    }
 * </pre>
 * <hr>
 * <p>
 * <STRONG>Simplified Stream Methods on Connector</STRONG>
 * <p>
 * Please note the following: The
 * <code>Connector</code> class defines the following
 * convenience methods for retrieving an input or output stream directly
 * for a specified URL:
 *
 * <UL>
 * <LI> <CODE> InputStream openInputStream(String url) </CODE>
 * <LI> <CODE> DataInputStream openDataInputStream(String url) </CODE>
 * <LI> <CODE> OutputStream openOutputStream(String url) </CODE>
 * <LI> <CODE> DataOutputStream openDataOutputStream(String url) </CODE>
 * </UL>
 *
 * Please be aware that using these methods implies certain restrictions.
 * You will not get a reference to the actual connection, but rather just
 * references to the input or output stream of the connection. Not having
 * a reference to the connection means that you will not be able to manipulate
 * or query the connection directly. This in turn means that you will not
 * be able to call any of the following methods:
 *
 * <UL>
 * <LI> <CODE> getRequestMethod() </CODE>
 * <LI> <CODE> setRequestMethod() </CODE>
 * <LI> <CODE> getRequestProperty() </CODE>
 * <LI> <CODE> setRequestProperty() </CODE>
 * <LI> <CODE> getLength() </CODE>
 * <LI> <CODE> getType() </CODE>
 * <LI> <CODE> getEncoding() </CODE>
 * <LI> <CODE> getHeaderField() </CODE>
 * <LI> <CODE> getResponseCode() </CODE>
 * <LI> <CODE> getResponseMessage() </CODE>
 * <LI> <CODE> getHeaderFieldInt</CODE>
 * <LI> <CODE> getHeaderFieldDate</CODE>
 * <LI> <CODE> getExpiration</CODE>
 * <LI> <CODE> getDate</CODE>
 * <LI> <CODE> getLastModified</CODE>
 * <LI> <CODE> getHeaderField</CODE>
 * <LI> <CODE> getHeaderFieldKey</CODE>
 * </UL>
 * @since MIDP 1.0
 */

public interface HttpConnection extends ContentConnection {
    /** HTTP Head method. */
    public final static String HEAD = "HEAD";
    /** HTTP Get method. */
    public final static String GET = "GET";
    /** HTTP Post method. */
    public final static String POST = "POST";

    /** 2XX: generally "OK" */
    /** 200: The request has succeeded. */
    public static final int HTTP_OK = 200;

    /**
     * 201: The request has been fulfilled and resulted in a new
     * resource being created.
     */
    public static final int HTTP_CREATED = 201;

    /**
     * 202: The request has been accepted for processing, but the processing
     * has not been completed.
     */
    public static final int HTTP_ACCEPTED = 202;

    /**
     * 203: The returned meta-information in the entity-header is not the
     *  definitive set as available from the origin server.
     */
    public static final int HTTP_NOT_AUTHORITATIVE = 203;

    /**
     * 204:  The server has fulfilled the request but does not need to
     * return an entity-body, and might want to return updated
     * meta-information.
     */
    public static final int HTTP_NO_CONTENT = 204;

    /**
     * 205: The server has fulfilled the request and the user agent SHOULD reset
     *	the document view which caused the request to be sent.
     */
    public static final int HTTP_RESET = 205;

    /**
     *  206: The server has fulfilled the partial GET request for the resource.
     */
    public static final int HTTP_PARTIAL = 206;

    /** 3XX: relocation/redirect */

    /**
     * 300: The requested resource corresponds to any one of a set of
     *   representations, each with its own specific location, and agent-
     *   driven negotiation information is being provided so that
     *   the user (or user agent) can select a preferred representation and
     *   redirect its request to that location.
     */
    public static final int HTTP_MULT_CHOICE = 300;

    /**
     * 301:  The requested resource has been assigned a new permanent URI and
     *   any future references to this resource SHOULD use one of the returned
     *   URIs.
     */
    public static final int HTTP_MOVED_PERM = 301;

    /**
     * 302: The requested resource resides temporarily under a
     *   different URI. (<strong>Note:</strong> the name of this
     *   status code
     *   reflects the earlier publication of RFC2068, which
     *   was changed in RFC2616 from "moved temporalily" to
     *   "found". The semantics were not changed. The <code>Location</code>
     *   header indicates where the application should resend
     *   the request.)
     */
    public static final int HTTP_MOVED_TEMP = 302;

    /**
     * 303: The response to the request can be found under a different URI and
     *   SHOULD be retrieved using a GET method on that resource.
     */
    public static final int HTTP_SEE_OTHER = 303;

    /**
     * 304: If the client has performed a conditional GET request and access is
     *   allowed, but the document has not been modified, the server SHOULD
     *   respond with this status code.
     */
    public static final int HTTP_NOT_MODIFIED = 304;

    /**
     * 305: The requested resource MUST be accessed through the proxy given by
     *	the Location field.
     */
    public static final int HTTP_USE_PROXY = 305;

    /**
     * 307: The requested resource resides temporarily under a different
     *   URI.
     */
    public static final int HTTP_TEMP_REDIRECT = 307;

    /** 4XX: client error */
    /**
     * 400: The request could not be understood by the server due to malformed
     *   syntax.
     */
    public static final int HTTP_BAD_REQUEST = 400;

    /**
     * 401: The request requires user authentication. The response MUST
     *   include a WWW-Authenticate header field  containing a challenge
     *   applicable to the requested resource.
     */
    public static final int HTTP_UNAUTHORIZED = 401;

    /** 402: This code is reserved for future use. */
    public static final int HTTP_PAYMENT_REQUIRED = 402;

    /**
     * 403:  The server understood the request, but is refusing to fulfill it.
     *   Authorization will not help and the request SHOULD NOT be repeated.
     */
    public static final int HTTP_FORBIDDEN = 403;

    /**
     * 404: The server has not found anything matching the Request-URI. No
     *   indication is given of whether the condition is temporary or
     *   permanent.
     */
    public static final int HTTP_NOT_FOUND = 404;

    /**
     * 405: The method specified in the Request-Line is not allowed for the
     *	resource identified by the Request-URI.
     */
    public static final int HTTP_BAD_METHOD = 405;

    /**
     * 406: The resource identified by the request is only capable of generating
     *   response entities which have content characteristics not acceptable
     *   according to the accept headers sent in the request.
     */
    public static final int HTTP_NOT_ACCEPTABLE = 406;

    /**
     * 407: This code is similar to 401 (Unauthorized), but indicates that the
     *	client must first authenticate itself with the proxy.
     */
    public static final int HTTP_PROXY_AUTH = 407;

    /**
     * 408: The client did not produce a request within the time that the server
     *   was prepared to wait. The client MAY repeat the request without
     *   modifications at any later time.
     */
    public static final int HTTP_CLIENT_TIMEOUT = 408;

    /**
     * 409: The request could not be completed due to a conflict with
     * the current state of the resource.
     */
    public static final int HTTP_CONFLICT = 409;

    /**
     * 410: The requested resource is no longer available at the server and no
     *	forwarding address is known.
     */
    public static final int HTTP_GONE = 410;

    /**
     * 411: The server refuses to accept the request without a defined Content-
     *	Length.
     */
    public static final int HTTP_LENGTH_REQUIRED = 411;

    /**
     * 412: The precondition given in one or more of the request-header fields
     *	evaluated to false when it was tested on the server.
     */
    public static final int HTTP_PRECON_FAILED = 412;

    /**
     * 413: The server is refusing to process a request because the request
     *	entity is larger than the server is willing or able to process.
     */
    public static final int HTTP_ENTITY_TOO_LARGE = 413;

    /**
     * 414: The server is refusing to service the request because the
     * Request-URI is longer than the server is willing to interpret.
     */
    public static final int HTTP_REQ_TOO_LONG = 414;

    /**
     * 415: The server is refusing to service the request because the entity of
     *   the request is in a format not supported by the requested resource
     *   for the requested method.
     */
    public static final int HTTP_UNSUPPORTED_TYPE = 415;

    /**
     * 416: A server SHOULD return a response with this status code if a request
     *   included a Range request-header field , and none of
     *   the range-specifier values in this field overlap the current extent
     *   of the selected resource, and the request did not include an If-Range
     *   request-header field.
     */
    public static final int HTTP_UNSUPPORTED_RANGE = 416;

    /**
     * 417: The expectation given in an Expect request-header field
     *   could not be met by this server, or, if the server is a proxy,
     *   the server has unambiguous evidence that the request could not be met
     *   by the next-hop server.
     */
    public static final int HTTP_EXPECT_FAILED = 417;

    /** 5XX: server error */
    /**
     * 500: The server encountered an unexpected condition which prevented it
     *	from fulfilling the request.
     */
    public static final int HTTP_INTERNAL_ERROR = 500;

    /**
     * 501: The server does not support the functionality required to
     * fulfill the request.
     */
    public static final int HTTP_NOT_IMPLEMENTED = 501;

    /**
     * 502: The server, while acting as a gateway or proxy, received an invalid
     *   response from the upstream server it accessed in attempting to
     *   fulfill the request.
     */
    public static final int HTTP_BAD_GATEWAY = 502;

    /**
     * 503: The server is currently unable to handle the request due to a
     *   temporary overloading or maintenance of the server.
     */
    public static final int HTTP_UNAVAILABLE = 503;

    /**
     * 504: The server, while acting as a gateway or proxy, did not receive a
     *   timely response from the upstream server specified by the URI
     *   or some other auxiliary server it needed
     *   to access in attempting to complete the request.
     */
    public static final int HTTP_GATEWAY_TIMEOUT = 504;

    /**
     * 505: The server does not support, or refuses to support, the HTTP
     *  protocol version that was used in the request message.
     */
    public static final int HTTP_VERSION = 505;

    /**
     * Return a string representation of the URL for this connection.
     *
     * @return the string representation of the URL for this connection.
     */
    public String getURL();

    /**
     * Returns the protocol name of the URL of this
     * <code>HttpConnection</code>. e.g., http or https
     *
     * @return  the protocol of the URL of this <code>HttpConnection</code>.
     */
    public String getProtocol();

    /**
     * Returns the host information of the URL of
     * this <code>HttpConnection</code>. e.g. host name or IPv4 address
     *
     * @return  the host information of the URL of
     * this <code>HttpConnection</code>.
     */
    public String getHost();

    /**
     * Returns the file portion of the URL of this <code>HttpConnection</code>.
     *
     * @return  the file portion of the URL of this <code>HttpConnection</code>.
     * <code>null</code> is returned if there is no file.
     */
    public String getFile();

    /**
     * Returns the ref portion of the URL of this <code>HttpConnection</code>.
     * RFC2396 specifies the optional fragment identifier as the
     * the text after the crosshatch (#) character in the URL.
     * This information  may be used by the user agent as additional
     * reference information after the resource is successfully retrieved.
     * The format and interpretation of the fragment identifier is dependent
     * on the media type[RFC2046] of the retrieved information.
     *
     * @return  the ref portion of the URL of this <code>HttpConnection</code>.
     * <code>null</code> is returned if there is no value.
     */
    public String getRef();

    /**
     * Returns the query portion of the URL of this
     * <code>HttpConnection</code>.
     *
     * RFC2396 defines the query component as the text after the first
     * question-mark (?)  character in the URL.
     *
     * @return  the query portion of the URL of this
     *  <code>HttpConnection</code>.
     * <code>null</code> is returned if there is no value.  */
    public String getQuery();

    /**
     * Returns the network port number of the URL for this
     * <code>HttpConnection</code>.
     *
     * @return  the network port number of the URL for this
     * <code>HttpConnection</code>.
     * The default HTTP port number (80) is returned if there was
     * no port number in the string passed to <code>Connector.open</code>.
     */
    public int getPort();

    /**
     * Get the current request method. e.g. HEAD, GET, POST
     * The default value is GET.
     * @return the HTTP request method
     * @see #setRequestMethod
     */
    public String getRequestMethod();

    /**
     * Set the method for the URL request, one of:
     * <UL>
     *  <LI>GET
     *  <LI>POST
     *  <LI>HEAD
     * </UL> are legal, subject to protocol restrictions.  The default
     * method is GET.
     *
     * @param method the HTTP method
     * @exception IOException if the method cannot be reset or if
     *              the requested method isn't valid for HTTP.
     * @see #getRequestMethod
     */
    public void setRequestMethod(String method) throws IOException;

    /**
     * Returns the value of the named general request property for this
     * connection.
     *
     * @param key the keyword by which the request property is
     *  known (e.g., "accept").
     * @return  the value of the named general request property for this
     *           connection. If there is no key with the specified name then
     *		<CODE>null</CODE> is returned.
     * @see #setRequestProperty
     */
    public String getRequestProperty(String key);

    /**
     * Sets the general request property. If a property with the key already
     * exists, overwrite its value with the new value.
     *
     * <p> <strong>Note:</strong> HTTP requires all request properties
     * which can legally have multiple instances with the same key
     * to use a comma-separated list syntax which enables multiple
     * properties to be appended into a single property.
     *
     * @param   key     the keyword by which the request is known
     *                  (e.g., "<code>accept</code>").
     * @param   value   the value associated with it.
     * @exception IOException is thrown if the connection is in the
     *  connected state.
     * @see #getRequestProperty
     */
    public void setRequestProperty(String key, String value) throws IOException;

    /**
     * Returns the HTTP response status code.
     * It parses responses like:
     * <PRE>
     * HTTP/1.0 200 OK
     * HTTP/1.0 401 Unauthorized
     * </PRE>
     * and extracts the ints 200 and 401 respectively.
     * from the response (i.e., the response is not valid HTTP).
     * @exception IOException if an error occurred connecting to the server.
     * @return the HTTP Status-Code or -1 if no status code can be discerned.
     */
    public int getResponseCode() throws IOException;

    /**
     * Gets the HTTP response message, if any, returned along with the
     * response code from a server.  From responses like:
     * <PRE>
     * HTTP/1.0 200 OK
     * HTTP/1.0 404 Not Found
     * </PRE>
     * Extracts the Strings "OK" and "Not Found" respectively.
     * Returns null if none could be discerned from the responses
     * (the result was not valid HTTP).
     * @exception IOException if an error occurred connecting to the server.
     * @return the HTTP response message, or <code>null</code>
     */
    public String getResponseMessage() throws IOException;

    /**
     * Returns the value of the <code>expires</code> header field.
     *
     * @return  the expiration date of the resource that this URL references,
     *          or 0 if not known. The value is the number of milliseconds
     *          since January 1, 1970 GMT.
     * @exception IOException if an error occurred connecting to the server.
     */
    public long getExpiration() throws IOException;

    /**
     * Returns the value of the <code>date</code> header field.
     *
     * @return  the sending date of the resource that the URL references,
     *          or <code>0</code> if not known. The value returned is the
     *          number of milliseconds since January 1, 1970 GMT.
     * @exception IOException if an error occurred connecting to the server.
     */
    public long getDate() throws IOException;

    /**
     * Returns the value of the <code>last-modified</code> header field.
     * The result is the number of milliseconds since January 1, 1970 GMT.
     *
     * @return  the date the resource referenced by this
     *          <code>HttpConnection</code> was last modified, or
     *  0 if not known.
     * @exception IOException if an error occurred connecting to the server.
     */
    public long getLastModified() throws IOException;

    /**
     * Returns the value of the named header field.
     *
     * @param   name of a header field.
     * @return  the value of the named header field, or <code>null</code>
     *          if there is no such field in the header.
     * @exception IOException if an error occurred connecting to the server.
     */
    public String getHeaderField(String name) throws IOException;

    /**
     * Returns the value of the named field parsed as a number.
     * <p>
     * This form of <code>getHeaderField</code> exists because some
     * connection types (e.g., <code>http-ng</code>) have pre-parsed
     * headers. Classes for that connection type can override this method
     * and short-circuit the parsing.
     *
     * @param   name      the name of the header field.
     * @param   def   the default value.
     * @return  the value of the named field, parsed as an integer. The
     *          <code>def</code> value is returned if the field is
     *          missing or malformed.
     * @exception IOException if an error occurred connecting to the server.
     */
    public int getHeaderFieldInt(String name, int def) throws IOException;

    /**
     * Returns the value of the named field parsed as date.
     * The result is the number of milliseconds since January 1, 1970 GMT
     * represented by the named field.
     * <p>
     * This form of <code>getHeaderField</code> exists because some
     * connection types (e.g., <code>http-ng</code>) have pre-parsed
     * headers. Classes for that connection type can override this method
     * and short-circuit the parsing.
     *
     * @param   name     the name of the header field.
     * @param   def   a default value.
     * @return  the value of the field, parsed as a date. The value of the
     *          <code>def</code> argument is returned if the field is
     *          missing or malformed.
     * @exception IOException if an error occurred connecting to the server.
     */
    public long getHeaderFieldDate(String name, long def) throws IOException;


    /**
     * Gets a header field value by index.
     *
     * @return the value of the  nth  header field or
     * <code>null</code> if the array index is out of range.
     * An empty String is returned if the field does not have a value.
     * @param n the index of the header field
     * @exception IOException if an error occurred connecting to the server.
     */
    public String getHeaderField(int n) throws IOException;

    /**
     * Gets a header field key by index.
     *
     * @return the key of the nth header field or
     *  <code>null</code> if the array index is out of range.
     * @param n the index of the header field
     * @exception IOException if an error occurred connecting to the server.
     */
    public String getHeaderFieldKey(int n) throws IOException;

}
