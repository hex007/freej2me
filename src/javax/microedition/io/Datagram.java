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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * This class defines an abstract interface for datagram packets.
 * The implementations of this interface hold data to be
 * sent or received from a <code>DatagramConnection</code> object.
 * <p>
 * Since this is an interface class, the internal structure
 * of the datagram packets is not defined here.  However, it is
 * assumed that each implementation of this interface will
 * provide the following fields / state variables (the actual
 * implementation and the names of these fields may vary):
 * <ul>
 * <li><i>buffer</i>: the internal buffer in which data is stored
 * <li><i>offset</i>: the read/write offset for the internal buffer
 * <li><i>length</i>: the length of the data in datagram packet
 * <li><i>address</i>: the destination or source address
 * <li><i>read/write pointer</i>: a pointer that is added to the
 *   <i>offset</i> to point to the current data location during a
 *   read or write operation
 * </ul>
 * <p>
 * <strong>Reading and Writing</strong>
 * <p>
 * The <code>Datagram</code> interface extends interfaces
 * <code>DataInput</code> and <code>DataOutput</code> in order
 * to provide a simple way to read and write binary data in and out of
 * the datagram buffer instead of using <code>getData</code> and
 * <code>setData</code> methods. Writing automatically increments
 * <i>length</i> and reading will continue while the <i>read/write pointer</i>
 * is less than <i>length</i>. Before any writing is done reset must be called.
 * If <code>setData()</code> is to be used when reading or writing, any value
 * for the <code>offset</code> parameter other than 0 is not supported.
 * <p>
 * For example to write to datagram:
 * <pre>
 *    datagram = connection.newDatagram(max);
 *
 *    // Reset prepares the datagram for writing new message.
 *    datagram.reset();
 *
 *    // writeUTF automatically increases the datagram length.
 *    datagram.writeUTF("hello world");
 *
 *    connection.send(datagram);
 * </pre>
 * For example to read from a datagram (single use only):
 * <pre>
 *    datagram = connection.newDatagram(max);
 *
 *    connection.receive(datagram);
 *
 *    message = datagram.readUTF();
 * </pre>
 * <strong>Reusing Datagrams</strong>
 * <p>
 * It should be noted the <i>length</i> above is returned
 * from <code>getLength</code> and can have different meanings at different
 * times. When sending <i>length</i> is the number of bytes to send. Before
 * receiving <i>length</i> is the maximum number of bytes to receive.
 * After receiving <i>length</i> is the number of bytes that were received.
 * So when reusing a datagram to receive after sending or receiving, length
 * must be set back to the maximum using <code>setLength</code>.
 * <pre>
 *    datagram = connection.newDatagram(max);
 *
 *    while (notDone) {
 *
 *        // The last receive in the loop changed the length
 *        // so put it back to the maximum length.
 *        datagram.setLength(max);
 *        connection.receive(datagram);
 *
 *        data = datagram.getData();
 *        bytesReceived = datagram.getLength();
 *
 *        // process datagram ...
 *    }
 * </pre>
 * When reading instead of using <code>getData</code> the <code>reset</code>
 * method must be used.
 * <pre>
 *    datagram = connection.newDatagram(max);
 *
 *    while (notDone) {
 *
 *        // The last read in the loop changed the read pointer
 *        // so reset the pointer.
 *        datagram.reset();
 *        datagram.setLength(max);
 *        connection.receive(datagram);
 *
 *        message = datagram.readUTF(message);
 *
 *        // process message ...
 *    }
 * </pre>
 * For example to reread a datagram:
 * <pre>
 *    connection.receive(datagram);
 *
 *    message = datagram.readUTF(message);
 *
 *    len = datagram.getLength();
 *
 *    datagram.reset();
 *
 *    datagram.setLength(len);
 *
 *    copy = datagram.readUTF(message);
 * </pre>
 *
 * @author  Brian Modra, Nik Shaylor, Stephen Flores
 * @version 12/17/01 (CLDC 1.1)
 * @since   CLDC 1.0
 */
public interface Datagram extends DataInput, DataOutput {

    /**
     * Get the address of the datagram.
     *
     * @return the address in string form, or null if no address was set
     *
     * @see #setAddress
     */
    public String getAddress();

    /**
     * Get the contents of the data buffer.
     * <p>
     * Depending on the implementation, this operation may return 
     * the internal buffer or a copy of it.  However, the user
     * must not assume that the contents of the internal data 
     * buffer can be manipulated by modifying the data returned by
     * this operation.  Rather, the <code>setData</code> operation
     * should be used for changing the contents of the internal
     * buffer.
     *
     * @return the data buffer as a byte array
     *
     * @see #setData
     */
    public byte[] getData();

    /**
     * Get the length of the datagram.
     *
     * @return the length state variable
     *
     * @see #setLength
     */
    public int getLength();

    /**
     * Get the offset.
     *
     * @return the offset state variable
     */
    public int getOffset();

    /**
     * Set datagram address.
     * <p>
     * The actual addressing scheme is implementation-dependent.
     * Please read the general comments on datagram addressing
     * in <code>DatagramConnection.java</code>.
     * <p>
     * Note that if the address of a datagram is not specified, then 
     * it defaults to that of the connection.
     *
     * @param addr the new target address as a URL
     * @exception IllegalArgumentException if the address is not valid
     * @exception IOException if a some kind of I/O error occurs
     *
     * @see #getAddress
     */
    public void setAddress(String addr) throws IOException;

    /**
     * Set datagram address, copying the address from another datagram.
     *
     * @param reference to the datagram whose address will be copied as
     * the new target address for this datagram.
     * @exception IllegalArgumentException if the address is not valid
     *
     * @see #getAddress
     */
    public void setAddress(Datagram reference);

    /**
     * Set the <code>length</code> state variable.
     *
     * @param len the new length of the datagram
     * @exception IllegalArgumentException if the length or length plus offset
     *            fall outside the buffer
     *
     * @see #getLength
     */
    public void setLength(int len);

    /**
     * Set the <code>buffer</code>, <code>offset</code> and <code>length</code>
     * state variables.  Depending on the implementation, this operation may 
     * copy the buffer or just set the state variable <code>buffer</code>
     * to the value of the <code>buffer</code> argument.  However,
     * the user must not assume that the contents of the internal data
     * buffer can be manipulated by modifying the buffer passed on to 
     * this operation.
     *
     * @param buffer  the data buffer
     * @param offset  the offset into the data buffer
     * @param len     the length of the data in the buffer
     * @exception IllegalArgumentException if the length or offset
     *                or offset plus length fall outside the 
     *                buffer, or if the buffer parameter is invalid
     * @see #getData
     */
    public void setData(byte[] buffer, int offset, int len);

    /**
     * Zero the <code>read/write pointer</code> as well as the
     * <code>offset</code> and <code>length</code> state variables.
     */
    public void reset();

}

