package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import model.GameBoard;
import model.GameMove;
import model.GameState;
import model.GameToken;
import model.Player;
import model.Vector;

public class ZenMasterAIV2Controller extends AIController {		//betrachtet alle moeglichen zuege fuer spieler und naechsten zug vom gegner
	
	public ZenMasterAIV2Controller(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}

	@Override
	public GameMove getNextMove(GameState gameStateNEVERUSE) {
		GameState currentGameState = new GameState(gameStateNEVERUSE);
		self = currentGameState.getCurrentPlayer();
		
		return null;
	}
}
