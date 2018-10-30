/**
 * 
 */
package com.toennies.ci1429.app.util.binary.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The model for the reader and writer. Contains {@link BField}s in a specific order.
 * Use the {@link BModelBuilder} to easily construct such a model.
 * @author renkenh
 */
public class BModel
{

	private final LinkedHashMap<String, BField> structure = new LinkedHashMap<>();
	
	
	/** Internal constructor, used by the model builder. */
	protected BModel(LinkedHashMap<String, BField> fields)
	{
		this.structure.putAll(fields);
		this.setupDynLengthFields();
	}

	/**
	 * Public constructor.
	 * @param structure The bfields of this model in a defined order.
	 */
	public BModel(BField[] structure)
	{
		for (BField field : structure)
			this.structure.put(field.id, field);
		this.setupDynLengthFields();
	}

	private void setupDynLengthFields()
	{
		for (BField field : this.structure.values())
		{
			if (field instanceof BDynField)
			{
				BDynField dynField = (BDynField) field;
				BDynLengthField lengthField = new BDynLengthField(dynField.getLengthRef());
				this.structure.put(lengthField.id, lengthField);
			}
		}
	}
	
	
	/**
	 * Returns a list of all known field ids - in the order specified during construction.
	 * @return A list of all known field ids.
	 */
	public List<String> getFieldIDs()
	{
		return new ArrayList<>(this.structure.keySet());
	}

	/**
	 * Returns the field belonging to the given id. <code>null</code> if the id is unknown.
	 * @param id The id of a field.
	 * @return The field with the given id.
	 */
	public BField getField(String id)
	{
		return this.structure.get(id);
	}

	/**
	 * Returns a list (maintaining the specified order) with all fields.
	 * @return A list with all known fields - in their specified order.
	 */
	public List<BField> getFields()
	{
		return new ArrayList<>(this.structure.values());
	}

}
