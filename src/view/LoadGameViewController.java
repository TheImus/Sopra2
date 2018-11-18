package view;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;



import controller.OnitamaController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import model.Game;



/**
 * @author thilo
 *
 */
public class LoadGameViewController {

	
	OnitamaController onitamaController;

	@FXML
    private BorderPane outerPane;
	
    @FXML
    private Button btnGame1;

    @FXML
    private Button btnGame2;

    @FXML
    private Button btnGame3;

    @FXML
    private Button btnGame4;

    @FXML
    private Button btnGame5;

    @FXML
    private Button btnGame6;
    
    List<Button> saveGames;
    
    @FXML
    private Button btnBack;
    private LoadViewController loadViewController;
    

    @FXML
    void onBack(ActionEvent event) {

    	loadViewController.loadMainMenuView();
    }
    
	
	public OnitamaController getOnitamaController() {
		return onitamaController;
	}

	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}
    
    public void refresh(){

    }
    
    /**
     * Game buttons are set to safeButtons(safe slots)
     * @param nextWindow which will be entered after clicking on zurück
     */
    public void initSave(NextWindow nextWindow){
    	loadViewController=new LoadViewController(onitamaController);
    	btnBack.setOnAction(event -> { loadViewController.loadGameCanvasView();});
    	saveGames = Arrays.asList(btnGame1,btnGame2,btnGame3,btnGame4,btnGame5,btnGame6);
    	Game [] games = onitamaController.getSavedGames();
    	
    	//button set up 
    	for(int i =0; i <saveGames.size(); i++){
    		Button currentBtn = saveGames.get(i);
    		Game currentGame = games[i];
    		if(currentGame == null){
    			currentBtn.setText("freier Slot");
    		}
    		else{
    			currentBtn.setText(currentGame.getName());
    		}
    		final int currentSlot = i;
			currentBtn.setOnAction(event -> {
				boolean correct = true;
				TextInputDialog dialog = new TextInputDialog("");
				dialog.setTitle("Spiel speichern");
				dialog.setContentText("Bitte Spielnamen eingeben:");

				// Traditional way to get the response value.
				Optional<String> result = dialog.showAndWait();
				String name = deleteSpace(result.get());
				System.out.println("+" + name+"+");
				if (result.isPresent()) {
					if (name.equals("")) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error");
						alert.setHeaderText("This name is empty!");
						correct = false;
						alert.showAndWait();

					}
					
					
					for (int j = 0; j < saveGames.size(); j++) {
						if (result.get().equals(saveGames.get(j).getText())  && currentSlot != j) {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error");
							alert.setHeaderText("This name already exists or is empty!");
							correct = false;
							alert.showAndWait();

						}
					}
					
					if (correct) {
						onitamaController.getOnitama().getCurrentGame().setName(result.get());
						onitamaController.saveGame(onitamaController.getOnitama().getCurrentGame(), currentSlot);
						if (nextWindow.equals(NextWindow.MAIN_MENU)) {
							if (correct) {
								loadViewController.loadMainMenuView();
							}
						} else {
							loadViewController.loadGameCanvasView();
						}
					}
				}
			});
    	}
    }
    
	/**
	 * init for loading games
	 */
	public void init() {
		loadViewController=new LoadViewController(onitamaController);
		saveGames = Arrays.asList(btnGame1,btnGame2,btnGame3,btnGame4,btnGame5,btnGame6);
		
		
		Game [] games = onitamaController.getSavedGames();
	
		for(int i =0; i< saveGames.size(); i++){
			Button currentBtn = saveGames.get(i);
			Game currentGame = games[i];
			if(currentGame == null){
				currentBtn.setDisable(true);
				currentBtn.setText("kein Spiel");
				currentBtn.setOpacity(0.5);
				continue;
			}
			final int currentSlot = i;
			currentBtn.setOnAction(
					event -> {
						onitamaController.loadGame(currentSlot);
						/**
						 * TODO wechsel zumGameboard
						 */
						loadViewController.loadGameCanvasView();
			});
			currentBtn.setText(currentGame.getName());
			currentBtn.setDisable(false);
			//currentBtn.setOpacity(1);			
		}
		
	}
	
	public String deleteSpace(String string){
		if(string.length() > 0 && string.charAt(0)==" ".charAt(0)){
			return deleteSpace(string.substring(1));
		}
		
		return string;
	}
	
	//die sind nur da weil sie da sein müssen
    @FXML
    void onGame1(ActionEvent event) {
    }

    @FXML
    void onGame2(ActionEvent event) {  	
    }

    @FXML
    void onGame3(ActionEvent event) {
    }

    @FXML
    void onGame4(ActionEvent event) {
    }

    @FXML
    void onGame5(ActionEvent event) {
    }

    @FXML
    void onGame6(ActionEvent event) {
    }
    
    public enum NextWindow {
    	MAIN_MENU,
    	GAME_CANVAS
    }
}

