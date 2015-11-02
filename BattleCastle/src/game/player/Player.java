package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.MouseHandler;
import utility.Utility;

public class Player {
	
	public static final int HEIGHT = 64;
	public static final int WIDTH = 32;
	
	private Rectangle bounds;
	private double vX, vY;
	private static final double GRAVITY = 9.8;
	private BufferedImage image;
	private ArrayList<Arrow> arrowStorage;
	private Arrow currentArrow;
	private int arrowCount;
	
	
	public Player()
	{
		this((BufferedImage)null);
	}
	
	public Player(BufferedImage image)
	{
		arrowStorage = new ArrayList<Arrow>();
		arrowStorage.add(new Arrow());
		arrowStorage.add(new Arrow());
		arrowStorage.add(new Arrow());
		arrowStorage.add(new Arrow());
		arrowStorage.add(new Arrow());
		
		
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
		
		if(vX > 0)
			vX -=.1;
		else if(vX < 0)
			vX +=.1;
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
		
		for(int i = 0; i < arrowStorage.size(); i++)
		{
			arrowStorage.get(i).render(g);
		}
		
		g.setColor(Color.black);
		g.drawString(String.format("(%d,%d)", bounds.x, bounds.y ), bounds.x, bounds.y-5);
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
	public double getvX() {
		return vX;
	}

	public void setvX(double vX) {
		this.vX = vX;
	}

	public double getvY() {
		return vY;
	}

	public void setvY(double vY) {
		this.vY = vY;
	}
	
	public void addArrow(int x, int y)
	{
		double theta = Math.atan2((bounds.y + bounds.height / 2 - y),(bounds.x + bounds.width/2 - x ));
		int dc =(int) Math.hypot(
				MouseHandler.mouse.x - (bounds.x + bounds.width/2),
				MouseHandler.mouse.y - (bounds.y + bounds.height/2)
				);
		//theta = Math.toDegrees( theta)
//		if(theta < 0)
//			theta = 360 + theta;
		//UP = 90
		//DOWN = 270
		//LEFT = 180
		//RIGHT = 0
//		System.out.println(Math.toDegrees(theta));
//		int dc = (int)Math.sqrt(
//				Math.pow(
//						, 2)
//				+ Math.pow(
//						, 2) 
//				);
//		System.out.printf("[DC:%d,COS:%d,SIN:%d]\n",dc,(int)(Math.cos(theta)*dc),(int)(Math.sin(theta)*dc));
		if(currentArrow == null)
			currentArrow= arrowStorage.get(0);
		else
		{
			currentArrow.fix( 
					bounds.x + bounds.width/2 - (int)(Math.cos(theta) * Player.WIDTH ),
					bounds.y + bounds.height/2 - (int)(Math.sin(theta) * Player.HEIGHT ),
					(int)(Math.cos(theta)*-dc),(int)(Math.sin(theta)*-dc));
		}
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
			case "ImageFile":
				//create 4 or 4+ folders in resources/entity/player/ to be used for a specific character
				break;
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
			case "Arrow": currentArrow.decode(key_value[1]);
				break;
				
			}
		}
	}
}

