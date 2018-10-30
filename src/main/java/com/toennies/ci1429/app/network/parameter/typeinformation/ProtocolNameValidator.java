package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.toennies.ci1406.lib.model.ClassAnnotationInformation;
import com.toennies.ci1406.lib.utils.AnnotationUtils;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.network.protocol.AtProtocol;

/**
 * Checks if a proper protocol was chosen.
 * Since a protocol is bound to a specific device type. This information must be given 
 * in the constructor.
 * This validator allows reconfiguration at runtime in contrast to all other validators.
 * This feature was build in for the usage of this validator within the "New device Wizard".
 * 
 * <pre>
 * typeInformation="java:com.toennies.ci1429.app.network.parameter.typeinformation.ProtocolNameValidator"
 * </pre>
 */
@AtValidator(ProtocolNameValidator.VALIDATOR_NAME)
public class ProtocolNameValidator implements IListTypeInformationValidator
{
	
	public static final String VALIDATOR_NAME = "protocol";
	

	private DeviceType type;
	private List<String> values;

	public ProtocolNameValidator()
	{
		// Needed because Validators#createValidatorFrom invokes this constructor
	}

	/**
	 * Standard constructor for the validation framework.
	 * @param typeInfo The type info containing the DeviceType for which this validator has to be setup.
	 */
	public ProtocolNameValidator(String typeInfo)
	{
		this(parseTypeInfo(typeInfo));
	}
	
	/**
	 * Constructor. Creates a validator for the given device type.
	 * @param deviceType The device type for which to load available protocols.
	 */
	public ProtocolNameValidator(DeviceType deviceType)
	{
		this.values = loadProtocolClassnames(deviceType);
		this.type = deviceType;
	}

	
	/**
	 * Reconfigure the validator with a new DeviceType.
	 * @param type The device type for which to reconfigure.
	 * @return Whether the validator had to be reconfigured. <code>false</code> if the given device type matches the current config.
	 */
	public boolean updateValidator(DeviceType type)
	{
		if (this.type != type)
		{
			this.values = loadProtocolClassnames(type);
			this.type = type;
			return true;
		}
		return false;
	}

	/**
	 * @return The type for which this validator currently is setup.
	 */
	public DeviceType currentDeviceType()
	{
		return this.type;
	}

	@Override
	public ValidationResult validate(String value2Validate) throws IllegalArgumentException
	{
		boolean isValid = this.values.stream().anyMatch(validValue -> validValue.equals(value2Validate));
		if (isValid)
			return ValidationResult.OK;
		return new ValidationResult("Could not validate "+value2Validate+" as a valid IProtocol implementation.");
	}

	@Override
	public List<String> getValidValues()
	{
		return Collections.unmodifiableList(this.values);
	}


	private static final DeviceType parseTypeInfo(String typeInfo)
	{
		String[] strippedTypeInformation = typeInfo.split(":");
		if (strippedTypeInformation.length < 2 || StringUtils.isBlank(strippedTypeInformation[1]))
			return null;
		
		return DeviceType.valueOf(strippedTypeInformation[1]);
	}
	

	private static final List<String> loadProtocolClassnames(DeviceType deviceType)
	{
		if (deviceType == null)
			return Collections.emptyList();
		return AnnotationUtils.returnClassInformationMarkedByAnnotation(AtProtocol.class)
							  .stream()
							  .filter((cai) -> deviceType == null || cai.getAnnotationObject().deviceType() == deviceType)
							  .map(ClassAnnotationInformation::getClassNameMarkedByAnnotation)
							  .collect(Collectors.toList());
	}
	
}
