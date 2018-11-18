package view.gamecanvas;

import controller.HighscoreController;
import model.Game;
import model.GameMove;
import model.GameState;
import model.Player;
import model.PlayerType;
import view.GameCanvasViewController;

/**
 * This Class Manages the view state of the game
 * 
 * @author Fabian Kemper
 *
 */

public class CanvasState {
	private GameCanvasViewController viewController;

	// TODO: Implement view controller to operate on this
	// current game state, for game move history, changes the game state which is shown
	private GameState currentGameState = null;
	// currently selected token
	// if the game token is null, no token is selected
	private CanvasToken selectedToken = null;
	
	// is the AI working?
	private boolean aiWorking = false;
	// save if game is force ended
	private boolean gameQuitted;
	// is an animation blocking input?
	private boolean animationRunning = false;
	// save highlighted move
	private GameMove highlightedMove = null;
	
	// === SETTINGS ===
	private boolean rotateGameBoard = false;

	
	
	public CanvasState(GameCanvasViewController gameCanvasViewController) {
		this.viewController = gameCanvasViewController;
	}


	public GameCanvasViewController getViewController() {
		return viewController;
	}


	public void setViewController(GameCanvasViewController viewController) {
		this.viewController = viewController;
	}


	public CanvasToken getSelectedToken() {
		return selectedToken;
	}


	public void setSelectedToken(CanvasToken selectedToken) {
		this.selectedToken = selectedToken;
		if (selectedToken == null) {
			if (viewController.getGameBoardContainer() != null 
					&& viewController.getGameBoardContainer().getCanvasTokenClickHandler() != null) {
				viewController.getGameBoardContainer().getCanvasTokenClickHandler().unselectCanvasToken();
			}
		}
	}


	/**
	 * Get the current player (player on side down)
	 * @return
	 */
	public Player getCurrentPlayer() {
		if (this.currentGameState == null) {
			throw new NullPointerException("Game State is not set!");
		}
		return this.currentGameState.getCurrentPlayer();
	}


	public GameState getCurrentGameState() {
		return currentGameState;
	}
	
	public Game getCurrentGame () {
		return this.viewController.getOnitamaController().getOnitama().getCurrentGame();
	}

	/**
	 * Refresh everything for the new game state
	 */
	public void setCurrentGameState(GameState currentGameState) {
		this.currentGameState = currentGameState;
		setSelectedToken(null);
		this.highlightedMove = null;
		viewController.getGameBoardContainer().refresh();
		viewController.getCardContainer().refresh();
		if (viewController.getCanvasState().getRotateGameBoard()) {
			viewController.getRotatePane().setRotate(viewController.getAnimationController().getRotationOfGameState(currentGameState)); // rotate correct
		}
		viewController.getGameBoardContainer().getTargetMarkerContainer().setHighlightedCell(null); // reset highlight
		// do next move if AI
		newGameStateChecks();
	}


	public boolean isAiWorking() {
		return aiWorking;
	}


	public void setAIWorking(boolean aiWorking) {
		this.aiWorking = aiWorking;
	}


	public boolean isGameQuitted() {
		return this.gameQuitted;
	}
	
	public boolean isGameOver() {
		return (viewController.getOnitamaController().getGameStateController().getWinningPlayers(getCurrentGameState()).size() > 0);
	}
	
	/**
	 * Call all checks to be done after a new game move
	 */
	private void newGameStateChecks() {
		checkIsGameOver();
		checkAIPlayerTurn();
	}
	

	
	/**
	 * Check if the game is over
	 */
	private void checkIsGameOver() {
		if (isGameOver()) {
    		addHighscore();
			viewController.getGameOverScreen().setWinningPlayers(
				viewController.getOnitamaController().getGameStateController().getWinningPlayers(getCurrentGameState())
			);
		}
		viewController.getGameOverScreen().setVisible(isGameOver());
	}
	
	
	/**
	 * Checks if highscore is already added and adds it afterwards
	 */
	private void addHighscore() {
		if (getCurrentGame().isHighscoreEnabled()) {
			HighscoreController highScoreController = viewController.getOnitamaController().getHighscoreController();
			highScoreController.addToHighscore( viewController.getOnitamaController().getOnitama().getCurrentGame());
			getCurrentGame().setHighscoreEnabled(false);
		}
	}
	
	/**
	 * Make an AI player turn
	 */
	private void checkAIPlayerTurn() {
		if (!isGameQuitted() && !isGameOver()) {
			Player currentPlayer = getCurrentPlayer();
			if (!(currentPlayer.getPlayerType() == PlayerType.HUMAN)) {
				viewController.getAIController().doAIMove();
			}
		}
	}
	
	

	/**
	 * Checks if it is acceptable to input something
	 * @return
	 */
	public boolean accecptInput() {
		//return !(isGameOver() || aiWorking || animationRunning);
		return !(aiWorking || animationRunning);
	}


	public void setGameQuitted(boolean gameQuitted) {
		this.gameQuitted = gameQuitted;
	}


	public boolean isAnimationRunning() {
		return animationRunning;
	}


	public void setAnimationRunning(boolean animationRunning) {
		this.animationRunning = animationRunning;
	}

	/**
	 * Force set the game state, but without a refresh
	 * @param currentGameState
	 */
	public void setCurrentGameStateWithOutRefresh(GameState currentGameState) {
		this.currentGameState = currentGameState;
	}


	public boolean getRotateGameBoard() {
		return rotateGameBoard;
	}


	public void setRotateGameBoard(boolean rotateGameBoard) {
		this.rotateGameBoard = rotateGameBoard;
	}


	public GameMove getHighlightedMove() {
		return highlightedMove;
	}

	/**
	 * Highlight a game move
	 * @param highlightedMove
	 */
	public void setHighlightedMove(GameMove highlightedMove) {
		this.highlightedMove = highlightedMove;
		
		if (highlightedMove != null) {
			viewController.getCanvasState().setSelectedToken(null); // unselect
			viewController.getGameBoardContainer().highlightTip(highlightedMove);
			viewController.getCardContainer().highlightTip(highlightedMove.getSelectedCard());
		}
	}
}
