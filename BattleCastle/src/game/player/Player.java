package game.player;

import game.physics.PhysicsRect;
import game.physics.Vector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utility.Utility;
import core.MouseHandler;
import core.constants.ImageFilePaths;

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
	private boolean falling = true;
	
	
	/**
	 * constructs a default player no image
	 * @param uuid the id of the player
	 */
	public Player(String uuid)
	{
		this((BufferedImage)null,uuid);
	}
	
	/**
	 * constructs a player with given image and id
	 * @param image the image to display
	 * @param uuid the id of the player
	 */
	public Player(BufferedImage image, String uuid)
	{
		super(0, 0, WIDTH, HEIGHT,0, null, ANG_VEL, MASS, DRAG_C);
		//GRAVITY = 0;
		this.image = image;
		this.uuid = uuid;
		arrowStorage = new ArrayList<Arrow>();
		for(int i = 0; i < 100; i++)
			arrowStorage.add(new Arrow(this,ImageFilePaths.ARROW));
		
	}
	
	/**
	 * constructs a player with given image path and id
	 * @param imageFileName the imagepath to the image
	 * @param uuid id of the player
	 */
	public Player(String imageFileName,String uuid)
	{
		this(Utility.loadImage(imageFileName), uuid);
		this.imageFilePath = imageFileName;
	}
	
	/**
	 * updates player
	 */
	public void tick()
	{
		if(falling)
			super.tick();
		if(currentArrow == null && arrowStorage.size() > 0)
			currentArrow= arrowStorage.get(0);
		else if(arrowStorage.size() > 0)
		{
			fixArrows(2,MouseHandler.mouse.x, MouseHandler.mouse.y);
		}
	}
	
	/**
	 * draws player to screen
	 */
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
	
	/**
	 * moves (top left of) player to specified x and y
	 * @param x the x value on screen
	 * @param y the y value on screen
	 */
	public void setLocation(int x, int y)
	{
		move(new Vector(x,y));
	}
	
	/**
	 * Sets location of player given a point
	 * @param p the Point of top left of player on screen
	 */
	public void setLocation(Point p)
	{
		setLocation(p.x,p.y);
	}
	
	/**
	 * Sets Horizontal Velocity of player
	 * @param vX the horizontal velocity value
	 */
	public void setvX(double vX) {
		if(vX == 0)
		{
			setVelocity(getVelocity().vectorSub(new Vector(getVelocity().XExact(),0)));
		}else
			setVelocity(getVelocity().vectorAdd(new Vector(vX-getVelocity().XExact(),0)));
	}

	/**
	 * Sets Vertical Velocity of player
	 * @param vY the vertical velocity value
	 */
	public void setvY(double vY) {
		setVelocity(getVelocity().vectorAdd(new Vector(0,vY-getVelocity().YExact())));
	}
	
	/**
	 * gets player unique id;
	 * @return player id
	 */
	public String getUUID() {
		return uuid;
	}
	
	//TODO:: REMOVE Temp used to not have errors with interactive tile 
	public Rectangle getBounds()
	{
		return null;
	}
	
	/**
	 * tests for player equality
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Player)
		{
			return uuid.equals(( (Player)obj).getUUID());
		}
		return false;
	}
	
	/**
	 * Used to center and rotate arrow on player
	 * @param num the index in the arrow array to fix
	 * @param x the x position of the mouse
	 * @param y the y position of the mouse
	 */
	public void fixArrows(int num, int x, int y)
	{
		double theta = Math.atan2((y-getCenter().YPoint()),(x-getCenter().XPoint()));
		
		for(int i = 0; i < num & i < arrowStorage.size(); i++)
		{
			Arrow a = arrowStorage.get(i);
			a.fix(theta);
		}
		
	}
	
	/**
	 * used to 'launch an arrow'
	 * @return arrow to be removed, null if none left to fire
	 */
	public Arrow removeArrow()
	{
		if(arrowStorage.size() > 0)
		{
			currentArrow = null;
			return arrowStorage.remove(0);
		}
		return null;
	}
	
	/**
	 * used to send location data to other client
	 * @return
	 */
//	public String getPlayerInformation()
//	{
//		return String.format("X:%d Y:%d W:%d H:%d", getCorners()[0].XPoint(),getCorners()[0].YPoint(),WIDTH,HEIGHT);
//	}
	
	/**
	 * turns object into string to send over network
	 * @return string representation of object
	 */
	public String stringify()
	{
		return String.format("ImageFile#%s<X#%d<Y#%d<W#%d<H#%d<Arrow#%s",
					imageFilePath,
					getCorners()[0].XPoint(),getCorners()[0].YPoint(),WIDTH,HEIGHT, (currentArrow != null ? currentArrow.stringify() : "")
					);
	}
	
	/**
	 * decodes a string representation of object
	 * @param s the string representation of the object
	 */
	public void decode(String s)
	{
		String[] items = s.split("<");
		for(String item : items)
		{
			String[] key_value = item.split("#");
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
			case "Arrow": 
					if(1 < key_value.length && currentArrow != null)
					{
						
						currentArrow.decode(key_value[1]);
					}
				break;
				
			}
		}
	}
	
	private boolean loadedImage = false;
	
	@Override
	public String toString() {
		return String.format("%s[ID:%s]", this.getClass().getName(), uuid);
	}
	
	public Arrow getCurrentArrow()
	{
		return currentArrow;
	}
}

