package game.message;

public enum MessageType {

	CHANGE_STATE("state_change"),
	START_ROUND("start_round"),
	SELECT_MAP("select_map"),
	UPDATE_PLAYER("update_player"),
	MOVE_PLAYER("move_player"),
	UPDATE_ARROW("update_arrow"),
	MOVE_ARROW("move_arrow"),
	UPDATE_TILE("update_tile"),
	UPDATE_MOUSE_LOC("upadte_mouse_loc"),
	PERFORM_ACTION("perform_action"),
	LAUNCH_ARROW("launch_arrow"),
	REMOVE_ARROW("remove_arrow"),
	ADD_ARROW_TO_PLAYER("add_arrow_to_player"),
	RESET_GAME("reset_game"),
	GAME_OVER("game_over"),
	DISCONNECT_ALL_USERS("disconnect_all"),
	DISCONNECT_ONE_USER("disconnect_one");
	
	
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
