/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pagosoft.swing;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author pago
 */
public class BaseColor implements Paint {
	private float h, s, b, a;

	public BaseColor(float h, float s, float b) {
		this(h, s, b, 1f);
	}

	public BaseColor(float h, float s, float b, float a) {
		this.h = h;
		this.s = s;
		this.b = b;
		this.a = a;
	}

	public BaseColor(int h, float s, float b) {
		this(h, s, b, 1f);
	}

	public BaseColor(int h, float s, float b, float a) {
		this.h = (float)h / 360f;
		this.s = s;
		this.b = b;
		this.a = a;
	}

	public BaseColor(Color c) {
		float[] parts = new float[3];
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), parts);
		h = parts[0];
		s = parts[1];
		b = parts[2];
		a = c.getAlpha();
	}

	public BaseColor(int c) {
		this(new Color(c));
	}

	public Color toColor() {
		int c = Color.HSBtoRGB(h, s, b);
		int alpha = (int)(255 * a + 0.5);
		return new Color(((alpha & 0xFF) << 24) | c, true);
	}

	public BaseColor withHue(float f) {
		return new BaseColor(f, s, b, a);
	}

	public BaseColor shiftHue(int grad) {
		return withHue((h+((float)grad/360f))%1f);
	}

	public BaseColor withSaturation(float ps) {
		return new BaseColor(h, ps, b, a);
	}

	public BaseColor shiftSaturation(float ps) {
		return withSaturation((s+ps)%1f);
	}

	public BaseColor withBrightness(float pb) {
		return new BaseColor(h, s, pb, a);
	}

	public BaseColor shiftBrightness(float pb) {
		return withBrightness((b+pb)%1f);
	}

	public BaseColor withAlpha(float pa) {
		return new BaseColor(h, s, b, pa);
	}

	public BaseColor complement() {
		return shiftHue(180);
	}

	public BaseColor gray() {
		return withSaturation(0);
	}

    public boolean isGray() {
        return s == 0f;
    }

    /**
	 * Calculates the brightness of the color. Return range is [1, 10]. A higher value
	 * indicates a brighter color. This is not related to the brightness value of HSB-color space.
	 *
	 * @return The brightness index of <code>clr</code>, which is in [1, 10]
	 */
	public int colorBrightness() {
        Color clr = toColor();
		// this formula is based on the transformation of an rgb-color to grey.
		// originial formula: grey = 0.299 * red + 0.587 * green + 0.114 * blue

		long redIndex = 299 * (long) Math.pow(clr.getRed(), 3);
		long grnIndex = 587 * (long) Math.pow(clr.getGreen(), 3);
		long bluIndex = 114 * (long) Math.pow(clr.getBlue(), 3);

		return (int)((redIndex + grnIndex + bluIndex) / 1658137500l);
	}

    public BaseColor textColor() {
        int brightness = colorBrightness();
        if(brightness < 4) {
            return new BaseColor(Color.WHITE);
        } else {
            return new BaseColor(Color.BLACK);
        }
    }

	public static double GOLD_SECTION = 0.61803;
	public BaseColor darker(int times) {
		return new BaseColor(h, s, Math.min(1f,
				b * (float)Math.pow(GOLD_SECTION, times)));
	}

	public String toHexString() {
		String colString = Integer.toHexString(Color.HSBtoRGB(h, s, b) & 0xffffff).toUpperCase();
		return "#000000".substring(0,7 - colString.length()).concat(colString);
	}

	public float getAlpha() {
		return a;
	}

	public float getBrightness() {
		return b;
	}

	public float getHue() {
		return h;
	}

	public float getSaturation() {
		return s;
	}

	public boolean equals(Object o) {
		if(o instanceof BaseColor) {
			BaseColor c = (BaseColor)o;
			return c.h == h && c.s == s && c.b == b && c.a == a;
		}
		return false;
	}

	public int hashCode() {
		return toColor().hashCode() + 1;
	}

	public String toString() {
		return "hsba("+h+", "+s+", "+b+", "+a+")";
	}

	transient private PaintContext paintContext;
	public synchronized PaintContext createContext(ColorModel cm,
				      Rectangle deviceBounds,
				      Rectangle2D userBounds,
				      AffineTransform xform, RenderingHints hints) {
		PaintContext ctx = paintContext;
		if(ctx == null) {
			ctx = new BaseColorPaintContext(this);
			paintContext = ctx;
		}
		return ctx;
	}

	public int getTransparency() {
        if (a == 1f) {
            return Transparency.OPAQUE;
        } else if (a == 0) {
            return Transparency.BITMASK;
        } else {
            return Transparency.TRANSLUCENT;
        }
    }

	private static class BaseColorPaintContext implements PaintContext {
		private BaseColor color;
		private WritableRaster savedTile;

		public BaseColorPaintContext(BaseColor color) {
			this.color = color;
		}

		public void dispose() {
		}

		public ColorModel getColorModel() {
			return ColorModel.getRGBdefault();
		}

		public synchronized Raster getRaster(int x, int y, int w, int h) {
			WritableRaster raster = savedTile;
			if(raster == null || raster.getWidth() < w || raster.getHeight() < h) {
				// translate color into rgba
				Color rgb = color.toColor();
				int r = rgb.getRed(), g = rgb.getGreen(), b = rgb.getBlue(), a = rgb.getAlpha();
				// init raster and set colors
				raster = getColorModel().createCompatibleWritableRaster(w, h);
				int[] colors = new int[w*h*4];
				int offset = 0;
				for(int i = 0; i < w; i++) for(int j = 0; j < h; j++) {
					colors[offset++] = r;
					colors[offset++] = g;
					colors[offset++] = b;
					colors[offset++] = a;
				}
				raster.setPixels(0, 0, w, h, colors);
				// cache if raster is less than 64x64
				if(w <= 64 && h <= 64) {
					savedTile = raster;
				}
			}
			return raster;
		}
	}

	public static void main(String[] args) {
		BaseColor blue = new BaseColor(0x0000FF);
		// test complement calculation
		BaseColor complement = blue.complement();
		System.out.println(complement.toHexString());

        System.out.println(blue.shiftHue(180).toHexString());
        System.out.println(blue.getHue());

		// test toColor
		System.out.println(new BaseColor(blue.toColor()).toHexString());

		// ui test
		JFrame frm = new JFrame("Hello World");
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setSize(200, 200);
		frm.getContentPane().add(new TestTile(blue));
		frm.setVisible(true);
	}

	private static class TestTile extends JComponent {
		private BaseColor color;

		public TestTile(BaseColor color) {
			this.color = color;
			setPreferredSize(new Dimension(200, 200));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D gfx = (Graphics2D)g;
			gfx.setPaint(color);
			gfx.fillRect(0, 0, getWidth(), getHeight());
			gfx.setPaint(color.textColor());
			gfx.drawString("Hello World", 20, 20);
		}
	}
}
