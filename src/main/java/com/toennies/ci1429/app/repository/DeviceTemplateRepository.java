package com.toennies.ci1429.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.toennies.ci1429.app.services.devicetemplates.DeviceTemplateEntity;

@RepositoryRestResource(exported=false)
public interface DeviceTemplateRepository extends JpaRepository<com.toennies.ci1429.app.services.devicetemplates.DeviceTemplateEntity, Integer>
{
	DeviceTemplateEntity findByTemplateName(String templateName);
}
