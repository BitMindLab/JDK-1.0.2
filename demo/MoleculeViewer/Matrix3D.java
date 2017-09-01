/*
 *@(#)Matrix3D.java
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

/** A fairly conventional 3D matrix object that can transform sets of
    3D points and perform a variety of manipulations on the transform */
class Matrix3D {
    float xx, xy, xz, xo;
    float yx, yy, yz, yo;
    float zx, zy, zz, zo;
    static final double pi = 3.14159265;
    /** Create a new unit matrix */
    Matrix3D () {
	xx = 1.0f;
	yy = 1.0f;
	zz = 1.0f;
    }
    /** Scale by f in all dimensions */
    void scale(float f) {
	xx *= f;
	xy *= f;
	xz *= f;
	xo *= f;
	yx *= f;
	yy *= f;
	yz *= f;
	yo *= f;
	zx *= f;
	zy *= f;
	zz *= f;
	zo *= f;
    }
    /** Scale along each axis independently */
    void scale(float xf, float yf, float zf) {
	xx *= xf;
	xy *= xf;
	xz *= xf;
	xo *= xf;
	yx *= yf;
	yy *= yf;
	yz *= yf;
	yo *= yf;
	zx *= zf;
	zy *= zf;
	zz *= zf;
	zo *= zf;
    }
    /** Translate the origin */
    void translate(float x, float y, float z) {
	xo += x;
	yo += y;
	zo += z;
    }
    /** rotate theta degrees about the y axis */
    void yrot(double theta) {
	theta *= (pi / 180);
	double ct = Math.cos(theta);
	double st = Math.sin(theta);

	float Nxx = (float) (xx * ct + zx * st);
	float Nxy = (float) (xy * ct + zy * st);
	float Nxz = (float) (xz * ct + zz * st);
	float Nxo = (float) (xo * ct + zo * st);

	float Nzx = (float) (zx * ct - xx * st);
	float Nzy = (float) (zy * ct - xy * st);
	float Nzz = (float) (zz * ct - xz * st);
	float Nzo = (float) (zo * ct - xo * st);

	xo = Nxo;
	xx = Nxx;
	xy = Nxy;
	xz = Nxz;
	zo = Nzo;
	zx = Nzx;
	zy = Nzy;
	zz = Nzz;
    }
    /** rotate theta degrees about the x axis */
    void xrot(double theta) {
	theta *= (pi / 180);
	double ct = Math.cos(theta);
	double st = Math.sin(theta);

	float Nyx = (float) (yx * ct + zx * st);
	float Nyy = (float) (yy * ct + zy * st);
	float Nyz = (float) (yz * ct + zz * st);
	float Nyo = (float) (yo * ct + zo * st);

	float Nzx = (float) (zx * ct - yx * st);
	float Nzy = (float) (zy * ct - yy * st);
	float Nzz = (float) (zz * ct - yz * st);
	float Nzo = (float) (zo * ct - yo * st);

	yo = Nyo;
	yx = Nyx;
	yy = Nyy;
	yz = Nyz;
	zo = Nzo;
	zx = Nzx;
	zy = Nzy;
	zz = Nzz;
    }
    /** rotate theta degrees about the z axis */
    void zrot(double theta) {
	theta *= (pi / 180);
	double ct = Math.cos(theta);
	double st = Math.sin(theta);

	float Nyx = (float) (yx * ct + xx * st);
	float Nyy = (float) (yy * ct + xy * st);
	float Nyz = (float) (yz * ct + xz * st);
	float Nyo = (float) (yo * ct + xo * st);

	float Nxx = (float) (xx * ct - yx * st);
	float Nxy = (float) (xy * ct - yy * st);
	float Nxz = (float) (xz * ct - yz * st);
	float Nxo = (float) (xo * ct - yo * st);

	yo = Nyo;
	yx = Nyx;
	yy = Nyy;
	yz = Nyz;
	xo = Nxo;
	xx = Nxx;
	xy = Nxy;
	xz = Nxz;
    }
    /** Multiply this matrix by a second: M = M*R */
    void mult(Matrix3D rhs) {
	float lxx = xx * rhs.xx + yx * rhs.xy + zx * rhs.xz;
	float lxy = xy * rhs.xx + yy * rhs.xy + zy * rhs.xz;
	float lxz = xz * rhs.xx + yz * rhs.xy + zz * rhs.xz;
	float lxo = xo * rhs.xx + yo * rhs.xy + zo * rhs.xz + rhs.xo;

	float lyx = xx * rhs.yx + yx * rhs.yy + zx * rhs.yz;
	float lyy = xy * rhs.yx + yy * rhs.yy + zy * rhs.yz;
	float lyz = xz * rhs.yx + yz * rhs.yy + zz * rhs.yz;
	float lyo = xo * rhs.yx + yo * rhs.yy + zo * rhs.yz + rhs.yo;

	float lzx = xx * rhs.zx + yx * rhs.zy + zx * rhs.zz;
	float lzy = xy * rhs.zx + yy * rhs.zy + zy * rhs.zz;
	float lzz = xz * rhs.zx + yz * rhs.zy + zz * rhs.zz;
	float lzo = xo * rhs.zx + yo * rhs.zy + zo * rhs.zz + rhs.zo;

	xx = lxx;
	xy = lxy;
	xz = lxz;
	xo = lxo;

	yx = lyx;
	yy = lyy;
	yz = lyz;
	yo = lyo;

	zx = lzx;
	zy = lzy;
	zz = lzz;
	zo = lzo;
    }

    /** Reinitialize to the unit matrix */
    void unit() {
	xo = 0;
	xx = 1;
	xy = 0;
	xz = 0;
	yo = 0;
	yx = 0;
	yy = 1;
	yz = 0;
	zo = 0;
	zx = 0;
	zy = 0;
	zz = 1;
    }
    /** Transform nvert points from v into tv.  v contains the input
        coordinates in floating point.  Three successive entries in
	the array constitute a point.  tv ends up holding the transformed
	points as integers; three successive entries per point */
    void transform(float v[], int tv[], int nvert) {
	float lxx = xx, lxy = xy, lxz = xz, lxo = xo;
	float lyx = yx, lyy = yy, lyz = yz, lyo = yo;
	float lzx = zx, lzy = zy, lzz = zz, lzo = zo;
	for (int i = nvert * 3; (i -= 3) >= 0;) {
	    float x = v[i];
	    float y = v[i + 1];
	    float z = v[i + 2];
	    tv[i    ] = (int) (x * lxx + y * lxy + z * lxz + lxo);
	    tv[i + 1] = (int) (x * lyx + y * lyy + z * lyz + lyo);
	    tv[i + 2] = (int) (x * lzx + y * lzy + z * lzz + lzo);
	}
    }
    public String toString() {
	return ("[" + xo + "," + xx + "," + xy + "," + xz + ";"
		+ yo + "," + yx + "," + yy + "," + yz + ";"
		+ zo + "," + zx + "," + zy + "," + zz + "]");
    }
}
