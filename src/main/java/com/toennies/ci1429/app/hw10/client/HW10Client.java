package com.toennies.ci1429.app.hw10.client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.DataTransformer;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.connector.IConnectorWrapper;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.connector.LoggingConnector;
import com.toennies.ci1429.app.network.event.AEventNotifier;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.event.IEventNotifier;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.parameter.AConfigContainer;
import com.toennies.ci1429.app.network.socket.ATCPSocket;
import com.toennies.ci1429.app.network.socket.AWireSocket;

/**
 * Implementation to create and use existing HW 1.0 client (e.g., ProFood,
 * Meatline) pipeline.
 * 
 * @author renkenh
 */
public class HW10Client extends AEventNotifier implements IEventHandler
{

	/**
	 * Published when data has been send to the hardware device. The event
	 * usually has a byte[] or an {@link IMessage} object as payload.
	 */
	public static final String EVENT_DATA_SEND = LoggingConnector.EVENT_DATA_SEND;
	/**
	 * Published when data has been received from the hardware device. The event
	 * usually has a byte[] or an {@link IMessage} object as payload.
	 */
	public static final String EVENT_DATA_RECEIVED = LoggingConnector.EVENT_DATA_RECEIVED;

	private final IFlexibleConnector<DeviceResponse, String> pipeline;

	/**
	 * Constructor.
	 * 
	 * @param socket
	 *            the socket is inherited from {@link HW10Server} in order to
	 *            set communication between the client and server.
	 */
	public HW10Client(Socket socket)
	{
		this.pipeline = this.createPipeline(socket);
		this.setupPipelineNotifier(this.pipeline);
	}

	/**
	 * This method initializes a {@link HW10Client}.
	 * <p>
	 * It is responsible for adding the Client ID to the sender queue of
	 * {@link HW10Connector} and starting the {@link ClientInitializer}-
	 * {@link Runnable} and the {@link CommandListener} - {@link Runnable}.
	 * 
	 * @throws IOException
	 *             when the connection through the pipeline timed out
	 */
	public void init() throws IOException
	{
		this.pipeline.connect(new AConfigContainer()
		{
			@Override
			protected Map<String, String> _config()
			{
				return defaultPipeConfig();
			}
		});
	}

	/**
	 * This method sends data to the remote system {@link #push(String)}.
	 */
	public void push(DeviceResponse response)
	{
		try
		{
			this.pipeline.push(response);
			this.handleEvent(EVENT_DATA_RECEIVED, this, response);
		}
		catch (IOException e)
		{
			// do nth, because no command was received after a certain time
		}
	}

	/**
	 * This method retrieves data from the remote system.
	 */
	public String take() throws IOException
	{
		try
		{
			return this.pipeline.pop();
		}
		catch (TimeoutException e)
		{
			return null;
		}
	}

	/**
	 * This method returns information about the health of the connection to the
	 * remote system.
	 */
	public boolean isConnected()
	{
		return this.pipeline.isConnected();
	}

	/**
	 * This method attempts to stop all executive tasks.
	 * <p>
	 * It is responsible to disconnect from the remote system
	 * {@link IConnector}.
	 */
	public void shutdown()
	{
		this.setdownPipelineNotifier(this.pipeline);
		this.pipeline.shutdown();
	}

	// FIXME this is copied from AProtocol
	private void setupPipelineNotifier(IFlexibleConnector<?, ?> connector)
	{
		if (connector instanceof IEventNotifier)
			((IEventNotifier) connector).registerEventHandler(this);
		if (connector instanceof IConnectorWrapper)
			setupPipelineNotifier(((IConnectorWrapper<?, ?, ?>) connector).getWrappedConnector());
	}

	/**
	 * This method creates the pipeline responsible for communication between
	 * the server and client(s).
	 * <p>
	 * Its role is to control handshaking process between server and the
	 * clients.
	 * 
	 * @return the connection status (the authentication performance)
	 */
	private IFlexibleConnector<DeviceResponse, String> createPipeline(Socket socket)
	{
		HW10ClientTCPSocket tcpSocket = new HW10ClientTCPSocket(socket);
		LoggingConnector<byte[]> logging = new LoggingConnector<>(tcpSocket);
		ADataTransformer envelope = new DataTransformer(new HW10MSGTransformer(), logging);
		HW10Connector connector = new HW10Connector(envelope);
		HW10RequestTransformer request = new HW10RequestTransformer(connector);
		HW10HandshakePerformer performer = new HW10HandshakePerformer(request);
		ResponseTransformer response = new ResponseTransformer(performer);
		return response;
	}

	// FIXME this is copied from AProtocol
	private void setdownPipelineNotifier(IFlexibleConnector<?, ?> connector)
	{
		if (connector instanceof IEventNotifier)
			((IEventNotifier) connector).unregisterEventHandler(this);
		if (connector instanceof IConnectorWrapper)
			setdownPipelineNotifier(((IConnectorWrapper<?, ?, ?>) connector).getWrappedConnector());
	}

	// FIXME this is copied from AProtocol
	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		this.publishEvent(eventID, params);
	}

	/**
	 * This method sets a default pipeline configuration.
	 * <p>
	 * Timeout is set to control the communication between the server and
	 * client(s).
	 * 
	 * @param the
	 *            PARAM_TIMEOUT is set for a period of five seconds to retrieve
	 *            clients requests (commands) {@link #init()}.
	 * @return the timeout parameter
	 */
	private static final Map<String, String> defaultPipeConfig()
	{
		Map<String, String> parameters = new HashMap<>();
		parameters.put(AWireSocket.PARAM_TIMEOUT, "5000");
		parameters.put(ATCPSocket.PARAM_PING, "false");
		return parameters;
	}

	private HW10HandshakePerformer getHandshaker()
	{
		return (HW10HandshakePerformer) ((IConnectorWrapper<?, ?, ?>) this.pipeline).getWrappedConnector();
	}

	/**
	 * Retrieves current process ID (unique decimal number) that is used to
	 * specify the process when attaching a debugger to it.
	 * <p>
	 * This ID is retrieved during the authentication phase (namely pipeline
	 * creation) {@link HW10HandshakePerformer #getPid()}.
	 * 
	 * @return the processes id sent from the client during the handshake
	 *         process.
	 */
	public String getPid()
	{
		return this.getHandshaker().getPid();
	}

	/**
	 * Retrieves executive file (in short EXE).
	 * 
	 * @return the executive file name is set during the handshake process
	 *         {@link HW10HandshakePerformer #getExe()}.
	 */
	public String getExe()
	{
		return this.getHandshaker().getExe();
	}

	/**
	 * Retrieves the folder directory (namely Pfad) where the HW is located.
	 *
	 * @return the name of directory is set the handshake process
	 *         {@link HW10HandshakePerformer #getPath()}.
	 */
	public String getPath()
	{
		return this.getHandshaker().getPath();
	}

	/**
	 * Retrieves last incremented ID.
	 * <p>
	 * This ID is retrieved from {@link HW10HandshakePerformer #getCid()} when
	 * the client is accepted from server {@link HW10Server}.
	 * 
	 * @return the client ID that is generated in
	 *         {@link HW10Server #generateNextClientId()} when the client is
	 *         admitted from the server.
	 */
	public String getID()
	{
		return this.getHandshaker().getCid();
	}

	public ClientType getType()
	{
		return this.getHandshaker().getClientType();
	}
}