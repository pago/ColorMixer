/*
 * TextFieldIconDecoration.java
 *
 * Created on 23. Juni 2006, 10:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.pagosoft.swing;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.JTextComponent;

/**
 *
 * @author pago
 */
public class TextFieldIconDecoration {
	// Creation ---------------------------------------------------------------------------
	private static final String CLIENT_KEY = "com.pagosoft.swing.TextFieldIconDecoration";
	
	public static TextFieldIconDecoration getInstance(JTextComponent c) {
		Object o = c.getClientProperty(CLIENT_KEY);
		if(o != null && o instanceof TextFieldIconDecoration) {
			return (TextFieldIconDecoration)o;
		}
		TextFieldIconDecoration deco = new TextFieldIconDecoration(c);
		c.putClientProperty(CLIENT_KEY, deco);
		return deco;
	}
	
	// Implementation------------------------------------------------------------------------
	public static enum Position {
		LINE_START, LINE_END
	}
	
	List<Icon> lineStartIcons;
	List<Icon> lineEndIcons;
	
	int iconGap = 5;
	
	private JTextComponent textComponent;
	
	private ButtonChangeHandler buttonChangeHandler;
	
	/** Creates a new instance of TextFieldIconDecoration */
	private TextFieldIconDecoration(JTextComponent c) {
		this.textComponent = c;
		
		lineStartIcons = new ArrayList<Icon>();
		lineEndIcons = new ArrayList<Icon>();
		
		textComponent.setBorder(new IconifiedBorder(this, textComponent.getBorder()));
		
		MouseHandler handler = new MouseHandler();
		textComponent.addMouseListener(handler);
		textComponent.addMouseMotionListener(handler);
		textComponent.addPropertyChangeListener("border", new BorderChangeHandler());
	}
	
	public void addIcon(Icon ico, Position position) {
		if(position == Position.LINE_START) {
			lineStartIcons.add(ico);
		} else if(position == Position.LINE_END) {
			lineEndIcons.add(ico);
		}
		addButtonHandler(ico);
		clearCache();
	}
	
	public void addIcon(Icon ico, Position position, int index) {
		if(position == Position.LINE_START) {
			lineStartIcons.add(index, ico);
		} else if(position == Position.LINE_END) {
			lineEndIcons.add(index, ico);
		}
		addButtonHandler(ico);
		clearCache();
	}
	
	public void setIcon(Icon ico, Position position, int index) {
		if(position == Position.LINE_START) {
			Icon old = lineStartIcons.get(index);
			if(old != null) {
				removeButtonHandler(old);
			}
			lineStartIcons.set(index, ico);
		} else if(position == Position.LINE_END) {
			Icon old = lineEndIcons.get(index);
			if(old != null) {
				removeButtonHandler(old);
			}
			lineEndIcons.set(index, ico);
		}
		addButtonHandler(ico);
		clearCache();
	}
	
	public void removeIcon(Icon ico) {
		if(!lineStartIcons.remove(ico)) {
			lineEndIcons.remove(ico);
		}
		removeButtonHandler(ico);
		clearCache();
	}
	
	private void addButtonHandler(Icon ico) {
		if(ico instanceof IconButton) {
			if(buttonChangeHandler == null) {
				buttonChangeHandler = new ButtonChangeHandler();
			}
			((IconButton)ico).addPropertyChangeListener("enabled", buttonChangeHandler);
		}
	}
	
	private void removeButtonHandler(Icon ico) {
		if(ico instanceof IconButton && buttonChangeHandler != null) {
			((IconButton)ico).removePropertyChangeListener("enabled", buttonChangeHandler);
		}
	}
	
	public int getIconIndex(Icon ico, Position position) {
		if(position == Position.LINE_START) {
			return lineStartIcons.indexOf(ico);
		} else if(position == Position.LINE_END) {
			return lineEndIcons.indexOf(ico);
		}
		// unreachable
		return -1;
	}
	
	private int lineStartIconWidth = -1;
	int getLineStartIconWidth() {
		if(lineStartIconWidth == -1) {
			lineStartIconWidth = getIconWidth(lineStartIcons);
		}
		return lineStartIconWidth;
	}
	
	private int lineEndIconWidth = -1;
	int getLineEndIconWidth() {
		if(lineEndIconWidth == -1) {
			lineEndIconWidth = getIconWidth(lineEndIcons);
		}
		return lineEndIconWidth;
	}
	
	private int getIconWidth(List<Icon> icons) {
		int width = 0;
		for(int i = 0; i < icons.size(); i++) {
			if(i != 0) {
				width += iconGap;
			}
			width += icons.get(i).getIconWidth();
		}
		return width;
	}
	
	private int iconHeight = -1;
	int getIconHeight() {
		if(iconHeight == -1) {
			iconHeight = Math.max(getIconHeight(lineStartIcons),
					getIconHeight(lineEndIcons));
		}
		return iconHeight;
	}
	
	private int getIconHeight(List<Icon> icons) {
		int height = 0;
		for(int i = 0; i < icons.size(); i++) {
			height = Math.max(height, icons.get(i).getIconHeight());
		}
		return height;
	}
	
	private void clearCache() {
		((IconifiedBorder) textComponent.getBorder()).insets = null;
		lineEndIconWidth   = -1;
		lineStartIconWidth = -1;
		iconHeight         = -1;
		textComponent.repaint();
	}
	
	public Icon getIconAt(Point p) {
		return ((IconifiedBorder)textComponent.getBorder()).getIconAt(textComponent, p);
	}
	
	private class MouseHandler extends MouseInputAdapter {
		private MouseSensitiveIcon last;
		
		public void mouseMoved(MouseEvent e) {
			Icon ico = getIconAt(e.getPoint());
			MouseIconEvent evt = new MouseIconEvent(textComponent);
			if(last != null && last != ico) {
				last.mouseExit(evt);
			}
			if(ico instanceof MouseSensitiveIcon) {
				((MouseSensitiveIcon)ico).mouseOver(evt);
				last = (MouseSensitiveIcon)ico;
			}
		}

		public void mouseReleased(MouseEvent e) {
			Icon ico = getIconAt(e.getPoint());
			if(ico instanceof MouseSensitiveIcon) {
				((MouseSensitiveIcon)ico).mouseClicked(new MouseIconEvent(textComponent, e.getButton(), e.getClickCount()));
				e.consume();
			}
		}
	}
	
	private class BorderChangeHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			final Border oldBorder = (Border)evt.getOldValue();
			Border newBorder = (Border)evt.getNewValue();
			if(!(oldBorder instanceof IconifiedBorder) || newBorder instanceof IconifiedBorder) {
				// unsure on how to handle this.
				return;
			}
			((IconifiedBorder)oldBorder).innerBorder = newBorder;
			// overwrite new border with the old one
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					textComponent.setBorder(oldBorder);
					clearCache();
				}
			});
		}
	}
	
	private class ButtonChangeHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if("enabled".equals(evt.getPropertyName())) {
				textComponent.repaint();
			}
		}
	}
}
