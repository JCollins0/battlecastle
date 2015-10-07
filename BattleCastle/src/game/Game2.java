package game;

import java.io.IOException;
import java.net.InetAddress;
import java.util.TreeMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.BattleCastleCanvas;
import core.HostType;
import game.player.BattleCastleUser;

public class Game2 {
	
	private static final int SERVER_PORT = 25565;
	
	private Server gameServer;
	private Client gameClient;
	private BattleCastleCanvas canvasRef;
	private HostType hostType;
	private InetAddress serverIP;
	private TreeMap<String, BattleCastleUser> playerMap;
	
	public Game2(BattleCastleCanvas canvasRef, HostType hostType)
	{
		this.canvasRef = canvasRef;
		this.hostType = hostType;
		
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
	
	public void registerUser(BattleCastleUser user)
	{
		gameClient.sendTCP(user);
	}
	

}

