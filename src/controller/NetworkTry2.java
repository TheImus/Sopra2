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
public class NetworkTry2 {
	public static final int INPUT_LAYER_SIZE = 66;
	public static final int HIDDEN_LAYER_COUNT = 4;
	public static final int HIDDEN_LAYER_SIZE = 25 ;
	public static final int OUTPUT_LAYER_SIZE = 41;
	public static final String FILEPATHGIT1 = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).getAbsolutePath() + "/ki1.aif";
	public static final String FILEPATHGIT2 = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).getAbsolutePath() + "/ki2.aif";
	public static final int TRAININGDURATION = 1000;
	public static final int TRAININGDURATIONSENSEI = 100;
	public static final int PRINTDISTANCESENSEI = 10;
	public static final int PRINTDISTANCE = TRAININGDURATION/10;
	public static final int MAXMOVES = 50;
	private OnitamaController onitamaController;
	public static final double MINVAL = 0;			//darf nicht geandert werden, bei negativen zahlen dauert es sehr lange
	public static final int MAXVAL = 1;
	public static final int RANDDISTANCE = 5;
	public static final int RANDNETWORKDISTANCE = 30;
	
	//67 input nodes
	//4 hidden layer
	//25 hidden nodes per hidden layer
	//3 output nodes
	
	private NeuralNetwork<BackPropagation> neuralNetwork;//TODO might want to change Hopfield learning
	
	public static void main(String[] args){
		
		//TODO: learning rule eventuell anpassen
		System.out.println("main aufgerufen");
		OnitamaController onitamaCont= new OnitamaController();
		NetworkTry2 networkController = onitamaCont.getNetworkController();
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
				//network1 = createNewNeuralNetwork();
				//network2 = createNewNeuralNetwork();
			}
			else{
				throw fe;
			}
		}
		 
		int i = 0;
		while(true){
			System.out.println("Iteration: " + i++);
			networkController.startTraining(network1, network2);
			networkController.trainAgainstSensei(network1,network2);
		}
		
		
	}
	
	
	public NetworkTry2(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
		System.out.println(FILEPATHGIT1);
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
				learnOneIteration(network1, network2, newGameController, gameStateController, movementController);
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
		boolean illegalMovePlayer1 = false;
		boolean illegalMovePlayer2 = false;
		
		for(int i=0;i<MAXMOVES;i++){				
			GameState currentGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();				
			Player currentPlayer = currentGameState.getCurrentPlayer();
			List<Player> winningPlayers = onitamaController.getGameStateController().getWinningPlayers(currentGameState);
			if(!winningPlayers.isEmpty() || illegalMovePlayer1 || illegalMovePlayer2){				
				if(winningPlayers.contains(player1) || illegalMovePlayer2){
					network2.learn(dataSet1);				
				}else if(winningPlayers.contains(player2) || illegalMovePlayer1){//player2
					network1.learn(dataSet2);	
				}					
				break;
			}
			GameMove nextMove = null;	
			double [] input = createInputVector(currentGameState);
			if(currentPlayer.equals(player1)){//player1 ist dran
				nextMove = addComputedNextMoveToDataSet(network1, dataSet1, currentGameState, input,i);					
				if(!movementController.isValidGameMove(currentGameState, nextMove)){	
					//System.out.println("illegaler zug spieler 1");
					illegalMovePlayer1 = true;
					continue;
				}
			}else{//player2 ist dran
				nextMove = addComputedNextMoveToDataSet(network2, dataSet2, currentGameState, input,i);					
				if(!movementController.isValidGameMove(currentGameState, nextMove)){
					//System.out.println("illegaler zug spieler 2");
					illegalMovePlayer1 = true;
					continue;
				}
			}				
			GameState nextGameState = gameStateController.generateNextGameState(currentGameState, nextMove);
			List<GameState> gameStates = onitamaController.getOnitama().getCurrentGame().getGameStates();
			gameStates.add(nextGameState);
			onitamaController.getOnitama().getCurrentGame().setGameStates(gameStates);

		}
	}



	private GameMove addComputedNextMoveToDataSet(NeuralNetwork<BackPropagation> network, DataSet dataSet, GameState currentGameState, double[] input, int i) {
		GameMove nextMove;
		network.setInput(input);		
		network.calculate(); 
		double[] output = network.getOutput();
		output = adaptInputArray(output,currentGameState);
		nextMove = calculateNextMove(output, currentGameState,i);
		DataSetRow newRow = new DataSetRow(input,output); 
		dataSet.add(newRow);
		return nextMove;
	}
	
	public void trainAgainstSensei(NeuralNetwork<BackPropagation> network1,NeuralNetwork<BackPropagation> network2){
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
		GameStateController gameStateController, MovementController movementController, ZenMasterAIController sensei) {
		Game currentGame;
		for(int j=0;j<1;j++){
			if(j%50==0){
				System.out.println(j);
			}
			DataSet dataSet = new DataSet(INPUT_LAYER_SIZE,OUTPUT_LAYER_SIZE);
			newGameController.createDefaultGame();
			currentGame = onitamaController.getOnitama().getCurrentGame();
			currentGame.setMaxMoves(MAXMOVES);			
			Player player1 = currentGame.getPlayers().get(0);
			Player player2 = currentGame.getPlayers().get(1);
			boolean illegalMovePlayer1 = false;			
			for(int i=0;i<MAXMOVES;i++){				
				GameState currentGameState = onitamaController.getOnitama().getCurrentGame().getCurrentGameState();				
				Player currentPlayer = currentGameState.getCurrentPlayer();
				List<Player> winningPlayers = onitamaController.getGameStateController().getWinningPlayers(currentGameState);
				if(!winningPlayers.isEmpty() || illegalMovePlayer1){
					if(illegalMovePlayer1){
						System.out.println("illegaler zug wurde gemacht");
					}
					if(winningPlayers.contains(player2) || illegalMovePlayer1){//ai soll lernen
						network.learn(dataSet);	
					}					
					break;
				}
				GameMove nextMove = null;
				double [] input = createInputVector(currentGameState);				
				if(currentPlayer.equals(player1)){//player1 ist dran
					network.setInput(input);		
					network.calculate();
					double[] output = network.getOutput();
					output = adaptInputArray(output,currentGameState);		
					nextMove = calculateNextMove(output, currentGameState,i);			
					if(!movementController.isValidGameMove(currentGameState, nextMove)){	
						System.out.println("illegaler zug spieler 1");
						illegalMovePlayer1 = true;
						continue;
					}else{
						
					}
				}else{//player2 ist dran
					nextMove = sensei.getNextMove(currentGameState);
					double[] output = fillWithValues(currentGameState);
					DataSetRow newRow = new DataSetRow(input,output);
					dataSet.add(newRow);
					
				}
				MovementController moveCont = onitamaController.getMovementController();
				if(!moveCont.isValidGameMove(currentGameState, nextMove)) {
					System.out.println("ungueltiger zug");
					continue;
				}
				
				GameState nextGameState = gameStateController.generateNextGameState(currentGameState, nextMove);
				List<GameState> gameStates = onitamaController.getOnitama().getCurrentGame().getGameStates();
				gameStates.add(nextGameState);
				onitamaController.getOnitama().getCurrentGame().setGameStates(gameStates);				
			}
			
		}
	}
	
	public double[] fillWithValues(GameState gameState){		//TODO: funktioniert nicht
		double[] result = new double[OUTPUT_LAYER_SIZE];
		ZenMasterAIController zenMaster = onitamaController.getZenMasterAIController();
		MovementController movementController = onitamaController.getMovementController();
		GameStateController gameStateController = onitamaController.getGameStateController(); 
		for(int i=0;i<result.length-1;i++){
			GameMove nextMove = convertIntToMove(i, gameState);
			if(nextMove==null || !movementController.isValidGameMove(gameState, nextMove)){
				result[i] = 0;
			}
			else{
				GameState simulatedState = gameStateController.generateNextGameState(gameState, nextMove);
				simulatedState.setSimulated(true);
				double minVal = Integer.MAX_VALUE;
				for(GameMove simulatedMove: movementController.getPossibleMovements(simulatedState,simulatedState.getCurrentPlayer())){
					GameState nextSimulatedState = gameStateController.generateNextGameState(simulatedState, simulatedMove);
					nextSimulatedState.setSimulated(true);
					double actVal = zenMaster.getValueInTwoMoves(simulatedState);
					if(actVal<minVal){
						minVal = actVal;
					}
				}	
				minVal = minVal/Integer.MAX_VALUE;			
				minVal += 1;
				minVal = minVal/2;
				result[i] = minVal;
			}
			if(result[i]<0){
				System.out.println("value negativ: " + result[i]);
				result[i] = 0;
				
			}
		}		
		return result;
	}
	
	public double[] adaptInputArray(double[] input,GameState gameState){ //TODO: minimaler wert geaendert
		double[] copy = Arrays.copyOf(input, input.length);		
		AIController aiController = onitamaController.getSenseiAIController();
		for(int i=0;i<copy.length-1;i++) {
			GameMove actMove = convertIntToMove(i, gameState);
			if(actMove==null || !onitamaController.getMovementController().isValidGameMove(gameState, actMove)) {
				copy[i] = MINVAL;
			}
			else{
				if(aiController.canWin(gameState, actMove)){
					copy[i] = MAXVAL;
				}
				else if(aiController.canLoose(gameState, actMove)){
					copy[i] = MINVAL;
				}
			}
		}
		return copy;
	}
	
	public GameMove calculateNextMove(double[] output, GameState gameState, int j){  //output only contains valid moves		
		GameMove result = null;
		int winner = 0;
		int second = 0;
		double actBest = output[0];	
		for(int i=0;i<output.length-1;i++){
			if(output[i]>actBest){
				second = winner;
				actBest = output[i];
				winner = i;				
			}			
		}
		if(j%RANDDISTANCE==0){
			winner = second;
		}
		result = convertIntToMove(winner,gameState);
		return result;	
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
		GameToken master = playerController.getMasterToken(gameState, currentPlayer);
		List<GameToken> tokenList = playerController.getPlayerTokens(gameState, currentPlayer);
		tokenList.remove(master);
		List<GameCard> cardList = gameState.getCardDistribution().get(currentPlayer);
		GameMove result = null;
		GameToken resultToken = null;
		GameCard resultCard = null;
		Vector resultVector = null;
		if(index/8==0){
			resultToken = master;
		}
		else if(index/8<tokenList.size()){				//TODO nochmal rechnung ueberpruefen
			resultToken = tokenList.get((index/8)-1);
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
	
	public static NeuralNetwork<BackPropagation> createNewNeuralNetwork(){
		NeuralNetwork<BackPropagation> neuralNetwork = null;
		neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,INPUT_LAYER_SIZE,HIDDEN_LAYER_SIZE,HIDDEN_LAYER_SIZE, HIDDEN_LAYER_SIZE, HIDDEN_LAYER_SIZE, OUTPUT_LAYER_SIZE);
		BackPropagation learningRule = new BackPropagation();
		learningRule.setMaxIterations(1000);		
		neuralNetwork.setLearningRule(learningRule);
//		Layer inputLayer = new InputLayer(INPUT_LAYER_SIZE);
//		Layer outputLayer = new Layer();					
//				
////		for(int i = 0; i < INPUT_LAYER_SIZE; i++){//create inputLayer neurons
////			Neuron inputNeuron = new Neuron();
////			inputLayer.addNeuron(inputNeuron);
////			inputNeuron.setTransferFunction(new Sigmoid());
//////			Neuron connectionNeuron = new Neuron();
//////			ConnectionFactory.createConnection(connectionNeuron, inputNeuron);
//////			Connection connection = connectionNeuron.getOutConnections().get(0);
//////			connection.setWeight(new Weight(0));	
////			System.out.println(i);
////		}
//		neuralNetwork.addLayer(inputLayer);
//		neuralNetwork.setInputNeurons(inputLayer.getNeurons());
//				
//		Layer hiddenLayer;
//		for(int x = 0; x < HIDDEN_LAYER_COUNT; x++){
//			hiddenLayer = new Layer();
//			for(int y = 0; y < HIDDEN_LAYER_SIZE; y++){
//				Neuron neuron = new Neuron();
//				hiddenLayer.addNeuron(neuron);
//				neuron.setTransferFunction(new Sigmoid());
//			}
//			neuralNetwork.addLayer(hiddenLayer);
//		}
//				
//		for(int i = 0; i < OUTPUT_LAYER_SIZE; i++){//create outputLayer neurons
//			Neuron neuron = new Neuron();
//			outputLayer.addNeuron(neuron);
//			neuron.setTransferFunction(new Sigmoid());
//		}
//		neuralNetwork.addLayer(outputLayer);
//		neuralNetwork.setOutputNeurons(outputLayer.getNeurons());
//				
//		ConnectionFactory.fullConnect(inputLayer, neuralNetwork.getLayerAt(0));
//		for(int i = 0; i < (neuralNetwork.getLayersCount()-1); i++){
//			ConnectionFactory.fullConnect(neuralNetwork.getLayerAt(i), neuralNetwork.getLayerAt(i+1));
//		}
//		ConnectionFactory.fullConnect(neuralNetwork.getLayerAt(0), neuralNetwork.getLayerAt(neuralNetwork.getLayersCount()-1), false);//
//		
		
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
		while(index<48) {
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
