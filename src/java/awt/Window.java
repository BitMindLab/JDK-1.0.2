/*
 * @(#)Window.java	1.24 96/05/02 Arthur van Hoff
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package java.awt;

import java.awt.peer.WindowPeer;

/**
 * A Window is a top-level window with no borders and no
 * menubar. It could be used to implement a pop-up menu.
 * The default layout for a window is BorderLayout.
 *
 * @version 	1.24, 02 May 1996
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class Window extends Container {
    String      warningString;

    private FocusManager   focusMgr;

    Window() {
	SecurityManager sm = System.getSecurityManager();
	if ((sm != null) && !sm.checkTopLevelWindow(this)) {
	    warningString = System.getProperty("awt.appletWarning",
					       "Warning: Applet Window");
	}
	focusMgr = new FocusManager(this);
    }

    /**
     * Constructs a new Window initialized to an invisible state. It
     * behaves as a modal dialog in that it will block input to other
     * windows when shown.
     *
     * @param parent the owner of the dialog
     * @see Component#resize
     * @see #show
     */
    public Window(Frame parent) {
/*
	SecurityManager sm = System.getSecurityManager();

	if ((sm != null) && !sm.checkTopLevelWindow(this)) {
	    warningString = System.getProperty("awt.appletWarning",
					       "Warning: Applet Window");
	}
*/
	this();
	this.parent = parent;
	visible = false;
	setLayout(new BorderLayout());
    }

    /**
     * Creates the Window's peer.  The peer allows us to modify the appearance of the
     * Window without changing its functionality.
     */
    public synchronized void addNotify() {
	if (peer == null) {
	    peer = getToolkit().createWindow(this);
	}
	super.addNotify();
    }

    /**
     * Packs the components of the Window.
     */
    public synchronized void pack() {
        if (parent != null && parent.getPeer() == null) {
            parent.addNotify();
        }
	if (peer == null) {
	    addNotify();
	}
	resize(preferredSize());
	validate();
    }

    /**
     * Shows the Window. This will bring the window to the
     * front if the window is already visible.
     * @see Component#hide
     */
    public void show() {
	synchronized(this) {
            if (parent != null && parent.getPeer() == null) {
            	parent.addNotify();
            }
	    if (peer == null) {
		addNotify();
	    }
	    validate();	    
	}
	if (visible) {
	    toFront();
	} else {
	    synchronized(this) {
	        visible = true;
	    }
	    peer.show();
	}
    }

    /**
     * Disposes of the Window. This method must
     * be called to release the resources that
     * are used for the window.
     */
    public synchronized void dispose() {
	hide();
	removeNotify();
    }

    /**
     * Brings the frame to the front of the Window.
     */
    public void toFront() {
	WindowPeer peer = (WindowPeer)this.peer;
	if (peer != null) {
	    peer.toFront();
	}
    }

    /**
     * Sends the frame to the back of the Window.
     */
    public void toBack() {
	WindowPeer peer = (WindowPeer)this.peer;
	if (peer != null) {
	    peer.toBack();
	}
    }

    /**
     * Returns the toolkit of this frame.
     * @see Toolkit
     */
    public Toolkit getToolkit() {
	return Toolkit.getDefaultToolkit();
    }

    /**
     * Gets the warning string for this window. This is
     * a string that will be displayed somewhere in the
     * visible area of windows that are not secure.
     */
    public final String getWarningString() {
	return warningString;
    }

    /* Handle TAB and Shift-TAB events. */
    boolean handleTabEvent(Event e) {
        if (e.id != Event.KEY_PRESS && e.id != Event.KEY_RELEASE) {
            return false;
        }
        if (e.key != '\t' || (e.target instanceof TextArea)) {
            return false;
        }
	if ((e.modifiers & ~Event.SHIFT_MASK) > 0) {
	    return false;
	}
	if (e.id == Event.KEY_RELEASE) {
	    return true;
	}
	if (e.shiftDown()) {
	    return focusMgr.focusPrevious();
	} else {
	    return focusMgr.focusNext();
	}
     }

    void setFocusOwner(Component c) {
	focusMgr.setFocusOwner(c);
    }

    void nextFocus(Component base) {
	focusMgr.focusNext(base);
    }
}

class FocusManager {
     Container focusRoot;
     Component focusOwner;

     FocusManager(Container cont) {
	focusRoot = cont;
     }
     
     synchronized void setFocusOwner(Component c) {
	focusOwner = c;
     }
	
     boolean focusNext() {
	return focusNext(focusOwner);
     }

     synchronized boolean focusNext(Component base) {
	int i;	
	Component target = base;
	if (target == null || target.parent == null) {
	    return false;
	}   
	do {
	    boolean found = false;
	    Container p = target.parent;
	    Component c;
	    for (i = 0; i < p.ncomponents; i++) {
	    	c = p.component[i];
	    	if (found) {
		    if (c instanceof Container) {
		    	if (focusForward((Container)c)) {
			    return true;
		    	}
		    } else {
		    	if (assignFocus(c)) {
			    return true;
		    	}
		    }		    
	    	} else if (c == target) {
		    found = true;	
	    	}
	    } 
	    target = p;
	} while (target != focusRoot);

        return false;		
    }

    boolean focusPrevious() {
	return focusPrevious(focusOwner);
    }
    
    synchronized boolean focusPrevious(Component base) {
	int i;
	Component target = base;
	if (target == null || target.parent == null) {
	    return false;
	}       
	do {
	    boolean found = false;
	    Container p = target.parent;
	    Component c;
	    for (i = p.ncomponents-1; i >= 0; i--) {
	    	c = p.component[i];
	    	if (found) {
		    if (c instanceof Container) {
		    	if (focusBackward((Container)c)) {
			    return true;
		    	}
		    } else {
		    	if (assignFocus(c)) {
			    return true;
		    	}
		    }		    
	    	} else if (c == target) {
		    found = true;	
	    	}
	    } 
	    target = p;
	} while (target != focusRoot);

        return false;		
    }

    boolean assignFocus(Component c) {
        if (c.isVisible() && c.tabbable() && c.isEnabled() ) {
            c.requestFocus();
            return true;
        }
        return false;
    }

    boolean focusForward(Container cont) {
	int i;
        for(i = 0; i < cont.ncomponents; i++) {
           if (cont.component[i] instanceof Container) {
                if (focusForward((Container)cont.component[i]) == true) {
                    return true;
                }
            } else {
                if (assignFocus(cont.component[i]) == true) {
                    return true;
                }
            }    
        }
        return false;
    }

    boolean focusBackward(Container cont) {
	int i;
        for(i = cont.ncomponents-1; i >= 0; i--) {
           if (cont.component[i] instanceof Container) {
                if (focusBackward((Container)cont.component[i]) == true) {
                    return true;
                }
            } else {
                if (assignFocus(cont.component[i]) == true) {
                    return true;
                }
            }    
        }
        return false;
    }
}
