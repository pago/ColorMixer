/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Thomas Zander zander@kde.org  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "uic", "UICompiler", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package com.pagosoft.swing;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ScreenShot extends Dialog implements MouseListener, MouseMotionListener {
	Image background;
	Robot robot;
	int x=0, y=0, w, h, top=0;
	Color targetColor = null;
	public ScreenShot(Dialog parent) throws AWTException {
		super(parent);
		robot = new Robot(); // if not available it throws an AWTException
	}
	public ScreenShot(Frame parent) throws AWTException {
		super(parent);
		robot = new Robot(); // if not available it throws an AWTException
	}
	public void init() {
		Rectangle size = new Rectangle();
		GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (int x = 0; x < gs.length; x++) {
			GraphicsConfiguration[] gc = gs[x].getConfigurations();
			for (int i=0; i < gc.length; i++)
				size = size.union(gc[i].getBounds());
		}
		background = robot.createScreenCapture(size);
		setSize(size.width, size.height);
		setLocation(size.x, size.y);
		
		setUndecorated(true);
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(background, 0, 0, this);
		// where to position the box..
		if(top==0 && x <= 55 && y <= 55)
			top = 100;
		else if(top == 100 && x <= 55 && y >= 100 && y <= 155)
			top = 0;
		
		g2.setClip(0, top, 55, 55);
		g2.scale(5.0, 5.0);
		g2.drawImage(background, 5-x, 5-y + (top==0?0:20), this);
		g2.scale(0.2, 0.2);
		
		// draw lines.
		g2.setXORMode(Color.WHITE);
		g2.drawLine(0, top, 24, top + 25);
		g2.drawLine(55, top, 31, top + 25);
		g2.drawLine(0, top+55, 24, top + 31);
		g2.drawLine(55, top+55, 31, top + 31);
	}
	public Color getColor() {
		synchronized(this) {
			if(targetColor == null)
				try { wait(); } catch(InterruptedException e) { }
		}
		return targetColor;
	}
	public void mouseClicked(MouseEvent e) {
		targetColor = robot.getPixelColor(e.getX(), e.getY());
		synchronized(this) {
			notifyAll();
		}
	}
	public void mouseMoved(MouseEvent e) {
		x=e.getX();
		y=e.getY();
		repaint(0, top, 55, 55);
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
	}
	
	// these needed to allow mouseevents to be accepted.
	public boolean isFocusTraversable() {
		return true;
	}
	public boolean isFocusable() {
		return isFocusTraversable();
	}
}