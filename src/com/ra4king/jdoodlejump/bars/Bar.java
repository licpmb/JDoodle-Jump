package com.ra4king.jdoodlejump.bars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import com.ra4king.gameutils.gameworld.GameComponent;
import com.ra4king.gameutils.gameworld.GameWorld;
import com.ra4king.jdoodlejump.Doodle;
import com.ra4king.jdoodlejump.monsters.StationaryMonster;
import com.ra4king.jdoodlejump.powerups.PowerUp;

public abstract class Bar extends GameComponent {
	private Image barImage;
	private String bar;
	private StationaryMonster monster;
	private PowerUp powerup;
	private Color color = Color.green;
	
	public Bar(String b) {
		this.bar = b;
	}
	
	@Override
	public void init(GameWorld world) {
		super.init(world);
		show();
	}
	
	@Override
	public void show() {
		barImage = getParent().getGame().getArt().get(bar);
		setWidth(barImage.getWidth(null));
		setHeight(barImage.getHeight(null));
	}
	
	public void installMonster(StationaryMonster m) {
		if(powerup != null) throw new IllegalArgumentException("PowerUp is already installed.");
		
		m.setX(getX()+(getWidth()-m.getWidth())/2);
		m.setY(getY()-m.getHeight()-5);
		m.setBar(this);
		
		monster = m;
	}
	
	public boolean isMonsterInstalled() {
		return monster == null ? false : true;
	}
	
	public void uninstallMonster() {
		monster.setBar(null);
		monster = null;
	}
	
	public void installPowerUp(PowerUp p) {
		if(monster != null) throw new IllegalArgumentException("Monster is already installed.");
		
		p.setX(getX()+(getWidth()-p.getWidth())/2);
		p.setY(getY()-p.getHeight()-5);
		p.setBar(this);
		
		powerup = p;
	}
	
	public boolean isPowerUpInstalled() {
		return powerup == null ? false : true;
	}
	
	public void uninstallPowerUp() {
		powerup.setBar(null);
		powerup = null;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	protected void finalize() {
		monster.setBar(null);
		monster = null;
		powerup.setBar(null);
		powerup = null;
	}
	
	public void jump(Doodle doodle) {
		doodle.setVelocityY(doodle.getMaxVelocityY());
		doodle.setY(getY()-doodle.getHeight());
	}
	
	public void playSound() {
		getParent().getGame().getSound().play("jump");
	}
	
	public boolean playsDefaultSound() {
		return true;
	}
	
	public void setBar(String bar) {
		this.bar = bar;
		show();
	}
	
	@Override
	public void update(long deltaTime) {
		if(getScreenY() > getParent().getHeight())
			getParent().remove(this);
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(barImage, getIntX(), getIntY(), null);
	}
}