package game.physics;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import core.Tree;
import editor.Tile;
import game.Game;
import game.player.Arrow;
import game.player.Player;

public class CollisionDetector
{
	final Vector stopped = new Vector(0,0);
	
	Stack<Integer> active=new Stack<Integer>();
	Tree<Double,String> sortAndSweep=new Tree<Double,String>();
	
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

	Game gameRef;
	
	public CollisionDetector(Game gameRef)
	{
		this.gameRef = gameRef;
	}
	
	public void broadCheck(List<Polygon> poly)
	{
		//System.out.println("List of Polygons " + poly);
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
		
		Vector ac = a.getCenter();
		Vector bc = b.getCenter();
		Vector acbc = a instanceof Tile ? bc.vectorSub(ac) : ac.vectorSub(bc);
		if(minimal.vectotDot(acbc) < 0)
		{
			minimal.negate();
		}
	//	minimal.normalize();
	//	System.out.println("COLLIDE DETECT: " + a.getClass() + ", " + minimal.XExact()+" "+minimal.YExact()+"   "+overlap);
		
		/*
		 * Tile collision
		 */
		if(a instanceof Tile)
		{
			if(b instanceof PhysicsPoly)
			{
			//	System.out.println("Minimal.X is non-zero: " + minimal.getNormal().YExact() );
				overlap = Math.ceil(overlap+.45); //round to not clip into tiles
				
				b.move(minimal.vectorScale(overlap));
				((PhysicsPoly) b).setVelocity(stopped);
				
				if(b instanceof Arrow)
				{
					((PhysicsPoly)b).setNormalForce(0,0);
					((Arrow) b).setRotate(false);
					b.move(minimal.vectorScale(-5));
				}
				else if(b instanceof Player)
				{
					((PhysicsPoly)b).setNormalForce(minimal.getNormal().getNormal().getNormal().XExact(), minimal.getNormal().getNormal().getNormal().YExact());
					//System.out.println("PLAYER OPPOSITE NORMAL " + minimal.getNormal().getNormal().getNormal());
				}
				
			}
		}
		if(b instanceof Tile)
		{
			if(a instanceof PhysicsPoly)
			{
				
		//		System.out.println("Minimal scaled with overlap " + minimal.vectorScale(overlap) );
				//minimal = minimal;
				
				overlap = Math.ceil(overlap+.45);
				
				a.move(minimal.vectorScale(overlap));
			//	System.out.println("Player Velocity " + ((PhysicsPoly) a).getVelocity());
				((PhysicsPoly) a).setVelocity(stopped);
				if(a instanceof Arrow)
				{
					((PhysicsPoly)a).setNormalForce(0,0);
					((Arrow) a).setRotate(false);
					a.move(minimal.vectorScale(-5));
				}else if(a instanceof Player)
				{
					((PhysicsPoly)a).setNormalForce(minimal.getNormal().XExact(),minimal.getNormal().YExact());
				}
				
			}
		}
		
		/* ===========================
		 * Player and Arrow Collision
		 * ===========================
		 */
		if(a instanceof Player && !((Player)a).isDead() )
		{
			if(b instanceof Arrow)
			{
				if(((Arrow) b).getLaunchCoolDown() <= 0 )
				{
					if(((Arrow)b).isStuck())
						gameRef.addArrowToPlayer((Arrow)b,(Player)a);
					else
					{
						gameRef.killPlayer((Player)a);
						((Arrow)b).setVelocity(stopped);
					}
						
				}
			}
		}
		if(b instanceof Player && !((Player)b).isDead())
		{
			if(a instanceof Arrow)
			{
				if(((Arrow) a).getLaunchCoolDown() <= 0 )
				{
					if(((Arrow)a).isStuck())
						gameRef.addArrowToPlayer((Arrow)a,(Player)b);
					else
					{
						gameRef.killPlayer((Player)b);
						((Arrow)a).setVelocity(stopped);
					}
				}
			}
		}
		
		
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
		
	//	System.out.println("MPV_BEST_EDGE: " + e.getMax());
		
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
