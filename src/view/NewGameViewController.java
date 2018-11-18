package view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import controller.NewGameController;
import controller.OnitamaController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import model.Game;
import model.GameCard;
import model.Player;
import model.PlayerColor;
import model.PlayerType;
import view.LoadGameViewController.NextWindow;

public class NewGameViewController {
	private static final int PLAYER_SMALL_BOARDSIZE = 5;
	private static final int PLAYER_BIG_BOARDSIZE = 5;

    @FXML
    private GridPane outerPane;
    
    @FXML
    private ToggleGroup playercount;
    
    @FXML
    private RadioButton btn2Player;

    @FXML
    private RadioButton btn3Player;

    @FXML
    private RadioButton btn4Player;

    @FXML
    private TextField textMaxMoves;

    @FXML
    private Label labelGameSize;

    @FXML
    private Button btnSmaller;

    @FXML
    private Button btnBigger;

    @FXML
    private TextField textPlayer1;
    
    @FXML
    private TextField textPlayer2;

    @FXML
    private TextField textPlayer3;

    @FXML
    private TextField textPlayer4;

    @FXML
    private ComboBox<PlayerType> boxPlayer1;

    @FXML
    private ComboBox<PlayerType> boxPlayer2;

    @FXML
    private ComboBox<PlayerType> boxPlayer3;

    @FXML
    private ComboBox<PlayerType> boxPlayer4;
    
    @FXML
    private ImageView imgPlayer3;

    @FXML
    private ImageView imgPlayer4;

    @FXML
    private Button btnGameStart;
    
    @FXML
    private ImageView imgBack;
   
    @FXML
    private Button btnImport;
    
    @FXML
    private Button btnBack;
    
    @FXML
    private Label labelKarte1;

    @FXML
    private Label labelKarte2;
    
    @FXML
    private Label labelKarte3;
    
    @FXML
    private Label labelKarte4;
   
    @FXML
    private Label labelFreeCard;
    
    @FXML
    private Label labelImportCard;

    private int boardSize;
    private int maxMoves;
    private boolean legitGame;
    private OnitamaController onitamaController;
    private List<TextField> playerNames;
    private List<Label> playerCards;
    private List<ComboBox<PlayerType>> comboBoxes;
    private boolean imported;
    private boolean fail;
    LoadViewController loadViewController;    
    File importFile;
    
    public OnitamaController getOnitamaController() {
		return onitamaController;
	}

	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}

	@FXML
    void init() {
		fail = false;
		importFile = null;
		imported = false;
		loadViewController =new LoadViewController(onitamaController);
    	boardSize = 5;
    	disable3Player(true);
    	maxMoves = -1;
    	legitGame = true;
    	
    	//set up comboBoxes
    	comboBoxes = new ArrayList<ComboBox<PlayerType>>();
    	comboBoxes.add(boxPlayer1);
    	comboBoxes.add(boxPlayer2);
    	comboBoxes.add(boxPlayer3);
    	comboBoxes.add(boxPlayer4);
    	
    	for(ComboBox<PlayerType> comboBox : comboBoxes){
    		setUpComboBox(comboBox);
    	}
    	
    	//set up Player
    	playerNames = new ArrayList<TextField>();
    	
    	playerNames.add(textPlayer1);
    	playerNames.add(textPlayer2);
    	playerNames.add(textPlayer3);
    	playerNames.add(textPlayer4);
    	
    	for(TextField player : playerNames){
    		player.setText("Spieler" + (playerNames.indexOf(player)+1));
    	}
    	
    	playerCards = new ArrayList<Label>();
    	
    	playerCards.add(labelKarte1);
    	playerCards.add(labelKarte2);
    	playerCards.add(labelKarte3);
    	playerCards.add(labelKarte4);
    	
    	
    	refresh();

    	
    	
    	
    }
	
	public void setUpComboBox(ComboBox<PlayerType> box){
	box.setCellFactory( players ->
	new ListCell<PlayerType>(){
		@Override protected void updateItem(PlayerType player, boolean empty){
				super.updateItem(player, empty);
				if(player == null){
					setText("");
				} else{
					this.setText(player.toString());
				}
			}
		});
	PlayerType [] playerTypes = PlayerType.values();
	for(PlayerType type :playerTypes){
		box.getItems().add(type);
		//System.out.println(type.toString());
		}
	box.getSelectionModel().select(0);
	}
    
    /** changes to the gameboard with an new default game, which gets boardSize, Moves and Player from GUI
     * 
     * @param event
     */
	
	public void createGame(){
		fail = false;
		NewGameController gameController = onitamaController.getNewGameController();
    	gameController.createDefaultGame();
		
    	gameController.setBoardSize(boardSize);
    	if(maxMoves > -1){
    		maxMoves++;
    	}
    	if(boardSize >= 69)
    	{
    		gameController.setBoardSize(69);
    	}
    	
    	gameController.setMaxMoves(maxMoves);
    	
    	gameController.addPlayer(textPlayer1.getText(), boxPlayer1.getSelectionModel().getSelectedItem(),PlayerColor.EARTH);	
    	gameController.addPlayer(textPlayer2.getText(), boxPlayer2.getSelectionModel().getSelectedItem(),PlayerColor.FIRE);	
    	
    	if(btn3Player.isSelected())
    		gameController.addPlayer(textPlayer3.getText(), boxPlayer3.getSelectionModel().getSelectedItem(),PlayerColor.WATER);
    	
    	if(btn4Player.isSelected()){
    		gameController.addPlayer(textPlayer3.getText(), boxPlayer3.getSelectionModel().getSelectedItem(),PlayerColor.WATER);
    		gameController.addPlayer(textPlayer4.getText(), boxPlayer4.getSelectionModel().getSelectedItem(),PlayerColor.AIR);
    	}
    	

    	if(importFile != null){
    		try {
				gameController.loadConfiguration(importFile, gameController.getOnitamaController().getOnitama().getCurrentGame().getPlayers().get(0),gameController.getOnitamaController().getOnitama().getCurrentGame().getPlayers().get(1));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				fail =true;
				//e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				fail =true;
				//e.printStackTrace();
			}
    	}
    	
		
	}
	
    @FXML
    void btnGameStart(ActionEvent event){
    	createGame();
		loadViewController.loadGameCanvasView();
    }
    
    
    
    @FXML
    void onBack(MouseEvent event) {
    }
    
    @FXML
    void onBtnBack(ActionEvent event) {
 	   loadViewController.loadMainMenuView();
    }

    @FXML
    void onBigger(ActionEvent event) {
    	boardSize += 2;
    	changeTextMaxMoves(boardSize);
    	btn3Player.setDisable(false);
    	btn4Player.setDisable(false);
    	refresh();
    	

    }
    
    @FXML
    void onSmaller(ActionEvent event) {
       	if(boardSize > PLAYER_SMALL_BOARDSIZE){
       		boardSize -= 2;
        	changeTextMaxMoves(boardSize);
        	
        	if(boardSize == PLAYER_SMALL_BOARDSIZE){
        		on2PlayerSelected(event);
        	}
       	}
       	refresh();
    }
    
    public void changeTextMaxMoves(int moves){
    	labelGameSize.setText(moves + " x " + moves);
    }
    
    @FXML
    void on2PlayerSelected(ActionEvent event) {
    	
    	for(ComboBox<PlayerType> comboBox: comboBoxes){
    		if(!comboBox.getItems().contains(PlayerType.BRAIN))
    			comboBox.getItems().add(PlayerType.BRAIN);
        	
    	}
    		
		
    	disable3Player(true);
    	refresh();
    }

    @FXML
    void on3PlayerSelected(ActionEvent event) {   		
    	
   	
    	for(ComboBox<PlayerType> comboBox: comboBoxes)
    			comboBox.getItems().remove(PlayerType.BRAIN);
    	
    	disable3Player(false);
    	disable4Player(true);
    	if(boardSize <= PLAYER_SMALL_BOARDSIZE){
    		boardSize = 7;
    		changeTextMaxMoves(7);
    	}
    	refresh();
    }

    @FXML
    void on4PlayerSelected(ActionEvent event) {	
    	for(ComboBox<PlayerType> comboBox: comboBoxes)
    			comboBox.getItems().remove(PlayerType.BRAIN);
    	
    	disable4Player(false);
    	if(boardSize <= 5){
    		boardSize = 7;
    		changeTextMaxMoves(7);
    	}
    	refresh();
    }

    @FXML
    void onNamePlayer1(KeyEvent event) {
    	refresh();
    }

    @FXML
    void onNamePlayer2(KeyEvent event) {
    	refresh();
    }

    @FXML
    void onNamePlayer3(KeyEvent event) {
    	refresh();
    }

    @FXML
    void onNamePlayer4(KeyEvent event) {
    	refresh();
    }

    @FXML
    void onNewText(KeyEvent event) {	
    	refresh();
    }
    
    /**
     * check if entered number in textMaxMoves is a number and >0. If not hightlight the text field
     */
    void checkNumber(){
    	try{
    		textMaxMoves.setStyle("-fx-border-color: white;");
    		if(!textMaxMoves.getText().equals("") ){
    			if(Integer.parseInt(textMaxMoves.getText()) <= 0){
    				throw new IllegalArgumentException();
    			}
    			maxMoves = Integer.parseInt(textMaxMoves.getText());
    		}
    			
    		else
    			maxMoves = -1;
    		
    	}
    	catch(Exception e){
    		textMaxMoves.setStyle("-fx-border-color: orange;");
    		legitGame = false;
    	}
    	//System.out.println(maxMoves);
    }

   
    
    /**
     * refresh the game and checks if this game is  valid
     */
    private void refresh() {
    	
    	legitGame = true;
    	
    	//checkNameFields
    	for(TextField field : playerNames){
    		if(!field.isDisable()){
    			checkName(field);
    		}
    		else{
    			//field.setText("");
    			field.setStyle("-fx-border-color: white;");
    		}
    	}
    	//check if valid number of moves
    	checkNumber();
    	
    	btnSmaller.setDisable(false);
    	if(legitGame){
    		btnGameStart.setDisable(false);
    	}
    	else
    	{    	
    		btnGameStart.setDisable(true);
    		btnGameStart.setOpacity(0.5);
    	}

    	if(btn2Player.isSelected()){
    		if(boardSize <= PLAYER_SMALL_BOARDSIZE){
    			btnSmaller.setDisable(true);
    		}
    	}
    	else if(boardSize <= PLAYER_BIG_BOARDSIZE){
    		btnSmaller.setDisable(true);
    	}
    		
		
	}

	void disable4Player(boolean disable){
    	//zwei Spieler anwählen(einzige Option)
    	textPlayer4.setDisable(disable);
    	boxPlayer4.setDisable(disable);
    	
    	if(disable)
    		imgPlayer4.setOpacity(0.3);
    	else
    		imgPlayer4.setOpacity(1);
    	
    	if(!disable){
    		disable3Player(disable);
    	}
    	
    }
    
    void disable3Player(boolean disable){
    	if(disable){
    		btn2Player.setSelected(disable);
    	}
    	
    	
    	
    	//btn3Player.setDisable(disable);
    	textPlayer3.setDisable(disable);
    	boxPlayer3.setDisable(disable);
    	if(disable)
    		imgPlayer3.setOpacity(0.5);
    	else
    		imgPlayer3.setOpacity(1);
    	if(disable)
    		disable4Player(disable);
    }
    
    public void checkName(TextField textField){
    	String name = deleteSpace(textField.getText());
    	if(! name.equals(textField.getText()))
    	{
    		textField.setStyle("-fx-border-color: orange;");
    		legitGame = false;
    	}
    	else{
    		textField.setStyle("-fx-border-color: white;");
    	}
    		

    }
    
    /** import of a txt from cards. If thats possible private value file is set.
     * @param event
     */
    @FXML
    void onImport(ActionEvent event) {
    	
    	
    	FileChooser fileChooser = new FileChooser();
    	
    	fileChooser.getExtensionFilters().addAll(
    			new FileChooser.ExtensionFilter("TXT", "*.txt"));
    	fileChooser.setTitle("Spielkarten laden");
    	File file = fileChooser.showOpenDialog(null);
    	System.out.println(file);
    	importFile = file;
    	
    	try{
    		createGame();
    		if(importFile != null && ! fail)
    		{
    			
    			
	    		Game game =onitamaController.getOnitama().getCurrentGame();
		    	labelImportCard.setText("Freie Karte: " + GameCard.TO_STRING.get(game.getInitialFreeCard()));
		    	//labelKarte1.setText(value);
		    	
		    	//Map<> map =game.getInitialCardDistribution();
		    	
		    	List<Player> playerList = game.getPlayers();
		    	
		    	for(int i=0; i < playerList.size(); i++){
		    		List<GameCard> currentCards = game.getInitialCardDistribution().get(playerList.get(i));
		    		
		    		playerCards.get(i).setText("");
		    		for(GameCard card : currentCards){
		    			playerCards.get(i).setText( playerCards.get(i).getText() + " " + GameCard.TO_STRING.get(card));//card.toString());
		    		}
		    		
		    	}
		    	imported = true;
    		}
    		else{
    			
    			Alert alert = new Alert(AlertType.INFORMATION);
    	    	alert.setTitle("Import fehlgeschlagen");
    	    	alert.setHeaderText("Datei konnte nicht geladen werden.");
    	    	//alert.setContentText("Wähle eine Option aus:");

    	    	ButtonType buttonTypeCancel = new ButtonType("OK", ButtonData.CANCEL_CLOSE);

    	    	alert.getButtonTypes().setAll(buttonTypeCancel);

    	    	Optional<ButtonType> result = alert.showAndWait();
    	    	
 
    		}
    	}
    	catch(Exception e){
    		System.err.println("laden fehlgeschlagen");
    	}
    	
    
    }

    /**
     * @param string
     * @return string without spaces in the beginning
     */
    public String deleteSpace(String string){
		if(string.length() > 0 && string.charAt(0)==" ".charAt(0)){
			return deleteSpace(string.substring(1));
		}
		
		return string;
	}
}
