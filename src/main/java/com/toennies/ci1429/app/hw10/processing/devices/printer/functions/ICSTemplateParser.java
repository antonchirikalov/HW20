package com.toennies.ci1429.app.hw10.processing.devices.printer.functions;

import com.toennies.ci1429.app.model.printer.ICSTemplate;

/**
 * Implementation to convert ICS to ZPL format.
 * 
 */
class ICSTemplateParser
{
	
	/**
	 * This method converts entire ICS to ZPL format.
	 * <p>
	 * It extracts head and body from the ICS format.
	 * 
	 * @param ics The whole HW1.0 command - including the command at the beginning.
	 * @return the ZPL format including head {@link #extractLabelHead(String[])}
	 *         and body {@link #extractLabelBody(String[])} of the label
	 */
	public static final ICSTemplate parseICSFormat(String ics)
	{
		ics = ics.substring(8); //cut off HW10 command
		ics = ics.replaceAll("\\s+", "");
		String[] arr = ics.split("\\^");
		
		ICSTemplate data = parseHeader(arr[0]);
		for (int i = 1; i < arr.length; i++)
		{
			parseField(arr[i], data);
		}

		return data;
//		sb.append(this.zplHead());
//		sb.append(this.extractLabelBody(splitArray));
//		sb.append("^XZ");
//		return sb.toString();
	}
	
	private static final ICSTemplate parseHeader(String headerString)
	{
		int density = Integer.parseInt(headerString.substring(0, 2));
		String speed = headerString.substring(2, 3);
		String charSet = headerString.substring(3, 5);
		int labelLength = Integer.parseInt(headerString.substring(5, 8));
		int homePosX = Integer.parseInt(headerString.substring(8, 10));
		int homePosY = Integer.parseInt(headerString.substring(10,  12));
		String direction = headerString.substring(12, 13);
		int quantity = Integer.parseInt(headerString.substring(13, 16));
		String additional = headerString.length() >= 17 ? headerString.substring(16) : "";
		return new ICSTemplate(density, speed, charSet, labelLength, homePosX, homePosY, direction, quantity, additional);
	}

	private static final void parseField(String fieldString, ICSTemplate data)
	{
		String fieldId = fieldString.substring(0, 2);
		int xPos = Integer.parseInt(fieldString.substring(2, 5));
		int yPos = Integer.parseInt(fieldString.substring(5, 8));
		int length = Integer.parseInt(fieldString.substring(8, 10));
		int font = Integer.parseInt(fieldString.substring(10, 12));
		int xWidth = Integer.parseInt(fieldString.substring(12, 15));
		int yWidth = Integer.parseInt(fieldString.substring(15, 18));
		String rotation = fieldString.substring(18, 19);
		String text = fieldString.length() >= 20 ? fieldString.substring(19) : "";
		data.addField(fieldId, xPos, yPos, length, font, xWidth, yWidth, rotation, text);
	}
	
}