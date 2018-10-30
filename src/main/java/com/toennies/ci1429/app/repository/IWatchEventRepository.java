/**
 * 
 */
package com.toennies.ci1429.app.repository;

import java.time.Instant;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.toennies.ci1429.app.model.watcher.IWatchEvent;
import com.toennies.ci1429.app.model.watcher.Watcher;


/**
 * Repository to locally hold watched events. (Directly) used by {@link Watcher}. 
 * @author renkenh
 */
@RepositoryRestResource(exported=false)
public interface IWatchEventRepository extends JpaRepository<WatchEventEntity, Long>
{

	@Query(value="SELECT w FROM WatchEventEntity w WHERE w.deviceID = ?1")
	public List<? extends IWatchEvent> findByDeviceID(int deviceID);
	
	@Query(value="SELECT w FROM WatchEventEntity w WHERE w.deviceID = ?1 AND w.timestamp >= ?2")
	public List<WatchEventEntity> findByDeviceIDAndTimestampGreaterThanEqual(int deviceID, Instant timestamp);
	
	@Query(value="DELETE FROM WatchEventEntity w WHERE w.deviceID = ?1 AND w.timestamp < ?2")
	@Transactional
	@Modifying
	public void deleteByDeviceIDAndTimestampLessThan(int deviceID, Instant timestamp);

}
