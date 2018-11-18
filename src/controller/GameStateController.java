package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Game;
import model.GameBoard;
import model.GameCard;
import model.GameMove;
import model.GameState;
import model.Player;
import model.PlayerColor;
import model.PlayerType;

/**
 * @author sopr027 alias Nico 
 * Controller for functions for administration of the GameStates
 */
public class GameStateController {

	/**
	 * Reference for the central Controller, which knows all the other controllers
	 */
	private OnitamaController onitamaController;
	private List<NewGameStateAUI> newGameStateAUI;
	/**
	 * Constructor
	 * @param onitamaController
	 *		Reference for the central controller in order to exchange information with all the other controllers
	 */
	public GameStateController(OnitamaController onitamaController){
		this.onitamaController = onitamaController;
		newGameStateAUI = new ArrayList<NewGameStateAUI>();
	}
	
	/**
	 * move to Last player move and delete every Game State before
	 */
	/*
	public void lastPlayerMove(){
		List<GameState> gameStates = new ArrayList<GameState>();
		gameStates.addAll(onitamaController.getOnitama().getCurrentGame().getGameStates());
		Collections.reverse(gameStates);
		gameStates.indexOf()
		int newPlace = 0;
		for(int i=0; i < gameStates.size()-1; i++){
			GameState currentGameState = gameStates.get(i);
			if(currentGameState.getLastMove().getMovedToken().getPlayer().getPlayerType() == PlayerType.HUMAN){
				System.out.println("GameState removed");
				newPlace = i;
				break;
			}
			else{
				System.out.println("GameState skipped");
			}
		
		}
		gameStates = gameStates.subList(newPlace+1, gameStates.size());
		//Collections.reverse(gameStates);
		onitamaController.getGameStateController().resetToGameState(gameStates.get(newPlace));

		for (NewGameStateAUI aui : newGameStateAUI) {
			aui.refreshGameState();
		}
		
	}*/
	
	public void lastPlayerMove(){
		Game game = onitamaController.getOnitama().getCurrentGame();

		GameState currentGameState = game.getCurrentGameState();
		List<GameState> gameStates = game.getGameStates();
		System.out.println(gameStates.size());
		
		int newPosition = gameStates.indexOf(currentGameState)-1;
		System.out.println(newPosition);
		
		for(int i = newPosition ; i> 0 ; i--){
			newPosition = i;
			if(gameStates.get(i).getCurrentPlayer().getPlayerType().equals(PlayerType.HUMAN))
				break;
			
		}
		
		
		if(newPosition >= 0){
			onitamaController.getGameStateController().resetToGameState(gameStates.get(newPosition));
		}
			
	}
	
	public void futurePlayerMove(){
		Game game = onitamaController.getOnitama().getCurrentGame();

		GameState currentGameState = game.getCurrentGameState();
		List<GameState> gameStates = game.getGameStates();
		System.out.println(gameStates.size());
		int newPosition = gameStates.indexOf(currentGameState)+1;
		System.out.println(newPosition);
		
		for(int i = newPosition ; i < gameStates.size(); i++){
			newPosition = i;
			if(gameStates.get(i).getCurrentPlayer().getPlayerType().equals(PlayerType.HUMAN))
				break;
		}
		
		if(newPosition < gameStates.size()){
			onitamaController.getGameStateController().resetToGameState(gameStates.get(newPosition));
		}
	}

	/**
	 * take a gameState and set it as the current GameState
	 * @param gameState which is desired to be set
	 */
	public void resetToGameState (GameState  gameState) {
		Game game = onitamaController.getOnitama().getCurrentGame();
		List<GameState> gameStates = game.getGameStates();
		
		if (gameStates.contains(gameState)) {
			game.setCurrentGameState(gameState);
		}

		for(NewGameStateAUI aui: newGameStateAUI){
			aui.refreshGameState();
		}
	}
	

	/**
	 * take a gameState and return the player who is going to play in the next turn
	 * @param gameState current gameState
	 * @return Player
	 */
	public Player getNextPlayer(GameState  gameState){
		return getNextPlayer(gameState, gameState.getCurrentPlayer());
	}
	private Player getNextPlayer(GameState  gameState, Player currentPlayer) {
		PlayerController playerController = onitamaController.getPlayerController();
		for (int i = 0; i < 4; i++) {
			PlayerColor playerColor = currentPlayer.getPlayerColor();
			List<Player> allPlayers = onitamaController.getOnitama().getCurrentGame().getPlayers();
			PlayerColor nextColors[] = { playerColor.getnextColorTwoPlayers(), playerColor.getnextColorThreePlayers(),
					playerColor.getnextColorFourPlayers() };
			List<Player> potentialPlayer = allPlayers.stream()
					.filter(p -> p.getPlayerColor().equals(nextColors[allPlayers.size() - 2]))
					.collect(Collectors.toList());
			Player nextPlayer = potentialPlayer.get(0);

			if (playerController.isAlive(gameState, nextPlayer)) {
				return nextPlayer;
			}
			currentPlayer = nextPlayer;
		}
		throw new RuntimeException("Why are all Players dead?!");
	}

	/**
	 * Take a gameMove & an old GameState and generate the next GameState
	 * @param gameMove which is made by a player
	 * @return a gameState in which the given gameMove is made
	 */
	public GameState generateNextGameState(GameState oldGameState, GameMove gameMove) {
		
		if(onitamaController.getMovementController().getPossibleMovements(oldGameState, gameMove.getMovedToken().getPlayer()).contains(gameMove)){
			throw new IllegalArgumentException("bruder mach nicht diese");
		}
		// Make a copy of the old GameState
		GameState newGameState = new GameState(oldGameState);
		
		// get the current Player
		Player currentPlayer = newGameState.getCurrentPlayer();
		
		//get current GameBoard  
		GameBoard currentGameboard = oldGameState.getGameBoard();
		
		// get current roundNumber and add 1 
		int roundNumber = newGameState.getRoundNumber();
		newGameState.setRoundNumber(roundNumber+1);
		
		// get the general card distribution and the one for the current player
		Map<Player, List<GameCard>> cardDistribution = 	newGameState.getCardDistribution();
		List<GameCard> currentPlayerCards = cardDistribution.get(currentPlayer);
		//System.out.println("anzahl karten fuer" +currentPlayer.getName() +": " + currentPlayerCards.size());
		//System.out.println("karten: " + currentPlayerCards);
		
		// get the used card and the free card
		GameCard usedMove = gameMove.getSelectedCard();
		GameCard freeCard = oldGameState.getFreeGameCard();
		//System.out.println("genutzte Karte: " + usedMove.toString());
		//System.out.println("vorher freie Karte: " + freeCard.toString());
		
		// remove the used card from the current player and add the free card to his hand 
		currentPlayerCards.remove(usedMove);
		currentPlayerCards.add(freeCard);
		//System.out.println("anzahl karten fuer " + currentPlayer.getName() + ": " + currentPlayerCards.size());
		//System.out.println("karten: " + currentPlayerCards);
		
		// update general card distribution
		//cardDistribution.put(currentPlayer, currentPlayerCards);
		//newGameState.setCardDistribution(cardDistribution);
		newGameState.setFreeGameCard(usedMove);
		
		// determine the next player and set him to the new GameState
		//Player nextPlayer = getNextPlayer(oldGameState);
		//newGameState.setCurrentPlayer(nextPlayer);
		newGameState.setLastMove(gameMove);
		GameBoard newGameBoard = onitamaController.getGameboardController().getNextGameboard(currentGameboard, gameMove);
		newGameState.setGameBoard(newGameBoard);
		Player nextPlayer = getNextPlayer(newGameState);
		newGameState.setCurrentPlayer(nextPlayer);
		
		if(onitamaController.getMovementController().getPossibleMovements(newGameState, nextPlayer).isEmpty()){
			onitamaController.getGameboardController().removePlayerTokens(newGameBoard, nextPlayer);
			for(NewGameStateAUI aui: newGameStateAUI){
				if(!oldGameState.isSimulated()){
					aui.playerLost(nextPlayer);
				}
			}
		}
						
		return newGameState;
	}

	/**
	 * Get winning Players based on the current GameState 
	 * @return List of winning Players
	 */
	public List<Player> getWinningPlayers(GameState gameState) {
		List<Player> winningPlayers = new ArrayList<>();
		if(gameState == null){
			throw new NullPointerException();
		}
		
		List<Player> winningRound = checkRoundWinningCondition(gameState);
		List<Player> winningPosition = checkPositionWinningCondition(gameState);
		List<Player> winningMaster = checkMasterWinningCondition(gameState);
		
		if(!winningPosition.isEmpty()){
			return winningPosition; 
		}
		
		if(!winningMaster.isEmpty()){
			return winningMaster;
		}
		
		if(!winningRound.isEmpty()){
			return winningRound;
		}
		return winningPlayers;
	}
	
	/**
	 * Check if the roundNumber is exceeded and return the player(s) with the most token of the field 
	 * @param gameState
	 * @return List of winning player
	 */
	public List<Player> checkRoundWinningCondition(GameState gameState){
		int roundNumber = onitamaController.getOnitama().getCurrentGame().getGameStates().size();
		int maxMoves = onitamaController.getOnitama().getCurrentGame().getMaxMoves();
		List<Player> winningPlayers = new ArrayList<Player>();
		if(roundNumber < maxMoves || maxMoves == -1){
			return winningPlayers;
		}
		GameboardController gameboardController = onitamaController.getGameboardController();
		List<Player> potentialWinners = onitamaController.getOnitama().getCurrentGame().getPlayers();
		int winnerTokenCount = 0;
		int currentTokenCount;
		for(Player player : potentialWinners){
			currentTokenCount = gameboardController.getTokenCount(gameState.getGameBoard(), player);
			if(winnerTokenCount < currentTokenCount){
				winnerTokenCount = currentTokenCount;
				winningPlayers.clear();
				winningPlayers.add(player);
			}else if(winnerTokenCount == currentTokenCount){
				winningPlayers.add(player);
			}
		}
		return winningPlayers;
	}
	
	/**
	 * Check if a Master is on the position of another player's master position and return the respective player
	 * @param gameState
	 * @return List of Winning Players
	 */
	public List<Player> checkPositionWinningCondition(GameState gameState){
		List<Player> potentialWinners = onitamaController.getOnitama().getCurrentGame().getPlayers();
		ArrayList<Player> winningPlayers = new ArrayList<Player>();
		GameBoard gameBoard = gameState.getGameBoard();
		for(Player player : potentialWinners){
			if(onitamaController.getGameboardController().isOnWinningPosition(gameBoard, player)){
				winningPlayers.add(player);
			}
		}
		return winningPlayers;
	}
	
	/**
	 * Check if only token of one player are on the board and return the respective player
	 * @param gameState
	 * @return List of Winning Players
	 */
	public List<Player> checkMasterWinningCondition (GameState gameState){
		GameBoard gameBoard = gameState.getGameBoard();
		List<Player> potentialWinners = onitamaController.getOnitama().getCurrentGame().getPlayers();
		 
		int maximum = 0;
		int sum = 0;
		int index = -1;
		for(int i = 0; i < potentialWinners.size(); i++){
			int var = onitamaController.getGameboardController().getTokenCount(gameBoard, potentialWinners.get(i));
			maximum = Math.max(maximum, var);
			sum += var;
			if (var != 0)
				index = i;
		}
		if(maximum != sum){
			return new ArrayList<Player>();
		}else{
			ArrayList<Player> winningPlayer = new ArrayList<>();
			winningPlayer.add(potentialWinners.get(index));
			return winningPlayer;
		}
		
	}
	/**
	 * @return onitama Controller
	 */
	public OnitamaController getOnitamaController() {
		return onitamaController;
	}

	/**
	 * @param onitamaController
	 * 	set central onitama controller
	 */
	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}
	
	/**
	 * Removes all games states after "current" game state and adds a new game state behind
	 * 
	 * Call this method to do a game move, do not call this in AI
	 * @param gameState
	 */
	public void setNextGameState(GameState gameState) {//never call inside ai, even if this all is simulated
		GameState oldGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();
		List<GameState> gameStates = onitamaController.getOnitama().getCurrentGame().getGameStates();
		Game game = onitamaController.getOnitama().getCurrentGame();
		
		// remove game states after current game state
		int currentGameStateIndex = gameStates.indexOf(game.getCurrentGameState());
		
		// Create a new list with game states - copy only to current game state
		List<GameState> newGameStates = new ArrayList<GameState>();
		for(int i = 0; i < currentGameStateIndex+1;i++){
			newGameStates.add(gameStates.get(i));
				
		}
//		gameStates.subList(0, currentGameStateIndex+1)
		
		newGameStates.add(gameState); // append new game state to list
		game.setGameStates(newGameStates); // overwrite game state list
		game.setCurrentGameState(gameState); // set current game state to new game state
		
		if(!gameState.isSimulated()){
			for( NewGameStateAUI aui : newGameStateAUI){
				aui.animateNewGameState(oldGameState, gameState);
			}
		}
	}
	
	public void addAUI(NewGameStateAUI gameStateAUI){
		// remove old aui's from this type -- fixes nasty save game bug
		// but at this point it is only a workaround!
		List<NewGameStateAUI> removeAUIs = newGameStateAUI.stream()
			.filter(aui -> aui.getClass().equals(gameStateAUI.getClass()))
			.collect(Collectors.toList());
		this.newGameStateAUI.removeAll(removeAUIs);
		
		if (!this.newGameStateAUI.contains(gameStateAUI)) {
			this.newGameStateAUI.add(gameStateAUI);
		}
	}
	
	public void removeAUI(NewGameStateAUI gameStateAUI){
		this.newGameStateAUI.remove(gameStateAUI);
	}

	public List<NewGameStateAUI> getNewGameStateAUI() {
		return newGameStateAUI;
	}

}
