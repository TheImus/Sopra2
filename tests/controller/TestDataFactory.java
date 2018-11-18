/**
 * 
 */
package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Game;
import model.GameBoard;
import model.GameCard;
import model.GameMove;
import model.GameState;
import model.GameToken;
import model.Onitama;
import model.Player;
import model.PlayerColor;
import model.PlayerType;
import model.Vector;

/**
 * @author sopr026
 *
 */
public class TestDataFactory {

	/**
	 * Creates an Onitama-instance with initial 4-Player Game
	 * @return Onitama-instance with initial 4-Player Game
	 */
	public static Onitama createSampleFourPlayerGameOnitama(){
		Onitama onitama = new Onitama();
		onitama.setCurrentGame(createSampleFourPlayerGame());
		return onitama;
	}
	
	/**
	 * Create a simple initial 4-Player Game
	 * @return initial 4-Player Game
	 */
	public static Game createSampleFourPlayerGame(){
		Game game = new Game();
		game.setBoardSize(7);
		game.setName("testGame4Player");
		List<Player> fourPlayers = generateFourPlayers();
		game.setPlayers(fourPlayers);
		return game;
	}
	
	/**
	 * Creates an Onitama-instance with initial 3-Player Game
	 * @return Onitama-instance with initial 4-Player Game
	 */
	public static Onitama createSampleThreePlayerGameOnitama(){
		Onitama onitama = new Onitama();
		onitama.setCurrentGame(createSampleThreePlayerGame());
		return onitama;
	}
	
	/**
	 * Create a simple initial 3-Player Game
	 * @return initial 3-Player Game
	 */
	public static Game createSampleThreePlayerGame(){
		Game game = new Game();
		game.setBoardSize(7);
		game.setName("testGame3Player");
		List<Player> threePlayers = generateThreePlayers();
		game.setPlayers(threePlayers);
		return game;
	}

	public static Game createInconsistentFourPlayerWinnableGame(){
		Game game = createSampleFourPlayerGame();
		GameState currentGameState = game.getCurrentGameState();
		Player currentPlayer = currentGameState.getCurrentPlayer();
		GameBoard gameBoard = currentGameState.getGameBoard();
		Map<Player, List<GameCard>> cardDistribution = currentGameState.getCardDistribution();
		Vector masterPosition = currentPlayer.getStartingPosition();
		GameToken winningNovice = gameBoard.getTokenAt(masterPosition.addTo(new Vector(2,0).rotate(currentPlayer)));
		assert(winningNovice != null);
		gameBoard.moveTo(winningNovice, new Vector(0,-3).rotate(currentPlayer));
		List<GameCard> playerCards = cardDistribution.get(currentPlayer);
		playerCards.remove(0);
		playerCards.add(GameCard.BOAR);
		cardDistribution.put(currentPlayer, playerCards);
		
		//currentGameState.setCardDistribution(cardDistribution);
		//currentGameState.setGameBoard(gameBoard);
		return game;
	}
	
	public static Game createSampleTwoPlayerGameWinableInNextMove(){
		System.out.println("Test start");
		Game game = new Game();
		game.setName("win in next move!");
		HashMap<Player, List<GameCard>> initialCardDistribution = new HashMap<Player, List<GameCard>>();
		List<Player> players = game.getPlayers();
		assert(players.size()==2);
		List<GameCard> winningPlayerHand = new ArrayList<GameCard>();
		winningPlayerHand.add(GameCard.TIGER);
		winningPlayerHand.add(GameCard.MONKEY);
		List<GameCard> losingPlayerHand = new ArrayList<GameCard>();
		losingPlayerHand.add(GameCard.BOAR);
		losingPlayerHand.add(GameCard.MANTIS);
		if(players.get(0).getPlayerColor() == PlayerColor.EARTH){
			initialCardDistribution.put(players.get(0), winningPlayerHand);
			game.setStartingPlayer(players.get(0));
			initialCardDistribution.put(players.get(1), losingPlayerHand);
		}
		else{
			initialCardDistribution.put(players.get(1), winningPlayerHand);
			game.setStartingPlayer(players.get(1));
			initialCardDistribution.put(players.get(0), losingPlayerHand);
		}
		game.setInitialCardDistribution(initialCardDistribution);
		game.setInitialFreeCard(GameCard.DRAGON);

		// Gewinner mit Monkey novice vor master, dann Verlierer Master einen vor mit Boar
		// Dann kann Gewinner gegnerischen Master mit Tiger auf den Novice vor seinem Master schlagen
		List<GameState> gameStates = game.getGameStates();
		GameState firstGameState = game.getCurrentGameState();
		GameState secondGameState = new GameState(firstGameState);
		Player secondCurrentPlayer = secondGameState.getCurrentPlayer();
		GameBoard secondGameboard = firstGameState.getGameBoard();
		System.out.println(secondGameboard.getString());
		
		Vector secondCurrentMaster = secondCurrentPlayer.getStartingPosition();
		Vector winningNovicePosition = secondCurrentMaster.addTo(new Vector(1,0).rotate(secondCurrentPlayer));
		GameToken winningNovice = secondGameboard.getTokenAt(winningNovicePosition);
		GameMove firstMove = new GameMove(winningNovice, GameCard.MONKEY, GameCard.MONKEY.getPossibleMovements().get(0));

		int roundNumber = secondGameState.getRoundNumber();
		secondGameState.setRoundNumber(roundNumber+1);
		Map<Player, List<GameCard>> secondCardDistribution = secondGameState.getCardDistribution();
		List<GameCard> secondCurrentPlayerCards = secondCardDistribution.get(secondCurrentPlayer);

		secondCurrentPlayerCards.remove(GameCard.MONKEY);
		secondCurrentPlayerCards.add(GameCard.DRAGON);
		
		secondGameState.setFreeGameCard(GameCard.MONKEY);
		if(players.get(0).equals(secondCurrentPlayer)){
			secondGameState.setCurrentPlayer(players.get(1));
		}
		else{
			secondGameState.setCurrentPlayer(players.get(0));
		}
		secondGameState.setLastMove(firstMove);
		
		GameBoard newGameBoard = new GameBoard(secondGameboard);
		Vector newMove = firstMove.getSelectedMove().rotate(firstMove.getMovedToken().getPlayer());
		firstMove.setDefeatedToken(newGameBoard.moveTo(firstMove.getMovedToken(), newMove));
		secondGameState.setGameBoard(newGameBoard);
		
		GameState thirdGameState = new GameState(secondGameState);
		Player thirdCurrentPlayer = thirdGameState.getCurrentPlayer();
		GameBoard thirdGameboard = secondGameState.getGameBoard();
		System.out.println(thirdGameboard.getString());
		
		//Vector losingMasterPostition = thirdCurrentPlayer.getStartingPosition();
		//Vector losingNovicePosition = thirdCurrentMaster.addTo(new Vector(1,0));
		GameToken losingMaster = thirdGameboard.getTokenAt(thirdCurrentPlayer.getStartingPosition());
		GameMove secondMove = new GameMove(losingMaster, GameCard.BOAR, GameCard.BOAR.getPossibleMovements().get(0));

		roundNumber = thirdGameState.getRoundNumber();
		thirdGameState.setRoundNumber(roundNumber+1);
		Map<Player, List<GameCard>> thirdCardDistribution = thirdGameState.getCardDistribution();
		List<GameCard> thirdCurrentPlayerCards = thirdCardDistribution.get(thirdCurrentPlayer);

		thirdCurrentPlayerCards.remove(GameCard.BOAR);
		thirdCurrentPlayerCards.add(GameCard.MONKEY);
		
		thirdGameState.setFreeGameCard(GameCard.BOAR);
		if(players.get(0).equals(thirdCurrentPlayer)){
			thirdGameState.setCurrentPlayer(players.get(1));
		}
		else{
			thirdGameState.setCurrentPlayer(players.get(0));
		}
		thirdGameState.setLastMove(secondMove);
		
		GameBoard nextGameBoard = new GameBoard(thirdGameboard);
		Vector nextMove = secondMove.getSelectedMove().rotate(secondMove.getMovedToken().getPlayer());
		secondMove.setDefeatedToken(nextGameBoard.moveTo(secondMove.getMovedToken(), nextMove));
		thirdGameState.setGameBoard(nextGameBoard);	
		System.out.println(nextGameBoard.getString());
		//List<GameState> gameStates = game.getGameStates();
		assert(gameStates.size()==1);
		gameStates.add(secondGameState);
		gameStates.add(thirdGameState);
		game.setGameStates(gameStates);
		return game;
	}

	public static Game createSampleTwoPlayerWonGame(){
		Game game = createSampleTwoPlayerGameWinableInNextMove();
		
		GameState currentGameState = game.getCurrentGameState();
		GameState nextGameState = new GameState(currentGameState);
		Player currentPlayer = nextGameState.getCurrentPlayer();
		GameBoard currentGameboard = currentGameState.getGameBoard();
		//System.out.println("Test start");
		Vector winningCurrentMaster = currentPlayer.getStartingPosition();
		Vector winningNovicePosition = winningCurrentMaster.addTo(new Vector(0,-1).rotate(currentPlayer));
		System.out.println(currentGameboard.getString());
		GameToken winningNovice = currentGameboard.getTokenAt(winningNovicePosition);
		GameMove nextMove = new GameMove(winningNovice, GameCard.TIGER, GameCard.TIGER.getPossibleMovements().get(0));
		
		int roundNumber = nextGameState.getRoundNumber();
		nextGameState.setRoundNumber(roundNumber+1);
		Map<Player, List<GameCard>> cardDistribution = nextGameState.getCardDistribution();
		List<GameCard> currentPlayerCards = cardDistribution.get(currentPlayer);

		currentPlayerCards.remove(GameCard.TIGER);
		currentPlayerCards.add(GameCard.BOAR);
		nextGameState.setFreeGameCard(GameCard.TIGER);
		
		List<Player> players = game.getPlayers();
		if(players.get(0).equals(currentPlayer)){
			nextGameState.setCurrentPlayer(players.get(1));
		}
		else{
			nextGameState.setCurrentPlayer(players.get(0));
		}
		nextGameState.setLastMove(nextMove);
		
		GameBoard nextGameBoard = new GameBoard(currentGameboard);
		Vector newMove = nextMove.getSelectedMove().rotate(nextMove.getMovedToken().getPlayer());
		nextMove.setDefeatedToken(nextGameBoard.moveTo(nextMove.getMovedToken(), newMove));
		if(nextMove.getDefeatedToken() != null && nextMove.getDefeatedToken().isMaster()){
			removePlayerTokens(currentGameboard, nextMove.getDefeatedToken().getPlayer());
		}
		nextGameState.setGameBoard(nextGameBoard);	
		System.out.println(nextGameBoard.getString());
		List<GameState> gameStates = game.getGameStates();
		gameStates.add(nextGameState);
		game.setGameStates(gameStates);	
		System.out.println(game.getPlayers().get(0).getPlayerType());
		System.out.println(game.getPlayers().get(1).getPlayerType());
		System.out.println("test ende");
		return game;
	}
	
	/**
	 * Remove all tokens of the player.
	 * @param gameboard the board to remove on
	 * @param player the player to remove the tokens of
	 */
	private static void removePlayerTokens(GameBoard gameBoard, Player player) {
		for(int x = 0; x < gameBoard.getSize(); x++){
			for(int y = 0; y < gameBoard.getSize(); y++){
				Vector position = new Vector(x, y);
				GameToken currentToken = gameBoard.getTokenAt(position);
				if(currentToken != null && currentToken.getPlayer().equals(player)){
					gameBoard.remove(position);
				}
			}
		}
	}
	
	/**
	 * Generates three simple Players
	 * @return List of three Players
	 */
	private static List<Player> generateThreePlayers() {
		Player player1 = new Player("Player1", PlayerType.HUMAN, PlayerColor.EARTH);
		Player player2 = new Player("Player2", PlayerType.HUMAN, PlayerColor.FIRE);
		Player player3 = new Player("Player3", PlayerType.HUMAN, PlayerColor.WATER);
		List<Player> players= new ArrayList<Player>();
		players.add(player1);
		players.add(player2);
		players.add(player3);
		return players;
	}

	/**
	 * Generates four simple Players
	 * @return List of four Players
	 */
	private static List<Player> generateFourPlayers() {
		Player player1 = new Player("Player1", PlayerType.HUMAN, PlayerColor.EARTH);
		Player player2 = new Player("Player2", PlayerType.HUMAN, PlayerColor.FIRE);
		Player player3 = new Player("Player3", PlayerType.HUMAN, PlayerColor.WATER);
		Player player4 = new Player("Player4", PlayerType.HUMAN, PlayerColor.AIR);
		List<Player> players= new ArrayList<Player>();
		players.add(player1);
		players.add(player2);
		players.add(player3);
		players.add(player4);
		return players;
	}
	
	

}
