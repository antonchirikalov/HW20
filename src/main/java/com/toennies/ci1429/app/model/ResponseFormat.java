package com.toennies.ci1429.app.model;

/**
 * The response format supported by almost all device classes.
 * @author renkenh
 */
public enum ResponseFormat
{

	RAW("Raw"), STRING("String"), EAN128("EAN128"), HUMAN("Human");


	public final String presentation;
	
	private ResponseFormat(String presentation)
	{
		this.presentation = presentation;
	}
}