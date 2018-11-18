package view.gamecanvas;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Player;
import view.GameCanvasViewController;
import view.Vector2D;

public class GameOverScreen extends StackPane {
	// parent
	private GameCanvasViewController viewController;
	// sizes
	private Vector2D canvasSize;
	
	// layers
	private Canvas backgroundCanvas;
	private Canvas textCanvas;
	private Button toHighscore;
	
	// winning tokens
	private List<GameOverWinningToken> masterTokens = new ArrayList<>();
	
	// local sizes
	private double textTopOffset = 100;
	private double toHighscreenTopOffset = 200;
	private double tokenTopOffset = 300;
	
	// Settings ====
	private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 0.88);
	private static final Color TEXT_COLOR = new Color(.9, .9, .9, 0.88); 
	
	private static Vector2D tokenSize = new Vector2D(50, 50);
	
	
	private static final double TEXT_TOP_OFFSET_FACTOR = 0.33;
	private static final double TOKEN_TOP_OFFSET_FACTOR = 0.50;
	private static final double TO_HIGHSCORE_TOP_OFFSET_FACTOR = 0.66;
	private static final double TO_HIGHSCORE_BUTTON_WIDTH = 200;
	private static final double TO_HIGHSCORE_BUTTON_HEIGHT = 40;
	private static final double TOKEN_CANVAS_SIZE_FACTOR = 0.12;
	private static final double TOKEN_GAP = 30; 
	private static final double TEXT_WIDTH_1 = 325;
	private static final double TEXT_WIDTH_2 = 365;
	private static final double TEXT_LINE_HEIGHT = 50;
	
	
	
	public GameOverScreen (GameCanvasViewController viewController, Vector2D canvasSize) {
		this.setAlignment(Pos.TOP_LEFT);
		//this.viewController = viewController;
		
		this.backgroundCanvas = new Canvas();
		this.getChildren().add(this.backgroundCanvas);
		
		this.textCanvas = new Canvas();
		this.getChildren().add(this.textCanvas);
		
		this.toHighscore = new Button();
		this.toHighscore.setPrefWidth(TO_HIGHSCORE_BUTTON_WIDTH);
		this.toHighscore.setPrefHeight(TO_HIGHSCORE_BUTTON_HEIGHT);
		this.toHighscore.setText("Zum Highscore");
		this.toHighscore.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				viewController.releaseAUIHook();
				viewController.getLoadViewController().loadHighscoreView();
			}
		});
		this.getChildren().add(toHighscore);
		
		setCanvasSize(canvasSize);
	}

	public Vector2D getCanvasSize() {
		return canvasSize;
	}
	
	public void setCanvasSize(Vector2D canvasSize) {
		this.canvasSize = canvasSize;
		textTopOffset = canvasSize.getY() * TEXT_TOP_OFFSET_FACTOR;
		tokenTopOffset = canvasSize.getY() * TOKEN_TOP_OFFSET_FACTOR;
		toHighscreenTopOffset = canvasSize.getY() * TO_HIGHSCORE_TOP_OFFSET_FACTOR;
		
		double tokenSideLength = Math.min(canvasSize.getX(), canvasSize.getY()) * TOKEN_CANVAS_SIZE_FACTOR;
		tokenSize = new Vector2D(tokenSideLength, tokenSideLength);
		
		repaintBackground();
		repaintWonText();
		repaintHighscoreButton();
	}
	
	private void repaintBackground() {
		GraphicsContext graphicsContext = backgroundCanvas.getGraphicsContext2D();
		backgroundCanvas.setWidth(canvasSize.getX());
		backgroundCanvas.setHeight(canvasSize.getY());
		graphicsContext.clearRect(0, 0, canvasSize.getX(), canvasSize.getY());
		graphicsContext.setFill(BACKGROUND_COLOR);
		graphicsContext.fillRect(0, 0, canvasSize.getX(), canvasSize.getY());
	}
	
	private void repaintWonText() {
		GraphicsContext graphicsContext = textCanvas.getGraphicsContext2D();
		textCanvas.setWidth(canvasSize.getX());
		textCanvas.setHeight(canvasSize.getY());
		graphicsContext.clearRect(0, 0, canvasSize.getX(), canvasSize.getY());
		graphicsContext.setFill(TEXT_COLOR);

		graphicsContext.setFont(new Font(32));
		graphicsContext.fillText("Das Spiel ist vorbei!", (canvasSize.getX()-TEXT_WIDTH_1)/2, textTopOffset, TEXT_WIDTH_1);
		graphicsContext.fillText("Die Sieger stehen fest:", (canvasSize.getX()-TEXT_WIDTH_2)/2, textTopOffset+TEXT_LINE_HEIGHT, TEXT_WIDTH_2);
	}
	
	private void repaintHighscoreButton() {
		toHighscore.setTranslateY(toHighscreenTopOffset);
		toHighscore.setTranslateX((this.canvasSize.getX() - TO_HIGHSCORE_BUTTON_WIDTH) / 2);
	}
	
	/**
	 * Show the master token of winning players
	 * @param playersWon
	 */
	public void setWinningPlayers(List<Player> playersWon) {
		this.getChildren().removeAll(masterTokens);
		masterTokens.clear();
		
		// get positions for new master tokens
		List<Vector2D> winningTokenPositions = getWinningTokenPositions(playersWon);
		
		for (int index = 0; index < playersWon.size(); index++) {
			Player player = playersWon.get(index);
			Vector2D position = winningTokenPositions.get(index);
			
			// add a token
			GameOverWinningToken canvasToken = new GameOverWinningToken(player, tokenSize);
			canvasToken.setSize(tokenSize);
			canvasToken.setPosition(position);
			masterTokens.add(canvasToken);
		}
		
		this.getChildren().addAll(masterTokens);
	}

	private List<Vector2D> getWinningTokenPositions(List<Player> playersWon) {
		// calculate positions of players who won
		List<Vector2D> result = new ArrayList<>();
		int playerCount = playersWon.size();
		if (playerCount == 0) { throw new IllegalStateException("Player won called, without a player who won ... suspecious"); }
		
		double length = (playerCount - 1) * (TOKEN_GAP + tokenSize.getX());
		double center = canvasSize.getX() / 2;

		for (int index = 0; index < playerCount; index++) {
			// Position is center  ... so left starts at -length/2 
			// each other token is tokenSize + token_gap more left
			result.add(new Vector2D(center + index*(tokenSize.getX() + TOKEN_GAP) - (length/2), tokenTopOffset));
		}
		
		return result;
	}

	public GameCanvasViewController getViewController() {
		return viewController;
	}

	public void setViewController(GameCanvasViewController viewController) {
		this.viewController = viewController;
	}
}
