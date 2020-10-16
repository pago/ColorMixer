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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Patrick Gotthardt
 */
public class IconButton extends BasicMouseSensitiveIcon {
	private EventListenerList listenerList;
	private Icon rolloverIcon;
	private Icon disabledIcon;
	private boolean enabled;
	private boolean rollover;
	private PropertyChangeSupport support;
	
	public IconButton(String url) {
		this(new ImageIcon(url));
	}
	
	public IconButton(URL url) {
		this(new ImageIcon(url));
	}
	
	public IconButton(Action action) {
		this((Icon)action.getValue(Action.SMALL_ICON));
		addActionListener(action);
	}
	
	public IconButton(Icon ico) {
		super(ico);
		listenerList = new EventListenerList();
		support = new PropertyChangeSupport(this);
		enabled = true;
		rollover = false;
	}
	
	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}
	
	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if(rollover && rolloverIcon != null) {
			rolloverIcon.paintIcon(c, g, x, y);
		} else if(!enabled && disabledIcon != null) {
			disabledIcon.paintIcon(c, g, x, y);
		} else {
			super.paintIcon(c, g, x, y);
		}
	}
	
	public void mouseOver(MouseIconEvent e) {
		super.mouseOver(e);
		if(rolloverIcon != null && !rollover) {
			rollover = true;
			((JTextComponent)e.getSource()).repaint();
			support.firePropertyChange("rollover", false, true);
		}
	}
	
	public void mouseExit(MouseIconEvent e) {
		super.mouseExit(e);
		rollover = false;
		if(rolloverIcon != null) {
			((JTextComponent)e.getSource()).repaint();
			support.firePropertyChange("rollover", true, false);
		}
	}

	public void mouseClicked(MouseIconEvent e) {
		super.mouseClicked(e);
		if(!enabled) return;
		ActionListener[] listeners = listenerList.getListeners(ActionListener.class);
		ActionEvent event = null;
		for(int i = listeners.length-1; i >= 0; i--) {
			if(event == null) {
				event = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, null);
			}
			listeners[i].actionPerformed(event);
		}
	}

	public Icon getRolloverIcon() {
		return rolloverIcon;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isRollover() {
		return rollover;
	}

	public void setEnabled(boolean enabled) {
		if(this.enabled != enabled) {
			this.enabled = enabled;
			support.firePropertyChange("enabled", !enabled, enabled);
		}
	}

	public void setRolloverIcon(Icon rolloverIcon) {
		if(this.rolloverIcon != rolloverIcon) {
			Icon old = this.rolloverIcon;
			this.rolloverIcon = rolloverIcon;
			support.firePropertyChange("rolloverIcon", old, rolloverIcon);
		}
	}

	public Icon getDisabledIcon() {
		return disabledIcon;
	}

	public void setDisabledIcon(Icon disabledIcon) {
		this.disabledIcon = disabledIcon;
	}
	
	public void addPropertyChangeListener(String property, PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(String property, PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}
}
