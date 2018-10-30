/**
 * 
 */
package com.toennies.ci1429.app.util.binary.model;

/**
 * Field of variable length. This field has a predefined but configurable (and therefore variable) length.
 * @author renkenh
 */
public class BVarField extends BField
{

	/** The length of the field in bytes. */
	public final int length;


	/**
	 * Constructor.
	 * @param type The type of the field.
	 * @param id The id.
	 * @param length The length in bytes.
	 */
	public BVarField(BType type, String id, int length)
	{
		super(type, id);
		this.length = length;
		checkFixed(type);
	}

	private static final void checkFixed(BType type)
	{
		if (type != BType.RAW_FIXED && type != BType.STRING_FIXED)
			throw new RuntimeException("Wrong type. Must be RAW_FIXED or STRING_FIXED");
	}


	@Override
	public int length()
	{
		return this.length;
	}

}
