package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import utility.Utility;

public class Arrow {

	public static final int WIDTH = 16, HEIGHT = 8;
	private Rectangle bounds;
	private double vX, vY;
	private Color color; 
	
	public Arrow(int x, int y, double vX, double vY)
	{
		bounds = new Rectangle(x,y,WIDTH,HEIGHT);
		color = Utility.randomRGBColor();
	}
	
	public String stringify()
	{
		return "";
	}
	
	public void decode(String text)
	{
		
	}
	
	public void render(Graphics g)
	{
		g.setColor(color);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public void tick()
	{
		
	}
}
