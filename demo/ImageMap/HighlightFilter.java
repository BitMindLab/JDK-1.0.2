/*
 * @(#)HighlightFilter.java	1.3 96/04/24  
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted. 
 * Please refer to the file http://java.sun.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://java.sun.com/licensing.html for further important licensing
 * information for the Java (tm) Technology.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */

import java.applet.Applet;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import java.net.URL;
import java.awt.image.*;
import java.net.MalformedURLException;

/**
 * An image filter to highlight an image by brightening or darkening
 * the pixels in the images.
 *
 * @author 	Jim Graham
 * @version 	1.3, 04/24/96
 */
class HighlightFilter extends RGBImageFilter {
    boolean brighter;
    int percent;

    public HighlightFilter(boolean b, int p) {
	brighter = b;
	percent = p;
	canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
	int r = (rgb >> 16) & 0xff;
	int g = (rgb >> 8) & 0xff;
	int b = (rgb >> 0) & 0xff;
	if (brighter) {
	    r = (255 - ((255 - r) * (100 - percent) / 100));
	    g = (255 - ((255 - g) * (100 - percent) / 100));
	    b = (255 - ((255 - b) * (100 - percent) / 100));
	} else {
	    r = (r * (100 - percent) / 100);
	    g = (g * (100 - percent) / 100);
	    b = (b * (100 - percent) / 100);
	}
	if (r < 0) r = 0;
	if (r > 255) r = 255;
	if (g < 0) g = 0;
	if (g > 255) g = 255;
	if (b < 0) b = 0;
	if (b > 255) b = 255;
	return (rgb & 0xff000000) | (r << 16) | (g << 8) | (b << 0);
    }
}

