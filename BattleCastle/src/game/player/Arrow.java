package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import utility.Utility;

public class Arrow {

	public static final int WIDTH = 32, HEIGHT = 16;
	private Rectangle bounds;
	private double vX, vY;
	private Color color; 
	private Player shotByPlayer;
	
	public Arrow()
	{
		bounds = new Rectangle(0,0,WIDTH,HEIGHT);
		color = Utility.randomRGBColor();
	}
	
	public String stringify()
	{
		return "";
	}
	
	public void decode(String text)
	{
		String[] items = text.split(",");
		for(String item : items)
		{
			String[] key_value = item.split(":");
			switch(key_value[0])
			{
			case "ImageFile":
				break;
			case "X": bounds.x = Integer.parseInt(key_value[1]);
				break;
			case "Y": bounds.y = Integer.parseInt(key_value[1]);
				break;
			case "W": bounds.width = Integer.parseInt(key_value[1]);
				break;
			case "H": bounds.height = Integer.parseInt(key_value[1]);
				break;
			case "rotation": //used for rotating maybe? 
				break;
			case "S": //used in animation?
				break;				
			}
		}
	}
	
	public void render(Graphics g)
	{
		g.setColor(color);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public void fix(int x, int y, double vX, double vY)
	{
		bounds.x = x;
		bounds.y = y;
	}
	
	public void tick()
	{
		
	}
}
