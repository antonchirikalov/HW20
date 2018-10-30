/**
 * 
 */
package com.toennies.ci1429.app.util.binary.pojo;

import java.lang.reflect.Method;

import com.toennies.ci1429.app.util.binary.model.BField.BType;

/**
 * Utility for pojo reader/writer especially for type conversion, e.g. byte -> Byte. 
 * @author renkenh
 */
class TypeUtil
{
	
	private enum ConvertibleType
	{
		CHAR(char.class, Character.class, byte.class, Byte.class),
		BYTE(byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class),
		SHORT(short.class, Short.class, int.class, Integer.class, long.class, Long.class),
		INT(int.class, Integer.class, long.class, Long.class),
		LONG(long.class, Long.class),
		FLOAT(float.class, Float.class, double.class, Double.class),
		DOUBLE(double.class, Double.class);
		
		public final Class<?>[] types;
		
		private ConvertibleType(Class<?>... types)
		{
			this.types = types;
		}
		
		public static final ConvertibleType findType(Class<?> clazz)
		{
			for (ConvertibleType type : ConvertibleType.values())
				if (type.types[0].equals(clazz))
					return type;
			return null;
		}
	}
	
	/**
	 * Searches for a method that takes a parameter that is compatible to {@link BType#clazz} from the given parameter.
	 * If no compatible method can be found, <code>null</code> is returned.
	 * @param methodName The method name to search for.
	 * @param type The type of the value, i.e. the type for which to find a compatible method.
	 * @param clazz The class in which to search. Super classes are also searched.
	 * @return The method instance or <code>null</code>.
	 */
	public static final Method findMethodByType(String methodName, BType type, Class<?> clazz)
	{
		Method m = findMethod(methodName, type.clazz, clazz);
		if (m != null)
			return m;
		ConvertibleType convertible = ConvertibleType.findType(type.clazz);
		if (convertible == null)
			return null;
		for (Class<?> param : convertible.types)
		{
			m = findMethod(methodName, param, clazz);
			if (m != null)
				return m;
		}
		return null;
	}

	private static final Method findMethod(String methodName, Class<?> paramType, Class<?> clazz)
	{
		try
		{
			return clazz.getMethod(methodName, paramType);
		}
		catch (NoSuchMethodException | SecurityException | IllegalArgumentException e)
		{
			return null;
		}
	}


	private TypeUtil()
	{
		//no instance
	}

}
