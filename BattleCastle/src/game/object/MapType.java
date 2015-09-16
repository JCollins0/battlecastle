package game.object;

public enum MapType {
	ONE("map1"),
	TWO("map2"),
	THREE("map3");
	
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
