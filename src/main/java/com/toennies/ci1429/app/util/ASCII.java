package com.toennies.ci1429.app.util;

/**
 * Helper method to process ASCII control characters.
 * @author renkenh
 */
public enum ASCII
{

	NUL('\u0000'),
	SOH('\u0001'),
	STX('\u0002'),
	ETX('\u0003'),
	EOT('\u0004'),
	ENQ('\u0005'),
	ACK('\u0006'),
	BEL('\u0007'),
	BS('\u0008'),
	HT('\u0009'),
	LF('\n'),
	VT('\u000B'),
	FF('\u000C'),
	CR('\r'),
	SO('\u000E'),
	SI('\u000F'),
	DLE('\u0010'),
	DC1('\u0011'),
	DC2('\u0012'),
	DC3('\u0013'),
	DC4('\u0014'),
	NAK('\u0015'),
	SYN('\u0016'),
	ETB('\u0017'),
	CAN('\u0018'),
	EM('\u0019'),
	SUB('\u001A'),
	ESC('\u001B'),
	FS('\u001C'),
	GS('\u001D'),
	RS('\u001E'),
	US('\u001F'),
	DEL('\u007F');


	/**
	 * Parses the "so called" human representation of ASCII control characters, like [NUL] into the actual ascii character, which is
	 * (in hex) \u0000.
	 * @param toParse The string to parse.
	 * @return The string where all human readable control character definitions are replaced by actual control characters.
	 */
	public static final String parseHuman(String toParse)
	{
		for (ASCII a : ASCII.values())
			toParse = toParse.replaceAll(a.regex, a.s);
		return toParse;
	}

	/**
	 * Formats a given string into a human readable form. It replaces all control characters by a human readable string like [NUL].
	 * @param rawData The raw ASCII data.
	 * @return A human readable string where all control characters are placed by their human readable counterpart.
	 */
	public static final String formatHuman(byte... rawData)
	{
		StringBuilder sb = new StringBuilder();
		for (byte b : rawData)
		{
			if (b >= 32 && b <= 126)
				sb.append((char) b);
			else if (b == DEL.code)
				sb.append(DEL);
			else if (b >= 0 && b < 32)
				sb.append(ASCII.values()[b]);
			else
			{
				sb.append('[');
				sb.append(b);
				sb.append("?]");
			}
		}
		return sb.toString();
	}
	
	
	/** The original ASCII character. */
	public final char c;
	private final String s;
	/** The byte code representation of the ASCII character. */
	public final byte code;
	private final String human;
	private final String regex;
	

	private ASCII(char c)
	{
		this.c = c;
		this.s = String.valueOf(c);
		this.code = (byte) c;
		this.human = '[' + this.name() + ']';
		this.regex = "\\[" + this.name() + "\\]";
	}


	@Override
	public String toString()
	{
		return this.human;
	}

}