package game;

import java.awt.Graphics;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TreeMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.BattleCastleCanvas;
import core.HostType;
import game.object.MapType;
import game.player.BattleCastleUser;
import game.player.Player;

public class Game2 {
	
	private static final int SERVER_PORT = 25565;
	
	private Server gameServer;
	private Client gameClient;
	private BattleCastleCanvas canvasRef;
	private HostType hostType;
	private InetAddress serverIP;
	private TreeMap<String, BattleCastleUser> playerMap;
	private Player[] playerList;
	private String myUUID;
	
	public Game2(BattleCastleCanvas canvasRef, HostType hostType)
	{
		this.canvasRef = canvasRef;
		this.hostType = hostType;
		playerList = new Player[4];
		playerMap = new TreeMap<String, BattleCastleUser>();
		
		if(hostType == HostType.SERVER)
		{
			startServer();
		}
		startClient();	
		registerClasses();
	}
	
	private void startServer()
	{
		
		try {
			serverIP = InetAddress.getLocalHost();
			gameServer = new Server();
			gameServer.start();
			gameServer.bind(SERVER_PORT);
			gameServer.addListener(new Listener() {
				public void received (Connection connection, Object object) {
					if (object instanceof BattleCastleUser) {
						System.out.println("Recieved user");
						BattleCastleUser user = (BattleCastleUser)object;
						if(playerMap.get( user.getUUID() ) == null)
						{
							
							playerMap.put(user.getUUID(), user);
						}
						int playerNum = playerMap.size()-1;
						user.setPlayerNumber(playerNum);
						gameServer.sendToAllTCP(playerMap);
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startClient()
	{
		try {
			gameClient = new Client();
			gameClient.start();
			gameClient.connect(5000, serverIP, SERVER_PORT);
			gameClient.addListener(new Listener(){
				
				public void received (Connection connection, Object object) {
					if (object instanceof TreeMap<?, ?>) {
						playerMap = (TreeMap<String, BattleCastleUser>)object;
					}
					else if (object instanceof BattleCastleUser)
					{
						BattleCastleUser user = (BattleCastleUser)object;
						if(playerMap.get( user.getUUID() ) == null)
						{
							
							playerMap.put(user.getUUID(), user);
						}
						playerMap.get(user.getUUID()).setPlayerNumber(user.getPlayerNumber());
						gameServer.sendToAllTCP(playerMap);
					}
			       }
			});
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void registerClasses() //register classes in order to send them 
	{
		Kryo serverRegistry = gameServer.getKryo();
			serverRegistry.register(BattleCastleUser.class);
			serverRegistry.register(TreeMap.class);
		Kryo clientRegistry = gameClient.getKryo();
			clientRegistry.register(BattleCastleUser.class);
			clientRegistry.register(TreeMap.class);
			
	}
	
	public void sendUserData(BattleCastleUser user)
	{
		playerMap.put(user.getUUID(), user);
		gameClient.sendTCP(user);
	}
	
	public void tick()
	{
		
	}
	
	public void render(Graphics g)
	{
		
	}
	
	public void setServerIP(String ip)
	{
		try {
			serverIP = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
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
		
	}
	
	public void setMyUserUUID(String uuid)
	{
		myUUID = uuid;
	}

	
}

