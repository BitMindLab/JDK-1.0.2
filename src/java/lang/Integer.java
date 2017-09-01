/*
 * @(#)Integer.java	1.30 96/03/29  
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

package java.lang;

/**
 * The Integer class is a wrapper for integer values.  In Java, integers are not 
 * objects and most of the Java utility classes require the use of objects.  Thus, 
 * if you needed to store an integer in a hashtable, you would have to "wrap" an 
 * Integer instance around it.
 * @version 	1.30, 29 Mar 1996
 * @author	Lee Boynton
 * @author	Arthur van Hoff
 */
public final
class Integer extends Number {
    /**
     * The minimum value an Integer can have.  The lowest minimum value an
     * Integer can have is 0x80000000.
     */
    public static final int   MIN_VALUE = 0x80000000;

    /**
     * The maximum value an Integer can have.  The greatest maximum value an
     * Integer can have is 0x7fffffff.
     */
    public static final int   MAX_VALUE = 0x7fffffff;

    /**
     * Returns a new String object representing the specified integer in
     * the specified radix.
     * @param i		the integer to be converted
     * @param radix	the radix
     * @see Character#MIN_RADIX
     * @see Character#MAX_RADIX
     */
    public static String toString(int i, int radix) {
	if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
	    radix = 10;
	StringBuffer buf = new StringBuffer(radix >= 8 ? 12 : 33);
	boolean negative = (i < 0);
        if (!negative)
	    i = -i;
	while (i <= -radix) {
	    buf.append(Character.forDigit(-(i % radix), radix));
	    i = i / radix;
	}
	buf.append(Character.forDigit(-i, radix));
	if (negative)
	    buf.append('-');
        return buf.reverse().toString();
    }

    /**
     * Returns a new String object representing the specified integer 
     * as unsigned hexidecimal number. 
     */
    public static String toHexString(int i) {
	return toUnsignedString(i, 4);
    }

    /**
     * Returns a new String object representing the specified integer 
     * as unsigned octal number. 
     */
    public static String toOctalString(int i) {
	return toUnsignedString(i, 3);
    }

    /**
     * Returns a new String object representing the specified integer 
     * as unsigned binary number. 
     */
    public static String toBinaryString(int i) {
	return toUnsignedString(i, 1);
    }

    /** 
     * Convert the integer to an unsigned number.
     */
    private static String toUnsignedString(int i, int shift) {
	StringBuffer buf = new StringBuffer(shift >= 3 ? 11 : 32);
	int radix = 1 << shift;
	int mask = radix - 1;
	do {
	    buf.append(Character.forDigit(i & mask, radix));
	    i >>>= shift;
	} while (i != 0);
	return buf.reverse().toString();
    }


    /**
     * Returns a new String object representing the specified integer. The radix
     * is assumed to be 10.
     * @param i	the integer to be converted
     */
    public static String toString(int i) {
	return toString(i,10);
    }
    
    /**
     * Assuming the specified String represents an integer, returns that integer's
     * value. Throws an exception if the String cannot be parsed as an int.
     * @param s		the String containing the integer
     * @param radix 	the radix to be used
     * @exception	NumberFormatException If the String does not contain a parsable 
     *                                        integer.
     */
    public static int parseInt(String s, int radix) throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }
	int result = 0;
	boolean negative = false;
	int i=0, max = s.length();
	if (max > 0) {
	    if (s.charAt(0) == '-') {
		negative = true;
		i++;
	    }
	    while (i < max) {
		int digit = Character.digit(s.charAt(i++),radix);
		if (digit < 0)
		    throw new NumberFormatException(s);
		result = result * radix + digit;
	    }
	} else
	    throw new NumberFormatException(s);
	if (negative)
	    return -result;
	else
	    return result;
    }

    /**
     * Assuming the specified String represents an integer, returns that integer's
     * value. Throws an exception if the String cannot be parsed as an int.
     * The radix is assumed to be 10.
     * @param s		the String containing the integer
     * @exception	NumberFormatException If the string does not contain a parsable 
     *                                        integer.
     */
    public static int parseInt(String s) throws NumberFormatException {
	return parseInt(s,10);
    }

    /**
     * Assuming the specified String represents an integer, returns a new Integer
     * object initialized to that value. Throws an exception if the String cannot be
     * parsed as an int.
     * @param s		the String containing the integer
     * @param radix 	the radix to be used
     * @exception	NumberFormatException If the String does not contain a parsable 
     *                                        integer.
     */
    public static Integer valueOf(String s, int radix) throws NumberFormatException {
	return new Integer(parseInt(s,radix));
    }

    /**
     * Assuming the specified String represents an integer, returns a new Integer
     * object initialized to that value. Throws an exception if the String cannot be
     * parsed as an int. The radix is assumed to be 10.
     * @param s		the String containing the integer
     * @exception	NumberFormatException If the String does not contain a parsable 
     *                                        integer.
     */
    public static Integer valueOf(String s) throws NumberFormatException
    {
	return new Integer(parseInt(s, 10));
    }

    /**
     * The value of the Integer.
     */
    private int value;

    /**
     * Constructs an Integer object initialized to the specified int value.
     * @param value	the initial value of the Integer
     */
    public Integer(int value) {
	this.value = value;
    }

    /**
     * Constructs an Integer object initialized to the value specified by the
     * String parameter.  The radix is assumed to be 10.
     * @param s		the String to be converted to an Integer
     * @exception	NumberFormatException If the String does not contain a parsable 
     *                                        integer.
     */
    public Integer(String s) throws NumberFormatException {
	this.value = parseInt(s, 10);
    }

    /**
     * Returns the value of this Integer as an int.
     */
    public int intValue() {
	return value;
    }

    /**
     * Returns the value of this Integer as a long.
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the value of this Integer as a float.
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the value of this Integer as a double.
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a String object representing this Integer's value.
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Returns a hashcode for this Integer.
     */
    public int hashCode() {
	return value;
    }

    /**
     * Compares this object to the specified object.
     * @param obj	the object to compare with
     * @return 		true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Integer)) {
	    return value == ((Integer)obj).intValue();
	}
	return false;
    }

    /**
     * Gets an Integer property. If the property does not
     * exist, it will return 0.
     * @param nm the property name
     */
    public static Integer getInteger(String nm) {
	return getInteger(nm, null);
    }

    /**
     * Gets an Integer property. If the property does not
     * exist, it will return val. Deals with hexadecimal
     * and octal numbers.
     * @param nm the String name
     * @param val the Integer value
     */
    public static Integer getInteger(String nm, int val) {
        Integer result = getInteger(nm, null);
        return (result == null) ? new Integer(val) : result;
    }

    /**
     * Gets an Integer property. If the property does not
     * exist, it will return val. Deals with hexadecimal
     * and octal numbers.
     * @param nm the property name
     * @param val the integer value
     */
    public static Integer getInteger(String nm, Integer val) {
	String v = System.getProperty(nm);
	if (v != null) {
	    try {
		return Integer.decode(v);
	    } catch (NumberFormatException e) {
	    }
	}	
	return val;
    }

    /**
     * Decodes a string into an Integer. Deals with decimal, hexadecimal,
     * and octal numbers.
     * @param nm the string to decode
     */
    private static Integer decode(String nm) throws NumberFormatException {
	if (nm.startsWith("0x")) {
	    return Integer.valueOf(nm.substring(2), 16);
	}
	if (nm.startsWith("#")) {
	    return Integer.valueOf(nm.substring(1), 16);
	}
	if (nm.startsWith("0") && nm.length() > 1) {
	    return Integer.valueOf(nm.substring(1), 8);
	}
	
	return Integer.valueOf(nm);
    }
}


