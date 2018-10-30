/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.printer;

import org.apache.commons.lang3.StringUtils;

import com.toennies.ci1429.app.hw10.processing.devices.printer.functions.PrinterModelImpl;
import com.toennies.ci1429.app.model.printer.ICSTemplate;
import com.toennies.ci1429.app.model.printer.ICSTemplate.FieldData;

/**
 * The class to convert a given Template model into zpl language.
 * @author renkenh
 */
class ICSTemplateToZPLConverter
{

	/**
	 * Enum containing all known font types.
	 * @author renkenh
	 */
	private enum ZebraFontType
	{
		_0,
		A(5, 9), B(7, 11), C(10, 18), D(10, 21), E(13, 26), F(40, 60), G(11, 11), H(24, 24),
		P, Q, R, S, T, U, V,
		BC,
		LG,
		GB;
		
		public final int xm;
		public final int ym;

		private ZebraFontType()
		{
			this(5, 9);
		}
		private ZebraFontType(int xm, int ym)
		{
			this.xm = xm;
			this.ym = ym;
		}
		
		/**
		 * The name of the font in ZPL (it differs from the names of the enum).
		 * @return The name of the font in zpl language.
		 */
		public String zpl()
		{
			if (this == _0)
				return "0";
			return this.name();
		}
		
		/**
		 * This methods set label font size.
		 * <p>
		 * It defines font-size of barcode (BC), logo (LG), or the text row (GB).
		 * 
		 * @return the Zebra ZPL font size depending on
		 */
		public static final ZebraFontType getZebraFontType(FieldData data)
		{
			if (data.font == 0)	//Default if empty
				return _0;
			
			if (data.font < 9)	//A - H
				return ZebraFontType.values()[A.ordinal() + data.font - 1];
			
			if (data.font >= 9 && data.font <= 15) //P - V
				return ZebraFontType.values()[P.ordinal() + data.font - 9];
			
			if (data.font >= 51 && data.font <= 63)	//barcode
				return BC;
			
			if (data.font == 98) //logo
				return LG;
			
			if (data.font == 99) //box or line
				return GB;
			
			return A;
		}

	}
	
	/**
	 * Uses a given ICSTemplate and converts it into zpl language.
	 * @param data The template to convert.
	 * @return A String containing all the zpl commands to send to a zpl printer.
	 */
	public static final String convertToZPLFormat(ICSTemplate data)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(zplHeader(data));
		for (FieldData field : data.getFields())
			sb.append(zplField(field));
		sb.append("^XZ");
		return sb.toString();
	}
	
	/**
	 * This methods sets the form of ZPL-head format.
	 * <p>
	 * It starts deleting old format to ~EF_EF
	 * <ul>
	 * <li>'PQ' sets the quantity of labels (German: Anzahl der Etiketten)</li>
	 * <li>'LL' sets the header length (German: Labellaenge einstellen).</li>
	 * <li>'LH' sets the header position (German: Homeposition enstellen).</li>
	 * <li>'FW' sets the label direction (German: Etikettausrichtung
	 * einstellen).</li>
	 * <li>'PR' sets the printing speed (German: Druckgeschw.)</li>
	 * <li>'CI' sets Command-Instruction-Prefix</li>
	 * </ul>
	 *
	 * @return the ZPL header format
	 */
	private static final String zplHeader(ICSTemplate data)
	{
		StringBuilder zpl = new StringBuilder();
		zpl.append("~EF_EF");	//delete old format
		zpl.append("^XA^DF"+PrinterModelImpl.TEMPLATE_NAME);
		zpl.append("^PQ" + data.printQuantity + ",0,1,Y");//FIXME + zplFormat.getAdditional()
		zpl.append("^LL" + (data.labelLength * data.density));
		zpl.append("^LH" + (data.homePosX * data.density) + "," + (data.homePosY * data.density));
		zpl.append("^FW" + data.direction);
		zpl.append("^PR" + data.printSpeed);
		zpl.append("^CI" + data.characterSet);
		return zpl.toString();
	}

	/**
	 * This method converts body of ICS to ZPL format.
	 * <p>
	 * First, it retrieves the position in {@link #getXandYPosition(long)}.
	 * <p>
	 * Second, it retrieves the font size by {@link #getZebraWriting(int)}
	 * 
	 * @return a zebra format command
	 */
	private static final String zplField(FieldData data)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("^FO" + (data.xPos * data.format.density) + "," + (data.yPos * data.format.density));
		ZebraFontType fontType = ZebraFontType.getZebraFontType(data);
		switch (fontType)
		{
			case BC:
				sb.append(generateBarcode(data));
				break;
			case LG:
				sb.append("^XG" + data.text + "," + data.xWidth + "," + data.yHeight);
				break;
			case GB:
				sb.append("^GB" + (data.xWidth * data.format.density) + "," + (data.yHeight * data.format.density) + ",3,B");
				break;
			default:
				sb.append("^A" + fontType.zpl() + data.rotation + "," + (data.yHeight * fontType.ym) + "," + (data.xWidth * fontType.xm));
				break;
		}
		sb.append(getZPLforNonLogo(data));
		
		sb.append("^FS");
		return sb.toString();
	}

	/**
	 * This method defines the field content.
	 * <p>
	 * It defines whether the content contains fixed Text or variable field
	 * (German: Haben wir hier ein varables Feld oder einen Fixtext?)
	 */
	private static final String getZPLforNonLogo(FieldData data)
	{
		ZebraFontType fontType = ZebraFontType.getZebraFontType(data);
		if (fontType == ZebraFontType.LG)
			return "";

		if (data.text.length() > 0)
			return "^FD" + data.text;

		String field = "^FN" + data.fieldId;
		if (data.font != 63)
			field += "^FA" + (data.length + (data.font == 61 ? 4 : 0));
		return field;
	}

	/**
	 * This method first sets a format based on the bound parameter.
	 * <p>
	 * Afterwards it replaces this format with specified values
	 * 
	 * @return the ZPL command for a Barcode
	 */
	private static final String generateBarcode(FieldData data)
	{
		String stmp = null;
		switch (data.font)
		{
			case 51: // Code 39 ohne Prfziffer
				stmp = "^B3a,,b,,";
				break;
			case 52: // Code 2/5 Interleave
				stmp = "^B2a,b,,,";
				break;
			case 53: // UPC-A
				stmp = "^BUa,b,,,";
				break;
			case 54: // EAN 13
				stmp = "^BEa,b,,";
				break;
			case 55: // LOGMARS
				stmp = "^BLa,b,";
				break;
			case 56: // Ansi Codebar
				stmp = "^BKa,,b,,";
				break;
			case 57: // Code 128
				stmp = "^BCa,b,,";
				break;
			case 58: // ITF 14
				stmp = "^BEa,b,,";
				break;
			case 59: // EAN 8
				stmp = "^BEa,b,,";
				break;
			case 60: // Code 39 mit Prüfziffer
				stmp = "^BEa,b,,";
				break;
			case 61: // EAN 128
				stmp = "^BCa,b,,,,D";
				break;
			case 62: // DataMatrix
				stmp = "^BXa,,200";
				break;
			case 63: // Expanded Databar
				stmp = "^BRa,6,b,,100,15";
				break;
			default:
				return "";
		}

		stmp = stmp.replaceAll("a", data.rotation);
		
		if (data.font == 63)
			stmp = stmp.replaceAll("b", String.valueOf(data.length));
		else
			stmp = stmp.replaceAll("b", String.valueOf(data.yHeight * data.format.density));
		
		if (data.font == 62 || data.font == 63)
			stmp = "^BY" + StringUtils.right(String.valueOf(data.xWidth), 2) + ",3," + data.yHeight + stmp;
		else
			stmp = "^BY" + StringUtils.right(String.valueOf(data.xWidth), 2) + ",3" + stmp;
		return stmp;
	}


	private ICSTemplateToZPLConverter()
	{
		//no instance
	}

}
