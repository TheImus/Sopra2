package view;

import java.net.URL;
import java.util.ResourceBundle;

import controller.OnitamaController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.HighScoreEntry;


public class HighscoreViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<HighScoreEntry> highscore;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnChangeHighscore;

    @FXML
    private TableColumn<HighScoreEntry, String> columnMaxMoves;

    @FXML
    private TableColumn<HighScoreEntry, String> columnPlayer;

    @FXML
    private TableColumn<HighScoreEntry, String> columnBeatenMaster;

    @FXML
    private TableColumn<HighScoreEntry, String> columnBeatenToken;
    
    @FXML
    private TableColumn<HighScoreEntry, String> columnGameMoves;
    
    @FXML
    private BorderPane outerPane;
    
    private boolean twoPlayerHighscore;
    private boolean threePlayerHighscore;
    private int nextList;
    
    private OnitamaController onitamaController;
    
    ObservableList<HighScoreEntry> currentList;
    
    LoadViewController loadViewController;
    
    Stage stage;
    
    public OnitamaController getOnitamaController() {
		return onitamaController;
		
	}

	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}
	
	public void setStage(Stage stage){
		this.stage = stage;
	}
	
	public Stage getStage(){
		return stage;
	}
	
	

	
	@FXML
    void onBack(ActionEvent event) {
		stage.setTitle("Onitama");
		loadViewController.loadMainMenuView();
    }

	
    @FXML
    void onChangeHighscore(ActionEvent event) {
    	changeList(); 
    		
    }  
    
    public void init() {
    	loadViewController=new LoadViewController(onitamaController);
    	
    	//Highscore list from current game is shown. If there is no current game 2 Player highscore is selected.
        nextList = 2;
        if(onitamaController.getOnitama().getCurrentGame() != null)
        	nextList = onitamaController.getOnitama().getCurrentGame().getPlayers().size();
        //twoPlayerHighscore = true; 
        //threePlayerHighscore = true;
        highscore.getItems().setAll(( FXCollections.observableArrayList(onitamaController.getHighscoreController().getTwoPlayerHighScoreList())));
        changeList();
    }   
    
    /**
     * changes the shown list
     */
    public void changeList(){
    	ObservableList<HighScoreEntry> testList = highscore.getItems();
    	
    	
    	switch(nextList){
        case 2: //btnChangeHighscore.setText("3 Spieler"); 
        		testList.setAll(( FXCollections.observableArrayList(onitamaController.getHighscoreController().getTwoPlayerHighScoreList())));
        		stage.setTitle("2 Spieler Highscore");
        		break;
        case 3: //btnChangeHighscore.setText("4 Spieler"); 
        		testList.setAll(( FXCollections.observableArrayList(onitamaController.getHighscoreController().getThreePlayerHighScoreList())));
        		stage.setTitle("3 Spieler Highscore");
        		break;
        case 4: //btnChangeHighscore.setText("2 Spieler"); 
        		testList.setAll(FXCollections.observableArrayList(onitamaController.getHighscoreController().getFourPlayerHighScoreList())); 
        		stage.setTitle("4 Spieler Highscore");
        		nextList = 1;
        		break;
    	}
    	nextList++;
    }

}
