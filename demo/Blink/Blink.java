/*
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

import java.awt.*;
import java.util.StringTokenizer;

/**
 * I love blinking things.
 *
 * @author Arthur van Hoff
 * @modified 96/04/24 Jim Hagen : use getBackground
 */
public class Blink extends java.applet.Applet implements Runnable {
    Thread blinker;
    String lbl;
    Font font;
    int speed;

    public void init() {
	font = new java.awt.Font("TimesRoman", Font.PLAIN, 24);
	String att = getParameter("speed");
	speed = (att == null) ? 400 : (1000 / Integer.valueOf(att).intValue());
	att = getParameter("lbl");
	lbl = (att == null) ? "Blink" : att;
    }
    
    public void paint(Graphics g) {
	int x = 0, y = font.getSize(), space;
	int red = (int)(Math.random() * 50);
	int green = (int)(Math.random() * 50);
	int blue = (int)(Math.random() * 256);
	Dimension d = size();

	g.setColor(Color.black);
	g.setFont(font);
	FontMetrics fm = g.getFontMetrics();
	space = fm.stringWidth(" ");
	for (StringTokenizer t = new StringTokenizer(lbl) ; t.hasMoreTokens() ; ) {
	    String word = t.nextToken();
	    int w = fm.stringWidth(word) + space;
	    if (x + w > d.width) {
		x = 0;
		y += font.getSize();
	    }
	    if (Math.random() < 0.5) {
		g.setColor(new java.awt.Color((red + y * 30) % 256, (green + x / 3) % 256, blue));
	    } else {
                g.setColor(getBackground());
	    }
	    g.drawString(word, x, y);
	    x += w;
	}
    }

    public void start() {
	blinker = new Thread(this);
	blinker.start();
    }
    public void stop() {
	blinker.stop();
    }
    public void run() {
	while (true) {
	try {Thread.currentThread().sleep(speed);} catch (InterruptedException e){}
	    repaint();
	}
    }
}
