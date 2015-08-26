package core;

import java.awt.Dimension;

import javax.swing.JFrame;

public class BattleCastleFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -412527797459057884L;

	public static final String GAME_NAME = "Battle Castle";
	public static final String GAME_VERSION = "1.0";
	public static final Dimension GAME_SIZE = new Dimension(700,500);
	
	
	public static void main(String[] args) {
		new BattleCastleFrame();
	}
	
	public BattleCastleFrame()
	{
		super(String.format("%s v%s",GAME_NAME,GAME_VERSION));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(GAME_SIZE);
		setMaximumSize(GAME_SIZE);
		setSize(GAME_SIZE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
