package com.ra4king.jdoodlejump;

import java.awt.Image;
import java.awt.geom.Rectangle2D;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.gameworld.GameWorld;
import com.ra4king.jdoodlejump.bars.Bar;
import com.ra4king.jdoodlejump.bars.BreakingBar;
import com.ra4king.jdoodlejump.bars.DisappearingBar;
import com.ra4king.jdoodlejump.bars.MovingBar;
import com.ra4king.jdoodlejump.bars.StationaryBar;
import com.ra4king.jdoodlejump.monsters.AlienMonster;
import com.ra4king.jdoodlejump.monsters.BlackHoleMonster;
import com.ra4king.jdoodlejump.monsters.MovingMonster;
import com.ra4king.jdoodlejump.monsters.StationaryMonster;
import com.ra4king.jdoodlejump.powerups.BigRocketPowerUp;
import com.ra4king.jdoodlejump.powerups.CopterHatPowerUp;
import com.ra4king.jdoodlejump.powerups.PowerUp;
import com.ra4king.jdoodlejump.powerups.RocketPowerUp;
import com.ra4king.jdoodlejump.powerups.ShieldPowerUp;
import com.ra4king.jdoodlejump.powerups.SpringShoesPowerUp;

public class PlatformGenerator {
	private GameWorld gameWorld;
	private int monsterNum = 6;
	private int powerupNum = 4;
	private double highestBar;
	
	public PlatformGenerator(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}
	
	public double getHighestBar() {
		return highestBar;
	}
	
	public void addToHighestBar(double value) {
		highestBar += value;
	}
	
	public void setHighestBar(double highestBar) {
		this.highestBar = highestBar;
	}
	
	public void reset() {
		highestBar = 0;
	}
	
	public void loadWorld(Doodle doodle, int level, int extent) {
		double prevHighestBar;
		if(highestBar == 0) {
			gameWorld.add(new StationaryBar(gameWorld.getWidth() / 2 - 25, gameWorld.getHeight() - 20));
			highestBar = gameWorld.getHeight() - 20;
			prevHighestBar = gameWorld.getHeight() - 20;
		}
		else
			prevHighestBar = highestBar;
		
		// gameWorld.add(new BlackHoleMonster(50,-100));
		
		long time = System.currentTimeMillis();
		int bar = 0;
		
		for(int a = 0; highestBar > prevHighestBar - extent; bar++) {
			double x, y;
			
			int random = (int)Math.round(Math.random() * 100);
			
			if(level > 0 && a < 2 && random > 90 && doodle.getVelocityY() <= doodle.getDefaultMaxVelocityY()) {
				PowerUp powerup = null;
				
				switch((int)(Math.random() * powerupNum)) {
					case 0:
						powerup = new CopterHatPowerUp();
						break;
					case 1:
						powerup = new RocketPowerUp();
						break;
					case 2:
						powerup = Themes.getThemes().getCurrentTheme() == 5 ? new BigRocketPowerUp() : new SpringShoesPowerUp();
						break;
					case 3:
						powerup = new ShieldPowerUp();
				}
				
				x = Math.random() * (gameWorld.getWidth() - 50);
				y = highestBar - (Math.random() * (level * 3) + 50);
				
				gameWorld.add(4, powerup);
				((Bar)gameWorld.add(new StationaryBar(x, y))).installPowerUp(powerup);
				
				y -= powerup.getHeight();
				
				a++;
			}
			else if((level > 3 && random > 30 && random < 50) || (level > 0 && level % 7 == 0 && level % 8 == 0 && random > 50)) {
				x = (Math.random() * (gameWorld.getWidth() - 50));
				y = highestBar - (Math.random() * (level * 3) + 50);
				
				gameWorld.add(new MovingBar(x, y, x, true));
			}
			else {
				x = Math.random() * (gameWorld.getWidth() - 50);
				y = highestBar - (Math.random() * (level * 3) + 50);
				
				double random2 = Math.random() * 100;
				if(random2 > 80 && level > 2)
					gameWorld.add(new DisappearingBar(x, y));
				else
					gameWorld.add(new StationaryBar(x, y));
			}
			
			highestBar = y;
		}
		
		long dif = System.currentTimeMillis() - time;
		System.out.println((bar--) + " linear bars\t" + dif + " milliseconds");
		
		time = System.currentTimeMillis();
		
		double length = prevHighestBar - highestBar;
		int tries = 5;
		bar = 0;
		
		for(int a = 0; a < 25 - (level / 2); a++) {
			double x, y;
			
			if(a % 10 == 0 && level > 5 && Math.random() < 0.25 && doodle.getVelocityY() <= doodle.getDefaultMaxVelocityY()) {
				Image monster;
				if(Math.random() * 100 > 50)
					monster = gameWorld.getGame().getArt().get("blackhole");
				else
					monster = gameWorld.getGame().getArt().get("alien");
				
				boolean tooclose;
				int count = 0;
				do {
					x = Math.random() * (gameWorld.getWidth() - monster.getWidth(null));
					y = prevHighestBar - (Math.random() * length);
					tooclose = false;
					
					count++;
					if(count >= tries)
						break;
					
					for(Entity e : gameWorld.getEntities()) {
						if(e.getBounds().intersects(new Rectangle2D.Double(x, y, monster.getWidth(null), monster.getHeight(null)))) {
							tooclose = true;
							break;
						}
					}
				} while(tooclose);
				
				if(count < tries) {
					bar++;
					if(monster == gameWorld.getGame().getArt().get("blackhole"))
						gameWorld.add(1, new BlackHoleMonster(x, y));
					else
						gameWorld.add(1, new AlienMonster(x, y));
				}
			}
			else if(a % 4 == 0 && level > 0 && doodle.getVelocityY() <= doodle.getDefaultMaxVelocityY()) {
				int b = (int)(Math.random() * monsterNum) + 1;
				Image monster = gameWorld.getGame().getArt().get("monster" + b);
				
				boolean tooclose;
				int count = 0;
				do {
					x = Math.random() * (gameWorld.getWidth() - 50);
					y = prevHighestBar - (Math.random() * length);
					tooclose = false;
					
					count++;
					if(count >= tries)
						break;
					
					for(Entity e : gameWorld.getEntities()) {
						if(e.getBounds().intersects(new Rectangle2D.Double(x - 20, y - 10 - monster.getHeight(null), 90, 30 + monster.getHeight(null)))) {
							tooclose = true;
							break;
						}
					}
				} while(tooclose);
				
				if(count < tries) {
					bar++;
					StationaryMonster m = (StationaryMonster)gameWorld.add(1, new StationaryMonster(b, (int)Math.round(Math.random() * (level / 10) + 1)));
					((Bar)gameWorld.add(new StationaryBar(x, y))).installMonster(m);
				}
			}
			else if(((level > 5 && a % 3 == 0) || (level > 0 && level % 8 == 0 && level % 9 == 0 && a % 2 == 0)) && doodle.getVelocityY() <= doodle.getDefaultMaxVelocityY()) {
				int b = (int)(Math.random() * monsterNum) + 1;
				Image monster = gameWorld.getGame().getArt().get("monster" + b);
				
				boolean tooclose;
				int count = 0;
				do {
					x = Math.random() * (gameWorld.getWidth() - monster.getWidth(null));
					y = prevHighestBar - (Math.random() * length);
					tooclose = false;
					
					count++;
					if(count >= tries)
						break;
					
					for(Entity e : gameWorld.getEntities()) {
						if(e.getBounds().intersects(new Rectangle2D.Double(0, y, gameWorld.getWidth(), monster.getHeight(null)))) {
							tooclose = true;
							break;
						}
					}
				} while(tooclose);
				
				if(count < tries) {
					bar++;
					gameWorld.add(1, new MovingMonster(b, x, y, (int)Math.round(Math.random() * (level / 10) + 1)));
				}
			}
			else if((level > 3 && (a % 2 == 0 || a % 5 == 0)) || (level > 0 && level % 7 == 0 && level % 8 == 0 && (a % 2 == 0 || a % 5 == 0))) {
				double startDist;
				
				boolean tooclose;
				int count = 0;
				do {
					x = Math.random() * (gameWorld.getWidth() - 50);
					y = prevHighestBar - (Math.random() * (length - 300));
					tooclose = false;
					
					startDist = Math.random() * 400;
					
					count++;
					if(count >= tries)
						break;
					
					for(Entity e : gameWorld.getEntities()) {
						if(e.getBounds().intersects(new Rectangle2D.Double(x, y - startDist, 50, 410))) {
							tooclose = true;
							break;
						}
					}
				} while(tooclose);
				
				if(count < tries) {
					bar++;
					gameWorld.add(new MovingBar(x, y, startDist, false));
				}
			}
			else {
				boolean tooclose;
				int count = 0;
				do {
					x = Math.random() * (gameWorld.getWidth() - 50);
					y = prevHighestBar - (Math.random() * length);
					tooclose = false;
					
					if(count >= tries)
						break;
					
					for(Entity e : gameWorld.getEntities()) {
						if(e.getBounds().intersects(new Rectangle2D.Double(x - 20, y - 20, 90, 50))) {
							tooclose = true;
							break;
						}
					}
				} while(tooclose);
				
				if(count < tries) {
					bar++;
					
					double random2 = Math.random() * 100;
					if(random2 > 40)
						gameWorld.add(new StationaryBar(x, y));
					else if(random2 > 30 && level > 2)
						gameWorld.add(new DisappearingBar(x, y));
					else
						gameWorld.add(new BreakingBar(x, y));
				}
			}
		}
		
		dif = System.currentTimeMillis() - time;
		System.out.println(bar + " random bars          " + dif + " milliseconds");
	}
}
