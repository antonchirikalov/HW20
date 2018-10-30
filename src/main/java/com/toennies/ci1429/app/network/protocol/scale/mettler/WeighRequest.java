/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.mettler;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;


/**
 * Weight request + response parser for mettler scales.
 * @author renkenh
 */
class WeighRequest implements IHardwareRequest
{

	private static final String EICH_PATTERN = "(\\d+)?";
	private static final String DATE_PATTERN = "(?:\\d{1,2}\\.\\d{1,2}\\.\\d{2,4})";
	private static final String TIME_PATTERN = "(?:\\d{1,2}:\\d{1,2}:\\d{1,2})";
	private static final String W = "\\s+";

	private final IMessage request;

	private final Pattern abBrutto;
	private final Pattern abNetto;
	private final Pattern abTara;
	private final Pattern abWeightData;

	/**
	 * Constructor.
	 */
	public WeighRequest(String request, IConfigContainer config)
	{
		this(request, config.getEntry(MettlerINDProtocol.PARAM_AB_BRUTTO),
					  config.getEntry(MettlerINDProtocol.PARAM_AB_NETTO),
					  config.getEntry(MettlerINDProtocol.PARAM_AB_TARA),
					  config.getEntry(MettlerINDProtocol.PARAM_AB_WEIGHT));
	}
	
	/**
	 * Constructor.
	 */
	public WeighRequest(String request, String abBrutto, String abNetto, String abTara, String abWeightData)
	{
		byte[] word = request.getBytes(CHARSET);
		this.request = new Message(word);
		
		this.abBrutto     = Pattern.compile(abBrutto + W + Responses.WEIGHT_PATTERN);
		this.abNetto      = Pattern.compile(abNetto  + W + Responses.WEIGHT_PATTERN);
		this.abTara       = Pattern.compile(abTara   + W + Responses.WEIGHT_PATTERN);
		this.abWeightData = Pattern.compile(abWeightData +  W + EICH_PATTERN +
															W + DATE_PATTERN +
															W + TIME_PATTERN +
															W + Responses.WEIGHT_PATTERN +
															W + Responses.WEIGHT_PATTERN +
															W + Responses.WEIGHT_PATTERN);
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
		HardwareResponse result = Responses.checkCommand(response, this.request.words().get(0)[0], this.request.words().get(0)[1]);
		if (result == HardwareResponse.OK)
			result = map2Result(response);
		if (result == null)
			return new HardwareResponse("Received unexpected message: " + response);
		if (result == HardwareResponse.OK)
		{
			try
			{
				WeightData data = parseWeightMessage(response);
				return new HardwareResponse(data);
			}
			catch (ParseException e)
			{
				return new HardwareResponse("Could not parse weight response. " + response);
			}
		}
		return result;
	}
	
	private final WeightData parseWeightMessage(IMessage response) throws ParseException
	{
		WeightData data = new WeightData();
		String input = new String(response.words().get(0), CHARSET);
		
		Double brutto = parseWeight(input, this.abBrutto);
		if (brutto != null)
			data.setBrutto(brutto.doubleValue());
		Double netto = parseWeight(input, this.abNetto);
		if (netto != null)
			data.setNetto(netto.doubleValue());
		Double tara = parseWeight(input, this.abTara);
		if (tara != null)
			data.setTara(tara.doubleValue());
		
		Matcher m = this.abWeightData.matcher(input);
		if (m.find())
		{
			
			String eich = m.group(1);
			if (eich != null && eich.length() > 0)
			{
				int eichCount = Integer.parseInt(eich);
				data.setCounter(eichCount);
			}
			
			if (brutto == null)
			{
				brutto = parseWeight(m.group(2) + " " + m.group(3));
				if (brutto != null)
					data.setBrutto(brutto.doubleValue());
			}
			if (netto == null)
			{
				netto = parseWeight(m.group(4) + " " + m.group(5));
				if (netto != null)
					data.setNetto(netto.doubleValue());
			}
			if (tara == null)
			{
				tara = parseWeight(m.group(6) + " " + m.group(7));
				if (tara != null)
					data.setTara(tara.doubleValue());
			}
		}
		return data;
	}

	private static final Double parseWeight(String input) throws ParseException
	{
		double weight = Responses.parseWeight(input);
		return Double.valueOf(weight);
	}
	
	private static final Double parseWeight(String input, Pattern regexp) throws ParseException
	{
		double weight = Responses.parseWeight(input, regexp);
		return Double.valueOf(weight);
	}
	
	private static final HardwareResponse map2Result(IMessage response)
	{
		switch (response.words().get(0)[2])
		{
			case ' ':
			case 'D':
				return HardwareResponse.OK;
			case 'I':
				return new HardwareResponse("Scale returned I for error.");
		}
		return null;
	}

}
