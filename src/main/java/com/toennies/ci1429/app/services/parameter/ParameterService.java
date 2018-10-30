package com.toennies.ci1429.app.services.parameter;

import java.util.Collection;
import java.util.stream.Collectors;

import com.toennies.ci1406.lib.utils.AnnotationUtils;
import com.toennies.ci1429.app.network.parameter.ParameterInstance;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.socket.AtSocket;

/**
 * There are two annotations marking designated classes. These annotations are
 * {@link AtSocket} and {@link AtProtocol}. This class provides access to
 * classes that are marked by these two annotations.
 */
public class ParameterService
{

	/**
	 * Every class that is marked by {@link AtSocket} annotation is present in
	 * this collection.
	 */
	private static Collection<Class<?>> socketClasses;

	/**
	 * Every class that is marked by {@link AtProtocol} annotation is present in
	 * this collection.
	 */
	private static Collection<Class<?>> protocolClasses;

	static
	{
		socketClasses = AnnotationUtils.returnClassesMarkedByAnnotation(AtSocket.class);
		protocolClasses = AnnotationUtils.returnClassesMarkedByAnnotation(AtProtocol.class);
	}

	/**
	 * @return class objects of clsses that are marked by {@link AtSocket}
	 *         annotation.
	 */
	public static Collection<Class<?>> getSocketClasses()
	{
		return socketClasses;
	}

	/**
	 * Wraps classes marked by {@link AtSocket} annotation in
	 * {@link ParameterInstance} objects and returns them.
	 */
	public static Collection<ParameterInstance> getSocketParameterInstances()
	{
		return socketClasses.stream().map(s -> new ParameterInstance(s)).collect(Collectors.toList());
	}

	/**
	 * Searches for a class marked by {@link AtSocket} with given String.
	 */
	public static ParameterInstance getSocketClassByName(String searchedSocket)
	{
		return getParameterInstanceByClassName(socketClasses, searchedSocket);
	}

	/**
	 * @return class objects of clsses that are marked by {@link AtProtocol}
	 *         annotation.
	 */
	public static Collection<Class<?>> getProtocolClasses()
	{
		return protocolClasses;
	}

	/**
	 * Wraps classes marked by {@link AtProtocol} annotation in
	 * {@link ParameterInstance} objects and returns them.
	 */
	public static Collection<ParameterInstance> getProtocolParameterInstances()
	{
		return protocolClasses.stream().map(s -> new ParameterInstance(s)).collect(Collectors.toList());
	}

	/**
	 * Searches for a class marked by {@link AtProtocol} with given String.
	 */
	public static ParameterInstance getProtocolClassByName(String searchedProtocol)
	{
		return getParameterInstanceByClassName(protocolClasses, searchedProtocol);
	}

	/**
	 * Helper method in order to search for a class in given collection.
	 * 
	 * @param classes
	 *            in this collection a class is searched for.
	 * @param searchedClass
	 * @return the may found class wrapped by {@link ParameterInstance} or null
	 *         if no class was found.
	 */
	private static ParameterInstance getParameterInstanceByClassName(Collection<Class<?>> classes,
			final String searchedClass)
	{
		Class<?> foundClass = classes.stream().filter(c -> c.getCanonicalName().equalsIgnoreCase(searchedClass))
				.findFirst().orElse(null);
		if (foundClass == null)
		{
			return null;
		}
		return new ParameterInstance(foundClass);
	}

}
