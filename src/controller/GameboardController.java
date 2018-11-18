package controller;

import java.util.List;

import model.GameBoard;
import model.GameMove;
import model.GameToken;
import model.Player;
import model.Vector;

public class GameboardController {

	private OnitamaController onitamaController;
	
	public GameboardController(OnitamaController onitamaController){
		this.onitamaController = onitamaController;
	}

	/**
	 * Calculate the next GameBoard depending on the given move.
	 * @param gameboard the board to get the next move of
	 * @param gameMove the move to be executed
	 * @return GameBoard the next Gameboard
	 * @throws IllegalArgumentException if the gameMove is illegal we throw this
	 */
	public GameBoard getNextGameboard(GameBoard gameBoardParam, GameMove gameMove) {
		//if(onitamaController.getMovementController().isValidGameMove(gameState, gameMove))		
		GameBoard gameBoard = new GameBoard(gameBoardParam);
		Vector newMove = gameMove.getSelectedMove().rotate(gameMove.getMovedToken().getPlayer());
		gameMove.setDefeatedToken(gameBoard.moveTo(gameMove.getMovedToken(), newMove));
		//check for game Ending conditions
		if(gameMove.getDefeatedToken() != null && gameMove.getDefeatedToken().isMaster()){
			removePlayerTokens(gameBoard, gameMove.getDefeatedToken().getPlayer());
			for(NewGameStateAUI aui: onitamaController.getGameStateController().getNewGameStateAUI()){
				if(!gameBoard.isSimulated()){
					aui.playerLost(gameMove.getDefeatedToken().getPlayer());
				}
			}
		}
		
		return gameBoard;
	}
	
	
	/**
	 * Return the number of tokens on the board that belong to the player
	 * @param gameBoard
	 * @param player
	 * @return
	 */
	public int getTokenCount(GameBoard gameBoard, Player player){
		int counter = 0;
		for(int x = 0; x < gameBoard.getSize(); x++){
			for(int y = 0; y < gameBoard.getSize(); y++){
				Vector position = new Vector(x, y);
				GameToken currentToken = gameBoard.getTokenAt(position);
				if(currentToken != null && currentToken.getPlayer().equals(player)){
					counter++;
				}
			}
		}
		return counter;
	}

	/**
	 * Check if the player is on another masters throne.
	 * @param gameboard the board to check on
	 * @param gameMove the player to check the condition on
	 * @return boolean the result of the check
	 */
	public boolean isOnWinningPosition(GameBoard gameBoard, Player player) {
		List<Vector> winningPositions = onitamaController.getOnitama().getCurrentGame().getWinningPositions(player);		
		for(Vector vector : winningPositions){
			GameToken currentToken = gameBoard.getTokenAt(vector);
			if(currentToken != null){
				if(currentToken.isMaster() && currentToken.getPlayer().equals(player)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Remove all tokens of the player.
	 * @param gameboard the board to remove on
	 * @param player the player to remove the tokens of
	 */
	public void removePlayerTokens(GameBoard gameBoard, Player player) {
		for(int x = 0; x < gameBoard.getSize(); x++){
			for(int y = 0; y < gameBoard.getSize(); y++){
				Vector position = new Vector(x, y);
				GameToken currentToken = gameBoard.getTokenAt(position);
				if(currentToken != null && currentToken.getPlayer().equals(player)){
					gameBoard.remove(position);
				}
			}
		}
		
		if(!gameBoard.isSimulated()) {
			for (NewGameStateAUI gameStateAUI : onitamaController.getGameStateController().getNewGameStateAUI()) {
				gameStateAUI.playerLost(player);
			}
		}
	}

	public OnitamaController getOnitamaController() {
		return onitamaController;
	}

	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}

}
