package com.toennies.ci1429.app.hw10.processing.devices.scale;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher.Event;
import com.toennies.ci1429.app.hw10.processing.devices.scale.functions.ScaleTareNullify;
import com.toennies.ci1429.app.hw10.processing.devices.scale.functions.ScaleWeigh;
import com.toennies.ci1429.app.hw10.processing.events.IHW10ClientCommandMatcher;
import com.toennies.ci1429.app.hw10.util.CommandParser;
import com.toennies.ci1429.app.hw10.util.DeviceValidator;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.services.devices.IDevicesService;
import com.toennies.ci1429.app.util.ASCII;

/**
 * This class manages scale commands. It identifies existing scale commands and
 * processes {@link #processRequest(Event)} further the request. Finally,
 * returns the response received to the client (e.g., netweigh).
 * 
 * @author renkenh
 */
@Component
public class ScaleProcessor implements IHW10ClientCommandMatcher
{

	/* Non-greedy match on filler */
	private final static String SKIP = ".*?";
	/* Scale identification */
	private final static String MCI = "(MCI)";
	/* Net weight */
	private final static String WI = "(WI)";
	/* Weige foerderer */
	private final static String ST = "(ST)";
	/* Clear tara */
	private final static String TL = "(TL)";
	/* Weigh with added value */
	private final static String WT = "(WT)";
	private final static String CL = "(CL)";
	/* Clear function - reset */
	private final static String NU = "(NU)";
	private final static String clients = "(clients)";
	private final static String exe = "(exe)";
	/* Tare */
	private final static String TA = "(TA)";
	/* Tare with added value */
	private final static String TW = "(TW)";
	/* Autoweight */
	private final static String AW = "(AW)";

	private static final Pattern MCI_WI_PATTERN = Pattern.compile(MCI + SKIP + WI,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern MCI_ST_PATTERN = Pattern.compile(MCI + SKIP + ST,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern MCI_WT_PATTERN = Pattern.compile(SKIP + MCI + SKIP + WT,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern MCI_CL_PATTERN = Pattern.compile(MCI + SKIP + CL + SKIP + clients + SKIP + exe,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern MCI_NU_PATTERN = Pattern.compile(MCI + SKIP + NU,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern MCI_TL_PATTERN = Pattern.compile(MCI + SKIP + TL,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern MCI_TA_PATTERN = Pattern.compile(MCI + SKIP + TA,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern MCI_TW_PATTERN = Pattern.compile(MCI + SKIP + TW,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern MCI_AW_PATTERN = Pattern.compile(MCI + SKIP + AW,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern MCI_PATTERN = Pattern.compile(MCI, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	@Autowired
	private ScaleTareNullify scaleTareNullify;

	@Autowired
	private ScaleWeigh scaleWeigh;

	@Autowired
	private IDevicesService service;

	/**
	 * Pattern matching for scale net-weight {@link #MCI_WI_PATTERN}
	 * 
	 * @param command
	 *            this parameter matches clients command for net weight e.g.,
	 *            [SOH]MCI011WI[ETX]RHE19\clients\exe
	 */
	public static boolean isNetWeighRequest(String command)
	{
		Matcher m = MCI_WI_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Patter matching for scale wiegefoerderer
	 * 
	 * @param command
	 *            this parameter matches clients command for wiegefoerder
	 *            (Starten) e.g., [SOH]MCI011ST[ETX]
	 */
	public static boolean wiegefoerderer(String command)
	{
		Matcher m = MCI_ST_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Pattern matching for scale weight with tara added value (German: Tara
	 * Wertvorgabe)
	 */
	public static boolean isWeightWithReg(String command)
	{
		Matcher m = MCI_WT_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Pattern matching to end the function
	 */
	public static boolean funktionBeenden(String command)
	{
		Matcher m = MCI_CL_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Pattern matching for scale nullify function (German: Nullstellen)
	 */
	public static boolean isSetNull(String command)
	{
		Matcher m = MCI_NU_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Pattern matching for scale clear tare function (German: Tara Loeschen)
	 */
	public static boolean isClearTara(String command)
	{
		Matcher m = MCI_TL_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Pattern matching for scale tare function (German: Tarieren)
	 */
	public static boolean isTare(String command)
	{
		Matcher m = MCI_TA_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Pattern matching for scale tare with value (German: Tara Wertvorgabe)
	 */
	public static boolean isTareWithValue(String command)
	{
		Matcher m = MCI_TW_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Pattern matching for scale automatic weighting function (Automatikwiegen)
	 */
	public static boolean isAutoWeigh(String command)
	{
		Matcher m = MCI_AW_PATTERN.matcher(command);
		return m.find();
	}

	@Override
	public boolean matchesRequest(String command)
	{
		Matcher m = MCI_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceResponse processRequest(Event event)
	{
		String command = (String) event.parameters[0];
		
		final int deviceId = CommandParser.deviceIdParser(command);
		IDevice device = this.service.getDeviceById(deviceId);
		DeviceResponse response = DeviceValidator.isDeviceValid(device, DeviceType.SCALE);
		if (response != DeviceResponse.OK)
			return response;
		
		if (isNetWeighRequest(command))
			return addACK(scaleWeigh.netWeigh(command));
		
		if (isWeightWithReg(command))
			return addACK(scaleWeigh.itemAdd(command));
		
		if (isSetNull(command))
			return scaleTareNullify.zero(command);
		
		if (isClearTara(command))
			return scaleTareNullify.clearTare(command);
		
		if (isTare(command))
			return addACK(scaleTareNullify.tare(command));
		
		if (isTareWithValue(command))
			return scaleTareNullify.tareWithValue(command);
	
		if (isAutoWeigh(command))
			return addACK(scaleWeigh.weighAutomatic(command));
		
		return new DeviceResponse(Status.BAD_REQUEST, "Unknown command '"+command+"'.");
	}

	private static final DeviceResponse addACK(DeviceResponse response)
	{
		if (response.getStatus() != Status.OK_DATA)
			return response;
		return new DeviceResponse(String.valueOf(ASCII.ACK.c) + response.getPayload().toString());
	}
}