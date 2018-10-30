/**
 * 
 */
package com.toennies.ci1429.app.util.binary.model;

import com.toennies.ci1429.app.util.binary.reader.ABFieldReader;
import com.toennies.ci1429.app.util.binary.writer.ABFieldWriter;

/**
 * A specialization of the BField for custom implementations. This field can e.g. be used to serialize whole pojos.
 * It takes a reader and/or writer class. These classes must be derived from {@link ABFieldReader} and {@link ABFieldWriter} respectively.
 * They are responsible for reading, writing the actual data.
 * 
 * If you want to implement some fancy stuff, then the type of the field should be {@link BType#RAW_DYNAMIC}.
 * 
 * @author renkenh
 */
public class BCustomField extends BField
{

	/** The class name of the reader class. */
	public final String readerClass;
	/** The class name of the writer class. */
	public final String writerClass;
	

	/**
	 * Constructor.
	 * @param type The type of the field. 
	 * @param id The id of the field.
	 * @param readerClass The class name of the reader.
	 * @param writerClass The class name of the writer.
	 */
	public BCustomField(BType type, String id, String readerClass, String writerClass)
	{
		super(type, id);
		this.readerClass = readerClass;
		this.writerClass = writerClass;
	}

}
