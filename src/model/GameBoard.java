package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a game board of a game state
 * 
 * @author Fabian Kemper
 */

public class GameBoard implements Serializable{
	private static final long serialVersionUID = -5008085857202091044L;
	// size of the board, the board is squared
	private int size;
	// the actual board
	private GameToken board[][];
	
	private boolean simulated;
	
	/**
	 * Create a new empty game board of the given size
	 * @param size
	 */
	public GameBoard(int size, boolean simulated) {
		if (size < 1) {
			throw new IndexOutOfBoundsException();
		}
		this.size = size;
		this.board = new GameToken[size][size];
		this.setSimulated(simulated);
	}
	
	/**
	 * Copy a given game board with all references to the tokens
	 * 
	 * @param gameBoard
	 */
	public GameBoard(GameBoard gameBoard) {
		this.size = gameBoard.getSize();
		this.board = Arrays.stream(gameBoard.board).
				map(row -> row.clone()).toArray(clone -> gameBoard.board.clone());
		this.simulated = gameBoard.simulated;
		/*
		 * i think the version below is a bit slower, but untested
		this.board = new GameToken[this.size][this.size];
		for (int row = 0; row < this.size; row++) {
			System.arraycopy(gameBoard.board[row], 0, this.board[row], 0, this.size);
		}
		*/
	}
	
	/**
	 * Find a token in the game board
	 * @param gameToken token to look for
	 * @return vector(row, column) if token is found, null if the token is not in game board
	 */
	public Vector getPositionOf(GameToken gameToken) {
		if (gameToken == null) {
			throw new NullPointerException();
		}
		
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				if (this.board[row][column] == gameToken) {
					return new Vector(column, row);
				}
			}
		}
		
		return null;
	}

	/**
	 * Return the GameToken at a given position 
	 * @param position position of the token
	 * @return gameToken of target position, null if there is no token
	 * @throws NullPointerException if vector is not given
	 * @throws IndexOutOfBounds if given a position which is not in the board size
	 */
	public GameToken getTokenAt(Vector position) {
		if (isPositionInBounds(position)) {
			return this.board[position.getY()][position.getX()];
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Checks if a given position of a token is in the bounds of the game board
	 * @param vector the position to check
	 * @return true, if position is inside the game board, false if not
	 * @throws NullPointerException if vector is null
	 */
	public boolean isPositionInBounds(Vector vector) {
		if (vector == null) {
			throw new NullPointerException();
		} else {
			return vector.getX() < size && vector.getY() < size && vector.getX() >=0 && vector.getY() >= 0;
		}
	}

	/**
	 * Removes a token at a given position
	 * @param position 
	 * @return the removed token, null if there was no token
	 * @throws NullPointerException if no vector is given
	 * @throws IndexOutOfBoundsException if position is outside the game board
	 */
	public GameToken remove(Vector position) {
		if (!isPositionInBounds(position)) {
			throw new IndexOutOfBoundsException();
		}
		if(position == null) throw new NullPointerException("GameBoard.remove -> argument is null");
		
		GameToken token = this.getTokenAt(position); // this checks bounds!
		this.board[position.getY()][position.getX()] = null; // set no token
		return token;
	}

	/**
	 * 
	 * @param gameToken
	 * @param position
	 * @return
	 * @throws NullPointerException if no vector is given
	 * @throws IndexOutOfBoundsException if position is outside the game board
	 */
	// TODO: Changed signature of this method! (from void to GameToken)
	public GameToken add(GameToken gameToken, Vector position) {
		GameToken removed = this.remove(position);
		this.board[position.getY()][position.getX()] = gameToken;
		return removed;
	}
	
	/**
	 * Moves a token to a destination
	 * @param token token to move
	 * @param shift amount to shift (in board coordinates, keep this in mind, you have to rotate by your own!)
	 * @return token on destination
	 * @throws NullPointerException if no vector is given
	 * @throws IndexOutOfBoundsException if position is outside the game board
	 */
	public GameToken moveTo(GameToken token, Vector shift) {
		Vector startPosition = this.getPositionOf(token);
		Vector destination   = startPosition.addTo(shift);
		GameToken removed = this.add(this.remove(startPosition), destination);
		return removed;
	}

	/**
	 * Returns the actual game Board, be careful, this is the raw datatype
	 * 
	 * @return
	 */
	public GameToken[][] getBoard() {
		return this.board;
	}

	/** 
	 * You should never set the game board directly ... but if you wish so
	 * @param board new game board
	 * @throws NullPointerException if no board or a row not specified
	 * @throws IndexOutOfBoundsException if columns are not as long as the rows
	 */
	public void setBoard(GameToken[][] board) {
		if (board == null) {
			throw new NullPointerException();
		}
		
		// get length of row
		int size = board.length;
		// check length of all columns
		for (int row = 0; row < size; row++) {
			if (board[row] == null) {
				throw new NullPointerException();
			}
			// does the column length match the row length?
			if (board[row].length != size) {
				throw new IndexOutOfBoundsException();
			}
		}
		// all checks passed
		this.size  = size;
		this.board = board;
	}
	
	/**
	 * This gets the destination of a token, keep in mind 
	 * that you have to move the token in the new game state 
	 * not  in this game state, otherwise the game state is undefined
	 * @param gameMove a game move of the old board
	 * @return
	 */
	public Vector getDestination(GameMove gameMove) {
		if (gameMove == null) {
			throw new NullPointerException("GameMove must not be null.");
		}
		if (gameMove.getMovedToken() == null) {
			throw new NullPointerException("Moved token can not be null.");
		}
		// fetch starting position of token, the game board must hold the old state
		Vector startPosition = this.getPositionOf(gameMove.getMovedToken());
		if (startPosition == null) {
			throw new NullPointerException("Token was not found on board.");
		}
		
		if (gameMove.getSelectedMove()== null || gameMove.getMovedToken().getPlayer() == null) {
			throw new NullPointerException("Selected move is null or token has no player");
		}
		
		// rotate the selected movePossibility of the card
		Vector shift = gameMove.getSelectedMove().rotate(gameMove.getMovedToken().getPlayer());
		
		return startPosition.addTo(shift);
	}
	
	/**
	 * Returns the size of this board
	 * @return
	 */
	public int getSize() {
		return this.size;
	}
	
	
	/**
	 * Returns all tokens of this game board
	 * @return List of game tokens
	 */
	public List<GameToken> getAllTokens() {
		List<GameToken> result = new ArrayList<GameToken>();
		for (int row = 0; row < board.length; row++) {
			for (int column = 0; column < board[row].length; column++) {
				GameToken token = this.board[row][column];
				if (token != null) {
					result.add(token);
				}
			}
		}
		return result;
	}
	
	/**
	 * Get a String to print in the console
	 * @return
	 */
	public String getString() {			
		String result = "";
		for (int row = 0; row < this.size; row ++) {
			String line = "";
			for (int column = 0; column < this.size; column++) {
				line += printToken(this.getTokenAt(new Vector(column, row))) + " ";
			}
			
			result += line + System.lineSeparator();
		}
		return result;
	}
	
	/**
	 * Helper Method, prints a single token for the console
	 * @param token 
	 * @return starting character of player color, uppercase if master, else lowercase
	 */
	private static String printToken(GameToken token) {
		final String ANSI_EARTH	= "e";
		final String ANSI_AIR  	= "a";
		final String ANSI_FIRE	= "f";
		final String ANSI_WATER	= "w";
		
		String result = "";
		
		if (token == null) {
			result += "-";
		} else {
			switch(token.getPlayer().getPlayerColor()) {
				case EARTH: result += ANSI_EARTH; break;
				case AIR: 	result += ANSI_AIR; break;
				case FIRE:	result += ANSI_FIRE; break;
				case WATER:	result += ANSI_WATER; break; 
			}
			if (token.isMaster()) {
				result = result.toUpperCase();
			}
		}
		
		return result;
	}

	public boolean isSimulated() {
		return simulated;
	}

	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
	}

}
