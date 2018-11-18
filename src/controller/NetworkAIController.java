package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.UnsupervisedHebbianNetwork;
import org.neuroph.nnet.comp.layer.InputLayer;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.nnet.learning.HopfieldLearning;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.TransferFunctionType;

import model.Game;
import model.GameBoard;
import model.GameCard;
import model.GameMove;
import model.GameState;
import model.GameToken;
import model.Onitama;
import model.Player;
import model.Vector;


/*
 * For more information consult this link: https://www.baeldung.com/neuroph
 * another: https://sourceforge.net/p/neuroph/code/HEAD/tree/trunk/neuroph-2.8/Samples/src/main/java/org/neuroph/samples/XorMultiLayerPerceptronSample.java#l58
 */
public class NetworkAIController {
	public static final int INPUT_LAYER_SIZE = 66;
	public static final int HIDDEN_LAYER_COUNT = 4;
	public static final int HIDDEN_LAYER_SIZE = 25 ;
	public static final int OUTPUT_LAYER_SIZE = 41;
	public static final String FILEPATH1 = "";
	public static final String FILEPATH2 = "";
	public static final int TRAININGDURATION = 1000;
	public static final int MAXMOVES = 50;
	private OnitamaController onitamaController;
	public static final double MINVAL = 0;
	
	
	//67 input nodes
	//4 hidden layer
	//25 hidden nodes per hidden layer
	//3 output nodes
	
	private NeuralNetwork<DynamicBackPropagation> neuralNetwork;//TODO might want to change Hopfield learning
	
	public static void main(String[] args){
		//TODO: learning rule eventuell anpassen
		NetworkAIController networkController = new NetworkAIController();
		NeuralNetwork<DynamicBackPropagation> network1 = null;
		NeuralNetwork<DynamicBackPropagation> network2 = null;

		try{
			network1 = (NeuralNetwork<DynamicBackPropagation>)(NeuralNetwork.createFromFile(FILEPATH1));
			network2 = (NeuralNetwork<DynamicBackPropagation>)(NeuralNetwork.createFromFile(FILEPATH2));			
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
		networkController.startTraining(network1,network2);		
	}
	
	public NetworkAIController() {
		onitamaController = new OnitamaController();
	
	}
	
	public void startTraining(NeuralNetwork<DynamicBackPropagation> network1, NeuralNetwork<DynamicBackPropagation> network2){
		Onitama trainingOnitama = new Onitama();
		onitamaController.setOnitama(trainingOnitama);
		NewGameController newGameController = onitamaController.getNewGameController();			
		GameStateController gameStateController = onitamaController.getGameStateController();		
		Game currentGame;
		for(int j=0;j<TRAININGDURATION;j++){	
			//if(j%50==0) {
				System.out.println("gerade bei trainingsdurchlauf: " + (j+1));
			//}
			DataSet dataSet1 = new DataSet(INPUT_LAYER_SIZE,OUTPUT_LAYER_SIZE);
			DataSet dataSet2 = new DataSet(INPUT_LAYER_SIZE,OUTPUT_LAYER_SIZE);
			newGameController.createDefaultGame();
			currentGame = onitamaController.getOnitama().getCurrentGame();
			
			Player player1 = currentGame.getPlayers().get(0);
			Player player2 = currentGame.getPlayers().get(1);
			
			
			for(int i=0;i<MAXMOVES;i++){				
				GameState currentGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();
				
				Player currentPlayer = currentGameState.getCurrentPlayer();
				List<Player> winningPlayers = onitamaController.getGameStateController().getWinningPlayers(currentGameState);
				if(!winningPlayers.isEmpty()){
					if(winningPlayers.contains(player1)){
						System.out.println("groesse datenset" + dataSet1.getRows().size());
						System.out.println("Before: " + getStringFromArray(network1.getWeights()));
						network2.learn(dataSet1);				
						System.out.println("After: " + getStringFromArray(network1.getWeights()));
					}else{//player2
						System.out.println("groesse datenset " + dataSet2.getRows().size());
						System.out.println(getStringFromArray(network1.getWeights()));
						network1.learn(dataSet2);	
						System.out.println(getStringFromArray(network1.getWeights()));
					}					
					break;
				}
				GameMove nextMove = null;				
				if(currentPlayer.equals(player1)){//player1 ist dran
					double [] input = createInputVector(currentGameState);
					network1.setInput(input);
					System.out.println("input = " + getStringFromArray(input));		
					network1.calculate();
					double[] output = network1.getOutput();
					System.out.println("output vor remove = " + getStringFromArray(output));
					output = removeIllegalActions(output,currentGameState);
					DataSetRow newRow = new DataSetRow(input,output); 
					dataSet1.add(newRow);
					nextMove = calculateNextMove(output, currentGameState);
				}else{//player2 ist dran
					double [] input = createInputVector(currentGameState);
					network2.setInput(input);
					network2.calculate();
					double[] output = network2.getOutput();
					System.out.println("output vor remove = " + getStringFromArray(output));
					output = removeIllegalActions(output,currentGameState);
					DataSetRow newRow = new DataSetRow(input,output);
					dataSet2.add(newRow);
					nextMove = calculateNextMove(output, currentGameState);
				}
				MovementController moveCont = onitamaController.getMovementController();
				if(!moveCont.isValidGameMove(currentGameState, nextMove)) {
					System.out.println("ungueltiger zug");
				}
				
				GameState nextGameState = gameStateController.generateNextGameState(currentGameState, nextMove);
				List<GameState> gameStates = onitamaController.getOnitama().getCurrentGame().getGameStates();
				gameStates.add(nextGameState);
				onitamaController.getOnitama().getCurrentGame().setGameStates(gameStates);

			}
			
		}
		network1.save(FILEPATH1);
		network2.save(FILEPATH2);
	}
	
	public double[] removeIllegalActions(double[] input,GameState gameState){ //TODO: minimaler wert geaendert
		double[] copy = new double[input.length];
		for(int i=0;i<input.length;i++) {
			copy[i] = input[i];
		}
		for(int i=0;i<copy.length-1;i++) {
			GameMove actMove = convertIntToMove(i, gameState);
			if(actMove==null || !onitamaController.getMovementController().isValidGameMove(gameState, actMove)) {
				copy[i] = MINVAL;
			}
		}
		return copy;
	}
	
	public GameMove calculateNextMove(double[] output, GameState gameState){  //output only contains valid moves		
		GameMove result = null;
		int winner = 0;
		double actBest = output[0];
		for(int i=0;i<output.length-1;i++){
			if(output[i]>actBest){
				actBest = output[i];
				winner = i;
			}
		}
		result = convertIntToMove(winner,gameState);
		return result;	
	}
	
	public GameMove convertIntToMove(int index, GameState gameState){		
		PlayerController playerController = onitamaController.getPlayerController();
		Player currentPlayer = gameState.getCurrentPlayer();
		List<GameToken> tokenList = playerController.getPlayerTokens(gameState, currentPlayer);
		List<GameCard> cardList = gameState.getCardDistribution().get(currentPlayer);
		GameMove result = null;
		GameToken resultToken = null;
		GameCard resultCard = null;
		Vector resultVector = null;
		if(index/8<tokenList.size()){				//TODO nochmal rechnung ueberpruefen
			resultToken = tokenList.get(index/8);
		}
		else{
			return null;
		}
		resultCard = cardList.get((index/4)%2);			//TODO nochmal ueberpruefen, vielleicht modulo
		List<Vector> possibleMoves = resultCard.getPossibleMovements();
		if(index%4<possibleMoves.size()) {
			resultVector = possibleMoves.get(index%4);
		}
		else {
			return null;
		}
		result = new GameMove(resultToken,resultCard,resultVector);
		return result;
	}
	
	public static NeuralNetwork<DynamicBackPropagation> createNewNeuralNetwork(){
		NeuralNetwork<DynamicBackPropagation> neuralNetwork = null;
		neuralNetwork = new NeuralNetwork<DynamicBackPropagation>();
		DynamicBackPropagation learningRule = new DynamicBackPropagation();
		learningRule.setMaxIterations(1000);		
		neuralNetwork.setLearningRule(learningRule);
		Layer inputLayer = new InputLayer(INPUT_LAYER_SIZE);
		Layer outputLayer = new Layer();					
				
//		for(int i = 0; i < INPUT_LAYER_SIZE; i++){//create inputLayer neurons
//			Neuron inputNeuron = new Neuron();
//			inputLayer.addNeuron(inputNeuron);
//			inputNeuron.setTransferFunction(new Sigmoid());
////			Neuron connectionNeuron = new Neuron();
////			ConnectionFactory.createConnection(connectionNeuron, inputNeuron);
////			Connection connection = connectionNeuron.getOutConnections().get(0);
////			connection.setWeight(new Weight(0));	
//			System.out.println(i);
//		}
		neuralNetwork.addLayer(inputLayer);
		neuralNetwork.setInputNeurons(inputLayer.getNeurons());
				
		Layer hiddenLayer;
		for(int x = 0; x < HIDDEN_LAYER_COUNT; x++){
			hiddenLayer = new Layer();
			for(int y = 0; y < HIDDEN_LAYER_SIZE; y++){
				Neuron neuron = new Neuron();
				hiddenLayer.addNeuron(neuron);
				neuron.setTransferFunction(new Sigmoid());
			}
			neuralNetwork.addLayer(hiddenLayer);
		}
				
		for(int i = 0; i < OUTPUT_LAYER_SIZE; i++){//create outputLayer neurons
			Neuron neuron = new Neuron();
			outputLayer.addNeuron(neuron);
			neuron.setTransferFunction(new Sigmoid());
		}
		neuralNetwork.addLayer(outputLayer);
		neuralNetwork.setOutputNeurons(outputLayer.getNeurons());
				
		ConnectionFactory.fullConnect(inputLayer, neuralNetwork.getLayerAt(0));
		for(int i = 0; i < (neuralNetwork.getLayersCount()-1); i++){
			ConnectionFactory.fullConnect(neuralNetwork.getLayerAt(i), neuralNetwork.getLayerAt(i+1));
		}
		ConnectionFactory.fullConnect(neuralNetwork.getLayerAt(0), neuralNetwork.getLayerAt(neuralNetwork.getLayersCount()-1), false);//
		
		
		neuralNetwork.randomizeWeights();		
		return neuralNetwork;
	}
	
	
	
	public double[] createInputVector(GameState gameState){//
		double[] result = new double[INPUT_LAYER_SIZE];
		GameBoard gameBoard = gameState.getGameBoard();
		int index = 0;
		Player currentPlayer = gameState.getCurrentPlayer();
		PlayerController playerController = onitamaController.getPlayerController();
		List<GameToken> currentPlayerTokens = playerController.getPlayerTokens(gameState, currentPlayer);
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
			List<GameToken> playerTokens = playerController.getPlayerTokens(gameState, player);
			for(GameToken token:playerTokens) {
				Vector position = gameBoard.getPositionOf(token);
				result[index++] = position.getX();
				result[index++] = position.getY();
			}
			while(index<20+10*playerCounter) { //geloeschte Tokens werden durch -1 symbolisiert
				result[index++] = -1;
			}
			playerCounter++;
		}
		while(index<40) {
			result[index++] = -1;
		}
		if(index!=40) {
			//System.out.println("irgendwas stimmt nicht, sind bei index: " + index);
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
		result[index++]=gameState.getFreeGameCard().ordinal();
		while(index<49) {
			result[index++] = -1;
		}
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
				result[index++]=-5;
				result[index++]=-5;
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
	
	public String getFilepath1() {
		return FILEPATH1;
	}
	
	public String getFilepath2() {
		return FILEPATH2;
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
	
	public void setOutputsFromInputLayer(NeuralNetwork<DynamicBackPropagation> network){
		List<Neuron> inputNeurons = network.getInputNeurons();
		for(int i=0;i<inputNeurons.size();i++){
			Neuron neuron = inputNeurons.get(i);
			neuron.setOutput(neuron.getNetInput());
		}
	}
	
	
}
