/**
 * 
 */
package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Collector class to collect several {@link ValidationResult}s.
 * If at least one {@link ValidationResult} is {@link ValidationResult#isError()} then the whole set is
 * specified to be an error, i.e. {@link ValidationResults#hasErrors()} returns <code>true</code>.
 * @author renkenh
 */
public class ValidationResults
{

	private final Map<String, ValidationResult> results = new HashMap<>();
	private boolean isError;

	
	/**
	 * Package private constructor. To create a new validation results without any
	 * map copying costs.
	 */
	ValidationResults()
	{
		//do nothing
	}
	
	void put(String name, ValidationResult result)
	{
		this.results.put(name, result);
		this.isError = this.results.values().stream().filter(ValidationResult::isError).findAny().isPresent();
	}
	
	
	/**
	 * Public constructor. Makes a flat copy of the given results.
	 */
	public ValidationResults(Map<String, ValidationResult> results)
	{
		this();
		results.forEach(this::put);
	}

	
	/**
	 * @return An unmodifiable map with all results.
	 */
	public Map<String, ValidationResult> getResults()
	{
		return Collections.unmodifiableMap(this.results);
	}
	
	/**
	 * @return A map with all errors. Is empty if {@link #hasErrors()} returns <code>false</code>.
	 */
	public Map<String, ValidationResult> getErrors()
	{
		if (!this.hasErrors())
			return Collections.emptyMap();
		return this.results.entrySet().stream()
									  .filter((e) -> e.getValue().isError())
									  .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
	
	/**
	 * @return Whether at least one {@link ValidationResult} is an error.
	 */
	public boolean hasErrors()
	{
		return this.isError;
	}

}
