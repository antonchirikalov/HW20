package com.toennies.ci1429.app.hw10.processing.devices.printer.functions;

import com.toennies.ci1429.app.model.printer.LabelData;
import com.toennies.ci1429.app.model.printer.LabelData.Value;

/**
 * Implementation of Zebra printer - converting printing data to ZPL format.
 */
class ICSLabelDataParser
{

	/**
	 * Parse the given ics string into a LabelData object.
	 * @param toParse The whole HW10 command (including the command itself).
	 * @return The parsed labeldata object.
	 */
	public static final LabelData parseToLabelData(String toParse)	//FIXME add handling of comments
	{
		toParse = toParse.substring(8); //cut off HW10 command

		final LabelData data = new LabelData();
		data.setTemplate(PrinterModelImpl.TEMPLATE_NAME);
		
		String[] fields = toParse.split("\\^");
		for (String field : fields)
			if (field.length() >= 2)	//if completely empty do nothing
			{
				String key = getKey(field);
				String value = getValue(field);
				data.addField(key, new Value(value));
			}

		return data;
	}
	
	private static final String getKey(String field)
	{
		return field.substring(0, 2);
	}
	
	private static final String getValue(String field)
	{
		if (field.length() <= 2)
			return "";
		return field.substring(2);
	}
	
}