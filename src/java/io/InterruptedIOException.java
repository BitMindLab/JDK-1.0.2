/*
 * @(#)IOException.java	1.10 95/08/09  
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

package java.io;

/**
 * Signals that an I/O operation has been interrupted.
 * @see	java.io.InputStream
 * @see	java.io.OutputStream
 * @version 	1.10, 09 Aug 1995
 */
public
class InterruptedIOException extends IOException {
    /**
     * Constructs an IOException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public InterruptedIOException() {
	super();
    }

    /**
     * Constructs an IOException with the specified detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     */
    public InterruptedIOException(String s) {
	super(s);
    }

    /**
     * Reports how many bytes had been transferred as
     * part of the IO operation before it was interrupted.
     */ 
    public int bytesTransferred = 0;
    
}
