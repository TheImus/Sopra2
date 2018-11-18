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

import static org.junit.Assert.assertNotNull;

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

public class AIControllerTest {
	
	private OnitamaController onitamaController;
	private NewGameController newGameController;
	private AIController aiController1;
	private AIController aiController2;
	private GameStateController gameStateController;
	

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
		//System.out.println("winnable: /n" + TestDataFactory.createSampleTwoPlayerGameWinableInNextMove().getCurrentGameState().getGameBoard().getString());
		//System.out.println(onitama.getCurrentGame().getCurrentGameState().getGameBoard().getString());
		aiController1 = new NoviceAIController(onitamaController);
		aiController2 = new NoviceAIController(onitamaController);
		gameStateController = onitamaController.getGameStateController();
		onitamaController.getNewGameController().createDefaultGame();
		onitamaController.getOnitama().getCurrentGame().getCurrentGameState().setCurrentPlayer(onitamaController.getOnitama().getCurrentGame().getCurrentGameState().getPlayers().get(0));
	}

	@After
	public void tearDown() throws Exception {
	}

	
	public static int MAXMOVES = 60;
	@Test
	public void testAI() {
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
		
		for(int i=0;i<MAXMOVES;i++){
			System.out.println("--------------------------------------------");
			System.out.println("zug: " + (i+1));
			GameState currentGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();
			GameMove nextMove = null;
			try{
				if(i % 2 == 0){//player1´
					aiController1.setOnitamaController(onitamaController);
					nextMove = aiController1.getNextMove(currentGameState);
					System.out.println("ai-1 moves");
				}else{//player2
					aiController2.setOnitamaController(onitamaController);
					nextMove = aiController2.getNextMove(currentGameState);
					System.out.println("ai-2 moves");
				}
			}catch(IllegalStateException e){
				//someone probably won
			}catch(Exception e){
				System.err.println(e.toString());
				System.err.println("oops");
			}
			if(nextMove == null){
				System.err.println("Player-" + ((i%2)+1) + " lost.");
				break;
			}
			GameState nextGameState = gameStateController.generateNextGameState(currentGameState, nextMove);
			List<GameState> gameStates = onitamaController.getOnitama().getCurrentGame().getGameStates();
			gameStates.add(nextGameState);
			onitamaController.getOnitama().getCurrentGame().setGameStates(gameStates);
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

}
