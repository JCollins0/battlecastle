package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class Player {
	
	public static final int HEIGHT = 64;
	public static final int WIDTH = 32;
	
	private Rectangle bounds;
	private double vX, vY;
	private static final double GRAVITY = 9.8;
		
	public Player()
	{
		bounds = new Rectangle(-WIDTH,-HEIGHT,WIDTH,HEIGHT);
	}
	
	public void setLocation(int x, int y)
	{
		bounds.x = x;
		bounds.y = y;
	}
	
	public void setLocation(Point p)
	{
		setLocation(p.x,p.y);
	}
	
	public String getPlayerInformation()
	{
		return String.format("X:%d Y:%d W:%d H:%d", bounds.x,bounds.y,bounds.width,bounds.height);
	}
	
	public void tick()
	{
		vY += GRAVITY / 100;
		if ( vY > GRAVITY )
			vY = GRAVITY;
		bounds.y += vY;
		
		bounds.x += vX;
	}
	
	public void render(Graphics g)
	{
		g.setColor(Color.cyan);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		g.setColor(Color.black);
		g.drawString(String.format("(%d,%d)", bounds.x, bounds.y ), bounds.x, bounds.y-5);
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
}
