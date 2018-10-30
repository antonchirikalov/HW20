/**
 * 
 */
package com.toennies.ci1429.app.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.ResponseType;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scale.Scale.Unit;
import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.model.scale.WeightDataFormatter;
import com.toennies.ci1429.app.network.message.IMessage;

/**
 * @author stenzelk
 *
 */
public class LogbookUtil
{

	public static final String convertToString(Object payload)
	{
		if (payload instanceof byte[])
			return ASCII.formatHuman((byte[]) payload);
		if (payload instanceof IMessage)
			return payload.toString();
		if (payload instanceof Collection)
			return ((Collection<?>) payload).stream().map(LogbookUtil::convertToString).collect(Collectors.joining(",", "{", "}"));
		if (payload instanceof Object[])
		{
			Object[] arr = (Object[]) payload;
			if (arr.length == 0)
				return "";
			if (arr.length == 1)
				return convertToString(arr[0]);
			return "("+arr.length+")" + Arrays.stream(arr).map(LogbookUtil::convertToString).collect(Collectors.joining(",", "{", "}"));
		}
		if (payload instanceof WeightData)
		{
			Map<String, String> formatted = WeightDataFormatter.formatWeightData((WeightData) payload, ResponseFormat.HUMAN, 3, Unit.KG);
			return formatted.entrySet().stream().map((e) -> e.getKey()+"="+e.getValue()).collect(Collectors.joining(",", "{", "}"));
		}
		if (payload instanceof DeviceResponse)
		{
			DeviceResponse response = (DeviceResponse) payload;
			StringBuilder sb = new StringBuilder();
			sb.append(response.getStatus());
			if (response.getStatus().type != ResponseType.NO_DATA)
			{
				sb.append('|');
				sb.append(convertToString(response.getPayload()));
			}
			return sb.toString();
		}
		return String.valueOf(payload);
	}

	private LogbookUtil()
	{
		// no instance
	}

}
