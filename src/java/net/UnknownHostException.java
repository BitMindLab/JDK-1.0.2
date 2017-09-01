/*
 * @(#)UnknownHostException.java	1.5 95/08/16 Jonathan Payne
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

import java.io.IOException;

/**
 * Signals that the address of the server specified by a network client could not
 * be resolved.
 * 
 * @version     1.5,16 Aug 1995
 * @author      Jonathan Payne 
 */
public 
class UnknownHostException extends IOException {
    
    /**
     * Constructs a new UnknownHostException with the specified detail message.
     * A detail message is a String that gives a specific description
     * of this error. 
     * @param host the detail message
     */
    public UnknownHostException(String host) {
	super(host);
    }

    /**
     * Constructs a new UnknownHostException with no detail message. 
     * A detail message is a String that gives a specific description
     * of this error.  
     */
    public UnknownHostException() {
    }
}

