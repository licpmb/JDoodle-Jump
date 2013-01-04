package com.ra4king.jdoodlejump.powerups;

import com.ra4king.jdoodlejump.Doodle;

public class BigRocketPowerUp extends PowerUp {
	private long time;
	
	public BigRocketPowerUp() {
		super("bigrocket");
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
		
		doodle.setVelocityY(3000);
		doodle.setInvisible(true);
		
		getParent().getGame().getSound().play("rocket");
	}
	
	@Override
	public void update(long deltaTime) {
		Doodle doodle = getDoodle();
		
		if(doodle == null) {
			super.update(deltaTime);
			return;
		}
		
		doodle.setAbleToShoot(false);
		doodle.setInvincible(true);
		
		setPowerUp(getDoodle().isFacingRight() ? "bigrocket-right" : "bigrocket-left");
		setX(doodle.getX() - (getWidth() - doodle.getWidth()) / 2);
		setY(doodle.getY() - 32);
		
		time += deltaTime;
		if(time >= getParent().getGame().getSound().get("rocket").getMicrosecondLength() * 1000) {
			doodle.uninstallPowerUp(this);
			doodle.setInvincible(false);
			doodle.setInvisible(false);
			doodle.setAbleToShoot(true);
		}
	}
}
