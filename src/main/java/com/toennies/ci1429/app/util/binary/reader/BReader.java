/**
 * 
 */
package com.toennies.ci1429.app.util.binary.reader;

import java.io.IOException;
import java.util.LinkedHashMap;

import com.toennies.ci1429.app.util.Utils;
import com.toennies.ci1429.app.util.binary.model.BCustomField;
import com.toennies.ci1429.app.util.binary.model.BDynField;
import com.toennies.ci1429.app.util.binary.model.BField;
import com.toennies.ci1429.app.util.binary.model.BModel;
import com.toennies.ci1429.app.util.binary.model.BVarField;
import com.toennies.ci1574.lib.helper.Generics;

/**
 * Reader that takes a {@link BModel} and a byte array and parses the byte array according to the given model.
 * The reader associates the fields of the model with the needed readers. It has a simplified API that makes it easy
 * to get values from the raw data array.
 * @author renkenh
 */
public class BReader
{

	private final BModel model;
	private final LinkedHashMap<String, ABFieldReader> readers = new LinkedHashMap<>();


	private BReader(BModel model, byte[] rawData)
	{
		this.model = model;
		this.load(rawData);
	}

	
	private void load(byte[] rawData)
	{
		int position = 0;
		for (BField field : this.model.getFields())
		{
			ABFieldReader reader = createReader(field, position, rawData);
			this.readers.put(field.id, reader);
			position += reader.length();
		}
	}
	
	private ABFieldReader createReader(BField field, int position, byte[] rawData)
	{
		if (field instanceof BCustomField)
			return Utils.instantiate(((BCustomField) field).readerClass, field, position, rawData);
		switch (field.type)
		{
			case CHAR:
				return new BCharFieldReader(field, position, rawData);
			case BYTE:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case SHORT:
			case UBYTE:
			case UINT:
			case USHORT:
				return new BNumberFieldReader(field, position, rawData);
			case RAW_FIXED:
			case STRING_FIXED:
				BVarField varField = (BVarField) field;
				return new BVarFieldReader(varField, position, rawData);
			case STRING_DYNAMIC:
			case RAW_DYNAMIC:
				BDynField dynField = (BDynField) field;
				BNumberFieldReader dynLength = (BNumberFieldReader) this.readers.get(field.id+"_length");
				return new BVarFieldReader(dynField, position, dynLength, rawData);
			default:
				return null;
		}
	}
	
	/**
	 * The model of this reader.
	 * @return The model.
	 */
	public BModel model()
	{
		return this.model;
	}
	
	/**
	 * Uses the reader for the specified field id to read the value from the binary data.
	 * @param fieldID The field id.
	 * @return The value.
	 * @throws IOException If the field ID is unknown or if the value could not be parsed. 
	 */
	public <TYPE> TYPE getValue(String fieldID) throws IOException
	{
		if (!this.readers.containsKey(fieldID))
			throw new IOException("Field " + fieldID + " not found.");
		return Generics.convertUnchecked(this.readers.get(fieldID).getValue());
	}

	/**
	 * Create a new reader instance for a specific model and specific raw data.
	 * @param model The model for the reader.
	 * @param rawData The raw data.
	 * @return The created instance.
	 */
	public static final BReader createFor(BModel model, byte[] rawData)
	{
		return new BReader(model, rawData);
	}

}
