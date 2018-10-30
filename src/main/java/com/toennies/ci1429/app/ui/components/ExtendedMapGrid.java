package com.toennies.ci1429.app.ui.components;

import java.util.Collections;
import java.util.Map;

import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.toennies.ci1501.lib.components.MapGrid;

/**
 * Grid class for displaying {@link ParamDescriptor} information stored in a
 * Map.
 * //FIXME this has to be integrated into the MapGrid! (ci1505lib)
 */
@SuppressWarnings("serial")
public class ExtendedMapGrid extends MapGrid<String, String>
{

	/**
	 * Creates a new grid with a given caption and two columns.
	 * @param caption The caption of the grid.
	 * @param column1caption First column name (displayed).
	 * @param column2caption Second column name (displayed).
	 */
	public ExtendedMapGrid(String caption, String column1caption, String column2caption) {
		this(Collections.emptyMap(), caption, column1caption, column2caption);
	}

	/**
	 * Creates a new grid with a given caption and two columns and the given data.
	 * @param data The data to show in this grid initially. May not be <code>null</code>.
	 * @param caption The caption of the grid.
	 * @param column1caption First column name (displayed).
	 * @param column2caption Second column name (displayed).
	 */
	public ExtendedMapGrid(Map<String, String> data, String caption, String column1caption, String column2caption)
	{
		super(data, caption, column1caption, column2caption);
	}

	/**
	 * This method can be used to update the displayed data.
	 */
	public void setRows(Map<String, String> userEnteredData)
	{
		this.getContainerDataSource().removeAllItems();
		if (userEnteredData != null)
			this.addRows(userEnteredData);
	}

}
