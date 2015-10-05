package game;

import java.awt.Graphics;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.TreeMap;

import core.BattleCastleCanvas;
import core.HostType;
import game.object.GameMap;
import game.object.MapType;
import game.player.BattleCastleUser;
import game.player.Player;

public class Game {

	public static final int SERVER_PORT = 25565;
	public static final int CLIENT_PORT = 25566;
	
	private BattleCastleCanvas canvasRef;
	
	private ServerThread serverThread;
	private ClientThread clientThread;
	
	private GameMap gameMap;
	private boolean loadedMap;
	private boolean allPlayersConnected;
	private boolean objectLocationsSet;
	private HostType type;
	
	private static final int MIN_PlAYERS = 2;
	
	public Game(BattleCastleCanvas canvasRef, HostType type)
	{
		this.canvasRef = canvasRef;
		this.type = type;
		playerMap = new TreeMap<String, BattleCastleUser>();
		playerList = new Player[4];
		
		/*
		 * Server specific stuff
		 */
		if (type == HostType.SERVER)
		{
			try {
				serverSocket = new DatagramSocket(SERVER_PORT);
				System.out.printf("Server- Receive: %d, Send: %d \n",serverSocket.getReceiveBufferSize(),serverSocket.getSendBufferSize());
				serverSocket.setReceiveBufferSize(16384);
				serverSocket.setSendBufferSize(16384);
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
			System.out.printf("Client- Receive: %d, Send: %d \n",clientSocket.getReceiveBufferSize(),clientSocket.getSendBufferSize());
			clientSocket.setReceiveBufferSize(16384);
			clientSocket.setSendBufferSize(16384);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		clientThread = new ClientThread();
		Thread clientTask = new Thread(clientThread);
		clientTask.start();	
	}
	
	public BattleCastleCanvas getCanvas()
	{
		return canvasRef;
	}
	
	public void render(Graphics g)
	{
		if(playerMap.size() >= MIN_PlAYERS) //player map on client side is not full
		{
//			System.out.println("GameMap null? " + gameMap == null);
			if (gameMap != null)
			{
				gameMap.render(g);
				
				if(objectLocationsSet)
				{
					for(int i = 0; i < playerList.length; i++)
					{
						if(playerList[i] != null)
						{
							playerList[i].render(g);
						}
					}
				}
				
			}
		}
	}
	
	public void tick()
	{
		if(type == HostType.SERVER) //server stuff
		{
			System.out.println(allPlayersConnected());
			if(playerMap.size() >= MIN_PlAYERS && allPlayersConnected())
			{
				//System.out.println(playerMap.size());			
				if(!loadedMap)
				{
					//Send map number
					loadedMap =  true;
				}else
				{
					//send data to other clients
					if(!objectLocationsSet)
					{
						System.out.println("playerList.length: " + playerList.length);
						for(int i = 0; i < playerList.length; i++)
						{
							if(playerList[i] != null)
							{
								playerList[i].setLocation(i * 32 + 32, 32);
							}
						}
						objectLocationsSet = true;
					}else
					{
						for(int i = 0; i < playerList.length; i++)
						{
							if(playerList[i] != null)
							{
								playerList[i].tick();
							}
						}
					}
					
				}
				//update objects
				
				
				
				
			}else
			{
				if(playerMap.size() >= MIN_PlAYERS && allPlayersConnected())
				{
					//send all players recieved
					String sendData = (char)ServerOption.CONFIRM_STATE_MESSAGE.ordinal() + " " + myUUID +"-connected";
					sendToServerPacket = new DatagramPacket(
							sendData.getBytes(),
							sendData.length());
					sendPacketToServer(sendToServerPacket);
				}else
				{
					System.out.println("Sending User info to all clients");
					//send user data to all
					for(String id : playerMap.keySet())
					{
						String sendData = (char)ClientOption.REGISTER_USERS.ordinal() + " " + playerMap.get(id).toString();
						sendToClientPacket = new DatagramPacket(
								sendData.getBytes(),
								sendData.length());
						sendPacketToAll(sendToClientPacket);
					}
				}
			}
		}
		//client stuff
		if (type == HostType.CLIENT)
		{
			if(playerMap.size() >= MIN_PlAYERS && !allPlayersConnected)
			{
				//send all players recieved
				String sendData = (char)ServerOption.CONFIRM_STATE_MESSAGE.ordinal() + " " + myUUID +"-connected";
				sendToServerPacket = new DatagramPacket(
						sendData.getBytes(),
						sendData.length());
				sendPacketToServer(sendToServerPacket);
				allPlayersConnected = true;
			}else
			{
				if(loadedMap)
				{
					if(!objectLocationsSet)
					{
						for(int i = 0; i < playerList.length; i++)
						{
							if(playerList[i] != null)
							{
								playerList[i].setLocation(i * 32 + 32, 32);
							}
						}
						objectLocationsSet = true;
					}else
					{
						for(int i = 0; i < playerList.length; i++)
						{
							if(playerList[i] != null)
							{
								playerList[i].tick();
							}
						}
					}
				}
			}
		}
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
			if(!uuid.equals(myUUID) )
			{
				BattleCastleUser user = new BattleCastleUser(name,serverReceivePacket.getAddress(), CLIENT_PORT);
				user.setPlayerNumber(playerNum);
				playerMap.put(user.getUUID(), user);
				playerList[user.getPlayerNumber()] = new Player();

				System.out.println("SERVER RECEIVED DATA: " + user.toString());
				System.out.println("playerMap Contents:  " + playerMap);
			}
			
			
/*			send data to other players
			//System.out.println("Player Map: " + playerMap.toString());
			try
			{
				for(String id : playerMap.keySet())
				{
					String sendData = (char)ClientOption.REGISTER_USERS.ordinal() + " " + playerMap.get(id).toString();
					sendToClientPacket = new DatagramPacket(
							sendData.getBytes(),
							sendData.length());
					sendPacketToAll(sendToClientPacket);
				}
				
				for(int t= 0; t < 10; t++)
				{
					ArrayList<String> pMapList = new ArrayList<String>(playerMap.keySet());
					for(int i = 0; i < pMapList.size(); i++)
					{

						String id = pMapList.get(i);
						for(int j = 0; j < pMapList.size(); j++)
						{

							String oid = pMapList.get(j);

							System.out.println("SENDING  " + playerMap.get(id).toString() + " DATA TO " + playerMap.get(oid).getAddress());
							String sendData = (char)ClientOption.REGISTER_USERS.ordinal() + " " + playerMap.get(id).toString();

							sendToClientPacket = new DatagramPacket(
									sendData.getBytes(),
									sendData.length(),
									playerMap.get(oid).getAddress(),
									CLIENT_PORT);

							serverSocket.send(sendToClientPacket);
						}
					}
				}
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
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
					
					Thread.sleep(100);
				}catch(Exception e)
				{
					e.printStackTrace();
				}.
 */
			
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
					sendToClientPacket = new DatagramPacket(removeData.getBytes(),
													removeData.length(),
													playerMap.get(id).getAddress(),
													playerMap.get(id).getPort());
					clientSocket.send(sendToClientPacket);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			break;
		case MAP_SELECTION:
			
			
			//Not Sure if this needs to be used because server will be the one choosing
			
			break;
		case CONFIRM_STATE_MESSAGE:
			
			String message = new String(data, 1, length-1);
			System.out.println("Player is connected: " + message);
			uuid = message.trim().split("-")[0];
			String state = message.trim().split("-")[1];
			System.out.println(uuid);
			switch(state)
			{
			case "connected": playerMap.get(uuid).setConnected(true);
				break;
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
		ClientOption cOption = ClientOption.values()[code];
		
		switch(cOption)
		{
		case REGISTER_USERS:
			
			String userData = new String(data, 1, length - 1);
			String name = userData.substring(userData.indexOf("name=")+5,userData.indexOf(",uuid")).trim();
			String uuid = userData.substring(userData.indexOf("uuid=")+5,userData.indexOf(",ip=")).trim();
			String address = userData.substring(userData.indexOf("ip=")+3,userData.indexOf(",playerNum=")).trim();
			String playerNum = userData.substring(userData.indexOf("playerNum=")+10,userData.length()-1).trim();
			try
			{
				if(getHostType() != HostType.SERVER) //!uuid.equals(myUUID.trim())
				{
					BattleCastleUser user = new BattleCastleUser(name,InetAddress.getByName(address),CLIENT_PORT);
					user.setPlayerNumber(Integer.parseInt(playerNum));
					playerMap.put(uuid, user);

					playerList[user.getPlayerNumber()] = new Player();
					//	System.out.println("CLIENT RECIEVED DATA: " + user.toString());
					System.out.println("Client Player Map Size: " + playerMap.size());
					//	System.out.println("Player Map Contents: " + playerMap);
				}else
				{
					playerMap.get(myUUID).setPlayerNumber(Integer.parseInt(playerNum));
				}
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
		case LOAD_MAP:
			
			String mapData = new String(data, 1, length - 1);
			System.out.println("MAP DATA: " +mapData);
			if (getHostType() != HostType.SERVER)
			{
				gameMap = new GameMap(mapData.trim());
				System.out.println(gameMap.toString());
				loadedMap = true;
			}
			
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
		playerMap.put(user.getUUID(), user);
//		System.out.println("Player Map: " + playerMap.toString());
		myUUID = user.getUUID();
		try
		{
			String data = (char)ServerOption.LOGIN_USER.ordinal() + " " + user.toString();
			sendToServerPacket = new DatagramPacket(data.getBytes(),
											data.length(),
											serverIP,
											SERVER_PORT);
			clientSocket.send(sendToServerPacket);
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
			sendToServerPacket = new DatagramPacket(data.getBytes(),
											data.length(),
											serverIP,
											SERVER_PORT);
			clientSocket.send(sendToServerPacket);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendMapChoice(MapType type)
	{
		try {
			gameMap = new GameMap(type.getText());
			String mapName = type.getText();
			String data = (char)ClientOption.LOAD_MAP.ordinal() + " " + mapName;
			//
			while(playerMap.size() < MIN_PlAYERS) //turn into a timer
			{
				Thread.sleep(100);
			}
			
			Thread.sleep(100);
			
			for(String id : playerMap.keySet())
			{	
				sendToClientPacket = new DatagramPacket(
						data.getBytes(),
						data.length(),
						playerMap.get(id).getAddress(),
						CLIENT_PORT);
				serverSocket.send(sendToClientPacket);
				System.out.println("SENDING MAP DATA TO: " + playerMap.get(id).getAddress());
				Thread.sleep(1);
			}
			
			//
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendPacketToAll(DatagramPacket packet)
	{
//		for(int i = 0; i < 5; i++)
//		{
			ArrayList<String> pMapList = new ArrayList<String>(playerMap.keySet());
			for(int j = 0; j < pMapList.size(); j++)
			{
				String oid = pMapList.get(j);
				System.out.println("SENDING DATA TO " + playerMap.get(oid).getAddress());

				packet.setAddress(playerMap.get(oid).getAddress());
				packet.setPort(CLIENT_PORT);

				try {
					serverSocket.send(sendToClientPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//		}
		
	}
	
	private void sendPacketToServer(DatagramPacket packet)
	{	
		try {
			packet.setAddress(serverIP);
			packet.setPort(SERVER_PORT);
			clientSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean allPlayersConnected()
	{
		boolean connected = true;
		for(String uuid : playerMap.keySet())
		{
			connected = playerMap.get(uuid).getConnected() && connected;
//			System.out.println("Player " + uuid + " is connect: " + playerMap.get(uuid).getConnected());
		}
		return connected && playerMap.size() >= MIN_PlAYERS;
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
		return type;
	}
	
	public Player getMyPlayer() {
		return playerList[playerMap.get(myUUID).getPlayerNumber()];
	}
	
	public BattleCastleUser getMyUser() {
		return playerMap.get(myUUID);
	}
	
	private TreeMap<String, BattleCastleUser> playerMap;
	private DatagramPacket clientReceivePacket;
	private DatagramPacket serverReceivePacket;
	private DatagramSocket serverSocket;
	private DatagramSocket clientSocket;
	private DatagramPacket sendToServerPacket;
	private DatagramPacket sendToClientPacket;
	private InetAddress serverIP;
	private String myUUID;
	
	private Player[] playerList;

	

	
}
