package game.message;

public enum MessageType {

	CHANGE_STATE("state_change"),
	START_ROUND("start_round"),
	SELECT_MAP("select_map"),
	UPDATE_PLAYER("update_player");
	
	private String value;
	MessageType()
	{
		
	}
	
	MessageType(String value)
	{
		this.value = value;
	}
	public String toString()
	{
		return value;
	}
	
}
