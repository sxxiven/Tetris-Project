/**
**
 @author Steven Robles
 @author Andres Ramos 
 Assignment 1
 CS 3331 Object Oriented
 Instructer: Edger Padilla
 Purpose: 
 	The purpose of this file is to provide the controller class for the 
 	tetris game  
 */

package Model;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import Graphics.TetrisUI;
import NetworkFiles.NetworkMessageListener;
import NetworkFiles.NetworkAdapter.MessageType;

public class Controller implements KeyListener, NetworkMessageListener {
	//classes and variables used
	private int [][] clone = new int[4][2];  
	private static Board board;
	private static TetrisUI Ui; 
	private static Tetromino currentPiece;
	private static tetrominoFactory factory; 
	private boolean stopTimer = false; 
	private String opponentName; 

	/**
	 * Defines action when a key is pressed
	 * @param key input
	 */
	@Override
	public void keyPressed(KeyEvent e) {

		int keyCode = e.getKeyCode();
		
		//pause
		if  (keyCode == KeyEvent.VK_ESCAPE)
		{ 
			if(stopTimer == false) {
				stopTimer = true; 
				Ui.stopTimer();
			}
			else {
				Ui.startTimer(); 
				stopTimer = false; 
			}
			return;
		}
		if(board.getIsGameActive() && stopTimer == false)
		{
			switch(keyCode) //control swtich cases
			{ 
			case KeyEvent.VK_DOWN: 
				 currentPiece.setCoordinates(board.moveTetrominoDown(currentPiece.getCoordinates(), currentPiece.getLetter())); 
				 checkForNewPieceGeneration(); 
				break;
			case KeyEvent.VK_LEFT:
				currentPiece.setCoordinates(board.moveTetrominoLeft(currentPiece.getCoordinates(), currentPiece.getLetter()));
				checkForNewPieceGeneration(); 
				break;
			case KeyEvent.VK_RIGHT :
				currentPiece.setCoordinates(board.moveTetrominoRight(currentPiece.getCoordinates(), currentPiece.getLetter()));
				checkForNewPieceGeneration(); 
				break; 
			case KeyEvent.VK_UP:
				currentPiece.setCoordinates(board.slamTetrominoDown(currentPiece.getCoordinates(), currentPiece.getLetter()));
				checkForNewPieceGeneration(); 
				break;
			case KeyEvent.VK_Z:
				if(currentPiece.getLetter()!= "O") { //prevents from the 'O' tetromino from rotating
					clone = currentPiece.rotateLeft(currentPiece.getCoordinates()); 
					currentPiece.setCoordinates(board.movePiece(currentPiece.getCoordinates(), clone, currentPiece.getLetter()));
					checkForNewPieceGeneration(); 
				}
				break;
			case KeyEvent.VK_C:
				if(currentPiece.getLetter()!="O") //prevents the 'O' tetromino from rotating
				{
					clone = currentPiece.rotateLeft(currentPiece.getCoordinates());  
					currentPiece.setCoordinates(board.movePiece(currentPiece.getCoordinates(),clone, currentPiece.getLetter()));
					checkForNewPieceGeneration();
				}
				break; 
			case KeyEvent.VK_SPACE:
					currentPiece = factory.swapWithHoldPiece(currentPiece);
					checkForNewPieceGeneration();
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * automatically shifts game piece down according to the timer
	 */
	public void shiftDown() 
	{	
		if(currentPiece.getCoordinates() != null && board.getIsGameActive()) {
		currentPiece.setCoordinates(board.moveTetrominoDown(currentPiece.getCoordinates(), currentPiece.getLetter()));
		checkForNewPieceGeneration(); 
		}
	}
	
	/**
	 * checks if a new piece is required to be generated for the 'next piece' position
	 */
	protected void checkForNewPieceGeneration() {
		if (currentPiece.getCoordinates() == null && board.getIsGameActive())
			currentPiece = factory.generateNextPiece(currentPiece);
	}
	/**
	 * defines action when key is released
	 */
	@Override
	public void keyReleased(KeyEvent e) {
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
	/**
	 * 
	 * @return current score
	 */
	public int getScore() 
	{
		return board.getScore(); 
	}
	/**
	 * 
	 * @return current lines cleared
	 */
	public int getLines() 
	{
		return board.getLinesCleared(); 
	}
	/**
	 * 
	 * @return current level player is on
	 */
	public int getLevel() 
	{
		return board.getLevel(); 
	} 
	/**
	 * 
	 * @return current game is active or not
	 */
	public boolean getGameActive() 
	{
		return board.getIsGameActive(); 
	}
	
	/**
	 * 
	 *gets board object from TetrusUI
	 */
	public void addBoardObj(Board obj) {
		this.board = obj; 
	}
	public void addUi(TetrisUI obj) 
	{
		this.Ui = obj;
	}
	/**
	 *adds factory object to the controller file 
	 * @param obj
	 */
	public void addFactory(tetrominoFactory obj) {
		this.factory = obj; 
	}
	/**
	 * sets the new current piece object type tetromino from initlized game onto controller
	 * @param obj1
	 */
	public void setNewCurrentPiece(Tetromino obj1) {
		this.currentPiece = obj1; 
	}
	/**
	 * function returns current piece object type tetromino
	 * @return currentPiece 
	 */
	public Tetromino getCurrentPiece() {
		return currentPiece; 
	}
	
	/*
	 * this function returns the opponents name in multiplayer mode
	 * @return opponentName
	 */
	public String getOpponentName() 
	{
		return opponentName; 
	}
	
	
	//the followoing interacts with the listner
	//Board board; 
	@Override
	public void messageReceived(MessageType type, String s, int x, int y, int z, int[] others) {
		// TODO Auto-generated method stub
		switch (type) {
		case NEW:
			opponentName = s; 
			Ui.gameConfirmation(s); 
			break; 
		case NEW_ACK: 
			if(x == 1) 
			{	
				opponentName = s; 
				Ui.setConnectedStatus();
				Ui.startNewGame();
				board.setMultiplayer(true);	
			}
			else
				Ui.gameDeclinedNotice();			 
			break; 
		case STATUS: //intx = score, int y = onlineStatus, 
			board.setRecievedStatus(x, y, others); 
			break; 
		case FILL: 
			board.addFill(x);
			break; 
		case QUIT: 
			Ui.setDisconnectedStatus();
			break; 
		case UNKNOWN: 
		default: 
			break; 
		}
	}
}