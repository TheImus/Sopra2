package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import controller.NewGameStateAUI;
import controller.OnitamaController;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import model.GameCard;
import model.GameState;
import model.GameToken;
import model.Player;
import model.Vector;

public class MoveHistoryController implements NewGameStateAUI {
	
	  @FXML
	    private TableView<MoveHistoryObject> tableGameMoves;

	    @FXML
	    private TableColumn<MoveHistoryObject, String> columnPlayer;

	    @FXML
	    private TableColumn<MoveHistoryObject, String> columnCard;

	    @FXML
	    private TableColumn<MoveHistoryObject, String> columnTokenStart;

	    @FXML
	    private TableColumn<MoveHistoryObject, String> columnTokenEnd;
	    
	    @FXML
	    private TableColumn<MoveHistoryObject, String> columnTurn;
	    
	    @FXML
	    private GridPane basePane;
	    

	    private OnitamaController onitamaController;
	    private ObservableList<MoveHistoryObject> history;
	    private ChangeListener <MoveHistoryObject> listener;
	    private GameCanvasViewController parent;

	
	
	public class MoveHistoryObject{
		
		private String card;
		private String player;
		private String startPosition;
		private String endPosition;
		private GameState gameState;
		private String turn;
		
		
		/** a possible tableEntry for a Movement from a player
		 * @param before gameState where you come from 
		 * @param gameState gameState where you are now
		 */
		MoveHistoryObject(GameState before ,GameState gameState){
			this.gameState = before;
			GameToken movedToken = gameState.getLastMove().getMovedToken();
			//card = gameState.getLastMove().getSelectedCard().getCardName();
			card = GameCard.TO_STRING.get(gameState.getLastMove().getSelectedCard());
			player = gameState.getLastMove().getMovedToken().getPlayer().getName();
			Vector oldVector = before.getGameBoard().getPositionOf(movedToken);
			Vector startVector = new Vector(oldVector.getX(),before.getGameBoard().getSize() -(oldVector.getY()+1));
			startPosition = startVector.getPrintableString();
			oldVector = gameState.getGameBoard().getPositionOf(movedToken);
			Vector endVector = new Vector(oldVector.getX(),before.getGameBoard().getSize() -(oldVector.getY()+1));
			endPosition = endVector.getPrintableString();
			turn = Integer.toString(gameState.getRoundNumber());
		}

		public boolean equals(MoveHistoryObject other){
		
			return this.gameState.equals(other.getGameState());
		}
		
		/**
		 * creates a state with filler entry but a gameState whereyou can jump to
		 * 
		 * @param gameState game state where you can go to by clicking on the enrty
		 */
		MoveHistoryObject(GameState gameState){
			player = "---";
			card = "---";
			startPosition= "---";
			endPosition = "---";
			this.gameState = gameState;
			turn = "---";
			
		}
		
		public void setTurn(String turn){
			this.turn = turn;
		}
		
		public String getTurn(){
			return turn;
		}
		
		public GameState getGameState(){
			return gameState;
		}

		public String getCard() {
			return card;
		}

		public void setCard(GameCard card) {
			this.card = card.toString();
		}

		public String getPlayer() {
			return player;
		}

		public void setPlayer(Player player) {
			this.player = player.getName();
		}

		public String getStartPosition() {
			return startPosition;
		}

		public void setStartPosition(Vector startPosition) {
			this.startPosition = startPosition.getPrintableString();
		}

		public String getEndPosition() {
			return endPosition;
		}

		public void setEndPosition(Vector endPosition) {
			this.endPosition = endPosition.getPrintableString();
		}	
		
	}
 
    public OnitamaController getOnitamaController() {
		return onitamaController;
	}

	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}
	public void setGameController (GameCanvasViewController controller){
		parent = controller;
	}
	
	public GameCanvasViewController getGameController(){
		return parent;
	}
	
	@FXML
    void init() {
		history = FXCollections.observableArrayList(new ArrayList<MoveHistoryObject>());		
		tableGameMoves.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		//handles a click in the table to change the shown gameState on the gameBoard
		tableGameMoves.setOnMousePressed(event ->{
			//checking if a highscoreentry is possible and warns the player about losing the option if changing the state
			if(onitamaController.getOnitama().getCurrentGame().isHighscoreEnabled()){
				Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Zug zurück?");
            	alert.setHeaderText("Möchtest du wirklich Zug zurück nutzen? Das Spiel wird dann nicht mehr im Highscore berücksichtigt.");
        		
            	ButtonType buttonTypeYes = new ButtonType("Ja");
            	ButtonType buttonTypeNo = new ButtonType("Nein", ButtonData.CANCEL_CLOSE);
            	
        		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == buttonTypeYes) {
            		parent.getCanvasState().getCurrentGame().setHighscoreEnabled(false);
            		System.out.println("ListenerCalled");
        			tableGameMoves.getSelectionModel().selectedItemProperty().removeListener(listener);
        			
        			//change to the selected gameState in the gameCanvas
        			System.out.println("ungeleiche Values");
        			if(parent.getCanvasState().accecptInput()){
        				MoveHistoryObject hMove =tableGameMoves.getSelectionModel().getSelectedItem();	
        				System.out.println(hMove);
        				onitamaController.getGameStateController().resetToGameState(hMove.getGameState());
        				onitamaController.getOnitama().getCurrentGame().setHighscoreEnabled(false);
        				tableGameMoves.setDisable(false);
        			}
            	}
			}
			
			else{
				System.out.println("ListenerCalled");
			tableGameMoves.getSelectionModel().selectedItemProperty().removeListener(listener);
			
			System.out.println("ungeleiche Values");
			if(parent.getCanvasState().accecptInput()){
				MoveHistoryObject hMove =tableGameMoves.getSelectionModel().getSelectedItem();	
				System.out.println(hMove);
				onitamaController.getGameStateController().resetToGameState(hMove.getGameState());
				onitamaController.getOnitama().getCurrentGame().setHighscoreEnabled(false);
				tableGameMoves.setDisable(false);
			}
			}
			
			//tableGameMoves.getSelectionModel().clearSelection();
		
			
		});
		
		refresh();
		
		
		
    }
	
	/**
	 * updates tableview ,but creates new elements for every gameState. 
	 */
	public void refresh(){
		tableGameMoves.setDisable(true);
		tableGameMoves.getSelectionModel().selectedItemProperty().removeListener(listener);
		List<GameState> gameStates = onitamaController.getOnitama().getCurrentGame().getGameStates();
		history = FXCollections.observableArrayList(new ArrayList<MoveHistoryObject>());
		for(int i = 1; i<gameStates.size();i++){
			history.add(new MoveHistoryObject(gameStates.get(i-1), gameStates.get(i)));
		}
		tableGameMoves.setItems(history);
		tableGameMoves.getItems().add(new MoveHistoryObject(gameStates.get(gameStates.size()-1)));
		tableGameMoves.getSelectionModel().selectLast();
		tableGameMoves.getSelectionModel().selectedItemProperty().addListener(listener);
		tableGameMoves.setDisable(false);
	
	}
	
		
	
	@Override
	public void refreshGameState(){
		refresh();
	}

	@Override
	public void animateNewGameState(GameState oldGameState, GameState newGameState) {
		// TODO Auto-generated method stub
		refresh();
		
	}

	@Override
	public void playerLost(Player player) {
		// TODO Auto-generated method stub
		refresh();
		
	}
	
	/** 
	 * Call this every time when you close this window
	 */
	public void closeWindow() {
		parent.setMoveHistoryController(null);
		onitamaController.getGameStateController().removeAUI(this);
		Stage stage = (Stage) basePane.getScene().getWindow();
		stage.close();
	}

	/** 
	 * this gets called after the scene is set
	 */
	public void initAUI() {
		onitamaController.getGameStateController().addAUI(this);
		// remove AUI if this window gets closed
		basePane.getScene().getWindow().setOnCloseRequest(event -> this.closeWindow());
	}

}
