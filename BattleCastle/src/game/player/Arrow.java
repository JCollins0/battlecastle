package game.player;

import game.Game;
import game.physics.PhysicsPoly;
import game.physics.PhysicsRect;
import game.physics.Polygon;
import game.physics.Vector;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import core.HostType;
import utility.Utility;

public class Arrow extends PhysicsRect{

	public static final int WIDTH = 48, HEIGHT = 24;
	private Player shotByPlayer;
	private String ID = "";
	private BufferedImage image;
	private String imagePath;
	private double graphicsRotationTheta;
	public static final double MASS = 10, DRAGC = 1.05;
	static int setDistance = 10;
	private PhysicsPoly headCollision;
	private static final int HEAD_COLL_W = 20; //10
	private boolean rotate = true;
	public static final int DEFAULT_LAUNCH_COOLDOWN = 2; 
	private int launchCoolDown = DEFAULT_LAUNCH_COOLDOWN;
	Vector[] hcrnrs;
	
	public static final String KEY_VALUE_SEPARATOR = ":", ENTRY_SEPARATOR = ",";
	
	public Arrow(int x, int y,
				 double theta, double graphicsTheta,
				 Vector velocity, double torque, Player player,
				 String imagePath, String ID) {
		
		super(x, y, WIDTH, HEIGHT, theta, velocity, torque, MASS,DRAGC);
		this.shotByPlayer = player;
		this.image = Utility.loadImage(imagePath);
		this.imagePath = imagePath;
		this.graphicsRotationTheta = graphicsTheta;
		this.ID = ID;
		headCollision = new PhysicsPoly(x,y,HEAD_COLL_W,24,theta,velocity,torque,MASS,DRAGC);
	}
	
	
	public Arrow(Player player, String imagePath)
	{
		this(player.getCenter().XPoint(),player.getCenter().YPoint(),0,0,null,0,player,imagePath,Utility.generateRandomUUID(20));		
	}
	
	/**
	 * 
	 * @return a string representation of the object
	 */
	public String stringify()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ImageFile" + KEY_VALUE_SEPARATOR + "%s" + ENTRY_SEPARATOR);
		builder.append("Corner0X" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("Corner0Y" + KEY_VALUE_SEPARATOR + "%d" + ENTRY_SEPARATOR);
		builder.append("Theta" + KEY_VALUE_SEPARATOR + "%f" + ENTRY_SEPARATOR);
		builder.append("MyTheta" + KEY_VALUE_SEPARATOR + "%f" + ENTRY_SEPARATOR);
		builder.append("PlayerID" + KEY_VALUE_SEPARATOR + "%s" + ENTRY_SEPARATOR);
		builder.append("VELOCITYX" + KEY_VALUE_SEPARATOR + "%f" + ENTRY_SEPARATOR);
		builder.append("VELOCITYY" + KEY_VALUE_SEPARATOR + "%f");
//		String format = "ImageFile:%s,Corner0X:%d,Corner0Y:%d,Theta:%f,MyTheta:%f,"+
//						"PlayerID:%s,VELOCITYX:%f,VELOCITYY:%f";
		return String.format(builder.toString(),imagePath, getCorners()[0].XPoint(), getCorners()[0].YPoint()
				, theta, graphicsRotationTheta, shotByPlayer.getUUID(), getVelocity().XExact(), getVelocity().YExact());
	}
	
	/**
	 * 
	 * @param text a string that describes an object
	 */
	public void decode(String text)
	{
		String[] items = text.split(ENTRY_SEPARATOR);
		for(String item : items)
		{
			String[] key_value = item.split(KEY_VALUE_SEPARATOR);
			switch(key_value[0])
			{
			case "ImageFile":
				if(image == null)
					image = Utility.loadImage(key_value[1]);
				break;
			case "Corner0X": moveTo(Integer.parseInt(key_value[1]), getCorners()[0].YPoint());
				break;
			case "Corner0Y": moveTo(getCorners()[0].XPoint(),Integer.parseInt(key_value[1]));
				break;
//			case "Theta": theta = Double.parseDouble(key_value[1]);
//				break;
			case "MyTheta": graphicsRotationTheta = Double.parseDouble(key_value[1]);
				break;
			}
			
		}
	}

	/**
	 * @param g the graphics for drawing
	 */
	public void render(Graphics g)
	{
		//super.render(g);
		//headCollision.render(g);
		//System.out.println("Drawing Arrow: " + ID + "-:"+stringify());
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.translate(getTopLeft().XPoint() , getTopLeft().YPoint() );
		//if(Game.hostType != HostType.CLIENT)
		g2d.rotate(graphicsRotationTheta);
		g2d.drawImage(image, 0 , 0, WIDTH, HEIGHT, null);
		//if(Game.hostType != HostType.CLIENT)
		g2d.rotate(-graphicsRotationTheta);
		g2d.translate(-(getTopLeft().XPoint() ),-( getTopLeft().YPoint()));
		
//		
//		g2d.translate(headCollision.getTopLeft().XPoint() , headCollision.getTopLeft().YPoint() );
//		g2d.rotate(graphicsRotationTheta);
//		
//		//g2d.drawImage(image, 0 , 0, WIDTH, HEIGHT, null);
//		g2d.rotate(-graphicsRotationTheta);
//		g2d.translate(-(headCollision.getTopLeft().XPoint() ),-( headCollision.getTopLeft().YPoint()));
//		//System.out.println( (getTopLeft().XPoint() + WIDTH/2 - HEIGHT/2) + " " +  (getTopLeft().YPoint() + HEIGHT/2 - WIDTH/2) );
		
	}
	
	/**
	 * Adds a certain velocity based on angle around player
	 */
	public void addVelocity()
	{
		int vX = (int) (setDistance * Math.cos(graphicsRotationTheta));
		int vY = (int) (setDistance * Math.sin(graphicsRotationTheta));
		setVelocity(new Vector(vX,vY));
	}
	
	public void addVelocity(int vx, int vy)
	{
		setVelocity(new Vector(vx,vy));
	}
	
	/**
	 * 
	 * @param theta the angle to rotate the arrow to
	 */
	public void fix(double theta)
	{
		move(new Vector(shotByPlayer.getCenter().XPoint()-getCenter().XPoint(),shotByPlayer.getCenter().YPoint()-getCenter().YPoint()));
		rotateTo(Math.toDegrees(theta));
		//System.out.println(theta + " " + graphicsRotationTheta);
		this.graphicsRotationTheta = theta;
		//System.out.println(graphicsRotationTheta);
		
		hcrnrs = headCollision.getCorners();
		hcrnrs[0] = getCorners()[1].vectorAdd(new Vector(-HEAD_COLL_W*Math.cos(theta),-HEAD_COLL_W*Math.sin(theta)));
		hcrnrs[1] = getCorners()[1];
		hcrnrs[2] = getCorners()[2];
		hcrnrs[3] = getCorners()[2].vectorAdd(new Vector(-HEAD_COLL_W*Math.cos(theta),-HEAD_COLL_W * Math.sin(theta)));
		
	}
	
	/**
	 * updates arrow
	 */
	public void tick()
	{
		super.tick();
//		if(launchCoolDown == 0)
//			System.out.println("ARROW IS NOW ACTIVE");
		if(launchCoolDown > 0)
			launchCoolDown--;
		
		double velX = getVelocity().XExact();
		double velY = getVelocity().YExact();
		double angle = Math.toDegrees(Math.atan2(velY,velX));
		if(!(velX == 0 && velY == 0) && rotate)
			rotateTo(angle);
		
		hcrnrs = headCollision.getCorners();
		hcrnrs[0] = getCorners()[1].vectorAdd(new Vector(-HEAD_COLL_W*Math.cos(Math.toRadians(theta)),-HEAD_COLL_W*Math.sin(Math.toRadians(theta) )));
		hcrnrs[1] = getCorners()[1];
		hcrnrs[2] = getCorners()[2];
		hcrnrs[3] = getCorners()[2].vectorAdd(new Vector(-HEAD_COLL_W*Math.cos(Math.toRadians(theta)),-HEAD_COLL_W * Math.sin(Math.toRadians(theta))));
		
//		System.out.println("ANGLE OF ROTATION: "  +theta);
//		System.out.println("-----------\n" + Arrays.toString(getCorners()) + "\n" + Arrays.toString(headCollision.getCorners()) + "\n----------\n");
		
		if(rotate)
		{
			if(graphicsRotationTheta - theta < 0)
			{
				graphicsRotationTheta= Math.toRadians(Math.abs(graphicsRotationTheta - theta));
			}else
			{
				graphicsRotationTheta= -Math.toRadians((graphicsRotationTheta - theta));
			}
		}		
		//System.out.println("MY LOCATION: " + Arrays.toString(getCorners()));
		//System.out.println("MY VELOCITY: " + getVelocity());
//		System.out.println("-------");
		
//		if(getVelocity().XPoint() > -1 && getVelocity().XPoint() < 1)
//		{
//			getVelocity().setX(0);
//		}
	}
	
	/**
	 * 
	 * @return angle of image rotation
	 */
	public double getGraphicsRotationTheta() {
		return graphicsRotationTheta;
	}
	
	/**
	 * 
	 * @return arrow id
	 */
	public String getID() {
		return ID;
	}
	
	/**
	 * Returns string representation of object
	 */
	public String toString()
	{
		return stringify();
	}
	
	/**
	 * Sets the player who holds this arrow
	 * @param p the player
	 */
	public void setPlayer(Player p)
	{
		shotByPlayer = p;
	}
	
	public Player getPlayer()
	{
		return shotByPlayer;
	}
	
	/**
	 * Sets the arrow rotation property.
	 * True = arrow can rotate
	 * False = arrow can't rotate
	 * @param rotate true or false
	 */
	public void setRotate(boolean rotate)
	{
		this.rotate=rotate;
	}
	
	/**
	 * Used to tell if arrow is stuck in a wall
	 */
	public boolean isStuck()
	{
		return !rotate;
	}
	
	/**
	 * Sets the cooldown to test for collision
	 * @param lcool the cooldown time
	 */
	public void setLaunchCooldown(int lcool)
	{
		this.launchCoolDown= lcool;
	}
	
	/**
	 * Gets the current launch cooldown for the arrow
	 * @return the launch cooldown
	 */
	public int getLaunchCoolDown() {
		return launchCoolDown;
	}
	
	/**
	 * gets collision bounds for the arrow (the tip)
	 */
	@Override
	public Polygon getCollisionBounds() {
		if(isStuck()) return this;
		return headCollision;
	}
	
	public double getTheta()
	{
		return Math.toRadians(theta);
	}
	
	/**
	 * Test if arrow objects are equal
	 * @return true if ID's are the same, False if not.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Arrow)
		{
			return ((Arrow)obj).ID.equals(ID);
		}
		return false;
	}
	
}
