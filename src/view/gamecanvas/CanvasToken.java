package view.gamecanvas;

import assets.player.tokens.PlayerTokenAssets;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.GameToken;
import model.Player;
import model.PlayerColor;
import model.Vector;
import view.GameCanvasViewController;
import view.Vector2D;

/**
 * This class is added on the canvas
 * @author fabian
 *
 */

public class CanvasToken extends ImageView {
	private GameCanvasViewController viewController;
	
	private GameToken token;
	private Vector position = new Vector (0, 0);
	private Vector2D cellSize = new Vector2D(40, 40);
	private Vector2D offset = new Vector2D(0, 0);
	private PlayerColor angle; // if angle = token color, this token shows the picture for "down"
	private boolean highlighted = false;
	
	// Field in the middle has size of golden ratio!
	private static final double GOLDEN_RATIO = 1.61803398875;
	// max token size in percent of field size
	private final static double CELL_SIZE_TOKEN_SIZE_RATIO = 1 / GOLDEN_RATIO;
	
	// duration for the token to move exactly one cell ... 
	// for longer distances the duration is calculated
	private static final double ANIM_MOVEMENT_MS_PER_CELL = 300;
	
	// highlight
	private static final int GLOW_DURATION = 350;
	private static final double GLOW_STROKE_MAX = 0.5;
	private static final double GLOW_STROKE_MIN = 0;
	
	
	public CanvasToken(GameCanvasViewController viewController, GameToken token, CanvasTokenClickHandler clickHandler) {
		this.viewController = viewController;
		this.token = token;
		this.setPickOnBounds(true); // allow a click in transparent region
		angle = token.getPlayer().getPlayerColor();
		
		// Get graphic for this token
		Image graphic;
		graphic = PlayerTokenAssets.getGameTokenImage(token, angle);
		
		this.setImage(graphic);
		this.setAngle(angle);
		
		// if active player, make clickable
		if (this.viewController.getCanvasState().getCurrentGameState().getCurrentPlayer().equals(token.getPlayer())) {
			this.setCursor(Cursor.HAND);
		}
		
		if (clickHandler != null) {
			this.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
		} else {
			System.out.println("Warning: Adding a GameCanvasToken without a click handler!");
		}

	}
	
	public PlayerColor getAngle() {
		return angle;
	}

	public void setAngle(PlayerColor angle) {
		this.angle = angle;
		this.setRotate(-angle.getAngle());
	}

	public GameToken getToken() {
		return token;
	}

	public void setToken(GameToken token) {
		this.token = token;
	}
	
	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
		Vector2D effectivePosition = calculatePosition(position);
		this.setTranslateX(effectivePosition.getX());
		this.setTranslateY(effectivePosition.getY());
	}
	
	private Vector2D calculatePosition(Vector position) {
		return new Vector2D(
			offset.getX() + position.getX() * cellSize.getX(),
			offset.getY() + position.getY() * cellSize.getY()
		);
	}
	
	/**
	 * For animation, animate to target pos
	 * @param target
	 */
	public void animatePosition(Vector target, EventHandler<ActionEvent> onAnimFinished) {
		int animDuration = animationDuration(this.position, target);
		Vector2D targetPosition = calculatePosition(target);
		this.toFront(); // make top layer
		Timeline timeline = new Timeline(
			new KeyFrame(Duration.ZERO,
				new KeyValue(this.translateXProperty(), this.getTranslateX(), Interpolator.EASE_IN),
				new KeyValue(this.translateYProperty(), this.getTranslateY(), Interpolator.EASE_IN)
			),
			new KeyFrame(Duration.millis(animDuration),
				onAnimFinished,
				new KeyValue(this.translateXProperty(), targetPosition.getX(), Interpolator.EASE_OUT),
				new KeyValue(this.translateYProperty(), targetPosition.getY(), Interpolator.EASE_OUT)
			)
		);
		timeline.play();
	}

	/**
	 * helper function for animate position
	 * calculates the animation time via euclidean distance
	 */
	private int animationDuration(Vector origin, Vector target) {
		Vector distance = new Vector(origin.getX() - target.getX(), origin.getY() - target.getY());
		double euclidianDistance = Math.sqrt(Math.pow(distance.getX(), 2) + Math.pow(distance.getY(), 2));
		return (int) (euclidianDistance * ANIM_MOVEMENT_MS_PER_CELL);
	}
	
	/**
	 * sets the size of this token
	 * @param size
	 */
	public void setCellSize(Vector2D cellSize) {
		if (cellSize == null) { throw new NullPointerException("Size must be set"); }
		this.cellSize = cellSize;
		Vector2D tokenSize = new Vector2D(cellSize.getX() * CELL_SIZE_TOKEN_SIZE_RATIO, cellSize.getY() * CELL_SIZE_TOKEN_SIZE_RATIO);
		
		this.offset = new Vector2D(
			(cellSize.getX() - tokenSize.getX()) / 2,
			(cellSize.getY() - tokenSize.getY()) / 2);
		
		this.setFitWidth(tokenSize.getX());
		this.setFitHeight(tokenSize.getY());
		//System.out.println(tokenSize.getPrintableString());
		// reposition
		setPosition(this.position);
	}
	
	public Vector2D getCellSize() {
		return this.cellSize;
	}

	/** 
	 * Shake animation
	 */
	public void animateElimination() {
		ShakeTransition animation = new ShakeTransition(this, null);
		animation.playFromStart();
	}

	/**
	 * Amimates the face away animation, same as in CanvasCard ... damn
	 * @param animationController
	 * @param player
	 */
	public void animateFadeAway(CanvasAnimationController animationController, Player player) {
		if (this.getToken().isMaster()) {
			//animateElimination();
			return;
		}
		
		Vector2D startPos = new Vector2D(this.getTranslateX(), this.getTranslateY());
		Vector2D targetPos = animationController.getFlowOutPosition(startPos, player.getPlayerColor());
		
		Timeline timeline = new Timeline(
			new KeyFrame(Duration.ZERO,
				new KeyValue(this.translateXProperty(), startPos.getX(), Interpolator.LINEAR),
				new KeyValue(this.translateYProperty(), startPos.getY(), Interpolator.LINEAR),
				new KeyValue(this.opacityProperty(), this.getOpacity(), Interpolator.LINEAR)
			),
			new KeyFrame(Duration.millis(CanvasAnimationController.AINM_WASH_AWAY_DELAY),
				new KeyValue(this.translateXProperty(), startPos.getX(), Interpolator.EASE_BOTH),
				new KeyValue(this.translateYProperty(), startPos.getY(), Interpolator.EASE_BOTH),
				new KeyValue(this.opacityProperty(), this.getOpacity(), Interpolator.EASE_BOTH)
			),
			new KeyFrame(Duration.millis(CanvasAnimationController.ANIM_WASH_AWAY_DURATION + CanvasAnimationController.AINM_WASH_AWAY_DELAY),
				new KeyValue(this.translateXProperty(), targetPos.getX()),
				new KeyValue(this.translateYProperty(), targetPos.getY()),
				new KeyValue(this.opacityProperty(), 0)
			)
		);
		timeline.play();
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
		if (highlighted) {
			//Instantiating the Glow class 
		    Glow glow = new Glow(); 
		    this.setEffect(glow);
			
			// animate pulsing glow
			Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO,
					new KeyValue(glow.levelProperty(), GLOW_STROKE_MIN, Interpolator.EASE_BOTH)
				),
				new KeyFrame(Duration.millis(GLOW_DURATION),
					new KeyValue(glow.levelProperty(), GLOW_STROKE_MAX, Interpolator.EASE_BOTH)
				)
			);
			timeline.setAutoReverse(true);
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.play();
			this.setEffect(glow);
		} else {
			this.setEffect(null);
		}
	}

}
