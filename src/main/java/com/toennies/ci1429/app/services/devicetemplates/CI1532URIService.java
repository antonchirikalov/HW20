package com.toennies.ci1429.app.services.devicetemplates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CI1532URIService {

	@Value("${ci1532ws.protocol}")
	private String protocol;

	@Value("${ci1532ws.host}")
	private String host;

	@Value("${ci1532ws.port}")
	private String port;

	@Value("${ci1532ws.path}")
	private String path;

	/**
	 * Builds the uri for accessing ci1532ws templates services
	 */
	public String getCI1532URI() {
		StringBuilder sb = new StringBuilder();
		sb.append(protocol);
		sb.append(host);
		sb.append(port);
		sb.append(path);

		return sb.toString();
	}

}
