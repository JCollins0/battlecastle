package game.physics;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

import editor.Tile;
import game.player.Arrow;
import game.player.Player;

public class CollisionDetector
{
	
	Stack<Integer> active=new Stack<Integer>();
	TreeMap<Double,String> sortAndSweep=new TreeMap<Double,String>();
	
	double proj;
	double max;
	int index;
	
	Vector[] axes1;
	Vector[] axes2;
	
	Vector[] corners;
	//Vector[] corners2;
	
	Projection p1=new Projection();
	Projection p2=new Projection();
	
	Vector axis;
	Vector minimal=new Vector();
	Vector point;
	
	Vector v0;
	Vector v1;
	
	Vector maxProj;
	
	Edge e1=new Edge();
	Edge e2=new Edge();
	
	Edge ref=new Edge();
	Edge inc=new Edge();
	
	Vector l;
	Vector r;
	
	boolean flip;
	
	double overlap = Double.MAX_VALUE;
	//ArrayList<Integer> ranges
	Polygon ac, bc; // collision polygons
	
	int i;

	public CollisionDetector()
	{
		
	}
	
	public void broadCheck(List<Polygon> poly)
	{
		sortAndSweep.clear();
		active.clear();
		i=0;
		for(;i<poly.size();i++)
		{
			sortAndSweep.put(poly.get(i).getLowestX(), "B"+i);//x value, object index
			sortAndSweep.put(poly.get(i).getHighestX(), "E"+i);
		}
		while(!sortAndSweep.isEmpty())
		{
			String delete=sortAndSweep.pollFirstEntry().getValue();
			if(delete.contains("E"))
			{
				int n=Integer.parseInt(delete.substring(1));				
				Iterator<Integer> itr=active.iterator();
				while(itr.hasNext())
				{
					int current=itr.next();
					if(current==n)
						itr.remove();
					else
					{
						SATCheck(poly.get(n), poly.get(current));
					}
				}
			}
			else
			{
				int n=Integer.parseInt(delete.substring(1));
				active.add(n);
			}
		}
	}
	
	private boolean SATCheck(Polygon a,Polygon b)
	{
		//System.out.println("sat");
		ac = a.getCollisionBounds();
		bc = b.getCollisionBounds();
		
		axes1=ac.getAxes();
		axes2=bc.getAxes();
		
		overlap = Double.MAX_VALUE;
		
		//loop over axes1
		for(i=0;i<axes1.length;i++)
		{
			axis=axes1[i];
			p1.project(ac,axis);
			p2.project(bc,axis);
			
			if(!p1.overlap(p2))
			{
				return false;
			}
			else
			{
				double small = p1.getOverlap(p2);
				if(small < overlap)
				{
					overlap= small;
					minimal=axis;
				}
				
			}
		}
		
		//loop over axes2
		for(i=0;i<axes2.length;i++)
		{
			axis=axes2[i];
			p1.project(ac,axis);
			p2.project(bc,axis);
			
			if(!p2.overlap(p1))
			{
				return false;
			}
			else
			{
				double small = p1.getOverlap(p2);
				if(small < overlap)
				{
					overlap= small;
					minimal=axis;
				}
			}
		}
		
//		minimal.normalize();
//		FindPointOfCollision(a,b);
//		if(flip)
//		{
//			minimal.vectorScale(-1);
//		}
		
		if(a.getCenter().vectorDistance(b.getCenter())>a.getCenter().vectorAdd(minimal).vectorDistance(b.getCenter()))
			overlap*=-1;
		
		System.out.println("COLLIDE DETECT: " + a.getClass() + ", " + minimal.XExact()+" "+minimal.YExact()+"   "+overlap);
		if(a instanceof Tile)
		{
			//((PhysicsPoly) a).setVelocity(new Vector(0,0));
			if(b instanceof PhysicsPoly)
			{
				//minimal = minimal.vector);
				System.out.println("Minimal.X is non-zero: " + minimal.vectorScale(overlap) );
				//minimal = minimal;
				b.move(minimal.vectorScale(Math.round(overlap)));
				//System.out.println(minimal.getNormal());
				//((PhysicsPoly)b).setNormalForce(minimal.getNormal().XPoint(), minimal.getNormal().YPoint());
				((PhysicsPoly)b).setNormalForce(minimal.XPoint(), minimal. YPoint());
			//	((PhysicsPoly) a).setExternalForce(0,-90);
//				((PhysicsPoly) a).setVelocity(((PhysicsPoly) a).getVelocity().vectorScale(-1));
				
			}
		//	((PhysicsPoly) a).getAcceleration().vectotDot(minimal.getNormal());
		}
		if(b instanceof Tile)
		{
			if(a instanceof Arrow)
			{
				a.move(minimal.vectorScale(Math.round(overlap)));
				((PhysicsPoly)a).setNormalForce(0,0);
			}
			//((PhysicsPoly) a).setVelocity(new Vector(0,0));
			else if(a instanceof PhysicsPoly)
			{
				
				System.out.println("Minimal scaled with overlap " + minimal.vectorScale(Math.round(overlap)) );
				//minimal = minimal;
				
				a.move(minimal.vectorScale(Math.round(overlap)));
			//	System.out.println(minimal.getNormal());
				((PhysicsPoly)a).setNormalForce(minimal.getNormal().XPoint(), minimal.getNormal().YPoint());
			//	((PhysicsPoly) a).setExternalForce(0,-90);
//				((PhysicsPoly) a).setVelocity(((PhysicsPoly) a).getVelocity().vectorScale(-1));
				
			}
		//	((PhysicsPoly) a).getAcceleration().vectotDot(minimal.getNormal());
		}
		
		
		
		
//		if(a instanceof Tile)
//		{
//			if(b instanceof PhysicsPoly)
//			{
//				
//				System.out.println("Minimal.X is non-zero: " + minimal.vectorScale(overlap) );
//				
//				b.move(minimal.vectorScale(overlap));
//				((PhysicsPoly) b).setVelocity(((PhysicsPoly) b).getVelocity().vectorScale(-1));
//			}
//		}
//		if(b instanceof Tile)	
//		{
//			if(a instanceof PhysicsPoly)
//			{
//				
//				System.out.println("Minimal.X is non-zero: " + minimal.vectorScale(overlap) );
//				
//				a.move(minimal.vectorScale(overlap));
//				((PhysicsPoly) a).setVelocity(((PhysicsPoly) a).getVelocity().vectorScale(-1));
//			}
//		}
//		
		
		
		
		
		return true;
	}
	
	private void FindPointOfCollision(Polygon a,Polygon b)
	{
		determineMPVandBestEdge(a,e1);
		determineMPVandBestEdge(b,e2);
	}
	
	private void determineMPVandBestEdge(Polygon a,Edge e)
	{
	//	findReferenceEdge();
		corners=a.getCorners();
		max=corners[0].vectotDot(minimal);
		index=0;
		//corners2=b.getCorners();
		for(i=1;i<a.getCount();i++)
		{
			proj=corners[i].vectotDot(minimal);
			if(proj>max)
			{
				max=proj;
				index=i;
			}
		}
		maxProj=corners[index];
		int temp=index;
		if(index==i)
			temp=-1;
		v0=corners[++temp < corners.length ? temp : 0];
		if(index==0)
			temp=i;
		v1=corners[--temp >= 0 ? temp : corners.length-1];
		
		
		l=maxProj.vectorSub(v1);
		r=maxProj.vectorSub(v0);
		
		if(r.getNormal().vectotDot(minimal)<=l.getNormal().vectotDot(minimal))
		{
			e.set(maxProj, r);
		}
		else
		{
			e.set(maxProj, l);
		}
		
		System.out.println("MPV_BEST_EDGE: " + e.getMax());
		
	}
	
	public void findReferenceEdge()
	{
		if(Math.abs(e1.getEdge().vectotDot(minimal))<=Math.abs(e2.getEdge().vectotDot(minimal)))
		{
			flip=false;
			ref=e1;
			inc=e2;
		}
		else
		{
			flip=true;
			ref=e2;
			inc=e1;
		}
	}
	
	public void clippingOperations()
	{
		
	}

}
