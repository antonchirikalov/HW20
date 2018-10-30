/**
 * 
 */
package com.toennies.ci1429.app.util.binary.pojo;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
/**
 * Annotation for a field with a fixed length (in bytes). The length must be specified in the annotation and will never change.
 * This annotation works for byte[] or String fields only.
 * @author renkenh
 */
public @interface AtFixedBField
{
	/** The length of the field. */
	public int value();
}
