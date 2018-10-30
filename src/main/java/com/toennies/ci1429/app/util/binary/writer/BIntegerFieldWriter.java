/**
 * 
 */
package com.toennies.ci1429.app.util.binary.writer;

import com.toennies.ci1429.app.util.Binary;
import com.toennies.ci1429.app.util.binary.model.BField;

/**
 * Writer implementation that can write {@link Number}s and Strings as integer values. The string must contain a number that can be parsed by
 * {@link Long#parseLong(String)}.
 * @author renkenh
 */
public class BIntegerFieldWriter extends ABFieldWriter<BField>
{

	/**
	 * @param field
	 * @param writer
	 */
	public BIntegerFieldWriter(BField field)
	{
		super(field);
	}


	@Override
	public void write(byte[] buffer, int position)
	{
		long lValue = 0;
		if (this.value instanceof Number)
			lValue = ((Number) this.value).longValue();
		if (this.value instanceof String)
			lValue = Long.parseLong((String) this.value);
		switch (this.field.type)
		{
			case BYTE:
				buffer[position] = (byte) lValue;
				break;
			case INT:
				Binary.putInt(buffer, position, (int) lValue);
				break;
			case LONG:
				Binary.putLong(buffer, position, lValue);
				break;
			case SHORT:
				Binary.putShort(buffer, position, (short) lValue);
				break;
			case UBYTE:
				Binary.putUByte(buffer, position, (short) lValue);
				break;
			case UINT:
				Binary.putUInt(buffer, position, (long) lValue);
				break;
			case USHORT:
				Binary.putUShort(buffer, position, (int) lValue);
				break;
			default:
				throw new RuntimeException("Wrong method to write " + this.field.type);
		}
	}

}
