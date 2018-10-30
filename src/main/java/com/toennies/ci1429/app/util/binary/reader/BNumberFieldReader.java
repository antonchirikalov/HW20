/**
 * 
 */
package com.toennies.ci1429.app.util.binary.reader;

import com.toennies.ci1429.app.util.Binary;
import com.toennies.ci1429.app.util.binary.model.BField;
import com.toennies.ci1429.app.util.binary.model.BField.BType;

/**
 * Implementation of the {@link ABFieldReader} class to read a number. Supports
 * {@link BType#BYTE}, {@link BType#UBYTE}, ..., {@link BType#LONG}, {@link BType#FLOAT}, {@link BType#DOUBLE}.
 * @author renkenh
 */
public class BNumberFieldReader extends ABFieldReader
{

	/**
	 * Constructor.
	 * @param field The field this reader is associated with.
	 * @param position The position where the character is located.
	 * @param rawData The data to read.
	 */
	public BNumberFieldReader(BField field, int position, byte[] rawData)
	{
		super(field, position, rawData);
	}


	@Override
	public Number getValue()
	{
		switch (this.field.type)
		{
			case BYTE:
				return Byte.valueOf(this.rawData[this.position]);
			case INT:
				return Integer.valueOf(Binary.getInt(this.rawData, this.position));
			case LONG:
				return Long.valueOf(Binary.getLong(this.rawData, this.position));
			case SHORT:
				return Short.valueOf(Binary.getShort(this.rawData, this.position));
			case UBYTE:
				return Short.valueOf(Binary.getUByte(this.rawData, this.position));
			case UINT:
				return Long.valueOf(Binary.getUInt(this.rawData, this.position));
			case USHORT:
				return Integer.valueOf(Binary.getUShort(this.rawData, this.position));
			case FLOAT:
				return Float.valueOf(Binary.getFloat(this.rawData, this.position));
			case DOUBLE:
				return Double.valueOf(Binary.getDouble(this.rawData, this.position));
			default:
				throw new RuntimeException("Wrong parser for " + this.field.type);
		}
	}

}
