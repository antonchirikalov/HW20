/*
This file is part of the d3fact common library.
Copyright (C) 2005-2012 d3fact Project Team

This library is subject to the terms of the Mozilla Public License, v. 2.0.
You should have received a copy of the MPL along with this library; see the
file LICENSE. If not, you can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.toennies.ci1429.app.util;

public class Colors
{

	public static final int WHITE = -1;
	public static final int RED = -16776961;
	public static final int GREEN = 16711935;
	public static final int BLUE = 65535;
	public static final int YELLOW = -65281;
	public static final int ORANGE = -8388353;
	public static final int TURQUOISE = 16777215;
	public static final int PINK = -4142081;
	public static final int VIOLET = -293409025;
	public static final int GRAY = -2139062017;
	public static final int BLACK = 255;


	public static final int from(int... color)
	{
		validateColor(color);
		int result = 0;
		result |= color[0] << 24;
		result |= color[1] << 16;
		result |= color[2] << 8;
		result |= color[3] << 0;
		return result;
	}

	public static final int from(float... color)
	{
		validateColor(color);
		int result = 0;
		result |= ((int) (color[0] * 255f)) << 24;
		result |= ((int) (color[1] * 255f)) << 16;
		result |= ((int) (color[2] * 255f)) << 8;
		result |= ((int) (color[3] * 255f)) << 0;
		return result;
	}

	public static final float[] convert(int color, float... result)
	{
		if (result.length < 4)
			result = new float[4];
		result[0] = ((color >> 24) & 0xFF) / 255f;
		result[1] = ((color >> 16) & 0xFF) / 255f;
		result[2] = ((color >> 8) & 0xFF) / 255f;
		result[3] = ((color >> 0) & 0xFF) / 255f;
		return result;
	}

	public static final int[] convert(int color, int... result)
	{
		if (result.length < 4)
			result = new int[4];
		result[0] = ((color >> 24) & 0xFF);
		result[1] = ((color >> 16) & 0xFF);
		result[2] = ((color >> 8) & 0xFF);
		result[3] = ((color >> 0) & 0xFF);
		return result;
	}
	
	public static final String convertHex(int color)
	{
		return String.format("%0" + (4 << 1) + "X", Integer.valueOf(color));
	}

	public static final void validateColor(float... color)
	{
		if (color.length != 4)
			throw new IllegalArgumentException("Given color array does not have a length of 4");
		for (int i = 0; i < 4; i++)
			if (color[i] < 0 || color[i] > 1)
				throw new IllegalArgumentException("Given color values are not in the range [0..1]");
	}

	private static final void validateColor(int... color)
	{
		if (color.length != 4)
			throw new IllegalArgumentException("Given color array does not have a length of 4");
		for (int i = 0; i < 4; i++)
			if (color[i] < 0 || color[i] > 255)
				throw new IllegalArgumentException("Given color values are not in the range [0..255]");
	}

	private static final double FACTOR = 0.7;

	/**
	 * Makes a given color brighter. Code copied and adapted from
	 * {@linkplain java.awt.Color#brighter()}.
	 * 
	 * @param color
	 * @return a brighter color code
	 */
	public static final int brighter(int color)
	{
		return brighter(color, FACTOR);
	}
	public static final int brighter(int color, double factor)
	{
		factor = Math.max(0, Math.min(1, factor));
		int[] rgba = convert(color);

		/*
		 * From 2D group: 1. black.brighter() should return grey 2. applying
		 * brighter to blue will always return blue, brighter 3. non pure color
		 * (non zero rgb) will eventually return white
		 */
		int i = (int) (1.0 / (1.0 - factor));
		if (rgba[0] == 0 && rgba[1] == 0 && rgba[2] == 0)
		{
			return Colors.from(i, i, i, rgba[3]);
		}
		if (rgba[0] > 0 && rgba[0] < i)
			rgba[0] = i;
		if (rgba[1] > 0 && rgba[1] < i)
			rgba[1] = i;
		if (rgba[2] > 0 && rgba[2] < i)
			rgba[2] = i;

		return Colors.from(Math.min((int) (rgba[0] / factor), 255), Math.min((int) (rgba[1] / factor), 255),
				Math.min((int) (rgba[2] / factor), 255), rgba[3]);
	}

	/**
	 * Makes a given color darker. Code copied and adapted from
	 * {@linkplain java.awt.Color#darker()}.
	 * 
	 * @param color
	 * @return a darker color code
	 */
	public static final int darker(int color)
	{
		return darker(color, FACTOR);
	}
	
	public static final int darker(int color, double factor)
	{
		factor = Math.max(0, Math.min(1, factor));
		int[] rgba = convert(color);
		return Colors.from(Math.max((int) (rgba[0] * factor), 0), Math.max((int) (rgba[1] * factor), 0),
				Math.max((int) (rgba[2] * factor), 0), rgba[3]);
	}

	private Colors()
	{
		// no instance
	}

}