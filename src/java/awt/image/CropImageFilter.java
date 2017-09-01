/*
 * @(#)CropImageFilter.java	1.4 96/03/21 Jim Graham
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
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

package java.awt.image;

import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.awt.Rectangle;

/**
 * An ImageFilter class for cropping images.
 * This class extends the basic ImageFilter Class to extract a given
 * rectangular region of an existing Image and provide a source for a
 * new image containing just the extracted region.  It is meant to
 * be used in conjunction with a FilteredImageSource object to produce
 * cropped versions of existing images.
 *
 * @see FilteredImageSource
 * @see ImageFilter
 *
 * @version	1.4 21 Mar 1996
 * @author 	Jim Graham
 */
public class CropImageFilter extends ImageFilter {
    int cropX;
    int cropY;
    int cropW;
    int cropH;
    
    /**
     * Constructs a CropImageFilter that extracts the absolute rectangular
     * region of pixels from its source Image as specified by the x, y,
     * w, and h parameters.
     * @param x the x location of the top of the rectangle to be extracted
     * @param y the y location of the top of the rectangle to be extracted
     * @param w the width of the rectangle to be extracted
     * @param h the height of the rectangle to be extracted
     */
    public CropImageFilter(int x, int y, int w, int h) {
	cropX = x;
	cropY = y;
	cropW = w;
	cropH = h;
    }

    /**
     * Passes along  the properties from the source object after adding a
     * property indicating the cropped region.
     */
    public void setProperties(Hashtable props) {
	props = (Hashtable) props.clone();
	props.put("croprect", new Rectangle(cropX, cropY, cropW, cropH));
	super.setProperties(props);
    }

    /**
     * Override the source image's dimensions and pass the dimensions
     * of the rectangular cropped region to the ImageConsumer.
     * @see ImageConsumer
     */
    public void setDimensions(int w, int h) {
	consumer.setDimensions(cropW, cropH);
    }
   
    /**
     * Determine whether the delivered byte pixels intersect the region to
     * be extracted and passes through only that subset of pixels that
     * appear in the output region.
     */
    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, byte pixels[], int off,
			  int scansize) {
	int x1 = x;
	if (x1 < cropX) {
	    x1 = cropX;
	}
	int x2 = x + w;
	if (x2 > cropX + cropW) {
	    x2 = cropX + cropW;
	}
	int y1 = y;
	if (y1 < cropY) {
	    y1 = cropY;
	}
	int y2 = y + h;
	if (y2 > cropY + cropH) {
	    y2 = cropY + cropH;
	}
	if (x1 >= x2 || y1 >= y2) {
	    return;
	}
	consumer.setPixels(x1 - cropX, y1 - cropY, (x2 - x1), (y2 - y1),
			   model, pixels,
			   off + (y1 - y) * scansize + (x1 - x), scansize);
    }
    
    /**
     * Determine if the delivered int pixels intersect the region to
     * be extracted and pass through only that subset of pixels that
     * appear in the output region.
     */
    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, int pixels[], int off,
			  int scansize) {
	int x1 = x;
	if (x1 < cropX) {
	    x1 = cropX;
	}
	int x2 = x + w;
	if (x2 > cropX + cropW) {
	    x2 = cropX + cropW;
	}
	int y1 = y;
	if (y1 < cropY) {
	    y1 = cropY;
	}
	int y2 = y + h;
	if (y2 > cropY + cropH) {
	    y2 = cropY + cropH;
	}
	if (x1 >= x2 || y1 >= y2) {
	    return;
	}
	consumer.setPixels(x1 - cropX, y1 - cropY, (x2 - x1), (y2 - y1),
			   model, pixels,
			   off + (y1 - y) * scansize + (x1 - x), scansize);
    }
}
