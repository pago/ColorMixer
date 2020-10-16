/*
 * Copyright (c) 2009 Patrick Gotthardt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.pagosoft.colormixer.paint;

import com.pagosoft.swing.BaseColor;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 *
 * @author pago
 */
public abstract class AbstractGradientPaintContext implements PaintContext {
	protected ColorModel cm;
	protected Rectangle deviceBounds;
	protected Rectangle2D userBounds;
	protected AffineTransform xform;
	protected RenderingHints hints;
	protected BaseColor color;

	public AbstractGradientPaintContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints, BaseColor color) {
		this.cm = cm;
		this.deviceBounds = deviceBounds;
		this.userBounds = userBounds;
		this.xform = xform;
		this.hints = hints;
		this.color = color;

		if(color == null) {
			this.color = new BaseColor(.5f, 1f, 1f);
		}
	}

	public void dispose() {
	}

	public ColorModel getColorModel() {
		return ColorModel.getRGBdefault();
	}

	public Raster getRaster(int x, int y, int w, int h) {
		WritableRaster raster;
		x -= deviceBounds.x;
		// init raster and set colors
		raster = getColorModel().createCompatibleWritableRaster(w, h);
		int[] colors = new int[w*h*4];
		int offset = 0;
		for(int j = 0; j < h; j++) for(int i = 0; i < w; i++) {
			Color rgb = calculateColor(x+i);
			// translate color into rgba
			int r = rgb.getRed(), g = rgb.getGreen(), b = rgb.getBlue();
			colors[offset++] = r;
			colors[offset++] = g;
			colors[offset++] = b;
			colors[offset++] = 255;
		}
		raster.setPixels(0, 0, w, h, colors);
		return raster;
	}

	protected abstract Color calculateColor(int x);
}
