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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Patrick Gotthardt
 */
public class BasicMouseSensitiveIcon implements MouseSensitiveIcon {
	private Icon delegate;
	public BasicMouseSensitiveIcon(Icon delegate) {
		this.delegate = delegate;
	}

	public void mouseOver(MouseIconEvent e) {
		((JComponent)e.getSource()).setCursor(Cursor.getDefaultCursor());
	}
	
	public void mouseExit(MouseIconEvent e) {
		((JComponent)e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
	}

	public void mouseClicked(MouseIconEvent e) {
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		delegate.paintIcon(c, g, x, y);
	}

	public int getIconWidth() {
		return delegate.getIconWidth();
	}

	public int getIconHeight() {
		return delegate.getIconHeight();
	}
	
	public Icon getIcon() {
		return delegate;
	}
}
