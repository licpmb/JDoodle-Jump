package com.ra4king.jdoodlejump.powerups;

import com.ra4king.jdoodlejump.Doodle;

public class RocketPowerUp extends PowerUp {
	private long time;
	
	public RocketPowerUp() {
		super("powerup2");
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
		
		setPowerUp("powerup2alt");
		
		doodle.setVelocityY(2000);
		
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
		
		if(doodle.isFacingRight())
			setX(doodle.getX() - getWidth());
		else
			setX(doodle.getX() + doodle.getWidth());
		
		setY(doodle.getY() + (doodle.getHeight() - getHeight()) / 2 + 10);
		
		time += deltaTime;
		if(time >= getParent().getGame().getSound().get("rocket").getMicrosecondLength() * 1000) {
			doodle.uninstallPowerUp(this);
			doodle.setInvincible(false);
			doodle.setAbleToShoot(true);
		}
	}
}