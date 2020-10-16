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
package com.pagosoft.swing;


import com.pagosoft.plaf.PgsLookAndFeel;
import com.pagosoft.plaf.PgsUtils;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import javax.swing.*;

/**
 *
 * @author Patrick
 */
public class TitleLabel extends JLabel {
	
	/** Creates a new instance of TitleLabel */
	public TitleLabel(String title) {
		super(title);
		
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setFont(getFont().deriveFont(10f));
	}

	protected void paintComponent(Graphics g) {
		PgsUtils.drawGradient(g, getWidth(), getHeight(),
				PgsLookAndFeel.getControl(),
				PgsLookAndFeel.getControlShadow());
		Color oldColor = g.getColor();
		g.setColor(PgsLookAndFeel.getControlDarkShadow());
		g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);

		if(getClientProperty("drawUpperBorder") == Boolean.TRUE) {
			g.drawLine(0, 0, getWidth(), 0);
		}

		g.setColor(oldColor);

		
		super.paintComponent(g);
	}
}
