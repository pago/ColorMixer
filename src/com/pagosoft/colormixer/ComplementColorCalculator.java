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

/**
 *
 * @author pago
 */
public class ComplementColorCalculator implements ColorCalculator {
	public BaseColor[] calculateColors(BaseColor base) {
		BaseColor[] result = new BaseColor[9];

		// add base color and derivates
		result[0] = base;
		result[1] = base.darker(1);
		result[2] = base.darker(2);

		// add complement and derivates
		BaseColor complement = base.complement();
		result[3] = complement;
		result[4] = complement.darker(1);
		result[5] = complement.darker(2);

		// add gray color tones
		BaseColor gray = base.gray();
		result[6] = gray;
		result[7] = gray.darker(1);
		result[8] = gray.darker(2);

		return result;
	}

	public static void main(String[] args) {
		BaseColor blue = new BaseColor(0x0000FF);
		BaseColor complement = blue.complement();
		System.out.println(complement.toHexString());
	}
}
