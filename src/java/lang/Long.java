/*
 * @(#)Long.java	1.23 96/01/02  
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
 * The Long class provides an object wrapper for Long data values and serves as 
 * a place for long-oriented operations.  A wrapper is useful because most of Java's
 * utility classes require the use of objects.  Since longs are not objects in Java,
 * they need to be "wrapped" in a Long instance.
 * @version 	1.23, 02 Jan 1996
 * @author	Lee Boynton
 * @author	Arthur van Hoff
 */
public final
class Long extends Number {
    /**
     * The minimum value a Long can have.  The lowest minimum value that a
     * Long can have is 0x8000000000000000.
     */
    public static final long MIN_VALUE = 0x8000000000000000L;

    /**
     * The maximum value a Long can have.  The larget maximum value that a
     * Long can have is 0x7fffffffffffffff.
     */
    public static final long MAX_VALUE = 0x7fffffffffffffffL;

    /**
     * Returns a new String object representing the specified long in
     * the specified radix.
     * @param i		the long to be converted
     * @param radix	the radix
     * @see Character#MIN_RADIX
     * @see Character#MAX_RADIX
     */
    public static String toString(long i, int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
	    radix = 10;
	StringBuffer buf = new StringBuffer(radix >= 8 ? 23 : 65);
	boolean negative = (i < 0);
        if (!negative)
	    i = -i;
	while (i <= -radix) {
	    buf.append(Character.forDigit((int)(-(i % radix)), radix));
	    i = i / radix;
	}
	buf.append(Character.forDigit((int)(-i), radix));
	if (negative)
	    buf.append('-');
        return buf.reverse().toString();
    }

    /**
     * Returns a new String object representing the specified long 
     * as unsigned hexidecimal number. 
     */
    public static String toHexString(long i) {
	return toUnsignedString(i, 4);
    }

    /**
     * Returns a new String object representing the specified long 
     * as unsigned octal number. 
     */
    public static String toOctalString(long i) {
	return toUnsignedString(i, 3);
    }

    /**
     * Returns a new String object representing the specified long 
     * as unsigned octal number. 
     */
    public static String toBinaryString(long i) {
	return toUnsignedString(i, 1);
    }

    /** 
     * Convert the integer to an unsigned number.
     */
    private static String toUnsignedString(long i, int shift) {
	StringBuffer buf = new StringBuffer(shift >= 3 ? 22 : 64);
	int radix = 1 << shift;
	long mask = radix - 1;
	do {
	    buf.append(Character.forDigit((int)(i & mask), radix));
	    i >>>= shift;
	} while (i != 0);
        return buf.reverse().toString();
    }


    /**
     * Returns a new String object representing the specified integer. The radix
     * is assumed to be 10.
     * @param i	the long to be converted
     */
    public static String toString(long i) {
	return toString(i, 10);
    }


    /**
     * Assuming the specified String represents a long, returns that long's
     * value. Throws an exception if the String cannot be parsed as a long.
     * @param s		the String containing the integer
     * @param radix 	the radix to be used
     * @exception	NumberFormatException If the String does not 
     *                  contain a parsable integer.
     */
    public static long parseLong(String s, int radix) 
              throws NumberFormatException 
   {
        if (s == null) {
            throw new NumberFormatException("null");
        }
	long result = 0;
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
     * Assuming the specified String represents a long, return that long's
     * value. Throws an exception if the String cannot be parsed as a long.
     * The radix is assumed to be 10.
     * @param s		the String containing the long
     * @exception	NumberFormatException If the string does not contain
     *                   a parsable long.
     */
    public static long parseLong(String s) throws NumberFormatException {
	return parseLong(s, 10);
    }

    /**
     * Assuming the specified String represents a long, returns a new Long
     * object initialized to that value. Throws an exception if the String cannot be
     * parsed as a long.
     * @param s		the String containing the long.
     * @param radix 	the radix to be used
     * @exception	NumberFormatException If the String does not contain a parsable 
     *                                        long.
     */
    public static Long valueOf(String s, int radix) throws NumberFormatException {
	return new Long(parseLong(s, radix));
    }

    /**
     * Assuming the specified String represents a long, returns a new Long
     * object initialized to that value. Throws an exception if the String cannot be
     * parsed as a long. The radix is assumed to be 10.
     * @param s		the String containing the long
     * @exception	NumberFormatException If the String does not contain a parsable 
     *                                        long.
     */
    public static Long valueOf(String s) throws NumberFormatException 
    {
	return new Long(parseLong(s, 10));
    }


    /**
     * The value of the Long.
     */
    private long value;

    /**
     * Constructs a Long object initialized to the specified value.
     * @param value	the initial value of the Long
     */
    public Long(long value) {
	this.value = value;
    }

    /**
     * Constructs a Long object initialized to the value specified by the
     * String parameter.  The radix is assumed to be 10.
     * @param s		the String to be converted to a Long
     * @exception	NumberFormatException If the String does not contain a parsable 
     *                                        long.
     */
    public Long(String s) throws NumberFormatException {
	this.value = parseLong(s, 10);
    }

    /**
     * Returns the value of this Long as an int.
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the value of this Long as a long.
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the value of this Long as a float.
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the value of this Long as a double.
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a String object representing this Long's value.
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Computes a hashcode for this Long.
     */
    public int hashCode() {
	return (int)(value ^ (value >> 32));
    }

    /**
     * Compares this object against the specified object.
     * @param obj		the object to compare with
     * @return 		true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Long)) {
	    return value == ((Long)obj).longValue();
	}
	return false;
    }

    /**
     * Gets a Long property. If the property does not
     * exist, it will return 0.
     * @param nm the property name
     */
    public static Long getLong(String nm) {
	return getLong(nm, null);
    }

    /**
     * Gets a Long property. If the property does not
     * exist, it will return val. Deals with Hexadecimal and octal numbers.
     * @param nm the String name
     * @param val the Long value
     */
    public static Long getLong(String nm, long val) {
        Long result = Long.getLong(nm, null);
        return (result == null) ? new Long(val) : result;
    }

    /**
     * Gets a Long property. If the property does not
     * exist, it will return val. Deals with Hexadecimal and octal numbers.
     * @param nm the property name
     * @param val the Long value
     */
    public static Long getLong(String nm, Long val) {
	String v = System.getProperty(nm);
	if (v != null) {
	    try {
		if (v.startsWith("0x")) {
		    return Long.valueOf(v.substring(2), 16);
		}
		if (v.startsWith("#")) {
		    return Long.valueOf(v.substring(1), 16);
		}
		if (v.startsWith("0") && v.length() > 1) {
		    return Long.valueOf(v.substring(1), 8);
		}
		return Long.valueOf(v);
	    } catch (NumberFormatException e) {
	    }
	}	
	return val;
    }
}

