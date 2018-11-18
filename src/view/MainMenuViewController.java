package view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import controller.OnitamaController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;

public class MainMenuViewController {
	
    @FXML
    private GridPane outerGridPane;

    @FXML
    private GridPane innerGridPane;

	@FXML
    private Button btnNewGame;

    @FXML
    private Button btnLoadGame;

    @FXML
    private Button btnHighscore;

    @FXML
    private Button btnQuit;

    OnitamaController onitamaController;
    
    LoadViewController loadViewController;
    
    
    
   
    
    public OnitamaController getOnitamaController() {
		return onitamaController;
	}

	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}

	@FXML
	public void init(){
		
		loadViewController=new LoadViewController(onitamaController);
    	//outerGridPane.getChildren().add(new ImageView("res/assets/Download.jpg"));
    }
    
    @FXML
    void onHighscore(ActionEvent event) {
    	// FIX java bug
    	//outerGridPane.setTop(null);
    	loadViewController.loadHighscoreView();
    }

    @FXML
    void onLoadGame(ActionEvent event) {
    	// FIX java bug
    	//outerGridPane.setTop(null);
    	
    	loadViewController.loadGameView();
    	

    }

    @FXML
    void onNewGame(ActionEvent event) {
    	// FIX java bug
    	//outerGridPane.setTop(null);
    	
    	loadViewController.loadNewGameView();
    }

    @FXML
    void onQuit(ActionEvent event) {
    	
    		System.exit(0);
    	

    }


}

