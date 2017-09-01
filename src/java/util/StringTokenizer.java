/*
 * @(#)StringTokenizer.java	1.13 95/08/10  
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

package java.util;

import java.lang.*;

/**
 * StringTokenizer is a class that controls simple linear tokenization
 * of a String. The set of delimiters, which defaults to common 
 * whitespace characters, may be specified at creation time or on a 
 * per-token basis.<p>
 *
 * Example usage:
 * <pre>
 *	String s = "this is a test";
 *	StringTokenizer st = new StringTokenizer(s);
 *	while (st.hasMoreTokens()) {
 *		println(st.nextToken());
 *	}
 * </pre>
 * Prints the following on the console:
 * <pre>
 *	this
 *	is
 *	a
 *	test
 * </pre>
 * @version 	1.13, 10 Aug 1995
 */
public
class StringTokenizer implements Enumeration {
    private int currentPosition;
    private int maxPosition;
    private String str;
    private String delimiters;
    private boolean retTokens;

    /**
     * Constructs a StringTokenizer on the specified String, using the
     * specified delimiter set.
     * @param str	   the input String
     * @param delim        the delimiter String
     * @param returnTokens returns delimiters as tokens or skip them
     */
    public StringTokenizer(String str, String delim, boolean returnTokens) {
	currentPosition = 0;
	this.str = str;
	maxPosition = str.length();
	delimiters = delim;
	retTokens = returnTokens;
    }

    /**
     * Constructs a StringTokenizer on the specified String, using the
     * specified delimiter set.
     * @param str	the input String
     * @param delim the delimiter String
     */
    public StringTokenizer(String str, String delim) {
	this(str, delim, false);
    }

    /**
     * Constructs a StringTokenizer on the specified String, using the
     * default delimiter set (which is " \t\n\r").
     * @param str the String
     */
    public StringTokenizer(String str) {
	this(str, " \t\n\r", false);
    }

    /**
     * Skips delimiters.
     */
    private void skipDelimiters() {
	while (!retTokens &&
	       (currentPosition < maxPosition) &&
	       (delimiters.indexOf(str.charAt(currentPosition)) >= 0)) {
	    currentPosition++;
	}
    }

    /**
     * Returns true if more tokens exist.
     */
    public boolean hasMoreTokens() {
	skipDelimiters();
	return (currentPosition < maxPosition);
    }

    /**
     * Returns the next token of the String.
     * @exception NoSuchElementException If there are no more 
     * tokens in the String.
     */
    public String nextToken() {
	skipDelimiters();

	if (currentPosition >= maxPosition) {
	    throw new NoSuchElementException();
	}

	int start = currentPosition;
	while ((currentPosition < maxPosition) && 
	       (delimiters.indexOf(str.charAt(currentPosition)) < 0)) {
	    currentPosition++;
	}
	if (retTokens && (start == currentPosition) &&
	    (delimiters.indexOf(str.charAt(currentPosition)) >= 0)) {
	    currentPosition++;
	}
	return str.substring(start, currentPosition);
    }

    /**
     * Returns the next token, after switching to the new delimiter set.
     * The new delimiter set remains the default after this call.
     * @param delim the new delimiters
     */
    public String nextToken(String delim) {
	delimiters = delim;
	return nextToken();
    }

    /**
     * Returns true if the Enumeration has more elements.
     */
    public boolean hasMoreElements() {
	return hasMoreTokens();
    }

    /**
     * Returns the next element in the Enumeration.
     * @exception NoSuchElementException If there are no more elements 
     * in the enumeration.
     */
    public Object nextElement() {
	return nextToken();
    }

    /**
     * Returns the next number of tokens in the String using
     * the current deliminter set.  This is the number of times
     * nextToken() can return before it will generate an exception.
     * Use of this routine to count the number of tokens is faster
     * than repeatedly calling nextToken() because the substrings
     * are not constructed and returned for each token.
     */
    public int countTokens() {
	int count = 0;
	int currpos = currentPosition;

	while (currpos < maxPosition) {
	    /*
	     * This is just skipDelimiters(); but it does not affect
	     * currentPosition.
	     */
	    while (!retTokens &&
		   (currpos < maxPosition) &&
		   (delimiters.indexOf(str.charAt(currpos)) >= 0)) {
		currpos++;
	    }

	    if (currpos >= maxPosition) {
		break;
	    }

	    int start = currpos;
	    while ((currpos < maxPosition) && 
		   (delimiters.indexOf(str.charAt(currpos)) < 0)) {
		currpos++;
	    }
	    if (retTokens && (start == currpos) &&
		(delimiters.indexOf(str.charAt(currpos)) >= 0)) {
		currpos++;
	    }
	    count++;

	}
	return count;
    }
}


