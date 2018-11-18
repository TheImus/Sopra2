package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import model.GameMove;
import model.GameState;
import model.Player;

public class ZenMasterAIController extends AIController {		//betrachtet alle moeglichen zuege fuer spieler und naechsten zug vom gegner
	
	public static final int DEPTH = 6;
	
	public ZenMasterAIController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}
	
	public GameMove getNextMove(GameState gameState, BiFunction<GameState,Player,Double> function){
		gameState = new GameState(gameState);
		gameState.setSimulated(true);
		Random rand = java.util.concurrent.ThreadLocalRandom.current();
		final long start =  System.currentTimeMillis();
		double maxVal = Integer.MIN_VALUE;
		List<GameMove> actBest = new ArrayList<>(); 
		Player currentPlayer = gameState.getCurrentPlayer();
		MovementController moveCont = onitamaController.getMovementController();
		GameStateController gameStateCont = onitamaController.getGameStateController();
		if(moveCont.getPossibleMovements(gameState,currentPlayer).size()==0) {System.out.println("kein zug moeglich");}
		for(GameMove move : moveCont.getPossibleMovements(gameState,currentPlayer)) {
			if(System.currentTimeMillis()-start > 7000){
				//System.out.println("vorzeitig abgebrochen!");
				//return actBest.get(rand.nextInt(actBest.size()));
			}
			double minVal = Integer.MAX_VALUE;
			GameState simulatedState = gameStateCont.generateNextGameState(gameState,move);
			//simulatedState.setSimulated(true);
			//gameStateCont.getWinningPlayers(simulatedState).contains(currentPlayer)
			if(gameStateCont.getWinningPlayers(simulatedState).contains(currentPlayer)){
				return move;
			}
			Player currentSimulatedPlayer = simulatedState.getCurrentPlayer();
			for(GameMove simulatedMove : moveCont.getPossibleMovements(simulatedState,currentSimulatedPlayer)) {
				GameState nextSimulatedState = gameStateCont.generateNextGameState(simulatedState,simulatedMove);
				//nextSimulatedState.setSimulated(true);
				double actVal = 0;
				if(gameStateCont.getWinningPlayers(nextSimulatedState).contains(currentSimulatedPlayer)) {			//hinzugefuegt
					actVal=Integer.MIN_VALUE;
				}
				else {
					actVal = function.apply(nextSimulatedState,currentPlayer);
				}
				if(actVal<minVal) {
					minVal = actVal;
				}
			}
			if(minVal>maxVal) {
				maxVal = minVal;
				actBest.clear();
				actBest.add(move);
			}
			else if(minVal==maxVal) {
				actBest.add(move);
			}
		}
		//System.out.println("actBest = "+ maxVal);
		int next = rand.nextInt(actBest.size());
		GameMove result = actBest.get(next);
		return result;
	}
	
	public GameMove getNextMoveMorePlayers(GameState gameState, BiFunction<GameState,Player,Double> function){ //TODO fertig stellen
		gameState = new GameState(gameState);
		Random rand = new Random();
		final long start =  System.currentTimeMillis();
		double maxVal = Integer.MIN_VALUE;
		List<GameMove> actBest = new ArrayList<>();
		Player currentPlayer = gameState.getCurrentPlayer();
		MovementController moveCont = onitamaController.getMovementController();
		GameStateController gameStateCont = onitamaController.getGameStateController();
		if(moveCont.getPossibleMovements(gameState,currentPlayer).size()==0) {System.out.println("kein zug moeglich");}
		for(GameMove move : moveCont.getPossibleMovements(gameState,currentPlayer)) {
			if(System.currentTimeMillis()-start > 7000){
				//System.out.println("vorzeitig abgebrochen!");
				//return actBest.get(rand.nextInt(actBest.size()));
			}
			double minVal = Integer.MAX_VALUE;
			GameState simulatedState = gameStateCont.generateNextGameState(gameState,move);
			//gameStateCont.getWinningPlayers(simulatedState).contains(currentPlayer)
			if(gameStateCont.getWinningPlayers(simulatedState).contains(currentPlayer)){
				return move;
			}
			simulatedState.setSimulated(true);
			List<Player> otherPlayers = new ArrayList<>();
			otherPlayers.addAll(gameState.getPlayers());
			otherPlayers.remove(currentPlayer);
			for(Player currentSimulatedPlayer : otherPlayers) {
			for(GameMove simulatedMove : moveCont.getPossibleMovements(simulatedState,currentSimulatedPlayer)) {
				GameState nextSimulatedState = gameStateCont.generateNextGameState(simulatedState,simulatedMove);
				nextSimulatedState.setSimulated(true);
				double actVal = 0;
				if(gameStateCont.getWinningPlayers(nextSimulatedState).contains(currentSimulatedPlayer)) {			//hinzugefuegt
					actVal=Integer.MIN_VALUE;
				}
				else {
					actVal = function.apply(nextSimulatedState,currentPlayer);
				}
				if(actVal<minVal) {
					minVal = actVal;
				}
			}
			}
			if(minVal>maxVal) {
				maxVal = minVal;
				actBest.clear();
				actBest.add(move);
			}
			else if(minVal==maxVal) {
				actBest.add(move);
			}
		}
		System.out.println("actBest = "+ maxVal);
		int next = rand.nextInt(actBest.size());
		GameMove result = actBest.get(next);
		return result;
	}
	
	@Override
	public GameMove getNextMove(GameState gameState) {
		int playerNumber = gameState.getPlayers().size();
		switch(playerNumber){
			default:
				return getNextMove(gameState,(stateOfGame, currentPlayer) -> getValueInTwoMoves(stateOfGame));
		}
		//return alphaBeta(gameState);	
	}
	
	
	public GameMove getNextMoveTwoForward(GameState gameState) {		//guckt 4 Schritte in die Zukunft, gleiches wie oben, anstatt valueFunction nimmt er valueFunction in zwei Zügen Zukunft		final long start =  System.currentTimeMillis();
		return getNextMove(gameState,(stateOfGame, currentPlayer) -> getValueInTwoMoves(stateOfGame));					
	}
	
	public double getValueInTwoMoves(GameState gameState) {
		final long start =  System.currentTimeMillis();
		double maxVal = Integer.MIN_VALUE;
		GameMove actBest = null;
		Player currentPlayer = gameState.getCurrentPlayer();
		MovementController moveCont = onitamaController.getMovementController();
		GameStateController gameStateCont = onitamaController.getGameStateController();
		for(GameMove move : moveCont.getPossibleMovements(gameState,currentPlayer)) {
			if(System.currentTimeMillis()-start > 7000){
				System.out.println("vorzeitig abgebrochen!");
				return maxVal;
			}
			double minVal = Integer.MAX_VALUE;
			GameState simulatedState = gameStateCont.generateNextGameState(gameState,move);
			simulatedState.setSimulated(true);
			if(gameStateCont.getWinningPlayers(simulatedState).contains(currentPlayer)){
				return Integer.MAX_VALUE;
			}
			Player currentSimulatedPlayer = simulatedState.getCurrentPlayer();
			for(GameMove simulatedMove : moveCont.getPossibleMovements(simulatedState,currentSimulatedPlayer)) {
				GameState nextSimulatedState = gameStateCont.generateNextGameState(simulatedState,simulatedMove);
				nextSimulatedState.setSimulated(true);
				double actVal = valueFunction(nextSimulatedState,currentPlayer);
				if(actVal<minVal) {
					minVal = actVal;
				}
			}
			if(minVal>maxVal) {
				maxVal = minVal;
				actBest = move;
			}
		}
		return maxVal;
	}
	
	public GameMove getNextMoveFourForward(GameState gameState) {		//guckt 4 Schritte in die Zukunft, gleiches wie oben, anstatt valueFunction nimmt er valueFunction in zwei Zügen Zukunft		final long start =  System.currentTimeMillis();
		return getNextMove(gameState,(stateOfGame, currentPlayer) -> getValueInFourMoves(stateOfGame));				
	}
	
	public double getValueInFourMoves(GameState gameState) {		//guckt 4 Schritte in die Zukunft, gleiches wie oben, anstatt valueFunction nimmt er valueFunction in zwei Zügen Zukunft		final long start =  System.currentTimeMillis();
		Random rand = new Random();
		final long start =  System.currentTimeMillis();
		double maxVal = Integer.MIN_VALUE;
		List<GameMove> actBest = new ArrayList<>();
		Player currentPlayer = gameState.getCurrentPlayer();
		MovementController moveCont = onitamaController.getMovementController();
		GameStateController gameStateCont = onitamaController.getGameStateController();
		for(GameMove move : moveCont.getPossibleMovements(gameState,currentPlayer)) {
			if(System.currentTimeMillis()-start > 7000){
				System.out.println("vorzeitig abgebrochen!");
				return maxVal;
			}
			double minVal = Integer.MAX_VALUE;
			GameState simulatedState = gameStateCont.generateNextGameState(gameState,move);
			simulatedState.setSimulated(true);
			if(gameStateCont.getWinningPlayers(gameState).contains(currentPlayer)){
				return Integer.MAX_VALUE;
			}
			Player currentSimulatedPlayer = simulatedState.getCurrentPlayer();
			for(GameMove simulatedMove : moveCont.getPossibleMovements(simulatedState,currentSimulatedPlayer)) {
				GameState nextSimulatedState = gameStateCont.generateNextGameState(simulatedState,simulatedMove);
				nextSimulatedState.setSimulated(true);
				double actVal = getValueInTwoMoves(nextSimulatedState);
				if(actVal<minVal) {
					minVal = actVal;
				}
			}
			if(minVal>maxVal) {
				maxVal = minVal;
				actBest.clear();
				actBest.add(move);
			}
			else if(minVal==maxVal) {
				actBest.add(move);
			}
		}
		
		return maxVal;
	}
	private GameMove returnMove;
	
	public GameMove alphaBeta(GameState gameState){
		returnMove = null;
		gameState = new GameState(gameState);
		gameState.setSimulated(true);
		max(DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, gameState, gameState.getCurrentPlayer());
		return returnMove;
	}
	
	
	
	public double max(int depth, double alpha, double beta, GameState gameState, Player aiPlayer){
		if(depth == 0){
			return valueFunction(gameState, aiPlayer);
		}
		double maxVal = alpha;
		for(GameMove move: onitamaController.getMovementController().getPossibleMovements(gameState, gameState.getCurrentPlayer())){
			if(depth==DEPTH && returnMove ==null){
				returnMove = move;
			}
			GameState simulated = onitamaController.getGameStateController().generateNextGameState(gameState, move);
			simulated.setSimulated(true);
			double val = 0;
			if(onitamaController.getGameStateController().getWinningPlayers(simulated).contains(aiPlayer)){
				if(depth==DEPTH){
					returnMove = move;
				}
				maxVal = Double.POSITIVE_INFINITY;
				break;				
			}
			else if(onitamaController.getGameStateController().getWinningPlayers(simulated).size()>0){
				val = Double.NEGATIVE_INFINITY;
				continue;
			}
			val = min(depth-1, maxVal,beta, simulated, aiPlayer);
			if(val>maxVal){
				maxVal = val;
				if(maxVal >=beta){
					break;
				}
				if(depth == DEPTH){
					returnMove = move;
					//result = move;
				}
			}
			
		}
		
		return maxVal;
	}
	
	
	public double min(int depth, double alpha, double beta, GameState gameState, Player aiPlayer){
		if(depth == 0){
			return valueFunction(gameState, aiPlayer);
		}
		double minVal = beta;
		for(GameMove move: onitamaController.getMovementController().getPossibleMovements(gameState, gameState.getCurrentPlayer())){
			GameState simulated = onitamaController.getGameStateController().generateNextGameState(gameState, move);
			simulated.setSimulated(true);
			double val = 0;
			if(onitamaController.getGameStateController().getWinningPlayers(simulated).contains(simulated.getCurrentPlayer())){
				return Double.NEGATIVE_INFINITY;
			}
			else if(onitamaController.getGameStateController().getWinningPlayers(simulated).contains(aiPlayer)){
				continue;
			}
			val = max(depth-1, alpha,minVal, simulated, aiPlayer);
			if(val<minVal){
				minVal = val;
				if(minVal<=alpha){
					break;
				}
			}
			
		}		
		return minVal;
	}
	

		
}
