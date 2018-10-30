/**
 * 
 */
package com.toennies.ci1429.app.util.binary.writer;

import com.toennies.ci1429.app.util.Binary;
import com.toennies.ci1429.app.util.binary.model.BField;

/**
 * Writer implementation that can write {@link Number} and {@link String}s as float or double values. The string must contain a number that 
 * can be parsed by {@link Double#parseDouble(String)}.
 * @author renkenh
 */
public class BFloatFieldWriter extends ABFieldWriter<BField>
{

	/**
	 * @param field
	 * @param writer
	 */
	public BFloatFieldWriter(BField field)
	{
		super(field);
	}


	@Override
	public void write(byte[] buffer, int position)
	{
		double dValue = 0;
		if (this.value instanceof Number)
			dValue = ((Number) value).doubleValue();
		if (this.value instanceof String)
			dValue = Double.parseDouble((String) this.value);
		switch (this.field.type)
		{
			case FLOAT:
				Binary.putFloat(buffer, position, (float) dValue);
				break;
			case DOUBLE:
				Binary.putDouble(buffer, position, dValue);
				break;
			default:
				throw new RuntimeException("Wrong method to write " + this.field.type);
		}
	}

}
