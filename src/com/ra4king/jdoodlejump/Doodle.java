package com.ra4king.jdoodlejump;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.gameworld.GameComponent;
import com.ra4king.gameutils.util.Score;
import com.ra4king.jdoodlejump.bars.Bar;
import com.ra4king.jdoodlejump.monsters.Monster;
import com.ra4king.jdoodlejump.powerups.PowerUp;

public class Doodle extends GameComponent {
	private Image doodle, doodleShooting;
	
	private Rectangle2D.Double feetBounds;
	private PowerUp powerup;
	private PowerUp powerup2;
	private Score score;
	
	private double scale = 1;
	private int intersectionDist = 1;
	private final double defaultMaxVY = 600;
	private double maxVY = defaultMaxVY;
	private double vy;
	private double g = 1000;
	
	private boolean facingRight;
	private boolean canShoot = true;
	private boolean isDying;
	private boolean isHit;
	private boolean isShooting;
	private boolean isInvincible;
	private boolean isInvisible;
	private boolean isUpdating;
	
	public Doodle(Score score, boolean isUpdating) {
		setWidth(30);
		setHeight(42);
		vy = maxVY;
		
		this.score = score;
		
		this.isUpdating = isUpdating;
		
		facingRight = true;
		
		feetBounds = new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
	}
	
	public double getMaxVelocityY() {
		return maxVY;
	}
	
	public void setMaxVelocityY(double dVY) {
		this.maxVY = dVY;
	}
	
	public void resetMaxVelocityY() {
		maxVY = defaultMaxVY;
	}
	
	public double getDefaultMaxVelocityY() {
		return defaultMaxVY;
	}
	
	public double getVelocityY() {
		return vy;
	}
	
	public void setVelocityY(double vy) {
		this.vy = vy;
	}
	
	public void setDying(boolean isDying) {
		this.isDying = isDying;
	}
	
	public boolean isDying() {
		return isDying;
	}
	
	public void setHit(boolean isHit) {
		this.isHit = isHit;
	}
	
	public boolean isHit() {
		return isHit;
	}
	
	public void setShooting(boolean shooting) {
		isShooting = shooting;
	}
	
	public boolean isShooting() {
		return isShooting;
	}
	
	public void setAbleToShoot(boolean shoot) {
		canShoot = shoot;
	}
	
	public boolean isAbleToShoot() {
		return canShoot && !isDying;
	}
	
	public void setInvincible(boolean inv) {
		isInvincible = inv;
	}
	
	public boolean isInvincible() {
		return isInvincible;
	}
	
	public void setInvisible(boolean invisible) {
		isInvisible = invisible;
	}
	
	public boolean isInvisible() {
		return isInvisible;
	}
	
	public void setFacingRight(boolean right) {
		facingRight = right;
	}
	
	public boolean isFacingRight() {
		return facingRight;
	}
	
	public void setIntersection(int dist) {
		intersectionDist = dist;
	}
	
	public int getIntersection() {
		return intersectionDist;
	}
	
	public void uninstallPowerUp(PowerUp p) {
		if(p.canAttachMultiplePowerups() && p == powerup2) {
			getParent().remove(p);
			powerup2 = null;
		}
		else if(p == powerup) {
			getParent().remove(p);
			powerup = null;
		}
		else
			throw new IllegalArgumentException();
	}
	
	public boolean isPowerUpInstalled() {
		return powerup != null;
	}
	
	public PowerUp getPowerUp() {
		return powerup;
	}
	
	@Override
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();
		bounds.setFrame(getX() + 5, getY() + 8, getWidth() - 10, getHeight() - 15);
		return bounds;
	}
	
	private Rectangle2D.Double getFeetBounds() {
		feetBounds.setFrame(getX() + 5, getY() + getHeight() - intersectionDist - 1, getWidth() - 10, intersectionDist);
		return feetBounds;
	}
	
	public double getScale() {
		return scale;
	}
	
	public void setScale(double scale) {
		this.scale = scale;
	}
	
	@Override
	public void show() {
		super.show();
		doodle = getParent().getGame().getArt().get("doodle");
		doodleShooting = getParent().getGame().getArt().get("doodle shooting");
	}
	
	@Override
	public void update(long deltaTime) {
		if(isUpdating) {
			if(powerup == null || powerup.isDoodleJumping()) {
				vy -= (g * (deltaTime / 1e9));
				
				if(vy < -maxVY)
					vy = -maxVY;
			}
			
			setY(getY() - (vy * (deltaTime / 1e9)));
		}
		
		if(!isDying) {
			if(isUpdating) {
				if(getScreenY() + getHeight() / 2 > getParent().getHeight()) {
					isDying = true;
					getParent().getGame().getSound().play("fall");
					return;
				}
			}
			
			boolean hasJumped = false;
			
			for(Entity e : getParent().getEntities()) {
				if(e == null || ((GameComponent)e).getScreenY() < 0)
					continue;
				
				if(!(e instanceof Doodle) && e.getBounds().intersects(getFeetBounds())) {
					if(e instanceof Bar && vy < 10) {
						((Bar)e).jump(this);
						
						if(((Bar)e).playsDefaultSound()) {
							if((powerup == null || !powerup.jump(this)) && (powerup2 == null || !powerup2.jump(this)))
								((Bar)e).playSound();
						}
						else
							((Bar)e).playSound();
						
						hasJumped = true;
					}
					else if(e instanceof Monster && ((Monster)e).isJumpable() && ((Monster)e).getJumpBounds().intersects(getFeetBounds())) {
						if(vy < maxVY + 300) {
							if(powerup == null || powerup.isDoodleJumping()) {
								vy = maxVY + 300;
								if(isUpdating) {
									setY(e.getY() - getHeight());
								}
								getParent().getGame().getSound().play("bounce");
							}
							
							((Monster)e).fall();
							((Monster)e).checkStopSound();
							
							score.add(200);
							
							hasJumped = true;
						}
						else if(isInvincible()) {
							getParent().remove(e);
							((Monster)e).checkStopSound();
							score.add(200);
							hasJumped = true;
						}
					}
				}
				
				if(!(e instanceof Doodle) && e != powerup && e != powerup2 && e.getBounds().intersects(getBounds())) {
					
					if(e instanceof PowerUp && (powerup == null || powerup2 == null)) {
						PowerUp p = (PowerUp)e;
						if(!p.canAttachMultiplePowerups() && powerup == null)
							powerup = p;
						else if(p.canAttachMultiplePowerups() && powerup2 == null)
							powerup2 = p;
						else
							continue;
						
						if(p.getBar() != null)
							p.getBar().uninstallPowerUp();
						p.setDoodle(this);
					}
					else if(e instanceof Monster && getBounds().intersects(((Monster)e).getHitBounds()) &&
							!hasJumped && !isInvincible) {
						if(!((Monster)e).kill(this)) {
							isDying = true;
							vy = 0;
							getParent().getGame().getSound().play("hitnfall");
							return;
						}
					}
				}
			}
		}
		else {
			if(getScreenY() - 50 > getParent().getHeight())
				isHit = true;
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(!isInvisible) {
			int xos = Themes.getThemes().getDoodleXOffset();
			int yos = Themes.getThemes().getDoodleYOffset();
			
			double scale = this.scale;
			
			if(scale != 1) {
				g.translate(getX() - xos + getWidth() / 2, getY() - yos + getHeight() / 2);
				g.scale(scale, scale);
				g.translate(-(getX() - xos + getWidth() / 2), -(getY() - yos + getHeight() / 2));
			}
			
			if(isShooting)
				g.drawImage(doodleShooting, getIntX(), (int)Math.round(getY() - 12), null);
			else if(facingRight)
				g.drawImage(doodle, getIntX() - xos, getIntY() - yos, null);
			else {
				g.scale(-1, 1);
				g.drawImage(doodle, -(getIntX() + getIntWidth() + xos), getIntY() - yos, null);
				g.scale(-1, 1);
			}
			
			if(isDying) {
				g.setColor(Color.yellow);
				g.fill(new Ellipse2D.Double(getX() - 5, getY() - 5, getWidth() + 10, 10));
			}
		}
		
		if(Themes.getThemes().isDebug()) {
			g.setColor(new Color(255, 0, 0, 200));
			g.fill(getBounds());
			
			g.setColor(new Color(0, 0, 255, 200));
			g.fill(getFeetBounds());
		}
	}
}