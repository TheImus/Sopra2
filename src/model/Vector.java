package model;

import java.io.Serializable;

/**
 * Represents a 2D Vector class
 * @author Fabian Kemper
 *
 */
public class Vector implements Serializable{

	private static final long serialVersionUID = 131544779154153714L;
	private int x;
	private int y;

	public Vector addTo(Vector vector) {
		return new Vector(this.x + vector.x, this.y + vector.y);
	}

	/**
	 * Rotate the Vector depending on the player Color
	 * @param player 
	 * @return the rotated Vector
	 */
	public Vector rotate(PlayerColor color) {
		switch(color.getAngle()){
			case 90: 
				return new Vector(this.y, -this.x);
			case 180:
				return new Vector(-this.x, -this.y);
			case 270:
				return new Vector(-this.y, this.x);//
			default://o or 360
				return new Vector(this.x, this.y);
				//break;
		}
	}
	
	public Vector rotate(Player player) {
		return rotate(player.getPlayerColor());
	}

	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public String getPrintableString() {
		String result = "";
		result += "[" + numberToLetter(this.x) + ", " + (this.y+1) + "]";
		return result;
	}
	
	public String getString(){
		String result = "";
		result += "[" + this.x + ", " + this.y + "]";
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	/**
	 * es funktioniert frgt mich aber nicht wie
	 * @param string
	 * @return place for board
	 */
	public static int letterToNumber(String string){
		int  number = 0;
		for(int i=0; i <string.length();i++){
			number = (int) (number +((int)(string.charAt(i)-64)) * Math.pow(26,string.length() - (i+1))) ;
		}
		
		return number-1;
	}
	
	/**
	 * Calculates a number to an upper case representation 1 => A, 2 => B, 26 => AA
	 * 
	 * Be careful, there are only 25 characters ... and NO Zero! 
	 * "A" means 0 in last place, but 1 in each other place
	 * 65 is ASCII A ... 39+26 = 65, but 39+25 + 1 (non zero) = 64
	 * @param number
	 * @return
	 */
	public static String numberToLetter(int number){
		String result = "";
		int base = 26;
		int asciiOffset = 65;
		do {
			int currentNumber = number % base;
			result =  Character.toString((char) (currentNumber+asciiOffset)) + result;
			number /= base;
			number--;
			//base = 25;
		} while(number >= 0);
		
		return result;
	}
	
	public static Vector gameMoveToVector(String string){
		int cut = 0;
		//finden der Stelle an der der String geteilt werden muss
		for(int i =0; i <string.length(); i++){
			if(string.charAt(i) < 65){
				cut = i;
				break;
			}
		}
		
		String x = string.substring(0, cut);
		String y = string.substring(cut);
		
		System.out.println(letterToNumber(x) + ":" + (Integer.parseInt(y)-1));
		
		return new Vector(letterToNumber(x), (Integer.parseInt(y)-1));
	}
	
	
}
