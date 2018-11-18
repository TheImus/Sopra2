package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState  implements Serializable{
	private static final long serialVersionUID = 7696920201585549265L;
	private int roundNumber;
	private GameBoard gameBoard;
	private List<Player> players;
	private Player currentPlayer;
	private GameCard freeGameCard;
	private GameMove lastMove;
	private Map<Player, List<GameCard>> cardDistribution;

	/**
	 * normal Constructor
	 */
	public GameState() {
		this.cardDistribution = new HashMap<Player, List<GameCard>>();
	}
	
	/**
	 * copy constructor
	 * @param oldGameState new copy of this gamestate
	 */
	public GameState(GameState oldGameState) {		//cardDistribution muss haendisch kopiert werden!
		this.roundNumber      = oldGameState.getRoundNumber();
		this.gameBoard        = new GameBoard(oldGameState.getGameBoard());
		this.players          = oldGameState.getPlayers();
		this.freeGameCard     = oldGameState.getFreeGameCard();
		this.lastMove         = oldGameState.getLastMove(); 
		this.cardDistribution = copyDistribution(oldGameState.cardDistribution);
		//TODO add currentPlayer
		this.currentPlayer    = oldGameState.currentPlayer;
	}
	
	/**
	 * 
	 * @return roundnumber
	 */
	public int getRoundNumber() {
		return roundNumber;
	}
	
	public Map<Player,List<GameCard>> copyDistribution(Map<Player,List<GameCard>> oldMap){
		Map<Player,List<GameCard>> newMap = new HashMap<>();
		for(Player p:players) {
			List<GameCard> newList = new ArrayList<>();
			newList.addAll(oldMap.get(p));
			newMap.put(p, newList);
		}
		
		return newMap;
	}

	/**
	 * set roundnumber
	 * @param roundNumber
	 */
	public void setRoundNumber(int roundNumber) {
		this.roundNumber = roundNumber;
	}

	/**
	 * 
	 * @return true, if simulated
	 */
	public boolean isSimulated() {
		return this.gameBoard.isSimulated();
	}

	/**
	 * set simulated
	 * @param simulated
	 */
	public void setSimulated(boolean simulated) {
		this.gameBoard.setSimulated(simulated);
	}

	/**
	 * 
	 * @return gameboard
	 */
	public GameBoard getGameBoard() {
		return gameBoard;
	}

	/**
	 * set gameboard
	 * @param gameBoard
	 */
	public void setGameBoard(GameBoard gameBoard) {
		if (gameBoard == null) {
			throw new NullPointerException();
		}

		this.gameBoard = gameBoard;
	}

	/**
	 * 
	 * @return players
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * set players
	 * @param players
	 */
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	/**
	 * 
	 * @return currentPlayer
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * set current player
	 * @param currentPlayer
	 */
	public void setCurrentPlayer(Player currentPlayer) {
		if (currentPlayer == null) {
			throw new NullPointerException();
		}
		if (!players.contains(currentPlayer)) {
			throw new IllegalStateException("Current player is not in the players list");
		}
		this.currentPlayer = currentPlayer;
	}

	/**
	 * 
	 * @return free card
	 */
	public GameCard getFreeGameCard() {
		return freeGameCard;
	}

	/**
	 * set free card
	 * @param gameCard
	 */
	public void setFreeGameCard(GameCard gameCard) {
		this.freeGameCard = gameCard;
	}

	/**
	 * 
	 * @return last move (move into this gamestate)
	 */
	public GameMove getLastMove() {
		return lastMove;
	}

	/**
	 * set last move (move into this gamestate)
	 * @param lastMove
	 */
	public void setLastMove(GameMove lastMove) {
		this.lastMove = lastMove;
	}

	/**
	 * 
	 * @return hand cards of players
	 */
	public Map<Player, List<GameCard>> getCardDistribution() {
		return cardDistribution;
	}

	/**
	 * set hand cards
	 * @param cardDistribution
	 */
	public void setCardDistribution(Map<Player, List<GameCard>> cardDistribution) {
		this.cardDistribution = cardDistribution;
	}
	
	/**
	 * Returns all cards of this gamestate
	 * @return
	 */
	public List<GameCard> getAllCards() {
		List<GameCard> result = new ArrayList<>();
		result.add(this.getFreeGameCard());
		for (Player player : this.players) {
			result.addAll(this.getCardDistribution().get(player));
		}
		return result;
	}

}
