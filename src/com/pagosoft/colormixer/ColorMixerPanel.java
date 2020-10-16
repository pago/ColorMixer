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
import com.pagosoft.action.ActionObject;
import com.pagosoft.action.ContainerBuilder;
import com.pagosoft.eventbus.EventBus;
import com.pagosoft.layout.FillBoxLayout;
import com.pagosoft.swing.BaseColor;
import com.pagosoft.swing.ScreenShot;
import com.pagosoft.swing.TitleLabel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.*;

/**
 *
 * @author Patrick
 */
public class ColorMixerPanel extends JPanel {
	private ActionObject obj;
	/** Creates a new instance of ColorMixerPanel */
	public ColorMixerPanel() {
		super(new FillBoxLayout(FillBoxLayout.HORIZONTAL));
		
		add(new TitleLabel("Color Selection"));
		add(new HSBColorChooser());
		add(Box.createVerticalStrut(10));
		add(new ColorEditor());
		
		obj = new ActionObject(this);
		obj.get("setComplementCalculator").putValue(Action.SMALL_ICON, new RedButtonIcon());
		obj.get("setTriadicCalculator").putValue(Action.SMALL_ICON, new GreenButtonIcon());
		obj.get("setAnalogousCalculator").putValue(Action.SMALL_ICON, new BlueButtonIcon());
		obj.get("setSplitComplementsCalculator").putValue(Action.SMALL_ICON, new RedButtonIcon());
		
		JToolBar jtb = new ContainerBuilder()
			.container("main")
				.add(obj.get("showColorChooser"))
				.add(obj.get("pickScreenColor"))
				.addSeparator()
				.add(obj.get("setComplementCalculator"))
				.add(obj.get("setTriadicCalculator"))
				.add(obj.get("setAnalogousCalculator"))
				.add(obj.get("setSplitComplementsCalculator"))
				.addSeparator()
				.add(obj.get("showAbout"))
			.getContainer().createToolBar(new ToolBarButtonProvider());
		jtb.setFloatable(false);
		jtb.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
		// some UI-magic
		jtb.putClientProperty("gradientStart", UIManager.getColor("Panel.background"));
		add(jtb);
	}
	
	@ActionMethod(label="About", icon="/com/pagosoft/colormixer/help-browser.png")
	private void showAbout() {
		final JWindow wnd = new JWindow(Application.getFrame());
		wnd.setAlwaysOnTop(true);
		wnd.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				wnd.setVisible(false);
				wnd.dispose();
			}
		});
		JLabel content = new JLabel(
				new ImageIcon(ColorMixerPanel.class.getResource("about.png")));
		content.setBorder(BorderFactory.createLineBorder(new Color(0x969189), 1));
		wnd.getContentPane().add(content);
		wnd.pack();
		wnd.setLocationRelativeTo(Application.getFrame());
		wnd.setVisible(true);
	}
	
	@ActionMethod(label="Pick Screen Color", icon="/com/pagosoft/colormixer/colorpicker.png")
	private void pickScreenColor() {
		/*
		 * Copyright by UICompiler-Project,
		 * see com.pagosoft.swing.ScreenShot for license.
		 */
        final ScreenShot screenshot;
        final Window w = SwingUtilities.getWindowAncestor(ColorMixerPanel.this);
        try {
            if(w instanceof Dialog)
                screenshot = new ScreenShot((Dialog) w);
            else if(w instanceof Frame)
                screenshot = new ScreenShot((Frame) w);
            else
                return;
        } catch(AWTException e) {
            JOptionPane.showMessageDialog(this, "Could not start color picker. This is a permanent error.");
            return;
        }
        w.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        new Thread("WaitForColorSelection") {
            public void run() {
                screenshot.init();
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        screenshot.setVisible(true);
                    }
                });
				EventBus.getInstance().fireEvent(new ColorChangedEvent(new BaseColor(screenshot.getColor())));
                screenshot.dispose();
                w.setCursor(null);
            }
        }.start();
	}
	
	@ActionMethod(label="Choose Color", icon="/com/pagosoft/colormixer/color.png")
	private void showColorChooser() {
		Color color = JColorChooser.showDialog(this, "Choose Color",
				Application.getFrame().getMixerPanel().getColors()[0].toColor());
		if(color != null) {
			EventBus.getInstance().fireEvent(new ColorChangedEvent(new BaseColor(color)));
		}
	}
	
	@ActionMethod(label="Triadic Mode")
	private void setTriadicCalculator() {
		Application.getInstance().setCalculator(new TriadicColorCalculator());
	}
	
	@ActionMethod(label="Analogous Mode")
	private void setAnalogousCalculator() {
		Application.getInstance().setCalculator(new AnalogousColorCalculator());
	}
	
	@ActionMethod(label="Complement Mode")
	private void setComplementCalculator() {
		Application.getInstance().setCalculator(new ComplementColorCalculator());
	}

	@ActionMethod(label="Split Complement Mode")
	private void setSplitComplementsCalculator() {
		Application.getInstance().setCalculator(new SplitComplementsColorCalculator());
	}
	
	private Icon getIcon(String name) {
		return new ImageIcon(ColorMixerPanel.class.getResource(name+".png"));
	}
	
	private abstract class AbstractColorButtonIcon implements Icon {
		private GradientPaint activeGradient;
		private GradientPaint inactiveGradient = new GradientPaint(0, 0, new Color(0xd2d2d2), 0, 15, new Color(0xa4a4a4));
		
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if(activeGradient == null) {
				activeGradient = createActiveGradient();
			}
			
			Graphics2D gfx = (Graphics2D)g;
			gfx.setPaint(activeGradient);
			gfx.fillRect(x, y, 15, 15);
			gfx.setColor(Color.BLACK);
			gfx.drawRect(x, y, 15, 15);
		}
		
		public int getIconWidth() {
			return 16;
		}
		
		public int getIconHeight() {
			return 16;
		}
		
		protected abstract GradientPaint createActiveGradient();
	}
	
	private class RedButtonIcon extends AbstractColorButtonIcon {
		protected GradientPaint createActiveGradient() {
			return new GradientPaint(0, 0, new Color(0xff4848), 0, 15, new Color(0xe20404));
		}
	}
	
	private class GreenButtonIcon extends AbstractColorButtonIcon {
		protected GradientPaint createActiveGradient() {
			return new GradientPaint(0, 0, new Color(0x80ff42), 0, 15, new Color(0x51f500));
		}
	}
	
	private class BlueButtonIcon extends AbstractColorButtonIcon {
		protected GradientPaint createActiveGradient() {
			return new GradientPaint(0, 0, new Color(0x367cfd), 0, 15, new Color(0x004edd));
		}
	}
}
