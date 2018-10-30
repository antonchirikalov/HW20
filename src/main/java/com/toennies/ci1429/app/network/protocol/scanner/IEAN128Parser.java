/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


/**
 * Parser interface for EAN128 based codes.
 * @author renkenh
 */
public interface IEAN128Parser
{
	
	/**
	 * Method to parse the given byte buffer with this parser. Returns all found strings. 
	 * @param bb The byte buffer to parse.
	 * @param fnc1 The function code to separate records.
	 * @return An array with all codes found.
	 * @throws ParseException If the code could not be parsed.
	 */
	public abstract String[] parse(ByteBuffer bb, byte fnc1) throws ParseException;
	

	/**
	 * Static method to parse the whole code. Does delegation to the different parsers implementing this interface.
	 * @param rawData The data to parse.
	 * @param fnc1 The separator.
	 * @return A map containing all codes found. The key is the EAN128 code, the value is the result. Can be a single string or an string array.
	 * @throws ParseException If the code could not be parsed (because it is not a valid EAN128 code).
	 */
	public static Map<String, Object> parse(byte[] rawData, byte fnc1) throws ParseException
	{
		Map<String, Object> ret = new HashMap<>();
		//strip ending
		ByteBuffer bb = ByteBuffer.wrap(rawData, 0, rawData.length);
		while (bb.hasRemaining())
		{
			EAN128DB ean = EAN128DB.get(bb);
			if (ean == null)
				throw new ParseException("Not an EAN128 barcode.", bb.position());
			String[] arr = ean.parseData(bb, fnc1);
			if (arr == null)
				throw new ParseException("Not an EAN128 barcode.", bb.position());
			if (arr.length == 1)
				ret.put(String.valueOf(ean.getDB()), arr[0]);
			else if (arr.length > 1)
				ret.put(String.valueOf(ean.getDB()), arr);
		}
		return ret;
	}
	
}
