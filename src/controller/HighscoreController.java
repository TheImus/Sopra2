package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import model.*;
import java.nio.*;
import java.nio.file.Path;

/**
 * @author Erkan
 *
 */
public class HighscoreController {
	
	public static final int PLAYER_COUNT_MIN = 2;
	public static final int PLAYER_COUNT_BETWEEN = 3;
	public static final int PLAYER_COUNT_MAX = 4;
	public static final String SAVELOCATION = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).getAbsolutePath() + "/onitama.highscore";
	

	private OnitamaController onitamaController;

	public OnitamaController getOnitamaController() {
		
		return onitamaController;
	}

	public HighscoreController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}

	
	/**
	 * this method adds the winner of the given game into the highscore list and saves it
	 * @param game name of the game you want to add to the Highscore list
	 */
	public void addToHighscore(Game game) {
		
		int[] countEntries = new int[4];
		List<Player> players = game.getPlayers();
		int playerCount = players.size();
		HighScoreList highScoreList = onitamaController.getOnitama().getHighScoreList();
		
		
		if(game.isHighscoreEnabled()){
				
			for(int i = 0; i< players.size(); i++){
		
				if(players.get(i).getPlayerType().equals(PlayerType.HUMAN))	{
					HighScoreEntry highScoreEntry = new HighScoreEntry("",0,0,0,0);
					highScoreEntry.setName(players.get(i).getName());
					countEntries = getEntryFromGameStates(game, players.get(i));

					highScoreEntry.setBeatenTokens(countEntries[0]);
					highScoreEntry.setBeatenMaster(countEntries[1]);
					highScoreEntry.setGameMoves(countEntries[2]);
					highScoreEntry.setGameDuration(countEntries[3]);

					
					highScoreList.add(highScoreEntry, playerCount);
				}
			}
			onitamaController.getOnitama().setHighScoreList(highScoreList);
			
		}
		
		
		saveHighscore();
	}
		
	/**
	 * help method for addToHighscore
	 * @param game
	 * @param player
	 * @return Array with all entries besides name (0 = beatenTokens, 1 = beatenMasters, 2 = playerMoves, 3 = gameDuration)
	 */
	private int[] getEntryFromGameStates(Game game, Player player)
	{
		int[] highScoreEntry = new int[4];
		highScoreEntry[0] = 0;
		highScoreEntry[1] = 0;
		highScoreEntry[2] = 0;
		
		List<GameState> gameStates = game.getGameStates();
		
		for(int i = 0; i < gameStates.size()-1;i++)
		{
			if(gameStates.get(i).getCurrentPlayer().equals(player)){
				if(!(gameStates.get(i+1).getLastMove().getDefeatedToken() == null))
				{
					if(!gameStates.get(i+1).getLastMove().getDefeatedToken().isMaster())
					{
						highScoreEntry[0]++;
					}
					else
					{
						highScoreEntry[1]++;
					}
				}
				highScoreEntry[2]++;
			}
		}
		
		highScoreEntry[3] = gameStates.size()-1;
		return highScoreEntry;
	}
	
	/**
	 * this method loads the 2-player highscore list and returns it
	 * @return 2-Player highscore list
	 */
	public List<HighScoreEntry> getTwoPlayerHighScoreList()
	{
		loadHighscore();
		return onitamaController.getOnitama().getHighScoreList().getTwoPlayer();
	}
	
	/**
	 * this method loads the 3-player highscore list and returns it
	 * @return 3-Player highscore list
	 */
	public List<HighScoreEntry> getThreePlayerHighScoreList()
	{
		loadHighscore();
		return onitamaController.getOnitama().getHighScoreList().getThreePlayer();
	}
	
	/**
	 * this method loads the 4-player highscore list and returns it
	 * @return 4-Player highscorelist
	 */
	public List<HighScoreEntry> getFourPlayerHighScoreList()
	{
		loadHighscore();
		return onitamaController.getOnitama().getHighScoreList().getFourPlayer();
	}
	
	public HighScoreList getHighScoreList()
	{
		//loadHighscore();
		return onitamaController.getOnitama().getHighScoreList();
	}
	
	
	/**
	 *this method saves the Highscore in a file
	 */
	private void saveHighscore(){
        try {
        	HighScoreList highScoreList = onitamaController.getOnitama().getHighScoreList();
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(SAVELOCATION));
            stream.writeObject(highScoreList);
            stream.close();
        } catch (IOException ioex) {
            System.err.println("Fehler beim Schreiben des Objekts aufgetreten.");
            ioex.printStackTrace();
        }
	}
	
	
	/**
	 *this method loads the highscore from a file 
	 */
	public void loadHighscore(){
		HighScoreList highScoreList = new HighScoreList();
		
		try {
	        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(SAVELOCATION));
	        highScoreList = (HighScoreList) stream.readObject();
	        stream.close();
	        onitamaController.getOnitama().setHighScoreList(highScoreList);
	    }
		catch (FileNotFoundException e) {
			System.out.println("empty");
		}	catch (ClassNotFoundException cnfex) {
	        System.err.println("Die Klasse des geladenen Objekts konnte nicht gefunden werden.");
	    } catch (IOException ioex) {
	        System.err.println("Das Objekt konnte nicht geladen werden.");
	        ioex.printStackTrace();
	    } 
		
	
	}
	
	/**
	 * this method resets (deletes all entrys) the list that matches the player count
	 * @param playerCount
	 */
	public void resetHighscore(int playerCount){
		
		HighScoreList highScoreList = onitamaController.getOnitama().getHighScoreList();
		
		if(playerCount == PLAYER_COUNT_MIN){
			highScoreList.getTwoPlayer().clear();
		}
		else if(playerCount == PLAYER_COUNT_BETWEEN){
			highScoreList.getThreePlayer().clear();
		}
		else{
			highScoreList.getFourPlayer().clear();
		}
	}
	
	/**
	 *this method resets the whole Highscore list 
	 */
	public void resetHighscore(){
		
		HighScoreList highScoreList = onitamaController.getOnitama().getHighScoreList();
		
		
			highScoreList.getTwoPlayer().clear();
			highScoreList.getFourPlayer().clear();
			highScoreList.getThreePlayer().clear();
	
	}
	

}
