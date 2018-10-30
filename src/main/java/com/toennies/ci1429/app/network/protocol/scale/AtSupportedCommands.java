/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.toennies.ci1429.app.model.scale.Commands.Command;


/**
 * Annotation used by scale protocols to provide a list of supported commands.
 * @author renkenh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AtSupportedCommands
{
	/** The commands supported by the annotated scale protocol. */
	public AtCommand[] value();
	
	/**
	 * Command definition.
	 * @author renkenh
	 */
	@Repeatable(AtSupportedCommands.class)
	public @interface AtCommand
	{
		/** Command reference. */
		public Command value();
		
	}

}

