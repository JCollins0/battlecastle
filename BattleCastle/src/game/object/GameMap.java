package game.object;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utility.Utility;

public class GameMap {
	
	private BufferedImage background;
	
	public GameMap()
	{
		background = Utility.loadImage("background");
	}
	
	public void render(Graphics g)
	{
		if(background != null)
			g.drawImage(background, 0, 0, null);
	}
	
	public void tick()
	{
		
	}
}