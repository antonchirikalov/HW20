/**
 * 
 */
package com.toennies.ci1429.app.util.binary.reader;

import com.toennies.ci1429.app.util.Binary;
import com.toennies.ci1429.app.util.binary.model.BDynField;
import com.toennies.ci1429.app.util.binary.model.BVarField;

/**
 * Reader for fields with specific types but different lengths. Supports BType#STRING_* and BType#RAW_* types.  
 * @author renkenh
 */
public class BVarFieldReader extends ABFieldReader
{

	private final int length;
	
	/**
	 * Constructor.
	 * @param field The field associated with this reader.
	 * @param position The position where to start within the array.
	 * @param rawData The actual data.
	 */
	public BVarFieldReader(BVarField field, int position, byte[] rawData)
	{
		super(field, position, rawData);
		this.length = field.length;
	}

	/**
	 * Constructor. 
	 * @param field The field associated with this reader.
	 * @param position The position where to start within the array.
	 * @param rawData The actual data.
	 */
	public BVarFieldReader(BDynField field, int position, BNumberFieldReader lengthRef, byte[] rawData)
	{
		super(field, position, rawData);
		this.length = lengthRef.getValue().intValue();
	}


	@Override
	public Object getValue()
	{
		switch (this.field.type)
		{
			case RAW_FIXED:
			case RAW_DYNAMIC:
				return Binary.getArray(this.rawData, this.position, this.length());
			case STRING_FIXED:
			case STRING_DYNAMIC:
				return Binary.getString(this.rawData, this.position, this.length());
			default:
				throw new RuntimeException("Wrong parser for " + this.field.type);
		}
	}

	@Override
	public int length()
	{
		return this.length;
	}

}
