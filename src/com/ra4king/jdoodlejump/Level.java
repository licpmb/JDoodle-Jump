package com.ra4king.jdoodlejump;


import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JOptionPane;

import com.ra4king.gameutils.Game;
import com.ra4king.gameutils.InputAdapter;
import com.ra4king.gameutils.MenuPage;
import com.ra4king.gameutils.Screen;
import com.ra4king.gameutils.gameworld.GameWorld;
import com.ra4king.gameutils.util.Score;
import com.ra4king.jdoodlejump.monsters.Monster;

public class Level extends GameWorld {
	private Doodle doodle;
	private Score score;
	private PlatformGenerator generator;
	private int level;
	private double limit;
	
	private int doodleSpeed = 400;
	
	private boolean useMouse;
	private int mouseSpeed = 600;
	private int mouseX;
	
	private long startTime;
	
	public Level() {
		score = new Score();
		generator = new PlatformGenerator(this);
	}
	
	@Override
	public void init(final Game g) {
		super.init(g);
		
		final JDoodleJump game = (JDoodleJump)g;
		
		game.addInputListener(this, new InputAdapter() {
			@Override
			public void keyPressed(KeyEvent key, Screen screen) {
				if(game.isPaused())
					return;
				
				int keyCode = key.getKeyCode();
				
				switch(keyCode) {
					case KeyEvent.VK_S:
						game.changeTheme();
						break;
					case KeyEvent.VK_SPACE:
						if(!doodle.isShooting() && doodle.isAbleToShoot()) {
							add(2,new Bullet(score,new Point2D.Double(doodle.getX()+doodle.getWidth()/2,doodle.getY()),
											new Point2D.Double(doodle.getX()+doodle.getWidth()/2,doodle.getY()-100)));
							doodle.setShooting(true);
							game.getSound().play("shoot");
						}
				}
			}
			
			@Override
			public void keyReleased(KeyEvent key, Screen screen) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_P:
						if(game.isPaused())
							game.resume();
						else
							game.pause();
						break;
					case KeyEvent.VK_M:
						game.getSound().setOn(!game.getSound().isOn());
						break;
					case KeyEvent.VK_C:
						useMouse = !useMouse;
						hasRightClicked = false;
						break;
					case KeyEvent.VK_SPACE:
						doodle.setShooting(false);
				}
			}
			
			private boolean hasRightClicked;
			private int lastMouseX;
			
			@Override
			public void mouseMoved(MouseEvent me, Screen screen) {
				if(game.isPaused())
					return;
				
				if(hasRightClicked) {
					if(doodle.getIntCenterX() > lastMouseX && doodle.getIntCenterX() < me.getX())
						hasRightClicked = false;
					else if(doodle.getIntCenterX() < lastMouseX && doodle.getIntCenterX() > me.getX())
						hasRightClicked = false;
					
					lastMouseX = me.getX();
					
					if(!hasRightClicked)
						mouseX = lastMouseX;
				}
				else {
					if(useMouse) {
						int diff = me.getX()-mouseX;
						if(diff != 0)
							doodle.setFacingRight(diff > 0);
					}
					mouseX = me.getX();
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent me, Screen screen) {
				mouseMoved(me,screen);
			}
			
			@Override
			public void mousePressed(MouseEvent me, Screen screen) {
				if(game.isPaused())
					return;
				
				if(me.getButton() == MouseEvent.BUTTON1 && doodle.isAbleToShoot()) {
					add(2,new Bullet(score,new Point2D.Double(doodle.getX()+doodle.getWidth()/2,doodle.getY()),new Point2D.Double(me.getX(),me.getY()-getYOffset())));
					doodle.setShooting(true);
					game.getSound().play("shoot");
				}
				else if(me.getButton() == MouseEvent.BUTTON3 && useMouse) {
					hasRightClicked = !hasRightClicked;
					
					if(!hasRightClicked)
						mouseX = me.getX();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent me, Screen screen) {
				doodle.setShooting(false);
			}
		});
	}
	
	@Override
	public JDoodleJump getGame() {
		return (JDoodleJump)super.getGame();
	}
	
	@Override
	public void update(long deltaTime) {
		try{
			if(useMouse) {
				double change = mouseX - doodle.getCenterX();
				
				if(Math.abs(change) <= mouseSpeed*(deltaTime/1e9))
					doodle.setX(doodle.getX() + change);
				else if(change > 0)
					doodle.setX(doodle.getX()+(mouseSpeed*(deltaTime/1e9)));
				else if(change < 0)
					doodle.setX(doodle.getX()-(mouseSpeed*(deltaTime/1e9)));
			}
			else {
				if(getGame().getInput().isKeyDown(KeyEvent.VK_LEFT) || getGame().getInput().isKeyDown(KeyEvent.VK_A)) {
					doodle.setX(doodle.getX()-(doodleSpeed*(deltaTime/1e9)));
					doodle.setFacingRight(false);
				}
				
				if(getGame().getInput().isKeyDown(KeyEvent.VK_RIGHT) || getGame().getInput().isKeyDown(KeyEvent.VK_D)) {
					doodle.setX(doodle.getX()+(doodleSpeed*(deltaTime/1e9)));
					doodle.setFacingRight(true);
				}
				
				if(doodle.getX()+doodle.getWidth()/2 > getWidth())
					doodle.setX(-doodle.getWidth()/2);
				else if(doodle.getX()+doodle.getWidth()/2 < 0)
					doodle.setX(getWidth()-doodle.getWidth()/2);
			}
			
			super.update(deltaTime);
			
			if(generator.getHighestBar() > -getYOffset()) {
				System.out.println("\nLoading new world! highestBar = " + generator.getHighestBar() + " score = " + score.get() + " level = " + level);
				
				if(level < 30)
					level++;
				
				generator.loadWorld(doodle,level,2000);
			}
			
			if(doodle.getScreenY() > limit && limit > getHeight()/2) {
				limit -= (300*(deltaTime/1e9));
				
				if(limit < getHeight()/2)
					limit = getHeight()/2;
				
				setYOffset(limit - doodle.getY());
			}
			else if(doodle.getScreenY() < getHeight()/2) {
				double dist = getHeight()/2-doodle.getScreenY();
				
				score.add(dist);
				
				setYOffset(getHeight()/2-doodle.getY());
			}
			
			if(doodle.isHit()) {
				boolean isNewHighscore = false;
				
				if(score.getLong() > score.getHighScore()) {
					score.setHighScore();
					isNewHighscore = true;
				}
				
				gameOver();
				
				getGame().submit(score.getInt(),System.currentTimeMillis()-startTime,isNewHighscore);
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
			JOptionPane.showMessageDialog(getGame(),"<html>ERROR!<br />Error message: " + exc);
			gameOver();
		}
	}
	
	public void newGame() {
		clear();
		
		score.set(0);
		generator.reset();
		level = 0;
		limit = getHeight();
		
		mouseX = getWidth()/2;
		
		getGame().getInput().reset();
		
		setYOffset(0);
		
		doodle = (Doodle)add(3,new Doodle(score,true));
		
		doodle.setLocation(getWidth()/2, 450);
		
		try{
			generator.loadWorld(doodle,level,2000);
		}
		catch(Exception exc) {
			JOptionPane.showMessageDialog(null,"Error loading game");
			getGame().setScreen("Menus");
		}
		
		getGame().setCurrentTheme();
		
		getGame().setScreen("GameWorld");
		
		startTime = System.currentTimeMillis();
		
		getGame().resume();
	}
	
	public void gameOver() {
		getGame().scoreLabel.setText("Score: " + score.getLong());
		getGame().highscoreLabel.setText("Highscore: " + score.getHighScore());
		((MenuPage)getGame().scoreLabel.getParent()).getMenus().setMenuPageShown("GameOver Menu");
		
		getGame().setDefaultTheme();
		
		getGame().setScreen("Menus");
		
		getGame().getSound().stopAll();
		
		Monster.isLoopingSound = false;
	}
	
	public Score getScore() {
		return score;
	}
}
