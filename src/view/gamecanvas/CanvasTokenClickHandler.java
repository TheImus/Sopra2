package view.gamecanvas;

import java.util.List;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import model.GameMove;
import model.GameState;
import model.GameToken;
import model.Player;
import model.PlayerType;
import model.Vector;
import view.GameCanvasViewController;

public class CanvasTokenClickHandler implements EventHandler<MouseEvent> {
	// reference to the main window
	private GameCanvasViewController viewController;
	
	public CanvasTokenClickHandler(GameCanvasViewController viewController) {
		this.viewController = viewController;
	}
	

	@Override
	public void handle(MouseEvent event) {
		if (!(event.getSource() instanceof CanvasToken)) {
			System.out.println("TokenClickHandler.handle - wrong instance given for event.getSource().");
			return;
		}
		
		if (!viewController.getCanvasState().accecptInput()) {
			event.consume();
			return; // do nothing!
		}
		
		CanvasToken canvasToken = (CanvasToken) event.getSource();
		
		if (viewController.getCanvasState().getCurrentGameState() == null) { throw new NullPointerException("No current game state"); }
		// check if the token clicked is from active player
		Player currentPlayer = viewController.getCanvasState().getCurrentPlayer();
		if (canvasToken.getToken().getPlayer().equals(currentPlayer) && currentPlayer.getPlayerType().equals(PlayerType.HUMAN)) {
			this.selectCanvasToken(canvasToken);
		}
		
		event.consume();
	}
	
	
	/**
	 * Wrong position for this function?
	 * 
	 * @return
	 */
	// TODO: Put this into gameBoard or targetMarker container
	public void selectCanvasToken(CanvasToken canvasToken) {
		if ((canvasToken == null) || (canvasToken == viewController.getCanvasState().getSelectedToken()) ) {
			viewController.getCanvasState().setSelectedToken(null);
			return;
		}
		
		viewController.getCanvasState().setSelectedToken(canvasToken);
		
		TargetMarkerContainer targetMarkerContainer = getViewController().getGameBoardContainer().getTargetMarkerContainer();
		GameState gameState = viewController.getCanvasState().getCurrentGameState();
		GameToken token = canvasToken.getToken();
		
		// get position of game token
		Vector position = gameState.getGameBoard().getPositionOf(token);
		if (position == null) { throw new NullPointerException("Error: Gameboard can not find token!");	}
		
		// fetch possible moves for the selected token
		List<GameMove> possibleMoves = viewController.getOnitamaController().getMovementController().getPossibleMovements(gameState, token);
		Map<Vector, TargetMarker> targetMarkers = targetMarkerContainer.getTargetMarkers();
		targetMarkers.clear(); // clear old targets
		
		for (GameMove move : possibleMoves) {			
			// test draw an icon
			Player player = move.getMovedToken().getPlayer();
			// rotated target cell of the gameMove
			Vector moveTarget = position.addTo(move.getSelectedMove().rotate(player));
			
			TargetMarker targetMarker;
			boolean newTargetMarker = false; // if new one, start animation, else not
			// no target marker at position
			if (targetMarkers.containsKey(moveTarget)) {
				targetMarker = targetMarkers.get(moveTarget);
			} else {
				TargetMarkerClickHandler clickHandler = viewController.getGameBoardContainer().getTargetMarkerContainer().getTargetMarkerClickHandler();
				targetMarker = new TargetMarker(token.getPlayer().getPlayerColor());
				
				targetMarker.setClickHandler(clickHandler);
				newTargetMarker = true;
			}
			// IMPORTANT: The order of the following commands is critical! Do not reorder them!
			targetMarker.getGameMoves().add(move);
			targetMarker.setIsEliminatingMove(move.getDefeatedToken() != null);
			if (newTargetMarker) {
				targetMarker.fadeInAnimation();				
			}
			targetMarkers.put(moveTarget, targetMarker);
		}
		targetMarkerContainer.setTargetMarkers(targetMarkers);
		
		
		// do not call refresh here - refresh kills the animation
		//targetMarkerContainer.refresh();
	}
	
	
	/**
	 * never call this directly ... 
	 * this is only for view
	 */
	protected void unselectCanvasToken() {
		// TODO: Make this better!
		if (this.getViewController().getGameBoardContainer() == null) { return; } // fix error on init
		TargetMarkerContainer targetMarkerContainer = this.getViewController().getGameBoardContainer().getTargetMarkerContainer();
		targetMarkerContainer.getChildren().clear();
		targetMarkerContainer.getTargetMarkers().clear();
		targetMarkerContainer.refresh();
	}


	public GameCanvasViewController getViewController() {
		return viewController;
	}


	public void setViewController(GameCanvasViewController viewController) {
		this.viewController = viewController;
	}

}
