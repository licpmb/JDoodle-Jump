package com.ra4king.jdoodlejump;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ra4king.gameutils.Game;
import com.ra4king.gameutils.InputAdapter;
import com.ra4king.gameutils.MenuPage;
import com.ra4king.gameutils.Menus;
import com.ra4king.gameutils.Screen;
import com.ra4king.gameutils.Sound;
import com.ra4king.gameutils.gameworld.GameWorld;
import com.ra4king.gameutils.gui.Button;
import com.ra4king.gameutils.gui.Label;
import com.ra4king.gameutils.gui.Widget;
import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.SocketPacketIO;
import com.ra4king.jdoodlejump.bars.StationaryBar;
import com.ra4king.jdoodlejump.gui.HighScores;

public class JDoodleJump extends Game {
	private static final long serialVersionUID = 2470014367406791416L;
	
	private static boolean isJavaWS;
	
	public static void main(String[] args) throws Exception {
		if(args.length > 0 && args[0].equals("JavaWS"))
			isJavaWS = true;
		else {
			JOptionPane.showMessageDialog(null, "You are not using the latest version. Please click OK and delete this game.");
			if(Desktop.isDesktopSupported())
				Desktop.getDesktop().browse(new URL("http://www.ra4king.com/games/JDoodleJump/JDoodleJump.jnlp").toURI());
			else
				JOptionPane.showMessageDialog(null,"Please visit http://www.ra4king.com/games/JDoodleJump and download the latest update.");
			
			System.exit(0);
		}
		
		JDoodleJump game = new JDoodleJump();
		game.setupFrame("JDoodle Jump",false);
		game.start();
	}
	
	private Level level;
	private boolean isMultiplayer = false;
	private Menus menus;
	private Sound.Loader soundLoader;
	private HighScores highScores;
	private MenuPage mainMenu;
	private Button play;
	private Label titleLabel;
	Label scoreLabel;
	Label highscoreLabel;
	private boolean hasFinishedLoading;
	private int currentTheme;
	
	public JDoodleJump() {
		super(500,
			  500,
			  Runtime.getRuntime().availableProcessors() >= 4 ? 200 : 120,
			  6.7);
	}
	
	@Override
	public void initGame() {
		Themes.init(getArt());
		
		level = new Level();
		level.setBackground("background");
		addScreen("GameWorld",level);
		
		menus = new Menus();
		setScreen("Menus",menus);
		
		try{
			loadResources();
			
//			Themes themes = Themes.getThemes();
//			while(themes.getLoaderStatus()+soundLoader.getStatus() < themes.getTotalImages()+soundLoader.getTotal())
//				Thread.sleep(100);
		}
		catch(Exception exc) {
			exc.printStackTrace();
			if(!isApplet())
				System.exit(0);
		}
		
//		try {
//			SocketPacketIO io = new SocketPacketIO("ra4king.com",5052,80*1024);
//			Packet p = new Packet();
//			p.writeString("DoodleJump game");
//			io.write(p);
//			
//			p = new Packet();
//			p.writeInt(0);
//			p.writeString("ra4king","roi4ever");
//			io.write(p);
//			
//			if(io.read().readInt() == 0)
//				System.out.println("LOGGED IN!");
//			else
//				throw new Exception();
//			
//			p = new Packet();
//			p.writeInt(0);
//			io.write(p);
//			
//			System.out.println("Waiting for match.");
//			
//			while((p = io.read()).readInt() == 0)
//				System.out.println("waiting...");
//			
//			System.out.println("NEW MATCH! Player #" + p.readInt());
//			
//			level = new Multiplayer(io);
//			setScreen("GameWorld",level);
//			
//			level.setBackground("background");
//			
//			return;
//		}
//		catch(Exception exc) {
//			exc.printStackTrace();
//			System.exit(0);
//		}
		
		//MAIN MENU
		mainMenu = menus.addPage("Main Menu",new MenuPage(menus));
		mainMenu.setBackground("background");
		
		if(!isJavaWS && !isApplet()) {
			mainMenu.add(new Label("PLEASE DELETE THIS GAME",25,getWidth()/2,20,true));
			mainMenu.add(new Label("AND DOWNLOAD NEW FROM WEBSITE!!",25,getWidth()/2,50,true));
		}
		
		if(isApplet()) {
			titleLabel = (Label)mainMenu.add(new Label("JDoodle Jump",Color.blue,50,getWidth()/2,100,true));
			play = (Button)mainMenu.add(new Button("Loading... 0%",40,150,160,75,75,false,new Button.Action() {
				public void doAction(Button b) {
					level.newGame();
				}
			}));
			play.setBackgroundGradient(Color.orange);
			play.setEnabled(false);
			
			Button highscores = (Button)mainMenu.add(new Button("High Scores!",40,180,235,75,75,false,new Button.Action() {
				@Override
				public void doAction(Button b) {
					menus.setMenuPageShown("Highscores Menu");
				}
			}));
			highscores.setBackgroundGradient(Color.orange);
			
			Button download = (Button)mainMenu.add(new Button("Download!",40,250,310,75,75,false,new Button.Action() {
				@Override
				public void doAction(Button b) {
					new Thread() {
						@Override
						public void run() {
							try {
								SocketPacketIO io = new SocketPacketIO("ra4king.is-a-geek.net",5050);
								
								Packet p = new Packet();
								p.writeString("DoodleJump game");
								io.write(p);
								
								p = new Packet();
								p.writeInt(4);
								io.write(p);
								
								p = new Packet();
								p.writeInt(-1);
								io.write(p);
								
								io.close();
							}
							catch(Exception exc) {}
						}
					}.start();
					
					try {
						getAppletContext().showDocument(new URL("http://www.ra4king.com/games/JDoodleJump/JDoodleJump.jnlp"));
					}
					catch(Exception exc) {}
				}
			}));
			download.setBackgroundGradient(Color.orange);
		}
		else {
			titleLabel = (Label)mainMenu.add(new Label("JDoodle Jump",Color.blue,50,getWidth()/2,100,true));
			play = (Button)mainMenu.add(new Button("Loading... 0%",50,getWidth()/2-20,getHeight()/2-20,75,75,true,new Button.Action() {
				@Override
				public void doAction(Button b) {
					level.newGame();
				}
			}));
			play.setBackgroundGradient(Color.orange);
			play.setEnabled(false);
			
			Button highscores = (Button)mainMenu.add(new Button("High Scores",50,getWidth()/2+68,getHeight()/2+80,75,75,true,new Button.Action() {
				@Override
				public void doAction(Button b) {
					menus.setMenuPageShown("Highscores Menu");
				}
			}));
			highscores.setBackgroundGradient(Color.orange);
		}
		
		//HIGHSCORES MENU
		MenuPage hsMenu = menus.addPage("Highscores Menu",new MenuPage(menus));
		hsMenu.setBackground("background");
		
		highScores = (HighScores)hsMenu.add(new HighScores(getVersion()));
		Button bkBtn = (Button)hsMenu.add(new Button("Back to Main Menu",20,getWidth()/2,100,50,50,true,new Button.Action() {
			@Override
			public void doAction(Button b) {
				menus.setMenuPageShown("Main Menu");
			}
		}));
		bkBtn.setBackgroundGradient(Color.orange);
		
		level.getScore().setHighScore(highScores.readFromCookie());
		
		//GAMEOVER MENU
		final MenuPage gameOverMenu = menus.addPage("GameOver Menu",new MenuPage(menus));
		gameOverMenu.setBackground("background");
		
		gameOverMenu.add(new Label("Game Over!",30,getWidth()/2,100,true));
		scoreLabel = (Label)gameOverMenu.add(new Label("Score: ",30,getWidth()/2,150,true));
		highscoreLabel = (Label)gameOverMenu.add(new Label("Highscore: ",30,getWidth()/2,200,true));
		Button playAgain = (Button)gameOverMenu.add(new Button("Play Again!",40,getWidth()/2,getHeight()/2+30,75,75,true,new Button.Action() {
			@Override
			public void doAction(Button b) {
				level.newGame();
			}
		}));
		playAgain.setBackgroundGradient(Color.orange);
		
		Button backToMainMenu = (Button)gameOverMenu.add(new Button("Main Menu",35,375,getHeight()/2+180,75,75,true,new Button.Action() {
			@Override
			public void doAction(Button b) {
				menus.setMenuPageShown("Main Menu");
			}
		}));
		backToMainMenu.setBackgroundGradient(Color.orange);
		
		Button highscores = (Button)gameOverMenu.add(new Button("High Scores",35,150,getHeight()/2+120,75,75,true,new Button.Action() {
			@Override
			public void doAction(Button b) {
				menus.setMenuPageShown("Highscores Menu");
			}
		}));
		highscores.setBackgroundGradient(Color.orange);
		
		InputAdapter newGameListener = new InputAdapter() {
			@Override
			public void keyPressed(KeyEvent key, Screen screen) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_SPACE:
					case KeyEvent.VK_ENTER:
						level.newGame();
				}
			}
		};
		
		addInputListener(mainMenu,newGameListener);
		addInputListener(gameOverMenu,newGameListener);
		
		//MAIN MENU INPUT LISTENERS
		InputAdapter nameListener =  new InputAdapter() {
			@Override
			public void mousePressed(MouseEvent me, Screen screen) {
				if(screen == mainMenu && new Rectangle(253,390,230,30).contains(me.getPoint())) {
					try {
						if(isApplet())
							getAppletContext().showDocument(new URL("http://www.facebook.com/RoisWebsite"), "_blank");
						else if(Desktop.isDesktopSupported())
							Desktop.getDesktop().browse(new URL("http://www.facebook.com/RoisWebsite").toURI());
						else
							JOptionPane.showMessageDialog(null,"Visit my Facebook page at www.facebook.com/RoisWebsite!");
						
						new Thread() {
							@Override
							public void run() {
								try {
									SocketPacketIO io = new SocketPacketIO("ra4king.is-a-geek.net",5050);
									
									Packet p = new Packet();
									p.writeString("DoodleJump game");
									io.write(p);
									
									p = new Packet();
									p.writeInt(3);
									io.write(p);
									
									p = new Packet();
									p.writeInt(-1);
									io.write(p);
									
									io.close();
								}
								catch(Exception exc) {}
							}
						}.start();
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
				}
				
				if(highScores.getName() != null) {
					FontMetrics fm = getGraphics().getFontMetrics(new Font(Font.SANS_SERIF,Font.BOLD,20));
					Rectangle2D.Double rect = new Rectangle2D.Double(120,0,fm.stringWidth(highScores.getName()),fm.getHeight());
					
					if(rect.contains(me.getX(),me.getY())) {
						String name = "";
						while(name.equals("")) {
							name = JOptionPane.showInputDialog(getRootParent(),"Type your name:");
							
							if(name == null)
								return;
							else if(name.length() > 25) {
								JOptionPane.showMessageDialog(getRootParent(),"Names longer than 25 character are not allowed.");
								name =  "";
							}
							else
								name = name.trim();
						}
						
						String oldName = highScores.getName();
						
						highScores.setName(name);
						
						checkSexy(oldName);
					}
				}
			}
		};
		
		addInputListener(mainMenu, nameListener);
		addInputListener(gameOverMenu, nameListener);
	}
	
	private void loadResources() throws Exception {
		String themeNames[] = {
			"normal",
			"winter",
			"jungle",
			"pygmy",
			"underwater",
			"space"
		};
		
		String imageNames[] = {
			"alien.png",
			"background.png",
			"bar.png",
			"blackhole.png",
			"breakingbar.png",
			"brokenbar.png",
			"bullet.png",
			"disappearingbar.png",
			"doodle.png",
			"doodle shooting.png",
			"horizmovingbar.png",
			"monster1.png",
			"monster2.png",
			"monster3.png",
			"monster4.png",
			"monster5.png",
			"monster6.png",
			"powerup1.png",
			"powerup1alt.png",
			"powerup2.png",
			"powerup2alt.png",
			"powerup3.png",
			"powerup3alt.png",
			"vertmovingbar.png"
		};
		
		String images[][] = new String[themeNames.length][imageNames.length];
		
		getArt().add("images/space/bigrocket.png");
		getArt().add("images/space/bigrocket-right.png");
		getArt().add("images/space/bigrocket-left.png");
		getArt().add("images/taptochange.png");
		getArt().add("images/levels.png");
		getArt().add("images/leveltear.png");
		getArt().add("images/facebook.png");
		getArt().add("images/facebook-like.png");
		
		for(int a = 0; a < images.length; a++)
			for(int b = 0; b < imageNames.length; b++)
				images[a][b] = "images/".concat(themeNames[a]).concat("/").concat(imageNames[b]);
		
		String soundFiles[] = {
			"sounds/blackhole.ogg",
			"sounds/boom.ogg",
			"sounds/bounce.ogg",
			"sounds/break.ogg",
			"sounds/copter.ogg",
			"sounds/fall.ogg",
			"sounds/hitnfall.ogg",
			"sounds/jump.ogg",
			"sounds/moan.ogg",
			"sounds/monsters.ogg",
			"sounds/monsterdeath.ogg",
			"sounds/rocket.ogg",
			"sounds/spring.ogg",
			"sounds/shoot.ogg",
			"sounds/ufoabduct.ogg",
			"sounds/ufodeath.ogg",
			"sounds/ufos.ogg"
		};
		
		Themes themes = Themes.getThemes();
		for(String s[] : images)
			themes.addTheme(s);
		themes.loadThemes();
		
		soundLoader = getSound().new Loader();
		soundLoader.addFiles(soundFiles);
		soundLoader.start();
	}
	
	void changeTheme() {
		Themes.getThemes().rotateThemes();
		currentTheme = Themes.getThemes().getCurrentTheme();
		setCurrentTheme();
	}
	
	void setCurrentTheme() {
		changeTheme(currentTheme);
	}
	
	void setDefaultTheme() {
		changeTheme(0);
	}
	
	private void changeTheme(int newTheme) {
		Themes themes = Themes.getThemes();
		themes.setCurrentTheme(newTheme);
		
		switch(themes.getCurrentTheme()) {
			case 0:
				titleLabel.setTextPaint(Color.blue);
				themes.putSetting("doodleXOffset",0);
				themes.putSetting("doodleYOffset",0);
				break;
			case 1:
				titleLabel.setTextPaint(Color.yellow);
				themes.putSetting("doodleXOffset",16);
				themes.putSetting("doodleYOffset",15);
				break;
			case 2:
				titleLabel.setTextPaint(Color.red);
				themes.putSetting("doodleXOffset",6);
				themes.putSetting("doodleYOffset",0);
				break;
			case 3:
				titleLabel.setTextPaint(Color.blue);
				themes.putSetting("doodleXOffset",4);
				themes.putSetting("doodleYOffset",13);
				break;
			case 4:
				titleLabel.setTextPaint(Color.yellow);
				themes.putSetting("doodleXOffset",3);
				themes.putSetting("doodleYOffset",0);
				break;
			case 5:
				titleLabel.setTextPaint(Color.yellow);
				themes.putSetting("doodleXOffset", 14);
				themes.putSetting("doodleYOffset", 7);
				break;
		}
		
		level.show();
	}
	
	void submit(int score, long duration, boolean isNewHighscore) {
		String oldName = highScores.getName();
		
		highScores.submitScore(score,duration,isNewHighscore);
		
		checkSexy(oldName);
	}
	
	private void checkSexy(String oldName) {
		String newName = highScores.getName();
		if(((oldName == null || !oldName.toUpperCase().equals("SEXY")) && newName != null && newName.toUpperCase().equals("SEXY")) ||
				(oldName != null && oldName.toUpperCase().equals("SEXY") && !newName.toUpperCase().equals("SEXY")))
			getSound().swap("moan", "jump");
	}
	
	@Override
	public void update(long deltaTime) {
		if(!isMultiplayer && !hasFinishedLoading) {
			Themes themes = Themes.getThemes();
			try{
				play.setText("Loading..." + (int)(((double)(themes.getLoaderStatus()+soundLoader.getStatus())/(double)(themes.getTotalImages()+soundLoader.getTotal()))*100) + "%");
				
				if(themes.getLoaderStatus()+soundLoader.getStatus() == themes.getTotalImages()+soundLoader.getTotal()) {
					if(getRootParent() instanceof JFrame) {
						((JFrame)getRootParent()).setIconImage(getArt().get("doodle"));
					}
					
					getSound().setOn(true);
					
					mainMenu.add(new DoodleSample());
					
					play.setText("Play!");
					play.setEnabled(true);
					
					hasFinishedLoading = true;
				}
				else if(themes.getLoaderStatus() == -1) {
					JOptionPane.showMessageDialog(JDoodleJump.this,"Error extracting images!");
					play.setText("ERROR");
					hasFinishedLoading = true;
				}
				else if(soundLoader.getStatus() == -1) {
					JOptionPane.showMessageDialog(JDoodleJump.this,"Error extracting sounds!");
					play.setText("ERROR");
					hasFinishedLoading = true;
				}
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
		}
		
		if(getFPS() == 60) {
			if(lastTime == 0)
				lastTime = System.nanoTime();
			
			if(deltaTime < 1e9/60) {
				totalCount += deltaTime;
				count++;
			}
			
			long diff;
			if((diff = System.nanoTime() - lastTime) >= 1e9) {
				System.out.println("average error: " + totalCount / (double)count + " with a count of: " + count);
				totalCount = count = 0;
				lastTime += diff;
			}
		}
		
		super.update(deltaTime);
	}
	
	private long lastTime, totalCount, count;
	
	@Override
	public void focusLost() {
		if(getScreenName().equals("Main Menu") || getScreenName().equals("Highscores Menu"))
			return;
		
		if(!isMultiplayer)
			pause();
	}
	
	@Override
	public void paused() {
		super.paused();
		getInput().reset();
		getSound().pause();
	}
	
	@Override
	public void resumed() {
		super.resumed();
		getSound().resume();
	}
	
	@Override
	public boolean stopGame() {
		if(!isApplet()) {
			try {
				Frame frame = (Frame)getRootParent();
				frame.dispose();
				getSound().setOn(false);
				while(highScores.isNetworkActive());
					Thread.sleep(100);
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
		}
		
		return true;
	}
	
	@Override
	public void paint(Graphics2D g) {
		super.paint(g);
		
		g.setColor(Color.magenta);
		
		if(!isMultiplayer) {
			if(highScores.getName() != null) {
				g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,20));
				g.drawString(highScores.getName(),120,20);
				
				if(getScreenName().equals("Main Menu") || getScreenName().equals("GameOver Menu"))
					g.drawImage(getArt().get("taptochange"),0,30,null);
			}
		}
		
		if(level.getScore().getHighScore() > 0) {
			g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,15));
			g.drawString("highscore",350,20);
			g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,20));
			g.drawString(""+level.getScore().getHighScore(),425,20);
		}
		
		if(getScreenName().equals("GameWorld")) {
			g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,30));
			g.drawString("" + level.getScore().getLong(),10,30);
			
			if(isPaused()) {
				g.setColor(new Color(100,100,100,75));
				g.fillRect(0,0,getWidth(),getHeight());
				g.setColor(Color.blue);
				g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,30));
				g.drawString("PAUSED",200,250);
				
				g.setColor(Color.black);
				g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,20));
				g.drawString("Press 'P' to resume", 165, 280);
			}
		}
		else if(getScreenName().equals("Main Menu")) {
			drawThemeChooser(g);
			g.drawImage(getArt().get("facebook-like"),getWidth()-247,getHeight()-110,null);
			g.setColor(Color.blue);
			g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,15));
			g.drawString("Like me on Facebook!",getWidth()-170,getHeight()-90);
		}
		
		g.setColor(Color.black);
	}
	
	private Polygon left = new Polygon(new int[] {10,25,25},new int[] {getHeight()-30,getHeight()-40,getHeight()-20},3);
	private Polygon right = new Polygon(new int[] {490,475,475},new int[] {getHeight()-30,getHeight()-40,getHeight()-20},3);
	
	private void drawThemeChooser(Graphics2D g) {
		Themes themes = Themes.getThemes();
		Point p = getInput().getCurrentMouseLocation();
		MouseEvent p2 = getInput().getLastMousePressed();
		
		g.drawImage(getArt().get("levels"),-currentTheme*getWidth(),getHeight()-60,null);
		g.drawImage(getArt().get("leveltear"),0,getHeight()-60,null);
		
		g.setColor(Color.red);
		if(currentTheme > 0) {
			g.fill(left);
			
			if(p != null && left.contains(p)) {
				g.setColor(Color.yellow);
				g.draw(left);
			}
			
			if(p2 != null && left.contains(p2.getPoint()))
				currentTheme--;
		}
		
		if(currentTheme < themes.getTotalThemes()-1) {
			g.setColor(Color.red);
			g.fill(right);
			
			if(p != null && right.contains(p)) {
				g.setColor(Color.yellow);
				g.draw(right);
			}
			
			if(p2 != null && right.contains(p2.getPoint()))
				currentTheme++;
		}
	}
	
	private class DoodleSample extends Widget {
		private GameWorld gameWorld;
		
		@Override
		public void init(Screen parent) {
			super.init(parent);
			
			gameWorld = new GameWorld();
			gameWorld.init(JDoodleJump.this);
			gameWorld.setBackground(new Color(0,0,0,0));
			
			MenuPage page = (MenuPage)parent;
			
			page.getMenus().setMenuPageShown("Main Menu");
		}
		
		@Override
		public void show() {
			Doodle d = (Doodle)gameWorld.add(1,new Doodle(null,true));
			d.setX(60);
			d.setY(470);
			d.setVelocityY(d.getMaxVelocityY());
			d.show();
			gameWorld.add(new StationaryBar(50,400)).show();
		}
		
		@Override
		public void hide() {
			gameWorld.clear();
		}
		
		@Override
		public void update(long deltaTime) {
			gameWorld.update(deltaTime);
		}
		
		@Override
		public void draw(Graphics2D g) {
			gameWorld.draw(g);
		}
	}
}