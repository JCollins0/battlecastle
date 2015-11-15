package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import game.physics.PhysicsRect;
import game.physics.Vector;
import utility.Utility;

public class Arrow extends PhysicsRect{

	public static final int WIDTH = 32, HEIGHT = 16;
	private Rectangle bounds;
	private double vX, vY;
	private Color color; 
	private Player shotByPlayer;
	private String ID = "";
	private BufferedImage image;
	private double mYtheta;
	
	public Arrow(int x, int y, int width, int height, double theta, Vector velocity, double torque, double mass,
			double dragC, Player player, String imagePath) {
		super(x, y, width, height, theta, velocity, torque, mass, dragC);
		this.shotByPlayer = player;
		this.image = Utility.loadImage(imagePath);
	}
	
	
	public Arrow(Player player, String imagePath)
	{
		this(player.getCenterX(),player.getCenterY(),WIDTH,HEIGHT,0,null,0,500,1.05,player,imagePath);
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
	
	double translateX, translateY;
	static int setDistance = 10;
	ArrayList<Point> points = new ArrayList<Point>();


	/**
	 * @param g the graphics for drawing
	 */
	public void render(Graphics g)
	{
		super.render(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.translate(getTopLeft().XPoint() , getTopLeft().YPoint() );
		g2d.rotate(mYtheta);
		g2d.drawImage(image, 0 , 0, WIDTH, HEIGHT, null);
		g2d.rotate(-mYtheta);
		g2d.translate(-(getTopLeft().XPoint() ),-( getTopLeft().YPoint()));
		
		//System.out.println( (getTopLeft().XPoint() + WIDTH/2 - HEIGHT/2) + " " +  (getTopLeft().YPoint() + HEIGHT/2 - WIDTH/2) );
		
	}
	
	public void addVelocity()
	{
		int vX = (int) (setDistance * Math.cos(mYtheta));
		int vY = (int) (setDistance * Math.sin(mYtheta));
		setVelocity(new Vector(-vX,-vY));
		
		if(Math.cos(mYtheta) < 0)
			setAngularVelocity(2);
		else
			setAngularVelocity(-2);
	}
	
	public void fix(int x, int y, double vX, double vY, double theta)
	{
		move(new Vector(shotByPlayer.getCenterX()-getCenter().XPoint(),shotByPlayer.getCenterY()-getCenter().YPoint()));
		rotateTo(Math.toDegrees(theta));
		this.mYtheta = theta;
		//System.out.println(Arrays.toString(getCorners()));
	}
	
	double deltaTheta = 0;
	
	public void tick()
	{
		super.tick();
	
		double velX = getVelocity().XExact();
		double velY = getVelocity().YExact();
		rotateTo(Math.toDegrees(Math.atan2(-velY,-velX)));
//		if(velY >= 0 && getAngularVelocity() != 0)
//		{
//			//deltaTheta = .1;
//		
//		}
	//	System.out.println(theta + " " + getAngularVelocity());
		
		if(mYtheta - theta < 0)
		{
			mYtheta= Math.toRadians(Math.abs(mYtheta - theta));
		}else
		{
			mYtheta= -Math.toRadians((mYtheta - theta));
		}
	}
	

	public double getTheta() {
		return mYtheta;
	}
}
