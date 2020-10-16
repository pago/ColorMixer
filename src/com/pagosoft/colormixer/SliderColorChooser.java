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

import com.pagosoft.eventbus.ApplicationHandler;
import com.pagosoft.eventbus.EventBus;
import com.pagosoft.eventbus.EventHandler;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author Patrick
 */
public class SliderColorChooser extends JPanel {
	private ColorComponentChooser[] choosers;
	/** Creates a new instance of SliderColorChooser */
	public SliderColorChooser() {
		super(new GridLayout(3, 1, 0, 2));
		
		choosers = new ColorComponentChooser[] {
			new ColorComponentChooser(this, 0),
			new ColorComponentChooser(this, 1),
			new ColorComponentChooser(this, 2)
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
	
	private Color getCurrentColor() {
		return new Color(
				choosers[0].getComponent(),
				choosers[1].getComponent(),
				choosers[2].getComponent());
	}
	
	@EventHandler(ColorChangedEvent.class)
	public void colorChanged(ColorChangedEvent e) {
		isAdjusting = true;
		Color c = e.getSource().toColor();
		choosers[0].setComponent(c.getRed());
		choosers[1].setComponent(c.getGreen());
		choosers[2].setComponent(c.getBlue());
		isAdjusting = false;
	}
	
	private static class ColorComponentChooser extends JPanel {
		private static String[] index2str = {"R", "G", "B"};
		
		JSlider slider;
		JTextField textField;
		
		SliderColorChooser chooser;
		
		public ColorComponentChooser(final SliderColorChooser chooser, int compIndex) {
			super(new BorderLayout(2, 0));
			
			this.chooser = chooser;
			
			slider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					chooser.updateColor();
				}
			});
			
			textField = new JTextField("255", 3);
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
			
			add(new JLabel(index2str[compIndex]), BorderLayout.LINE_START);
			add(slider, BorderLayout.CENTER);
			add(textField, BorderLayout.LINE_END);
		}
		
		public int getComponent() {
			return slider.getValue();
		}
		
		public void setComponent(int value) {
			slider.setValue(value);
			textField.setText(String.valueOf(value));
		}
		
		private void updateIfValid() {
			try {
				int i = Integer.parseInt(textField.getText());
				if(0 <= i && i <= 255) {
					slider.setValue(i);
					chooser.updateColor();
				}
			} catch(Exception ex) {
				// can be ignored
			}
		}
	}
}
