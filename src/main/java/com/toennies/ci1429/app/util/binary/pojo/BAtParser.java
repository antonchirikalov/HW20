/**
 * 
 */
package com.toennies.ci1429.app.util.binary.pojo;

import java.lang.reflect.Field;

import com.toennies.ci1429.app.util.binary.model.BModel;
import com.toennies.ci1429.app.util.binary.model.BModelBuilder;
import com.toennies.ci1429.app.util.binary.model.BField.BType;

/**
 * Parser for Pojo-objects with annotations. The parser creates a BModel from the annotations found in the classes - in the order the fields where found.
 * The parser does search for annotations in the super classes also. Fields from the super classes are added to the BModel first (preorder travel on a cripled tree). 
 * @author renkenh
 */
class BAtParser
{
	
	private static final BType[] JAVA_PRIMITIVES = { BType.BYTE, BType.CHAR, BType.DOUBLE, BType.FLOAT, BType.INT, BType.LONG, BType.SHORT };

	/**
	 * Method to parse the given class for fields.
	 * @param clazz The clazz to parse.
	 * @return A BModel containing all fields found.
	 */
	public static final BModel parse(Class<?> clazz)
	{
		BModelBuilder builder = BModelBuilder.start();
		loadFields(clazz, builder);
		return builder.compile();
	}

	private static final void loadFields(Class<?> clazz, BModelBuilder builder)
	{
		if (clazz.equals(Object.class))
			return;
		loadFields(clazz.getSuperclass(), builder);
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields)
		{
			AtBField ann = field.getAnnotation(AtBField.class);
			if (ann != null)
			{
				builder.addPrimtive(map(field.getType()), field.getName());
				continue;
			}
			AtTypedBField tann = field.getAnnotation(AtTypedBField.class);
			if (tann != null)
			{
				builder.addPrimtive(tann.value(), field.getName());
				continue;
			}
			AtFixedBField vann = field.getAnnotation(AtFixedBField.class);
			if (vann != null && field.getType().equals(BType.STRING_FIXED.clazz))
			{
				builder.addFixedString(field.getName(), vann.value());
				continue;
			}
			if (vann != null && field.getType().equals(BType.RAW_FIXED.clazz))
			{
				builder.addFixedArray(field.getName(), vann.value());
				continue;
			}
			AtDynBField dann = field.getAnnotation(AtDynBField.class);
			if (dann != null && field.getType().equals(BType.STRING_DYNAMIC.clazz))
			{
				builder.addPrimtive(dann.value(), field.getName()+"_length");
				builder.addDynamicString(field.getName(), field.getName()+"_length");
				continue;
			}
			if (dann != null && field.getType().equals(BType.RAW_DYNAMIC.clazz))
			{
				builder.addPrimtive(dann.value(), field.getName()+"_length");
				builder.addDynamicArray(field.getName(), field.getName()+"_length");
				continue;
			}
			AtCustomBField cann = field.getAnnotation(AtCustomBField.class);
			if (cann != null)
			{
				builder.addCustomField(map(field.getType()), field.getName(), cann.reader(), cann.writer());
				continue;
			}
		}
	}
	
	private static final BType map(Class<?> clazz)
	{
		for (BType type : JAVA_PRIMITIVES)
			if (type.clazz.equals(clazz))
				return type;
		return null;
	}
	
	private BAtParser()
	{
		//no instance
	}
}
