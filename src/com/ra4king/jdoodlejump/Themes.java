package com.ra4king.jdoodlejump;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ra4king.gameutils.Art;

public class Themes {
	private static Themes instance;
	
	private Art art;
	private Map<String,Object> settings;
	private ArrayList<String[][]> themes;
	private ArrayList<Art.Loader> loaders;
	
	private int currentTheme;
	
	private Themes(Art art) {
		this.art = art;
		
		settings = new HashMap<String,Object>();
		themes = new ArrayList<String[][]>();
		loaders = new ArrayList<Art.Loader>();
		
		putSetting("isDebug", false);
		putSetting("doodleXOffset", 0);
		putSetting("doodleYOffset", 0);
	}
	
	public static void init(Art art) {
		instance = new Themes(art);
	}
	
	public static Themes getThemes() {
		return instance;
	}
	
	public void addTheme(String images[]) {
		String[][] images2 = new String[images.length][2];
		for(int a = 0; a < images.length; a++) {
			images2[a][0] = images[a];
			images2[a][1] = Art.getFileName(images[a])+(themes.size() == 0 ? "" : themes.size());
		}
		
		addTheme(images2);
	}
	
	public void addTheme(String images[][]) {
		themes.add(images);
		
		Art.Loader artLoader = art.new Loader();
		artLoader.addFiles(images);
		loaders.add(artLoader);
	}
	
	public void removeTheme(int idx) {
		loaders.remove(idx);
		themes.remove(idx);
	}
	
	public void loadThemes() {
		for(Art.Loader loader : loaders)
			loader.start();
	}
	
	public int getLoaderStatus() {
		int status = 0;
		for(Art.Loader loader : loaders)
			status += loader.getStatus();
		return status;
	}
	
	public int getTotalImages() {
		int total = 0;
		for(Art.Loader loader : loaders)
			total += loader.getTotal();
		return total;
	}
	
	public int getTotalThemes() {
		return themes.size();
	}
	
	public int getCurrentTheme() {
		return currentTheme;
	}
	
	public void setCurrentTheme(int newTheme) {
		if(newTheme == currentTheme)
			return;
		
		int prevTheme = currentTheme;
		currentTheme = newTheme;
		
		for(int a = 0; a < themes.get(currentTheme).length; a++)
			art.swap(themes.get(0)[a][1], themes.get(prevTheme)[a][1]);
		
		for(int a = 0; a < themes.get(currentTheme).length; a++)
			art.swap(themes.get(0)[a][1], themes.get(currentTheme)[a][1]);
	}
	
	public void rotateThemes() {
		setCurrentTheme((currentTheme+1)%themes.size());
	}
	
	public void putSetting(String setting, Object value) {
		settings.put(setting, value);
	}
	
	public boolean isDebug() {
		return (Boolean)settings.get("isDebug");
	}
	
	public int getDoodleXOffset() {
		return (Integer)settings.get("doodleXOffset");
	}
	
	public int getDoodleYOffset() {
		return (Integer)settings.get("doodleYOffset");
	}
}