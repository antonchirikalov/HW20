/**
 * 
 */
package com.toennies.ci1429.app.util;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Utils for the Freemaker Template Engine.
 * @author renkenh
 */
public class FTLUtils
{
	
	private static final Logger logger = LogManager.getLogger();


	/**
	 * Uses the given template to convert the model data into a string. Never returns <code>null</code>.
	 * @param dataModel The data to convert - can be anything that can be processed by the template.
	 * @param template The template to use.
	 * @return The converted string.
	 * @throws IOException If something goes wrong during conversion.
	 */
	public static final String convert(Object dataModel, Template template) throws IOException
	{
		if (template == null)
			throw new IOException("No template available.");

		StringWriter writer = new StringWriter();
		try
		{
			template.process(dataModel, writer);
		}
		catch (TemplateException e)
		{
			throw new IOException(e);
		}
		return writer.toString();
	}

	/**
	 * Returns the template with the specified name.
	 * The templates are loaded from the directory "/templates". Where / specifies the root of the artifact (or a source folder in eclipse). 
	 * @param templateName The name of the template - without any additional paths, without an ending like .ftl.
	 * @return The template if it could be loaded. <code>null</code> otherwise.
	 */
	public static final Template getTemplate(String templateName)
	{
		try
		{
			return getTemplateConfig().getTemplate("/" + templateName + ".ftl");
		}
		catch (IOException e)
		{
			logger.error("Could not load template: {}", templateName, e);
			return null;
		}
	}
	
    /**
     * Gets the template configuration.
     *
     * @return the template configuration
     */
    public static final Configuration getTemplateConfig()
    {
        Configuration templateCFG = new Configuration(Configuration.VERSION_2_3_23);
        templateCFG.setDefaultEncoding("UTF-8");
        templateCFG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        templateCFG.setLocalizedLookup(false);
        templateCFG.setTemplateLoader(new ClassTemplateLoader(FTLUtils.class, "/templates"));
        return templateCFG;
    }


	private FTLUtils()
	{
		//no instance
	}

}
