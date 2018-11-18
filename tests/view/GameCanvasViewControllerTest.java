package view;

import controller.OnitamaController;
import controller.TestDataFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Onitama;

public class GameCanvasViewControllerTest extends Application {
	
	private OnitamaController onitamaController;

	@Override
	public void init() throws Exception {
		onitamaController = new OnitamaController();
		Onitama onitama = new Onitama();
		//onitama.setCurrentGame(new Game());
		//onitama.setCurrentGame(TestDataFactory.createSampleTwoPlayerGameWinableInNextMove());
		onitama.setCurrentGame(TestDataFactory.createInconsistentFourPlayerWinnableGame());
		
		onitamaController.setOnitama(onitama);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			
			GridPane root = new GridPane();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/GameCanvas.fxml"));
			try {
			    root = fxmlLoader.load();
			} catch (Exception e) {
			    throw new RuntimeException(e);
			}
			GameCanvasViewController gameCanvas = (GameCanvasViewController) fxmlLoader.getController();
			gameCanvas.setOnitamaController(onitamaController);
			
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			gameCanvas.init();
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
