package game.physics;

import java.awt.Color;
import java.awt.Graphics;

public class Vector {
	
	private double x,y;

	public Vector()
	{
		this.x=0;
		this.y=0;
	}
	
	public Vector(double x,double y)
	{
		this.x=x;
		this.y=y;
	}
	
	public Vector vectorAdd(Vector a)
	{
		return new Vector(x+a.x,y+a.y);
	}
	
	public Vector vectorSub(Vector a)
	{
		return new Vector(x-a.x,y-a.y);
	}
	
	public Vector vectorScale(double scalar)
	{
		return new Vector((x*scalar),(y*scalar));
	}
	
	public Vector vectorMult(Vector v)
	{
		return new Vector(x*v.x,y*v.y);
	}
	
	public double vectotDot(Vector a)
	{
		return (x*a.x)+(y*a.y);
	}
	
	public double vectorCross(Vector a)
	{
		return ((x*a.y)-(y*a.x));
	}
	
	public double vectorLengthCross(Vector a)
	{
		return Math.abs((x*a.y)-(y*a.x));
	}
	
	public double vectorMagnitude()
	{
		return Math.sqrt((x*x)+(y*y));
	}
	
	public Vector vectorRotate(double degrees,Vector axis)//take another look at this to simplify the vector math
	{
		double tempX=this.x-axis.x;
		double tempY=this.y-axis.y;
		
		//System.out.println(tempX+" "+tempY);
		
		double cs=Math.cos(Math.toRadians(degrees));
		double sn=Math.sin(Math.toRadians(degrees));
		
		return new Vector(tempX*cs-tempY*sn+axis.x,tempX*sn+tempY*cs+axis.y);//garbage stuff?
	}
	
	public Vector getNormal()
	{
		return new Vector(y,-x).vectorScale(1/vectorMagnitude());//check for garbage issues later
	}
	
	public void normalize()
	{
		double m=vectorMagnitude();
		x/=m;
		y/=m;
	}
	
	public void negate()
	{
		x = -x;
		y = -y;
	}
	
	public void absolute()
	{
		x=Math.abs(x);
		y = Math.abs(y);
	}
	
	public double vectorDistance(Vector a)
	{
		return Math.sqrt(Math.pow(a.x-x, 2)+Math.pow(a.y-y, 2));
	}
	
	public int XPoint()
	{
		return (int)Math.round(x);
	}
	
	public int YPoint()
	{
		return (int)Math.round(y);
	}
	
	public double XExact()
	{
		return x;
	}
	
	public double YExact()
	{
		return y;
	}
	
	
	public void setX(double x) 
	{
		this.x = x;
	}

	public void setY(double y) 
	{
		this.y = y;
	}

	public void render(Graphics g)
	{
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(0, 0, (int)x, (int)y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vector)
		{
			Vector v = (Vector)obj;
			return YPoint() == v.YPoint() && XPoint() == v.XPoint();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[X: " + x + ", Y: " + y + "]";
	}

}
