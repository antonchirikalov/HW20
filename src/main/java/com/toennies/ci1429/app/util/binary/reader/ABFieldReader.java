/**
 * 
 */
package com.toennies.ci1429.app.util.binary.reader;

import java.io.IOException;

import com.toennies.ci1429.app.util.binary.model.BField;
import com.toennies.ci1429.app.util.binary.model.BField.BType;

/**
 * Super type to implement a reader for binary data. The reader must provide data about the type it is able to read. In the simplest implementation
 * it gets this information from the associated {@link BField}. A reader is always associated with specific data. To read different data, a new read must be
 * created.
 * @author renkenh
 */
public abstract class ABFieldReader
{

	/** The associated field. */
	protected final BField field;
	/** The start position on the array of the value to read. */
	protected final int position;
	/** The actual data from which to read the value. */
	protected final byte[] rawData;

	
	/**
	 * Constructor.
	 * @param field The associated field.
	 * @param position The start position of the value in the array.
	 * @param rawData The actual data from which to read the data.
	 */
	protected ABFieldReader(BField field, int position, byte[] rawData)
	{
		this.field = field;
		this.position = position;
		this.rawData = rawData;
	}


	/**
	 * The id of the associated field.
	 * @return The id of the associated field.
	 */
	public String getFieldID()
	{
		return this.field.id;
	}
	
	/**
	 * The type that this read will read. This is needed as one implementation may be able to process different types, e.g. the {@link BNumberFieldReader}.
	 * @return The type this reader will read.
	 */
	public BType getType()
	{
		return this.field.type;
	}

	/**
	 * The type of the read value. This can be different from the {@link #getType()}. E.g. for {@link BType#UINT} the value is of type {@link Long}.
	 * @return The type of the value returned when calling {@link #getValue()}.
	 */
	public Class<?> getReadClass()
	{
		return this.field.type.clazz;
	}

	/**
	 * The length of the value to read (in bytes). Must be determined before actual reading the value.
	 * @return The length of the value to read - in bytes.
	 */
	public int length()
	{
		return this.field.type.length;
	}
	
	/**
	 * Returns the value from the data read. When the data is read does not concern this method.
	 * @return The value encoded in the byte array.
	 * @throws IOException If something goes wrong during reading from the byte array.
	 */
	public abstract Object getValue() throws IOException;
	
}
