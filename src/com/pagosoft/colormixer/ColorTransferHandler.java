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

import com.pagosoft.eventbus.EventBus;
import com.pagosoft.swing.BaseColor;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Patrick
 */
public class ColorTransferHandler extends TransferHandler {
	//The data type exported from JColorChooser.
	String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
			";class=java.awt.Color";
	public static DataFlavor colorFlavor;
	private boolean changesForegroundColor = true;
	
	public ColorTransferHandler() {
		//Try to create a DataFlavor for color.
		try {
			colorFlavor = new DataFlavor(mimeType);
		} catch (ClassNotFoundException e) { }
	}
	
	/**
	 * Overridden to import a Color if it is available.
	 * getChangesForegroundColor is used to determine whether
	 * the foreground or the background color is changed.
	 */
	public boolean importData(JComponent c, Transferable t) {
		if (hasColorFlavor(t.getTransferDataFlavors())) {
			try {
				Color col = (Color)t.getTransferData(colorFlavor);
				return importColor(c, col);
			} catch (UnsupportedFlavorException ufe) {
			} catch (IOException ioe) { }
		} else if(t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				String data = (String)t.getTransferData(DataFlavor.stringFlavor);
				Color col = ColorUtils.toColor(data);
				return importColor(c, col);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}
	
	private boolean importColor(JComponent c, Color col) {
		if(c instanceof JList && ((JList)c).getModel() instanceof DefaultListModel) {
			DefaultListModel dlm = (DefaultListModel)((JList)c).getModel();
			dlm.addElement(col);
			return true;
		} else if(c instanceof JTextComponent) {
			((JTextComponent)c).setText(ColorUtils.toHexString(col));
			EventBus.getInstance().fireEvent(new ColorChangedEvent(col));
			return true;
		} else if(c instanceof ColorDetailsPanel) {
			EventBus.getInstance().fireEvent(new ColorChangedEvent(col));
		}
		return false;
	}
	
	/**
	 * Does the flavor list have a Color flavor?
	 */
	protected boolean hasColorFlavor(DataFlavor[] flavors) {
		if (colorFlavor == null) {
			return false;
		}
		
		for (int i = 0; i < flavors.length; i++) {
			if (colorFlavor.equals(flavors[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Overridden to include a check for a color flavor.
	 */
	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		if (colorFlavor == null) {
			return false;
		}
		
		for (int i = 0; i < flavors.length; i++) {
			if (DataFlavor.stringFlavor.equals(flavors[i]) || colorFlavor.equals(flavors[i])) {
				return true;
			}
		}
		return false;
	}
	
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}
	
	protected Transferable createTransferable(JComponent c) {
		if(c instanceof JList) {
			return new ColorTransferable(((BaseColor)((JList)c).getSelectedValue()).toColor());
		} else if(c instanceof JTextComponent) {
			return new ColorTransferable(
					ColorUtils.toColor(((JTextComponent)c).getText()));
		} else if(c instanceof ColorDetailsPanel) {
			return new ColorTransferable(((ColorDetailsPanel)c).getColor().toColor());
		}
		return null;
	}
	
	private static class ColorTransferable implements Transferable {
		private Color color;
		private String colorAsHex;
		
		public ColorTransferable(Color color) {
			this.color = color;
			this.colorAsHex = ColorUtils.toHexString(color);
		}
		
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.stringFlavor, colorFlavor };
		}
		
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.stringFlavor.equals(flavor) || colorFlavor.equals(flavor);
		}
		
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if(flavor.equals(colorFlavor)) {
				return color;
			} else {
				return colorAsHex;
			}
		}
	}
}
