package game.message;

public class Message {

	public static final String SEPARATOR = "~";
	public static final String SEPARATOR_0 = "=";
	public static final String SEPARATOR_1 = ",";
	public static final String SEPARATOR_2 = ":";
	public static final String SEPARATOR_3 = "<";
	public static final String SEPARATOR_4 = "#";
	public static final String SEPARATOR_5 = "`";
	
	
	private String text;
	
	public Message()
	{
		
	}
	
	public Message(MessageType option, String text)
	{
		this.text = option + SEPARATOR + text;
	}
	
	public String toString()
	{
		return text;
	}
}
