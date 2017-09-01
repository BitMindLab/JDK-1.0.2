/*
 * @(#)SocketOutputStream.java	1.9 95/11/06 Jonathan Payne
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package java.net;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This stream extends FileOutputStream to implement a
 * SocketOutputStream. Note that this class should <b>NOT</b> be
 * public.
 *
 * @version     1.9, 06 Nov 1995
 * @author 	Jonathan Payne
 * @author	Arthur van Hoff
 */
class SocketOutputStream extends FileOutputStream
{
    private SocketImpl impl;
    private byte temp[] = new byte[1];
    
    /**
     * Creates a new SocketOutputStream. Can only be called
     * by a Socket. This method needs to hang on to the owner Socket so
     * that the fd will not be closed.
     * @param impl the socket output stream inplemented
     */
    SocketOutputStream(SocketImpl impl) throws IOException {
	super(impl.getFileDescriptor());
	this.impl = impl;
    }

    /**
     * Writes to the socket.
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    private native void socketWrite(byte b[], int off, int len)
	throws IOException;

    /** 
     * Writes a byte to the socket. 
     * @param b the data to be written
     * @exception IOException If an I/O error has occurred. 
     */
    public void write(int b) throws IOException {
	temp[0] = (byte)b;
	socketWrite(temp, 0, 1);
    }

    /** 
     * Writes the contents of the buffer <i>b</i> to the socket.
     * @param b the data to be written
     * @exception SocketException If an I/O error has occurred. 
     */
    public void write(byte b[]) throws IOException {
	socketWrite(b, 0, b.length);
    }

    /** 
     * Writes <i>length</i> bytes from buffer <i>b</i> starting at 
     * offset <i>len</i>.
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception SocketException If an I/O error has occurred.
     */
    public void write(byte b[], int off, int len) throws IOException {
	socketWrite(b, off, len);
    }

    /**
     * Closes the stream.
     */
    public void close() throws IOException {
	impl.close();
    }

    /** 
     * Overrides finalize, the fd is closed by the Socket.
     */
    protected void finalize() {}
}
