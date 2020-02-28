package Model;
import java.awt.Color;
import java.util.*;
import java.util.Random;

import NetworkFiles.NetworkAdapter;
/**
 Steven Robles
 Andres Ramos 
 Assignment 1
 CS 3331 Object Oriented
 Instructer: Edger Padilla
 Purpose: 
 	The purpose of this file is to provide the calss for the board in order to create a function 
 	tetris game  
 */
public class Board extends Observable
{
	private  int level = 1;
	private  int score = 0;
	private  int linesCleared = 0; 
	private  int rows = 20; 
	private  int  columns = 10; 
	private  int currentLinesCleared = 0; 
	private  boolean isGameActive = false;
	public static String[][] board = new String[20][10];
	private int [] recievedBoard; 
	private int max = 10; 
	private int min = 0; 
	private NetworkAdapter adapter; 
	private int opponentScore = 0; 
	private boolean opponentStatus = false; 
	private boolean multiplayer = false; 

	
	/*
	 * this function is used to acces the board's content in the UI
	 * @return board
	 */
	public static String[][] getBoard()
	{
		return board; 
	}
	/**
	 * 
	 * @return boolean about active game
	 */
	public boolean getIsGameActive()
	{ 
		return isGameActive;
	}
	public void setGameStatus() {
		isGameActive = true; 
	}
	/**
	 * 
	 * @return current score
	 */
	public int getScore()
	{
		if(currentLinesCleared == 1)
			score += 100; 
		if(currentLinesCleared==2)
			score += 300; 
		if(currentLinesCleared==3) 
			score += 600;
		if(currentLinesCleared==4) 
			score += 1000;
		currentLinesCleared = 0;
		return score;
	}
	/**
	 * 
	 * @return current level
	 */
	public int getLevel()
	{	
		return level;
	}
	/**
	 * 
	 * @return current number of lines cleared
	 */
	public int getLinesCleared() 
	{
		return linesCleared; 
	}
	/*
	 * this function sets the multiplayer 
	 */
	public void setMultiplayer(boolean inMultiplayer) 
	{
		multiplayer = inMultiplayer; 
	}
	/*
	 * this function returns the status of multiplayer
	 * @return multiplayer
	 */
	public boolean getMultiplayer() 
	{
		return multiplayer; 
	}
	/**
	 * checks if the game has been lost
	 * */
	public void checkLostGame(int[][] newPiece) 
	{ 
		for(int i = 0; i < 4; i++) 
		{
			if (board[newPiece[i][0]][newPiece[i][1]] != null)
				isGameActive = false;  //generated piece colides with antohher piece 
		} 
	}
	
	/*
	 * this fucntion will set the network
	 */
	public void setAdapter(NetworkAdapter inAdapter) 
	{
		adapter = inAdapter; 
	}
	
	/*
	 * set the status recieved from the other plaer
	 */
	public void setRecievedStatus(int otherScore, int otherGameStatus, int[]otherBoard) 
	{
		opponentScore = otherScore; 
		if(otherGameStatus == 0) 
		{
			opponentStatus = false; 
		}
		else {
			opponentStatus = true; 
		}
		recievedBoard = otherBoard; 
		setChanged(); //notifies UI to run update to decrease the delay time
		notifyObservers();
	}
	
	/*
	 * returns opponents score
	 * @return opponents score
	 */
	public int getOpponentScore() 
	{
		return opponentScore; 
	}
	/*
	 * this function sends the opponents status to the UI
	 * @retrun Opponent status
	 */
	public boolean getOpponentGameStatus() 
	{
		return opponentStatus; 
	}
	
	private void writeFill(int lines) 
	{	
		if(lines > 1) 
		adapter.writeFill(lines-1);
	}
	
	public void sendStatus(int sendScore, int sendStatus, Tetromino tetromino) 
	{
		int[] networkBoard = new int[612];
		if (tetromino.getCoordinates() != null) 
		{
			int[][] piece = tetromino.getCoordinates(); 
			for (int i = 0 ; i < 4; i++) 
			{
				networkBoard[(i*3)] = piece[i][1]; 
				networkBoard[(i*3)+1] = piece[i][0];
				networkBoard[(i*3)+2] = letterToNum(tetromino.getLetter()); 
			}
		}
		
		int count = 12; 
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++) 
			{
				if(board[i][j] != null) 
				{
					networkBoard[count] = j; 
					networkBoard[count+1] = i;
					networkBoard[count+2] = letterToNum(board[i][j]);
					count+=3; 
				}
			}
		}
		adapter.writeStatus(sendScore, sendStatus, networkBoard);
	}
	
	/*
	 * The following will add filler blocks for the multiplayer version
	 */
	public void addFill(int fillers)
	{	
		if (isGameActive) {
			Random rand = new Random();
			//the following try catch moves everything up from one
			if(isGameActive) {
				for(int fill = 0; fill < fillers; fill++) {
					try {
						int randomNum = rand.nextInt((max - min) + 1) + min;
						for(int i = 0; i < rows-1; i++) 
						{
							for (int j = 0; j < columns; j++) 
							{
								board[i][j] = board[i+1][j]; 
								if(i == rows-2) 
								{
									if(j != randomNum)
										board[rows-1][j] = "F"; 
									else
										board[rows-1][j] = null; 									
								}
							}
						} 
					}
					catch(ArrayIndexOutOfBoundsException exception) {
					    isGameActive = false; 
					}
				}
				setChanged(); //notifies UI to run update to decrease the delay time
				notifyObservers();
			}
		}
	}

	/**
	 * moves down rows accoding to the number deleted
	 */
	private void moveRowsDown(int initRow)
	{ 
		for (int i = initRow; i > 0; i--)
		{
			for (int j = 0; j < 10; j++)
			{
				board[i][j] = board[i-1][j]; 
			}
		}
	}

	/**
	 * checks if the piece placed completed any rows
	 */
	private void checkLineDeletion()
	{ 
		boolean delete;
		for (int i = 0; i < 20; i++) //goes through each of the segments of the tetromino
		{
			delete = true; 
			for(int j = 0; j < 10; j++) //checks the whole row at the given y coordinate
			{
				if(board[i][j] == null) //empty spot is found. Deletion is inactivated
					delete = false;  
			}
			if(delete) //delition is activated
			{
				currentLinesCleared+=1; //keeps count how many lines were cleared at once for point addition
				linesCleared++; 
				if(linesCleared % 5 == 0) //updates the level based upon number of lines cleared
				{
					level++; 
					setChanged(); //notifies UI to run update to decrease the delay time
					notifyObservers();
				}
				moveRowsDown(i); 
			}
		}
		
		if(multiplayer)
			writeFill(currentLinesCleared); 
	}

	/**
	 * 
	 * @return piece validation
	 */
	private boolean validateTetrominoPosition(int[][] position)
	{
		//checks for piece collision and out of bounds
		for (int i = 0; i < 4; i++){ 
			//checks out of bounds
			if (position[i][0] < 0 || position[i][0] > rows-1 || position[i][1] < 0 || position[i][1] > columns-1)
			{	
				return false; 
			}
			if(board[position[i][0]][position[i][1]] != null)//checks piece collision, mainly in rotation
			{
				return false; 
			}
			//prevents piece from rotating to the other side of the board
			if(Math.abs(position[0][1]- position[i][1])>3){
				return false; 
			}
		}
		return true; 
	}

	/*
	 * @return coordinates that are validated
	 */
	private int[][] checkSaveToBoard(int[][] coordinates, String letter)
	{	 
		checkLostGame(coordinates); 
		if(isGameActive) 
		{
			int [][] checkBelow = new int[4][2]; //creates array to validate below
			for(int i = 0; i < 4; i++)
			{
				checkBelow[i][0] = coordinates[i][0]+1;
				checkBelow[i][1] = coordinates[i][1]; 
			}
			boolean check = validateTetrominoPosition(checkBelow); 
			if (check == false) //saves piece onto board *as in it cannot to got futher and its in its final place*
				{
					for (int i = 0; i < 4; i++)
					{
						board[coordinates[i][0]][coordinates[i][1]] = letter; 
					}
					score += 5; //this is temporary, only for assignment1. 
					checkLineDeletion();
					coordinates = null; //piece is added to the board, so new piece is going to be requested
				}
			//notifies the observer to run update
			setChanged();
			notifyObservers(); 
			return coordinates; 
		}
		else {
			return null; 
		}
	}

	/**
	 * 
	 * @param origional
	 * @param coord
	 * @param letter
	 * @return new coordinates if the move is valid
	 */
	public int[][] movePiece(int[][] origional, int[][] coord, String letter)
	{	
		if(isGameActive) {
			boolean check = validateTetrominoPosition(coord); //check if the move is valid
			if (check)
			{
				for(int i = 0; i < 4; i++)//updates the piece coodinates if the move is valid
				{
					origional[i][0] = coord[i][0]; 
					origional[i][1] = coord[i][1];
				}
				origional = checkSaveToBoard(origional, letter); 
			} 
		}
		return origional; 
		
	}
	/**
	 * 
	 * @param coordinates
	 * @param letter
	 * @return new coordinates if move down is valid
	 */
	public int[][] moveTetrominoDown(int[][] coordinates, String letter)
	{	
		if(isGameActive) 
		{
			int [][] destination = new int[4][2]; //creates the desitnation coordinates
			for(int i = 0; i < 4; i++)
			{
				destination[i][0] = coordinates[i][0]+1; 
				destination[i][1] = coordinates[i][1];
			}
			boolean check = validateTetrominoPosition(coordinates); //checks the destination validation 
			if(check)//validation is valid, proceeds to print the board
				coordinates = checkSaveToBoard(destination, letter);//checks if the piece must be added to the board
		}
			return coordinates; 
	}
	
	/**
	 * 
	 * @param coordinates
	 * @param letter
	 * @return new coordinates if move right is valid
	 */
	public int[][] moveTetrominoRight(int[][] coordinates, String letter)
	{
		checkLostGame(coordinates); 
		if(isGameActive) 
		{
			int [][] destination = new int[4][2]; //creates the desitnation coordinates
			for(int i = 0; i < 4; i++)
			{	 
				destination[i][0] = coordinates[i][0]; 
				destination[i][1] = coordinates[i][1]+1;
			}
			boolean check = validateTetrominoPosition(destination); 
			if(check)//validation is valid, proceeds to print the board
				coordinates = checkSaveToBoard(destination, letter);//checks if the piece must be added to the board
		}
		return coordinates;  
	}
	/**
	 * 
	 * @param coordinates
	 * @param letter
	 * @return new coordinates if move left is valid
	 */
	public int[][] moveTetrominoLeft(int [][] coordinates, String letter)
	{	
		if(isGameActive) 
		{
			int [][] destination = new int[4][2]; //creates the desitnation coordinates
			for(int i = 0; i < 4; i++)
			{	 
				destination[i][0] = coordinates[i][0]; 
				destination[i][1] = coordinates[i][1]-1; 
			}
			boolean check = validateTetrominoPosition(destination); 
			if(check) {//validation is valid, proceeds to print the board
				coordinates = checkSaveToBoard(destination, letter);//checks if the piece must be added to the board 
			}
		}
		return coordinates; 
	}
	
	/**
	 * 
	 * @param coordinates
	 * @param letter
	 * @return new coordinates if piece is added to the board
	 */
	public int[][] slamTetrominoDown(int[][] coordinates, String letter)
	{	
		checkLostGame(coordinates); 
		if(isGameActive) 
		{	
			boolean slamingDown = true; 
			while(slamingDown) 
			{
				for(int i = 0; i < 4; i++) {
					if(board[coordinates[i][0]+1][coordinates[i][1]]!= null || coordinates[i][0] >=19)
						slamingDown = false; 
				}
				if(slamingDown) {
					for(int i = 0; i <4; i++) 
					{
						coordinates[i][0]+= 1; 
						if(coordinates[i][0] == 19)
							slamingDown = false; 
					}
				}
			}
			coordinates = checkSaveToBoard(coordinates, letter);//checks if the piece must be added to the board
			return coordinates; 
		}
		return null; 
	}
	
	/*
	 * converts letters to numbers
	 * @return number of letter
	 */
	
	public int letterToNum(String letter) 
	{
		int number; 
		switch (letter)
		{
		case "I":
			number = 1; break;
		case "J":
			number = 2; break;
		case "L":
			number = 3; break;
		case "O":
			number = 4; break;
		case "S":
			number = 5; break;
		case "Z":
			number = 6; break;
		case "T":
			number = 7; break;
		case "F":
			number = 8; break;
		default:
			number = 0; break;
		}//end switch
		return number;
	}
	
	/*
	 * converts letters to numbers
	 * @return letter of piece
	 */
	
	public String numToLetter(int number) 
	{
		String letter; 
		switch (number)
		{
		case 1:
			letter = "I"; break;
		case 2:
			letter = "J"; break;
		case 3:
			letter = "L"; break;
		case 4:
			letter = "O"; break;
		case 5:
			letter = "S"; break;
		case 6:
			letter = "Z"; break;
		case 7:
			letter = "T"; break;
		case 8: 
			letter = "F"; break;
		default:
			letter = null; 
		}//end switch
		return letter;
	}
	
	public int[] getRecievedBoard() 
	{
		return recievedBoard; 
	}
	
	public void wipeScreen() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				board[i][j] = null; 
			}
		}
		score = 0; 
		level = 1; 
		linesCleared = 0;
	}
}