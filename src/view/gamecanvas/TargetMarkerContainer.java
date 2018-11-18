package view.gamecanvas;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.layout.StackPane;
import model.GameMove;
import model.Player;
import model.Vector;

public class TargetMarkerContainer extends StackPane {
	private BoardContainer gameCanvasBoard;
	
	private Map<Vector, TargetMarker> targetMarkers;
	private Vector highlightedCell = null;

	private TargetMarkerClickHandler targetMarkerClickHandler;
	
	
	public TargetMarkerContainer(BoardContainer gameCanvasBoard) {
		this.targetMarkers = new HashMap<>();
		this.gameCanvasBoard = gameCanvasBoard;
		this.targetMarkerClickHandler = new TargetMarkerClickHandler(gameCanvasBoard.getViewController());
	}


	public BoardContainer getGameCanvasBoard() {
		return gameCanvasBoard;
	}


	public void setGameCanvasBoard(BoardContainer gameCanvasBoard) {
		this.gameCanvasBoard = gameCanvasBoard;
	}

	/**
	 * Redraw the complete pane
	 */
	public void refresh() {
		this.getChildren().clear();
		
		for (Map.Entry<Vector, TargetMarker> entry : targetMarkers.entrySet()) {
			// calculate position of target markers
			positionTargetMarker(entry.getKey(), entry.getValue());
			entry.getValue().setHighlighted(entry.getKey() == highlightedCell);
			this.getChildren().add(entry.getValue());
		}
	}
	
	
	/**
	 * Sets a position for the given target marker
	 * @param position
	 * @param targetMarker
	 */
	protected void positionTargetMarker(Vector position, TargetMarker targetMarker) {
		targetMarker.setCellSize(gameCanvasBoard.getCellSize());
		targetMarker.setPosition(position);
	}


	public Map<Vector, TargetMarker> getTargetMarkers() {
		return targetMarkers;
	}


	public void setTargetMarkers(Map<Vector, TargetMarker> targetMarkers) {
		this.targetMarkers = targetMarkers;
		this.getChildren().clear();
		
		GameMove highlightedMove = gameCanvasBoard.getViewController().getCanvasState().getHighlightedMove();
		CanvasToken canvasToken = gameCanvasBoard.getViewController().getCanvasState().getSelectedToken();
		Vector moveTarget = null;
		if (highlightedMove != null && canvasToken != null) {
			if (highlightedMove.getMovedToken().equals(canvasToken.getToken())) {
				Vector position = canvasToken.getPosition();
				Player player = highlightedMove.getMovedToken().getPlayer();
				moveTarget = position.addTo(highlightedMove.getSelectedMove().rotate(player));
			}
		}
			
		for (Map.Entry<Vector, TargetMarker> entry : targetMarkers.entrySet()) {
			positionTargetMarker(entry.getKey(), entry.getValue());
			entry.getValue().setHighlighted(entry.getKey().equals(moveTarget));
			//entry.getValue().setHighlighted(true);
		}
		this.getChildren().addAll(targetMarkers.values());
	}

	
	public void animateTargetSelected(Vector targetPosition) {
		for (Map.Entry<Vector, TargetMarker> entry : targetMarkers.entrySet()) {
			entry.getValue().fadeOutAnimation(entry.getKey().equals(targetPosition));
		}
	}


	public TargetMarkerClickHandler getTargetMarkerClickHandler() {
		return this.targetMarkerClickHandler;
	}


	public Vector getHighlightedCell() {
		return highlightedCell;
	}


	public void setHighlightedCell(Vector highlightedCell) {
		this.highlightedCell = highlightedCell;
		refresh();
	}
	
	
	
}
