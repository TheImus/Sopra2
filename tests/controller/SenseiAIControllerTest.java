package controller;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.Game;
import model.GameCard;
import model.GameMove;
import model.GameState;
import model.Onitama;
import model.Player;
import model.PlayerColor;

public class SenseiAIControllerTest {
	
	private OnitamaController onitamaController;
	private NewGameController newGameController;
	private ZenMasterAIController aiController;
	private GameStateController gameStateController;
	private NoviceAIController novice;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Onitama onitama = new Onitama();
		onitamaController = new OnitamaController();
		onitamaController.setOnitama(onitama);	
		newGameController = onitamaController.getNewGameController();
		newGameController.createDefaultGame();
		//onitama.setCurrentGame(TestDataFactory.createSampleTwoPlayerGameWinableInNextMove());
		//System.out.println("winnable: ");
		//System.out.println(TestDataFactory.createSampleTwoPlayerGameWinableInNextMove().getCurrentGameState().getGameBoard().getString());
		//System.out.println(onitama.getCurrentGame().getCurrentGameState().getGameBoard().getString());
		aiController = new ZenMasterAIController(onitamaController);
		novice = new NoviceAIController(onitamaController);
		aiController.setOnitamaController(onitamaController);
		gameStateController = onitamaController.getGameStateController();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetNextMove() {
		Game currentGame = onitamaController.getOnitama().getCurrentGame();
		Map<Player,List<GameCard>> map = currentGame.getInitialCardDistribution();
		for(Player p:currentGame.getPlayers()){
			for(GameCard card:map.get(p)){
				System.out.println(card.toString() + "  ");
			}
			System.out.println("");
		}
		System.out.println("freie Karte: " + currentGame.getInitialFreeCard().toString());
		System.out.println("");
		
		
		System.out.println(onitamaController.getOnitama().getCurrentGame().getCurrentGameState().getGameBoard().getString());
		for(int i=0;i<60;i++){
			System.out.println("zug: " + (i+1));
			GameState currentGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();
			//System.out.println(currentGameState.getGameBoard().getString());
			GameMove nextMove = null;
			if(currentGameState.getCurrentPlayer().getPlayerColor().equals(PlayerColor.FIRE) ) {
				nextMove = aiController.getNextMoveTwoForward(currentGameState);
				System.out.println("fire player");
			}
			else {
				System.out.println("earth player bei zug: " + (i+1));
				nextMove = aiController.getNextMoveTwoForward(currentGameState);
			}
			assertNotNull(onitamaController.getOnitama().getCurrentGame());
			assertNotNull(onitamaController.getOnitama().getCurrentGame().getGameStates());
			assertNotNull(gameStateController);
			onitamaController.getOnitama().getCurrentGame().getGameStates().add(gameStateController.generateNextGameState(currentGameState, nextMove));
			System.out.println(onitamaController.getOnitama().getCurrentGame().getCurrentGameState().getGameBoard().getString());
			
			currentGame = onitamaController.getOnitama().getCurrentGame();
			map = currentGame.getCurrentGameState().getCardDistribution();
			for(Player p:currentGame.getPlayers()){
				for(GameCard card:map.get(p)){
					System.out.println(card.toString() + "  ");
				}
				System.out.println("");
			}
			System.out.println("freie Karte: " + currentGame.getCurrentGameState().getFreeGameCard().toString());
			System.out.println("");
		}
		
	}

	@Test
	public void testGetNextMoveDeeper() {
	}

	@Test
	public void testGetValueInTwoMoves() {
	}

}
