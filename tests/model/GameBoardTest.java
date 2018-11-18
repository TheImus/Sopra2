package model;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author Fabian Kemper
 *
 */
public class GameBoardTest {
	private static final int TOKEN_COUNT = 5;
	private static final int GAME_BOARD_SIZE = 5;
	private GameBoard gameBoard;
	private Player redPlayer;
	private Player bluePlayer;
	private GameToken redTokens[];
	private GameToken blueTokens[];
	
	

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
		this.gameBoard = new GameBoard(GAME_BOARD_SIZE, false);
		this.redPlayer  = new Player("earth", PlayerType.HUMAN, PlayerColor.EARTH); // 0 degrees
		this.bluePlayer = new Player("fire",  PlayerType.HUMAN, PlayerColor.FIRE); // 180 degrees
		
		redTokens  = new GameToken[TOKEN_COUNT];
		blueTokens = new GameToken[TOKEN_COUNT];
		
		// add two tokens
		for (int i = 0; i < TOKEN_COUNT; i++) {
			GameToken redToken  = new GameToken(i == TOKEN_COUNT/2, redPlayer);
			GameToken blueToken = new GameToken(i == TOKEN_COUNT/2, bluePlayer);
			
			redTokens[i] = redToken;
			blueTokens[i] = blueToken;
			
			gameBoard.add(redToken, new Vector(i, GAME_BOARD_SIZE-1));
			gameBoard.add(blueToken, new Vector(i, 0));
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void leaveBoardTest() {
		try {
			GameToken targetToken = gameBoard.moveTo(blueTokens[0], new Vector(-1, -1));
			assertEquals(null, targetToken);
			fail("No exception thrown!");
		} catch (IndexOutOfBoundsException e) {
			// Ok
		} catch (Exception e) {
			fail("Excpected IndexOutOfBoundsException");
		}
	}
	
	@Test
	public void moveTokenTest() {//TODO
		System.out.println("---------");
		printGameBoard();
		GameToken token = gameBoard.getTokenAt(new Vector(0, 0));
		gameBoard.moveTo(token, new Vector(0, 1));//move down by one
		printGameBoard();
		gameBoard.moveTo(token, new Vector(1, 0));//move right by one
		printGameBoard();
		gameBoard.moveTo(token, new Vector(0, -1));//move left by one
		printGameBoard();
		/*boolean wasOutOfBoundsThrown = false;
		try{
			gameBoard.moveTo(token, new Vector(-1, -1));//move left + up -> Out of Bounds
		}catch(ArrayIndexOutOfBoundsException e){
			wasOutOfBoundsThrown = true;
		}
		if(!wasOutOfBoundsThrown) fail("We moved out of the board a no error was thrown");*/
		System.out.println("---------");
	}
	
	@Test
	public void printGameBoard() {
		System.out.println(gameBoard.getString());
	}

}
