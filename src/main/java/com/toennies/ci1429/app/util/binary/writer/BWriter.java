/**
 * 
 */
package com.toennies.ci1429.app.util.binary.writer;

import java.io.IOException;
import java.util.LinkedHashMap;

import com.toennies.ci1429.app.util.Utils;
import com.toennies.ci1429.app.util.binary.model.BCustomField;
import com.toennies.ci1429.app.util.binary.model.BDynField;
import com.toennies.ci1429.app.util.binary.model.BField;
import com.toennies.ci1429.app.util.binary.model.BModel;
import com.toennies.ci1429.app.util.binary.model.BVarField;

/**
 * Implementation to serialize given data into a byte array.
 * @author renkenh
 */
public class BWriter
{

	/** The model used by this writer. */
	protected final BModel model;
	/** The writers accessible by their keys. */
	protected final LinkedHashMap<String, ABFieldWriter<?>> writers = new LinkedHashMap<>();


	private BWriter(BModel model)
	{
		this.model = model;
		for (BField field : this.model.getFields())
		{
			ABFieldWriter<?> tmp = this.createWriter(field);
			this.writers.put(field.id, tmp);
		}
	}

	private ABFieldWriter<?> createWriter(BField field)
	{
		if (field instanceof BCustomField)
			return Utils.instantiate(((BCustomField) field).writerClass, field, this);
		if (field instanceof BDynField)
			return new BDynFieldWriter((BDynField) field, this);
		if (field instanceof BVarField)
			return new BVarFieldWriter((BVarField) field);
		switch (field.type)
		{
			case CHAR:
				return new BCharFieldWriter(field);
			case BYTE:
			case INT:
			case LONG:
			case SHORT:
			case UBYTE:
			case UINT:
			case USHORT:
				return new BIntegerFieldWriter(field);
			case DOUBLE:
			case FLOAT:
				return new BFloatFieldWriter(field);
			default:
				throw new RuntimeException("Missconfigured Binary Model. " + field.id);
		}
	}


	/**
	 * The model that this writer uses.
	 * @return The model.
	 */
	public BModel model()
	{
		return this.model;
	}
	
	/**
	 * Set a value for a specific field. The field is specified by its id.
	 * @param id The of the field.
	 * @param value The value to set.
	 */
	public void setValue(String id, Object value)
	{
		this.writers.get(id).setValue(value);
	}

	/**
	 * The data specified by using {@link #setValue(String, Object)} is compiled into a byte array. 
	 * @return The byte array containing the serialized data.
	 * @throws IOException If the data could not be written.
	 */
	public byte[] compile() throws IOException
	{
		byte[] buffer = new byte[this.computeArrayLength()];
		int position = 0;
		for (ABFieldWriter<?> writer : this.writers.values())
		{
			writer.write(buffer, position);
			position += writer.length();
		}
		return buffer;
	}

	private int computeArrayLength()
	{
		return this.writers.values().stream().mapToInt((w) -> w.length()).sum();
	}
	
	/**
	 * Method to create a new writer for the given {@link BModel}.
	 * @param model The model for which to create a writer.
	 * @return The writer instance.
	 */
	public static final BWriter createFor(BModel model)
	{
		return new BWriter(model);
	}
}
