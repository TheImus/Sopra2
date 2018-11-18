package view.gamecanvas;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import model.GameMove;
import model.GameState;
import model.GameToken;
import model.Player;
import model.PlayerColor;
import model.Vector;
import view.GameCanvasViewController;
import view.Vector2D;

public class CanvasAnimationController {
	private GameCanvasViewController viewController;
	
	// ==== SETTINGS FOR ANIMATION ====
	private static final int ANIM_ROTATION_DELAY = 1000;
	private static final int ANIM_ROTATION_DURATION = 500; // milliseconds!

	private static final int ANIM_MOVE_CARD_CORRECTION = -120;
	
	public static final int AINM_WASH_AWAY_DELAY = 400;
	public static final int ANIM_WASH_AWAY_DURATION = 1000;
	
	public CanvasAnimationController(GameCanvasViewController viewController) {
		this.viewController = viewController;
	}
	

	/**
	 * Animate the next game board with a move from the previous one
	 * @param oldGameState
	 * @param newGameState
	 */
	public void animateNextGameState(GameState oldGameState, GameState newGameState) {
		viewController.getCanvasState().setAnimationRunning(true);
		animateTokenMove(oldGameState, newGameState);
		animateCardMove(oldGameState, newGameState);
	}


	/************************************************************************************
	 * TOKEN MOVEMENT
	 ***********************************************************************************/
	
	private void animateTokenMove(GameState oldGameState, GameState newGameState) {
		GameMove gameMove = newGameState.getLastMove();
		GameToken token = gameMove.getMovedToken();
		Vector startPosition = oldGameState.getGameBoard().getPositionOf(token);
		Vector targetPosition = newGameState.getGameBoard().getPositionOf(token);
		
		if (startPosition == null || targetPosition == null) {
			throw new NullPointerException("Token not found on game board");
		}
		
		// kick token if eliminated
		if (newGameState.getLastMove().getDefeatedToken() != null) {
			GameToken defeatedToken = newGameState.getLastMove().getDefeatedToken();
			viewController.getGameBoardContainer().eliminateToken(defeatedToken);
		}
		
		// remove game board target markers
		viewController.getGameBoardContainer().getTargetMarkerContainer().animateTargetSelected(targetPosition);

		// Animate the Token move
		CanvasToken canvasToken = viewController.getGameBoardContainer().findCanvasTokenOf(token);
		if (canvasToken == null) { throw new NullPointerException("Canvas token not found."); }
		canvasToken.animatePosition(targetPosition, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				rotateGameBoard(oldGameState, newGameState);
			}
		});
	}
	
	
	/************************************************************************************
	 * CARD DISTRIBUTION
	 ***********************************************************************************/
	
	private void animateCardMove(GameState oldGameState, GameState newGameState) {
		viewController.getCardContainer().animateCardDistribuiton(oldGameState, newGameState);
	}
	
	
	/************************************************************************************
	 * ROTATION
	 ***********************************************************************************/
	
	/**
	 * Rotate the game board counter clockwise until the next player is reached
	 * @param oldGameState
	 * @param newGameState
	 */
	private void rotateGameBoard(GameState oldGameState, GameState newGameState) {
		OnAnimationNextGameStateFinish finishAnim = new OnAnimationNextGameStateFinish();
		
		if (!viewController.getCanvasState().getRotateGameBoard()) {
			new Timeline(
				new KeyFrame(Duration.ZERO),
				new KeyFrame(Duration.millis(CanvasCard.ANIM_MOVE_CARD_DURATION +ANIM_MOVE_CARD_CORRECTION), finishAnim)
			).play();
			return;
		}
		
		Timeline timeline = new Timeline();
		
		int oldAngle = getRotationOfGameState(oldGameState);
		int newAngle = getRotationOfGameState(newGameState);
		
		if (oldAngle < newAngle) {
			newAngle -= 360; // rotate always clockwise!
		}
		
		// do not rotate if same side is already down
		if (Math.abs((int) viewController.getRotatePane().getRotate() - newAngle) % 360 == 0) {
			newAngle = (int) viewController.getRotatePane().getRotate(); 
		}
		int rotationDuration = (Math.abs(newAngle - oldAngle) / 90) * ANIM_ROTATION_DURATION; 
		
		timeline.getKeyFrames().addAll(
			new KeyFrame(Duration.millis(ANIM_ROTATION_DELAY), // start at 0
				/*new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						System.out.println("Started");
					}
				},*/
				//new KeyValue(viewController.getRotatePane().rotateProperty(), oldAngle, Interpolator.EASE_IN)
				new KeyValue(viewController.getRotatePane().rotateProperty(), viewController.getRotatePane().getRotate(), Interpolator.EASE_IN)
			),
			new KeyFrame(Duration.millis(ANIM_ROTATION_DELAY + rotationDuration), 
				finishAnim,
				new KeyValue(viewController.getRotatePane().rotateProperty(), newAngle, Interpolator.EASE_OUT)
			)
		);
		timeline.play();
	}
	
	/** 
	 * Returns the angle for game of game board of given game state
	 * @param gameState
	 * @return angle in degree
	 */
	public int getRotationOfGameState(GameState gameState) {
		return gameState.getCurrentPlayer().getPlayerColor().getAngle();
	}

	/**
	 * Implements the call to finish method of finished rotation
	 * @author Fabian Kemper
	 *
	 */
	private class OnAnimationNextGameStateFinish implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			viewController.getCanvasState().setAnimationRunning(false); // this is the last command
			viewController.refreshGameState(); // force a reload of game board
		}
	}
	
	
	/************************************************************************************
	 * Killing player
	 ***********************************************************************************/
	
	/**
	 * Animate fade off, of one player
	 */
	public void eliminatePlayer(Player player) {
		viewController.getCardContainer().animateRemoveCards(player);
		viewController.getGameBoardContainer().animateRemoveTokens(player);
	}
	
	
	/**
	 * Get Position for remove
	 * 
	 * needed for tokens and cards
	 * 
	 * @param position now
	 * @param color Playre color
	 * @return
	 */
	public Vector2D getFlowOutPosition(Vector2D position, PlayerColor color) {
		Vector2D posOffset = new Vector2D(0, GameCanvasViewController.WINDOW_SIZE_DEFAULT);
		Vector2D nullPos = new Vector2D(0, 0);
		return position.addTo(Vector2D.rotateVectorCC(posOffset, nullPos, Math.toRadians(-color.getAngle())));
	}
	
}
