package controller;

import model.GameState;
import model.Player;

/**
 * This interface is implemented by the GameCanvasViewController 
 * 
 * Everytime a game state is changed in the model one of this refresh methods should be called,
 * so the GUI can show the new state
 * 
 * @author Fabian Kemper
 *
 */
public interface NewGameStateAUI {
	
	// Refreshes a game state without doing an animation in the gameCanvas
	// this forces the gui to re-read the current game state of the current game
	public void refreshGameState();
	
	// Refreshes a game state with an animation
	public void animateNewGameState(GameState oldGameState, GameState newGameState);
	
	// Player lost
	public void playerLost(Player player);
}
