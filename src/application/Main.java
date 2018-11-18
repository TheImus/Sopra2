package application;
	
import controller.OnitamaController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Onitama;
import view.LoadViewController;
import view.MainMenuViewController;

/**
 * 
 * @author Fabian Kemper
 *
 *
 * INSTRUCTIONS TO EXPORT A RUNNABLE JAR FILE:
 * Eclipse -> File -> Export -> Runnable Jar file
 * Select Extract required libraries into runnable jar
 */
public class Main extends Application {

	private OnitamaController onitamaController;

	
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
			LoadViewController.setPrimaryStage(primaryStage);
			
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			
			mainController.init();
			primaryStage.setTitle("Onitama");
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("OnitamaIcon.jpg")));
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
	}
	
	
	@Override
	public void init() throws Exception {
		onitamaController = new OnitamaController();
		onitamaController.setOnitama(new Onitama());


	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
