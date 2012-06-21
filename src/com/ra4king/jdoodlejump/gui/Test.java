package com.ra4king.jdoodlejump.gui;

import com.ra4king.gameutils.Game;
import com.ra4king.gameutils.MenuPage;
import com.ra4king.gameutils.Menus;

public class Test extends Game {
	private static final long serialVersionUID = 4838144629754785203L;
	
	public static void main(String args[]) {
		Test t = new Test();
		t.setupFrame("Test", true);
		t.start();
	}
	
	public Test() {
		super(500,500);
	}
	
	@Override
	public void initGame() {
		Menus menus = new Menus();
		
		MenuPage page = new MenuPage(menus);
		
		page.add(new ToggleButton());
		
		setScreen("MenuPage",page);
	}
}
