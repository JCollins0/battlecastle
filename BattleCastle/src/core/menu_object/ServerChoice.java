package core.menu_object;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.InetAddress;

import utility.Utility;
import core.constants.ImageFilePaths;

public class ServerChoice {

	private InetAddress address;
	private Rectangle bounds;
	public static final int HEIGHT = 64;
	public static final int WIDTH = 256;
	public static final Font IP_FONT = new Font("Courier New",Font.PLAIN,30);
	public static final BufferedImage image = Utility.loadImage(ImageFilePaths.SERVER_CHOICE);
	
	public ServerChoice(int x, int y, InetAddress address )
	{
		bounds = new Rectangle(x,y,WIDTH,HEIGHT);
		this.address = address;
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
	public void tick()
	{
		
	}
	
	public void render(Graphics g)
	{
		if (image != null)
		{
			g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
		}else{
			g.setColor(Color.red);
			g.drawRect(bounds.x,bounds.y,bounds.width,bounds.height);
		}
		g.setColor(Color.black);
		g.setFont(IP_FONT);
		g.drawString(address.getHostAddress(), bounds.x+bounds.width/2-g.getFontMetrics(IP_FONT).charsWidth(address.getHostAddress().toCharArray(), 0, address.getHostAddress().length())/2, bounds.y + bounds.height/2+15);
	}
	
	public void setY(int y)
	{
		bounds.y = y;
	}
	
	public int getY()
	{
		return bounds.y;
	}
	
	public String getAddress()
	{
		return address.getHostAddress();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ServerChoice)
		{
			ServerChoice choice = (ServerChoice)o;
			return choice.address.equals(address);
		}
		return false;
	}
}
