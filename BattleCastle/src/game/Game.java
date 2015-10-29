package game;

import java.awt.Graphics;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TreeMap;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.BattleCastleCanvas;
import core.Error;
import core.GameState;
import core.HostType;
import core.KeyPress;
import core.constants.ImageFilePaths;
import game.message.Message;
import game.message.MessageType;
import game.object.GameMap;
import game.object.MapType;
import game.player.BattleCastleUser;
import game.player.Player;

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
	private String myUUID;
	private GameMap gameMap;
	private static final int MIN_PLAYERS = 1;
	
	public Game(BattleCastleCanvas canvasRef, HostType hostType)
	{
		this.canvasRef = canvasRef;
		this.hostType = hostType;
		playerList = new Player[4];
		playerMap = new TreeMap<String, BattleCastleUser>();
		
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
								playerList[playerNum].setvX(4);
							}else if(num[1].equals(KeyPress.LEFT_D.getText()))
							{
								playerList[playerNum].setvX( -4);
							}
							else if(num[1].equals(KeyPress.JUMP_D.getText()))
							{
								playerList[playerNum].setvY(-4);
							}
						}
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
					playerList[user.getPlayerNumber()] = new Player();
					
				}else if(object instanceof Message)
				{
					Message messageOb = (Message)object;
					String message = messageOb.toString();
					String[] messageArr = message.split(Message.SEPARATOR);
					String type = messageArr[0].trim();
					if(type.equals(MessageType.SELECT_MAP.toString()))
					{
						gameMap = new GameMap(messageArr[1].trim());
					}else if(type.equals(MessageType.UPDATE_PLAYER.toString()))
					{
						String[] num = messageArr[1].split("=");
						playerList[Integer.parseInt(num[0])].decode(num[1]);
					}
					
				}
			}
		});
	}
	
	public void connectToServer()
	{
		try {
			gameClient.connect(5000, serverIP, SERVER_PORT, SERVER_UDP);
		} catch (Exception e) {
			canvasRef.setCurrentState(GameState.MAIN_MENU);
			canvasRef.addError(new Error("Could Not Connect To Specified IP Address", 300));
		}
	}
	
	public void sendUserData(BattleCastleUser user)
	{
		playerMap.put(user.getUUID(), user);
		playerMap.get(user.getUUID()).setConnected(true);
		gameClient.sendTCP(user);
	}
	
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


				}
			}
		}
	}
	
	public void render(Graphics g)
	{
		if(gameMap != null)
			gameMap.render(g);
		
		for(int i = 0; i < playerList.length; i++)
			if(playerList[i] != null)
				playerList[i].render(g);
	}
	
	public void setServerIP(String ip)
	{
		try {
			serverIP = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			canvasRef.setCurrentState(GameState.MAIN_MENU);
			canvasRef.addError(new Error("IP Formatted Incorrectly: " + ip, 300));
		}
	}
	
	public void setServerIP(InetAddress address)
	{
		serverIP = address;
	}
	
	public int getPlayersInList()
	{
		int count = 0;
		for(int i = 0; i < playerList.length; i++)
			if(playerList[i] != null)
				count++;
		return count;
	}
	
	public HostType getHostType()
	{
		return hostType;
	}
	
	public Player getMyPlayer() {
		return playerList[playerMap.get(myUUID).getPlayerNumber()];
	}
	
	public BattleCastleUser getMyUser() {
		return playerMap.get(myUUID);
	}
	
	public void sendMapChoice(MapType mapType)
	{
		gameMap = GameMap.map1;//new GameMap(mapType.getBackground());
		
		//load players and then send to clients
		for(int i = 0; i < playerMap.size(); i++)
		{
			playerList[i] = new Player(ImageFilePaths.TEMP_PLAYER);
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
	
	public void updateMyPlayer(KeyPress press)
	{
		
		Message message = new Message(MessageType.MOVE_PLAYER,
				playerMap.get(myUUID).getPlayerNumber() + "=" + press.getText() );
		gameClient.sendTCP(message);
	}
	
	public void setMyUserUUID(String uuid)
	{
		myUUID = uuid;
	}
	
	public Client getClient()
	{
		return gameClient;
	}

	public void stopServer()
	{
		if(gameServer != null)
			gameServer.stop();
	}	
	
	public void stopClient()
	{
		if(gameClient != null)
			gameClient.stop();
	}
	
}

