package model;

import java.io.Serializable;

public class Player implements Serializable{
	
	private static final long serialVersionUID = -7559010669051345015L;
	private String name;
	private Vector startingPosition;
	private PlayerType playerType;
	private PlayerColor playerColor;
	private Vector position;
	
	
	public Player(String name, PlayerType playerType, PlayerColor playerColor){
		if (name == null || playerType == null || playerColor == null) {
			throw new NullPointerException();
		}
		
		this.name = name;
		this.playerType = playerType;
		this.playerColor = playerColor;	
		this.startingPosition = new Vector(0, 0); // dummy starting position ... is overwritten by game class
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
	}

	/**
	 * @return the startingPosition
	 */
	public Vector getStartingPosition() {
		return startingPosition;
	}

	/**
	 * @param startingPosition the startingPosition to set
	 */
	public void setStartingPosition(Vector startingPosition) {
		if (startingPosition == null) {
			throw new NullPointerException();
		}
		this.startingPosition = startingPosition;
	}

	/**
	 * @return the playerType
	 */
	public PlayerType getPlayerType() {
		return playerType;
	}

	/**
	 * @param playerType the playerType to set
	 */
	public void setPlayerType(PlayerType playerType) {
		if (playerType == null) {
			throw new NullPointerException();
		}
		this.playerType = playerType;
	}

	/**
	 * @return the playerColor
	 */
	public PlayerColor getPlayerColor() {
		return playerColor;
	}

	/**
	 * @param playerColor the playerColor to set
	 */
	public void setPlayerColor(PlayerColor playerColor) {
		if (playerColor == null) {
			throw new NullPointerException();
		}
		this.playerColor = playerColor;
	}

	/**
	 * @return the position
	 */
	public Vector getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector position) {
		if (position == null) {
			throw new NullPointerException();
		}
		this.position = position;
	}

}
