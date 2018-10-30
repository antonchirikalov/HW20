/**
 * 
 */
package com.toennies.ci1429.app.network.protocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.toennies.ci1429.app.model.DeviceType;


/**
 * Annotation to mark a protocol implementation. Only {@link IProtocol}s should be annotated.
 * @author renkenh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AtProtocol
{

	/**
	 * The description of the protocol. The description will be shown to the end user.
	 * @return The description.
	 */
	public String value();

	/**
	 * This attribute contains device types which are being supported by marked
	 * Protocols
	 */
	public DeviceType deviceType();
	
}
