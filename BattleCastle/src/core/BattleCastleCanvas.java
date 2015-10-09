package core;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.menu_object.MapSelectionObject;
import core.menu_object.MenuButton;
import core.menu_object.MenuButtonType;
import core.menu_object.MenuTextField;
import core.menu_object.MenuTextFieldType;
import game.Game;
import game.Game2;
import game.object.MapType;

public class BattleCastleCanvas extends Canvas implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2960889500737732477L;
	
	private static BufferedImage buffer;
	private boolean running;
	private GameState currentState;
	private Game game;
	private Game2 game2;
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
		serverIPField.setDefaultText("10.118.40.251");
		serverIPField.setAllowableCharacters("0123456789.");
		userNameField = new MenuTextField(100, 600, 100, 50, MenuTextFieldType.USERNAME_FIELD, GameState.JOIN_SERVER, GameState.INPUT_USER_NAME);
		userNameField.setAllowableCharacters("abcdefghijklmnopqrstuvwxyz0123456789");
		menuTextFieldList.add(userNameField);
		menuTextFieldList.add(serverIPField);
		menuButtonList = new ArrayList<MenuButton>();
		map1 = new MapSelectionObject(200, 200, 200, 200, MenuButtonType.SELECT_MAP, MapType.ONE, GameState.SELECT_MAP);
		map2 = new MapSelectionObject(500, 200, 200, 200, MenuButtonType.SELECT_MAP, MapType.TWO, GameState.SELECT_MAP);
		map3 = new MapSelectionObject(800, 200, 200, 200, MenuButtonType.SELECT_MAP, MapType.THREE, GameState.SELECT_MAP);
		menuButtonList.add(map1);
		menuButtonList.add(map2);
		menuButtonList.add(map3);
		hostGame = new MenuButton(500,400,200,50,MenuButtonType.HOST_GAME, GameState.MAIN_MENU);
		joinGame = new MenuButton(500,500,200,50,MenuButtonType.JOIN_GAME, GameState.MAIN_MENU);
		connectToServer = new MenuButton(200,300,100,50,MenuButtonType.CONNECT_TO_IP, GameState.JOIN_SERVER);
		continueToGame = new MenuButton(200,300,100,50,MenuButtonType.CONTINUE_TO_GAME, GameState.INPUT_USER_NAME);
		menuButtonList.add(connectToServer);
		menuButtonList.add(hostGame);
		menuButtonList.add(joinGame);
		menuButtonList.add(continueToGame);
		running = true;
	}
	
	private ArrayList<MenuTextField> menuTextFieldList;
	private ArrayList<MenuButton> menuButtonList;
	private MenuTextField serverIPField;
	private MenuButton hostGame;
	private MenuButton joinGame;
	private MenuButton connectToServer;
	private MenuButton continueToGame;
	private MapSelectionObject map1,map2,map3;
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
		case INPUT_USER_NAME:
		case SELECT_MAP:
			
			for(MenuTextField menuTextField : menuTextFieldList)
				if(menuTextField.isVisibleAtState(currentState) )
					menuTextField.render(b);
			
			for(MenuButton menuButton : menuButtonList)
				if(menuButton.isVisibleAtState(currentState) )
					menuButton.render(b);
			
			break;
		case GAMEPLAY:
			
//			if (game != null)
//			{
//				game.render(b);
//			}
			
			if(game2 != null)
			{
				game2.render(b);
			}
			
			break;
		
		default:
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
//		if(game != null)
//			game.tick();
		
		if(game2 != null)
			game2.tick();
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
//		if(host)
//			game = new Game(this, HostType.SERVER);
//		else
//			game = new Game(this, HostType.CLIENT);
		
		if(host)
			game2 = new Game2(this, HostType.SERVER);
		else
			game2 = new Game2(this, HostType.CLIENT);
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
	
//	public Game getGame()
//	{
//		return game;
//		return game2;
//	}
	public Game2 getGame()
	{
		return game2;
	}

	public MenuTextField getTextFieldByID(MenuTextFieldType textFieldType) {
		for(MenuTextField field : menuTextFieldList)
			if (field.getID() == textFieldType)
				return field;
		return null;
	}
	
	
}

