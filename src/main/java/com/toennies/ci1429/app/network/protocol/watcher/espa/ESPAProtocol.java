package com.toennies.ci1429.app.network.protocol.watcher.espa;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.DataTransformer;
import com.toennies.ci1429.app.network.connector.ExceptionConnector;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.connector.LoggingConnector;
import com.toennies.ci1429.app.network.connector.ReConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.protocol.watcher.AWatcherProtocol;
import com.toennies.ci1429.app.network.protocol.watcher.espa.data.ESPACall;
import com.toennies.ci1429.app.network.socket.ISocket;
import com.toennies.ci1429.app.util.IExecutors;


/**
 * The ESPA protocol. This protocol is based on the watcher (monitoring) architecture.
 * @author renkenh
 */
@Parameter(name=ESPAProtocol.PARAM_ADDRESS, value="2", isRequired=true, typeInformation="int:1..", toolTip="The address of the hardware server on the ESPA bus.")
@Parameter(name=ESPAProtocol.PARAM_CRUDE_MASTER, value="false", typeInformation="boolean", toolTip="If the master is a 'crude' one, i.e. if the master does not want [ACK] send.")
@Parameter(name=ESPAProtocol.PARAM_HEALTHCHECK, value="3000", typeInformation="int:0..", toolTip="The period of the health check. In milliseconds.")
@AtProtocol(value="ESPA Protocol", deviceType=DeviceType.WATCHER)
public class ESPAProtocol extends AWatcherProtocol<ESPACall>
{

	/** Whether the master is a crude one, that does not want ACK to be send after sending an espa call. */
	public static final String PARAM_CRUDE_MASTER = "crudemaster";
	/** The address of this client on the ESPA bus protocol. */
	public static final String PARAM_ADDRESS = "espaaddress";
	/** The timeout for the health check. */
	public static final String PARAM_HEALTHCHECK = "healthcheck";

	
	private class Repop implements Runnable
	{
		@Override
		public void run()
		{
			if (!ESPAProtocol.this.isConnected())
			{
				ESPAProtocol.this.executor.schedule(this, 1, TimeUnit.SECONDS);
				return;
			}
			try
			{
				Object event = ESPAProtocol.this.pipeline().pop();
				ESPAProtocol.this.handleEvent(event);
			}
			catch (TimeoutException ex)
			{
				//ignore this. timeout happens if no one scans for a certain amount of time.
				//instead use high level request-timeout in ScannerConnector to timeout request
			}
			catch (IOException ex)
			{
				logger.error("Could not connect to device.", ex);
				ESPAProtocol.this.handleEvent(ex);
			}
			if (!ESPAProtocol.this.executor.isTerminated())
				ESPAProtocol.this.executor.execute(new Repop());
		}
	}
	

	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(IExecutors.NETWORK_FACTORY);
	{
		this.executor.execute(new Repop());
	}

	
	private void handleEvent(Object event)
	{
		if (event instanceof ESPACall)
		{
			ESPAFault fault = new ESPAFault(this.config().getEntry(PARAM_NAME), (ESPACall) event);
//			this.handleEvent(EVENT_WATCH_EVENT, this, fault);	//FIXME reactivate - use pipeline notifier for this
		}
		//FIXME publish errors
		//handle other commun. problems in poll&select protocol? or just ignore and wait for correctly working bus again?
	}


	@Override
	protected IConnector<ESPACall> createPipeline(ISocket socket)
	{
		LoggingConnector<byte[]> rawLogger = new LoggingConnector<>(socket);
		ADataTransformer transformer = new DataTransformer(new ESPAMSGTransformer(), rawLogger);
		ESPAHealthcheck<IMessage, IExtendedMessage> healthCheck = new ESPAHealthcheck<>(transformer);
		ESPAConnector<ESPACall> connector = new ESPAConnector<>(healthCheck);
		LoggingConnector<ESPACall> espaLogger = new LoggingConnector<>(connector);
		ExceptionConnector<ESPACall> exception = new ExceptionConnector<>(espaLogger);
		ReConnector<ESPACall> reconnector = new ReConnector<>(exception);
		return reconnector;
	}

}
