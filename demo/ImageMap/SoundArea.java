/*
 * @(#)SoundArea.java	1.6 96/04/24  
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
import java.applet.AudioClip;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * An audio feedback ImageArea class.
 * This class extends the basic ImageArea Class to play a sound when
 * the user enters the area.
 *
 * @author 	Jim Graham
 * @author 	Chuck McManis
 * @version 	1.6, 04/24/96
 */
class SoundArea extends ImageMapArea {
    /** The URL of the sound to be played. */
    URL sound;
    AudioClip soundData = null;
    boolean hasPlayed; 
    boolean isReady = false;
    long	lastExit = 0;
    final static int HYSTERESIS = 1500;

    /**
     * The argument is the URL of the sound to be played.
     */
    public void handleArg(String arg) {
	try {
	    sound = new URL(parent.getDocumentBase(), arg);
	} catch (MalformedURLException e) {
	    sound = null;
	}
	hasPlayed = false;
    }

    /**
     * The applet thread calls the getMedia() method when the applet
     * is started.
     */
    public void getMedia() {
	if (sound != null && soundData == null) {
	    soundData = parent.getAudioClip(sound);
	}
	if (soundData == null) {
	    System.out.println("SoundArea: Unable to load data "+sound);
	}
	isReady = true;
    }

    /**
     * The enter method is called when the mouse enters the area.
     * The sound is played if the mouse has been outside of the
     * area for more then the delay indicated by HYSTERESIS.
     */
    public void enter() {
	// is the sound sample loaded?
	if (! isReady) {
	    parent.showStatus("Loading media file...");
	    return;
	}

	/*
 	 * So we entered the selection region, play the sound if
	 * we need to. Track the mouse entering and exiting the
	 * the selection box. If it doesn't stay out for more than
	 * "HYSTERESIS" millis, then don't re-play the sound.
	 */
	long now = System.currentTimeMillis();
	if (Math.abs(now - lastExit) < HYSTERESIS) {
	    // if within the window pretend that it was played.
	    hasPlayed = true;
    	    return;
	}

	// Else play the sound.
	if (! hasPlayed && (soundData != null)) {
	    hasPlayed = true;
	    soundData.play();
	}
    }

    /**
     * The exit method is called when the mouse leaves the area.
     */
    public void exit() {
	if (hasPlayed) {
	    hasPlayed = false;
	    lastExit = System.currentTimeMillis(); // note the time of exit
	}
    }
}
