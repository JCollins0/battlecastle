package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import utility.Utility;

public class Arrow {

	public static final int WIDTH = 32, HEIGHT = 16;
	private Rectangle bounds;
	private double vX, vY;
	private Color color; 
	private Player shotByPlayer;
	private String ID = "";
	private BufferedImage image;
	private double theta;
	private static final double GRAVITY = 9.8;
	
	public Arrow(Player player, String imagePath)
	{
		bounds = new Rectangle(0,0,WIDTH,HEIGHT);
		color = Utility.randomRGBColor();
		int random = (int)(Math.random() * 26 + 65);
		for(int i = 0; i < 20; i++)
		{
			ID += (char)random;
			random = (int)(Math.random() * 26 + 65);
		}
		image = Utility.loadImage(imagePath);
		shotByPlayer = player;
		System.out.println(ID);
		
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
		
		Graphics2D g2 = (Graphics2D)g;
		g2.translate(shotByPlayer.getCenterX(), shotByPlayer.getCenterY()-HEIGHT/2);
		g2.rotate(theta);
		g2.translate(bounds.x, bounds.y);
		if(image !=  null)
		{
			g2.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
		}else
		{
			g.setColor(color);
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		g2.translate(-bounds.x, -bounds.y);
		g2.rotate(-theta);
		g2.translate(-shotByPlayer.getCenterX(), -shotByPlayer.getCenterY()+HEIGHT/2);
		
	}
	
	public void fix(int x, int y, double vX, double vY, double theta)
	{
	//	bounds.x = x;
	//	bounds.y = y;
		this.vX = vX;
		this.vY = vY;
		this.theta = theta;
		
	}
	
	public void tick()
	{
		if(Math.abs(theta)<Math.PI/2)
			theta -= .01;
		else
			theta += .01;
		
		vY += GRAVITY / 100;
		if ( vY > GRAVITY )
			vY = GRAVITY;
		
		bounds.x += vX;
		bounds.y += vY;
		
	}
	
	public double getTheta() {
		return theta;
	}
}
