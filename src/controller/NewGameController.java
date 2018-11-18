package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import model.Game;
import model.GameCard;
import model.Player;
import model.PlayerColor;
import model.PlayerType;


public class NewGameController {

	private OnitamaController onitamaController;
	private Scanner fileScanner;
	
	/**
	 * Constructor for newGameController
	 * @param onitmaController
	 */
	public NewGameController(OnitamaController onitmaController){
		this.onitamaController = onitmaController;
	}

	/**
	 * Creates a new default game as current game
	 */
	public void createDefaultGame() {
		Game game = new Game();
		if(onitamaController == null)
			System.out.println("onitamanull");
		if(onitamaController.getOnitama() == null)
			System.out.println("onnnnn");
		onitamaController.getOnitama().setCurrentGame(game);
	}

	/**
	 * Adds a player to current game
	 * @param name Playername
	 * @param type human or AI?
	 * @param color Playercolor, if there already is a Player with this color, the new Player will get a free one
	 */
	public void addPlayer(String name, PlayerType type, PlayerColor color) {
		Game currentGame = onitamaController.getOnitama().getCurrentGame();
		List<Player> currentPlayers = currentGame.getPlayers();
		int colorIndex = -1;
		for(Player player : currentPlayers){
			if(player.getPlayerColor().equals(color)){
				colorIndex = currentPlayers.indexOf(player);
			}
		}
		if(colorIndex > -1){
			currentPlayers.remove(colorIndex);
		}
		Player player = new Player(name, type, color);	
		currentPlayers.add(player);
		currentGame.setPlayers(currentPlayers);
	}

	/**
	 * Loads a starting configuration from file, and
	 * creates a new current game from it
	 * @param file .txt-file with starting configuration
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public void loadConfiguration(File file, Player player1, Player player2) throws FileNotFoundException, UnsupportedEncodingException, NullPointerException {
		if(file == null){
			throw new NullPointerException();
		}
		//new Scanner for File
		fileScanner = new Scanner(file);
		//create a new default game (2 player)
		//createDefaultGame();
		Game currentGame = onitamaController.getOnitama().getCurrentGame();
		List<Player> players = new ArrayList<Player>();
		//preliminaries for cardDistribution from file
		HashMap<Player, List<GameCard>> cardDistribution = new HashMap<Player, List<GameCard>>();
		players.add(player1);
		players.add(player2);
		currentGame.setPlayers(players);
		ArrayList<GameCard> playerOneCards = new ArrayList<GameCard>();
		ArrayList<GameCard> playerTwoCards = new ArrayList<GameCard>();
		//there should be 5 words in the file
		for(int i = 0; i < 4; i++){
			if(fileScanner.hasNext()){
				//convert the next word from the file to a gamecard
				String nextCardString = fileScanner.next();
				GameCard nextCard = GameCard.FROM_STRING.get(nextCardString);
				if(nextCard == null){
					throw new UnsupportedEncodingException();
				}
				if(i<2){
					playerOneCards.add(nextCard);
				}
				else{
					playerTwoCards.add(nextCard);
				}
			}
			else{
				throw new UnsupportedEncodingException();
			}
		}
		if(fileScanner.hasNext()){
			String nextCardString = fileScanner.next();
			GameCard nextCard = GameCard.FROM_STRING.get(nextCardString);
			currentGame.setInitialFreeCard(nextCard);
		}
		else{
			throw new UnsupportedEncodingException();
		}
		//close filescanner and set card distribution from file
		fileScanner.close();
		if(playerOneCards.contains(playerTwoCards.get(0)) || playerOneCards.contains(playerTwoCards.get(1)) 
				|| playerOneCards.contains(currentGame.getInitialFreeCard())){
			throw new UnsupportedEncodingException();
		}
		cardDistribution.put(player1, playerOneCards);
		cardDistribution.put(player2, playerTwoCards);
		currentGame.setInitialCardDistribution(cardDistribution);
		// find starting player
		PlayerColor startingColor;
		final int PLAYER_COUNT_MIN = 2;
		if (players.size() == PLAYER_COUNT_MIN) {
			startingColor = currentGame.getInitialFreeCard().getColor();
			for (Player player : players) {
				if (player.getPlayerColor() != startingColor) {
					currentGame.setStartingPlayer(player);
				}
			}
		} else {
			Random rand = ThreadLocalRandom.current();
			currentGame.setStartingPlayer(players.get(rand.nextInt(players.size())));
		}
	}

	/**
	 * Sets BoardSize of current game
	 * @param size new board size of current game to set
	 */
	public void setBoardSize(int size) {
		if(size<5 || size % 2 == 0){
			return;
		}
		Game currentGame = onitamaController.getOnitama().getCurrentGame();
		if(currentGame.getPlayers().size()>2 && size < 7){
			return;
		}
		currentGame.setBoardSize(size);
	}

	/**
	 * Sets maximum amount of moves for current game
	 * @param maxMove maximum amount of moves
	 */
	public void setMaxMoves(int maxMove) {
		if(maxMove < 0){
			return;
		}
		Game currentGame = onitamaController.getOnitama().getCurrentGame();
		currentGame.setMaxMoves(maxMove);
	}

	/**
	 * Removes a player from current game
	 * @param player to remove from current game
	 */
	public void removePlayer(Player player) {
		Game currentGame = onitamaController.getOnitama().getCurrentGame();
		List<Player> players = currentGame.getPlayers();
		players.remove(player);
		currentGame.setPlayers(players);
	}

	/**
	 * 
	 * @return onitamaController
	 */
	public OnitamaController getOnitamaController() {
		return onitamaController;
	}

	/**
	 * Sets onitamaController
	 * @param onitamaController
	 */
	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}

}
