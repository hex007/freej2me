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
 * This class is used to signal that a connection target
 * cannot be found, or the protocol type is not supported. 
 *
 * @author  Nik Shaylor
 * @version 12/17/01 (CLDC 1.1)
 * @since   CLDC 1.0
 */
public class ConnectionNotFoundException extends IOException {
    /**
     * Constructs a ConnectionNotFoundException with no detail 
     * message.
     */
    public ConnectionNotFoundException() {
        super();
    }

    /**
     * Constructs a ConnectionNotFoundException with the 
     * specified detail message.  A detail message is a String 
     * that describes this particular exception.
     * @param s the detail message
     */
    public ConnectionNotFoundException(String s) {
        super(s);
    }
}

