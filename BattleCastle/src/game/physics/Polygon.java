package game.physics;// you are working on screen wrap!

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;


public class Polygon
{	
	private double area=-1;
	
	private Vector[] corners;
	protected Vector[] axes;//extend to the shapes implemented already for parallel
	
	protected int count;
	protected double theta;

	/*public Polygon(int x,int y,int width,int height,double theta)
	{	
		corners=new Vector[4];
		corners[0]=new Vector(x,y);
		corners[1]=new Vector(x+width,y);
		corners[2]=new Vector(x+width,y+height);
		corners[3]=new Vector(x,y+height);
		getArea(true);
		rotate(theta,null);
	}*/
	
	public Polygon(int x, int y, int width, int height)
	{
		this(new int[]{x,x+width,x+width,x},new int[]{y,y,y+height,y+height},0);
	}
	
	public Polygon(int[] xPoints,int[] yPoints,double theta)
	{
		corners=new Vector[xPoints.length];
		axes=new Vector[xPoints.length];
		for(int i=0;i<xPoints.length;i++)
			corners[i]=new Vector(xPoints[i],yPoints[i]);
		calculateArea();
		rotate(theta,null);
		count=corners.length;
		this.theta = theta;
	}
	
	public Vector getCenter()//uses the summation equation for finding the centroid of a non-self-intersecting closed polygon
	{
		Vector centroid=new Vector(0,0);
		double signedArea=0;
		double partialSignedArea=0;
		int i=0;
		
		for(;i<count-1;i++)
		{
			partialSignedArea=corners[i].vectorCross(corners[i+1]);
			signedArea+=partialSignedArea;
			centroid=centroid.vectorAdd(corners[i].vectorAdd(corners[i+1]).vectorScale(partialSignedArea));
		}
		
		partialSignedArea=corners[i].vectorCross(corners[0]);
		signedArea+=partialSignedArea;
		centroid=centroid.vectorAdd(corners[i].vectorAdd(corners[0]).vectorScale(partialSignedArea));
		
		signedArea*=.5;
		centroid=centroid.vectorScale(1.0/(6*signedArea));
		
		//System.out.println(centroid.X()+" "+centroid.Y());
		//for(i=0;i<count;i++)
			//centroid=centroid.vectorAdd(corners[i]);
		return centroid;//.vectorScale(1.0/count);
	}
	
	public void rotate(double angle,Vector[] points)
	{
		Vector axis=new Vector();
		if(points==null)
		{
			this.theta += angle;
			if(theta > 360)
				theta %= 360;
			axis=this.getCenter();
		}
		else
		{
			//set axis
		}
		//System.out.println(center.X()+"-"+center.Y());
		for(int i=0;i<count;i++)
			corners[i]=corners[i].vectorRotate(angle, axis);
	}
	
	public void rotateTo(double angle)
	{
		
		rotate(angle-theta,null);
		theta = angle;
	}
	
	public void move(Vector pos)
	{
		for(int i=0;i<count;i++)
		{
			corners[i]=corners[i].vectorAdd(pos);
		}
	}
	
	public void moveTo(int x, int y)
	{
		Vector v = corners[0];
		move(new Vector(x-v.XExact(),y-v.YExact()));
	}
	
	public void calculateArea()
	{
		//area of polygon: A=(.5)((x0*y1+x1*y2+x2*y0)-(y0*x1+y1*x2+y2*x0))
		double sum=0;
		int i=0;
		for(;i<count-1;i++)
		{
			sum+=corners[i].vectorCross(corners[i+1]);
			//sum+=corners[i].X()*corners[i+1].Y();
			//sum-=corners[i].Y()*corners[i+1].X();
		}
		sum+=corners[i].vectorCross(corners[0]);
		//sum+=corners[i].X()*corners[0].Y();
		//sum-=corners[i].Y()*corners[0].X();
		area=sum/200;
	}
	
	public double getArea()
	{
		return area;
	}
	
	public Vector[] getCorners()
	{
		return corners;
	}
	
	public double getHighestX()
	{
		double n=corners[0].XExact();
		for(int i=1;i<count;i++)
			if(n<corners[i].XExact())
				n=corners[i].XExact();
		return n;
	}
	
	public double getHighestY()
	{
		double n=corners[0].YExact();
		for(int i=1;i<count;i++)
			if(n<corners[i].YExact())
				n=corners[i].YExact();
		return n;
	}
	
	public double getLowestX()
	{
		double n=corners[0].XExact();
		for(int i=1;i<count;i++)
			if(n>corners[i].XExact())
				n=corners[i].XExact();
		return n;
	}
	
	public double getLowestY()
	{
		double n=corners[0].YExact();
		for(int i=1;i<count;i++)
			if(n>corners[i].YExact())
				n=corners[i].YExact();
		return n;
	}
	
	public Vector[] getAxes()
	{
		int i=0;
		for(;i<count-1;i++)
		{
			axes[i]=corners[i].vectorSub(corners[i+1]).getNormal();
			//axes[i]=corners[i].getNormal();
		}
		axes[i]=corners[i].vectorSub(corners[0]).getNormal();
		//axes[i]=corners[i].getNormal();
		return axes;
	}
	
	public Vector getVector(double x, double y)
	{
		Vector v = new Vector(x,y);
		for(int i = 0; i < corners.length; i++)
		{
			if(corners[i].equals(v))
			{
				System.out.println("A MATCH " + v);
				return corners[i];
			}
		}
		
		return null;
	}
		
	public int getCount()
	{
		return count;
	}
	
	public void tick()//dimensions are 1024 by 786
	{
		for(Vector v:corners)
		{
			if(v.XPoint()<0||v.XPoint()>1024)
				cloneX();
		}
	}
	
	public void cloneX()
	{
		
	}
	
	public void render(Graphics g)
	{
		g.setColor(Color.BLACK);
		int i=0;
		for(;i<count-1;i++)
			g.drawLine(corners[i].XPoint(), corners[i].YPoint(), corners[i+1].XPoint(), corners[i+1].YPoint());
		g.drawLine(corners[i].XPoint(), corners[i].YPoint(), corners[0].XPoint(), corners[0].YPoint());
	}
	
	public Polygon getCollisionBounds()
	{
		return this;
	}

	public boolean contains(Point p)
	{
		return contains(p.x,p.y);
	}
	
	public boolean contains(int x, int y)
	{
		return (x >= corners[0].XPoint() && x <= corners[1].XPoint() ) && (y >= corners[0].YPoint() && y <= corners[2].YPoint()); 
	}
}
