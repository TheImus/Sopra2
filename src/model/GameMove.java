package model;

import java.io.Serializable;

public class GameMove implements Serializable{
	
	private static final long serialVersionUID = 6015992863137018736L;
	private Vector selectedMove;
	private GameCard selectedCard;
	private GameToken defeatedToken;
	private GameToken movedToken;

	public GameMove(GameToken movedToken, GameCard selectedCard, Vector selectedMove) {
		if (movedToken == null || selectedCard == null || selectedMove == null) {
			throw new NullPointerException();
		}
		
		this.selectedMove = selectedMove;
		this.selectedCard = selectedCard;
		this.defeatedToken = null;
		this.movedToken = movedToken;
	}

	/**
	 * @return the selectedMove
	 */
	public Vector getSelectedMove() {
		return selectedMove;
	}

	/**
	 * @param selectedMove the selectedMove to set
	 */
	public void setSelectedMove(Vector selectedMove) {
		if (selectedMove == null) {
			throw new NullPointerException();
		}
		this.selectedMove = selectedMove;
	}

	/**
	 * @return the selectedCard
	 */
	public GameCard getSelectedCard() {
		return selectedCard;
	}

	/**
	 * @param selectedCard the selectedCard to set
	 */
	public void setSelectedCard(GameCard selectedCard) {
		if (selectedCard == null) {
			throw new NullPointerException();
		}
		this.selectedCard = selectedCard;
	}

	/**
	 * @return the defeatedToken
	 */
	public GameToken getDefeatedToken() {
		return defeatedToken;
	}

	/**
	 * @param defeatedToken the defeatedToken to set
	 */
	public void setDefeatedToken(GameToken defeatedToken) {
		this.defeatedToken = defeatedToken;
	}

	/**
	 * @return the movedToken
	 */
	public GameToken getMovedToken() {
		return movedToken;
	}

	/**
	 * @param movedToken the movedToken to set
	 */
	public void setMovedToken(GameToken movedToken) {
		if (movedToken == null) {
			throw new NullPointerException();
		}
		this.movedToken = movedToken;
	}
	
//	public boolean equals(GameMove gameMove) {
//		return gameMove.getSelectedCard().equals(selectedCard) 
//				&& gameMove.getMovedToken().equals(movedToken) 
//				&& gameMove.getSelectedMove().getX()==selectedMove.getX() 
//				&& gameMove.getSelectedMove().getY()==selectedMove.getY();
//	}

}
