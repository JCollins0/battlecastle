package game.physics;

import java.awt.Graphics;

public class PhysicsPoly extends Polygon
{
	
	private Vector acceleration;
	private Vector velocity;
	
	int i;
	
	private double angularVelocity;
	private double inertiaAboutCenter;
	
	private double mass;
	//private double dampingC;
	private double dragC;

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
		i=0;
		for(;i<corners.length-1;i++)
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
		super.move(velocity.vectorScale(RobotCanvas.timeStep).vectorAdd(acceleration.vectorScale(Math.pow(RobotCanvas.timeStep, 2)*.5)).vectorScale(100));//last scale makes the meters per pixel into centimeters per pixel
		
		//calculate the forces on the object
		Vector netForce=new Vector(0,(int)(mass*RobotCanvas.gravity));// force of gravity
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
		velocity=velocity.vectorAdd(avg_acceleration.vectorScale(RobotCanvas.timeStep));
		//System.out.println(velocity.Y());//check the objects terminal velocity
		acceleration=new_acceleration;
	}

}
