/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * An {@link IEAN128Parser} that is able to parse fixed records.
 * @author renkenh
 */
public class EAN128FixedParser implements IEAN128Parser
{

	private static final String REGEXP = "^n\\d\\+n(\\d{1,2})$";
	/** Pattern to parse the db definition in {@link EAN128DB} to determine whether this type of parser is appropriate. */
	static final Pattern PATTERN = Pattern.compile(REGEXP);
	

	private final String[] ret = new String[1];
	private final byte[] read;


	/**
	 * Constructor.
	 */
	public EAN128FixedParser(String def)
	{
		this.read = new byte[parseDef(def)];
	}

	
	/**
	 * @return The length of code blocks this parser will parse.
	 */
	public int length()
	{
		return this.read.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] parse(ByteBuffer bb, byte fnc1)
	{
		bb.get(this.read);
		this.ret[0] = new String(this.read);
		return this.ret;
	}

	
	private static final int parseDef(String def)
	{
		Matcher matcher = PATTERN.matcher(def);
		if (!matcher.matches())
			throw new IllegalArgumentException(def + " does not represent an array length EAN128.");
		String length = matcher.group(1);
		if (length == null)
			throw new IllegalArgumentException(def + " does not represent a fixed length EAN128.");
		try
		{
			return Integer.parseInt(length);
		}
		catch (NumberFormatException ex)
		{
			throw new IllegalArgumentException(def + " does not represent a fixed length EAN128.", ex);
		}
	}
}
