/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.mettler;

import java.text.ParseException;

import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;


/**
 * {@link Command#TARE_WITH_VALUE} hardware request for mettler scales.
 * @author renkenh
 */
class TaraValueRequest implements IHardwareRequest
{


	private final IMessage request;
	
	
	/**
	 * Constructor.
	 */
	public TaraValueRequest(String request)
	{
		byte[] word = request.getBytes(CHARSET);
		this.request = new Message(word);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMessage getRequestMessage()
	{
		return this.request;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public HardwareResponse handleResponse(IMessage response)
	{
		HardwareResponse result = Responses.checkCommand(response, this.request);
		if (result == HardwareResponse.OK)
			result = Responses.map2Result(response);
		if (result == HardwareResponse.OK)
		{
			try
			{
				double weight = Responses.parseWeight(response.words().get(0), 4, 10);	//4,10 according to mettler documentation
				WeightData data = new WeightData();
				data.setTara(weight);
				return new HardwareResponse(data);
			}
			catch (ParseException e)
			{
				return new HardwareResponse("Could not parse tara response properly. " + response);
			}
		}
		if (result == null)
			return new HardwareResponse("Received unexpected message: " + response);
		return result;
	}

}
