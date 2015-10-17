package game.message;

public class Message {

	public static final String SEPARATOR = "~";
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
