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
import java.awt.Graphics;
import java.util.Stack;
import java.util.Vector;

/**
 * A (not-yet) Context sensitive L-System Fractal applet class.
 *
 * The rules for the Context L-system are read from the java.applet.Applet's
 * attributes and then the system is iteratively applied for the
 * given number of levels, possibly drawing each generation as it
 * is generated.  Note that the ContextLSystem class does not yet
 * handle the lContext and rContext attributes, although this
 * class is already designed to parse the '[' and ']' characters
 * typically used in Context sensitive L-Systems.
 *
 * @author 	Jim Graham
 * @version 	1.1f, 27 Mar 1995
 */
public class CLSFractal extends java.applet.Applet implements Runnable {
    ContextLSystem cls;
    int fractLevel = 1;
    int repaintDelay = 50;
    boolean incrementalUpdates;
    float startAngle;
    float rotAngle;
    float Xmin;
    float Xmax;
    float Ymin;
    float Ymax;
    int border;
    boolean normalizescaling;

    public void init() {
	String s;
	cls = new ContextLSystem(this);
	s = getParameter("level");
	if (s != null) fractLevel = Integer.parseInt(s);
	s = getParameter("incremental");
	if (s != null) incrementalUpdates = s.equals("true");
	s = getParameter("delay");
	if (s != null) repaintDelay = Integer.parseInt(s);
	s = getParameter("startAngle");
	if (s != null) startAngle = Float.valueOf(s).floatValue();
	s = getParameter("rotAngle");
	if (s != null) rotAngle = Float.valueOf(s).floatValue();
	rotAngle = rotAngle / 360 * 2 * 3.14159265358f;
	s = getParameter("border");
	if (s != null) border = Integer.parseInt(s);
	s = getParameter("normalizescale");
	if (s != null) normalizescaling = s.equals("true");
    }

    Thread kicker;

    public void run() {
	Thread me = Thread.currentThread();
	boolean needsRepaint = false;
	while (kicker == me && cls.getLevel() < fractLevel) {
	    cls.generate();
	    if (kicker == me && incrementalUpdates) {
		repaint();
		try {Thread.sleep(repaintDelay);} catch (InterruptedException e){}
	    } else {
		needsRepaint = true;
	    }
	}
	if (kicker == me) {
	    kicker = null;
	    if (needsRepaint) {
		repaint();
	    }
	}
    }

    public void start() {
	kicker = new Thread(this);
	kicker.start();
    }

    public void stop() {
	kicker = null;
    }

    public boolean mouseUp(java.awt.Event evt, int x, int y) {
	cls = new ContextLSystem(this);
	savedPath = null;
	start();
	return true;
    }

    String savedPath;

    public void paint(Graphics g) {
	String fractalPath = cls.getPath();
	if (fractalPath == null) {
	    super.paint(g);
	    return;
	}
	if (savedPath == null || !savedPath.equals(fractalPath)) {
	    savedPath = fractalPath;
	    render(null, fractalPath);
	}

	for (int i = 0; i < border; i++) {
	    g.draw3DRect(i, i, size().width - i * 2, size().height - i * 2,false);
	}
	render(g, fractalPath);
    }

    void render(Graphics g, String path) {
	Stack turtleStack = new Stack();
	CLSTurtle turtle;

	if (g == null) {
	    Xmin = 1E20f;
	    Ymin = 1E20f;
	    Xmax = -1E20f;
	    Ymax = -1E20f;
	    turtle = new CLSTurtle(startAngle, 0, 0, 0, 0, 1, 1);
	} else {
	    float frwidth = Xmax - Xmin;
	    if (frwidth == 0)
		frwidth = 1;
	    float frheight = Ymax - Ymin;
	    if (frheight == 0)
		frheight = 1;
	    float xscale = (size().width - border * 2 - 1) / frwidth;
	    float yscale = (size().height - border * 2 - 1) / frheight;
	    int xoff = border;
	    int yoff = border;
	    if (normalizescaling) {
		if (xscale < yscale) {
		    yoff += ((size().height - border * 2)
			     - ((Ymax - Ymin) * xscale)) / 2;
		    yscale = xscale;
		} else if (yscale < xscale) {
		    xoff += ((size().width - border * 2)
			     - ((Xmax - Xmin) * yscale)) / 2;
		    xscale = yscale;
		}
	    }
	    turtle = new CLSTurtle(startAngle, 0 - Xmin, 0 - Ymin,
				   xoff, yoff, xscale, yscale);
	}

	for (int pos = 0; pos < path.length(); pos++) {
	    switch (path.charAt(pos)) {
	    case '+':
		turtle.rotate(rotAngle);
		break;
	    case '-':
		turtle.rotate(-rotAngle);
		break;
	    case '[':
		turtleStack.push(turtle);
		turtle = new CLSTurtle(turtle);
		break;
	    case ']':
		turtle = (CLSTurtle) turtleStack.pop();
		break;
	    case 'f':
		turtle.jump();
		break;
	    case 'F':
		if (g == null) {
		    includePt(turtle.X, turtle.Y);
		    turtle.jump();
		    includePt(turtle.X, turtle.Y);
		} else {
		    turtle.draw(g);
		}
		break;
	    default:
		break;
	    }
	}
    }

    void includePt(float x, float y) {
	if (x < Xmin)
	    Xmin = x;
	if (x > Xmax)
	    Xmax = x;
	if (y < Ymin)
	    Ymin = y;
	if (y > Ymax)
	    Ymax = y;
    }
}

/**
 * A Logo turtle class designed to support Context sensitive L-Systems.
 *
 * This turtle performs a few basic maneuvers needed to support the
 * set of characters used in Context sensitive L-Systems "+-fF[]".
 *
 * @author 	Jim Graham
 * @version 	1.1f, 27 Mar 1995
 */
class CLSTurtle {
    float angle;
    float X;
    float Y;
    float scaleX;
    float scaleY;
    int xoff;
    int yoff;

    public CLSTurtle(float ang, float x, float y,
		     int xorg, int yorg, float sx, float sy) {
	angle = ang;
	scaleX = sx;
	scaleY = sy;
	X = x * sx;
	Y = y * sy;
	xoff = xorg;
	yoff = yorg;
    }

    public CLSTurtle(CLSTurtle turtle) {
	angle = turtle.angle;
	X = turtle.X;
	Y = turtle.Y;
	scaleX = turtle.scaleX;
	scaleY = turtle.scaleY;
	xoff = turtle.xoff;
	yoff = turtle.yoff;
    }

    public void rotate(float theta) {
	angle += theta;
    }

    public void jump() {
	X += (float) Math.cos(angle) * scaleX;
	Y += (float) Math.sin(angle) * scaleY;
    }

    public void draw(Graphics g) {
	float x = X + (float) Math.cos(angle) * scaleX;
	float y = Y + (float) Math.sin(angle) * scaleY;
	g.drawLine((int) X + xoff, (int) Y + yoff,
		   (int) x + xoff, (int) y + yoff);
	X = x;
	Y = y;
    }
}

/**
 * A (non-)Context sensitive L-System class.
 *
 * This class initializes the rules for Context sensitive L-Systems
 * (pred, succ, lContext, rContext) from the given java.applet.Applet's attributes.
 * The generate() method, however, does not (yet) apply the lContext
 * and rContext parts of the rules.
 *
 * @author 	Jim Graham
 * @version 	1.1f, 27 Mar 1995
 */
class ContextLSystem {
    String axiom;
    Vector rules = new Vector();
    int level;

    public ContextLSystem(java.applet.Applet app) {
	axiom = app.getParameter("axiom");
	int num = 1;
	while (true) {
	    String pred = app.getParameter("pred"+num);
	    String succ = app.getParameter("succ"+num);
	    if (pred == null || succ == null) {
		break;
	    }
	    rules.addElement(new CLSRule(pred, succ,
					 app.getParameter("lContext"+num),
					 app.getParameter("rContext"+num)));
	    num++;
	}
	currentPath = new StringBuffer(axiom);
	level = 0;
    }

    public int getLevel() {
	return level;
    }

    StringBuffer currentPath;

    public synchronized String getPath() {
	return ((currentPath == null) ? null : currentPath.toString());
    }

    private synchronized void setPath(StringBuffer path) {
	currentPath = path;
	level++;
    }

    public void generate() {
	StringBuffer newPath = new StringBuffer();
	int pos = 0;
	while (pos < currentPath.length()) {
	    CLSRule rule = findRule(pos);
	    if (rule == null) {
		newPath.append(currentPath.charAt(pos));
		pos++;
	    } else {
		newPath.append(rule.succ);
		pos += rule.pred.length();
	    }
	}
	setPath(newPath);
    }

    public CLSRule findRule(int pos) {
	for (int i = 0; i < rules.size(); i++) {
	    CLSRule rule = (CLSRule) rules.elementAt(i);
	    if (rule.matches(currentPath, pos)) {
		return rule;
	    }
	}
	return null;
    }
}

/**
 * A Context sensitive L-System production rule.
 *
 * This class encapsulates a production rule for a Context sensitive
 * L-System (pred, succ, lContext, rContext).
 * The matches() method, however, does not (yet) verify the lContext
 * and rContext parts of the rule.
 *
 * @author 	Jim Graham
 * @version 	1.1f, 27 Mar 1995
 */
class CLSRule {
    String pred;
    String succ;
    String lContext;
    String rContext;

    public CLSRule(String p, String d, String l, String r) {
	pred = p;
	succ = d;
	lContext = l;
	rContext = r;
    }

    public boolean matches(StringBuffer sb, int pos) {
	if (pos + pred.length() > sb.length()) {
	    return false;
	}
	char cb[] = new char[pred.length()];
	sb.getChars(pos, pos + pred.length(), cb, 0);
	return pred.equals(new String(cb));
    }
}
