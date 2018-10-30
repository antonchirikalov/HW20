package com.toennies.ci1429.app.hw10.util;

/**
 * Implementation to control handshake performance. Returns <code>true</code> if
 * the command(s) received (i.e., {@link ClientInfoType}}) match commands during
 * authentication phase.
 * 
 * @author renkenh
 */
public class ClientInfoUtil
{


	public enum ClientInfoType
	{
		PID, EXE, PFD;
	}

	private ClientInfoUtil()
	{
		// do nth, there must not be an instance of this class
	}

	/**
	 * This method extracts prefix information from commands received during the
	 * handshaking process (e.g., PID001220 -> PID).
	 * 
	 * @return the prefix information extracted during handshake process
	 */
	public static ClientInfoType getInfoType(String clientInfo)
	{
		if (clientInfo.length() < 3)
			return null;

		return ClientInfoType.valueOf(clientInfo.substring(0, 3).toUpperCase());
	}

	

}