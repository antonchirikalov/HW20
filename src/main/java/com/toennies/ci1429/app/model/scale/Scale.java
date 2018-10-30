/**
 * 
 */
package com.toennies.ci1429.app.model.scale;

import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.model.ADevice;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.network.protocol.scale.AScaleProtocol;


/**
 * The device class "Scale"
 * @author renkenh
 */
public class Scale extends ADevice<AScaleProtocol>
{

	/**
	 * Special logger. This logger has to be used to log into a special "scale.log" file.
	 * This logger is public and can be used throughout the hardware server to log scale related things into this special log-file.
	 */
	public static final Logger scaleLogger = LogManager.getLogger("scale");


	/**
	 * Enum that specifies available return units for weight data.
	 * @author renkenh
	 */
	public enum Unit
	{
		G, KG, T, LB
	}


	
	public Scale(IDeviceDescription description)
	{
		this.updateDevice(description);
	}

	
	@Override
	public @NotNull DeviceResponse process(Object... params)
	{
		DeviceResponse response = super.process(params);
		if (response.getPayload() instanceof WeightData)
		{
			WeightData data = response.getPayload();
			scaleLogger.info("Scale {}: Response: {}", this.getDeviceID(), WeightDataFormatter.formatWeightData(data, ResponseFormat.HUMAN, 2, Unit.KG));
		}
		else
			scaleLogger.info("Scale {}: Response: {}", this.getDeviceID(), response);
		return response;
	}
}
