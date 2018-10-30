/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.bizerba;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import com.toennies.ci1429.app.util.ASCII;
import com.toennies.ci1429.app.util.ScaleUtil;


/**
 * Request parser for complex responses. Usually, this means weight data.
 * @author renkenh
 */
class WeightDataRequest implements IHardwareRequest
{
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final NumberFormat LOCAL_FORMAT = NumberFormat.getInstance(Locale.GERMANY);
	
	private final IMessage request;

	
	private enum Type
	{
		NETTO, BRUTTO, TARA, EICH, COUNTER, UNKOWN
	}

	
	/**
	 * Constructor.
	 */
	public WeightDataRequest(String request)
	{
		this(request.getBytes(CHARSET));
	}

	/**
	 * Constructor.
	 */
	public WeightDataRequest(byte[]... words)
	{
		this.request = words == null ? null : new Message(words);
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
	public HardwareResponse handleResponse(IMessage message)
	{
		HardwareResponse response = Responses.map2Result(message);
		if (response != null)
			return response;
		
		try
		{
			WeightData data = parseWeightData(message);
			return new HardwareResponse(data);
		}
		catch (ParseException ex)
		{
			return new HardwareResponse("Could not parse given weigh in message " + message + " at offset " + ex.getErrorOffset());
		}
	}
	
	private static final WeightData parseWeightData(IMessage message) throws ParseException
	{
		WeightData data = new WeightData();
		for (int i = 1; i < message.words().size(); i++)	//ignore header
		{
			byte[] word = message.words().get(i);
			Type type = parseType(word);
			switch (type)
			{
				case BRUTTO:
					double brutto = parseWeight(word);
					data.setBrutto(brutto);
					break;
				case NETTO:
					double netto = parseWeight(word);
					data.setNetto(netto);
					break;
				case TARA:
					double tara = parseWeight(word);
					data.setTara(tara);
					break;
				case EICH:
					int counter = parseEich(word);
					data.setCounter(counter);
					break;
				case COUNTER:
					if (data.getCounter() <= 0)
					{
						counter = parseCounter(word);
						data.setCounter(counter);
					}
					break;
				default:
					LOGGER.info("Unknown Receipt Identifier: {}", ASCII.formatHuman(word));
					break;
			}
		}
		return data;
	}

	private static final double parseWeight(byte[] word) throws ParseException
	{
		byte[] weightArray = new byte[word.length-4];
		System.arraycopy(word, 2, weightArray, 0, weightArray.length);
		byte[] unitArray = new byte[2];
		System.arraycopy(word, word.length-unitArray.length, unitArray, 0, unitArray.length);
		return parseWeightString(new String(weightArray, CHARSET)) * ScaleUtil.toGram(new String(unitArray, CHARSET));
	}

	protected static final double parseWeightString(String weigh) throws ParseException
	{
		weigh = weigh.trim();
		weigh = weigh.replaceAll(" ", "0");
		return LOCAL_FORMAT.parse(weigh).doubleValue();
	}


	private static final Type parseType(byte[] word)
	{
		switch (word[0])
		{
			case '+':
				return Type.BRUTTO;
			case ',':
			case '-':
			case '6':
				return Type.NETTO;
			case ' ':
			case '.':
			case '/':
			case '5':
				return Type.TARA;
			case 'Z':
				return Type.EICH;
			case '1':
			case '2':
			case '3':
			case '4':
			case '|':
				return Type.COUNTER;
			default:
				return Type.UNKOWN;
		}
	}
	
	
	private static final int parseEich(byte[] word) throws ParseException
	{
		if (word[1] == '0')
		{
			byte[] array = new byte[word.length-2];
			System.arraycopy(word, 2, array, 0, array.length);
			try
			{
				return Integer.parseInt(new String(array, CHARSET).trim());
			}
			catch (NumberFormatException ex)
			{
				throw new ParseException("Could not parse given eich counter in message: " + ASCII.formatHuman(word), 2);
			}
		}
		return -1;
	}

	private static final int parseCounter(byte[] word) throws ParseException
	{
		if (word[1] == '3' || word[1] == '7' || word[1] == '0' && word[2] == '0')
		{
			byte[] array = new byte[6];
			System.arraycopy(word, 3, array, 0, array.length);
			try
			{
				return Integer.parseInt(new String(array, CHARSET).trim());
			}
			catch (NumberFormatException ex)
			{
				throw new ParseException("Could not parse given counter in message: " + ASCII.formatHuman(word), 3);
			}
		}
		return -1;
	}

}
