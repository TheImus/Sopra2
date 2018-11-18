package assets.player.icons;

import javafx.scene.image.Image;
import model.PlayerColor;

/**
 * Class to import the player icons
 * @author Fabian Kemper
 *
 */
public class PlayerColorAssets {
	
	public static Image getPlayerIcon(PlayerColor playerColor) {
		String fileName = "";
		switch (playerColor) {
			case EARTH: fileName = "earth.png"; break;
			case AIR: 	fileName = "air.png"; 	break;
			case FIRE: 	fileName = "fire.png"; 	break;
			case WATER: fileName = "water.png"; break;
		}
		
		return new Image(PlayerColorAssets.class.getResourceAsStream(fileName));
	}
	
}
