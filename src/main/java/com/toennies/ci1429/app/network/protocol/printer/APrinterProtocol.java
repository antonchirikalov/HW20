/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.printer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.printer.ICSTemplate;
import com.toennies.ci1429.app.model.printer.LabelData;
import com.toennies.ci1429.app.model.printer.Preview;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AProtocol;


/**
 * Protocol basis for printer.
 * @author renkenh
 */
@Parameter(name=APrinterProtocol.PARAM_CHARSET, isRequired=true, value="US-ASCII", typeInformation="java:com.toennies.ci1429.app.network.parameter.typeinformation.CharsetValidator", toolTip="Specify the charset of the communication. Usually US-ASCII.")
@Parameter //Dummy Parameter. this is only because the repeatable annotation is not found by reflection when defined only once
public abstract class APrinterProtocol extends AProtocol<IFlexibleConnector<byte[], byte[]>, byte[], byte[]>
{

	/** the charset to use to convert printer data. */
	public static final String PARAM_CHARSET = "charset";
	

	private Charset charset;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Map<String, String> parameters) throws IOException
	{
		super.setup(parameters);
		this.charset = Charset.forName(parameters.get(PARAM_CHARSET));
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized Object _send(Object... params) throws IOException, TimeoutException
	{
		if (params == null || params.length == 0)
			throw new IllegalArgumentException("No data to send.");

		byte[] toPrint = null;
		if (params[0] instanceof LabelData)
		{
			LabelData labelData = (LabelData) params[0];
			if (labelData.isPreview())
			{
				if (!this.supportsPreview())
					return new DeviceResponse(Status.BAD_REQUEST, "Preview function not supported by this printer.");
				Preview preview = this.preview(labelData);
				if (preview == null)
					return new DeviceResponse(Status.BAD_GENERIC_ERROR, "Could not load preview. See log file.");
				return preview;
			}
			
			String data = this.convertByTemplate(labelData);
			toPrint = this.getBytes(data);
		}
		else if (params[0] instanceof ICSTemplate)
		{
			String data = this.convertToPrinterFormat((ICSTemplate) params[0]);
			toPrint = this.getBytes(data);
		}
		else if (params[0] instanceof String)
		{
			toPrint = this.getBytes(params[0].toString());
		}
		else if (params[0] instanceof byte[])
		{
			toPrint = (byte[]) params[0];
		}

		if (toPrint == null || toPrint.length == 0)
			throw new IllegalArgumentException("No data to send.");

//		this.publishLogEvent(Type.DEVICE_SEND, toPrint);
		this.pipeline().push(toPrint);
		return DeviceResponse.OK;
	}

	/** Abstract method to convert the given LabelData object into printable data. */
	protected abstract String convertByTemplate(LabelData data) throws IOException;

	/** Abstract method to convert the given LabelData object into printable data. */
	protected abstract String convertToPrinterFormat(ICSTemplate template) throws IOException;

	/**
	 * Returns whether the printer supports preview or not. This returns true even if the printer is not connected.
	 * Default to false
	 * @return Whether the printer (in general) supports label preview function or not.
	 */
	public boolean supportsPreview()
	{
		return false;
	}

	/**
	 * Method to overwrite to implement label preview function. 
	 * @param labelData The label data to preview.
	 * @return A preview or <code>null</code> if something went wrong or the printer is not reachable.
	 */
	protected Preview preview(LabelData labelData)
	{
		return null;
	}

	private byte[] getBytes(String toPrint)
	{
		return toPrint == null ? null : toPrint.getBytes(this.charset);
	}

}
