package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.MouseHandler;
import core.constants.ImageFilePaths;
import utility.Utility;

public class Player {
	
	public static final int HEIGHT = 64;
	public static final int WIDTH = 32;
	public static final int ARROW_SPEED = 8;
	
	private Rectangle bounds;
	private double vX, vY;
	private static final double GRAVITY = 9.8;
	private BufferedImage image;
	private ArrayList<Arrow> arrowStorage;
	private Arrow currentArrow;
	private int arrowCount;
	private String uuid;
	private String imageFilePath;
	
	public Player(String uuid)
	{
		this((BufferedImage)null,uuid);
	}
	
	public Player(BufferedImage image, String uuid)
	{
		bounds = new Rectangle(-WIDTH,-HEIGHT,WIDTH,HEIGHT);
		this.image = image;
		
		arrowStorage = new ArrayList<Arrow>();
		for(int i = 0; i < 100; i++)
			arrowStorage.add(new Arrow(this,ImageFilePaths.ARROW));
//		arrowStorage.add(new Arrow(this,ImageFilePaths.ARROW));
//		arrowStorage.add(new Arrow(this,ImageFilePaths.ARROW));
//		arrowStorage.add(new Arrow(this,ImageFilePaths.ARROW));
//		arrowStorage.add(new Arrow(this,ImageFilePaths.ARROW));
	}
	
	public Player(String imageFileName,String uuid)
	{
		this(Utility.loadImage(imageFileName), uuid);
		this.imageFilePath = imageFileName;
	}
	
	public void setLocation(int x, int y)
	{
		bounds.x = x;
		bounds.y = x;
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
		//bounds.y += vY;
		
		bounds.x += vX;
		bounds.y += vY;
		
		if(vY > 0)
			vY -=.1;
		else if(vY < 0)
			vY +=.1;
		
		if(vX > 0)
			vX -=.1;
		else if(vX < 0)
			vX +=.1;
		
		fixArrow(MouseHandler.mouse.x, MouseHandler.mouse.y);
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
		
		if(currentArrow != null)
			currentArrow.render(g);	
		
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
	
	public int getX()
	{
		return bounds.x;
	}
	
	public int getY()
	{
		return bounds.y;
	}
	
	public int getCenterX()
	{
		return bounds.x + bounds.width/2;
	}
	
	public int getCenterY()
	{
		return bounds.y + bounds.height/2;
	}
	
	public String getUUID() {
		return uuid;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Player)
		{
			return uuid.equals(( (Player)obj).getUUID());
		}
		return false;
	}
	
	public void fixArrow(int x, int y)
	{
		double theta = Math.atan2((bounds.y + bounds.height / 2 - y),(bounds.x + bounds.width/2 - x ));
		int dc =(int) Math.hypot(
				MouseHandler.mouse.x - (bounds.x + bounds.width/2),
				MouseHandler.mouse.y - (bounds.y + bounds.height/2)
				);

		if(currentArrow == null && arrowStorage.size() > 0)
			currentArrow= arrowStorage.get(0);
		else if(arrowStorage.size() > 0)
		{
			int ax = bounds.x - (int)(Math.cos(theta) * WIDTH);
			int ay = bounds.y - (int)(Math.sin(theta) * HEIGHT);
			currentArrow.fix( 
					ax , ay,
					(int)(Math.cos(theta)*-ARROW_SPEED),(int)(Math.sin(theta)*-ARROW_SPEED), theta);
		}
	}
	
	public Arrow removeArrow()
	{
		if(arrowStorage.size() > 0)
		{
			currentArrow = null;
			return arrowStorage.remove(0);
		}
		return null;
	}

	public String stringify()
	{
		return String.format("ImageFile:%s,X:%d,Y:%d,W:%d,H:%d",
					imageFilePath,
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
				if(image == null)
					image = Utility.loadImage(key_value[1]);
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

