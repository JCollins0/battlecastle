package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import jdk.nashorn.internal.runtime.FindProperty;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.BattleCastleCanvas;
import core.DoubleLinkedList;
import core.Error;
import core.GameState;
import core.HostType;
import core.KeyHandler;
import core.KeyPress;
import core.MouseHandler;
import core.constants.ImageFilePaths;
import editor.Tile;
import game.message.Message;
import game.message.MessageType;
import game.object.GameMap;
import game.object.MapType;
import game.physics.CollisionDetector;
import game.physics.Polygon;
import game.physics.Vector;
import game.player.Arrow;
import game.player.BattleCastleUser;
import game.player.Player;

public class Game {
	
	//Client-Server Constants
	public static final int SERVER_PORT = 25565;
	public static final int SERVER_UDP = 25566;
	public static final int CLIENT_PORT = 25564;
	private static final int MIN_PLAYERS = 1;
	
	//Client-Server
	private Server gameServer;
	private Client gameClient;
	private HostType hostType;
	private InetAddress serverIP;
	private String myUUID;
	
	//Players & Arrows
	private TreeMap<String, BattleCastleUser> playerMap;
	private Player[] playerList;
	private ConcurrentHashMap<String,Arrow> arrows;

	//Score
	private PlayerScoreHandler playerScoreHandler;
	//Collision
	private CollisionDetector collideDetect;
	
	//GameMap
	private GameMap gameMap, gameMapLoader;
	private boolean mapSelected;
	private MapType mapType;
	private String mapName;

	//Other Objects
	private Random random;
	private BattleCastleCanvas canvasRef;
	private boolean gameOver;
	private int gameOverResetTimer; 
	private int GAME_OVER_RESET_COUNT = 90;	
	
	/**
	 * Initialize Game class
	 * @param canvasRef the reference to the Canvas class
	 * @param hostType either Server or Client
	 */
	public Game(BattleCastleCanvas canvasRef, HostType hostType)
	{
		this.canvasRef = canvasRef;
		this.hostType = hostType;
		playerList = new Player[4];
		playerMap = new TreeMap<String, BattleCastleUser>();
		arrows = new ConcurrentHashMap<String,Arrow>();
		collideDetect = new CollisionDetector(this);
		
		if(hostType == HostType.SERVER)
		{
			startServer();
			startClient();
			connectToServer();
		}
		if(hostType == HostType.CLIENT)
			startClient();
		
		System.setProperty("java.net.preferIPv4Stack" , "true");
		
		playerScoreHandler = new PlayerScoreHandler(this);
		gameMapLoader = new GameMap();
		random = new Random();
	}

	/* ##################
	 * GAME CLASS METHODS
	 * ################## 
	 */
	
	/**
 	 * Initializes Server
	 */
	private void startServer()
	{
		
		try {
			serverIP = InetAddress.getLocalHost(); //sets the server ip to the local host because hosting
			gameServer = new Server(65536, 16384); //creates a new server with server buffers
			gameServer.getKryo().setRegistrationRequired(false); //skips having to register classes to send over network
			
			gameServer.start(); //starts the server
			gameServer.bind(SERVER_PORT,SERVER_UDP); //binds the port numbers for TCP and UDP
			gameServer.addListener(new Listener() {  //used to recieve messages
				public void received (Connection connection, Object object) {
					if (object instanceof BattleCastleUser) { //for registering users
						
						BattleCastleUser user = (BattleCastleUser)object;
						registerUser(user, false);
						
					}else if(object instanceof Message)
					{
						Message messageOb = (Message)object;
						String message = messageOb.toString();
						String[] messageArr = message.split(Message.MESSAGE_TYPE_SEPARATOR);
						String type = messageArr[0].trim();
						processServerMessage(type, messageArr[1]);
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes client
	 */
	public void startClient()
	{
		gameClient = new Client(65536, 16384); //create a new client with buffer sizes
		gameClient.getKryo().setRegistrationRequired(false); //allows classes to be passed over network without registering them
		gameClient.start(); //starts the client
		gameClient.addListener(new Listener(){ //adds listener to accept object packets
			
			public void received (Connection connection, Object object) {
//					System.out.println("CLIENT RECEIVED OBJECT - " + object.toString());
				if (object instanceof BattleCastleUser) //registering users on the client
				{
					BattleCastleUser user = (BattleCastleUser)object;
					registerUser(user, true);

				}else if(object instanceof Message) //Receiving things that happen every tick
				{
					Message messageOb = (Message)object;
					String message = messageOb.toString();
					String[] messageArr = message.split(Message.MESSAGE_TYPE_SEPARATOR);
					String type = messageArr[0].trim();
					processClientMessage(type, messageArr[1]);
				}
			}
		});
	}
	
	
	/**
	 * connects client to server
	 */
	public void connectToServer()
	{
		try {
			gameClient.connect(5000, serverIP, SERVER_PORT, SERVER_UDP);
		} catch (Exception e) {
			canvasRef.setCurrentState(GameState.MAIN_MENU);
			canvasRef.addError(new Error("Could Not Connect To Specified IP Address", 300));
		}
	}
	
	/**
	 * updates everything
	 */
	public void tick()
	{
		if(hostType == HostType.SERVER)
		{
			//will be handling collision
			if(canvasRef.getCurrentState() == GameState.GAMEPLAY)
			{

				if(playerMap.size() >= MIN_PLAYERS)
				{
					
					if(!gameOver)
					{
						for(int i = 0; i < playerList.length; i++)
							if(playerList[i] != null)
							{
								playerList[i].tick();
								Message message = new Message(MessageType.UPDATE_PLAYER,i + Message.EQUALS_SEPARATOR + playerList[i].stringify());
								gameServer.sendToAllTCP(message);
							}
						
						for(Arrow arrow : arrows.values())
						{
							arrow.tick();
							Message message = new Message(MessageType.MOVE_ARROW,arrow.getID() + Message.EQUALS_SEPARATOR + arrow.stringify());
							gameServer.sendToAllTCP(message);
						}
						
						if(gameMap != null)
						{	
							gameMap.tick();
							for(Tile t : gameMap.getTiles())
							{
								Message message = new Message(MessageType.UPDATE_TILE, t.getID() + Message.EQUALS_SEPARATOR + t.stringify() );
								gameServer.sendToAllTCP(message);
							}
						}
						
						List<Arrow> plist = Collections.list(Collections.enumeration(arrows.values()));
						DoubleLinkedList<Tile> tlist = gameMap.getTiles();
						List<Polygon> list = new ArrayList<Polygon>();
						
//						System.out.println("Start");
						for(Tile t : tlist)
						{
							list.add(t);
							
						}
//						System.out.println(list);
//						System.out.println("Finish");
						
						list.addAll(plist);
						for(int i = 0; i < playerList.length && playerList[i] != null; i++)
							list.add(playerList[i]);
						collideDetect.broadCheck(list);
					
						gameOver();
					}	
					else
					{
						if(gameOverResetTimer < GAME_OVER_RESET_COUNT)
						{
							if(gameOverResetTimer == 0) // happens once
							{
								Message message = new Message(MessageType.GAME_OVER,"g");
								gameServer.sendToAllUDP(message);
							}
							gameOverResetTimer++;
						}
						else
						{
							gameOverResetTimer = 0;
							reset();
						}
					
					}
					
					
				}
			}
		}
		
		if(playerMap.size() >= MIN_PLAYERS)
			updateMyPlayer();
	}
	
	/**
	 * draws everything to screen
	 * @param g Graphics for drawing
	 */
	public void render(Graphics g)
	{
		if(!gameOver){
			if(gameMap != null)
				gameMap.render(g);

			for(int i = 0; i < playerList.length; i++)
				if(playerList[i] != null)
					playerList[i].render(g);

			for(Arrow arrow : arrows.values())
			{
				arrow.render(g);
			}
		}
		else
		{
			//Draw score for players
			playerScoreHandler.render(g);
//			for(int i = 0; i < playerList.length; i++)
//				if(playerList[i] != null)
//				{
//					g.drawImage(getPlayerFaceFromPlayer(playerList[i]),32,64*i+32,32,32,null);
//					for(int s = 0; s < playerList[i].getScore(); s++)
//					{
//						g.fillRect(64*(s+1)+32,64*i+32,32,32);	
//					}
//				}
		}
		
			
	}
	
	/* ######################################
	 * EVENTS THAT GET SENT TO SERVER/CLIENTS
	 * ######################################
	 */
	
	/**
	 * @param user the user to login to server
	 */
	public void sendUserData(BattleCastleUser user)
	{
		playerMap.put(user.getUUID(), user);
		playerMap.get(user.getUUID()).setConnected(true);
		gameClient.sendTCP(user);
	}
	
	/**
	 * 
	 * @param mapType the type of map to load
	 * @param mapName the name only used if custom map
	 */
	public void sendMapChoice(MapType mapType, String mapName)
	{
		this.mapType = mapType;
		this.mapName = mapName;
		mapSelected = true;
//		System.out.println("T: " + mapType  +  " " + mapName);
		if(mapType.equals(MapType.CUSTOM) )
		{
			//JOptionPane.showMessageDialog(null, "loading customLevel" + canvasRef.getCustomLevels().get(mapName));
			gameMap = canvasRef.getCustomLevels().get(mapName);
			for(int i = 0; i < playerMap.size(); i++)
			{
				playerList[i] = new Player(ImageFilePaths.TEMP_PLAYER,getUUIDFromPlayer(playerList[i]),getArrowPathFromPlayer(playerMap.get(getUUIDFromPlayer(playerList[i]))));
				//playerList[i].setLocation(gameMap.getPlayerStartPoint(i));
			}
		}
		else
		{
			gameMap = gameMapLoader.getByType(mapType.getBackground());//new GameMap(mapType.getBackground());
			
			//load players and then send to clients
			for(int i = 0; i < playerMap.size(); i++)
			{
				playerList[i] = new Player(ImageFilePaths.TEMP_PLAYER,getUUIDFromPlayer(playerList[i]),getArrowPathFromPlayer(playerMap.get(getUUIDFromPlayer(playerList[i]))));
				playerList[i].setLocation(gameMap.getPlayerStartPoint(i));
			}
			Message message = new Message(MessageType.SELECT_MAP, mapType.getBackground());
			gameServer.sendToAllTCP(message);
			for(int i = 0; i < playerList.length; i++)
			{
				if(playerList[i] != null)
				{
					message = new Message(MessageType.UPDATE_PLAYER,i + Message.EQUALS_SEPARATOR + playerList[i].stringify());
					gameServer.sendToAllTCP(message);
				}
			}
		}
	}
	
	/**
	 * sends player information to the server
	 */
	public void updateMyPlayer()
	{
		for(int i = 0; i < KeyHandler.presses.size(); i++)
		{
			try{
				if(KeyHandler.presses.get(i).getText().equals(KeyPress.LEFT_D.getText()))
					getMyPlayer().setPlayerFacing(Player.FACING_LEFT);
				if(KeyHandler.presses.get(i).getText().equals(KeyPress.RIGHT_D.getText()))
					getMyPlayer().setPlayerFacing(Player.FACING_RIGHT);

				Message message = new Message(MessageType.MOVE_PLAYER,
					playerMap.get(myUUID).getPlayerNumber() + Message.EQUALS_SEPARATOR + KeyHandler.presses.get(i).getText() );
				gameClient.sendTCP(message);
			}catch(Exception e)
			{
				canvasRef.addError(new Error("Error Updating Player [Game.class]",300));
			}
			
		}
		
		KeyHandler.presses.remove(KeyPress.DASH_L);
		KeyHandler.presses.remove(KeyPress.DASH_R);
		KeyHandler.presses.remove(KeyPress.LEFT_U);
		KeyHandler.presses.remove(KeyPress.RIGHT_U);
	}
	
	/**
	 * sends mouse x,y coordinates to the server to point the player
	 * arrow to the correct point
	 */
	public void sendMouseLocation() {
		Message message = new Message(MessageType.UPDATE_MOUSE_LOC,
				playerMap.get(myUUID).getPlayerNumber() + Message.EQUALS_SEPARATOR + String.format("MouseX#%d<MouseY#%d",MouseHandler.mouse.x,MouseHandler.mouse.y) );
		gameClient.sendTCP(message);
	}
	
	/**
	 * sends arrow information to server
	 */
	public void launchArrow()
	{
		launchArrow(getMyPlayer());		
	}
	
	/**
	 * sends arrow information to server given a player
	 * @param player the player that launched the arrow
	 */
	public void launchArrow(Player player, int option)
	{
		if(player.isDead()) return;
		//System.out.println("Launching Arrow For: " + player);
		if(hostType.equals(HostType.SERVER))
		{
			Arrow arrow = player.removeArrow();
			
			if(arrow != null)
			{
				switch(option){
				case 0:	arrow.addVelocity();
					break;
				case 1: arrow.addVelocity(-5 + random.nextInt(10), -5 + random.nextInt(10));
				}
				arrows.put(arrow.getID(), arrow);
				Message message = new Message(MessageType.UPDATE_ARROW, arrow.getID() + Message.EQUALS_SEPARATOR + arrow.stringify());
				gameClient.sendTCP(message);
				message = new Message(MessageType.PERFORM_ACTION, MessageType.REMOVE_ARROW.toString() + Message.EQUALS_SEPARATOR + playerMap.get(getUUIDFromPlayer(player)).getPlayerNumber() );
				gameServer.sendToAllTCP(message);
			}
		}else
		{
			//System.out.println(getMyUser().getPlayerNumber() + " is supposed to get it removed");
			//System.out.println(playerMap.get(getUUIDFromPlayer(getMyPlayer())).getPlayerNumber() + " is then going to be removed");
			Message message = new Message(MessageType.PERFORM_ACTION, MessageType.LAUNCH_ARROW.toString() + Message.EQUALS_SEPARATOR + getMyUser().getPlayerNumber() );
			gameClient.sendTCP(message);
			//getMyPlayer().removeArrow();
		}
		
	}
	
	public void launchArrow(Player p) { launchArrow(p,0); }
	
	/**
	 * adds arrow to player
	 * @param b the arrow
	 * @param p the player
	 */
	public void addArrowToPlayer(Arrow b, Player p)
	{
		p.addArrow(arrows.remove(b.getID()));
		b.setPlayer(p);
		
		//send message to client to update number of arrows
		Message message = new Message(MessageType.PERFORM_ACTION,
				MessageType.ADD_ARROW_TO_PLAYER.toString() + Message.EQUALS_SEPARATOR + 
				playerMap.get(getUUIDFromPlayer(p)).getPlayerNumber() + Message.EQUALS_SEPARATOR + 
				b.getID() + Message.ACCENT_SEPARATOR + b.stringify() );
		
		gameServer.sendToAllTCP(message);
	}
	
	/* ###########################
	 * GENERAL GETTERS AND SETTERS
	 * ###########################
	 */
	
	/**
	 * Gets the list of arrows currently in game
	 * @return list of arrows
	 */
	public ConcurrentHashMap<String,Arrow> getArrows() {
		return arrows;
	}
	
	/**
	 * sets the uuid for this client's player
	 * @param uuid the uuid to set for this client
	 */
	public void setMyUserUUID(String uuid)
	{
		myUUID = uuid;
	}
	
	/**
	 * @return this client
	 */
	public Client getClient()
	{
		return gameClient;
	}
	
	/**
	 * 
	 * @return hosting type (CLIENT or SERVER)
	 */
	public HostType getHostType()
	{
		return hostType;
	}
	
	/**
	 * 
	 * @return this client's player
	 */
	public Player getMyPlayer() {
		return playerList[playerMap.get(myUUID).getPlayerNumber()];
	}
	
	/**
	 * 
	 * @return this client's user data
	 */
	public BattleCastleUser getMyUser() {
		return playerMap.get(myUUID);
	}
	
	/**
	 * 
	 * @param ip the String IP of the server to join
	 */
	public void setServerIP(String ip)
	{
		try {
			serverIP = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			canvasRef.setCurrentState(GameState.MAIN_MENU);
			canvasRef.addError(new Error("IP Formatted Incorrectly: " + ip, 300));
		}
	}
	
	/**
	 * 
	 * @param address the InetAddress of the server to join.
	 */
	public void setServerIP(InetAddress address)
	{
		serverIP = address;
	}
	
	/* ##############
	 * HELPER METHODS
	 * ##############
	 */
	
	/**
	 * Register a user on client and server
	 * @param user the user to register
	 * @param fromServer True if sent by Server, False if sent by a Client
	 */
	public void registerUser(BattleCastleUser user,boolean fromServer)
	{
		System.out.println("Recieved user: " + user);
		if(playerMap.get( user.getUUID() ) == null)
		{
			playerMap.put(user.getUUID(), user);
		}
		System.out.println(playerMap);
		
		if(hostType == HostType.SERVER && !fromServer)
		{
			int playerNum = playerMap.size()-1;
			playerMap.get(user.getUUID()).setPlayerNumber(playerNum);
			canvasRef.addPlayerFace(user.getPlayerName(), canvasRef.getPlayerFacePath(user.getType())); 

			for(String uuid : playerMap.keySet())
			{
				gameServer.sendToAllTCP(playerMap.get(uuid));
			}

			if(mapSelected)
				sendMapChoice(mapType, mapName);
		}

		playerMap.get(user.getUUID()).setPlayerNumber(user.getPlayerNumber());
		playerList[user.getPlayerNumber()] = new Player(ImageFilePaths.TEMP_PLAYER,user.getUUID(),getArrowPathFromPlayer(user)); //TODO Change TEMP PLAYER to whatever player chooses
		playerScoreHandler.registerPlayer(playerList[user.getPlayerNumber()]);
		System.out.println(Arrays.toString(playerList));

		
	}
	
	/**
	 * Process messages sent to the server
	 * @param type the messageType (String) to process
	 * @param restOfMessage the rest of the message
	 */
	public void processServerMessage(String type, String restOfMessage )
	{
		if(type.equals( MessageType.MOVE_PLAYER.toString()) )
		{
			String[] num = restOfMessage.split(Message.EQUALS_SEPARATOR);
			//System.out.println("NUM: " + num[1]);
			int playerNum = Integer.parseInt(num[0]);
			
			if(playerList[playerNum].isDead()) return;
			//System.out.println(KeyHandler.presses);
			
			if(num[1].equals(KeyPress.RIGHT_D.getText())) //move player right
			{
				playerList[playerNum].setvX(2);
			}
			if(num[1].equals(KeyPress.LEFT_D.getText())) //move player left
			{
				playerList[playerNum].setvX(-2);
			}
			if(num[1].equals(KeyPress.JUMP_D.getText())) //move player up
			{
				if(!playerList[playerNum].isJumping())
				{
					playerList[playerNum].setNormalForce(1, 1);
					playerList[playerNum].setExternalForce(0, 0);
					playerList[playerNum].setvY(-7);
					playerList[playerNum].setJumping(true);
					playerList[playerNum].setFalling(false);
				}
			}
			if(num[1].equals(KeyPress.DOWN_D.getText())) //move player down
			{
				playerList[playerNum].setvY(2);
			}
			if(num[1].equals(KeyPress.DASH_L.getText())) //make player dash left
			{
			//	System.out.println("DASHING LEFT");
				playerList[playerNum].setvXSync(-10);
			}
			if(num[1].equals(KeyPress.DASH_R.getText())) //make player dash right
			{
				playerList[playerNum].setvXSync(10);
			}
			
		}else if(type.equals(MessageType.UPDATE_ARROW.toString())) //update location of an arrow
		{
			String[] id = restOfMessage.split(Message.EQUALS_SEPARATOR);
			String uuid = id[0];
			if(arrows.get(uuid)== null)
			{
				String[] items = id[1].split(Arrow.ENTRY_SEPARATOR);
//				System.out.println("hello");
//				System.out.println(Arrays.toString(items));
				arrows.put(uuid,
						new Arrow(
								Integer.parseInt(items[1].split(Arrow.KEY_VALUE_SEPARATOR)[1]),Integer.parseInt(items[2].split(Arrow.KEY_VALUE_SEPARATOR)[1]),
								Double.parseDouble(items[3].split(Arrow.KEY_VALUE_SEPARATOR)[1]), Double.parseDouble(items[4].split(Arrow.KEY_VALUE_SEPARATOR)[1]),
								new Vector(Double.parseDouble(items[6].split(Arrow.KEY_VALUE_SEPARATOR)[1]), Double.parseDouble(items[7].split(Arrow.KEY_VALUE_SEPARATOR)[1]) ),
								0, playerList[playerMap.get(items[5].split(Arrow.KEY_VALUE_SEPARATOR)[1]).getPlayerNumber()],
								items[0].split(Arrow.KEY_VALUE_SEPARATOR)[1],uuid
								));
			}
			
			Message mess = new Message(MessageType.UPDATE_ARROW, arrows.get(uuid).getID() + Message.EQUALS_SEPARATOR + arrows.get(uuid).stringify());
			gameServer.sendToAllTCP(mess);
			
		}else if(type.equals(MessageType.UPDATE_MOUSE_LOC.toString())) //update mouse location of a player
		{
			String[] id = restOfMessage.split(Message.EQUALS_SEPARATOR);
			int playerNum = Integer.parseInt(id[0]);
			if(playerList[playerNum] != null)
				playerList[playerNum].decode(id[1]);
//			System.out.println("recieved new mouse location " + id[1]);
//			System.out.println("player location is " + playerList[playerNum].getMouseLocation());
		}else if(type.equals(MessageType.PERFORM_ACTION.toString())) //perform specific actions (actions that don't occur every tick
		{
			String[] mess = restOfMessage.split(Message.EQUALS_SEPARATOR);
			
			if(mess[0].equals(MessageType.LAUNCH_ARROW.toString()))
			{
				int playerNum = Integer.parseInt(mess[1]);
				launchArrow(playerList[playerNum]);
			//	System.out.println("lAunching Arrow fOR plAyer: " + playerNum );
			}
//			switch(mess[0])
//			{
//			case "launch_a": //launching arrow from client player
//				int playerNum = Integer.parseInt(mess[1]);
//				launchArrow(playerList[playerNum]);
//				System.out.println("lAunching Arrow fOR plAyer: " + playerNum );
//				break;
//			}
		}else if(type.equals(MessageType.DISCONNECT_ONE_USER.toString()))
		{
			disconnectOneUserServerSide(playerMap.get(restOfMessage));
		}
	}
	
	/**
	 * Process message sent to a client
	 * @param type the MessageType as a (String)
	 * @param restOfMessage the rest of the message to process
	 */
	public void processClientMessage(String type, String restOfMessage )
	{
		if(type.equals(MessageType.SELECT_MAP.toString())) //sends which map the server picked
		{
			gameMap = gameMapLoader.getByType(restOfMessage.trim());//new GameMap(messageArr[1].trim());
		}else if(type.equals(MessageType.UPDATE_PLAYER.toString())) //updates player location client side
		{
			String[] num = restOfMessage.split(Message.EQUALS_SEPARATOR);
			int playerNum = Integer.parseInt(num[0]);
			if(playerList[playerNum] != null )
				playerList[playerNum].decode(num[1]);
		}else if(type.equals(MessageType.UPDATE_ARROW.toString())) //initializes arrow location client side
		{
			String[] id = restOfMessage.split(Message.EQUALS_SEPARATOR);
			String uid = id[0];
			if(arrows.get(uid)==null)
			{
				String[] items = id[1].split(Arrow.ENTRY_SEPARATOR);
				arrows.put(uid, new Arrow(
						Integer.parseInt(items[1].split(Arrow.KEY_VALUE_SEPARATOR)[1]),Integer.parseInt(items[2].split(Arrow.KEY_VALUE_SEPARATOR)[1]),
						Double.parseDouble(items[3].split(Arrow.KEY_VALUE_SEPARATOR)[1]), Double.parseDouble(items[4].split(Arrow.KEY_VALUE_SEPARATOR)[1]),
						new Vector(Double.parseDouble(items[6].split(Arrow.KEY_VALUE_SEPARATOR)[1]), Double.parseDouble(items[7].split(Arrow.KEY_VALUE_SEPARATOR)[1]) ),
						0,playerList[playerMap.get(items[5].split(Arrow.KEY_VALUE_SEPARATOR)[1]).getPlayerNumber()],
						items[0].split(Arrow.KEY_VALUE_SEPARATOR)[1],uid)
						);
			}
			
		}else if(type.equals(MessageType.MOVE_ARROW.toString())) //moves arrow on client side
		{
			
			if(hostType == HostType.CLIENT){
				String[] id = restOfMessage.split(Message.EQUALS_SEPARATOR);
				String uuid = id[0];
				Arrow updateThis = arrows.get(uuid);
				if(updateThis != null)
				{
					updateThis.decode(id[1]);	
				}else
				{
					System.out.println("THE ARROW IS NULL: " + uuid);
				}
			}
		}else if(type.equals(MessageType.UPDATE_TILE.toString()))
		{
			if(hostType == HostType.CLIENT)
			{
				String[] id = restOfMessage.split(Message.EQUALS_SEPARATOR);
				String uuid = id[0];
				try{
					gameMap.getTileByID(uuid).execute(id[1]);
				}catch(Exception e)
				{
					
				}
			}
		}else if(type.equals(MessageType.RESET_GAME.toString()))
		{
			if(hostType == HostType.CLIENT)
			{
				arrows.clear();
				reset();
			}
		}else if(type.equals(MessageType.GAME_OVER.toString()))
		{
			if(hostType == HostType.CLIENT)
			{
				gameOver = true;
			}
		}else if(type.equals(MessageType.DISCONNECT_ALL_USERS.toString()))
		{
			if(hostType == HostType.CLIENT)
			{
				disconnectAllUsers();
			}
		}else if(type.equals(MessageType.DISCONNECT_ONE_USER.toString()))
		{
			if(hostType == HostType.CLIENT)
			{
				disconnectOneUserServerSide(playerMap.get(restOfMessage));
			}
		}
		else if(type.equals(MessageType.PERFORM_ACTION.toString())) //perform specific action
		{
			String[] mess = restOfMessage.split(Message.EQUALS_SEPARATOR);
			
			if(mess[0].equals(MessageType.REMOVE_ARROW.toString()))
			{
				if(getHostType().equals(HostType.CLIENT))
				{
					int playerNum = Integer.parseInt(mess[1]);
					//System.out.println(playerNum + "'s arrow is actually being removed" );
					playerList[playerNum].removeArrow();
				}
			}else if(mess[0].equals(MessageType.ADD_ARROW_TO_PLAYER.toString()))
			{
				if(getHostType().equals(HostType.CLIENT))
				{	
					int playerNum = Integer.parseInt(mess[1]);
					String[] id = mess[2].split(Message.ACCENT_SEPARATOR);
					String uid = id[0];
					String[] items = id[1].split(Arrow.ENTRY_SEPARATOR);
					
					///System.out.printf("player num: %d, arrow id: %s",playerNum,uid);
					
					playerList[playerNum].addArrow(new Arrow(
							Integer.parseInt(items[1].split(Arrow.KEY_VALUE_SEPARATOR)[1]),Integer.parseInt(items[2].split(Arrow.KEY_VALUE_SEPARATOR)[1]),
							0 , Double.parseDouble(items[4].split(Arrow.KEY_VALUE_SEPARATOR)[1]), //Double.parseDouble(items[3].split(Arrow.KEY_VALUE_SEPARATOR)[1])
							new Vector(Double.parseDouble(items[6].split(Arrow.KEY_VALUE_SEPARATOR)[1]), Double.parseDouble(items[7].split(Arrow.KEY_VALUE_SEPARATOR)[1]) ),
							0,playerList[playerNum],items[0].split(Arrow.KEY_VALUE_SEPARATOR)[1],uid)
							);
					
					arrows.remove(uid);
					
					//System.out.println(playerList[playerNum].stringify());
					playerList[playerNum].fixArrows(2, playerList[playerNum].getMouseLocation().x, playerList[playerNum].getMouseLocation().y);
				}
			}
		}
	
	}
	
	/**
	 * Gets uuid from a player
	 * @param p Player to get id from
	 * @return uuid if player exists, null if not
	 */
	public String getUUIDFromPlayer(Player p)
	{
		if (p != null)
			for(String s : playerMap.keySet())
			{
				if(playerMap.get(s).getUUID().equals(p.getUUID()))
				{
					return s;
				}
			}
		System.out.println("NO UUID FOUND FOR PLAYER: " + p);
		return null;
	}
	
	public Player getPlayerFromUUID(String uuid)
	{
		for(String s : playerMap.keySet())
		{
			if(playerMap.get(s).getUUID().equals(uuid))
			{
				return playerList[playerMap.get(uuid).getPlayerNumber()];
			}
		}
		return null;
	}

	/**
	 * 
	 * @return number of players in game
	 */
	public int getPlayersInList()
	{
		int count = 0;
		for(int i = 0; i < playerList.length; i++)
			if(playerList[i] != null)
				count++;
		return count;
	}
	
	/**
	 * Kills an alive player
	 * @param p the player to kill
	 */
	public void killPlayer(Player p)
	{
		if(p == null) return;
		
		int index = playerMap.get(p.getUUID()).getPlayerNumber();
		
		while( p.getArrowStorage().size() != 0)
		{
			launchArrow(p,1);
			p.updateCurrentArrowOnDeath();
		}
		
		playerList[index].setDead(true);
		playerList[index].setImage(playerList[index].getDI());
		System.out.println(p + " is now dead");
	}
	
	/**
	 * Determines if round is over when one player or less is alive
	 * @return True if game over, false if not
	 */
	public boolean gameOver()
	{
		int deadCount = 0;
		int aliveCount = 0;
		for(int i = 0; i < playerList.length; i++) 
		{
			if(playerList[i] != null)
			{
				deadCount += playerList[i].isDead()? 1:0;
				aliveCount += playerList[i].isDead() ? 0:1;
			}
		}
		gameOver = aliveCount - deadCount <= MIN_PLAYERS-1;
		return gameOver;
	}
	
	public BufferedImage getPlayerFaceFromPlayer(Player p)
	{
		return canvasRef.getPlayerFaceFromType(playerMap.get(getUUIDFromPlayer(p)).getType());
	}
	
	public String getArrowPathFromPlayer(BattleCastleUser user)
	{
		switch(user.getType())
		{
		case BLUE:  return ImageFilePaths.ARROW_BLUE;
		case GREEN: return ImageFilePaths.ARROW_GREEN;
		case RED:	return ImageFilePaths.ARROW_RED;
		case YELLOW:return ImageFilePaths.ARROW_YELLOW;
		default: return "";		
		}
	}
	
	/* #############
	 * RESETING GAME
	 * #############
	 */
	
	public void reset() //TODO add more things that get reset
	{
		for(int i = 0; i < playerList.length; i++)
		{
			if(playerList[i] != null)
			{
				if(playerList[i].isDead())
					playerList[i].setDead(false);
			}
					
		}
		gameOver = false;
		
		if(hostType == HostType.SERVER)
		{
			int numPlayers = playerMap.size();
			int arrowsMaxPerPlayer = 10;
			for(int i = 0 ;i < numPlayers; i++)
			{
				int playerArrows = playerList[i].getArrowCount();
				int giveToPlayer = arrowsMaxPerPlayer-playerArrows;
				ArrayList<String> arrStr = new ArrayList<String>(arrows.keySet());
				for(int j = 0; j < giveToPlayer; j++)
				{
					Arrow ar = arrows.remove(arrStr.remove(0));
					ar.reset();
					ar.setPlayer(playerList[i]);
					playerList[i].addArrow(ar);
				}
			}	
			Message message = new Message(MessageType.RESET_GAME,"r");
			gameServer.sendToAllUDP(message);
		}
	}
	
	public void disconnect()
	{
		if(hostType == HostType.SERVER)
			disconnectAllUsers();
		else
			disconnectOneUserClientSide(getMyUser());
	}
	
	public void disconnectOneUserServerSide(BattleCastleUser user)
	{
		if(hostType == HostType.SERVER){
			Message message = new Message(MessageType.DISCONNECT_ONE_USER,user.getUUID());
			gameServer.sendToAllUDP(message);
		}
		
		playerMap.remove(user.getUUID());
		playerList[user.getPlayerNumber()] = null;
	}
	
	public void disconnectOneUserClientSide(BattleCastleUser user)
	{
		Message message = new Message(MessageType.DISCONNECT_ONE_USER,user.getUUID());
		gameClient.sendUDP(message);
		
		clearAllUsers();
	}
	
	public void disconnectAllUsers()
	{
		if(hostType == HostType.SERVER){
			Message message = new Message(MessageType.DISCONNECT_ALL_USERS,"d");
			gameServer.sendToAllUDP(message);
		}

		clearAllUsers();
	}
	
	private void clearAllUsers()
	{
		playerMap.clear();
		for(int i = 0; i < playerList.length; i++)
		{
			if(playerList[i] != null)
				playerList[i] = null;
		}
	}
	
	/**
	 * stops the server if hosting
	 */
	public void stopServer()
	{
		if(gameServer != null)
			gameServer.stop();
	}	
	
	/**
	 * stops client
	 */
	public void stopClient()
	{
		if(gameClient != null)
			gameClient.stop();
	}
}

