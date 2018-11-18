package controller;

import model.GameMove;
import model.GameState;

public class SenseiAIController extends AIController {
	private ZenMasterAIController zenMaster;
	
	public SenseiAIController(OnitamaController onitamaController){
		this.onitamaController = onitamaController;
		zenMaster = new ZenMasterAIController(onitamaController);
	}
	
	public GameMove getNextMove(GameState gameState){
		return zenMaster.getNextMove(gameState,(stateOfGame, currentPlayer) -> valueFunction(stateOfGame,currentPlayer));
	}
	
	

}
