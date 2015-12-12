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
	
	/*
	 * Top Left = 0
	 * Top Right = 1
	 * Bottom Right = 2
	 * Bottom Left = 3
	 */
	public PhysicsRect(int x,int y,int width,int height,double theta,Vector velocity,double torque,double mass,double dragC)
	{
		super(new int[]{x,x+width,x+width,x},new int[]{y,y,y+height,y+height},theta, velocity, torque, mass, dragC);
		
	}

	public Vector getTopLeft()
	{
		return getCorners()[0];
	}
	
	public int getWidth()
	{
		return (int)(getCorners()[1].XExact() - getCorners()[0].XExact());
	}
	
	public int getHeight()
	{
		return (int) (getCorners()[2].YExact() - getCorners()[0].YExact());
	}
	
	public Vector getLeftHighest()
	{
		int highestY = Integer.MIN_VALUE;
		int lowestX = Integer.MAX_VALUE;
		for(Vector v : getCorners())
		{
			lowestX = Math.min(lowestX, v.XPoint());
			highestY = Math.max(highestY, v.YPoint());
		}
		
		for(Vector v : getCorners())
		{
			if(v.YPoint() == highestY)
			{
				if(v.XPoint() == lowestX)
				{
					return v;
				}
			}
		}
		
		//not the same point use the one to left with same height
		lowestX = Integer.MAX_VALUE;
		for(Vector v : getCorners())
		{
			if(v.YPoint() == highestY)
			{
				lowestX = Math.min(lowestX, v.XPoint());
			}
		}
		
		for(Vector v : getCorners())
		{
			if(v.YPoint() == highestY)
			{
				if(v.XPoint() == lowestX)
				{
					return v;
				}
			}
		}	
		
		return null;
	}
	
	public Vector getRightHighest()
	{
		int highestY = Integer.MIN_VALUE;
		int highestX = Integer.MIN_VALUE;
		for(Vector v : getCorners())
		{
			highestX = Math.max(highestX, v.XPoint());
			highestY = Math.max(highestY, v.YPoint());
		}
		
		for(Vector v : getCorners())
		{
			if(v.YPoint() == highestY)
			{
				if(v.XPoint() == highestX)
				{
					return v;
				}
			}
		}
		//not the same point use the one to right with same height
		highestX = Integer.MIN_VALUE;
		for(Vector v : getCorners())
		{
			if(v.YPoint() == highestY)
			{
				highestX = Math.max(highestX, v.XPoint());
			}
		}
		
		for(Vector v : getCorners())
		{
			if(v.YPoint() == highestY)
			{
				if(v.XPoint() == highestX)
				{
					return v;
				}
			}
		}
		
		
		return null;
	}
	
	//create get leftHighest and rightHighest
	public void tick()
	{
		super.tick();
		if(getLeftHighest().YPoint() > 786 + getHeight()) //move from bottom to top
		{
			moveTo(getLeftHighest().XPoint(), 0-getHeight());
		}
		if(getLeftHighest().XPoint() > 1024 + getWidth()) //move from right to left
		{
			moveTo(0-getWidth(), getLeftHighest().YPoint());
		}
		if(getLeftHighest().XPoint() < 0 - getWidth()) //move from left to right
		{
			moveTo(1024, getLeftHighest().YPoint());
		}
		if(getRightHighest().YPoint() < 0 - getHeight()) //move from top to bottom
		{
			moveTo(getTopLeft().XPoint(), 786);
		}
	}
}
