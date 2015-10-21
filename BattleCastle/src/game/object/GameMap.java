package game.object;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import utility.Utility;

public class GameMap {
	
	private BufferedImage background;
	private Point[] playerStartLocations;
	
	public GameMap(String imageName)
	{
		background = Utility.loadImage(imageName);
	}
	
	public void render(Graphics g)
	{
		if(background != null)
			g.drawImage(background, 0, 0, null);
	}
	
	public void tick()
	{
		
	}
	
	public String toString()
	{
		return "";
	}

	public Point getPlayerStartPoint(int playerNum) {
		switch(playerNum)
		{
		case 0: return new Point(32,32);
		case 1: return new Point(128,64);
		case 2: return new Point(256,32);
		case 3: return new Point(128,128);
		}
		return null;
	}
	
	public static GameMap map1, map2, map3;
	
	static
	{
		map1 = new GameMap("");
		map2 = new GameMap("");
		map3 = new GameMap("");
	}
}
