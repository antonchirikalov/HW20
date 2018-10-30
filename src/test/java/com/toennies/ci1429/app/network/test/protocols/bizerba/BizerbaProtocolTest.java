package com.toennies.ci1429.app.network.test.protocols.bizerba;

import com.toennies.ci1429.app.Application;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.socket.ISocket;
import com.toennies.ci1429.app.network.test.ScriptDevice;
import com.toennies.ci1429.app.network.test.TestSocket;
import com.toennies.ci1429.app.util.ASCII;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.assertTrue;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class BizerbaProtocolTest
{

	private static final Logger logger = LogManager.getLogger();

	@Mock
	private IConfigContainer config;
	@Autowired
	private TestSocket testSocket;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		when(config.getEntry(ADataTransformer.PARAM_FRAME_END)).thenReturn("[ETB]");
		when(config.hasEntry(ISocket.PARAM_TIMEOUT)).thenReturn(true);
		when(config.getIntEntry(ISocket.PARAM_TIMEOUT)).thenReturn(2000);
	}


	private Callable<Boolean> checkConnection(TestSocket testSocket){

		return () ->
		{
			logger.info("TestSocket is connected: " + testSocket.isConnected());
			return testSocket.isConnected();
		};
	}



	@Test
	public void testBizerbaItemAddingScript() throws IOException, TimeoutException, InterruptedException
	{
		when(config.getEntry(ScriptDevice.PARAM_TEST_SCRIPT)).thenReturn("testscripts/bizerba/bizerba_item_adding.script");


		testSocket.connect(config);

		await().atMost(1000, TimeUnit.MILLISECONDS).until(checkConnection(testSocket), equalTo(true) );


		testSocket.push(ASCII.parseHuman("[ENQ]").getBytes());
		byte[] response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("[ACK]").getBytes()));

		testSocket.push(ASCII.parseHuman("[SOH]00010[ETX]qY[ETB]").getBytes());


		testSocket.pop();
		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("[ACK]").getBytes()));


		testSocket.pop();
		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("[ENQ]").getBytes()));

		testSocket.push(ASCII.parseHuman("[ACK]").getBytes());
		testSocket.pop();
		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("[SOH]08001[ETX]w").getBytes()));


		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("5[ETB]").getBytes()));

		testSocket.push(ASCII.parseHuman("[ACK]").getBytes());
		testSocket.pop();
		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("[ENQ]").getBytes()));
		testSocket.push(ASCII.parseHuman("[ACK]").getBytes());

		testSocket.pop();
		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("[SOH]02001[ETX]]").getBytes()));


		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("1[ETX]Z01429").getBytes()));


		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("31[ETX]+!   0101,0kg").getBytes()));


		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("[ETX]   0000").getBytes()));


		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman(",0kg[ETX]]1[ETX]17002172[ETX]+!   01").getBytes()));


		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("01,0kg[ETX]   0000,0").getBytes()));


		response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("kg[ETX]+!   0101,0kg[ETB]").getBytes()));

		testSocket.push(ASCII.parseHuman("[ACK]").getBytes());
		await().atMost(5000, TimeUnit.MILLISECONDS).until(checkConnection(testSocket), equalTo(false) );

	}

	@Test
	public void testBizerbaWeightScript()
	{
		when(config.getEntry(ScriptDevice.PARAM_TEST_SCRIPT)).thenReturn("testscripts/bizerba/bizerba_weight.script");
		when(config.getEntry(ISocket.PARAM_TIMEOUT)).thenReturn("2000");

	}


}
