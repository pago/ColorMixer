/*
 * MouseIconEvent.java
 *
 * Created on 23. Juni 2006, 12:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.pagosoft.swing;

import java.util.EventObject;
import javax.swing.text.JTextComponent;

/**
 *
 * @author pago
 */
public class MouseIconEvent extends EventObject {
	private int mouseButton = -1;
	private int clickCount = -1;
	
	/** Creates a new instance of MouseIconEvent */
	public MouseIconEvent(JTextComponent source) {
		super(source);
	}
	
	public MouseIconEvent(JTextComponent source, int mouseButton, int clickCount) {
		super(source);
		this.mouseButton = mouseButton;
		this.clickCount = clickCount;
	}

	public int getClickCount() {
		return clickCount;
	}

	public int getMouseButton() {
		return mouseButton;
	}
}
