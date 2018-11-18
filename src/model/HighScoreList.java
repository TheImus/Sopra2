package model;

import java.io.Serializable;

/**
 * 
 * @author Thilo Röthemeyer
 *
 */


import java.util.ArrayList;
import java.util.List;

public class HighScoreList implements Serializable {

	private static final long serialVersionUID = -6559594034269902627L;
	private static final int PLAYER_COUNT_LIST_1 = 2;
	private static final int PLAYER_COUNT_LIST_2 = 3;
	private static final int PLAYER_COUNT_LIST_3 = 4;
	private List<HighScoreEntry> twoPlayer;
	private List<HighScoreEntry> threePlayer;
	private List<HighScoreEntry> fourPlayer;
	
	
	
	/**
	 *  generates three Lists for each Playoption (two, three and four players)
	 */
	public HighScoreList() {
		twoPlayer = new ArrayList<HighScoreEntry>();
		threePlayer = new ArrayList<HighScoreEntry>();
		fourPlayer = new ArrayList<HighScoreEntry>();
	}
	
	/**
	 * @return Highscorelist von vier Spieler Spielen
	 */
	public List<HighScoreEntry> getFourPlayer() {
		return fourPlayer;
	}
	
	
	/**
	 * @return Highscorelist von drei Spieler Spielen
	 */
	public List<HighScoreEntry> getThreePlayer() {
		return threePlayer;
	}
	
	/**
	 * @return Highscorelist von zwei Spieler Spielen
	 */
	public List<HighScoreEntry> getTwoPlayer() {
		return twoPlayer;
	}
	
	
	
	/**
	 * @param highScoreEntry für eine der Listen
	 * @param playerCount Spielmodus(zwei, drei oder vier Spieler)
	 */
	public void add(HighScoreEntry highScoreEntry, int playerCount){
		if(playerCount == PLAYER_COUNT_LIST_1){
			twoPlayer.add(highScoreEntry);
		}
		else if (playerCount == PLAYER_COUNT_LIST_2){
			threePlayer.add(highScoreEntry);
		}
		else if(playerCount == PLAYER_COUNT_LIST_3){
			fourPlayer.add(highScoreEntry);
		}
	}
	

	




}
