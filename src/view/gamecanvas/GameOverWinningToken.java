package view.gamecanvas;

import assets.player.tokens.PlayerTokenAssets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.GameToken;
import model.Player;
import model.PlayerColor;
import view.Vector2D;

public class GameOverWinningToken extends ImageView {
	private Vector2D size;
	private Vector2D position = new Vector2D(0, 0);
	
	
	public GameOverWinningToken(Player player, Vector2D size) {
		setSize(size);
		setTokenImage(player);
	}
	
	private void setTokenImage(Player player) {
		GameToken token = new GameToken(true, player);
		Image image = PlayerTokenAssets.getGameTokenImage(token, PlayerColor.EARTH);
		this.setImage(image);
	}

	public Vector2D getSize() {
		return size;
	}

	public void setSize(Vector2D size) {
		this.size = size;
		this.setPosition(this.getPosition());
		this.setFitWidth(size.getX());
		this.setFitHeight(size.getY());
	}
	
	public void setPosition(Vector2D position) {
		Vector2D offset = new Vector2D(-this.getSize().getX() / 2, -this.getSize().getY()/2);
		this.setTranslateX(position.getX() + offset.getX());
		this.setTranslateY(position.getY() + offset.getY());
	}

	public Vector2D getPosition() {
		return position;
	}
	
}
