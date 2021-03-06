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

public class Player extends PhysicsRect implements Comparable{
	
	public static final int HEIGHT = 64;
	public static final int WIDTH = 32;
	public static final int ARROW_SPEED = 8;
	public static final double MASS = 20;
	public static final double ANG_VEL = 0;
	public static final double DRAG_C = 8;

	public static final int 
		STATE_RUNNING = 0,
		STATE_ALIVE = 1,
		STATE_DEAD = 2,
		STATE_JUMPING = 3,
		FACING_RIGHT = 0,
		FACING_LEFT = 1;
	
	public static final String KEY_VALUE_SEPARATOR = "#", ENTRY_SEPARATOR = "<";
	
	private BufferedImage image, deadI;
	
	private ArrayList<Arrow> arrowStorage;
	private Arrow currentArrow;
	private int arrowCount;
	private String uuid;
	private String imageFilePath;
	private boolean jumping = false;
	private boolean falling = true;
	private boolean clingToWall = false;
	private Point mouseLocation;
	private boolean dead;
	private boolean loadedImage = false;
	private double velToSet;
	private int score;
	/*
	 * Animation 
	 */
	private int playerState = STATE_ALIVE;
	private int playerFacing = FACING_RIGHT;
	private BufferedImage[][] images = Utility.loadBufferedMatrix(ImageFilePaths.TEMP_PLAYER_ANIMATED, WIDTH, HEIGHT);
	private int animation;
	private int animationCount;
	private static final int ANIMATION_DELAY= 2;
	
	
	/**
	 * constructs a default player no image
	 * @param uuid the id of the player
	 */
//	public Player(String uuid)
//	{
//		this((BufferedImage)null,uuid);
//	}
	
	/**
	 * constructs a player with given image and id
	 * @param image the image to display
	 * @param uuid the id of the player
	 */
//	public Player(BufferedImage image, String uuid)
//	{
//		super(0, 0, WIDTH, HEIGHT,0, null, ANG_VEL, MASS, DRAG_C);
//		this.image = image;
//		this.uuid = uuid;
//		arrowStorage = new ArrayList<Arrow>();
//		for(int i = 0; i < 10; i++)
//			arrowStorage.add(new Arrow(this,ImageFilePaths.ARROW));
//		currentArrow = arrowStorage.get(0);
//		mouseLocation = MouseHandler.mouse;
//		//GRAVITY = 0;
//		deadI = Utility.loadImage(ImageFilePaths.TEMP_PLAYER_DEAD);
//	}
	
	/**
	 * constructs a player with given image path and id
	 * @param imageFileName the imagepath to the image
	 * @param uuid id of the player
	 */
	public Player(String imageFileName,String uuid,String arrowPath)
	{
		//this(Utility.loadImage(imageFileName), uuid);
		super(0, 0, WIDTH, HEIGHT,0, null, ANG_VEL, MASS, DRAG_C);
		this.image = Utility.loadImage(imageFileName);
		this.uuid = uuid;
		arrowStorage = new ArrayList<Arrow>();
		for(int i = 0; i < 10; i++)
			arrowStorage.add(new Arrow(this,arrowPath,ArrowType.BOUNCE));
		currentArrow = arrowStorage.get(0);
		mouseLocation = MouseHandler.mouse;
		//GRAVITY = 0;
		deadI = Utility.loadImage(ImageFilePaths.TEMP_PLAYER_DEAD);
		this.imageFilePath = imageFileName;
		//System.out.println("Loading from filePath" + imageFileName);
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
		
		//System.out.println("Fixing Arrows " + uuid +" : " + "X: " + x + " Y: " + y + " Theta: " + theta);
		
		for(int i = 0; i < num && i < arrowStorage.size(); i++)
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
			//System.out.println("GRAPHICS ROTATION = " + arrowStorage.get(0).getGraphicsRotationTheta() + "Draw Coords: " + arrowStorage.get(0).getDrawCoords());
			return arrowStorage.remove(0);
		}
		return null;
	}
	
	/**
	 * used to pickup arrows
	 * @param a the arrow
	 */
	public void addArrow(Arrow a)
	{
		a.moveTo(-64,-64);
		a.setRotate(true);
		a.setLaunchCooldown(Arrow.DEFAULT_LAUNCH_COOLDOWN);
		a.setNormalForce(1, 1);
		arrowStorage.add(a);
		fixArrows(arrowStorage.size(), mouseLocation.x, mouseLocation.y);
	}
	
	/**
	 * updates player
	 */
	public void tick()
	{
		if(falling||jumping)
			super.tick();
		
		if(velToSet!=0)
		{
			setvX(velToSet);
			velToSet=0;
		}
		//if(getVelocity().XExact()!=0)
			//System.out.println("P.Velocity.x = " + getVelocity().XExact());
		
		animationCount ++;
		if(animationCount > ANIMATION_DELAY)
		{
			animationCount = 0;
			animation++;
			if(animation >= images[2*playerState+playerFacing].length)
			{
				animation = 0;
			}
		}
		
		if(dead)
		{
			playerState = STATE_DEAD; 
			animation = 0;
		}else{
			if(jumping || (Math.abs(getVelocity().YPoint() )> 1 ) )
			{
				
				if(getVelocity().YPoint() == 0)
				{
					falling = true;
					animation = 0;
				}
				if(playerState != STATE_JUMPING)
				{
					playerState = STATE_JUMPING;
					animation = 0;
				}
				
				
			}else{
				if(getVelocity().XPoint() == 0)
				{
					animation = 0;
					playerState = STATE_ALIVE;
					//			playerFacing = ;
				}
			}
		}
	
		clingToWall = false;
	}
	
	/**
	 * draws player to screen
	 */
	public void render(Graphics g)
	{			
		if(image != null)
		{
			//System.out.println("playerState: " + playerState + " playerfacing: " + playerFacing);
			//g.drawImage(image, getCorners()[0].XPoint(), getCorners()[0].YPoint(),WIDTH,HEIGHT,null);
			g.drawImage(images[2*playerState+playerFacing][animation], getCorners()[0].XPoint(), getCorners()[0].YPoint(),WIDTH,HEIGHT,null);
		}else
		{
			g.setColor(Color.cyan);
			g.fillRect( getCorners()[0].XPoint(), getCorners()[0].YPoint(),WIDTH,HEIGHT);
		}
		
		if(!isDead())
			if(currentArrow != null)
				currentArrow.render(g);
		
		g.setColor(Color.black);
		g.drawString(String.format("(%d,%d)",  getCorners()[0].XPoint(), getCorners()[0].YPoint() ),  getCorners()[0].XPoint(), getCorners()[0].YPoint()-5);
			
	}
	
	/* ===============
	 * GETTERS-SETTERS
	 * ===============
	 */
	
	/**
	 * get current held arrow
	 * @return current held arrow
	 */
	public Arrow getCurrentArrow()
	{
		return currentArrow;
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
	 * Sets mouse location for where player is aiming
	 * @param p the mouse Point
	 */
	public void setMouseLocation(Point p)
	{
		mouseLocation = p;
	}
	
	/**
	 * Gets mouse location for where player is aiming
	 * @return mouse Point
	 */
	public Point getMouseLocation()
	{
		return mouseLocation;
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
		{
			if(Math.abs(getVelocity().XExact()) > Math.abs(vX) ) return;
			double dx = vX-getVelocity().XExact();
			setVelocity(getVelocity().vectorAdd(new Vector(dx,0)));
		}
	}
	
	/**
	 * Sets Horizontal Velocity of player based upon the game clock as to avoid variation
	 * @param vX the horizontal velocity value
	 */
	public void setvXSync(double vX)
	{
		velToSet=vX;
	}

	/**
	 * Sets Vertical Velocity of player
	 * @param vY the vertical velocity value
	 */
	public void setvY(double vY) {
		setVelocity(getVelocity().vectorAdd(new Vector(0,vY-getVelocity().YExact())));
		//setVelocity(getVelocity().vectorAdd(new Vector(0,vY)));
	}
	
	/**
	 * gets player unique id;
	 * @return player id
	 */
	public String getUUID() {
		return uuid;
	}
	
	public int getArrowCount()
	{
		return arrowStorage.size();
	}
	
	public int getPlayerState() {
		return playerState;
	}

	public void setPlayerState(int playerState) {
		this.playerState = playerState;
	}

	public int getPlayerFacing() {
		return playerFacing;
	}

	public void setPlayerFacing(int playerFacing) {
		this.playerFacing = playerFacing;
		if(getVelocity().XPoint() != 0 && !(jumping || Math.abs((getVelocity().YPoint()))>1))
		{
			playerState = STATE_RUNNING;
			if(animation >= images[2*playerState+playerFacing].length)
				animation = 0;
		}
	}

	//TODO:: REMOVE Temp used to not have errors with interactive tile 
	public Rectangle getBounds()
	{
		return null;
	}
	
	//TODO:: Change
	public BufferedImage getDI() {return deadI; }
	
    /**
     * Sets player Image
     * @param image the image to setTo
     */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * Sets player alive status
	 * @param dead whether the player is dead
	 */
	public void setDead(boolean dead) { this.dead = dead;}
	
	/**
	 * gets player death status
	 * @return is player dead
	 */
	public boolean isDead() {return dead;}
	
	public boolean isJumping() {
		return jumping;
	}

	
	public boolean isFalling() {
		return falling;
	}

	
	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	
	public void setFalling(boolean falling) {
		this.falling = falling;
	}
	

	/**
	 * Gets the Arrow the player is capable of shooting
	 * @return List<Arrow>
	 */
	public ArrayList<Arrow> getArrowStorage() {
		return arrowStorage;
	}
	
	/**
	 * Updates arrows to the players location on death
	 */
	public void updateCurrentArrowOnDeath()
	{
		fixArrows(arrowStorage.size(), mouseLocation.x, mouseLocation.y);
		if(currentArrow == null && arrowStorage.size() > 0)
			currentArrow= arrowStorage.get(0);
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
	 * turns object into string to send over network
	 * @return string representation of object
	 */
	public String stringify()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ImageFile" + KEY_VALUE_SEPARATOR + "%s" + ENTRY_SEPARATOR);
		builder.append("X" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("Y" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("W" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("H" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("S" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("F" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("A" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("Score" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("MouseX" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("MouseY" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("Arrow" + KEY_VALUE_SEPARATOR + "%s");
//		"ImageFile#%s<X#%d<Y#%d<W#%d<H#%d<MouseX#%d<MouseY#%d<Arrow#%s"
		return String.format(builder.toString(),
					imageFilePath,
					getCorners()[0].XPoint(),getCorners()[0].YPoint(),WIDTH,HEIGHT, playerState, playerFacing, animation, score,
					mouseLocation.x, mouseLocation.y, (currentArrow != null ? currentArrow.stringify() : "")
					);
	}
	
	/**
	 * decodes a string representation of object
	 * @param s the string representation of the object
	 */
	public void decode(String s)
	{
		String[] items = s.split(ENTRY_SEPARATOR);
		for(String item : items)
		{
			String[] key_value = item.split(KEY_VALUE_SEPARATOR);
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
			case "S": //current state;
					playerState = Integer.parseInt(key_value[1]);
				break;
			case "F": //current direction Facing
					playerFacing = Integer.parseInt(key_value[1]);
				break;
			case "A": //current direction Facing
				animation = Integer.parseInt(key_value[1]);
				break;
			case "Score":
				score = Integer.parseInt(key_value[1]);
				break;
			case "Arrow": 
					if(key_value.length > 1 && currentArrow != null)
					{
						currentArrow.decode(key_value[1]);
						fixArrows(2, mouseLocation.x, mouseLocation.y);
					}else
					{
						if(currentArrow == null && arrowStorage.size() > 0)
							currentArrow= arrowStorage.get(0);
					}
				break;
			case "MouseX":
				mouseLocation.x = Integer.parseInt(key_value[1]);
				break;
			case "MouseY":
				mouseLocation.y = Integer.parseInt(key_value[1]);
				
			}
		}
	}

	public void reset()
	{
		score = 0;
	}
	
	public int getScore() { return score; }
	public void addPoint() { score++; }
	public void subPoint() { score--; if(score < 0) score=0;}
	public void setScore(int score) { this.score = score; }
	
	public void setCling(boolean clinging) {this.clingToWall = clinging;}
	public boolean isClinging(){return clingToWall;}
	
	/**
	 * @return class.Name[ID:'id']
	 */
	@Override
	public String toString() {
		return String.format("%s[ID:%s]", this.getClass().getName(), uuid);
	}
	
	public int compareTo(Object o)
	{
		if(o instanceof Player)
			return this.uuid.compareTo(((Player)o).uuid);
		return -1;
	}
	
	public void debugMe() {
		System.out.println("Velocity: " + getVelocity() + ", Score: " + score + ", Dead: " + dead + ", Falling: " + falling + ", Jumping: " + jumping);		
	}

}

