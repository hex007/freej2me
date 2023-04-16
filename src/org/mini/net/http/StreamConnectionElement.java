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

import javax.microedition.io.StreamConnection;
import java.io.*;

/**
 * This class implements the necessary functionality
 * for an HTTP connection pool element container. Each
 * element contains appropriate http connection information
 * including a reference to the underlying socket stream connection.
 * 
 * @version 1.0
 */
public class StreamConnectionElement  
    implements StreamConnection {

    /** Current protocol. */
    String                      m_protocol;
    /** Current host name. */
    String                    m_host;
    /** Http port number. */
    int                       m_port;
    /** Connection stream to http server. */
    private StreamConnection          m_stream;
    /** Input stream to http server. */
    private DataInputStream             m_data_input_stream;
    /** Output stream to http server. */
    private DataOutputStream            m_data_output_stream;
    /** In use flag. */
    boolean                   m_in_use;
    /** Start time in milliseconds. */
    long                      m_time;
    /** Removed from pool flag while in use. (lingered too long) */
    boolean m_removed;
    
    /**
     * Create a new instance of this class.
     *
     * @param p_protocol protocol for the connection
     * @param p_host     hostname for the connection
     * @param p_port     port number for the connection
     * @param p_sc       stream connection
     * @param p_dos      data output stream from the stream connection
     * @param p_dis      data input stream from the stream connection
     */
    StreamConnectionElement(String p_protocol,
                            String p_host,
                            int p_port,
                            StreamConnection p_sc,
                            DataOutputStream p_dos,
                            DataInputStream p_dis) {
        m_protocol = p_protocol;
        m_host = p_host;
        m_port = p_port;
        m_stream = p_sc;
        m_data_output_stream = p_dos;
        m_data_input_stream = p_dis;
        m_time = System.currentTimeMillis();
    }

    /**
     * Clear the fields of the saved connection and release any 
     * system resources. Any open input and output streams are closed
     * as well as the connection itself.
     */
    public void close() {
	try {
	    if (m_data_output_stream != null) {
		m_data_output_stream.close();
		m_data_output_stream = null;
	    }
	    if (m_data_input_stream != null) {
		m_data_input_stream.close();
		m_data_input_stream = null;
	    }
	    if (m_stream != null) {
		m_stream.close();
		m_stream = null;
	    }
	} catch (IOException ioe) {
	    /*
	     * No special processing for errors, since the 
	     * the cached connection is no longer in use, and
	     * the server may already have shutdown its end
	     * of the connection.
	     */
	}
    }

    /**
     * Get the stream connection for this element.
     *
     * @return                     base stream connection
     */
    public StreamConnection getBaseConnection() {
        return m_stream;
    }

    /**
     * Get the current output stream for the stream connection.
     *
     * @return                     output stream
     */
    public OutputStream openOutputStream() {
        return m_data_output_stream;
    }

    /**
     * Get the current data output stream for the stream connection.
     *
     * @return                     data output stream
     */
    public DataOutputStream openDataOutputStream() { 
        return m_data_output_stream;
    }
    /**
     * Get the current data stream for the stream connection.
     *
     * @return                     input stream
     */
    public InputStream openInputStream() { 
        return m_data_input_stream;
    }
    /**
     * Get the current data input stream for the stream connection.
     *
     * @return                     data input stream
     */
    public DataInputStream openDataInputStream() { 
        return m_data_input_stream;
    }

}
                        
