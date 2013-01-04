package com.ra4king.jdoodlejump.monsters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.gameworld.GameComponent;
import com.ra4king.jdoodlejump.Doodle;
import com.ra4king.jdoodlejump.Themes;

public abstract class Monster extends GameComponent {
	private Image monsterImage;
	private String monster;
	private int hitsCount;
	private final int hitsTotal;
	private boolean hit;
	private int count;
	private int isFalling = -1;
	
	public static boolean isLoopingSound;
	
	public Monster(String image, int hitsTotal) {
		monster = image;
		
		this.hitsTotal = hitsTotal;
	}
	
	public void checkStopSound() {
		for(Entity e : getParent().getEntitiesAt(1))
			if(e != this && e instanceof Monster && !(e instanceof AlienMonster || e instanceof BlackHoleMonster))
				if(((GameComponent)e).getScreenY() > -100)
					return;
		
		getParent().getGame().getSound().stop("monsters");
		isLoopingSound = false;
	}
	
	@Override
	public void show() {
		super.show();
		monsterImage = getParent().getGame().getArt().get(monster);
		setWidth(monsterImage.getWidth(null));
		setHeight(monsterImage.getHeight(null));
	}
	
	public abstract boolean isJumpable();
	
	public abstract boolean isHittable();
	
	public abstract void hit();
	
	public void fall() {
		if(isJumpable())
			isFalling = 0;
	}
	
	public abstract boolean kill(Doodle doodle);
	
	public void setHit(boolean isHit) {
		hit = isHit;
	}
	
	@Override
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();
		bounds.setFrame(bounds.x + 5, bounds.y + 5, bounds.width - 10, bounds.height - 10);
		return bounds;
	}
	
	public Rectangle2D.Double getJumpBounds() {
		return getBounds();
	}
	
	public Rectangle2D.Double getHitBounds() {
		return getBounds();
	}
	
	public int getHitsTotal() {
		return hitsTotal;
	}
	
	public int getHitsCount() {
		return hitsCount;
	}
	
	public void addHit() {
		hitsCount++;
	}
	
	@Override
	public void update(long deltaTime) {
		if(getScreenY() > getParent().getHeight()) {
			getParent().remove(this);
			checkStopSound();
			return;
		}
		
		if(isFalling > -1) {
			setY(getY() + (600 * (deltaTime / 1e9)));
			isFalling += (600 * (deltaTime / 1e9));
			
			if(isFalling >= 80) {
				isFalling = -1;
			}
		}
		
		if(getScreenY() > -100 && !isLoopingSound && !(this instanceof AlienMonster || this instanceof BlackHoleMonster)) {
			isLoopingSound = true;
			getParent().getGame().getSound().loop("monsters");
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(!hit)
			g.drawImage(monsterImage, getIntX(), getIntY(), null);
		else {
			g.drawImage(monsterImage, getIntX(), getIntY(), Color.red, null);
			count++;
			if(count > 5) {
				count = 0;
				hit = false;
			}
		}
		
		if(Themes.getThemes().isDebug()) {
			g.setColor(new Color(255, 0, 0, 100));
			g.fill(getBounds());
		}
	}
}