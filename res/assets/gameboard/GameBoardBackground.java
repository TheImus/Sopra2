package assets.gameboard;

import javafx.scene.image.Image;

public class GameBoardBackground {
	public static Image getBackground() {
		String fileName = "gameBoard.png";
		
		return new Image(GameBoardBackground.class.getResourceAsStream(fileName));
	}
}
