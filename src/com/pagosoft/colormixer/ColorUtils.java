/*
 * This file is part of ColorMixer
 * Copyright (c) 2006 Patrick Gotthardt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.pagosoft.colormixer;

import java.awt.*;
import java.util.*;

/**
 *
 * @author Patrick
 */
public class ColorUtils {
	private ColorUtils() {}
	
	/**
	 * Calculates the brightness of a color. Return range is [1, 10]. A higher value
	 * indicate a brighter color.
	 *
	 * @param clr The color to find the brightness of
	 *
	 * @return The brightness index of <code>clr</code>, which is in [1, 10]
	 */
	public static long colorBrightness(Color clr) {
		// this formula is based on the transformation of an rgb-color to grey.
		// originial formula: grey = 0.299 * red + 0.587 * green + 0.114 * blue

		long redIndex = 299 * (long) Math.pow(clr.getRed(), 3);
		long grnIndex = 587 * (long) Math.pow(clr.getGreen(), 3);
		long bluIndex = 114 * (long) Math.pow(clr.getBlue(), 3);
		
		return (redIndex + grnIndex + bluIndex) / 1658137500l;
	}
	
	/**
	 * Can be:
	 * (255, 0, 0)
	 * 255, 0, 0
	 * #FF0000
	 * #F00
	 * red
	 */
	public static Color toColor(String str) {
		switch (str.charAt(0)) {
			case '(':
				int red, green, blue;
				int index;
				
				red = nextColorInt(str, 1);
				
				index = str.indexOf(',');
				green = nextColorInt(str, index + 1);
				
				index = str.indexOf(',', index + 1);
				blue = nextColorInt(str, index + 1);
				
				return new Color(red, green, blue);
			case '#':
				// Shorthand?
				if (str.length() == 4) {
					return new Color(
							getShorthandValue(str.charAt(1)),
							getShorthandValue(str.charAt(2)),
							getShorthandValue(str.charAt(3))
							);
				} else {
					return new Color(Integer.parseInt(str.substring(1), 16));
				}
			default:
				if(Character.isDigit(str.charAt(0))) {
					red = nextColorInt(str, 0);
					
					index = str.indexOf(',');
					green = nextColorInt(str, index + 1);
					
					index = str.indexOf(',', index + 1);
					blue = nextColorInt(str, index + 1);
					
					return new Color(red, green, blue);
				}
				return (Color) colorNamesMap.get(str);
		}
	}
	
	private static int nextColorInt(String str, int index) {
		// start with adjusting the start index
		while (index < str.length()) {
			char c = str.charAt(index);
			// a digit?
			if ('0' <= c && c <= '9') {
				break;
			} else {
				index++;
			}
		}
		// that's only the maximum limit!
		int colorLength = index;
		for (; colorLength < index + 3; colorLength++) {
			char c = str.charAt(colorLength);
			// not a digit?
			if (c < '0' || '9' < c) {
				break;
			}
		}
		return Integer.parseInt(str.substring(index, colorLength));
	}
	
	private static int getShorthandValue(char c) {
		c = Character.toUpperCase(c);
		if ('A' <= c && c <= 'F') {
			return colorShorthandTable[c - 'A' + 10];
		}
		return colorShorthandTable[c - '0'];
	}
	
	private static int[] colorShorthandTable = {
		0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66,
		0x77, 0x88, 0x99, 0xAA, 0xBB, 0xCC, 0xDD,
		0xEE, 0xFF
	};
	
	private static Map colorNamesMap;
	
	static {
		colorNamesMap = new TreeMap();
		colorNamesMap.put("white", new Color(0xFFFFFF));
		colorNamesMap.put("lightGray", new Color(0xC0C0C0));
		colorNamesMap.put("gray", new Color(0x808080));
		colorNamesMap.put("darkGray", new Color(0x404040));
		colorNamesMap.put("black", new Color(0x000000));
		colorNamesMap.put("red", new Color(0xFF0000));
		colorNamesMap.put("pink", new Color(0xFFAFAF));
		colorNamesMap.put("orange", new Color(0xFFC800));
		colorNamesMap.put("yellow", new Color(0xFFFF00));
		colorNamesMap.put("green", new Color(0x00FF00));
		colorNamesMap.put("magenta", new Color(0xFF00FF));
		colorNamesMap.put("cyan", new Color(0x00FFFF));
		colorNamesMap.put("blue", new Color(0x0000FF));
	}
	
	public static Color oposite(Color a) {
		return new Color(255 - a.getRed(), 255 - a.getGreen(), 255 - a.getBlue(), a.getAlpha());
	}
	
	public static Color subtract(Color a, Color b) {
		return new Color(
				Math.max(0, Math.min(255, a.getRed() - b.getRed())),
				Math.max(0, Math.min(255, a.getGreen() - b.getGreen())),
				Math.max(0, Math.min(255, a.getBlue() - b.getBlue())));
	}
	
	public static String toHexString(Color c) {
		String colString = Integer.toHexString(c.getRGB() & 0xffffff).toUpperCase();
		return "#000000".substring(0,7 - colString.length()).concat(colString);
	}
	
	public static Color getTranslucentColor(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}
	
	public static Color getSimiliarColor(Color color, float factor) {
		return new Color(
				between((int)(color.getRed()*factor), 0, 255),
				between((int)(color.getGreen()*factor), 0, 255),
				between((int)(color.getBlue()*factor), 0, 255),
				color.getAlpha());
	}
	
	private static int between(int v, int min, int max) {
		return Math.max(min, Math.min(v, max));
	}
	
	public static Color getColor(Color color, float factor) {
		float[] hsbValues = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbValues);
		return Color.getHSBColor(hsbValues[0], hsbValues[1], hsbValues[2] * factor);
	}
}
