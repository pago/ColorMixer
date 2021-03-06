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

import com.pagosoft.action.ActionButtonProvider;
import javax.swing.AbstractButton;
import javax.swing.JComponent;

/**
 *
 * @author Patrick
 */
public class ToolBarButtonProvider extends ActionButtonProvider {
	public JComponent createComponent(Object object) {
		JComponent c = super.createComponent(object);
		if(c instanceof AbstractButton) {
			AbstractButton btn = (AbstractButton)c;
			btn.setToolTipText(btn.getText());
			btn.setText("");
		}
		return c;
	}
}
