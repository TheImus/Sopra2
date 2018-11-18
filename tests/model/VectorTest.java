package model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Fabian Kemper
 *
 */

public class VectorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRotation() {
		Vector oneRight = new Vector(1, 0);
		Vector oneUp    = new Vector(0, -1);
		Vector oneDown  = new Vector(0, 1);
		Vector oneLeft  = new Vector(-1, 0);


		// yellow player

		Player redPlayer    = new Player("Earth Player", PlayerType.HUMAN, PlayerColor.EARTH);
		Player yellowPlayer = new Player("Air player", 	 PlayerType.HUMAN, PlayerColor.AIR);
		Player bluePlayer   = new Player("Fire player",  PlayerType.HUMAN, PlayerColor.FIRE);
		Player purplePlayer = new Player("Water player", PlayerType.HUMAN, PlayerColor.WATER);
		Vector rotatedUp    = oneRight.rotate(yellowPlayer);
		Vector rotatedLeft  = oneRight.rotate(bluePlayer);
		Vector rotatedDown  = oneRight.rotate(purplePlayer);
		
		System.out.println("Rotated 0   degrees (right): " + oneRight.getString());
		System.out.println("Rotated 90  degrees (up):    " + rotatedUp.getString());
		System.out.println("Rotated 180 degrees (left):  " + rotatedLeft.getString());
		System.out.println("Rotated 270 degrees (down):  " + rotatedDown.getString());
		assertEquals(oneRight, oneRight.rotate(redPlayer));
		assertEquals(oneUp,    oneRight.rotate(yellowPlayer));
		assertEquals(oneLeft,  oneRight.rotate(bluePlayer));
		assertEquals(oneDown,  oneRight.rotate(purplePlayer));
	}
	
	@Test
	public void testOutput(){
		
		Vector test = new Vector(3566,25);
		System.out.println(test.getString());
		System.out.println(Vector.letterToNumber("EGE"));
		Vector test2 = Vector.gameMoveToVector("A2");
		System.out.println(test2.getString());
	}

	@Test
	public void testNumberToLetterConversion() {
		assertEquals("A", Vector.numberToLetter(0));
		assertEquals("Z", Vector.numberToLetter(25));
		assertEquals("AA", Vector.numberToLetter(26));
		assertEquals("AZ", Vector.numberToLetter(26*1 + 25));
		assertEquals("ZZ", Vector.numberToLetter(26*26 + 25));
		assertEquals("AAA", Vector.numberToLetter(26*26 + 25 + 1));
		assertEquals("ZZZ", Vector.numberToLetter(26*26*26+26*26+25));
	}
}
