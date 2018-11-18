package view.gamecanvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import model.GameCard;
import model.GameState;
import model.Player;
import model.PlayerColor;
import view.GameCanvasViewController;
import view.Vector2D;

/**
 * This class contains the game card positions and game cards
 * @author Fabian Kemper
 */
public class CardContainer extends StackPane {
	private GameCanvasViewController viewController;
	
	// add positions for every player
	private Map<PlayerColor, List<Vector2D>> handCardPositions;
	// free card positions
	private Map<PlayerColor, Vector2D> freeCardPositions;
	
	// holds the canvas cards
	private List<CanvasCard> canvasCards;
	
	// =========================== settings
	// golden ratio is size of game board in the middle and size of cards
	private static final double GOLDEN_RATIO = 1.61803398875;
	private static final double INVERSE_GOLDEN_RATIO = 1d / GOLDEN_RATIO;
	private static final double ROTATION_OFFSET = 180d; // define positions of cards top ... 
	private static final double RATIO_LEFT_CARD = 1 - INVERSE_GOLDEN_RATIO; //1d/3d;
	private static final double RATIO_RIGHT_CARD = INVERSE_GOLDEN_RATIO; //2d/3d;
	private static final double HAND_CARD_CENTER = 1d/2d; // ratio of postion of hand card center - top is 0, bottom is 1
	private static final double FREE_CARD_Y_CENTER = 15d/24d;
	private static final double FREE_CARD_X_CENTER = 3d/24d;
	
	// dimensions
	// size of the complete canvas
	private Vector2D canvasSize = new Vector2D(100, 100);
	// size of the game board (i know, its redundant ... )
	private Vector2D boardSize = new Vector2D(0, 0);
	// size of panel around the game board
	private Vector2D borderSize = new Vector2D(0, 0);
	// max size of the game cards
	private Vector2D cardSize = new Vector2D(0, 0);
	
	public CardContainer(GameCanvasViewController viewController, Vector2D canvasSize) {
		this.viewController = viewController;
		this.setAlignment(Pos.TOP_LEFT);
		this.setPickOnBounds(false); // ignore transparent area
		
		// initialize the position map for all players
		handCardPositions = new EnumMap<PlayerColor, List<Vector2D>>(PlayerColor.class);
		freeCardPositions = new EnumMap<PlayerColor, Vector2D>(PlayerColor.class);
		canvasCards = new ArrayList<>();
		
		initDimensions(canvasSize);
		setCardPositions();
		reloadCards();
		refresh();
	}

	/**
	 * Re-Position gameCards
	 */
	public void refresh() {
		GameState currentGameState = this.viewController.getCanvasState().getCurrentGameState();
		Map<Player, List<GameCard>> currentCardDistribution = this.viewController.getCanvasState().getCurrentGameState().getCardDistribution();
		
		// draw hand cards
		for (Player player : currentGameState.getPlayers()) {
			List<Vector2D> positions = this.handCardPositions.get(player.getPlayerColor());
			List<GameCard> handCards = currentCardDistribution.get(player);
			
			if (positions == null) { throw new NullPointerException("Position is not set"); }
			if (handCards == null) { throw new NullPointerException("Error while fetching the hand Cards"); }
			if (positions.size() != handCards.size()) { throw new IllegalStateException("HandCards.size(): " + handCards.size() + " shoud be same as positions.size(): " + positions.size());}
			
			// get all cards in game an find position for them
			for (int i = 0; i < handCards.size(); i++) {
				CanvasCard canvasCard = findCanvasCardOf(handCards.get(i));
				if (canvasCard == null) {
					throw new NullPointerException("CanvasCard should not be null here.");
				}
				
				// is the player alive? if not, hide the cards
				canvasCard.setVisible(viewController.getOnitamaController().getPlayerController().isAlive(currentGameState, player));
				
				Vector2D position = positions.get(i);
				canvasCard.setPosition(position);
				canvasCard.setFreeCard(false);
				canvasCard.setRotate(-player.getPlayerColor().getAngle());
				canvasCard.setHighlighted(false);
				
				if (player.equals(viewController.getCanvasState().getCurrentPlayer())) {
					canvasCard.animateBloom(player.getPlayerColor());
				} else {
					canvasCard.animateBloom(null);
				}
			}
		}
		
		// adjust free card
		CanvasCard freeCard = findCanvasCardOf(currentGameState.getFreeGameCard());
		// next player
		Player currentPlayer = currentGameState.getCurrentPlayer();
		freeCard.setPosition(freeCardPositions.get(currentPlayer.getPlayerColor()));
		freeCard.setFreeCard(true);
		freeCard.animateBloom(null);
		freeCard.setRotate(-currentPlayer.getPlayerColor().getAngle());
	}
	
	/**
	 * Remove all animations from canvas cards
	 */
	public void removeGlowAnimations() {
		canvasCards.forEach((card) -> card.animateBloom(null));
	}
	
	/**
	 * 
	 */
	public void reloadCards() {
		this.getChildren().removeAll(canvasCards);
		canvasCards.clear();
		
		// get all cards
		List<GameCard> allCards = this.viewController.getCanvasState().getCurrentGameState().getAllCards();
		for (GameCard gameCard : allCards) {
			CanvasCard canvasCard = new CanvasCard(gameCard);
			canvasCard.setSize(cardSize);
			canvasCards.add(canvasCard);
		}
		this.getChildren().addAll(canvasCards);
		refresh();
	}
	
	/**
	 * Find a card on the game board
	 * @return null, if card not in canvas!
	 */
	private CanvasCard findCanvasCardOf(GameCard gameCard) {
		for (CanvasCard canvasCard : canvasCards) {
			if (canvasCard.getGameCard().equals(gameCard)) {
				return canvasCard;
			}
		}
		return null;
	}

	/**
	 * Creates the positions for every game card
	 */
	private void initDimensions(Vector2D canvasSize) {
		if (canvasSize == null) {
			throw new NullPointerException("Canvas size must be set.");
		}
		this.canvasSize = canvasSize;
		
		this.boardSize = new Vector2D(canvasSize.getX() * INVERSE_GOLDEN_RATIO, canvasSize.getY() * INVERSE_GOLDEN_RATIO);
		
		// size for the border around the game board
		this.borderSize = new Vector2D((canvasSize.getX() - boardSize.getX()) / 2, (canvasSize.getY() - boardSize.getY()) / 2);
		
		// card height ... should be minimum of boarder sizes
		double cardHeight;
		if (borderSize.getX() < borderSize.getY()) {
			cardHeight = borderSize.getX() * INVERSE_GOLDEN_RATIO;
		} else {
			cardHeight = borderSize.getY() * INVERSE_GOLDEN_RATIO;
		}
		double cardWidth = cardHeight * GOLDEN_RATIO;
		this.cardSize = new Vector2D(cardWidth, cardHeight);
	}
	
	/**
	 * Sets the card positions
	 */
	private void setCardPositions() {
		handCardPositions.clear();
		freeCardPositions.clear();
		
		// get border height from cardHeight ... (the shorter side is saved there), little hacky here
		double borderHeight = cardSize.getY() * GOLDEN_RATIO;
		
		// shorter side of canvas
		double canvasMinLength;
		if (canvasSize.getX() < canvasSize.getY()) {
			canvasMinLength = canvasSize.getX();
		} else {
			canvasMinLength = canvasSize.getY();
		}
		
		Vector2D leftCard = new Vector2D(canvasMinLength*RATIO_LEFT_CARD, borderHeight * HAND_CARD_CENTER);
		Vector2D rightCard = new Vector2D(canvasMinLength*RATIO_RIGHT_CARD, borderHeight * HAND_CARD_CENTER);
		Vector2D canvasCenter = new Vector2D(canvasSize.getX() / 2, canvasSize.getY() / 2);
		
		for (PlayerColor color : PlayerColor.values()) {
			double rotationAngle = ROTATION_OFFSET - (double) color.getAngle();
			// This is important: add the right card fist, so a 
			// card swap don't mess up our animation!
			List<Vector2D> handPositions = new ArrayList<Vector2D>(Arrays.asList(
				Vector2D.rotateVectorCC(rightCard, canvasCenter, Math.toRadians(rotationAngle)),
				Vector2D.rotateVectorCC(leftCard, canvasCenter, Math.toRadians(rotationAngle))
			));
			Vector2D freeCardPosition = new Vector2D(canvasMinLength * FREE_CARD_X_CENTER, borderHeight * FREE_CARD_Y_CENTER);
			
			handCardPositions.put(color, handPositions);
			freeCardPositions.put(color, Vector2D.rotateVectorCC(freeCardPosition, canvasCenter, Math.toRadians(rotationAngle)));
		}
	}
	
	/**
	 * Local class for position, angle pairs
	 * @author Fabian Kemper
	 *
	 */
	private class CardPosition {
		private Vector2D position;
		private int angle;
		
		private CardPosition(Vector2D position, int angle) {
			this.position = position;
			this.angle = angle;
		}
	}
	
	/** 
	 * calculate the position of a card
	 * @param gameCard
	 * @return
	 */
	private CardPosition getCardPosition(GameState gameState, GameCard gameCard) {
		// is card free card?
		if (gameState.getFreeGameCard().equals(gameCard)) {
			PlayerColor playerColor = gameState.getCurrentPlayer().getPlayerColor();
			return new CardPosition(freeCardPositions.get(playerColor), -playerColor.getAngle());
		} else {
			for (Player player : gameState.getPlayers()) {
				List<GameCard> cards = gameState.getCardDistribution().get(player);
				if (cards.size() != 2) { throw new IllegalStateException("Expect exactly two hand cards."); }
				for (int index = 0; index < cards.size(); index++) {
					if (cards.get(index).equals(gameCard)) {
						
						Vector2D position = handCardPositions.get(player.getPlayerColor()).get(index);
						int angle = -player.getPlayerColor().getAngle();
						return new CardPosition(position, angle);
					}
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Animate a new card distribution ...
	 * @return
	 */
	
	public void animateCardDistribuiton(GameState oldGameState, GameState newGameState) {
		removeGlowAnimations();
		Player oldPlayer = oldGameState.getCurrentPlayer();
		
		List<GameCard> oldPlayerCards = oldGameState.getCardDistribution().get(oldPlayer);
		List<CanvasCard> canvasCards = oldPlayerCards.stream().map((card) -> findCanvasCardOf(card)).collect(Collectors.toList());
		canvasCards.add(findCanvasCardOf(oldGameState.getFreeGameCard()));
		
		canvasCards.forEach((card) -> {
			CardPosition pos = this.getCardPosition(newGameState, card.getGameCard());
			boolean isFreeCard = card.getGameCard().equals(newGameState.getFreeGameCard());
			// rotate always clockwise (new angle > old angle)
			if (card.getRotate() > pos.angle) {
				pos.angle += 360;
			}
			card.animateTo(pos.position, pos.angle, isFreeCard);
		});
	}
	
	/**
	 * Animate a fade of of the cards
	 * @param player
	 */
	public void animateRemoveCards(Player player) {
		// get cards of player
		List<GameCard> playerCards = viewController.getCanvasState().getCurrentGameState().getCardDistribution().get(player);
		
		List<CanvasCard> removeCards = canvasCards.stream().filter(
			entry -> playerCards.contains(entry.getGameCard()) 
		).collect(Collectors.toList());
		
		removeCards.forEach(card -> card.animateFadeAway(viewController.getAnimationController(), player));
	}

	public GameCanvasViewController getViewController() {
		return viewController;
	}

	public void setViewController(GameCanvasViewController viewController) {
		this.viewController = viewController;
	}

	/**
	 * Search card and highlight it
	 * @param selectedCard
	 */
	public void highlightTip(GameCard selectedCard) {
		CanvasCard canvasCard = findCanvasCardOf(selectedCard);
		if (canvasCard != null) {
			canvasCard.setHighlighted(true);
		}
		
	}

	
}
