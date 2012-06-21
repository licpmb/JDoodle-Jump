package com.ra4king.jdoodlejump.bars;

import java.awt.Color;

import com.ra4king.jdoodlejump.Doodle;


public class DisappearingBar extends Bar {
	public DisappearingBar(double x, double y) {
		super("disappearingbar");
		
		setX(x);
		setY(y);
		
		setColor(Color.white);
	}
	
	@Override
	public void jump(Doodle doodle) {
		super.jump(doodle);
		
		getParent().remove(this);
	}
	
	@Override
	public void playSound() {
		getParent().getGame().getSound().play("boom");
	}
	
	@Override
	public boolean playsDefaultSound() {
		return false;
	}
}