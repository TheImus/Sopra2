package model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Vector;

/*
 * @author Fabian Kemper
 * The origin is in the middle of the card (0, 0) 
 * The x axis grows to the right
 * The y axis grows down
 */

public enum GameCard {
	OX       (PlayerColor.FIRE,  Arrays.asList(new Vector( 0, -1), new Vector( 1,  0), new Vector( 0,  1))),
	GOOSE    (PlayerColor.FIRE,  Arrays.asList(new Vector(-1, -1), new Vector(-1,  0), new Vector( 1,  0), new Vector( 1, 1))),
	EEL      (PlayerColor.FIRE,  Arrays.asList(new Vector(-1, -1), new Vector( 1,  0), new Vector(-1,  1))),
	CRANE    (PlayerColor.FIRE,  Arrays.asList(new Vector( 0, -1), new Vector(-1,  1), new Vector( 1,  1))),
	CRAB     (PlayerColor.FIRE,  Arrays.asList(new Vector( 0, -1), new Vector(-2,  0), new Vector( 2,  0))),
	MONKEY   (PlayerColor.FIRE,  Arrays.asList(new Vector(-1, -1), new Vector( 1, -1), new Vector(-1,  1), new Vector( 1 , 1))),
	RABBIT   (PlayerColor.FIRE,  Arrays.asList(new Vector( 1, -1), new Vector( 2,  0), new Vector(-1,  1))),
	TIGER    (PlayerColor.FIRE,  Arrays.asList(new Vector( 0, -2), new Vector( 0,  1))),
	MANTIS   (PlayerColor.EARTH, Arrays.asList(new Vector(-1, -1), new Vector( 1, -1), new Vector( 0,  1))),
	COBRA    (PlayerColor.EARTH, Arrays.asList(new Vector( 1, -1), new Vector(-1,  0), new Vector( 1,  1))),
	ROOSTER  (PlayerColor.EARTH, Arrays.asList(new Vector( 1, -1), new Vector(-1,  0), new Vector( 1,  0), new Vector(-1 , 1))),
	ELEPHANT (PlayerColor.EARTH, Arrays.asList(new Vector(-1, -1), new Vector( 1, -1), new Vector(-1,  0), new Vector( 1 , 0))),
	FROG     (PlayerColor.EARTH, Arrays.asList(new Vector(-1, -1), new Vector(-2,  0), new Vector( 1,  1))),
	HORSE    (PlayerColor.EARTH, Arrays.asList(new Vector( 0, -1), new Vector(-1,  0), new Vector( 0,  1))),
	BOAR     (PlayerColor.EARTH, Arrays.asList(new Vector( 0, -1), new Vector(-1,  0), new Vector( 1,  0))),
	DRAGON   (PlayerColor.EARTH, Arrays.asList(new Vector(-2, -1), new Vector( 2, -1), new Vector(-1,  1), new Vector( 1,  1)));
	
	// card settings
	private static final int MOVE_FIELD_SIZE = 5;
	
	private final PlayerColor color;
	private final List<Vector> possibleMovements;
	
	public final static Map<String, GameCard> FROM_STRING = new HashMap<String, GameCard>(){
		private static final long serialVersionUID = 4973007936741252502L;

		{
			put("Ochse", GameCard.OX);
			put("Gans", GameCard.GOOSE);
			put("Aal", GameCard.EEL);
			put("Kranich", GameCard.CRANE);
			put("Krabbe", GameCard.CRAB);
			put("Affe", GameCard.MONKEY);
			put("Hase", GameCard.RABBIT);
			put("Tiger", GameCard.TIGER);
			put("Gottesanbeterin", GameCard.MANTIS);
			put("Kobra", GameCard.COBRA);
			put("Hahn", GameCard.ROOSTER);
			put("Elefant", GameCard.ELEPHANT);
			put("Frosch", GameCard.FROG);
			put("Pferd", GameCard.HORSE);
			put("Wildschwein", GameCard.BOAR);
			put("Drache", GameCard.DRAGON);
		}
	};
	
	public final static Map<GameCard, String> TO_STRING = new HashMap<GameCard, String>(){
		private static final long serialVersionUID = -1151354171753557216L;

		{
			put(GameCard.OX, "Ochse");
			put(GameCard.GOOSE, "Gans");
			put(GameCard.EEL, "Aal");
			put(GameCard.CRANE, "Kranich");
			put(GameCard.CRAB, "Krabbe");
			put(GameCard.MONKEY, "Affe");
			put(GameCard.RABBIT, "Hase");
			put(GameCard.TIGER, "Tiger");
			put(GameCard.MANTIS, "Gottesanbeterin");
			put(GameCard.COBRA, "Kobra");
			put(GameCard.ROOSTER, "Hahn");
			put(GameCard.ELEPHANT, "Elefant");
			put(GameCard.FROG, "Frosch");
			put(GameCard.HORSE, "Pferd");
			put(GameCard.BOAR, "Wildschwein");
			put(GameCard.DRAGON, "Drache");
		}
	};
	
	private GameCard(PlayerColor color, List<Vector> moves) {
		this.color = color;
		this.possibleMovements = moves;
	}

	public PlayerColor getColor() {
		return color;
	}

	public List<Vector> getPossibleMovements() {
		return possibleMovements;
	}
	
	/**
	 * Print an ASCII representation of the card
	 * x is the starting point an 
	 * o is a point where this card can move to
	 * @return
	 */
	public String toASCII() {
		String result = this.getCardName() + System.lineSeparator();
		
		int reachable[][] = new int[MOVE_FIELD_SIZE][MOVE_FIELD_SIZE];
		reachable[MOVE_FIELD_SIZE/2][MOVE_FIELD_SIZE/2] = 2; // Two is for starting point
		
		for (Vector move : this.getPossibleMovements()) {
			reachable[move.getY()+(MOVE_FIELD_SIZE/2)][move.getX()+(MOVE_FIELD_SIZE/2)] = 1;
		}
		
		for (int row = 0; row < MOVE_FIELD_SIZE; row++) {
			String line = "";
			for (int column = 0; column < MOVE_FIELD_SIZE; column++) {
				if (reachable[row][column] == 1) {
					line += " o";
				} else if (reachable[row][column] == 2){
					line += " x";
				} else {
					line += " -";
				}
			}
			
			result += line + System.lineSeparator();
		}
		
		return result;
	}
	
	/**
	 * Prints the name of the this card 
	 * @return
	 */
	public String getCardName() {
		return this.name().toLowerCase();
	}
}

