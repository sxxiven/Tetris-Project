package Graphics;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import Model.Board;
import Model.Controller;
import Model.tetrominoFactory;
import NetworkFiles.NetworkAdapter;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.Icon; 

/**
 * Creates main user interface. See AnimationApplet to see what is being inherited.
 * @author epadilla2
 *
 */


@SuppressWarnings("serial")
public class TetrisUI extends AnimationApplet implements Observer {
	//variables and objects
	private static Controller controller = new Controller();
	protected static Board board = new Board();
	private static tetrominoFactory factory = new tetrominoFactory(); 
	public boolean stopTimer = false; 
	public int shiftDelay = 700;
	private int currentLevel = 1; 
	private ActionListener menuClick = null;
	private String name = null;
	private ServerSocket ss; 
	private String host; 
	private int port; 
	private Socket socket; 
	private NetworkAdapter adapter;
	JButton statusButton = new JButton(); 

	public TetrisUI(String[] args) 
	{
		super(args);
	}
	
	/**
	 * action listener for menu click items
	 * @param clickMenu
	 */
	public void setActionListener (ActionListener clickMenu) {
		this.menuClick = clickMenu;
	}

	/**
	 * creates and returns a tool bar to be utilized by the user
	 * @return
	 */
	protected JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setBackground(Color.BLACK);
		JButton button = new JButton(new ImageIcon(getImage(getCodeBase(), "ngbutton.png")));
		JButton button2 = new JButton(new ImageIcon(getImage(getCodeBase(), "instructionsbutton.png")));
		JButton button3 = new JButton(new ImageIcon(getImage(getCodeBase(), "quitbutton.png")));
		setDisconnectedStatus();  
		button.setFocusable(false);
		button2.setFocusable(false);
		button3.setFocusable(false);
		button.setToolTipText("Click here to start a new game!");
		
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) //menu button is selected
			{
				timer.stop();
				int input1; 
				String first = "Single Player";
				Object[] players = { "Single Player", "Multiplayer" };
				Object selection = JOptionPane.showInputDialog(null, "Please choose number of players:\n", 
				"Players", 1, null, players, first);
				
				//singleplayer is selecteds
				if(selection == "Single Player") 
				{
					if(board.getIsGameActive()) 
					{
					input1 = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new Game?", 
							"Select an Option...",
							JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
						if(input1 == 0) 
						{
							if(board.getMultiplayer()) 
							{
								setDisconnectedStatus();
								adapter.writeQuit();
								adapter.close(); 
							}
							startNewGame(); 
						}
						if(input1 == 1) {
							timer.start();
							return;
						}
					}
					else {
						if(board.getMultiplayer()) 
						{
							setDisconnectedStatus();
							adapter.writeQuit();
							adapter.close(); 
						}
						startNewGame();
					}
				}
				else {	
					//mulitplayer options is selected
					name = JOptionPane.showInputDialog(null, "Please enter your name", "Player Name", 1); 
					System.out.println(name);
					Object[] options1 = { "Host", "Connect",};
					
					
					JPanel panel = new JPanel();
			        panel.add(new JLabel("Please select an option"));
			        
			        int result = JOptionPane.showOptionDialog(null, panel, "Please choose an option.",
			                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
			                null, options1, null);
			        //host
			        if (result == 0) {
			        	createServer(); 
			        }
			        //client
			        if (result == 1) {
			        	createClient(); 			
					}

				}
			repaint();
			}
		});
		
		button2.setToolTipText("Click here to view controls!");
		button2.addActionListener(new ActionListener() {
			
			//the folloiwn displayes the instructions
			public void actionPerformed(ActionEvent e){displayInstructions(); }	
		});
					
		button3.setToolTipText("Click here to quit the game!");
		button3.addActionListener(new ActionListener() {
			
			//the following runs the quit game selections
			public void actionPerformed(ActionEvent e){quitGame();}		
		});
		
			toolBar.add(button);
			toolBar.add(button2);
			toolBar.add(button3);
			toolBar.add(statusButton);
			toolBar.setFloatable(false);
			return toolBar;
	}

	/**
	 * creates and returns a menu bar to be used by user
	 * @return
	 */
	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("Main menu");
		menu.setBackground(Color.BLACK);
		menuBar.add(menu);
		
		JMenuItem instructions = new JMenuItem("View Instructions", KeyEvent.VK_T);
		KeyStroke ctrlSKeyStroke = KeyStroke.getKeyStroke("control S");
		instructions.setAccelerator(ctrlSKeyStroke);
		instructions.setBackground(Color.BLACK);
		instructions.setForeground(Color.WHITE);	
		instructions.addActionListener(new ActionListener() 
		{	//the following diplays the instuctions when button is pressaed
			public void actionPerformed(ActionEvent e) {displayInstructions();}
		});
		
		JMenuItem quitGame = new JMenuItem("Quit Game");
		quitGame.setBackground(Color.BLACK);
		quitGame.setForeground(Color.WHITE);
		quitGame.addActionListener(new ActionListener() {
			
			//the folloiwng runs the quit method when selected
			public void actionPerformed(ActionEvent e){ quitGame();}		
		});
		
		
		JMenuItem testNetworkDialog = new JMenuItem("New Game", KeyEvent.VK_T);
		testNetworkDialog.setMnemonic(KeyEvent.VK_T);
		testNetworkDialog.setBackground(Color.BLACK);
		testNetworkDialog.setForeground(Color.WHITE);
		testNetworkDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				timer.stop();
				int input1; 
				String first = "Single Player";
				Object[] players = { "Single Player", "Multiplayer" };
				Object selection = JOptionPane.showInputDialog(null, "Please choose number of players:\n", 
				"Players", 1, null, players, first);
				
				//single player is seleceted
				if(selection == "Single Player") 
				{
					if(board.getIsGameActive()) 
					{
					input1 = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new Game?", 
							"Select an Option...",
							JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
						if(input1 == 0) 
						{
							board.setMultiplayer(false);
							if(board.getMultiplayer()) 
							{
								setDisconnectedStatus();
								adapter.writeQuit();
								adapter.close(); 
							}
							startNewGame(); 
						}
						if(input1 == 1) {
							timer.start();
							return;
						}
					}
					else {
						if(board.getMultiplayer()) 
						{
							setDisconnectedStatus();
							adapter.writeQuit();
							adapter.close(); 
						}
						startNewGame(); 
					}
				}
				
				else //multiplayer is selected
				{
					//creates diolog for the muliplayer option
					name = JOptionPane.showInputDialog(null, "Please enter your name", "Player Name", 1); 
					Object[] options1 = { "Host", "Client",};
					
					JPanel panel = new JPanel();
			        panel.add(new JLabel("Please select an option"));
			        
			        int result = JOptionPane.showOptionDialog(null, panel, "Please choose an option.",
			                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
			                null, options1, null);
			        //host
			        if (result == 0) {
			        	createServer(); 
			        }
			        //client
			        if (result == 1) {
			        	createClient(); 
					}
				}
				
			repaint();
			}
		});
		instructions.setMnemonic(KeyEvent.VK_A);
		menu.add(testNetworkDialog);
		menu.add(instructions);
		menu.add(quitGame);
		return menuBar;
	}
	/*
	 * the folliwng is called when quit game is selected
	 */
	public void quitGame() 
	{
		timer.stop();
		int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", 
				"Select an Option...",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
		if(input == 0) 
			if(board.getMultiplayer()) 
			{
				setDisconnectedStatus();
				adapter.writeQuit();
				board.setMultiplayer(false);
				adapter.close(); 
			}
			System.exit(0); //quits current game
		if(input == 1)
			timer.start(); //resumes the game
		return;	
	}
	
	/*
	 * the following function is called when displaying the instructions window
	 */
	public void displayInstructions() 
	{
		TetrisUI.this.stop();
		JDialog instructionsBox = new JDialog();
		instructionsBox.setSize(300, 200);
		instructionsBox.setTitle("Controls");
		
		StringBuilder instructText = new StringBuilder();
		instructText.append("Right Arrow Key: Move tetromino to the right.\n");
		instructText.append("Left Arrow Key: Move tetromino to the left.\n");
		instructText.append("Down Arrow Key: Push tetromino down.\n");
		instructText.append("Up Arrow Key: Slam tetromino down.\n");
		instructText.append("\"A\" Key: Rotate tetromino right.\n");
		instructText.append("\"Z\" Key: Rotate tetromino left.\n");
		instructText.append("Escape Key: Pause-Start game.\n");
        JTextArea instructionsText = new JTextArea(instructText.toString());
        
        instructionsText.setEditable(false);
        instructionsText.setBackground(Color.WHITE);
        instructionsText.setForeground(Color.BLACK);
        instructionsBox.add(new JScrollPane(instructionsText));
        instructionsBox.setVisible(true);
        timer.start();
	}
	/*
	 * this funtion runs when creating a server 
	 * 
	 */
	void createServer() 
	{
    	host = JOptionPane.showInputDialog(null, "Enter IP Address: ", "IP Address", 1); 
    	System.out.println(host);
    	port = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Port Number: ", "Connect", 1)); 
    	System.out.println(port);
    	if (true) {
			try 
			{
				ss = new ServerSocket(port);
				socket = ss.accept(); 
				
			}
			catch(IOException e1) {e1.printStackTrace(); System.out.println("Failed connection");}
			adapter = new  NetworkAdapter(socket); 
			adapter.setMessageListener(controller);  
			adapter.receiveMessagesAsync(); 
		}
	}
	
	/*
	 * This function runs when accepting or declinging incoming connection
	 */
	public void gameConfirmation(String clientName) 
	{
		int input = JOptionPane.showConfirmDialog(null, "Connect with " + clientName + "?", 
				"Select an Option...",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
		if (input == 0) {
			board.setMultiplayer(true);
			board.setAdapter(adapter);
			setConnectedStatus();
			startNewGame(); 
			adapter.writeNewAck(name, 1);
		}
		if (input == 1) 
			adapter.writeNewAck(name, 0); 
	}
	
	/*
	 * this  function is called to notirfy that the client request was declined
	 */
	public void gameDeclinedNotice() 
	{
		JOptionPane.showMessageDialog(null, "Game Decline"); 
	}
	
	/*
	 * this function runs to create the client when selsected in multiplayer mode
	 */
	public void createClient() 
	{
    	host = JOptionPane.showInputDialog(null, "Enter IP Address: ", "IP Address", 1); 
    	System.out.println(host);
    	port = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Port Number: ", "Port Number", 1)); 
    	System.out.println(port);
    	if(true) {
			try 
			{
				System.out.println("Connecting");
				
				socket = new Socket(host, port); 
				System.out.println("Connection Successful");
			}
			catch(IOException e1) {e1.printStackTrace(); }
			adapter = new  NetworkAdapter(socket); 
			adapter.setMessageListener(controller);
			board.setAdapter(adapter); 
			adapter.receiveMessagesAsync();
			adapter.writeNew(name);
    	}
	}
	
	/*
	 * the follwing function sets the cololr of the connected status to green when connected
	 */
	public void setConnectedStatus() { 
		statusButton.setIcon(new ImageIcon(getImage(getCodeBase(), "green.png")));
		board.setMultiplayer(true);
	}
	
	/*
	 * the following function sets the color of the connected status to red when dissconnected
	 */
	public void setDisconnectedStatus() { 
		statusButton.setIcon(new ImageIcon(getImage(getCodeBase(), "red.png")));
		if(board.getMultiplayer()) {
			adapter.close();
			board.setMultiplayer(false);
		}
	}
	/*
	 * this function is called when a new game is started
	 */
	public void startNewGame() 
	{
		board.wipeScreen();
		factory.setNewNextPiece();
		controller.setNewCurrentPiece(factory.generateTetrominoPiece(null)); 
		factory.resetHold(); 
		board.setGameStatus();
		repaint();
		timer.start();
		PlaySound();
	}
	
	/** Sets the status bar at the bottom
	 * 	@param String to add to status bar
	 * 
	 */
	public void setSBText (String n) {
		this.statusBar.setText(n);
	}
	/**
	 * creates the status bar 
	 */
	@Override
	protected JPanel createUI() {

		JPanel root = new JPanel();
		root.setLayout(new BorderLayout());
		JPanel l2 = new JPanel();
		l2.setLayout(new BorderLayout());	
        root.add(createMenuBar(), BorderLayout.NORTH);
        root.add(this, BorderLayout.CENTER);
        root.add(statusBar, BorderLayout.SOUTH);
        root.add(createToolBar(), BorderLayout.WEST);
        return root;
    }
	
	/**
	 * Plays sound based on file
	 * @param Sound
	 */
	public void PlaySound() {
		
		new Thread(new Runnable() {
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResource("tetrisga.wav"));
					clip = AudioSystem.getClip();
					clip.open(inputStream);
					clip.start();
				}
				catch(Exception e){System.err.println(e.getMessage());}
			}	
		}).start();
	}
	
	/**
	 * Stops sound that needs to be stopped
	 * @param Sound 
	 */
	public void StopSound(File Sound) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(Sound));
			clip.stop();
		}
		catch (Exception e){}
	}
	
	/*
	 * the following starts the init method which is ran by main
	 */
	public void init()
	{	 
		super.init();
		this.statusBar.setBackground(Color.black);
		this.statusBar.setText("Welcome to TETRIS!");
		this.statusBar.setForeground(Color.black);
		this.addKeyListener(new Controller());
		this.setFocusable(true);
		this.requestFocus();
		timer = new Timer(shiftDelay, e -> periodicTask());
		dim = getSize();
	}

	/**
	 * action performed throughout duration of timer
	 */
	@Override
	public void periodicTask()
	{ 
		//the following stops the timers if the game is lost
		if(controller.getGameActive() == false) {
			if(board.getMultiplayer()) 
			{ 
				board.sendStatus(board.getScore(), 0, controller.getCurrentPiece()); 
				if (board.getOpponentGameStatus()) {
					this.statusBar.setText(" YOU LOST THE GAME!");
				}
				else 
				{
					this.statusBar.setText(" YOU WON THE GAME!");
				}
			}
			else 
			{
				this.statusBar.setText(" GAME OVER!");
			}
			timer.stop();
			repaint(); 
		}
		else {
			if(board.getMultiplayer() ) 
			{
				if(board.getOpponentGameStatus() == false)
					this.statusBar.setText(" YOU WON THE GAME!");
			}
			controller.shiftDown();
			}
			repaint();
	}
	
	/*the folloiwng stops the timer when called*/
	public void stopTimer() {
		timer.stop(); 
	}
	/*the following starts the timer when called */
	public void startTimer() {
		timer.start(); 
	}

	/** 
	 * overriden from NoApplet
	 * creates graphics and sets colors of everything on the window
	 */
	@Override
	public void paintFrame(Graphics g) 
	{
		if (g==null)
			return;
		// fill the background
		g .fillRect(0, 0, dim.width, dim.height);
		
		//draws the in game board and grid
		g.drawImage(getImage(getCodeBase(), "spacebackground.jpg"),0, 0, this);
		int alpha = 10;
		Color color = new Color(0, 0, 0, alpha);
		g.setColor(color);
		g.fillRect(20, 20, 300, 600);
		
		//the following sets the info on the right side of the board
		g.setColor(Color.white);
		g.drawRect(350, 25, 180, 100);
		g.drawString("Next Piece:", 360, 50);
		
		//the following draws the rectangle and displays in game status
		g.drawRect(350,  150,  180,  80);
		g.drawString("Level: " + controller.getLevel(), 360, 170);
	    g.drawString("Lines: " + controller.getLines(), 360, 185);
	    g.drawString("Score: " + controller.getScore(), 360, 200);
		
		g.drawRect(350, 255, 180, 100);
		g.drawString("Tetromino In Hold:", 360, 280);
		
		//the following draws the board of the opponent if in multiplayer mode
		if(board.getMultiplayer()) 
		{
			g.drawString(controller.getOpponentName()+ " Score: " + board.getOpponentScore(), 360, 215);
			g.drawString(controller.getOpponentName(), 395, 380);
			int [] otherBoard = board.getRecievedBoard(); 
			int k = 2; 
			try
			{
				while(otherBoard[k]!= 0 && k < 612) 
				{
					g.setColor(getTetrominoColor(board.numToLetter(otherBoard[k])));
					g.fillRect((otherBoard[k-2]*10)+395, (otherBoard[k-1]*10)+405, 10, 10);
					k+=3; 	
				}
			}
			catch(NullPointerException e) {}
			g.setColor(Color.white);
			for (int i = 0; i < 11; i++) 
			{	//draws the vertical lines //height board is 600
				g.drawLine(395+(10*i), 405, 395+(10*i), 605);
			}
			for(int i = 0; i < 21; i++) 
			{
				//draws the horezontal lines //width board is 300
				g.drawLine(395, 405+(10*i), 495, 405+(10*i));
			}
		}
		
		//the following prints the rest of the board
		String [][] board = Board.getBoard(); 
		for(int i = 0; i < 20; i++) 
		{
			for (int j = 0; j < 10; j++) 
			{
				if(board[i][j] != null) 
				{
					g.setColor(getTetrominoColor(board[i][j]));
					g.fillRect((j*30)+20, (i*30)+20, 30, 30);
				}
			}
		}
		//prints the piece in hold if any
		try
		{
			int [][] pieceInHold = factory.getHoldPiece().getCoordinates(); 
			g.setColor(getTetrominoColor(factory.getHoldPiece().getLetter()));
			for(int i = 0; i < 4; i++) {
				g.fillRect((pieceInHold[i][1]*20)+360, (pieceInHold[i][0]*20)+305, 20, 20);
			}
		}
		catch(NullPointerException e) {}
		//prints current piece if any
		try
		{
			int [][] currentPiece = controller.getCurrentPiece().getCoordinates();
			g.setColor(getTetrominoColor(controller.getCurrentPiece().getLetter()));
			for(int i = 0; i < 4; i++) {
				g.fillRect((currentPiece[i][1]*30)+20, (currentPiece[i][0]*30)+20, 30, 30);
			}
		}
		catch(NullPointerException e) {}
		
		//the following displays the next piece onto top left of the panel if any
		try 
		{
			int [][] nextPiece = factory.getNextPiece().getCoordinates();
			g.setColor(getTetrominoColor(factory.getNextPiece().getLetter()));
			for(int i = 0; i < 4; i++) {
				g.fillRect((nextPiece[i][1]*20)+360, (nextPiece[i][0]*20)+75, 20, 20);
				
			}			
		}
		catch(NullPointerException e) {}
		
		g.setColor(Color.white);
		for (int i = 0; i < 11; i++) 
		{	//draws the vertical lines //height board is 600
			g.drawLine(20+(30*i), 20, 20+(30*i), 620);
		}
		for(int i = 0; i < 21; i++) 
		{
			//draws the horezontal lines //width board is 300
			g.drawLine(20, 20+(30*i), 320, 20+(30*i));
		}
	}

	/**Returns value of tetromino colors
	 * @param the letter value of the Tetromino Enum
	 * @return Color of tetromino
	 * 
	 * */
	protected static Color getTetrominoColor( String tetrominoEnum)
	{
		Color color = null;
		switch (tetrominoEnum)
		{
		case "I":
			color = Color.CYAN; break;
		case "J":
			color = Color.BLUE; break;
		case "L":
			color = Color.ORANGE; break;
		case "O":
			color  = Color.YELLOW; break;
		case "S":
			color = Color.GREEN; break;
		case "Z":
			color = Color.RED; break;
		case "T":
			color = Color.MAGENTA; break;
		default:
			color =  Color.WHITE; break;
		}//end switch
		return color;
	}//end getTetrominoColor
	/**
	 * When there is a change on the model, the 
	 * View (GUI) gets notified (this method is called)
	 * @param observable object and Object
	 * 
	 */
	public void update(Observable obs, Object obj)
	{
		if(currentLevel < controller.getLevel() && board.getIsGameActive()) {
			currentLevel++;
			shiftDelay -= shiftDelay*.20;
			timer.stop(); 
			timer.setDelay(shiftDelay); 
			timer.start(); 
		}
		if(board.getMultiplayer() && board.getIsGameActive()) 
		{
			if(board.getOpponentGameStatus()) 
			{
				if(board.getScore() < board.getOpponentScore())
					this.statusBar.setText(" OPPONENT IS WINNING!");
				if(board.getScore() == board.getOpponentScore())
					this.statusBar.setText(" THE GAME IS TIED!");
				if (board.getScore() > board.getOpponentScore())
					this.statusBar.setText(" YOU ARE WINNING!"); 
			}
			board.sendStatus(board.getScore(), 1, controller.getCurrentPiece());
		}
		repaint(); 
		return; 
	}
	/**
	 * Main Method
	 * @param args
	 */
	public static void main(String[] args){  
		TetrisUI Ui = new TetrisUI(args); 
		board.addObserver(Ui);
		controller.addBoardObj(board); 
		controller.addUi(Ui); 
		controller.addFactory(factory); 
		Ui.run(); 
		
	}
}//end TetrisUI class
