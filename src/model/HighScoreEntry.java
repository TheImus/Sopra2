package model;

import java.io.Serializable;

/**
 * 
 * @author Fabian Kemper
 *
 */

public class HighScoreEntry implements Serializable{
	private static final long serialVersionUID = -1650812188985869079L;
	private String name;
	private int beatenTokens;
	private int beatenMasters;
	private int gameMoves;
	private int gameDuration;
	
	
	public HighScoreEntry(String name, int beatenTokens, int beatenMasters, int gameMoves, int gameDuration) {
		this.setName(name);
		this.setBeatenMaster(beatenMasters);
		this.setBeatenTokens(beatenTokens);
		this.setGameMoves(gameMoves);
		this.setGameDuration(gameDuration);
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
		this.name = name;
	}
	/**
	 * @return the beatenToken
	 */
	public int getBeatenTokens() {
		return beatenTokens;
	}
	/**
	 * @param beatenToken the beatenToken to set
	 */
	public void setBeatenTokens(int beatenToken) {
		this.beatenTokens = beatenToken;
	}
	/**
	 * @return the beatenMaster
	 */
	public int getBeatenMaster() {
		return beatenMasters;
	}
	/**
	 * @param beatenMaster the beatenMaster to set
	 */
	public void setBeatenMaster(int beatenMaster) {
		this.beatenMasters = beatenMaster;
	}
	/**
	 * @return the gameMoves
	 */
	public int getGameMoves() {
		return gameMoves;
	}
	/**
	 * @param gameMoves the gameMoves to set
	 */
	public void setGameMoves(int gameMoves) {
		this.gameMoves = gameMoves;
	}
	
	/**
	 * @param gameDuration the gameDuration to set
	 */
	public void setGameDuration(int gameDuration) {
		this.gameDuration = gameDuration;
	}

	/**
	 * @return the gameDuration
	 */
	public int getGameDuration() {
		return gameDuration;
	}
}
