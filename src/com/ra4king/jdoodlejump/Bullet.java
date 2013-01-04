package com.ra4king.jdoodlejump;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.gameworld.GameComponent;
import com.ra4king.gameutils.util.FastMath;
import com.ra4king.gameutils.util.Score;
import com.ra4king.jdoodlejump.monsters.Monster;

public class Bullet extends GameComponent {
	private Image bullet;
	private Score score;
	private Point2D.Double doodle, mouse;
	private double vx, vy;
	
	public Bullet(Score score, Point2D.Double doodle, Point2D.Double mouse) {
		setWidth(15);
		setHeight(15);
		
		this.score = score;
		
		this.doodle = doodle;
		this.mouse = mouse;
		
		double x = mouse.x - doodle.x;
		double y = Math.abs(-mouse.y + doodle.y);
		double angle = FastMath.atan2(y, x);
		
		vx = 1500 * FastMath.cos(angle);
		vy = 1500 * FastMath.sin(angle);
		
		setX(doodle.x - (getWidth() / 2));
		setY(doodle.y);
	}
	
	public Point2D.Double getDoodlePoint() {
		return doodle;
	}
	
	public Point2D.Double getMousePoint() {
		return mouse;
	}
	
	@Override
	public void show() {
		super.show();
		bullet = getParent().getGame().getArt().get("bullet");
	}
	
	@Override
	public void update(long deltaTime) {
		if(getScreenY() + getHeight() < 0 || getScreenY() > getParent().getHeight()) {
			getParent().remove(this);
			return;
		}
		
		for(Entity e : getParent().getEntities()) {
			if(e == null)
				continue;
			
			if(e instanceof Monster && e.getBounds().intersects(getBounds()) && ((Monster)e).isHittable()) {
				((Monster)e).hit();
				
				getParent().remove(this);
				
				if(score != null)
					score.add(300);
				
				return;
			}
		}
		
		setX(getX() + (vx * (deltaTime / 1e9)));
		setY(getY() - (vy * (deltaTime / 1e9)));
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(bullet, (int)Math.round(getX()), (int)Math.round(getY()), null);
	}
}