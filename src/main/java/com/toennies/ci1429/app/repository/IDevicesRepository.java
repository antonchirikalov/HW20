/**
 * 
 */
package com.toennies.ci1429.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;



/**
 * @author renkenh
 *
 */
@RepositoryRestResource(exported=false)
public interface IDevicesRepository extends CrudRepository<DeviceDescriptionEntity, Integer>
{

}
