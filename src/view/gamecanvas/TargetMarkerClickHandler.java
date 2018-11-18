package view.gamecanvas;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import model.GameMove;
import view.GameCanvasViewController;

public class TargetMarkerClickHandler implements EventHandler<MouseEvent> {
	GameCanvasViewController viewController;
	
	
	public TargetMarkerClickHandler(GameCanvasViewController viewController) {
		if (viewController == null) {
			throw new NullPointerException("View controller must be set.");
		}
		this.viewController = viewController;
	}

	@Override
	public void handle(MouseEvent event) {
		if (!(event.getSource() instanceof TargetMarker)) {
			System.out.println("TargetMarkerClickHandler.handle - wrong instance given for event.getSource().");
			return;
		}
		
		if (!viewController.getCanvasState().accecptInput()) {
			event.consume();
			System.out.println("Input not allowed");
			return; 
		}

		TargetMarker targetMarker = (TargetMarker) event.getSource();
		List<GameMove> moves = targetMarker.getGameMoves();
		
		if (moves.size() == 0) { throw new IllegalStateException("List of Game moves of the selected target marker is empty"); }
		
		if (moves.size() == 1) {
			// unique move
			viewController.invokeGameMove(moves.get(0));
		} else {
			// multiple moves
			viewController.getCardPicker().pickCards(moves);
		}
		event.consume();
	}
}
