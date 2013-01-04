package com.ra4king.jdoodlejump.monsters;

import java.awt.geom.Rectangle2D;

import com.ra4king.gameutils.util.FastMath;
import com.ra4king.jdoodlejump.Doodle;
import com.ra4king.jdoodlejump.bars.Bar;

public class StationaryMonster extends Monster {
	private Bar bar;
	private int count = 90;
	private long lastTime;
	
	public StationaryMonster(int num, int hitsTotal) {
		super("monster" + num, hitsTotal);
	}
	
	@Override
	public void show() {
		super.show();
		
		lastTime = System.nanoTime();
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
	public void fall() {
		if(getBar() != null)
			getBar().uninstallMonster();
		super.fall();
	}
	
	@Override
	public boolean kill(Doodle doodle) {
		return false;
	}
	
	public void setBar(Bar bar) {
		this.bar = bar;
	}
	
	public Bar getBar() {
		return bar;
	}
	
	@Override
	protected void finalize() {
		bar = null;
	}
	
	@Override
	public void update(long deltaTime) {
		super.update(deltaTime);
		
		long diff;
		if((diff = System.nanoTime() - lastTime) >= 1e9 / 60) {
			lastTime += diff;
			
			count += (Math.random() + 1) * 500.0 / 60.0;
			
			super.setX(getX() + FastMath.cosDeg(count));
			
			super.setY(getY() + FastMath.sinDeg(count / .7) / 5);
		}
	}
}