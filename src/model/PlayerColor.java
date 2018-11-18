package model;

import javafx.scene.paint.Color;

public enum PlayerColor {
	EARTH  (  0, new Color(.753, .514, .000, 1),Math.toRadians(0)), 	// Earth stars down
	AIR    ( 90, new Color(.392, .651, .608, 1),Math.toRadians(90)), 	// Air starts right
	FIRE   (180, new Color(.765, .275, .192, 1),Math.toRadians(180)),	// Fire starts up
	WATER  (270, new Color(.071, .333, .694, 1),Math.toRadians(270));	// Water starts left
	
	private final int angle;
	private final Color color;
	private final double radiant;
	
	private PlayerColor(int angle, Color color,double radiant) {
		this.angle = angle;
		this.color = color;
		this.radiant = radiant;
	}
	
	public int getAngle() {
		return this.angle;
	}
	
	public double getRadiant(){
		return this.radiant;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public PlayerColor getnextColorTwoPlayers() {
		switch (this) {
		case EARTH:
			return FIRE;
		case AIR:
			return null;
		case FIRE:
			return EARTH;
		case WATER:
			return null;
		default:
			return this;
		}
	}
	
	public PlayerColor getnextColorThreePlayers() {
		switch (this) {
		case EARTH:
			return WATER;
		case AIR:
			return EARTH;
		case FIRE:
			return EARTH;
		case WATER:
			return FIRE;
		default:
			return this;
		}
	}
	
	public PlayerColor getnextColorFourPlayers() {
		switch (this) {
		case EARTH:
			return WATER;
		case AIR:
			return EARTH;
		case FIRE:
			return AIR;
		case WATER:
			return FIRE;
		default:
			return this;
		}
	}
}
