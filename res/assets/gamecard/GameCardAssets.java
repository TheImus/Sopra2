package assets.gamecard;

import java.io.InputStream;

import javafx.scene.image.Image;
import model.GameCard;

public class GameCardAssets {

	public enum Resolution {
		LOW,
		HIGH
	}
	
	public static Image getGameCardImage(GameCard gameCard, Resolution resolution) {
		String fileName = gameCard.getCardName();
		switch (resolution) {
			case HIGH: fileName += "_L"; break;
			default: break;
		}
		fileName += ".png";
		InputStream inputStream = null;
		try {
			inputStream = GameCardAssets.class.getResourceAsStream(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (inputStream == null) {
			throw new NullPointerException("Could not find ressource for card " + fileName);
		}
		
		return new Image(inputStream);
	}
}
