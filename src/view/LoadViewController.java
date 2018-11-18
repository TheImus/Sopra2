package view;

import application.Main;
import controller.OnitamaController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import view.LoadGameViewController.NextWindow;

public class LoadViewController {
	private OnitamaController onitamaController;
	private static Stage primaryStage;
	
	private Stage gameMoveHistoryStage;
	
	public LoadViewController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}
	
	
	/**
	 * @param parent 
	 * @return null if loadMoveHistory crashes else return MoveHistory Controller
	 */
	public MoveHistoryController loadMoveHistoryView(GameCanvasViewController parent){
		try {

			GridPane root = new GridPane();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MoveHistory.fxml"));
			root = loader.load();
			MoveHistoryController viewController = (MoveHistoryController) loader.getController();
			viewController.setOnitamaController(onitamaController);
			viewController.setGameController(parent);
			viewController.init();
			
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setTitle("Spielverlauf");
			stage.getIcons().add(new Image(Main.class.getResourceAsStream("OnitamaIcon.jpg")));
			stage.setScene(scene);
			viewController.initAUI();
			stage.show();
			//((Stage) outerPane.getScene().getWindow()).setScene(scene);
			return viewController;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
		
	}
	
	/**
	 * main windows changes to Highscore table 
	 */
	public void loadHighscoreView() {
		try {

			BorderPane root = new BorderPane();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Highscore.fxml"));
			root = loader.load();
			HighscoreViewController highscoreController = (HighscoreViewController) loader.getController();
			highscoreController.setOnitamaController(onitamaController);
			highscoreController.setStage(primaryStage);
			highscoreController.init();
			
			Scene scene = new Scene(root);
			//((Stage) outerPane.getScene().getWindow()).setScene(scene);
			//primaryStage.setScene(scene);
			Platform.runLater(() -> { LoadViewController.primaryStage.setScene(scene); });

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * main view changes to load menu with the possibility to load Games
	 */
	public void loadGameView() {
		try {
			primaryStage.setTitle("Spiel Laden");
			BorderPane root = new BorderPane();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoadGame.fxml"));
			root = loader.load();
			LoadGameViewController loadGameViewController = (LoadGameViewController) loader.getController();
			loadGameViewController.setOnitamaController(onitamaController);
			loadGameViewController.init();
			Scene scene = new Scene(root);
			//((Stage) outerPane.getScene().getWindow()).setScene(scene);
			//LoadViewController.primaryStage.setScene(scene);
			Platform.runLater(() -> { LoadViewController.primaryStage.setScene(scene); });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * main view changes to load menu with the possibility to save games
	 * @param nextWindow the window which should appear after closing the window
	 */
	public void saveGameView(NextWindow nextWindow) {
		try {
			primaryStage.setTitle("Spiel Speichern");
			BorderPane root = new BorderPane();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoadGame.fxml"));
			root = loader.load();
			LoadGameViewController loadGameViewController = (LoadGameViewController) loader.getController();
			loadGameViewController.setOnitamaController(onitamaController);
			loadGameViewController.initSave(nextWindow);
			Scene scene = new Scene(root);
			//((Stage) outerPane.getScene().getWindow()).setScene(scene);
			Platform.runLater(() -> { LoadViewController.primaryStage.setScene(scene); });
			//LoadViewController.primaryStage.setScene(scene);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * changes main view to a new Game view to create a new game
	 */
	public void loadNewGameView()
	{
		try {
			primaryStage.setTitle("Neues Spiel");
			GridPane root = new GridPane();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/NewGame.fxml"));
			root = loader.load();
			NewGameViewController viewController = (NewGameViewController) loader.getController();
			viewController.setOnitamaController(onitamaController);
			viewController.init();

			Scene scene = new Scene(root);
			//((Stage) outerPane.getScene().getWindow()).setScene(scene);
			Platform.runLater(() -> { LoadViewController.primaryStage.setScene(scene); });
			//LoadViewController.primaryStage.setScene(scene);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  changes main view to game board
	 */
	public void loadGameCanvasView()
	{
		try {
			primaryStage.setTitle("Onitama");
			GridPane root = new GridPane();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GameCanvas.fxml"));
			root = loader.load();
			GameCanvasViewController viewController = (GameCanvasViewController) loader.getController();
			viewController.setOnitamaController(onitamaController);
			viewController.init();

			Scene scene = new Scene(root);
	    	// this fixes java bug when change scnene after dialog
			// javafx bug https://bugs.openjdk.java.net/browse/JDK-8140491
			Platform.runLater(() -> { 
				LoadViewController.primaryStage.setScene(scene);
			});
			//LoadViewController.primaryStage.setScene(scene);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * changes main view to main menu
	 */
	public void loadMainMenuView()
	{

		try {
			primaryStage.setTitle("Onitama");
			GridPane root = new GridPane();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
			root = loader.load();
			MainMenuViewController viewController = (MainMenuViewController) loader.getController();
			viewController.setOnitamaController(onitamaController);
			viewController.init();
			

			Scene scene = new Scene(root);
			//((Stage) outerPane.getScene().getWindow()).setScene(scene);
			//LoadViewController.primaryStage.setScene(scene);
			Platform.runLater(() -> { LoadViewController.primaryStage.setScene(scene); });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new stage for the game history ... this needs a second window 
	 */
	public void showMoveHistory() {
		try {
			if (gameMoveHistoryStage == null) {
				gameMoveHistoryStage = new Stage();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static Stage getPrimaryStage() {
		return LoadViewController.primaryStage;
	}


	public static void setPrimaryStage(Stage primaryStage) {
		LoadViewController.primaryStage = primaryStage;
	}

}
