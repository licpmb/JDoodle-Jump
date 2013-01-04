package com.ra4king.jdoodlejump.gui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import com.ra4king.gameutils.gui.Widget;

public class ToggleButton extends Widget {
	private int toggleX;
	
	public ToggleButton() {
		setBounds(50, 50, 200, 50);
	}
	
	public RoundRectangle2D.Double getButtonBounds() {
		return new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), 20, 20);
	}
	
	public RoundRectangle2D.Double getToggleBounds() {
		return new RoundRectangle2D.Double(getX() + toggleX, getY(), getWidth() / 2, getHeight(), 20, 20);
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.draw(getButtonBounds());
		
		g.fill(getToggleBounds());
	}
	
	private int clickX;
	
	@Override
	public void mousePressed(MouseEvent me) {
		if(me.getButton() == MouseEvent.BUTTON1 && getToggleBounds().contains(me.getPoint()))
			clickX = me.getX() - getIntX() - toggleX;
	}
	
	@Override
	public void mouseReleased(MouseEvent me) {
		clickX = -1;
		
		if(toggleX + getIntWidth() / 4 < getIntWidth() / 2)
			toggleX = 0;
		else
			toggleX = getIntWidth() - getIntWidth() / 2;
	}
	
	@Override
	public void mouseDragged(MouseEvent me) {
		if(clickX != -1) {
			toggleX = me.getX() - getIntX() - clickX;
			
			if(toggleX < 0)
				toggleX = 0;
			else if(toggleX > getIntWidth() - getIntWidth() / 2)
				toggleX = getIntWidth() - getIntWidth() / 2;
		}
	}
}
