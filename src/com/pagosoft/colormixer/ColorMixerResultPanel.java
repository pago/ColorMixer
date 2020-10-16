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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.pagosoft.eventbus.*;
import com.pagosoft.swing.BaseColor;
/**
 *
 * @author Patrick
 */
public class ColorMixerResultPanel extends JPanel {
	private ColorDetailsPanel[] detailsPanel;
	private BaseColor[] colors;
	
	public ColorMixerResultPanel() {
		super(new GridLayout(3, 3, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		detailsPanel = new ColorDetailsPanel[9];
		for(int i = 0; i < detailsPanel.length; i++) {
			detailsPanel[i] = new ColorDetailsPanel();
			add(detailsPanel[i]);
		}
		
		EventBus.getInstance().add(new ApplicationHandler(this));
	}
	
	public void setColors(BaseColor[] colors) {
		this.colors = colors;
		for(int i = 0; i < detailsPanel.length; i++) {
			detailsPanel[i].setColor(colors[Math.min(i, colors.length-1)]);
		}
	}
	
	@EventHandler(ColorChangedEvent.class)
	public void handleColorChanged(ColorChangedEvent e) {
		setColors(e.getMixedColors());
	}
	
	private BufferedImage img;
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(img == null) {
			try {
				img = ImageIO.read(ColorMixerResultPanel.class.getResource("background.png"));
			} catch (IOException ex) {
				ex.printStackTrace();
				return;
			}
		}
		
		int width = getWidth();
		int height = getHeight();
		
		Graphics2D gfx = (Graphics2D)g;
		Paint oldPaint = gfx.getPaint();
		gfx.setPaint(new TexturePaint(img, new Rectangle2D.Float(0, 0, 15, 15)));
		gfx.fillRect(0, 0, width, height);
	}
	
	public static void main(String[] args) {
		JFrame frm = new JFrame("Test");
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setSize(360, 340);
		((JComponent)frm.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		ColorMixerResultPanel panel = new ColorMixerResultPanel();
		ColorCalculator gncc = new ComplementColorCalculator();
		panel.setColors(gncc.calculateColors(new BaseColor(0xFF0000)));
		
		frm.add(panel);
		frm.setVisible(true);
	}
	
	public BaseColor[] getColors() {
		return colors;
	}
}
