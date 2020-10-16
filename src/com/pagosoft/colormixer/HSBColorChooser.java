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
package com.pagosoft.colormixer;

import com.pagosoft.colormixer.ui.BrightnessSliderUI;
import com.pagosoft.colormixer.ui.HueSliderUI;
import com.pagosoft.colormixer.ui.SaturationSliderUI;
import com.pagosoft.eventbus.ApplicationHandler;
import com.pagosoft.eventbus.EventBus;
import com.pagosoft.eventbus.EventHandler;
import com.pagosoft.swing.BaseColor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 *
 * @author pago
 */
public class HSBColorChooser extends JPanel {

	private ColorComponentChooser[] choosers;

	/** Creates a new instance of HSBColorChooser */
	public HSBColorChooser() {
		super(new GridLayout(3, 1, 0, 2));

		choosers = new ColorComponentChooser[] {
					new ColorComponentChooser(this, ColorComponent.HUE),
					new ColorComponentChooser(this, ColorComponent.SATURATION),
					new ColorComponentChooser(this, ColorComponent.BRIGTHNESS)
				};

		for(ColorComponentChooser chooser : choosers) {
			add(chooser);
		}

		setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

		EventBus.getInstance().add(new ApplicationHandler(this));
	}

	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		//d.height = 80;
		//d.width = 180;
		return d;
	}
	private boolean isAdjusting = false;

	private void updateColor() {
		if(!isAdjusting) {
			EventBus.getInstance().fireEvent(new ColorChangedEvent(getCurrentColor()));
		}
	}

	private BaseColor getCurrentColor() {
		return new BaseColor(
				choosers[0].getComponent(),
				choosers[1].getComponent(),
				choosers[2].getComponent());
	}

	@EventHandler(ColorChangedEvent.class)
	public void colorChanged(ColorChangedEvent e) {
		isAdjusting = true;
		BaseColor c = e.getSource();
		choosers[0].setComponent(c.getHue());
		choosers[1].setComponent(c.getSaturation());
		choosers[2].setComponent(c.getBrightness());
		isAdjusting = false;
	}

	enum ColorComponent {
		HUE {
			@Override
			public SliderUI getSliderUI(JSlider slider) {
				return new HueSliderUI(slider);
			}

			public int getMaxValue() { return 360; }

		}, SATURATION {
			@Override
			public SliderUI getSliderUI(JSlider slider) {
				return new SaturationSliderUI(slider);
			}

			public int getMaxValue() { return 100; }

		}, BRIGTHNESS {
			@Override
			public SliderUI getSliderUI(JSlider slider) {
				return new BrightnessSliderUI(slider);
			}

			public int getMaxValue() { return 100; }

		};

		public abstract SliderUI getSliderUI(JSlider slider);
		public abstract int getMaxValue();
	}

	private static class ColorComponentChooser extends JPanel {
		JSlider slider;
		JTextField textField;
		HSBColorChooser chooser;
		private final ColorComponent comp;

		public ColorComponentChooser(final HSBColorChooser chooser, ColorComponent comp) {
			super(new BorderLayout(2, 0));

			this.chooser = chooser;
			this.comp = comp;

			slider = new JSlider(JSlider.HORIZONTAL, 0, comp.getMaxValue(), comp.getMaxValue());
			slider.setUI(comp.getSliderUI(slider));
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					chooser.updateColor();
				}
			});

			textField = new JTextField(String.valueOf(comp.getMaxValue()), 3);
			textField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateIfValid();
				}
			});
			/*textField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
			updateIfValid();
			}
			});*/

			//add(new JLabel(index2str[compIndex]), BorderLayout.LINE_START);
			add(slider, BorderLayout.CENTER);
			add(textField, BorderLayout.LINE_END);
		}

		public float getComponent() {
			return ((float)slider.getValue()/(float)comp.getMaxValue());
		}

		public void setComponent(float value) {
			int val = (int)(value * comp.getMaxValue());
			slider.setValue(val);
			textField.setText(String.valueOf(val));
		}

		private void updateIfValid() {
			try {
				int i = Integer.parseInt(textField.getText());
				if(0 <= i && i <= comp.getMaxValue()) {
					slider.setValue(i);
					chooser.updateColor();
				}
			} catch(Exception ex) {
				// can be ignored
			}
		}
	}
}
