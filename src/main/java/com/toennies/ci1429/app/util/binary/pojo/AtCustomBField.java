/**
 * 
 */
package com.toennies.ci1429.app.util.binary.pojo;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.toennies.ci1429.app.util.binary.reader.ABFieldReader;
import com.toennies.ci1429.app.util.binary.writer.ABFieldWriter;

@Retention(RUNTIME)
@Target(FIELD)
/**
 * Annotation to setup reader and writer classes for a field. The value of the annotated field will be read and written using the specified
 * reader/writer classes.
 * @author renkenh
 */
public @interface AtCustomBField
{

	/** The class to be used for reading the custom field data. */
	public Class<? extends ABFieldReader> reader();
	
	/** The class to be used for writing the custom field data. */
	public Class<? extends ABFieldWriter<?>> writer();
}
