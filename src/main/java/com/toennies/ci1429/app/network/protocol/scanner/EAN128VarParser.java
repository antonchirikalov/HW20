/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parser for EAN128 based codes that is able to parse records of variable length.
 * @author renkenh
 */
public class EAN128VarParser implements IEAN128Parser
{

	private static final String REGEXP = "^n\\d\\+a?n\\.{3}(\\d{1,2})$";
	/** Pattern to parse the db definition in {@link EAN128DB} to determine whether this type of parser is appropriate. */
	static final Pattern PATTERN = Pattern.compile(REGEXP);
	

	private final int maxLength;
	private final String[] ret = new String[1];
	
	
	/**
	 * Constructor
	 */
	public EAN128VarParser(String def)
	{
		this.maxLength = parseDef(def);
	}

	/**
	 * @return Returns the max length a code from this parser can be.
	 */
	public int maxLength()
	{
		return this.maxLength;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] parse(ByteBuffer bb, byte fnc1) throws ParseException
	{
		StringBuilder sb = new StringBuilder(this.maxLength);
		while (bb.hasRemaining())
		{
			byte b = bb.get();
			if (b == fnc1)
				break;
			sb.append((char) b);
		}
		this.ret[0] = sb.toString();
		if (this.ret[0].length() > this.maxLength)
			throw new ParseException("Could not parse current barcode. Value is not a variable EAN128 code.", bb.position());
		return this.ret;
	}

	
	private static final int parseDef(String def)
	{
		Matcher matcher = PATTERN.matcher(def);
		if (!matcher.matches())
			throw new IllegalArgumentException(def + " does not represent an array length EAN128.");
		String length = matcher.group(1);
		if (length == null)
			throw new IllegalArgumentException(def + " does not represent a variable length EAN128.");
		try
		{
			return Integer.parseInt(length);
		}
		catch (NumberFormatException ex)
		{
			throw new IllegalArgumentException(def + " does not represent a variable length EAN128.", ex);
		}
	}
}
