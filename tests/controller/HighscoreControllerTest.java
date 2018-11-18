/**
 * 
 */
package controller;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.*;

/**
 * @author sopr026
 *
 */
public class HighscoreControllerTest {

	OnitamaController onitamaController;
	
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
		Onitama onitama = new Onitama();
		//onitama.setCurrentGame(TestDataFactory.createSampleTwoPlayerWonGame());
		onitamaController.setOnitama(onitama);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link controller.HighscoreController#getOnitamaController()}.
	 */
	@Test
	public void testGetOnitamaController() {
		HighscoreController highscoreController = onitamaController.getHighscoreController();
		assertEquals(onitamaController, highscoreController.getOnitamaController());
	}

	/**
	 * Test method for {@link controller.HighscoreController#HighscoreController(controller.OnitamaController)}.
	 */
	@Test
	public void testHighscoreController() {
		HighscoreController newHighscoreController = new HighscoreController(onitamaController);
		assertEquals(onitamaController, newHighscoreController.getOnitamaController());
	}

	/**
	 * Test method for {@link controller.HighscoreController#addToHighscore(model.Game)}.
	 */
	@Test
	public void testAddToHighscore() {
		HighscoreController highscoreController = new HighscoreController(onitamaController);
		Game game = TestDataFactory.createSampleTwoPlayerWonGame();
		game.setHighscoreEnabled(false);
		highscoreController.addToHighscore(game);
		List<HighScoreEntry> emptyHighscoreList = highscoreController.getTwoPlayerHighScoreList();
		assert(emptyHighscoreList.isEmpty());
		highscoreController.addToHighscore(TestDataFactory.createSampleTwoPlayerWonGame());
		List<HighScoreEntry> highscoreList = highscoreController.getTwoPlayerHighScoreList();
		assert(!highscoreList.isEmpty());
		assertEquals(highscoreList.size(), 1);	
	}

	/**
	 * Test method for {@link controller.HighscoreController#getTwoPlayerHighScoreList()}.
	 */
	@Test
	public void testGetTwoPlayerHighScoreList() {
		HighscoreController highscoreController = new HighscoreController(onitamaController);
		assert(highscoreController.getTwoPlayerHighScoreList()!= null);
		Game game = TestDataFactory.createSampleTwoPlayerWonGame();
		System.out.println("Spielertypen");

		System.out.println(game.getPlayers().get(0).getPlayerType());
		System.out.println(game.getPlayers().get(1).getPlayerType());
		
		highscoreController.addToHighscore(game);

		List<HighScoreEntry> highscoreList = highscoreController.getTwoPlayerHighScoreList();
		assert(!highscoreList.isEmpty());
		System.out.println("//////////////////////////////////////////");
		HighScoreEntry firstEntry = highscoreList.get(0);
		System.out.println(firstEntry.getBeatenMaster());
		
		assertEquals(highscoreList.size(), 2);

		//HighScoreEntry secondEntry = highscoreList.get(1);
		//assertEquals(firstEntry.getBeatenTokens(), secondEntry.getBeatenTokens());
		assertEquals(firstEntry.getBeatenTokens(), 0);
		assertEquals(firstEntry.getBeatenMaster(), 1);
		assertEquals(firstEntry.getGameDuration(), 4);
		assertEquals(firstEntry.getGameMoves(), 2);
	}

	/**
	 * Test method for {@link controller.HighscoreController#getFourPlayerHighScoreList()}.
	 */
	@Test
	public void testGetFourPlayerHighScoreList() {
		HighscoreController highscoreController = new HighscoreController(onitamaController);
		assertNotEquals(highscoreController.getFourPlayerHighScoreList(), null);
	}

	/**
	 * Test method for {@link controller.HighscoreController#getHighScoreList()}.
	 */
	@Test
	public void testGetHighScoreList() {
		HighscoreController highscoreController = new HighscoreController(onitamaController);
		assert(highscoreController.getHighScoreList()!= null);
		assertNotEquals(highscoreController.getTwoPlayerHighScoreList(), null);
		assertNotEquals(highscoreController.getFourPlayerHighScoreList(), null);
	}

	/**
	 * Test method for {@link controller.HighscoreController#loadHighscore()}.
	 
	@Test
	public void testLoadHighscore() {
		Game game = TestDataFactory.createInconsistentFourPlayerWinnableGame();
		System.out.println(game.getCurrentGameState().getGameBoard().getString());
		System.out.println(game.getCurrentGameState().getCurrentPlayer().getPlayerColor());
	}
*/
	/**
	 * Test method for {@link controller.HighscoreController#resetHighscore(int)}.
	 */
	@Test
	public void testResetHighscoreInt() {
		HighscoreController highscoreController = new HighscoreController(onitamaController);
		highscoreController.addToHighscore(TestDataFactory.createSampleTwoPlayerWonGame());
		List<HighScoreEntry> highscoreList = highscoreController.getTwoPlayerHighScoreList();
		highscoreController.resetHighscore(2);
		assertNotEquals(highscoreList, highscoreController.getTwoPlayerHighScoreList());
		
	}

	/**
	 * Test method for {@link controller.HighscoreController#resetHighscore()}.
	 */
	@Test
	public void testResetHighscore() {
		HighscoreController highscoreController = new HighscoreController(onitamaController);
		highscoreController.addToHighscore(TestDataFactory.createSampleTwoPlayerWonGame());
		List<HighScoreEntry> highscoreList = highscoreController.getTwoPlayerHighScoreList();
		highscoreController.resetHighscore();
		assertNotEquals(highscoreList, highscoreController.getTwoPlayerHighScoreList());
	}

}
