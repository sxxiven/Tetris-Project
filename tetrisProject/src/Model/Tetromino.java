package Model; 
/**
Steven Robles 
Assignment 1
CS 3331 Object Oriented
Instructer: Edger Padilla
Purpose: 
	The purpose of this file is to provide the calss for the tetromino piece in order to build a 
	funtoinal tetris game.  
*/

/** This class is will model a tetromino.
* @author epadilla2
* @param <U>
* @param <T>
*
*/
public abstract class Tetromino{

	protected int[][] coordinates = new int[4][2]; 
	protected String letter; 
	
	
	public String getLetter() {
		return letter; 
	}
	public int [][] getCoordinates() {
		return coordinates; 
	}
	public void setCoordinates(int [][] newCoordinates){
		if (newCoordinates == null)
			coordinates = null; 
		else
			for (int i = 0; i < 4; i++) 
			{
				for (int j = 0; j < 2; j++) 
				{
					coordinates[i][j] = newCoordinates[i][j]; 	
				}
			}
	}
	
	/**
	 * Rotates tetromino to the right.
	 * @param the coordinates of the piece you're rotating
	 * @return the coordinates of the cloned array
	 */
	public int[][]rotateRight(int[][] coord)
	{
		int[][] clone = new int[4][2]; //creates clone to check if the rotation is valid first
		for (int i = 0; i < 4; i++)
		{	 
			clone[i][0] = ((coord[i][1]+ (coord[1][0]-coord[1][1]))%20); 
			clone[i][1] = ((-coord[i][0] +(coord[1][1]+coord[1][0]))%10);
		}
		return clone;
	}
	/**
	 * Rotates tetromino to the left.
	 * @param the coordinates of the piece you're rotating
	 * @return the coordinates of the cloned array
	 */
	public int[][] rotateLeft(int [][] coord)
	{	
		int[][] clone = new int[4][2]; //creates clone to check if the rotation is valid first
		for (int i = 0; i < 4; i++)
		{	 
			clone[i][0] = ((-coord[i][1]+ (coord[1][0]+coord[1][1]))%20); 
			clone[i][1] = ((coord[i][0] +(coord[1][1]-coord[1][0]))%10);
		}
		return clone; 
	}
}
/**
 * 
 * class which contains information of the I tetromino
 *
 */
	class IPiece extends Tetromino {
		public IPiece() {
			for(int i = 0; i < 4; i++){
				coordinates[i][0] = 0; 
				coordinates[i][1] = i+3; 
			}
			letter = "I";
		}
	}
	/**
	 * 
	 * class which contains information of the L tetromino
	 *
	 */
	class LPiece extends Tetromino {
		public LPiece() {
			for(int i = 0; i < 3; i++)
			{
				coordinates[i][0]= 1; 
				coordinates[i][1]= i+3; 
			}
			coordinates[3][0] = 0; 
			coordinates[3][1] = 5;
			letter = "L";
		}
	}	
	/**
	 * 
	 * class which contains information of the J tetromino
	 *
	 */
	class JPiece extends Tetromino {
		public JPiece() {
			for(int i = 0; i < 3; i++)
			{
				coordinates[i][0]= 1; 
				coordinates[i][1]= i+3; 
			}
			coordinates[3][0] = 0; 
			coordinates[3][1] = 3;
			letter = "J";
		}
	}	
	/**
	 * 
	 * class which contains information of the O tetromino
	 *
	 */
	class OPiece extends Tetromino {
		public OPiece() {
			for(int i = 0; i < 2; i++)
			{
				coordinates[i][0]= i; 
				coordinates[i][1]= 4;
				coordinates[i+2][0]= i; 
				coordinates[i+2][1]= 5; 
			} 
			letter = "O";
		}
	}	
	/**
	 * 
	 * class which contains information of the S tetromino
	 *
	 */
	class SPiece extends Tetromino {
		public SPiece() {
			for(int i = 0; i < 2; i++)
			{
				coordinates[i][0]= i; 
				coordinates[i][1]= 6-i;
				coordinates[i+2][0] = i; 
				coordinates[i+2][1] = 5-i; 
			}
			letter = "S";
		}
	}	
	/**
	 * 
	 * class which contains information of the Z tetromino
	 *
	 */
	class ZPiece extends Tetromino {
		public ZPiece() {
			for(int i = 0; i < 2; i++)
			{
				coordinates[i][0]= i; 
				coordinates[i][1]= 4+i;
				coordinates[i+2][0] = i; 
				coordinates[i+2][1] = 5+i; 
			}
			letter = "Z"; 
		}
	}	
	/**
	 * 
	 * class which contains information of the T tetromino
	 *
	 */
	class TPiece extends Tetromino {
		public TPiece() {
			for(int i = 0; i < 3; i++)
			{
				this.coordinates[i][0]= 1; 
				this.coordinates[i][1]= i+4;
			}
			this.coordinates[3][0] = 0; 
			this.coordinates[3][1] = 5; 
			letter = "T";
		}
	}