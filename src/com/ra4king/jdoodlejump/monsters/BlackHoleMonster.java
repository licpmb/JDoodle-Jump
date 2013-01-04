package com.ra4king.jdoodlejump.monsters;

import java.awt.geom.Rectangle2D;

import com.ra4king.jdoodlejump.Doodle;

public class BlackHoleMonster extends Monster {
	private Doodle doodle;
	private long time;
	
	public BlackHoleMonster(double x, double y) {
		super("blackhole", 1);
		
		setX(x);
		setY(y);
	}
	
	@Override
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();
		bounds.setFrame(getX() + 15, getY() + 15, getWidth() - 30, getHeight() - 30);
		return bounds;
	}
	
	@Override
	public Rectangle2D.Double getHitBounds() {
		return getBounds();
	}
	
	@Override
	public boolean isJumpable() {
		return false;
	}
	
	@Override
	public boolean isHittable() {
		return false;
	}
	
	@Override
	public void hit() {}
	
	@Override
	public boolean kill(Doodle doodle) {
		if(this.doodle == null) {
			this.doodle = doodle;
			getParent().getGame().getSound().play("blackhole");
		}
		return true;
	}
	
	@Override
	public void update(long deltaTime) {
		super.update(deltaTime);
		
		if(doodle != null) {
			doodle.setVelocityY(0);
			doodle.setX(getX() + (getWidth() - doodle.getWidth()) / 2);
			doodle.setY(getY() + (getHeight() - doodle.getHeight()) / 2);
			doodle.setScale(doodle.getScale() * 0.99);
			
			time += deltaTime;
			
			if(time >= 1e9)
				doodle.setHit(true);
		}
	}
}