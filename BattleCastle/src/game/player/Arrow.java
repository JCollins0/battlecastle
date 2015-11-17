package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import game.physics.PhysicsRect;
import game.physics.Vector;
import utility.Utility;

public class Arrow extends PhysicsRect{

	public static final int WIDTH = 32, HEIGHT = 16;
	private Player shotByPlayer;
	private String ID = "";
	private BufferedImage image;
	private String imagePath;
	private double mYtheta;
	public static final double MASS = 500, DRAGC = 1.05;
	
	public Arrow(int x, int y, double theta, Vector velocity, double torque, Player player, String imagePath) {
		super(x, y, WIDTH, HEIGHT, theta, velocity, torque, MASS,DRAGC);
		this.shotByPlayer = player;
		this.image = Utility.loadImage(imagePath);
		this.imagePath = imagePath;
	}
	
	
	public Arrow(Player player, String imagePath)
	{
		this(player.getCenterX(),player.getCenterY(),0,null,0,player,imagePath);
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
		String format = "ImageFile:%s,Corner0X:%d,Corner0Y:%d,Theta:%f,MyTheta:%f"+
						"PlayerID:%s";
		String s=String.format(format,imagePath, getCorners()[0].XPoint(), getCorners()[0].YPoint()
				, theta, mYtheta, shotByPlayer.getUUID());
		return s;
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
				if(image == null)
					image = Utility.loadImage(key_value[1]);
				break;
			case "Corner0X":
				break;
			case "Corner0Y":
				break;
			case "Theta":
				break;
			case "MyTheta":
				break;
			case "PlayerID":
				break;
			}
			
		}
	}
	
	double translateX, translateY;
	static int setDistance = 10;

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
	
	public String getID() {
		return ID;
	}
	
	
}
