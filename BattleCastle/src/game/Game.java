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

	private static final int PORT = 25565;
	
	private BattleCastleCanvas canvasRef;
	
	private ServerThread serverThread;
	private ClientThread clientThread;
	
	
	private Player player;
	
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
			
			playerMap = new TreeMap<String, BattleCastleUser>();
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
		
		String output = new String(data,0,length);
		System.out.println(output);
	}
	
	public void processClientPacket()
	{
//		System.out.println("ProcessClient");
		byte[] data = clientReceivePacket.getData();
		int length = clientReceivePacket.getLength();
		
		String output = new String(data,0,length);
		System.out.println(output);
	}
	
	public void sendPacketToClients(DatagramPacket send)
	{
		
	}
	
	public void sendPacketToServer(DatagramPacket send2)
	{
			String send = "Test Packet";
			sendPacket = new DatagramPacket(send.getBytes(), send.length(), serverIP, PORT );
			System.out.println(serverIP.toString()  + " " + PORT + " send: " + send );
			try {
				clientSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
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
		playerMap.put(user.getUUID(), user);
		
		try
		{
			String data = user.toString();
			sendPacket = new DatagramPacket(data.getBytes(),
											data.length(),
											serverIP,
											PORT);
			clientSocket.send(sendPacket);
		}catch(Exception e)
		{
			
		}
	}
	
	private TreeMap<String, BattleCastleUser> playerMap;
	private DatagramPacket clientReceivePacket;
	private DatagramPacket serverReceivePacket;
	private DatagramSocket serverSocket;
	private DatagramSocket clientSocket;
	private DatagramPacket sendPacket;
	private InetAddress serverIP;
}
