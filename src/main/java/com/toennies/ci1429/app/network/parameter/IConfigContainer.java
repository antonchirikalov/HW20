package com.toennies.ci1429.app.network.parameter;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Simple type that holds a configuration based on a map concept.
 * @author renkenh
 */
public interface IConfigContainer
{

	public boolean hasEntry(String key);
	
	/**
	 * Generic method to get any entry.
	 * @param key The name of the entry.
	 * @return The value.
	 * @throws NoSuchElementException Is thrown if the requested parameter is not available.
	 */
	public String getEntry(String key) throws NoSuchElementException;

	/**
	 * Interprets the value as an integer value.
	 * @param key The name of the entry.
	 * @return The value.
	 */
	public int getIntEntry(String key);

	/**
	 * Interprets the value as a floating point value.
	 * @param key The name of the entry.
	 * @return The value.
	 */
	public float getFloatEntry(String key);

	/**
	 * Interprets the value as a boolean value.
	 * @param key The name of the entry.
	 * @return The value.
	 * @see Boolean#parseBoolean(String)
	 */
	public boolean getBooleanEntry(String key);

	/**
	 * Interprets the value as a given enum definition.
	 * This method ignores the case of the enum definition and parameter value.
	 * @param key The name of the entry.
	 * @param enumClass The class of the enum.
	 * @return If found, the parsed enum value.
	 * @throws NoSuchElementException Is thrown if the requested parameter is not available
	 * 		or the parameter value cannot be interpreted as an enum.
	 */
	public <E extends Enum<E>> E getEnumEntry(String key, Class<? extends Enum<E>> enumClass);
	
	/**
	 * Returns the whole entry set.
	 * @return An unmodifiable map containing all entries.
	 */
	public Map<String, String> getConfig();

}