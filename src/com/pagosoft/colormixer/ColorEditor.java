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
import com.pagosoft.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Patrick
 */
public class ColorEditor extends JPanel {
	private JTextField colorField;
	private BaseColor color;
	
	/** Creates a new instance of ColorEditor */
	public ColorEditor() {
		super(new BorderLayout(2, 0));
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		add(new JLabel("Color"), BorderLayout.LINE_START);
		
		ActionHandler handler = new ActionHandler();
		colorField = new JTextField("#0000FF", 10);
		colorField.setDragEnabled(true);
		colorField.setTransferHandler(new ColorTransferHandler());
		colorField.addActionListener(handler);
		add(colorField, BorderLayout.CENTER);
		
		TextFieldIconDecoration deco = TextFieldIconDecoration.getInstance(colorField);
		
		IconButton btn = new IconButton(new ImageIcon(ColorEditor.class.getResource("view-refresh.png")));
		btn.addActionListener(handler);
		deco.addIcon(btn, TextFieldIconDecoration.Position.LINE_END);
		
		EventBus.getInstance().add(new ApplicationHandler(this));
	}
	
	@EventHandler(ColorChangedEvent.class)
	public void colorChanged(ColorChangedEvent e) {
		if(e.getSource() != color) {
			color = e.getSource();
			colorField.setText(color.toHexString());
		}
	}
	
	private class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				color = new BaseColor(ColorUtils.toColor(colorField.getText()));
				EventBus.getInstance().fireEvent(new ColorChangedEvent(color));
			} catch(Exception ex) {
				// handle it later
			}
		}
	}
}
