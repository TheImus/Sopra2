package controller;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.Game;
import model.GameCard;
import model.GameState;
import model.Onitama;
import model.Player;
import model.PlayerColor;
import model.PlayerType;
import model.Vector;

public class NewGameControllerTest {
	private Game currentGame;
	private OnitamaController onitamaController;
	private NewGameController newGameController;

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
		
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * test, if currentGame is set to a game with two players and a valid initial card distribution
	 */
	@Test
	public void testCreateDefaultGame() {
		newGameController.createDefaultGame();
		Game currentGame = onitamaController.getOnitama().getCurrentGame();
		assertEquals(2,currentGame.getPlayers().size());
		assertNotNull(currentGame.getInitialFreeCard());
		Map<Player,List<GameCard>> map = currentGame.getInitialCardDistribution();
		for(Player p:currentGame.getPlayers()){
			assertEquals(2,map.get(p).size());
		}
		assertEquals(5,currentGame.getBoardSize());
	}

	/**
	 * test with all possible combinations of player color and player type:
	 * if game already contains player with given color: test if attributes are changed
	 * else: check if player is added
	 */
	@Test
	public void testSetPlayer() {
		newGameController.createDefaultGame();
		Game currentGame = onitamaController.getOnitama().getCurrentGame();
		String name = "Player1";
		PlayerType[] playertypes = PlayerType.values();
		PlayerColor[] playercolors = PlayerColor.values();
		for(PlayerType pt: playertypes){
			for(PlayerColor pc: playercolors){
				PlayerType type = pt;
				PlayerColor color = pc;
				int size = currentGame.getPlayers().size();
				boolean containsColor = false;					//check if game already contains player with given color
				for(Player p:currentGame.getPlayers()){
					if(p.getPlayerColor().equals(color)){
						containsColor = true;
					}
				}
				
				newGameController.addPlayer(name, type, color);			
				
				if(containsColor){
					assertEquals(size,currentGame.getPlayers().size());		//check if no player was removed
				}else{
					containsColor = false;					
					for(Player p:currentGame.getPlayers()){					//if game didn´t contain player with given color, check if playercolor was added
						if(p.getPlayerColor().equals(color)){
							containsColor = true;
						}
					}
					assertEquals(size+1,currentGame.getPlayers().size());
					assertTrue(containsColor);
				}
				for(Player p:currentGame.getPlayers()){
					if(p.getPlayerColor().equals(color)){
						assertEquals("Player1",p.getName());				//test if attributes are set correctly
						assertEquals(pt,p.getPlayerType());
					}
				}
			}
		}		
	}

	@Test
	public void testLoadConfiguration() {
		File file = new File("/sopra/sopgr02/sopr022/git/project2/SampleData/BeispielStartKonfig.txt");
		try {
			onitamaController.getNewGameController().createDefaultGame();
			Game currentGame = onitamaController.getOnitama().getCurrentGame();
			List<Player> players = currentGame.getPlayers();
			assert(players.size() == 2);
			newGameController.loadConfiguration(file, players.get(0), players.get(1));
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Game currentGame = onitamaController.getOnitama().getCurrentGame();
		GameState currentGameState = currentGame.getGameStates().get(0);
		Map<Player,List<GameCard>> map = currentGameState.getCardDistribution();
		Player p1 = currentGame.getPlayers().get(0);
		assertEquals(map.get(p1).get(0),GameCard.BOAR);
		assertEquals(map.get(p1).get(1),GameCard.CRAB);
		
		Player p2 = currentGame.getPlayers().get(1);
		assertEquals(map.get(p2).get(0),GameCard.CRANE);
		assertEquals(map.get(p2).get(1),GameCard.TIGER);
		
		assertEquals(currentGameState.getFreeGameCard(),GameCard.COBRA);
	}
	
	
	/**
	 * testcases:
	 * 1)no valid size (two Players)
	 * 2)valid size
	 * 3)valid but big size 
	 * 4)no valid size (four Players)
	 * 5)valid size(four Players)
	 */
	@Test
	public void testSetBoardSize() {
		newGameController.createDefaultGame();
		Game currentGame = onitamaController.getOnitama().getCurrentGame();		//create example game with two Players
		
		
		int size = 3;															//no valid size
		newGameController.setBoardSize(size);
		assertNotEquals(currentGame.getBoardSize(),size);
		
		size=5;																	//valid size
		newGameController.setBoardSize(size);
		assertEquals(currentGame.getBoardSize(),size);
		
		size=1248124011;														//valid, but big size
		newGameController.setBoardSize(size);
		assertEquals(currentGame.getBoardSize(),size);
		
		Game newCurrentGame = TestDataFactory.createSampleFourPlayerGame();
		onitamaController.getOnitama().setCurrentGame(newCurrentGame);
		
		size=6;																	//no valid size for four players
		newGameController.setBoardSize(size);
		assertNotEquals(newCurrentGame.getBoardSize(),size);
		
		size=7;																	//valid size for four players
		newGameController.setBoardSize(size);
		assertEquals(newCurrentGame.getBoardSize(),size);
		
		
	}

	/**
	 * testcases:
	 * 1)negative int not valid
	 * 2)positiv int valid
	 */
	@Test
	public void testSetMaxMoves() {
		newGameController.createDefaultGame();
		Game currentGame = onitamaController.getOnitama().getCurrentGame();		//create example game with two Players
		
		int moves = -4;															//no valid int
		newGameController.setMaxMoves(moves);
		assertNotEquals(moves, currentGame.getMaxMoves());
		
		moves = 3;															// valid int
		newGameController.setMaxMoves(moves);
		assertEquals(moves, currentGame.getMaxMoves());
		
		moves = 57295724;													// valid int
		newGameController.setMaxMoves(moves);
		assertEquals(moves, currentGame.getMaxMoves());
	}

	/**
	 * testcases:
	 * 1)two players: contains player
	 * 2)two players: does not contains player
	 * 3)one player : contains player
	 * 4)two players: does not contains player
	 * 5)one player : contains player
	 * 6)two players: does not contains player
	 * 7)one player : contains player
	 * 8)two players: does not contains player
	 */
	@Test
	public void testRemovePlayer() {
		newGameController.createDefaultGame();
		Game currentGame = onitamaController.getOnitama().getCurrentGame();		//create example game with two Players
		Player player1  = currentGame.getPlayers().get(0);
		
		newGameController.removePlayer(player1);
		assertEquals(1,currentGame.getPlayers().size());
		assertFalse(currentGame.getPlayers().contains(player1));
		
		//now game does not contain player1 -> case 2)
		newGameController.removePlayer(player1);
		assertEquals(1,currentGame.getPlayers().size());
		assertFalse(currentGame.getPlayers().contains(player1));
		
		
		currentGame = TestDataFactory.createSampleThreePlayerGame();		//create example game with three Players	
		onitamaController.getOnitama().setCurrentGame(currentGame);
		Player player  = currentGame.getPlayers().get(0);		
		newGameController.removePlayer(player);
		assertEquals(2,currentGame.getPlayers().size());
		assertFalse(currentGame.getPlayers().contains(player));
		
		
		currentGame = TestDataFactory.createSampleThreePlayerGame();		//create example game with three Players	
		onitamaController.getOnitama().setCurrentGame(currentGame);
		Player newPlayer  = new Player("test",PlayerType.HUMAN,PlayerColor.FIRE);		
		newGameController.removePlayer(newPlayer);
		assertEquals(3,currentGame.getPlayers().size());
		assertFalse(currentGame.getPlayers().contains(newPlayer));
		
		
		currentGame = TestDataFactory.createSampleFourPlayerGame();		//create example game with three Players	
		onitamaController.getOnitama().setCurrentGame(currentGame);
		player1  = currentGame.getPlayers().get(0);		
		newGameController.removePlayer(player1);
		assertEquals(3,currentGame.getPlayers().size());
		assertFalse(currentGame.getPlayers().contains(player1));
				
		
		currentGame = TestDataFactory.createSampleFourPlayerGame();		//create example game with three Players	
		onitamaController.getOnitama().setCurrentGame(currentGame);
		newPlayer  = new Player("test",PlayerType.HUMAN,PlayerColor.FIRE);		
		newGameController.removePlayer(newPlayer);
		assertEquals(4,currentGame.getPlayers().size());
		assertFalse(currentGame.getPlayers().contains(newPlayer));
				
	}

	/**
	 * testcases
	 * 1) two player
	 * 2) three players
	 * 3) four player
	 */
//	@Test
//	public void testGenerateStartingCards() {
//		newGameController.createDefaultGame();
//		Game currentGame = onitamaController.getOnitama().getCurrentGame();		//create example game with two Players
//		newGameController.generateStartingCards();
//		Map<Player,List<GameCard>> map = currentGame.getInitialCardDistribution();		
//		List<GameCard> cardList = new ArrayList<>();
//		for(Player p:currentGame.getPlayers()){
//			assertEquals(2,map.get(p).size());									//check if every player has two cards
//			cardList.addAll(map.get(p));
//		}
//		
//		for(GameCard card: cardList){
//			int count = 0;
//			for(GameCard c:cardList){
//				if(card.equals(c)){
//					count++;
//				}
//			}
//			assertEquals(1,count);												//check if every card in the map is unique
//		}
//		
//		
//		currentGame = TestDataFactory.createSampleThreePlayerGame();		//create example game with three Players	
//		onitamaController.getOnitama().setCurrentGame(currentGame);
//		newGameController.generateStartingCards();
//		map = currentGame.getInitialCardDistribution();
//		cardList = new ArrayList<>();
//		for(Player p:currentGame.getPlayers()){
//			assertEquals(2,map.get(p).size());
//			cardList.addAll(map.get(p));
//		}
//		for(GameCard card: cardList){
//			int count = 0;
//			for(GameCard c:cardList){
//				if(card.equals(c)){
//					count++;
//				}
//			}
//			assertEquals(1,count);												//check if every card in the map is unique
//		}
//		
//		
//		//TODO change method to createGame
//		currentGame = TestDataFactory.createSampleFourPlayerGame();		//create example game with three Players	
//		onitamaController.getOnitama().setCurrentGame(currentGame);
//		newGameController.generateStartingCards();
//		cardList = new ArrayList<>();
//		map = currentGame.getInitialCardDistribution();
//		for(Player p:currentGame.getPlayers()){
//			assertEquals(2,map.get(p).size());
//			cardList.addAll(map.get(p));
//		}
//		for(GameCard card: cardList){
//			int count = 0;
//			for(GameCard c:cardList){
//				if(card.equals(c)){
//					count++;
//				}
//			}
//			assertEquals(1,count);												//check if every card in the map is unique
//		}
//	}

}
