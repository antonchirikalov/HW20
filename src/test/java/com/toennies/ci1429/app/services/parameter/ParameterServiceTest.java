package com.toennies.ci1429.app.services.parameter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import com.toennies.ci1429.app.network.parameter.ParameterInstance;
import com.toennies.ci1429.app.network.protocol.scanner.ChipReaderProtocol;
import com.toennies.ci1429.app.network.socket.TCPSocket;

public class ParameterServiceTest
{

	@Test
	public void getSocketClassesTest()
	{
		// Next collection only contains "socket" classes. See javadoc of
		// invoked method
		Collection<Class<?>> socketClasses = ParameterService.getSocketClasses();
		// TCPSocket is a socket. So class needs to be present in collection
		assertTrue(socketClasses.contains(TCPSocket.class));
		// ChipReaderProtocol is *not* a socket. So class is not present in
		// collection
		assertFalse(socketClasses.contains(ChipReaderProtocol.class));
	}

	@Test
	public void getSocketClassByNameTest()
	{
		// This checks are very similar to getSocketClassesTest tests.
		// Difference
		// is, that search for a class that is might be a socket class is bases
		// on canonical class name
		ParameterInstance tcpSocketParameters = ParameterService
				.getSocketClassByName(TCPSocket.class.getCanonicalName());
		ParameterInstance chipreaderParameters = ParameterService
				.getSocketClassByName(ChipReaderProtocol.class.getCanonicalName());

		assertTrue(tcpSocketParameters != null);
		assertTrue(chipreaderParameters == null);
	}

	@Test
	public void getProtocolClassesTest()
	{
		// Next collection only contains "protocol" classes. See javadoc of
		// invoked method
		Collection<Class<?>> protocolClasses = ParameterService.getProtocolClasses();
		assertFalse(protocolClasses.contains(TCPSocket.class));
		assertTrue(protocolClasses.contains(ChipReaderProtocol.class));
	}

	@Test
	public void getProtocolClassByNameTest()
	{
		ParameterInstance tcpSocketParameters = ParameterService
				.getProtocolClassByName(TCPSocket.class.getCanonicalName());
		ParameterInstance chipreaderParameters = ParameterService
				.getProtocolClassByName(ChipReaderProtocol.class.getCanonicalName());

		assertTrue(tcpSocketParameters == null);
		assertTrue(chipreaderParameters != null);
	}
}
