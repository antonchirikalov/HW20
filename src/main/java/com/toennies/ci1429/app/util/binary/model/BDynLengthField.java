package com.toennies.ci1429.app.util.binary.model;

/**
 * Field type needed by {@link BDynField} to save its length (as it is not predefined).
 * @author renkenh
 */
public class BDynLengthField extends BField
{

	/**
	 * Constructor.
	 * @param lengthField The actual field.
	 */
	public BDynLengthField(BField lengthField)
	{
		super(lengthField.type, lengthField.id);
	}

}