package view.gamecanvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import assets.player.icons.PlayerColorAssets;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import model.GameMove;
import model.PlayerColor;
import model.Vector;
import view.Vector2D;

/**
 * This class represents a target marker if the player selected a token
 * 
 * Information: javafx.scene.layout.Region does support translateX etc. javafx.scene.Group does not support this ... sadly ...
 * 
 * @author Fabian Kemper
 *
 */
public class TargetMarker extends Group {

	private PlayerColor color;
	private Vector2D cellSize = new Vector2D(40, 40);
	private Vector2D offset = new Vector2D(0, 0); // offset center to top_left
	private Vector position = new Vector(0, 0);
	private PlayerColor angle;
	private List<GameMove> gameMoves;
	private TargetMarkerClickHandler clickHandler;
	private boolean isEliminatingMove = false;
	private boolean animationAdded = false;
	
	// background .. test
	private Canvas eliminationMarker;
	// contains image
	private ImageView image;
	private boolean highlighted = false;
	
	// Field in the middle has size of golden ratio!
	private static final double GOLDEN_RATIO = 1.61803398875;
	// max token size in percent of field size
	private final static double CELL_SIZE_TOKEN_SIZE_RATIO = 1 / GOLDEN_RATIO;
	private final static double TARGET_MARKER_TOKEN_RATIO = 1.1;
	
	// ============ ANIMATION SETTINGS =============
	// fade in delay max
	private final static int FADE_IN_DELAY_MAX = 200;
	private final static int FADE_IN_DURATION = 100;
	private final static int FADE_OUT_DELAY_MAX = 100;
	private final static int FADE_OUT_DURATION = 100;
	
	// inset for elimiation marker
	private final static double ELIMINATION_MARKER_OUTSET = 0.6;
	private final static double ELIMINATION_MARKER_INSET = 1.6;
	private final static int TARGET_MARKER_ROTATION_SPEED = 1000; 
	// opacities 
	private final static double OPACITY_DEFAULT = 0.5;
	private final static double OPACITY_ELIMINATION = 0;
	
	// highlight
	private static final int GLOW_DURATION = 350;
	private static final double GLOW_STROKE_MAX = 1;
	private static final double GLOW_STROKE_MIN = 0;
	
	
	public TargetMarker(PlayerColor color) {
		this.getChildren().add(new Canvas()); // fix offset errors
		
		// background
		this.eliminationMarker = new Canvas();
		this.eliminationMarker.setVisible(false);
		drawEliminationMarker();
		this.getChildren().add(eliminationMarker);
		
		// unclickable!
		eliminationMarker.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> event.consume());
		
		// image 
		this.image = new ImageView();
		this.getChildren().add(image);
		image.setCursor(Cursor.HAND); // hand cursor on image
		
		setColor(color);	
		// first add an image an then rotate this
		setAngle(color);
		//this.setPickOnBounds(true);
		image.setPickOnBounds(true); // the transparent area also allows a click
		gameMoves = new ArrayList<>();
	}


	public PlayerColor getColor() {
		return color;
	}

	/**
	 * fetches graphic for targetMarker
	 * @param color
	 */
	public void setColor(PlayerColor color) {
		this.color = color;
		drawEliminationMarker();
		image.setImage(PlayerColorAssets.getPlayerIcon(color));
		setIsEliminatingMove(isEliminatingMove);
	}


	public Vector2D getCellSize() {
		return cellSize;
	}
	
	private void drawEliminationMarker() {
		Vector2D tokenSize = new Vector2D(cellSize.getX() * CELL_SIZE_TOKEN_SIZE_RATIO, cellSize.getY() * CELL_SIZE_TOKEN_SIZE_RATIO);
		Vector2D targetSize = new Vector2D(Math.min(tokenSize.getX(), tokenSize.getY()) * TARGET_MARKER_TOKEN_RATIO, 
				Math.min(tokenSize.getX(), tokenSize.getY()) * TARGET_MARKER_TOKEN_RATIO);
		
		// position top left corner
		eliminationMarker.setWidth(targetSize.getX());
		eliminationMarker.setHeight(targetSize.getY());
		eliminationMarker.setTranslateX((cellSize.getX() - targetSize.getX()) / 2);
		eliminationMarker.setTranslateY((cellSize.getY() - targetSize.getY()) / 2);
		
		GraphicsContext gc = eliminationMarker.getGraphicsContext2D();
		gc.clearRect(0, 0, cellSize.getX(), cellSize.getY());
		gc.setLineWidth(5);
		// color of team
		if (this.color != null) {
			Color color = this.color.getColor();
			color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.7);
			gc.setStroke(color);
		}
		double radius = Math.min(targetSize.getX() / 2, targetSize.getY() / 2);
		
		Vector2D center = new Vector2D((targetSize.getX()) / 2, (targetSize.getY()) / 2);
		Vector2D edge   = new Vector2D(
				(1 - Math.sin(Math.PI / 4)) * radius, 
				(1 - Math.cos(Math.PI / 4)) * radius);
		Vector2D outset = new Vector2D(edge.getX() * ELIMINATION_MARKER_OUTSET, edge.getY() * ELIMINATION_MARKER_OUTSET);
		Vector2D inset  = new Vector2D(edge.getX() * ELIMINATION_MARKER_INSET,  edge.getY() * ELIMINATION_MARKER_INSET);
		
		// mark corners
		for (int theta = 0; theta < 360; theta += 90) {
			Vector2D start = Vector2D.rotateVectorCC(outset, center, Math.toRadians(theta));
			Vector2D end   = Vector2D.rotateVectorCC(inset, center, Math.toRadians(theta));
			gc.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
		}
		// draw circle
		gc.strokeOval(3, 3,	(radius-3)*2, (radius-3)*2); // no clipped circle
	}

	public void setCellSize(Vector2D cellSize) {
		if (cellSize == null) { throw new NullPointerException("Size must be set"); }
		this.cellSize = cellSize;
		
		Vector2D tokenSize = new Vector2D(cellSize.getX() * CELL_SIZE_TOKEN_SIZE_RATIO, cellSize.getY() * CELL_SIZE_TOKEN_SIZE_RATIO);
		
		this.offset = new Vector2D(
			(cellSize.getX() - tokenSize.getX()) / 2,
			(cellSize.getY() - tokenSize.getY()) / 2);
		
		image.setFitWidth(tokenSize.getX());
		image.setFitHeight(tokenSize.getY());
		
		// reposition
		setPosition(this.position);
		drawEliminationMarker();
	}
	
	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
		this.setTranslateX((double) position.getX() * cellSize.getX());
		this.setTranslateY((double) position.getY() * cellSize.getY());
		image.setTranslateX(offset.getX());
		image.setTranslateY(offset.getY());
	}

	public List<GameMove> getGameMoves() {
		return gameMoves;
	}


	public void setGameMoves(List<GameMove> gameMoves) {
		this.gameMoves = gameMoves;
	}


	public PlayerColor getAngle() {
		return angle;
	}


	public void setAngle(PlayerColor angle) {
		if (angle == null) {
			throw new NullPointerException("Angle must be set.");
		}
		this.angle = angle;
		image.setRotate(-angle.getAngle());
	}

	public void setClickHandler(TargetMarkerClickHandler clickHandler) {
		this.clickHandler = clickHandler;
		this.setOnMouseClicked(clickHandler);
	}
	
	public TargetMarkerClickHandler getClickHandler() {
		return this.clickHandler;
	}

	public void setIsEliminatingMove(boolean isEliminatingMove) {
		//isEliminatingMove = true; // to test the target marker
		this.isEliminatingMove = isEliminatingMove;
		if (isEliminatingMove) {
			image.setOpacity(OPACITY_ELIMINATION);
			this.eliminationMarker.setVisible(true);
		} else {
			image.setOpacity(OPACITY_DEFAULT);
			this.eliminationMarker.setVisible(false);
		}
	}
	
	public boolean getIsEliminatingMove() {
		return isEliminatingMove;
	}
	
	
	private void addEliminatingAnimation() {
		if (animationAdded) { return; }
		animationAdded = true;
		
		Timeline timeline = new Timeline(
			new KeyFrame(
				Duration.ZERO,
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						
					}
				},
				//new KeyValue(this.eliminationMarker.scaleXProperty(), 1, Interpolator.EASE_BOTH),
				//new KeyValue(this.eliminationMarker.scaleYProperty(), 1, Interpolator.EASE_BOTH),
				new KeyValue(this.eliminationMarker.rotateProperty(), 0, Interpolator.LINEAR)
			),
			new KeyFrame(
				Duration.millis(TARGET_MARKER_ROTATION_SPEED),
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						
					}
				},
				//new KeyValue(this.eliminationMarker.scaleXProperty(), .6, Interpolator.EASE_BOTH),
				//new KeyValue(this.eliminationMarker.scaleYProperty(), .6, Interpolator.EASE_BOTH),
				new KeyValue(this.eliminationMarker.rotateProperty(), 90, Interpolator.LINEAR)
			)
		);
		timeline.setCycleCount(Animation.INDEFINITE);
		//timeline.setAutoReverse(true);
		timeline.play();
	}
	
	/**
	 * Called to show a target marker on the pane, do not refresh directly after this animation
	 */
	public void fadeInAnimation() {
		if (this.isEliminatingMove) {
			double elStartScale = 0.5;
			eliminationMarker.setOpacity(0);
			
			Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO,
					new KeyValue(eliminationMarker.scaleXProperty(), elStartScale, Interpolator.LINEAR),
					new KeyValue(eliminationMarker.scaleYProperty(), elStartScale, Interpolator.LINEAR),
					new KeyValue(eliminationMarker.rotateProperty(), -45, Interpolator.LINEAR),
					new KeyValue(eliminationMarker.opacityProperty(), 0, Interpolator.LINEAR)
				),
				new KeyFrame(Duration.millis(FADE_IN_DURATION),
					(event) -> { addEliminatingAnimation(); },
					new KeyValue(eliminationMarker.scaleXProperty(), 1, Interpolator.EASE_OUT),
					new KeyValue(eliminationMarker.scaleYProperty(), 1, Interpolator.EASE_OUT),
					new KeyValue(eliminationMarker.rotateProperty(), 0, Interpolator.EASE_OUT),
					new KeyValue(eliminationMarker.opacityProperty(), 1, Interpolator.LINEAR)
				)
			);
			timeline.play();
		} else {
			// this is a normal move token 
			Random random = java.util.concurrent.ThreadLocalRandom.current();
			int startOffset = random.nextInt(FADE_IN_DELAY_MAX);
			
			final double startScale = 0.4;
			final double startOpacity = 0;
			
			Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO,
					new KeyValue(image.scaleXProperty(), startScale, Interpolator.LINEAR),
					new KeyValue(image.scaleYProperty(), startScale, Interpolator.LINEAR),
					new KeyValue(image.opacityProperty(), startOpacity, Interpolator.LINEAR)
				),
				new KeyFrame(Duration.millis(startOffset),
					new KeyValue(image.scaleXProperty(), startScale, Interpolator.LINEAR),
					new KeyValue(image.scaleYProperty(), startScale, Interpolator.LINEAR),
					new KeyValue(image.opacityProperty(), startOpacity, Interpolator.LINEAR)
				),
				new KeyFrame(Duration.millis(startOffset + FADE_IN_DURATION),
					new KeyValue(image.scaleXProperty(), 1, Interpolator.EASE_OUT),
					new KeyValue(image.scaleYProperty(), 1, Interpolator.EASE_OUT),
					new KeyValue(image.opacityProperty(), OPACITY_DEFAULT, Interpolator.LINEAR)
				)
			);
			
			timeline.play();
		}
	}
	
	/**
	 * Called to remove the target marker from the target marker container
	 * @param isTargetMove
	 */
	public void fadeOutAnimation(boolean isTargetMove) {
		this.image.setOnMouseClicked(null); // is doubled but just to be sure
		this.image.setCursor(Cursor.DEFAULT);
		
		Random random = java.util.concurrent.ThreadLocalRandom.current();
		int startOffset = random.nextInt(FADE_OUT_DELAY_MAX);
		
		if (this.isEliminatingMove) {
			// eliminating move
			double targetScale = .4;
			
			if (isTargetMove) {
				startOffset = 0;
			}
			
			Timeline timeline = new Timeline(
				new KeyFrame(Duration.millis(startOffset),
					new KeyValue(eliminationMarker.scaleXProperty(), image.getScaleX(), Interpolator.EASE_IN),
					new KeyValue(eliminationMarker.scaleYProperty(), image.getScaleY(), Interpolator.EASE_IN),
					new KeyValue(eliminationMarker.opacityProperty(), image.getOpacity(), Interpolator.LINEAR)
				),
				new KeyFrame(Duration.millis(FADE_OUT_DURATION + startOffset),
					(event) -> { setVisible(false); },
					new KeyValue(eliminationMarker.scaleXProperty(), targetScale, Interpolator.LINEAR),
					new KeyValue(eliminationMarker.scaleYProperty(), targetScale, Interpolator.LINEAR),
					new KeyValue(eliminationMarker.opacityProperty(), 0, Interpolator.LINEAR)
				)
			);
			timeline.play();
		} else {
			// default move
			double targetScale = .4;
			if (isTargetMove) {
				targetScale = 1.4;
				startOffset = 0;
			}
			
			Timeline timeline = new Timeline(
				new KeyFrame(Duration.millis(startOffset),
					new KeyValue(image.scaleXProperty(), image.getScaleX(), Interpolator.EASE_IN),
					new KeyValue(image.scaleYProperty(), image.getScaleY(), Interpolator.EASE_IN),
					new KeyValue(image.opacityProperty(), image.getOpacity(), Interpolator.LINEAR)
				),
				new KeyFrame(Duration.millis(startOffset + FADE_OUT_DURATION),
					(event) -> { setVisible(false); },
					new KeyValue(image.scaleXProperty(), targetScale, Interpolator.LINEAR),
					new KeyValue(image.scaleYProperty(), targetScale, Interpolator.LINEAR),
					new KeyValue(image.opacityProperty(), 0, Interpolator.LINEAR)
				)
			);
			timeline.play();
		}
	}
	
	

	/**
	 * This overrides "your turn"
	 * @param highlighted
	 */
	public void setHighlighted(boolean highlighted) {
		this.highlighted   = highlighted;
		if (highlighted) {
			//Instantiating the Glow class 
		    Glow glow = new Glow(); 
		    this.setEffect(glow);
		    DropShadow shadow = new DropShadow();
		    shadow.setColor(new Color(.3, .3, .3, .5));
		    
		    //glow.setInput(shadow);
		    shadow.setInput(glow);
			
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
			this.setEffect(shadow);
		} else {
			this.setEffect(null);
		}
	}

	public boolean isHighlighted() {
		return highlighted;
	}
	
}
