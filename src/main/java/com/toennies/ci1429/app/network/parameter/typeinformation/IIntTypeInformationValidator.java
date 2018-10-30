/**
 * 
 */
package com.toennies.ci1429.app.network.parameter.typeinformation;

/**
 * This validator interface can be used as a front end to display a range of valid values.
 * Must be implemented by any {@link ITypeInformationValidator} that returns {@link InputType#INT}
 * on {@link #getInputType()}.
 * The implementation must guarantee that {@link #getLowerBound()} <= {@link #getUpperBound()} if applicable.
 * @author renkenh
 */
public interface IIntTypeInformationValidator extends ITypeInformationValidator
{
	
	@Override
	public default InputType getInputType()
	{
		return InputType.INT;
	}


	/**
	 * @return The lower bound if available, otherwise <code>null</code>
	 */
	public Integer getLowerBound();
	
	/**
	 * @return The upper bound if available, otherwise <code>null</code>
	 */
	public Integer getUpperBound();

}
