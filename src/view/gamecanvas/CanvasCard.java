package view.gamecanvas;

import assets.gamecard.GameCardAssets;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import model.GameCard;
import model.Player;
import model.PlayerColor;
import view.Vector2D;


/**
 * 
 * @author Fabian Kemper
 *
 */
public class CanvasCard extends ImageView {
	private GameCard gameCard;
	private Vector2D size;
	private Vector2D offset = new Vector2D(0, 0); // offset in positon to be placed on center position
	private Vector2D position = new Vector2D(0, 0);
	private boolean isFreeCard = false;
	private boolean highlighted = false;
	
	public static final int ANIM_MOVE_CARD_DURATION = 800;
	private static final double FREE_CARD_OPACITY = 0.7;
	
	private static final int SHADOW_DURATION = 500;
	private static final double SHADOW_STROKE_MAX = 0.5;
	private static final double SHADOW_STROKE_MIN = 0;
	
	// highlight
	private static final int GLOW_DURATION = 350;
	private static final double GLOW_STROKE_MAX = 0.5;
	private static final double GLOW_STROKE_MIN = 0;
	
	public CanvasCard(GameCard gameCard) {
		this.setGameCard(gameCard);
		this.setSmooth(true);
	}

	public Vector2D getSize() {
		return size;
	}

	public void setSize(Vector2D size) {
		this.size = size;
		this.setFitWidth(size.getX());
		this.setFitHeight(size.getY());
		
		double aspectRatio = this.getImage().getWidth() / this.getImage().getHeight();
		double realWidth   = Math.min(this.getFitWidth(),  this.getFitHeight() * aspectRatio);
		double realHeight  = Math.min(this.getFitHeight(), this.getFitWidth() / aspectRatio);
		this.offset = new Vector2D(-realWidth / 2, -realHeight / 2);
	}

	public GameCard getGameCard() {
		return gameCard;
	}

	public void setGameCard(GameCard gameCard) {
		this.gameCard = gameCard;
		this.setImage(assets.gamecard.GameCardAssets.getGameCardImage(gameCard, GameCardAssets.Resolution.LOW));	
	}

	public Vector2D getPosition() {
		return position;
	}
	
	public void setPosition(Vector2D position) {
		this.position = position;
		this.setTranslateX(position.getX() + offset.getX());
		this.setTranslateY(position.getY() + offset.getY());
	}
	
	
	public void animateTo(Vector2D position, int angle, boolean isFreeCard) {
		double newOpacity = isFreeCard ? FREE_CARD_OPACITY : 1;
		Timeline timeline = new Timeline(
			new KeyFrame(Duration.ZERO,
				new KeyValue(translateXProperty(), getTranslateX(), Interpolator.EASE_IN),
				new KeyValue(translateYProperty(), getTranslateY(), Interpolator.EASE_IN),
				new KeyValue(this.opacityProperty(), getOpacity(), Interpolator.LINEAR),
				new KeyValue(rotateProperty(), getRotate(), Interpolator.EASE_IN)
			), 
			new KeyFrame(Duration.millis(ANIM_MOVE_CARD_DURATION),
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						setPosition(position);
						setFreeCard(isFreeCard);
					}
				},
				new KeyValue(translateXProperty(), position.getX() + offset.getX(), Interpolator.EASE_OUT),
				new KeyValue(translateYProperty(), position.getY() + offset.getY(), Interpolator.EASE_OUT),
				new KeyValue(this.opacityProperty(), newOpacity, Interpolator.LINEAR),
				new KeyValue(rotateProperty(), angle, Interpolator.EASE_OUT)
			)
		);
		
		timeline.play();
	}

	public boolean isFreeCard() {
		return isFreeCard;
	}

	public void setFreeCard(boolean isFreeCard) {
		this.isFreeCard = isFreeCard;
		double newOpacity = isFreeCard ? FREE_CARD_OPACITY : 1;
		this.setOpacity(newOpacity);
	}

	
	/**
	 * Set a bloom effect for this canvas card
	 * @param value
	 */
	public void animateBloom(PlayerColor color) {
		if (color == null) {
			this.setEffect(null);
		} else {
			// drop inner shadow
			InnerShadow innerShadow = new InnerShadow();
			innerShadow.setBlurType(BlurType.GAUSSIAN);
			innerShadow.setRadius(25);
			innerShadow.setColor(color.getColor());
			
			// animate pulsing shadow
			Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO,
					new KeyValue(innerShadow.chokeProperty(), SHADOW_STROKE_MIN, Interpolator.EASE_BOTH)
				),
				new KeyFrame(Duration.millis(SHADOW_DURATION),
					new KeyValue(innerShadow.chokeProperty(), SHADOW_STROKE_MAX, Interpolator.EASE_BOTH)
				)
			);
			timeline.setAutoReverse(true);
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.play();
			
			this.setEffect(innerShadow);
		}

	}
	
	/**
	 * Amimates the face away animation
	 * @param animationController
	 * @param player
	 */
	public void animateFadeAway(CanvasAnimationController animationController, Player player) {
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

	/**
	 * This overrides "your turn"
	 * @param highlighted
	 */
	public void setHighlighted(boolean highlighted) {
		this.highlighted  = highlighted;
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

	public boolean isHighlighted() {
		return highlighted;
	}
	
	
}
