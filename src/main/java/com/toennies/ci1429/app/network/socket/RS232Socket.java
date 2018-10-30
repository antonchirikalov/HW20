package com.toennies.ci1429.app.network.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;

/**
 * Socket implementation for the RS232 protocol.
 * @author renkenh
 */
@AtSocket("RS232 Socket")
@Parameter(name = RS232Socket.PARAM_PORT, value = "COM1", isRequired = true, typeInformation="java:com.toennies.ci1429.app.network.parameter.typeinformation.ComPortTypeValidator", toolTip="The OS name of the port. On Windows usualy COM<n>. On Linux ttyS<n>.")
@Parameter(name = RS232Socket.PARAM_BAUDRATE, value = "R9600", typeInformation="enum:com.toennies.ci1429.app.network.socket.RS232Socket$BaudRate", toolTip="The baudrate of the connection. Must match the hardware settings.")
@Parameter(name = RS232Socket.PARAM_DATABITS, isRequired=true, typeInformation="int:7..8", toolTip="The databits usually 7 or 8. Must match the hardware settings.")
@Parameter(name = RS232Socket.PARAM_STOP_BITS, isRequired=true, typeInformation="enum:com.toennies.ci1429.app.network.socket.RS232Socket$StopBits", toolTip="Stopbits. Must match the hardware settings.")
@Parameter(name = RS232Socket.PARAM_PARITY, isRequired=true, typeInformation="enum:com.toennies.ci1429.app.network.socket.RS232Socket$Parity", toolTip="Parity. Must match the hardware settings.")
@Parameter(name = RS232Socket.PARAM_FLOW_CONTROL, isRequired=true, typeInformation="enum:com.toennies.ci1429.app.network.socket.RS232Socket$FlowControl", toolTip="Flow Control. Must match the hardware settings.")
public class RS232Socket extends AWireSocket implements ISocket
{

	/** The port on which to connect. e.g. COM1 or ttyS1 */
	public static final String PARAM_PORT = "port";
	/** The baudrate. Must be an int. */
	public static final String PARAM_BAUDRATE = "baudrate";
	/** The databits. Usually 7 or 8. */
	public static final String PARAM_DATABITS = "databits";
	/** The stop bits. Must be of type {@link StopBits}. */
	public static final String PARAM_STOP_BITS = "stopbits";
	/** Parameter for the parity. Must be of type {@link Parity}. */
	public static final String PARAM_PARITY = "parity";
	/** Parameter for the flow control. Must be of type {@link FlowControl}. */
	public static final String PARAM_FLOW_CONTROL = "flowcontrol";


	/**
	 * All allowed baud rates.
	 */
	public enum BaudRate
	{
		R2400,
		R4800,
		R9600,
		R19200,
		R57600,
		R115200;
		
		public int getRate()
		{
			String rate = this.name().substring(1);
			return Integer.parseInt(rate);
		}
	}

	/**
	 * This enum represents every possible value for flowcontrol parameter.
	 */
	public enum FlowControl
	{
		NONE(SerialPort.FLOW_CONTROL_DISABLED),
		CTS(SerialPort.FLOW_CONTROL_CTS_ENABLED),
		RTSCTS(SerialPort.FLOW_CONTROL_RTS_ENABLED | SerialPort.FLOW_CONTROL_CTS_ENABLED),
		DSR(SerialPort.FLOW_CONTROL_DSR_ENABLED),
		DTRDSR(SerialPort.FLOW_CONTROL_DTR_ENABLED | SerialPort.FLOW_CONTROL_DSR_ENABLED),
		XONXOFF(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED | SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED);
		
		
		private final int bitFlags;

		private FlowControl(int bitFlags)
		{
			this.bitFlags = bitFlags;
		}

		public int bitFlags()
		{
			return this.bitFlags;
		}
	}

	/**
	 * Parity according to the RS232 specifications.
	 */
	public enum Parity
	{
		NONE,
		ODD,
		EVEN,
		MARK,
		SPACE;
		
		public int bitFlags()
		{
			return this.ordinal();
		}

	}

	/**
	 * Possible stop bits definitions.
	 */
	public enum StopBits
	{
		ONE("1", SerialPort.ONE_STOP_BIT),
		ONE_POINT_FIVE("1.5", SerialPort.ONE_POINT_FIVE_STOP_BITS),
		TWO("2", SerialPort.TWO_STOP_BITS);
		
		private final String representation;
		private final int bitFlags;
		
		
		private StopBits(String rep, int flags)
		{
			this.representation = rep;
			this.bitFlags = flags;
		}
		
		public int bitFlags()
		{
			return this.bitFlags;
		}
		
		public static final StopBits parse(String value)
		{
			for (StopBits bits : StopBits.values())
				if (bits.representation.equals(value))
					return bits;
			throw new IllegalArgumentException("Could not find StopBits " + value);
		}
	}

	private SerialPort serialPort;


	@Override
	protected InputStream getInputStream()
	{
		return this.serialPort.getInputStream();
	}

	@Override
	protected OutputStream getOutputStream()
	{
		return this.serialPort.getOutputStream();
	}

	@Override
	protected void _setupWireSocket(IConfigContainer config) throws IOException
	{
		this.serialPort = connectTo(config);
	}



	@Override
	protected boolean _isConnected()
	{
		return serialPort != null && serialPort.isOpen();
	}

	@Override
	protected void _setdownWireSocket()
	{
//		Utils.close(this.getInputStream());
//		Utils.close(this.getOutputStream());
//		this.serialPort.removeDataListener();
		if (this.serialPort == null)
			return;
		
		this.serialPort.closePort();
		this.serialPort = null;
	}


	/**
	 * Convenient method to create and connect a serial port. Parses the values given and initializes the port with these. 
	 * @param config The config from which to parse the parameters.
	 * @return An open Serial port.
	 * @throws IOException If the port could not be opened.
	 */
	static final SerialPort connectTo(IConfigContainer config) throws IOException
	{

		String port = config.getEntry(PARAM_PORT);
		BaudRate baudrate = config.getEnumEntry(PARAM_BAUDRATE, BaudRate.class);
		int databits = config.getIntEntry(PARAM_DATABITS);
//		int timeout = config.getIntEntry(IFlexibleConnector.PARAM_TIMEOUT);
		StopBits stopbits = config.getEnumEntry(PARAM_STOP_BITS, StopBits.class);
		Parity parity = config.getEnumEntry(PARAM_PARITY, Parity.class);
		FlowControl flowcontrol = config.getEnumEntry(PARAM_FLOW_CONTROL, FlowControl.class);

		SerialPort serialPort = SerialPort.getCommPort(port);
		serialPort.setComPortParameters(baudrate.getRate(), databits, stopbits.bitFlags(), parity.bitFlags());
		//Seems as if this prevents the socket from being opened a second time
//		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, timeout, timeout);
		serialPort.setFlowControl(flowcontrol.bitFlags());
		//workaround to enable reading from inputstream - does not work without in v1.3.11
//		serialPort.addDataListener(new SerialPortDataListener() {
//			
//			@Override
//			public void serialEvent(SerialPortEvent event) {
//				//do nothing
//			}
//			
//			@Override
//			public int getListeningEvents() {
//				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
//			}
//		});

		if (!serialPort.openPort())
			throw new IOException("Could not open port " + port);

		return serialPort;
	}

}
