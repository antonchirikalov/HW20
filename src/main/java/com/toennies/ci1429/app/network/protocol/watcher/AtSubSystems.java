/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used to specify on an {@link AWatcherProtocol} which subsystems are available for state tracking.
 * @author renkenh
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface AtSubSystems
{

	public AtSubSystem[] value();

	/**
	 * System definition.
	 * @author renkenh
	 */
	@Repeatable(AtSubSystems.class)
	public @interface AtSubSystem
	{
		public String value();
	}
}
