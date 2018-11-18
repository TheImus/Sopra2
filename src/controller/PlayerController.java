package controller;


import java.util.List;

import java.util.*;

import model.GameState;
import model.GameToken;
import model.Player;

public class PlayerController {

	private OnitamaController onitamaController;

	public OnitamaController getOnitamaController() {
		return onitamaController;
	}

	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}

	public PlayerController(OnitamaController onitamaController) {
		this.onitamaController  = onitamaController;
	}

	public List<GameToken> getPlayerTokens(GameState gameState, Player player) {
		GameToken[][] board = gameState.getGameBoard().getBoard();
		List<GameToken> result = new ArrayList<>();
		for(GameToken[] tokenArray: board){
			for(GameToken token:tokenArray){
				if(token!=null && token.getPlayer().equals(player)){
					result.add(token);
				}
			}
		}		
		return result;
	}

	public boolean isAlive(GameState gameState, Player player) {
		//return onitamaController.getGameboardController().getTokenCount(gameState.getGameBoard(), player) > 0;
		return getPlayerTokens(gameState, player).size() > 0;
	}
	
	public List<Player> getDiedPlayers(GameState oldGameState, GameState newGameState){
		List<Player> result = new ArrayList<>();
		List<Player> allPlayers = onitamaController.getOnitama().getCurrentGame().getPlayers();
		List<Player> alivePlayersOldGameState = new ArrayList<>();
		for(Player p:allPlayers){
			if(isAlive(oldGameState,p)){
				alivePlayersOldGameState.add(p);
			}
		}
		
		List<Player> alivePlayersNewGameState = new ArrayList<>();
		for(Player p:allPlayers){
			if(isAlive(newGameState,p)){
				alivePlayersNewGameState.add(p);
			}
		}
		
		for(Player player:alivePlayersOldGameState){
			if(!alivePlayersNewGameState.contains(player)){
				result.add(player);
			}
		}		
		
		return result;
	}
	
	public GameToken getMasterToken(GameState gameState, Player player){
		List<GameToken> tokenList = getPlayerTokens(gameState,player);
		GameToken playerMaster = null;
		for(GameToken token:tokenList) {
			if(token.isMaster()) {
				playerMaster = token;
			}
		}
		
		return playerMaster;
	}

}
