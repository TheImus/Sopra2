package view.gamecanvas;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import assets.player.startpos.PlayerStartposAssets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import model.GameBoard;
import model.GameMove;
import model.GameState;
import model.GameToken;
import model.Player;
import model.PlayerColor;
import model.Vector;
import view.GameCanvasViewController;
import view.Vector2D;

/**
 * The GameBoard to be shown in the game canvas
 * 
 * This Class includes the background grid and Game tokens
 * 
 * @author Fabian Kemper
 *
 */
public class BoardContainer extends ScrollPane {
	// Instance of "Parent" view
	private GameCanvasViewController viewController;
	
	// the angle of board (color which is now down)
	private PlayerColor angle = PlayerColor.EARTH;

	// the size of this game board
	private Vector2D viewPortSize = new Vector2D(100, 100);
	
	// Contains gameTokens 
	private List<CanvasToken> gameCanvasTokens = new ArrayList<CanvasToken>();
	
	// === LAYERS
	// start markers
	private List<StartMarker> startMarkers;
	// grid of the game board
	private Canvas gameBoardGrid;
	// the labels 
	private List<Canvas> gridLabels;
	// contains everything of this scroll pane 
	private StackPane container;
	// contains tokens
	private StackPane tokenContainer;
	// contains target markers
	private TargetMarkerContainer targetMarkerContainer;
	
	// click handler
	private CanvasTokenClickHandler canvasTokenClickHandler;
	
	// Cell count of the board
	private Vector2D cellCount = new Vector2D(5, 5);
	// dimensions of the game board
	private Vector2D cellSize = new Vector2D(0, 0); 			// size of a single cell 
	private Vector2D boardSize = new Vector2D(0, 0);			// size the complete board (not the viewport!)
	
	
	// SETTINGS ==============================
	// minimal offset of fields
	protected static final Vector2D FIELD_OFFSET = new Vector2D(1, 1); // offset of the grid
	// draw the scroll pane a little bigger to prevent scroll bars
	private final Vector2D SCROLL_PANE_OVERSIZE = new Vector2D(2, 2);
	// Fields for board without scroll bars
	private final double CELLS_WITHOUT_SCROLLBARS = 11;
	// start marker opacity
	private final double START_MARKER_OPACITY = 0.5;
	// label offset to bottom
	private final double LABEL_BOTTOM_MARGIN = 2; // pixel
	private final double LABEL_OFFSET_X = -3; 
	private final int GAME_BOARD_SIDE_COUNT = 4; // this is so stupid ... 
	

	
	
	public BoardContainer(GameCanvasViewController viewController, Vector2D viewPortSize) {
		if (viewController == null) {
			throw new NullPointerException("View controller must be set.");
		}
		if (viewPortSize == null) {
			throw new NullPointerException("Need a size for the gameBoard");
		}
		
		// init the game board
		this.viewController = viewController;
		this.viewPortSize = viewPortSize;
		
		this.gameCanvasTokens = new ArrayList<CanvasToken>();
		this.startMarkers = new ArrayList<StartMarker>();
		container = new StackPane();
		container.setAlignment(Pos.TOP_LEFT); // align figures to top left (calculations should fit)
		this.setContent(container);
		
		// start position markers
		for (PlayerColor color : PlayerColor.values()) {
			startMarkers.add(new StartMarker(color));
		}
		container.getChildren().addAll(startMarkers);
		
		// grid with labels
		gameBoardGrid = new Canvas();
		container.getChildren().add(gameBoardGrid);
		
		gridLabels = new ArrayList<>();
		for (int index = 0; index < GAME_BOARD_SIDE_COUNT; index++) {
			gridLabels.add(new Canvas());
		}
		container.getChildren().addAll(gridLabels);
		
		// game tokens
		tokenContainer = new StackPane();
		tokenContainer.setAlignment(Pos.TOP_LEFT);
		container.getChildren().add(tokenContainer);
		
		// target markers
		targetMarkerContainer = new TargetMarkerContainer(this);
		targetMarkerContainer.setAlignment(Pos.TOP_LEFT);
		targetMarkerContainer.setPickOnBounds(false); // allow mouse click events to pass through to layer below
		container.getChildren().add(targetMarkerContainer);
		
		// click handler für this game board
		canvasTokenClickHandler = new CanvasTokenClickHandler(viewController);
		
		init();
	}
	
	private void init() {
		initDimensions(new Vector2D(getGameBoard().getSize(), getGameBoard().getSize()));
		
		this.setMaxWidth (this.viewPortSize.getX() + FIELD_OFFSET.getX() + SCROLL_PANE_OVERSIZE.getX());  
		this.setMaxHeight(this.viewPortSize.getY() + FIELD_OFFSET.getY() + SCROLL_PANE_OVERSIZE.getY());
		container.setPrefWidth(boardSize.getX());
		container.setPrefHeight(boardSize.getY());

		drawStartingMarkers();
		drawGridCanvas(gameBoardGrid);
		drawGridLabels(gridLabels);
		refresh();
	}
	
	/** 
	 * Sets all initial dimensions for the game board
	 */
	private void initDimensions(Vector2D cellCount) {
		this.cellCount = cellCount;
		
		if (this.cellCount.getX() > CELLS_WITHOUT_SCROLLBARS) {
			cellSize.setX(viewPortSize.getX() / CELLS_WITHOUT_SCROLLBARS);
		} else {
			cellSize.setX(viewPortSize.getX() / cellCount.getX());
		}
		
		if (this.cellCount.getY() > CELLS_WITHOUT_SCROLLBARS) {
			cellSize.setY(viewPortSize.getY() / CELLS_WITHOUT_SCROLLBARS);
		} else {
			cellSize.setY(viewPortSize.getY() / cellCount.getY());
		}
		
		this.boardSize = new Vector2D(
			cellSize.getX() * cellCount.getX(),
			cellSize.getY() * cellCount.getY());
	}
	
	
	/**
	 * Draws starting markers
	 */
	private void drawStartingMarkers() {
		Vector2D center = new Vector2D(
				(cellCount.getX() / 2) * cellSize.getX() - cellSize.getX() / 2, 
				(cellCount.getY() / 2) * cellSize.getY() - cellSize.getY() / 2);
		Vector2D border = new Vector2D(
				(cellCount.getX() - 1) * cellSize.getX() + 4, 
				(cellCount.getY() - 1) * cellSize.getY() + 4);
		
		for (StartMarker marker : startMarkers) {
			PlayerColor color = marker.getColor();
			marker.setFitHeight(cellSize.getX());
			marker.setFitWidth(cellSize.getY());
			marker.setImage(PlayerStartposAssets.getPlayerIcon(color));
			marker.setOpacity(START_MARKER_OPACITY);
			
			switch(color) {
			case EARTH: 
				marker.setTranslateX(center.getX());
				marker.setTranslateY(border.getY());
				break;
			case FIRE: 
				marker.setTranslateX(center.getX());
				marker.setTranslateY(0);
				break;
			case WATER: 
				marker.setTranslateX(0);
				marker.setTranslateY(center.getY());
				break;
			case AIR: 
				marker.setTranslateX(border.getX());
				marker.setTranslateY(center.getY());
				break;
			}
		}
	}
	
	/**
	 * Draws the background grid
	 * 
	 * @param gridCanvas
	 */
	private void drawGridCanvas(Canvas gridCanvas) {		
		// prepare canvas
		gridCanvas.setWidth(boardSize.getX() + 1); // one bigger to fit line in
		gridCanvas.setHeight(boardSize.getY() + 1); // one bigger to fit line in
		// prepare brush for drawing lines
		GraphicsContext graphic = gridCanvas.getGraphicsContext2D();
		graphic.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight()); // clear canvas
		graphic.setFill(null); // no fill
		graphic.setStroke(new Color(0.2, 0.2, 0.2, 0.5)); // dark gray
		graphic.setLineWidth(2);
		
		// horizontal
		for (int i = 0; i <= cellCount.getY(); i++) {
			double offset = (double) i * cellSize.getY();
			graphic.strokeLine(0, offset+FIELD_OFFSET.getY(), boardSize.getY(), offset+FIELD_OFFSET.getY());
		}
		// vertical
		for (int i = 0; i <= cellCount.getX(); i++) {
			double offset = (double) i * cellSize.getX();
			graphic.strokeLine(offset+FIELD_OFFSET.getX(), 0, offset+FIELD_OFFSET.getX(), boardSize.getX());
		}
	}
	
	/**
	 * Draws the labels on the grid
	 */
	private void drawGridLabels(List<Canvas> labels) {
		for (int canvasIndex = 0; canvasIndex < GAME_BOARD_SIDE_COUNT; canvasIndex++) {
			Canvas canvas  = labels.get(canvasIndex);
			
			canvas.setWidth(boardSize.getX() + 1);
			canvas.setHeight(boardSize.getY() + 1);
			
			GraphicsContext graphic = canvas.getGraphicsContext2D();
			graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			graphic.setFill(new Color(.2, .2, .2, .8));
			
			// calculate center
			Vector2D centerOfCell = new Vector2D(cellSize.getX() / 2, cellSize.getY() / 2);
			
			// a, b, c, ... if x is rising
			// ... , 3, 2, 1 ... if y is rising (keep that in mind!)
			// always write on the bottom of the canvas and rotate canvas afterwards
			boolean reversed = (canvasIndex) == 1 || (canvasIndex == 2);
			
			if (canvasIndex % 2 == 0) {
				// letter
				double posYalpha = boardSize.getY() - LABEL_BOTTOM_MARGIN;
				for (int index = 0; index < cellCount.getX(); index++) {
					double posXalpha = cellSize.getX() * index + LABEL_OFFSET_X + centerOfCell.getX();
					int printIndex = reversed ? ((int) cellCount.getX() - index - 1) : index; 
					graphic.fillText(Vector.numberToLetter(printIndex).toUpperCase(), posXalpha, posYalpha);
				}
			} else {
				// numeric
				double posYalpha = boardSize.getX() - LABEL_BOTTOM_MARGIN;
				for (int index = 0; index < cellCount.getY(); index++) {
					double posXalpha = cellSize.getY() * index + LABEL_OFFSET_X + centerOfCell.getY();
					int printIndex = reversed ? ((int) cellCount.getY() - index) : index + 1; 
					graphic.fillText(String.valueOf(printIndex), posXalpha, posYalpha);
				}
				
			}
			canvas.setRotate(canvasIndex * 90);
		}
	}
	
	/**
	 * Removes old game tokens and add a new set of game tokens to this board
	 */
	public void refresh() {
		refreshStartingMarkers();
		refreshGameTokens();
		refreshTargetMarkers();
	}
	
	private void refreshStartingMarkers() {
		GameState currentGameState = viewController.getCanvasState().getCurrentGameState();
		startMarkers.forEach(marker -> marker.setVisible(false));
		if (currentGameState != null) {
			currentGameState.getPlayers()
				.forEach(
					player -> startMarkers.stream()
					.filter(marker -> marker.getColor().equals(player.getPlayerColor()))
					.collect(Collectors.toList()).forEach(item -> item.setVisible(true))
			);
		}
	}

	/**
	 * Refresh game tokens
	 */
	private void refreshGameTokens() {
		// remove all tokens
		this.tokenContainer.getChildren().removeAll(this.gameCanvasTokens);
		this.gameCanvasTokens.clear();
		
		List<GameToken> tokens = getGameBoard().getAllTokens();
		for (GameToken token : tokens) {
			Vector position = getGameBoard().getPositionOf(token);
			CanvasToken canvasToken = new CanvasToken(viewController, token, canvasTokenClickHandler);
			canvasToken.setPosition(position);
			canvasToken.setCellSize(cellSize);
			canvasToken.setHighlighted(false);
			this.gameCanvasTokens.add(canvasToken);
		}
		this.tokenContainer.getChildren().addAll(this.gameCanvasTokens);
	}
	
	/**
	 * redraw the target markers, they jump directly on target positions
	 */
	private void refreshTargetMarkers() {
		this.canvasTokenClickHandler.selectCanvasToken(viewController.getCanvasState().getSelectedToken());
		this.targetMarkerContainer.refresh();
	}
	
	private GameBoard getGameBoard() {
		GameState gameState = this.viewController.getCanvasState().getCurrentGameState();
		if (gameState == null) {
			throw new NullPointerException("No game state set to view");
		}
		return gameState.getGameBoard();
	}
	
	/**
	 * Returns the current orientation of the game board
	 * @return
	 */
	public PlayerColor getAngle() {
		return angle;
	}
	
	/**
	 * Returns the canvas Token of a game token
	 * @param token
	 * @return
	 */
	public CanvasToken findCanvasTokenOf(GameToken token) {
		for (CanvasToken canvasToken : gameCanvasTokens) {
			if (token.equals(canvasToken.getToken())) {
				return canvasToken;
			}
		}
		return null;
	}

	/**
	 * Set Angle of graphics
	 * @param angle
	 */
	public void setAngle(PlayerColor angle) {
		this.angle = angle;
		refresh();
	}

	public GameCanvasViewController getViewController() {
		return viewController;
	}

	public void setViewController(GameCanvasViewController viewController) {
		this.viewController = viewController;
	}

	public TargetMarkerContainer getTargetMarkerContainer() {
		return targetMarkerContainer;
	}

	public void setTargetMarkerContainer(TargetMarkerContainer targetMarkerContainer) {
		this.targetMarkerContainer = targetMarkerContainer;
	}

	public Vector2D getCellSize() {
		return cellSize;
	}
	
	public void setGridLabelsVisible(boolean value) {
		//this.gridLabels.setVisible(value);
		gridLabels.forEach((canvas) -> canvas.setVisible(value));
	}

	/**
	 * Eliminate token of game board (with a fresh animation!)
	 * @param token
	 */
	public void eliminateToken(GameToken token) {
		// find token
		CanvasToken canvasToken = this.findCanvasTokenOf(token);
		
		if (canvasToken == null) { 
			throw new NullPointerException("CanvasToken not found ... ");
		}
		
		canvasToken.animateElimination();
	}

	/**
	 * Flow away canvas tokens
	 * @param player
	 */
	public void animateRemoveTokens(Player player) {
		// find canvas tokens of player
		List<CanvasToken> canvasTokens = gameCanvasTokens.stream().filter(
			element -> element.getToken().getPlayer().equals(player)
		).collect(Collectors.toList());
		
		canvasTokens.forEach(token -> { token.toBack(); token.animateFadeAway(viewController.getAnimationController(), player); } );
	}
	
	/**
	 * Find token and highlight it
	 * @param movedToken
	 * @param selectedMove
	 */
	public void highlightTip(GameMove highlightedMove) {
		// find token
		CanvasToken canvasToken = this.findCanvasTokenOf(highlightedMove.getMovedToken());
		canvasToken.setHighlighted(true);
		
		Vector position = canvasToken.getPosition();
		Player player = highlightedMove.getMovedToken().getPlayer();
		Vector moveTarget = position.addTo(highlightedMove.getSelectedMove().rotate(player));
		
		// get target move
		targetMarkerContainer.setHighlightedCell(moveTarget);
	}

	public CanvasTokenClickHandler getCanvasTokenClickHandler() {
		return canvasTokenClickHandler;
	}
}
