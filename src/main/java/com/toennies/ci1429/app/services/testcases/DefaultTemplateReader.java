package com.toennies.ci1429.app.services.testcases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class DefaultTemplateReader {

	/**
	 * Path to default template.
	 */
	public final static String DEFAULT_TEMPLATE = "/template.zpl";

	/**
	 * @return the content of template.zpl file. With content as String the
	 *         template can be send to printer
	 */
	public static String readDefaultZPLFile() {
		return readZPLFileByFileName(DEFAULT_TEMPLATE);
	}

	/**
	 * @return content of a given file from classpath as {@link String}
	 * 
	 *         found here:
	 *         http://www.adam-bien.com/roller/abien/entry/reading_inputstream_into_string_with
	 */
	private static String readZPLFileByFileName(String filePath) {

		InputStream in = DefaultTemplateReader.class.getResourceAsStream(filePath);
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(in))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			return "";
		}
	}

}
