package com.pagosoft.swing;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.Icon;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;


class IconifiedBorder extends AbstractBorder {
	private TextFieldIconDecoration deco;

	Border innerBorder;
	Insets insets;
	
	public IconifiedBorder(TextFieldIconDecoration deco, Border innerBorder) {
		this.deco = deco;
		this.innerBorder = innerBorder;
	}
	
	public Border getInnerBorder() {
		return innerBorder;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		innerBorder.paintBorder(c, g, x, y, width, height);
		Insets innerInsets = innerBorder.getBorderInsets(c);
		// TODO: Add right-to-left support
		// first the line start icons
		int ix = x + innerInsets.left;
		int iy = y + innerInsets.top;
		for (int i = 0; i < deco.lineStartIcons.size(); i++) {
			Icon icon = (Icon)deco.lineStartIcons.get(i);
			icon.paintIcon(c, g, ix, iy);
			ix += deco.iconGap + icon.getIconWidth();
		}
		
		// second: the line end icons
		ix = x + width - innerInsets.right;
		for (int i = deco.lineEndIcons.size()-1; i >= 0; i--) {
			Icon icon = (Icon)deco.lineEndIcons.get(i);
			ix -= deco.iconGap + icon.getIconWidth();
			icon.paintIcon(c, g, ix, iy);
		}
	}
	
	public Icon getIconAt(Component c, Point p) {
		Insets innerInsets = innerBorder.getBorderInsets(c);
		// TODO: Add right-to-left support
		// first the line start icons
		int ix = innerInsets.left;
		int iy = innerInsets.top;
		for (int i = 0; i < deco.lineStartIcons.size(); i++) {
			Icon icon = (Icon)deco.lineStartIcons.get(i);
			if(ix <= p.x && p.x <= ix + icon.getIconWidth()) {
				return icon;
			}
			ix += deco.iconGap + icon.getIconWidth();
		}
		
		// second: the line end icons
		ix = c.getWidth() - innerInsets.right - deco.iconGap - 2;
		for (int i = deco.lineEndIcons.size()-1; i >= 0; i--) {
			Icon icon = (Icon)deco.lineEndIcons.get(i);
			if(ix - icon.getIconWidth() <= p.x && p.x <= ix) {
				return icon;
			}
			ix -= deco.iconGap + icon.getIconWidth();
		}
		return null;
	}

	public Insets getBorderInsets(Component c) {
		if (insets == null) {
			JTextComponent txt = (JTextComponent)c;
			insets = (Insets) innerBorder.getBorderInsets(c).clone();
			insets.left += deco.getLineStartIconWidth();
			insets.right += deco.getLineEndIconWidth() + deco.iconGap + 2; // minor correction
			
			Insets margin = txt.getMargin();
			FontMetrics fm = txt.getFontMetrics(txt.getFont());
			double diff = deco.getIconHeight() - fm.getHeight();
			if(diff > 0) {
				insets.top += diff/2;
				insets.bottom += diff/2;
			}
		}
		return insets;
	}
}