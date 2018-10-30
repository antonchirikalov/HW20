/**
 * 
 */
package com.toennies.ci1429.app.network.parameter.typeinformation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
/**
 * Annotation that marks type validators for the validator framework for given parameters.
 * The validator needs its identifier which is used at the beginning of the typeinformation
 * added to the AtDefaultParameter.Parameter.
 * @author renkenh
 */
public @interface AtValidator
{
	/**
	 * @return The identifier used in type informations to specify the type of the validator.
	 */
	public String value();
}
