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
import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;

/**
 * Exports the colors in Photoshops ACO format.
 * @author Patrick
 */
public class ACOExport implements ColorExport {
	
	/** Creates a new instance of ACTExport */
	public ACOExport() {
	}
	
	public void write(OutputStream os, BaseColor[] colors) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(4+(10*colors.length));
		//buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// version (2 bytes)
		buf.putShort((short)1);
		// count (2 bytes)
		buf.putShort((short)colors.length);
		System.out.println("Writing "+colors.length+" colors");
		System.out.println("Color[] expected = new Color[] {");
		for(BaseColor baseColor : colors) {
			Color color = baseColor.toColor();
			System.out.println("ColorUtils.toColor(\""+ColorUtils.toHexString(color)+"\"),");
			// color space (2 bytes)
			buf.putShort((short)0); // RGB color space
			// color (8 bytes)
			float[] colorsPercent = color.getRGBColorComponents(null);
			// needs _intensive_ testing!
			buf.putShort((short)((colorsPercent[0])*65535));
			buf.putShort((short)((colorsPercent[1])*65535));
			buf.putShort((short)((colorsPercent[2])*65535));
			// extra, not required by RGB
			buf.putShort((short)0);
		}
		System.out.println("};");
		
		os.write(buf.array());
	}
	
	public static void main(String[] args) {
		ACOExport export = new ACOExport();
		ComplementColorCalculator gncc = new ComplementColorCalculator();
		BaseColor[] colors = gncc.calculateColors(new BaseColor(0x6F9668));
		
		try {
			OutputStream os = new FileOutputStream("D:/test.aco");
			export.write(os, colors);
			os.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
