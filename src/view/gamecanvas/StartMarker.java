package view.gamecanvas;

import javafx.scene.image.ImageView;
import model.PlayerColor;

public class StartMarker extends ImageView {
	private PlayerColor color;
	
	public StartMarker(PlayerColor color) {
		this.color = color;
	}

	public PlayerColor getColor() {
		return color;
	}

	public void setColor(PlayerColor color) {
		this.color = color;
	}
}
