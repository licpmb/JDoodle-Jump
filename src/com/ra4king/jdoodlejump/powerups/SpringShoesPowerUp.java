package com.ra4king.jdoodlejump.powerups;

import com.ra4king.jdoodlejump.Doodle;

public class SpringShoesPowerUp extends PowerUp {
	private int doodleHeight;
	private int doodleIntersectDist;
	private long time;
	
	public SpringShoesPowerUp() {
		super("powerup3");
	}
	
	@Override
	public boolean isDoodleJumping() {
		return true;
	}
	
	@Override
	public boolean canAttachMultiplePowerups() {
		return false;
	}
	
	@Override
	public void setDoodle(Doodle doodle) {
		super.setDoodle(doodle);
		
		setPowerUp("powerup3alt");
		
		setWidth(getParent().getGame().getArt().get("powerup3alt").getWidth(null));
		setHeight(getParent().getGame().getArt().get("powerup3alt").getHeight(null));
		
		doodleHeight = (int)doodle.getHeight();
		doodleIntersectDist = doodle.getIntersection();
		doodle.setMaxVelocityY(1200);
		doodle.setHeight(doodleHeight+getHeight()-6);
		doodle.setIntersection((int)getHeight());
	}
	
	@Override
	public boolean jump(Doodle doodle) {
		getParent().getGame().getSound().play("spring");
		
		return true;
	}
	
	@Override
	public void update(long deltaTime) {
		Doodle doodle = getDoodle();
		
		if(doodle == null) {
			super.update(deltaTime);
			return;
		}
		
		setX(doodle.getX() + 1);
		setY(doodle.getY() + doodleHeight - 6);
		
		time += deltaTime;
		if(time >= 10000000000L) {
			doodle.uninstallPowerUp(this);
			doodle.resetMaxVelocityY();
			doodle.setHeight(doodleHeight);
			doodle.setIntersection(doodleIntersectDist);
		}
	}
}