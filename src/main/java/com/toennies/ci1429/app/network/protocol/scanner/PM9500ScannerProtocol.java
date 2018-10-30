/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Map;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.ConfigurableDataTransformer;
import com.toennies.ci1429.app.network.connector.FlexibleExceptionConnector;
import com.toennies.ci1429.app.network.connector.FlexibleReConnector;
import com.toennies.ci1429.app.network.connector.FlexibleTimedHealthCheckTransformer;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.connector.LoggingConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.socket.ISocket;
import com.toennies.ci1429.app.util.ASCII;


/**
 * Creates the network stack for the Datalogic PM9500 scanner series.
 * @author renkenh
 */
@AtProtocol(value = "Datalogic PM9500 Protocol", deviceType = DeviceType.SCANNER)
@Parameter(name=FlexibleTimedHealthCheckTransformer.PARAM_HEALTHCHECK, value="10000", typeInformation="int:0..", toolTip="The period of the health check. In milliseconds.")
@Parameter(name=IProtocol.PARAM_DEFAULT_RESPONSE, value="EAN128", typeInformation="enum:com.toennies.ci1429.app.model.ResponseFormat", toolTip="The default response used when no format is given on call.")
public class PM9500ScannerProtocol extends AScannerProtocol
{
	

	@Override
	protected Object map(IExtendedMessage rawData, ResponseFormat format) throws IOException
	{
		if (rawData == null)
			return new DeviceResponse(Status.BAD_DEVICE_RESPONSE, "No result.");
		
		switch (format)
		{
			case EAN128:
				try
				{
					Map<String, Object> ean128 = IEAN128Parser.parse(rawData.word(0), this.fnc1);
					return ean128;
				}
				catch (ParseException e)
				{
					return new IOException("Not a EAN128 code: " + rawData + " at position: " + e.getErrorOffset());
				}
			case HUMAN:
				return ASCII.formatHuman(rawData.getRawData());
			case RAW:
				return rawData.getRawData();
			default:
			case STRING:
				return new String(rawData.word(0), StandardCharsets.UTF_8);
		}
	}

	@Override
	protected IFlexibleConnector<Void, IExtendedMessage> createPipeline(ISocket socket)
	{
		LoggingConnector<byte[]> socketLogger = new LoggingConnector<>(socket);
		ADataTransformer transformer = new ConfigurableDataTransformer(socketLogger);
		ScannerRequestTimoutConnector scanConnector = new ScannerRequestTimoutConnector(transformer);
		FlexibleExceptionConnector<Void, IExtendedMessage> exception = new FlexibleExceptionConnector<>(scanConnector);
		FlexibleReConnector<Void, IExtendedMessage> reconnector = new FlexibleReConnector<>(exception);
		return reconnector;
	}

}
