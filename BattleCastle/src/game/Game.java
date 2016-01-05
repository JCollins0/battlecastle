package game;

import game.message.Message;
import game.message.MessageType;
import game.object.GameMap;
import game.object.MapType;
import game.physics.CollisionDetector;
import game.physics.Polygon;
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
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.BattleCastleCanvas;
import core.Error;
import core.GameState;
import core.HostType;
import core.KeyHandler;
import core.KeyPress;
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
	private GameMap gameMap;
	private static final int MIN_PLAYERS = 1;
	
	public Game(BattleCastleCanvas canvasRef, HostType hostType)
	{
		this.canvasRef = canvasRef;
		this.hostType = hostType;
		playerList = new Player[4];
		playerMap = new TreeMap<String, BattleCastleUser>();
		arrows = new ConcurrentHashMap<String,Arrow>();
		collideDetect = new CollisionDetector();

		if(hostType == HostType.SERVER)
		{
			startServer();
			startClient();
			connectToServer();
		}
		if(hostType == HostType.CLIENT)
			startClient();
		System.setProperty("java.net.preferIPv4Stack" , "true");
	}

	/**
	 * Initializes Server
	 */
	private void startServer()
	{
		
		try {
			serverIP = InetAddress.getLocalHost();
			gameServer = new Server(65536, 16384);
			gameServer.getKryo().setRegistrationRequired(false);
			
			gameServer.start();
			gameServer.bind(SERVER_PORT,SERVER_UDP);
			gameServer.addListener(new Listener() {
				public void received (Connection connection, Object object) {
					if (object instanceof BattleCastleUser) {
						
						BattleCastleUser user = (BattleCastleUser)object;
						System.out.println("Recieved user: " + user);
						if(playerMap.get( user.getUUID() ) == null)
						{
							playerMap.put(user.getUUID(), user);
						}
						int playerNum = playerMap.size()-1;
						playerMap.get(user.getUUID()).setPlayerNumber(playerNum);
						System.out.println(playerMap);
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
							if(num[1].equals(KeyPress.RIGHT_D.getText()))
							{
								playerList[playerNum].setvX(2);
							}
							if(num[1].equals(KeyPress.LEFT_D.getText()))
							{
								playerList[playerNum].setvX(-2);
							}
							if(num[1].equals(KeyPress.JUMP_D.getText()))
							{
								playerList[playerNum].setvY(-2);
							}
							if(num[1].equals(KeyPress.DOWN_D.getText()))
							{
								playerList[playerNum].setvY(2);
							}
							
						}else if(type.equals(MessageType.UPDATE_ARROW.toString()))
						{
							String[] id = messageArr[1].split("=");
							String uuid = id[0];
							if(arrows.get(uuid)== null)
							{
								String[] items = id[1].split(",");
								System.out.println("hello");
								System.out.println(Arrays.toString(items));
								arrows.put(uuid, new Arrow(Integer.parseInt(items[1].split(":")[1]),Integer.parseInt(items[2].split(":")[1]),Double.parseDouble(items[3].split(":")[1]),
										Double.parseDouble(items[4].split(":")[1]),null,0,playerList[playerMap.get(items[5].split(":")[1]).getPlayerNumber()],items[0].split(":")[1],uuid));
							}
							
							Message mess = new Message(MessageType.UPDATE_ARROW, arrows.get(uuid).getID() + "=" + arrows.get(uuid).stringify());
							gameServer.sendToAllTCP(mess);
							
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
		gameClient = new Client(65536, 16384);
		gameClient.getKryo().setRegistrationRequired(false);
		gameClient.start();
		gameClient.addListener(new Listener(){
			
			public void received (Connection connection, Object object) {
//					System.out.println("CLIENT RECEIVED OBJECT - " + object.toString());
				if (object instanceof BattleCastleUser)
				{
					BattleCastleUser user = (BattleCastleUser)object;
					if(playerMap.get( user.getUUID() ) == null)
					{
						playerMap.put(user.getUUID(), user);
					}
					playerMap.get(user.getUUID()).setPlayerNumber(user.getPlayerNumber());
					playerList[user.getPlayerNumber()] = new Player(user.getUUID());
					
				}else if(object instanceof Message)
				{
					Message messageOb = (Message)object;
					String message = messageOb.toString();
					String[] messageArr = message.split(Message.SEPARATOR);
					String type = messageArr[0].trim();
					if(type.equals(MessageType.SELECT_MAP.toString()))
					{
						gameMap = GameMap.map1;//new GameMap(messageArr[1].trim());
					}else if(type.equals(MessageType.UPDATE_PLAYER.toString()))
					{
						String[] num = messageArr[1].split("=");
						playerList[Integer.parseInt(num[0])].decode(num[1]);
					}else if(type.equals(MessageType.UPDATE_ARROW.toString()))
					{
						String[] id = messageArr[1].split("=");
						String uid = id[0];
						if(arrows.get(uid)==null)
						{
							String[] items = id[1].split(",");
							arrows.put(uid, new Arrow(Integer.parseInt(items[1].split(":")[1]),Integer.parseInt(items[2].split(":")[1]),Double.parseDouble(items[3].split(":")[1]),
									Double.parseDouble(items[4].split(":")[1]),null,0,playerList[playerMap.get(items[5].split(":")[1]).getPlayerNumber()],items[0].split(":")[1],uid));
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
					}
					
					List<Arrow> plist = Collections.list(Collections.enumeration(arrows.values()));
					List<Polygon> list = new ArrayList<Polygon>();
					list.addAll(plist);
					collideDetect.broadCheck(list);
				}
			}
		}else if(hostType == HostType.CLIENT)
		{
			if(canvasRef.getCurrentState() == GameState.GAMEPLAY)
			{
				if(playerMap.size() >= MIN_PLAYERS)
				{					
					for(Arrow arrow : arrows.values())
					{
						arrow.tick();
					}
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
		
		for(int i = 0; i < KeyHandler.presses.size(); i++)
		{
			g.setColor(Color.darkGray);
			g.drawString(KeyHandler.presses.get(i).getText(), 100, i * 64 + 15 );
		}
			
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
			gameMap = GameMap.map1;//new GameMap(mapType.getBackground());

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
				canvasRef.addError(new Error("Error Updating Player [Game.class -Line 429]",300));
			}
			
		}
		
		KeyHandler.presses.remove(KeyPress.LEFT_U);
		KeyHandler.presses.remove(KeyPress.RIGHT_U);
	}
	
	/**
	 * sends arrow information to server
	 */
	public void launchArrow()
	{
		Player player = getMyPlayer();
		Arrow arrow = player.removeArrow();
		
		if(arrow != null)
		{
			arrow.addVelocity();
			arrows.put(arrow.getID(), arrow);
//			for(Arrow a : arrows.values())
//			{
				Message message = new Message(MessageType.UPDATE_ARROW, arrow.getID() + "=" + arrow.stringify());
				gameClient.sendTCP(message);
//			}
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
			if(playerMap.get(s).equals(p))
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
	
	
}

