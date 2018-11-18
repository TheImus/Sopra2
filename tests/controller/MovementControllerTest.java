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

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import model.*;
import model.Vector;

/**
 * @author Erkan
 *
 */
public class MovementControllerTest {
	
	
	private MovementController movementController;
	private Game currentGame;
	private OnitamaController onitamaController;
	private PlayerController playerController;
	private Player player;
	private Player player2;
	private GameState gameState;
	private Onitama onitama;
	private GameBoard gameBoard;
	private final BiPredicate<GameMove, GameMove> equals=(gameMove1,gameMove2) -> {if (gameMove1.getMovedToken()==gameMove2.getMovedToken() 
			&& gameMove1.getSelectedCard()==gameMove2.getSelectedCard() 
			&& gameMove1.getSelectedMove().getX()==gameMove2.getSelectedMove().getX() 
			&& gameMove1.getSelectedMove().getY()==gameMove2.getSelectedMove().getY()){return true;} else{return false;}}; 
	private final BiPredicate<List<GameMove>, GameMove> contain=(possibleMovementsList,gamemove) -> {for(GameMove possibleGameMove : possibleMovementsList)
	{if(equals.test(gamemove, possibleGameMove)){return true;}
	} return false;};

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
		onitama.setCurrentGame(currentGame);
		onitamaController.setOnitama(onitama);
		playerController = onitamaController.getPlayerController();
		movementController = onitamaController.getMovementController();
		gameState = currentGame.getCurrentGameState();
	
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 *Check if all GameMoves for all tokens with all cards are in the list
	 * Test method for {@link controller.MovementController#getPossibleMovements(model.Player)}.
	 */
	@Test
	public void testGetPossibleMovementsPlayer() {
		
		Map<Player, List<GameCard>> cardDistribution = new HashMap<Player, List<GameCard>>();
		List<GameCard> gameCards = new ArrayList<GameCard>();
		List<GameCard> gameCards2 = new ArrayList<GameCard>();
		List<GameMove> gameMoves = new ArrayList<GameMove>();
		
		gameBoard =gameState.getGameBoard();
		
		List<Player> players = currentGame.getPlayers();
		player = players.get(0);
		player2 = players.get(1);
		
		gameBoard.remove(new Vector(0,4));
		gameBoard.remove(new Vector(3,4));
		gameBoard.remove(new Vector(4,4));
		
		GameToken tokenDefeated = new GameToken(false, player2);
		gameBoard.add(tokenDefeated, new Vector(1,1));
		
		
		
	
		gameBoard.remove(new Vector(0,0));
		gameBoard.remove(new Vector(3,0));
		gameBoard.remove(new Vector(4,0));
		
		gameCards.add(GameCard.TIGER);
		gameCards.add(GameCard.CRAB);
		gameCards2.add(GameCard.BOAR);
		gameCards2.add(GameCard.FROG);
		cardDistribution.put(player, gameCards);
		cardDistribution.put(player2, gameCards2);
		
		/*
		//currentGame.setInitialCardDistribution(cardDistribution);
		 * 
		 */
		gameState.setCardDistribution(cardDistribution);
		System.out.println(player);
	
		//add all moves with Tiger card
//		gameMoves.add(new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.TIGER, new Vector( 0, -2)));
//		gameMoves.add(new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.TIGER, new Vector( 0, -2)));
//		gameMoves.add(new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.TIGER, new Vector( 0,  1)));
//		GameMove gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.TIGER, new Vector( 0, 1));
// 		gameMoveRegular.setDefeatedToken(gameBoard.getTokenAt(new Vector(1,1)));
//		gameMoves.add(gameMoveRegular);
		
		GameMove gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.TIGER, new Vector( 0, -2));
		GameMove gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.TIGER, new Vector( 0, -2));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.TIGER, new Vector( 0,  1));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.TIGER, new Vector( 0, 1));
		gameMoveRegular.setDefeatedToken(gameBoard.getTokenAt(new Vector(1,1)));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);

		
//		gameMoves.add(new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector( 0, -1)));
//		gameMoves.add(new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector( 0, -1)));
//		gameMoves.add(new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector(-2,  0)));
//		gameMoves.add(new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector(-2,  0)));
//		gameMoves.add(new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector( 2,  0)));
//		gameMoves.add(new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector( 2,  0)));
		
		//add all moves with Crab card
		
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector( 0, -1));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector( 0, -1));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector(-2,  0));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector(-2,  0));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector( 2,  0));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector( 2,  0));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		
		
		System.out.println(gameBoard.getString());
		//check assert for each move -> 10 moves total
		
		System.out.println(gameState);
		System.out.println(player);
		System.out.println(movementController);
		System.out.println(gameMoves.size());
				
		
		
		for(int i = 0; i < gameMoves.size(); i++)
		{
			System.out.println(gameMoves.get(i).getSelectedCard());
		}
		
		System.out.println("/////////");
		List<GameMove> possibleMovements=movementController.getPossibleMovements(gameState, player);
		for(int i = 0; i < possibleMovements.size(); i++)
		{
			System.out.println(possibleMovements.get(i).getSelectedCard());
		}
		
		for(int i = 0; i < gameMoves.size(); i++)
		{
			if(movementController.isValidGameMove(gameState,gameMoves.get(i)))
			{
			  assertTrue(contain.test(possibleMovements, gameMoves.get(i)));
			}
		}
	
	}

	/**
	 * Check if all GameMoves for one token with all cards are in the list
	 * Test method for {@link controller.MovementController#getPossibleMovements(model.GameToken)}.
	 */
	@Test
	public void testGetPossibleMovementsGameToken() {

		Map<Player, List<GameCard>> cardDistribution = new HashMap<Player, List<GameCard>>();
		List<GameCard> gameCards = new ArrayList<GameCard>();
		List<GameCard> gameCards2 = new ArrayList<GameCard>();
		List<GameMove> gameMoves = new ArrayList<GameMove>();
		
		gameBoard = gameState.getGameBoard();
		
		List<Player> players = currentGame.getPlayers();
		player = players.get(0);
		player2 = players.get(1);
		
		gameBoard.remove(new Vector(0,4));
		gameBoard.remove(new Vector(3,4));
		gameBoard.remove(new Vector(4,4));
		
		GameToken tokenDefeated = new GameToken(false, player2);
		gameBoard.add(tokenDefeated, new Vector(1,1));
		
		
		
	
		gameBoard.remove(new Vector(0,0));
		gameBoard.remove(new Vector(3,0));
		gameBoard.remove(new Vector(4,0));
		
		gameCards.add(GameCard.TIGER);
		gameCards.add(GameCard.CRAB);
		gameCards2.add(GameCard.BOAR);
		gameCards2.add(GameCard.FROG);
		cardDistribution.put(player, gameCards);
		cardDistribution.put(player2, gameCards2);
		
		//currentGame.setInitialCardDistribution(cardDistribution);
		gameState.setCardDistribution(cardDistribution);
		
		System.out.println(player);
	
		//add all moves with Tiger card
		GameMove gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.TIGER, new Vector( 0, -2));
		//GameMove gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.TIGER, new Vector( 0, -2));
		gameMoves.add(gameMoveMaster);
		//gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.TIGER, new Vector( 0,  1));
		//gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.TIGER, new Vector( 0, 1));
		//gameMoveRegular.setDefeatedToken(gameBoard.getTokenAt(new Vector(1,1)));
		gameMoves.add(gameMoveMaster);
		//gameMoves.add(gameMoveRegular);
		
		//add all moves with Crab card
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector( 0, -1));
		//gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector( 0, -1));
		gameMoves.add(gameMoveMaster);
		//gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector(-2,  0));
		//gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector(-2,  0));
		gameMoves.add(gameMoveMaster);
		//gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector( 2,  0));
		//gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector( 2,  0));
		gameMoves.add(gameMoveMaster);
		//gameMoves.add(gameMoveRegular);
		
		System.out.println(gameBoard.getString());
		
		
		System.out.println(gameState);
		System.out.println(player);
		System.out.println(movementController);
		System.out.println(gameMoves.size());
				
		for(int i = 0; i < gameMoves.size(); i++)
		{
			System.out.println(gameMoves.get(i).getSelectedCard());
		}
		List<GameMove> possibleMovements=movementController.getPossibleMovements(gameState, gameBoard.getTokenAt(new Vector(2, 0)));
		//check assert for one token with all cards -> 5 moves total
		for(int i = 0; i < gameMoves.size(); i++)
		{
			if(movementController.isValidGameMove(gameState,gameMoves.get(i)))
			{
			  assertTrue(contain.test(possibleMovements, gameMoves.get(i)));
			}
		}
		
	}

	/**
	 * Check if all GameMoves for all tokens with one card are in the list
	 * Test method for {@link controller.MovementController#getPossibleMovements(model.GameCard)}.
	 */
	@Test
	public void testGetPossibleMovementsGameCard() {

		Map<Player, List<GameCard>> cardDistribution = new HashMap<Player, List<GameCard>>();
		List<GameCard> gameCards = new ArrayList<GameCard>();
		List<GameCard> gameCards2 = new ArrayList<GameCard>();
		List<GameMove> gameMoves = new ArrayList<GameMove>();
		
		gameBoard = gameState.getGameBoard();
		
		List<Player> players = currentGame.getPlayers();
		player = players.get(0);
		player2 = players.get(1);
		
		gameBoard.remove(new Vector(0,4));
		gameBoard.remove(new Vector(3,4));
		gameBoard.remove(new Vector(4,4));
		
		GameToken tokenDefeated = new GameToken(false, player2);
		gameBoard.add(tokenDefeated, new Vector(1,1));
		
		
		
	
		gameBoard.remove(new Vector(0,0));
		gameBoard.remove(new Vector(3,0));
		gameBoard.remove(new Vector(4,0));
		
		gameCards.add(GameCard.TIGER);
		gameCards.add(GameCard.CRAB);
		gameCards2.add(GameCard.BOAR);
		gameCards2.add(GameCard.FROG);
		cardDistribution.put(player, gameCards);
		cardDistribution.put(player2, gameCards2);
		
		//currentGame.setInitialCardDistribution(cardDistribution);
		gameState.setCardDistribution(cardDistribution);
		
		System.out.println(player);
	
		//add all moves with Tiger card
		GameMove gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.TIGER, new Vector( 0, -2));
		GameMove gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.TIGER, new Vector( 0, -2));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.TIGER, new Vector( 0,  1));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.TIGER, new Vector( 0, 1));
		gameMoveRegular.setDefeatedToken(gameBoard.getTokenAt(new Vector(1,1)));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		
		//add all moves with Crab card
		/*gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector( 0, -1));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector( 0, -1));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector(-2,  0));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector(-2,  0));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector( 2,  0));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector( 2,  0));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		*/
		System.out.println(gameBoard.getString());
		
		
		System.out.println(gameState);
		System.out.println(player);
		System.out.println(movementController);
		System.out.println(gameMoves.size());
				
		
		
		for(int i = 0; i < gameMoves.size(); i++)
		{
			System.out.println(gameMoves.get(i).getSelectedCard());
		}
	
		List<GameMove> possibleMovements=movementController.getPossibleMovements(gameState, GameCard.TIGER);
		//check assert for each token with one card -> 4 moves total
		for(int i = 0; i < gameMoves.size(); i++)
		{
			if(movementController.isValidGameMove(gameState,gameMoves.get(i)))
			{
			  assertTrue(contain.test(possibleMovements, gameMoves.get(i)));
			}
		}
	}

	/**
	 * test if GameMove is actually possible (no out of bounds, no allied token on target position)
	 * Test method for {@link controller.MovementController#isValidGameMove(model.GameMove)}.
	 */
	@Test
	public void testIsValidGameMove() {
	
		
		Map<Player, List<GameCard>> cardDistribution = new HashMap<Player, List<GameCard>>();
		List<GameCard> gameCards = new ArrayList<GameCard>();
		List<GameCard> gameCards2 = new ArrayList<GameCard>();
		List<GameMove> gameMoves = new ArrayList<GameMove>();
		/*
		//gameBoard = currentGame.getCurrentGameState().getGameBoard();
		 * 
		 */
		gameBoard=gameState.getGameBoard();
		
		List<Player> players = currentGame.getPlayers();
		player = players.get(0);
		player2 = players.get(1);
		
		gameBoard.remove(new Vector(0,4));
		gameBoard.remove(new Vector(3,4));
		gameBoard.remove(new Vector(4,4));
		
		GameToken tokenDefeated = new GameToken(false, player2);
		GameToken token = new GameToken(false, player2);
		gameBoard.add(tokenDefeated, new Vector(1,1));
		gameBoard.add(token, new Vector(0,4));
		
		
		
	
		gameBoard.remove(new Vector(0,0));
		gameBoard.remove(new Vector(3,0));
		gameBoard.remove(new Vector(4,0));
		
		GameMove gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,1)), GameCard.TIGER, new Vector( 0, -2));
		GameMove gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,4)), GameCard.TIGER, new Vector( 0, -2));
		System.out.println(gameBoard.getString());
		
		
		gameCards.add(GameCard.TIGER);
		gameCards.add(GameCard.CRAB);
		gameCards2.add(GameCard.BOAR);
		gameCards2.add(GameCard.FROG);
		cardDistribution.put(player2, gameCards);
		cardDistribution.put(player, gameCards2);
		
		/*
		//currentGame.setInitialCardDistribution(cardDistribution);
		 * 
		 */
		gameState.setCardDistribution(cardDistribution);
		
		//GameMove gameMoveMaster = new GameMove(master1, GameCard.TIGER, new Vector( 0, -2));
		//GameMove gameMoveRegular = new GameMove(token1, GameCard.TIGER, new Vector(0,-2));
		// the selected card is not belong to the player, who possess the token
		
		//test moving token2 on free spot
		assertTrue(movementController.isValidGameMove(gameState, gameMoveMaster));
		// the selected card is not belong to the player, who possess the token
		gameMoveMaster.setSelectedCard(GameCard.ROOSTER);
		assertFalse(movementController.isValidGameMove(gameState, gameMoveMaster));
		//test moving master token on allied token
		gameMoveMaster=new GameMove(gameBoard.getTokenAt(new Vector(2, 4)),GameCard.CRAB , new Vector(-2, 0));
		System.out.println(gameState.getGameBoard().getString());
		assertFalse(movementController.isValidGameMove(gameState, gameMoveMaster));
		
		//test moving token out of bounds
		
		assertFalse(movementController.isValidGameMove(gameState, gameMoveRegular));
		
		
		
	}

	/**
	 * test possible movements with selected game card and selected game token
	 * Test method for {@link controller.MovementController#getPossibleMovements(model.GameCard, model.GameToken)}.
	 */
	@Test
	public void testGetPossibleMovementsGameCardGameToken() {

		Map<Player, List<GameCard>> cardDistribution = new HashMap<Player, List<GameCard>>();
		List<GameCard> gameCards = new ArrayList<GameCard>();
		List<GameCard> gameCards2 = new ArrayList<GameCard>();
		List<GameMove> gameMoves = new ArrayList<GameMove>();
		
		gameBoard = gameState.getGameBoard();
		
		List<Player> players = currentGame.getPlayers();
		player = players.get(0);
		player2 = players.get(1);
		
		gameBoard.remove(new Vector(0,4));
		gameBoard.remove(new Vector(3,4));
		gameBoard.remove(new Vector(4,4));
		
		GameToken tokenDefeated = new GameToken(false, player2);
		gameBoard.add(tokenDefeated, new Vector(1,1));
		
		
		
	
		gameBoard.remove(new Vector(0,0));
		gameBoard.remove(new Vector(3,0));
		gameBoard.remove(new Vector(4,0));
		
		gameCards.add(GameCard.TIGER);
		gameCards.add(GameCard.CRAB);
		gameCards2.add(GameCard.BOAR);
		gameCards2.add(GameCard.FROG);
		cardDistribution.put(player, gameCards);
		cardDistribution.put(player2, gameCards2);
		/*
		//currentGame.setInitialCardDistribution(cardDistribution);
		 * 
		 */
		gameState.setCardDistribution(cardDistribution);
		
		System.out.println(player);
	
		//add all moves with Tiger card
		GameMove gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.TIGER, new Vector( 0, -2));
		//GameMove gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.TIGER, new Vector( 0, -2));
		gameMoves.add(gameMoveMaster);
		//gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.TIGER, new Vector( 0,  1));
		//gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.TIGER, new Vector( 0, 1));
		//gameMoveRegular.setDefeatedToken(gameBoard.getTokenAt(new Vector(1,1)));
		gameMoves.add(gameMoveMaster);
		//gameMoves.add(gameMoveRegular);
		
		//add all moves with Crab card
		
		/*gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector( 0, -1));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector( 0, -1));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector(-2,  0));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector(-2,  0));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		gameMoveMaster = new GameMove(gameBoard.getTokenAt(new Vector(2,0)), GameCard.CRAB, new Vector( 2,  0));
		gameMoveRegular = new GameMove(gameBoard.getTokenAt(new Vector(1,0)), GameCard.CRAB, new Vector( 2,  0));
		gameMoves.add(gameMoveMaster);
		gameMoves.add(gameMoveRegular);
		*/
		System.out.println(gameBoard.getString());
		
		
		System.out.println(gameState);
		System.out.println(player);
		System.out.println(movementController);
		System.out.println(gameMoves.size());
				
		
		
		for(int i = 0; i < gameMoves.size(); i++)
		{
			System.out.println(gameMoves.get(i).getSelectedCard());
		}
		List<GameMove> possibleMovements=movementController.getPossibleMovements(gameState, GameCard.TIGER,gameBoard.getTokenAt(new Vector(2, 0)));
		System.out.println("/////////");
		for(int i = 0; i < possibleMovements.size(); i++)
		{
			System.out.println(possibleMovements.get(i).getSelectedCard());
		}
		
		//check assert for each move -> 2 moves total
		for(int i = 0; i < gameMoves.size(); i++)
		{
			if(movementController.isValidGameMove(gameState,gameMoves.get(i)))
			{
			  assertTrue(contain.test(possibleMovements, gameMoves.get(i)));
			}
		}
		

	}
}
