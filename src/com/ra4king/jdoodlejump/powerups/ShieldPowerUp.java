package com.ra4king.jdoodlejump.powerups;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import com.ra4king.jdoodlejump.Doodle;

public class ShieldPowerUp extends PowerUp {
	private Ellipse2D.Double shield;
	private long time;
	
	public ShieldPowerUp() {
		super(null);
		
		setWidth(50);
		setHeight(50);
	}
	
	private Ellipse2D.Double getShield() {
		if(shield == null)
			shield = new Ellipse2D.Double();
		
		shield.setFrame(getX(), getY(), getWidth(), getHeight());
		
		return shield;
	}
	
	@Override
	public boolean isDoodleJumping() {
		return true;
	}
	
	@Override
	public boolean canAttachMultiplePowerups() {
		return true;
	}
	
	@Override
	public void setDoodle(Doodle doodle) {
		super.setDoodle(doodle);
		
		setWidth(100);
		setHeight(100);
	}
	
	@Override
	public void update(long deltaTime) {
		Doodle doodle = getDoodle();
		
		if(doodle == null) {
			super.update(deltaTime);
			return;
		}
		
		doodle.setInvincible(true);
		
		if(doodle.isFacingRight())
			setX(doodle.getX() - ((getWidth() / 2) - 20));
		else
			setX(doodle.getX() - ((getWidth() / 2) - 10));
		
		setY(doodle.getY() - ((getHeight() / 2) - 21));
		
		time += deltaTime;
		if(time >= 15000000000L) {
			doodle.uninstallPowerUp(this);
			doodle.setInvincible(false);
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(getX() + getHeight() < 0)
			return;
		
		if(time > 12000000000L && System.currentTimeMillis() / 10 % 2 == 0)
			return;
		else {
			g.setPaint(new GradientPaint((float)(getX() + getWidth() / 2), (float)getY(), new Color(255, 255, 255, 100), (float)(getX() + getWidth() / 2), (float)(getY() + getHeight()), new Color(0, 0, 255, 100)));
			Ellipse2D.Double e = getShield();
			g.fill(e);
			g.setColor(Color.lightGray);
			g.draw(e);
		}
	}
}