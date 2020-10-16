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

import com.pagosoft.action.*;
import com.pagosoft.swing.BaseColor;
import com.pagosoft.swing.TitledPanel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.swing.*;

/**
 *
 * @author Patrick
 */
public class ColorPalette extends JPanel {
	private JList palette;
	/** Creates a new instance of ColorPalette */
	public ColorPalette() {
		super(new BorderLayout());
		
		palette = new JList(new UniqueListModel());
		new DropTarget(palette, new DropTargetListener() {
			public void dragEnter(DropTargetDragEvent dtde) {
			}

			public void dragOver(DropTargetDragEvent dtde) {
			}

			public void dropActionChanged(DropTargetDragEvent dtde) {
			}

			public void dragExit(DropTargetEvent dte) {
			}

			public void drop(DropTargetDropEvent dtde) {
				Point p = dtde.getLocation();
				int idx = palette.locationToIndex(p);
				DefaultListModel model = (DefaultListModel)palette.getModel();
				boolean success = true;
				if(idx == -1) {
					try {
						model.addElement(new BaseColor((Color)dtde.getTransferable().getTransferData(ColorTransferHandler.colorFlavor)));
					} catch (Exception ex) {
						ex.printStackTrace();
						success = false;
					}
				} else {
					try {
						model.add(idx, new BaseColor((Color)dtde.getTransferable().getTransferData(ColorTransferHandler.colorFlavor)));
					} catch (Exception ex) {
						ex.printStackTrace();
						success = false;
					}
				}
				dtde.dropComplete(success);
			}
		});
		palette.setTransferHandler(new ColorTransferHandler());
		palette.setDragEnabled(true);
		palette.setCellRenderer(new ColorRenderer());
		palette.setBorder(null);
		
		JScrollPane scrollPane = new JScrollPane(palette);
		scrollPane.setBorder(null);
		
		TitledPanel panel;
		add(panel=new TitledPanel("Palette", scrollPane));
		panel.getLabel().putClientProperty("drawUpperBorder", Boolean.TRUE);
		
		Map<String,Action> actionMap = ActionFactory.createActionMap(this);
		
		palette.getActionMap().put("removeColor", actionMap.get("removeColor"));
		palette.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "removeColor");
		
		JToolBar jtb = new ContainerBuilder()
			.container("main")
				.add(actionMap.get("clearColors"))
				.addSeparator()
				.add(actionMap.get("addAllColors"))
				.add(actionMap.get("addColor"))
				.add(actionMap.get("removeColor"))
				.addSeparator()
				.add(actionMap.get("importPalette"))
				.add(actionMap.get("exportPalette"))
			.getContainer().createToolBar(new ToolBarButtonProvider());
		jtb.setFloatable(false);
		
		add(jtb, BorderLayout.PAGE_END);
	}
	
	private class UniqueListModel extends DefaultListModel {
		public void addElement(Object obj) {
			for(int i = 0; i < size(); i++) {
				if(get(i).equals(obj)) {
					remove(i);
					break;
				}
			}
			super.addElement(obj);
		}

		public void add(int index, Object element) {
			for(int i = 0; i < size(); i++) {
				if(get(i).equals(element)) {
					remove(i);
					break;
				}
			}
			super.add(index, element);
		}
	}
	
	@ActionMethod(id="addColor", icon="/com/pagosoft/colormixer/list-add.png", label="Add Color")
	public void addColor() {
		JColorChooser jcc = new JColorChooser();
		BaseColor c = new BaseColor(jcc.showDialog(this, "Choose Color", Color.BLUE));
		if(c != null) {
			((DefaultListModel)palette.getModel()).addElement(c);
		}
	}
	
	@ActionMethod(id="removeColor", icon="/com/pagosoft/colormixer/list-remove.png", label="Remove Color")
	public void removeColor() {
		((DefaultListModel)palette.getModel()).remove(palette.getSelectedIndex());
	}
	
	@ActionMethod(id="addAllColors", icon="/com/pagosoft/colormixer/format-indent-less.png", label="Add all colors")
	public void addAllColors() {
		BaseColor[] colors = Application.getFrame().getMixerPanel().getColors();
		DefaultListModel model = (DefaultListModel)palette.getModel();
		for(BaseColor c : colors) {
			model.addElement(c);
		}
	}
	
	@ActionMethod(id="clearColors", icon="/com/pagosoft/colormixer/document-new.png", label="New Palette")
	public void clearColors() {
		((DefaultListModel)palette.getModel()).removeAllElements();
	}
	
	@ActionMethod(icon="/com/pagosoft/colormixer/document-save-as.png", label="Export Palette")
	public void exportPalette() {
		JFileChooser jfc = Application.getFileChooser();
		jfc.setMultiSelectionEnabled(false);
		jfc.resetChoosableFileFilters();
		jfc.addChoosableFileFilter(Application.acoFilter);
		jfc.addChoosableFileFilter(Application.cmpFilter);
		int result = jfc.showSaveDialog(Application.getFrame());
		if(result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			String fileName = selectedFile.getName().toLowerCase();
			ColorExport export = null;
			if(fileName.endsWith(".aco")) {
				export = new ACOExport();
			} else {
				export = new SimpleExport();
			}
			OutputStream os = null;
			try {
				os = new FileOutputStream(selectedFile);
				export.write(os, Application.getFrame().getPalette().getColors());
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(Application.getFrame(), "Error: Could not export colors.");
			} finally {
				try {
					os.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	@ActionMethod(icon="/com/pagosoft/colormixer/document-open.png", label="Import Palette")
	public void importPalette() {
		JFileChooser jfc = Application.getFileChooser();
		jfc.setMultiSelectionEnabled(false);
		jfc.resetChoosableFileFilters();
		jfc.addChoosableFileFilter(Application.cmpFilter);
		int result = jfc.showOpenDialog(Application.getFrame());
		if(result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			String fileName = selectedFile.getName().toLowerCase();
			ColorImport imp = new SimpleImport();
			InputStream is = null;
			try {
				is = new FileInputStream(selectedFile);
				BaseColor[] colors = imp.read(is);
				DefaultListModel model = (DefaultListModel)palette.getModel();
				for(BaseColor c : colors) {
					model.addElement(c);
				}
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(Application.getFrame(), "Error: Could not export colors.");
			} finally {
				try {
					is.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public BaseColor[] getColors() {
		ListModel model = palette.getModel();
		BaseColor[] colors = new BaseColor[model.getSize()];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = (BaseColor)model.getElementAt(i);
		}
		return colors;
	}
	
	private static class ColorRenderer extends DefaultListCellRenderer {
		private ColorIcon ico;
		public ColorRenderer() {
			ico = new ColorIcon();
		}
		
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			BaseColor c = (BaseColor)value;
			
			ico.setColor(c);
			setIcon(ico);
			setText(c.toHexString());
			
			return this;
		}
	}
	
	private static class ColorIcon implements Icon {
		private Color color;
		
		public void setColor(BaseColor color) {
			this.color = color.toColor();
		}
		
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(color);
			g.fillRect(x, y, getIconWidth(), getIconHeight());
			g.setColor(Color.BLACK);
			g.drawRect(x, y, getIconWidth()-1, getIconHeight()-1);
		}
		
		public int getIconWidth() {
			return 25;
		}
		
		public int getIconHeight() {
			return getIconWidth();
		}
	}
}
