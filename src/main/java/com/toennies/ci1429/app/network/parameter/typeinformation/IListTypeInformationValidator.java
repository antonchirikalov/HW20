/**
 * 
 */
package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.util.List;

/**
 * This validator interface can be used as a front end to display a list of valid values.
 * Must be implemented by any {@link ITypeInformationValidator} that returns {@link InputType#LIST}
 * on {@link #getInputType()}.
 * @author renkenh
 */
public interface IListTypeInformationValidator extends ITypeInformationValidator
{
	
	@Override
	public default InputType getInputType()
	{
		return InputType.LIST;
	}

	/**
	 * @return The list of valid values. Unmodifiable. Can be used to display them on an UI.
	 */
	public List<String> getValidValues();

}
