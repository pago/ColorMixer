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
package com.pagosoft.layout;

import java.awt.*;

/**
 * <p>The FillBoxLayout is a custom version of the BoxLayout shipped with AWT.
 * Anyway, it is not a replacement.</p>
 * <p>FillBoxLayout aligns it's components in a box (just like BoxLayout), but
 * fills all available space in one direction (specified by the first parameter
 * in the constructor).</p>
 * <p>It also supports gap between the components.</p>
 * <p>The real tricky part is, how the box is arranged. One might think a
 * FillBoxLayout with fill set to VERTICAL is aligned vertical, but that's wrong.</p>
 * <p>Use FillBoxLayout.VERTICAL to create a <strong>horizontal</strong> box
 * where all components fill the space <strong>vertical</strong>, and otherwise.</p>
 */
public class FillBoxLayout implements LayoutManager {
	protected int fill;
	protected int gap;

	/**
	 * Span the components to fill the horziontal space while using it's preferred height.
	 */
	public final static int HORIZONTAL = 1;
	/**
	 * Span the components to fill the vertical space while using it's preferred width.
	 */
	public final static int VERTICAL = 2;

	public FillBoxLayout(int fill, int gap) {
		if(fill < HORIZONTAL || fill > VERTICAL) {
			throw new IllegalArgumentException("fill must be one of BOTH, HORIZONTAL or VERTICAL");
		}
		this.fill = fill;
		this.gap = gap;
	}

	public FillBoxLayout(int fill) {
		this(fill, 0);
	}

	public FillBoxLayout() {
		this(VERTICAL, 0);
	}
	/* Required by LayoutManager. */
    public void addLayoutComponent(String name, Component comp) {
    }

    /* Required by LayoutManager. */
    public void removeLayoutComponent(Component comp) {
    }

	protected Dimension size;
	public Dimension preferredLayoutSize(Container parent) {
		int limit = parent.getComponentCount();
		Insets i = parent.getInsets();
		int width = i.left + i.right;
		int height = i.top + i.bottom;
		Component child;
		switch(fill) {
			case VERTICAL:
				for(int index = 0; index < limit; index++) {
					child = parent.getComponent(index);
					if(!child.isVisible()) {
						continue;
					}
					size = child.getPreferredSize();
					height = Math.max(height, size.height + i.top + i.bottom);
					width += gap + size.width;
				}
				break;
			case HORIZONTAL:
				for(int index = 0; index < limit; index++) {
					child = parent.getComponent(index);
					if(!child.isVisible()) {
						continue;
					}
					size = child.getPreferredSize();
					width = Math.max(width, size.width + i.left + i.right);
					height += gap + size.height;
				}
				break;
		}
		size = new Dimension(width, height);
		return size;
	}

	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	public void layoutContainer(Container parent) {
		Insets i = parent.getInsets();
		int x = i.left;
		int y = i.top;
		int width, height;
		int limit = parent.getComponentCount();
		int maxWidth = parent.getWidth() - i.left - i.right;
		int maxHeight = parent.getHeight() - i.top - i.bottom;
		Component child;
		Dimension childSize;
		for(int index = 0; index < limit; index++) {
			child = parent.getComponent(index);
			if(!child.isVisible()) {
				continue;
			}
			childSize = child.getPreferredSize();
			if(fill == VERTICAL) {
				height = maxHeight;
				width = childSize.width;
			} else {
				height = childSize.height;
				width = maxWidth;
			}
			child.setBounds(x, y, width, height);
			if(fill == VERTICAL) {
				x += gap + width;
			} else {
				y += gap + height;
			}
		}
	}
}
