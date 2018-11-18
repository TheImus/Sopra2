package view;

import model.Player;

/**
 * Represents a 2D Vector class
 * @author Fabian Kemper
 *
 */
public class Vector2D {

	private double x;
	private double y;

	public Vector2D addTo(Vector2D vector) {
		return new Vector2D(this.x + vector.x, this.y + vector.y);
	}

	/**
	 * Rotate the Vector depending on the player Color
	 * @param player 
	 * @return the rotated Vector
	 */
	public Vector2D rotate(Player player) {
		Double angleRadian = Math.toRadians(player.getPlayerColor().getAngle());
		
		// correct vectors, we flipped the y axis
		Vector2D thisFlipped   = new Vector2D(this.x, -this.y);
		Vector2D resultFlipped = rotateVectorCC(thisFlipped, new Vector2D(0, 0), angleRadian);
		return new Vector2D(resultFlipped.getX(), -resultFlipped.getY());
	}

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * Rotate the Vector counterClockwise and return a clone;
	 * This method calculates the mathematical correct solution
	 * @param vector point to rotate
	 * @param axis rotation vector
	 * @param theta rotation in radian
	 * @return
	 */
	public static Vector2D rotateVectorCC(Vector2D vector, Vector2D axis, double theta) {
	    double x = vector.getX(); 
	    double y = vector.getY(); // mathematically wrong y axis ... correct this!
	    double u = axis.getX();
	    double v = axis.getY();
	    
	    double xPrime = (x - u) * Math.cos(theta) - (y - v) * Math.sin(theta) + u;
	    double yPrime = (y - v) * Math.cos(theta) + (x - u) * Math.sin(theta) + v;
	    
	    return new Vector2D(xPrime, yPrime);
	}
	
	public String getString() {
		String result = "";
		result += "[" + this.x + ", " + this.y + "]";
		return result;
	}

	
	
	
	
}
