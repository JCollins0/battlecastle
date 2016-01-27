package game.physics;

import java.awt.Graphics;

import core.BattleCastleCanvas;

public class PhysicsPoly extends Polygon
{
	
	private Vector acceleration;
	private Vector velocity;
	
	private double angularVelocity;
	private double inertiaAboutCenter;
	
	private double mass;
	//private double dampingC;
	private double dragC;
	protected double GRAVITY = 9.8;

	/*public PhysicsPoly(int x, int y, int width, int height, double theta, Vector velocity, double torque, double mass, double dragC) {
		super(x, y, width, height,theta);
		if(velocity==null)
			this.velocity=new Vector();
		else
			this.velocity=velocity;
		this.acceleration=new Vector();
		this.omega=0;
		this.mass=mass;
		//this.dampingC=dampingC;
		this.dragC=dragC;
		this.torque=torque;
	}*/
	public PhysicsPoly(int x, int y, int width, int height,double theta,Vector velocity,double angularVelocity,double mass,double dragC)
	{
		this(new int[]{x,x+width,x+width,x},new int[]{y,y,y+height,y+height},theta,velocity,angularVelocity,mass,dragC);
	}
	
	
	public PhysicsPoly(int[] x,int[] y,double theta,Vector velocity,double angularVelocity,double mass,double dragC)
	{
		super(x,y,theta);
		if(velocity==null)
			this.velocity=new Vector();
		else
			this.velocity=velocity;
		this.acceleration=new Vector();
		this.angularVelocity=angularVelocity;
		this.mass=mass;
		this.dragC=dragC;
		calculateInertiaRelativeToCentroid();
	}
	
	public void calculateInertiaRelativeToCentroid()
	{
		Vector[] corners=super.getCorners();
		double mag;
		double top=0;
		double bot=0;
		
		for(int i = 0;i<corners.length-1;i++)
		{
			mag=corners[i].vectorSub(corners[0]).vectorLengthCross(corners[i+1].vectorSub(corners[0]));
			top+=mag*(corners[i].vectorSub(corners[0]).vectotDot(corners[i].vectorSub(corners[0]))+corners[i].vectorSub(corners[0]).vectotDot(corners[i+1].vectorSub(corners[0]))+corners[i+1].vectorSub(corners[0]).vectotDot(corners[i+1].vectorSub(corners[0])));
			bot+=mag;
		}

		//mag=(corners[0].vectorCross(corners[i]));
		//top+=mag*(corners[0].vectotDot(corners[0])+corners[0].vectotDot(corners[i])+corners[i].vectotDot(corners[i]));
		//bot+=mag;
		
		//System.out.println(sum/12);
		
		inertiaAboutCenter=(((top/bot)*mass)/600);
	}
	
	public double getInertiaRelativeTo(Vector pointOfAxis)
	{
		if(pointOfAxis==null)
			return inertiaAboutCenter;
		return inertiaAboutCenter+(mass*Math.pow(super.getCenter().vectorDistance(pointOfAxis), 2));
	}
	
	public void calculateCollisionForces()
	{
		
	}
	
	public void setVelocity(Vector velocity)
	{
		this.velocity = velocity;
	}
	
	public double getAngularVelocity()
	{
		return angularVelocity;
	}
	
	public void setAngularVelocity(double vel)
	{
		this.angularVelocity = vel;
	}
	
	public double getMomentum()
	{
		return mass*velocity.vectorMagnitude();
	}
	
	public double getAngularMomentum(Vector axis)
	{
		return getInertiaRelativeTo(axis)*angularVelocity;
	}
	
	public void render(Graphics g)
	{
		super.render(g);
	}

	public void tick()
	{
		//move the thing-start Verlet integration
		super.move(velocity.vectorScale(BattleCastleCanvas.time_Step).vectorAdd(acceleration.vectorScale(Math.pow(BattleCastleCanvas.time_Step, 2)*.5)).vectorScale(100));//last scale makes the meters per pixel into centimeters per pixel
		//calculate the forces on the object
		Vector netForce=new Vector(0,(int)(mass*GRAVITY));// force of gravity
		calculateArea();
		netForce=netForce.vectorSub(velocity.vectorScale(1.225*.5*dragC*super.getArea()));//force of drag
		//System.out.println(super.getCenter().X());
		//netForce=netForce.vectorSub(velocity.vectorScale(dampingC));//force of damping
		
		//check collision here and apply any needed forces??? or adjust momentum instead but less accurate
		
		//rotation??
		super.rotate(angularVelocity,null);
		//System.out.println(new Vector(2,2).vectorCross(new Vector(2,4)));
		
		//Verlet integration finished
		Vector new_acceleration=netForce.vectorScale(1/mass);
		Vector avg_acceleration = acceleration.vectorAdd(new_acceleration).vectorScale(.5);
		velocity=velocity.vectorAdd(avg_acceleration.vectorScale(BattleCastleCanvas.time_Step));
		//check the objects terminal velocity
		acceleration=new_acceleration;
		
		
		//wrap screen
		/*
		if(getVelocity().YPoint() < 0) //going up
		{
			if(getRightHighest().YPoint() < 0 - getHeight()) //move from top to bottom
			{
				moveTo(getCorners()[0].XPoint(), 786);
			}
			
		}
		if (getVelocity().YPoint() >= 0) //going down
		{
			if(getLeftHighest().YPoint() > 786 + getHeight()) //move from bottom to top
			{
				moveTo(getCorners()[0].XPoint(), 0-getHeight());
			}
			
		}
		
		if(getVelocity().XPoint() < 0) //going left
		{
			if(getLeftHighest().XPoint() < 0 - getWidth()) //move from left to right
			{
				moveTo(1024, getCorners()[0].YPoint());
			}
			
		}
		if(getVelocity().XPoint() >= 0) //going right
		{
			if(getLeftHighest().XPoint() > 1024 + getWidth()) //move from right to left
			{
				moveTo(0-getWidth(), getCorners()[0].YPoint());
			}
		}
		*/
	}
	
	public void setAcceleration(Vector acceleration) {
		this.acceleration = acceleration;
	}
	
	public Vector getAcceleration()
	{
		return acceleration;
	}
	
	public Vector getVelocity()
	{
		return velocity;
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

}
