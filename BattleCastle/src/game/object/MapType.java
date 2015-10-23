package game.object;

import core.constants.ImageFilePaths;

public enum MapType {
	ONE(ImageFilePaths.MAP_1),
	TWO(ImageFilePaths.MAP_2),
	THREE(ImageFilePaths.MAP_3);
	
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
