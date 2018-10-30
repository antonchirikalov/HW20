package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.toennies.ci1406.lib.model.ClassAnnotationInformation;
import com.toennies.ci1406.lib.utils.AnnotationUtils;
import com.toennies.ci1429.app.network.socket.AtSocket;
import com.toennies.ci1429.app.network.socket.ISocket;

/**
 * Checks if a proper socket was chosen.
 * Validates against {@link ISocket} implementations that are annotated with {@link AtSocket} annotation.
 * 
 * <pre>
 * typeInformation="java:com.toennies.ci1429.app.network.parameter.typeinformation.SocketTypeValidator"
 * </pre>
 */
public class SocketTypeValidator implements IListTypeInformationValidator
{

	private final List<String> socketClassNames = loadSocketClassnames();

	public SocketTypeValidator()
	{
		// default constructor - needed for Validators Framework
	}

	@Override
	public ValidationResult validate(String value2Validate) throws IllegalArgumentException
	{
		boolean success = this.socketClassNames.contains(value2Validate);
		if (success)
			return ValidationResult.OK;
		return new ValidationResult("Could not validate "+value2Validate+" as a valid ISocket implementation.");

	}

	@Override
	public List<String> getValidValues()
	{
		return Collections.unmodifiableList(this.socketClassNames);
	}

	private static final List<String> loadSocketClassnames()
	{
		return AnnotationUtils.returnClassInformationMarkedByAnnotation(AtSocket.class)
							  .stream()
							  .map(ClassAnnotationInformation::getClassNameMarkedByAnnotation)
							  .collect(Collectors.toList());
	}

}
