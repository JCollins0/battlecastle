package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import utility.Utility;

public class Arrow {

	public static final int WIDTH = 32, HEIGHT = 16d;
	private Rectangle bounds;
	private double vX, vY;
	private Color color; 
	
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
