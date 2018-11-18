package model;

/**
 * 
 * @author Fabian Kemper
 *
 */

public class Onitama {
	private Game currentGame;

	private HighScoreList highScoreList;

	
	public Onitama() {
		this.highScoreList = new HighScoreList();
		this.currentGame = null;
	}
	
	/**
	 * This method allow a value of null!
	 * @return the currentGame
	 */
	public Game getCurrentGame() {
		return currentGame;
	}

	/**
	 * @param currentGame the currentGame to set
	 */
	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}

	/**
	 * @return the highScoreList
	 */
	public HighScoreList getHighScoreList() {
		return highScoreList;
	}

	/**
	 * @param highScoreList the highScoreList to set
	 */
	public void setHighScoreList(HighScoreList highScoreList) {
		if (highScoreList == null) {
			throw new NullPointerException();
		}
		this.highScoreList = highScoreList;
	}

}
