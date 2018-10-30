/**
 * 
 */
package com.toennies.ci1429.app.repository;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.toennies.ci1429.app.model.watcher.Fault.Severity;
import com.toennies.ci1429.app.model.watcher.IFaultEvent;
import com.toennies.ci1429.app.model.watcher.ISystemEvent;
import com.toennies.ci1429.app.model.watcher.IWatchEvent;

/**
 * @author renkenh
 *
 */
@Entity
public class WatchEventEntity implements IFaultEvent, ISystemEvent
{

	public enum EntityType
	{
		FAULT,
		SYSTEM
	}
	

	@Id @GeneratedValue
	private long uId;
	private int deviceID;
	private EntityType type;

	private String systemId;
	private EventType eventType;
	private Instant timestamp;
	private String message;
	private Severity severity;
	private String faultId;
	
	
	/**
	 * 
	 */
	public WatchEventEntity()
	{
		//bean constructor
	}
	
	/**
	 * Copy constructor.
	 */
	public WatchEventEntity(int deviceID, IFaultEvent event)
	{
		this.setEntityType(EntityType.FAULT);
		this.setDeviceID(deviceID);
		this.setEventType(event.getType());
		this.setId(event.getId());
		this.setMessage(event.getMessage());
		this.setSeverity(event.getSeverity());
		this.setSystemId(event.getSystemId());
		this.setTimestamp(event.getTimestamp());
	}

	/**
	 * Copy constructor.
	 */
	public WatchEventEntity(int deviceID, ISystemEvent event)
	{
		this.setEntityType(EntityType.SYSTEM);
		this.setDeviceID(deviceID);
		this.setEventType(event.getType());
		this.setMessage(event.getMessage());
		this.setSystemId(event.getSystemId());
		this.setTimestamp(event.getTimestamp());
	}

	/**
	 * Copy Constructor.
	 */
	public static final WatchEventEntity createFrom(int deviceID, IWatchEvent event)
	{
		if (event instanceof IFaultEvent)
			return new WatchEventEntity(deviceID, (IFaultEvent) event);
		if (event instanceof ISystemEvent)
			return new WatchEventEntity(deviceID, (ISystemEvent) event);
		throw new UnsupportedOperationException();
	}
	

	public int getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}
	
	public long getUId() {
		return uId;
	}

	public void setUId(long uId) {
		this.uId = uId;
	}
	
	public EntityType getEntityType() {
		return type;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public void setId(String faultId) {
		this.faultId = faultId;
	}

	public void setEntityType(EntityType type) {
		this.type = type;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	@Override
	public String getId()
	{
		return this.faultId;
	}

	@Override
	public String getSystemId()
	{
		return this.systemId;
	}

	@Override
	public EventType getType()
	{
		return this.eventType;
	}

	@Override
	public Severity getSeverity()
	{
		return this.severity;
	}

	@Override
	public Instant getTimestamp()
	{
		return this.timestamp;
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}

}
