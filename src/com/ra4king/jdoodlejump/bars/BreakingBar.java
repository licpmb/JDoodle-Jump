package com.ra4king.jdoodlejump.bars;

import java.awt.geom.Rectangle2D;

import com.ra4king.jdoodlejump.Doodle;

public class BreakingBar extends Bar {
	private boolean broken;
	
	public BreakingBar(double x, double y) {
		super("breakingbar");
		
		setX(x);
		setY(y);
	}
	
	@Override
	public void jump(Doodle doodle) {
		broken = true;
		setBar("brokenbar");
	}
	
	@Override
	public void playSound() {
		getParent().getGame().getSound().play("break");
	}
	
	@Override
	public boolean playsDefaultSound() {
		return false;
	}
	
	@Override
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();
		
		if(broken)
			bounds.setFrame(0, 0, 0, 0);
		
		return bounds;
	}
	
	@Override
	public void update(long deltaTime) {
		super.update(deltaTime);
		
		if(broken)
			setY(getY() + (420 * (deltaTime / 1e9)));
	}
}