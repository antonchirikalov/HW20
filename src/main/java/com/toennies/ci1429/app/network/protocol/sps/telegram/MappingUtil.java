/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps.telegram;

import com.toennies.ci1429.app.network.protocol.sps.telegram.TelegramDeSerializer.TelegramPojoMapping;

/**
 * Simple utility class to handle the mapping annotations.
 * @author renkenh
 */
public class MappingUtil
{

	/**
	 * Returns the specified mapping (specified by {@link AtTelegramMapping}) for a pojo class.
	 * @param pojoClass The class to read.
	 * @return The mapping if found. <code>null</code> otherwise.
	 */
	public static final TelegramPojoMapping getMapping(Class<? extends Telegram> pojoClass)
	{
		AtTelegramMapping mapping = pojoClass.getDeclaredAnnotation(AtTelegramMapping.class);
		if (mapping == null)
			return null;
		return new TelegramPojoMapping(mapping.id(), mapping.dialog(), pojoClass);
	}
	

	private MappingUtil()
	{
		//no instance
	}

}
