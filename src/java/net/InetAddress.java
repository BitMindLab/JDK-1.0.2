/*
 * @(#)InetAddress.java	1.28 96/04/29 Jonathan Payne
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

package java.net;

import java.util.Hashtable;

/**
 * A class that represents Internet addresses.
 *
 * @version     1.28, 29 Apr 1996
 * @author 	Jonathan Payne
 * @author 	Arthur van Hoff
 * @author 	Chris Warth
 */
public final 
class InetAddress {
    private static boolean inCheck = false;
    String hostName;
    int address;    // Currently we only deal effectively with 32-bit addresses. 
		    // However this field can be expanded to be a byte array 
		    // or a 64-bit quantity without too much effort.
    int family;

    /*
     * Load net library into runtime.
     */
    static {
	System.loadLibrary("net");
    }

    /** 
     * Constructor for the Socket.accept() method.
     * This creates an empty InetAddress, which is filled in by
     * the accept() method.  This InetAddress, however, is not
     * put in the address cache, since it is not created by name.
     */
    InetAddress() {}

    /**
     * Creates an InetAddress with the specified host name and IP address.
     * @param hostName the specified host name
     * @param addr the specified IP address.  The address is expected in 
     *	      network byte order.
     * @exception UnknownHostException If the address is unknown.
     */
    InetAddress(String hostName, byte addr[]) {
	this.hostName = new String(hostName);
	this.family = getInetFamily();
	/*
	 * We must be careful here to maintain the network byte
	 * order of the address.  As it comes in, the most
	 * significant byte of the address is in addr[0].  It
	 * actually doesn't matter what order they end up in the
	 * array, as long as it is documented and consistent.
	 */
	address  = addr[3] & 0xFF;
	address |= ((addr[2] << 8) & 0xFF00);
	address |= ((addr[1] << 16) & 0xFF0000);
	address |= ((addr[0] << 24) & 0xFF000000);
    }

    /**
     * Gets the hostname for this address; also the key in the 
     * hashtable.
     * If the host is equal to null, then this address refers to any
     * of the local machine's available network addresses.
     */
    public String getHostName() {
	if (hostName == null) {
	    try {
		hostName = getHostByAddr(address);
	    } catch (UnknownHostException e) {
		hostName = getHostAddress();
	    }
	}

	return hostName;
    }

    /**
     * Returns the raw IP address in network byte order.  The highest
     * order byte position is in addr[0]. To be prepared for 64-bit
     * IP addresses n array of bytes is returned.

     * @return raw IP address in network byte order.
     */
    public byte[] getAddress() {	
	byte[] addr = new byte[4];

	addr[0] = (byte) ((address >>> 24) & 0xFF);
	addr[1] = (byte) ((address >>> 16) & 0xFF);
	addr[2] = (byte) ((address >>> 8) & 0xFF);
	addr[3] = (byte) (address & 0xFF);
	return addr;
    }

      /**
      * Returns the IP address string "%d.%d.%d.%d"
      * @return raw IP address in a string format
      */
     public String getHostAddress() {	
         return ((address >>> 24) & 0xFF) + "." +
                ((address >>> 16) & 0xFF) + "." +
                ((address >>>  8) & 0xFF) + "." +
                ((address >>>  0) & 0xFF);
     }
 

    /**
     * Returns a hashcode for this InetAddress.
     */
    public int hashCode() {
	return address;
    }

    /**
     * Compares this object against the specified object.
     * @param obj the object to compare against.
     * @return true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	return (obj != null) && (obj instanceof InetAddress) &&
	    (((InetAddress)obj).address == address);
    }

    /**
     * Converts the InetAddress to a String.
     */
    public String toString() {
	return ((hostName != null) ? hostName + "/" : "") +
	getHostAddress();
    }

    /* 
     * Cached addresses - our own litle nis, not! 
     *
     * Do not purge cache of numerical IP addresses, since 
     * duplicate dynamic DNS name lookups can leave the system
     * vulnerable to hostname spoofing attacks.  Once a hostname
     * has been looked up in DNS and entered into the Java cache, 
     * from then on, the hostname is translated to IP address only
     * via the cache.
     */
    static Hashtable	    addressCache = new Hashtable();
    static InetAddress	    unknownAddress;
    static InetAddress	    anyLocalAddress;
    static InetAddress      localHost;
    /* 
     * generic localHost to give back to applets 
     * - private so not API delta
     */
    private static InetAddress      loopbackHost;

    static {
	unknownAddress = new InetAddress();
	anyLocalAddress = new InetAddress();
	makeAnyLocalAddress(anyLocalAddress);
	byte[] IP = new byte[4];
	IP[0] = 0x7F;
	IP[1] = 0x00;
	IP[2] = 0x00;
	IP[3] = 0x01;
	loopbackHost = new InetAddress("localhost", IP);
	
	try {
	    localHost = getByName(getLocalHostName());
	} catch (Exception ex) {
	    localHost = unknownAddress;
	}

	/* cache the name/address pair "0.0.0.0"/0.0.0.0 */
	String unknownByAddr = new String("0.0.0.0");
	InetAddress[] unknown_array = new InetAddress[1];
	unknown_array[0] = new InetAddress(unknownByAddr, unknownAddress.getAddress());
	addressCache.put(unknownByAddr, unknown_array);
    }

    /**
     * Returns a network address for the indicated host.  A host name
     * of null refers to default address for the local machine.  A local
     * cache is used to speed access to addresses.  If all
     * addresses for a host are needed, use the getAllByName() method.
     * @param host the specified host
     * @exception UnknownHostException If the address is unknown.
     */
    public static synchronized InetAddress getByName(String host)
	throws UnknownHostException
    {
	if (host == null || host.length() == 0) {
	    return loopbackHost;
	}
	return getAllByName(host)[0];
   }

    /** 
     * Given a hostname, returns an array of all the corresponding InetAddresses.  
     * @exception UnknownHostException If the host name could not be resolved
     */
    public static synchronized InetAddress getAllByName(String host)[]
	throws UnknownHostException
    {

	Object objcopy = null;

	if (host == null) {
	    throw new UnknownHostException(host);
	}

	/* make sure the connection to the host is allowed, before we
	   create the InetAddress */
	SecurityManager security = System.getSecurityManager();
	if (security != null && !security.getInCheck()) {
	    security.checkConnect(host, -1);
	}

       /* Cache.get can return: null, unknownAddress, or InetAddress[] */
	Object obj = addressCache.get(host);

//	  if ((obj != null) && (obj != unknownAddress))
//	    for (int i = 0; i < ((InetAddress [])obj).length; i++) {
//		System.out.println("GetAllByName(" + host + ")  = " + ((InetAddress [])obj)[i]);
//    }

	/* If no entry in cache, then do the host lookup */
	if (obj == null) {
	    try {
		/*
		 * Do not put the call to lookup() inside the
		 * constructor.  if you do you will still be
		 * allocating space when the lookup fails.
		 */
		byte[][] byte_array = lookupAllHostAddr(host);
		InetAddress[] addr_array = new InetAddress[byte_array.length];

		for (int i = 0; i < byte_array.length; i++) {
		    byte addr[] = byte_array[i];
		    addr_array[i] = new InetAddress(host, addr);
		    /*
		     * For each address pair like foo.com/1.1.1.1,
		     * add "1.1.1.1"/1.1.1.1 to the cache, as well
		     */
	            InetAddress[] hostByAddr_array = new InetAddress[1];
		    String hostByAddr = new String(addr_array[i].getHostAddress());
		    hostByAddr_array[0] = new InetAddress(hostByAddr, addr);
		    addressCache.put(hostByAddr, hostByAddr_array);
		}
		obj = addr_array;
	    } catch (UnknownHostException e) {
		obj  = unknownAddress;
	    }
	    addressCache.put(host, obj);
	}

	if (obj == unknownAddress) {
	    /*
	     * We currently cache the fact that a host is unknown.
	     */
	    throw new UnknownHostException(host);
	}

	/* Make a copy of the InetAddress array */
	try {
	      objcopy = ((InetAddress [])obj).clone();
	} catch (CloneNotSupportedException cnse) {
	      cnse.printStackTrace();
	}

	return (InetAddress [])objcopy;
    }

    /**
     * Returns the local host.
     * @exception UnknownHostException If the host name could not be resolved
     */
    public static InetAddress getLocalHost() throws UnknownHostException {
        if (localHost.equals(unknownAddress)) {
	    throw new UnknownHostException();
	}
        /* make sure the connection to the host is allowed: if yes,
	 * return the "real" localHost; if not, return loopback "127.0.0.1"   
	 */
	SecurityManager security = System.getSecurityManager();
	try {
	    if (security != null && !security.getInCheck()) 
		security.checkConnect(localHost.getHostName(), -1);
	} catch (java.lang.SecurityException e) { 
	    return loopbackHost;
	}
	return localHost;
    }

    private static native String getLocalHostName() throws UnknownHostException;
    private static native void makeAnyLocalAddress(InetAddress addr);
    private static native byte[] lookupHostAddr(String hostname) throws UnknownHostException;
    private static native byte[][]
        lookupAllHostAddr(String hostname) throws UnknownHostException;
    private static native String getHostByAddr(int addr) throws UnknownHostException;
    private static native int getInetFamily();
}

