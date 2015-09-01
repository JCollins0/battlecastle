package core;

import game.Game;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.menu_object.MenuButton;
import core.menu_object.MenuButtonType;
import core.menu_object.MenuTextField;
import core.menu_object.MenuTextFieldType;

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

		mouseHandler = new MouseHandler(this);
		keyHandler = new KeyHandler(this);
		addMouseMotionListener(mouseHandler);
		addMouseListener(mouseHandler);
		addKeyListener(keyHandler);
		
		menuTextFieldList = new ArrayList<MenuTextField>();
		serverIPField = new MenuTextField(100, 100, 300, 50, MenuTextFieldType.SERVER_IP_FIELD, GameState.JOIN_SERVER);
		serverIPField.setAllowableCharacters("0123456789.");
		userNameField = new MenuTextField(100, 600, 100, 50, MenuTextFieldType.USERNAME_FIELD, GameState.JOIN_SERVER);
		userNameField.setAllowableCharacters("abcdefghijklmnopqrstuvwxyz0123456789");
		menuTextFieldList.add(userNameField);
		menuTextFieldList.add(serverIPField);
		menuButtonList = new ArrayList<MenuButton>();
		hostGame = new MenuButton(500,400,200,50,MenuButtonType.HOST_GAME, GameState.MAIN_MENU);
		joinGame = new MenuButton(500,500,200,50,MenuButtonType.JOIN_GAME, GameState.MAIN_MENU);
		connectToServer = new MenuButton(200,300,100,50,MenuButtonType.CONNECT_TO_IP, GameState.JOIN_SERVER);
		menuButtonList.add(connectToServer);
		menuButtonList.add(hostGame);
		menuButtonList.add(joinGame);
		running = true;
	}
	
	private ArrayList<MenuTextField> menuTextFieldList;
	private ArrayList<MenuButton> menuButtonList;
	private MenuTextField serverIPField;
	private MenuButton hostGame;
	private MenuButton joinGame;
	private MenuButton connectToServer;
	private MenuTextField userNameField;
	
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
		case JOIN_SERVER:
			
			for(MenuTextField menuTextField : menuTextFieldList)
				if(menuTextField.getVisibleState() == currentState)
					menuTextField.render(b);
			
			for(MenuButton menuButton : menuButtonList)
				if (menuButton.getVisibleState() == currentState)
					menuButton.render(b);
			
			break;
		case GAMEPLAY:
			
			if (game != null)
			{
				game.render(b);
			}
			
			break;
		
		}
		
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(buffer, 0, 0, BattleCastleFrame.GAME_SIZE.width,
				    BattleCastleFrame.GAME_SIZE.height, null);
		
		g.dispose();
		bs.show();
	}
	
	public void tick()
	{
		if(game != null)
			game.tick();
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
	
	public void setGame(boolean host)
	{
		if(host)
			game = new Game(this, HostType.SERVER);
		else
			game = new Game(this, HostType.CLIENT);
	}
	
	public GameState getCurrentState()
	{
		return currentState;
	}

	public ArrayList<MenuButton> getMenuButtons(){
		return menuButtonList;
	}

	public void setCurrentState(GameState currentState) {
		this.currentState = currentState;
	}
	
	public ArrayList<MenuTextField> getMenuTextFields()
	{
		return menuTextFieldList;
	}
	
	public MenuTextField getSelectedMenuTextField()
	{
		MenuTextField ret = null;
		for(MenuTextField field : menuTextFieldList)
			if (field.isSelected())
				ret = field;
		return ret;
	}
	
	public Game getGame()
	{
		return game;
	}

	public MenuTextField getTextFieldByID(MenuTextFieldType textFieldType) {
		for(MenuTextField field : menuTextFieldList)
			if (field.getID() == textFieldType)
				return field;
		return null;
	}
}

