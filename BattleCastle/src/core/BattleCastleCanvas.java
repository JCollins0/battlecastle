package core;

import game.Game;
import game.object.GameMap;
import game.object.MapType;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import utility.AudioHandler;
import utility.ConfigLoader;
import utility.Utility;

import com.esotericsoftware.kryonet.Client;

import core.constants.DataConstants;
import core.constants.ImageFilePaths;
import core.constants.Keys;
import core.menu_object.MapSelectionObject;
import core.menu_object.MenuButton;
import core.menu_object.MenuButtonType;
import core.menu_object.MenuLabel;
import core.menu_object.MenuSlider;
import core.menu_object.MenuSliderType;
import core.menu_object.MenuTextField;
import core.menu_object.MenuTextFieldType;
import core.menu_object.ServerChoice;
import core.menu_object.ServerSelectionBox;
import core.menu_object.TutorialObject;

public class BattleCastleCanvas extends Canvas implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2960889500737732477L;
	
	private static final int GAME_FRAMES = 30;
	public static final double time_Step = 1.0/GAME_FRAMES;
	
	private static BufferedImage buffer;

	public static Font defaultFont;
	
	private GameState currentState;
	private Game game;
	private MouseHandler mouseHandler;
	private KeyHandler keyHandler;
	private ConfigLoader configLoader;
	private AudioHandler audioHandler;
	private BufferedImage title_image;
	private BufferedImage screenShotImage;
	private BufferedImage backgroundImage;
	private TreeMap<String, GameMap> customLevels;
	private ArrayList<Error> error_messages;
	private ArrayList<TutorialObject> tutorialObjectList;
	private ArrayList<MenuTextField> menuTextFieldList;
	private ArrayList<MenuButton> menuButtonList;
	private ArrayList<MenuLabel> menuLabelList;
	private ArrayList<MenuSlider> sliderList;
	private MenuTextField serverIPField, userNameField;;
	private MenuButton hostGame, joinGame, connectToServer,
					   continueToGame, backButton, levelEditor,
					   refreshLanServers, infoButton;
	private MapSelectionObject map1,map2,map3;
	private MenuLabel userNameLabel, serverIPLabel;
	private TutorialObject leftMouse, upKey, downKey, leftKey, rightKey, dashKey, screenShotKey;
	private ServerSelectionBox serverSelectionBox;
	private MenuSlider volumeSlider;
	private ArrayList<BufferedImage> backgroundImageList;
	
	private ArrayList<MenuLabel> playerFaces;
	
	private boolean running;
	private boolean searchingForServers;
	public boolean debug;
	
	public BattleCastleCanvas()
	{
		setPreferredSize(BattleCastleFrame.GAME_SIZE);
		setMinimumSize(BattleCastleFrame.GAME_SIZE);
		setMaximumSize(BattleCastleFrame.GAME_SIZE);
		buffer = new BufferedImage(BattleCastleFrame.GAME_SIZE.width,
								   BattleCastleFrame.GAME_SIZE.height,
								   BufferedImage.TYPE_INT_ARGB);
		
		screenShotImage = new BufferedImage(BattleCastleFrame.GAME_SIZE.width,
				   							BattleCastleFrame.GAME_SIZE.height,
				   							BufferedImage.TYPE_INT_ARGB);
		
		defaultFont = buffer.getGraphics().getFont();
		
		currentState = GameState.MAIN_MENU;
		
		configLoader = new ConfigLoader(this);
		
		mouseHandler = new MouseHandler(this);
		keyHandler = new KeyHandler(this);
		addMouseMotionListener(mouseHandler);
		addMouseListener(mouseHandler);
		addMouseWheelListener(mouseHandler);
		addKeyListener(keyHandler);
		
		audioHandler = new AudioHandler();
		
		title_image = Utility.loadImage(ImageFilePaths.TITLE);
		
		serverSelectionBox = new ServerSelectionBox(672,200,Utility.loadImage(ImageFilePaths.SERVER_SELECT_BOX));
		error_messages = new ArrayList<Error>();
		
		menuButtonList = new ArrayList<MenuButton>();
		menuTextFieldList = new ArrayList<MenuTextField>();
		menuLabelList = new ArrayList<MenuLabel>();
		tutorialObjectList = new ArrayList<TutorialObject>();
		sliderList = new ArrayList<MenuSlider>();
		
		serverIPField = new MenuTextField(100, 350, 500, 100,
				MenuTextFieldType.SERVER_IP_FIELD,
				Utility.loadImage(ImageFilePaths.TEXT_FIELD),
				GameState.JOIN_SERVER);
		serverIPField.setDefaultText("10.118.40.251");
		serverIPField.setAllowableCharacters("0123456789.");
		
		userNameField = new MenuTextField(100, 200, 500, 100,
				MenuTextFieldType.USERNAME_FIELD,
				Utility.loadImage(ImageFilePaths.TEXT_FIELD),
				GameState.JOIN_SERVER, GameState.INPUT_USER_NAME);
		userNameField.setAllowableCharacters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
		
		menuTextFieldList.add(userNameField);
		menuTextFieldList.add(serverIPField);
		
		map1 = new MapSelectionObject(64, 64, 256, 256,
				MenuButtonType.SELECT_MAP, MapType.ONE, GameState.SELECT_MAP);
		
		map2 = new MapSelectionObject(384, 64, 256, 256,
				MenuButtonType.SELECT_MAP, MapType.TWO, GameState.SELECT_MAP);
		
		map3 = new MapSelectionObject(704, 64, 256, 256,
				MenuButtonType.SELECT_MAP, MapType.THREE, GameState.SELECT_MAP);
		//MapSelectionObject map4 = new MapSelectionObject(64, 384, 256, 256, MenuButtonType.SELECT_MAP, MapType.ONE, GameState.SELECT_MAP);
		
		customLevels = new TreeMap<String, GameMap>();
		
		menuButtonList.add(map1);
		menuButtonList.add(map2);
		menuButtonList.add(map3);
		//menuButtonList.add(map4);
		
		//load maps if any
		loadMaps();
		
		fixMapSelectionObjects(getMapSelectionSubset());
		
		hostGame = new MenuButton(250,300,500,100,
				MenuButtonType.HOST_GAME, 
				Utility.loadImage(ImageFilePaths.HOST_GAME),
				Utility.loadImage(ImageFilePaths.HOST_GAME_SELECTED),
				GameState.MAIN_MENU);
		
		joinGame = new MenuButton(250,425,500,100,
				MenuButtonType.JOIN_GAME,
				Utility.loadImage(ImageFilePaths.JOIN_GAME),
				Utility.loadImage(ImageFilePaths.JOIN_GAME_SELECTED),
				GameState.MAIN_MENU);
		
		levelEditor = new MenuButton(250,575,500,100,
				MenuButtonType.LEVEL_EDITOR,
				Utility.loadImage(ImageFilePaths.LEVEL_EDITOR),
				Utility.loadImage(ImageFilePaths.LEVEL_EDITOR_SELECTED),
				GameState.MAIN_MENU);
		
		infoButton = new MenuButton(64,608,64,64,
				MenuButtonType.INFO_BUTTON,
				Utility.loadImage(ImageFilePaths.INFO_BUTTON),
				Utility.loadImage(ImageFilePaths.INFO_BUTTON_SELECTED),
				GameState.MAIN_MENU);
		
		connectToServer = new MenuButton(150,500,400,100,
				MenuButtonType.CONNECT_TO_IP,
				Utility.loadImage(ImageFilePaths.CONNECT_TO_SERVER),
				Utility.loadImage(ImageFilePaths.CONNECT_TO_SERVER_SELECTED),
				GameState.JOIN_SERVER);
		
		continueToGame = new MenuButton(150,300,400,100,
				MenuButtonType.CONTINUE_TO_GAME,
				Utility.loadImage(ImageFilePaths.CONTINUE),
				Utility.loadImage(ImageFilePaths.CONTINUE_SELECTED),
				GameState.INPUT_USER_NAME);
		
		backButton = new MenuButton(250,650,200,50,
				MenuButtonType.BACK_TO_MENU,
				Utility.loadImage(ImageFilePaths.BACK),
				Utility.loadImage(ImageFilePaths.BACK_SELECTED),
				GameState.JOIN_SERVER, GameState.INPUT_USER_NAME, GameState.SELECT_MAP, GameState.INFO);
		
		refreshLanServers = new MenuButton(704,472,192,48,
				MenuButtonType.REFRESH_LAN_SERVERS,
				Utility.loadImage(ImageFilePaths.REFRESH),
				Utility.loadImage(ImageFilePaths.REFRESH_SELECTED),
				GameState.JOIN_SERVER);
		
		menuButtonList.add(connectToServer);
		menuButtonList.add(backButton);
		menuButtonList.add(hostGame);
		menuButtonList.add(joinGame);
		menuButtonList.add(continueToGame);
		menuButtonList.add(levelEditor);
		menuButtonList.add(refreshLanServers);
		menuButtonList.add(infoButton);
		
		userNameLabel = new MenuLabel(250, 150, 200, 50,
				Utility.loadImage(ImageFilePaths.USER_NAME_LABEL),
				GameState.JOIN_SERVER, GameState.INPUT_USER_NAME);
		
		serverIPLabel = new MenuLabel(250, 300, 200, 50,
				Utility.loadImage(ImageFilePaths.SERVER_IP_LABEL),
				GameState.JOIN_SERVER);
		
		menuLabelList.add(userNameLabel);
		menuLabelList.add(serverIPLabel);
		
		volumeSlider = new MenuSlider(300, 400, 256, 64, 0, 100, 0,
				MenuSliderType.VOLUME, GameState.INFO);
		
		sliderList.add(volumeSlider);
		
		
		//read in data from config.dat; maybe encrypt/decrypt data...
		configLoader.loadConfig();
				
		leftMouse = new TutorialObject(64, 64, 64, 128,
				Utility.loadBufferedList(ImageFilePaths.LEFT_MOUSE_CLICK, 64, 128),
				3, "Launch Arrow", false, GameState.INFO);
		
		upKey = new TutorialObject(370,64, 64, 64,
				Utility.loadBufferedList(ImageFilePaths.KEY_PRESS, 64, 64),
				10, "Jump Key", true, GameState.INFO);
		upKey.setText(Keys.getKeyConfigString(Keys.UP));
		
		leftKey = new TutorialObject(260,134, 64, 64,
				Utility.loadBufferedList(ImageFilePaths.KEY_PRESS, 64, 64),
				10, "Move Left", false, GameState.INFO);
		leftKey.setText(Keys.getKeyConfigString(Keys.LEFT));
		
		downKey = new TutorialObject(370,134, 64, 64,
				Utility.loadBufferedList(ImageFilePaths.KEY_PRESS, 64, 64),
				10, "Move Down", false, GameState.INFO);
		downKey.setText(Keys.getKeyConfigString(Keys.DOWN));
		
		rightKey = new TutorialObject(480,134, 64, 64,
				Utility.loadBufferedList(ImageFilePaths.KEY_PRESS, 64, 64),
				10, "Move Right", false, GameState.INFO);
		rightKey.setText(Keys.getKeyConfigString(Keys.RIGHT));
		
		dashKey = new TutorialObject(590,99, 64, 64,
				Utility.loadBufferedList(ImageFilePaths.KEY_PRESS, 64, 64),
				10, "Dash Key", false, GameState.INFO);
		dashKey.setText(Keys.getKeyConfigString(Keys.DASH));
		
		screenShotKey = new TutorialObject(686,99,64,64,
				Utility.loadBufferedList(ImageFilePaths.KEY_PRESS, 64, 64),
				10, "Screenshot", false, GameState.INFO);
		screenShotKey.setText("F2");
		
		tutorialObjectList.add(leftMouse);
		tutorialObjectList.add(upKey);
		tutorialObjectList.add(leftKey);
		tutorialObjectList.add(rightKey);
		tutorialObjectList.add(downKey);
		tutorialObjectList.add(dashKey);
		tutorialObjectList.add(screenShotKey);
		
		
		
		running = true;
		
		if(debug)
			System.out.println("DEBUGGING...");
		
		backgroundImageList = new ArrayList<BufferedImage>();
		backgroundImageList.add(Utility.loadImage(ImageFilePaths.MENU_BACKGROUND_IMAGE_0));
		backgroundImageList.add(Utility.loadImage(ImageFilePaths.MENU_BACKGROUND_IMAGE_1));
		backgroundImageList.add(Utility.loadImage(ImageFilePaths.MENU_BACKGROUND_IMAGE_2));
		backgroundImage = backgroundImageList.get(0);
		
		playerFaces = new ArrayList<MenuLabel>();
		
	}
	
	/**
	 * Resizes map selection objects depending on number of levels
	 * @param objs the list of possible maps
	 */
	private void fixMapSelectionObjects(ArrayList<MapSelectionObject> objs)
	{
		int size = objs.size();
		int xOffset = 0, yOffset = 0, xSize = 0, ySize = 0;
		int width = BattleCastleFrame.GAME_SIZE.width;
		int height = BattleCastleFrame.GAME_SIZE.height - 150;
		
		int desiredAmountPerRow = getRowsCols(size).x;
		
		int yRows = (int)Math.ceil(size / (double)desiredAmountPerRow) + 1;
		int xCols = desiredAmountPerRow + 1;
		
		xSize = width/xCols;
		xOffset = (width - desiredAmountPerRow * xSize) / (desiredAmountPerRow + 1 );
		
		ySize = height/yRows;
		yOffset = (height - (yRows-1) * ySize ) / (yRows);
		
//		System.out.printf("%d,%d,%d,%d%n",xSize,ySize,xOffset,yOffset);
		int remainder = size % desiredAmountPerRow;
//		System.out.println(remainder);
		
		int x = xOffset;
		int y = yOffset;
		for(int i = 0; i < objs.size(); i++)
		{
			if(i != 0 && i % desiredAmountPerRow == 0)
			{
				x = xOffset;
				
				if(i >= objs.size() - remainder)
				{
					//System.out.println("Changing XOffset");
					if( i == objs.size() - remainder)
						x = 0;
					int remainOffset = width/2 - (xSize * remainder + xOffset * (remainder - 1))/2;
					x += remainOffset;
				}
				
				y += yOffset + ySize;
			}
			
			objs.get(i).setX(x);
			objs.get(i).setY(y);
			objs.get(i).setWidth(xSize);
			objs.get(i).setHeight(ySize);
			
			//System.out.println(objs.get(i).getBounds());
			x += xOffset + xSize;
		}
	}
	
	/**
	 * returns # of rows and columns for map selection screen
	 * @param size the number of levels
	 * @return a point with x as columns and y as rows
	 */
	private Point getRowsCols(int size)
	{
		Point p = new Point();
		for(int i = 0; i < size; i++)
		{
			if(size < Math.pow(i, 2))
			{
				if(size < (i) * (i-1))
				{
					p.x = i;
					p.y = i-1;
					return p;
				}else
				{
					p.x = p.y = i;
					return p;
				}
			}
		}
		return p;
	}
	
	public ConfigLoader getConfigLoader()
	{
		return configLoader;
	}
	
	public MenuTextField getUserNameField()
	{
		return userNameField;
	}
	
	public MenuTextField getServerIpField()
	{
		return serverIPField;
	}
	
	public MenuSlider getVolumeSlider()
	{
		return volumeSlider;
	}
	
	private ArrayList<MapSelectionObject> getMapSelectionSubset()
	{
		ArrayList<MapSelectionObject> obj = new ArrayList<MapSelectionObject>();
		for(int i = 0; i < menuButtonList.size(); i++)
		{
			if(menuButtonList.get(i).getButtonType() == MenuButtonType.SELECT_MAP)
				obj.add((MapSelectionObject)menuButtonList.get(i));
		}
		return obj;
	}
	
	int bgf = 1500;
	int bgfc = 0;
	int alpha = 0;
	int alphaInc = 1;
	int bgilc = 0;
	
	//int processCount=10;
	/**
	 * Draws everything to screen
	 */
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
		
		
		b.setColor(Color.black);
		b.drawString(String.format("%d,%d",MouseHandler.mouse.x, MouseHandler.mouse.y),
				MouseHandler.mouse.x, MouseHandler.mouse.y);
		
		switch (currentState)
		{
		case MAIN_MENU:
			//draw background image;
			b.drawImage(backgroundImage, 0, 0, BattleCastleFrame.GAME_SIZE.width, BattleCastleFrame.GAME_SIZE.height,null);
			
			if(bgfc != bgf)
			{
				bgfc++;
			}else
			{
				if(alpha+alphaInc >= 256)
					alphaInc = -1;
				if(alpha+alphaInc <= -1)
					alphaInc = 1;
				
				alpha+= alphaInc;
				
				if(alpha == 255)
				{
					bgilc++;
					if(bgilc >= backgroundImageList.size())
					{
						bgilc = 0;
						//System.out.println("Changing to original image");
					}
					backgroundImage = backgroundImageList.get(bgilc);
					//System.out.println("Changing to next image");
				}

				b.setColor(new Color(0,0,0,alpha));
				b.fillRect(0, 0, BattleCastleFrame.GAME_SIZE.width, BattleCastleFrame.GAME_SIZE.height);
				
				if(alphaInc < 0 && alpha == 0)
					bgfc = 0;
			}
			
			
			b.setColor(Color.RED);
			b.setFont(Error.ERROR_FONT);
			if(error_messages.size() > 0)
				b.drawString("ERRORS:",16,error_messages.get(0).getY()-20);
			for(int i = 0; i < error_messages.size(); i++)
				error_messages.get(i).render(b);
			b.drawImage(title_image, 0 , 32 , 1024, 128, null);
			
		case JOIN_SERVER:
		case INPUT_USER_NAME:
			for(MenuLabel menuLabel : menuLabelList)
				if(menuLabel.isVisibleAtState(currentState))
					menuLabel.render(b);
			
			
		case SELECT_MAP:
			
			for(MenuTextField menuTextField : menuTextFieldList)
				if(menuTextField.isVisibleAtState(currentState) )
					menuTextField.render(b);
			
			for(MenuButton menuButton : menuButtonList)
				if(menuButton.isVisibleAtState(currentState) )
					menuButton.render(b);
			
			//draw player faces here
			for(MenuLabel playerFace : playerFaces)
				if(playerFace.isVisibleAtState(currentState))
					playerFace.render(b);
			
			break;
		case GAMEPLAY:
			
			if(game != null)
			{
				game.render(b);
			}			
			break;
		case INFO:
			for(TutorialObject tutorialObject : tutorialObjectList)
				if(tutorialObject.isVisibleAtState(currentState))
					tutorialObject.render(b);		
			for(MenuButton menuButton : menuButtonList)
				if(menuButton.isVisibleAtState(currentState) )
					menuButton.render(b);
			for(MenuSlider menuSlider : sliderList)
				if(menuSlider.isVisibleAtState(currentState))
					menuSlider.render(b);
			break;
		}
		
		if(currentState == GameState.JOIN_SERVER)
		{
			if(serverSelectionBox != null)
				serverSelectionBox.render(b);
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(buffer, 0, 0, BattleCastleFrame.GAME_SIZE.width,
				    BattleCastleFrame.GAME_SIZE.height, null);
		
		Graphics s = screenShotImage.getGraphics();
		
		s.drawImage(buffer, 0, 0, BattleCastleFrame.GAME_SIZE.width,
			    BattleCastleFrame.GAME_SIZE.height, null);
		
		g.dispose();
		bs.show();
	}
	
	/**
	 * updates everything 
	 */
	public void tick()
	{

		for(int i = 0; i < error_messages.size(); i++)
		{
			error_messages.get(i).tick();
			if(error_messages.get(i).shouldRemove())
				error_messages.remove(i--);
		}
		
		for(MenuTextField menuTextField : menuTextFieldList)
			if(menuTextField.isVisibleAtState(currentState) )
				menuTextField.tick();
		
		for(TutorialObject tutorialObject : tutorialObjectList)
			if(tutorialObject.isVisibleAtState(currentState))
				tutorialObject.tick();
		for(MenuSlider menuSlider : sliderList)
			if(menuSlider.isVisibleAtState(currentState))
				menuSlider.tick();
		
		if(game != null)
			game.tick();
	}
	
	/**
	 * main thread
	 */
	@Override
	public void run() 
	{
		
		GameTimer timer = new GameTimer();
		timer.restart();
		
		while(running)
		{
			if (timer.getElapsedTime() > GAME_FRAMES)
			{
				timer.restart();
				tick();
			}
			
			render();
		}
	}
	
	/**
	 * decides how to initialize game
	 * @param host t/f if hosting game
	 */
	public void setGame(boolean host)
	{
		
		if(host)
			game = new Game(this, HostType.SERVER);
		else
		{
			game = new Game(this, HostType.CLIENT);
			
			searchForLanServers();
		}
	}
	
	private int pfdx = 256;
	public void addPlayerFace(String playerName, String imagePath)
	{
		MenuLabel playerFace = new MenuLabel(pfdx+=32+128,700,32,32,Utility.loadImage(imagePath),playerName,GameState.SELECT_MAP);
		playerFaces.add(playerFace);
	}
	
	public void clearFaces()
	{
		playerFaces.clear();
		this.pfdx = 256;
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
	
	public ArrayList<MenuSlider> getSliderBars() {
		return sliderList;
	}
	
	public MenuTextField getSelectedMenuTextField()
	{
		MenuTextField ret = null;
		for(MenuTextField field : menuTextFieldList)
			if (field.isSelected())
				ret = field;
		return ret;
	}
	
	public MenuSlider getSlider(MenuSliderType type)
	{
		MenuSlider ret = null;
		for(MenuSlider slider : sliderList)
			if (slider.getType().equals(type))
				ret = slider;
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
	
	public void searchForLanServers()
	{
		searchingForServers = true;
		serverSelectionBox.setStatus(ServerSelectionBox.SEARCHING);
		Timer searchTimer = new Timer();
		searchTimer.schedule(new TimerTask(){
			public void run() {
				
				Client client = game.getClient();
				List<InetAddress> possibleServers = client.discoverHosts(Game.SERVER_UDP, 20000);
				System.out.println(possibleServers.size());
				for(int i = 0; i < possibleServers.size(); i++)
				{
					ServerChoice choice = new ServerChoice(672,200 + (i * ServerChoice.HEIGHT), possibleServers.get(i));
					serverSelectionBox.addServer(choice);
				}
				searchingForServers = false;
				serverSelectionBox.setStatus(possibleServers.size() > 0 ? ServerSelectionBox.FOUND : ServerSelectionBox.NOTHING);
			}
		}, 1000);
	}

	public ServerSelectionBox getServerSelectionBox() {
		return serverSelectionBox;
	}
	
	public void addError(Error error)
	{
		
		if(error_messages.isEmpty())
			error.setPosition(16, 256);
		else
			error.setPosition(16, error_messages.get(error_messages.size()-1).getY()+20);
			
		error_messages.add(error);
	}
	public boolean isSearchingForServers()
	{
		return searchingForServers;
	}
	
	public BufferedImage getBuffer()
	{
		return screenShotImage;
	}

	public TreeMap<String, GameMap> getCustomLevels() {
		return customLevels;
	}
	
	private void loadMaps()
	{
		File directory = new File(DataConstants.LEVELS);
		File[] levels  = directory.listFiles();
		
		for(int i = 0; i < levels.length; i++)
		{
			String levelname = levels[i].getName();
			if(levels[i].isDirectory())
			{
				File[] levelData = levels[i].listFiles();
				
				GameMap map = new GameMap();
				for(int j = 0; j < levelData.length; j++)
				{
					String name = levelData[j].getName();
					
					if(name.contains(".png"))
					{
						map.loadBackground(levelData[j].getAbsolutePath());
						MapSelectionObject obj = new MapSelectionObject(0, 0, 0, 0, MenuButtonType.SELECT_MAP, MapType.CUSTOM, levelname, levelData[j].getAbsolutePath(), GameState.SELECT_MAP);
						menuButtonList.add(obj);
					}else
					{
						//load level here
						map.loadLevelData(levelData[j].getAbsolutePath());
					}
					
				}
				customLevels.put(levelname, map);
			}		
		}
	}
	
}

