/*
 * Copyright 2005 Patrick Gotthardt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pagosoft.swing;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Patrick Gotthardt
 */
public class IconPopupButton extends IconButton implements ActionListener {
	private JPopupMenu popup;
	private int xPos = -1;
	
	public IconPopupButton(String url) {
		super(url);
		init();
	}
	
	public IconPopupButton(URL url) {
		super(url);
		init();
	}
	
	public IconPopupButton(Action action) {
		super(action);
		init();
	}
	
	public IconPopupButton(Icon ico) {
		super(ico);
		init();
	}

	public JPopupMenu getPopup() {
		return popup;
	}

	public void setPopup(JPopupMenu popup) {
		this.popup = popup;
	}
	
	private void init() {
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if(popup != null) {
			if(popup.isVisible()) {
				popup.setVisible(false);
			} else {
				popup.show((Component)e.getSource(), xPos, getIconHeight());
			}
		}
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		super.paintIcon(c, g, x, y);
		xPos = x;
	}
}
