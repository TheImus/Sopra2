package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import model.Onitama;
import controller.OnitamaController;
import view.MainMenuViewController;

public class MainMenuViewControllerTest extends Application{

	private OnitamaController onitamaController;
	
	@Override
	public void init() throws Exception {
		onitamaController = new OnitamaController();
		Onitama onitama = new Onitama();
		onitamaController.setOnitama(onitama);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			
			GridPane root = new GridPane();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
			try {
			    root = fxmlLoader.load();
			} catch (Exception e) {
			    throw new RuntimeException(e);
			}
			//GameCanvasViewController gameCanvas = (GameCanvasViewController) fxmlLoader.getController();
			//gameCanvas.setOnitamaController(onitamaController);
			
			MainMenuViewController mainController = (MainMenuViewController) fxmlLoader.getController();
			mainController.setOnitamaController(onitamaController);
			
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			mainController.init();
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
