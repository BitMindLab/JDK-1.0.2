/*
 * @(#)FontMetrics.java	1.10 96/02/28 Jim Graham
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
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

package java.awt;

/** 
 * A font metrics object. Note that the implementations of these
 * methods are inefficient, they are usually overridden with more
 * efficient toolkit specific implementations.
 * 
 * @version 	1.10 28 Feb 1996
 * @author 	Jim Graham
 */
public abstract class FontMetrics {
    /**
     * The actual font.
     * @see #getFont
     */
    protected Font font;

    /**
     * Creates a new FontMetrics object with the specified font.
     * @param font the font
     * @see Font
     */
    protected FontMetrics(Font font) {
	this.font = font;
    }

    /**
     * Gets the font.
     */
    public Font getFont() {
	return font;
    }

    /**
     * Gets the standard leading, or line spacing, for the font.  
     * This is the logical amount of space to be reserved between the
     * descent of one line of text and the ascent of the next line.
     * The height metric is calculated to include this extra space.
     */
    public int getLeading() {
	return 0;
    }

    /**
     * Gets the font ascent. The font ascent is the distance from the 
     * base line to the top of most Alphanumeric characters.  Note,
     * however, that some characters in the font may extend above
     * this height.
     * @see #getMaxAscent
     */
    public int getAscent() {
	return font.getSize();
    }

    /**
     * Gets the font descent. The font descent is the distance from the 
     * base line to the bottom of most Alphanumeric characters.  Note,
     * however, that some characters in the font may extend below this
     * height.
     * @see #getMaxDescent
     */
    public int getDescent() {
	return 0;
    }

    /**
     * Gets the standard height of a line of text in this font.  This
     * is the distance between the baseline of adjacent lines of text.
     * It is the sum of the leading + ascent + descent.  There is no
     * guarantee that lines of text spaced at this distance will be
     * disjoint; such lines may overlap if some characters overshoot
     * either the standard ascent or the standard descent metric.
     */
    public int getHeight() {
	return getLeading() + getAscent() + getDescent();
    }

    /**
     * Gets the maximum ascent of all characters in this Font.
     * No character will extend further above the baseline than this 
     * distance.
     * @see #getAscent
     */
    public int getMaxAscent() {
	return getAscent();
    }

    /**
     * Gets the maximum descent of all characters in this Font.
     * No character will descend futher below the baseline than this
     * distance.
     * @see #getDescent
     */
    public int getMaxDescent() {
	return getDescent();
    }

    /**
     * For backward compatibility only.
     * @see #getMaxDescent
     */
    public int getMaxDecent() {
	return getMaxDescent();
    }

    /**
     * Gets the maximum advance width of any character in this Font. 
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @return -1 if the max advance is not known.
     */
    public int getMaxAdvance() {
	return -1;
    }

    /** 
     * Returns the advance width of the specified character in this Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param ch the character to be measured
     * @see #stringWidth
     */
    public int charWidth(int ch) {
	return charWidth((char)ch);
    }

    /** 
     * Returns the advance width of the specified character in this Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param ch the character to be measured
     * @see #stringWidth
     */
    public int charWidth(char ch) {
	if (ch < 256) {
	    return getWidths()[ch];
	}
	char data[] = {ch};
	return charsWidth(data, 0, 1);
    }

    /** 
     * Returns the total advance width for showing the specified String
     * in this Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param str the String to be measured
     * @see #charsWidth
     * @see #bytesWidth
     */
    public int stringWidth(String str) {
	int len = str.length();
	char data[] = new char[len];
	str.getChars(0, len, data, 0);
	return charsWidth(data, 0, len);
    }

    /** 
     * Returns the total advance width for showing the specified array
     * of characters in this Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param data the array of characters to be measured
     * @param off the start offset of the characters in the array
     * @param len the number of characters to be measured from the array
     * @see #stringWidth
     * @see #bytesWidth
     */
    public int charsWidth(char data[], int off, int len) {
	return stringWidth(new String(data, off, len));
    }

    /** 
     * Returns the total advance width for showing the specified array
     * of bytes in this Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param data the array of bytes to be measured
     * @param off the start offset of the bytes in the array
     * @param len the number of bytes to be measured from the array
     * @see #stringWidth
     * @see #charsWidth
     */
    public int bytesWidth(byte data[], int off, int len) {
	return stringWidth(new String(data, 0, off, len));
    }

    /**
     * Gets the advance widths of the first 256 characters in the Font.
     * The advance width is the amount by which the current point is
     * moved from one character to the next in a line of text.
     */
    public int[] getWidths() {
	int widths[] = new int[256];
	for (char ch = 0 ; ch < 256 ; ch++) {
	    widths[ch] = charWidth(ch);
	}
	return widths;
    }

    /** 
     * Returns the String representation of this FontMetric's values.
     */
    public String toString() {
	return getClass().getName() + "[font=" + getFont() + "ascent=" +
	    getAscent() + ", descent=" + getDescent() + ", height=" + getHeight() + "]";
    }
}
