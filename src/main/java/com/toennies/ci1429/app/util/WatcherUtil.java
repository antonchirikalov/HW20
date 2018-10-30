package com.toennies.ci1429.app.util;

import java.util.LinkedHashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.network.protocol.watcher.AWatcherProtocol;
import com.toennies.ci1429.app.network.protocol.watcher.AtSubSystems;
import com.toennies.ci1429.app.network.protocol.watcher.AtSubSystems.AtSubSystem;

/**
 * Small utils type for the watcher device.
 * @author renkenh
 */
public class WatcherUtil
{

	private static final Logger LOGGER = LogManager.getLogger();

	private static final String[] EMPTY_ARRAY = new String[0];


	/**
	 * Tries to load the class specified by the given class name - If the name references an {@link AWatcherProtocol},
	 * specified {@link AtSubSystem}s are loaded.
	 * @param className The class name of an {@link AWatcherProtocol} implementation.
	 * @return A list of specified sub systems. If an error occurs, an empty array is returned.
	 */
	public static String[] getSubsystems(String className)
	{
		try
		{
			Class<?> clazz = Class.forName(className);
			return getSubsystems(clazz);
		}
		catch (ClassNotFoundException | ClassCastException e)
		{
			LOGGER.warn("Could not get protocol class.", e);
		}
		return EMPTY_ARRAY;
	}

	/**
	 * Tries to load the given class - If the class references an {@link AWatcherProtocol} implementation,
	 * specified {@link AtSubSystem}s are loaded.
	 * @param clazz The class of an {@link AWatcherProtocol} implementation.
	 * @return A list of specified sub systems. If an error occurs, an empty array is returned.
	 */
	public static String[] getSubsystems(Class<?> clazz)
	{
		if (!AWatcherProtocol.class.isAssignableFrom(clazz))
			return EMPTY_ARRAY;
		
		try
		{
			LinkedHashSet<String> subsystems = new LinkedHashSet<>();
			do
			{
				AtSubSystems.AtSubSystem[] descs = clazz.getAnnotationsByType(AtSubSystems.AtSubSystem.class);
				for (AtSubSystem atCMD : descs)
					subsystems.add(atCMD.value());
				
				clazz = clazz.getSuperclass();
			}
			while (!clazz.equals(Object.class));
			return subsystems.toArray(new String[subsystems.size()]);
		}
		catch (SecurityException | IllegalArgumentException ex)
		{
			LOGGER.warn("Could not gather available commands.", ex);
			return EMPTY_ARRAY;
		}
	}

	
	private WatcherUtil()
	{
		//no instance
	}

}
