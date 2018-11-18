package model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameTest {
	private Game defaultGame;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		 defaultGame = new Game();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPrint() {
		System.out.println("Default game:");
		System.out.println(defaultGame.getGameStates().get(0).getGameBoard().getString());
		assertTrue(true);
	}
	
	@Test
	public void addPlayer() {
		System.out.println("Default game with yellow:");
		List<Player> players = defaultGame.getPlayers();
		
		Player yellow = new Player("Yellow Player", PlayerType.HUMAN, PlayerColor.AIR);
		players.add(yellow);
		defaultGame.setPlayers(players);
		System.out.println(defaultGame.getGameStates().get(0).getGameBoard().getString());
	}
	
	
	@Test
	public void resizeGameBoard() {
		System.out.println("Default Game with 7x7 board and 4 players:");
		List<Player> players = defaultGame.getPlayers();
		
		players.add(new Player("Yellow Player", PlayerType.HUMAN, PlayerColor.AIR));
		players.add(new Player("Purple Player", PlayerType.HUMAN, PlayerColor.WATER));
		//defaultGame.setPlayers(players);
		defaultGame.setBoardSize(7);
		System.out.println(defaultGame.getGameStates().get(0).getGameBoard().getString());
	}

}
