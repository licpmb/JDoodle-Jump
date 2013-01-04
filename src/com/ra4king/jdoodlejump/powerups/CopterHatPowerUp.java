package com.ra4king.jdoodlejump.powerups;

import java.awt.Graphics2D;

import com.ra4king.jdoodlejump.Doodle;

public class CopterHatPowerUp extends PowerUp {
	private long time;
	
	public CopterHatPowerUp() {
		super("powerup1");
	}
	
	@Override
	public boolean isDoodleJumping() {
		return false;
	}
	
	@Override
	public boolean canAttachMultiplePowerups() {
		return false;
	}
	
	@Override
	public void setDoodle(Doodle doodle) {
		super.setDoodle(doodle);
		
		setPowerUp("powerup1alt");
		
		doodle.setVelocityY(1020);
		
		getParent().getGame().getSound().play("copter");
	}
	
	@Override
	public void update(long deltaTime) {
		Doodle doodle = getDoodle();
		
		if(doodle == null) {
			super.update(deltaTime);
			return;
		}
		
		doodle.setInvincible(true);
		doodle.setAbleToShoot(false);
		
		setX(doodle.getX() + (doodle.getWidth() - getWidth()) / 2);
		setY(doodle.getY() - getHeight() + 10);
		
		time += deltaTime;
		if(time >= getParent().getGame().getSound().get("copter").getMicrosecondLength() * 1000) {
			doodle.uninstallPowerUp(this);
			doodle.setInvincible(false);
			doodle.setAbleToShoot(true);
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(getDoodle() != null && System.currentTimeMillis() / 10 % 2 == 0) {
			if(getPowerUp().equals("powerup1"))
				setPowerUp("powerup1alt");
			else
				setPowerUp("powerup1");
		}
		
		super.draw(g);
	}
}