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
	
	public GameMap(String imageName, Point[] playerLocations)
	{
		this(imageName);
		this.playerStartLocations = playerLocations;
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
		if(playerStartLocations == null)
		{
			System.out.println("ADD SOME PLAYER LOCATIONS: " );
			return new Point(-64,-64);
		}
		
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
		map1 = new GameMap("", new Point[]{new Point(),new Point(),new Point(),new Point()});
		map2 = new GameMap("", new Point[]{new Point(),new Point(),new Point(),new Point()});
		map3 = new GameMap("", new Point[]{new Point(),new Point(),new Point(),new Point()});
	}
}
