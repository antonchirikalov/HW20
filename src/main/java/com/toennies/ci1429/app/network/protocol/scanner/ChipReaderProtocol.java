/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.io.IOException;
import java.nio.charset.Charset;
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
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.connector.LoggingConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.socket.ISocket;
import com.toennies.ci1429.app.util.ASCII;

/**
 * Protocol that initiates the network stack for chip reader. It is based on the scanner protocol.
 * @author krawinkm
 */
@AtProtocol(value = "Chipreader Protocol", deviceType = DeviceType.SCANNER)
@Parameter(name=ADataTransformer.PARAM_FRAME_END, value="[CR]", toolTip="Specifies the control characters defining the end of a scanned value. ASCII-control characters must be written in [], e.g. [SOH] or [CR]")
@Parameter(name=ChipReaderProtocol.PARAM_CHARSET, value="US-ASCII", typeInformation="java:com.toennies.ci1429.app.network.parameter.typeinformation.CharsetValidator", toolTip="Specify the charset of the communication. Usually US-ASCII.")
public class ChipReaderProtocol extends AScannerProtocol
{

	/** Specify the charset used to interpret the byte stream. */
	public static final String PARAM_CHARSET = "charset";


	private Charset charset;
	
	
	@Override
	public void setup(Map<String, String> parameters) throws IOException
	{
		super.setup(parameters);
		this.charset = Charset.forName(this.config().getEntry(ChipReaderProtocol.PARAM_CHARSET));
	}

	
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
				long decimal = Long.parseLong(new String(rawData.word(0), this.charset), 16);
				return String.valueOf(decimal);
		}
	}


	@Override
	protected IFlexibleConnector<Void, IExtendedMessage> createPipeline(ISocket socket)
	{
		LoggingConnector<byte[]> logging = new LoggingConnector<>(socket);
		ADataTransformer transformer = new ConfigurableDataTransformer(logging);
		ScannerRequestTimoutConnector scanConnector = new ScannerRequestTimoutConnector(transformer);
		FlexibleExceptionConnector<Void, IExtendedMessage> exception = new FlexibleExceptionConnector<>(scanConnector);
		FlexibleReConnector<Void, IExtendedMessage> reconnector = new FlexibleReConnector<>(exception);
		return reconnector;
	}

}
