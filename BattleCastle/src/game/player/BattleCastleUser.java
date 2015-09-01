package game.player;

import java.net.InetAddress;
import java.util.Arrays;

public class BattleCastleUser {
	
	private String playerName;
	private InetAddress address;
	private int port;
	private String uniqueID;
	
	public BattleCastleUser(String name, InetAddress address, int port)
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
		if(obj instanceof BattleCastleUser)
		{
			return address.equals( ((BattleCastleUser)obj).address );
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
