/**
 * 
 */
package com.toennies.ci1429.app.util;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.socket.AtSocket;
import com.toennies.ci1574.lib.helper.Generics;


/**
 * @author renkenh
 *
 */
public class Utils
{
	
	private static final Logger logger = LogManager.getLogger();

	
	public static <T> T instantiate(String classname, Object... parameters)
	{
		try
		{
			if (classname == null)
				return null;
			Class<?>[] clazzes = Arrays.stream(parameters).map(Object::getClass).toArray(Class[]::new);
			Object instance = Class.forName(classname).getConstructor(clazzes).newInstance(parameters);
			return Generics.convertUnchecked(instance);
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			logger.error("Exception during Instantiation of {} using Parameters {}.", classname, StringUtils.arrayToCommaDelimitedString(parameters), e);
			return null;
		}
	}
	
	public static final void close(Closeable close)
	{
		try
		{
			if (close != null)
				close.close();
		}
		catch (IOException e)
		{
			logger.debug("Could not close {} properly.", close, e);
		}
	}
	
	public static String getDescription(String className)
	{
		try
		{
			return getDescription(Generics.<Class<?>>convertUnchecked(Class.forName(className)));
		}
		catch (ClassNotFoundException | ClassCastException e)
		{
			logger.warn("Could not get protocol class.", e);
		}
		return className;
	}

	public static String getDescription(Class<?> clazz)
	{
		if (IProtocol.class.isAssignableFrom(clazz))
		{
			AtProtocol[] descs = clazz.getAnnotationsByType(AtProtocol.class);
			if (descs.length == 0)
				return clazz.getName();
			return descs[0].value();
		}
		AtSocket[] descs = clazz.getAnnotationsByType(AtSocket.class);
		if (descs.length == 0)
			return clazz.getName();
		return descs[0].value();
	}

	/**
	 * Creates the source by device id. Used by ADevice and DeviceEventsPanel to have a consistence value in getting event source.
	 *
	 * @param deviceId the device id
	 * @return the string
	 */
	public static String createSourceByDeviceId(int deviceId)
	{
		return "Device."+deviceId; 
	}
	
	private Utils()
	{
		//no instance
	}
	
}
