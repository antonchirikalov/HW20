/**
 * 
 */
package com.toennies.ci1429.app.util.binary.writer;

import com.toennies.ci1429.app.util.binary.model.BField;

/**
 * Fieldwriter that writes a single char. Supports {@link Byte}, {@link Character} and {@link String} as value.
 * @author renkenh
 */
public class BCharFieldWriter extends ABFieldWriter<BField>
{

	/**
	 * Constructor.
	 * @param field The associated field.
	 */
	public BCharFieldWriter(BField field)
	{
		super(field);
	}


	@Override
	public void write(byte[] buffer, int position)
	{
		if (this.value instanceof Byte)
		{
			buffer[position] = (byte) ((Byte) this.value).byteValue();
			return;
		}
		if (this.value instanceof Character)
		{
			buffer[position] = (byte) ((Character) this.value).charValue();
			return;
		}
		if (this.value instanceof String)
			buffer[position] = (byte) String.valueOf(this.value).charAt(0);
	}

}
