package game.object;

import core.constants.ImageFilePaths;

public enum MapType {
	ONE(ImageFilePaths.MAP_1_BACKGROUND),
	TWO(ImageFilePaths.MAP_2_BACKGROUND),
	THREE(ImageFilePaths.MAP_3_BACKGROUND),
	CUSTOM("CUSTOM");
	
	
	MapType(String background)
	{
		this.background = background;
	}
	
	private String background;
	public String getBackground()
	{
		return background;
	}
}
