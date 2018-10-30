/**
 * 
 */
package com.toennies.ci1429.app.util.binary.pojo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.util.Utils;
import com.toennies.ci1429.app.util.binary.model.BDynLengthField;
import com.toennies.ci1429.app.util.binary.model.BField;
import com.toennies.ci1429.app.util.binary.model.BModel;
import com.toennies.ci1429.app.util.binary.reader.BReader;

/**
 * Simple reader that reads a byte array an composes a pojo from that. The pojo class must provide the usual setters - bean style. 
 * @author renkenh
 */
public class BPojoReader<TYPE>
{
	
	private static final Logger logger = LogManager.getLogger();


	private final BModel model;
	private final Class<TYPE> clazz;


	/**
	 * Creates a new reader for the given type. Can be reused.
	 * @param clazz The type.
	 */
	public BPojoReader(Class<TYPE> clazz)
	{
		this.model = BAtParser.parse(clazz);
		this.clazz = clazz;
	}

	
	/**
	 * Parses the given byte array according to the type of this reader. 
	 * @param arr The array to parse.
	 * @return A pojo with the values read from the given byte array.
	 * @throws IOException when something goes wrong during reading from the byte array.
	 */
	public TYPE parse(byte[] arr) throws IOException
	{
		BReader parser = BReader.createFor(this.model, arr);
		TYPE instance = Utils.instantiate(this.clazz.getName());
		if (instance == null)
			throw new RuntimeException("Could not instantiate Pojo.");
		for (BField field : this.model.getFields())
		{
			if (field instanceof BDynLengthField)
				continue;
			try
			{
				Method m = findMethodFor(field, this.clazz);
				m.invoke(instance, parser.<Object>getValue(field.id));
			}
			catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				logger.error("Could not invoke method {} with parameter {} on type {}", "set"+WordUtils.capitalize(field.id), field.type, this.clazz);
				throw new RuntimeException("Could not instantiate Pojo.", e);
			}
		}
		return instance;
	}
	
	private static final Method findMethodFor(BField field, Class<?> clazz)
	{
		final String methodName = "set"+WordUtils.capitalize(field.id);
		Method m = TypeUtil.findMethodByType(methodName, field.type, clazz);
		if (m != null)
			return m;
		throw new RuntimeException("Could not find appropriate method for " + field.id);
	}

}
