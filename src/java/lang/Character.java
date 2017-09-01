/*
 * W% 96/02/05  
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

package java.lang;

/**
 * The Character class provides an object wrapper for Character data values
 * and serves as a place for character-oriented operations.  A wrapper is useful
 * because most of Java's utility classes require the use of objects.  Since characters
 * are not objects in Java, they need to be "wrapped" in a Character instance.
 * @version 	1.30, 05 Feb 1996
 * @author	Lee Boynton, Guy Steele
 */

public final
class Character extends Object {
    /**
     * The minimum radix available for conversion to and from Strings.  
     * The minimum value that a radix can be is 2.
     * @see Integer#toString
     */
    public static final int MIN_RADIX = 2;

    /**
     * The maximum radix available for conversion to and from Strings.  The
     * maximum value that a radix can be is 36.
     * @see Integer#toString
     */
    public static final int MAX_RADIX = 36;

    /**
     * The minimum value a Character can have.  The lowest minimum value a
     * Character can have is &#92;u0000.
     */
    public static final char   MIN_VALUE = '\u0000';

    /**
     * The maximum value a Character can have.  The greatest maximum value a
     * Character can have is &#92;uffff.
     */
    public static final char   MAX_VALUE = '\uffff';
    

    private static long isLowerCaseTable[] = {
	0x0000000000000000L, 0x07FFFFFE00000000L, 0x0000000000000000L, 0xFF7FFFFF80000000L, // 00xx
	0x55AAAAAAAAAAAAAAL, 0xD4AAAAAAAAAAAB55L, 0x2651292A4E243129L, 0xA829AAAAB5555240L, // 01xx
	0x0000000000AAAAAAL, 0xFFAFFBFBFFFF0000L, 0x000001F9640F7FFCL, 0x0000000000000000L, // 02xx
	0x0000000000000000L, 0x0000000000000000L, 0xFFFFF00000010000L, 0x0003AAA800637FFFL, // 03xx
	0xFFFF000000000000L, 0xAAAAAAAADFFEFFFFL, 0xAAAAAAAAAAAA0002L, 0x022A8AAAAAAA1114L, // 04xx
	0x0000000000000000L, 0xFFFFFFFE00000000L, 0x00000000000000FFL, 0x0000000000000000L, // 05xx
	0xAAAAAAAAAAAAAAAAL, 0xAAAAAAAAAAAAAAAAL, 0xAAAAAAAA07EAAAAAL, 0x02AAAAAAAAAAAAAAL, // 1Exx
	0x00FF00FF003F00FFL, 0x3FFF00FF00FF003FL, 0x00DF00FF00FF00FFL, 0x00DC00FF00CF00DCL, // 1Fxx
    };

    /**
     * Determines whether the specified character is a lowercase character.
     * 
     * <p> A character is considered to be lowercase if and only if all
     * of the following are true:
     * <ul>
     * <li>  The character is not in the range 0x2000 through 0x2FFF.
     * <li>  The Unicode 1.1.5 attribute table does not specify a
     *       mapping to lowercase for the character.
     * <li>  At least one of the following is true:
     *       <ul>
     *       <li>  The Unicode 1.1.5 attribute table does specify a
     *             mapping to uppercase for the character.
     *       <li>  The Unicode 1.1.5 name for the character contains
     *             the words "SMALL LETTER".
     *       <li>  The Unicode 1.1.5 name for the character contains
     *             the words "SMALL LIGATURE".
     *       </ul>
     * </ul>
     * Of the ISO-LATIN-1 characters (character codes 0x0000 through 0x00FF),
     * the following are lowercase:
     * a b c d e f g h i j k l m n o p q r s t u v w x y z
     * \u00DF \u00E0 \u00E1 \u00E2 \u00E3 \u00E4 \u00E5 \u00E6 \u00E7
     * \u00E8 \u00E9 \u00EA \u00EB \u00EC \u00ED \u00EE \u00EF \u00F0
     * \u00F1 \u00F2 \u00F3 \u00F4 \u00F5 \u00F6 \u00F8 \u00F9 \u00FA
     * \u00FB \u00FC \u00FD \u00FE \u00FF
     *
     * @param ch	the character to be tested
     * @return 	true if the character is lowercase; false otherwise.
     *
     * @see java.lang.Character#isUpperCase
     * @see java.lang.Character#isTitleCase
     * @see java.lang.Character#toLowerCase
     */

    public static boolean isLowerCase(char ch) {
	return ((((ch < 0x0600) ? isLowerCaseTable[ch >> 6] :
		  ((ch & 0xFE00) == 0x1E00) ? isLowerCaseTable[(ch - (0x1E00 - 0x0600)) >> 6] :
		  ((ch & 0xFFC0) == 0xFB00) ? 0x0000000000F8007FL :
		  ((ch & 0xFFC0) == 0xFF40) ? 0x0000000007FFFFFEL : 0) >> (ch & 0x3F)) & 0x1L) != 0;
    }

    private static long isUpperCaseTable[] = {
	0x0000000000000000L, 0x0000000007FFFFFEL, 0x0000000000000000L, 0x000000007F7FFFFFL, // 00xx
	0xAA55555555555555L, 0x2B555555555554AAL, 0x11AED295B1DBCED6L, 0x541255554AAAA490L, // 01xx
	0x0000000000555555L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, // 02xx
	0x0000000000000000L, 0x0000000000000000L, 0x00000FFBFFFED740L, 0x0000555400000000L, // 03xx
	0x0000FFFFFFFFDFFEL, 0x5555555500000000L, 0x5555555555550001L, 0x011545555555088AL, // 04xx
	0xFFFE000000000000L, 0x00000000007FFFFFL, 0x0000000000000000L, 0x0000000000000000L, // 05xx
	0x5555555555555555L, 0x5555555555555555L, 0x5555555500155555L, 0x0155555555555555L, // 1Exx
	0xFF00FF003F00FF00L, 0x0000FF00AA003F00L, 0x1F00FF00FF00FF00L, 0x1F001F000F001F00L, // 1Fxx
    };

    /**
     * Determines whether the specified character is an uppercase character.
     * 
     * <p> A character is considered to be uppercase if and only if all
     * of the following are true:
     * <ul>
     * <li>  The character is not in the range 0x2000 through 0x2FFF.
     * <li>  The Unicode 1.1.5 attribute table does not specify a
     *       mapping to uppercase for the character.
     * <li>  At least one of the following is true:
     *       <ul>
     *       <li>  The Unicode 1.1.5 attribute table does specify a
     *             mapping to lowercase for the character.
     *       <li>  The Unicode 1.1.5 name for the character contains
     *             the words "CAPITAL LETTER".
     *       <li>  The Unicode 1.1.5 name for the character contains
     *             the words "CAPITAL LIGATURE".
     *       </ul>
     * </ul>
     * Of the ISO-LATIN-1 characters (character codes 0x0000 through 0x00FF),
     * the following are uppercase:
     * A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
     *
     * \u00C0 \u00C1 \u00C2 \u00C3 \u00C4 \u00C5 \u00C6 \u00C7
     * \u00C8 \u00C9 \u00CA \u00CB \u00CC \u00CD \u00CE \u00CF \u00D0
     * \u00D1 \u00D2 \u00D3 \u00D4 \u00D5 \u00D6 \u00D8 \u00D9 \u00DA
     * \u00DB \u00DC \u00DD \u00DE
     *
     * @param ch	the character to be tested
     * @return 	true if the character is uppercase; false otherwise.
     * @see java.lang.Character#isLowerCase
     * @see java.lang.Character#isTitleCase
     * @see java.lang.Character#toUpperCase
     */

    public static boolean isUpperCase(char ch) {
	return ((((ch < 0x0600) ? isUpperCaseTable[ch >> 6] :
		  ((ch & 0xFE00) == 0x1E00) ? isUpperCaseTable[(ch - (0x1E00 - 0x0600)) >> 6] :
		  ((ch >= 0x10A0) && (ch <= 0x10CF)) ? 0xFFFFFFFF0000003FL :
		  ((ch & 0xFFC0) == 0xFF00) ? 0x07FFFFFE00000000L : 0) >> (ch & 0x3F)) & 0x1L) != 0;
    }


    /**
     * Determines whether the specified character is a titlecase character.
     *
     * <p> A character is considered to be titlecase if and only if all
     * of the following are true:
     * <ul>
     * <li>  The character is not in the range 0x2000 through 0x2FFF.
     * <li>  The Unicode 1.1.5 attribute table does specify a
     *       mapping to uppercase for the  character.
     * <li>  The Unicode 1.1.5 attribute table does specify a
     *       mapping to lowercase for the character.
     * </ul>
     * The basic idea is that there are a few Unicode characters
     * whose printed representations look like pairs of Latin
     * letters.  For example, there is an uppercase letter that
     * looks like "LJ" and the corresponding lowercase letter looks
     * like "lj".  These letters have a third form, a titlecase
     * form, that looks like "Lj".  This is the appropriate form
     * to use when rendering a word in lowercase with initial
     * capitals, as for a book title.
     * 
     * <p> There are exactly four Unicode characters for which this method
     * returns true:
     * <dl>
     * <dt>  \u01C5  <dd>  LATIN CAPITAL LETTER D WITH SMALL LETTER Z WITH CARON
     * <dt>  \u01C8  <dd>  LATIN CAPITAL LETTER L WITH SMALL LETTER J
     * <dt>  \u01CB  <dd>  LATIN CAPITAL LETTER N WITH SMALL LETTER J
     * <dt>  \u01F2  <dd>  LATIN CAPITAL LETTER D WITH SMALL LETTER Z
     * </dl>
     * 
     * @param ch	the character to be tested
     * @return 	true if the character is titlecase; false otherwise.
     * @see java.lang.Character#isUpperCase
     * @see java.lang.Character#isLowerCase
     * @see java.lang.Character#toTitleCase
     */

    /* A code point is considered to be titlecase if it is not in the range 2000-2FFF
       and has both a translation to lowercase and a translation to uppercase
       in the Unicode 1.1.5 attributes table.  There are exactly four such characters.
     */

    public static boolean isTitleCase(char ch) {
	return ch == '\u01C5'	// LATIN CAPITAL LETTER D WITH SMALL LETTER Z WITH CARON
	    || ch == '\u01C8'	// LATIN CAPITAL LETTER L WITH SMALL LETTER J
	    || ch == '\u01CB'	// LATIN CAPITAL LETTER N WITH SMALL LETTER J
	    || ch == '\u01F2';	// LATIN CAPITAL LETTER D WITH SMALL LETTER Z
    }

    /**
     * Determines whether the specified character is a digit.
     * 
     * <p> A character is considered to be a digit if and only if both
     * of the following are true:
     * <ul>
     * <li>  The character is not in the range 0x2000 through 0x2FFF.
     * <li>  The Unicode 1.1.5 name for the character contains
     *       the word "DIGIT".
     * </ul>
     * These are the ranges of Unicode characters that are considered digits:
     * <dl>
     * <dt>  0x0030 through 0x0039  <dd>  ISO-LATIN-1 digits ('0' through '9')
     * <dt>  0x0660 through 0x0669  <dd>  Arabic-Indic digits
     * <dt>  0x06F0 through 0x06F9  <dd>  Extended Arabic-Indic digits
     * <dt>  0x0966 through 0x096F  <dd>  Devanagari digits
     * <dt>  0x09E6 through 0x09EF  <dd>  Bengali digits
     * <dt>  0x0A66 through 0x0A6F  <dd>  Gurmukhi digits
     * <dt>  0x0AE6 through 0x0AEF  <dd>  Gujarati digits
     * <dt>  0x0B66 through 0x0B6F  <dd>  Oriya digits
     * <dt>  0x0BE7 through 0x0BEF  <dd>  Tamil digits
     * <dt>  0x0C66 through 0x0C6F  <dd>  Telugu digits
     * <dt>  0x0CE6 through 0x0CEF  <dd>  Kannada digits
     * <dt>  0x0D66 through 0x0D6F  <dd>  Malayalam digits
     * <dt>  0x0E50 through 0x0E59  <dd>  Thai digits
     * <dt>  0x0ED0 through 0x0ED9  <dd>  Lao digits
     * <dt>  0xFF10 through 0xFF19  <dd>  Fullwidth digits
     * </dl>
     *
     * @param ch	the character to be tested
     * @return 	true if the character is a digit; false otherwise.
     * @see java.lang.Character#digit
     * @see java.lang.Character#forDigit
     */

    public static boolean isDigit(char ch) {
	if ((ch >= '0') && (ch <= '9'))
	    return true;
	switch (ch>>8) {
	  case 0x06:
	    return 
	        ((ch >= 0x0660) && (ch <=0x0669)) ||	// Arabic-Indic
	        ((ch >= 0x06F0) && (ch <=0x06F9));	// Extended Arabic-Indic
	  case 0x09:
	    return
	        ((ch >= 0x0966) && (ch <=0x096F)) ||	// Devanagari
	        ((ch >= 0x09E6) && (ch <=0x09EF));	// Bengali
          case 0x0A:
	    return
	        ((ch >= 0x0A66) && (ch <=0x0A6F)) ||	// Gurmukhi
	        ((ch >= 0x0AE6) && (ch <=0x0AEF));	// Gujarati
	  case 0x0B:
	    return
	        ((ch >= 0x0B66) && (ch <=0x0B6F)) ||	// Oriya
	        ((ch >= 0x0BE7) && (ch <=0x0BEF));	// Tamil
	  case 0x0C:
	    return
	        ((ch >= 0x0C66) && (ch <=0x0C6F)) ||	// Telugu
	        ((ch >= 0x0CE6) && (ch <=0x0CEF));	// Kannada
	  case 0x0D:
	    return
	        ((ch >= 0x0D66) && (ch <=0x0D6F));	// Malayalam
	  case 0x0E:
	    return
	        ((ch >= 0x0E50) && (ch <=0x0E59)) ||	// Thai
	        ((ch >= 0x0ED0) && (ch <=0x0ED9));	// Lao
	  // Unicode 1.0 Tibetan script was withdrawn in Unicode 1.1 for further study
	  case 0xFF:
	    return
		((ch >= 0xFF10) && (ch <=0xFF19));	// Fullwidth digits
	  default:
	    return false;
	}
     }

    private static long isDefinedTable01C0through06FF[] = {
								       0xFC3FFFFFFFFFFFFFL,  // 01xx
	0x0000000000FFFFFFL, 0xFFFFFFFFFFFF0000L, 0xFFFF01FFFFFFFFFFL, 0x000003FF7FFFFFFFL,  // 02xx
	0xFFFFFFFFFFFFFFFFL, 0x443000030000003FL, 0xFFFFFFFBFFFFD7F0L, 0x000FFFFD547F7FFFL,  // 03xx
	0xFFFFFFFFFFFFDFFEL, 0xFFFFFFFFDFFEFFFFL, 0xFFFFFFFFFFFF007FL, 0x033FCFFFFFFF199FL,  // 04xx
	0xFFFE000000000000L, 0xFFFFFFFEFE7FFFFFL, 0xFBFF0000000002FFL, 0x001F07FFFFFF000FL,  // 05xx
	0x07FFFFFE88001000L, 0xFFFF3FFF0007FFFFL, 0x7CFFFFFFFFFFFFFFL, 0x03FF3FFFFFFF7FFFL,  // 06xx
    };

    private static long isDefinedTable0900through0EFF[] = {
	0xF3FFFFFFFFFFFFEEL, 0x0001FFFFFF1F3FFFL, 0xD3C5FDFFFFF99FEEL, 0x07FFFFCFB080399FL,  // 09xx
	0xD36DFDFFFFF987E4L, 0x001FFFC05E003987L, 0xF3EDFDFFFFFBAFEEL, 0x0000FFC100013BBFL,  // 0Axx
	0xF3CDFDFFFFF99FEEL, 0x0001FFC3B0C0398FL, 0xC3BFC718D63DC7ECL, 0x0007FF8000803DC7L,  // 0Bxx
	0xC3EFFDFFFFFDDFEEL, 0x0000FFC300603DDFL, 0xC3EFFDFFFFFDDFECL, 0x0000FFC340603DDFL,  // 0Cxx
	0xC3FFFDFFFFFDDFECL, 0x0000FFC300803DCFL, 0x0000000000000000L, 0x0000000000000000L,  // 0Dxx
	0x87FFFFFFFFFFFFFEL, 0x000000000FFFFFFFL, 0x3BFFECAEFEF02596L, 0x0000000033FF3F5FL,  // 0Exx
    };

    private static long isDefinedTable1080through11FF[] = {
						  0xFFFFFFFF00000000L, 0x087FFFFFFFFF003FL,  // 10xx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFF83FFFFFFL, 0xFFFFFF07FFFFFFFFL, 0x03FFFFFFFFFFFFFFL,  // 11xx
    };

    private static long isDefinedTable1E00through27BF[] = {
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFF07FFFFFFL, 0x03FFFFFFFFFFFFFFL,  // 1Exx
	0xFFFFFFFF3F3FFFFFL, 0x3FFFFFFFAAFF3F3FL, 0xFFDFFFFFFFFFFFFFL, 0x7FDCFFFFEFCFFFDFL,  // 1Fxx
	0xFFFF7FFFFFFFFFFFL, 0xFFF1FC000000007FL, 0x000007FF00007FFFL, 0x00000003FFFF0000L,  // 20xx
	0x01FFFFFFFFFFFFFFL, 0xFFFFFFFFFFF80000L, 0xFFFFFFFFFFFF0007L, 0x000007FFFFFFFFFFL,  // 21xx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0x0003FFFFFFFFFFFFL,  // 22xx
	0xFFFFFFFFFFFFFFFDL, 0x07FFFFFFFFFFFFFFL, 0x0000000000000000L, 0x0000000000000000L,  // 23xx
	0x0000001FFFFFFFFFL, 0xFFFFFFFF000007FFL, 0xFFFFFFFFFFFFFFFFL, 0x000007FFFFFFFFFFL,  // 24xx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFF003FFFFFL, 0x0000FFFFFFFFFFFFL,  // 25xx
	0xFFFFFFFFFC0FFFFFL, 0x0000FFFFFFFFFFFFL, 0x0000000000000000L, 0x0000000000000000L,  // 26xx
	0xFFFFFEFFFFFFF3DEL, 0xFFC000FE7F47AFFFL, 0x7FFEFFFFFF1FFFFFL,			     // 27xx
    };

    private static long isDefinedTable3000through33FF[] = {
	0x80FFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFEL, 0xFFFFFFFE7E1FFFFFL, 0x7FFFFFFFFFFFFFFFL,  // 30xx
	0xFFFE1FFFFFFFFFE0L, 0xFFFFFFFFFFFFFFFFL, 0x00000000FFFF7FFFL, 0x0000000000000000L,  // 31xx
	0xFFFFFFFF1FFFFFFFL, 0x8FFFFFFF0000000FL, 0x0001FFFFFFFFFFFFL, 0x7FFFFFFFFFFF0FFFL,  // 32xx
	0xFFFFFFFFFFFFFFFFL, 0xF87FFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0x7FFFFFFF3FFFFFFFL,  // 33xx
    };

    private static long isDefinedTableFB00throughFFFF[] = {
	0x5F7FFFFFC0F8007FL, 0xFFFFFFFFFFFFFFDBL, 0x0003FFFFFFFFFFFFL, 0xFFFFFFFFFFF80000L,  // FBxx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL,  // FCxx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFF0000L, 0xFFFFFFFFFFFCFFFFL, 0x0FFF0000000000FFL,  // FDxx
	0xFFFF000F00000000L, 0xFFD70F7FFFF7FE1FL, 0xFFFFFFFFFFFFFFFFL, 0x9FFFFFFFFFFFFFFFL,  // FExx
	0xFFFFFFFFFFFFFFFEL, 0xFFFFFFFE7FFFFFFFL, 0x7FFFFFFFFFFFFFFFL, 0x20007F7F1CFCFCFCL,  // FFxx
    };

    /**
     * Determines whether the specified character actually has a Unicode definition.
     * 
     * @param ch	the character to be tested
     * @return 	true if the character has a defined meaning in Unicode 1.1.5; false otherwise.
     *
     * @see java.lang.Character#isDigit
     * @see java.lang.Character#isLetter
     * @see java.lang.Character#isLetterOrDigit
     * @see java.lang.Character#isUpperCase
     * @see java.lang.Character#isLowerCase
     * @see java.lang.Character#isTitleCase
     */

    public static boolean isDefined(char ch) {
	if (ch < 0x01F6)
	    return true;
	else if (ch < 0x0700)
	    return ((isDefinedTable01C0through06FF[(ch - 0x01C0) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x0900) && (ch <= 0x0EFF))
	    return ((isDefinedTable0900through0EFF[(ch - 0x0900) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x1080) && (ch <= 0x11FF))
	    return ((isDefinedTable1080through11FF[(ch - 0x1080) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x1E00) && (ch <= 0x27BF))
	    return ((isDefinedTable1E00through27BF[(ch - 0x1E00) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x3000) && (ch <= 0x33FF))
	    return ((isDefinedTable3000through33FF[(ch - 0x3000) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if (ch >= 0xFB00)
	    return ((isDefinedTableFB00throughFFFF[(ch - 0xFB00) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else return ((ch >= 0x3400) && (ch <= 0x9FA5)) || ((ch >= 0xF900) && (ch <= 0xFA2D));
    }

    private static long isLetterTable0000through06FF[] = {
	0x0000000000000000L, 0x07FFFFFE07FFFFFEL, 0x0000000000000000L, 0xFF7FFFFFFF7FFFFFL,  // 00xx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFC3FFFFFFFFFFFFFL,  // 01xx
	0x0000000000FFFFFFL, 0xFFFFFFFFFFFF0000L, 0xFFFF01FFFFFFFFFFL, 0x000003FF7FFFFFFFL,  // 02xx
	0xFFFFFFFFFFFFFFFFL, 0x443000030000003FL, 0xFFFFFFFBFFFFD7F0L, 0x000FFFFD547F7FFFL,  // 03xx
	0xFFFFFFFFFFFFDFFEL, 0xFFFFFFFFDFFEFFFFL, 0xFFFFFFFFFFFF007FL, 0x033FCFFFFFFF199FL,  // 04xx
	0xFFFE000000000000L, 0xFFFFFFFEFE7FFFFFL, 0xFBFF0000000002FFL, 0x001F07FFFFFF000FL,  // 05xx
	0x07FFFFFE88001000L, 0xFFFF3C000007FFFFL, 0x7CFFFFFFFFFFFFFFL, 0x00003FFFFFFF7FFFL,  // 06xx
    };

    private static long isLetterTable0900through0EFF[] = {
	0xF3FFFFFFFFFFFFEEL, 0x0001003FFF1F3FFFL, 0xD3C5FDFFFFF99FEEL, 0x07FF000FB080399FL,  // 09xx
	0xD36DFDFFFFF987E4L, 0x001F00005E003987L, 0xF3EDFDFFFFFBAFEEL, 0x0000000100013BBFL,  // 0Axx
	0xF3CDFDFFFFF99FEEL, 0x00010003B0C0398FL, 0xC3BFC718D63DC7ECL, 0x0007000000803DC7L,  // 0Bxx
	0xC3EFFDFFFFFDDFEEL, 0x0000000300603DDFL, 0xC3EFFDFFFFFDDFECL, 0x0000000340603DDFL,  // 0Cxx
	0xC3FFFDFFFFFDDFECL, 0x0000000300803DCFL, 0x0000000000000000L, 0x0000000000000000L,  // 0Dxx
	0x87FFFFFFFFFFFFFEL, 0x000000000C00FFFFL, 0x3BFFECAEFEF02596L, 0x0000000030003F5FL,  // 0Exx
    };

    private static long isLetterTable1080through11FF[] = isDefinedTable1080through11FF;

    private static long isLetterTable1E00through1FFF[] = isDefinedTable1E00through27BF;

    private static long isLetterTable3000through33FF[] = isDefinedTable3000through33FF;

    private static long isLetterTableFB00throughFFFF[] = {
	0x5F7FFFFFC0F8007FL, 0xFFFFFFFFFFFFFFDBL, 0x0003FFFFFFFFFFFFL, 0xFFFFFFFFFFF80000L,  // FBxx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL,  // FCxx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFF0000L, 0xFFFFFFFFFFFCFFFFL, 0x0FFF0000000000FFL,  // FDxx
	0x0000000000000000L, 0xFFD7000000000000L, 0xFFFFFFFFFFFFFFFFL, 0x1FFFFFFFFFFFFFFFL,  // FExx
	0x07FFFFFE00000000L, 0xFFFFFFC007FFFFFEL, 0x7FFFFFFFFFFFFFFFL, 0x000000001CFCFCFCL,  // FFxx
    };

    /**
     * Determines whether the specified character is a letter.
     * <p> A character is considered to be a letter if and only if
     * is it a "letter or digit" but is not a "digit".
     *
     * <p> Note that not all letters have case: many Unicode characters are
     * letters but are neither uppercase nor lowercase nor titlecase.
     * 
     * @param ch	the character to be tested
     * @return 	true if the character is a letter; false otherwise.
     *
     *
     * @see java.lang.Character#isDigit
     * @see java.lang.Character#isLetterOrDigit
     * @see java.lang.Character#isJavaLetter
     * @see java.lang.Character#isJavaLetterOrDigit
     * @see java.lang.Character#isUpperCase
     * @see java.lang.Character#isLowerCase
     * @see java.lang.Character#isTitleCase
     */

    public static boolean isLetter(char ch) {
	if (ch < 0x0700)
	    return ((isLetterTable0000through06FF[ch >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x0900) && (ch <= 0x0EFF))
	    return ((isLetterTable0900through0EFF[(ch - 0x0900) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x1080) && (ch <= 0x11FF))
	    return ((isLetterTable1080through11FF[(ch - 0x1080) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x1E00) && (ch <= 0x1FFF))
	    return ((isLetterTable1E00through1FFF[(ch - 0x1E00) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x3040) && (ch <= 0x33FF))
	    return ((isLetterTable3000through33FF[(ch - 0x3000) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if (ch >= 0xFB00)
	    return ((isLetterTableFB00throughFFFF[(ch - 0xFB00) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else return ((ch >= 0x3400) && (ch <= 0x9FA5)) || ((ch >= 0xF900) && (ch <= 0xFA2D));
    }

    private static long isLDTable0000through06FF[] = {
	0x03FF000000000000L, 0x07FFFFFE07FFFFFEL, 0x0000000000000000L, 0xFF7FFFFFFF7FFFFFL,  // 00xx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFC3FFFFFFFFFFFFFL,  // 01xx
	0x0000000000FFFFFFL, 0xFFFFFFFFFFFF0000L, 0xFFFF01FFFFFFFFFFL, 0x000003FF7FFFFFFFL,  // 02xx
	0xFFFFFFFFFFFFFFFFL, 0x443000030000003FL, 0xFFFFFFFBFFFFD7F0L, 0x000FFFFD547F7FFFL,  // 03xx
	0xFFFFFFFFFFFFDFFEL, 0xFFFFFFFFDFFEFFFFL, 0xFFFFFFFFFFFF007FL, 0x033FCFFFFFFF199FL,  // 04xx
	0xFFFE000000000000L, 0xFFFFFFFEFE7FFFFFL, 0xFBFF0000000002FFL, 0x001F07FFFFFF000FL,  // 05xx
	0x07FFFFFE88001000L, 0xFFFF3FFF0007FFFFL, 0x7CFFFFFFFFFFFFFFL, 0x03FF3FFFFFFF7FFFL,  // 06xx
    };

    private static long isLDTable0900through0EFF[] = {
	0xF3FFFFFFFFFFFFEEL, 0x0001FFFFFF1F3FFFL, 0xD3C5FDFFFFF99FEEL, 0x07FFFFCFB080399FL,  // 09xx
	0xD36DFDFFFFF987E4L, 0x001FFFC05E003987L, 0xF3EDFDFFFFFBAFEEL, 0x0000FFC100013BBFL,  // 0Axx
	0xF3CDFDFFFFF99FEEL, 0x0001FFC3B0C0398FL, 0xC3BFC718D63DC7ECL, 0x0007FF8000803DC7L,  // 0Bxx
	0xC3EFFDFFFFFDDFEEL, 0x0000FFC300603DDFL, 0xC3EFFDFFFFFDDFECL, 0x0000FFC340603DDFL,  // 0Cxx
	0xC3FFFDFFFFFDDFECL, 0x0000FFC300803DCFL, 0x0000000000000000L, 0x0000000000000000L,  // 0Dxx
	0x87FFFFFFFFFFFFFEL, 0x000000000FFFFFFFL, 0x3BFFECAEFEF02596L, 0x0000000033FF3F5FL,  // 0Exx
    };

    private static long isLDTable1080through11FF[] = isDefinedTable1080through11FF;

    private static long isLDTable1E00through1FFF[] = isDefinedTable1E00through27BF;

    private static long isLDTable3000through33FF[] = isDefinedTable3000through33FF;

    private static long isLDTableFB00throughFFFF[] = {
	0x5F7FFFFFC0F8007FL, 0xFFFFFFFFFFFFFFDBL, 0x0003FFFFFFFFFFFFL, 0xFFFFFFFFFFF80000L,  // FBxx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL,  // FCxx
	0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFF0000L, 0xFFFFFFFFFFFCFFFFL, 0x0FFF0000000000FFL,  // FDxx
	0x0000000000000000L, 0xFFD7000000000000L, 0xFFFFFFFFFFFFFFFFL, 0x1FFFFFFFFFFFFFFFL,  // FExx
	0x07FFFFFE03FF0000L, 0xFFFFFFC007FFFFFEL, 0x7FFFFFFFFFFFFFFFL, 0x000000001CFCFCFCL,  // FFxx
    };

    /**
     * Determines whether the specified character is a letter or digit.
     *
     * These are the ranges of Unicode characters that are considered to be letters or digits:
     * <dl>
     * <dt>  0x0030 through 0x0039  <dd>  ISO-LATIN-1 digits ('0' through '9')
     * <dt>  0x0041 through 0x005A  <dd>  ISO-LATIN-1 uppercase Latin letters ('A' through 'Z')
     * <dt>  0x0061 through 0x007A  <dd>  ISO-LATIN-1 lowercase Latin letters ('A' through 'Z')
     * <dt>  0x00C0 through 0x00D6,
     *       0x00D8 through 0x00F6, and
     *       0x00F8 through 0x00FF  <dd>  ISO-LATIN-1 supplementary letters
     * <dt>  0x0100 through 0x1FFF, but only if there is an entry in the Unicode 1.1.5 table
     *                              <dd>  Latin extended-A, Latin extended-B, IPA extensions,
     *					  spacing modifier letters, combining diacritical marks,
     *					  basic Greek, Greek symbols and Coptic, Cyrillic, Armenian,
     *					  Hebrew extended-A, Basic Hebrew, Hebrew extended-B,
     *					  Basic Arabic, Arabic extended, Devanagari, Bengali,
     *					  Gurmukhi, Gujarati, Oriya, Tamil, Telugu, Kannada,
     *					  Malayalam, Thai, Lao, Basic Georgian, Georgian extended,
     *					  Hanguljamo, Latin extended additional, Greek extended
     * <dt>  0x3040 through 0x9FA5  <dd>  Hiragana, Katakana, Bopomofo, Hangul compatibility Jamo,
     *					  CJK miscellaneous, enclosed CJK characters and months,
     *					  CJK compatibility, Hangul, Hangul supplementary-A,
     *					  Hangul supplementary-B, CJK unified ideographs
     * <dt>  0xF900 through 0xFA2D  <dd>  CJK compatibility ideographs
     * <dt>  0xFB00 through 0xFDFF, but only if there is an entry in the Unicode 1.1.5 table
     *                              <dd>  alphabetic presentation forms,
     *					  Arabic presentation forms-A
     * <dt>  0xFE70 through 0xFEFE, but only if there is an entry in the Unicode 1.1.5 table
     *                              <dd>  Arabic presentation forms-B
     * <dt>  0xFF10 through 0xFF19  <dd>  Fullwidth digits
     * <dt>  0xFF21 through 0xFF3A  <dd>  Fullwidth Latin uppercase
     * <dt>  0xFF41 through 0xFF5A  <dd>  Fullwidth Latin lowercase
     * <dt>  0xFF66 through 0xFFDC  <dd>  Halfwidth Katakana and Hangul
     * </dl>
     * 
     * @param ch	the character to be tested
     * @return 	true if the character is a letter or digit; false otherwise.
     *
     * @see java.lang.Character#isDigit
     * @see java.lang.Character#isLetter
     * @see java.lang.Character#isJavaLetter
     * @see java.lang.Character#isJavaLetterOrDigit
     */


    public static boolean isLetterOrDigit(char ch) {
	if (ch < 0x0700)
	    return ((isLDTable0000through06FF[ch >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x0900) && (ch <= 0x0EFF))
	    return ((isLDTable0900through0EFF[(ch - 0x0900) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x1080) && (ch <= 0x11FF))
	    return ((isLDTable1080through11FF[(ch - 0x1080) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x1E00) && (ch <= 0x1FFF))
	    return ((isLDTable1E00through1FFF[(ch - 0x1E00) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if ((ch >= 0x3040) && (ch <= 0x33FF))
	    return ((isLDTable3000through33FF[(ch - 0x3000) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else if (ch >= 0xFB00)
	    return ((isLDTableFB00throughFFFF[(ch - 0xFB00) >> 6] >> (ch & 0x3F)) & 0x1L) != 0;
	else return ((ch >= 0x3400) && (ch <= 0x9FA5)) || ((ch >= 0xF900) && (ch <= 0xFA2D));
    }

    /**
     * Determines whether the specified character is a "Java" letter,
     * that is, permissible as the first character in a Java identifier.
     *
     * <p> A character is considered to be a Java letter if and only if
     * it is a letter or the dollar sign "$" or the underscore "_".
     *
     * @param ch	the character to be tested
     * @return 	true if the character is a Java letter; false otherwise.
     * @see java.lang.Character#isLetter
     * @see java.lang.Character#isLetterOrDigit
     * @see java.lang.Character#isJavaLetterOrDigit
     */

    public static boolean isJavaLetter(char ch) {
      return isLetter(ch) || ch == '$' || ch == '_';
    }

    /**
     * Determines whether the specified character is a "Java" letter or digit,
     * that is, permissible as a non-initial character in a Java identifier.
     * <p> A character is considered to be a Java letter or digit if and only if
     * it is a letter or a digit or the dollar sign "$" or the underscore "_".
     *
     * 
     * @param ch	the character to be tested
     * @return 	true if the character is a Java letter or digit; 
     *          false otherwise.
     *
     * @see java.lang.Character#isLetter
     * @see java.lang.Character#isLetterOrDigit
     * @see java.lang.Character#isJavaLetter
     */

    public static boolean isJavaLetterOrDigit(char ch) {
      return isLetterOrDigit(ch) || ch == '$' || ch == '_';
    }

    private static long toLowerCaseTable[] = {
	0xAA54555555555555L, 0x2B555555555554AAL, 0x11AED29531DBCCD6L, 0x541655554AAAADB0L, // 01xx
	0x0000000000555555L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, // 02xx
	0x0000000000000000L, 0x0000000000000000L, 0x00000FFBFFFED740L, 0x0000555400000000L, // 03xx
	0x0000FFFFFFFFDFFEL, 0x5555555500000000L, 0x5555555555550001L, 0x011545555555088AL, // 04xx
	0xFFFE000000000000L, 0x00000000007FFFFFL, 0x0000000000000000L, 0x0000000000000000L, // 05xx
	0x5555555555555555L, 0x5555555555555555L, 0x5555555500155555L, 0x0155555555555555L, // 1Exx
	0xFF00FF003F00FF00L, 0x0000FF00AA003F00L, 0x1F00FF00FF00FF00L, 0x1F001F000F001F00L, // 1Fxx
    };

    /**
     * The given character is mapped to its lowercase equivalent;
     * if the character has no lowercase equivalent, the character itself is returned.
     * A character has a lowercase equivalent if and only if a lowercase mapping
     * is specified for the character in the Unicode 1.1.5 attribute table.
     *
     * Note that some Unicode characters in the range 0x2000 through 0x2FFF have
     * lowercase mappings; for example, \u2160 (ROMAN NUMERAL ONE) has a lowercase
     * mapping to \u2170 (SMALL ROMAN NUMERAL ONE).  This method does map such
     * characters to their lowercase equivalents even though the method isUpperCase
     * does not return true for such characters.
     *
     * @param ch	the character to be converted
     * @return 	the lowercase equivalent of the character, if any;
     *		otherwise the character itself.
     *
     * @see java.lang.Character#isLowerCase
     * @see java.lang.Character#isUpperCase
     * @see java.lang.Character#toUpperCase
     * @see java.lang.Character#toTitleCase
     */

    public static char toLowerCase(char ch) {
	// Quick check for ISO-LATIN-1
	if ((ch >= 'A') && (ch <= 'Z'))
	    return (char)(ch + ('a' - 'A'));
	if (ch < 0x00C0) return ch;
	if (ch < 0x0100) {
	    if (ch >= 0x00DF) return ch;
	    if (ch == '\u00D7') return ch;		// MULTIPLICATION SIGN
	    return (char)(ch + 0x0020);
	}
	// Okay, it's more complicated
	if (((((ch < 0x0600) ? toLowerCaseTable[(ch - 0x0100) >> 6] :
	      ((ch & 0xFE00) == 0x1E00) ? toLowerCaseTable[(ch - (0x1E00 - (0x0600 - 0x0100))) >> 6] :
	      ((ch >= 0x10A0) && (ch <= 0x10C5)) ? 0xFFFFFFFF0000003FL :
	      ((ch & 0xFFC0) == 0x2140) ? 0x0000FFFF00000000L :
	      ((ch >= 0x24B6) && (ch <= 0x24CF)) ? 0xFFC000000000FFFFL :
	      ((ch & 0xFFC0) == 0xFF00) ? 0x07FFFFFE00000000L : 0) >> (ch & 0x3F)) & 0x1L) != 0) {
	    // It is known to be lowercase; now figure out the conversion
	    switch (ch >> 8) {
	      case 0x01:
		switch (ch & 0xFF) {
		  case 0x78: return '\u00FF';	// LATIN CAPITAL LETTER Y WITH DIAERESIS
		  case 0x81: return '\u0253';	// LATIN CAPITAL LETTER B WITH HOOK
		  case 0x86: return '\u0254';	// LATIN CAPITAL LETTER OPEN O
		  case 0x8A: return '\u0257';	// LATIN CAPITAL LETTER D WITH HOOK
		  case 0x8E: return '\u0258';	// LATIN CAPITAL LETTER REVERSED E
		  case 0x8F: return '\u0259';	// LATIN CAPITAL LETTER SCHWA
		  case 0x90: return '\u025B';	// LATIN CAPITAL LETTER OPEN E
		  case 0x93: return '\u0260';	// LATIN CAPITAL LETTER G WITH HOOK
		  case 0x94: return '\u0263';	// LATIN CAPITAL LETTER GAMMA
		  case 0x96: return '\u0269';	// LATIN CAPITAL LETTER IOTA
		  case 0x97: return '\u0268';	// LATIN CAPITAL LETTER I WITH STROKE
		  case 0x9C: return '\u026F';	// LATIN CAPITAL LETTER TURNED M
		  case 0x9D: return '\u0272';	// LATIN CAPITAL LETTER N WITH LEFT HOOK
		  case 0xA9: return '\u0283';	// LATIN CAPITAL LETTER ESH
		  case 0xAE: return '\u0288';	// LATIN CAPITAL LETTER T WITH RETROFLEX HOOK
		  case 0xB1: return '\u028A';	// LATIN CAPITAL LETTER UPSILON
		  case 0xB2: return '\u028B';	// LATIN CAPITAL LETTER V WITH HOOK
		  case 0xB7: return '\u0292';	// LATIN CAPITAL LETTER EZH
		  case 0xC4: return '\u01C6';	// LATIN CAPITAL LETTER DZ WITH CARON
		  case 0xC7: return '\u01C9';	// LATIN CAPITAL LETTER LJ
		  case 0xCA: return '\u01CC';	// LATIN CAPITAL LETTER NJ
		  case 0xF1: return '\u01F3';	// LATIN CAPITAL LETTER DZ
		  default:   return (char)(ch + 1);
		}
	      case 0x02:
		return (char)(ch + 1);
	      case 0x03:
		switch (ch & 0xFF) {
		  case 0x86: return '\u03AC';	// GREEK CAPITAL LETTER ALPHA WITH TONOS
		  case 0x88: return '\u03AD';	// GREEK CAPITAL LETTER EPSILON WITH TONOS
		  case 0x89: return '\u03AE';	// GREEK CAPITAL LETTER ETA WITH TONOS
		  case 0x8A: return '\u03AF';	// GREEK CAPITAL LETTER IOTA WITH TONOS
		  case 0x8C: return '\u03CC';	// GREEK CAPITAL LETTER OMICRON WITH TONOS
		  case 0x8E: return '\u03CD';	// GREEK CAPITAL LETTER UPSILON WITH TONOS
		  case 0x8F: return '\u03CE';	// GREEK CAPITAL LETTER OMEGA WITH TONOS
		  default:
		    if (ch < 0x03B0)
			return (char)(ch + 0x0020);
		    else
			return (char)(ch + 1);
		}
	      case 0x04:
		if (ch < 0x0410) return (char)(ch + 0x0050);
		if (ch < 0x0460) return (char)(ch + 0x0020);
		return (char)(ch + 1);
	      case 0x05:
		// drop through
	      case 0x10:
		return (char)(ch + 0x0030);
	      case 0x1E:
		return (char)(ch + 1);
	      case 0x1F:
		switch (ch & 0xFF) {
		  case 0xBA: return '\u1F70';	// GREEK CAPITAL LETTER ALPHA WITH VARIA
		  case 0xBB: return '\u1F71';	// GREEK CAPITAL LETTER ALPHA WITH OXIA
		  case 0xBC: return '\u1FB3';	// GREEK CAPITAL LETTER ALPHA WITH PROSGEGRAMMENI
		  case 0xC8: return '\u1F72';	// GREEK CAPITAL LETTER EPSILON WITH VARIA
		  case 0xC9: return '\u1F73';	// GREEK CAPITAL LETTER EPSILON WITH OXIA
		  case 0xCA: return '\u1F74';	// GREEK CAPITAL LETTER ETA WITH VARIA
		  case 0xCB: return '\u1F75';	// GREEK CAPITAL LETTER ETA WITH OXIA
		  case 0xCC: return '\u1FC3';	// GREEK CAPITAL LETTER ETA WITH PROSGEGRAMMENI
		  case 0xDA: return '\u1F76';	// GREEK CAPITAL LETTER IOTA WITH VARIA
		  case 0xDB: return '\u1F77';	// GREEK CAPITAL LETTER IOTA WITH OXIA
		  case 0xEA: return '\u1F7A';	// GREEK CAPITAL LETTER UPSILON WITH VARIA
		  case 0xEB: return '\u1F7B';	// GREEK CAPITAL LETTER UPSILON WITH OXIA
		  case 0xEC: return '\u1FE5';	// GREEK CAPITAL LETTER RHO WITH DASIA
		  case 0xF8: return '\u1F78';	// GREEK CAPITAL LETTER OMICRON WITH VARIA
		  case 0xF9: return '\u1F79';	// GREEK CAPITAL LETTER OMICRON WITH OXIA
		  case 0xFA: return '\u1F7C';	// GREEK CAPITAL LETTER OMEGA WITH VARIA
		  case 0xFB: return '\u1F7D';	// GREEK CAPITAL LETTER OMEGA WITH OXIA
		  case 0xFC: return '\u1FF3';	// GREEK CAPITAL LETTER OMEGA WITH PROSGEGRAMMENI
		  default:   return (char)(ch - 8);
		}
	      case 0x21:
		return (char)(ch + 0x0010);
	      case 0x24:
		return (char)(ch + 0x001A);
	      case 0xFF:
		return (char)(ch + 0x0020);
	    }
	}
	return ch;
    }

    /**
     * The given character is mapped to its uppercase equivalent;
     * if the character has no uppercase equivalent, the character itself is returned.
     * 
     * <p>
     * A character has a uppercase equivalent if and only if a uppercase mapping
     * is specified for the character in the Unicode 1.1.5 attribute table.
     *
     * Note that some Unicode characters in the range 0x2000 through 0x2FFF have
     * uppercase mappings; for example, \u2170 (SMALL ROMAN NUMERAL ONE) has an
     * uppercase mapping to \u2160 (ROMAN NUMERAL ONE).  This method does map such
     * characters to their uppercase equivalents even though the method isLowerCase
     * does not return true for such characters.
     *
     * @param ch	the character to be converted
     * @return 	the uppercase equivalent of the character, if any;
     *		otherwise the character itself.
     *
     * @see java.lang.Character#isUpperCase
     * @see java.lang.Character#isLowerCase
     * @see java.lang.Character#toLowerCase
     * @see java.lang.Character#toTitleCase
     */

    private static long toUpperCaseTable[] = {
	0x54A8AAAAAAAAAAAAL, 0xD4AAAAAAAAAAA955L, 0x2251212A02041128L, 0xA82CAAAA95555B60L, // 01xx
	0x0000000000AAAAAAL, 0x0004830B0B980000L, 0x0000000000040D08L, 0x0000000000000000L, // 02xx
	0x0000000000000000L, 0x0000000000000000L, 0xFFFEF00000000000L, 0x0003AAA800637FFFL, // 03xx
	0xFFFF000000000000L, 0xAAAAAAAADFFEFFFFL, 0xAAAAAAAAAAAA0002L, 0x022A8AAAAAAA1114L, // 04xx
	0x0000000000000000L, 0xFFFFFFFE00000000L, 0x000000000000007FL, 0x0000000000000000L, // 05xx
	0xAAAAAAAAAAAAAAAAL, 0xAAAAAAAAAAAAAAAAL, 0xAAAAAAAA002AAAAAL, 0x02AAAAAAAAAAAAAAL, // 1Exx
	0x00FF00FF003F00FFL, 0x3FFF00FF00AA003FL, 0x000B00FF00FF00FFL, 0x0008002300030008L, // 1Fxx
    };

    public static char toUpperCase(char ch) {
	// Quick check for ISO-LATIN-1
	if ((ch >= 'a') && (ch <= 'z'))
	    return (char)(ch + ('A' - 'a'));
	if (ch < 0x00E0) return ch;
	if (ch < 0x0100) {
	    if (ch == '\u00F7') return ch;		// DIVISION SIGN
	    if (ch == '\u00FF') return '\u0178';	// Y WITH DIAERESIS
	    return (char)(ch - 0x0020);
	}
	// Okay, it's more complicated
	if (((((ch < 0x0600) ? toUpperCaseTable[(ch - 0x0100) >> 6] :
	      ((ch & 0xFE00) == 0x1E00) ? toUpperCaseTable[(ch - (0x1E00 - (0x0600 - 0x0100))) >> 6] :
	      ((ch & 0xFFC0) == 0x2140) ? 0xFFFF000000000000L :
	      ((ch & 0xFFC0) == 0x24C0) ? 0x000003FFFFFF0000L :
	      ((ch & 0xFFC0) == 0xFF40) ? 0x0000000007FFFFFEL : 0) >> (ch & 0x3F)) & 0x1L) != 0) {
	    // It is known to be lowercase; now figure out the conversion
	    switch (ch >> 8) {
	      case 0x01:
		if (ch == '\u017F') return 'S';
		if ((ch == '\u01C6') || (ch == '\u01C9') || (ch == '\u01CC') || (ch == '\u01F3'))
		    return (char)(ch - 2);
		return (char)(ch - 1);
	      case 0x02:
		if (ch < 0x0240) return (char)(ch - 1);
		switch (ch & 0xFF) {
		  case 0x53: return '\u0181';	// LATIN SMALL LETTER B WITH HOOK
		  case 0x54: return '\u0186';	// LATIN SMALL LETTER OPEN O
		  case 0x57: return '\u018A';	// LATIN SMALL LETTER D WITH HOOK
		  case 0x58: return '\u018E';	// LATIN SMALL LETTER REVERSED E
		  case 0x59: return '\u018F';	// LATIN SMALL LETTER SCHWA
		  case 0x5B: return '\u0190';	// LATIN SMALL LETTER OPEN E
		  case 0x60: return '\u0193';	// LATIN SMALL LETTER G WITH HOOK
		  case 0x63: return '\u0194';	// LATIN SMALL LETTER GAMMA
		  case 0x68: return '\u0197';	// LATIN SMALL LETTER I WITH STROKE
		  case 0x69: return '\u0196';	// LATIN SMALL LETTER IOTA
		  case 0x6F: return '\u019C';	// LATIN SMALL LETTER TURNED M
		  case 0x72: return '\u019D';	// LATIN SMALL LETTER N WITH LEFT HOOK
		  case 0x83: return '\u01A9';	// LATIN SMALL LETTER ESH
		  case 0x88: return '\u01AE';	// LATIN SMALL LETTER T WITH RETROFLEX HOOK
		  case 0x8A: return '\u01B1';	// LATIN SMALL LETTER UPSILON
		  case 0x8B: return '\u01B2';	// LATIN SMALL LETTER V WITH HOOK
		  case 0x92: return '\u01B7';	// LATIN SMALL LETTER EZH
		  default:   return ch;		// shouldn't happen, actually
		}
	      case 0x03:
		switch (ch & 0xFF) {
		  case 0xAC: return '\u0386';	// GREEK SMALL LETTER ALPHA WITH TONOS
		  case 0xAD: return '\u0388';	// GREEK SMALL LETTER EPSILON WITH TONOS
		  case 0xAE: return '\u0389';	// GREEK SMALL LETTER ETA WITH TONOS
		  case 0xAF: return '\u038A';	// GREEK SMALL LETTER IOTA WITH TONOS
		  case 0xC2: return '\u03A3';	// GREEK SMALL LETTER FINAL SIGMA
		  case 0xCC: return '\u038C';	// GREEK SMALL LETTER OMICRON WITH TONOS
		  case 0xCD: return '\u038E';	// GREEK SMALL LETTER UPSILON WITH TONOS
		  case 0xCE: return '\u038F';	// GREEK SMALL LETTER OMEGA WITH TONOS
		  case 0xD0: return '\u0392';	// GREEK BETA SYMBOL
		  case 0xD1: return '\u0398';	// GREEK THETA SYMBOL
		  case 0xD5: return '\u03A6';	// GREEK PHI SYMBOL
		  case 0xD6: return '\u03A0';	// GREEK PI SYMBOL
		  case 0xF0: return '\u039A';	// GREEK KAPPA SYMBOL
		  case 0xF1: return '\u03A1';	// GREEK RHO SYMBOL
		  default:
		    if (ch < 0x03E0)
			return (char)(ch - 0x0020);
		    else
			return (char)(ch - 1);
		}
	      case 0x04:
		if (ch < 0x0450) return (char)(ch - 0x0020);
		if (ch < 0x0460) return (char)(ch - 0x0050);
		return (char)(ch - 1);
	      case 0x05:
		return (char)(ch - 0x0030);
	      case 0x1E:
		return (char)(ch - 1);
	      case 0x1F:
		switch (ch & 0xFF) {
		  case 0x70: return '\u1FBA';	// GREEK SMALL LETTER ALPHA WITH VARIA
		  case 0x71: return '\u1FBB';	// GREEK SMALL LETTER ALPHA WITH OXIA
		  case 0x72: return '\u1FC8';	// GREEK SMALL LETTER EPSILON WITH VARIA
		  case 0x73: return '\u1FC9';	// GREEK SMALL LETTER EPSILON WITH OXIA
		  case 0x74: return '\u1FCA';	// GREEK SMALL LETTER ETA WITH VARIA
		  case 0x75: return '\u1FCB';	// GREEK SMALL LETTER ETA WITH OXIA
		  case 0x76: return '\u1FDA';	// GREEK SMALL LETTER IOTA WITH VARIA
		  case 0x77: return '\u1FDB';	// GREEK SMALL LETTER IOTA WITH OXIA
		  case 0x78: return '\u1FF8';	// GREEK SMALL LETTER OMICRON WITH VARIA
		  case 0x79: return '\u1FF9';	// GREEK SMALL LETTER OMICRON WITH OXIA
		  case 0x7A: return '\u1FEA';	// GREEK SMALL LETTER UPSILON WITH VARIA
		  case 0x7B: return '\u1FEB';	// GREEK SMALL LETTER UPSILON WITH OXIA
		  case 0x7C: return '\u1FFA';	// GREEK SMALL LETTER OMEGA WITH VARIA
		  case 0x7D: return '\u1FFB';	// GREEK SMALL LETTER OMEGA WITH OXIA
		  case 0xB3: return '\u1FBC';	// GREEK SMALL LETTER ALPHA WITH YPOGEGRAMMENI
		  case 0xC3: return '\u1FCC';	// GREEK SMALL LETTER ETA WITH YPOGEGRAMMENI
		  case 0xE5: return '\u1FEC';	// GREEK SMALL LETTER RHO WITH DASIA
		  case 0xF3: return '\u1FFC';	// GREEK SMALL LETTER OMEGA WITH YPOGEGRAMMENI
		  default:   return (char)(ch + 8);
		}
	      case 0x21:
		return (char)(ch - 0x0010);
	      case 0x24:
		return (char)(ch - 0x001A);
	      case 0xFF:
		return (char)(ch - 0x0020);
	    }
	}
	return ch;
    }

    /**
     * The given character is mapped to its titlecase equivalent;
     * if the character has no titlecase equivalent, the character itself is returned.
     *
     * A character has a titlecase equivalent if and only if a titlecase mapping
     * is specified for the character in the Unicode 1.1.5 attribute table.
     *
     * Note that some Unicode characters in the range 0x2000 through 0x2FFF have
     * titlecase mappings; for example, \u2170 (SMALL ROMAN NUMERAL ONE) has an
     * titlecase mapping to \u2160 (ROMAN NUMERAL ONE).  This method does map such
     * characters to their titlecase equivalents.
     *
     * There are only four Unicode characters that are truly titlecase forms
     * that are distinct from uppercase forms.  As a rule, if a character has no
     * true titlecase equivalent but does have an uppercase mapping, then the
     * Unicode 1.1.5 attribute table specifies a titlecase mapping that is the
     * same as the uppercase mapping.
     *
     * 
     * @param ch	the character to be converted
     * @return 	the titlecase equivalent of the character, if any;
     *		otherwise the character itself.
     * @see java.lang.Character#isTitleCase
     * @see java.lang.Character#toUpperCase
     * @see java.lang.Character#toLowerCase
     */

    public static char toTitleCase(char ch) {
	if ((ch & 0xFFC0) != 0x01C0)
	    return toUpperCase(ch);
	if (ch >= '\u01C4' && ch <= '\u01C6') return '\u01C5';		// DZ WITH CARON
	if (ch >= '\u01C7' && ch <= '\u01C9') return '\u01C8';		// LJ
	if (ch >= '\u01CA' && ch <= '\u01CC') return '\u01CB';		// NJ
	if (ch >= '\u01F1' && ch <= '\u01F3') return '\u01F2';		// DZ
	return toUpperCase(ch);
    }

    /**
     * Returns the numeric value of the character digit using the specified
     * radix. If the radix is not a valid radix, or the character is not a
     * valid digit in the specified radix, -1 is returned..
     * <p>
     * A radix is valid if its value is not less than Character.MIN_RADIX
     * and not greater than Character.MAX_RADIX.
     *
     * A character is a valid digit iff one of the following is true:
     * <ul>
     * <li>  The method isDigit is true of the character and the Unicode 1.1.5
     *       decimal digit value of the character (or its single-character
     *       decomposition) is less than the specified radix.
     * <li>  The character is an uppercase or lowercase Latin letter and the
     *       position of the letter in the Latin alphabet, plus 9, is less than
     *       the specified radix.
     * </ul>
     *
     *
     * @param ch		the character to be converted
     * @param radix 	the radix
     * @see java.lang.Character#isDigit
     * @see java.lang.Character#forDigit
     */

    public static int digit(char ch, int radix) {
        int value = -1;
	if (radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX) {
	    if ((ch >= '0') && (ch <= '9'))
		value = ch - '0';
	    else if ((ch >= 'A') && (ch <= 'Z'))
		value = ch + (10 - 'A');
	    else if ((ch >= 'a') && (ch <= 'z'))
		value = ch + (10 - 'a');
	    else {
		switch (ch>>8) {
		  case 0x06:
		    if ((ch >= 0x0660) && (ch <=0x0669))	// Arabic-Indic
			value = ch - 0x0660;
		    else
		    if ((ch >= 0x06F0) && (ch <=0x06F9))	// Extended Arabic-Indic
			value = ch - 0x06F0;
		    break;
		  case 0x09:
		    if ((ch >= 0x0966) && (ch <=0x096F))	// Devanagari
			value = ch - 0x0966;
		    else
		    if ((ch >= 0x09E6) && (ch <=0x09EF))	// Bengali
			value = ch - 0x09E6;
		    break;
		  case 0x0A:
		    if ((ch >= 0x0A66) && (ch <=0x0A6F))	// Gurmukhi
			value = ch - 0x0A66;
		    else
		    if ((ch >= 0x0AE6) && (ch <=0x0AEF))	// Gujarati
			value = ch - 0x0AE6;
		    break;
		  case 0x0B:
		    if ((ch >= 0x0B66) && (ch <=0x0B6F))	// Oriya
			value = ch - 0x0B66;
		    else
		    if ((ch >= 0x0BE7) && (ch <=0x0BEF))	// Tamil
			value = ch - (0x0BE7 - 1);		// First Tamil digit has value 1, not 0
		    break;
		  case 0x0C:
		    if ((ch >= 0x0C66) && (ch <=0x0C6F))	// Telugu
			value = ch - 0x0C66;
		    else
		    if ((ch >= 0x0CE6) && (ch <=0x0CEF))	// Kannada
			value = ch - 0x0CE6;
		    break;
		  case 0x0D:
		    if ((ch >= 0x0D66) && (ch <=0x0D6F))	// Malayalam
			value = ch - 0x0D66;
		    break;
		  case 0x0E:
		    if ((ch >= 0x0E50) && (ch <=0x0E59))	// Thai
			value = ch - 0x0E50;
		    else
		    if ((ch >= 0x0ED0) && (ch <=0x0ED9))	// Lao
			value = ch - 0x0ED0;
		    break;
		  // Unicode 1.0 Tibetan script was withdrawn in Unicode 1.1 for further study
		  case 0xFF:
		    if ((ch >= 0xFF10) && (ch <=0xFF19))	// Fullwidth digits
			value = ch - 0xFF10;
		    break;
		}
	    }
	}
	return (value < radix) ? value : -1;
    }

    /**
     * Determines if the specified character is ISO-LATIN-1 white space according to Java.
     * @param ch		the character to be tested
     * @return  true if the character is white space; false otherwise.
     */
    public static boolean isSpace(char ch) {
	switch (ch) {
	case ' ':
	case '\t':
	case '\f': // form feed
	case '\n':
	case '\r':
		return (true);
	}
	return (false);
    }


    /**
     * Returns the character value for the specified digit in the specified
     * radix. If the digit is not valid in the radix, the 0 character
     * is returned.
     * @param digit	the digit chosen by the character value
     * @param radix 	the radix containing the digit
     */
    public static char forDigit(int digit, int radix) {
	if ((digit >= radix) || (digit < 0)) {
	    return '\0';
	}
	if ((radix < MIN_RADIX) || (radix > MAX_RADIX)) {
	    return '\0';
	}
	if (digit < 10) {
	    return (char)('0' + digit);
	} 
	return (char)('a' + digit - 10);
    }

    /**
     * The value of the Character.
     */
    private char value;

    /**
     * Constructs a Character object with the specified value.
     * @param value value of this Character object
     */
    public Character(char value) {
	this.value = value;
    }

    /**
     * Returns the value of this Character object.
     */
    public char charValue() {
	return value;
    }

    /**
     * Returns a hashcode for this Character.
     */
    public int hashCode() {
	return (int)value;
    }

    /**
     * Compares this object against the specified object.
     * @param obj		the object to compare with
     * @return 		true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Character)) {
	    return value == ((Character)obj).charValue();
	} 
	return false;
    }

    /**
     * Returns a String object representing this character's value.
     */
    public String toString() {
	char buf[] = {value};
	return String.valueOf(buf);
    }
}

