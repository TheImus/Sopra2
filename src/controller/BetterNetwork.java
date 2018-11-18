package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.transfer.Sigmoid;
import org.neuroph.nnet.CompetitiveNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.UnsupervisedHebbianNetwork;
import org.neuroph.nnet.comp.layer.InputLayer;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.HopfieldLearning;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.TransferFunctionType;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLoggerFactory;

import model.Game;
import model.GameBoard;
import model.GameCard;
import model.GameMove;
import model.GameState;
import model.GameToken;
import model.Onitama;
import model.Player;
import model.Vector;
import sun.util.calendar.BaseCalendar.Date;


/*
 * For more information consult this link: https://www.baeldung.com/neuroph
 * another: https://sourceforge.net/p/neuroph/code/HEAD/tree/trunk/neuroph-2.8/Samples/src/main/java/org/neuroph/samples/XorMultiLayerPerceptronSample.java#l58
 */
public class BetterNetwork {
	public static final int INPUT_LAYER_SIZE = 46;
	public static final int HIDDEN_LAYER_COUNT = 3;
	public static final int HIDDEN_LAYER_SIZE = 25;
	public static final int OUTPUT_LAYER_SIZE = 1;
	public static final String FILEPATHGIT1 = System.getProperty("user.home")+ "/git/project2/KIFiles/betterKI1.aif";
	public static final String FILEPATHGIT2 = System.getProperty("user.home")+ "/git/project2/KIFiles/betterKI2.aif";
	public static final int TRAININGDURATION = 50;
	public static final int TRAININGDURATIONSENSEI = 100;
	public static final int PRINTDISTANCESENSEI = 10;
	public static final int PRINTDISTANCE = TRAININGDURATION/10;
	public static final int MAXMOVES = 30;
	private OnitamaController onitamaController;
	public static final double MINVAL = 0;			//darf nicht geandert werden, bei negativen zahlen dauert es sehr lange
	public static final int MAXVAL = 1;
	public static final int RANDDISTANCE = 5;
	public static final int RANDNETWORKDISTANCE = TRAININGDURATION/5;	
	
	
	
	public static void main(String[] args){
		System.out.println("starte Learning fuer betterNetwork");
		OnitamaController onitamaCont= new OnitamaController();
		BetterNetwork networkController = onitamaCont.getBetterNetworkController();
		NeuralNetwork<BackPropagation> network1 = null;
		NeuralNetwork<BackPropagation> network2 = null;
		

		try{
			network1 = (NeuralNetwork<BackPropagation>)(NeuralNetwork.createFromFile(FILEPATHGIT1));
			network2 = (NeuralNetwork<BackPropagation>)(NeuralNetwork.createFromFile(FILEPATHGIT2));	
			System.out.println("netzwerke geladen");
//			if(args.length == 1){
//				network1.save(System.getProperty("user.home") + "git/project2/Intelligenz/" + args[0] + "1.aif");
//				network2.save(System.getProperty("user.home") + "git/project2/Intelligenz/" + args[0] + "2.aif");
//			}
		}		
		catch(NeurophException fe){
			if(fe.getCause() instanceof FileNotFoundException){
				System.out.println("erstelle neue neurale Netze");
				network1 = createNewNeuralNetwork();
				network2 = createNewNeuralNetwork();
			}
			else{
				throw fe;
			}
		}
		 
		int i = 0;
		while(true){
			System.out.println("Iteration: " + i++);
			networkController.startTraining(network1, network2);
			networkController.trainAgainstZenMaster(network1,network2);
		}
		
		
	}
	
	
	public BetterNetwork(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}
	
	public void startTraining(NeuralNetwork<BackPropagation> network1, NeuralNetwork<BackPropagation> network2){
		NeuralNetwork<BackPropagation> randNetwork = createNewNeuralNetwork();

		
		Onitama trainingOnitama = new Onitama();
		onitamaController.setOnitama(trainingOnitama);
		NewGameController newGameController = onitamaController.getNewGameController();			
		GameStateController gameStateController = onitamaController.getGameStateController();
		MovementController movementController = onitamaController.getMovementController();
		for(int j=0;j<TRAININGDURATION;j++){
			if(j%PRINTDISTANCE==0){
				System.out.println("Durchlauf in Iteration: " + (j+1));
				
			}
			if(j%RANDNETWORKDISTANCE==0){
				learnOneIteration(network1, randNetwork, newGameController, gameStateController, movementController);
				learnOneIteration(randNetwork, network2, newGameController, gameStateController, movementController);
			}
			else{
				learnOneIteration(network1, randNetwork, newGameController, gameStateController, movementController);
		
			}
		}
		System.out.println("speichere netzwerke");
		NeuralNetwork<BackPropagation> newNetwork1 = createNewNeuralNetwork();
		NeuralNetwork<BackPropagation> newNetwork2 = createNewNeuralNetwork();
		double[] weights1 = doubleFromDouble(network1.getWeights());
		double[] weights2 = doubleFromDouble(network2.getWeights());
		newNetwork1.setWeights(weights1);
		newNetwork2.setWeights(weights2);
		newNetwork1.save(FILEPATHGIT1);		
		newNetwork2.save(FILEPATHGIT2);
		network1 = newNetwork1;
		network2 = newNetwork2;
	}


	private void learnOneIteration(NeuralNetwork<BackPropagation> network1, NeuralNetwork<BackPropagation> network2,
		NewGameController newGameController, GameStateController gameStateController,
		MovementController movementController) {
		DataSet dataSet1 = new DataSet(INPUT_LAYER_SIZE,OUTPUT_LAYER_SIZE);
		DataSet dataSet2 = new DataSet(INPUT_LAYER_SIZE,OUTPUT_LAYER_SIZE);
		newGameController.createDefaultGame();
		Game currentGame = onitamaController.getOnitama().getCurrentGame();
		currentGame.setMaxMoves(MAXMOVES);		
		Player player1 = currentGame.getPlayers().get(0);
		Player player2 = currentGame.getPlayers().get(1);
		
		for(int i=0;i<2*MAXMOVES +1;i++){				
			GameState currentGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();				
			Player currentPlayer = currentGameState.getCurrentPlayer();
			List<Player> winningPlayers = onitamaController.getGameStateController().getWinningPlayers(currentGameState);
			if(!winningPlayers.isEmpty()){	
				if(winningPlayers.contains(player1)){
					network2.learn(dataSet1);			
				}else if(winningPlayers.contains(player2)){//player2
					network1.learn(dataSet2);	
				}					
				break;
			}
			GameMove nextMove = null;
			double actMax = 0;
			if(currentPlayer.equals(player1)){
				for(GameMove move:movementController.getPossibleMovements(currentGameState, currentPlayer))	{
					GameState simulated = gameStateController.generateNextGameState(currentGameState, move);
					simulated.setSimulated(true);
					double [] input = createInputVector(simulated);					
					network1.setInput(input);
					network1.calculate();
					double actVal = network1.getOutput()[0];
					if(gameStateController.getWinningPlayers(simulated).contains(currentPlayer)){
						actVal=1;
					}
					else if(onitamaController.getSenseiAIController().canLoose(currentGameState, move)){
						actVal=0;
					}
					double[] output = {actVal};
					DataSetRow newRow = new DataSetRow(input,output);
					dataSet2.add(newRow);
					if(actVal>=actMax){
						actMax = actVal;
						nextMove = move;
					}
				}					
			}			
			else{
				for(GameMove move:movementController.getPossibleMovements(currentGameState, currentPlayer))	{
					GameState simulated = gameStateController.generateNextGameState(currentGameState, move);
					simulated.setSimulated(true);
					double [] input = createInputVector(simulated);					
					network2.setInput(input);
					network2.calculate();
					double actVal = network2.getOutput()[0];
					if(gameStateController.getWinningPlayers(simulated).contains(currentPlayer)){
						actVal=1;
					}
					else if(onitamaController.getSenseiAIController().canLoose(currentGameState, move)){
						actVal=0;
					}
					double[] output = {actVal};
					DataSetRow newRow = new DataSetRow(input,output);
					dataSet2.add(newRow);
					if(actVal>=actMax){
						actMax = actVal;
						nextMove = move;
					}						
				}
			}
			GameState nextGameState = gameStateController.generateNextGameState(currentGameState, nextMove);
			List<GameState> gameStates = onitamaController.getOnitama().getCurrentGame().getGameStates();
			gameStates.add(nextGameState);
			onitamaController.getOnitama().getCurrentGame().setGameStates(gameStates);
		}
	}


	public void trainAgainstZenMaster(NeuralNetwork<BackPropagation> network1, NeuralNetwork<BackPropagation> network2){
		Onitama trainingOnitama = new Onitama();
		onitamaController.setOnitama(trainingOnitama);
		NewGameController newGameController = onitamaController.getNewGameController();			
		GameStateController gameStateController = onitamaController.getGameStateController();
		MovementController movementController = onitamaController.getMovementController();
		ZenMasterAIController zenMaster = onitamaController.getZenMasterAIController();
		learnGameAgainstZenMaster(network1, newGameController, gameStateController, movementController, zenMaster);
		learnGameAgainstZenMaster(network2, newGameController, gameStateController, movementController, zenMaster);
		System.out.println("speichere netzwerke");
		NeuralNetwork<BackPropagation> newNetwork1 = createNewNeuralNetwork();
		NeuralNetwork<BackPropagation> newNetwork2 = createNewNeuralNetwork();
		double[] weights1 = doubleFromDouble(network1.getWeights());
		double[] weights2 = doubleFromDouble(network2.getWeights());
		newNetwork1.setWeights(weights1);
		newNetwork2.setWeights(weights2);
		newNetwork1.save(FILEPATHGIT1);		
		newNetwork2.save(FILEPATHGIT2);
		network1 = newNetwork1;
		network2 = newNetwork2;
	}
	
	private void learnGameAgainstZenMaster(NeuralNetwork<BackPropagation> network, NewGameController newGameController,
	GameStateController gameStateController, MovementController movementController, ZenMasterAIController zenMaster) {
		Game currentGame;
		for(int j=0;j<TRAININGDURATIONSENSEI;j++){
			if(j%PRINTDISTANCESENSEI==0){
				System.out.println("training gegen zen: " + j);
			}
			DataSet dataSet = new DataSet(INPUT_LAYER_SIZE,OUTPUT_LAYER_SIZE);
			newGameController.createDefaultGame();
			currentGame = onitamaController.getOnitama().getCurrentGame();
			currentGame.setMaxMoves(MAXMOVES);			
			Player player1 = currentGame.getPlayers().get(0);
			Player player2 = currentGame.getPlayers().get(1);	
			for(int i=0;i<MAXMOVES;i++){				
				GameState currentGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();				
				Player currentPlayer = currentGameState.getCurrentPlayer();
				List<Player> winningPlayers = onitamaController.getGameStateController().getWinningPlayers(currentGameState);
				if(!winningPlayers.isEmpty()){					
					if(winningPlayers.contains(player2)){	//ai soll lernen
						//System.out.println("gegen zen verloren");
						network.learn(dataSet);	
					}					
					break;
				}
				GameMove nextMove = null;			
				if(currentPlayer.equals(player1)){//player1 ist dran
					nextMove = calculateNextMove(currentGameState, network);
				}else{//player2 ist dran
					nextMove = zenMaster.getNextMove(currentGameState);
					double[] input = createInputVector(currentGameState);
					double[] output = {zenMaster.getValueInFourMoves(currentGameState)};
					DataSetRow newRow = new DataSetRow(input,output);
					dataSet.add(newRow);
					
				}
				
				GameState nextGameState = gameStateController.generateNextGameState(currentGameState, nextMove);
				List<GameState> gameStates = onitamaController.getOnitama().getCurrentGame().getGameStates();
				gameStates.add(nextGameState);
				onitamaController.getOnitama().getCurrentGame().setGameStates(gameStates);				
			}
		}
			
	}

	public GameMove calculateNextMove(GameState gameState, NeuralNetwork<BackPropagation> network){
		Player currentPlayer = gameState.getCurrentPlayer();
		GameMove result = null;
		double actMax = 0;
		for(GameMove move:onitamaController.getMovementController().getPossibleMovements(gameState, currentPlayer))	{
			GameState simulated = onitamaController.getGameStateController().generateNextGameState(gameState, move);
			simulated.setSimulated(true);
			double [] input = createInputVector(simulated);					
			network.setInput(input);
			network.calculate();
			double actVal = network.getOutput()[0];			
			if(onitamaController.getGameStateController().getWinningPlayers(simulated).contains(currentPlayer)){
				actVal=1;
			}
			else if(onitamaController.getSenseiAIController().canLoose(gameState, move)){
				actVal=0;
			}
			
			if(actVal>=actMax){
				actMax = actVal;
				result = move;
			}		
		
		}
		return result;
	}


	
	public static NeuralNetwork<BackPropagation> createNewNeuralNetwork(){
		NeuralNetwork<BackPropagation> neuralNetwork = null;
		neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,INPUT_LAYER_SIZE,HIDDEN_LAYER_SIZE,HIDDEN_LAYER_SIZE, HIDDEN_LAYER_SIZE, OUTPUT_LAYER_SIZE);
		BackPropagation learningRule = new BackPropagation();
		learningRule.setMaxIterations(1000);		
		neuralNetwork.setLearningRule(learningRule);		
		neuralNetwork.randomizeWeights();		
		return neuralNetwork;
	}
	
	
	
	public double[] createInputVector(GameState gameState){//
		double[] result = new double[INPUT_LAYER_SIZE];
		GameBoard gameBoard = gameState.getGameBoard();
		int index = 0;
		Player currentPlayer = gameState.getCurrentPlayer();
		PlayerController playerController = onitamaController.getPlayerController();
		GameToken playerMaster = playerController.getMasterToken(gameState, currentPlayer);
		Vector positionMaster = gameBoard.getPositionOf(playerMaster);
		result[index++] = positionMaster.getX();
		result[index++] = positionMaster.getY();
		List<GameToken> currentPlayerTokens = playerController.getPlayerTokens(gameState, currentPlayer);
		currentPlayerTokens.remove(playerMaster);
		for(GameToken token:currentPlayerTokens) {
			Vector position = gameBoard.getPositionOf(token);
			result[index++] = position.getX();
			result[index++] = position.getY();
		}
		while(index<10) {
			result[index++] = -1;
		}
		//System.out.println(" nach currentPlayer: " + getStringFromArray(result));
		List<Player> otherPlayers = onitamaController.getOnitama().getCurrentGame().getPlayers(); 
		otherPlayers.remove(currentPlayer);
		int playerCounter = 0;
		for(Player player:otherPlayers) {
			if(onitamaController.getGameboardController().getTokenCount(gameBoard, player) > 0){
				GameToken master = playerController.getMasterToken(gameState, player);
				Vector masterPosition = gameBoard.getPositionOf(master);
				result[index++] = masterPosition.getX();
				result[index++] = masterPosition.getY();			
				List<GameToken> playerTokens = playerController.getPlayerTokens(gameState, player);
				playerTokens.remove(playerMaster);
				for(GameToken token:playerTokens) {
					Vector position = gameBoard.getPositionOf(token);
					result[index++] = position.getX();
					result[index++] = position.getY();
				}
			}
			while(index<20+10*playerCounter) { //geloeschte Tokens werden durch -1 symbolisiert
				result[index++] = -1;
			}
			playerCounter++;
		}
		while(index<20) {
			result[index++] = -1;
		}
		
		//die naechsten 9 neuronen stehen fuer die karten auf dem Feld
		Map<Player,List<GameCard>> map = gameState.getCardDistribution();		//immer gleicher ablauf: zunaechst karten vom currentPlayer, dann die anderen spieler, dann freie Karte
		for(GameCard card: map.get(currentPlayer)){
			result[index++]= card.ordinal();
		}
		for(Player player: otherPlayers) {
			for(GameCard card:map.get(player)) {
				result[index++]=card.ordinal();
			}
		}			
		while(index<28) {
			result[index++] = -1;
		}
		result[index++]=gameState.getFreeGameCard().ordinal();
	//	System.out.println("index nach karten: " + index);
		//die naechsten 16 Neuronen sind fuer die moeglichen Spielzuege:
		//jede Karte hat hoechsten 4 moeglichkeiten mit jeweils x und y komponente, hat eine Karte weniger
		//Moeglichkeiten, so wird -5 angegeben (-1 ist ein regulaerer Wert)
		int anzahlMoeglichkeiten = 0;
		for(GameCard card: map.get(currentPlayer)) {
			for(Vector vector:card.getPossibleMovements()) {
				anzahlMoeglichkeiten++;
				result[index++]=vector.getX();
				result[index++]=vector.getY();
				//System.out.println("anzahl moeglichkeiten:" + anzahlMoeglichkeiten);
			}
			while(anzahlMoeglichkeiten<4) {		//es kann karten mit weniger als 4 moeglichkeiten geben
				//System.out.println("in while schleife");
				result[index++]=-10;
				result[index++]=-10;
				anzahlMoeglichkeiten++;
			}
			anzahlMoeglichkeiten=0;
		}
				
		//letzes neuron: verbleibene Zuege
		int maxMoves = onitamaController.getOnitama().getCurrentGame().getMaxMoves();
		if(maxMoves!=-1) {
			result[index++]= maxMoves-gameState.getRoundNumber();
		}
		else {
			result[index++] = MAXMOVES-gameState.getRoundNumber();
		}	
		return result;
	}
	
	
	public String getStringFromArray(double[] arr) {
		String res = "[";
		for(double d:arr) {
			res+=d + " ";
		}
		return res+"]";
	}
	
	public String getStringFromArray(Double[] arr) {
		String res = "[";
		for(double d:arr) {
			res+=d + " ";
		}
		return res+"]";
	}
	
	public void setOutputsFromInputLayer(NeuralNetwork<BackPropagation> network){
		List<Neuron> inputNeurons = network.getInputNeurons();
		for(int i=0;i<inputNeurons.size();i++){
			Neuron neuron = inputNeurons.get(i);
			neuron.setOutput(neuron.getNetInput());
		}
	}
	
	public double[] doubleFromDouble(Double[] arr){
		double[] res = new double[arr.length];
		for(int i=0;i<arr.length;i++){
			res[i] = arr[i];
		}
		return res;
	}
	
}

