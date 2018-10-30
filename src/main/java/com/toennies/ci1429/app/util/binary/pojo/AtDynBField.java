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
 * Annotation for a dynamic length field. The data read or written does not have a specific length, instead the length should or must be
 * read or written also. Another option is to compute the length from some other value. However, the length must be defined/known before
 * anything is read or written.
 * 
 * This annotation works for byte[] or String fields only.
 * @author renkenh
 */
public @interface AtDynBField
{
	/** The type of the field. Should be {@value BType#STRING_DYNAMIC} or {@link BType#RAW_DYNAMIC}. */
	public BType value();
}
