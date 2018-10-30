/**
 * 
 */
package com.toennies.ci1429.app.util;

import java.util.LinkedHashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.model.scale.Scale.Unit;
import com.toennies.ci1429.app.network.protocol.scale.AScaleProtocol;
import com.toennies.ci1429.app.network.protocol.scale.AtSupportedCommands;
import com.toennies.ci1429.app.network.protocol.scale.AtSupportedCommands.AtCommand;

/**
 * @author renkenh
 *
 */
public class ScaleUtil
{

	private static final Logger LOGGER = LogManager.getLogger();

	private static final Command[] EMPTY_ARRAY = new Command[0];


	public static Command[] getCommands(String className)
	{
		try
		{
			Class<?> clazz = Class.forName(className);
			return getCommands(clazz);
		}
		catch (ClassNotFoundException | ClassCastException e)
		{
			LOGGER.warn("Could not get protocol class.", e);
		}
		return EMPTY_ARRAY;
	}


	public static Command[] getCommands(Class<?> clazz)
	{
		if (!AScaleProtocol.class.isAssignableFrom(clazz))
			return EMPTY_ARRAY;
		
		try
		{
			LinkedHashSet<Command> commands = new LinkedHashSet<>();
			do
			{
				AtSupportedCommands.AtCommand[] descs = clazz.getAnnotationsByType(AtSupportedCommands.AtCommand.class);
				for (AtCommand atCMD : descs)
					commands.add(atCMD.value());
				
				clazz = clazz.getSuperclass();
			}
			while (!clazz.equals(Object.class));
			return commands.toArray(new Command[commands.size()]);
		}
		catch (SecurityException | IllegalArgumentException ex)
		{
			LOGGER.warn("Could not gather available commands.", ex);
			return EMPTY_ARRAY;
		}
	}


	public static final double toGram(String unit)
	{
		Unit eUnit = Unit.valueOf(unit.toUpperCase());
		return toGram(eUnit);
	}
	
	/**
	 * Returns a computation factor according to the given unit. The factor is based on gram {@link Unit#G}.
	 * @param unit The unit for which to get the factor.
	 * @return The factor.
	 */
	public static final double toGram(Unit unit)
	{
		switch (unit)
		{
			case G:
			default:
				return 1;
			case KG:
				return 1000;
			case T:
				return 1_000_000;
			case LB:
				return 453.59237;
		}
	}


	/**
	 * Returns a computation factor according to the given unit. The factor is based on gram {@link Unit#G}.
	 * @param unit The unit for which to get the factor.
	 * @return The factor.
	 */
	public static final double gramTo(Unit unit)
	{
		switch (unit)
		{
			case G:
			default:
				return 1;
			case KG:
				return 1 / 1000d;
			case T:
				return 1 / (1_000_000d);
			case LB:
				return 1 / 453.59237;
		}
	}


	private ScaleUtil()
	{
		//no instance
	}

}
