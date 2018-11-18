package controller;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.NeuralNetworkType;

import model.GameMove;
import model.GameState;
import model.Player;
import model.PlayerColor;
import model.Vector;

public class BrainAIController extends AIController {
	private NeuralNetwork<BackPropagation> neuralNetwork1;
	private NeuralNetwork<BackPropagation> neuralNetwork2;
	private NetworkTry2 networkController;
	private BetterNetwork betterNetworkController;
	
	public BrainAIController(OnitamaController onitamaController){	//TODO: filepfad eventuell wieder aendern
		this.onitamaController = onitamaController;
		try{
		neuralNetwork1 = NeuralNetwork.createFromFile(NetworkTry2.FILEPATHGIT1);
		neuralNetwork2 = NeuralNetwork.createFromFile(NetworkTry2.FILEPATHGIT2);
//		neuralNetwork1 = NeuralNetwork.createFromFile(BetterNetwork.FILEPATHGIT1);
//		neuralNetwork2 = NeuralNetwork.createFromFile(BetterNetwork.FILEPATHGIT2);
		}
		catch(NeurophException fe){
			if(fe.getCause() instanceof FileNotFoundException){
				System.out.println("keine files gefunden");
			}
			else{
				throw fe;
			}
		}
		networkController = onitamaController.getNetworkController();
		betterNetworkController = onitamaController.getBetterNetworkController();
	}
	
	@Override
	public GameMove getNextMove(GameState gameStateNEVERUSE) {			//TODO: eventuell wieder zu altem brain aendern
		
		
		
		
		
		GameState gameState = new GameState(gameStateNEVERUSE);
		gameState.setSimulated(true);
		double input[] = networkController.createInputVector(gameState);
		Player player1 = onitamaController.getOnitama().getCurrentGame().getPlayers().get(0);
		Player player2 = onitamaController.getOnitama().getCurrentGame().getPlayers().get(1);
		double output[] = null;
		if(gameState.getCurrentPlayer().equals(player1)){
			neuralNetwork1.setInput(input);
			neuralNetwork1.calculate();
			output = neuralNetwork1.getOutput();
		}
		else{
			neuralNetwork2.setInput(input);
			neuralNetwork2.calculate();
			output = neuralNetwork2.getOutput();
		}
		output = networkController.adaptInputArray(output, gameState);
		GameMove result = networkController.calculateNextMove(output, gameState);
		if(!onitamaController.getMovementController().isValidGameMove(gameState, result) || canLoose(gameState, result)){
//			System.out.println("illegaler move abgefangen");
			result = onitamaController.getZenMasterAIController().getNextMove(gameState);
			if(canLoose(gameState, result)){
				result = onitamaController.getNoviceAIController().getNextMove(gameState);
			}
		}
		Vector posToken = gameState.getGameBoard().getPositionOf(result.getMovedToken());
//		System.out.println("waehle token an position: " + posToken.getX() + ", " + posToken.getY());
//		System.out.println("gehe nach " + gameState.getGameBoard().getDestination(result).getX() + ", " +gameState.getGameBoard().getDestination(result).getY() );
		
		
		
		//<NoviceFilter>
		self = gameState.getCurrentPlayer();
		List<GameMove> possibleMoves = getAllMoves(gameState);
		for (Iterator<GameMove> iterator = possibleMoves.listIterator(); iterator.hasNext(); ) {//for each entry we do the following
			GameMove currentMove = iterator.next();
		    if 		(canWin(gameState, currentMove)) {//if we can win, we win
		    	return currentMove;
		    }
		}
		//</NoviceFilter>
		
		
		
		return result;
//		gameState = new GameState(gameState);
//		gameState.setSimulated(true);
//		GameMove result = null;
//		Player player1 = onitamaController.getOnitama().getCurrentGame().getPlayers().get(0);
//		Player currentPlayer = gameState.getCurrentPlayer();
//		double actMax = 0;
//		if(currentPlayer.getPlayerColor().equals(PlayerColor.FIRE)){
//			for(GameMove move: onitamaController.getMovementController().getPossibleMovements(gameState, currentPlayer)){
//				GameState simulated = onitamaController.getGameStateController().generateNextGameState(gameState, move);
//				simulated.setSimulated(true);
//				double[] input = betterNetworkController.createInputVector(simulated);			
//				neuralNetwork1.setInput(input);
//				neuralNetwork1.calculate();
//				double actVal = neuralNetwork1.getOutput()[0];
//				if(actVal>=actMax){
//					actMax = actVal;
//					result = move;
//				}
//			}
//		}
//		else{
//			for(GameMove move: onitamaController.getMovementController().getPossibleMovements(gameState, currentPlayer)){
//				GameState simulated = onitamaController.getGameStateController().generateNextGameState(gameState, move);
//				simulated.setSimulated(true);
//				double[] input = betterNetworkController.createInputVector(simulated);			
//				neuralNetwork2.setInput(input);
//				neuralNetwork2.calculate();
//				double actVal = neuralNetwork2.getOutput()[0];
//				if(actVal>=actMax){
//					actMax = actVal;
//					result = move;
//				}
//			}
//		}
//		
//		return result;
	}
	
	public GameMove generateTip(){
		return null;
	}

}
