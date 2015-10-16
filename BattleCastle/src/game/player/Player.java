package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import utility.Utility;

public class Player {
	
	public static final int HEIGHT = 64;
	public static final int WIDTH = 32;
	
	private Rectangle bounds;
	private double vX, vY;
	private static final double GRAVITY = 9.8;
	private BufferedImage image;

	public Player()
	{
		this((BufferedImage)null);
	}
	
	public Player(BufferedImage image)
	{
		bounds = new Rectangle(-WIDTH,-HEIGHT,WIDTH,HEIGHT);
		this.image = image;
	}
	
	public Player(String imageFileName)
	{
		this(Utility.loadImage(imageFileName));
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
		if(image != null)
		{
			g.drawImage(image, bounds.x, bounds.y, bounds.width,bounds.height,null);
		}else
		{
			g.setColor(Color.cyan);
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		
		g.setColor(Color.black);
		g.drawString(String.format("(%d,%d)", bounds.x, bounds.y ), bounds.x, bounds.y-5);
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
	public String stringify()
	{
		return String.format("X:%d,Y:%d,W:%d,H:%d",
					bounds.x,bounds.y,bounds.width,bounds.height
					);
	}
	
	public void decode(String s)
	{
		String[] items = s.split(",");
		for(String item : items)
		{
			String[] key_value = item.split(":");
			switch(key_value[0])
			{
			case "X": bounds.x = Integer.parseInt(key_value[1]);
				break;
			case "Y": bounds.y = Integer.parseInt(key_value[1]);
				break;
			case "W": bounds.width = Integer.parseInt(key_value[1]);
				break;
			case "H": bounds.height = Integer.parseInt(key_value[1]);
				break;
			case "F": //falling = Boolean.parseBoolean(key_value[1]); 
				break;
			case "J": //jumping = Boolean.parseBoolean(key_value[1]);
				break;
			case "S": //used in animation?
				break;
				
			}
		}
	}
}

