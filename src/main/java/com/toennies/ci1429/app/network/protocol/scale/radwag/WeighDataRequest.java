package com.toennies.ci1429.app.network.protocol.scale.radwag;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import com.toennies.ci1429.app.util.ScaleUtil;

class WeighDataRequest implements IHardwareRequest
{

	private static final Pattern WEIGH_VALUE_PATTERN = Pattern.compile("(-?\\s*?\\d+\\.\\d+)");

	private static final Pattern WEIGH_UNIT_PATTERN = Pattern.compile("(g|kg|t)");

	private static final NumberFormat LOCAL_FORMAT = NumberFormat.getInstance(Locale.US);

	private enum Type
	{

		NETTO(Pattern.compile("N" + WEIGH_VALUE_PATTERN.pattern() + WEIGH_UNIT_PATTERN.pattern())), BRUTTO(
				Pattern.compile("B" + WEIGH_VALUE_PATTERN.pattern() + WEIGH_UNIT_PATTERN.pattern())), TARA(
						Pattern.compile("T" + WEIGH_VALUE_PATTERN.pattern() + WEIGH_UNIT_PATTERN.pattern()));

		private final Pattern pattern;

		private Type(Pattern pattern)
		{
			this.pattern = pattern;
		}

		public static Type getMatchingType(String weightData)
		{
			return Arrays.asList(Type.values()).stream().filter(m -> m.pattern.matcher(weightData).matches()).findAny()
					.orElseGet(() -> NETTO);
		}

	}

	private final IMessage request;

	public WeighDataRequest(String request)
	{
		this.request = new Message(request.getBytes(CHARSET));
	}

	@Override
	public IMessage getRequestMessage()
	{
		return this.request;
	}

	@Override
	public HardwareResponse handleResponse(IMessage response)
	{
		HardwareResponse resp = Responses.map2Result(response, request.words().get(0));
		if (resp != null)
			return resp;
		try
		{
			WeightData data = this.parseWeightData(response);
			return new HardwareResponse(data);
		}
		catch (ParseException e)
		{
			return new HardwareResponse(
					"Could not parse given weigh in message " + response + " at offset " + e.getErrorOffset());
		}
	}

	private WeightData parseWeightData(IMessage msg) throws ParseException
	{
		WeightData data = new WeightData();
		for (byte[] word : msg.words())
		{
			String string = new String(word, CHARSET);
			Type type = Type.getMatchingType(string);
			this.setWeightData(data, type, string);
		}
		return data;
	}

	private void setWeightData(WeightData data, Type dataType, String weight) throws ParseException
	{
		switch (dataType)
		{
			case BRUTTO:
				data.setBrutto(this.getWeightValue(weight));
				break;
			case NETTO:
				data.setNetto(this.getWeightValue(weight));
				break;
			case TARA:
				data.setTara(this.getWeightValue(weight));
				break;
			default:
				break;

		}
	}

	private double getWeightValue(String weight) throws ParseException
	{
		Matcher valueMatcher = WEIGH_VALUE_PATTERN.matcher(weight);
		String weightValue = (valueMatcher.find()) ? valueMatcher.group() : "0";
		weightValue = weightValue.replaceAll("\\s", "");
		Matcher unitMatcher = WEIGH_UNIT_PATTERN.matcher(weight);
		String unitValue = (unitMatcher.find()) ? unitMatcher.group() : "kg";
		double value = this.parseWeightString(weightValue) * ScaleUtil.toGram(unitValue);

		return value;
	}

	private double parseWeightString(String weigh) throws ParseException
	{
		return LOCAL_FORMAT.parse(weigh).doubleValue();
	}

}
