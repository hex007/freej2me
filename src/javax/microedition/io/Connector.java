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

import com.sun.cldc.io.*;

import java.io.*;

/**
 * This class is factory for creating new Connection objects.
 * <p>
 * The creation of Connections is performed dynamically by looking
 * up a protocol implementation class whose name is formed from the
 * platform name (read from a system property) and the protocol name
 * of the requested connection (extracted from the parameter string
 * supplied by the application programmer.)
 *
 * The parameter string that describes the target should conform
 * to the URL format as described in RFC 2396.
 * This takes the general form:
 * <p>
 * <code>{scheme}:[{target}][{params}]</code>
 * <p>
 * where <code>{scheme}</code> is the name of a protocol such as
 * <i>http</i>}.
 * <p>
 * The <code>{target}</code> is normally some kind of network
 * address.
 * <p>
 * Any <code>{params}</code> are formed as a series of equates
 * of the form ";x=y".  Example: ";type=a".
 * <p>
 * An optional second parameter may be specified to the open
 * function. This is a mode flag that indicates to the protocol
 * handler the intentions of the calling code. The options here
 * specify if the connection is going to be read (READ), written
 * (WRITE), or both (READ_WRITE). The validity of these flag
 * settings is protocol dependent. For instance, a connection
 * for a printer would not allow read access, and would throw
 * an IllegalArgumentException. If the mode parameter is not
 * specified, READ_WRITE is used by default.
 * <p>
 * An optional third parameter is a boolean flag that indicates
 * if the calling code can handle timeout exceptions. If this
 * flag is set, the protocol implementation may throw an
 * InterruptedIOException when it detects a timeout condition.
 * This flag is only a hint to the protocol handler, and it
 * does not guarantee that such exceptions will actually be thrown.
 * If this parameter is not set, no timeout exceptions will be
 * thrown.
 * <p>
 * Because connections are frequently opened just to gain access
 * to a specific input or output stream, four convenience
 * functions are provided for this purpose.
 *
 * See also: {@link DatagramConnection DatagramConnection}
 * for information relating to datagram addressing
 *
 * @author  Nik Shaylor
 * @version 12/17/01 (CLDC 1.1)
 * @since   CLDC 1.0
 */

public class Connector {

/*
 * Implementation notes: The open parameter is used for
 * dynamically constructing a class name in the form:
 * <p>
 * <code>com.sun.cldc.io.{platform}.{protocol}.Protocol</code>
 * <p>
 * The platform name is derived from the system by looking for
 * the system property "microedition.platform".  If this property
 * key is not found or the associated class is not present, then
 * "j2me" is used by default.
 * <p>
 * The protocol name is derived from the parameter string
 * describing the target of the connection. This takes the from:
 * <p>
 * <code> {protocol}:[{target}][ {params}] </code>
 * <p>
 * The protocol name is used for dynamically finding the
 * appropriate protocol implementation class.  This information
 * is stripped from the target name that is given as a parameter
 * to the open() method. In order to avoid problems with illegal
 * class file names, all the '-' characters in the protocol name
 * are automatically converted into '_' characters.
 */

    /**
     * Access mode READ.
     */
    public final static int READ  = 1;

    /**
     * Access mode WRITE.
     */
    public final static int WRITE = 2;

    /**
     * Access mode READ_WRITE.
     */
    public final static int READ_WRITE = (READ|WRITE);

    /**
     * The platform name.
     */
    private static String platform;

    /**
     * True if we are running on a J2ME system
     */
    private static boolean j2me = true;

    /**
     * The root of the classes.
     */
    private static String classRoot;

    /**
     * Class initializer.
     */
    static {
        /* Find out if we are running on a J2ME system */
        if (System.getProperty("microedition.configuration") != null) {
            j2me = true;
        }

        /* Set up the platform name */
        platform = System.getProperty("microedition.platform");

        /* Set up the library class root path */
        /* This may vary from one CLDC implementation to another */
        classRoot = System.getProperty("javax.microedition.io.Connector.protocolpath");
        if (classRoot == null) {
            classRoot = "org.mini.net";
        }
    }

    /**
     * Prevent instantiation of this class.
     */
    private Connector() { }

    /**
     * Create and open a Connection.
     *
     * @param name             The URL for the connection.
     * @return                 A new Connection object.
     *
     * @exception IllegalArgumentException If a parameter is invalid.
     * @exception ConnectionNotFoundException If the target of the
     *   name cannot be found, or if the requested protocol type
     *   is not supported.
     * @exception IOException  If some other kind of I/O error occurs.
     * @exception SecurityException  May be thrown if access to the
     *   protocol handler is prohibited.
     */
    public static Connection open(String name) throws IOException {
        return open(name, READ_WRITE);
    }

    /**
     * Create and open a Connection.
     *
     * @param name             The URL for the connection.
     * @param mode             The access mode.
     * @return                 A new Connection object.
     *
     * @exception IllegalArgumentException If a parameter is invalid.
     * @exception ConnectionNotFoundException If the target of the
     *   name cannot be found, or if the requested protocol type
     *   is not supported.
     * @exception IOException  If some other kind of I/O error occurs.
     * @exception SecurityException  May be thrown if access to the
     *   protocol handler is prohibited.
     */
    public static Connection open(String name, int mode) throws IOException {
        return open(name, mode, false);
    }

    /**
     * Create and open a Connection.
     *
     * @param name             The URL for the connection
     * @param mode             The access mode
     * @param timeouts         A flag to indicate that the caller
     *                         wants timeout exceptions
     * @return                 A new Connection object
     *
     * @exception IllegalArgumentException If a parameter is invalid.
     * @exception ConnectionNotFoundException If the target of the
     *   name cannot be found, or if the requested protocol type
     *   is not supported.
     * @exception IOException  If some other kind of I/O error occurs.
     * @exception SecurityException  May be thrown if access to the
     *   protocol handler is prohibited.
     */
    public static Connection open(String name, int mode, boolean timeouts)
        throws IOException {

        /* If the "microedition.platform" property is defined,    */
        /* use that as the platform name for opening a connection */
        if (platform != null) {
            try {
                return openPrim(name, mode, timeouts, platform);
            } catch (ClassNotFoundException x) {
            }
        }

        /* If the "microedition.platform" property is not defined, */
        /* use one of the default values */
        try {
            return openPrim(name, mode, timeouts, j2me ? "" : "");
        } catch (ClassNotFoundException x) {
        }

        throw new ConnectionNotFoundException(
                  "The requested protocol does not exist " + name);
    }

    /**
     * Create and open a Connection.
     *
     * @param name           The URL for the connection
     * @param mode             The access mode
     * @param timeouts         A flag to indicate that the caller
     *                         wants timeout exceptions
     * @param platform         Platform name
     * @return                 A new Connection object
     *
     * @exception ClassNotFoundException  If the protocol cannot be found.
     * @exception IllegalArgumentException If a parameter is invalid.
     * @exception ConnectionNotFoundException If the target of the
     *   name cannot be found, or if the requested protocol type
     *   is not supported.
     * @exception IOException If some other kind of I/O error occurs.
     * @exception IllegalArgumentException If a parameter is invalid.
     */
    private static Connection openPrim(String name, int mode,
        boolean timeouts, String platform)
        throws IOException, ClassNotFoundException {

        /* Test for null argument */
        if (name == null) {
            throw new IllegalArgumentException("Null URL");
        }

        /* Look for : as in "http:", "file:", or whatever */
        int colon = name.indexOf(':');

        /* Test for null argument */
        if (colon < 1) {
            throw new IllegalArgumentException("no ':' in URL");
        }

        try {
            String protocol;

            /* Strip off the protocol name */
            protocol = name.substring(0, colon);

            /* Strip off the rest of the string */
            name = name.substring(colon+1);

            /* Convert all the '-' characters in the protocol */
            /* name to '_' characters (dashes are not allowed */
            /* in class names).  This operation creates garbage */
            /* only if the protocol name actually contains dashes */
            protocol = protocol.replace('-', '_');

            /* Use the platform and protocol names to look up */
            /* a class to implement the connection */
            Class clazz =
                Class.forName(classRoot +
//                              "." + platform +
                              "." + protocol + ".Protocol");

            /* Construct a new instance of the protocol */
            ConnectionBaseInterface uc =(ConnectionBaseInterface)clazz.newInstance();

            /* Open the connection, and return it */
            return uc.openPrim(name, mode, timeouts);

        } catch (InstantiationException x) {
            throw new IOException(x.toString());
        } catch (IllegalAccessException x) {
            throw new IOException(x.toString());
        } catch (ClassCastException x) {
            throw new IOException(x.toString());
        }
    }

    /**
     * Create and open a connection input stream.
     *
     * @param  name            The URL for the connection.
     * @return                 A DataInputStream.
     *
     * @exception IllegalArgumentException If a parameter is invalid.
     * @exception ConnectionNotFoundException If the target of the
     *   name cannot be found, or if the requested protocol type
     *   is not supported.
     * @exception IOException  If some other kind of I/O error occurs.
     * @exception SecurityException  May be thrown if access to the
     *   protocol handler is prohibited.
     */
    public static DataInputStream openDataInputStream(String name)
        throws IOException {

        InputConnection con =
            (InputConnection)Connector.open(name, Connector.READ);

        try {
            return con.openDataInputStream();
        } finally {
            con.close();
        }
    }

    /**
     * Create and open a connection output stream.
     *
     * @param  name            The URL for the connection.
     * @return                 A DataOutputStream.
     *
     * @exception IllegalArgumentException If a parameter is invalid.
     * @exception ConnectionNotFoundException If the target of the
     *   name cannot be found, or if the requested protocol type
     *   is not supported.
     * @exception IOException  If some other kind of I/O error occurs.
     * @exception SecurityException  May be thrown if access to the
     *   protocol handler is prohibited.
     */
    public static DataOutputStream openDataOutputStream(String name)
        throws IOException {

        OutputConnection con =
            (OutputConnection)Connector.open(name, Connector.WRITE);

        try {
            return con.openDataOutputStream();
        } finally {
            con.close();
        }
    }

    /**
     * Create and open a connection input stream.
     *
     * @param  name            The URL for the connection.
     * @return                 An InputStream.
     *
     * @exception IllegalArgumentException If a parameter is invalid.
     * @exception ConnectionNotFoundException If the target of the
     *   name cannot be found, or if the requested protocol type
     *   is not supported.
     * @exception IOException  If some other kind of I/O error occurs.
     * @exception SecurityException  May be thrown if access to the
     *   protocol handler is prohibited.
     */
    public static InputStream openInputStream(String name)
        throws IOException {

        return openDataInputStream(name);
    }

    /**
     * Create and open a connection output stream.
     *
     * @param  name            The URL for the connection.
     * @return                 An OutputStream.
     *
     * @exception IllegalArgumentException If a parameter is invalid.
     * @exception ConnectionNotFoundException If the target of the
     *   name cannot be found, or if the requested protocol type
     *   is not supported.
     * @exception IOException  If some other kind of I/O error occurs.
     * @exception SecurityException  May be thrown if access to the
     *   protocol handler is prohibited.
     */
    public static OutputStream openOutputStream(String name)
        throws IOException {

        return openDataOutputStream(name);
    }

}

