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

import com.toennies.ci1429.app.util.binary.model.BDynLengthField;
import com.toennies.ci1429.app.util.binary.model.BField;
import com.toennies.ci1429.app.util.binary.model.BModel;
import com.toennies.ci1429.app.util.binary.writer.BWriter;

/**
 * @author renkenh
 *
 */
public class BPojoWriter<TYPE>
{

	private static final Logger logger = LogManager.getLogger();


	private final BModel model;
	private final Class<? extends TYPE> clazz;


	/**
	 * 
	 */
	public BPojoWriter(Class<? extends TYPE> clazz)
	{
		this.model = BAtParser.parse(clazz);
		this.clazz = clazz;
	}

	
	public byte[] write(TYPE pojo) throws IOException
	{
		BWriter writer = BWriter.createFor(this.model);
		for (BField field : this.model.getFields())
		{
			if (field instanceof BDynLengthField)
				continue;
			try
			{
				Method m = this.clazz.getMethod("get"+WordUtils.capitalize(field.id));
				writer.setValue(field.id, m.invoke(pojo));
			}
			catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				logger.error("Could not invoke method {} with parameter {} on type {}", "get"+WordUtils.capitalize(field.id), field.type, this.clazz);
				throw new RuntimeException("Could not write buffer.", e);
			}
		}
		return writer.compile();
	}

}
