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
 * Annotation to mark a field in a pojo as a field that should be de-/serialized by the binary package.
 * The type of the field determines how its value is de-/serialized.
 * If you, e.g. want to write an int field as a ushort value, then use {@link AtTypedBField}.
 * @author renkenh
 */
public @interface AtBField
{

}
