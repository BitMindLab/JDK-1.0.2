/*
 * @(#)ClassLoader.java	1.30 96/03/27 Arthur van Hoff
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

import java.io.InputStream;

/**
 * ClassLoader is an abstract Class that can be used to define a policy
 * for loading Java classes into the runtime environment. By default,
 * the runtime system loads classes that originate as files by reading 
 * them from the directory defined by the <tt>CLASSPATH</tt> environment
 * variable (this is platform dependent). The default mechanism does not involve
 * a Class loader. <p>
 *
 * However, some classes may not originate from a file; they could be
 * loaded from some other source, e.g., the network. Classes loaded
 * from the network are an array of bytes. A ClassLoader can be used to
 * tell the runtime system to convert an array of bytes into an instance
 * of class Class.
 * This conversion information is passed to the runtime using the defineClass()
 * method.<p>
 *
 * Classes that are created through the defineClass() mechanism can 
 * reference other classes by name. To resolve those names, the runtime
 * system calls the ClassLoader that originally created the Class.
 * The runtime system calls the abstract method loadClass() to load
 * the referenced classes.<p>
 * <pre>
 * 	ClassLoader loader = new NetworkClassLoader(host, port);
 *  	Object main = loader.loadClass("Main").newInstance();
 *	....
 * </pre>
 *
 * The NetworkClassLoader subclass must define the method loadClass() to 
 * load a Class from the network. Once it has downloaded the bytes
 * that make up the Class it should use the method defineClass() to create a Class
 * instance. A sample implementation could be:
 * <pre>
 *	class NetworkClassLoader {
 *	    String host;
 *	    int port;
 *	    Hashtable cache = new Hashtable();
 *
 *	    private byte loadClassData(String name)[] {
 *		// load the class data from the connection
 *		...
 *	    }
 *
 *	    public synchronized Class loadClass(String name) {
 *	        Class c = cache.get(name);
 *		if (c == null) {
 *		    byte data[] = loadClassData(name);
 *		    cache.put(name, defineClass(data, 0, data.length));
 *		}
 *		return c;
 *	    }
 *	}
 * </pre>
 * @see		Class
 * @version 	1.30, 27 Mar 1996
 * @author	Arthur van Hoff
 */

public abstract class ClassLoader {

    /**
     * If initialization succeed this is set to true and security checks will
     * succeed. Otherwise the object is not initialized and the object is
     * useless.
     */
    private boolean initialized = false;

    /**
     * Constructs a new Class loader and initializes it.
     */
    protected ClassLoader() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkCreateClassLoader();
	}
	init();
	initialized = true;
    }

    /**
     * Resolves the specified name to a Class. The method loadClass() is 
     * called by the virtual machine.
     * As an abstract method, loadClass() must be defined in a subclass of 
     * ClassLoader. By using a Hashtable, you can avoid loading the same 
     * Class more than once. 
     * @param	name	the name of the desired Class
     * @param resolve true if the Class needs to be resolved
     * @return		the resulting Class, or null if it was not found.
     * @exception ClassNotFoundException 
     *                         Cannot find a definition for the class
     * @see		java.util.Hashtable
     */
    protected abstract Class loadClass(String name, boolean resolve) throws ClassNotFoundException;

    /**
     * Converts an array of bytes to an instance of class Class. Before the
     * Class can be used it must be resolved.
     * @param	data	the bytes that make up the Class
     * @param	offset	the start offset of the Class data
     * @param	length	the length of the Class data
     * @return		the Class object which was created from the data.
     * @exception ClassFormatError If the data does not contain a valid 
     * Class.
     * @see 		ClassLoader#loadClass
     * @see 		ClassLoader#resolveClass
     */
    protected final Class defineClass(byte data[], int offset, int length) { 
	check();
	return defineClass0(data, offset, length);
    }

    /**
     * Resolves classes referenced by this Class. This must be done before the
     * Class can be used. Class names referenced by the resulting Class are
     * resolved by calling loadClass().
     * @param	c	the Class to be resolved
     * @see 		ClassLoader#defineClass
     */
    protected final void resolveClass(Class c) { 
	check();
	resolveClass0(c);
    }

    /**
     * Loads a system Class. A system Class is a class with the
     * primordial Class loader (which is null).
     * @param name the name of the system Class
     * @exception NoClassDefFoundError If the Class is not found.
     * @exception ClassNotFoundException 
     *                         Cannot find a definition for the class
     */
    protected final Class findSystemClass(String name) 
    throws ClassNotFoundException {
	check();
	return findSystemClass0(name);
    }


    /**
     * Initializes the Class loader.
     */
    private native void init();
    private native Class defineClass0(byte data[], int offset, int length);
    private native void resolveClass0(Class c);
    private native Class findSystemClass0(String name) 
        throws ClassNotFoundException;

    private void check() { 
	if (initialized == true) 
	    return;
	throw new SecurityException("Security Exception: ClassLoader object not initialized.");
    }
}

