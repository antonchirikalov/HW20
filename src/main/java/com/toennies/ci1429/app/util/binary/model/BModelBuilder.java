/**
 * 
 */
package com.toennies.ci1429.app.util.binary.model;

import java.util.LinkedHashMap;

import com.toennies.ci1429.app.util.binary.model.BField.BType;
import com.toennies.ci1429.app.util.binary.reader.ABFieldReader;
import com.toennies.ci1429.app.util.binary.writer.ABFieldWriter;

/**
 * Builder pattern to easily create a {@link BModel}.
 * @author renkenh
 */
public class BModelBuilder
{

	private final LinkedHashMap<String, BField> structure = new LinkedHashMap<>();


	private BModelBuilder()
	{
		//do nothing
	}


	/**
	 * Add a field of a "primitive" type. A primitive type are all types that have a static/defined length. 
	 * @param type The type of the field. Only types with an {@link Enum#ordinal()} lower than or equal to {@link BType#CHAR} are allowed. 
	 * @param id The id of the field. 
	 * @return This instance.
	 * @throws IllegalArgumentException If the type is not primitive or the given id is already known.
	 */
	public BModelBuilder addPrimtive(BType type, String id)
	{
		if (type.ordinal() > BType.CHAR.ordinal())
			throw new IllegalArgumentException("Given type " + type + " is not a primitive type.");
		if (this.structure.containsKey(id))
			throw new IllegalArgumentException("Id " + id + " already known.");
		this.structure.put(id, new BField(type, id));
		return this;
	}

	/**
	 * Add a string field with a fixed length. The length must be specified by the protocol - the length will not be written or read from the byte stream. 
	 * @param id The id of the field.
	 * @param length The length of the string field.
	 * @return This instance.
	 * @throws IllegalArgumentException If the given id is already known.
	 */
	public BModelBuilder addFixedString(String id, int length)
	{
		if (this.structure.containsKey(id))
			throw new IllegalArgumentException("Id " + id + " already known.");
		this.structure.put(id, new BVarField(BType.STRING_FIXED, id, length));
		return this;
	}

	/**
	 * Add an array field with a fixed length. The length must be specified by the protocol - the length will not be written or read from the byte stream. 
	 * @param id The id of the field.
	 * @param length The length of the field.
	 * @return This instance.
	 * @throws IllegalArgumentException If the given id is already known.
	 */
	public BModelBuilder addFixedArray(String id, int length)
	{
		if (this.structure.containsKey(id))
			throw new IllegalArgumentException("Id " + id + " already known.");
		this.structure.put(id, new BVarField(BType.RAW_FIXED, id, length));
		return this;
	}
	
	/**
	 * Add a string field of dynamic length. The length will be written into the byte stream. To do that, a field for the length must be specified.
	 * The length field must already be known. 
	 * @param id The id of the field.
	 * @param length The length of the string field.
	 * @return This instance.
	 * @throws IllegalArgumentException If the given id is already known.
	 */
	public BModelBuilder addDynamicString(String id, String lengthFieldId)
	{
		this.addDynamicField(BType.STRING_DYNAMIC, id, lengthFieldId);
		return this;
	}
	
	/**
	 * Add an array field of dynamic length. The length will be written into the byte stream. To do that, a field for the length must be specified.
	 * The length field must already be known. 
	 * @param id The id of the field.
	 * @param length The length of the string field.
	 * @return This instance.
	 * @throws IllegalArgumentException If the given id is already known.
	 */
	public BModelBuilder addDynamicArray(String id, String lengthFieldId)
	{
		this.addDynamicField(BType.RAW_DYNAMIC, id, lengthFieldId);
		return this;
	}

	/**
	 * Add a custom field. A custom field is specified by types to read and write values. 
	 * @param type The type of the custom field - can be anything. For custom pojos this would {@link BType#RAW_DYNAMIC}.
	 * @param id The id of the field.
	 * @param readerClass The class name of the reader. The reader type must be derived from {@link ABFieldReader}. Can be <code>null</code> if the model will not be used for reading.
	 * @param writerClass The class name of the writer. The writer type must be derived from {@link ABFieldWriter}. Can be <code>null</code> if the model will not be used for writing.
	 * @return This instance.
	 * @throws IllegalArgumentException If the given id is already known.
	 */
	public BModelBuilder addCustomField(BType type, String id, String readerClass, String writerClass)
	{
		if (this.structure.containsKey(id))
			throw new IllegalArgumentException("Id " + id + " already known.");
		BCustomField field = new BCustomField(type, id, readerClass, writerClass);
		this.structure.put(id, field);
		return this;
	}

	/**
	 * Add a custom field. A custom field is specified by types to read and write values. 
	 * @param type The type of the custom field - can be anything. For custom pojos this would {@link BType#RAW_DYNAMIC}.
	 * @param id The id of the field.
	 * @param reader The type of the reader. The reader type must be derived from {@link ABFieldReader}. Can be <code>null</code> if the model will not be used for reading.
	 * @param writer The type of the writer. The writer type must be derived from {@link ABFieldWriter}. Can be <code>null</code> if the model will not be used for writing.
	 * @return This instance.
	 * @throws IllegalArgumentException If the given id is already known.
	 */
	public BModelBuilder addCustomField(BType type, String id, Class<? extends ABFieldReader> reader, Class<? extends ABFieldWriter<?>> writer)
	{
		return this.addCustomField(type, id, reader != null ? reader.getName() : null, writer != null ? writer.getName() : null);
	}

	private final void addDynamicField(BType type, String id, String lengthFieldId)
	{
		if (this.structure.containsKey(id))
			throw new IllegalArgumentException("Id " + id + " already known.");
		BField field = this.structure.get(lengthFieldId);
		BDynField valueField = new BDynField(type, id);
		valueField.setLengthRef(field);
		this.structure.put(id, valueField);
	}
	
	/**
	 * From the given information a {@link BModel} is compiled and returned. Closes the building pattern.
	 * @return A {@link BModel} instance.
	 */
	public BModel compile()
	{
		return new BModel(this.structure);
	}
	
	/**
	 * The entry point to the building pattern.
	 * @return A new {@link BModelBuilder} instance.
	 */
	public static final BModelBuilder start()
	{
		return new BModelBuilder();
	}
	
}
