/*
 * @(#)ClickArea.java	1.5 96/04/24  
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

import java.awt.Graphics;

/**
 * An click feedback ImageArea class.
 * This class extends the basic ImageArea Class to show the locations
 * of clicks in the image in the status message area.  This utility
 * ImageArea class is useful when setting up ImageMaps.
 *
 * @author 	Jim Graham
 * @version 	1.5, 04/24/96
 */
class ClickArea extends ImageMapArea {
    /** The X location of the last mouse press. */
    int startx;
    /** The Y location of the last mouse press. */
    int starty;
    /** A boolean to indicate whether we are currently being dragged. */
    boolean dragging;

    static String ptstr(int x, int y) {
	return "("+x+", "+y+")";
    }

    /**
     * When the user presses the mouse button, start showing coordinate
     * feedback in the status message line.
     */
    public boolean press(int x, int y) {
	showStatus("Clicked at "+ptstr(x, y));
	startx = x;
	starty = y;
	dragging = true;
	return false;
    }

    /**
     * Update the coordinate feedback every time the user moves the mouse
     * while he has the button pressed.
     */
    public boolean drag(int x, int y) {
	showStatus("Rectangle from "+ptstr(startx, starty)
		   +" to "+ptstr(x, y)
		   +" is "+(x-startx)+"x"+(y-starty));
	return false;
    }

    /**
     * Update the coordinate feedback one last time when the user releases
     * the mouse button.
     */
    public boolean lift(int x, int y) {
	dragging = false;
	return drag(x, y);
    }

    /**
     * This utility method returns the status string this area wants to
     * put into the status bar.  If this area is currently animating
     * a message, then that message takes precedence over any other area
     * that a higher stacked area may want to display, otherwise the
     * message from the higher stacked area takes precedence.
     */
    public String getStatus(String prevmsg) {
	if (dragging) {
	    return (status != null) ? status : prevmsg;
	} else {
	    return (prevmsg == null) ? status : prevmsg;
	}
    }
}

