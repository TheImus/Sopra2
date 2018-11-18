package controller;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.GameBoard;
import model.GameCard;
import model.GameMove;
import model.GameState;
import model.GameToken;
import model.Player;
import model.Vector;

public class MovementController {

	private OnitamaController onitamaController;
	
	public MovementController(OnitamaController onitamaController){
		this.onitamaController = onitamaController;
	}

	/**
	 *  if the player is not null and still alive
	 *  then get the all GameTokens of the player,
	 *  and generate the all possible movement of all the GameTokens according to the 2 GameCard possessed by the player
	 *  and return the GameMove list.
	 *  if the player is not null and is defeated then return null.
	 *  if the player is null throw a NullPointException.
	 * @param player is a player 
	 * @return list of GameMove.
	 */
	public List<GameMove> getPossibleMovements(GameState gameState, Player player) {
		try {
			List<GameMove> possibleMovements=new ArrayList<>();
			PlayerController playerController=onitamaController.getPlayerController();
			if (playerController.isAlive(gameState, player))
			{
				List<GameToken> gameTokensOfPlayer=onitamaController.getPlayerController().getPlayerTokens(gameState, player);
				List<GameCard> gameCardsOfPlayer=gameState.getCardDistribution().get(player);
				for(GameToken gameToken : gameTokensOfPlayer)
				{
					for(GameCard gameCard : gameCardsOfPlayer)
					{
						 possibleMovements.addAll(getPossibleMovements(gameState, gameCard, gameToken));
					}
				}
			}
			return possibleMovements;
			
		} catch (NullPointerException e) {
			throw e;
		}
	}

	/**
	 * if the token is not killed,
	 * generate the all possible movement of this token according to the 2 GameCard possessed by the player,who the token is belong to, then return the GameMove list.
	 * if the token is killed, return null. 
	 * @param token a token,which is chosen by the player,and the player want to move it.
	 * @return a list of GameMove,which contain all the possible GameMove of the token
	 */
	public List<GameMove> getPossibleMovements(GameState gameState, GameToken token) {
		try {
			List<GameMove> possibleMovements= new ArrayList<>();
			if(onitamaController.getPlayerController().getPlayerTokens(gameState, token.getPlayer()).contains(token))
			{
				List<GameCard> gameCardOfPlayer=gameState.getCardDistribution().get(token.getPlayer());
				for(GameCard gameCard : gameCardOfPlayer )
				{
					possibleMovements.addAll(getPossibleMovements(gameState, gameCard, token));
				}
			}
			return possibleMovements;
		} catch (NullPointerException e) {
				throw e;
		}
		
	}

	/**
	 * according to the card, generate all the possible movement of every GameToken of the player,retrun it.
	 * @param card is a selected card of two card of the player.
	 * @return GameMove list
	 */
	public List<GameMove> getPossibleMovements(GameState gameState, GameCard card) {
		try {
			List<GameMove> possibleMovements=new ArrayList<>();
			Player playerOfCard=null;
		    Map<Player, List<GameCard>> cardDistribution=gameState.getCardDistribution();
		    List<Player> players = gameState.getPlayers();
		    List<GameCard> gameCardsOfPlayer=null;
		    for(Player player : players)
		    {
		    	gameCardsOfPlayer=cardDistribution.get(player);
		    	if(gameCardsOfPlayer.contains(card))
		    	{
		    		playerOfCard=player;
		    		break;
		    	}
		    }
		    if(playerOfCard != null)
		    {
		    	List<GameToken> gameTokens=onitamaController.getPlayerController().getPlayerTokens(gameState, playerOfCard);
			    for(GameToken gameToken :gameTokens)
			    {
			    	possibleMovements.addAll(getPossibleMovements(gameState, card, gameToken));
			    }
		    }
		    return possibleMovements;
		} catch (NullPointerException e) {
			throw e;
		}
		
	    
	}

	/**
	 * check whether the gameMove is valid or not
	 * @param gameMove
	 * @return if gameMove is valid, then return true;if gameMove isn't valid , then return false.
	 */
	public boolean isValidGameMove(GameState gameState, GameMove gameMove) {
		try {
			GameCard selectedCard=gameMove.getSelectedCard();
			GameToken movedToken=gameMove.getMovedToken();
			model.Vector destination=gameState.getGameBoard().getDestination(gameMove);
			if(!gameState.getCardDistribution().get(movedToken.getPlayer()).contains(selectedCard))
			{
				return false;
			}
			else if(!selectedCard.getPossibleMovements().contains(gameMove.getSelectedMove()))
			{
				return false;
			}
			else if(! gameState.getGameBoard().isPositionInBounds(destination))
			{
				return false;
			}
			else if(gameState.getGameBoard().getTokenAt(destination)!=null && movedToken.getPlayer().equals(gameState.getGameBoard().getTokenAt(destination).getPlayer()))
			{
				return false;
			}
			return true;
		} catch (NullPointerException e) {
			throw e;
		}
		
		
		
	}

	/**
	 * calculate the possible Movement of the the GameToken with the selected card
	 * @param gameCard is a selected card of player's two card.
	 * @param token is the selected card.
	 * @return list of GameMove.
	 */
	public List<GameMove> getPossibleMovements(GameState gameState, GameCard gameCard, GameToken token) {
		if(gameState == null || gameCard==null || token== null)
		{
			throw new NullPointerException();
		}
		List<GameMove> possibleMovements=new ArrayList<>();
		List<model.Vector> possibleMovementsOfGamecard=gameCard.getPossibleMovements();
		for(model.Vector movement : possibleMovementsOfGamecard)
		{
			GameMove newGameMove=new GameMove(token, gameCard, movement);
			if(isValidGameMove(gameState, newGameMove))
			{
				GameBoard gameBoard=gameState.getGameBoard();
				GameToken defeatedGameToken=gameBoard.getTokenAt(gameBoard.getDestination(newGameMove));
				if(defeatedGameToken!=null)
				{
					newGameMove.setDefeatedToken(defeatedGameToken);
				}
				possibleMovements.add(newGameMove);
			}
		}
		return possibleMovements;
	}

	public OnitamaController getOnitamaController() {
		return onitamaController;
	}

	public void setOnitamaController(OnitamaController onitamaController) {
		this.onitamaController = onitamaController;
	}

}
