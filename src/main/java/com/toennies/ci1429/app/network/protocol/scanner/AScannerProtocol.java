/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AProtocol;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.util.ASCII;


/**
 * Abstract scanner protocol.
 * Defines some parameters needed for the scanner network stack.
 * @author renkenh
 */
@Parameter(name=ADataTransformer.PARAM_FRAME_END, value="[CR][LF]", toolTip="Specifies the control characters defining the end of a scanned value. ASCII-control characters must be written in [], e.g. [SOH] or [CR]")
@Parameter(name=ADataTransformer.PARAM_FRAME_SEP, value="[GS]", toolTip="Specifies the control characters defining the blocks within an EAN128 code. ASCII-control characters must be written in [], e.g. [SOH] or [CR]")
@Parameter(name=AScannerProtocol.PARAM_REQUEST_TIMEOUT, value="14400000", typeInformation="int:0..", toolTip="The timeout of the scan request, i.e. when a scan request is aborted. In milliseconds.")
@Parameter(name=IProtocol.PARAM_DEFAULT_RESPONSE, value="STRING", typeInformation="enum:com.toennies.ci1429.app.model.ResponseFormat", toolTip="The default response used when no format is given on a device call.")
public abstract class AScannerProtocol extends AProtocol<IFlexibleConnector<Void, IExtendedMessage>, Void, IExtendedMessage>
{

	protected byte fnc1 = -1;
	private ResponseFormat defaultFormat;
	
	@Override
	public void setup(Map<String, String> parameters) throws IOException
	{
		super.setup(parameters);
//		this.charset = Charset.forName(this.config().getEntry(AScannerProtocol.PARAM_CHARSET));
		this.fnc1 = (byte) ASCII.parseHuman(this.config().getEntry(ADataTransformer.PARAM_FRAME_SEP)).charAt(0);
		this.defaultFormat = this.config().getEnumEntry(IProtocol.PARAM_DEFAULT_RESPONSE, ResponseFormat.class);
	}

	
	@Override
	protected Object _send(Object... params) throws IOException, TimeoutException
	{
		ResponseFormat format = params.length > 0 ? (ResponseFormat) params[0] : this.defaultFormat;
		return this.map(this.pipeline().pop(), format);
	}


	/**
	 * This is used to map the result of the protocol (which is an arbitrary object) to a client response object.
	 * This method is called for each request, for each response. The params parameters are the parameters of the
	 * request.
	 * @param protocolResponse The response from the protocol.
	 * @param params The parameters of the request.
	 * @return A client response. May never be <code>null</code>.
	 * @see #process(Object...)
	 */
	protected abstract Object map(IExtendedMessage rawData, ResponseFormat format) throws IOException;
	
}
