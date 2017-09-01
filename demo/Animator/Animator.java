/*
 * @(#)Animator.java	1.8 96/04/08 Herb Jellinek
 *                      1.9 96/04/24 Jim Hagen : use getBackground
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

import java.awt.*;
import java.awt.image.ImageProducer;
import java.applet.Applet;
import java.applet.AudioClip;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * An applet that plays a sequence of images, as a loop or a one-shot.
 * Can have a soundtrack and/or sound effects tied to individual frames.
 *
 * @author Herb Jellinek
 * @version 1.8, 08 Apr 1996
 */

public class Animator extends Applet implements Runnable {
    
    /**
     * The images, in display order (Images).
     */
    Vector images = null;

    /**
     * Duration of each image (Integers, in milliseconds).
     */
    Hashtable durations = null;

    /**
     * Sound effects for each image (AudioClips).
     */
    Hashtable sounds = null;

    /**
     * Position of each image (Points).
     */
    Hashtable positions = null;

    /**
     * MediaTracker 'class' ID numbers.
     */

    static final int STARTUP_ID    = 0;
    static final int BACKGROUND_ID = 1;
    static final int ANIMATION_ID  = 2;

    /**
     * Start-up image URL, if any.
     */
    URL startUpImageURL = null;

    /**
     * Start-up image, if any.
     */
    Image startUpImage = null;

    /**
     * Background image URL, if any.
     */
    URL backgroundImageURL = null;

    /**
     * Background image, if any.
     */
    Image backgroundImage = null;

    /**
     * Background color, if any.
     */
    Color backgroundColor = getBackground() ;
    
    /**
     * The soundtrack's URL.
     */
    URL soundtrackURL = null;

    /**
     * The soundtrack.
     */
    AudioClip soundtrack = null;

    /**
     * URL to link to, if any.
     */
    URL hrefURL = null;

    /**
     * Our width.
     */
    int appWidth = 0;

    /**
     * Our height.
     */
    int appHeight = 0;

    /**
     * Was there a problem loading the current image?
     */
    boolean imageLoadError = false;

    /**
     * The directory or URL from which the images are loaded
     */
    URL imageSource = null;

    /**
     * The directory or URL from which the sounds are loaded
     */
    URL soundSource = null;

    /**
     * The thread animating the images.
     */
    Thread engine = null;

    /**
     * The current loop slot - index into 'images.'
     */
    int frameNum;

    /**
     * frameNum as an Object - suitable for use as a Hashtable key.
     */
    Integer frameNumKey;
    
    /**
     * The current X position (for painting).
     */
    int xPos = 0;
    
    /**
     * The current Y position (for painting).
     */
    int yPos = 0;
    
    /**
     * The default number of milliseconds to wait between frames.
     */
    public static final int defaultPause = 3900;
    
    /**
     * The global delay between images, which can be overridden by
     * the PAUSE parameter.
     */
    int globalPause = defaultPause;

    /**
     * Whether or not the thread has been paused by the user.
     */
    boolean userPause = false;

    /**
     * Repeat the animation?  If false, just play it once.
     */
    boolean repeat;

    /**
     * The offscreen image, used in double buffering
     */
    Image offScrImage;

    /**
     * The offscreen graphics context, used in double buffering
     */
    Graphics offScrGC;

    /**
     * The MediaTracker we use to load our images.
     */
    MediaTracker tracker;
    
    /**
     * Can we paint yet?
     */
    boolean loaded = false;

    /**
     * Was there an initialization error?
     */
    boolean error = false;

    /**
     * What we call an image file in messages.
     */
    static final String imageLabel = "image";
    
    /**
     * What we call a sound file in messages.
     */
    static final String soundLabel = "sound";
    
    /**
     * Print silly debugging info?
     */
    static final boolean debug = false;

    /**
     * Frame in which to display applet description.
     */
    DescriptionFrame description = null;

    /**
     * Where to find the source code and documentation - for informational
     * purposes.
     */
    static final String sourceLocation =
                   "http://java.sun.com/applets/applets/Animator/";

    /**
     * Applet info.
     */
    public String getAppletInfo() {
	return "Animator v1.8 (08 Apr 1996), by Herb Jellinek";
    }

    /**
     * Parameter info.
     */
    public String[][] getParameterInfo() {
	String[][] info = {
	    {"imagesource", 	"URL", 		"a directory"},
	    {"startup", 	"URL", 		"image displayed at startup"},
	    {"backgroundcolor", "int",          "(24-bit number) displayed as background"},
	    {"background", 	"URL", 		"image displayed as background"},
	    {"startimage", 	"int", 		"start index"},
	    {"endimage", 	"int", 		"end index"},
	    {"namepattern",     "URL",          "used to generate indexed names"},
	    {"images",          "URLs",         "list of images"},
	    {"href",		"URL",		"page to visit on mouse-click"},
	    {"pause", 	        "int", 		"milliseconds"},
	    {"pauses", 	        "ints", 	"milliseconds"},
	    {"repeat", 	        "boolean", 	"repeat or not"},
	    {"positions",	"coordinates", 	"path"},
	    {"soundsource",	"URL", 		"audio directory"},
	    {"soundtrack",	"URL", 		"background music"},
	    {"sounds",		"URLs",		"list of audio samples"},
	};
	return info;
    }

    /**
     * Show a crude "About" box.
     */
    void showDescription() {
	if (description == null) {
	    description = new DescriptionFrame();
	    String params[][] = getParameterInfo();
	    
	    description.tell("\t\t"+getAppletInfo()+"\n");
	    description.tell("Updates, documentation at "+sourceLocation+"\n\n");
	    description.tell("Document base: "+getDocumentBase()+"\n");
	    description.tell("Code base: "+getCodeBase()+"\n\n");
	    description.tell("Applet parameters:\n\n");
	    
	    description.tell("width = "+super.getParameter("WIDTH")+"\n");
	    description.tell("height = "+super.getParameter("HEIGHT")+"\n");

	    for (int i = 0; i < params.length; i++) {
		String name = params[i][0];
		description.tell(name+" = "+super.getParameter(name)+"\n");
	    }
	}
	description.show();
    }

    /**
     * Print silly debugging info to the standard output.
     */
    void dbg(String s) {
	if (debug) {
	    System.out.println("> "+s);
	}
    }

    /**
     * Local version of getParameter for debugging purposes.
     */
    public String getParameter(String key) {
	String result = super.getParameter(key);
	dbg("getParameter("+key+") = "+result);
	return result;
    }

    final int setFrameNum(int newFrameNum) {
	frameNumKey = new Integer(frameNum = newFrameNum);
	return frameNum;
    }
    
    /**
     * Parse the IMAGES parameter.  It looks like
     * 1|2|3|4|5, etc., where each number (item) names a source image.
     *
     * @return a Vector of (URL) image file names.
     */
    Vector parseImages(String attr, String pattern)
    throws MalformedURLException {
	if (pattern == null) {
	    pattern = "T%N.gif";
	}
	Vector result = new Vector(10);
	for (int i = 0; i < attr.length(); ) {
	    int next = attr.indexOf('|', i);
	    if (next == -1) next = attr.length();
	    String file = attr.substring(i, next);
	    result.addElement(new URL(imageSource, doSubst(pattern, file)));
	    i = next + 1;
	}
	return result;
    }

    /**
     * Fetch the images named in the argument.  Is restartable.
     *
     * @param images a Vector of URLs
     * @return true if all went well, false otherwise.
     */
    boolean fetchImages(Vector images) {
	int i;
	int size = images.size();
	for (i = 0; i < size; i++) {
	    Object o = images.elementAt(i);
	    if (o instanceof URL) {
		URL url = (URL)o;
		tellLoadingMsg(url, imageLabel);
		Image im = getImage(url);
		tracker.addImage(im, ANIMATION_ID);
		images.setElementAt(im, i);
	    }
	}

	try {
	    tracker.waitForID(ANIMATION_ID);
	} catch (InterruptedException e) {}
	if (tracker.isErrorID(ANIMATION_ID)) {
	    return false;
	}

	return true;
    }

    /**
     * Parse the SOUNDS parameter.  It looks like
     * train.au||hello.au||stop.au, etc., where each item refers to a
     * source image.  Empty items mean that the corresponding image
     * has no associated sound.
     *
     * @return a Hashtable of SoundClips keyed to Integer frame numbers.
     */
    Hashtable parseSounds(String attr, Vector images)
    throws MalformedURLException {
	Hashtable result = new Hashtable();

	int imageNum = 0;
	int numImages = images.size();
	for (int i = 0; i < attr.length(); ) {
	    if (imageNum >= numImages) break;
	    
	    int next = attr.indexOf('|', i);
	    if (next == -1) next = attr.length();
	    
	    String sound = attr.substring(i, next);
	    if (sound.length() != 0) {
		result.put(new Integer(imageNum),
			   new URL(soundSource, sound));
	    }
	    i = next + 1;
	    imageNum++;
	}

	return result;
    }

    /**
     * Fetch the sounds named in the argument.
     * Is restartable.
     *
     * @return URL of the first bogus file we hit, null if OK.
     */
    URL fetchSounds(Hashtable sounds) {
	for (Enumeration e = sounds.keys() ; e.hasMoreElements() ;) {
	    Integer num = (Integer)e.nextElement();
	    Object o = sounds.get(num);
	    if (o instanceof URL) {
		URL file = (URL)o;
		tellLoadingMsg(file, soundLabel);
		try {
		    sounds.put(num, getAudioClip(file));
		} catch (Exception ex) {
		    return file;
		}
	    }
	}
	return null;
    }

    /**
     * Parse the PAUSES parameter.  It looks like
     * 1000|500|||750, etc., where each item corresponds to a
     * source image.  Empty items mean that the corresponding image
     * has no special duration, and should use the global one.
     *
     * @return a Hashtable of Integer pauses keyed to Integer
     * frame numbers.
     */
    Hashtable parseDurations(String attr, Vector images) {
	Hashtable result = new Hashtable();

	int imageNum = 0;
	int numImages = images.size();
	for (int i = 0; i < attr.length(); ) {
	    if (imageNum >= numImages) break;
	    
	    int next = attr.indexOf('|', i);
	    if (next == -1) next = attr.length();

	    if (i != next) {
		int duration = Integer.parseInt(attr.substring(i, next));
		result.put(new Integer(imageNum), new Integer(duration));
	    } else {
		result.put(new Integer(imageNum),
			   new Integer(globalPause));
	    }
	    i = next + 1;
	    imageNum++;
	}

	return result;
    }

    /**
     * Parse a String of form xxx@yyy and return a Point.
     */
    Point parsePoint(String s) throws ParseException {
	int atPos = s.indexOf('@');
	if (atPos == -1) throw new ParseException("Illegal position: "+s);
	return new Point(Integer.parseInt(s.substring(0, atPos)),
			 Integer.parseInt(s.substring(atPos + 1)));
    }


    /**
     * Parse the POSITIONS parameter.  It looks like
     * 10@30|11@31|||12@20, etc., where each item is an X@Y coordinate
     * corresponding to a source image.  Empty items mean that the
     * corresponding image has the same position as the preceding one.
     *
     * @return a Hashtable of Points keyed to Integer frame numbers.
     */
    Hashtable parsePositions(String param, Vector images)
    throws ParseException {
	Hashtable result = new Hashtable();

	int imageNum = 0;
	int numImages = images.size();
	for (int i = 0; i < param.length(); ) {
	    if (imageNum >= numImages) break;
	    
	    int next = param.indexOf('|', i);
	    if (next == -1) next = param.length();

	    if (i != next) {
		result.put(new Integer(imageNum),
			   parsePoint(param.substring(i, next)));
	    }
	    i = next + 1;
	    imageNum++;
	}

	return result;
    }
    
    /**
     * Get the dimensions of an image.
     * @return the image's dimensions.
     */
    Dimension getImageDimensions(Image im) {
	return new Dimension(im.getWidth(null), im.getHeight(null));
    }

    /**
     * Substitute an integer some number of times in a string, subject to
     * parameter strings embedded in the string.
     * Parameter strings:
     *   %N - substitute the integer as is, with no padding.
     *   %<digit>, for example %5 - substitute the integer left-padded with
     *        zeros to <digits> digits wide.
     *   %% - substitute a '%' here.
     * @param inStr the String to substitute within
     * @param theInt the int to substitute, as a String.
     */
    String doSubst(String inStr, String theInt) {
	String padStr = "0000000000";
	int length = inStr.length();
	StringBuffer result = new StringBuffer(length);
	
	for (int i = 0; i < length;) {
	    char ch = inStr.charAt(i);
	    if (ch == '%') {
		i++;
		if (i == length) {
		    result.append(ch);
		} else {
		    ch = inStr.charAt(i);
		    if (ch == 'N') {
			// just stick in the number, unmolested
			result.append(theInt);
			i++;
		    } else {
			int pad;
			if ((pad = Character.digit(ch, 10)) != -1) {
			    // we've got a width value
			    String numStr = theInt;
			    String scr = padStr+numStr;
			    result.append(scr.substring(scr.length() - pad));
			    i++;
			} else {
			    result.append(ch);
			    i++;
			}
		    }
		}
	    } else {
		result.append(ch);
		i++;
	    }
	}
	return result.toString();
    }	

    /**
     * Stuff a range of image names into a Vector.
     * @return a Vector of image URLs.
     */
    Vector prepareImageRange(int startImage, int endImage, String pattern)
    throws MalformedURLException {
	Vector result = new Vector(Math.abs(endImage - startImage) + 1);
	if (pattern == null) {
	    pattern = "T%N.gif";
	}
	if (startImage > endImage) {
	    for (int i = startImage; i >= endImage; i--) {
		result.addElement(new URL(imageSource, doSubst(pattern, i+"")));
	    }
	} else {
	    for (int i = startImage; i <= endImage; i++) {
		result.addElement(new URL(imageSource, doSubst(pattern, i+"")));
	    }
	}
	return result;
    }

    
    /**
     * Initialize the applet.  Get parameters.
     */
    public void init() {


	tracker = new MediaTracker(this);

	appWidth = size().width;
	appHeight = size().height;

	try {
	    String param = getParameter("IMAGESOURCE");	
	    imageSource = (param == null) ? getDocumentBase() : new URL(getDocumentBase(), param + "/");
	
	    String href = getParameter("HREF");
	    if (href != null) {
		try {
		    hrefURL = new URL(getDocumentBase(), href);
		} catch (MalformedURLException e) {
		    showParseError(e);
		}
	    }

	    param = getParameter("PAUSE");
	    globalPause =
		(param != null) ? Integer.parseInt(param) : defaultPause;

	    param = getParameter("REPEAT");
	    repeat = (param == null) ? true : (param.equalsIgnoreCase("yes") ||
					       param.equalsIgnoreCase("true"));

	    int startImage = 1;
	    int endImage = 1;
	    param = getParameter("ENDIMAGE");
	    if (param != null) {
		endImage = Integer.parseInt(param);
		param = getParameter("STARTIMAGE");
		if (param != null) {
		    startImage = Integer.parseInt(param);
		}
		param = getParameter("NAMEPATTERN");
		images = prepareImageRange(startImage, endImage, param);
	    } else {
		param = getParameter("STARTIMAGE");
		if (param != null) {
		    startImage = Integer.parseInt(param);
		    param = getParameter("NAMEPATTERN");
		    images = prepareImageRange(startImage, endImage, param);
		} else {
		    param = getParameter("IMAGES");
		    if (param == null) {
			showStatus("No legal IMAGES, STARTIMAGE, or ENDIMAGE "+
				   "specified.");
			return;
		    } else {
			images = parseImages(param,
					     getParameter("NAMEPATTERN"));
		    }
		}
	    }

	    param = getParameter("BACKGROUND");
	    if (param != null) {
		backgroundImageURL = new URL(imageSource, param);
	    }

	    // REMIND implement this!
	    param = getParameter("BACKGROUNDCOLOR");
	    if (param != null) {
		backgroundColor = decodeColor(param);
	    }

	    param = getParameter("STARTUP");
	    if (param != null) {
		startUpImageURL = new URL(imageSource, param);
	    }

	    param = getParameter("SOUNDSOURCE");
	    soundSource = (param == null) ? imageSource : new URL(getDocumentBase(), param + "/");
	
	    param = getParameter("SOUNDS");
	    if (param != null) {
		sounds = parseSounds(param, images);
	    }

	    param = getParameter("PAUSES");
	    if (param != null) {
		durations = parseDurations(param, images);
	    }

	    param = getParameter("POSITIONS");
	    if (param != null) {
		positions = parsePositions(param, images);
	    }

	    param = getParameter("SOUNDTRACK");
	    if (param != null) {
		soundtrackURL = new URL(soundSource, param);
	    }
	} catch (MalformedURLException e) {
	    showParseError(e);
	} catch (ParseException e) {
	    showParseError(e);
	}

	setFrameNum(0);
    }

    Color decodeColor(String s) {
	int val = 0;
	try {
	    if (s.startsWith("0x")) {
		val = Integer.parseInt(s.substring(2), 16);
	    } else if (s.startsWith("#")) {
		val = Integer.parseInt(s.substring(1), 16);
	    } else if (s.startsWith("0") && s.length() > 1) {
		val = Integer.parseInt(s.substring(1), 8);
	    } else {
		val = Integer.parseInt(s, 10);
	    }
	    return new Color(val);
	} catch (NumberFormatException e) {
	    return null;
	}
    }

    void tellLoadingMsg(String file, String fileType) {
	showStatus("Animator: loading "+fileType+" "+file);
    }

    void tellLoadingMsg(URL url, String fileType) {
	tellLoadingMsg(url.toExternalForm(), fileType);
    }

    void clearLoadingMessage() {
	showStatus("");
    }
    
    void loadError(String fileName, String fileType) {
	String errorMsg = "Animator: Couldn't load "+fileType+" "+
	    fileName;
	showStatus(errorMsg);
	System.err.println(errorMsg);
	error = true;
	repaint();
    }

    void loadError(URL badURL, String fileType) {
	loadError(badURL.toExternalForm(), fileType);
    }

    void showParseError(Exception e) {
	String errorMsg = "Animator: Parse error: "+e;
	showStatus(errorMsg);
	System.err.println(errorMsg);
	error = true;
	repaint();
    }

    void startPlaying() {
	if (soundtrack != null) {
	    soundtrack.loop();
	}
    }

    void stopPlaying() {
	if (soundtrack != null) {
	    soundtrack.stop();
	}
    }

    /**
     * Run the animation. This method is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
	Thread me = Thread.currentThread();
	URL badURL;
	
	me.setPriority(Thread.MIN_PRIORITY);

	if (! loaded) {
	    try {
		// ... to do a bunch of loading.
		if (startUpImageURL != null) {
		    tellLoadingMsg(startUpImageURL, imageLabel);
		    startUpImage = getImage(startUpImageURL);
		    tracker.addImage(startUpImage, STARTUP_ID);
		    tracker.waitForID(STARTUP_ID);
		    if (tracker.isErrorID(STARTUP_ID)) {
			loadError(startUpImageURL, "start-up image");
		    }
		    repaint();
		}
	    
		if (backgroundImageURL != null) {
		    tellLoadingMsg(backgroundImageURL, imageLabel);
		    backgroundImage = getImage(backgroundImageURL);
		    tracker.addImage(backgroundImage, BACKGROUND_ID);
		    tracker.waitForID(BACKGROUND_ID);
		    if (tracker.isErrorID(BACKGROUND_ID)) {
			loadError(backgroundImageURL, "background image");
		    }
		    repaint();
		}

		// Fetch the animation frames
		if (!fetchImages(images)) {
		    Object errors[] = tracker.getErrorsAny();
		    Image im = (Image)errors[0];
		    // Need to get some kind of readable name here
		    loadError(im+"", imageLabel);
		    return;
		}

		if (soundtrackURL != null && soundtrack == null) {
		    tellLoadingMsg(soundtrackURL, imageLabel);
		    soundtrack = getAudioClip(soundtrackURL);
		    if (soundtrack == null) {
			loadError(soundtrackURL, "soundtrack");
			return;
		    }
		}

		if (sounds != null) {
		    badURL = fetchSounds(sounds);
		    if (badURL != null) {
			loadError(badURL, soundLabel);
			return;
		    }
		}

		clearLoadingMessage();

		offScrImage = createImage(appWidth, appHeight);
		offScrGC = offScrImage.getGraphics();
		offScrGC.setColor(Color.lightGray);

		loaded = true;
		error = false;
	    } catch (Exception e) {
		error = true;
		e.printStackTrace();
	    }
	}

	if (userPause) {
	    return;
	}

	if (repeat || frameNum < images.size()) {
	    startPlaying();
	}

	try {
	    if (images.size() > 1) {
		while (appWidth > 0 && appHeight > 0 && engine == me) {
		    if (frameNum >= images.size()) {
			if (!repeat) {
			    return;
			}
			setFrameNum(0);
		    }
		    repaint();

		    if (sounds != null) {
			AudioClip clip =
			    (AudioClip)sounds.get(frameNumKey);
			if (clip != null) {
			    clip.play();
			}
		    }

		    try {
			Integer pause = null;
			if (durations != null) {
			    pause = (Integer)durations.get(frameNumKey);
			}
			if (pause == null) {
			    Thread.sleep(globalPause);
			} else {
			    Thread.sleep(pause.intValue());
			}
		    } catch (InterruptedException e) {
			// Should we do anything?
		    }
		    setFrameNum(frameNum+1);
		}
	    }
	} finally {
	    stopPlaying();
	}
    }

    /**
     * No need to clear anything; just paint.
     */
    public void update(Graphics g) {
	paint(g);
    }

    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	if (error || !loaded) {
	    if (startUpImage != null) {
		if (tracker.checkID(STARTUP_ID)) {
		    g.drawImage(startUpImage, 0, 0, this);
		}
	    } else {
		if (backgroundImage != null) {
		    if (tracker.checkID(BACKGROUND_ID)) {
			g.drawImage(backgroundImage, 0, 0, this);
		    }
		} else {
		    g.clearRect(0, 0, appWidth, appHeight);
		}
	    }
	} else {
	    if ((images != null) && (images.size() > 0) &&
		tracker.checkID(ANIMATION_ID)) {
		if (frameNum < images.size()) {
		    if (backgroundImage == null) {
			offScrGC.clearRect(0, 0, appWidth, appHeight);
		    } else {
			offScrGC.drawImage(backgroundImage, 0, 0, this);
		    }

		    Image image = (Image)images.elementAt(frameNum);
		    
		    Point pos = null;
		    if (positions != null) {
			pos = (Point)positions.get(frameNumKey);
		    }
		    if (pos != null) {
			xPos = pos.x;
			yPos = pos.y;
		    }
		    if (backgroundColor != null) {
			offScrGC.setColor(backgroundColor);
			offScrGC.fillRect(0, 0, appWidth, appHeight);
			offScrGC.drawImage(image, xPos, yPos, backgroundColor,
					   this);
		    } else {
			offScrGC.drawImage(image, xPos, yPos, this);
		    }
		    g.drawImage(offScrImage, 0, 0, this);
		} else {
		    // no more animation, but need to draw something
		    dbg("No more animation; drawing last image.");
		    if (backgroundImage == null) {
			g.fillRect(0, 0, appWidth, appHeight);
		    } else {
			g.drawImage(backgroundImage, 0, 0, this);
		    }
		    g.drawImage((Image)images.lastElement(), 0, 0, this);
		}
	    }
	}
    }

    /**
     * Start the applet by forking an animation thread.
     */
    public void start() {
	if (engine == null) {
	    engine = new Thread(this);
	    engine.start();
	}
	showStatus(getAppletInfo());
    }

    /**
     * Stop the insanity, um, applet.
     */
    public void stop() {
	if (engine != null && engine.isAlive()) {
	    engine.stop();
	}
	engine = null;
    }

    /**
     * Pause the thread when the user clicks the mouse in the applet.
     * If the thread has stopped (as in a non-repeat performance),
     * restart it.
     */
    public boolean handleEvent(Event evt) {
	switch (evt.id) {
	case Event.MOUSE_DOWN:
	    if ((evt.modifiers & Event.SHIFT_MASK) != 0) {
		showDescription();
		return true;
	    } else if (hrefURL != null) {
		// let mouse-up handle going to the new page
		return true;
	    } else if (loaded) {
		if (engine != null && engine.isAlive()) {
		    if (userPause) {
			engine.resume();
			startPlaying();
		    } else {
			engine.suspend();
			stopPlaying();
		    }
		    userPause = !userPause;
		} else {
		    userPause = false;
		    setFrameNum(0);
		    engine = new Thread(this);
		    engine.start();
		}
	    }
	    return true;
	case Event.MOUSE_UP:
	    if (hrefURL != null && ((evt.modifiers & Event.SHIFT_MASK) == 0)) {
		getAppletContext().showDocument(hrefURL);
	    }
	    return true;
	case Event.MOUSE_ENTER:
	    showStatus(getAppletInfo()+" -- shift-click for info");
	    return true;
	case Event.MOUSE_EXIT:
	    showStatus("");
	    return true;
	case Event.KEY_ACTION:
	case Event.KEY_RELEASE:
	case Event.KEY_ACTION_RELEASE:
	    dbg("Got event "+evt);
	    return true;
	default:
	    return super.handleEvent(evt);
	}
    }
    
}


class ParseException extends Exception {
    ParseException(String s) {
	super(s);
    }
}


class DescriptionFrame extends Frame {

    static final int rows = 26;
    static final int cols = 70;
    
    TextArea info;

    DescriptionFrame() {
	super("Animator v1.8");
	add("Center", info = new TextArea(rows, cols));
	info.setEditable(false);
	info.setBackground(Color.white);
	Panel buttons = new Panel();
	buttons.setLayout(new FlowLayout());
	add("South", buttons);
	buttons.add(new Button("Cancel"));
	
	pack();
    }

    public void show() {
	info.select(0,0);
	super.show();
    }

    void tell(String s) {
	info.appendText(s);
    }

    public boolean action(Event e, Object arg) {
	if (e.target instanceof Button) {
	    hide();
	    return true;
	}
	return false;
    }
}
