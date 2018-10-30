/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.BlockingByteBuffer;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.socket.AtSocket;
import com.toennies.ci1429.app.network.socket.ISocket;
import com.toennies.ci1429.app.util.ASCII;
import com.toennies.ci1429.app.util.IExecutors;


/**
 * A socket that mimic a datalogic PM9500 scanner. Supports health check and random scanning.
 * Always generates EAN128 codes of various length.
 * @author renkenh
 */
@AtSocket("Dummy PM9500 Scanner")
@Parameter(name=DummyScannerSocket.PARAM_GEN_FORMAT, isRequired=true, value="EAN128", typeInformation="enum:com.toennies.ci1429.app.network.protocol.scanner.DummyScannerSocket$GenFormat", toolTip="Specifies what type of values the socket should generate.")
@Parameter //workaround for bug in JDK
public class DummyScannerSocket implements ISocket
{
	
	/** Parameter for the dummy socket to specify which barcode types to generate. */
	public static final String PARAM_GEN_FORMAT = "Gen.Format";

	/**
	 * Enum with the possible types.
	 * @author renkenh
	 */
	public enum GenFormat
	{
		EAN128,
		NUMBERS,
		STRINGS
	}


	private static final String WORDS = "Cup Tea Help Tönnies Hardware Server Update Test Entry Device School Hat Mouse Trailer";
	private static final int MAX_BUFFER_SIZE = 4096;
	private static final int MIN_SCAN_DELAY_MS = 1000;
	private static final int MAX_SCAN_DELAY_MS = 5000;
	
	private final class AutoScanner implements Runnable
	{
		public void run()
		{
			if (DummyScannerSocket.this.isConnected())
			{
				try
				{
					byte[] rawbytes = DummyScannerSocket.this.genBarcode().getBytes(StandardCharsets.US_ASCII);
					try
					{
						DummyScannerSocket.this.buffer.pushData(rawbytes);
					}
					catch (InterruptedException e)
					{
						throw new RuntimeException(e);
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			DummyScannerSocket.this.scanExecutor.schedule(this, DummyScannerSocket.this.pollScanDelay(), TimeUnit.MILLISECONDS);
		}
	};


	private final static int MAX_COUNT = 3;
	private final Random random = new Random();
	private final BlockingByteBuffer buffer = new BlockingByteBuffer(MAX_BUFFER_SIZE);
	private final ScheduledExecutorService scanExecutor = Executors.newSingleThreadScheduledExecutor(IExecutors.NETWORK_FACTORY);
	{
		this.scanExecutor.schedule(new AutoScanner(), this.pollScanDelay(), TimeUnit.MILLISECONDS);
	}


	private volatile IConfigContainer config;
	private volatile int timeout; 


	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.timeout = config.getIntEntry(IFlexibleConnector.PARAM_TIMEOUT);
		this.config = config;
		this.buffer.clear();
	}
	
	private int pollScanDelay()
	{
		return (int) (MIN_SCAN_DELAY_MS + (this.random.nextGaussian() + 1) * 0.5 * (MAX_SCAN_DELAY_MS - MIN_SCAN_DELAY_MS));
	}

	@Override
	public boolean isConnected()
	{
		return this.config != null;
	}

	@Override
	public byte[] poll() throws IOException
	{
		return this.buffer.pollData();
	}

	@Override
	public byte[] pop() throws IOException, TimeoutException
	{
		return this.buffer.popData(this.timeout);
	}


	private String genBarcode()
	{
		GenFormat format = this.config.getEnumEntry(PARAM_GEN_FORMAT, GenFormat.class);
		switch (format)
		{
			case EAN128:
				return genEAN128();
			case NUMBERS:
				return this.genNumber();
			default:
			case STRINGS:
				return this.genString();
		}
	}
	
	private String genNumber()
	{
		return String.valueOf(this.random.nextInt(Integer.MAX_VALUE-10) + 1) + this.getFrameEnd();
	}
	
	private String genString()
	{
		String[] words = WORDS.split(" ");
		return words[this.random.nextInt(words.length)] + this.getFrameEnd();
	}
	
	private String genEAN128()
	{
		int count = this.random.nextInt(MAX_COUNT);
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < count)
		{
			EAN128DB db = EAN128DB.values()[this.random.nextInt(EAN128DB.values().length)];
			String value = this.genCode(db, true);
			if (value != null && value.length() > 0)
			{
				sb.append(db.getDB());
				sb.append(value);
				i++;
			}
		}
		i = 0;
		while (i < 1)
		{
			EAN128DB db = EAN128DB.values()[this.random.nextInt(EAN128DB.values().length)];
			String value = this.genCode(db, false);
			if (value != null && value.length() > 0)
			{
				sb.append(db.getDB());
				sb.append(value);
				i++;
			}
		}
		sb.append(this.getFrameEnd());
		return sb.toString();
	}
	
	private String genCode(EAN128DB db, boolean appendFnc1)
	{
		int size = 0;
		boolean var = false;
		if (db.parser() instanceof EAN128FixedParser)
			size = ((EAN128FixedParser) db.parser()).length();
		else if (db.parser() instanceof EAN128VarParser)
		{
			size = this.random.nextInt(((EAN128VarParser) db.parser()).maxLength());
			var = true;
		}
		if (size == 0)
			return null;
		IntStream s = this.random.ints(0, 10);
		StringBuilder ret = s.limit(size).<StringBuilder>collect(StringBuilder::new, (sb, i) -> sb.append(i), (a,b) -> new StringBuilder(a.toString() + b.toString()));
		if (var && appendFnc1)
		{
			Character c = this.getFnc1();
			if (c != null)
				ret.append(c.charValue());
		}
		return ret.toString();
	}

	private Character getFnc1()
	{
		String fnc1 = null;
		if (this.config instanceof NoSEPConfigContainer)
			fnc1 = ((NoSEPConfigContainer) this.config).frameSep();
		else if (this.config.hasEntry(ADataTransformer.PARAM_FRAME_SEP))
			fnc1 = this.config.getEntry(ADataTransformer.PARAM_FRAME_SEP);
		if (StringUtils.isBlank(fnc1))
			return null;
		return ASCII.parseHuman(fnc1).charAt(0);
	}
	
	private String getFrameEnd()
	{
		return ASCII.parseHuman(this.config.getEntry(ADataTransformer.PARAM_FRAME_END));
	}

	@Override
	public void push(byte[] entity) throws IOException
	{
		//do nothing
	}

	@Override
	public void disconnect()
	{
		this.config = null;
	}

	@Override
	public void shutdown()
	{
		this.disconnect();
		this.scanExecutor.shutdownNow();
	}
}
