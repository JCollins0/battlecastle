package game.physics;

public class Projection
{
	Vector[] c;
	double current;
	double high,low;
	int i;

	public Projection()
	{
		
	}
	
	public void project(Polygon shape,Vector axis)
	{
		c=shape.getCorners();
		low=high=c[0].vectotDot(axis);
		for(i=1;i<c.length;i++)
		{
			current=c[i].vectotDot(axis);
			if(current<low)
				low=current;
			if(current>high)
				high=current;
		}
	}
	
	public boolean overlap(Projection p)
	{
		return !(low>p.high||p.low>high);
	}
	
	public double getOverlap(Projection p)
	{
		return Math.min(high,p.high)-Math.max(low,p.low);
	}

}
