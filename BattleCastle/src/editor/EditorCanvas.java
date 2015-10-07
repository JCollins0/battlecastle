package editor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.GameState;
import core.KeyHandler;
import core.MouseHandler;
import game.Game;

public class EditorCanvas extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6879897978771655344L;

	private ArrayList<Tile> list=new ArrayList<Tile>();
	private static BufferedImage buffer;
	private boolean running;
	private GameState currentState;
	private Game game;
	private EditorMouseHandler mouseHandler;
	private KeyHandler keyHandler;
	
	public EditorCanvas()
	{
		init();
	}
	
	public void init()
	{
		setPreferredSize(core.BattleCastleFrame.GAME_SIZE);
		setBackground(Color.BLUE);
		
		mouseHandler = new EditorMouseHandler(this);
		keyHandler = new KeyHandler(this);
		addMouseMotionListener(mouseHandler);
		addMouseListener(mouseHandler);
		addKeyListener(keyHandler);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(Tile t:list)
		{
			t.draw(g);
		}
	}
	
}
