package com.ra4king.jdoodlejump.monsters;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.ra4king.gameutils.util.FastMath;
import com.ra4king.jdoodlejump.Doodle;

public class MovingMonster extends Monster {
	private double vx = 100;
	private boolean hasUpdated = false;
	private int count;
	private long lastTime;
	
	public MovingMonster(int num, double x, double y, int hitsTotal) {
		super("monster" + num, hitsTotal);
		
		setX(x);
		setY(y);
	}
	
	@Override
	public void show() {
		super.show();
		
		lastTime = System.nanoTime();
	}
	
	@Override
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();
		
		if(!hasUpdated)
			bounds.setFrame(0, getY(), getParent().getWidth(), getHeight());
		
		return bounds;
	}
	
	@Override
	public Rectangle2D.Double getHitBounds() {
		return getBounds();
	}
	
	@Override
	public boolean isJumpable() {
		return true;
	}
	
	@Override
	public boolean isHittable() {
		return true;
	}
	
	@Override
	public void hit() {
		addHit();
		
		setHit(true);
		
		if(getHitsCount() >= getHitsTotal()) {
			getParent().remove(this);
			checkStopSound();
			getParent().getGame().getSound().play("monsterdeath");
		}
	}
	
	@Override
	public boolean kill(Doodle doodle) {
		return false;
	}
	
	@Override
	public void update(long deltaTime) {
		super.update(deltaTime);
		
		if(!hasUpdated)
			hasUpdated = true;
		
		setX(getX() + vx * (deltaTime / 1000000000.0));
		
		if(getX() >= getParent().getWidth() - getWidth())
			vx = -Math.abs(vx);
		else if(getX() <= 0)
			vx = Math.abs(vx);
		
		long diff;
		if((diff = System.nanoTime() - lastTime) >= 1e9 / 60) {
			lastTime += diff;
			
			count += (Math.random() + 1) * 500.0 / 60.0;
			
			super.setY(getY() + FastMath.sinDeg(count));
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(vx < 0) {
			double x = getX();
			setX(-(x + getWidth()));
			g.scale(-1, 1);
			super.draw(g);
			setX(x);
		}
		else
			super.draw(g);
	}
}