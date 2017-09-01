/*
 * @(#)MouseTrack.java
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
import java.lang.Math;

public class MouseTrack extends java.applet.Applet {

    int mx, my;
    int onaroll;

    public void init() {
	onaroll = 0;
	resize(500, 500);
    }

    public void paint(Graphics g) {
	g.drawRect(0, 0, size().width - 1, size().height - 1);
	mx = (int)(Math.random()*1000) % (size().width - (size().width/10));
	my = (int)(Math.random()*1000) % (size().height - (size().height/10));
	g.drawRect(mx, my, (size().width/10) - 1, (size().height/10) - 1);
    }

    /*
     * Mouse methods
     */
    public boolean mouseDown(java.awt.Event evt, int x, int y) {
	requestFocus();
	if((mx < x && x < mx+size().width/10-1) && (my < y && y < my+size().height/10-1)) {
	    if(onaroll > 0) {
		switch(onaroll%4) {
		case 0:
		    play(getCodeBase(), "sounds/tiptoe.thru.the.tulips.au");
		    break;
		case 1:
		    play(getCodeBase(), "sounds/danger,danger...!.au");
		    break;
		case 2:
		    play(getCodeBase(), "sounds/adapt-or-die.au");
		    break;
		case 3:
		    play(getCodeBase(), "sounds/cannot.be.completed.au");
		    break;
		}
		onaroll++;
		if(onaroll > 5)
		    getAppletContext().showStatus("You're on your way to THE HALL OF FAME:"
			+ onaroll + "Hits!");
		else
		    getAppletContext().showStatus("YOU'RE ON A ROLL:" + onaroll + "Hits!");
	    }
	    else {
		getAppletContext().showStatus("HIT IT AGAIN! AGAIN!");
		play(getCodeBase(), "sounds/that.hurts.au");
		onaroll = 1;
	    }
	}
	else {
	    getAppletContext().showStatus("You hit nothing at (" + x + ", " + y + "), exactly");
	    play(getCodeBase(), "sounds/thin.bell.au");
	    onaroll = 0;
	}
	repaint();
	return true;
    }

    public boolean mouseMove(java.awt.Event evt, int x, int y) {
	if((x % 3 == 0) && (y % 3 == 0))
	    repaint();
	return true;
    }

    public void mouseEnter() {
	repaint();
    }

    public void mouseExit() {
	onaroll = 0;
	repaint();
    }

    /**
     * Focus methods
     */
    public void keyDown(int key) {
	requestFocus();
	onaroll = 0;
	play(getCodeBase(), "sounds/ip.au");
    }
}
