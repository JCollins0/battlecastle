package game.physics;

import java.awt.Rectangle;

public class PhysicsRect extends PhysicsPoly {

	public PhysicsRect(int[] x, int[] y, double theta, Vector velocity, double torque, double mass, double dragC) {
		super(x, y, theta, velocity, torque, mass, dragC);
	}
	
	public PhysicsRect(Rectangle r,double theta,Vector velocity,double torque,double mass,double dragC)
	{
		this((int)r.getX(),(int)r.getY(),(int)r.getWidth(),(int)r.getHeight(),theta, velocity, torque, mass, dragC);
		//super(new int[]{(int) r.getX(),(int) (r.getX()+r.getWidth()),(int) (r.getX()+r.getWidth()),(int) r.getX()},
				//new int[]{(int) r.getY(),(int) r.getY(),(int) (r.getY()+r.getHeight()),(int) (r.getY()+r.getHeight())},theta,velocity,torque,mass,dragC);
	}
	
	public PhysicsRect(int x,int y,int width,int height,double theta,Vector velocity,double torque,double mass,double dragC)
	{
		super(new int[]{x,x+width,x+width,x},new int[]{y,y,y+height,y+height},theta, velocity, torque, mass, dragC);
	}

}
