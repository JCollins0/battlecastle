package game.object;

public enum MapType {
	ONE("First"),
	TWO("Second"),
	THREE("Third");
	
	MapType(String s)
	{
		text = s;
	}
	
	private String text;
	public String getText()
	{
		return text;
	}
}
