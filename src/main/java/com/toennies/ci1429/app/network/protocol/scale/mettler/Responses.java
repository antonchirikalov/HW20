/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.mettler;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import com.toennies.ci1429.app.util.ScaleUtil;

/**
 * Helper class to parse hardware responses.
 * @author renkenh
 */
class Responses
{

	/** Pattern to parse weight. */
	static final String WEIGHT_PATTERN = "(-?\\d+\\.\\d+\\s+)(kg|t|g)\\b";
	private static final Pattern PWEIGHT = Pattern.compile(WEIGHT_PATTERN);

	private static final NumberFormat LOCAL_FORMAT = NumberFormat.getInstance(Locale.US);
//	private static final Logger LOGGER = LogManager.getLogger();


	/**
	 * Check if the given response (request parameter) does match the expected response.
	 * @param response The expected response.
	 * @param request The actual response to check.
	 * @return A hardware response that indicates whether the two messages match.
	 */
	public static final HardwareResponse checkCommand(IMessage response, IMessage request)
	{
		return checkCommand(response, request.words().get(0)[0]);
	}
	
	/**
	 * Check if the given response (request parameter) does match the expected response.
	 * @param response The expected response.
	 * @param request The actual response to check.
	 * @return A hardware response that indicates whether the two messages match.
	 */
	public static final HardwareResponse checkCommand(IMessage response, byte... request)
	{
		if (response.words().size() != 1)
			return new HardwareResponse("Messages does not have expected size of one. " + response.toString());

		byte[] rawResponse = response.words().get(0);
		if (rawResponse[0] == 'E')
			return new HardwareResponse("Scale returned 'Error'");

		for (int i = 0; i < request.length; i++)
			if (request[i] != rawResponse[i])
				return new HardwareResponse("Scale returned wrong command. " + response.toString());
			
		return HardwareResponse.OK;
	}

	/**
	 * Maps a result to {@link HardwareResponse#OK} or an error hardware response.
	 * @param response The hardware result to map.
	 * @return The mapped response.
	 */
	public static final HardwareResponse map2Result(IMessage response)
	{
		switch (response.words().get(0)[1])
		{
			case 'A':
			case 'B':
				return HardwareResponse.OK;
			case '+':
			case '-':
				return new HardwareResponse("Got respone indicating an error.");
		}
		return null;
	}


	/**
	 * Helper method to parse a weight value.
	 * @param word The word to parse
	 * @param from The offset.
	 * @param length The length
	 * @return The weight if parsed successfully.
	 * @throws ParseException Is thrown if the specified array could not be parsed.
	 */
	public static final double parseWeight(byte[] word, int from, int length) throws ParseException
	{
		String input = new String(word, from, length, IHardwareRequest.CHARSET);
		return parseWeight(input);
	}

	/**
	 * Parse the given string as a weight value. Calls {@link #parseWeight(String, Pattern)} with the Pattern {@link #PWEIGHT}.
	 * @param input The string to parse
	 * @return The weight if parsed successfully.
	 * @throws ParseException Is thrown if the specified array could not be parsed.
	 */
	public static final double parseWeight(String input) throws ParseException
	{
		return parseWeight(input, PWEIGHT);
	}

	/**
	 * Parses the input as a weight value with the given pattern. The pattern must specify in group one
	 * the digits, in group two the unit.
	 * @param input The input to parse
	 * @param pattern The pattern to use.
	 * @return The weight if parsed successfully.
	 * @throws ParseException Is thrown if the specified array could not be parsed.
	 */
	public static final double parseWeight(String input, Pattern pattern) throws ParseException
	{
		Matcher m = pattern.matcher(input);
		if (m.find())
		{
			String weight = m.group(1);
			String unit = m.group(2);
			return parseWeightString(weight) * ScaleUtil.toGram(unit);
		}
		throw new ParseException("Could not parse given weight in message: " + input, -1);
	}

	/**
	 * Most simple parse method to simply parse the digits. Replaces spaces with '0'.
	 * @param weight The string to parse. Should not contain any unit specifications.
	 * @return A number.
	 * @throws ParseException If the given string could not be parsed.
	 */
	public static final double parseWeightString(String weight) throws ParseException
	{
		weight = weight.trim();
		weight = weight.replaceAll(" ", "0");
		return LOCAL_FORMAT.parse(weight).doubleValue();
	}


	private Responses()
	{
		//no instance
	}

}
