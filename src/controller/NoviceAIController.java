package controller;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import model.GameMove;
import model.GameState;
import model.Player;

/**
 * 
 * 
 */
public class NoviceAIController extends AIController {


	private GameState gameState;
	//private Tree future;
	
	public NoviceAIController(OnitamaController onitamaController){
		this.onitamaController = onitamaController;
	}

	@Override
	public GameMove getNextMove(GameState gameStateNEVERUSE) {//TODO implement this
		gameState = new GameState(gameStateNEVERUSE);
		gameState.setSimulated(true);
		self = gameState.getCurrentPlayer();
		List<GameMove> possibleMoves = getAllMoves(gameState);
		if(possibleMoves.isEmpty()) throw new IllegalStateException("we can't even move -> that should NEVER happen, because we should have already lost at this point!");
		GameMove allElseFailsMove = possibleMoves.get(0);//this works, because the list is not empty
		for (Iterator<GameMove> iterator = possibleMoves.listIterator(); iterator.hasNext(); ) {//for each entry we do the following
			GameMove currentMove = iterator.next();
		    if 		(canWin(gameState, currentMove)) {//if we can win, we win
		    	return currentMove;
		    }else if(canLoose(gameState,currentMove)){//we dont want to loose, so we don't use the loosingMoves
		    	iterator.remove();
		    //}else if(canTakeMaster(gameState,currentMove)){
		    	//return currentMove;
		    }
		}
	    if(possibleMoves.isEmpty()) return allElseFailsMove; // we only got loosing moves, so we choose a random one (the first in the list)
	    
	    int randomPosition = ThreadLocalRandom.current().nextInt(0, possibleMoves.size() );
	    
	    return possibleMoves.get(randomPosition);//return a random move
	}
	
	
	

	
	
	

}
