/**
 * 
 */
package com.toennies.ci1429.app.util.binary.writer;

import java.nio.charset.StandardCharsets;

import com.toennies.ci1429.app.util.binary.model.BDynField;

/**
 * Writes a value of unspecified length into the buffer. The length is dynamically (on the fly) determined by the actual value.
 * Therefore, a field for the length of the field must be associated with this field.
 * Supports byte[] and {@link String} as value types.
 * @author renkenh
 */
public class BDynFieldWriter extends ABFieldWriter<BDynField>
{
	

	private final BWriter writer;
	private byte[] rawData;


	/**
	 * Constructor.
	 * @param field The associated field.
	 * @param writer The writer instance that uses this writer. Needed to access the length field.
	 */
	public BDynFieldWriter(BDynField field, BWriter writer)
	{
		super(field);
		this.writer = writer;
	}


	@Override
	public void setValue(Object value)
	{
		super.setValue(value);
		byte[] toWrite = new byte[0];
		if (this.value instanceof String)
			toWrite = ((String) this.value).getBytes(StandardCharsets.UTF_8);
		if (this.value instanceof byte[])
			toWrite = (byte[]) this.value;
		this.rawData = toWrite;
		if (this.rawData.length > Byte.MAX_VALUE)
			throw new RuntimeException("Value to write too large.");
		
		BIntegerFieldWriter lengthWriter = (BIntegerFieldWriter) this.writer.writers.get(this.field.getLengthRef().id);
		lengthWriter.setValue(Byte.valueOf((byte) this.length()));
	}

	@Override
	public void write(byte[] buffer, int position)
	{
		System.arraycopy(this.rawData, 0, buffer, position, this.length());
	}

	@Override
	public int length()
	{
		return this.rawData.length;
	}

}
