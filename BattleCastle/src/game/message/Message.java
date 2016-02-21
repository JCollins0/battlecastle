package game.message;

public class Message {

	/**
	 * separates message type from the rest of the message
	 */
	public static final String MESSAGE_TYPE_SEPARATOR = "~";
	/**
	 * Usually separates id or number of something with specified action
	 */
	public static final String EQUALS_SEPARATOR = "=";
	/**
	 * separates Entries in objects 
	 */
	public static final String COMMA_SEPARATOR = ",";
	/**
	 * separates K:V pairs
	 */
	public static final String COLON_SEPARATOR = ":";
	/**
	 * used in player class to separate Entries
	 */
	public static final String LESS_THAN_CHEVRON_SEPARATOR = "<";
	/**
	 * Used in player class to separate K#V pairs
	 */
	public static final String POUND_SEPARATOR = "#";
	/**
	 * Used to pickup arrows
	 */
	public static final String ACCENT_SEPARATOR = "`";
	
	
	private String text;
	
	public Message()
	{
		
	}
	
	public Message(MessageType option, String text)
	{
		this.text = option + MESSAGE_TYPE_SEPARATOR + text;
	}
	
	public String toString()
	{
		return text;
	}
}
