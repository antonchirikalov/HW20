package com.toennies.ci1429.app.services.devicetemplates;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("ci1532ServiceImpl")
public class CI1532ServiceImpl implements CI1532Service {

	private static final Logger LOGGER = LogManager.getLogger();

	@Autowired
	private CI1532URIService ci1532URIService;

	@Override
	public List<ITemplateDeviceDescription> getAllTemplates() {
		RestTemplate rt = new RestTemplate();

		URI getTemplatesURI = returnGetTemplates();

		try {
			ResponseEntity<List<ITemplateDeviceDescription>> exchange = rt.exchange(getTemplatesURI, HttpMethod.GET,
					null, new ParameterizedTypeReference<List<ITemplateDeviceDescription>>() {
					});

			if (exchange.getStatusCode().is2xxSuccessful()) {
				return exchange.getBody();
			}

		} catch (Exception e) {
			LOGGER.error("Error during load of Templates data", e);
			return null;
		}
		return null;

	}

	private URI returnGetTemplates() {
		URI uri;
		try {
			String uri2Service = ci1532URIService.getCI1532URI();
			LOGGER.info("Uri for syncing templates: {}", uri2Service);
			uri = new URI(uri2Service);
			return uri;
		} catch (URISyntaxException e) {
			LOGGER.error(e);
			return null;
		}

	}

}
