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

import com.pagosoft.layout.FillBoxLayout;
import com.pagosoft.plaf.PgsLookAndFeel;
import com.pagosoft.swing.TitleLabel;
import com.pagosoft.swing.TitledPanel;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author Patrick
 */
public class ColorMixer extends JFrame {
	private ColorMixerResultPanel mixerPanel;
	private ColorPalette palette;
	
	/** Creates a new instance of ColorMixer */
	public ColorMixer() {
		super("ColorMixer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//rootPane.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
		
		mixerPanel = new ColorMixerResultPanel();
		add(new TitledPanel("Suggested Colors", mixerPanel), BorderLayout.CENTER);
		
		JPanel left = new JPanel(new BorderLayout());
		left.setBorder(new AbstractBorder() {
			public Insets getBorderInsets(Component c, Insets insets) {
				insets.left = insets.top = insets.bottom = 0;
				insets.right = 1;
				return insets;
			}

			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				g.setColor(PgsLookAndFeel.getControlDarkShadow());
				g.drawLine(width-1, 0, width-1, height-1);
			}

			public Insets getBorderInsets(Component c) {
				return new Insets(0, 0, 0, 1);
			}
			
		});
		left.add(new ColorMixerPanel(), BorderLayout.PAGE_START);
		palette = new ColorPalette();
		left.add(palette, BorderLayout.CENTER);
		left.setPreferredSize(new Dimension(225, 300));
		add(left, BorderLayout.LINE_START);
		
		pack();
	}
	
	public ColorPalette getPalette() {
		return palette;
	}
	
	public ColorMixerResultPanel getMixerPanel() {
		return mixerPanel;
	}
}
