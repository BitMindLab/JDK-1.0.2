/*
 * @(#)ImageMap.java	1.7 96/04/24  
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
import java.awt.MediaTracker;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import java.net.URL;
import java.awt.image.ImageProducer;
import java.awt.image.ImageFilter;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.net.MalformedURLException;

/**
 * An extensible ImageMap applet class.
 * The active areas on the image are controlled by ImageArea classes
 * that can be dynamically loaded over the net.
 *
 * @author 	Jim Graham
 * @version 	1.7, 04/24/96
 */
public class ImageMap extends Applet implements Runnable {
    /**
     * The unhighlighted image being mapped.
     */
    Image baseImage;

    /**
     * The list of image area handling objects;
     */
    ImageMapArea areas[];

    /**
     * The primary highlight mode to be used.
     */
    static final int BRIGHTER = 0;
    static final int DARKER = 1;

    int hlmode = BRIGHTER;

    /**
     * The percentage of highlight to apply for the primary highlight mode.
     */
    int hlpercent = 50;

    /**
     * The MediaTracker for loading and constructing the various images.
     */
    MediaTracker tracker;

    /**
     * Get a rectangular region of the baseImage highlighted according to
     * the primary highlight specification.
     */
    Image getHighlight(int x, int y, int w, int h) {
	return getHighlight(x, y, w, h, hlmode, hlpercent);
    }

    /**
     * Get a rectangular region of the baseImage with a specific highlight.
     */
    Image getHighlight(int x, int y, int w, int h, int mode, int percent) {
	return getHighlight(x, y, w, h, new HighlightFilter(mode == BRIGHTER,
							    percent));
    }

    /**
     * Get a rectangular region of the baseImage modified by an image filter.
     */
    Image getHighlight(int x, int y, int w, int h, ImageFilter filter) {
	ImageFilter cropfilter = new CropImageFilter(x, y, w, h);
	ImageProducer prod = new FilteredImageSource(baseImage.getSource(),
						     cropfilter);
	return makeImage(prod, filter, 0);
    }

    /**
     * Make a filtered image based on another image.
     */
    Image makeImage(Image orig, ImageFilter filter) {
	return makeImage(orig.getSource(), filter);
    }

    /**
     * Make a filtered image based on another ImageProducer.
     */
    Image makeImage(ImageProducer prod, ImageFilter filter) {
	return makeImage(prod, filter,
			 (prod == baseImage.getSource()) ? 1 : 0);
    }

    /**
     * Make a filtered image based on another ImageProducer.
     * Add it to the media tracker using the indicated ID.
     */
    Image makeImage(ImageProducer prod, ImageFilter filter, int ID) {
	Image filtered = createImage(new FilteredImageSource(prod, filter));
	tracker.addImage(filtered, ID);
	return filtered;
    }

    /**
     * Add an image to the list of images to be tracked.
     */
    void addImage(Image img) {
	tracker.addImage(img, 1);
    }

    /**
     * Parse a string representing the desired highlight to be applied.
     */
    void parseHighlight(String s) {
	if (s == null) {
	    return;
	}
	if (s.startsWith("brighter")) {
	    hlmode = BRIGHTER;
	    if (s.length() > "brighter".length()) {
		hlpercent = Integer.parseInt(s.substring("brighter".length()));
	    }
	} else if (s.startsWith("darker")) {
	    hlmode = DARKER;
	    if (s.length() > "darker".length()) {
		hlpercent = Integer.parseInt(s.substring("darker".length()));
	    }
	}
    }

    /**
     * Initialize the applet. Get attributes.
     *
     * Initialize the ImageAreas.
     * Each ImageArea is a subclass of the class ImageArea, and is
     * specified with an attribute of the form:
     * 		areaN=ImageAreaClassName,arguments...
     * The ImageAreaClassName is parsed off and a new instance of that
     * class is created.  The initializer for that class is passed a
     * reference to the applet and the remainder of the attribute
     * string, from which the class should retrieve any information it
     * needs about the area it controls and the actions it needs to
     * take within that area.
     */
    public void init() {
	String s;

	tracker = new MediaTracker(this);
	parseHighlight(getParameter("highlight"));
	introTune = getParameter("startsound");
	baseImage = getImage(getDocumentBase(), getParameter("img"));
	Vector areaVec = new Vector();
	int num = 1;
	while (true) {
	    ImageMapArea newArea;
	    s = getParameter("area"+num);
	    if (s == null) {
		// Try rect for backwards compatibility.
		s = getParameter("rect"+num);
		if (s == null) {
		    break;
		}
		try {
		    newArea = new HighlightArea();
		    newArea.init(this, s);
		    areaVec.addElement(newArea);
		    String url = getParameter("href"+num);
		    if (url != null) {
			s += "," + url;
			newArea = new LinkArea();
			newArea.init(this, s);
			areaVec.addElement(newArea);
		    }
		} catch (Exception e) {
		    System.out.println("error processing: "+s);
		    e.printStackTrace();
		    break;
		}
	    } else {
		try {
		    int classend = s.indexOf(",");
		    String name = s.substring(0, classend);
		    newArea = (ImageMapArea) Class.forName(name).newInstance();
		    s = s.substring(classend+1);
		    newArea.init(this, s);
		    areaVec.addElement(newArea);
		} catch (Exception e) {
		    System.out.println("error processing: "+s);
		    e.printStackTrace();
		    break;
		}
	    }
	    num++;
	}
	areas = new ImageMapArea[areaVec.size()];
	areaVec.copyInto(areas);
	checkSize();
    }

    Thread aniThread = null;
    String introTune = null;

    public void start() {
	if (introTune != null)
	    try {
		play(new URL(getDocumentBase(), introTune));
	    } catch (MalformedURLException e) {}
	if (aniThread == null) {
            aniThread = new Thread(this);
            aniThread.setName("ImageMap Animator");
            aniThread.start();
	}
    }

    public void run() {
	Thread me = Thread.currentThread();
	tracker.checkAll(true);
	for (int i = areas.length; --i >= 0; ) {
	    areas[i].getMedia();
	}
	me.setPriority(Thread.MIN_PRIORITY);
	while (aniThread == me) {
	    boolean animating = false;
	    for (int i = areas.length; --i >= 0; ) {
		animating = areas[i].animate() || animating;
	    }
	    try {
		synchronized(this) {
		    wait(animating ? 100 : 0);
		}
	    } catch (InterruptedException e) {
		break;
	    }
	}
    }

    public synchronized void startAnimation() {
	notify();
    }

    public synchronized void stop() {
	aniThread = null;
	notify();
	for (int i = 0; i < areas.length; i++) {
	    areas[i].exit();
	}
    }

    /**
     * Check the size of this applet while the image is being loaded.
     */
    void checkSize() {
	int w = baseImage.getWidth(this);
	int h = baseImage.getHeight(this);
	if (w > 0 && h > 0) {
	    resize(w, h);
	    synchronized(this) {
		fullrepaint = true;
	    }
	    repaint(0, 0, w, h);
	}
    }

    private boolean fullrepaint = false;
    private final static long UPDATERATE = 100;

    /**
     * Handle updates from images being loaded.
     */
    public boolean imageUpdate(Image img, int infoflags,
			       int x, int y, int width, int height) {
	if ((infoflags & (WIDTH | HEIGHT)) != 0) {
	    checkSize();
	}
	if ((infoflags & (SOMEBITS | FRAMEBITS | ALLBITS)) != 0) {
	    synchronized(this) {
		fullrepaint = true;
	    }
	    repaint(((infoflags & (FRAMEBITS | ALLBITS)) != 0)
		    ? 0 : UPDATERATE,
		    x, y, width, height);
	}
	return (infoflags & (ALLBITS | ERROR)) == 0;
    }

    /**
     * Paint the image and all active highlights.
     */
    public void paint(Graphics g) {
	synchronized(this) {
	    fullrepaint = false;
	}
	if (baseImage == null) {
	    return;
	}
	g.drawImage(baseImage, 0, 0, this);
	if (areas != null) {
	    for (int i = areas.length; --i >= 0; ) {
		areas[i].highlight(g);
	    }
	}
    }

    /**
     * Update the active highlights on the image.
     */
    public void update(Graphics g) {
	boolean full;
	synchronized(this) {
	    full = fullrepaint;
	}
	if (full) {
	    paint(g);
	    return;
	}
	if (baseImage == null) {
	    return;
	}
	g.drawImage(baseImage, 0, 0, this);
	if (areas == null) {
	    return;
	}
	// First unhighlight all of the deactivated areas
	for (int i = areas.length; --i >= 0; ) {
	    areas[i].highlight(g);
	}
    }

    /**
     * Make sure that no ImageAreas are highlighted.
     */
    public boolean mouseExit(java.awt.Event evt, int x, int y) {
	for (int i = 0; i < areas.length; i++) {
	    areas[i].checkExit();
	}

	return true;
    }

    /**
     * Find the ImageAreas that the mouse is in.
     */
    public boolean mouseMove(java.awt.Event evt, int x, int y) {
	boolean eaten = false;

	for (int i = 0; i < areas.length; i++) {
	    if (!eaten && areas[i].inside(x, y)) {
		eaten = areas[i].checkEnter(x, y);
	    } else {
		areas[i].checkExit();
	    }
	}

	return true;
    }

    int pressX;
    int pressY;

    /**
     * Inform all active ImageAreas of a mouse press.
     */
    public boolean mouseDown(java.awt.Event evt, int x, int y) {
	pressX = x;
	pressY = y;

	for (int i = 0; i < areas.length; i++) {
	    if (areas[i].inside(x, y)) {
		if (areas[i].press(x, y)) {
		    break;
		}
	    }
	}

	return true;
    }

    /**
     * Inform all active ImageAreas of a mouse release.
     * Only those areas that were inside the original mouseDown()
     * are informed of the mouseUp.
     */
    public boolean mouseUp(java.awt.Event evt, int x, int y) {
	for (int i = 0; i < areas.length; i++) {
	    if (areas[i].inside(pressX, pressY)) {
		if (areas[i].lift(x, y)) {
		    break;
		}
	    }
	}

	return true;
    }

    /**
     * Inform all active ImageAreas of a mouse drag.
     * Only those areas that were inside the original mouseDown()
     * are informed of the mouseUp.
     */
    public boolean mouseDrag(java.awt.Event evt, int x, int y) {
	mouseMove(evt, x, y);
	for (int i = 0; i < areas.length; i++) {
	    if (areas[i].inside(pressX, pressY)) {
		if (areas[i].drag(x, y)) {
		    break;
		}
	    }
	}

	return true;
    }

    /**
     * Scan all areas looking for the topmost status string.
     */
    public void newStatus() {
	String msg = null;
	for (int i = 0; i < areas.length; i++) {
	    msg = areas[i].getStatus(msg);
	}
	showStatus(msg);
    }
}
