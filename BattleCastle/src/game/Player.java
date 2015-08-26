package game;

import java.net.InetAddress;
import java.util.Arrays;

public class Player {
	
	private String playerName;
	private InetAddress address;
	private int port;
	private String uniqueID;
	
	public Player(String name, InetAddress address, int port)
	{
		this.playerName = name;
		this.address = address;
		this.port = port;
		uniqueID = String.join("",address.getHostAddress().split("\\."));
		//System.out.println(uniqueID);

	}
	
	public String getPlayerName() {
		return playerName;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Player)
		{
			return address.equals( ((Player)obj).address );
		}
		return false;
	}
	
	public String getUUID()
	{
		return uniqueID;
	}
	
	@Override
	public String toString() {
		return "BattleCastlePlayer [address=" + address + ":" + port + "]";
	}
}
