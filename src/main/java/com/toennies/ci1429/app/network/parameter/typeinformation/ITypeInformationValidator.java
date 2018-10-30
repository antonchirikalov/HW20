package com.toennies.ci1429.app.network.parameter.typeinformation;

import com.toennies.ci1429.app.network.parameter.AtDefaultParameters;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.toennies.ci1429.app.network.protocol.scanner.ChipReaderProtocol;

/**
 * Base interface for feature request described by RFC0712-152.
 * 
 * Some designated classes are marked by a {@link AtDefaultParameters}
 * annotation. This annotation owns one or more {@link ParamDescriptor}
 * annotation(s). Those annotations can provide marked classes with
 * configuration parameters needed by protocol architecture. For instance have a
 * look at {@link ChipReaderProtocol}. Because of the fact, that there is no
 * possibility to define, which kind of type (a simple {@link String} or a int
 * value) this parameter values have, we have this interface and it's
 * subclasses. {@link ParamDescriptor#getValue()} always returns a
 * {@link String}!
 * 
 * Type information is stored in {@link ParamDescriptor#getTypeInformation()}
 * attribute. Through this, {@link ParamDescriptor#getValue()}s can be validated
 * against the respective type information.
 * 
 */
public interface ITypeInformationValidator
{

	
	/**
	 * The delimiter between the identifier and the parameters for the validator.
	 */
	public final static char VALIDATOR_DELIMITER = ':';


	/**
	 * Every {@link Parameter#value()} needs a concrete ui representation. This enum
	 * illustrates in which way a parameter can be entered by a user.
	 * 
	 * Example: if user has to enter a valid com port, the ui should display only a
	 * combo box with all avaible com ports and not a simple input text field.
	 * Through this, we can prevent misentries.
	 */
	public enum InputType
	{
		INT, FLOAT, LIST, TEXT, BOOLEAN;
	}

	/**
	 * Concrete implementation of validation algorithm.
	 * 
	 * @param value2Validate
	 *            is checked if matches defined typeinformation rule.
	 * @param typeInformation
	 *            defines the typeinformation rule. This String is 'compiled' to
	 *            a concrete validation implementation.
	 * @return true if value matches type validation rule.
	 */
	public ValidationResult validate(String value2Validate) throws IllegalArgumentException;

	/**
	 * Illustrates in which way a respective parameter (that gets validated by
	 * subclasses) should be displayed to an user.
	 */
	public InputType getInputType();

}
