/**
 * 
 */
package com.toennies.ci1429.app.util.binary.writer;

import java.io.IOException;

import com.toennies.ci1429.app.util.binary.model.BField;
import com.toennies.ci1429.app.util.binary.model.BField.BType;

/**
 * Super class of the writers used to write {@link BField}s and their respective values into a given byte array.
 * @author renkenh
 */
public abstract class ABFieldWriter<F extends BField>
{

	/** The field, this writer is associated to. */
	protected final F field;
	/** The value this writer will write into a provided byte array. */
	protected Object value = null;

	
	/**
	 * Constructor.
	 */
	protected ABFieldWriter(F field)
	{
		this.field = field;
	}


	/**
	 * The field id which also identifies this writer.
	 * @return An id.
	 */
	public final String getFieldID()
	{
		return this.field.id;
	}
	
	/**
	 * The type of the associated field. This defines, what is written into the array.
	 * @return The type of the associated field.
	 */
	public BType getType()
	{
		return this.field.type;
	}

	/**
	 * The length of the value in bytes. This is the number of bytes which will be written into the byte array and must be reserved in the byte array.
	 * @return The length of the value in bytes.
	 */
	public int length()
	{
		return this.field.type.length;
	}
	
	/**
	 * Sets a new value which should be written into the array.
	 * @param value The new value.
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 * Protected method to write the associated value into the given array.
	 * @param buffer The array to write to.
	 * @param position The position where to start with writing
	 * @throws IOException If something goes wrong, e.g. the value has the wrong type.
	 */
	abstract void write(byte[] buffer, int position) throws IOException;

}
