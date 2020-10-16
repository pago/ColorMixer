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
public class HueGradientPaint extends AbstractGradientPaint {
	public HueGradientPaint(BaseColor color) {
		super(color);
	}

	@Override
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		return new HueGradientPaintContext(cm, deviceBounds, userBounds, xform, hints, color);
	}

	private static class HueGradientPaintContext extends AbstractGradientPaintContext {
		public HueGradientPaintContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints, BaseColor color) {
			super(cm, deviceBounds, userBounds, xform, hints, color);
		}

		@Override
		protected Color calculateColor(int x) {
			return color.withHue((float)((float)x/(float)userBounds.getWidth())).toColor();
		}
	}
}
