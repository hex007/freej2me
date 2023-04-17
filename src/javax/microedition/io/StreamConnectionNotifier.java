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
 * This interface defines the capabilities that a connection notifier
 * must have.
 *
 * @author  Nik Shaylor
 * @version 03/13/02 (CLDC 1.1)
 * @since   CLDC 1.0
 */
public interface StreamConnectionNotifier extends Connection {
    /**
     * Returns a <code>StreamConnection</code> object that represents
     * a server side socket connection.  The method blocks until
     * a connection is made.
     *
     * @return  A <code>StreamConnection</code> to communicate with a client.
     * @exception  IOException  If an I/O error occurs.
     */
    public StreamConnection acceptAndOpen() throws IOException;
}

