package game;

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

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

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

public class Game {
	
	public static final int SERVER_PORT = 25565;
	public static final int SERVER_UDP = 25566;
	public static final int CLIENT_PORT = 25564;
	
	private Server gameServer;
	private Client gameClient;
	private BattleCastleCanvas canvasRef;
	private HostType hostType;
	private InetAddress serverIP;
	private TreeMap<String, BattleCastleUser> playerMap;
	private Player[] playerList;
	private ConcurrentHashMap<String,Arrow> arrows;
	
	private CollisionDetector collideDetect;
	
	private String myUUID;
	private GameMap gameMap, gameMapLoader;
	private static final int MIN_PLAYERS = 1;
	
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
		
		gameMapLoader = new GameMap();
	}

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
						System.out.println("Recieved user: " + user);
						if(playerMap.get( user.getUUID() ) == null)
						{
							playerMap.put(user.getUUID(), user);
						}
						int playerNum = playerMap.size()-1;
						playerMap.get(user.getUUID()).setPlayerNumber(playerNum);
						System.out.println(playerMap);
						
						canvasRef.addPlayerFace(user.getPlayerName(), ImageFilePaths.CHECK); //TODO: Change to acutal face of player depending on player number
						
						for(String uuid : playerMap.keySet())
						{
							gameServer.sendToAllTCP(playerMap.get(uuid));
						}
						
					}else if(object instanceof Message)
					{
						Message messageOb = (Message)object;
						String message = messageOb.toString();
						String[] messageArr = message.split(Message.SEPARATOR);
						String type = messageArr[0].trim();
						if(type.equals( MessageType.MOVE_PLAYER.toString()) )
						{
							String[] num = messageArr[1].split("=");
							//System.out.println("NUM: " + num[1]);
							int playerNum = Integer.parseInt(num[0]);
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
								playerList[playerNum].setvY(-2);
							}
							if(num[1].equals(KeyPress.DOWN_D.getText())) //move player down
							{
								playerList[playerNum].setvY(2);
							}
							if(num[1].equals(KeyPress.DASH.getText())) //make player dash
							{
								
							}
							
						}else if(type.equals(MessageType.UPDATE_ARROW.toString())) //update location of an arrow
						{
							String[] id = messageArr[1].split("=");
							String uuid = id[0];
							if(arrows.get(uuid)== null)
							{
								String[] items = id[1].split(",");
//								System.out.println("hello");
//								System.out.println(Arrays.toString(items));
								arrows.put(uuid,
										new Arrow(
												Integer.parseInt(items[1].split(":")[1]),Integer.parseInt(items[2].split(":")[1]),
												Double.parseDouble(items[3].split(":")[1]), Double.parseDouble(items[4].split(":")[1]),
												new Vector(Double.parseDouble(items[6].split(":")[1]), Double.parseDouble(items[7].split(":")[1]) ),0, playerList[playerMap.get(items[5].split(":")[1]).getPlayerNumber()],
												items[0].split(":")[1],uuid
												));
							}
							
							Message mess = new Message(MessageType.UPDATE_ARROW, arrows.get(uuid).getID() + "=" + arrows.get(uuid).stringify());
							gameServer.sendToAllTCP(mess);
							
						}else if(type.equals(MessageType.UPDATE_MOUSE_LOC.toString())) //update mouse location of a player
						{
							String[] id = messageArr[1].split("=");
							int playerNum = Integer.parseInt(id[0]);
							if(playerList[playerNum] != null)
								playerList[playerNum].decode(id[1]);
//							System.out.println("recieved new mouse location " + id[1]);
//							System.out.println("player location is " + playerList[playerNum].getMouseLocation());
						}else if(type.equals(MessageType.PERFORM_ACTION.toString())) //perform specific actions (actions that don't occur every tick
						{
							String[] mess = messageArr[1].split("=");
							switch(mess[0])
							{
							case "launch_a": //launching arrow from client player
								
								int playerNum = Integer.parseInt(mess[1]);
								launchArrow(playerList[playerNum]);
								
								break;
							}
						}
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
					if(playerMap.get( user.getUUID() ) == null)
					{
						playerMap.put(user.getUUID(), user);
					}
					playerMap.get(user.getUUID()).setPlayerNumber(user.getPlayerNumber());
					playerList[user.getPlayerNumber()] = new Player(user.getUUID());
					
					System.out.println(Arrays.toString(playerList));
					
				}else if(object instanceof Message) //Receiving things that happen every tick
				{
					Message messageOb = (Message)object;
					String message = messageOb.toString();
					String[] messageArr = message.split(Message.SEPARATOR);
					String type = messageArr[0].trim();
					if(type.equals(MessageType.SELECT_MAP.toString())) //sends which map the server picked
					{
						gameMap = gameMapLoader.getByType(messageArr[1].trim());//new GameMap(messageArr[1].trim());
					}else if(type.equals(MessageType.UPDATE_PLAYER.toString())) //updates player location client side
					{
						String[] num = messageArr[1].split("=");
						playerList[Integer.parseInt(num[0])].decode(num[1]);
					}else if(type.equals(MessageType.UPDATE_ARROW.toString())) //initializes arrow location client side
					{
						String[] id = messageArr[1].split("=");
						String uid = id[0];
						if(arrows.get(uid)==null)
						{
							String[] items = id[1].split(",");
							arrows.put(uid, new Arrow(
									Integer.parseInt(items[1].split(":")[1]),Integer.parseInt(items[2].split(":")[1]),
									Double.parseDouble(items[3].split(":")[1]), Double.parseDouble(items[4].split(":")[1]),
									new Vector(Double.parseDouble(items[6].split(":")[1]), Double.parseDouble(items[7].split(":")[1]) ),0,playerList[playerMap.get(items[5].split(":")[1]).getPlayerNumber()],
									items[0].split(":")[1],uid)
									);
						}
						
					}else if(type.equals(MessageType.MOVE_ARROW.toString())) //moves arrow on client side
					{
						
						if(hostType == HostType.CLIENT){
							String[] id = messageArr[1].split("=");
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
					}else if(type.equals(MessageType.PERFORM_ACTION.toString())) //perform specific action
					{
						String[] mess = messageArr[1].split("=");
						switch(mess[0])
						{
						case "remove_a": //remove server player arrow from client side users
							
							if(getHostType().equals(HostType.CLIENT))
							{
								int playerNum = Integer.parseInt(mess[1]);
								playerList[playerNum].removeArrow();
							}
							break;
						case "add_arrow_player":
							if(getHostType().equals(HostType.CLIENT))
							{
								int playerNum = Integer.parseInt(mess[1]);
								String[] id = mess[2].split("separator");
								String uid = id[0];
								String[] items = id[1].split(",");
								
								
								playerList[playerNum].addArrow(new Arrow(
										Integer.parseInt(items[1].split(":")[1]),Integer.parseInt(items[2].split(":")[1]),
										Double.parseDouble(items[3].split(":")[1]), Double.parseDouble(items[4].split(":")[1]),
										new Vector(Double.parseDouble(items[6].split(":")[1]), Double.parseDouble(items[7].split(":")[1]) ),0,playerList[playerMap.get(items[5].split(":")[1]).getPlayerNumber()],
										items[0].split(":")[1],uid)
										);
								
								arrows.remove(uid);
								
								
							}
						}
					}
					
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
	 * @param user the user to login to server
	 */
	public void sendUserData(BattleCastleUser user)
	{
		playerMap.put(user.getUUID(), user);
		playerMap.get(user.getUUID()).setConnected(true);
		gameClient.sendTCP(user);
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
				if(gameMap != null)
					gameMap.tick();

				if(playerMap.size() >= MIN_PLAYERS)
				{
					for(int i = 0; i < playerList.length; i++)
						if(playerList[i] != null)
						{
							playerList[i].tick();
							Message message = new Message(MessageType.UPDATE_PLAYER,i + "=" + playerList[i].stringify());
							gameServer.sendToAllTCP(message);
						}
					
					for(Arrow arrow : arrows.values())
					{
						arrow.tick();
						Message message = new Message(MessageType.MOVE_ARROW,arrow.getID() + "=" + arrow.stringify());
						gameServer.sendToAllTCP(message);
					}
					
					List<Arrow> plist = Collections.list(Collections.enumeration(arrows.values()));
					DoubleLinkedList<Tile> tlist = gameMap.getTiles();
					List<Polygon> list = new ArrayList<Polygon>();
					
//					System.out.println("Start");
					for(Tile t : tlist)
					{
						list.add(t);
						
					}
//					System.out.println(list);
//					System.out.println("Finish");
					
					list.addAll(plist);
					for(int i = 0; i < playerList.length && playerList[i] != null; i++)
						list.add(playerList[i]);
					collideDetect.broadCheck(list);
				}
			}
		}
		
		updateMyPlayer();
	}
	
	/**
	 * draws everything to screen
	 * @param g Graphics for drawing
	 */
	public void render(Graphics g)
	{
		if(gameMap != null)
			gameMap.render(g);
		
		for(int i = 0; i < playerList.length; i++)
			if(playerList[i] != null)
				playerList[i].render(g);
		
		for(Arrow arrow : arrows.values())
		{
			arrow.render(g);
		}
		
//		Iterator<KeyPress> kPi = KeyHandler.presses.iterator();
//		int i = 0;
//		while(kPi.hasNext())
//		{
//			g.setColor(Color.darkGray);
//			g.drawString(kPi.next().getText(), 100, i * 64 + 15 );
//			i++;
//		}
//		for(int i = 0; i < KeyHandler.presses.size(); i++)
//		{
//			
//		}
			
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
	
	public void addArrowToPlayer(Arrow b, Player p)
	{
		p.addArrow(arrows.remove(b.getID()));
		
		//send message to client to update number of arrows
		Message message = new Message(MessageType.PERFORM_ACTION,
				"add_arrow_player" + "=" + 
				playerMap.get(getUUIDFromPlayer(p)).getPlayerNumber() + "=" + 
				b.getID() + "separator" + b.stringify() );
		
		gameServer.sendToAllTCP(message);
	}
	
	/**
	 * 
	 * @param mapType the type of map to load
	 * @param mapName the name only used if custom map
	 */
	public void sendMapChoice(MapType mapType, String mapName)
	{
		
		if(mapType.equals(MapType.CUSTOM) )
		{
			//JOptionPane.showMessageDialog(null, "loading customLevel" + canvasRef.getCustomLevels().get(mapName));
			gameMap = canvasRef.getCustomLevels().get(mapName);
			for(int i = 0; i < playerMap.size(); i++)
			{
				playerList[i] = new Player(ImageFilePaths.TEMP_PLAYER,getUUIDFromPlayer(playerList[i]));
				//playerList[i].setLocation(gameMap.getPlayerStartPoint(i));
			}
		}
		else
		{
			gameMap = gameMapLoader.getByType(mapType.getBackground());//new GameMap(mapType.getBackground());
			
			//load players and then send to clients
			for(int i = 0; i < playerMap.size(); i++)
			{
				playerList[i] = new Player(ImageFilePaths.TEMP_PLAYER,getUUIDFromPlayer(playerList[i]));
				playerList[i].setLocation(gameMap.getPlayerStartPoint(i));
			}
			Message message = new Message(MessageType.SELECT_MAP, mapType.getBackground());
			gameServer.sendToAllTCP(message);
			for(int i = 0; i < playerList.length; i++)
			{
				if(playerList[i] != null)
				{
					message = new Message(MessageType.UPDATE_PLAYER,i + "=" + playerList[i].stringify());
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
				Message message = new Message(MessageType.MOVE_PLAYER,
					playerMap.get(myUUID).getPlayerNumber() + "=" + KeyHandler.presses.get(i).getText() );
				gameClient.sendTCP(message);
			}catch(Exception e)
			{
				canvasRef.addError(new Error("Error Updating Player [Game.class]",300));
			}
			
		}
		
		KeyHandler.presses.remove(KeyPress.LEFT_U);
		KeyHandler.presses.remove(KeyPress.RIGHT_U);
	}
	
	/**
	 * sends mouse x,y coordinates to the server to point the player
	 * arrow to the correct point
	 */
	public void sendMouseLocation() {
		Message message = new Message(MessageType.UPDATE_MOUSE_LOC,
				playerMap.get(myUUID).getPlayerNumber() + "=" + String.format("MouseX#%d<MouseY#%d",MouseHandler.mouse.x,MouseHandler.mouse.y) );
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
	public void launchArrow(Player player)
	{
		
		if(hostType.equals(HostType.SERVER))
		{
			Arrow arrow = player.removeArrow();
			
			if(arrow != null)
			{
				arrow.addVelocity();
				arrows.put(arrow.getID(), arrow);
				Message message = new Message(MessageType.UPDATE_ARROW, arrow.getID() + "=" + arrow.stringify());
				gameClient.sendTCP(message);
				message = new Message(MessageType.PERFORM_ACTION, "remove_a" + "=" +getMyUser().getPlayerNumber() );
				gameServer.sendToAllTCP(message);
			}
		}else
		{
			Message message = new Message(MessageType.PERFORM_ACTION, "launch_a" + "=" +getMyUser().getPlayerNumber() );
			gameClient.sendTCP(message);
			getMyPlayer().removeArrow();
		}
		
	}
	
	/**
	 * 
	 * @param p Player to get id from
	 * @return uuid if player exists, null if not
	 */
	public String getUUIDFromPlayer(Player p)
	{
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
	
	/**
	 * 
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

	public ConcurrentHashMap<String,Arrow> getArrows() {
		// TODO Auto-generated method stub
		return arrows;
	}

	
	
	
}

