package com.toennies.ci1429.app.network.parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.toennies.ci1429.app.util.Utils;

public class ParameterInstance {
	private final String description;
	private final Collection<ParamDescriptor> parameters;
	private final Class<?> clazz;

	public ParameterInstance(Class<?> clazz) {
		// TODO maybe check, if generic is type of IProtocol or IConnector
		this.clazz = clazz;
		this.description = Utils.getDescription(clazz);
		this.parameters = new ArrayList<>(Parameters.getParameters(clazz).values());
	}

	public String getProtocolClass() {
		return this.clazz.getName();
	}

	public String getDescription() {
		return this.description;
	}

	public Collection<ParamDescriptor> getParameters() {
		return Collections.unmodifiableCollection(this.parameters);
	}
}
