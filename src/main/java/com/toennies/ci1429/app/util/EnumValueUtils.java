package com.toennies.ci1429.app.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.model.ResponseFormat;

/**
 * Use this helper class in order to get information about enum values. Problem
 * here is, if you have just the (canonical) name of a class, there is not
 * "easy" way to get list of all enum values. Means, you can't invoke the
 * values() method easily. This values() method is present in every enum class.
 * See {@link ResponseFormat#values()}.
 * 
 * Because of this fact, we need this helper class. See javadoc of these
 * methods.
 */
public class EnumValueUtils
{

	private static final Logger logger = LogManager.getLogger();

	/**
	 * This string contains the method name of values method in enum classes.
	 * This method returns all possible values of an enum object.
	 */
	private static final String VALUES_METHOD_NAME = "values";

	private EnumValueUtils()
	{
		// no public instance; just static methods.
	}

	/**
	 * Invoke this method in order to get a collection of enum value strings of
	 * a given enum class. Example for {@link ResponseFormat} enum.
	 * 
	 * <pre>
	 * Collection: {"RAW", "STRING", "EAN128", "HUMAN"}
	 * </pre>
	 * 
	 * @param className
	 *            canonical name (-> {@link Class#getCanonicalName()}) of the
	 *            enum class you want to get enum value strings of.
	 * @return a collection containing enum values as {@link String}s or an
	 *         empty collection, if given class is no enum. Also returns an
	 *         empty collection if error(s) occour.
	 */
	public static List<String> getEnumValueStringsByClassName(String className)
	{
		return getEnumValuesByClasssName(className).stream().map(e -> e.toString()).collect(Collectors.toList());
	}

	/**
	 * Invoke this method in order to get a collection of enum values of a given
	 * enum class. Example for {@link ResponseFormat} enum.
	 * 
	 * <pre>
	 * Collection: {RAW, STRING, EAN128, HUMAN}
	 * </pre>
	 * 
	 * @param className
	 *            canonical name (-> {@link Class#getCanonicalName()}) of the
	 *            enum class you want to get enum values of.
	 * @return a collection containing enum values or an empty collection, if
	 *         given class is no enum. Also returns an empty collection if
	 *         error(s) occour.
	 */
	public static Collection<Object> getEnumValuesByClasssName(String className)
	{
		try
		{
			Class<?> mayEnumClass = Class.forName(className);
			Method m = mayEnumClass.getMethod(VALUES_METHOD_NAME);
			Object ret = m.invoke(null);
			Object[] enums = (Object[]) ret;
			return Arrays.asList(enums);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | ClassCastException | ClassNotFoundException e)
		{
			logger.warn("Error during read of enum values for class: {}. Returning an empty list.", className, e);
			return Collections.emptyList();
		}
	}

}
