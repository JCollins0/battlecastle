package game.message;

public enum MessageType {

	CHANGE_STATE("state_change"),
	START_ROUND("start_round");
	
	private String value;
	MessageType(String value)
	{
		this.value = value;
	}
	public String toString()
	{
		return value;
	}
	
}
