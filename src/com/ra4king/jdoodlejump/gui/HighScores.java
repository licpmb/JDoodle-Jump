package com.ra4king.jdoodlejump.gui;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import netscape.javascript.JSObject;

import com.ra4king.gameutils.InputAdapter;
import com.ra4king.gameutils.MenuPage;
import com.ra4king.gameutils.Screen;
import com.ra4king.gameutils.gui.Button;
import com.ra4king.gameutils.gui.Label;
import com.ra4king.gameutils.gui.Widget;
import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.SocketPacketIO;

public class HighScores extends Widget {
	private volatile String highScores[][][] = new String[4][][];
	private volatile boolean networkActive;
	private Button iButtons[] = new Button[4];
	private Polygon nextPage;
	private Polygon prevPage;
	private boolean nextPageHL;
	private boolean prevPageHL;
	private final String SERVER = "ra4king.is-a-geek.net";
	private final int PORT = 5050;
	private int iNum;
	private int pageNum[] = new int[4];
	private String name;
	private final double version;
	private boolean hasShownUpdate = false;
	
	public HighScores(double version) {
		this.version = version;
		
		{
			int x[] = {50,80,80};
			int y[] = {450,435,465};
			prevPage = new Polygon(x,y,3);
		}
		{
			int x[] = {370,370,400};
			int y[] = {435,465,450};
			nextPage = new Polygon(x,y,3);
		}
		
		highScores[0] = new String[100][2];
		highScores[1] = new String[500][2];
		highScores[2] = new String[1000][2];
		highScores[3] = new String[2000][2];
		
		Button.Action action = new Button.Action() {
			public void doAction(Button button) {
				String d = button.getText();
				
				iButtons[iNum].setBorder(Color.black);
				iButtons[iNum].setTextPaint(Color.black);
				
				if(d.equals("TODAY"))
					iNum = 0;
				else if(d.equals("WEEK"))
					iNum = 1;
				else if(d.equals("MONTH"))
					iNum = 2;
				else if(d.equals("ALL TIME"))
					iNum = 3;
				
				iButtons[iNum].setBorder(Color.blue);
				iButtons[iNum].setTextPaint(Color.red);
			}
		};
		
		iButtons[0] = new Button("TODAY",20,22,175,5,5,false,action);
		iButtons[1] = new Button("WEEK",20,iButtons[0].getIntX()+iButtons[0].getIntWidth()+1,175,5,5,false,action);
		iButtons[2] = new Button("MONTH",20,iButtons[1].getIntX()+iButtons[1].getIntWidth()+1,175,5,5,false,action);
		iButtons[3] = new Button("ALL TIME",20,iButtons[2].getIntX()+iButtons[2].getIntWidth()+1,175,5,5,false,action);
		
		for(Button b : iButtons) {
			b.setBackground(Color.white);
			b.setBorderHighlight(Color.red);
		}
		
		iButtons[iNum].setBorder(Color.blue);
		iButtons[iNum].setTextPaint(Color.red);
		
		clearHighScores();
	}
	
	@Override
	public void init(Screen parent) {
		super.init(parent);
		
		final MenuPage page = (MenuPage)parent;
		
		for(Button b : iButtons)
			page.add(b);
		
		page.add(new Label("High Scores",Color.green,30,parent.getGame().getWidth()/2,145,true));
		
		page.getGame().addInputListener("Highscores Menu", new InputAdapter() {
			public void mouseMoved(MouseEvent me, Screen screen) {
				nextPageHL = prevPageHL = false;
				
				if(nextPage.contains(me.getPoint()))
					nextPageHL = true;
				else if(prevPage.contains(me.getPoint()))
					prevPageHL = true;
			}
			
			public void mousePressed(MouseEvent me, Screen screen) {
				if(page.getMenus().getMenuPageShown() != getParent())
					return;
				
				if(nextPage.contains(me.getPoint())) {
					pageNum[iNum]++;
					if(pageNum[iNum] > highScores[iNum].length/10-1) pageNum[iNum] = 0;
				}
				else if(prevPage.contains(me.getPoint())) {
					pageNum[iNum]--;
					if(pageNum[iNum] < 0) pageNum[iNum] = highScores[iNum].length/10-1;
				}
			}
		});
	}
	
	@Override
	public void show() {
		super.show();
		updateHighScores();
	}
	
	private void clearHighScores() {
		for(int a = 0; a < highScores.length; a++) {
			for(int b = 0; b < highScores[a].length; b++)
				for(int c = 0; c < highScores[a][b].length; c++)
					highScores[a][b][c] = "";
			
			highScores[a][0][0] = "Fetching . . . . ";
		}
	}
	
	public void updateHighScores() {
		(new Thread() {
			@Override
			public void run() {
				networkActive = true;
				
				SocketPacketIO io = null;
				try{
					io = new SocketPacketIO(SERVER,PORT,128*1024);
					
					Packet p = new Packet();
					p.writeString("DoodleJump game");
					io.write(p);
					
					p = new Packet();
					p.writeInt(0);
					io.write(p);
					
					try{
						double serverVer = io.read().readDouble();
						if(version < serverVer) System.out.println("NEW UPDATE!");
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
					
					p = new Packet();
					p.writeInt(2);
					io.write(p);
					
					try{
						for(int iNum = 0; iNum < 4; iNum++) {
							Packet packet = io.read();
							for(int a = 0; a < highScores[iNum].length; a++) {
								String sections[] = packet.readString().split("<:>");
								
								highScores[iNum][a][0] = sections[0].trim();
								highScores[iNum][a][1] = sections[1].trim();
								
								if(highScores[iNum][a][1].length() == 7)
									highScores[iNum][a][1] = "  " + highScores[iNum][a][1];
								else if(highScores[iNum][a][1].length() == 6)
									highScores[iNum][a][1] = "    " + highScores[iNum][a][1];
								else if(highScores[iNum][a][1].length() == 5)
									highScores[iNum][a][1] = "      " + highScores[iNum][a][1];
								else if(highScores[iNum][a][1].length() == 4)
									highScores[iNum][a][1] = "        " + highScores[iNum][a][1];
								else if(highScores[iNum][a][1].length() == 3)
									highScores[iNum][a][1] = "          " + highScores[iNum][a][1];
								else if(highScores[iNum][a][1].length() == 2)
									highScores[iNum][a][1] = "            " + highScores[iNum][a][1];
							}
						}
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
					
					p = new Packet();
					p.writeInt(-1);
					io.write(p);
				}
				catch(Exception exc) {
					exc.printStackTrace();
					
					for(int a = 0; a < 4; a++)
						highScores[a][0][0] = "Connection Error";
				}
				finally {
					try{
						io.close();
					}
					catch(Exception exc) {}
					
					networkActive = false;
				}
			}
		}).start();
	}
	
	public void submitScore(final int points, final long duration, boolean isNewHighscore) {
		if(points > 0) {
			while(name == null || name.equals("")) {
				name = JOptionPane.showInputDialog(getParent().getGame().getRootParent(),"Type your name:");
				
				if(name == null)
					return;
				else if(name.length() > 25) {
					JOptionPane.showMessageDialog(getParent().getGame().getRootParent(),"Names longer than 25 character are not allowed.");
					name =  "";
				}
				else
					name = name.trim();
			}
			
			if(isNewHighscore)
				saveToCookie(points);
			
			(new Thread() {
				@Override
				public void run() {
					networkActive = true;
					
					SocketPacketIO io = null;
					try{
						io = new SocketPacketIO(SERVER,PORT);
						
						Packet p = new Packet();
						p.writeString("DoodleJump game");
						io.write(p);
						
						p = new Packet();
						p.writeInt(0);
						io.write(p);
						
						try{
							double serverVer = io.read().readDouble();
							if(version < serverVer && !hasShownUpdate) {
								if(getParent().getGame().isApplet())
									JOptionPane.showMessageDialog(getParent().getGame(),"<html>New Update! New version: " + serverVer + "<br><br>Please refresh the page...");
								else {
									JOptionPane.showMessageDialog(getParent().getGame().getRootParent(),"<html>New Update! New version: " + serverVer + "<br><br>Please restart this game...");
									System.exit(0);
								}
								hasShownUpdate = true;
							}
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
						
						p = new Packet();
						p.writeInt(1);
						p.writeString(name);
						p.writeInt(points);
						p.writeLong(duration);
						p.writeBoolean(getParent().getGame().isApplet());
						io.write(p);
						
						p = new Packet();
						p.writeInt(-1);
						io.write(p);
					}
					catch(Exception exc) {}
					finally {
						try{
							io.close();
						}
						catch(Exception exc) {}
						
						networkActive = false;
					}
				}
			}).start();
		}
	}
	
	public boolean isNetworkActive() {
		return networkActive;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.black);
		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,20));
		
		for(int a = pageNum[iNum]*10; a < pageNum[iNum]*10+10; a++) {
			g.drawString(""+(a+1),30,235+((a%10)*20));
			g.drawString(highScores[iNum][a][0],80,235+((a%10)*20));
			g.drawString(highScores[iNum][a][1],380,235+((a%10)*20));
		}
		
		g.setColor(Color.red);
		g.fill(prevPage);
		if(prevPageHL) {
			g.setColor(Color.blue);
			g.draw(prevPage);
		}
		
		g.setColor(Color.red);
		g.fill(nextPage);
		if(nextPageHL) {
			g.setColor(Color.blue);
			g.draw(nextPage);
		}
	}
	
	private void saveToCookie(int highscore) {
		if(getParent().getGame().isApplet()) {
			try {
				JSObject object = JSObject.getWindow(getParent().getGame());
				object.eval("document.cookie='name="+name+";expires=Thurs, 31 Dec 2999 23:59:59 GMT';");
				object.eval("document.cookie='score="+highscore+";expires=Thurs, 31 Dec 2999 23:59:59 GMT';");
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
		}
		else {
			try {
				Preferences prefs = Preferences.userRoot();
				prefs = prefs.node("JDoodleJump");
				prefs.put("name", name);
				prefs.putInt("highscore", highscore);
			}
			catch(Exception exc) {}
		}
	}
	
	public int readFromCookie() {
		if(getParent().getGame().isApplet()) {
			try {
				String c = (String)JSObject.getWindow(getParent().getGame()).eval("document.cookie");
				String[] cookies = c.split(";");
				
				int score = 0;
				for(String s : cookies) {
					s = s.trim();
					if(s.startsWith("score"))
						score = Integer.parseInt(s.split("=")[1]);
					else if(s.startsWith("name"))
						name = s.split("=")[1];
				}
				
				return score;
			}
			catch(Throwable exc) {
				exc.printStackTrace();
				return 0;
			}
		}
		else {
			try {
				Preferences prefs = Preferences.userRoot();
				prefs = prefs.node("JDoodleJump");
				prefs.flush();
				name = prefs.get("name", null);
				return prefs.getInt("highscore", 0);
			}
			catch(Throwable exc) {
				return 0;
			}
		}
	}
}