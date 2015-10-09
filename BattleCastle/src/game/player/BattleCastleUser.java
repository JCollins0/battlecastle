package game.player;

import java.net.InetAddress;
import java.util.Arrays;

public class BattleCastleUser {
	
	private String playerName;
//	private InetAddress address;
	private int port;
	private String uniqueID;
	private int playerNum;
	private boolean connected;
	
	public BattleCastleUser(){}
	
	public BattleCastleUser(String name, InetAddress address, int port)
	{
		this.playerName = name;
//		this.address = address;
		this.port = port;
		uniqueID = String.join("",address.getHostAddress().split("\\."));
		//System.out.println(uniqueID);

	}
	
	public String getPlayerName() {
		return playerName;
	}

//	public InetAddress getAddress() {
//		return address;
//	}

	public int getPort() {
		return port;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BattleCastleUser)
		{
			return uniqueID.equals( ((BattleCastleUser)obj).uniqueID );
		}
		return false;
	}
	
	public String getUUID()
	{
		return uniqueID;
	}
	
	public void setPlayerNumber(int number)
	{
		this.playerNum = number;
	}
	
	public int getPlayerNumber()
	{
		return playerNum;
	}
	
	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}
	
	public boolean getConnected()
	{
		return connected;
	}
	
	
	@Override
	public String toString() {
		return "BattleCastlePlayer[name=" + playerName + 
								 ",uuid=" + getUUID() + 
//								 ",ip=" + address.getHostAddress().toString() + 
								 ",playerNum=" + playerNum + "]";
	}
}
