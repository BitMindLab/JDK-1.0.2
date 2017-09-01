/*
 * @(#)RoundButtonFilter.java	1.5 96/04/24  
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

/**
 * An extensible ImageMap applet class.
 * The active areas on the image are controlled by ImageArea classes
 * that can be dynamically loaded over the net.
 *
 * @author 	Jim Graham
 * @version 	1.5, 04/24/96
 */
class RoundButtonFilter extends ButtonFilter {
    int Xcenter;
    int Ycenter;
    int Yradsq;
    int innerW;
    int innerH;
    int Yrad2sq;

    public RoundButtonFilter(boolean press, int p, int b, int w, int h) {
	super(press, p, b, w, h);
	Xcenter = w/2;
	Ycenter = h/2;
	Yradsq = h * h / 4;
	innerW = w - border * 2;
	innerH = h - border * 2;
	Yrad2sq = innerH * innerH / 4;
    }

    public boolean inside(int x, int y) {
	int yrel = Math.abs(Ycenter - y);
	int xrel = (int) (Math.sqrt(Yradsq - yrel * yrel) * width / height);
	return (x >= Xcenter - xrel && x < Xcenter + xrel);
    }

    public void buttonRanges(int y, int ranges[]) {
	int yrel = Math.abs(Ycenter - y);
	int xrel = (int) (Math.sqrt(Yradsq - yrel * yrel) * width / height);
	ranges[0] = 0;
	ranges[1] = Xcenter - xrel;
	ranges[6] = Xcenter + xrel;
	ranges[7] = width;
	ranges[8] = ranges[9] = y;
	if (y < border) {
	    ranges[2] = ranges[3] = ranges[4] = Xcenter;
	    ranges[5] = ranges[6];
	} else if (y + border >= height) {
	    ranges[2] = ranges[1];
	    ranges[3] = ranges[4] = ranges[5] = Xcenter;
	} else {
	    int xrel2 = (int) (Math.sqrt(Yrad2sq - yrel * yrel)
			       * innerW / innerH);
	    ranges[3] = Xcenter - xrel2;
	    ranges[4] = Xcenter + xrel2;
	    if (y < Ycenter) {
		ranges[2] = ranges[3];
		ranges[5] = ranges[6];
	    } else {
		ranges[2] = ranges[1];
		ranges[5] = ranges[4];
	    }
	}
    }

    public int filterRGB(int x, int y, int rgb) {
	boolean brighter;
	int percent;
	int i;
	int xrel, yrel;
	int ranges[] = getRanges(y);
	for (i = 0; i < 7; i++) {
	    if (x >= ranges[i] && x < ranges[i+1]) {
		break;
	    }
	}
	switch (i) {
	default:
	case 0:
	case 6:
	    return rgb & 0x00ffffff;
	case 1:
	    brighter = !pressed;
	    percent = defpercent;
	    break;
	case 5:
	    brighter = pressed;
	    percent = defpercent;
	    break;
	case 2:
	    yrel = y - Ycenter;
	    xrel = Xcenter - x;
	    percent = (int) (yrel * defpercent * 2 /
			     Math.sqrt(yrel * yrel + xrel * xrel))
		- defpercent;
	    if (!pressed) {
		percent = -percent;
	    }
	    if (percent == 0) {
		return rgb;
	    } else if (percent < 0) {
		percent = -percent;
		brighter = false;
	    } else {
		brighter = true;
	    }
	    break;
	case 4:
	    yrel = Ycenter - y;
	    xrel = x - Xcenter;
	    percent = (int) (yrel * defpercent * 2 /
			     Math.sqrt(yrel * yrel + xrel * xrel))
		- defpercent;
	    if (pressed) {
		percent = -percent;
	    }
	    if (percent == 0) {
		return rgb;
	    } else if (percent < 0) {
		percent = -percent;
		brighter = false;
	    } else {
		brighter = true;
	    }
	    break;
	case 3:
	    if (!pressed) {
		return rgb & 0x00ffffff;
	    }
	    brighter = false;
	    percent = defpercent;
	    break;
	}
	return filterRGB(rgb, brighter, percent);
    }
}
