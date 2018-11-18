package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.Game;
import model.Onitama;

public class OnitamaController {

	private GameboardController gameboardController;

	private GameStateController gameStateController;

	private PlayerController playerController;

	private HighscoreController highscoreController;

	private MovementController movementController;

	private NewGameController newGameController;
	
	private NetworkTry2 networkController;
	
	private BetterNetwork betterNetworkController;	

	private NoviceAIController noviceAIController;
	
	private SenseiAIController senseiAIController;

	private ZenMasterAIController zenMasterAIController;

	private BrainAIController brainAIController;
	
	
	
	private Onitama onitama;
	
	private static final String FILEPATH = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).getAbsolutePath() + "/save.oni";
	
	
	
	public OnitamaController(){
		this.gameboardController = new GameboardController(this);  
		this.gameStateController = new GameStateController(this);
		this.playerController = new PlayerController(this);
		this.highscoreController = new HighscoreController(this);
		//this.movementController = new MovementController();
		this.newGameController = new NewGameController(this);
		this.noviceAIController = new NoviceAIController(this);
		this.senseiAIController = new SenseiAIController(this);
		this.networkController = new NetworkTry2(this); // put before brain master (he needs this!)
		this.setBetterNetworkController(new BetterNetwork(this));
		this.zenMasterAIController = new ZenMasterAIController(this);
		this.brainAIController = new BrainAIController(this);
		this.movementController = new MovementController(this);
		
	}

	public void saveGame(Game game, int slot) {
        try {
        	//Game game = getOnitama().getCurrentGame();
        	ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(FILEPATH + slot));
            stream.writeObject(game);
            stream.close();
        } catch (IOException ioex) {
            System.err.println("Fehler beim Schreiben des Objekts aufgetreten.");
            ioex.printStackTrace();
        }
        
	}

	public void loadGame(int slot) {
		try {
	        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(FILEPATH + slot));
	        Game game = (Game) stream.readObject();
	        stream.close();
	        getOnitama().setCurrentGame(game);
	    } catch (ClassNotFoundException cnfex) {
	        System.err.println("Die Klasse des geladenen Objekts konnte nicht gefunden werden.");
	    } catch (IOException ioex) {
	        System.err.println("Das Objekt konnte nicht geladen werden.");
	        ioex.printStackTrace();
	    }
	}
	
	
	public Game[] getSavedGames(){
		Game[] savedGames = new Game[6];
		for(int i = 0; i < 6; i++){
			File file = new File (FILEPATH + i);
			if(file.exists()){
				try{
					ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
			        Game game = (Game) stream.readObject();
			        stream.close();
			        savedGames[i] = game;
			    } catch (ClassNotFoundException cnfex) {
			        System.err.println("Die Klasse des geladenen Objekts konnte nicht gefunden werden.");
			    } catch (IOException ioex) {
			        System.err.println("Das Objekt konnte nicht geladen werden.");
			        ioex.printStackTrace();
			    }
			}
		}
		return savedGames;
	}


	public GameboardController getGameboardController() {
		return gameboardController;
	}

	public void setGameboardController(GameboardController gameboardController) {
		this.gameboardController = gameboardController;
	}

	public GameStateController getGameStateController() {
		return gameStateController;
	}

	public void setGameStateController(GameStateController gameStateController) {
		this.gameStateController = gameStateController;
	}

	public PlayerController getPlayerController() {
		return playerController;
	}

	public void setPlayerController(PlayerController playerController) {
		this.playerController = playerController;
	}

	public HighscoreController getHighscoreController() {
		return highscoreController;
	}

	public void setHighscoreController(HighscoreController highscoreController) {
		this.highscoreController = highscoreController;
	}

	public MovementController getMovementController() {
		return movementController;
	}

	public void setMovementController(MovementController movementController) {
		this.movementController = movementController;
	}

	public NewGameController getNewGameController() {
		return newGameController;
	}

	public void setNewGameController(NewGameController newGameController) {
		this.newGameController = newGameController;
	}

	public NoviceAIController getNoviceAIController() {
		return noviceAIController;
	}

	public void setNoviceAIController(NoviceAIController noviceAIController) {
		this.noviceAIController = noviceAIController;
	}

	public SenseiAIController getSenseiAIController() {
		return senseiAIController;
	}
	
	public void setSenseiAIController(SenseiAIController senseiAIController){
		this.senseiAIController = senseiAIController;
	}
	
	public ZenMasterAIController getZenMasterAIController(){
		return this.zenMasterAIController;
	}

	public void setZenMasterAIController(ZenMasterAIController zenMasterAIController) {
		this.zenMasterAIController = zenMasterAIController;
	}

	public BrainAIController getBrainAIController() {
		return brainAIController;
	}

	public void setBrainAIAIController(BrainAIController brainAIController) {
		this.brainAIController = brainAIController;
	}

	public Onitama getOnitama() {
		return onitama;
	}

	public void setOnitama(Onitama onitama) {
		this.onitama = onitama;
	}

	public NetworkTry2 getNetworkController() {
		return networkController;
	}

	public void setNetworkController(NetworkTry2 networkController) {
		this.networkController = networkController;
	}

	public BetterNetwork getBetterNetworkController() {
		return betterNetworkController;
	}

	public void setBetterNetworkController(BetterNetwork betterNetworkController) {
		this.betterNetworkController = betterNetworkController;
	}


}
