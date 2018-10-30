package com.toennies.ci1429.app.services.devicetemplates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.repository.DeviceTemplateRepository;
import com.toennies.ci1574.lib.helper.Generics;

/**
 * Service class that provides access to hw20 instance template files
 */
@Component
public class DeviceTemplateServiceImpl implements DeviceTemplateService {

	@Autowired
	private DeviceTemplateRepository deviceTemplateRepository;

	@Autowired
	@Qualifier("ci1532ServiceImpl")
	private CI1532Service ci1532Service;

	@Override
	public ITemplateDeviceDescription findById(Integer id)
	{
		return deviceTemplateRepository.findOne(id);
	}

	@Override
	public List<ITemplateDeviceDescription> findAll() {
		return Generics.convertUnchecked(deviceTemplateRepository.findAll());
	}

	@Override
	public boolean syncTemplates() {
		// 1. get data from ci1532ws
		List<ITemplateDeviceDescription> allTemplates = ci1532Service.getAllTemplates();
		if (allTemplates == null) {
			return false;
		}

		// 2. then delete current templates, if getAllTemplates was successfull
		deviceTemplateRepository.deleteAll();

		// 3. save newly loaded templates
		deviceTemplateRepository.save(map2Entities(allTemplates));

		// 4. return all current templates
		return true;
	}

	@Override
	public ITemplateDeviceDescription findByTemplateName(String templateName)
	{
		return deviceTemplateRepository.findByTemplateName(templateName);
	}

	private List<DeviceTemplateEntity> map2Entities(List<ITemplateDeviceDescription> pojos) {
		List<DeviceTemplateEntity> entities = new ArrayList<>();
		pojos.stream().forEach(e -> entities.add(new DeviceTemplateEntity(e)));
		return entities;
	}

}
