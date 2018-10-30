/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A specific {@link IEAN128Parser} that is able to parse array based records.
 * @author renkenh
 */
public class EAN128ArrayParser implements IEAN128Parser
{
	
	private static final String REGEXP = "^n\\d\\+n(\\d)\\+a?n\\.{3}(\\d{1,2})$";
	/** Pattern to parse the db definition in {@link EAN128DB} to determine whether this type of parser is appropriate. */
	static final Pattern PATTERN = Pattern.compile(REGEXP);
	

	private final int valueLength;
	private final int maxCount;
	private final ArrayList<String> ret = new ArrayList<String>();

	
	/**
	 * Constructor.
	 */
	public EAN128ArrayParser(String def)
	{
		this.valueLength = parseDefValueLength(def);
		this.maxCount = parseDefMaxCount(def);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] parse(ByteBuffer bb, byte fnc1) throws ParseException
	{
		this.ret.clear();
		StringBuilder sb = new StringBuilder(this.valueLength);
		while (bb.hasRemaining())
		{
			byte b = bb.get();
			if (b == fnc1)
				break;
			sb.append((char) b);
			if (sb.length() == this.valueLength)
			{
				this.ret.add(sb.toString());
				sb = new StringBuilder(this.valueLength);
			}
		}
		if (this.ret.size() > this.maxCount)
			throw new ParseException("Could not parse current barcode. Value is not a variable EAN128 code.", bb.position());
		return this.ret.stream().toArray(String[]::new);
	}

	
	private static final int parseDefValueLength(String def)
	{
		Matcher matcher = PATTERN.matcher(def);
		if (!matcher.matches())
			throw new IllegalArgumentException(def + " does not represent an array length EAN128.");
		String length = matcher.group(1);
		if (length == null)
			throw new IllegalArgumentException(def + " does not represent an array length EAN128.");
		try
		{
			return Integer.parseInt(length);
		}
		catch (NumberFormatException ex)
		{
			throw new IllegalArgumentException(def + " does not represent an array length EAN128.", ex);
		}
	}

	private static final int parseDefMaxCount(String def)
	{
		Matcher matcher = PATTERN.matcher(def);
		if (!matcher.matches())
			throw new IllegalArgumentException(def + " does not represent an array length EAN128.");
		String slength = matcher.group(1);
		String svarLength = matcher.group(2);
		if (slength == null || svarLength == null)
			throw new IllegalArgumentException(def + " does not represent an array length EAN128.");
		try
		{
			int length = Integer.parseInt(slength);
			int varLength = Integer.parseInt(svarLength);
			
			if (varLength % length != 0)
				throw new IllegalArgumentException(def + " does not represent an array length EAN128.");
			return varLength / length + 1;
		}
		catch (NumberFormatException ex)
		{
			throw new IllegalArgumentException(def + " does not represent an array length EAN128.", ex);
		}
	}

}
