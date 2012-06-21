package com.ra4king.jdoodlejump.monsters;


import java.awt.geom.Rectangle2D;

import com.ra4king.gameutils.Entity;
import com.ra4king.jdoodlejump.Doodle;


public class AlienMonster extends Monster {
	private Doodle doodle;
	private long time;
	
	private static boolean isLoopingSound;
	
	public AlienMonster(double x, double y) {
		super("alien",1);
		
		setX(x);
		setY(y);
	}
	
	@Override
	public void checkStopSound() {
		for(Entity e : getParent().getEntitiesAt(1))
			if(e != this && e instanceof AlienMonster)
				if(e.getY() > -100)
					return;
		
		getParent().getGame().getSound().stop("ufos");
		isLoopingSound = false;
	}
	
	@Override
	public Rectangle2D.Double getJumpBounds() {
		Rectangle2D.Double bounds = getBounds();
		bounds.setFrame(getX(),getY(),getWidth(),30);
		return bounds;
	}
	
	@Override
	public Rectangle2D.Double getHitBounds() {
		Rectangle2D.Double bounds = getBounds();
		bounds.setFrame(getX(),getY()+50,getWidth(),getHeight()-50);
		return bounds;
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
		getParent().remove(this);
		checkStopSound();
		getParent().getGame().getSound().play("ufodeath");
	}
	
	@Override
	public boolean kill(Doodle doodle) {
		this.doodle = doodle;
		doodle.setDying(true);
		getParent().getGame().getSound().play("ufoabduct");
		return true;
	}
	
	@Override
	public void update(long deltaTime) {
		if(getScreenY() > getParent().getHeight()) {
			getParent().remove(this);
			checkStopSound();
			return;
		}
		
		super.update(deltaTime);
		
		if(getScreenY() > -100 && !isLoopingSound) {
			isLoopingSound = true;
			getParent().getGame().getSound().loop("ufos");
		}
		
		if(doodle != null) {
			doodle.setVelocityY(0);
			doodle.setX(getX()+(getWidth()-doodle.getWidth())/2);
			doodle.setY(getY()+getHeight()-doodle.getHeight()-((time/2e7)));
			time += deltaTime;
			
			if(time >= 1e9)
				doodle.setHit(true);
		}
	}
}