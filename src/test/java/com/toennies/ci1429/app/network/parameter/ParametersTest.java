package com.toennies.ci1429.app.network.parameter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.protocol.scanner.ChipReaderProtocol;

public class ParametersTest
{

	/**
	 * Test for {@link Parameters#getParameters(Class)} and
	 * {@link Parameters#getParameters(String)} methods.
	 */
	@Test
	public void chipReaderParametersTest()
	{
		// This class is annotated with respective Annotation
		Class<ChipReaderProtocol> chipReaderClass = ChipReaderProtocol.class;

		// This List contains paramter information from ChipReaderProtocol
		// class and super classes AScannerProtocol and AProtocol
		// Through this it's checked, that parameter information from entire
		// class
		// hierarchy are present in returned parameter map.
		List<String> mustBeAvailableParameter = Arrays.asList(ADataTransformer.PARAM_FRAME_SEP, IProtocol.PARAM_SOCKET);
		checkParameter4Class(chipReaderClass, mustBeAvailableParameter);

		Map<String, String> valuesMustMatch = new HashMap<String, String>();
		valuesMustMatch.put(ADataTransformer.PARAM_FRAME_END, "[CR]");
		valuesMustMatch.put(IProtocol.PARAM_SOCKET, "");

		checkParameterValues4Class(chipReaderClass, valuesMustMatch);
	}

	private void checkParameter4Class(Class<?> clazz2Test, Collection<String> mustBeAvailableParameter)
	{
		// First load information by class object
		Map<String, ParamDescriptor> parametersByClassObject = Parameters.getParameters(clazz2Test);
		checkParameterMap(parametersByClassObject, mustBeAvailableParameter);

		// Second load information by class name itself
		Map<String, ParamDescriptor> parametersByClassName = Parameters.getParameters(clazz2Test.getCanonicalName());
		checkParameterMap(parametersByClassName, mustBeAvailableParameter);
	}

	/**
	 * Takes a {@link Map} containing {@link ParamDescriptor} information and
	 * checks, if parameters that are stored in given collection are present in
	 * map.
	 * 
	 * So this test fails, if there a missing parameter. Parameter are defined
	 * in invoking method(s).
	 * 
	 * @param parametersMap
	 *            this Map gets checked if parameters are present
	 * @param mustBeAvailableParameter
	 *            All keys are stored in this collection that are needed to be
	 *            present in parameter map
	 */
	private void checkParameterMap(Map<String, ParamDescriptor> parametersMap,
			Collection<String> mustBeAvailableParameter)
	{
		assertNotNull(parametersMap);
		mustBeAvailableParameter.stream().forEach(s -> assertNotNull("Parameter: ".concat(s), parametersMap.get(s)));
	}

	private void checkParameterValues4Class(Class<?> clazz2Test, Map<String, String> valuesMustMatch)
	{
		// First load information by class object
		Map<String, ParamDescriptor> parametersByClassObject = Parameters.getParameters(clazz2Test);
		// Second load information by class name itself
		Map<String, ParamDescriptor> parametersByClassName = Parameters.getParameters(clazz2Test.getCanonicalName());
		checkParameterValues(parametersByClassObject, valuesMustMatch);
		checkParameterValues(parametersByClassName, valuesMustMatch);
	}

	private void checkParameterValues(Map<String, ParamDescriptor> parametersMap, Map<String, String> valuesMustMatch)
	{
		valuesMustMatch.entrySet()
				.forEach(e -> assertTrue(parametersMap.get(e.getKey()).getValue().equalsIgnoreCase(e.getValue())));
	}
}
