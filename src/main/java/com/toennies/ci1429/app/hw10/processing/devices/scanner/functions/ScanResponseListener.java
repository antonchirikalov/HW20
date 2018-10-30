package com.toennies.ci1429.app.hw10.processing.devices.scanner.functions;

import com.toennies.ci1429.app.hw10.client.HW10Client;
import com.toennies.ci1429.app.hw10.util.CommandParser;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;

/**
 * All classes who want to register in the {@link HW10ScanRegistry} for a scan
 * response need to implement this interface
 * 
 * @author Kai Stenzel
 *
 */
public class ScanResponseListener
{

	private final HW10Client client;

	private final String scanCommand;

	public ScanResponseListener(HW10Client listener, String scanCommand)
	{
		this.client = listener;
		this.scanCommand = scanCommand;
	}

	/**
	 * By calling this method, the scan response is sent to the client. This is
	 * done by pushing the message through the {@link HW10Client}
	 * <p>
	 * If the scan response is a NAK, only the NAK is sent. If not the scan
	 * response is appended to the scan command prefix (see
	 * {@link CommandParser#getScanResultPrefixByCommand(String)}) and then sent
	 * to the client.
	 */
	public void onScanResponse(DeviceResponse scanResponse)
	{
		if (scanResponse.getStatus() != Status.OK_DATA)
		{
			client.push(scanResponse);
			return;
		}
		String responsePayload = scanResponse.getPayload();
		String response = CommandParser.getScanResultPrefixByCommand(scanCommand) + responsePayload;
		client.push(new DeviceResponse(response));
	}

	/**
	 * @return a String containing an identifier of a
	 *         {@link IScanResponseListener}
	 */
	public String getID()
	{
		return this.client.getID();
	}

}