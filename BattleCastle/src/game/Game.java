package game;

import java.awt.Graphics;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.TreeMap;

import core.BattleCastleCanvas;
import core.HostType;

public class Game {

	private static final int PORT = 25565;
	
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
			
			playerMap = new TreeMap<String, Player>();
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
	
	public void processServerPacket()
	{
//		System.out.println("ProcessServer");
	}
	
	public void processClientPacket()
	{
//		System.out.println("ProcessClient");
	}
	
	public void sendPacketToClients(DatagramPacket send)
	{
		
	}
	
	public void sendPacketToServer(DatagramPacket send)
	{
		
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
					receivePacket = new DatagramPacket( data, data.length );
					//receive packet from socket
					serverSocket.receive(receivePacket);
					//process packet
					processServerPacket();

				}catch(Exception e)
				{
					e.printStackTrace();
					System.exit(1);
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
					receivePacket = new DatagramPacket(data,data.length);
					clientSocket.receive(receivePacket);
					processClientPacket();
					
				}catch(Exception e){
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}
	
	private TreeMap<String, Player> playerMap;
	private DatagramPacket receivePacket;
	private DatagramSocket serverSocket;
	private DatagramSocket clientSocket;
	private DatagramPacket sendPacket;
}
