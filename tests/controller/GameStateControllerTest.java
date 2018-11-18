package controller;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.GameBoard;
import model.GameState;
import model.GameToken;
import model.Onitama;
import model.Player;
import model.Vector;

public class GameStateControllerTest {

	OnitamaController onitamaController;
	
	
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
		
		//gamestateController = onitamaController.getGameStateController();
		//gameboardController = onitamaController.getGameboardController();
		//currentGameState = onitamaController.getOnitama().getCurrentGame().getGameStates().get(onitamaController.getOnitama().getCurrentGame().getGameStates().size() - 1);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGameStateController() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetCurrentGameState() {
		//fail("Not yet implemented");
	}

	@Test
	public void testSetCurrentGameState() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetNextPlayer() {
		try {
			setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//;;if(onitamaController.getOnitama().getCurrentGame().getPlayers().get(0).equals(gamestateController.getNextPlayer(currentGameState)))
		//;;	fail("The next player can't be the player himself // we have no 1-player games here");
		//the best way to test this is just using it ingame and seeing if the rotation fits
		
	}

	@Test
	public void testGenerateNextGameState() {
		//fail("Not yet implemented");
	}

	@Test
	public void testSetNextGameState() {//should be trivial
		//fail("Not yet implemented");
	}

	@Test
	public void testGetWinningPlayers() {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Player> winningPlayers;
		GameState currentGameState;
		GameBoard currentGameBoard;
		
		currentGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();
		currentGameBoard = currentGameState.getGameBoard();
		currentGameBoard.moveTo(currentGameBoard.getTokenAt(new Vector(2, 0)), new Vector(0, 1));
		currentGameBoard.moveTo(currentGameBoard.getTokenAt(new Vector(2, 4)), new Vector(0, -4));
		assert(currentGameBoard == currentGameState.getGameBoard());
		winningPlayers = onitamaController.getGameStateController().getWinningPlayers(currentGameState);
		System.out.println(currentGameBoard.getString());
		if(winningPlayers.isEmpty()){
			fail("we specifically moved a king to one of his winning positions, so atleast one person should have won");
		}
		
		
		int maxMoves;
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		currentGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();
		maxMoves = onitamaController.getOnitama().getCurrentGame().getMaxMoves();
		onitamaController.getOnitama().getCurrentGame().getCurrentGameState().setRoundNumber(maxMoves + 2);
		currentGameBoard = currentGameState.getGameBoard();
		currentGameBoard.moveTo(currentGameBoard.getTokenAt(new Vector(2, 0)), new Vector(0, 1));
		currentGameBoard.moveTo(currentGameBoard.getTokenAt(new Vector(2, 4)), new Vector(0, -4));
		assert(currentGameBoard == currentGameState.getGameBoard());
		winningPlayers = onitamaController.getGameStateController().getWinningPlayers(currentGameState);
		System.out.println(currentGameBoard.getString());
		if(winningPlayers.isEmpty()){
			fail("we specifically moved a king to one of his winning positions, so atleast one person should have won");
		}
	}

	@Test
	public void testGetOnitamaController() {
		//fail("Not yet implemented");
	}

	@Test
	public void testSetOnitamaController() {
		//fail("Not yet implemented");
	}

}
