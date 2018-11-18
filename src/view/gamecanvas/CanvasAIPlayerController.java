package view.gamecanvas;

import controller.AIController;
import javafx.application.Platform;
import model.GameMove;
import model.GameState;
import model.Player;
import view.GameCanvasViewController;

public class CanvasAIPlayerController {
	private GameCanvasViewController viewController;
	
	public CanvasAIPlayerController(GameCanvasViewController viewController) {
		this.viewController = viewController;
	}
	
	
	// TODO: Informationen für Julian ... 
	// https://www.concretepage.com/java/jdk-8/java-8-runnable-and-callable-lambda-example-with-argument
	
	/**
	 * Generates a move from the AI in the background
	 */
	public void doAIMove() {
		if (viewController.getCanvasState().isAiWorking()) {
			return;
		}
		
		viewController.getCanvasState().setAIWorking(true);
		Player currentPlayer = viewController.getCanvasState().getCurrentPlayer();
		AIController aiController = null;
		
		switch (currentPlayer.getPlayerType()) {
		case NOVICE: 
			aiController = viewController.getOnitamaController().getNoviceAIController();
			break;
		case SENSEI:
			aiController = viewController.getOnitamaController().getSenseiAIController();
			break;
		case ZEN_MASTER: 
			aiController = viewController.getOnitamaController().getZenMasterAIController();
			break;
		case BRAIN:
			aiController = viewController.getOnitamaController().getBrainAIController();
		default:
//			System.out.println("In doAIMove - currentPlayerType: " + currentPlayer.getPlayerType().name());
			break;
		}
		
		// fix this value
		final AIController finalAIController = aiController; 
		final GameState aiGameState = viewController.getCanvasState().getCurrentGameState();

		
		if (finalAIController != null) {
			// start a worker thread with AI calculations
			new Thread(() -> {
				GameMove aiMove = finalAIController.getNextMove(aiGameState);
				
				Platform.runLater(
					() -> {
						viewController.getCanvasState().setAIWorking(false);
						if (!viewController.getCanvasState().isGameQuitted()) {
							viewController.invokeGameMove(aiMove);
						}
					});
			}).start();
		} else {
			viewController.getCanvasState().setAIWorking(false);
			System.out.println("doAIMove Error: No AI Interface set");
		}

	}
	
	public void generateTip() {
		if (!viewController.getCanvasState().accecptInput() || viewController.getCanvasState().isAiWorking()) {
			return;
		}
		viewController.getCanvasState().setAIWorking(true);
		
		AIController aiController;
		if (viewController.getCanvasState().getCurrentGame().getPlayers().size() <= 1) {
			// get a zen master AI if there are two players
			aiController = viewController.getOnitamaController().getZenMasterAIController();
		} else {
			// get the novice AI if there are more than two players in game
			aiController = viewController.getOnitamaController().getNoviceAIController();
		}
		
		final AIController finalAIController = aiController;
		final GameState aiGamestate = viewController.getCanvasState().getCurrentGameState();
		
		if (finalAIController != null) {
			new Thread(() -> {
				GameMove aiMove = finalAIController.getNextMove(aiGamestate);
				
				Platform.runLater(
					() -> {
						viewController.getCanvasState().setAIWorking(false);
						if (!viewController.getCanvasState().isGameQuitted()) {
							viewController.getCanvasState().setHighlightedMove(aiMove);
						}
					}
				);
			}).start();
		} else {
			viewController.getCanvasState().setAIWorking(false);
			System.out.println("generateTip Error: No AI Interface set");
		}
		
	}
	

	public void doOnitamaClock() {
		
		new Thread(() -> {
			while(true) {
				Platform.runLater(
					() -> viewController.getRotatePane().setRotate(
							viewController.getRotatePane().getRotate() + 6)
				);
				
				try {
					//Thread.currentThread();
					Thread.sleep(1000);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
//		System.out.println("Started onitama clock");
	}
	
	
	
}
