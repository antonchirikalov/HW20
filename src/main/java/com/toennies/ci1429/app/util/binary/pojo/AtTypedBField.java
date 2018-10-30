/**
 * 
 */
package com.toennies.ci1429.app.util.binary.pojo;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.toennies.ci1429.app.util.binary.model.BField.BType;

@Retention(RUNTIME)
@Target(FIELD)
/**
 * Annotation with the same functionality like {@link AtBField}. However, with this annotation it is possible to specify the type. This
 * works for the standard conversion (e.g. float -> double, byte -> int, etc.) and for unsigned types. This annotation allows to use an int
 * typed field as a container for an unsigned short value. 
 * @author renkenh
 */
public @interface AtTypedBField
{
	/** The type of the field. Must be compatible with the real type of the field. */
	public BType value();
}
