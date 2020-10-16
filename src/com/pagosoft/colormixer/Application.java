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

import com.pagosoft.eventbus.EventBus;
import com.pagosoft.plaf.PlafOptions;
import com.pagosoft.plaf.themes.*;
import com.pagosoft.swing.BaseColor;
import com.pagosoft.swing.SimpleFileFilter;
import java.awt.Color;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author pago
 */
public class Application {
	private ColorCalculator calculator;
	
	public ColorCalculator getCalculator() {
		return calculator;
	}
	
	public void setCalculator(ColorCalculator calculator) {
		this.calculator = calculator;
		EventBus.getInstance().fireEvent(new ColorChangedEvent(frame.getMixerPanel().getColors()[0]));
	}
	
	private static JFileChooser fileChooser;
	public static final FileFilter acoFilter = new SimpleFileFilter("Adobe Color Object (*.aco)", ".aco");
	public static final FileFilter cmpFilter = new SimpleFileFilter("ColorMixer Palette (*.cmp)", ".cmp");
	public static JFileChooser getFileChooser() {
		if(fileChooser == null) {
			fileChooser = new JFileChooser();
		}
		return fileChooser;
	}
	
	private static ColorMixer frame;
	public static ColorMixer getFrame() {
		return frame;
	}
	
	
	private static Application instance;
	private Application() {
		instance = this;
		calculator =  new ComplementColorCalculator();
		
		frame = new ColorMixer();
		frame.setLocationRelativeTo(null);
		
		EventBus.getInstance().fireEvent(new ColorChangedEvent(new BaseColor(.5f, 1f, 1f)));
		
		frame.setVisible(true);
	}
	
	public static Application getInstance() {
		return instance;
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PlafOptions.setCurrentTheme(new SilverTheme());
				PlafOptions.setAsLookAndFeel();
				new Application();
			}
		});
	}
}
