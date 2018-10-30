/**
 * 
 */
package com.toennies.ci1429.app.network.parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1406.lib.utils.AnnotationUtils;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;

/**
 * Utils class to load {@link AtDefaultParameters.Parameter} annotations from a
 * given class. These annotations are converted to {@link ParamDescriptor}
 * instances.
 * 
 * @author renkenh
 */
public class Parameters
{

	private static final Logger logger = LogManager.getLogger();

	/**
	 * Already found {@link AtDefaultParameters.Parameter} are stored in this
	 * map. Through this simple cache, parameter search needs to be performed
	 * only once. Every further search for a already performed parameter search
	 * can be took from this map.
	 */
	private static final Map<String, Map<String, ParamDescriptor>> parameterCache = new HashMap<>();

	public static final ParamDescriptor getParameter(String name, String className)
	{
		Map<String, ParamDescriptor> descriptors = getParameters(className);
		return descriptors.get(name);
	}

	/**
	 * Invokes {@link #getParameters(Class)}. But before that, {@link Class}
	 * object is created in order to obey method signature.
	 */
	public static Map<String, ParamDescriptor> getParameters(String className)
	{
		try
		{
			Class<?> clazz = Class.forName(className);
			return getParameters(clazz);
		}
		catch (ClassNotFoundException | ClassCastException e)
		{
			logger.warn("Could not get protocol class for given className {}.",className, e);
		}
		return Collections.emptyMap();
	}

	/**
	 * Builds a {@link Map} containing {@link ParamDescriptor} information.
	 * These information are stored {@link AtDefaultParameters} annotation(s).
	 * Search is not only performed for given {@link Class} object, but also on
	 * super classes of this given class. Super class parameter will *not*
	 * override annotation information defined in sub classes. Example!
	 * 
	 * If parameter search was already performed, parameter map is taken from
	 * {@link #parameterCache}.
	 * 
	 * <pre>
	 * A (Parameter x=1)
	 * B extends A (Parameter y=2),
	 * C exends B (Parameter x=0; Parameter z=3)
	 * 
	 * Map contains (x=0, y=2, z=3)
	 * </pre>
	 * 
	 * @param clazz
	 *            this class and it's super classes that are may annotated by
	 *            {@link AtDefaultParameters}.
	 * @return an empty {@link Map}, if given class and super classes are *not*
	 *         annotated with {@link AtDefaultParameters} annotation.
	 */
	public static synchronized Map<String, ParamDescriptor> getParameters(Class<?> clazz)
	{
		// First, have a look in cache
		Map<String, ParamDescriptor> mapFromCache = parameterCache.get(clazz.getCanonicalName());
		if (mapFromCache != null)
		{
			// Search for parameters was already performed. Return parameter map
			// from cache.
			return mapFromCache;
		}

		// For given class, no parameters were loaded yet. So next step is to
		// fill parameter map ...
		Map<String, ParamDescriptor> parameterMap = new HashMap<>();
		do
		{
			AtDefaultParameters parameterAnnotationMarkingClazz = AnnotationUtils.returnAnnotationOfClass(clazz,
					AtDefaultParameters.class);
			if (parameterAnnotationMarkingClazz != null)
			{
				for (Parameter p : parameterAnnotationMarkingClazz.value())
				{
					if (!AtDefaultParameters.DUMMY_NAME.equals(p.name()) && !parameterMap.containsKey(p.name()))
						parameterMap.put(p.name(), new ParamDescriptor(p.name(), p.value(), p.isRequired(),
								p.typeInformation(), p.toolTip()));
				}
			}
			clazz = clazz.getSuperclass();
		}
		while (!clazz.equals(Object.class));

		// ... in order to put found parameters in cache
		updateParameterCache(clazz, parameterMap);
		return parameterMap;
	}

	private static void updateParameterCache(Class<?> clazz, Map<String, ParamDescriptor> parameterMap)
	{
		parameterCache.put(clazz.getCanonicalName(), parameterMap);
	}

	private Parameters()
	{
		// no instance - only static
	}

}
