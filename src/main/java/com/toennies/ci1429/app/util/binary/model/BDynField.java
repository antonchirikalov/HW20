/**
 * 
 */
package com.toennies.ci1429.app.util.binary.model;

/**
 * @author renkenh
 *
 */
public class BDynField extends BField
{
	
	private BField lengthRef;

	
	public BDynField(BType type, String id)
	{
		super(type, id);
		if (type != BType.RAW_DYNAMIC && type != BType.STRING_DYNAMIC)
			throw new RuntimeException("Wrong type. Must be RAW_VARIABLE or STRING_VARIABLE");
	}

	protected void setLengthRef(BField field)
	{
		this.lengthRef = field;
		if (this.lengthRef.type.ordinal() > BType.INT.ordinal())
			throw new RuntimeException("Length reference must be an integer value.");
	}

	public BField getLengthRef()
	{
		return this.lengthRef;
	}

}
