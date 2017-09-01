/*
 * @(#)GridBagLayout.java	1.5 95/11/16 Doug Stein
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

import java.util.Hashtable;
import java.util.Vector;

class GridBagLayoutInfo {
  int width, height;		/* number of cells horizontally, vertically */
  int startx, starty;		/* starting point for layout */
  int minWidth[];		/* largest minWidth in each column */
  int minHeight[];		/* largest minHeight in each row */
  double weightX[];		/* largest weight in each column */
  double weightY[];		/* largest weight in each row */

  GridBagLayoutInfo () {
    minWidth = new int[GridBagLayout.MAXGRIDSIZE];
    minHeight = new int[GridBagLayout.MAXGRIDSIZE];
    weightX = new double[GridBagLayout.MAXGRIDSIZE];
    weightY = new double[GridBagLayout.MAXGRIDSIZE];
  }
}

/**
    GridBagLayout is a flexible layout manager
    that aligns components vertically and horizontally,
    without requiring that the components be the same size.
    Each GridBagLayout uses a rectangular grid of cells,
    with each component occupying one or more cells
    (called its <em>display area</em>).
    Each component managed by a GridBagLayout 
    is associated with a
    <a href=java.awt.GridBagConstraints.html>GridBagConstraints</a> instance
    that specifies how the component is laid out
    within its display area.
    How a GridBagLayout places a set of components
    depends on each component's GridBagConstraints and minimum size,
    as well as the preferred size of the components' container.
    <p>

    To use a GridBagLayout effectively,
    you must customize one or more of its components' GridBagConstraints.
    You customize a GridBagConstraints object by setting one or more
    of its instance variables:
    <dl>
    <dt> <a href=java.awt.GridBagConstraints.html#gridx>gridx</a>,
         <a href=java.awt.GridBagConstraints.html#gridy>gridy</a>
    <dd> Specifies the cell at the upper left of the component's display area,
	 where the upper-left-most cell has address gridx=0, gridy=0.
	 Use GridBagConstraints.RELATIVE (the default value)
	 to specify that the component be just placed
	 just to the right of (for gridx)
	 or just below (for gridy)
	 the component that was added to the container
	 just before this component was added.
    <dt> <a href=java.awt.GridBagConstraints.html#gridwidth>gridwidth</a>,
         <a href=java.awt.GridBagConstraints.html#gridheight>gridheight</a>
    <dd> Specifies the number of cells in a row (for gridwidth)
	 or column (for gridheight)
	 in the component's display area.
	 The default value is 1.
	 Use GridBagConstraints.REMAINDER to specify 
	 that the component be the last one in its row (for gridwidth)
	 or column (for gridheight).
	 Use GridBagConstraints.RELATIVE to specify 
	 that the component be the next to last one
	 in its row (for gridwidth) or column (for gridheight).
    <dt> <a href=java.awt.GridBagConstraints.html#fill>fill</a>
    <dd> Used when the component's display area
   	 is larger than the component's requested size
	 to determine whether (and how) to resize the component.
	 Valid values are
	      GridBagConstraint.NONE
	      (the default),
	      GridBagConstraint.HORIZONTAL
	      (make the component wide enough to fill its display area
	      horizontally, but don't change its height),
	      GridBagConstraint.VERTICAL
	      (make the component tall enough to fill its display area
	      vertically, but don't change its width),
	      and 
	      GridBagConstraint.BOTH
	      (make the component fill its display area entirely).
    <dt> <a href=java.awt.GridBagConstraints.html#ipadx>ipadx</a>,
         <a href=java.awt.GridBagConstraints.html#ipady>ipady</a>
    <dd> Specifies the internal padding: 
	 how much to add to the minimum size of the component.
	 The width of the component will be at least
	 its minimum width plus ipadx*2 pixels
	 (since the padding applies to both sides of the component).
	 Similarly, the height of the component will be at least
	 the minimum height plus ipady*2 pixels.
    <dt> <a href=java.awt.GridBagConstraints.html#insets>insets</a>
    <dd> Specifies the external padding of the component --
	 the minimum amount of space between the component 
	 and the edges of its display area.
    <dt> <a href=java.awt.GridBagConstraints.html#anchor>anchor</a>
    <dd> Used when the component is smaller than its display area
	 to determine where (within the area) to place the component.
	 Valid values are
	 GridBagConstraints.CENTER (the default),
	 GridBagConstraints.NORTH,
	 GridBagConstraints.NORTHEAST,
	 GridBagConstraints.EAST,
	 GridBagConstraints.SOUTHEAST,
	 GridBagConstraints.SOUTH,
	 GridBagConstraints.SOUTHWEST,
	 GridBagConstraints.WEST, and
	 GridBagConstraints.NORTHWEST.
    <dt> <a href=java.awt.GridBagConstraints.html#weightx>weightx</a>,
         <a href=java.awt.GridBagConstraints.html#weighty>weighty</a>
    <dd> Used to determine how to distribute space;
	 this is important for specifying resizing behavior.
	 Unless you specify a weight
	 for at least one component in a row (weightx)
	 and column (weighty),
	 all the components clump together in the center of
	 their container.
	 This is because when the weight is zero (the default),
	 the GridBagLayout puts any extra space 
	 between its grid of cells and the edges of the container.
    </dl>

    The following figure shows ten components (all buttons)
    managed by a GridBagLayout:
    <blockquote>
    <img src=images/java.awt/GridBagEx.gif width=262 height=155>
    </blockquote>

    All the components have fill=GridBagConstraints.BOTH.
    In addition, the components have the following non-default constraints:
    <ul>
    <li>Button1, Button2, Button3:
        weightx=1.0
    <li>Button4:
        weightx=1.0,
        gridwidth=GridBagConstraints.REMAINDER
    <li>Button5:
        gridwidth=GridBagConstraints.REMAINDER
    <li>Button6:
        gridwidth=GridBagConstraints.RELATIVE
    <li>Button7:
        gridwidth=GridBagConstraints.REMAINDER
    <li>Button8:
        gridheight=2, weighty=1.0,
    <li>Button9, Button 10:
        gridwidth=GridBagConstraints.REMAINDER
    </ul>

    Here is the code that implements the example shown above:
    <blockquote>
    <pre>
import java.awt.*;
import java.util.*;
import java.applet.Applet;

public class GridBagEx1 extends Applet {

    protected void makebutton(String name,
                              GridBagLayout gridbag,
                              GridBagConstraints c) {
        Button button = new Button(name);
        gridbag.setConstraints(button, c);
        add(button);
    }

    public void init() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
 
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        setLayout(gridbag);
   
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        makebutton("Button1", gridbag, c);
        makebutton("Button2", gridbag, c);
        makebutton("Button3", gridbag, c);
    
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        makebutton("Button4", gridbag, c);
    
        c.weightx = 0.0;		   //reset to the default
        makebutton("Button5", gridbag, c); //another row
    
	c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last in row
        makebutton("Button6", gridbag, c);
    
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        makebutton("Button7", gridbag, c);
    
	c.gridwidth = 1;	   	   //reset to the default
	c.gridheight = 2;
        c.weighty = 1.0;
        makebutton("Button8", gridbag, c);
    
        c.weighty = 0.0;		   //reset to the default
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	c.gridheight = 1;		   //reset to the default
        makebutton("Button9", gridbag, c);
        makebutton("Button10", gridbag, c);
    
        resize(300, 100);
    }
    
    public static void main(String args[]) {
	Frame f = new Frame("GridBag Layout Example");
	GridBagEx1 ex1 = new GridBagEx1();
    
	ex1.init();
    
	f.add("Center", ex1);
	f.pack();
	f.resize(f.preferredSize());
	f.show();
    }
}
    </pre>
    </blockquote>
 *
 * @version 1.5, 16 Nov 1995
 * @author Doug Stein
 */
public class GridBagLayout implements LayoutManager {

  protected static final int MAXGRIDSIZE = 128;
  protected static final int MINSIZE = 1;
  protected static final int PREFERREDSIZE = 2;

  protected Hashtable comptable;
  protected GridBagConstraints defaultConstraints;
  protected GridBagLayoutInfo layoutInfo;

  public int columnWidths[];
  public int rowHeights[];
  public double columnWeights[];
  public double rowWeights[];

  /**
   * Creates a gridbag layout.
   */
  public GridBagLayout () {
    comptable = new Hashtable();
    defaultConstraints = new GridBagConstraints();
  }

  /**
   * Sets the constraints for the specified component.
   * @param comp the component to be modified
   * @param constraints the constraints to be applied
   */
  public void setConstraints(Component comp, GridBagConstraints constraints) {
    comptable.put(comp, constraints.clone());
  }

  /**
   * Retrieves the constraints for the specified component.  A copy of
   * the constraints is returned.
   * @param comp the component to be queried
   */
  public GridBagConstraints getConstraints(Component comp) {
    GridBagConstraints constraints = (GridBagConstraints)comptable.get(comp);
    if (constraints == null) {
      setConstraints(comp, defaultConstraints);
      constraints = (GridBagConstraints)comptable.get(comp);
    }
    return (GridBagConstraints)constraints.clone();
  }

  /**
   * Retrieves the constraints for the specified component.  The return
   * value is not a copy, but is the actual constraints class used by the
   * layout mechanism.
   * @param comp the component to be queried
   */
  protected GridBagConstraints lookupConstraints(Component comp) {
    GridBagConstraints constraints = (GridBagConstraints)comptable.get(comp);
    if (constraints == null) {
      setConstraints(comp, defaultConstraints);
      constraints = (GridBagConstraints)comptable.get(comp);
    }
    return constraints;
  }

  public Point getLayoutOrigin () {
    Point origin = new Point(0,0);
    if (layoutInfo != null) {
      origin.x = layoutInfo.startx;
      origin.y = layoutInfo.starty;
    }
    return origin;
  }

  public int [][] getLayoutDimensions () {
    if (layoutInfo == null)
      return new int[2][0];

    int dim[][] = new int [2][];
    dim[0] = new int[layoutInfo.width];
    dim[1] = new int[layoutInfo.height];

    System.arraycopy(layoutInfo.minWidth, 0, dim[0], 0, layoutInfo.width);
    System.arraycopy(layoutInfo.minHeight, 0, dim[1], 0, layoutInfo.height);

    return dim;
  }

  public double [][] getLayoutWeights () {
    if (layoutInfo == null)
      return new double[2][0];

    double weights[][] = new double [2][];
    weights[0] = new double[layoutInfo.width];
    weights[1] = new double[layoutInfo.height];

    System.arraycopy(layoutInfo.weightX, 0, weights[0], 0, layoutInfo.width);
    System.arraycopy(layoutInfo.weightY, 0, weights[1], 0, layoutInfo.height);

    return weights;
  }

  public Point location(int x, int y) {
    Point loc = new Point(0,0);
    int i, d;

    if (layoutInfo == null)
      return loc;

    d = layoutInfo.startx;
    for (i=0; i<layoutInfo.width; i++) {
      d += layoutInfo.minWidth[i];
      if (d > x)
	break;
    }
    loc.x = i;

    d = layoutInfo.starty;
    for (i=0; i<layoutInfo.height; i++) {
      d += layoutInfo.minHeight[i];
      if (d > y)
	break;
    }
    loc.y = i;

    return loc;
  }

  /**
   * Adds the specified component with the specified name to the layout.
   * @param name the name of the component
   * @param comp the component to be added
   */
  public void addLayoutComponent(String name, Component comp) {
  }

  /**
   * Removes the specified component from the layout. Does not apply.
   * @param comp the component to be removed
   */
  public void removeLayoutComponent(Component comp) {
  }

  /** 
   * Returns the preferred dimensions for this layout given the components
   * in the specified panel.
   * @param parent the component which needs to be laid out 
   * @see #minimumLayoutSize
   */
  public Dimension preferredLayoutSize(Container parent) {
    GridBagLayoutInfo info = GetLayoutInfo(parent, PREFERREDSIZE);
    return GetMinSize(parent, info);
  }

  /**
   * Returns the minimum dimensions needed to layout the components 
   * contained in the specified panel.
   * @param parent the component which needs to be laid out 
   * @see #preferredLayoutSize
   */
  public Dimension minimumLayoutSize(Container parent) {
    GridBagLayoutInfo info = GetLayoutInfo(parent, MINSIZE);
    return GetMinSize(parent, info);
  }

  /** 
   * Lays out the container in the specified panel.  
   * @param parent the specified component being laid out
   * @see Container
   */
  public void layoutContainer(Container parent) {
    ArrangeGrid(parent);
  }

  /**
   * Returns the String representation of this GridLayout's values.
   */
  public String toString() {
    return getClass().getName();
  }

  /**
   * Print the layout information.  Useful for debugging.
   */

  /* DEBUG
   *
   *  protected void DumpLayoutInfo(GridBagLayoutInfo s) {
   *    int x;
   *
   *    System.out.println("Col\tWidth\tWeight");
   *    for (x=0; x<s.width; x++) {
   *      System.out.println(x + "\t" +
   *			 s.minWidth[x] + "\t" +
   *			 s.weightX[x]);
   *    }
   *    System.out.println("Row\tHeight\tWeight");
   *    for (x=0; x<s.height; x++) {
   *      System.out.println(x + "\t" +
   *			 s.minHeight[x] + "\t" +
   *			 s.weightY[x]);
   *    }
   *  }
   */

  /**
   * Print the layout constraints.  Useful for debugging.
   */

  /* DEBUG
   *
   *  protected void DumpConstraints(GridBagConstraints constraints) {
   *    System.out.println(
   *		       "wt " +
   *		       constraints.weightx +
   *		       " " +
   *		       constraints.weighty +
   *		       ", " +
   *
   *		       "box " +
   *		       constraints.gridx +
   *		       " " +
   *		       constraints.gridy +
   *		       " " +
   *		       constraints.gridwidth +
   *		       " " +
   *		       constraints.gridheight +
   *		       ", " +
   *
   *		       "min " +
   *		       constraints.minWidth +
   *		       " " +
   *		       constraints.minHeight +
   *		       ", " +
   *
   *		       "pad " +
   *		       constraints.insets.bottom +
   *		       " " +
   *		       constraints.insets.left +
   *		       " " +
   *		       constraints.insets.right +
   *		       " " +
   *		       constraints.insets.top +
   *		       " " +
   *		       constraints.ipadx +
   *		       " " +
   *		       constraints.ipady);
   *  }
   */

  /*
   * Fill in an instance of the above structure for the current set
   * of managed children.  This requires three passes through the
   * set of children:
   *
   * 1) Figure out the dimensions of the layout grid
   * 2) Determine which cells the components occupy
   * 3) Distribute the weights and min sizes amoung the rows/columns.
   *
   * This also caches the minsizes for all the children when they are
   * first encountered (so subsequent loops don't need to ask again).
   */
  
  protected GridBagLayoutInfo GetLayoutInfo(Container parent, int sizeflag) {
    GridBagLayoutInfo r = new GridBagLayoutInfo();
    Component comp;
    GridBagConstraints constraints;
    Dimension d;
    Component components[] = parent.getComponents();

    int compindex, i, j, k, px, py, pixels_diff, nextSize;
    int curX, curY, curWidth, curHeight, curRow, curCol;
    double weight_diff, weight, start, size;
    int xMax[], yMax[];

    /*
     * Pass #1
     *
     * Figure out the dimensions of the layout grid (use a value of 1 for
     * zero or negative widths and heights).
     */
    
    r.width = r.height = 0;
    curRow = curCol = -1;
    xMax = new int[MAXGRIDSIZE];
    yMax = new int[MAXGRIDSIZE];

    for (compindex = 0 ; compindex < components.length ; compindex++) {
      comp = components[compindex];
      if (!comp.isVisible())
	continue;
      constraints = lookupConstraints(comp);
      
      curX = constraints.gridx;
      curY = constraints.gridy;
      curWidth = constraints.gridwidth;
      if (curWidth <= 0)
	curWidth = 1;
      curHeight = constraints.gridheight;
      if (curHeight <= 0)
	curHeight = 1;
      
      /* If x or y is negative, then use relative positioning: */
      if (curX < 0 && curY < 0) {
	if (curRow >= 0)
	  curY = curRow;
	else if (curCol >= 0)
	  curX = curCol;
	else
	  curY = 0;
      }
      if (curX < 0) {
	px = 0;
	for (i = curY; i < (curY + curHeight); i++)
	  px = Math.max(px, xMax[i]);
	
	curX = px - curX - 1;
	if(curX < 0)
	  curX = 0;
      }
      else if (curY < 0) {
	py = 0;
	for (i = curX; i < (curX + curWidth); i++)
	  py = Math.max(py, yMax[i]);
	
	curY = py - curY - 1;
	if(curY < 0)
	  curY = 0;
      }
      
      /* Adjust the grid width and height */
      for (px = curX + curWidth; r.width < px; r.width++);
      for (py = curY + curHeight; r.height < py; r.height++);
      
      /* Adjust the xMax and yMax arrays */
      for (i = curX; i < (curX + curWidth); i++) { yMax[i] = py; }
      for (i = curY; i < (curY + curHeight); i++) { xMax[i] = px; }
      
      /* Cache the current slave's size. */
      if (sizeflag == PREFERREDSIZE)
	d = comp.preferredSize();
      else
	d = comp.minimumSize();
      constraints.minWidth = d.width;
      constraints.minHeight = d.height;
      
      /* Zero width and height must mean that this is the last item (or
       * else something is wrong). */
      if (constraints.gridheight == 0 && constraints.gridwidth == 0)
	curRow = curCol = -1;
      
      /* Zero width starts a new row */
      if (constraints.gridheight == 0 && curRow < 0)
	curCol = curX + curWidth;
      
      /* Zero height starts a new column */
      else if (constraints.gridwidth == 0 && curCol < 0)
	curRow = curY + curHeight;
    }
    
    /*
     * Apply minimum row/column dimensions
     */
    if (columnWidths != null && r.width < columnWidths.length)
      r.width = columnWidths.length;
    if (rowHeights != null && r.height < rowHeights.length)
      r.height = rowHeights.length;

    /*
     * Pass #2
     *
     * Negative values for gridX are filled in with the current x value.
     * Negative values for gridY are filled in with the current y value.
     * Negative or zero values for gridWidth and gridHeight end the current
     *  row or column, respectively.
     */
    
    curRow = curCol = -1;
    xMax = new int[MAXGRIDSIZE];
    yMax = new int[MAXGRIDSIZE];
    
    for (compindex = 0 ; compindex < components.length ; compindex++) {
      comp = components[compindex];
      if (!comp.isVisible())
	continue;
      constraints = lookupConstraints(comp);
      
      curX = constraints.gridx;
      curY = constraints.gridy;
      curWidth = constraints.gridwidth;
      curHeight = constraints.gridheight;
      
      /* If x or y is negative, then use relative positioning: */
      if (curX < 0 && curY < 0) {
	if(curRow >= 0)
	  curY = curRow;
	else if(curCol >= 0)
	  curX = curCol;
	else
	  curY = 0;
      }
      
      if (curX < 0) {
	if (curHeight <= 0) {
	  curHeight += r.height - curY;
	  if (curHeight < 1)
	    curHeight = 1;
	}
	
	px = 0;
	for (i = curY; i < (curY + curHeight); i++)
	  px = Math.max(px, xMax[i]);
	
	curX = px - curX - 1;
	if(curX < 0)
	  curX = 0;
      }
      else if (curY < 0) {
	if (curWidth <= 0) {
	  curWidth += r.width - curX;
	  if (curWidth < 1)
	    curWidth = 1;
	}
	
	py = 0;
	for (i = curX; i < (curX + curWidth); i++)
	  py = Math.max(py, yMax[i]);
	
	curY = py - curY - 1;
	if(curY < 0)
	  curY = 0;
      }
      
      if (curWidth <= 0) {
	curWidth += r.width - curX;
	if (curWidth < 1)
	  curWidth = 1;
      }
      
      if (curHeight <= 0) {
	curHeight += r.height - curY;
	if (curHeight < 1)
	  curHeight = 1;
      }
      
      px = curX + curWidth;
      py = curY + curHeight;
      
      for (i = curX; i < (curX + curWidth); i++) { yMax[i] = py; }
      for (i = curY; i < (curY + curHeight); i++) { xMax[i] = px; }
      
      /* Make negative sizes start a new row/column */
      if (constraints.gridheight == 0 && constraints.gridwidth == 0)
	curRow = curCol = -1;
      if (constraints.gridheight == 0 && curRow < 0)
	curCol = curX + curWidth;
      else if (constraints.gridwidth == 0 && curCol < 0)
        curRow = curY + curHeight;
      
      /* Assign the new values to the gridbag slave */
      constraints.tempX = curX;
      constraints.tempY = curY;
      constraints.tempWidth = curWidth;
      constraints.tempHeight = curHeight;
    }
    
    /*
     * Apply minimum row/column dimensions and weights
     */
    if (columnWidths != null)
      System.arraycopy(columnWidths, 0, r.minWidth, 0, columnWidths.length);
    if (rowHeights != null)
      System.arraycopy(rowHeights, 0, r.minHeight, 0, rowHeights.length);
    if (columnWeights != null)
      System.arraycopy(columnWeights, 0, r.weightX, 0, columnWeights.length);
    if (rowWeights != null)
      System.arraycopy(rowWeights, 0, r.weightY, 0, rowWeights.length);

    /*
     * Pass #3
     *
     * Distribute the minimun widths and weights:
     */
    
    nextSize = Integer.MAX_VALUE;
    
    for (i = 1;
	 i != Integer.MAX_VALUE;
	 i = nextSize, nextSize = Integer.MAX_VALUE) {
      for (compindex = 0 ; compindex < components.length ; compindex++) {
	comp = components[compindex];
	if (!comp.isVisible())
	  continue;
	constraints = lookupConstraints(comp);
      
	if (constraints.tempWidth == i) {
	  px = constraints.tempX + constraints.tempWidth; /* right column */
	  
	  /* 
	   * Figure out if we should use this slave\'s weight.  If the weight
	   * is less than the total weight spanned by the width of the cell,
	   * then discard the weight.  Otherwise split the difference
	   * according to the existing weights.
	   */
	  
	  weight_diff = constraints.weightx;
	  for (k = constraints.tempX; k < px; k++)
	    weight_diff -= r.weightX[k];
	  if (weight_diff > 0.0) {
	    weight = 0.0;
	    for (k = constraints.tempX; k < px; k++)
	      weight += r.weightX[k];
	    for (k = constraints.tempX; weight > 0.0 && k < px; k++) {
	      double wt = r.weightX[k];
	      double dx = (wt * weight_diff) / weight;
	      r.weightX[k] += dx;
	      weight_diff -= dx;
	      weight -= wt;
	    }
	    /* Assign the remainder to the rightmost cell */
	    r.weightX[px-1] += weight_diff;
	  }
	  
	  /*
	   * Calculate the minWidth array values.
	   * First, figure out how wide the current slave needs to be.
	   * Then, see if it will fit within the current minWidth values.
	   * If it will not fit, add the difference according to the
	   * weightX array.
	   */
	  
	  pixels_diff =
	    constraints.minWidth + constraints.ipadx +
	    constraints.insets.left + constraints.insets.right;

	  for (k = constraints.tempX; k < px; k++)
	    pixels_diff -= r.minWidth[k];
	  if (pixels_diff > 0) {
	    weight = 0.0;
	    for (k = constraints.tempX; k < px; k++)
	      weight += r.weightX[k];
	    for (k = constraints.tempX; weight > 0.0 && k < px; k++) {
	      double wt = r.weightX[k];
	      int dx = (int)((wt * ((double)pixels_diff)) / weight);
	      r.minWidth[k] += dx;
	      pixels_diff -= dx;
	      weight -= wt;
	    }
	    /* Any leftovers go into the rightmost cell */
	    r.minWidth[px-1] += pixels_diff;
	  }
	}
	else if (constraints.tempWidth > i && constraints.tempWidth < nextSize)
	  nextSize = constraints.tempWidth;
	
	
	if (constraints.tempHeight == i) {
	  py = constraints.tempY + constraints.tempHeight; /* bottom row */
	  
	  /* 
	   * Figure out if we should use this slave\'s weight.  If the weight
	   * is less than the total weight spanned by the height of the cell,
	   * then discard the weight.  Otherwise split it the difference
	   * according to the existing weights.
	   */
	  
	  weight_diff = constraints.weighty;
	  for (k = constraints.tempY; k < py; k++)
	    weight_diff -= r.weightY[k];
	  if (weight_diff > 0.0) {
	    weight = 0.0;
	    for (k = constraints.tempY; k < py; k++)
	      weight += r.weightY[k];
	    for (k = constraints.tempY; weight > 0.0 && k < py; k++) {
	      double wt = r.weightY[k];
	      double dy = (wt * weight_diff) / weight;
	      r.weightY[k] += dy;
	      weight_diff -= dy;
	      weight -= wt;
	    }
	    /* Assign the remainder to the bottom cell */
	    r.weightY[py-1] += weight_diff;
	  }
	  
	  /*
	   * Calculate the minHeight array values.
	   * First, figure out how tall the current slave needs to be.
	   * Then, see if it will fit within the current minHeight values.
	   * If it will not fit, add the difference according to the
	   * weightY array.
	   */
	  
	  pixels_diff =
	    constraints.minHeight + constraints.ipady +
	    constraints.insets.top + constraints.insets.bottom;
	  for (k = constraints.tempY; k < py; k++)
	    pixels_diff -= r.minHeight[k];
	  if (pixels_diff > 0) {
	    weight = 0.0;
	    for (k = constraints.tempY; k < py; k++)
	      weight += r.weightY[k];
	    for (k = constraints.tempY; weight > 0.0 && k < py; k++) {
	      double wt = r.weightY[k];
	      int dy = (int)((wt * ((double)pixels_diff)) / weight);
	      r.minHeight[k] += dy;
	      pixels_diff -= dy;
	      weight -= wt;
	    }
	    /* Any leftovers go into the bottom cell */
	    r.minHeight[py-1] += pixels_diff;
	  }
	}
	else if (constraints.tempHeight > i &&
		 constraints.tempHeight < nextSize)
	  nextSize = constraints.tempHeight;
      }
    }

    return r;
  }
  
  /*
   * Adjusts the x, y, width, and height fields to the correct
   * values depending on the constraint geometry and pads.
   */
  protected void AdjustForGravity(GridBagConstraints constraints,
				  Rectangle r) {
    int diffx, diffy;

    r.x += constraints.insets.left;
    r.width -= (constraints.insets.left + constraints.insets.right);
    r.y += constraints.insets.top;
    r.height -= (constraints.insets.top + constraints.insets.bottom);
    
    diffx = 0;
    if ((constraints.fill != GridBagConstraints.HORIZONTAL &&
	 constraints.fill != GridBagConstraints.BOTH)
	&& (r.width > (constraints.minWidth + constraints.ipadx))) {
      diffx = r.width - (constraints.minWidth + constraints.ipadx);
      r.width = constraints.minWidth + constraints.ipadx;
    }
    
    diffy = 0;
    if ((constraints.fill != GridBagConstraints.VERTICAL &&
	 constraints.fill != GridBagConstraints.BOTH)
	&& (r.height > (constraints.minHeight + constraints.ipady))) {
      diffy = r.height - (constraints.minHeight + constraints.ipady);
      r.height = constraints.minHeight + constraints.ipady;
    }
    
    switch (constraints.anchor) {
    case GridBagConstraints.CENTER:
      r.x += diffx/2;
      r.y += diffy/2;
      break;
    case GridBagConstraints.NORTH:
      r.x += diffx/2;
      break;
    case GridBagConstraints.NORTHEAST:
      r.x += diffx;
      break;
    case GridBagConstraints.EAST:
      r.x += diffx;
      r.y += diffy/2;
      break;
    case GridBagConstraints.SOUTHEAST:
      r.x += diffx;
      r.y += diffy;
      break;
    case GridBagConstraints.SOUTH:
      r.x += diffx/2;
      r.y += diffy;
      break;
    case GridBagConstraints.SOUTHWEST:
      r.y += diffy;
      break;
    case GridBagConstraints.WEST:
      r.y += diffy/2;
      break;
    case GridBagConstraints.NORTHWEST:
      break;
    default:
      throw new IllegalArgumentException("illegal anchor value");
    }
  }

  /*
   * Figure out the minimum size of the
   * master based on the information from GetLayoutInfo()
   */
  protected Dimension GetMinSize(Container parent, GridBagLayoutInfo info) {
    Dimension d = new Dimension();
    int i, t;
    Insets insets = parent.insets();

    t = 0;
    for(i = 0; i < info.width; i++)
      t += info.minWidth[i];
    d.width = t + insets.left + insets.right;

    t = 0;
    for(i = 0; i < info.height; i++)
      t += info.minHeight[i];
    d.height = t + insets.top + insets.bottom;

    return d;
  }

  /*
   * Lay out the grid
   */
  protected void ArrangeGrid(Container parent) {
    Component comp;
    int compindex;
    GridBagConstraints constraints;
    Insets insets = parent.insets();
    Component components[] = parent.getComponents();
    Dimension d;
    Rectangle r = new Rectangle();
    int i, diffw, diffh;
    double weight;
    GridBagLayoutInfo info;
    
    /*
     * If the parent has no slaves anymore, then don't do anything
     * at all:  just leave the parent's size as-is.
     */
    if (components.length == 0 &&
	(columnWidths == null || columnWidths.length == 0) &&
	(rowHeights == null || rowHeights.length == 0)) {
      return;
    }
    
    /*
     * Pass #1: scan all the slaves to figure out the total amount
     * of space needed.
     */
    
    info = GetLayoutInfo(parent, PREFERREDSIZE);
    d = GetMinSize(parent, info);

    if (d.width < parent.width || d.height < parent.height) {
      info = GetLayoutInfo(parent, MINSIZE);
      d = GetMinSize(parent, info);
    }

    layoutInfo = info;
    r.width = d.width;
    r.height = d.height;

    /*
     * DEBUG
     *
     * DumpLayoutInfo(info);
     * for (compindex = 0 ; compindex < components.length ; compindex++) {
     * comp = components[compindex];
     * if (!comp.isVisible())
     *	continue;
     * constraints = lookupConstraints(comp);
     * DumpConstraints(constraints);
     * }
     * System.out.println("minSize " + r.width + " " + r.height);
     */
    
    /*
     * If the current dimensions of the window don't match the desired
     * dimensions, then adjust the minWidth and minHeight arrays
     * according to the weights.
     */
    
    diffw = parent.width - r.width;
    if (diffw != 0) {
      weight = 0.0;
      for (i = 0; i < info.width; i++)
	weight += info.weightX[i];
      if (weight > 0.0) {
	for (i = 0; i < info.width; i++) {
	  int dx = (int)(( ((double)diffw) * info.weightX[i]) / weight);
	  info.minWidth[i] += dx;
	  r.width += dx;
	  if (info.minWidth[i] < 0) {
	    r.width -= info.minWidth[i];
	    info.minWidth[i] = 0;
	  }
	}
      }
      diffw = parent.width - r.width;
    }
    else {
      diffw = 0;
    }
    
    diffh = parent.height - r.height;
    if (diffh != 0) {
      weight = 0.0;
      for (i = 0; i < info.height; i++)
	weight += info.weightY[i];
      if (weight > 0.0) {
	for (i = 0; i < info.height; i++) {
	  int dy = (int)(( ((double)diffh) * info.weightY[i]) / weight);
	  info.minHeight[i] += dy;
	  r.height += dy;
	  if (info.minHeight[i] < 0) {
	    r.height -= info.minHeight[i];
	    info.minHeight[i] = 0;
	  }
	}
      }
      diffh = parent.height - r.height;
    }
    else {
      diffh = 0;
    }

    /*
     * DEBUG
     *
     * System.out.println("Re-adjusted:");
     * DumpLayoutInfo(info);
     */
    
    /*
     * Now do the actual layout of the slaves using the layout information
     * that has been collected.
     */
    
    info.startx = diffw/2 + insets.left;
    info.starty = diffh/2 + insets.top;

    for (compindex = 0 ; compindex < components.length ; compindex++) {
      comp = components[compindex];
      if (!comp.isVisible())
	continue;
      constraints = lookupConstraints(comp);

      r.x = info.startx;
      for(i = 0; i < constraints.tempX; i++)
	r.x += info.minWidth[i];
      
      r.y = info.starty;
      for(i = 0; i < constraints.tempY; i++)
	r.y += info.minHeight[i];
      
      r.width = 0;
      for(i = constraints.tempX;
	  i < (constraints.tempX + constraints.tempWidth);
	  i++) {
	r.width += info.minWidth[i];
      }
      
      r.height = 0;
      for(i = constraints.tempY;
	  i < (constraints.tempY + constraints.tempHeight);
	  i++) {
	r.height += info.minHeight[i];
      }
      
      AdjustForGravity(constraints, r);
      
      /*
       * If the window is too small to be interesting then
       * unmap it.  Otherwise configure it and then make sure
       * it's mapped.
       */
      
      if ((r.width <= 0) || (r.height <= 0)) {
	comp.reshape(0, 0, 0, 0);
      }
      else {
	if (comp.x != r.x || comp.y != r.y ||
	    comp.width != r.width || comp.height != r.height) {
	  comp.reshape(r.x, r.y, r.width, r.height);
	}
      }
    }
  }
}
