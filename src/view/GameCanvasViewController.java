package view;


import java.io.File;
import java.io.IOException;
import java.util.Optional;

import controller.GameStateController;
import controller.NewGameStateAUI;
import controller.OnitamaController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.GameMove;
import model.GameState;
import model.Player;
import view.LoadGameViewController.NextWindow;
import view.gamecanvas.*;


// TODO: HELP HERE
// https://docs.oracle.com/javafx/2/canvas/jfxpub-canvas.htm

public class GameCanvasViewController implements NewGameStateAUI {
	@FXML
	private GridPane basePane;
	@FXML
	private StackPane gamePane; // base with fixed background
	@FXML
	private StackPane rotatePane;
	@FXML
	private ImageView rotatingBackgroundCanvas;	// rotation background
    @FXML
    private RadioMenuItem toggleRotateGameBoard;
    @FXML
    private RadioMenuItem toggleShowLabels;
	
	// external controllers
	private OnitamaController onitamaController;
	private LoadViewController loadViewController;
	private MoveHistoryController moveHistoryController = null;
	
	// internal controller
	private CanvasAIPlayerController aiController;
	private CanvasAnimationController animationController;
	
	// layers
	private BoardContainer gameBoardContainer;   	// contains grid with figures
	private CardContainer cardContainer; 		// same as game board for cards
	private CardPickerOverlay cardPicker;		// overlay for card choosing
	private GameOverScreen gameOverScreen;		// overlay for game over
	
	// manages the current state of the gui
	private CanvasState canvasState;
	
	// already initialized?
	private boolean initialized = false;
	
	// TODO: Change this to an observable list
	// https://stackoverflow.com/questions/41513455/javafx-observablelist-and-lists-item-change
	
	/**
	 * SETTINGS ================================================
	 */
	// Field in the middle has size of golden ratio!
	public static final double GOLDEN_RATIO = 1.61803398875;
	// min size of game board
	public static final double WINDOW_SIZE_DEFAULT = 800;
	// game board size
	private double gameGridSize = WINDOW_SIZE_DEFAULT / GOLDEN_RATIO;
	
	// rotate game board per default
	private final boolean ROTATE_GAMEBOARD_DEFAULT = false;
	// show grid label per default
	private final boolean SHOW_GRID_LABEL_DEFAULT = false;

	
	public void init() {
		// allow only one initialize
		if (initialized) {
			return;
		}
		initialized = true;
		
		// animation controller
		animationController = new CanvasAnimationController(this);
		
		// set size of game board
		loadViewController = new LoadViewController(onitamaController);
		rotatingBackgroundCanvas.setFitWidth(WINDOW_SIZE_DEFAULT);
		rotatingBackgroundCanvas.setFitHeight(WINDOW_SIZE_DEFAULT);
		rotatingBackgroundCanvas.setImage(assets.gameboard.GameBoardBackground.getBackground());
		
		// init canvas state 
		this.canvasState = new CanvasState(this);
		if (this.getOnitamaController() == null) { throw new NullPointerException("Can not fetch onitama controlller"); }
		if (this.getOnitamaController().getOnitama() == null) { throw new NullPointerException("Can not get onitama"); }
		if (this.getOnitamaController().getOnitama().getCurrentGame() == null) { throw new NullPointerException("No current game set"); }
		GameState currentGameState = this.getOnitamaController().getOnitama().getCurrentGame().getCurrentGameState();
		this.canvasState.setCurrentGameStateWithOutRefresh(currentGameState);

		// contains canvas tokens and the token target marker layer
		gameBoardContainer = new BoardContainer(this, new Vector2D(gameGridSize, gameGridSize));
		gameBoardContainer.setLayoutX((WINDOW_SIZE_DEFAULT - gameGridSize) / 2);
		gameBoardContainer.setLayoutY((WINDOW_SIZE_DEFAULT - gameGridSize) / 2);
		rotatePane.getChildren().add(gameBoardContainer);
		
		// container of game cards
		cardContainer = new CardContainer(this, new Vector2D(WINDOW_SIZE_DEFAULT, WINDOW_SIZE_DEFAULT));
		rotatePane.getChildren().add(cardContainer);
		
		// card picker overlay
		cardPicker = new CardPickerOverlay(this, new Vector2D(WINDOW_SIZE_DEFAULT, WINDOW_SIZE_DEFAULT));
		gamePane.getChildren().add(cardPicker);
		cardPicker.setVisible(false);
		
		// game over screen
		gameOverScreen = new GameOverScreen(this, new Vector2D(WINDOW_SIZE_DEFAULT, WINDOW_SIZE_DEFAULT));
		gamePane.getChildren().add(gameOverScreen);
		gameOverScreen.setVisible(false); 
		
		// get controller for AI players
    	aiController = new CanvasAIPlayerController(this);
		
		// set for AUI Änderung -- GameStateAUI ist eine Liste
    	onitamaController.getGameStateController().addAUI(this);

		// settings for game canvas
		toggleRotateGameBoard.selectedProperty().setValue(ROTATE_GAMEBOARD_DEFAULT);
    	canvasState.setRotateGameBoard(toggleRotateGameBoard.isSelected());
		
		toggleShowLabels.selectedProperty().setValue(SHOW_GRID_LABEL_DEFAULT);
		getGameBoardContainer().setGridLabelsVisible(toggleShowLabels.isSelected());
		
		
		// this invokes refresh game state!!!
		this.canvasState.setCurrentGameState(currentGameState);
		
		// start game now
		//refreshGameState(); // not needed	private Canvas backgroundCanvas;
	}

	/**
	 * Invokes a game move
	 */
	public void invokeGameMove(GameMove gameMove) {
		if (gameMove == null || canvasState.getCurrentGameState() == null) { throw new NullPointerException("gameMove or canvasState.currentGameState is null"); } 
		
		if (!canvasState.isGameQuitted()) {
			GameStateController gameStateController = getOnitamaController().getGameStateController();
			GameState nextGameState = gameStateController.generateNextGameState(canvasState.getCurrentGameState(), gameMove);
			
			gameStateController.setNextGameState(nextGameState);
		}
	}
	
	
	/**
	 * Hard set a new game state
	 */
	@Override
	public void refreshGameState() {
		//System.out.println("======== YOU CALLED REFRESH GAME STATE =========");
		GameState currentGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();
		this.getCanvasState().setCurrentGameState(currentGameState);
	}

	@Override
	public void animateNewGameState(GameState oldGameState, GameState newGameState) {
		//System.out.println("======== YOU CALLED ANIMATE NEW GAME STATE =========");
		animationController.animateNextGameState(oldGameState, newGameState);
	}
	
	@Override
	public void playerLost(Player player) {
		//System.out.println("======== YOU CALLED PLAYER LOST =========");
		animationController.eliminatePlayer(player);
	}
	
	public BoardContainer getGameBoardContainer() {
		return gameBoardContainer;
	}

	public OnitamaController getOnitamaController() {
		return onitamaController;
	}

	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}
	
	public CanvasState getCanvasState() {
		return canvasState;
	}

	public void setCanvasState(CanvasState canvasState) {
		this.canvasState = canvasState;
	}
	
	public CardPickerOverlay getCardPicker() {
		return cardPicker;
	}

	public void setCardPicker(CardPickerOverlay cardPicker) {
		this.cardPicker = cardPicker;
	}
	
	
    @FXML
    void onQuit(ActionEvent event) {
    	//if (!canvasState.accecptInput()) { return; } // do no accept input now!
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Spiel speichern");
    	alert.setHeaderText("Möchtest du das Spiel vor dem Beenden speichern?");
    	//alert.setContentText("Wähle eine Option aus:");

    	ButtonType buttonTypeYes = new ButtonType("Ja");
    	ButtonType buttonTypeNo = new ButtonType("Nein");
    	ButtonType buttonTypeCancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);

    	alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

    	Optional<ButtonType> result = alert.showAndWait();
    	
    	// stop game
    	if (result.get() == buttonTypeYes || result.get() == buttonTypeNo) {
    		canvasState.setGameQuitted(true);
    		onitamaController.getGameStateController().removeAUI(this);
    	}
    
    	
    	// TODO: KILL THREAD WORKERS

    	if (result.get() == buttonTypeYes){
    		releaseAUIHook();
    		loadViewController.saveGameView(NextWindow.MAIN_MENU);
    	} 
    	if (result.get() == buttonTypeNo) {
    		releaseAUIHook();
    		loadViewController.loadMainMenuView();
    	}
    }
    
    
    @FXML
    void onSaveGame(ActionEvent event){
    	if (!canvasState.accecptInput()) { return; }
    	releaseAUIHook();
    	loadViewController.saveGameView(NextWindow.GAME_CANVAS);
    }

    @FXML
    void onShowGameStateHistory(ActionEvent event) {
    	//if (!canvasState.accecptInput()) { return; }
    	if (moveHistoryController == null) {
    		moveHistoryController = loadViewController.loadMoveHistoryView(this);
    	}
    }

    @FXML
    void onShowHandbook(ActionEvent event) {
    	String pathForHandbook = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).getAbsolutePath();
    	try {
	        new ProcessBuilder("firefox", pathForHandbook + "/produktbeschreibung_onitama.pdf").start();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
    
    @FXML
    void onGetHelp(ActionEvent event) {
    	if (canvasState.accecptInput() && canvasState.getHighlightedMove() == null) {
    		if (useTipDialog("Tipp anzeigen?", "Möchtest du wirklich einen Tipp nutzen? Das Spiel wird dann nicht mehr im Highscore berücksichtigt.")) {
    			this.aiController.generateTip();
    		}
    	}
    }
    
    /**
     * asks if the user wants to set the highscore to disabled, 
     * the answers are yes and no ... if the user selects yes, the highscore gets disabled
     * @param title window title
     * @param info info string above the buttons
     * @return
     */
    private boolean useTipDialog(String title, String info) {
    	if (canvasState.getCurrentGame().isHighscoreEnabled()) {
    		Alert alert = new Alert(AlertType.CONFIRMATION);
        	alert.setTitle(title);
        	alert.setHeaderText(info);
    		
        	ButtonType buttonTypeYes = new ButtonType("Ja");
        	ButtonType buttonTypeNo = new ButtonType("Nein", ButtonData.CANCEL_CLOSE);
        	
    		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        	Optional<ButtonType> result = alert.showAndWait();
        	if (result.get() == buttonTypeYes) {
        		this.getCanvasState().getCurrentGame().setHighscoreEnabled(false);
        		return true;
        	} else {
        		return false;
        	}
    	} else {
    		return true; // highscore already disabled
    	}
    }
    
    @FXML
    void onToggleRotateGameboard(ActionEvent event) {
    	canvasState.setRotateGameBoard(toggleRotateGameBoard.isSelected());
    }
    
    @FXML
    void onToggleShowLabels(ActionEvent event) {
    	getGameBoardContainer().setGridLabelsVisible(toggleShowLabels.isSelected());
    }
    
    @FXML
    void onUndo(ActionEvent event){
    	if (!canvasState.accecptInput()) { return; } 
    	if (useTipDialog("Tipp anzeigen?", "Möchtest du wirklich einen Tipp nutzen? Das Spiel wird dann nicht mehr im Highscore berücksichtigt.")) {
    		onitamaController.getGameStateController().lastPlayerMove();
    	}
		//onitamaController.getOnitama().getCurrentGame().setHighscoreEnabled(false);
    }
    
    @FXML
    void onRedo(ActionEvent event){
    	if (!canvasState.accecptInput()) { return; } 
		onitamaController.getGameStateController().futurePlayerMove();    		
    }

	public StackPane getRotatePane() {
		return rotatePane;
	}

	public CardContainer getCardContainer() {
		return cardContainer;
	}

	public CanvasAnimationController getAnimationController() {
		return animationController;
	}

	public CanvasAIPlayerController getAIController() {
		return aiController;
	}

	public GameOverScreen getGameOverScreen() {
		return gameOverScreen;
	}

	public LoadViewController getLoadViewController() {
		return loadViewController;
	}

	public void setLoadViewController(LoadViewController loadViewController) {
		this.loadViewController = loadViewController;
	}
	
	public void releaseAUIHook() {
		onitamaController.getGameStateController().removeAUI(this);
		if (moveHistoryController != null) {
			moveHistoryController.closeWindow();
			moveHistoryController = null;
		}
	}

	public MoveHistoryController getMoveHistoryController() {
		return moveHistoryController;
	}

	public void setMoveHistoryController(MoveHistoryController moveHistoryController) {
		this.moveHistoryController = moveHistoryController;
	}
}
