/**
 * 
 */
package com.toennies.ci1429.app.network.socket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to indicate a socket for network stack.
 * Should only be used to annotate {@link ISocket}s.
 * @author renkenh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AtSocket
{

	/**
	 * @return The description of the socket. The description will be shown to the end user.
	 */
	public String value();
	
}
