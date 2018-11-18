package view;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import controller.NewGameController;
import controller.OnitamaController;
import controller.TestDataFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HistoryViewControllerTest extends Application{

	private OnitamaController onitamaController;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRefresh() {
		onitamaController.getNewGameController().createDefaultGame();
		onitamaController.getOnitama().setCurrentGame(TestDataFactory.createSampleTwoPlayerGameWinableInNextMove());
		
		
		
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		onitamaController = new OnitamaController();
		
		try {

			GridPane root = new GridPane();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MoveHistory.fxml"));
			root = loader.load();
			NewGameViewController viewController = (NewGameViewController) loader.getController();
			viewController.setOnitamaController(onitamaController);
			viewController.init();

			Scene scene = new Scene(root);
			((Stage) primaryStage.getScene().getWindow()).setScene(scene);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
