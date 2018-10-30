package com.toennies.ci1429.app.model.scale;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scale.Scale.Unit;
import com.toennies.ci1429.app.util.ScaleUtil;

/**
 * Utility class that can format a {@link WeightData} object according to given parameters
 * like {@link ResponseFormat} or {@link Unit}.
 * @author renkenh
 */
public final class WeightDataFormatter
{
	
	private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols();
	static
	{
		SYMBOLS.setDecimalSeparator('.');
	}
	

	private enum Type
	{
		//this is the mapping for the different formats. The numbers belong to the EAN128 (GS1) standard.
		BRUTTO("330", "Brutto"), NETTO("310", "Netto"), TARA(null, "Tara"), COUNTER("21", "Counter"), UNIT(null, "Unit");
		
		public final String ean128Prefix;
		public final String genericKey;
		
		private Type(String ean128, String key)
		{
			this.ean128Prefix = ean128;
			this.genericKey = key;
		}
		
		public String generateKey(int precision, ResponseFormat format)
		{
			if (format != ResponseFormat.EAN128)
				return this.genericKey;
			if (this == TARA)
				return null;
			if (COUNTER != this)
				return this.ean128Prefix + precision;
			return this.ean128Prefix;
		}
	}

	/**
	 * Convenient method when a client request object is not available.
	 * @param data The data object to format.
	 * @param responseFormat The {@link ResponseFormat}.
	 * @param precision The precision.
	 * @param unit The {@link Unit}. The unit is ignored for Response = {@link ResponseFormat#EAN128}
	 * @return A map with formatted strings.
	 */
	public static final Map<String, String> formatWeightData(WeightData data, ResponseFormat responseFormat, int precision, Unit unit)
	{
		DecimalFormat formatter = createFormatter(precision);
		if (responseFormat == ResponseFormat.EAN128)
			unit = Unit.KG;

		Map<String, String> map = new HashMap<>(4);
		map.put(Type.BRUTTO.generateKey(precision, responseFormat), formatter.format(data.getBrutto() * ScaleUtil.gramTo(unit)));
		map.put(Type.NETTO.generateKey(precision, responseFormat), formatter.format(data.getNetto() * ScaleUtil.gramTo(unit)));
		if (data.getCounter() >= 0)
			map.put(Type.COUNTER.generateKey(precision, responseFormat), String.valueOf(data.getCounter()));
		if (responseFormat != ResponseFormat.EAN128)
		{
			map.put(Type.TARA.generateKey(precision, responseFormat), formatter.format(data.getTara() * ScaleUtil.gramTo(unit)));
			map.put(Type.UNIT.generateKey(precision, responseFormat), unit.name());
		}
		return map;
	}

	private static final DecimalFormat createFormatter(int precision)
	{
		StringBuilder sb = new StringBuilder("0");
		if (precision > 0)
			sb.append('.');
		for (int i = 0; i < precision; i++)
			sb.append('0');
		return new DecimalFormat(sb.toString(), SYMBOLS);
	}

//	private static final DecimalFormat createEAN128Formatter(int precision)
//	{
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < 6-precision; i++)
//			sb.append('0');
//		sb.append('.');
//		for (int i = precision; i < 6; i++)
//			sb.append('0');
//		return new DecimalFormat(sb.toString(), SYMBOLS);
//	}


}