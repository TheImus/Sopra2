package view.gamecanvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import assets.gamecard.GameCardAssets;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import model.GameMove;
import view.GameCanvasViewController;
import view.Vector2D;

public class CardPickerOverlay extends StackPane {
	private GameCanvasViewController viewController;
	private Vector2D canvasSize;
	
	// layers
	private Canvas backgroundCanvas;
	private List<CardPickerCard> cards = new ArrayList<>();
	private List<Vector2D> cardPositions;
	
	// golden ratio ... for position of game cards, and sizes
	private static final double GOLDEN_RATIO = 1.61803398875;
	private static final double INVERSE_GOLDEN_RATIO = 1d / GOLDEN_RATIO;
	private static final double POS_X_LEFT_CARD = 17d/24d;
	private static final double POS_X_RIGHT_CARD = 7d/24d;
	private static final double CARD_WIDTH_FACTOR = 1d/3d;
	private static final int MOVES_CARD_COUNT = 2;
	private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 0.75);
	private Vector2D cardSize = new Vector2D(100, 100);
	
	
	public CardPickerOverlay(GameCanvasViewController viewController, Vector2D canvasSize) {
		this.viewController = viewController;
		this.setAlignment(Pos.TOP_LEFT);
		this.setPickOnBounds(false); // ignore transparent area

		// create background canvas
		backgroundCanvas = new Canvas();
		this.getChildren().add(backgroundCanvas);
		
		setCanvasSize(canvasSize);
		initPositions();
	}

	private void initPositions() {
		double cardWidth = canvasSize.getX() * CARD_WIDTH_FACTOR;
		this.cardSize = new Vector2D(
			cardWidth, cardWidth * INVERSE_GOLDEN_RATIO
		);
		// first the right card, this is the same order as in the hand positions
		cardPositions = new ArrayList<>(Arrays.asList(
			new Vector2D(canvasSize.getX() * POS_X_RIGHT_CARD, canvasSize.getY() / 2),
			new Vector2D(canvasSize.getX() * POS_X_LEFT_CARD, canvasSize.getY() / 2)
		));
	}

	public Vector2D getCanvasSize() {
		return canvasSize;
	}

	public void setCanvasSize(Vector2D canvasSize) {
		this.canvasSize = canvasSize;
		repaintBackground();
	}
	
	
	private void repaintBackground() {
		GraphicsContext graphicsContext = backgroundCanvas.getGraphicsContext2D();
		backgroundCanvas.setWidth(canvasSize.getX());
		backgroundCanvas.setHeight(canvasSize.getY());
		graphicsContext.clearRect(0, 0, canvasSize.getX(), canvasSize.getY());
		graphicsContext.setFill(BACKGROUND_COLOR);
		graphicsContext.fillRect(0, 0, canvasSize.getX(), canvasSize.getY());
	}
	
	
	/** 
	 * shows the card picker
	 * @param gameMoves
	 */
	public void pickCards(List<GameMove> gameMoves) {
		if (gameMoves.size() != MOVES_CARD_COUNT) { throw new IllegalStateException("Expect exactly two cards"); }
		if (cardPositions.size() != MOVES_CARD_COUNT) { throw new IllegalStateException("Expect exactly two card positions"); }
		this.setVisible(true);
		
		this.getChildren().removeAll(cards); // clear old cards
		cards.clear();

		for (int index = 0; index < gameMoves.size(); index++) {
			GameMove move = gameMoves.get(index);
			CardPickerCard newCard = new CardPickerCard(move, cardSize);
			newCard.setPosition(cardPositions.get(index));
			newCard.setCursor(Cursor.HAND);
			if (viewController.getCanvasState().getHighlightedMove() != null) {
				if (viewController.getCanvasState().getHighlightedMove().getSelectedCard()
						.equals(newCard.getGameMove().getSelectedCard())) {
					newCard.setHighlighted(true);
				}
			}
			newCard.setOnMouseClicked(new CardPickerClickHandler(this));
			cards.add(newCard);
		}
		this.getChildren().addAll(cards);
	}
	
	
	/**
	 * A Class representing a card in picker dialog
	 * @author sopr025
	 *
	 */
	private class CardPickerCard extends ImageView {
		private GameMove gameMove;
		private Vector2D offset; // center point
		
		// highlight
		// TODO: pack all these in one class ... 
		private static final int GLOW_DURATION = 350;
		private static final double GLOW_STROKE_MAX = 0.5;
		private static final double GLOW_STROKE_MIN = 0;
		
		
		protected CardPickerCard(GameMove gameMove, Vector2D cardSize) {
			this.gameMove = gameMove;
			setImage(assets.gamecard.GameCardAssets.getGameCardImage(gameMove.getSelectedCard(), GameCardAssets.Resolution.HIGH));
			setCardSize(cardSize);
		}
		
		public void setCardSize(Vector2D cardSize) {
			this.setFitWidth(cardSize.getX());
			this.setFitHeight(cardSize.getY());
			//System.out.println(cardSize.getPrintableString());
			// calculate center point
			double aspectRatio = this.getImage().getWidth() / this.getImage().getHeight();
			double realWidth   = Math.min(this.getFitWidth(),  this.getFitHeight() * aspectRatio);
			double realHeight  = Math.min(this.getFitHeight(), this.getFitWidth() / aspectRatio);
			this.offset = new Vector2D(-realWidth / 2, -realHeight / 2); //center point
		}
		
		public void setPosition(Vector2D center) {
			this.setTranslateX(offset.getX() + center.getX());
			this.setTranslateY(offset.getY() + center.getY());
		}

		public GameMove getGameMove() {
			return gameMove;
		}
		
		public void setHighlighted(boolean highlighted) {
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
	
	private class CardPickerClickHandler implements EventHandler<MouseEvent> {
		private CardPickerOverlay picker;
		
		protected CardPickerClickHandler(CardPickerOverlay picker) {
			this.picker = picker;
		}
		
		@Override
		public void handle(MouseEvent event) {
			if (!(event.getSource() instanceof CardPickerCard)) { throw new IllegalStateException("Expected a CardPickerCard here"); }
			CardPickerCard cardPickerCard = (CardPickerCard) event.getSource();
			
			// Hide picker
			picker.setVisible(false);
			viewController.invokeGameMove(cardPickerCard.getGameMove());
		}
	}

}
