package core;

import game.Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import core.menu_object.MenuTextField;

public class BattleCastleCanvas extends Canvas implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2960889500737732477L;
	
	private static BufferedImage buffer;
	private boolean running;
	private GameState currentState;
	private Game game;
	private MouseHandler mouseHandler;
	private KeyHandler keyHandler;
	
	public BattleCastleCanvas()
	{
		setPreferredSize(BattleCastleFrame.GAME_SIZE);
		buffer = new BufferedImage(BattleCastleFrame.GAME_SIZE.width,
								   BattleCastleFrame.GAME_SIZE.height,
								   BufferedImage.TYPE_INT_ARGB);
		
		currentState = GameState.MAIN_MENU;
		
		game = new Game(this, HostType.SERVER);
		
		mouseHandler = new MouseHandler(game);
		keyHandler = new KeyHandler(game);
		addMouseMotionListener(mouseHandler);
		addMouseListener(mouseHandler);
		addKeyListener(keyHandler);
		
		serverIPField = new MenuTextField(100, 100, 300, 50);
		running = true;
	}
	
	private MenuTextField serverIPField;
	
	public void render()
	{
		BufferStrategy bs = getBufferStrategy();
		
		if ( bs == null )
		{
			createBufferStrategy(3);
			return;
		}
		
		Graphics b = buffer.getGraphics();
		b.fillRect(0, 0, BattleCastleFrame.GAME_SIZE.width, BattleCastleFrame.GAME_SIZE.height);
		/*
		 * Draw to screen
		 */
		switch (currentState)
		{
		case MAIN_MENU:
			
			if (serverIPField != null)
				serverIPField.render(b);
			
			break;
		case JOIN_SERVER:
			
			
			break;
		case GAMEPLAY:
			
			if (game != null)
			{
				game.render(b);
			}
			
			break;
		
		}
		
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(buffer, 0, 0, BattleCastleFrame.GAME_SIZE.width+32,
				    BattleCastleFrame.GAME_SIZE.height+32, null);
		
		g.dispose();
		bs.show();
	}
	
	public void tick()
	{
		
	}
	
	@Override
	public void run() 
	{
		
		GameTimer timer = new GameTimer();
		timer.restart();
		
		while(running)
		{
			if (timer.getElapsedTime() > 30)
			{
				timer.restart();
				tick();
			}
			
			render();
		}
	}
	
	public GameState getCurrentState()
	{
		return currentState;
	}

	public MenuTextField getMenuText() {
		return serverIPField;
	}

	
}

enum GameState
{
	MAIN_MENU,
	JOIN_SERVER,
	GAMEPLAY;
}
