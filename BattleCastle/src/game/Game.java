package game;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.TreeMap;

import core.BattleCastleCanvas;
import core.HostType;
import game.object.GameMap;
import game.player.BattleCastleUser;
import game.player.Player;
import utility.Utility;

public class Game {

	public static final int SERVER_PORT = 25565;
	public static final int CLIENT_PORT = 25566;
	
	private BattleCastleCanvas canvasRef;
	
	private ServerThread serverThread;
	private ClientThread clientThread;
	
	private GameMap gameMap;
	private boolean loadedMap;
	private HostType type;
	
	private static final int MIN_PlAYERS = 2;
	
	public Game(BattleCastleCanvas canvasRef, HostType type)
	{
		this.canvasRef = canvasRef;
		this.type = type;
		 
		/*
		 * Server specific stuff
		 */
		if (type == HostType.SERVER)
		{
			try {
				serverSocket = new DatagramSocket(SERVER_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
			
			serverThread = new ServerThread();
			Thread serverTask = new Thread(serverThread);
			serverTask.start();
		}
				
		/*
		 * Client stuff
		 */
		try {
			clientSocket = new DatagramSocket(CLIENT_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		clientThread = new ClientThread();
		Thread clientTask = new Thread(clientThread);
		clientTask.start();
		
		playerMap = new TreeMap<String, BattleCastleUser>();
		playerList = new Player[4];
	}
	
	public BattleCastleCanvas getCanvas()
	{
		return canvasRef;
	}
	
	public void render(Graphics g)
	{
		
		if(playerMap.size() >= MIN_PlAYERS)
		{
			
		}
	}
	
	public void tick()
	{
		if(type == HostType.SERVER) //server stuff
		{
			if(playerMap.size() >= MIN_PlAYERS )
			{
				if(!loadedMap)
				{
					//Send map number
					loadedMap =  true;
				}
				
				//update objects
				
			}
		}

		//client stuff
	}
	
	public void reset()
	{
		
	}
	
	public void processServerPacket()
	{
//		System.out.println("ProcessServer");
		byte[] data = serverReceivePacket.getData();
		int length = serverReceivePacket.getLength();
		
		byte code = data[0];
//		System.out.println("CODE: " + code);
//		System.out.println("ORDINAL: " + ServerOption.LOGIN_USER.ordinal());
		ServerOption sOption = ServerOption.values()[code];
		
		switch(sOption)
		{
		case LOGIN_USER:
			
			String userData = new String(data, 1, length - 1);
			String name = userData.substring(userData.indexOf("name=")+5,userData.indexOf(",uuid")).trim();
			String uuid = userData.substring(userData.indexOf("uuid=")+5,userData.indexOf(",ip=")).trim();

			int playerNum = playerMap.size();
			
			BattleCastleUser user = new BattleCastleUser(name,serverReceivePacket.getAddress(), CLIENT_PORT);
			user.setPlayerNumber(playerNum);
			playerMap.put(user.getUUID(), user);
			playerList[user.getPlayerNumber()] = new Player();
			
			System.out.println("SERVER RECEIVED DATA: " + user.toString());
			//send data to other players
//			System.out.println("Player Map: " + playerMap.toString());
			for(String id : playerMap.keySet())
				for(String oid : playerMap.keySet())
				try
				{
					System.out.println("SENDING  " + playerMap.get(id).toString() + " DATA TO " + playerMap.get(oid).getAddress());
					String sendData = (char)ClientOption.REGISTER_USERS.ordinal() + " " + playerMap.get(id).toString();
					
					sendPacket = new DatagramPacket(
								sendData.getBytes(),
								sendData.length(),
								playerMap.get(oid).getAddress(),
								CLIENT_PORT);
					
					serverSocket.send(sendPacket);
					
					Thread.sleep(1);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			
			break;
		case LOGOUT_USER:
			
			userData = new String(data, 1, length - 1);
			uuid = userData.substring(userData.indexOf("uuid=")+5,userData.indexOf(",ip="));
			playerMap.remove(uuid);
			for(String id : playerMap.keySet())
			{
				try
				{
					String removeData = (char)ClientOption.REMOVE_USER.ordinal() + " " + uuid;
					sendPacket = new DatagramPacket(removeData.getBytes(),
													removeData.length(),
													playerMap.get(id).getAddress(),
													playerMap.get(id).getPort());
					clientSocket.send(sendPacket);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			break;
		}		
		
	}
	
	public void processClientPacket()
	{
//		System.out.println("ProcessClient");
		byte[] data = clientReceivePacket.getData();
		int length = clientReceivePacket.getLength();
		
		byte code = data[0];
		ClientOption sOption = ClientOption.values()[code];
		
		switch(sOption)
		{
		case REGISTER_USERS:
			
			String userData = new String(data, 1, length - 1);
			String name = userData.substring(userData.indexOf("name=")+5,userData.indexOf(",uuid")).trim();
			String uuid = userData.substring(userData.indexOf("uuid=")+5,userData.indexOf(",ip=")).trim();
			String address = userData.substring(userData.indexOf("ip=")+3,userData.indexOf(",playerNum=")).trim();
			String playerNum = userData.substring(userData.indexOf("playerNum=")+10,userData.length()-1).trim();
			try
			{
				BattleCastleUser user = new BattleCastleUser(name,InetAddress.getByName(address),CLIENT_PORT);
				user.setPlayerNumber(Integer.parseInt(playerNum));
				playerMap.put(uuid, user);
				
				playerList[user.getPlayerNumber()] = new Player();
//				System.out.println("CLIENT RECIEVED DATA: " + user.toString());
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			break;
		case REMOVE_USER:
			
			userData = new String(data, 1, length - 1);
			uuid = userData.substring(userData.indexOf("uuid=")+5,userData.indexOf(",ip="));
			playerMap.remove(uuid);
			
			System.out.println("REMOVED USER");
			break;
		default:
			break;
		}
	}
	
	private class ServerThread implements Runnable{
				
		@Override
		public void run() {
			
			while(true)
			{
//				System.out.println("ServerRunning?");
				try{
					//create array to store data
					byte[] data = new byte[100];
					//create receive packet
					serverReceivePacket = new DatagramPacket( data, data.length );
					//receive packet from socket
					serverSocket.receive(serverReceivePacket);
					//process packet
					processServerPacket();

				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private class ClientThread implements Runnable
	{
		@Override
		public void run() {
			while(true)
			{
//				System.out.println("ClientRunning?");
				try
				{
					byte[] data = new byte[100];
					clientReceivePacket = new DatagramPacket(data,data.length);
					clientSocket.receive(clientReceivePacket);
					processClientPacket();
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
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
	
	public void sendUserData(BattleCastleUser user)
	{
//		myUUID = user.getUUID();
//		playerMap.put(user.getUUID(), user);
//		System.out.println("Player Map: " + playerMap.toString());
		try
		{
			String data = (char)ServerOption.LOGIN_USER.ordinal() + " " + user.toString();
			sendPacket = new DatagramPacket(data.getBytes(),
											data.length(),
											serverIP,
											SERVER_PORT);
			clientSocket.send(sendPacket);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendRemoveUserData()
	{
		try
		{
			String data = (char)ServerOption.LOGOUT_USER.ordinal() + " " + playerMap.get(myUUID).toString();
			sendPacket = new DatagramPacket(data.getBytes(),
											data.length(),
											serverIP,
											SERVER_PORT);
			clientSocket.send(sendPacket);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private TreeMap<String, BattleCastleUser> playerMap;
	private DatagramPacket clientReceivePacket;
	private DatagramPacket serverReceivePacket;
	private DatagramSocket serverSocket;
	private DatagramSocket clientSocket;
	private DatagramPacket sendPacket;
	private InetAddress serverIP;
	private String myUUID;
	
	private Player[] playerList;
}
