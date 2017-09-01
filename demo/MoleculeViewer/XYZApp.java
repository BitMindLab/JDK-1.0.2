/*
 * @(#)XYZApp.java
 * 
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted. 
 * Please refer to the file http://www.javasoft.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://www.javasoft.com/licensing.html for further important licensing
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

/*
 * A set of classes to parse, represent and display Chemical compounds in
 * .xyz format (see http://chem.leeds.ac.uk/Project/MIME.html)
 */

import java.applet.Applet;
import java.awt.Image;
import java.awt.Event;
import java.awt.Graphics;
import java.io.StreamTokenizer;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.awt.image.IndexColorModel;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;


/** The representation of a Chemical .xyz model */
class XYZChemModel {
    float vert[];
    Atom atoms[];
    int tvert[];
    int ZsortMap[];
    int nvert, maxvert;

    static Hashtable atomTable = new Hashtable();
    static Atom defaultAtom;
    static {
	atomTable.put("c", new Atom(0, 0, 0));
	atomTable.put("h", new Atom(210, 210, 210));
	atomTable.put("n", new Atom(0, 0, 255));
	atomTable.put("o", new Atom(255, 0, 0));
	atomTable.put("p", new Atom(255, 0, 255));
	atomTable.put("s", new Atom(255, 255, 0));
	atomTable.put("hn", new Atom(150, 255, 150)); /* !!*/
	defaultAtom = new Atom(255, 100, 200);
    }

    boolean transformed;
    Matrix3D mat;

    float xmin, xmax, ymin, ymax, zmin, zmax;


    XYZChemModel () {
	mat = new Matrix3D();
	mat.xrot(20);
	mat.yrot(30);
    }


    /** Create a Cehmical model by parsing an input stream */
    XYZChemModel (InputStream is) throws Exception
    {
       this();
       StreamTokenizer st;
       st = new StreamTokenizer(new BufferedInputStream(is, 4000));
       st.eolIsSignificant(true);
       st.commentChar('#');
       int slot = 0;
      
       try
       {
scan:
          while (true)
          {
             switch ( st.nextToken() ) 
             {
                case StreamTokenizer.TT_EOF:
                   break scan;
                default:
                   break;
                case StreamTokenizer.TT_WORD:
                   String name = st.sval;
                   double x = 0, y = 0, z = 0;
                   if (st.nextToken() == StreamTokenizer.TT_NUMBER) 
                   {
                      x = st.nval;
                      if (st.nextToken() == StreamTokenizer.TT_NUMBER) 
                      {
                         y = st.nval;
                         if (st.nextToken() == StreamTokenizer.TT_NUMBER)
                            z = st.nval;
                      }
                   }
                   addVert(name, (float) x, (float) y, (float) z);
                   while( st.ttype != StreamTokenizer.TT_EOL &&
                          st.ttype != StreamTokenizer.TT_EOF )
                      st.nextToken();

             }   // end Switch

          }  // end while

          is.close();

       }  // end Try
       catch( IOException e) {}

       if (st.ttype != StreamTokenizer.TT_EOF)
          throw new Exception(st.toString());

    }  // end XYZChemModel()

    /** Add a vertex to this model */
    int addVert(String name, float x, float y, float z) {
	int i = nvert;
	if (i >= maxvert)
	    if (vert == null) {
		maxvert = 100;
		vert = new float[maxvert * 3];
		atoms = new Atom[maxvert];
	    } else {
		maxvert *= 2;
		float nv[] = new float[maxvert * 3];
		System.arraycopy(vert, 0, nv, 0, vert.length);
		vert = nv;
		Atom na[] = new Atom[maxvert];
		System.arraycopy(atoms, 0, na, 0, atoms.length);
		atoms = na;
	    }
	Atom a = (Atom) atomTable.get(name.toLowerCase());
	if (a == null) a = defaultAtom;
	atoms[i] = a;
	i *= 3;
	vert[i] = x;
	vert[i + 1] = y;
	vert[i + 2] = z;
	return nvert++;
    }

    /** Transform all the points in this model */
    void transform() {
	if (transformed || nvert <= 0)
	    return;
	if (tvert == null || tvert.length < nvert * 3)
	    tvert = new int[nvert * 3];
	mat.transform(vert, tvert, nvert);
	transformed = true;
    }


    /** Paint this model to a graphics context.  It uses the matrix associated
	with this model to map from model space to screen space.
	The next version of the browser should have double buffering,
	which will make this *much* nicer */
    void paint(Graphics g) {
	if (vert == null || nvert <= 0)
	    return;
	transform();
	int v[] = tvert;
	int zs[] = ZsortMap;
	if (zs == null) {
	    ZsortMap = zs = new int[nvert];
	    for (int i = nvert; --i >= 0;)
		zs[i] = i * 3;
	}

	/*
	 * I use a bubble sort since from one iteration to the next, the sort
	 * order is pretty stable, so I just use what I had last time as a
	 * "guess" of the sorted order.  With luck, this reduces O(N log N)
	 * to O(N)
	 */

	for (int i = nvert - 1; --i >= 0;) {
	    boolean flipped = false;
	    for (int j = 0; j <= i; j++) {
		int a = zs[j];
		int b = zs[j + 1];
		if (v[a + 2] > v[b + 2]) {
		    zs[j + 1] = a;
		    zs[j] = b;
		    flipped = true;
		}
	    }
	    if (!flipped)
		break;
	}

	int lg = 0;
	int lim = nvert;
	Atom ls[] = atoms;
	if (lim <= 0 || nvert <= 0)
	    return;
	for (int i = 0; i < lim; i++) {
	    int j = zs[i];
	    int grey = v[j + 2];
	    if (grey < 0)
		grey = 0;
	    if (grey > 15)
		grey = 15;
	    // g.drawString(names[i], v[j], v[j+1]);
	    atoms[j/3].paint(g, v[j], v[j + 1], grey);
	    // g.drawImage(iBall, v[j] - (iBall.width >> 1), v[j + 1] -
	    // (iBall.height >> 1));
	}
    }

    /** Find the bounding box of this model */
    void findBB() {
	if (nvert <= 0)
	    return;
	float v[] = vert;
	float xmin = v[0], xmax = xmin;
	float ymin = v[1], ymax = ymin;
	float zmin = v[2], zmax = zmin;
	for (int i = nvert * 3; (i -= 3) > 0;) {
	    float x = v[i];
	    if (x < xmin)
		xmin = x;
	    if (x > xmax)
		xmax = x;
	    float y = v[i + 1];
	    if (y < ymin)
		ymin = y;
	    if (y > ymax)
		ymax = y;
	    float z = v[i + 2];
	    if (z < zmin)
		zmin = z;
	    if (z > zmax)
		zmax = z;
	}
	this.xmax = xmax;
	this.xmin = xmin;
	this.ymax = ymax;
	this.ymin = ymin;
	this.zmax = zmax;
	this.zmin = zmin;
    }
}

/** An applet to put a Cehmical model into a page */
public class XYZApp extends Applet implements Runnable {
    XYZChemModel md;
    boolean painted = true;
    float xfac;
    int prevx, prevy;
    float xtheta, ytheta;
    float scalefudge = 1;
    Matrix3D amat = new Matrix3D(), tmat = new Matrix3D();
    String mdname = null;
    String message = null;
    Image backBuffer;
    Graphics backGC;


    public void init() {
	mdname = getParameter("model");
	try {
	    scalefudge = Float.valueOf(getParameter("scale")).floatValue();
	} catch(Exception e) {
	};
	amat.yrot(20);
	amat.xrot(20);
	if (mdname == null)
	    mdname = "model.obj";
	resize(size().width <= 20 ? 400 : size().width,
	       size().height <= 20 ? 400 : size().height);
    }
    public void run() {
	InputStream is = null;
	try {
	    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	    is = new URL(getDocumentBase(), mdname).openStream();
	    XYZChemModel m = new XYZChemModel (is);
	    Atom.setApplet(this);
	    md = m;
	    m.findBB();
	    float xw = m.xmax - m.xmin;
	    float yw = m.ymax - m.ymin;
	    float zw = m.zmax - m.zmin;
	    if (yw > xw)
		xw = yw;
	    if (zw > xw)
		xw = zw;
	    float f1 = size().width / xw;
	    float f2 = size().height / xw;
	    xfac = 0.7f * (f1 < f2 ? f1 : f2) * scalefudge;
	    backBuffer = createImage(size().width, size().height);
	    backGC = backBuffer.getGraphics();
	} catch(Exception e) {
	    e.printStackTrace();
	    md = null;
	    message = e.toString();
	}
	try {
	    if (is != null)
		is.close();
	} catch(Exception e) {
	}
	repaint();
    }
    public void start() {
	if (md == null && message == null)
	    new Thread(this).start();
    }
    public void stop() {
    }
    public boolean mouseDown(Event e, int x, int y) {
	prevx = x;
	prevy = y;
	return true;
    }
    public boolean mouseDrag(Event e, int x, int y) {
	tmat.unit();
	float xtheta = (prevy - y) * (360.0f / size().width);
	float ytheta = (x - prevx) * (360.0f / size().height);
	tmat.xrot(xtheta);
	tmat.yrot(ytheta);
	amat.mult(tmat);
	if (painted) {
	    painted = false;
	    repaint();
	}
	prevx = x;
	prevy = y;
	return true;
    }
    public void update(Graphics g) {
	if (backBuffer == null)
	    g.clearRect(0, 0, size().width, size().height);
	paint(g);
    }
    public void paint(Graphics g) {
	if (md != null) {
	    md.mat.unit();
	    md.mat.translate(-(md.xmin + md.xmax) / 2,
			     -(md.ymin + md.ymax) / 2,
			     -(md.zmin + md.zmax) / 2);
	    md.mat.mult(amat);
	    // md.mat.scale(xfac, -xfac, 8 * xfac / size().width);
	    md.mat.scale(xfac, -xfac, 16 * xfac / size().width);
	    md.mat.translate(size().width / 2, size().height / 2, 8);
	    md.transformed = false;
	    if (backBuffer != null) {
		backGC.setColor(getBackground());
		backGC.fillRect(0,0,size().width,size().height);
		md.paint(backGC);
		g.drawImage(backBuffer, 0, 0, this);
	    } else
		md.paint(g);
	    setPainted();
	} else if (message != null) {
	    g.drawString("Error in model:", 3, 20);
	    g.drawString(message, 10, 40);
	}
    }
    private synchronized void setPainted() {
	painted = true;
	notifyAll();
    }

    private synchronized void waitPainted() 
    {
       while (!painted)
       {
          try
          {
             wait();
          }
          catch (InterruptedException e) {}
       }
       painted = false;
    }
}   // end class XYZApp

class Atom {
    private static Applet applet;
    private static byte[] data;
    private final static int R = 40;
    private final static int hx = 15;
    private final static int hy = 15;
    private final static int bgGrey = 192;
    private final static int nBalls = 16;
    private static int maxr;

    private int Rl;
    private int Gl;
    private int Bl;
    private Image balls[];

    static {
	data = new byte[R * 2 * R * 2];
	int mr = 0;
	for (int Y = 2 * R; --Y >= 0;) {
	    int x0 = (int) (Math.sqrt(R * R - (Y - R) * (Y - R)) + 0.5);
	    int p = Y * (R * 2) + R - x0;
	    for (int X = -x0; X < x0; X++) {
		int x = X + hx;
		int y = Y - R + hy;
		int r = (int) (Math.sqrt(x * x + y * y) + 0.5);
		if (r > mr)
		    mr = r;
		data[p++] = r <= 0 ? 1 : (byte) r;
	    }
	}
	maxr = mr;
    }
    static void setApplet(Applet app) {
	applet = app;
    }
    Atom(int Rl, int Gl, int Bl) {
	this.Rl = Rl;
	this.Gl = Gl;
	this.Bl = Bl;
    }
    private final int blend(int fg, int bg, float fgfactor) {
	return (int) (bg + (fg - bg) * fgfactor);
    }
    private void Setup() {
	balls = new Image[nBalls];
	byte red[] = new byte[256];
	red[0] = (byte) bgGrey;
	byte green[] = new byte[256];
	green[0] = (byte) bgGrey;
	byte blue[] = new byte[256];
	blue[0] = (byte) bgGrey;
	for (int r = 0; r < nBalls; r++) {
	    float b = (float) (r+1) / nBalls;
	    for (int i = maxr; i >= 1; --i) {
		float d = (float) i / maxr;
		red[i] = (byte) blend(blend(Rl, 255, d), bgGrey, b);
		green[i] = (byte) blend(blend(Gl, 255, d), bgGrey, b);
		blue[i] = (byte) blend(blend(Bl, 255, d), bgGrey, b);
	    }
	    IndexColorModel model = new IndexColorModel(8, maxr + 1,
							red, green, blue, 0);
	    balls[r] = applet.createImage(
		new MemoryImageSource(R*2, R*2, model, data, 0, R*2));
	}
    }
    void paint(Graphics gc, int x, int y, int r) {
	Image ba[] = balls;
	if (ba == null) {
	    Setup();
	    ba = balls;
	}
	Image i = ba[r];
	int size = 10 + r;
	gc.drawImage(i, x - (size >> 1), y - (size >> 1), size, size, applet);
    }
}
