package com.ra4king.jdoodlejump;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.ra4king.gameutils.Game;
import com.ra4king.gameutils.InputAdapter;
import com.ra4king.gameutils.Screen;
import com.ra4king.gameutils.gameworld.GameWorld;
import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.SocketPacketIO;
import com.ra4king.gameutils.util.Score;
import com.ra4king.jdoodlejump.bars.BreakingBar;
import com.ra4king.jdoodlejump.bars.DisappearingBar;
import com.ra4king.jdoodlejump.bars.MovingBar;
import com.ra4king.jdoodlejump.bars.StationaryBar;
import com.ra4king.jdoodlejump.monsters.AlienMonster;
import com.ra4king.jdoodlejump.monsters.BlackHoleMonster;
import com.ra4king.jdoodlejump.monsters.MovingMonster;
import com.ra4king.jdoodlejump.monsters.StationaryMonster;
import com.ra4king.jdoodlejump.powerups.CopterHatPowerUp;
import com.ra4king.jdoodlejump.powerups.RocketPowerUp;
import com.ra4king.jdoodlejump.powerups.ShieldPowerUp;
import com.ra4king.jdoodlejump.powerups.SpringShoesPowerUp;

public strictfp class Multiplayer extends GameWorld {
	private SocketPacketIO io;
	
	private Doodle doodle, opponent;
	private Score score;
	private double limit;
	
	private Bullet lastBullet;
	
	private int doodleSpeed = 400;
	
	public Multiplayer(SocketPacketIO io) throws Exception {
		this.io = io;
		io.setBlocking(false);
		
		score = new Score();
		
		buildWorld();
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
					case KeyEvent.VK_SPACE:
						if(!doodle.isShooting() && doodle.isAbleToShoot()) {
							lastBullet = (Bullet)add(2, new Bullet(score, new Point2D.Double(doodle.getX() + doodle.getWidth() / 2, doodle.getY()),
									new Point2D.Double(doodle.getX() + doodle.getWidth() / 2, doodle.getY() - 100)));
							doodle.setShooting(true);
							game.getSound().play("shoot");
						}
				}
			}
			
			@Override
			public void keyReleased(KeyEvent key, Screen screen) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_M:
						game.getSound().setOn(!game.getSound().isOn());
						break;
					case KeyEvent.VK_SPACE:
						doodle.setShooting(false);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent me, Screen screen) {
				if(game.isPaused())
					return;
				
				if(me.getButton() == MouseEvent.BUTTON1 && doodle.isAbleToShoot() && lastBullet == null) {
					lastBullet = (Bullet)add(2, new Bullet(score, new Point2D.Double(doodle.getX() + doodle.getWidth() / 2, doodle.getY()), new Point2D.Double(me.getX(), me.getY() - getYOffset())));
					doodle.setShooting(true);
					game.getSound().play("shoot");
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent me, Screen screen) {
				doodle.setShooting(false);
			}
		});
	}
	
	@Override
	public void show() {
		super.show();
		
		getGame().setFPS(60);
		getGame().getInput().reset();
		limit = getHeight();
	}
	
	@Override
	public JDoodleJump getGame() {
		return (JDoodleJump)super.getGame();
	}
	
	private Packet out = new Packet();
	
	@Override
	public void update(long deltaTime) {
		out.clear();
		
		out.writeByte((byte)0);
		
		try {
			out.writeLong(deltaTime);
			
			if(getGame().getInput().isKeyDown(KeyEvent.VK_LEFT) || getGame().getInput().isKeyDown(KeyEvent.VK_A)) {
				doodle.setX(doodle.getX() - (doodleSpeed * (deltaTime / 1e9)));
				doodle.setFacingRight(false);
				out.writeInt(-1);
			}
			else if(getGame().getInput().isKeyDown(KeyEvent.VK_RIGHT) || getGame().getInput().isKeyDown(KeyEvent.VK_D)) {
				doodle.setX(doodle.getX() + (doodleSpeed * (deltaTime / 1e9)));
				doodle.setFacingRight(true);
				out.writeInt(1);
			}
			else
				out.writeInt(0);
			
			if(lastBullet != null) {
				out.writeInt(1);
				Point2D.Double d = lastBullet.getDoodlePoint();
				out.writeDouble(d.x);
				out.writeDouble(d.y);
				Point2D.Double m = lastBullet.getMousePoint();
				out.writeDouble(m.x);
				out.writeDouble(m.y);
				
				lastBullet = null;
			}
			else
				out.writeInt(0);
			
			io.write(out);
			
			Packet p;
			while((p = io.read()) != null) {
				int id = p.readInt();
				if(id == 0) {
					opponent.setX(p.readDouble());
					opponent.setY(p.readDouble());
					opponent.setFacingRight(p.readBoolean());
					
					if(p.readInt() == 1) {
						add(new Bullet(null, new Point2D.Double(p.readDouble(), p.readDouble()), new Point2D.Double(p.readDouble(), p.readDouble())));
						opponent.setShooting(true);
					}
					else if(System.currentTimeMillis() / 100 % 10 == 0)
						opponent.setShooting(false);
				}
				else if(id == -1) {
					if(p.hasMore()) {
						JOptionPane.showMessageDialog(getGame(), (p.readInt() == 1 ? "You win!" : "You lose!"));
						gameOver();
					}
					else
						throw new IOException();
				}
				else {
					JOptionPane.showMessageDialog(getGame(), "GOT ID: " + id);
				}
			}
			
			if(doodle.getX() + doodle.getWidth() / 2 > getWidth())
				doodle.setX(-doodle.getWidth() / 2);
			else if(doodle.getX() + doodle.getWidth() / 2 < 0)
				doodle.setX(getWidth() - doodle.getWidth() / 2);
			
			super.update(deltaTime);
			
			if(doodle.getY() + getYOffset() > limit && limit > getHeight() / 2) {
				limit -= (300 * (deltaTime / 1e9));
				
				if(limit < getHeight() / 2)
					limit = getHeight() / 2;
				
				double dist = doodle.getY() + getYOffset() - limit;
				
				setYOffset(getYOffset() - dist);
			}
			if(doodle.getY() + getYOffset() <= getHeight() / 2) {
				double dist = getHeight() / 2 - (doodle.getY() + getYOffset());
				score.add(dist);
				
				setYOffset(getYOffset() + dist);
			}
			
			if(doodle.isHit()) {
				gameOver();
			}
		} catch(IOException exc) {
			JOptionPane.showMessageDialog(getGame(), "Your opponent has disconnected!");
			gameOver();
		} catch(Exception exc) {
			exc.printStackTrace();
			JOptionPane.showMessageDialog(getGame(), "<html>ERROR!<br />Error message: " + exc);
			gameOver();
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		
		g.setColor(Color.black);
		g.drawString("yOffset: " + getYOffset(), 200, 10);
		g.drawString("opponent X: " + opponent.getX(), 200, 20);
		g.drawString("opponent Y: " + opponent.getY(), 200, 30);
	}
	
	public void gameOver() {
		// getGame().setScreen("Menus");
		
		try {
			Packet p = new Packet();
			p.writeInt(-1);
			io.write(p);
			
			while(io.read() == null);
		} catch(Exception exc) {
			exc.printStackTrace();
		}
		
		System.exit(0);
	}
	
	public Score getScore() {
		return score;
	}
	
	private void buildWorld() {
		try {
			Packet p;
			while((p = io.read()) == null);
			
			doodle = (Doodle)add(3, new Doodle(score, true));
			opponent = (Doodle)add(3, new Doodle(score, false));
			
			doodle.setX(p.readDouble());
			doodle.setY(p.readDouble());
			opponent.setX(p.readDouble());
			opponent.setY(p.readDouble());
			
			System.out.println("Doodle: " + doodle.getX() + " " + doodle.getY());
			System.out.println("Opponent: " + opponent.getX() + " " + opponent.getY());
			
			while(p.hasMore()) {
				byte a = p.readByte();
				switch(p.readByte()) {
					case 1:
						add(a, new StationaryBar(p.readDouble(), p.readDouble()));
						break;
					case 2:
						add(a, new MovingBar(p.readDouble(), p.readDouble(), p.readDouble(), p.readBoolean()));
						break;
					case 3:
						add(a, new DisappearingBar(p.readDouble(), p.readDouble()));
						break;
					case 4:
						add(a, new BreakingBar(p.readDouble(), p.readDouble()));
						break;
					case 5:
						add(a, new StationaryMonster(p.readByte(), p.readByte())).setLocation(p.readDouble(), p.readDouble());
						break;
					case 6:
						byte num = p.readByte();
						byte hitsTotal = p.readByte();
						add(a, new MovingMonster(num, p.readDouble(), p.readDouble(), hitsTotal));
						break;
					case 7:
						p.readByte();
						p.readByte();
						add(a, new AlienMonster(p.readDouble(), p.readDouble()));
						break;
					case 8:
						p.readByte();
						p.readByte();
						add(a, new BlackHoleMonster(p.readDouble(), p.readDouble()));
					case 9:
						add(a, new CopterHatPowerUp()).setLocation(p.readDouble(), p.readDouble());
						break;
					case 10:
						add(a, new RocketPowerUp()).setLocation(p.readDouble(), p.readDouble());
						break;
					case 11:
						add(a, new SpringShoesPowerUp()).setLocation(p.readDouble(), p.readDouble());
						break;
					case 12:
						add(a, new ShieldPowerUp()).setLocation(p.readDouble(), p.readDouble());
						break;
				}
			}
		} catch(Exception exc) {
			exc.printStackTrace();
			gameOver();
		}
	}
}
