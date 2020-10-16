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
package com.pagosoft.swing;

import java.io.File;
import javax.swing.filechooser.FileFilter;


public class SimpleFileFilter extends FileFilter {
	private String description;
	private String extension;
	
	public SimpleFileFilter(String d, String e) {
		this.description = d;
		this.extension = e;
	}
	
	public boolean accept(File f) {
		return f.getName().toLowerCase().endsWith(extension);
	}
	
	public String getDescription() {
		return description;
	}
}