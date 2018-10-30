/**
 * 
 */
package com.toennies.ci1429.app.util.binary.writer;

import java.nio.charset.StandardCharsets;

import com.toennies.ci1429.app.util.binary.model.BVarField;

/**
 * Writer implementation that writes byte[] or {@link String} of predefined length. If the string or byte[] is shorter than the length
 * provided by the associated field via {@link BVarField#length()} - then the content is left padded. If the content is longer than expected
 * it is simply truncated.
 * @author renkenh
 */
public class BVarFieldWriter extends ABFieldWriter<BVarField>
{

	/**
	 * @param field
	 * @param writer
	 */
	public BVarFieldWriter(BVarField field)
	{
		super(field);
	}


	@Override
	public void write(byte[] buffer, int position)
	{
		byte[] toWrite = new byte[0];
		if (this.value instanceof String)
			toWrite = ((String) this.value).getBytes(StandardCharsets.UTF_8);
		if (this.value instanceof byte[])
			toWrite = (byte[]) this.value;
		switch (this.field.type)
		{
			case RAW_FIXED:
			case STRING_FIXED:
				System.arraycopy(toWrite, 0, buffer, position, Math.min(toWrite.length, this.field.length));
				break;
			default:
				throw new RuntimeException("Wrong method to write " + this.field.type);
		}
	}

	@Override
	public int length()
	{
		return this.field.length;
	}

}
