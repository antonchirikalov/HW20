/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps.telegram;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
/**
 * Used as an annotation for a pojo class to specify the Tönnies unique identifier for it.
 * The unique identifier consists of a process id - which identifies the process to be executed (like status-process, weighting-process, store-process, etc.)
 * and an id within the process {@link AtTelegramMapping#id()}, which telegram it is (the {@link AtTelegramMapping#dialog()}) number. 
 * @author renkenh
 */
public @interface AtTelegramMapping
{

	/** The process id. */
	public int id();
	
	/** The unique id of the telegram within the process. */
	public int dialog();
}
