/*
 * @(#)HrefButtonArea.java	1.5 96/04/24  
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
import java.awt.Image;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * An improved "Fetch a URL" ImageArea class.
 * This class extends the basic ImageArea Class to fetch a URL when
 * the user clicks in the area.  In addition, special custom highlights
 * are used to make the area look and feel like a 3-D button.
 *
 * @author 	Jim Graham
 * @version 	1.5, 04/24/96
 */
class HrefButtonArea extends ImageMapArea {
    /** The URL to be fetched when the user clicks on this area. */
    URL anchor;
    /** The highlight image for when the button is "UP". */
    Image upImage;
    /** The highlight image for when the button is "DOWN". */
    Image downImage;
    /** This flag indicates if the "button" is currently pressed. */
    boolean pressed = false;
    /** The border size for the 3-D effect. */
    int border = 5;

    /**
     * The argument string is the URL to be fetched.
     * This method also constructs the various highlight images needed
     * to achieve the 3-D effect.
     */
    public void handleArg(String arg) {
	try {
	    anchor = new URL(parent.getDocumentBase(), arg);
	} catch (MalformedURLException e) {
	    anchor = null;
	}
	if (border * 2 > W || border * 2 > H) {
	    border = Math.min(W, H) / 2;
	}
    }

    public void makeImages() {
	upImage = parent.getHighlight(X, Y, W, H,
				      new ButtonFilter(false,
						       parent.hlpercent,
						       border, W, H));
	downImage = parent.getHighlight(X, Y, W, H,
					new ButtonFilter(true,
							 parent.hlpercent,
							 border, W, H));
    }

    public boolean imageUpdate(Image img, int infoflags,
			       int x, int y, int width, int height) {
	if (img == (pressed ? downImage : upImage)) {
	    return parent.imageUpdate(img, infoflags, x + X, y + Y,
				      width, height);
	} else {
	    return (img == downImage || img == upImage);
	}
    }

    /**
     * The isTerminal method indicates whether events should propagate
     * to the areas underlying this one.
     */
    public boolean isTerminal() {
	return true;
    }

    /**
     * The status message area is updated to show the destination URL.
     * The graphical highlight is achieved using the ButtonFilter.
     */
    public void highlight(Graphics g) {
	if (entered) {
	    g.drawImage(pressed ? downImage : upImage, X, Y, this);
	}
    }

    public void enter() {
	showStatus((anchor != null)
		   ? "Go To " + anchor.toExternalForm()
		   : null);
	repaint();
    }

    public void exit() {
	showStatus(null);
	repaint();
    }

    /**
     * Since the highlight changes when the button is pressed, we need
     * to record the "pressed" state and induce a repaint.
     */
    public boolean press() {
	pressed = true;
	repaint();
	return true;
    }

    /**
     * The new URL is fetched when the user releases the mouse button
     * only if they are still in the area.
     */
    public boolean lift(int x, int y) {
	pressed = false;
	repaint();
	if (inside(x, y) && anchor != null) {
	    showDocument(anchor);
	}
	return true;
    }
}

