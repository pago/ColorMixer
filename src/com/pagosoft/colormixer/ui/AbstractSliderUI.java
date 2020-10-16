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
package com.pagosoft.colormixer.ui;

import com.pagosoft.colormixer.ColorChangedEvent;
import com.pagosoft.eventbus.ApplicationHandler;
import com.pagosoft.eventbus.EventBus;
import com.pagosoft.eventbus.EventHandler;
import com.pagosoft.swing.BaseColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 *
 * @author pago
 */
public abstract class AbstractSliderUI extends BasicSliderUI {
	private BaseColor color;

	public AbstractSliderUI(JSlider b) {
		super(b);
	}

	@EventHandler(ColorChangedEvent.class)
	public void colorChanged(ColorChangedEvent e) {
		color = e.getSource();
		slider.repaint();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		c.putClientProperty("Slider.paintThumbArrowShape", Boolean.TRUE);
		EventBus.getInstance().add(new ApplicationHandler(this));
	}

	@Override
	public Dimension getPreferredHorizontalSize() {
		return new Dimension(255, 16);
	}

	@Override
	public void paintTrack(Graphics g) {
		int x = trackRect.x, y = trackRect.y,
			width = trackRect.width, height = trackRect.height;
		Graphics2D gfx = (Graphics2D)g;
		gfx.setPaint(getBackgroundPaint(color));
		gfx.fillRect(x, y, width, height);

		g.setColor(Color.WHITE);
		g.drawRect(x + 1, y + 1, width - 3, height - 3);
		Color outer = new Color(148, 148, 148);
		g.setColor(outer);
		g.drawRect(x, y, width - 1, height - 1);
	}

	protected abstract Paint getBackgroundPaint(BaseColor color);

	@Override
	public void paintThumb(Graphics g) {
		Graphics2D gfx = (Graphics2D)g;
		Rectangle knobBounds = thumbRect;
		int w = knobBounds.width;
		int h = knobBounds.height;

		g.translate(knobBounds.x, knobBounds.y);

		int cw = w / 2;
		Polygon p = new Polygon();
		p.addPoint(1, h - cw);
		p.addPoint(cw - 1, h - 1);
		p.addPoint(w - 2, h - 1 - cw);
		gfx.setPaint(new GradientPaint(0, 0, new Color(245, 245, 245),
				0, h, new Color(100, 100, 255)));
		g.fillRect(1, 1, w - 3, h - 1 - cw);
		g.fillPolygon(p);

		Color outer = new Color(108, 108, 108);

		g.setColor(outer);
		g.drawLine(0, 0, w - 2, 0);
		g.drawLine(0, 1, 0, h - 1 - cw);
		g.drawLine(0, h - cw, cw - 1, h - 1);

		g.drawLine(w - 1, 0, w - 1, h - 2 - cw);
		g.drawLine(w - 1, h - 1 - cw, w - 1 - cw, h - 1);

		g.drawLine(w - 2, 1, w - 2, h - 2 - cw);
		g.drawLine(w - 2, h - 1 - cw, w - 1 - cw, h - 2);

		g.translate(-knobBounds.x, -knobBounds.y);
	}

	@Override
	public void paintFocus(Graphics g) {
	}
}
