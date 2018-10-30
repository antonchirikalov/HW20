/**
 * 
 */
package com.toennies.ci1429.app.model.scanner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.toennies.ci1429.app.model.ADevice;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.ResponseType;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.network.protocol.scanner.AScannerProtocol;

/**
 * Device class "Scanner"
 * @author renkenh
 */
public class Scanner extends ADevice<AScannerProtocol>
{
	
	/**
	 * Constructor.
	 */
	public Scanner(IDeviceDescription description)
	{
		this.updateDevice(description);
	}


	/**
	 * You have to be aware of some things here in this method.
	 * 
	 * First, through batch parameter you can control that invoking instance
	 * expects serval scan results. This is quite helpful for printed labels
	 * that contain more than one barcode. So if there are two barcodes printed
	 * on one single label, you can invoke this method with batch=2. Inner while
	 * loop runs until result collection contains 2 distinct (!=equals) result
	 * elements. So you have to ensure, that there is a proper implementation of
	 * equals method for return type.
	 * 
	 * That brings us to second important thing! If the user never scans a
	 * second barcode, this method never will return. Be aware of this.
	 * 
	 * This feature was requested by ticket RFC0712-133
	 * 
	 * @param batch
	 *            decides, how many distinct scan results invoking instance
	 *            expects. Through this it's possible to perform several scan
	 *            procedures
	 * @return distinct scan results wrapped in a {@link Collection}. Returning
	 *         collection has the size of the given batch parameter.
	 */
	@Override
	public DeviceResponse batchProcess(int batch, Object... params)
	{
		final LinkedHashSet<Object> distinctResults = new LinkedHashSet<>();
		while (distinctResults.size() < batch)
		{
			DeviceResponse response = this.process(params);
			if (response.getStatus() != Status.OK_DATA)
				return response;

			if (response.getStatus().type == ResponseType.DATA)
				distinctResults.add(response.getPayload());
		}
		return new DeviceResponse(new ArrayList<>(distinctResults));
	}

}
