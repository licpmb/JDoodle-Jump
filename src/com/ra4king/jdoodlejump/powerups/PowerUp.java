package com.ra4king.jdoodlejump.powerups;


import java.awt.Graphics2D;
import java.awt.Image;

import com.ra4king.gameutils.gameworld.GameComponent;
import com.ra4king.jdoodlejump.Doodle;
import com.ra4king.jdoodlejump.bars.Bar;


public abstract class PowerUp extends GameComponent {
	private Image powerupImage;
	private Bar bar;
	private Doodle doodle;
	private String powerup;
	
	public PowerUp(String powerup) {
		this.powerup = powerup;
	}
	
	public void setBar(Bar bar) {
		if(doodle != null) throw new IllegalArgumentException("Doodle is not null.");
		
		this.bar = bar;
	}
	
	public Bar getBar() {
		return bar;
	}
	
	public void setDoodle(Doodle doodle) {
		if(bar != null) throw new IllegalArgumentException("Bar is not null.");
		
		this.doodle = doodle;
	}
	
	public Doodle getDoodle() {
		return doodle;
	}
	
	public boolean jump(Doodle doodle) {
		return false;
	}
	
	public String getPowerUp() {
		return powerup;
	}
	
	public void setPowerUp(String image) {
		this.powerup = image;
		show();
	}
	
	public abstract boolean isDoodleJumping();
	
	public abstract boolean canAttachMultiplePowerups();
	
	@Override
	protected void finalize() {
		bar = null;
		doodle = null;
	}
	
	@Override
	public void show() {
		if(powerup != null) {
			powerupImage = getParent().getGame().getArt().get(powerup);
			setWidth(powerupImage.getWidth(null));
			setHeight(powerupImage.getHeight(null));
		}
	}
	
	@Override
	public void update(long deltaTime) {
		if(getScreenY() > getParent().getHeight())
			getParent().remove(this);
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(powerup != null)
			g.drawImage(powerupImage,getIntX(),getIntY(),null);
	}
}