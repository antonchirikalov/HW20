package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.toennies.ci1406.lib.model.ClassAnnotationInformation;
import com.toennies.ci1406.lib.utils.AnnotationUtils;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.toennies.ci1429.app.network.parameter.Parameters;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.util.Utils;

/**
 * Central class for validating {@link ParamDescriptor} objects. Provides one
 * static method in order to perform a validation of a annotation object.
 * 
 * There are serveral implementations of validation. This class wraps all
 * implementations in order to provide one simple static method for
 * {@link ParamDescriptor} validation purpose.
 */
public class Validators
{

	private static List<ClassAnnotationInformation<AtValidator>> allClassesMarkedByAtValidator;

	static
	{
		// Be aware of the fact, that this costs some performance.
		allClassesMarkedByAtValidator = AnnotationUtils.returnClassInformationMarkedByAnnotation(AtValidator.class);
	}

	/**
	 * For a given type information, tries to find a proper validator. For this, the method extracts the identifier, located
	 * before the {@link ITypeInformationValidator#VALIDATOR_DELIMITER} (if any).
	 * 
	 * Special case: 'java:com.toennies.ci1429.app.network.parameters.typeinformation.ComPortTypeValidator'
	 * In this case, the specified java class is loaded and returned. This allows for dynamic validation.
	 * 
	 * Examples:
	 * 
	 * <pre>
	 * int:1...5  --> IIntTypeInformationValidator
	 * boolean    --> BooleanTypeValidator
	 * </pre>
	 * 
	 * @param typeInformation The information for which to get the validator.
	 * @return A validator instance - or <code>null</code> no validator could be found.
	 */
	public static final ITypeInformationValidator createValidatorFrom(String typeInformation)
	{
		String type = typeInformation;
		if (type.contains(String.valueOf(ITypeInformationValidator.VALIDATOR_DELIMITER)))
			type = type.split(String.valueOf(ITypeInformationValidator.VALIDATOR_DELIMITER))[0];
		
		for (ClassAnnotationInformation<AtValidator> classinfo : allClassesMarkedByAtValidator)
			if (classinfo.getAnnotationObject().value().equalsIgnoreCase(type))
				return Utils.instantiate(classinfo.getClassNameMarkedByAnnotation(), typeInformation);
		
		switch (type)
		{
            case "java":
                String classname = typeInformation.split(String.valueOf(ITypeInformationValidator.VALIDATOR_DELIMITER))[1];
                return Utils.instantiate(classname);
            case "boolean":
            	return BooleanTypeValidator.INSTANCE;
            default:
                return null;
        }
	}
	
	/**
	 * Validates given runtime parameters against a collection of {@link ParamDescriptor}s
	 * For each {@link ParamDescriptor} a value in the given map is searched (by {@link ParamDescriptor#getName()}) 
	 * and {@link ParamDescriptor#validate(String)} is called (even if not found, then <code>null</code> is used).
	 * 
	 * That means, the given map is valid only if (together with the default values of the param descriptors) all
	 * ParamDescriptors are satisfied.
	 *  
	 * @return true if annotation object contains valid data. Otherwise false.
	 */
	public static ValidationResults validate(Map<String, String> runtimeValues, Collection<ParamDescriptor> validationModel)
	{
		ValidationResults results = new ValidationResults();
		for (ParamDescriptor desc : validationModel)
		{
			ValidationResult result = desc.validate(runtimeValues.get(desc.getName()));
			results.put(desc.getName(), result);
		}
		return results;
	}

	/**
	 * Validates a given map of runtime parameters against a given protocol class. The {@link ParamDescriptor}s of the
	 * given protocol class are loaded and then {@link #validate(Map, Collection)} is called.
	 * @param runtimeValues The values to validate.
	 * @param protocolClassname The protocol class which specifies the validation model.
	 * @return <code>true</code> if the map is valid, i.e. {@link IProtocol#setup(Map)} can be called and all needed
	 * parameters are present with valid values.
	 */
	public static ValidationResults validate(Map<String, String> runtimeValues, String protocolClassname)
	{
		Map<String, ParamDescriptor> parameters = Parameters.getParameters(protocolClassname);
		return validate(runtimeValues, parameters.values());
	}
	
	/**
	 * Validates a whole description. It is checked if a device model, a vendor and a protocol are specified.
	 * Then, {@link #validate(Map, String)} is called with the parameters of the description plus the specified
	 * protocol class.
	 * @param description The description to validate.
	 * @return <code>true</code> if valid, <code>false</code> otherwise.
	 */
	public static ValidationResults validate(IDeviceDescription description)
	{
		ValidationResults results = validate(description.getParameters(), description.getProtocolClass());
		if (StringUtils.isBlank(description.getDeviceModel()))
			results.put("deviceModel", new ValidationResult("DeviceModel cannot be empty"));
		if (StringUtils.isBlank(description.getVendor()))
			results.put("vendor", new ValidationResult("Vendor cannot be empty"));
		if (StringUtils.isBlank(description.getProtocolClass()))
			results.put("protocolClass", new ValidationResult("ProtocolClass cannot be empty"));
		return results;
	}


	private Validators()
	{
		// no public instance; just static methods.
	}

}
