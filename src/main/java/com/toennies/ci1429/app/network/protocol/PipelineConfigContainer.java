/**
 * 
 */
package com.toennies.ci1429.app.network.protocol;

import java.util.HashMap;
import java.util.Map;

import com.toennies.ci1429.app.network.parameter.AConfigContainer;
import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.toennies.ci1429.app.network.parameter.Parameters;

/**
 * @author renkenh
 *
 */
class PipelineConfigContainer extends AConfigContainer
{
	
	
	private final Map<String, String> localConfig;
	private final Map<String, ParamDescriptor> protocolParameters;


	public PipelineConfigContainer(Map<String, String> localConfig, Class<? extends IProtocol> protocolClass)
	{
		this(localConfig, Parameters.getParameters(protocolClass));
	}

	/**
	 * 
	 */
	public PipelineConfigContainer(Map<String, String> localConfig, Map<String, ParamDescriptor> protocolParameters)
	{
		this.localConfig = new HashMap<>(localConfig);
		this.protocolParameters = new HashMap<>(protocolParameters);
	}

	/**
	 * 
	 */
	public PipelineConfigContainer(PipelineConfigContainer toCopy)
	{
		this.localConfig = new HashMap<>(toCopy.localConfig);
		this.protocolParameters = new HashMap<>(toCopy.protocolParameters);
	}


	@Override
	protected Map<String, String> _config()
	{
		HashMap<String, String> config = new HashMap<>();
		this.protocolParameters.values().forEach((p) -> config.put(p.getName(), p.getValue()));
		Map<String, ParamDescriptor> parameters = Parameters.getParameters(this.localConfig.get(IProtocol.PARAM_SOCKET));
		parameters.values().forEach((p) -> config.put(p.getName(), p.getValue()));
		config.putAll(this.localConfig);
		return config;
	}

}
