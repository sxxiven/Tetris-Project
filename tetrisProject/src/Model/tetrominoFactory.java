package Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.*; 

/**
 * This files prduces the actual indevidual tetromino pieces from the abstract class and passses the object 
 * to conroller. 
 * @author Steven Robles
 * @author Adrew Ramos
 */


public class tetrominoFactory {
	//global object that holds information of the next piece and holding. 
	private Tetromino nextPiece; 
	private Tetromino holdPiece; 
	
	/**
	 * returns the object that contains the information of the next
	 * upcoming piece
	 * @return nectPiece
	 */
	public Tetromino getNextPiece() {
		return nextPiece; 
	}
	/**
	 * this function retuns the holding object
	 * @return holdpiece
	 */
	public Tetromino getHoldPiece() {
		return holdPiece; 
	}
	/**
	 * function called when generating a new next peice when 
	 * a new game is started
	 */
	public void setNewNextPiece() {
		nextPiece = generateTetrominoPiece(null); 
	}
	/**
	 * clears the hold box when a new game is selected
	 */
	public void resetHold() 
	{
		holdPiece=null; 
	}
	/**
	 * 
	 * generates random piece using the enum class
	 * @param type which is the type specified for piece (typically when passing in next piece onto the board)
	 * @return coordinates of piece you're generating
	 * 
	 */
	
	public Tetromino generateTetrominoPiece(String type) 
	{	
		if (type == null) {
			TetrominoEnum Val = tetrominoFactory.TetrominoEnum.getRandomTetromino();
			type = Val.toString(); 
		}
		if (type == "I")
		{
			return new IPiece(); 
		}
		else if (type == "L")
		{
			return new LPiece();
		}
		else if (type == "J")
		{
			return new JPiece();
		}
		else if (type == "O")
		{
			return new OPiece();
		}
		else if (type == "S")
		{
			return new SPiece();
		}
		else if (type == "Z")
		{
			return new ZPiece();
		}
		else if (type == "T")
		{
			return new TPiece();
		}
		return null; 
	}
	/**
	 * this functions passes the information into next piece by using the enum and randomly generates a new 
	 * piece for the next tetromino
	 * @param currentpiece is passed to set the new information into this abject according to next piece
	 */
	public Tetromino generateNextPiece(Tetromino currentPiece) {
		currentPiece =  generateTetrominoPiece(nextPiece.getLetter()); 
		nextPiece = generateTetrominoPiece(null);
		return currentPiece; 
	}
	/**
	 * This functions swaps the piece in hold (if any) with the current piece
	 * @param the current piece that will be replaced by the holding piece
	 * @return current piece information
	 */
	public Tetromino swapWithHoldPiece(Tetromino currentPiece) 
	{
		if(holdPiece!= null) //first checks that there is a piece in hold
		{
			String temp = currentPiece.getLetter(); 
			boolean inBounds = true; 
			int [][] boardCoord = currentPiece.getCoordinates(), holdCoord = holdPiece.getCoordinates();
			int xDelta = boardCoord[1][1]-holdCoord[1][1], yDelta = boardCoord[1][0]-holdCoord[1][0]; 
			for(int i = 0; i < 4; i++) 
			{
			  holdCoord[i][1] += xDelta; 
			  holdCoord[i][0] += yDelta;
			  if (holdCoord[i][1] >= 10 || holdCoord[i][1] < 0 || holdCoord[i][0] >=20|| holdCoord[i][0] < 0)
				  inBounds = false; 
			}
			if(inBounds) 
			{
				currentPiece = generateTetrominoPiece(holdPiece.getLetter());
				currentPiece.setCoordinates(holdCoord);
				holdPiece = generateTetrominoPiece(temp);	
			}
			else
				//must reset the coordinates in order to be porperly printed on the screen 
				holdPiece = generateTetrominoPiece(holdPiece.getLetter());
		}
		else 
		{
			holdPiece = generateTetrominoPiece(currentPiece.getLetter()); 
			currentPiece.setCoordinates(null);  
		}
		return currentPiece; 
	}

	/*
	 * Enumeration to list the different tetrominos.
	 * FILLER used when in multiplayer sending a line to the other player.
	 * @author epadilla2
	 *
	 */
	public enum TetrominoEnum 
	{
		/** Types of tetrominos, filler represents punishment lines added in multiplayer mode */
		
		I(0), J(1), L(2), O(3), S(4), Z(5), T(6), FILLER(7);
		/** Integer value of each tetromino*/
		private int value;
		/**  Hash for inverse lookup of a tetromino based on value*/
		private static final Map<Integer, TetrominoEnum> reverseLookup = new HashMap<Integer, TetrominoEnum>();
		
		static {
			for (TetrominoEnum tetromino : TetrominoEnum.values()) {
				reverseLookup.put(tetromino.getValue(), tetromino);
	        }
		}
		/**
		 * Constructor that sets the integer value of tetromino 
		 * @param value
		 */
		TetrominoEnum(int value)
		{
			this.value = value;
		}
		/**
		 * Return integer value of tetromino
		 * @return
		 */
		public int getValue()
		{
			return value;
		}
		/**
		 * Return TetrominoEnum depending on value
		 * @param value
		 * @return
		 */
		public static TetrominoEnum getEnumByValue(int value)
		{
			return reverseLookup.get(value);
		}
		/**
		 * Returns a random TetrominoEnum
		 * @return
		 */
		public static TetrominoEnum getRandomTetromino() {
           Random random = new Random();
           return values()[random.nextInt(values().length-1)];
       }
	}
}
