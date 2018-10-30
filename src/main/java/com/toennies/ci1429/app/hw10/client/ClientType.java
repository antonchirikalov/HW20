package com.toennies.ci1429.app.hw10.client;

import java.util.Arrays;

/**
 * This enum holds different types of hw10 clients, such as Meatline, HW10 test
 * and so on. Furthermore it also defines the EXE value of a client type such as
 * EXE00HWServer for the HW10 server test client.
 * 
 * @author Kai Stenzel
 *
 */
public enum ClientType
{
	// EXE value looks similar everytime it is provided by a client -> A fixed
	// value can be used here
	MEATLINE("EXE00WinTerm"), HW10_TEST("EXE00HWServer"), UNKNOWN("");

	private final String exe;

	private ClientType(String exe)
	{
		this.exe = exe;
	}

	/**
	 * This method is trying to identify a clients type by its executive. It
	 * compares it to a fixed set of client executives.
	 * 
	 * @param clientExe
	 *            the executive of the client which type should be identified,
	 *            e.g.: "EXE00WinTerm".
	 * @return the type matching to the given clientExe,
	 *         {@link ClientType#UNKNOWN} if no match could be found.
	 */
	public static ClientType getMatchingClientType(String clientExe)
	{
		return Arrays.asList(ClientType.values()).stream()
				.filter(m -> m.exe.toUpperCase().equals(clientExe.toUpperCase())).findAny()
				.orElseGet(() -> ClientType.UNKNOWN);
	}
}
