package com.toennies.ci1429.app.network.parameter;

import org.apache.commons.lang3.StringUtils;

import com.toennies.ci1429.app.network.parameter.typeinformation.ITypeInformationValidator;
import com.toennies.ci1429.app.network.parameter.typeinformation.ValidationResult;
import com.toennies.ci1429.app.network.parameter.typeinformation.Validators;

/**
 * Runtime representation of {@link AtDefaultParameters.Parameter}.
 * 
 * @author renkenh
 */
public final class ParamDescriptor
{

	private final String name;
	private final String value;
	private final boolean isRequired;
	private final String typeInformation;
	private final ITypeInformationValidator validator;
	private final String toolTip;

	
	public ParamDescriptor(String name, String value, boolean isRequired, String typeInformation, String toolTip)
	{
		this.name = name;
		this.value = value;
		this.isRequired = isRequired;
		this.typeInformation = typeInformation;
		this.validator = Validators.createValidatorFrom(typeInformation);
		this.toolTip = toolTip;
	}


	public String getName()
	{
		return name;
	}

	public String getValue()
	{
		return value;
	}

	public boolean isRequired()
	{
		return isRequired;
	}

	public String getTypeInformation()
	{
		return typeInformation;
	}
	
	public ITypeInformationValidator getValidator()
	{
		return this.validator;
	}
	
	public String getToolTip(){
	    return this.toolTip;
	}
	public ValidationResult validate(String runtimeValue)
	{
		if (StringUtils.isBlank(runtimeValue))
			runtimeValue = this.getValue();
		
		if (this.isRequired() && StringUtils.isBlank(runtimeValue))
			return new ValidationResult("Missing value for required parameter.");
	
		if (this.getValidator() != null)
			return this.getValidator().validate(runtimeValue);
		
		return ValidationResult.OK;
	}


	@Override
	public String toString()
	{
		return "ParamDescriptor [name=" + name + ", value=" + value + ", isRequired=" + isRequired
				+ ", typeInformation=" + typeInformation + "]"+ ", toolTip="+ toolTip;
	}

}