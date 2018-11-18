package controller;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.Game;
import model.GameBoard;
import model.GameState;
import model.HighScoreEntry;
import model.HighScoreList;
import model.Onitama;
import model.Player;




public class HighScoreTest {
	

	

	private Game currentGame;
	private OnitamaController onitamaController;
	private PlayerController playerController;
	private Player player;
	private Player player2;
	private GameState gameState;
	private Onitama onitama;
	private GameBoard gameBoard;
	HighscoreController highScoreController;
	
	@Before
	public void setUp() throws Exception {
		
		onitamaController = new OnitamaController();
		onitama = new Onitama();
		
		currentGame = new Game();
		System.out.println("set up:");
		System.out.println(currentGame.getCurrentGameState().getGameBoard().getString());
		onitama.setCurrentGame(currentGame);
		onitamaController.setOnitama(onitama);
		playerController = onitamaController.getPlayerController();
		gameState = currentGame.getCurrentGameState();
		highScoreController = onitamaController.getHighscoreController();
		
	}
	
	@Test
	public void HighscoreTester()
	{
		
		HighScoreList highScoreList = onitama.getHighScoreList();
		HighScoreEntry highScoreEntry1 = new HighScoreEntry("Thilo",1,1,1,5);
		HighScoreEntry highScoreEntry2 = new HighScoreEntry("Mathias",2,2,2,5);
		HighScoreEntry highScoreEntry3 = new HighScoreEntry("Nico",3,3,3,5);
		HighScoreEntry highScoreEntry4 = new HighScoreEntry("Fabian",4,4,4,20010451);
		
		highScoreList.add(highScoreEntry1, 2);
		highScoreList.add(highScoreEntry2, 2);
		highScoreList.add(highScoreEntry3, 2);
		highScoreList.add(highScoreEntry4, 4);
		
		onitama.setHighScoreList(highScoreList);
		
		highScoreController.addToHighscore(currentGame);
		
	}
	
	@Test
	public void loadTest()
	{
		highScoreController.loadHighscore();
		
		for(HighScoreEntry entry : onitama.getHighScoreList().getTwoPlayer())
		{
			System.out.println(entry.getName());
		}
	}

}
