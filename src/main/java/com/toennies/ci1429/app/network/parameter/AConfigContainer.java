/**
 * 
 */
package com.toennies.ci1429.app.network.parameter;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import com.toennies.ci1574.lib.helper.Generics;


/**
 * Abstract implementation of the {@link IConfigContainer} interface.
 * @author renkenh
 */
public abstract class AConfigContainer implements IConfigContainer
{
	@Override
	public final boolean hasEntry(String key)
	{
		return this._config().containsKey(key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getEntry(String key)
	{
		String value = this._config().get(key);
		if (value == null)
			throw new NoSuchElementException("Parameter "+key+" is not set.");
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getIntEntry(String key)
	{
		String value = this.getEntry(key);
		return Integer.parseInt(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final float getFloatEntry(String key)
	{
		String value = this.getEntry(key);
		return Float.parseFloat(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean getBooleanEntry(String key)
	{
		String value = this.getEntry(key);
		return Boolean.parseBoolean(value);
	}

	@Override
	public <E extends Enum<E>> E getEnumEntry(String key, Class<? extends Enum<E>> enumClass)
	{
		String value = this.getEntry(key);	//FIXME do this with EnumUtil class when available
		try
		{
			Object values = enumClass.getMethod("values").invoke(null);
			Object[] arr = (Object[]) values;
			for (Object obj : arr)
			{
				if (String.valueOf(obj).equalsIgnoreCase(value.toLowerCase()))
					return Generics.convertUnchecked(obj);
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			//do nothing - throw exception 
		}
		throw new NoSuchElementException("Parameter "+key+" does not reference an enum.");
	}

	/** Implement this method to allow to access the data. This must be thread save!
	 * The returned map should be immutable. */
	protected abstract Map<String, String> _config();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getConfig()
	{
		return Collections.unmodifiableMap(this._config());
	}

}
