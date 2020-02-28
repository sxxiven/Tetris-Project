package Graphics;

import javax.swing.*;

public class AnimationApplet extends NoApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Timer timer = null;  
	protected int delay;
	
	public AnimationApplet(String[] args) {
		super(args);
	}
	public void init() {
		super.init();
		timer = new Timer(delay, e -> periodicTask());
	}
	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}
	
	public void periodicTask()
	{
		repaint();
	}
 }
	
	
	
	
	
	