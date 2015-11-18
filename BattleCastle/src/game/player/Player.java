package game.player;

import game.physics.PhysicsRect;
import game.physics.Vector;

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

public class Player extends PhysicsRect{
	
	public static final int HEIGHT = 64;
	public static final int WIDTH = 32;
	public static final int ARROW_SPEED = 8;
	public static final double MASS = 10;
	public static final double ANG_VEL = 0;
	public static final double DRAG_C = 2;
	
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
		super(0, 0, WIDTH, HEIGHT,0, null, ANG_VEL, MASS, DRAG_C);
		//GRAVITY = 0;
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
		
	public void tick()
	{
		super.tick();
		fixArrow(MouseHandler.mouse.x, MouseHandler.mouse.y);
//		System.out.println(getVelocity());
	}
	
	
	public void render(Graphics g)
	{			
		if(image != null)
		{
			g.drawImage(image, getCorners()[0].XPoint(), getCorners()[0].YPoint(),WIDTH,HEIGHT,null);
		}else
		{
			g.setColor(Color.cyan);
			g.fillRect( getCorners()[0].XPoint(), getCorners()[0].YPoint(),WIDTH,HEIGHT);
		}
		
		if(currentArrow != null)
			currentArrow.render(g);	
		
		g.setColor(Color.black);
		g.drawString(String.format("(%d,%d)",  getCorners()[0].XPoint(), getCorners()[0].YPoint() ),  getCorners()[0].XPoint(), getCorners()[0].YPoint()-5);
		
	}
	
	public void setLocation(int x, int y)
	{
		move(new Vector(x,y));
//		System.out.println("WHAT?");
	}
	
	public void setLocation(Point p)
	{
		setLocation(p.x,p.y);
	}

	public void setvX(double vX) {
		if(vX == 0)
		{
			setVelocity(getVelocity().vectorSub(new Vector(getVelocity().XExact(),0)));
		}else
			setVelocity(getVelocity().vectorAdd(new Vector(vX-getVelocity().XExact(),0)));
	}

	public void setvY(double vY) {
		setVelocity(getVelocity().vectorAdd(new Vector(0,vY-getVelocity().YExact())));
	}

	public String getUUID() {
		return uuid;
	}
	//Temp used to not have errors
	public Rectangle getBounds()
	{
		return null;
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
		double theta = Math.atan2((getCenter().YPoint()-y),(getCenter().XPoint()-x));
		
		if(currentArrow == null && arrowStorage.size() > 0)
			currentArrow= arrowStorage.get(0);
		else if(arrowStorage.size() > 0)
		{
			currentArrow.fix(theta);
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
	
	public String getPlayerInformation()
	{
		return String.format("X:%d Y:%d W:%d H:%d", getCorners()[0].XPoint(),getCorners()[0].YPoint(),WIDTH,HEIGHT);
	}

	public String stringify()
	{
		return String.format("ImageFile:%s,X:%d,Y:%d,W:%d,H:%d",
					imageFilePath,
					getCorners()[0].XPoint(),getCorners()[0].YPoint(),WIDTH,HEIGHT
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
				if(image == null && !loadedImage)
				{
					image = Utility.loadImage(key_value[1]);
					loadedImage = true;
				}
				break;
			case "X": move(new Vector(Integer.parseInt(key_value[1])-getCorners()[0].XPoint(),0));
				break;
			case "Y": move(new Vector(0,Integer.parseInt(key_value[1])-getCorners()[0].YPoint()));
				break;
			case "S": //used in animation?
				break;
			case "Arrow": currentArrow.decode(key_value[1]);
				break;
				
			}
		}
	}
	
	private boolean loadedImage = false;
}

