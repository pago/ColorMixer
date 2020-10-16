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

import com.pagosoft.action.ActionFactory;
import com.pagosoft.action.ActionMethod;
import com.pagosoft.eventbus.EventBus;
import com.pagosoft.swing.BaseColor;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author Patrick
 */
public class ColorDetailsPanel extends JComponent {
	private BaseColor color;
	private String colorAsHex;
	private String colorAsInt;
	
	/** Creates a new instance of ColorPanel */
	public ColorDetailsPanel() {
		setMinimumSize(new Dimension(100, 120));
		setPreferredSize(getMinimumSize());
		
		color = new BaseColor(0x0000FF);
		colorAsHex = "Hex: "+color.toHexString();
		Color rgbColor = color.toColor();
		colorAsInt = String.format("RGB: %s,%s,%s", rgbColor.getRed(),
				rgbColor.getGreen(), rgbColor.getBlue());
		
		MouseHandler handler = new MouseHandler(this);
		addMouseListener(handler);
		addMouseMotionListener(handler);
		
		setTransferHandler(new ColorTransferHandler());
		
		setOpaque(true);
		//setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		setBackground(color.toColor());
	}
	
	public void setColor(BaseColor color) {
		this.color = color;
		colorAsHex = "Hex: "+color.toHexString();
		Color rgbColor = color.toColor();
		colorAsInt = String.format("RGB: %s,%s,%s", rgbColor.getRed(),
				rgbColor.getGreen(), rgbColor.getBlue());
//	setBackground(color);
		repaint();
	}
	
	public BaseColor getColor() {
		return color;
	}
	
	///*
    @Override
	public void paintComponent(Graphics g) {
		g.setColor(getColor().toColor());
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.WHITE);
		g.drawRect(1, 1, getWidth()-3, getHeight()-3);

		g.setColor(new Color(148, 148, 148));
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		
		
        g.setColor(getColor().textColor().toColor());
		g.drawString(colorAsHex, 5, 15);
	}//*/
	
	/*
	public void paintComponent(Graphics g) {
	Graphics2D gfx = (Graphics2D)g;
	int x = 0;
	int y = 0;
	int width = getWidth();
	int height = getHeight();
	 
	// draw basic shape and border
	Shape oldClip = gfx.getClip();
	Shape shape = new RoundRectangle2D.Double(x, y, width, height, 10, 10);
	gfx.setClip(shape);
	gfx.setPaint(new GradientPaint(0, 0, new Color(0xFEFEFE), 0, height, new Color(0xD7D7D7)));
	gfx.fillRect(x, y, width, height);
	gfx.setClip(oldClip);
	gfx.setColor(Color.BLACK);
	Object oldHint = gfx.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
	gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	gfx.drawRoundRect(x, y, width-1, height-1, 10, 10);
	gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
	 
	// draw text
	Font oldFont = gfx.getFont();
	Font font = oldFont.deriveFont(10f);
	gfx.setFont(font);
	FontMetrics fm = gfx.getFontMetrics();
	gfx.drawString(colorAsInt, 15, height-4);
	gfx.drawString(colorAsHex, 15, height-4-fm.getHeight());
	 
	// draw the color itself
	gfx.setColor(color);
	gfx.fillRect(15, 15, width-30, height-(2*fm.getHeight())-15);
	gfx.setColor(Color.BLACK);
	gfx.drawRect(15, 15, width-30, height-(2*fm.getHeight())-15);
	}//*/
	
	public static class MouseHandler extends MouseInputAdapter {
		private ColorDetailsPanel panel;
		private Map<String,Action> actionMap;
		
		public MouseHandler(ColorDetailsPanel panel) {
			this.panel = panel;
			actionMap = ActionFactory.createActionMap(this);
		}
		
		public void mouseClicked(MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) {
				JPopupMenu menu = new JPopupMenu();
				Action act = actionMap.get("copyHex");
				act.putValue(Action.NAME, panel.getColor().toHexString());
				menu.add(act);

				act = actionMap.get("copyHexWithoutSharp");
				act.putValue(Action.NAME, panel.getColor().toHexString().substring(1));
				menu.add(act);

				menu.addSeparator();

				Color c = panel.getColor().toColor();
				act = actionMap.get("copyRGBasCSS");
				act.putValue(Action.NAME, String.format("rgb(%d, %d, %d)", c.getRed(), c.getGreen(), c.getBlue()));
				menu.add(act);

				act = actionMap.get("copyRGB");
				act.putValue(Action.NAME, String.format("%d, %d, %d", c.getRed(), c.getGreen(), c.getBlue()));
				menu.add(act);
				
				menu.show(panel, e.getX(), e.getY());
			} else if(e.getClickCount() == 2) {
				EventBus.getInstance().fireEvent(new ColorChangedEvent(panel.getColor()));
			}
		}
		
		MouseEvent firstMouseEvent = null;
		
		public void mousePressed(MouseEvent e) {
			firstMouseEvent = e;
			e.consume();
		}
		
		public void mouseDragged(MouseEvent e) {
			if (firstMouseEvent != null) {
				e.consume();
				
				//If they are holding down the control key, COPY rather than MOVE
				int action = TransferHandler.COPY;
				
				int dx = Math.abs(e.getX() - firstMouseEvent.getX());
				int dy = Math.abs(e.getY() - firstMouseEvent.getY());
				//Arbitrarily define a 5-pixel shift as the
				//official beginning of a drag.
				if (dx > 5 || dy > 5) {
					//This is a drag, not a click.
					JComponent c = (JComponent)e.getSource();
					//Tell the transfer handler to initiate the drag.
					TransferHandler handler = c.getTransferHandler();
					handler.exportAsDrag(c, firstMouseEvent, action);
					firstMouseEvent = null;
				}
			}
		}
		
		public void mouseReleased(MouseEvent e) {
			firstMouseEvent = null;
		}
		
		@ActionMethod(id="copyHex")
		public void copyHex() {
			toClipboard(panel.getColor().toHexString());
		}

		@ActionMethod
		public void copyHexWithoutSharp() {
			toClipboard(panel.getColor().toHexString().substring(1));
		}

		@ActionMethod
		public void copyRGBasCSS() {
			Color c = panel.getColor().toColor();
			toClipboard(String.format("rgb(%d, %d, %d)", c.getRed(), c.getGreen(), c.getBlue()));
		}

		@ActionMethod
		public void copyRGB() {
			Color c = panel.getColor().toColor();
			toClipboard(String.format("%d, %d, %d", c.getRed(), c.getGreen(), c.getBlue()));
		}
		
		private void toClipboard(String str) {
			StringSelection s = new StringSelection(str);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, s);
		}
	}
	
	public static void main(String[] args) {
		JFrame frm = new JFrame("Test");
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setSize(120, 140);
		((JComponent)frm.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frm.add(new ColorDetailsPanel());
		frm.setVisible(true);
	}
}
