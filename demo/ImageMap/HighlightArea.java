/*
 * @(#)HighlightArea.java	1.4 96/04/24  
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
import java.net.URL;
import java.net.MalformedURLException;

/**
 * An area highlighting ImageArea class.
 * This class extends the basic ImageArea Class to highlight an area of
 * the base image when the mouse enters the area.
 *
 * @author 	Jim Graham
 * @version 	1.4, 04/24/96
 */
class HighlightArea extends ImageMapArea {
    int hlmode;
    int hlpercent;

    /**
     * The argument string is the highlight mode to be used.
     */
    public void handleArg(String arg) {
	if (arg == null) {
	    hlmode = parent.hlmode;
	    hlpercent = parent.hlpercent;
	} else {
	    if (arg.startsWith("darker")) {
		hlmode = parent.DARKER;
		arg = arg.substring("darker".length());
	    } else {
		hlmode = parent.BRIGHTER;
		if (arg.startsWith("brighter")) {
		    arg = arg.substring("brighter".length());
		}
	    }
	    hlpercent = Integer.parseInt(arg);
	}
    }

    public void makeImages() {
	setHighlight(parent.getHighlight(X, Y, W, H, hlmode, hlpercent));
    }

    public void highlight(Graphics g) {
	if (entered) {
	    g.drawImage(hlImage, X, Y, this);
	}
    }

    /**
     * The area is repainted when the mouse enters.
     */
    public void enter() {
	repaint();
    }

    /**
     * The area is repainted when the mouse leaves.
     */
    public void exit() {
	repaint();
    }
}
