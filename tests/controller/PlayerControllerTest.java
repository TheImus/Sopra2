/**
 * 
 */
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
import model.GameCard;
import model.GameState;
import model.GameToken;
import model.Onitama;
import model.Player;
import model.PlayerColor;
import model.PlayerType;
import model.Vector;

/**
 * @author Erkan
 *
 */
public class PlayerControllerTest {
	
	
	private Game currentGame;
	private OnitamaController onitamaController;
	private PlayerController playerController;
	private Player player;
	private Player player2;
	private GameState gameState;
	private Onitama onitama;
	private GameBoard gameBoard;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
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
		
		
	
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}


	/**
	 * Test method for {@link controller.PlayerController#PlayerController(controller.OnitamaController)}.
	 */
	@Test
	public void testPlayerController() {
	}

	/**
	 * tests different token count, with and without (multiple) master token
	 * Test method for {@link controller.PlayerController#getPlayerToken(model.Player)}.
	 */
	@Test
	public void testGetPlayerToken() {
		
		List<Player> players = currentGame.getPlayers();
		player = players.get(0);
		player2 = players.get(1);
	
		gameState = currentGame.getCurrentGameState();
		GameBoard gameBoard = gameState.getGameBoard();
		
		
		GameToken master1 = new GameToken(true, player);
		GameToken token1 = new GameToken(false, player);
		GameToken token2 = new GameToken(false, player);
		GameToken token3 = new GameToken(false, player);
		GameToken token4 = new GameToken(false, player);
		
		//überschreibe alle Tokens mit eigenen
		gameBoard.add(master1, new Vector(2,0));
		gameBoard.add(token1, new Vector(0,0));
		gameBoard.add(token2, new Vector(1,0));
		gameBoard.add(token3, new Vector(3,0));
		gameBoard.add(token4, new Vector(4,0));
		

		
		System.out.println(gameBoard.getString());
		
		gameBoard.remove(new Vector(3,0));
		gameBoard.remove(new Vector(4,0));
		
		System.out.println(gameBoard.getString());
	
		//test with 3 tokens (1 master, 2 normal token)
		//System.out.println(playerController.getPlayerTokens(gameState, player).size());
		//System.out.println(playerController.getPlayerTokens(gameState, player2));
		assertTrue(playerController.getPlayerTokens(gameState, player).size() == 3);
		assertTrue(playerController.getPlayerTokens(gameState, player).contains(master1));
		assertTrue(playerController.getPlayerTokens(gameState, player).contains(token1));
		assertTrue(playerController.getPlayerTokens(gameState, player).contains(token2));
		
		gameBoard.add(token3, new Vector(3,0));
		gameBoard.add(token4, new Vector(4,0));
		
		master1.setPlayer(player2);
		token1.setPlayer(player2);
		token2.setPlayer(player2);
		token3.setPlayer(player2);
		token4.setPlayer(player2);
		
		gameBoard.remove(new Vector(2,4));
		gameBoard.remove(new Vector(0,4));
		gameBoard.remove(new Vector(1,4));
		gameBoard.remove(new Vector(3,4));
		gameBoard.remove(new Vector(4,4));
		
		//test with 5 tokens (1 master, 4 normal token)
		//System.out.println(playerController.getPlayerTokens(gameState, player2).size());
		assertTrue(playerController.getPlayerTokens(gameState, player2).size() == 5);
		assertTrue(playerController.getPlayerTokens(gameState, player2).contains(master1));
		assertTrue(playerController.getPlayerTokens(gameState, player2).contains(token1));
		assertTrue(playerController.getPlayerTokens(gameState, player2).contains(token2));
		assertTrue(playerController.getPlayerTokens(gameState, player2).contains(token3));
		assertTrue(playerController.getPlayerTokens(gameState, player2).contains(token4));
		
		//test with more than 5 tokens
		
		GameToken token5 = new GameToken(false, player2);
		gameBoard.add(token5, new Vector(1,4));
		
		//System.out.println(playerController.getPlayerTokens(gameState, player2).size());

		

	}

	/**
	 * Player is alive if his master token is still in the game. So test with one master token and one without a master token 
	 * but with a regular token. (shouldn't be possible)
	 * Test method for {@link controller.PlayerController#isAlive(model.Player)}.
	 */
	@Test
	public void testIsAlive() {		
		List<Player> players = currentGame.getPlayers();
		player = players.get(0);
		player2 = players.get(1);		
		
		
		GameBoard gameBoard = gameState.getGameBoard();
		
		gameBoard.remove(new Vector(2,4));
		gameBoard.remove(new Vector(0,4));
		gameBoard.remove(new Vector(1,4));
		gameBoard.remove(new Vector(3,4));
		gameBoard.remove(new Vector(4,4));
		
		
		System.out.println("alive: ");
		System.out.println(gameBoard.getString());
		
		//assertTrue(playerController.isAlive(gameState, player));
		assertEquals(0,playerController.getPlayerTokens(gameState, player2).size());
		assertFalse(playerController.isAlive(gameState, player2));
		
		
	}
	
	@Test
	public void testGetDiedPlayers(){
		
		
		Game game2 = new Game();
		//GameState gameStateNew = game2.getCurrentGameState();
		GameState gameStateNew = new GameState(gameState);
		GameState gameStateNew2 = new GameState(gameStateNew);
		System.out.println("HIER");
		System.out.println(gameStateNew.getGameBoard().getString());
		System.out.println(gameStateNew2.getGameBoard().getString());
		
		//GameState gameStateNew = new GameState(gameState);
		
		gameStateNew.getGameBoard().remove(new Vector(0,0));
		gameStateNew.getGameBoard().remove(new Vector(1,0));
		gameStateNew.getGameBoard().remove(new Vector(2,0));
		gameStateNew.getGameBoard().remove(new Vector(3,0));
		gameStateNew.getGameBoard().remove(new Vector(4,0));
		List<Player> players = onitamaController.getOnitama().getCurrentGame().getPlayers();
		player = players.get(0);
		player2 = players.get(1);
		
		
		System.out.println(playerController.getDiedPlayers(gameState, gameStateNew).size());
		
		assertTrue(playerController.getDiedPlayers(gameState, gameStateNew).size() == 1);
		assertTrue(playerController.getDiedPlayers(gameState, gameStateNew).contains(player));
	}

}
