/**
 * 
 */
package com.toennies.ci1429.app.network.parameter.typeinformation;

/**
 * This validator interface can be used as a front end to display a range of valid values.
 * Must be implemented by any {@link ITypeInformationValidator} that returns {@link InputType#FLOAT}
 * on {@link #getInputType()}.
 * The implementation must guarantee that {@link #getLowerBound()} <= {@link #getUpperBound()} if applicable.
 * @author renkenh
 */
public interface IFloatTypeInformationValidator extends ITypeInformationValidator
{
	
	@Override
	public default InputType getInputType()
	{
		return InputType.FLOAT;
	}


	/**
	 * @return The lower bound if available, otherwise <code>null</code>
	 */
	public Double getLowerBound();
	
	/**
	 * @return The upper bound if available, otherwise <code>null</code>
	 */
	public Double getUpperBound();

}
