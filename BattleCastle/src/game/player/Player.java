package game.player;

import java.awt.Rectangle;

public class Player {
	
	public static final int HEIGHT = 64;
	public static final int WIDTH = 32;
	
	private Rectangle bounds;
	
	public Player()
	{
		bounds = new Rectangle(-WIDTH,-HEIGHT,WIDTH,HEIGHT);
	}
	
	public void setLocation(int x, int y)
	{
		bounds.x = x;
		bounds.y = y;
	}
	
	public String getPlayerInformation()
	{
		return String.format("X:%d Y:%d W:%d H:%d", bounds.x,bounds.y,bounds.width,bounds.height);
	}
	
	public void tick()
	{
		
	}
	
	public void render()
	{
		
	}
}
