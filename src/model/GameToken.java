package model;

import java.io.Serializable;

/**
 * 
 * @author Fabian Kemper
 *
 */
public class GameToken implements Serializable{
	
	private static final long serialVersionUID = 4452267857857961548L;
	// is this a master token?
	private boolean isMaster;
	// to which player belongs this token
	private Player player;
	
	
	/**
	 * Create a new game token
	 * @param isMaster set this token as master token
	 * @param player the player this token belongs to
	 */
	public GameToken(boolean isMaster, Player player) {
		if (player == null) {
			throw new NullPointerException();
		}
		
		this.isMaster = isMaster;
		this.player = player;
	}

	/**
	 * @return true if this is a master token
	 */
	public boolean isMaster() {
		return isMaster;
	}

	/**
	 * @param isMaster the isMaster to set
	 */
	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		if (player == null) {
			throw new NullPointerException();
		}
		this.player = player;
	}

}
