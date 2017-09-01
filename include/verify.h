/*
 * @(#)verify.h	1.8 95/01/31 Arthur van Hoff
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

#ifndef _VERIFY_
#define _VERIFY_

/*
 * Limits. I have just picked some values for these, they
 * probably should be more constraint.
 */

#define DEFAULTMAX	(1 << 15)

#define MINCLASSSIZE	sizeof(ClassClass)
#define MAXCLASSSIZE	DEFAULTMAX
#define MINCONSTANTS	2
#define MAXCONSTANTS	DEFAULTMAX
#define MININTERFACES	0
#define MAXINTERFACES	DEFAULTMAX
#define MINFIELDS	0
#define MAXFIELDS	DEFAULTMAX
#define MINMETHODS	0
#define MAXMETHODS	DEFAULTMAX
#define MINATTR		0
#define MAXATTR		DEFAULTMAX
#define MINATTRLEN	0
#define MAXATTRLEN	DEFAULTMAX
#define MINCODELEN	1
#define MAXCODELEN	DEFAULTMAX
#define MINEXCEPTIONS	0
#define MAXEXCEPTIONS	DEFAULTMAX
#define MINLINENO	0
#define MAXLINENO	DEFAULTMAX
#define MINLOCALVAR	0
#define MAXLOCALVAR	256
#define MINSTRING	0
#define MAXSTRING	DEFAULTMAX
#define MINSTACK	0
#define MAXSTACK	DEFAULTMAX
#define MINLOCALS	0
#define MAXLOCALS	256
#define MINMETHSIZE	1
#define MAXMETHSIZE	DEFAULTMAX
#define MINARGSIZE	0
#define MAXARGSIZE	256

typedef unsigned char 	uchar;

/*
 * data
 */
extern int verify_verbose;
extern int verify_errors;
extern char verify_error_message[];
extern char *opnames[];

/*
 * functions
 */

extern long get_ubyte(uchar *ptr);
extern long get_ushort(uchar *ptr);
extern long get_uint(uchar *ptr);
extern long get_byte(uchar *ptr);
extern long get_short(uchar *ptr);
extern long get_int(uchar *ptr);

extern long read_ubyte(uchar **pptr);
extern long read_ushort(uchar **pptr);
extern long read_uint(uchar **pptr);
extern long read_byte(uchar **pptr);
extern long read_short(uchar **pptr);
extern long read_int(uchar **pptr);

extern int verify_field_signature(char *sig, int len);
extern int verify_method_signature(char *sig, int len);

extern int verify_Asciz(int nconstants, uchar **cpool, uint n);
extern int verify_Class(int nconstants, uchar **cpool, uint n);
extern int verify_NameAndType(int nconstants, uchar **cpool, uint n);
extern int verify_Fieldref(int nconstants, uchar **cpool, uint n);
extern int verify_Methodref(int nconstants, uchar **cpool, uint n);
extern int verify_InterfaceMethodref(int nconstants, uchar **cpool, uint n);

extern void verify_error(char *fmt, ...);
extern int verify_data(uchar *data, int length);
extern int verify_code(int nconst, uchar **cpool, int name, int sig, uchar *data, int length);

#endif
