package game.player;

import game.physics.PhysicsRect;
import game.physics.Vector;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import utility.Utility;

public class Arrow extends PhysicsRect{

	public static final int WIDTH = 32, HEIGHT = 16;
	private Rectangle bounds;
	private double vX, vY;
	private Color color; 
	private Player shotByPlayer;
	private String ID = "";
	private BufferedImage image;
	private double theta;
	
	public Arrow(int x, int y, int width, int height, double theta, Vector velocity, double torque, double mass,
			double dragC, Player player, String imagePath) {
		super(x, y, width, height, theta, velocity, torque, mass, dragC);
		this.shotByPlayer = player;
		this.image = Utility.loadImage(imagePath);
	}
	
	
	public Arrow(Player player, String imagePath)
	{
		this(player.getCenterX(),player.getCenterY(),WIDTH,HEIGHT,0,new Vector(5,0),0,500,1.05,player,imagePath);
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

	
//	public void render(Graphics g)
//	{
//		super.render(g);
//		
////		g.setColor(Color.black);
////		for(int i = 0; i < points.size(); i++)
////		{
////			Point p = points.get(i);
////			g.drawLine(p.x, p.y, p.x, p.y);
////		}
////		
////		if(image !=  null)
////		{
////			g.drawImage(image, bounds.x, bounds.y,null);
////		}else
////		{
////			g.setColor(color);
////			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
////		}
//
//		
//		
//	}
	
	public void addVelocity()
	{
		int vX = (int) (setDistance * Math.cos(theta));
		int vY = (int) (setDistance * Math.sin(theta));
		setVelocity(new Vector(-vX,-vY));
		
		if(Math.cos(theta) < 0)
			setAngularVelocity(2);
		else
			setAngularVelocity(-2);
	}
	
	public void fix(int x, int y, double vX, double vY, double theta)
	{
		move(new Vector(shotByPlayer.getCenterX()-getCenter().XPoint(),shotByPlayer.getCenterY()-getCenter().YPoint()));
		rotateTo(Math.toDegrees(theta));
		this.theta = theta;
	}
	
	public void tick()
	{
		super.tick();
	}
	
//	public void tick()
//	{
//		if(Math.abs(theta)<Math.PI/2)
//			theta -= .01;
//		else
//			theta += .01;
//		
//		vY += GRAVITY / 100;
//		if ( vY > GRAVITY )
//			vY = GRAVITY;
//		
//		bounds.x += vX;
//		bounds.y += vY;
//		
//	}
	
	public double getTheta() {
		return theta;
	}
}
