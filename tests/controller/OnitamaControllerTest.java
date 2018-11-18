/**
 * 
 */
package controller;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.Game;
import model.Onitama;
import controller.OnitamaController;


/**
 * @author sopr027
 *
 */
public class OnitamaControllerTest {
	
	//OnitamaController onitamaController;
	Onitama onitama;
	Game game;

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
		onitama = TestDataFactory.createSampleFourPlayerGameOnitama();
		game = TestDataFactory.createSampleFourPlayerGame();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSaveGame(){
		/*OnitamaController.saveGame(game, 1);
		Game loadedGame = OnitamaController.loadGame(1);
		
		String gameName = game.getName();
		String loadedGameName = loadedGame.getName();
		
		assert(gameName.equals(loadedGameName)); 
		*/
	}
	
	@Test 
	public void testLoadGame(){
		
	}
	
	
}
