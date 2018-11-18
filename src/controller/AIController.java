package controller;

import java.util.*;

import model.GameBoard;
import model.GameMove;
import model.GameState;
import model.GameToken;
import model.Onitama;
import model.Player;
import model.Vector;

public abstract class AIController {

	protected OnitamaController onitamaController;
	protected Player self;
	
	public OnitamaController getOnitamaController() {
		return onitamaController;
	}
	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}
	public abstract GameMove getNextMove(GameState  gameState);
	
	protected boolean canWin(GameState gameState, GameMove gameMove){//TODO implement this
		MovementController movementController = onitamaController.getMovementController();
		PlayerController playerController = onitamaController.getPlayerController();
		int playerCount = gameState.getPlayers().size();
		
		if(movementController.isValidGameMove(gameState, gameMove)){
			Player currentPlayer = gameState.getCurrentPlayer();
			List<Player> players = gameState.getPlayers();
			List<Vector> winningPositions = onitamaController.getOnitama().getCurrentGame().getWinningPositions(currentPlayer);
			
			GameBoard gameBoard = gameState.getGameBoard();
			Vector destination = gameBoard.getDestination(gameMove);
			
			if(gameBoard.getTokenAt(destination) != null){
			
				for(Player player : players )
				{
					if(!playerController.isAlive(gameState, player))
					{
						playerCount = playerCount--;
					}
				}
				
					if(gameBoard.getTokenAt(destination).isMaster() && playerCount == 2) return true;
				}
			
			if(winningPositions.contains(destination)) return true;
			
		}
		return false;
	}
	
	protected boolean canLoose(GameState gameState, GameMove gameMove){//only 2 player TODO fix for all
		GameState nextState = onitamaController.getGameStateController().generateNextGameState(gameState, gameMove);
		Player nextPlayer = onitamaController.getGameStateController().getNextPlayer(gameState);
		List<GameMove> allEnemyMoves = onitamaController.getMovementController().getPossibleMovements(nextState, nextPlayer);
		for(int j = 0; j < gameState.getPlayers().size()-1; j++)
		for(GameMove enemyMove : allEnemyMoves){
			if(canWin(nextState, enemyMove)){
				return true;
			}

		}
		//return canSomeoneElseWin(gameState, gameMove, gameState.getCurrentPlayer(), 3);
		return false;
	}
	private boolean canSomeoneElseWin(GameState gameState, GameMove gameMove, Player startingPlayer, int depth){
		//Player currentPlayer = gameState.getCurrentPlayer();
		GameState nextGameState = onitamaController.getGameStateController().generateNextGameState(gameState, gameMove);
		if(depth == 0){
			return false;
		}
		//System.out.println("P1: " + gameState.getCurrentPlayer().getPlayerColor().toString());
		//System.out.println("P2: " + onitamaController.getGameStateController().getNextPlayer(gameState).getPlayerColor().toString());
		
		Player nextPlayer = nextGameState.getCurrentPlayer();

//		System.out.println("startingPlayer: " + startingPlayer.getPlayerColor().toString());
//		System.out.println("prevPlayer: " + gameState.getCurrentPlayer().getPlayerColor().toString());
//		System.out.println("currentPlayer: " + nextPlayer.getPlayerColor().toString());
		
		if(startingPlayer.getPlayerColor().equals(nextPlayer.getPlayerColor())){
//			System.out.println("startingPlayer == currentPlayer == " + nextPlayer.getPlayerColor().toString());
			return false;//are we the player specified? if so, no one else canWin
			
		}
		List<Player> winnersInThisState = onitamaController.getGameStateController().getWinningPlayers(nextGameState);
		if(!winnersInThisState.isEmpty()){
//			System.out.println("someone wins on " + nextPlayer.getPlayerColor().toString() + "'s turn");
			return true;
		}
		List<GameMove> possibleMoves = onitamaController.getMovementController().getPossibleMovements(nextGameState, nextPlayer);
		for(GameMove move : possibleMoves){
			
			if(canSomeoneElseWin(nextGameState, move, startingPlayer, depth-1)){
//				System.out.println("someone wins on " + nextPlayer.getPlayerColor().toString() + "'SS turn");
				return true;
			}
		}
		//</if(remainingPlayersToPlay > 1)>
//		System.out.println("no one wins on " + nextPlayer.getPlayerColor().toString() + "'SS turn");
		
		  
		return false;
	}
	
	protected boolean canTake(GameState gameState, GameMove gameMove){//TODO implement this
		
		MovementController movementController = onitamaController.getMovementController();
		
		if(movementController.isValidGameMove(gameState, gameMove)){
			GameBoard gameBoard = gameState.getGameBoard();
			Vector destination = gameBoard.getDestination(gameMove);
			
			if(gameBoard.getTokenAt(destination) != null) return true;	
		}
		return false;
	}
	
	protected boolean canTakeMaster(GameState gameState, GameMove gameMove){
		MovementController movementController = onitamaController.getMovementController();
		
		if(movementController.isValidGameMove(gameState, gameMove)){
			GameBoard gameBoard = gameState.getGameBoard();
			Vector destination = gameBoard.getDestination(gameMove);
			
			if(gameBoard.getTokenAt(destination) != null && gameBoard.getTokenAt(destination).isMaster()) return true;	
		}
		return false;
	}
	
	protected boolean canBeTaken(GameState gameState, GameMove gameMove){
		MovementController movementController = onitamaController.getMovementController();
		
		if(movementController.isValidGameMove(gameState, gameMove)){
			GameBoard gameBoard = gameState.getGameBoard();
			Vector destination = gameBoard.getDestination(gameMove);
			
			if(gameBoard.getTokenAt(destination) != null) return true;	
		}
		return false;
	}  
	
	public int rewardForMasterPosition(GameState newGamestate, Player player) {
		int value = 0;
		PlayerController playerCont = onitamaController.getPlayerController();
		if(playerCont.isAlive(newGamestate, player)) {
			GameToken playerMaster = playerCont.getMasterToken(newGamestate, player);
			Player enemy = newGamestate.getPlayers().get(0);
			if(player.equals(enemy) && newGamestate.getPlayers().size()>1) {	//enemy auf den gegner setzen
				enemy = newGamestate.getPlayers().get(1);
			}
			GameToken enemyMaster = playerCont.getMasterToken(newGamestate, enemy);
			Vector playerStartPosition = player.getStartingPosition();
			Vector enemyStartPosition = enemy.getStartingPosition();
			GameBoard newGameBoard = newGamestate.getGameBoard();
			Double playerDistance = euklidDistance(newGameBoard.getPositionOf(playerMaster),enemyStartPosition);
			if(playerCont.isAlive(newGamestate, enemy)){
				Double enemyDistance = euklidDistance(newGameBoard.getPositionOf(enemyMaster),playerStartPosition);			
				
				if(playerDistance==0) {
					value = Integer.MAX_VALUE;
				}
				else if(enemyDistance==0) {
					value = Integer.MIN_VALUE;
				}
				else if(playerDistance<2) {
					value += (3-playerDistance)*2;
				}
				else if(enemyDistance<2) {
					value -= (3-enemyDistance)*2;
				}
			}
			else{
				return 0;
			}
			return value;
		}
		
		return 0;
	}
	
	public double valueFunction(GameState newGamestate, Player player) {
		PlayerController playerCont = onitamaController.getPlayerController();
		MovementController moveCont = onitamaController.getMovementController();
		double value = 0;
		int playerToken = playerCont.getPlayerTokens(newGamestate,player).size(); 	
		Player enemy = newGamestate.getPlayers().get(0);
		if(player.equals(enemy) && newGamestate.getPlayers().size()>1) {	//enemy auf den gegner setzen
			enemy = newGamestate.getPlayers().get(1);
		}
		GameStateController gameStateController = onitamaController.getGameStateController();
		List<Player> winningPlayers = gameStateController.getWinningPlayers(newGamestate); 
		if(winningPlayers.size()==1 && winningPlayers.contains(player)) {		//hat spieler gewonnen?
			value = Integer.MAX_VALUE;
		}
		else if(winningPlayers.size()==1 && winningPlayers.contains(enemy)) {	//hat spieler verloren?
			value = Integer.MIN_VALUE;
		}
		else if(winningPlayers.contains(player)) {								//unentschieden aber spieler nicht verloren
			value += 5;
		}
		
		int enemyTokenAfter = playerCont.getPlayerTokens(newGamestate,enemy).size(); 
		if(playerToken<enemyTokenAfter) {		//hat der gegner mehr Steine, so ist tauschen schlecht für uns
			value += playerToken*11;
			value -= enemyTokenAfter*10;
		}
		else if(playerToken == enemyTokenAfter) {							
			value += playerToken*10;
			value -= enemyTokenAfter*10;
		}
		else {
			value+=playerToken*9;
			value-=enemyTokenAfter*10;
		}
		int possibleMovesPlayer = moveCont.getPossibleMovements(newGamestate, player).size();
		int possibleMovesEnemy = moveCont.getPossibleMovements(newGamestate, enemy).size();
		value += possibleMovesPlayer*0.01;
		value -= possibleMovesEnemy*0.01;
		value+= rewardForMasterPosition(newGamestate,player);
		
		return value;
	}
	
	public double euklidDistance(Vector vector1, Vector vector2) {
		return Math.sqrt(Math.pow(vector1.getX()-vector2.getX(), 2) + Math.pow(vector1.getY()-vector2.getY(), 2));	
	}
	
	public List<GameMove> getAllMoves(GameState gameState){		
		return onitamaController.getMovementController().getPossibleMovements(gameState, self);
	}
	
}
