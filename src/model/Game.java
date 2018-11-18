package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Game implements Serializable {

	private static final long serialVersionUID = 1495211039116525310L;
	public static final int MAX_MOVES_NOT_SET = -1;
	public static final int GAME_BOARD_SIZE_MIN = 5; 
	public static final int GAME_BOARD_SIZE_DEFAULT = 5;
	public static final int CARDS_PER_PLAYER = 2;
	public static final int PLAYER_COUNT_MIN = 2; 
	public static final int PLAYER_COUNT_MAX = PlayerColor.values().length;
	public static final int PLAYER_STARTING_TOKEN_COUNT = 5;
	// minimal allowed game board size - in array
	private static final int GAME_BOARD_SIZE_INTERNAL_MIN = 1;
	
	// Name of the savegame
	private String name;
	// set > 0, if this winning condition is active
	private int maxMoves;
	// set >= 5, length of the gameboard, should be odd
	private int gameBoardSize;
	// if highscore is diabled, save it here
	private boolean highscoreEnabled;
	// history
	private List<GameState> gameStates;
	// current game state
	private GameState currentGameState;
	// start player
	private Player startingPlayer;
	// participating players
	private List<Player> players;
	// initial free card
	private GameCard initialFreeCard;
	// initial cards for the players
	private Map<Player, List<GameCard>> initialCardDistribution;


	/**
	 * Constructor for a already initialized default game
	 */
	public Game() {
		this.name = "";
		this.maxMoves = MAX_MOVES_NOT_SET;
		this.gameBoardSize = GAME_BOARD_SIZE_DEFAULT;
		this.highscoreEnabled = true;
		this.players = new ArrayList<Player>();
		this.gameStates = new ArrayList<GameState>();
		
		// add two players to default game
		Player blue = new Player("Einfacher Computer", 	PlayerType.NOVICE, 	PlayerColor.FIRE);
		Player red  = new Player("Erde Spieler", 		PlayerType.HUMAN, 	PlayerColor.EARTH);
		players.add(blue);
		players.add(red);
		
		// generate general data
		generateStartingPositions();
		generateInitialCardDistribution();
	}
	
	/**
	 * 
	 * @return name of game
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return maximum moves to play, if -1 then maxMoves is not set
	 */
	public int getMaxMoves() {
		return maxMoves;
	}

	/**
	 * 
	 * @param maxMoves to set
	 */
	public void setMaxMoves(int maxMoves) {
		this.maxMoves = maxMoves;
	}

	/**
	 * 
	 * @return boardSize
	 */
	public int getBoardSize() {
		return gameBoardSize;
	}

	/**
	 * new starting positions will be generated automatically
	 * @param boardSize to set
	 */
	public void setBoardSize(int boardSize) {
		// if more than one gamestate, do not recreate the first one each time
		if (this.getGameStates().size() > GAME_BOARD_SIZE_INTERNAL_MIN) {
			throw new IllegalStateException("Can not change game board size after starting a game");
		}
		this.gameBoardSize = boardSize;
		this.gameStates.clear();
		generateStartingPositions();
	}

	/**
	 * 
	 * @return true, if highscore is enabled
	 */
	public boolean isHighscoreEnabled() {
		return highscoreEnabled;
	}

	/**
	 * Set whether highscore is enabled
	 * @param highscoreEnabled
	 */
	public void setHighscoreEnabled(boolean highscoreEnabled) {
		this.highscoreEnabled = highscoreEnabled;
	}

	/**
	 * Creates a new initial game state with the settings of this game
	 * 
	 * @return
	 */
	public List<GameState> getGameStates() {
		if (this.gameStates.size() == 0) {
			this.gameStates = new ArrayList<GameState>();
			this.gameStates.add(generateInitialGameState());
		}
		return gameStates;
	}

	/**
	 * Setter for gameStates
	 * @param gameStates
	 */
	public void setGameStates(List<GameState> gameStates) {
		this.gameStates = gameStates;
		currentGameState = gameStates.get(gameStates.size()-1);
	}

	/**
	 * If you change some things in player call setPlayers() 
	 * @return
	 */
	public List<Player> getPlayers() {
		List<Player> result = new ArrayList<Player>(this.players);
		return result;
	}

	/**
	 * This method sets the players of this game
	 * side effects: - Generates new starting positions for players
	 *	             - Generates new card distribution 
	 * @param players
	 */
	public void setPlayers(List<Player> players) {
		if (players == null) {
			throw new NullPointerException();
		}
		if (players.size() > PLAYER_COUNT_MAX) {
			throw new IllegalStateException("Player count must be <= " + PLAYER_COUNT_MAX);
		}
		
		this.players = players;
		this.gameStates.clear();
		
		generateStartingPositions();
		generateInitialCardDistribution();
	}
	
	/**
	 * 
	 * @return starting player
	 */
	public Player getStartingPlayer() {
		return startingPlayer;
	}

	/**
	 * set starting player
	 * @param startingPlayer
	 */
	public void setStartingPlayer(Player startingPlayer) {
		if (!this.players.contains(startingPlayer)) {
			throw new IllegalStateException("Starting player is not in players list");
		} else {
			this.startingPlayer = startingPlayer;
		}
	}

	/**
	 * 
	 * @return initial free card
	 */
	public GameCard getInitialFreeCard() {
		return initialFreeCard;
	}

	/**
	 * set initial free card
	 * @param initialFreeCard
	 */
	public void setInitialFreeCard(GameCard initialFreeCard) {
		if (initialFreeCard == null) {
			throw new NullPointerException();
		}
		this.initialFreeCard = initialFreeCard;
	}
	
	/**
	 * 
	 * @return initial card distribution map
	 */
	public Map<Player, List<GameCard>> getInitialCardDistribution() {
		return initialCardDistribution;
	}

	/**
	 * set initial card distribution
	 * @param initialCardDistribution
	 */
	public void setInitialCardDistribution(Map<Player, List<GameCard>> initialCardDistribution) {
		if (initialCardDistribution == null) {
			throw new NullPointerException();
		}
		this.initialCardDistribution = initialCardDistribution;
	}
	
	/**
	 * Returns the current game state
	 * if no game history exists return a new game state
	 */
	public GameState getCurrentGameState() {
		if (this.gameStates.size() <= 1) {
			List<GameState> gameStates = this.getGameStates();
			this.currentGameState = gameStates.get(0);
		}
		return this.currentGameState;
	}
	
	/**
	 * Sets the current game state
	 * FIND CURRENT GAME STATE, REMOVE BEHIND AND ADD NEW
	 * @param currentGameState
	 */
	public void setCurrentGameState(GameState currentGameState) {
		if (!gameStates.contains(currentGameState)) {
			throw new IllegalStateException("Did not found currentGameState in game history. Please add the game first.");
		}
		this.currentGameState = currentGameState;
	}
	
	/**
	 * Get all positions for given player on which his master wins
	 * @param player 
	 * @return list of winning positions on board
	 */
	public List<Vector> getWinningPositions(Player player){
		List<Vector> positions = new ArrayList<Vector>();
		for(Player otherPlayer : players){
			if(!otherPlayer.equals(player)){
				positions.add(otherPlayer.getStartingPosition());
			}
		}
		return positions;
	}
	
	
	/**
	 * Generates a new game board for a initial game state
	 * 
	 * @return
	 */
	private GameState generateInitialGameState() {
		GameState gameState = new GameState();
		gameState.setGameBoard(this.generateInitialGameBoard());
		gameState.setCardDistribution(this.initialCardDistribution);
		gameState.setFreeGameCard(this.initialFreeCard);
		gameState.setPlayers(this.players);
		gameState.setCurrentPlayer(this.startingPlayer);
		gameState.setLastMove(null);
		gameState.setRoundNumber(0);
		gameState.setSimulated(false);
		return gameState;
	}
	
	/**
	 * Creates a new game board
	 * and set tokens of the players
	 * 
	 * this is a helper method of generateInitialGameState
	 */
	private GameBoard generateInitialGameBoard() {
		GameBoard gameBoard = new GameBoard(this.gameBoardSize, false);
		int tokenOffset = (this.gameBoardSize - PLAYER_STARTING_TOKEN_COUNT) / 2;
		
		for (Player player : this.players) {
			for (int i = 0; i < PLAYER_STARTING_TOKEN_COUNT; i++) {
				GameToken token = new GameToken(false, player);
				if (i == PLAYER_STARTING_TOKEN_COUNT / 2) {
					token.setMaster(true);
				}
				
				switch (player.getPlayerColor()) {
				case EARTH: 
					gameBoard.add(token, new Vector(tokenOffset + i, gameBoardSize - 1));
					break;
				case AIR:
					gameBoard.add(token, new Vector(gameBoardSize - 1, tokenOffset + i));
					break;
				case FIRE: 
					gameBoard.add(token, new Vector(tokenOffset + i, 0));
					break;
				case WATER:
					gameBoard.add(token, new Vector(0, tokenOffset + i));
					break;
				}
			}
		}
		return gameBoard;
	}
	
	
	/**
	 * Set the correct starting positions of the players
	 */
	private void generateStartingPositions() {
		for (Player player : this.players) {
			switch (player.getPlayerColor()) {
			case EARTH: 
				player.setStartingPosition(new Vector(this.gameBoardSize/2, this.gameBoardSize-1));
				break;
			case AIR:
				player.setStartingPosition(new Vector(this.gameBoardSize-1, this.gameBoardSize/2));
				break;
			case FIRE: 
				player.setStartingPosition(new Vector(this.gameBoardSize/2, 0));
				break;
			case WATER:
				player.setStartingPosition(new Vector(0, this.gameBoardSize/2));
				break;
			}
		}
	}
	
	/**
	 * Precondition: players.size() * 2 + 1 <= GameCard.values
	 */
	private void generateInitialCardDistribution() {
		List<GameCard> deck = new ArrayList<GameCard>(Arrays.asList(GameCard.values()));
		this.initialFreeCard = pickGameCard(deck);
		
		this.initialCardDistribution = new HashMap<Player, List<GameCard>>();
		
		// pick two cards for each player
		for (Player player : getPlayers()) {
			List<GameCard> cards = new ArrayList<GameCard>();
			for (int i = 0; i < CARDS_PER_PLAYER; i++) {
				cards.add(pickGameCard(deck));
			}
			this.initialCardDistribution.put(player, cards);
		}
		
		// find starting player
		PlayerColor startingColor;
		if (this.players.size() == PLAYER_COUNT_MIN) {
			startingColor = this.getInitialFreeCard().getColor();
			for (Player player : this.getPlayers()) {
				if (player.getPlayerColor() != startingColor) {
					this.startingPlayer = player;
				}
			}
		} else {
			Random rand = ThreadLocalRandom.current();
			this.startingPlayer = this.getPlayers().get(rand.nextInt(this.getPlayers().size()));
		}
		
	}
	
	/**
	 * Picks a card random from deck and removes it
	 * 
	 * Helper method for generateInitialCardDistribution
	 * @param deck
	 * @return picked card
	 */
	private GameCard pickGameCard(List<GameCard> deck) {
		Random rand = ThreadLocalRandom.current();
		GameCard pickedCard = deck.get(rand.nextInt(deck.size()));
		deck.remove(pickedCard);
		return pickedCard;
	}

}
