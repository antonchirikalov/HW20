/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.printer;

import java.io.IOException;
import java.util.Map;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.printer.ICSTemplate;
import com.toennies.ci1429.app.model.printer.LabelData;
import com.toennies.ci1429.app.model.printer.Preview;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.connector.LoggingConnector;
import com.toennies.ci1429.app.network.connector.ReConnector;
import com.toennies.ci1429.app.network.connector.TCPPingCheckConnector;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.socket.ISocket;
import com.toennies.ci1429.app.network.socket.TCPSocket;
import com.toennies.ci1429.app.util.FTLUtils;

import freemarker.template.Template;

/**
 * Protocol for zebra printer. Does special handling if printer is accessed via
 * TCP socket.
 * 
 * @author renkenh
 * @see ZebraTCPConnector
 */
@AtProtocol(value = "Zebra Printer Protocol", deviceType = DeviceType.PRINTER)
public class ZebraPrinterProtocol extends APrinterProtocol
{

	private static final String TEMPLATE_NAME = "labelFieldsZPLPrinter";
	private static final Template LABELFIELDS_TEMPLATE = FTLUtils.getTemplate(TEMPLATE_NAME);

	@Override
	public void setup(Map<String, String> parameters) throws IOException
	{
		super.setup(parameters);
	}

	@Override
	protected String convertByTemplate(LabelData data) throws IOException
	{
		return FTLUtils.convert(data, LABELFIELDS_TEMPLATE);
	}

	@Override
	protected String convertToPrinterFormat(ICSTemplate template) throws IOException
	{
		return ICSTemplateToZPLConverter.convertToZPLFormat(template);
	}

	@Override
	protected IFlexibleConnector<byte[], byte[]> createPipeline(ISocket socket)
	{
		IConnector<byte[]> rawLogger = new LoggingConnector<>(socket);
		if (socket instanceof TCPSocket)
		{
			rawLogger = new ZebraTCPConnector(rawLogger);
			rawLogger = new TCPPingCheckConnector<>(rawLogger);
		}
		ReConnector<byte[]> reconnector = new ReConnector<>(rawLogger);
		return reconnector;
	}

	@Override
	public boolean supportsPreview()
	{
		return this.config().getEntry(PARAM_SOCKET).equals(TCPSocket.class.getName());
	}

	@Override
	protected Preview preview(LabelData labelData)
	{
		return ZebraPreviewUtil.getReview(labelData, this);
	}

}
