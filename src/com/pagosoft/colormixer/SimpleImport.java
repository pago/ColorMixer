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

import com.pagosoft.swing.BaseColor;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Patrick
 */
public class SimpleImport implements ColorImport {
	
	/** Creates a new instance of SimpleImport */
	public SimpleImport() {
	}

	public BaseColor[] read(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		List<BaseColor> colors = new ArrayList<BaseColor>();
		String line;
		try {
			while((line = br.readLine()) != null) {
				colors.add(new BaseColor(ColorUtils.toColor(line)));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return colors.toArray(new BaseColor[colors.size()]);
	}
	
}
