/*
 * @(#)TickerArea.java	1.2 96/04/24  
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
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.StringTokenizer;

/**
 * This ImageArea renders a string of text that constantly scrolls across
 * the indicated area of the ImageMap in the specified color.
 *
 * @author	Jim Graham
 * @version	1.2, 04/24/96
 */
class TickerArea extends ImageMapArea {

    String tickertext;
    Color  tickercolor;
    Font   tickerfont;
    int    speed;		// In pixels per second for scrolling

    int tickerx;
    int tickery;
    int tickerlen;
    long lasttick;

    public void handleArg(String s) {
	StringTokenizer st = new StringTokenizer(s, ",");

	tickertext = st.nextToken();
	tickercolor = Color.black;
	speed = 100;
	String fontname = "TimesRoman";

	if (st.hasMoreTokens()) {
	    fontname = st.nextToken();
	    if (st.hasMoreTokens()) {
		String str = st.nextToken();
		if (str.startsWith("#")) {
		    str = str.substring(1);
		}
		try {
		    int colorval = Integer.parseInt(str, 16);
		    tickercolor = new Color((colorval >> 16) & 0xff,
					    (colorval >> 8) & 0xff,
					    (colorval >> 0) & 0xff);
		} catch (Exception e) {
		    tickercolor = Color.black;
		}
		if (st.hasMoreTokens()) {
		    str = st.nextToken();
		    try {
			speed = Integer.parseInt(str);
		    } catch (Exception e) {
			speed = 100;
		    }
		}
	    }
	}

	FontMetrics fm;
	int size;
	int nextsize = H;
	do {
	    size = nextsize;
	    tickerfont = new Font(fontname, Font.PLAIN, size);
	    fm = parent.getFontMetrics(tickerfont);
	    nextsize = (size * 9) / 10;
	} while (fm.getHeight() > H && size > 0);
	tickerlen = fm.stringWidth(tickertext);
	tickery = fm.getAscent();
    }

    public void getMedia() {
	tickerx = 0;
	repaint();
	lasttick = System.currentTimeMillis();
    }

    public boolean animate() {
	long curtick = System.currentTimeMillis();
	tickerx -= ((speed * (curtick - lasttick)) / 1000);
	if (tickerx > W || tickerx + tickerlen < 0) {
	    tickerx = W;
	}
	repaint();
	lasttick = curtick;
	return true;
    }

    public void highlight(Graphics g) {
	g.setColor(tickercolor);
	g.setFont(tickerfont);
	g.drawString(tickertext, X+tickerx, Y+tickery);
    }
}

