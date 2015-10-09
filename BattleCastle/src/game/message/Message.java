package game.message;

public class Message {

	private String text;
	
	public Message(MessageType option, String text)
	{
		this.text = option + ":" + text;
	}
	
	public String toString()
	{
		return text;
	}
}
