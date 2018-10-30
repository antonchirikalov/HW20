package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.requests;

import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.Responses;
import com.toennies.ci1429.app.util.CRC16;
import com.toennies.ci1429.app.util.ScaleUtil;
import org.apache.commons.lang3.StringUtils;


import java.math.BigDecimal;
import java.util.Arrays;

public class WeightDataRequest implements IHardwareRequest
{
	private static final int NETTO_VALUE_POSITION = 38;
	private static final int BRUTTO_VALUE_POSITION = 24;
	private static final int DOUBLE_VALUE_LENGTH = 8;

	private static final int CRC_VALUE_POSITION = 54;
	private static final int CRC_VALUE_LENGTH = 8;

	private static final int WEIGHT_UNIT_VALUE_POSITION = 46;
	private static final int WEIGHT_UNIT_VALUE_LENGTH = 2;

	private final IMessage request;

	public WeightDataRequest(String request)
	{
		this.request = new Message(request.getBytes(CHARSET));
	}

	@Override
	public IMessage getRequestMessage()
	{
		return request;
	}

	@Override
	public HardwareResponse handleResponse(IMessage responseMessage)
	{
		HardwareResponse response = Responses.map2Result(responseMessage);
		if (response != null)
			return response;

		if (!validateMessageCRC(responseMessage))
		{
			return new HardwareResponse("CRC16 checksum does not match");
		}

		WeightData weightData = populateWeightData(responseMessage);

		return new HardwareResponse(weightData);
	}

	private boolean validateMessageCRC(IMessage message)
	{
		int messageCRC = getCRCFromMessage(message);
		int calculatedCRC = calculateMessageCRC(message);

		return (messageCRC == calculatedCRC);
	}

	private int calculateMessageCRC(IMessage message)
	{
		return CRC16.crc16(getMessageWithoutCRC(message));
	}

	private byte[] getMessageWithoutCRC(IMessage message)
	{
		byte[] response = message.words().get(0);
		return Arrays.copyOf(response, response.length - CRC_VALUE_LENGTH);
	}

	private int getCRCFromMessage(IMessage message)
	{
		return getIntegerFromMessage(message.words().get(0), CRC_VALUE_POSITION, CRC_VALUE_LENGTH);
	}

	private WeightData populateWeightData(IMessage message)
	{
		WeightData weightData = new WeightData();

		String weightUnit = getWeightUnitFromMessage(message);

		Double netto = getNettoFromMessage(message);
		Double nettoInGrams = convertToGrams(netto, weightUnit);
		weightData.setNetto(nettoInGrams);

		Double brutto = getBruttoFromMessage(message);
		double bruttoInGrams = convertToGrams(brutto, weightUnit);
		weightData.setBrutto(bruttoInGrams);

		return weightData;
	}

	private double convertToGrams(Double weight, String weightUnit)
	{
		double factor = ScaleUtil.toGram(weightUnit);
		double weightInGrams = (weight * factor);
		return round(weightInGrams);
	}

	private double round(double weightInGrams)
	{
		return new BigDecimal(Double.toString(weightInGrams))
				.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
	}

	private String getWeightUnitFromMessage(IMessage message)
	{
		return getStringFromMessage(message.words().get(0), WEIGHT_UNIT_VALUE_POSITION, WEIGHT_UNIT_VALUE_LENGTH);
	}

	private Double getNettoFromMessage(IMessage message)
	{
		return getDoubleFromMessage(message.words().get(0), NETTO_VALUE_POSITION, DOUBLE_VALUE_LENGTH);
	}

	private Double getBruttoFromMessage(IMessage message)
	{
		return getDoubleFromMessage(message.words().get(0), BRUTTO_VALUE_POSITION, DOUBLE_VALUE_LENGTH);
	}

	private String getStringFromMessage(byte[] message, int valuePosition, int valueLength)
	{
		return StringUtils.deleteWhitespace(new String(message, valuePosition, valueLength));
	}

	private Integer getIntegerFromMessage(byte[] message, int valuePosition, int valueLength)
	{
		String value = new String(message, valuePosition, valueLength);
		return Integer.valueOf(StringUtils.deleteWhitespace(value));
	}

	private Double getDoubleFromMessage(byte[] message, int valuePosition, int valueLength)
	{
		String value = new String(message, valuePosition, valueLength);
		return Double.valueOf(StringUtils.deleteWhitespace(value));
	}
}
