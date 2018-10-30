/**
 * 
 */
package com.toennies.ci1429.app.network.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.toennies.ci1429.app.network.parameter.typeinformation.ITypeInformationValidator;


/**
 * Annotation that allows to annotate types with {@link AtDefaultParameters.Parameter}s.
 * @author renkenh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AtDefaultParameters
{
	
	public static final String DUMMY_NAME = "dummy";

	/**
	 * @return All specified parameters.
	 */
	public Parameter[] value();
	
	/**
	 * Annotation that specifies parameters and their default value.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Repeatable(AtDefaultParameters.class)
	public @interface Parameter
	{
		/** The name of the parameter. */
		public String name() default DUMMY_NAME;
		
		/** The default value of the parameter. Empty string if not specified. */
		public String value() default "";
		
		/** Returns whether the parameter is required or not. */
		public boolean isRequired() default false;

		/**
		 * Describes which values are allowed for this parameter.
		 * See {@link ITypeInformationValidator} and its implementations
		 * for examples and capabilities.
		 */
		public String typeInformation() default "";
		
		/** A short tool tip for the user shown on the UI. */
		public String toolTip() default "";
	}
}
