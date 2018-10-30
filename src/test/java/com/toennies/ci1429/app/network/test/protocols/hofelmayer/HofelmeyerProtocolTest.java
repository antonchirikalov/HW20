package com.toennies.ci1429.app.network.test.protocols.hofelmayer;


import com.toennies.ci1429.app.Application;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.message.MessageTransformer;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.HofelmeyerMSGTransformer;
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
public class HofelmeyerProtocolTest
{
	private static final Logger logger = LogManager.getLogger();
	private MessageTransformer transformer;

	@Mock
	private IConfigContainer config;
	@Autowired
	private TestSocket testSocket;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		when(config.getEntry(ADataTransformer.PARAM_FRAME_START)).thenReturn("<");
		when(config.getEntry(ADataTransformer.PARAM_FRAME_END)).thenReturn(">");
		when(config.hasEntry(ISocket.PARAM_TIMEOUT)).thenReturn(true);
		when(config.getIntEntry(ISocket.PARAM_TIMEOUT)).thenReturn(2000);
		logger.warn("Mocks initialized");
		transformer = new HofelmeyerMSGTransformer();

	}

	private Callable<Boolean> checkConnection(TestSocket testSocket)
	{

		return () ->
		{
			logger.info("TestSocket is connected: " + testSocket.isConnected());
			return testSocket.isConnected();
		};
	}

	@Test
	public void testHofelmeyerWeight() throws IOException, TimeoutException
	{
		when(config.getEntry(ScriptDevice.PARAM_TEST_SCRIPT)).thenReturn("testscripts/hofelmeyer/weight.script");

		testSocket.connect(config);
		await().atMost(1000, TimeUnit.MILLISECONDS).until(checkConnection(testSocket), equalTo(true));

		byte[] cmd = this.transformer.formatMessage(new Message("RN".getBytes()));
		testSocket.push(cmd);
		byte[] response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("<000002.03.1811:08  221  1010.0     0.0  1010.0kg     1   14142>[CR ][LF ]").getBytes()));
	}

	@Test
	public void testHofelmeyerClearTare() throws IOException, TimeoutException
	{
		when(config.getEntry(ScriptDevice.PARAM_TEST_SCRIPT)).thenReturn("testscripts/hofelmeyer/clear_tare.script");
		testSocket.connect(config);
		await().atMost(1000, TimeUnit.MILLISECONDS).until(checkConnection(testSocket), equalTo(true));

		byte[] cmd = this.transformer.formatMessage(new Message("TC".getBytes()));
		testSocket.push(cmd);
		byte[] response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("<00>[CR][LF]").getBytes()));


	}

	@Test
	public void testHofelMeyerTare() throws IOException, TimeoutException
	{
		when(config.getEntry(ScriptDevice.PARAM_TEST_SCRIPT)).thenReturn("testscripts/hofelmeyer/tare.script");
		testSocket.connect(config);
		await().atMost(1000, TimeUnit.MILLISECONDS).until(checkConnection(testSocket), equalTo(true));

		byte[] cmd = this.transformer.formatMessage(new Message("TA".getBytes()));
		testSocket.push(cmd);
		byte[] response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("<00>[CR][LF]").getBytes()));

	}

	@Test
	public void testHofelmeyerWeightInMotion() throws IOException, TimeoutException
	{
		when(config.getEntry(ScriptDevice.PARAM_TEST_SCRIPT)).thenReturn("testscripts/hofelmeyer/weight_in_motion.script");
		testSocket.connect(config);
		await().atMost(1000, TimeUnit.MILLISECONDS).until(checkConnection(testSocket), equalTo(true));

		byte[] cmd = this.transformer.formatMessage(new Message("RN".getBytes()));
		testSocket.push(cmd);
		byte[] response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("<13>[CR][LF]").getBytes()));

	}


	@Test
	public void testHofelmeyerZero() throws IOException, TimeoutException
	{
		when(config.getEntry(ScriptDevice.PARAM_TEST_SCRIPT)).thenReturn("testscripts/hofelmeyer/zero.script");
		testSocket.connect(config);
		await().atMost(1000, TimeUnit.MILLISECONDS).until(checkConnection(testSocket), equalTo(true));

		byte[] cmd = this.transformer.formatMessage(new Message("SZ".getBytes()));
		testSocket.push(cmd);
		byte[] response = testSocket.pop();
		assertTrue(Arrays.equals(response, ASCII.parseHuman("<00>[CR][LF]").getBytes()));
	}


}
