/*
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 * @modified 96/04/24 Jim Hagen : changed stressColor
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

import java.util.*;
import java.awt.*;
import java.applet.Applet;

class Node {
    double x;
    double y;

    double dx;
    double dy;

    boolean fixed;

    String lbl;
}

class Edge {
    int from;
    int to;

    double len;
}

class GraphPanel extends Panel implements Runnable {
    Graph graph;
    int nnodes;
    Node nodes[] = new Node[100];

    int nedges;
    Edge edges[] = new Edge[200];

    Thread relaxer;
    boolean stress;
    boolean random;

    GraphPanel(Graph graph) {
	this.graph = graph;
    }

    int findNode(String lbl) {
	for (int i = 0 ; i < nnodes ; i++) {
	    if (nodes[i].lbl.equals(lbl)) {
		return i;
	    }
	}
	return addNode(lbl);
    }
    int addNode(String lbl) {
	Node n = new Node();
	n.x = 10 + 380*Math.random();
	n.y = 10 + 380*Math.random();
	n.lbl = lbl;
	nodes[nnodes] = n;
	return nnodes++;
    }
    void addEdge(String from, String to, int len) {
	Edge e = new Edge();
	e.from = findNode(from);
	e.to = findNode(to);
	e.len = len;
	edges[nedges++] = e;
    }

    public void run() {
	while (true) {
	    relax();
	    if (random && (Math.random() < 0.03)) {
		Node n = nodes[(int)(Math.random() * nnodes)];
		if (!n.fixed) {
		    n.x += 100*Math.random() - 50;
		    n.y += 100*Math.random() - 50;
		}
		graph.play(graph.getCodeBase(), "audio/drip.au");
	    }
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		break;
	    }
	}
    }

    synchronized void relax() {
	for (int i = 0 ; i < nedges ; i++) {
	    Edge e = edges[i];
	    double vx = nodes[e.to].x - nodes[e.from].x;
	    double vy = nodes[e.to].y - nodes[e.from].y;
	    double len = Math.sqrt(vx * vx + vy * vy);
	    double f = (edges[i].len - len) / (len * 3) ;
	    double dx = f * vx;
	    double dy = f * vy;

	    nodes[e.to].dx += dx;
	    nodes[e.to].dy += dy;
	    nodes[e.from].dx += -dx;
	    nodes[e.from].dy += -dy;
	}

	for (int i = 0 ; i < nnodes ; i++) {
	    Node n1 = nodes[i];
	    double dx = 0;
	    double dy = 0;

	    for (int j = 0 ; j < nnodes ; j++) {
		if (i == j) {
		    continue;
		}
		Node n2 = nodes[j];
		double vx = n1.x - n2.x;
		double vy = n1.y - n2.y;
		double len = vx * vx + vy * vy;
		if (len == 0) {
		    dx += Math.random();
		    dy += Math.random();
		} else if (len < 100*100) {
		    dx += vx / len;
		    dy += vy / len;
		}
	    }
	    double dlen = dx * dx + dy * dy;
	    if (dlen > 0) {
		dlen = Math.sqrt(dlen) / 2;
		n1.dx += dx / dlen;
		n1.dy += dy / dlen;
	    }
	}

	Dimension d = size();
	for (int i = 0 ; i < nnodes ; i++) {
	    Node n = nodes[i];
	    if (!n.fixed) {
		n.x += Math.max(-5, Math.min(5, n.dx));
		n.y += Math.max(-5, Math.min(5, n.dy));
		//System.out.println("v= " + n.dx + "," + n.dy);
		if (n.x < 0) {
		    n.x = 0;
		} else if (n.x > d.width) {
		    n.x = d.width;
		}
		if (n.y < 0) {
		    n.y = 0;
		} else if (n.y > d.height) {
		    n.y = d.height;
		}
	    }
	    n.dx /= 2;
	    n.dy /= 2;
	}
	repaint();
    }

    Node pick;
    boolean pickfixed;
    Image offscreen;
    Dimension offscreensize;
    Graphics offgraphics;
    

    final Color fixedColor = Color.red;
    final Color selectColor = Color.pink;
    final Color edgeColor = Color.black;
    final Color nodeColor = new Color(250, 220, 100);
    final Color stressColor = Color.darkGray;
    final Color arcColor1 = Color.black;
    final Color arcColor2 = Color.pink;
    final Color arcColor3 = Color.red;

    public void paintNode(Graphics g, Node n, FontMetrics fm) {
	int x = (int)n.x;
	int y = (int)n.y;
	g.setColor((n == pick) ? selectColor : (n.fixed ? fixedColor : nodeColor));
	int w = fm.stringWidth(n.lbl) + 10;
	int h = fm.getHeight() + 4;
	g.fillRect(x - w/2, y - h / 2, w, h);
	g.setColor(Color.black);
	g.drawRect(x - w/2, y - h / 2, w-1, h-1);
	g.drawString(n.lbl, x - (w-10)/2, (y - (h-4)/2) + fm.getAscent());
    }

    public synchronized void update(Graphics g) {
	Dimension d = size();
	if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
	    offscreen = createImage(d.width, d.height);
	    offscreensize = d;
	    offgraphics = offscreen.getGraphics();
	    offgraphics.setFont(getFont());
	}

	offgraphics.setColor(getBackground());
	offgraphics.fillRect(0, 0, d.width, d.height);
	for (int i = 0 ; i < nedges ; i++) {
	    Edge e = edges[i];
	    int x1 = (int)nodes[e.from].x;
	    int y1 = (int)nodes[e.from].y;
	    int x2 = (int)nodes[e.to].x;
	    int y2 = (int)nodes[e.to].y;
	    int len = (int)Math.abs(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) - e.len);
	    offgraphics.setColor((len < 10) ? arcColor1 : (len < 20 ? arcColor2 : arcColor3)) ;
	    offgraphics.drawLine(x1, y1, x2, y2);
	    if (stress) {
		String lbl = String.valueOf(len);
		offgraphics.setColor(stressColor);
		offgraphics.drawString(lbl, x1 + (x2-x1)/2, y1 + (y2-y1)/2);
		offgraphics.setColor(edgeColor);
	    }
	}

	FontMetrics fm = offgraphics.getFontMetrics();
	for (int i = 0 ; i < nnodes ; i++) {
	    paintNode(offgraphics, nodes[i], fm);
	}

	g.drawImage(offscreen, 0, 0, null);
    }

    public synchronized boolean mouseDown(Event evt, int x, int y) {
	double bestdist = Double.MAX_VALUE;
	for (int i = 0 ; i < nnodes ; i++) {
	    Node n = nodes[i];
	    double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
	    if (dist < bestdist) {
		pick = n;
		bestdist = dist;
	    }
	}
	pickfixed = pick.fixed;
	pick.fixed = true;
	pick.x = x;
	pick.y = y;
	repaint();
	return true;
    }

    public synchronized boolean mouseDrag(Event evt, int x, int y) {
	pick.x = x;
	pick.y = y;
	repaint();
	return true;
    }

    public synchronized boolean mouseUp(Event evt, int x, int y) {
	pick.x = x;
	pick.y = y;
	pick.fixed = pickfixed;
	pick = null;
	
	repaint();
	return true;
    }

    public void start() {
	relaxer = new Thread(this);
	relaxer.start();
    }
    public void stop() {
	relaxer.stop();
    }
}

public class Graph extends Applet {
    GraphPanel panel;

    public void init() {
	setLayout(new BorderLayout());

	panel = new GraphPanel(this);
	add("Center", panel);
	Panel p = new Panel();
	add("South", p);
	p.add(new Button("Scramble"));
	p.add(new Button("Shake"));
	p.add(new Checkbox("Stress"));
	p.add(new Checkbox("Random"));

	String edges = getParameter("edges");
	for (StringTokenizer t = new StringTokenizer(edges, ",") ; t.hasMoreTokens() ; ) {
	    String str = t.nextToken();
	    int i = str.indexOf('-');
	    if (i > 0) {
		int len = 50;
		int j = str.indexOf('/');
		if (j > 0) {
		    len = Integer.valueOf(str.substring(j+1)).intValue();
		    str = str.substring(0, j);
		}
		panel.addEdge(str.substring(0,i), str.substring(i+1), len);
	    }
	}
	Dimension d = size();
	String center = getParameter("center");
	if (center != null){
	    Node n = panel.nodes[panel.findNode(center)];
	    n.x = d.width / 2;
	    n.y = d.height / 2;
	    n.fixed = true;
	}
    }

    public void start() {
	panel.start();
    }
    public void stop() {
	panel.stop();
    }
    public boolean action(Event evt, Object arg) {
	if (arg instanceof Boolean) {
	    if (((Checkbox)evt.target).getLabel().equals("Stress")) {
		panel.stress = ((Boolean)arg).booleanValue();
	    } else {
		panel.random = ((Boolean)arg).booleanValue();
	    }
	    return true;
	} 
	if ("Scramble".equals(arg)) {
	    play(getCodeBase(), "audio/computer.au");
	    Dimension d = size();
	    for (int i = 0 ; i < panel.nnodes ; i++) {
		Node n = panel.nodes[i];
		if (!n.fixed) {
		    n.x = 10 + (d.width-20)*Math.random();
		    n.y = 10 + (d.height-20)*Math.random();
		}
	    }
	    return true;
	}
	if ("Shake".equals(arg)) {
	    play(getCodeBase(), "audio/gong.au");
	    Dimension d = size();
	    for (int i = 0 ; i < panel.nnodes ; i++) {
		Node n = panel.nodes[i];
		if (!n.fixed) {
		    n.x += 80*Math.random() - 40;
		    n.y += 80*Math.random() - 40;
		}
	    }
	    return true;
	}
	return false;
    }
}
