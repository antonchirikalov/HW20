/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
/**
 * Annotation used to by the sps framework to specify all commands that a sps supports.
 * @author renkenh
 */
public @interface AtSpsCommand
{

	/**
	 * The unique id/name of the command. This name is used in the API.
	 * @return The unique name of the command.
	 */
	public String name();
	
	/**
	 * Returns the type of the expected payload. Default to no payload {@link Void#getClass()}.
	 * @return The type of the expected payload. References {@link Void} if no payload is expected.
	 */
	public Class<?> payload() default Void.class;

}
