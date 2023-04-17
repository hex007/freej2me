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

/**
 * This interface defines the stream connection over which
 * content is passed.
 *
 * @author  Nik Shaylor
 * @version 12/17/01 (CLDC 1.1)
 * @since   CLDC 1.0
 */
public interface ContentConnection extends StreamConnection {

    /**
     * Returns the type of content that the resource connected to is
     * providing.  For instance, if the connection is via HTTP, then
     * the value of the <code>content-type</code> header field is returned.
     *
     * @return  the content type of the resource that the URL references,
     *          or <code>null</code> if not known.
     */
    public String getType();

    /**
     * Returns a string describing the encoding of the content which
     * the resource connected to is providing.
     * E.g. if the connection is via HTTP, the value of the
     * <code>content-encoding</code> header field is returned.
     *
     * @return  the content encoding of the resource that the URL
     *          references, or <code>null</code> if not known.
     */
    public String getEncoding();

    /**
     * Returns the length of the content which is being provided.
     * E.g. if the connection is via HTTP, then the value of the
     * <code>content-length</code> header field is returned.
     *
     * @return  the content length of the resource that this connection's
     *          URL references, or <code>-1</code> if the content length
     *          is not known.
     */
    public long getLength();

}

