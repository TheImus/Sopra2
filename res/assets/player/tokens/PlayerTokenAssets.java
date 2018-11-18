package assets.player.tokens;

import java.io.InputStream;

import javafx.scene.image.Image;
import model.GameToken;
import model.PlayerColor;

public class PlayerTokenAssets {

	
	/**
	 * Placeholder image for testing
	 * @return
	 */
	public static Image getPlaceholderMaster() {
		return new Image(PlayerTokenAssets.class.getResourceAsStream("placeholder-master.png"));
	}
	
	public static Image getPlaceholderNovice() {
		return new Image(PlayerTokenAssets.class.getResourceAsStream("placeholder-novice.png"));
	}
	
	
	
	public static Image getGameTokenImage(GameToken token, PlayerColor angle) {
		if (token == null || token.getPlayer() == null) {
			throw new NullPointerException();
		}
		
		PlayerColor color = token.getPlayer().getPlayerColor();
		
		String resName = "";
		if (token.isMaster()) {
			resName = "master-" + getRessourceSuffix(color, angle) + ".png";
		} else {
			resName = "novice-" + getRessourceSuffix(color, angle) + ".png";
		}
		
		return getRessourceFromString(resName);
	}
	
	
	/**
	 * Generates suffix for file
	 * @param color
	 * @param angle
	 * @return
	 */
	private static String getRessourceSuffix(PlayerColor color, PlayerColor angle) {
		String resName = "";
		switch (color) {
			case EARTH: resName += "earth"; break;
			case AIR:   resName += "air"; break;
			case FIRE:  resName += "fire"; break;
			case WATER: resName += "water"; break;
		}
	
		resName += "-";
		resName += "front";
		
		return resName;
	}
	
	
	/**
	 * Returns one image from source
	 * @param ressource
	 * @return
	 */
	private static Image getRessourceFromString(String ressource) {
		InputStream inputStream = PlayerTokenAssets.class.getResourceAsStream(ressource);
		if (inputStream == null) {
			throw new NullPointerException("Ressource image " + ressource + " not found");
		}
		return new Image(PlayerTokenAssets.class.getResourceAsStream(ressource));
	}
}

