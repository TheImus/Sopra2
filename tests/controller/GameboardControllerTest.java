package controller;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.PlayerType;
import model.GameBoard;
import model.GameCard;
import model.GameMove;
import model.GameState;
import model.GameToken;
import model.Onitama;
import model.Player;
import model.PlayerColor;
import model.Vector;

public class GameboardControllerTest {
	/*
	 * 
	 * README
	 * 
	 * Hey Nico, Mathias hier;
	 * ich habe ausversehen schon hiermit angefangen, weil ich mich in den Aufgaben vertan habe.
	 * Wenn du möchtest kannst du vlt trotzdem diese Tests benutzen, schau aber nochmal drüber;
	 * ich dachte nur gemachte arbeit doppelt machen ist irgendwie doof.
	 * tr
	 * LG Mathias
	 * 
	 */
	GameStateController gamestateController;
	OnitamaController onitamaController;
	GameState gameState;
	GameBoard currentGameBoard;
	GameboardController gameboardController;
	PlayerController playerController;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		onitamaController = new OnitamaController();
		Onitama onitama = new Onitama();
		onitamaController.setOnitama(onitama);
		
		onitama.setCurrentGame(TestDataFactory.createSampleTwoPlayerGameWinableInNextMove());
		
		gamestateController = onitamaController.getGameStateController();
		gameboardController = onitamaController.getGameboardController();
		//onitamaController.getNewGameController().createDefaultGame();
		gameState = onitamaController.getOnitama().getCurrentGame().getGameStates().get(onitamaController.getOnitama().getCurrentGame().getGameStates().size() - 1);
		currentGameBoard = gameState.getGameBoard();
		playerController = onitamaController.getPlayerController();
		
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Create a player and a respective Token 
	 * Let the Token make a gameMove and add it to the gameboard 
	 * now compare the current position of the token with the expected location
	 *  
	 */
	@Test
	public void testGetNextGameboard() {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Player player = new Player("Mathias", PlayerType.HUMAN , PlayerColor.EARTH);
		GameToken token = new GameToken(false, player);
		GameMove gameMove = new GameMove(token, GameCard.OX, new Vector (1,0) );
		
		currentGameBoard.add(token, new Vector(3, 3));
				
		Vector expectedLocation = currentGameBoard.getDestination(gameMove);
		GameBoard nextBoard = gameboardController.getNextGameboard(currentGameBoard, gameMove);
		if(!expectedLocation.equals(nextBoard.getPositionOf(token)))
			fail("The position of the moved token and the expected location do not match");
	}
	
	

	/**
	 * Find a WinningPosition and place a master token on it 
	 * Check if isOnWinningPosition returns true
	 */
	@Test
	public void testIsOnWinningPosition() {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean breaker = false;
		GameToken masterToken = null;
		for(int x = 0; x < currentGameBoard.getBoard().length && !breaker; x++){
			for(int y = 0; y < currentGameBoard.getBoard().length && !breaker; y++){
				if(currentGameBoard.getTokenAt(new Vector(x, y)) != null && currentGameBoard.getTokenAt(new Vector(x, y)).isMaster()){
					masterToken = currentGameBoard.getTokenAt(new Vector(x, y));
					currentGameBoard.remove(new Vector(x, y));
					//find winning position
					Vector winningPosition = onitamaController.getOnitama().getCurrentGame().getWinningPositions(masterToken.getPlayer()).get(0);
					//\
					currentGameBoard.add(masterToken, winningPosition);
					currentGameBoard.remove(winningPosition);
					
					if(gameboardController.isOnWinningPosition(currentGameBoard, masterToken.getPlayer())){
						fail("we specifically moved a king to one of his winning positions, so this should not happen");
					}
					breaker = true;
				}
			}
		}
		
	}

	/**
	 * Create Players with different numbers of token (0-5)
	 * test for each player whether removePlayerTokens works correctly with each number of tokens
	 */
	@Test
	public void testRemovePlayerTokens() {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Test Case for Player with 5 Tokens 
		Player player1 = new Player("Mathias", PlayerType.HUMAN , PlayerColor.EARTH);
		GameToken token1 = new GameToken(false, player1);
		GameToken token2 = new GameToken(false, player1);
		GameToken token3 = new GameToken(false, player1);
		GameToken token4 = new GameToken(false, player1);
		GameToken token5 = new GameToken(true, player1);
		
		token1.setPlayer(player1);
		token2.setPlayer(player1);
		token3.setPlayer(player1);
		token4.setPlayer(player1);
		token5.setPlayer(player1);

		gameboardController.removePlayerTokens(currentGameBoard, player1);
		if(playerController.getPlayerTokens(gameState, player1).size() != 0) {
			fail("Test failed for Player with 5 Tokens left");
		}
		
		// TestCase for Player with 4 Tokens 
		Player player2 = new Player("Mathias2", PlayerType.HUMAN , PlayerColor.EARTH);
		GameToken token6 = new GameToken(false, player2);
		GameToken token7 = new GameToken(false, player2);
		GameToken token8 = new GameToken(false, player2);
		GameToken token9 = new GameToken(true, player2);
		
		token6.setPlayer(player2);
		token7.setPlayer(player2);
		token8.setPlayer(player2);
		token9.setPlayer(player2);
		
		gameboardController.removePlayerTokens(currentGameBoard, player2);
		if(playerController.getPlayerTokens(gameState, player2).size() != 0) {
			fail("Test failed for Player with 4 Tokens left");
		}
		
		// TestCase for Player with 3 Tokens 
		Player player3 = new Player("Mathias3", PlayerType.HUMAN , PlayerColor.EARTH);
		GameToken token10 = new GameToken(false, player3);
		GameToken token11 = new GameToken(false, player3);
		GameToken token12 = new GameToken(true, player3);
		

		token10.setPlayer(player3);
		token11.setPlayer(player3);
		token12.setPlayer(player3);

		
		gameboardController.removePlayerTokens(currentGameBoard, player3);
		if(playerController.getPlayerTokens(gameState, player3).size() != 0) {
			fail("Test failed for Player with 3 Tokens left");
		}
		
		// TestCase for Player with 2 Tokens 
		Player player4 = new Player("Mathias4", PlayerType.HUMAN , PlayerColor.EARTH);
		GameToken token13 = new GameToken(false, player4);
		GameToken token14 = new GameToken(true, player4);
		

		token13.setPlayer(player4);
		token14.setPlayer(player4);

		
		gameboardController.removePlayerTokens(currentGameBoard, player4);
		if(playerController.getPlayerTokens(gameState, player4).size() != 0) {
			fail("Test failed for Player with 2 Tokens left");
		}
		
		//TestCase for Player with 1 Tokens
		Player player5 = new Player("Mathias5", PlayerType.HUMAN , PlayerColor.EARTH);
		GameToken token15 = new GameToken(true, player5);
		
		token15.setPlayer(player5);

		
		gameboardController.removePlayerTokens(currentGameBoard, player5);
		if(playerController.getPlayerTokens(gameState, player5).size() != 0) {
			fail("Test failed for Player with 1 Tokens left");
		}
		
		//TestCase for Player with 0 Tokens 
		Player player6 = new Player("Mathias6", PlayerType.HUMAN , PlayerColor.EARTH);
		if(playerController.getPlayerTokens(gameState, player6).size() != 0) {
			fail("Test failed for Player with 0 Tokens left");
		}
		
	}

}
