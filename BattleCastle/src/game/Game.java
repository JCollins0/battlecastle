package game;

import java.awt.Graphics;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.TreeMap;

import core.BattleCastleCanvas;
import core.HostType;
import game.player.BattleCastleUser;
import game.player.Player;

public class Game {

	public static final int PORT = 25565;
	
	private BattleCastleCanvas canvasRef;
	
	private ServerThread serverThread;
	private ClientThread clientThread;
	
	public Game(BattleCastleCanvas canvasRef, HostType type)
	{
		this.canvasRef = canvasRef;
		
		/*
		 * Server specific stuff
		 */
		if (type == HostType.SERVER)
		{
			try {
				serverSocket = new DatagramSocket(PORT);
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
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		clientThread = new ClientThread();
		Thread clientTask = new Thread(clientThread);
		clientTask.start();
		
		playerMap = new TreeMap<String, BattleCastleUser>();
	}
	
	public BattleCastleCanvas getCanvas()
	{
		return canvasRef;
	}
	
	public void render(Graphics g)
	{
		
	}
	
	public void tick()
	{
		
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
		System.out.println("CODE: " + code);
		System.out.println("ORDINAL: " + ServerOption.LOGIN_USER.ordinal());
		ServerOption sOption = ServerOption.values()[code];
		
		switch(sOption)
		{
		case LOGIN_USER:
			
			String userData = new String(data, 1, length - 1);
			String name = userData.substring(userData.indexOf("name=")+5,userData.indexOf(",uuid")).trim();
			String uuid = userData.substring(userData.indexOf("uuid=")+5,userData.length()-1).trim();
		
			BattleCastleUser user = new BattleCastleUser(name,serverReceivePacket.getAddress(),serverReceivePacket.getPort());
			playerMap.put(uuid, user);
			
			//send data to other players
			for(String id : playerMap.keySet())
				try
				{
					String sendData = (char)ClientOption.REGISTER_USERS.ordinal() + " " + playerMap.get(id).toString();
					
					sendPacket = new DatagramPacket(
								sendData.getBytes(),
								sendData.length(),
								playerMap.get(id).getAddress(),
								playerMap.get(id).getPort());
					
					serverSocket.send(sendPacket);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			
			break;
		case LOGOUT_USER:
			
			userData = new String(data, 1, length - 1);
			uuid = userData.substring(userData.indexOf("uuid=")+5,userData.length()-1);
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
			String uuid = userData.substring(userData.indexOf("uuid=")+5,userData.length()-1).trim();
		
			BattleCastleUser user = new BattleCastleUser(name,clientReceivePacket.getAddress(),clientReceivePacket.getPort());
			playerMap.put(uuid, user);
			System.out.println("CLIENT RECIEVED DATA: " + user.toString());
			break;
		case REMOVE_USER:
			
			userData = new String(data, 1, length - 1);
			uuid = userData.substring(userData.indexOf("uuid=")+5,userData.length()-1);
			playerMap.remove(uuid);
			
			System.out.println("REMOVED USER");
			break;
		default:
			break;
		}
	}
	
//	public void sendPacketToClients(DatagramPacket send)
//	{
//		
//	}
//	
//	public void sendPacketToServer(DatagramPacket send2)
//	{
//
//	}
	
	private class ServerThread implements Runnable{
				
		@Override
		public void run() {
			
			while(true)
			{
				System.out.println("ServerRunning?");
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
				System.out.println("ClientRunning?");
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
	
	public void sendUserData(BattleCastleUser user)
	{
		myUUID = user.getUUID();
		playerMap.put(user.getUUID(), user);
		
		try
		{
			String data = (char)ServerOption.LOGIN_USER.ordinal() + " " + user.toString();
			sendPacket = new DatagramPacket(data.getBytes(),
											data.length(),
											serverIP,
											PORT);
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
											PORT);
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
	
}
